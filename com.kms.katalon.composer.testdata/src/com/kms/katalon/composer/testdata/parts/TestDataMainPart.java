package com.kms.katalon.composer.testdata.parts;

import java.io.File;
import java.text.MessageFormat;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.xml.bind.UnmarshalException;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.xml.sax.SAXParseException;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.testdata.constants.ImageConstants;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;

public abstract class TestDataMainPart implements EventHandler {
	public static int MAX_LABEL_WIDTH = 70;

	protected Text txtName, txtId, txtDesc, txtDataType;

	protected MPart mpart;

	protected DataFileEntity dataFile;

	protected DataFileEntity cloneDataFile;

	@Inject
	protected IEventBroker eventBroker;

	@Inject
	protected MDirtyable dirtyable;

	@Inject
	protected EModelService modelService;

	@Inject
	protected MApplication application;

	private static boolean isConfirmDialogShowed = false;

	protected ModifyListener modifyListener;
	private Composite infoCompositeName;
	private Composite infoCompositeId;
	private Label labelId;
	private Composite infoCompositeDescription;
	private Label labelDescription;
	private Composite infoCompositeDataType;
	private Label labelDataType;
	private Label lblSupporter;
	private Label lblSupporter1;
	private Composite compositeInfoHeader;
	private Composite compositeInfoDetails;
	private Composite compositeGeneralInfo;
	private Composite compositeFileInfo;
	private Composite compositeDataTable;
	private Button btnExpandInformation;
	private Label lblInformations;
	private boolean isInfoCompositeExpanded = true;

	public void createControls(Composite parent, MPart mpart) {
		this.mpart = mpart;

		createModifyListener();

		createInfoSection(parent);
		compositeFileInfo = createFileInfoPart(parent);
		compositeDataTable = createDataTablePart(parent);
		registerEventHandlers();

		updateDataFile((DataFileEntity) mpart.getObject());

		dirtyable.setDirty(false);
	}

	private void registerEventHandlers() {
		eventBroker.subscribe(EventConstants.TEST_DATA_UPDATED, this);
		eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, this);
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	protected void createInfoSection(Composite parent) {
		parent.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());
		compositeGeneralInfo = new Composite(parent, SWT.NONE);
		compositeGeneralInfo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		GridLayout gl_infoComposite = new GridLayout(1, true);
		gl_infoComposite.verticalSpacing = 5;
		gl_infoComposite.marginWidth = 0;
		gl_infoComposite.marginHeight = 0;
		compositeGeneralInfo.setLayout(gl_infoComposite);
		compositeGeneralInfo.setBackground(ColorUtil.getCompositeBackgroundColor());

