package com.kms.katalon.composer.integration.jira.report;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.integration.jira.constant.ComposerJiraIntegrationMessageConstant;
import com.kms.katalon.composer.integration.jira.constant.ImageConstants;
import com.kms.katalon.composer.integration.jira.constant.StringConstants;
import com.kms.katalon.composer.report.lookup.LogRecordLookup;
import com.kms.katalon.composer.report.parts.integration.IntegrationTestCaseColumnLabelProvider;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.integration.jira.JiraIntegrationException;
import com.kms.katalon.integration.jira.JiraObjectToEntityConverter;
import com.kms.katalon.integration.jira.entity.JiraIssueCollection;
import com.kms.katalon.integration.jira.entity.JiraReport;

public class JiraTestCaseIssueLabelProvider extends IntegrationTestCaseColumnLabelProvider {

    private JiraReportTestCaseColumnView view;

    public JiraTestCaseIssueLabelProvider(int columnIndex, JiraReportTestCaseColumnView view) {
        super(columnIndex);
        this.view = view;
    }

    @Override
    public void initialize(ColumnViewer viewer, ViewerColumn column) {
        super.initialize(viewer, column);
        registerMouseListener(viewer);
    }

    private void registerMouseListener(ColumnViewer viewer) {
        Control table = getTable(viewer);

        final MouseClickedListener mouseListener = new MouseClickedListener();
        table.addMouseListener(mouseListener);

        final MoveMouseOnHyperLinkListener mouseMoveListener = new MoveMouseOnHyperLinkListener();
        table.addMouseMoveListener(mouseMoveListener);
        table.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                Control control = (Control) e.getSource();
                control.removeMouseListener(mouseListener);
                control.removeMouseMoveListener(mouseMoveListener);
            }
        });
    }

    private final class MouseClickedListener extends MouseAdapter {

        @Override
        public void mouseDown(MouseEvent e) {
            Point point = new Point(e.x, e.y);
            ViewerCell cell = getViewer().getCell(point);
            if (!isPlacedHyperLinkCell(cell)) {
                return;
            }

            Rectangle rect = cell.getBounds();
            if (!rect.contains(point)) {
                return;
            }
            TestCaseLogRecord logRecord = (TestCaseLogRecord) cell.getElement();
            Shell activeShell = e.display.getActiveShell();
            JiraLinkedIssuesDialog dialog = new JiraLinkedIssuesDialog(activeShell, getJiraIssueCollection(logRecord),
                    logRecord);
            if (dialog.open() != JiraLinkedIssuesDialog.OK || !dialog.isChanged()) {
                return;
            }
            JiraReport jiraReport = JiraObjectToEntityConverter.getJiraReport(view.getReportEntity());
            jiraReport.getIssueCollectionMap().put(getTestCaseLogRecordIndex(logRecord, view.getReportEntity()),
                    dialog.getJiraIssueCollection());
            try {
                JiraObjectToEntityConverter.updateJiraReport(jiraReport, view.getReportEntity());
            } catch (JiraIntegrationException ex) {
                MessageDialog.openError(activeShell, StringConstants.ERROR, ex.getMessage());
            }
        }
    }

    @Override
    protected void drawCellTextAndImage(Event event, ViewerCell cell, GC gc) {
        cell.setImage(cell.getBounds().contains(cell.getControl().toControl(event.display.getCursorLocation()))
                ? getHoveredImage() : getDefaultImage((TestCaseLogRecord) cell.getElement()));
        super.drawCellTextAndImage(event, cell, gc);
    }

    private Image getHoveredImage() {
        return ImageConstants.IMG_ISSUE_HOVER_IN;
    }

    private Image getDefaultImage(TestCaseLogRecord logRecord) {
        return getJiraIssueCollection(logRecord).getIssues().isEmpty() ? ImageConstants.IMG_ISSUE_HOVER_OUT
                : getHoveredImage();
    }

    private JiraIssueCollection getJiraIssueCollection(TestCaseLogRecord logRecord) {
        ReportEntity reportEntity = view.getReportEntity();
        return JiraObjectToEntityConverter
                .getOptionalJiraIssueCollection(reportEntity, getTestCaseLogRecordIndex(logRecord, reportEntity))
                .map(jiraIssue -> jiraIssue)
                .orElse(new JiraIssueCollection(logRecord.getId()));
    }

    private int getTestCaseLogRecordIndex(TestCaseLogRecord logRecord, ReportEntity reportEntity) {
        return LogRecordLookup.getInstance().getTestSuiteLogRecord(reportEntity).getChildIndex(logRecord);
    }

    private final class MoveMouseOnHyperLinkListener implements MouseMoveListener {
        private ViewerCell lastFocusedIn;

        private Cursor newCursor() {
            return new Cursor(getTable(getViewer()).getDisplay(), SWT.CURSOR_HAND);
        }

        @Override
        public void mouseMove(MouseEvent e) {
            ColumnViewer viewer = getViewer();
            ViewerCell cell = viewer.getCell(new Point(e.x, e.y));
            try {
                Control table = getTable(viewer);
                if (!isPlacedHyperLinkCell(cell)) {
                    table.setCursor(null);
                    cell = null;
                    return;
                }

                if (table.getCursor() == null) {
                    table.setCursor(newCursor());
                }
            } finally {
                if (lastFocusedIn == cell) {
                    return;
                }
                if (lastFocusedIn != null) {
                    getViewer().refresh(lastFocusedIn.getElement());
                }
                if (cell != null) {
                    getViewer().refresh(cell.getElement());
                }
                lastFocusedIn = cell;
            }
        }
    }

    private Control getTable(ColumnViewer viewer) {
        return viewer.getControl();
    }

    private boolean isPlacedHyperLinkCell(ViewerCell cell) {
        return cell != null && cell.getColumnIndex() == columnIndex;
    }

    @Override
    protected String getElementToolTipText(TestCaseLogRecord element) {
        return ComposerJiraIntegrationMessageConstant.TOOLTIP_CLICK_TO_MANAGE_JIRA_ISSUES;
    }
}
