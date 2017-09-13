package com.kms.katalon.composer.testsuite.collection.listeners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TableDropTargetEffect;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.execution.collection.collector.TestExecutionGroupCollector;
import com.kms.katalon.composer.testsuite.collection.part.provider.TableViewerProvider;
import com.kms.katalon.composer.testsuite.collection.transfer.TestSuiteRunConfigurationTransferData;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;

public class TestSuiteTableDropListener extends TableDropTargetEffect {

    private TableViewerProvider tableViewerProvider;

    public TestSuiteTableDropListener(TableViewerProvider testSuiteCollectionPart) {
        super(testSuiteCollectionPart.getTableViewer().getTable());
        this.tableViewerProvider = testSuiteCollectionPart;
    }

    @Override
    public void drop(DropTargetEvent event) {
        TestSuiteRunConfiguration selectedItem = getSelectedItem(event);

        Object data = event.data;
        if (data instanceof ITreeEntity[]) {
            event.detail = DND.DROP_COPY;
            dropFromTreeToTable((ITreeEntity[]) data, selectedItem);
            return;
        }
        if (data instanceof TestSuiteRunConfigurationTransferData[]) {
            TestSuiteRunConfigurationTransferData[] configurationTransferDatas = (TestSuiteRunConfigurationTransferData[]) data;
            if (!configurationTransferDatas[0].getTestSuiteCollectionID()
                    .equals(tableViewerProvider.getTestSuiteCollection().getId())) {
                event.detail = DND.DROP_COPY;
                dropFromTableToTable(configurationTransferDatas, selectedItem);
            } else {
                event.detail = DND.DROP_MOVE;
                dropInsideTable(configurationTransferDatas, selectedItem);
            }
        }
    }

    private TestSuiteRunConfiguration getSelectedItem(DropTargetEvent event) {
        TableViewer testSuiteCollectionTable = tableViewerProvider.getTableViewer();
        Point pt = Display.getCurrent().map(null, testSuiteCollectionTable.getTable(), event.x, event.y);
        TableItem tableItem = testSuiteCollectionTable.getTable().getItem(pt);
        TestSuiteRunConfiguration selectedItem = (tableItem != null
                && tableItem.getData() instanceof TestSuiteRunConfiguration)
                        ? (TestSuiteRunConfiguration) tableItem.getData() : null;
        return selectedItem;
    }

