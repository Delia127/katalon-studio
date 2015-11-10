package com.kms.katalon.composer.explorer.handlers;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.UIEvents.EventTags;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class LinkEditorHandler implements EventHandler {

    private boolean fActive;
    @Inject
    private IEventBroker eventBroker;

    @PostConstruct
    public void initListener() {
        fActive = false;
        eventBroker.subscribe(UIEvents.UILifeCycle.BRINGTOTOP, this);
    }

    @CanExecute
    public boolean canExecute(MHandledToolItem item) {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute(MHandledToolItem item) {
        if (item.isSelected()) {
            fActive = true;
        } else {
            fActive = false;
        }
    }

    @Override
    public void handleEvent(Event event) {
        Object object = event.getProperty(EventTags.ELEMENT);
        if (fActive && UIEvents.UILifeCycle.BRINGTOTOP.equals(event.getTopic()) && (object != null)
                && (object instanceof MPart)) {
            MPart mpart = (MPart) object;
            try {
                IEntity entity = EntityPartUtil.getEntityByPartId(mpart.getElementId());

                if (entity == null) {
                    return;
                }

                ITreeEntity treeEntity = null;
                ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();
                if (entity instanceof TestCaseEntity) {
                    treeEntity = TreeEntityUtil.getTestCaseTreeEntity((TestCaseEntity) entity, projectEntity);
                }
                if (treeEntity == null) { return; }
                eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, treeEntity);
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
    }
}
