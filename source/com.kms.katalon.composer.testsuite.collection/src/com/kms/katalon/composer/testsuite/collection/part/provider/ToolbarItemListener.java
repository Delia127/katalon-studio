package com.kms.katalon.composer.testsuite.collection.part.provider;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.control.HotkeyActiveListener;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.collection.collector.TestExecutionGroupCollector;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.testsuite.collection.constant.StringConstants;
import com.kms.katalon.composer.testsuite.collection.dialog.TestSuiteSelectionDialog;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTestProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTestSuiteCollection;
import com.kms.katalon.integration.analytics.handler.AnalyticsGridHandler;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;

public class ToolbarItemListener extends SelectionAdapter implements HotkeyActiveListener {

    private TableViewerProvider provider;

    public ToolbarItemListener(TableViewerProvider provider) {
        this.provider = provider;
    }

    public void widgetSelected(SelectionEvent e) {
        Object source = e.getSource();
        if (!(source instanceof ToolItem)) {
            return;
        }

        String actionId = StringUtils.defaultString(((ToolItem) source).getText());
        executeAction(actionId);
    }

    @Override
    public void executeAction(String actionId) {
        switch (ActionId.parse(actionId)) {
            case ADD: {
                addTestSuiteRunConfigs();
                return;
            }
            case REMOVE: {
                deleteSelectedTestSuiteRunConfigs();
                return;
            }
            case UP: {
                moveUpSelectedTestSuiteRunConfigs();
                return;
            }
            case DOWN: {
                moveDownSelectedTestSuiteRunConfigs();
                return;
            }
            case EXECUTE: {
                executeTestRun();
                return;
            }
            case CREATE_TEST_PLAN: {
                createTestPlan();
                return;
            }
        }

    }

    private TableViewer getTableViewer() {
        return provider.getTableViewer();
    }

    private List<TestSuiteRunConfiguration> getTableItems() {
        return provider.getTableItems();
    }

    private void createTestPlan() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();

