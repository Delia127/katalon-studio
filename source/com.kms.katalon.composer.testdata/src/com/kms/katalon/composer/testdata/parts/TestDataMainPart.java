package com.kms.katalon.composer.testdata.parts;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.IPartListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.xml.sax.SAXParseException;

import com.kms.katalon.composer.components.impl.control.ImageButton;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.part.IComposerPart;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.testdata.constants.ImageConstants;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;

public abstract class TestDataMainPart implements EventHandler, IPartListener, IComposerPart {
    public static final int MAX_LABEL_WIDTH = 70;

    protected static final int MAX_COLUMN_COUNT = 100;

    protected static final int COLUMN_WIDTH = 200;

    @Inject
    protected IEventBroker eventBroker;

    @Inject
    protected MDirtyable dirtyable;

    @Inject
    protected EModelService modelService;

    @Inject
    protected MApplication application;

    protected Text txtName, txtId, txtDesc, txtDataType;

    protected MPart mpart;

    protected DataFileEntity originalDataFile;

    protected DataFileEntity cloneDataFile;

    private boolean isConfirmDialogShowed = false;

    protected Set<Thread> currentThreads;

    protected ModifyListener modifyListener;

    private Composite infoCompositeName;

    private Composite infoCompositeId;

    private Label labelId;

    private Composite infoCompositeDescription;

    private Label labelDescription;

    private Composite infoCompositeDataType;

    private Label labelDataType;

    private Composite compositeInfoHeader;

    private Composite compositeInfoDetails;

    private Composite compositeGeneralInfo;

    private Composite compositeFileInfo;

    private Composite compositeDataTable;

    private ImageButton btnExpandGeneralInformation;

    private Label lblInformations;

    private boolean isInfoCompositeExpanded;

    private Listener layoutGeneralCompositeListener = new Listener() {

        @Override
        public void handleEvent(org.eclipse.swt.widgets.Event event) {
            layoutGeneralComposite();
        }
    };

    public void createControls(Composite parent, MPart mpart) {
        this.mpart = mpart;
        currentThreads = new HashSet<Thread>();

        isConfirmDialogShowed = false;
        isInfoCompositeExpanded = true;

        createModifyListener();

        createInfoSection(parent);
        compositeFileInfo = createFileInfoPart(parent);
        compositeDataTable = createDataTablePart(parent);
        registerEventHandlers();
        registerControlModifyListeners();

        updateDataFile((DataFileEntity) mpart.getObject());

        dirtyable.setDirty(false);
    }

    private void registerControlModifyListeners() {
        btnExpandGeneralInformation.addListener(SWT.MouseDown, layoutGeneralCompositeListener);
        lblInformations.addListener(SWT.MouseDown, layoutGeneralCompositeListener);
    }

    protected abstract EPartService getPartService();

