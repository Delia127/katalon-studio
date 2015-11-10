package com.kms.katalon.composer.testsuite.listeners;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.entity.link.TestCaseTestDataLink;

public class TestDataTableDragListener implements DragSourceListener {
    private TableViewer testCaseTableViewer;

    public TestDataTableDragListener(TableViewer testCaseTableViewer) {
        this.testCaseTableViewer = testCaseTableViewer;
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
        TestCaseTestDataLink[] linkTransfers = new TestCaseTestDataLink[1];
        TableItem[] selection = testCaseTableViewer.getTable().getSelection();
        for (TableItem item : selection) {
            if (item.getData() instanceof TestCaseTestDataLink) {
                linkTransfers[0] = (TestCaseTestDataLink) item.getData();
            }
        }
        event.data = linkTransfers;
    }

    public void dragFinished(DragSourceEvent event) {
        testCaseTableViewer.refresh();
    }

}
