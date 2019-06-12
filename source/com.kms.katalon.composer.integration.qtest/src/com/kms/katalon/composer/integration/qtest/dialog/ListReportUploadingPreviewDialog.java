package com.kms.katalon.composer.integration.qtest.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.dialog.provider.TestCaseResultPreviewUploadedLableProvider;
import com.kms.katalon.composer.integration.qtest.dialog.support.TestCaseResultAttachmentEditingSupport;
import com.kms.katalon.composer.integration.qtest.dialog.support.TestCaseResultMessageEditingSupport;
import com.kms.katalon.integration.qtest.entity.QTestLogUploadedPreview;

public class ListReportUploadingPreviewDialog extends Dialog {

    private List<QTestLogUploadedPreview> input;

    private Composite container;
    private TableViewerColumn tableViewerColumnName;
    private TableViewerColumn tableViewerColumnAttachment;
    private TableViewerColumn tableViewerColumnMessage;
    private TableViewer tableViewer;

    public ListReportUploadingPreviewDialog(Shell parentShell, List<QTestLogUploadedPreview> input) {
        super(parentShell);
        setInput(input);
    }

    protected Control createDialogArea(Composite parent) {
        container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FillLayout(SWT.HORIZONTAL));

        Composite compositeTable = new Composite(container, SWT.NONE);

        tableViewer = new TableViewer(compositeTable, SWT.BORDER | SWT.FULL_SELECTION);
        Table table = tableViewer.getTable();
        table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));
        table.setHeaderVisible(true);

        TableViewerColumn tableViewerColumnNo = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNo = tableViewerColumnNo.getColumn();
        tblclmnNo.setText(StringConstants.NO_);

        tableViewerColumnName = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnName = tableViewerColumnName.getColumn();
        tblclmnName.setText(StringConstants.NAME);

        tableViewerColumnAttachment = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnAttachment = tableViewerColumnAttachment.getColumn();
        tblclmnAttachment.setText(StringConstants.ATTACHMENT);

        tableViewerColumnMessage = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnMessage = tableViewerColumnMessage.getColumn();
        tblclmnMessage.setText(StringConstants.MESSAGE);

        TableColumnLayout tableLayout = new TableColumnLayout();
        tableLayout.setColumnData(tblclmnNo, new ColumnWeightData(0, 50));
        tableLayout.setColumnData(tblclmnName, new ColumnWeightData(50, 150));
        tableLayout.setColumnData(tblclmnAttachment, new ColumnWeightData(0, 80));
        tableLayout.setColumnData(tblclmnMessage, new ColumnWeightData(0, 150));
        compositeTable.setLayout(tableLayout);

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        tableViewer.setLabelProvider(new TestCaseResultPreviewUploadedLableProvider());

        tableViewerColumnAttachment.setEditingSupport(new TestCaseResultAttachmentEditingSupport(tableViewer));
        tableViewerColumnMessage.setEditingSupport(new TestCaseResultMessageEditingSupport(tableViewer));
        return container;
    }

    @Override
    public void create() {
        super.create();
        initialize();
    }

    private void initialize() {
        tableViewer.setInput(getInput());
    }

    @Override
    protected Point getInitialSize() {
        return new Point(600, 500);
    }

    @Override
    protected void setShellStyle(int arg) {
        super.setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.RESIZE);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(StringConstants.DIA_TITLE_TEST_LOG_UPLOADING_PREVIEW);
    }

    public List<QTestLogUploadedPreview> getInput() {
        return input;
    }

    private void setInput(List<QTestLogUploadedPreview> input) {
        this.input = input;
    }
}
