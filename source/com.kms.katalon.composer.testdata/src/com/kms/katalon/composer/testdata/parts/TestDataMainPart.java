package com.kms.katalon.composer.testdata.parts;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.xml.bind.UnmarshalException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.IPartListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.xml.sax.SAXParseException;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.impl.util.EventUtil;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.part.IComposerPartEvent;
import com.kms.katalon.composer.parts.CPart;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;

public abstract class TestDataMainPart extends CPart implements EventHandler, IPartListener, IComposerPartEvent {
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
    protected EPartService partService;

    @Inject
    protected MApplication application;

    protected MPart mpart;

    protected DataFileEntity originalDataFile;

    protected DataFileEntity cloneDataFile;

    private boolean isConfirmDialogShowed = false;

    protected Set<Thread> currentThreads;

    protected ModifyListener modifyListener;

    public void createControls(Composite parent, MPart mpart) {
        this.mpart = mpart;
        initialize(mpart, partService);
        
        currentThreads = new HashSet<Thread>();

        isConfirmDialogShowed = false;

        createModifyListener();

        createFileInfoPart(parent);
        createDataTablePart(parent);
        registerEventHandlers();

        updateDataFile((DataFileEntity) mpart.getObject());

        dirtyable.setDirty(false);
    }

    protected abstract EPartService getPartService();

    private void registerEventHandlers() {
        eventBroker.subscribe(EventConstants.TEST_DATA_UPDATED, this);
        eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, this);
        getPartService().addPartListener(this);
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

        updateChildInfo(dataFile);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.getTopic().equals(EventConstants.TEST_DATA_UPDATED)) {
            Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
            if (object != null && object instanceof Object[]) {
                String elementId = EntityPartUtil.getTestDataPartId((String) ((Object[]) object)[0]);
                String publisher = StringUtils.defaultString((String) ((Object[]) object)[2]);
                if (!mpart.getElementId().equals(publisher) && elementId.equalsIgnoreCase(mpart.getElementId())) {
                    DataFileEntity dataFile = (DataFileEntity) ((Object[]) object)[1];
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

    @PreDestroy
    @Override
    public void dispose() {
        super.dispose();
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
        eventBroker.post(EventConstants.TEST_DATA_UPDATED, new Object[] { oldPk, originalDataFile, mpart.getElementId() });
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

    @Override
    @PreDestroy
    public void onClose() {
        EventUtil.post(EventConstants.PROPERTIES_ENTITY, null);
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
    
    public void setDirty(boolean dirty) {
        dirtyable.setDirty(dirty);
    }

    @Override
    public void partActivated(MPart part) {

    }

    @Override
    public void partBroughtToTop(MPart part) {

    }

    @Override
    public void partDeactivated(MPart part) {
        if (part == mpart) {
            removePart();
        }
    }

    @Override
    public void partHidden(MPart part) {
        if (part == mpart) {
            removePart();
        }
    }

    @Override
    public void partVisible(MPart part) {

    }

    private void removePart() {
        getPartService().removePartListener(this);
        if (!mpart.isVisible()) {
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

    protected void refreshTreeEntity() {
        try {
            TestDataTreeEntity testDataTreeEntity = TreeEntityUtil.getTestDataTreeEntity(originalDataFile,
                    ProjectController.getInstance().getCurrentProject());
            eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, testDataTreeEntity);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    @Inject
    @Optional
    public void onSelect(@UIEventTopic(UIEvents.UILifeCycle.BRINGTOTOP) Event event) {
        if (mpart == null || originalDataFile == null) {
            return;
        }
        MPart part = EventUtil.getPart(event);
        if (part == null || !StringUtils.equals(part.getElementId(), mpart.getElementId())) {
            return;
        }

        EventUtil.post(EventConstants.PROPERTIES_ENTITY, originalDataFile);
    }

    @Override
    @Inject
    @Optional
    public void onChangeEntityProperties(@UIEventTopic(EventConstants.PROPERTIES_ENTITY_UPDATED) Event event) {
        Object eventData = EventUtil.getData(event);
        if (!(eventData instanceof DataFileEntity)) {
            return;
        }

        DataFileEntity updatedEntity = (DataFileEntity) eventData;
        if (!StringUtils.equals(updatedEntity.getIdForDisplay(), getEntityId())) {
            return;
        }
        originalDataFile.setTag(updatedEntity.getTag());
        originalDataFile.setDescription(updatedEntity.getDescription());
    }

}
