package com.kms.katalon.composer.integration.analytics.testops.utils;

import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.integration.analytics.testops.constants.TestOpsStringConstants;
import com.kms.katalon.integration.analytics.entity.AnalyticsExecution;
import com.kms.katalon.integration.analytics.entity.AnalyticsExecutionStatus;
import com.kms.katalon.integration.analytics.entity.AnalyticsTestSuiteResource;

public class ExecutionViewer extends TableViewer {

	protected static final int COLUMN_INDEX_STATUS = 0;
	protected static final int COLUMN_INDEX_ID = 1;
	protected static final int COLUMN_INDEX_NAME = 2;
	protected static final int COLUMN_INDEX_DURATION = 3;
	private static int DEFAULT_COLUMN_WIDTH = 50;
	private static final String DURATION_UNIT_MILLISECOND = "ms";
	private static final String DURATION_UNIT_SECOND = "s";
	private static final String DURATION_UNIT_MINUTE = "m";

	public ExecutionViewer(Composite parent, int style) {
		super(parent, style);
		initViewer();
	}

	public ExecutionViewer(Composite parent) {
		super(parent);
		initViewer();
	}

	private void initViewer() {
		Table table = this.getTable();
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		table.setLayoutData(gridData);
		createColumns();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		setTableToolTip();
	}

	private void createColumns() {
		String[] titles = { TestOpsStringConstants.EXECUTION_STATUS, TestOpsStringConstants.EXECUTION_ID,
				TestOpsStringConstants.EXECUTION_NAME, TestOpsStringConstants.EXECUTION_DURATION };
		int[] bounds = { DEFAULT_COLUMN_WIDTH, DEFAULT_COLUMN_WIDTH, 9 * DEFAULT_COLUMN_WIDTH,
				3 * DEFAULT_COLUMN_WIDTH };

		for (int i = 0; i < titles.length; i++) {
			createTableViewerColumn(titles[i], bounds[i], i);
		}

	}

	private TableViewerColumn createTableViewerColumn(String header, int width, int idx) {
		TableViewerColumn column = new TableViewerColumn(this, SWT.LEFT, idx);
		column.getColumn().setText(header);
		column.getColumn().setWidth(width);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		return column;
	}

	public void populateData(List<AnalyticsExecution> executions) {
		if (executions == null) {
			return;
		}

		for (int i = 0; i < executions.size(); i++) {
			TableItem tableItem = new TableItem(this.getTable(), SWT.NONE, i);
			AnalyticsExecution exe = executions.get(i);
			String[] items = { StringUtils.EMPTY, new Long(exe.getOrder()).toString(),
					getTestSuiteName(exe.getExecutionTestSuiteResources()), formatDuration(exe.getDuration()), };
			tableItem.setText(items);

			if (exe.getStatus() == AnalyticsExecutionStatus.PASSED) {
				tableItem.setImage(COLUMN_INDEX_STATUS, ImageConstants.IMG_16_TESTOPS_EXECUTION_PASSED);
			} else if (exe.getStatus() == AnalyticsExecutionStatus.FAILED) {
				tableItem.setImage(COLUMN_INDEX_STATUS, ImageConstants.IMG_16_TESTOPS_EXECUTION_FAILED);
			}

			Link link = new Link(tableItem.getParent(), SWT.NONE);
			// Override default gray color of Link control
			link.setBackground(new Color(link.getDisplay(), new RGB(255, 255, 255)));
			link.setText(" <a href=\"" + exe.getWebUrl() + "\">" + exe.getOrder() + "</a>");
			link.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Program.launch(exe.getWebUrl());
				}
			});

		    tableItem.addDisposeListener(new DisposeListener() {
                
                @Override
                public void widgetDisposed(DisposeEvent e) {
                    link.dispose();
                }
            });
		    
			TableEditor editor = new TableEditor(tableItem.getParent());
			editor.grabHorizontal = true;
			editor.grabVertical = true;
			editor.setEditor(link, tableItem, COLUMN_INDEX_ID);
			editor.layout();
			
		}

	}

	private void setTableToolTip() {
		Table tblExecutions = this.getTable();
		ExecutionViewerListener tableListener = new ExecutionViewerListener(tblExecutions);
		tblExecutions.addListener(SWT.Dispose, tableListener);
		tblExecutions.addListener(SWT.KeyDown, tableListener);
		tblExecutions.addListener(SWT.MouseMove, tableListener);
		tblExecutions.addListener(SWT.MouseHover, tableListener);
	}
	
	private String formatDuration(Long durationInMillisecond) {
		if (durationInMillisecond < 100) {
			return durationInMillisecond.toString() + DURATION_UNIT_MILLISECOND;
		}
		long milli = durationInMillisecond % 1000;
		long second = durationInMillisecond / 1000;
		long minute = second / 60;
		second = second % 60;
		if (milli > 500) {
			second++;
		}

		String minutePart = StringUtils.EMPTY;
		String secondPart = StringUtils.EMPTY;

		if (minute > 0) {
			minutePart = minute + DURATION_UNIT_MINUTE;
		}

		if (second > 0) {
			secondPart = second + DURATION_UNIT_SECOND;
		}

		return (minutePart + " " + secondPart).trim();
	}

	private String getTestSuiteName(AnalyticsTestSuiteResource[] resources) {
		StringBuffer name = new StringBuffer();
		String delemiter = " | ";
		for (AnalyticsTestSuiteResource resource : resources) {
			name.append(resource.getTestSuite().getName() + delemiter);
		}

		if (name.length() != 0) {
			name.delete(name.length() - delemiter.length(), name.length() - 1);
		}

		return name.toString();
	}

}

