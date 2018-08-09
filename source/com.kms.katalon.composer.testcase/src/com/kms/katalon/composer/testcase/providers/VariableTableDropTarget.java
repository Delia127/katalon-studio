package com.kms.katalon.composer.testcase.providers;

import java.util.Arrays;

import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.composer.testcase.ast.variable.operations.MoveVariableOperation;
import com.kms.katalon.composer.testcase.parts.VariableTableActionOperator;

public class VariableTableDropTarget extends DropTargetAdapter {

    private VariableTableActionOperator operator;

    public VariableTableDropTarget(VariableTableActionOperator operator) {
       this.operator = operator;
    }

    @Override
    public void drop(DropTargetEvent event) {
        TableItem item = (TableItem) event.item;
        int newIndex = Arrays.asList(operator.getTableViewer().getTable().getItems()).indexOf(item);
        String index = (String) event.data;
        if (index != null && newIndex >= 0) {
            int indexVal = Integer.parseInt(index);
            operator.executeOperation(new MoveVariableOperation(operator, indexVal, newIndex));
        }
    }
}