    private void registerEventHandlers() {
        eventBroker.subscribe(EventConstants.TEST_DATA_UPDATED, this);
        eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, this);
        getPartService().addPartListener(this);
    }

    protected void layoutGeneralComposite() {
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
                    compositeGeneralInfo.setSize(compositeGeneralInfo.getSize().x, compositeGeneralInfo.getSize().y
                            - detailSize);
                } else {
                    ((GridData) compositeInfoDetails.getLayoutData()).exclude = false;
                }
                compositeGeneralInfo.layout(true, true);
                compositeGeneralInfo.getParent().layout();
                redrawBtnExpandInfo();
            }

        });
    }

    /**
     * @wbp.parser.entryPoint
     */
    protected void createInfoSection(Composite parent) {
        parent.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());
        compositeGeneralInfo = new Composite(parent, SWT.NONE);
        compositeGeneralInfo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        GridLayout glInfoComposite = new GridLayout(1, true);
        glInfoComposite.verticalSpacing = 5;
        glInfoComposite.marginWidth = 0;
        glInfoComposite.marginHeight = 0;
        compositeGeneralInfo.setLayout(glInfoComposite);
        compositeGeneralInfo.setBackground(ColorUtil.getCompositeBackgroundColor());

        compositeInfoHeader = new Composite(compositeGeneralInfo, SWT.NONE);
        GridLayout glCompositeInfoHeader = new GridLayout(2, false);
        glCompositeInfoHeader.marginWidth = 0;
        glCompositeInfoHeader.marginHeight = 0;
        compositeInfoHeader.setLayout(glCompositeInfoHeader);
        compositeInfoHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        compositeInfoHeader.setCursor(compositeInfoHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        btnExpandGeneralInformation = new ImageButton(compositeInfoHeader, SWT.NONE);
        GridData gd_btnExpandInformation = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnExpandInformation.widthHint = 18;
        gd_btnExpandInformation.heightHint = 18;
        btnExpandGeneralInformation.setLayoutData(gd_btnExpandInformation);
        redrawBtnExpandInfo();

        lblInformations = new Label(compositeInfoHeader, SWT.NONE);
        lblInformations.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblInformations.setText(StringConstants.PA_LBL_GENERAL_INFO);
        lblInformations.setFont(JFaceResources.getFontRegistry().getBold(""));

        compositeInfoDetails = new Composite(compositeGeneralInfo, SWT.NONE);
        compositeInfoDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glCompositeInfoDetails = new GridLayout(2, true);
        glCompositeInfoDetails.marginBottom = 10;
        glCompositeInfoDetails.marginHeight = 0;
        glCompositeInfoDetails.marginWidth = 0;
        glCompositeInfoDetails.marginRight = 40;
        glCompositeInfoDetails.verticalSpacing = 0;
        glCompositeInfoDetails.marginLeft = 40;
        glCompositeInfoDetails.horizontalSpacing = 30;
        compositeInfoDetails.setLayout(glCompositeInfoDetails);

        infoCompositeId = new Composite(compositeInfoDetails, SWT.NONE);
        infoCompositeId.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glInfoCompositeId = new GridLayout(2, false);
        infoCompositeId.setLayout(glInfoCompositeId);

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
        labelDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
        labelDescription.setText(StringConstants.PA_LBL_DESCRIPTION);

        txtDesc = new Text(infoCompositeDescription, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        GridData gdTxtDesc = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3);
        gdTxtDesc.heightHint = 90;
        txtDesc.setLayoutData(gdTxtDesc);
        txtDesc.addModifyListener(modifyListener);

        Label lblSupporter = new Label(infoCompositeDescription, SWT.NONE);
        lblSupporter.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));

        Label lblSupporter1 = new Label(infoCompositeDescription, SWT.NONE);
        lblSupporter1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));

        infoCompositeName = new Composite(compositeInfoDetails, SWT.NONE);
        infoCompositeName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glInfoCompositeName = new GridLayout(2, false);
        infoCompositeName.setLayout(glInfoCompositeName);

        Label label = new Label(infoCompositeName, SWT.NONE);
        GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1);
        label.setLayoutData(gridData);
        label.setText(StringConstants.PA_LBL_NAME);
        gridData.widthHint = MAX_LABEL_WIDTH;
        txtName = new Text(infoCompositeName, SWT.BORDER);
        GridData gdTxtName = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
        gdTxtName.heightHint = 18;
        txtName.setLayoutData(gdTxtName);

        infoCompositeDataType = new Composite(compositeInfoDetails, SWT.NONE);
        infoCompositeDataType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glInfoCompositeDataType = new GridLayout(2, false);
        infoCompositeDataType.setLayout(glInfoCompositeDataType);

        labelDataType = new Label(infoCompositeDataType, SWT.NONE);
        GridData gridDataLabelDataType = new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1);
        gridDataLabelDataType.widthHint = MAX_LABEL_WIDTH;
        labelDataType.setLayoutData(gridDataLabelDataType);
        labelDataType.setText(StringConstants.PA_LBL_DATA_TYPE);

        txtDataType = new Text(infoCompositeDataType, SWT.BORDER);
        GridData gdTxtDataType = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
        gdTxtDataType.heightHint = 18;
        txtDataType.setLayoutData(gdTxtDataType);
        txtDataType.setEditable(false);

        txtName.addModifyListener(modifyListener);
        // end info part
    }

    private void redrawBtnExpandInfo() {
        btnExpandGeneralInformation.getParent().setRedraw(false);
        if (isInfoCompositeExpanded) {
            btnExpandGeneralInformation.setImage(ImageConstants.IMG_16_ARROW_UP_BLACK);
        } else {
            btnExpandGeneralInformation.setImage(ImageConstants.IMG_16_ARROW_DOWN_BLACK);
        }
        btnExpandGeneralInformation.getParent().setRedraw(true);
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
        this.originalDataFile = dataFile;
        // update mpart
        mpart.setLabel(dataFile.getName());
        mpart.setElementId(EntityPartUtil.getTestDataPartId(dataFile.getId()));

        updateGeneralInfo(dataFile);
        updateChildInfo(dataFile);
    }

    private void updateGeneralInfo(DataFileEntity dataFile) {
        try {
            txtName.setText(dataFile.getName());
            txtId.setText(dataFile.getIdForDisplay());
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
                    if (dataFile.equals(originalDataFile)) {
                        return;
                    }

                    this.originalDataFile = dataFile;

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
                        if (refreshedDataFileEntity.getId().equals(originalDataFile.getId())) {
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
                        if (originalDataFile.getId().contains(folderEntity.getId() + File.separator)) {
                            if (TestDataController.getInstance().getTestData(originalDataFile.getId()) == null) {
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
        eventBroker.post(EventConstants.TEST_DATA_UPDATED, new Object[] { oldPk, originalDataFile });
    }

    private void verifySourceChanged() {
        try {
            if (originalDataFile != null) {
                DataFileEntity sourceDataFile = TestDataController.getInstance().getTestData(originalDataFile.getId());
                if (sourceDataFile != null) {
                    if (!sourceDataFile.equals(originalDataFile)) {
                        if (!isConfirmDialogShowed) {
                            isConfirmDialogShowed = true;
                            if (MessageDialog.openConfirm(
                                    Display.getCurrent().getActiveShell(),
                                    StringConstants.PA_CONFIRM_TITLE_FILE_CHANGED,
                                    MessageFormat.format(StringConstants.PA_CONFIRM_MSG_RELOAD_FILE,
                                            originalDataFile.getLocation()))) {
                                updateDataFile(sourceDataFile);
                                dirtyable.setDirty(false);
                            }
                            isConfirmDialogShowed = false;
                        }

                    }
                } else {
                    FolderTreeEntity parentFolderTreeEntity = getParentFolderTreeEntity(
                            originalDataFile.getParentFolder(),
                            FolderController.getInstance().getTestDataRoot(originalDataFile.getProject()));
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

                MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                        MessageFormat.format(StringConstants.PA_ERROR_MSG_FILE_X_IS_WRONG_FORMAT_AT_LINE_Y,
                                originalDataFile.getLocation(), saxParserException.getLineNumber()));
                isConfirmDialogShowed = false;
                try {
                    FolderTreeEntity parentFolderTreeEntity = getParentFolderTreeEntity(
                            originalDataFile.getParentFolder(),
                            FolderController.getInstance().getTestDataRoot(originalDataFile.getProject()));

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
        preDestroy();
        eventBroker.unsubscribe(this);
        Iterator<Thread> threadIterator = currentThreads.iterator();
        while (threadIterator.hasNext()) {
            Thread currentThread = threadIterator.next();
            if (currentThread.isAlive()) {
                currentThread.interrupt();
            }
        }
        threadIterator = null;
    }

    protected abstract void preDestroy();

    public void partActivated(MPart part) {

    }

    public void partBroughtToTop(MPart part) {

    }

    public void partDeactivated(MPart part) {
        if (part == mpart) {
            removePart();
        }
    }

    public void partHidden(MPart part) {
        if (part == mpart) {
            removePart();
        }
    }

    public void partVisible(MPart part) {

    }

    private void removePart() {
        getPartService().removePartListener(this);
        if (mpart.isVisible()) {
            getPartService().savePart(mpart, false);
        }
    }

    public DataFileEntity getDataFile() {
        return originalDataFile;
    }

    @Override
    public String getEntityId() {
        return getDataFile().getIdForDisplay();
    }
}
