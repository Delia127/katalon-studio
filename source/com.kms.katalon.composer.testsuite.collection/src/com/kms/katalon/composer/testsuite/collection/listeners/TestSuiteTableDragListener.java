package com.kms.katalon.composer.testsuite.collection.listeners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import com.kms.katalon.composer.testsuite.collection.part.TestSuiteCollectionPart;
import com.kms.katalon.composer.testsuite.collection.transfer.TestSuiteRunConfigurationTransferData;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;

public class TestSuiteTableDragListener implements DragSourceListener {

    private TestSuiteCollectionPart testsuiteCollectionPart;

    private List<TestSuiteRunConfiguration> previousSelection;

    public TestSuiteTableDragListener(TestSuiteCollectionPart testsuiteCollectionPart) {
        this.testsuiteCollectionPart = testsuiteCollectionPart;
    }

    @Override
    public void dragStart(DragSourceEvent event) {
        event.doit = !(testsuiteCollectionPart.getTableViewer().getSelection().isEmpty());
    }

    @Override
    public void dragSetData(DragSourceEvent event) {
        previousSelection = new ArrayList<TestSuiteRunConfiguration>();
        List<TestSuiteRunConfigurationTransferData> testSuiteRunConfigurationTransferDatas = new ArrayList<TestSuiteRunConfigurationTransferData>();
        StructuredSelection selection = (StructuredSelection) testsuiteCollectionPart.getTableViewer().getSelection();
        TestSuiteCollectionEntity testSuiteCollection = testsuiteCollectionPart.getTestSuiteCollection();
        for (Object item : selection.toList()) {
            if (item instanceof TestSuiteRunConfiguration) {
                TestSuiteRunConfiguration testSuiteRunConfiguration = (TestSuiteRunConfiguration) item;
                testSuiteRunConfigurationTransferDatas.add(new TestSuiteRunConfigurationTransferData(
                        testSuiteCollection, testSuiteRunConfiguration));
                previousSelection.add(testSuiteRunConfiguration);
            }
        }
        event.data = testSuiteRunConfigurationTransferDatas.toArray(new TestSuiteRunConfigurationTransferData[testSuiteRunConfigurationTransferDatas.size()]);
    }

    @Override
    public void dragFinished(DragSourceEvent event) {
        if (event.detail == DND.DROP_COPY) {
            testsuiteCollectionPart.getTableViewer().refresh();
            return;
        }
        if (event.detail == DND.DROP_MOVE) {
            testsuiteCollectionPart.getTableItems().removeAll(previousSelection);
            testsuiteCollectionPart.getTableViewer().refresh();
        }
    }

}
