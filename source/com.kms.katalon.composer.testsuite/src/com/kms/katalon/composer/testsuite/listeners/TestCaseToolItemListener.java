package com.kms.katalon.composer.testsuite.listeners;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.testsuite.constants.ToolItemConstants;
import com.kms.katalon.composer.testsuite.providers.TestCaseTableViewer;

public class TestCaseToolItemListener extends SelectionAdapter {

	private TestCaseTableViewer tableViewer;

	public TestCaseToolItemListener(TestCaseTableViewer tableViewer) {
		setTableViewer(tableViewer);
	}

	private void setTableViewer(TestCaseTableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.getSource() == null)
			return;

		if (e.getSource() instanceof ToolItem) {
			toolItemSelected(e);
		}
	}

	private void toolItemSelected(SelectionEvent e) {
		ToolItem toolItem = (ToolItem) e.getSource();
		String data = (String) toolItem.getData();

		if (data == null || data.isEmpty()) {
			return;
		}
		if (ToolItemConstants.ADD.equals(data)) {
			tableViewer.addNewItem();
			return;
		}
		if (ToolItemConstants.REMOVE.equals(data)) {
			tableViewer.removeSelectedItems();
			return;
		}
		if (ToolItemConstants.UP.equals(data)) {
			tableViewer.moveSelectedItemsUp();
			return;
		}
		if (ToolItemConstants.DOWN.equals(data)) {
			tableViewer.moveSelectedItemsDown();
		}
	}
}
