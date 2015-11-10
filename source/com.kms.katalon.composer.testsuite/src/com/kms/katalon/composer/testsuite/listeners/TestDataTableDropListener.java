package com.kms.katalon.composer.testsuite.listeners;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TableDropTargetEffect;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.composer.testsuite.parts.TestSuitePartDataBindingView;
import com.kms.katalon.entity.link.TestCaseTestDataLink;

public class TestDataTableDropListener extends TableDropTargetEffect {
    private TableViewer fTableViewer;
    private TestSuitePartDataBindingView fView;

    public TestDataTableDropListener(TableViewer tableViewer, TestSuitePartDataBindingView view) {
        super(tableViewer.getTable());
        fTableViewer = tableViewer;
        fView = view;
    }

    @Override
    public void drop(DropTargetEvent event) {
        event.detail = DND.DROP_COPY;
        List<TestCaseTestDataLink> input = fView.getSelectedTestCaseLink().getTestDataLinks();
        Point pt = Display.getCurrent().map(null, fTableViewer.getTable(), event.x, event.y);
        TableItem tableItem = fTableViewer.getTable().getItem(pt);
        TestCaseTestDataLink selectedItem = (tableItem != null && tableItem.getData() instanceof TestCaseTestDataLink) ? (TestCaseTestDataLink) tableItem
                .getData() : null;
        int selectedIndex = (selectedItem != null) ? input.indexOf(selectedItem) : input.size() - 1;
        if (event.data instanceof TestCaseTestDataLink[]) {
            TestCaseTestDataLink link = ((TestCaseTestDataLink[]) event.data)[0];
            int previousIndex = input.indexOf(link);
            if (previousIndex == selectedIndex) {
                return;
            }

            Collections.swap(input, selectedIndex, previousIndex);
            fTableViewer.refresh();
            fView.refreshVariableTable();
            fTableViewer.setSelection(new StructuredSelection(link));
            fView.setDirty(true);
        }
    }
}
