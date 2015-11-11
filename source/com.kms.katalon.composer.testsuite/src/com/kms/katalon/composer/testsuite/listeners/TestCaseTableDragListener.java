package com.kms.katalon.composer.testsuite.listeners;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.composer.testsuite.parts.TestSuitePartTestCaseView;
import com.kms.katalon.composer.testsuite.providers.TestCaseTableViewer;
import com.kms.katalon.composer.testsuite.transfer.TestSuiteTestCaseLinkTransferData;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;

public class TestCaseTableDragListener implements DragSourceListener {

    private TestCaseTableViewer testCaseTableViewer;
    private TestSuitePartTestCaseView view;

    public TestCaseTableDragListener(TestCaseTableViewer testCaseTableViewer, TestSuitePartTestCaseView view) {
        this.testCaseTableViewer = testCaseTableViewer;
        this.view = view;
    }

    public void dragStart(DragSourceEvent event) {
        TableItem[] selection = testCaseTableViewer.getTable().getSelection();
        if (selection.length == 1) {
            event.doit = true;
        } else {
            event.doit = false;
        }
    };

    public void dragSetData(DragSourceEvent event) {
        List<TestSuiteTestCaseLinkTransferData> testSuiteTestCaseLinkTransferDatas = 
                new ArrayList<TestSuiteTestCaseLinkTransferData>();
        TableItem[] selection = testCaseTableViewer.getTable().getSelection();
        for (TableItem item : selection) {
            if (item.getData() instanceof TestSuiteTestCaseLink) {
                testSuiteTestCaseLinkTransferDatas.add(new TestSuiteTestCaseLinkTransferData(view.getTestSuite(),
                        (TestSuiteTestCaseLink) item.getData()));
            }
        }
        event.data = testSuiteTestCaseLinkTransferDatas
                .toArray(new TestSuiteTestCaseLinkTransferData[testSuiteTestCaseLinkTransferDatas.size()]);
    }

    public void dragFinished(DragSourceEvent event) {
        testCaseTableViewer.refresh();
    }
}
