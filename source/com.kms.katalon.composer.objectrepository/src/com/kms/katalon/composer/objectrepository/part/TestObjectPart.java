package com.kms.katalon.composer.objectrepository.part;

import java.text.MessageFormat;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.composer.objectrepository.view.ObjectPropertyView;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;

public class TestObjectPart implements EventHandler {
    private static boolean isConfirmationDialogShowed = false;

    @Inject
    protected EModelService modelService;

    @Inject
    protected MApplication application;

    @Inject
    private IEventBroker eventBroker;

    @Inject
    protected MDirtyable dirtyable;

    private MPart mPart;

    private ObjectPropertyView objPropertyView;

    private WebElementEntity originalTestObject;

    @PostConstruct
    public void createComposite(Composite parent, MPart part) {
        this.mPart = part;
        parent.setLayout(new FillLayout());

        objPropertyView = new ObjectPropertyView(eventBroker, dirtyable);
        objPropertyView.createMainPage(parent);
        originalTestObject = (WebElementEntity) part.getObject();
        objPropertyView.changeOriginalTestObject(originalTestObject);

        registerListeners();
    }

    private void registerListeners() {
        eventBroker.subscribe(EventConstants.TEST_OBJECT_UPDATED, this);
        eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.getTopic().equals(EventConstants.TEST_OBJECT_UPDATED)) {
            Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
            if (object != null && object instanceof Object[]) {
                String elementId = EntityPartUtil.getTestObjectPartId((String) ((Object[]) object)[0]);

                if (elementId.equalsIgnoreCase(mPart.getElementId())) {
                    WebElementEntity webElement = (WebElementEntity) ((Object[]) object)[1];
                    updateTestObjectPart(webElement);
                    objPropertyView.changeOriginalTestObject(webElement);
                }
            }
        } else if (event.getTopic().equals(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM)) {
            try {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof ITreeEntity) {
                    if (object instanceof WebElementTreeEntity) {
                        WebElementTreeEntity testObjectTreeEntity = (WebElementTreeEntity) object;
                        WebElementEntity testObject = (WebElementEntity) (testObjectTreeEntity).getObject();
                        if (testObject != null && testObject.getId().equals(originalTestObject.getId())) {
                            if (ObjectRepositoryController.getInstance().getWebElement(testObject.getId()) != null) {
                                if (!dirtyable.isDirty()) {
                                    originalTestObject = testObject;
                                    objPropertyView.changeOriginalTestObject(testObject);
                                }
                            } else {
                                dispose();
                            }
                        } else {
                            if (ObjectRepositoryController.getInstance().getWebElement(originalTestObject.getId()) == null) {
                                dispose();
                            }
                        }
                    } else if (object instanceof FolderTreeEntity) {
                        FolderEntity folder = (FolderEntity) ((ITreeEntity) object).getObject();
                        if (folder != null
                                && FolderController.getInstance().isFolderAncestorOfEntity(folder, originalTestObject)) {
                            if (ObjectRepositoryController.getInstance().getWebElement(originalTestObject.getId()) == null) {
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
        mStackPart.getChildren().remove(mPart);
    }

    private void updateTestObjectPart(WebElementEntity webElement) {
        mPart.setLabel(webElement.getName());
        mPart.setElementId(EntityPartUtil.getTestObjectPartId(webElement.getId()));
    }

    @Persist
    public void save() {
        objPropertyView.save();
    }

    @Focus
    private void onFocused() {
        try {
            if (originalTestObject != null) {
                WebElementEntity srcWebElement = ObjectRepositoryController.getInstance().getWebElement(
                        originalTestObject.getId());
                if (srcWebElement != null) {
                    if (!srcWebElement.equals(originalTestObject) && !isConfirmationDialogShowed) {
                        isConfirmationDialogShowed = true;
                        if (MessageDialog.openConfirm(
                                Display.getCurrent().getActiveShell(),
                                StringConstants.PA_CONFIRM_TITLE_FILE_CHANGED,
                                MessageFormat.format(StringConstants.PA_CONFIRM_MSG_RELOAD_FILE,
                                        originalTestObject.getLocation()))) {
                            originalTestObject = srcWebElement;
                            objPropertyView.changeOriginalTestObject(srcWebElement);
                            dirtyable.setDirty(false);
                        }
                        isConfirmationDialogShowed = false;
                    }
                } else {
                    FolderTreeEntity parentTreeEntity = getParentFolderTreeEntity(originalTestObject.getParentFolder(),
                            FolderController.getInstance().getObjectRepositoryRoot(originalTestObject.getProject()));
                    if (parentTreeEntity != null) {
                        eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentTreeEntity);
                    }
                    dispose();
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @PreDestroy
    private void destroy() {
        eventBroker.unsubscribe(this);
    }

    private FolderTreeEntity getParentFolderTreeEntity(FolderEntity folderEntity, FolderEntity rootFolder) {
        if (folderEntity == null || folderEntity.equals(rootFolder)) {
            return null;
        }
        return new FolderTreeEntity(folderEntity, getParentFolderTreeEntity(folderEntity.getParentFolder(), rootFolder));
    }
}
