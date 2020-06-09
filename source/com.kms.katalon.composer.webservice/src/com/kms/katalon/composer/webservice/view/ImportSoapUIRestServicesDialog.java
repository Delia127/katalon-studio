package com.kms.katalon.composer.webservice.view;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.dialogs.CustomTitleAreaDialog;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.soapui.SoapUIImportNode;
import com.kms.katalon.composer.webservice.soapui.SoapUIImporter;
import com.kms.katalon.composer.webservice.soapui.SoapUIProjectImportResult;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class ImportSoapUIRestServicesDialog extends CustomTitleAreaDialog {

	private FolderEntity parentFolder;

	private String projectFilePath = ""; //$NON-NLS-1$

	private Text txtProjectFile;

	private Button btnBrowseFile;

	public ImportSoapUIRestServicesDialog(Shell shell, FolderEntity parentFolder) {
		super(shell);
		this.parentFolder = parentFolder;
	}

	@Override
	protected Composite createContentArea(Composite parent) {
		setMessage(
				ComposerWebserviceMessageConstants.ImportSoapUIRestServicesDialog_MSG_IMPORT_REST_SERVICES_FROM_SOAPUI_PROJECT,
				IMessageProvider.INFORMATION);

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		layout.marginBottom = 30;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label label = new Label(composite, SWT.NONE);
		label.setText(ComposerWebserviceMessageConstants.ImportSoapUIRestServicesDialog_LBL_SOAPUI_PROJECT);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		txtProjectFile = new Text(composite, SWT.BORDER);
		GridData gdTxtProjectFile = new GridData(SWT.FILL, SWT.FILL, true, false);
		gdTxtProjectFile.widthHint = 200;
		txtProjectFile.setLayoutData(gdTxtProjectFile);

		btnBrowseFile = new Button(composite, SWT.PUSH);
		btnBrowseFile.setText(StringConstants.BROWSE);
		btnBrowseFile.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));

		return composite;
	}

	@Override
	protected void registerControlModifyListeners() {
		txtProjectFile.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				Button btnOk = getButton(IDialogConstants.OK_ID);
				String text = ((Text) e.widget).getText();
				if (StringUtils.isBlank(text)) {
					btnOk.setEnabled(false);
				} else {
					btnOk.setEnabled(true);
				}
				projectFilePath = text;
			}
		});

		btnBrowseFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fileDialog = new FileDialog(getParentShell(), SWT.SINGLE);
				String filePath = fileDialog.open();
				txtProjectFile.setText(filePath);
				projectFilePath = filePath;
			}
		});
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(
				ComposerWebserviceMessageConstants.ImportSoapUIRestServicesDialog_DIA_TITLE_IMPORT_REST_SERVICES_FROM_SOAPUI);
	}

	@Override
	protected void okPressed() {
		try {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
			dialog.run(true, false, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						monitor.beginTask("Importing Rest Services from SoapUI...", SubMonitor.UNKNOWN);
						importRestServicesFromSoapUI();
					} catch (Exception e) {
						LoggerSingleton.logError(e);
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
				}
			});

		} catch (Exception e) {
			LoggerSingleton.logError(e);
			MessageDialog.openError(getShell(), StringConstants.ERROR_TITLE,
					ComposerWebserviceMessageConstants.ERROR_MSG_FAIL_TO_IMPORT_SOAPUI);
		}
		super.okPressed();
	}

	private void importRestServicesFromSoapUI() throws Exception {
		SoapUIImporter importer = new SoapUIImporter();
		SoapUIProjectImportResult projectImportResult = importer.importServices(projectFilePath, parentFolder);
		saveImportedArtifacts(projectImportResult);
		getEventBroker().post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY,
				TreeEntityUtil.getFolderTreeEntity(projectImportResult.getFileEntity()));
		getEventBroker().post(EventConstants.EXPLORER_SET_SELECTED_ITEM,
				TreeEntityUtil.getFolderTreeEntity(projectImportResult.getFileEntity()));
	}

	private void saveImportedArtifacts(SoapUIProjectImportResult projectImportResult) throws Exception {
		List<SoapUIImportNode> importNodes = flatten(projectImportResult).collect(Collectors.toList());
		for (SoapUIImportNode importNode : importNodes) {
			FileEntity fileEntity = importNode.getFileEntity();
			if (fileEntity != null && fileEntity instanceof FolderEntity) {
				FolderController.getInstance().saveFolder((FolderEntity) fileEntity);
			}
			if (fileEntity != null && fileEntity instanceof WebServiceRequestEntity) {
				ObjectRepositoryController.getInstance().saveNewTestObject((WebServiceRequestEntity) fileEntity);
			}
		}
	}

	private Stream<? extends SoapUIImportNode> flatten(SoapUIImportNode importNode) {
		return Stream.concat(Stream.of(importNode),
				Stream.of(importNode.getChildImportNodes()).flatMap(n -> flatten(n)));
	}

	private IEventBroker getEventBroker() {
		return EventBrokerSingleton.getInstance().getEventBroker();
	}

	@Override
	protected void setInput() {
		// TODO Auto-generated method stub

	}

}
