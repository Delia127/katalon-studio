package com.kms.katalon.composer.testsuite.collection.part;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.part.IComposerPartEvent;
import com.kms.katalon.composer.testsuite.constants.ImageConstants;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;

public class TestSuiteCollectionCompositePart extends EventServiceAdapter implements IComposerPartEvent {
    
    private static final int COMPOSITE_SIZE = 1;
    
    private static final int SUB_PARTSTACK_SIZE = 2;
    
    private static final int CHILD_MAIN_PART_INDEX = 0;
    
    private static final int CHILD_RESULT_PART_INDEX = 1;
    
    @Inject
    private MDirtyable dirty;
    
    @Inject
    private IEventBroker eventBroker;
    
    private MCompositePart compositePart;
    
    private MPartStack subPartStack;
    
    private CTabFolder tabFolder;
    
    private TestSuiteCollectionEntity testSuiteCollection;

    @PostConstruct
    public void init(Composite parent, MCompositePart compositePart) {
        dirty.setDirty(false);
        this.compositePart = compositePart;
        this.testSuiteCollection = (TestSuiteCollectionEntity) compositePart.getObject();
    }
    
    public void initComponent() {
        if (compositePart.getChildren().size() != COMPOSITE_SIZE
                || !(compositePart.getChildren().get(0) instanceof MPartStack)) {
            return;
        }
        subPartStack = (MPartStack) compositePart.getChildren().get(0);
        initTabFolder();
    }
    
    private void initListeners() {
        eventBroker.subscribe(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM, this);
        eventBroker.subscribe(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, this);
    }
    
    private void initTabFolder() {
       if (subPartStack.getWidget() instanceof CTabFolder) {
           tabFolder = (CTabFolder) subPartStack.getWidget();
           tabFolder.setTabPosition(SWT.BOTTOM);
           tabFolder.setBorderVisible(false);
           tabFolder.setMaximizeVisible(false);
           tabFolder.setMinimizeVisible(false);
           
           if (tabFolder.getItemCount() == SUB_PARTSTACK_SIZE) {
               CTabItem mainPart = tabFolder.getItem(CHILD_MAIN_PART_INDEX);
               mainPart.setText(StringConstants.PA_TAB_MAIN);
               mainPart.setImage(ImageConstants.IMG_16_MAIN);
               mainPart.setShowClose(false);
               
               CTabItem resultPart = tabFolder.getItem(CHILD_RESULT_PART_INDEX);
               resultPart.setText(StringConstants.PA_TAB_RESULT);
               resultPart.setShowClose(false);
           }
           
           tabFolder.layout();
       }
    }
    
    @Override
    @PreDestroy
    public void onClose() {
        compositePart.getChildren().clear();
    }

    @Override
    public String getEntityId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onSelect(Event event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onChangeEntityProperties(Event event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void handleEvent(Event event) {
        switch (event.getTopic()) {
            case EventConstants.EXPLORER_RENAME_SELECTED_ITEM:
               Object[] objects = getObjects(event);
               if (objects == null || objects.length != 2) {
                   return; 
               }
               if (ObjectUtils.equals(testSuiteCollection.getIdForDisplay(), objects[1])) {
                   String compositePartId = EntityPartUtil.getTestSuiteCollectionPartId(testSuiteCollection.getId());
                   compositePart.setElementId(compositePartId);
                   compositePart.setLabel(testSuiteCollection.getName());
               }
        }
        
    }
}
