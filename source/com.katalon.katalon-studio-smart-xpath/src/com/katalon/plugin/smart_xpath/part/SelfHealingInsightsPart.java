package com.katalon.plugin.smart_xpath.part;

import javax.annotation.PostConstruct;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.katalon.plugin.smart_xpath.constant.SmartXPathMessageConstants;
import com.kms.katalon.composer.components.impl.util.ControlUtils;

public class SelfHealingInsightsPart {
	private TableViewer tableViewer;

	private Table table;

	@PostConstruct
	public void init(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		createSelfHealingObjectsTable(parent);
	}

	private void createSelfHealingObjectsTable(Composite parent) {
		Composite compositeTable = new Composite(parent, SWT.BORDER);
		compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		tableViewer = new TableViewer(compositeTable, SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));

		TableViewerColumn tbViewerColumnObject = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnColumnObject = tbViewerColumnObject.getColumn();
		tblclmnColumnObject.setText(SmartXPathMessageConstants.TEST_OBJECT_ID_COLUMN);

		TableViewerColumn tbViewerColumnBrokenLocator = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnColumnBrokenLocator = tbViewerColumnBrokenLocator.getColumn();
		tblclmnColumnBrokenLocator.setText(SmartXPathMessageConstants.BROKEN_LOCATOR_COLUMN);

		TableViewerColumn tbViewerColumnProposedLocator = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnColumnProposedLocator = tbViewerColumnObject.getColumn();
		tblclmnColumnProposedLocator.setText(SmartXPathMessageConstants.PROPOSED_LOCATOR_COLUMN);

		TableViewerColumn tbViewerColumnRecoveredBy = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnColumnRecoveredBy = tbViewerColumnRecoveredBy.getColumn();
		tblclmnColumnObject.setText(SmartXPathMessageConstants.RECOVER_BY_COLUMN);

		TableViewerColumn tbViewerColumnImage = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnColumnImage = tbViewerColumnImage.getColumn();
		tblclmnColumnObject.setText(SmartXPathMessageConstants.IMAGE_COLUMN);

		TableViewerColumn tbViewerColumnApprove = new TableViewerColumn(tableViewer, SWT.CHECK);
		TableColumn tblclmnColumnApprove = tbViewerColumnApprove.getColumn();
		tblclmnColumnObject.setText(SmartXPathMessageConstants.APPROVE_COLUMN);

		TableColumnLayout tableComColumnLayout = new TableColumnLayout();
		compositeTable.setLayout(tableComColumnLayout);
		tableComColumnLayout.setColumnData(tblclmnColumnObject, new ColumnWeightData(0, 250));
		tableComColumnLayout.setColumnData(tblclmnColumnBrokenLocator, new ColumnWeightData(0, 150));
		tableComColumnLayout.setColumnData(tblclmnColumnProposedLocator, new ColumnWeightData(0, 100));
		tableComColumnLayout.setColumnData(tblclmnColumnRecoveredBy, new ColumnWeightData(0, 100));
		tableComColumnLayout.setColumnData(tblclmnColumnImage, new ColumnWeightData(0, 100));
		tableComColumnLayout.setColumnData(tblclmnColumnApprove, new ColumnWeightData(0, 50));
	}
}
