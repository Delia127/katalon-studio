package com.kms.katalon.composer.testcase.providers;

import java.util.Arrays;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.composer.testcase.ast.variable.operations.MoveVariableOperation;
import com.kms.katalon.composer.testcase.parts.TestCaseVariablePart;

public class VariableTableDropTarget extends DropTargetAdapter {
    private TestCaseVariablePart testCaseVariablePart;

    private TableViewer tableViewer;

    public VariableTableDropTarget(TestCaseVariablePart testCaseVariablePart) {
        this.testCaseVariablePart = testCaseVariablePart;
        this.tableViewer = testCaseVariablePart.getTableViewer();
    }

    @Override
    public void drop(DropTargetEvent event) {
        TableItem item = (TableItem) event.item;
        int newIndex = Arrays.asList(tableViewer.getTable().getItems()).indexOf(item);
        String index = (String) event.data;
        if (index != null && newIndex >= 0) {
            int indexVal = Integer.parseInt(index);
            testCaseVariablePart.executeOperation(new MoveVariableOperation(testCaseVariablePart, indexVal, newIndex));
        }
    }
}