        AnalyticsSettingStore analyticsSettingStore = new AnalyticsSettingStore(currentProject.getFolderLocation());
        try {
            String password = analyticsSettingStore.getPassword(analyticsSettingStore.isEncryptionEnabled());
            String serverUrl = analyticsSettingStore.getServerEndpoint(analyticsSettingStore.isEncryptionEnabled());
            String email = analyticsSettingStore.getEmail(analyticsSettingStore.isEncryptionEnabled());
            AnalyticsProject analyticsProject = analyticsSettingStore.getProject();

            String nameFileZip = currentProject.getName();
            AnalyticsTestProject analyticsTestProject = AnalyticsGridHandler.uploadProject(serverUrl, email, password,
                    nameFileZip, analyticsProject, currentProject.getFolderLocation(),
                    new ProgressMonitorDialog(Display.getCurrent().getActiveShell()));

            TestSuiteCollectionEntity testSuiteCollection = provider.getTestSuiteCollection();
            String name = testSuiteCollection.getName();
            List<AnalyticsTestSuiteCollection> listAnalyticsTestSuiteCollection = analyticsTestProject
                    .getTestSuiteCollections();
            if (listAnalyticsTestSuiteCollection == null) {
                MultiStatusErrorDialog.showErrorDialog(StringConstants.LS_MSG_ANALYTICS_UNABLE_TO_CREATE_TEST_PLAN,
                        StringConstants.LS_MSG_ANALYTICS_TEST_PROJECT_HAVE_NO_TSC, "");
                return;
            }

            AnalyticsTestSuiteCollection analyticsTestSuiteCollection = listAnalyticsTestSuiteCollection.stream()
                    .filter(tsc -> tsc.getName().equals(testSuiteCollection.getIdForDisplay()))
                    .findFirst()
                    .orElse(null);
            if (analyticsTestSuiteCollection == null) {
                MultiStatusErrorDialog.showErrorDialog(StringConstants.LS_MSG_ANALYTICS_UNABLE_TO_CREATE_TEST_PLAN,
                        StringConstants.LS_MSG_ANALYTICS_TSC_NOT_FOUND, "");
                return;
            }

            AnalyticsGridHandler.createTestPlan(serverUrl, email, password, name, analyticsProject,
                    analyticsTestProject, analyticsTestSuiteCollection,
                    new ProgressMonitorDialog(Display.getCurrent().getActiveShell()));

        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.LS_MSG_ANALYTICS_UNABLE_TO_CREATE_TEST_PLAN,
                    e.getMessage());
        }

        return;
    }

    private void executeTestRun() {
        provider.executeTestRun();
    }

    private void addTestSuiteRunConfigs() {
        try {
            List<TestSuiteEntity> chosenTestSuites = getSelectedTestSuitesOnDialog();
            if (chosenTestSuites.isEmpty()) {
                return;
            }

            List<TestSuiteRunConfiguration> newItems = new ArrayList<>();
            for (TestSuiteEntity selectedTestSuite : chosenTestSuites) {
                if (provider.containsTestSuite(selectedTestSuite)) {
                    continue;
                }

                TestSuiteRunConfiguration newTestSuiteRunConfig = TestSuiteRunConfiguration
                        .newInstance(selectedTestSuite, TestExecutionGroupCollector.getInstance()
                                .getDefaultConfiguration(ProjectController.getInstance().getCurrentProject()));
                getTableItems().add(newTestSuiteRunConfig);
                newItems.add(newTestSuiteRunConfig);
            }

            if (newItems.isEmpty()) {
                return;
            }
            getTableViewer().refresh();
            getTableViewer().getTable().setFocus();
            getTableViewer().setSelection(new StructuredSelection(newItems));
            updateRunColumnAndMarkDirty();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.LS_MSG_UNABLE_TO_ADD_TEST_SUITE, e.getMessage());
        }
    }

    private List<TestSuiteEntity> getSelectedTestSuitesOnDialog() throws Exception {
        EntityProvider entityProvider = new EntityProvider();
        TestSuiteSelectionDialog dialog = new TestSuiteSelectionDialog(Display.getCurrent().getActiveShell(),
                new EntityLabelProvider(), new EntityProvider(), new TestSuiteViewerFilter(entityProvider));
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        dialog.setInput(
                TreeEntityUtil.getChildren(null, FolderController.getInstance().getTestSuiteRoot(currentProject)));
        if (dialog.open() != Dialog.OK) {
            return Collections.emptyList();
        }
        return flattenDialogResult(dialog.getResult());
    }

    private List<TestSuiteEntity> flattenDialogResult(Object[] dialogResult) throws Exception {
        if (dialogResult == null) {
            return Collections.emptyList();
        }

        List<TestSuiteEntity> selectedTestSuites = new ArrayList<>();
        for (Object eachResult : dialogResult) {
            if (eachResult instanceof TestSuiteTreeEntity) {
                selectedTestSuites.add((TestSuiteEntity) ((TestSuiteTreeEntity) eachResult).getObject());
            }

            if (!(eachResult instanceof FolderTreeEntity)) {
                continue;
            }
            selectedTestSuites.addAll(flattenDialogResult(((FolderTreeEntity) eachResult).getChildren()));
        }

        return selectedTestSuites;
    }

    private void deleteSelectedTestSuiteRunConfigs() {
        IStructuredSelection selection = (IStructuredSelection) getTableViewer().getSelection();
        if (selection.isEmpty()) {
            return;
        }
        getTableItems().removeAll(selection.toList());
        getTableViewer().refresh();
        updateRunColumnAndMarkDirty();
    }

    private void updateRunColumnAndMarkDirty() {
        provider.updateRunColumn();
        provider.markDirty();
    }

    private void moveDownSelectedTestSuiteRunConfigs() {
        IStructuredSelection selection = (IStructuredSelection) getTableViewer().getSelection();
        if (selection.isEmpty()) {
            return;
        }

        new ItemSwapper(getTableItems(), selection.toList()) {
            /**
             * Cannot move down the last object
             */
            @Override
            protected boolean isIndexNotValid(int selectedIndex) {
                return selectedIndex == tableItems.size() - 1;
            }

            @Override
            protected int order(int firstIndex, int secondIndex) {
                return secondIndex - firstIndex;
            }

            @Override
            protected int indexToSwap(int selectedIndex) {
                return selectedIndex + 1;
            }

        }.swap();
    }

    private void moveUpSelectedTestSuiteRunConfigs() {
        IStructuredSelection selection = (IStructuredSelection) getTableViewer().getSelection();
        if (selection.isEmpty()) {
            return;
        }

        new ItemSwapper(getTableItems(), selection.toList()) {

            /**
             * Cannot move up the first object
             */
            @Override
            protected boolean isIndexNotValid(int selectedIndex) {
                return selectedIndex == 0;
            }

            @Override
            protected int order(int firstIndex, int secondIndex) {
                return firstIndex - secondIndex;
            }

            @Override
            protected int indexToSwap(int selectedIndex) {
                return selectedIndex - 1;
            }

        }.swap();
    }

    private abstract class ItemSwapper {

        final protected List<TestSuiteRunConfiguration> tableItems;

        final private List<?> selectedObjects;

        public ItemSwapper(final List<TestSuiteRunConfiguration> tableItems, final List<?> selectedObjects) {
            this.tableItems = tableItems;
            this.selectedObjects = selectedObjects;
        }

        protected abstract boolean isIndexNotValid(int selectedIndex);

        protected abstract int order(int firstIndex, int secondIndex);

        protected abstract int indexToSwap(int selectedIndex);

        public final void swap() {
            Collections.sort(selectedObjects, new Comparator<Object>() {
                @Override
                public int compare(Object first, Object second) {
                    return order(tableItems.indexOf(first), tableItems.indexOf(second));
                }
            });

            boolean reallySwap = false;
            for (Object selectedObject : selectedObjects) {
                int selectedIndex = tableItems.indexOf(selectedObject);

                if (isIndexNotValid(selectedIndex)) {
                    continue;
                }

                int indexToSwap = indexToSwap(selectedIndex);
                if (selectedObjects.contains(tableItems.get(indexToSwap))) {
                    continue;
                }

                Collections.swap(tableItems, selectedIndex, indexToSwap);
                reallySwap = true;
            }

            if (reallySwap) {
                getTableViewer().refresh();
                provider.markDirty();
            }
        }
    }

    public enum ActionId {
        ADD(StringConstants.ADD),
        REMOVE(StringConstants.REMOVE),
        UP(StringConstants.UP),
        DOWN(StringConstants.DOWN),
        CREATE_TEST_PLAN(StringConstants.PA_ACTION_ANALYTICS_CREATE_TEST_PLAN),
        EXECUTE(StringConstants.PA_ACTION_EXECUTE_TEST_SUITE_COLLECTION);

        private final String id;

        private ActionId(final String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public static ActionId parse(String id) {
            if (id == null) {
                return null;
            }
            for (ActionId actionId : values()) {
                if (actionId.getId().equals(id)) {
                    return actionId;
                }
            }
            return null;
        }
    }
}
