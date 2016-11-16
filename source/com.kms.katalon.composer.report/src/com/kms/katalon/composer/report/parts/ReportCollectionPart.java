package com.kms.katalon.composer.report.parts;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.part.IComposerPart;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.composer.report.provider.ReportActionColumnLabelProvider;
import com.kms.katalon.composer.report.provider.ReportCollectionTableLabelProvider;
import com.kms.katalon.entity.report.ReportCollectionEntity;

public class ReportCollectionPart implements IComposerPart {

    private ReportCollectionEntity reportCollectionEntity;

    private TableViewer tableViewer;

    @PostConstruct
    public void initialize(Composite parent, MPart mpart) {
        reportCollectionEntity = (ReportCollectionEntity) mpart.getObject();

        createControls(parent);

        updateInput();
    }

    private void updateInput() {
        tableViewer.setInput(reportCollectionEntity.getReportItemDescriptions());
    }

    private void createControls(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        tableViewer = new CTableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
        Table table = tableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        TableViewerColumn tableViewerColumnNo = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNo = tableViewerColumnNo.getColumn();
        tblclmnNo.setWidth(50);
        tblclmnNo.setText(StringConstants.NO_);
        tableViewerColumnNo.setLabelProvider(new ReportCollectionTableLabelProvider(
                ReportCollectionTableLabelProvider.CLM_NO_IDX));

        TableViewerColumn tableViewerColumnId = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnId = tableViewerColumnId.getColumn();
        tblclmnId.setWidth(250);
        tblclmnId.setText(StringConstants.ID);
        tableViewerColumnId.setLabelProvider(new ReportCollectionTableLabelProvider(
                ReportCollectionTableLabelProvider.CLM_ID_IDX));

        TableViewerColumn tableViewerColumnEnviroment = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnEnvironment = tableViewerColumnEnviroment.getColumn();
        tblclmnEnvironment.setWidth(100);
        tblclmnEnvironment.setText(StringConstants.REPORT_COLLECTION_LBL_ENVIRONMENT);
        tableViewerColumnEnviroment.setLabelProvider(new ReportCollectionTableLabelProvider(
                ReportCollectionTableLabelProvider.CLM_EVN_IDX));

        TableViewerColumn tableViewerColumnStatus = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnStatus = tableViewerColumnStatus.getColumn();
        tblclmnStatus.setWidth(100);
        tblclmnStatus.setText(StringConstants.STATUS);
        tableViewerColumnStatus.setLabelProvider(new ReportCollectionTableLabelProvider(
                ReportCollectionTableLabelProvider.CLM_STATUS_IDX));

        TableViewerColumn tableViewerColumnFailedTests = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnFailedTests = tableViewerColumnFailedTests.getColumn();
        tblclmnFailedTests.setWidth(120);
        tblclmnFailedTests.setText(StringConstants.REPORT_COLLECTION_COLUMN_FAILED_TEST);
        tableViewerColumnFailedTests.setLabelProvider(new ReportCollectionTableLabelProvider(
                ReportCollectionTableLabelProvider.CLM_FAILED_TESTS_IDX));

        TableViewerColumn tableViewerColumnAction = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnAction = tableViewerColumnAction.getColumn();
        tblclmnAction.setWidth(90);
        tableViewerColumnAction.setLabelProvider(new ReportActionColumnLabelProvider(
                ReportCollectionTableLabelProvider.CLM_ACTION_IDX));

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.NO_RECREATE);
    }

    @Override
    public String getEntityId() {
        return reportCollectionEntity.getIdForDisplay();
    }
}
