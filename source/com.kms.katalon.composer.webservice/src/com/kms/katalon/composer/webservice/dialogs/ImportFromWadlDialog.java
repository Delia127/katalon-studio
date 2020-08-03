package com.kms.katalon.composer.webservice.dialogs;

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
import com.kms.katalon.composer.webservice.importing.model.RestImportNode;
import com.kms.katalon.composer.webservice.importing.model.RestServiceImportResult;
import com.kms.katalon.composer.webservice.wadl.WadlImporter;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class ImportFromWadlDialog extends CustomTitleAreaDialog {

    private FolderEntity parentFolder;

    private Text txtWadlLocation;

    private Button btnBrowseFile;

    private String wadlLocation;

    public ImportFromWadlDialog(Shell parentShell, FolderEntity parentFolder) {
        super(parentShell);
        this.parentFolder = parentFolder;
    }

    @Override
    protected Composite createContentArea(Composite parent) {
        setMessage(ComposerWebserviceMessageConstants.ImportFromWadlDialog_MSG_IMPORT_FROM_WADL, IMessageProvider.INFORMATION);

        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 5;
        layout.marginHeight = 5;
        layout.marginBottom = 30;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label label = new Label(composite, SWT.NONE);
        label.setText(ComposerWebserviceMessageConstants.ImportFromWadlDialog_LBL_WADL_LOCATION);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        txtWadlLocation = new Text(composite, SWT.BORDER);
        GridData gdTxtWadlLocation = new GridData(SWT.FILL, SWT.FILL, true, false);
        gdTxtWadlLocation.widthHint = 200;
        txtWadlLocation.setLayoutData(gdTxtWadlLocation);

        btnBrowseFile = new Button(composite, SWT.PUSH);
        btnBrowseFile.setText(StringConstants.BROWSE);
        btnBrowseFile.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));

        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    @Override
    protected void registerControlModifyListeners() {
        txtWadlLocation.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                Button btnOk = getButton(IDialogConstants.OK_ID);
                String text = ((Text) e.widget).getText();
                if (StringUtils.isBlank(text)) {
                    btnOk.setEnabled(false);
                } else {
                    btnOk.setEnabled(true);
                }
                wadlLocation = text;
            }
        });

        btnBrowseFile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog fileDialog = new FileDialog(getParentShell(), SWT.SINGLE);
                String filePath = fileDialog.open();
                txtWadlLocation.setText(filePath);
                wadlLocation = filePath;
            }
        });
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(ComposerWebserviceMessageConstants.ImportFromWadlDialog_DIA_TITLE_IMPORT_FROM_WADL);
    }

    @Override
    protected void okPressed() {
        try {
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
            dialog.run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask(ComposerWebserviceMessageConstants.ImportFromWadlDialog_MSG_IMPORTING_FROM_WADL, SubMonitor.UNKNOWN);
                        importFromWadl();
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
            super.okPressed();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(getShell(), StringConstants.ERROR_TITLE, ComposerWebserviceMessageConstants.ImportFromWadlDialog_MSG_FAILED_TO_IMPORT_FROM_WADL);
        }
    }

    private void importFromWadl() throws Exception {
        WadlImporter importer = new WadlImporter();
        RestServiceImportResult serviceImportResult = importer.importService(wadlLocation, parentFolder);
        saveImportedArtifacts(serviceImportResult);
        getEventBroker().post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY,
                TreeEntityUtil.getFolderTreeEntity((FolderEntity) serviceImportResult.getFileEntity()));
        getEventBroker().post(EventConstants.EXPLORER_SET_SELECTED_ITEM,
                TreeEntityUtil.getFolderTreeEntity((FolderEntity) serviceImportResult.getFileEntity()));
    }

    private void saveImportedArtifacts(RestServiceImportResult serviceImportResult) throws Exception {
        List<RestImportNode> importNodes = flatten(serviceImportResult).collect(Collectors.toList());
        for (RestImportNode importNode : importNodes) {
            FileEntity fileEntity = importNode.getFileEntity();
            if (fileEntity != null && fileEntity instanceof FolderEntity) {
                FolderController.getInstance().saveFolder((FolderEntity) fileEntity);
            }
            if (fileEntity != null && fileEntity instanceof WebServiceRequestEntity) {
                ObjectRepositoryController.getInstance().saveNewTestObject((WebServiceRequestEntity) fileEntity);
            }
        }
    }

    private Stream<RestImportNode> flatten(RestImportNode importNode) {
        Stream<RestImportNode> childImportNodes = importNode.getChildImportNodes().stream().flatMap(n -> flatten(n));
        return Stream.concat(Stream.of(importNode), childImportNodes);
    }

    private IEventBroker getEventBroker() {
        return EventBrokerSingleton.getInstance().getEventBroker();
    }

    @Override
    protected void setInput() {
        // TODO Auto-generated method stub

    }

}
