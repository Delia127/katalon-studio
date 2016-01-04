package com.kms.katalon.composer.testsuite.listeners;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.entity.link.TestCaseTestDataLink;

public class TestDataTableDragListener implements DragSourceListener {
    
	private TableViewer testDataTableViewer;

    public TestDataTableDragListener(TableViewer testDataTableViewer) {
        this.testDataTableViewer = testDataTableViewer;
    }

    public void dragStart(DragSourceEvent event) {
    	TableItem[] selection = testDataTableViewer.getTable().getSelection();
        if (selection.length == 1) {
            event.doit = true;
        } else {
            event.doit = false;
        }
    }

    public void dragSetData(DragSourceEvent event) {
    	StringBuilder sb = new StringBuilder();
        TableItem[] selection = testDataTableViewer.getTable().getSelection();
        for (TableItem item : selection) {
            if (item.getData() instanceof TestCaseTestDataLink) {
            	if(sb.length() > 0){
            		sb.append("\n");
            	}
            	sb.append(((TestCaseTestDataLink)item.getData()).getTestDataId());
            }
        }
        event.data = sb.toString();
    }

    public void dragFinished(DragSourceEvent event) {
    	testDataTableViewer.refresh();
    }

}

