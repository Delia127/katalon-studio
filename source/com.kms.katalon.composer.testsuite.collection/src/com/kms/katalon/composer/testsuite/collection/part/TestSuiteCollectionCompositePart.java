package com.kms.katalon.composer.testsuite.collection.part;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.impl.util.EventUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.part.IComposerPartEvent;
import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.composer.testsuite.constants.ImageConstants;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;

public class TestSuiteCollectionCompositePart extends EventServiceAdapter implements IComposerPartEvent, SavableCompositePart {

    private static final int COMPOSITE_SIZE = 1;

    private static final int SUB_PARTSTACK_SIZE = 2;

    private static final int CHILD_MAIN_PART_INDEX = 0;

    private static final int CHILD_RESULT_PART_INDEX = 1;

    @Inject
    private MDirtyable dirty;

    @Inject
    private IEventBroker eventBroker;
    
    @Inject
    private EPartService partService;

    private MCompositePart compositePart;

    private MPartStack subPartStack;

    private CTabFolder tabFolder;

    private TestSuiteCollectionEntity testSuiteCollection;

    private TestSuiteCollectionPart testSuiteCollectionMainPart;

    private TestSuiteCollectionResultPart testSuiteCollectionResultPart;

    @PostConstruct
    public void init(Composite parent, MCompositePart compositePart) {
        dirty.setDirty(false);
        this.compositePart = compositePart;
        this.testSuiteCollection = (TestSuiteCollectionEntity) compositePart.getObject();
        initListeners();
    }

    public void initComponent() {
        if (compositePart.getChildren().size() != COMPOSITE_SIZE
                || !(compositePart.getChildren().get(0) instanceof MPartStack)) {
            return;
        }
        subPartStack = (MPartStack) compositePart.getChildren().get(0);
        if (subPartStack.getChildren().size() == SUB_PARTSTACK_SIZE) {
            for (MStackElement stackElement : subPartStack.getChildren()) {
                if (!(stackElement instanceof MPart)) {
                    continue;
                }

                Object part = ((MPart) stackElement).getObject();
                if (part instanceof TestSuiteCollectionPart) {
                    testSuiteCollectionMainPart = (TestSuiteCollectionPart) part;
                } else {
                    testSuiteCollectionResultPart = new TestSuiteCollectionResultPart(part);
                }
            }
        }
        initTabFolder();
    }

    private void initListeners() {
        eventBroker.subscribe(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM, this);
        eventBroker.subscribe(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, this);
        eventBroker.subscribe(EventConstants.TEST_SUITE_COLLECTION_FINISHED, this);
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
        EventUtil.post(EventConstants.PROPERTIES_ENTITY, null);
        compositePart.getChildren().clear();
        eventBroker.unsubscribe(this);
    }

    @Override
    public String getEntityId() {
        return testSuiteCollection.getId();
    }

    @Override
    @Inject
    @Optional
    public void onSelect(@UIEventTopic(UIEvents.UILifeCycle.BRINGTOTOP) Event event) {
        if (testSuiteCollection == null) {
            return;
        }
        MPart part = EventUtil.getPart(event);
        if (part == null || !StringUtils.startsWith(part.getElementId(), EntityPartUtil.getTestSuiteCompositePartId(testSuiteCollection.getId()))) {
            return;
        }
        EventUtil.post(EventConstants.PROPERTIES_ENTITY, testSuiteCollection);
    }

    @Override
    @Inject
    @Optional
    public void onChangeEntityProperties(@UIEventTopic(EventConstants.PROPERTIES_ENTITY_UPDATED) Event event) {
        Object eventData = EventUtil.getData(event);
        if (!(eventData instanceof TestSuiteCollectionEntity)) {
            return;
        }
        
        TestSuiteCollectionEntity updatedEntity = (TestSuiteCollectionEntity) eventData;
        if (!StringUtils.equals(updatedEntity.getIdForDisplay(), getEntityId())) {
            return;
        }
        testSuiteCollection.setTag(updatedEntity.getTag());
        testSuiteCollection.setDescription(updatedEntity.getDescription());
    }