		compositeInfoHeader = new Composite(compositeGeneralInfo, SWT.NONE);
		GridLayout gl_compositeInfoHeader = new GridLayout(2, false);
		gl_compositeInfoHeader.marginWidth = 0;
		gl_compositeInfoHeader.marginHeight = 0;
		compositeInfoHeader.setLayout(gl_compositeInfoHeader);
		compositeInfoHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnExpandInformation = new Button(compositeInfoHeader, SWT.NONE);
		GridData gd_btnExpandInformation = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnExpandInformation.widthHint = 18;
		gd_btnExpandInformation.heightHint = 18;
		btnExpandInformation.setLayoutData(gd_btnExpandInformation);
		redrawBtnExpandInfo();
		btnExpandInformation.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Display.getDefault().timerExec(10, new Runnable() {

					@Override
					public void run() {
						isInfoCompositeExpanded = !isInfoCompositeExpanded;

						compositeInfoDetails.setVisible(isInfoCompositeExpanded);
						if (!isInfoCompositeExpanded) {
							((GridData) compositeInfoDetails.getLayoutData()).exclude = true;
							int detailSize = compositeDataTable.getSize().y;
							if (compositeFileInfo != null) {
								detailSize += compositeFileInfo.getSize().y;
							}
							compositeGeneralInfo.setSize(compositeGeneralInfo.getSize().x,
									compositeGeneralInfo.getSize().y - detailSize);
						} else {
							((GridData) compositeInfoDetails.getLayoutData()).exclude = false;
						}
						compositeGeneralInfo.layout(true, true);
						compositeGeneralInfo.getParent().layout();
						redrawBtnExpandInfo();
					}

				});
			}
		});

		lblInformations = new Label(compositeInfoHeader, SWT.NONE);
		lblInformations.setText(StringConstants.PA_LBL_GENERAL_INFO);
		lblInformations.setFont(JFaceResources.getFontRegistry().getBold(""));

		compositeInfoDetails = new Composite(compositeGeneralInfo, SWT.NONE);
		compositeInfoDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_compositeInfoDetails = new GridLayout(2, true);
		gl_compositeInfoDetails.marginBottom = 10;
		gl_compositeInfoDetails.marginHeight = 0;
		gl_compositeInfoDetails.marginWidth = 0;
		gl_compositeInfoDetails.marginRight = 40;
		gl_compositeInfoDetails.verticalSpacing = 0;
		gl_compositeInfoDetails.marginLeft = 40;
		gl_compositeInfoDetails.horizontalSpacing = 30;
		compositeInfoDetails.setLayout(gl_compositeInfoDetails);

		infoCompositeId = new Composite(compositeInfoDetails, SWT.NONE);
		infoCompositeId.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_infoCompositeId = new GridLayout(2, false);
		infoCompositeId.setLayout(gl_infoCompositeId);

		labelId = new Label(infoCompositeId, SWT.NONE);
		GridData gdLabelId = new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1);
		gdLabelId.widthHint = MAX_LABEL_WIDTH;
		labelId.setLayoutData(gdLabelId);
		labelId.setText(StringConstants.PA_LBL_ID);

		txtId = new Text(infoCompositeId, SWT.BORDER);
		GridData gd_txtId = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		gd_txtId.heightHint = 18;
		txtId.setLayoutData(gd_txtId);
		txtId.setEditable(false);

		infoCompositeDescription = new Composite(compositeInfoDetails, SWT.NONE);
		infoCompositeDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		GridLayout glcompositeDescription = new GridLayout(2, false);
		glcompositeDescription.verticalSpacing = 10;
		infoCompositeDescription.setLayout(glcompositeDescription);

		labelDescription = new Label(infoCompositeDescription, SWT.NONE);
		GridData gdLabelDesc = new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1);
		gdLabelDesc.widthHint = MAX_LABEL_WIDTH;
		labelDescription.setLayoutData(gdLabelDesc);
		labelDescription.setText(StringConstants.PA_LBL_DESCRIPTION);

		txtDesc = new Text(infoCompositeDescription, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		GridData gdTxtDesc = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3);
		gdTxtDesc.heightHint = 90;
		txtDesc.setLayoutData(gdTxtDesc);
		txtDesc.addModifyListener(modifyListener);

		lblSupporter = new Label(infoCompositeDescription, SWT.NONE);
		lblSupporter.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));

		lblSupporter1 = new Label(infoCompositeDescription, SWT.NONE);
		lblSupporter1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));

		infoCompositeName = new Composite(compositeInfoDetails, SWT.NONE);
		infoCompositeName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_infoCompositeName = new GridLayout(2, false);
		infoCompositeName.setLayout(gl_infoCompositeName);

		Label label = new Label(infoCompositeName, SWT.NONE);
		GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1);
		label.setLayoutData(gridData);
		label.setText(StringConstants.PA_LBL_NAME);
		gridData.widthHint = MAX_LABEL_WIDTH;
		txtName = new Text(infoCompositeName, SWT.BORDER);
		GridData gd_txtName = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		gd_txtName.heightHint = 18;
		txtName.setLayoutData(gd_txtName);

		infoCompositeDataType = new Composite(compositeInfoDetails, SWT.NONE);
		infoCompositeDataType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		GridLayout gl_infoCompositeDataType = new GridLayout(2, false);
		infoCompositeDataType.setLayout(gl_infoCompositeDataType);

		labelDataType = new Label(infoCompositeDataType, SWT.NONE);
		GridData gridData3 = new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1);
		gridData3.widthHint = MAX_LABEL_WIDTH;
		labelDataType.setLayoutData(gridData3);
		labelDataType.setText(StringConstants.PA_LBL_DATA_TYPE);

		txtDataType = new Text(infoCompositeDataType, SWT.BORDER);
		GridData gd_txtDataType = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		gd_txtDataType.heightHint = 18;
		txtDataType.setLayoutData(gd_txtDataType);
		txtDataType.setEditable(false);

		txtName.addModifyListener(modifyListener);
		// end info part
	}

	private void redrawBtnExpandInfo() {
		btnExpandInformation.getParent().setRedraw(false);
		if (isInfoCompositeExpanded) {
			btnExpandInformation.setImage(ImageConstants.IMG_16_ARROW_UP_BLACK);
		} else {
			btnExpandInformation.setImage(ImageConstants.IMG_16_ARROW_DOWN_BLACK);
		}
		btnExpandInformation.getParent().setRedraw(true);
	}

	// Will be overridden in sub-class
	/**
	 * @wbp.parser.entryPoint
	 */
	protected abstract Composite createFileInfoPart(Composite parent);

	// Will be overridden in sub-class
	protected abstract Composite createDataTablePart(Composite parent);

	protected abstract void updateChildInfo(DataFileEntity dataFile);

	protected void updateDataFile(DataFileEntity dataFile) {
		this.dataFile = dataFile;
		// update mpart
		mpart.setLabel(dataFile.getName());
		mpart.setElementId(EntityPartUtil.getTestDataPartId(dataFile.getId()));

		updateGeneralInfo(dataFile);
		updateChildInfo(dataFile);
	}

	private void updateGeneralInfo(DataFileEntity dataFile) {
		try {
			txtName.setText(dataFile.getName());
			txtId.setText(TestDataController.getInstance().getIdForDisplay(dataFile));
			txtDesc.setText(dataFile.getDescription());
			txtDataType.setText(dataFile.getDriver().name());
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}

	}

	@Override
	public void handleEvent(Event event) {
		if (event.getTopic().equals(EventConstants.TEST_DATA_UPDATED)) {
			Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
			if (object != null && object instanceof Object[]) {
				String elementId = EntityPartUtil.getTestDataPartId((String) ((Object[]) object)[0]);

				if (elementId.equalsIgnoreCase(mpart.getElementId())) {
					DataFileEntity dataFile = (DataFileEntity) ((Object[]) object)[1];
					this.dataFile = dataFile;

					boolean oldDirty = dirtyable.isDirty();
					updateDataFile(dataFile);
					dirtyable.setDirty(oldDirty);
				}

			}
		} else if (event.getTopic().equals(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM)) {
			try {
				Object selectedTreeEntityObject = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
				if (selectedTreeEntityObject != null) {
					if (selectedTreeEntityObject instanceof TestDataTreeEntity) {
						TestDataTreeEntity selectedTreeEntity = (TestDataTreeEntity) selectedTreeEntityObject;
						DataFileEntity refreshedDataFileEntity = (DataFileEntity) selectedTreeEntity.getObject();
						if (refreshedDataFileEntity.getId().equals(dataFile.getId())) {
							if (TestDataController.getInstance().getTestData(refreshedDataFileEntity.getId()) != null) {
								if (dirtyable.isDirty()) {
									verifySourceChanged();
								} else {
									updateDataFile(refreshedDataFileEntity);
									dirtyable.setDirty(false);
								}
							} else {
								dispose();
							}
						}
					}

					if (selectedTreeEntityObject instanceof FolderTreeEntity) {
						FolderEntity folderEntity = (FolderEntity) ((FolderTreeEntity) selectedTreeEntityObject)
								.getObject();
						if (dataFile.getId().contains(folderEntity.getId() + File.separator)) {
							if (TestDataController.getInstance().getTestData(dataFile.getId()) == null) {
								dispose();
							}
						}
					}

				}
			} catch (Exception e) {
				LoggerSingleton.logError(e);
			}
		}
	}

	private void dispose() {
		eventBroker.unsubscribe(this);
		MPartStack mStackPart = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
		mStackPart.getChildren().remove(mpart);
	}

	private void createModifyListener() {
		modifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dirtyable.setDirty(true);
			}
		};
	}

	protected void sendTestDataUpdatedEvent(String oldPk) {
		eventBroker.post(EventConstants.TEST_DATA_UPDATED, new Object[] { oldPk, dataFile });
	}

	private void verifySourceChanged() {
		try {
			if (dataFile != null) {
				DataFileEntity sourceDataFile = TestDataController.getInstance().getTestData(dataFile.getId());
				if (sourceDataFile != null) {
					if (!sourceDataFile.equals(dataFile)) {
						if (!isConfirmDialogShowed) {
							isConfirmDialogShowed = true;
							if (MessageDialog.openConfirm(
									Display.getCurrent().getActiveShell(),
									StringConstants.PA_CONFIRM_TITLE_FILE_CHANGED,
									MessageFormat.format(StringConstants.PA_CONFIRM_MSG_RELOAD_FILE,
											dataFile.getLocation()))) {
								updateDataFile(sourceDataFile);
								dirtyable.setDirty(false);
							}
							isConfirmDialogShowed = false;
						}

					}
				} else {
					FolderTreeEntity parentFolderTreeEntity = getParentFolderTreeEntity(dataFile.getParentFolder(),
							FolderController.getInstance().getTestDataRoot(dataFile.getProject()));
					if (parentFolderTreeEntity != null) {
						eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentFolderTreeEntity);
					}
					dispose();
				}
			}
		} catch (UnmarshalException e) {
			if (!isConfirmDialogShowed) {
				isConfirmDialogShowed = true;
				SAXParseException saxParserException = (SAXParseException) e.getLinkedException().getCause();

				MessageDialog.openError(
						Display.getCurrent().getActiveShell(),
						StringConstants.ERROR_TITLE,
						MessageFormat.format(StringConstants.PA_ERROR_MSG_FILE_X_IS_WRONG_FORMAT_AT_LINE_Y,
								dataFile.getLocation(), saxParserException.getLineNumber()));
				isConfirmDialogShowed = false;
				try {
					FolderTreeEntity parentFolderTreeEntity = getParentFolderTreeEntity(dataFile.getParentFolder(),
							FolderController.getInstance().getTestDataRoot(dataFile.getProject()));

					if (parentFolderTreeEntity != null) {
						eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentFolderTreeEntity);
					}
					dispose();
				} catch (Exception e1) {
					LoggerSingleton.logError(e);
				}
			}
			LoggerSingleton.logError(e);
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}

	@Focus
	private void onFocused() {
		verifySourceChanged();
	}

	private FolderTreeEntity getParentFolderTreeEntity(FolderEntity folderEntity, FolderEntity rootFolder) {
		if (folderEntity == null || folderEntity.equals(rootFolder)) {
			return null;
		}
		return new FolderTreeEntity(folderEntity, getParentFolderTreeEntity(folderEntity.getParentFolder(), rootFolder));
	}

	@PreDestroy
	private void destroy() {
		eventBroker.unsubscribe(this);
	}
}