class ExecutionViewerListener implements Listener {
	private Shell tooltip;
	private Label lblTip;
	private Table tblExecutions;
	
	public ExecutionViewerListener(Table table) {
		this.tblExecutions = table;
		tooltip = null;
		lblTip = null;
	}
	
	@Override
	public void handleEvent(Event event) {
		switch (event.type) {
		case SWT.Dispose:
		case SWT.KeyDown:
		case SWT.MouseMove: {
			disposeTooltip();
			break;
		}

		case SWT.MouseHover: {
			TableItem item = tblExecutions.getItem(new Point(event.x, event.y));
			if (item == null) {
				break;
			}

			Point point = new Point(event.x, event.y);
			int column = 0;
			for (int i = 0; i < tblExecutions.getColumnCount(); i++) {
				if (item.getBounds(i).contains(point)) {
					column = i;
					break;
				}
			}

			switch (column) {
			case ExecutionViewer.COLUMN_INDEX_STATUS:
				showStatusTooltip(item);
				break;

			default:
				break;
			}

		}
		}
	}
	
	private void disposeTooltip() {
		if (tooltip != null && !tooltip.isDisposed()) {
			tooltip.setVisible(false);
			tooltip.dispose();
			tooltip = null;
		}
		
		if(lblTip != null && !lblTip.isDisposed()) {
			lblTip.dispose();
			lblTip = null;
		}
	}
	
	private void showStatusTooltip(TableItem item) {
		if (tooltip != null && !tooltip.isDisposed()) {
			tooltip.dispose();
		}
		
		tooltip = new Shell(tblExecutions.getDisplay(), SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
		FillLayout layout = new FillLayout();
		layout.marginWidth = 2;
		tooltip.setLayout(layout);
		lblTip = new Label(tooltip, SWT.NONE);
		if (item.getImage().equals(ImageConstants.IMG_16_TESTOPS_EXECUTION_PASSED)) {
			lblTip.setText(TestOpsStringConstants.LBL_EXECUTION_STATUS_PASSED);
		} else {
			lblTip.setText(TestOpsStringConstants.LBL_EXECUTION_STATUS_FAILED);
		}

		Point size = tooltip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Rectangle rect = item.getBounds(ExecutionViewer.COLUMN_INDEX_STATUS);
		Point pt = tblExecutions.toDisplay(rect.x, rect.y);
		tooltip.setBounds(pt.x, pt.y, size.x, size.y);
		tooltip.setVisible(true);
	}
	
}