    private void dropFromTableToTable(TestSuiteRunConfigurationTransferData[] configurationTransferDatas,
            TestSuiteRunConfiguration selectedItem) {
        List<TestSuiteRunConfiguration> addedTestSuiteRunConfiguration = new ArrayList<TestSuiteRunConfiguration>();
        TestSuiteController testSuiteController = TestSuiteController.getInstance();
        for (int i = 0; i < configurationTransferDatas.length; ++i) {
            TestSuiteRunConfiguration testSuiteRunConfiguration = configurationTransferDatas[i]
                    .getTestSuiteRunConfiguration();
            try {
                testSuiteRunConfiguration.setTestSuiteEntity(
                        testSuiteController.getTestSuite(testSuiteRunConfiguration.getTestSuiteEntity().getId()));
                tableViewerProvider.getTableItems().add(testSuiteRunConfiguration);
                addedTestSuiteRunConfiguration.add(testSuiteRunConfiguration);
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
        refreshTestSuiteTable(addedTestSuiteRunConfiguration);
    }

    private void refreshTestSuiteTable(List<TestSuiteRunConfiguration> addedTestSuiteRunConfiguration) {
        tableViewerProvider.getTableViewer().refresh();
        tableViewerProvider.getTableViewer().setSelection(new StructuredSelection(addedTestSuiteRunConfiguration));
        tableViewerProvider.getTableViewer().getTable().setFocus();
        tableViewerProvider.markDirty();
    }

    private void dropInsideTable(TestSuiteRunConfigurationTransferData[] testSuiteRunConfigurationTransferDatas,
            TestSuiteRunConfiguration selectedItem) {
        int selectedIndex = getSelectedItemIndex(selectedItem);
        int cloneSelectedIndex = selectedIndex;

        List<TestSuiteRunConfiguration> addedTestSuiteRunConfiguration = new ArrayList<TestSuiteRunConfiguration>();
        TestSuiteController controller = TestSuiteController.getInstance();
        for (int i = 0; i < testSuiteRunConfigurationTransferDatas.length; ++i) {
            TestSuiteRunConfiguration data = testSuiteRunConfigurationTransferDatas[i].getTestSuiteRunConfiguration();
            try {
                data.setTestSuiteEntity(controller.getTestSuite(data.getTestSuiteEntity().getId()));
                insertTestSuiteRunConfiguration(data, cloneSelectedIndex);
                addedTestSuiteRunConfiguration.add(data);
                ++cloneSelectedIndex;
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
        refreshTestSuiteTable(addedTestSuiteRunConfiguration);
    }

    private int getSelectedItemIndex(TestSuiteRunConfiguration selectedItem) {
        List<TestSuiteRunConfiguration> testSuiteRunConfigurations = tableViewerProvider.getTableItems();
        int selectedIndex = (selectedItem != null) ? testSuiteRunConfigurations.indexOf(selectedItem) + 1
                : testSuiteRunConfigurations.size();
        return selectedIndex;
    }

    private void dropFromTreeToTable(ITreeEntity[] treeEntities, TestSuiteRunConfiguration selectedItem) {
        int selectedIndex = getSelectedItemIndex(selectedItem);
        try {
            List<TestSuiteRunConfiguration> addedTestSuiteRunConfiguration = new ArrayList<TestSuiteRunConfiguration>();
            for (int i = treeEntities.length - 1; i >= 0; --i) {
                if (treeEntities[i] instanceof TestSuiteTreeEntity) {
                    insertTestSuite(addedTestSuiteRunConfiguration, ((TestSuiteTreeEntity) treeEntities[i]).getObject(),
                            selectedIndex);
                } else if (treeEntities[i] instanceof FolderTreeEntity) {
                    for (TestSuiteEntity testSuite : getTestSuiteFromFolderTree((FolderTreeEntity) treeEntities[i])) {
                        insertTestSuite(addedTestSuiteRunConfiguration, testSuite, selectedIndex);
                    }
                }
            }
            refreshTestSuiteTable(addedTestSuiteRunConfiguration);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private void insertTestSuite(List<TestSuiteRunConfiguration> addedTestSuiteRunConfiguration,
            TestSuiteEntity testSuite, int index) {
        if (isAlreadyAddedToTestSuiteCollection(addedTestSuiteRunConfiguration, testSuite)) {
            return;
        }
        try {
            TestSuiteEntity testSuiteEntity = TestSuiteController.getInstance().getTestSuite(testSuite.getId());
            TestSuiteRunConfiguration newTestSuiteRunConfig = TestSuiteRunConfiguration.newInstance(testSuiteEntity,
                    TestExecutionGroupCollector.getInstance().getDefaultConfiguration());
            insertTestSuiteRunConfiguration(newTestSuiteRunConfig, index);
            addedTestSuiteRunConfiguration.add(newTestSuiteRunConfig);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private void insertTestSuiteRunConfiguration(TestSuiteRunConfiguration configuration, int index) {
        if (index < 0 || index >= tableViewerProvider.getTableItems().size()) {
            tableViewerProvider.getTableItems().add(configuration);
            return;
        }
        tableViewerProvider.getTableItems().add(index, configuration);
    }

    private List<TestSuiteEntity> getTestSuiteFromFolderTree(FolderTreeEntity folderTree) {
        List<TestSuiteEntity> testSuitesList = new ArrayList<TestSuiteEntity>();
        try {
            for (Object child : folderTree.getChildren()) {
                if (child instanceof TestSuiteTreeEntity) {
                    testSuitesList.add(((TestSuiteTreeEntity) child).getObject());
                } else if (child instanceof FolderTreeEntity) {
                    testSuitesList.addAll(getTestSuiteFromFolderTree((FolderTreeEntity) child));
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return testSuitesList;
    }

    private boolean isAlreadyAddedToTestSuiteCollection(List<TestSuiteRunConfiguration> addedTestSuiteRunConfiguration,
            TestSuiteEntity testSuiteEntity) {
        for (TestSuiteRunConfiguration configuration : addedTestSuiteRunConfiguration) {
            if (configuration.getTestSuiteEntity().equals(testSuiteEntity)) {
                return true;
            }
        }
        return false;
    }

}
