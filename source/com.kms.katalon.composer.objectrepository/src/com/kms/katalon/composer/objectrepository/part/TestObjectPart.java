package com.kms.katalon.composer.objectrepository.part;

import java.text.MessageFormat;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.controls.HelpToolBarForMPart;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.impl.util.EventUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.part.IComposerPartEvent;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.composer.objectrepository.view.ObjectPropertyView;
import com.kms.katalon.composer.parts.CPart;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;

public class TestObjectPart extends CPart implements EventHandler, IComposerPartEvent {
    private static boolean isConfirmationDialogShowed = false;

    @Inject
    protected EModelService modelService;

    @Inject
    protected MApplication application;

    @Inject
    private IEventBroker eventBroker;

    @Inject
    protected MDirtyable dirtyable;
    
    @Inject
    private EPartService partService;

    private MPart mPart;

    private ObjectPropertyView objPropertyView;

    private WebElementEntity originalTestObject;

    @PostConstruct
    public void createComposite(Composite parent, MPart part) {
        this.mPart = part;
        new HelpToolBarForMPart(mPart, DocumentationMessageConstants.TEST_OBJECT_WEB_UI);
        parent.setLayout(new FillLayout());

        objPropertyView = new ObjectPropertyView(eventBroker, dirtyable, this);
        objPropertyView.createMainPage(parent);
        changeOriginalTestObject((WebElementEntity) part.getObject());

        initialize(mPart, partService);
        
        registerListeners();
    }

    private void changeOriginalTestObject(WebElementEntity testObject) {
        originalTestObject = testObject;
        objPropertyView.changeOriginalTestObject(originalTestObject);
    }

    private void registerListeners() {
        eventBroker.subscribe(EventConstants.TEST_OBJECT_UPDATED, this);
        eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, this);
    }
    
    public MPart getPart(){
    	return mPart;
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
                    changeOriginalTestObject(webElement);
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
                                    changeOriginalTestObject(testObject);
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

    @Override
    public void dispose() {
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
                            changeOriginalTestObject(srcWebElement);
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

    private FolderTreeEntity getParentFolderTreeEntity(FolderEntity folderEntity, FolderEntity rootFolder) {
        if (folderEntity == null || folderEntity.equals(rootFolder)) {
            return null;
        }
        return new FolderTreeEntity(folderEntity, getParentFolderTreeEntity(folderEntity.getParentFolder(), rootFolder));
    }

    public WebElementEntity getTestObject() {
        return originalTestObject;
    }

    @Override
    public String getEntityId() {
        return getTestObject().getIdForDisplay();
    }

    @Override
    @Inject
    @Optional
    public void onSelect(@UIEventTopic(UIEvents.UILifeCycle.BRINGTOTOP) Event event) {
        MPart part = EventUtil.getPart(event);
        if (part == null || !StringUtils.equals(mPart.getElementId(), part.getElementId())) {
            return;
        }

        EventUtil.post(EventConstants.PROPERTIES_ENTITY, originalTestObject);
    }

    @Override
    @Inject
    @Optional
    public void onChangeEntityProperties(@UIEventTopic(EventConstants.PROPERTIES_ENTITY_UPDATED) Event event) {
        Object eventData = EventUtil.getData(event);
        if (!(eventData instanceof WebElementEntity)) {
            return;
        }

        WebElementEntity updatedEntity = (WebElementEntity) eventData;
        if (!StringUtils.equals(updatedEntity.getIdForDisplay(), getEntityId())) {
            return;
        }
        originalTestObject.setTag(updatedEntity.getTag());
        originalTestObject.setDescription(updatedEntity.getDescription());
    }

    @Override
    @PreDestroy
    public void onClose() {
        EventUtil.post(EventConstants.PROPERTIES_ENTITY, null);
        objPropertyView.preDestroy();
        eventBroker.unsubscribe(this);
        dispose();
    }
}