    @Override
    public void handleEvent(Event event) {
        switch (event.getTopic()) {
            case EventConstants.EXPLORER_RENAMED_SELECTED_ITEM: {
                Object[] objects = getObjects(event);
                if (objects == null || objects.length != 2) {
                    return;
                }
                if (ObjectUtils.equals(testSuiteCollection.getIdForDisplay(), objects[1])) {
                    String newCompositePartId = EntityPartUtil.getTestSuiteCollectionPartId(testSuiteCollection.getId());
                    if (!newCompositePartId.equals(compositePart.getElementId())) {
                        compositePart.setElementId(newCompositePartId);
                        compositePart.setLabel(testSuiteCollection.getName());
                        MPartStack partStack = (MPartStack) compositePart.getChildren().get(0);
                        partStack.setElementId(
                                newCompositePartId + IdConstants.TEST_SUITE_COLLECTION_SUB_PART_STACK_ID_SUFFIX);
                        testSuiteCollectionMainPart.getMPart()
                                .setElementId(newCompositePartId + IdConstants.TEST_SUITE_COLLECTION_MAIN_PART_ID_SUFFIX);
                        testSuiteCollectionResultPart.getMPart()
                                .setElementId(newCompositePartId + IdConstants.TEST_SUITE_COLLECTION_RESULT_PART_ID_SUFFIX);
                    }
                }
                break;
            }
            case EventConstants.EXPLORER_DELETED_SELECTED_ITEM: {
                eventBroker.unsubscribe(this);
                partService.hidePart(compositePart, true);
                break;
            }
            case EventConstants.TEST_SUITE_COLLECTION_UPDATED: {
                Object[] objects = getObjects(event);
                if (objects == null || objects.length != 2) {
                    return;
                }
                if (testSuiteCollection.equals(objects[1])) {
                    String newCompositePartId = EntityPartUtil.getTestSuiteCollectionPartId(testSuiteCollection.getId());
                    if (!newCompositePartId.equals(compositePart.getElementId())) {
                        compositePart.setElementId(newCompositePartId);
                        compositePart.setLabel(testSuiteCollection.getName());
                        MPartStack partStack = (MPartStack) compositePart.getChildren().get(0);
                        partStack.setElementId(
                                newCompositePartId + IdConstants.TEST_SUITE_COLLECTION_SUB_PART_STACK_ID_SUFFIX);
                        testSuiteCollectionMainPart.getMPart()
                                .setElementId(newCompositePartId + IdConstants.TEST_SUITE_COLLECTION_MAIN_PART_ID_SUFFIX);
                        testSuiteCollectionResultPart.getMPart()
                                .setElementId(newCompositePartId + IdConstants.TEST_SUITE_COLLECTION_RESULT_PART_ID_SUFFIX);
                    }
                }
                break;
            }
            case EventConstants.TEST_SUITE_COLLECTION_FINISHED: {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof TestSuiteCollectionEntity) {
                    TestSuiteCollectionEntity eventTestSuiteCollection = (TestSuiteCollectionEntity) object;
                    if (eventTestSuiteCollection.getId().equals(testSuiteCollection.getId())) {
                        try {
                            ReportCollectionEntity report = ReportController.getInstance().getLastRunReportCollectionEntity(eventTestSuiteCollection);
                            testSuiteCollectionResultPart.updateReport(report);
                        } catch (Exception e) {
                            LoggerSingleton.logError(e);
                        } 
                    }
                }
            }
        }


    }

    @Override
    public List<MPart> getChildParts() {
        List<MPart> childrenParts = new ArrayList<MPart>();
        childrenParts.add(testSuiteCollectionMainPart.getMPart());
        childrenParts.add(testSuiteCollectionResultPart.getMPart());
        return childrenParts;
    }

    @Override
    public void save() throws Exception {
        testSuiteCollectionMainPart.save();        
    }

    @Override
    public void setDirty(boolean isDirty) {
        dirty.setDirty(isDirty);
    }

    @Override
    public boolean isDirty() {
        return compositePart.isDirty();
    }
}
