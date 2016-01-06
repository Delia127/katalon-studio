package com.kms.katalon.composer.testsuite.parts;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.parts.MultipleTabsCompositePart;
import com.kms.katalon.composer.testsuite.constants.ImageConstants;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.util.TestSuiteEntityUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestSuiteCompositePart implements EventHandler, MultipleTabsCompositePart {
    // compositePart has only one child is subPartStack.
    // Children of subPartStack: childTestSuiteGeneralPart &
    // childTestSuiteIntegrationPart
    // Control of subPartStack is tabFolder.
    // Children of tabFolder are CTabItem that also are widgets of children of
    // subPartStack
    private MCompositePart compositePart;

    private MPartStack subPartStack;

    private CTabFolder tabFolder;

    private TestSuitePart childTestSuiteMainPart;

    private TestSuiteIntegrationPart childTestSuiteIntegrationPart;

    private boolean isInitialized;

    private static final int COMPOSITE_SIZE = 1;

    private static final int SUB_PARTSTACK_SIZE = 2;

    private static final int CHILD_TESTSUITE_MAIN_PART_INDEX = 0;

    private static final int CHILD_TESTSUITE_INTEGRATION_PART_INDEX = 1;

    public static final String MAIN_TAB_TITLE = StringConstants.PA_TAB_MAIN;

    public static final String INTEGRATION_TAB_TITLE = StringConstants.PA_TAB_INTEGRATION;

    private static boolean isConfirmationDialogShowed = false;

    // originalTestSuite for saving.
    // testSuite for working on children part;
    private TestSuiteEntity originalTestSuite, testSuite;

    private Composite parent;

    @Inject
    private IEventBroker eventBroker;

    @Inject
    protected EModelService modelService;

    @Inject
    protected MApplication application;

    @Inject
    private MDirtyable dirty;

    public MDirtyable getDirty() {
        return dirty;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    @PostConstruct
    public void init(Composite parent, MCompositePart compositePart, TestSuiteEntity testSuite) {
        this.parent = parent;
        this.compositePart = compositePart;
        dirty.setDirty(false);
        isInitialized = false;

        changeOriginalTestSuite(testSuite);
        initListeners();
    }

    public void initComponent() {
        if (compositePart.getChildren().size() != COMPOSITE_SIZE
                || !(compositePart.getChildren().get(0) instanceof MPartStack)) return;

        subPartStack = (MPartStack) compositePart.getChildren().get(0);
        if (subPartStack.getChildren().size() == SUB_PARTSTACK_SIZE) {
            for (MStackElement stackElement : subPartStack.getChildren()) {
                if (!(stackElement instanceof MPart)) continue;

                if (((MPart) stackElement).getObject() instanceof TestSuitePart) {
                    childTestSuiteMainPart = (TestSuitePart) ((MPart) stackElement).getObject();
                } else if (((MPart) stackElement).getObject() instanceof TestSuiteIntegrationPart) {
                    childTestSuiteIntegrationPart = (TestSuiteIntegrationPart) ((MPart) stackElement).getObject();
                }
            }
        }

        initTabFolder();
    }

    private void initTabFolder() {
        if (subPartStack.getWidget() instanceof CTabFolder) {
            tabFolder = (CTabFolder) subPartStack.getWidget();
            tabFolder.setTabPosition(SWT.BOTTOM);
            tabFolder.setBorderVisible(false);
            tabFolder.setMaximizeVisible(false);
            tabFolder.setMinimizeVisible(false);

            if (tabFolder.getItemCount() == SUB_PARTSTACK_SIZE) {
                CTabItem testSuiteMainPart = tabFolder.getItem(CHILD_TESTSUITE_MAIN_PART_INDEX);
                testSuiteMainPart.setText(MAIN_TAB_TITLE);
                testSuiteMainPart.setImage(ImageConstants.IMG_16_MAIN);
                testSuiteMainPart.setShowClose(false);

                CTabItem testSuiteIntegrationPart = tabFolder.getItem(CHILD_TESTSUITE_INTEGRATION_PART_INDEX);
                testSuiteIntegrationPart.setText(INTEGRATION_TAB_TITLE);
                testSuiteIntegrationPart.setImage(ImageConstants.IMG_16_INTEGRATION);
                testSuiteIntegrationPart.setShowClose(false);
            }

            loadTestSuite();
            isInitialized = true;
        }
    }

    private void loadTestSuite() {
        childTestSuiteMainPart.loadTestSuite(testSuite);
        childTestSuiteIntegrationPart.loadInput();
    }

    private void initListeners() {
        eventBroker.subscribe(EventConstants.TEST_SUITE_UPDATED, this);
        eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, this);
    }

    public MPart getChildMainPart() {
        return (childTestSuiteMainPart != null) ? childTestSuiteMainPart.getMPart() : null;
    }

    public MPart getChildIntegrationPart() {
        return (childTestSuiteIntegrationPart != null) ? childTestSuiteIntegrationPart.getMPart() : null;
    }

    @Override
    public List<MPart> getChildParts() {
        List<MPart> childrenParts = new ArrayList<MPart>();
        childrenParts.add(getChildMainPart());
        childrenParts.add(getChildIntegrationPart());
        return childrenParts;
    }

    public void setSelectedPart(MPart partToSelect) {
        if (subPartStack.getChildren().contains(partToSelect)) {
            subPartStack.setSelectedElement(partToSelect);
        }
    }

    private void changeOriginalTestSuite(TestSuiteEntity testSuite) {
        originalTestSuite = testSuite;
        cloneTestSuite();
    }

    private void cloneTestSuite() {
        testSuite = originalTestSuite.clone();
        testSuite.setTestSuiteGuid(originalTestSuite.getTestSuiteGuid());
        testSuite.setLastRun(originalTestSuite.getLastRun());
    }

    public void dispose() {
        MPartStack mStackPart = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
        mStackPart.getChildren().remove(compositePart);
        eventBroker.unsubscribe(this);
        childTestSuiteMainPart.dispose();
    }

    public void checkDirty() {
        boolean isDirty = false;
        for (MPart childPart : getChildParts()) {
            if (childPart.isDirty()) {
                isDirty = true;
                break;
            }
        }

        setDirty(isDirty);

        for (MPart childPart : getChildParts()) {
            childPart.setDirty(false);
        }
    }

    public void setDirty(boolean isDirty) {
        dirty.setDirty(isDirty);
    }

    @Persist
    public void onSave() {
        try {
            save();
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_UNABLE_TO_SAVE_PART);
            LoggerSingleton.logError(e);
        }
    }

    private boolean prepareForSaving() {
        return childTestSuiteMainPart.prepareForSaving();
    }

    private void afterSaving() {
        childTestSuiteMainPart.afterSaving();
    }

    @Override
    public void save() throws Exception {
        // if prepare for saving have problems then cancel save
        if (!prepareForSaving()) {
            return;
        }
        // back-up
        TestSuiteEntity temp = new TestSuiteEntity();
        TestSuiteEntityUtil.copyTestSuiteProperties(originalTestSuite, temp);
        String oldIdForDisplay = originalTestSuite.getIdForDisplay();
        TestSuiteEntityUtil.copyTestSuiteProperties(testSuite, originalTestSuite);

        try {
            TestSuiteController.getInstance().updateTestSuite(originalTestSuite);
            eventBroker.post(EventConstants.EXPLORER_REFRESH, null);

            // Send event if Test Suite name has changed
            if (!StringUtils.equalsIgnoreCase(temp.getName(), originalTestSuite.getName())) {
                eventBroker.post(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM, new Object[] { oldIdForDisplay,
                        originalTestSuite.getIdForDisplay() });
            }

            eventBroker.post(EventConstants.TEST_SUITE_UPDATED, new Object[] { testSuite.getId(), originalTestSuite });

            if (parent.isDisposed()) {
                return;
            }

            updateTestSuitePart(originalTestSuite);

            afterSaving();
        } catch (Exception e) {
            // revert to original test suite
            TestSuiteEntityUtil.copyTestSuiteProperties(temp, originalTestSuite);
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, e.getMessage());
        }
    }

    private void updateTestSuitePart(TestSuiteEntity testSuite) {
        // update mpart
        String newElementId = EntityPartUtil.getTestSuiteCompositePartId(testSuite.getId());
        if (!newElementId.equals(compositePart.getElementId())) {
            compositePart.setLabel(testSuite.getName());
            compositePart.setElementId(newElementId);
            if (compositePart.getChildren().size() == 1 && compositePart.getChildren().get(0) instanceof MPartStack) {
                MPartStack partStack = (MPartStack) compositePart.getChildren().get(0);
                partStack.setElementId(newElementId + IdConstants.TEST_SUITE_SUB_PART_STACK_ID_SUFFIX);

                childTestSuiteMainPart.getMPart().setElementId(
                        newElementId + IdConstants.TEST_SUITE_MAIN_PART_ID_SUFFIX);
                childTestSuiteIntegrationPart.getMPart().setElementId(
                        newElementId + IdConstants.TEST_SUITE_INTEGRATION_PART_ID_SUFFIX);
            }
        }
        changeOriginalTestSuite(testSuite);
        setDirty(false);
        loadTestSuite();
    }

    @Override
    public void handleEvent(Event event) {
        if (event.getTopic().equals(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM)) {
            try {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof ITreeEntity) {
                    if (object instanceof TestSuiteTreeEntity) {
                        TestSuiteTreeEntity testSuiteTreeEntity = (TestSuiteTreeEntity) object;
                        TestSuiteEntity testSuite = (TestSuiteEntity) (testSuiteTreeEntity).getObject();
                        if (testSuite != null && testSuite.getId().equals(originalTestSuite.getId())) {
                            if (TestSuiteController.getInstance().getTestSuite(testSuite.getId()) != null) {
                                if (dirty.isDirty()) {
                                    verifyTestSuiteChanged();
                                } else {
                                    updateTestSuitePart(testSuite);
                                }
                            } else {
                                dispose();
                            }
                        }
                    } else if (object instanceof FolderTreeEntity) {
                        FolderEntity folder = (FolderEntity) ((ITreeEntity) object).getObject();
                        if (folder != null
                                && FolderController.getInstance().isFolderAncestorOfEntity(folder, originalTestSuite)) {
                            if (TestSuiteController.getInstance().getTestSuite(originalTestSuite.getId()) == null) {
                                dispose();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        } else if (event.getTopic().equals(EventConstants.TEST_SUITE_UPDATED)) {
            Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
            if (object != null && object instanceof Object[]) {
                String elementId = EntityPartUtil.getTestSuiteCompositePartId((String) ((Object[]) object)[0]);

                if (elementId.equalsIgnoreCase(compositePart.getElementId())) {
                    TestSuiteEntity testSuite = (TestSuiteEntity) ((Object[]) object)[1];
                    updateTestSuitePart(testSuite);
                }
            }
        }
    }

    @PreDestroy
    public void preDestroy() {
        compositePart.getChildren().clear();
        eventBroker.unsubscribe(this);
        interuptUIThreads();
    }

    @Focus
    public void setFocus() {
        verifyTestSuiteChanged();
    }

    private void interuptUIThreads() {
        childTestSuiteMainPart.interuptUIThreads();
    }

    public TestSuiteEntity getTestSuiteClone() {
        return testSuite;
    }

    public TestSuiteEntity getOriginalTestSuite() {
        return originalTestSuite;
    }

    private void verifyTestSuiteChanged() {
        try {
            if (originalTestSuite != null) {
                TestSuiteEntity testSuiteInFile = TestSuiteController.getInstance().getTestSuite(
                        originalTestSuite.getId());
                if (testSuiteInFile != null) {
                    if (!testSuiteInFile.equals(originalTestSuite) && !isConfirmationDialogShowed) {
                        if (MessageDialog.openConfirm(
                                null,
                                StringConstants.PA_CONFIRM_TITLE_FILE_CHANGED,
                                MessageFormat.format(StringConstants.PA_CONFIRM_MSG_RELOAD_FILE,
                                        originalTestSuite.getLocation()))) {
                            updateTestSuitePart(testSuiteInFile);
                        }
                    }
                } else {
                    dispose();
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    public void openAddTestCaseDialog() {
        setSelectedPart(getChildMainPart());
        childTestSuiteMainPart.openAddTestCaseDialog();
    }
}
