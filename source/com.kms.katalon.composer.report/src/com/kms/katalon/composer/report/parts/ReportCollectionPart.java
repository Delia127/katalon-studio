package com.kms.katalon.composer.report.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
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
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.controls.HelpToolBarForMPart;
import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.EventUtil;
import com.kms.katalon.composer.components.part.IComposerPartEvent;
import com.kms.katalon.composer.report.constants.StringConstants;
import com.kms.katalon.composer.report.provider.ReportActionColumnLabelProvider;
import com.kms.katalon.composer.report.provider.ReportCollectionTableLabelProvider;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.report.ReportItemDescription;

public class ReportCollectionPart extends EventServiceAdapter implements IComposerPartEvent {

    private ReportCollectionEntity reportCollectionEntity;

    private TableViewer tableViewer;

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private MPart mpart;

    @PostConstruct
    public void initialize(Composite parent, MPart mpart) {
        reportCollectionEntity = (ReportCollectionEntity) mpart.getObject();

        new HelpToolBarForMPart(mpart, DocumentationMessageConstants.REPORT_TEST_SUITE_COLLECTION);
        
        createControls(parent);

        updateInput();

        setPartLabel(reportCollectionEntity.getDisplayName());

        eventBroker.subscribe(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM, this);
        eventBroker.subscribe(EventConstants.REPORT_COLLECTION_RENAMED, this);
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
        table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));
        table.setHeaderVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        TableViewerColumn tableViewerColumnNo = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNo = tableViewerColumnNo.getColumn();
        tblclmnNo.setWidth(50);
        tblclmnNo.setText(StringConstants.NO_);
        tableViewerColumnNo.setLabelProvider(
                new ReportCollectionTableLabelProvider(ReportCollectionTableLabelProvider.CLM_NO_IDX));

        TableViewerColumn tableViewerColumnId = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnId = tableViewerColumnId.getColumn();
        tblclmnId.setWidth(250);
        tblclmnId.setText(StringConstants.ID);
        tableViewerColumnId.setLabelProvider(
                new ReportCollectionTableLabelProvider(ReportCollectionTableLabelProvider.CLM_ID_IDX));

        TableViewerColumn tableViewerColumnEnviroment = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnEnvironment = tableViewerColumnEnviroment.getColumn();
        tblclmnEnvironment.setWidth(100);
        tblclmnEnvironment.setText(StringConstants.REPORT_COLLECTION_LBL_ENVIRONMENT);
        tableViewerColumnEnviroment.setLabelProvider(
                new ReportCollectionTableLabelProvider(ReportCollectionTableLabelProvider.CLM_EVN_IDX));

        TableViewerColumn tableViewerColumnStatus = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnStatus = tableViewerColumnStatus.getColumn();
        tblclmnStatus.setWidth(100);
        tblclmnStatus.setText(StringConstants.STATUS);
        tableViewerColumnStatus.setLabelProvider(
                new ReportCollectionTableLabelProvider(ReportCollectionTableLabelProvider.CLM_STATUS_IDX));

        TableViewerColumn tableViewerColumnFailedTests = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnFailedTests = tableViewerColumnFailedTests.getColumn();
        tblclmnFailedTests.setWidth(120);
        tblclmnFailedTests.setText(StringConstants.REPORT_COLLECTION_COLUMN_FAILED_TEST);
        tableViewerColumnFailedTests.setLabelProvider(
                new ReportCollectionTableLabelProvider(ReportCollectionTableLabelProvider.CLM_FAILED_TESTS_IDX));

        TableViewerColumn tableViewerColumnAction = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnAction = tableViewerColumnAction.getColumn();
        tblclmnAction.setWidth(90);
        tableViewerColumnAction.setLabelProvider(
                new ReportActionColumnLabelProvider(ReportCollectionTableLabelProvider.CLM_ACTION_IDX));

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());
        ColumnViewerToolTipSupport.enableFor(tableViewer, ToolTip.NO_RECREATE);
        
        //KAT-3580: hide "Environment" column for API projects
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (currentProject.getType() == ProjectType.WEBSERVICE) {
            tblclmnEnvironment.setWidth(0);
            tblclmnEnvironment.setResizable(false);
        }
    }

    @Override
    public String getEntityId() {
        return reportCollectionEntity.getIdForDisplay();
    }

    @Override
    @Inject
    @Optional
    public void onSelect(@UIEventTopic(UIEvents.UILifeCycle.BRINGTOTOP) Event event) {
        EventUtil.post(EventConstants.PROPERTIES_ENTITY, null);
    }

    @Override
    @Inject
    @Optional
    public void onChangeEntityProperties(@UIEventTopic(EventConstants.PROPERTIES_ENTITY_UPDATED) Event event) {
        // do nothing
    }

    @Override
    @PreDestroy
    public void onClose() {
        // do nothing
    }

    @Override
    public void handleEvent(Event event) {
        switch (event.getTopic()) {
            case EventConstants.EXPLORER_RENAMED_SELECTED_ITEM:
                Object[] objects = getObjects(event);
                if (objects == null || objects.length != 2) {
                    return;
                }

                handleRenamedReportItem(objects);
                break;
            case EventConstants.REPORT_COLLECTION_RENAMED:
                Object eventObject = getObject(event);
                if (!(eventObject instanceof ReportCollectionEntity)) {
                    return;
                }
                ReportCollectionEntity reportCollection = (ReportCollectionEntity) eventObject;
                if (!StringUtils.equals(reportCollectionEntity.getId(), reportCollection.getId())) {
                    return;
                }
                setPartLabel(reportCollection.getDisplayName());
                break;
        }
    }

    private void handleRenamedReportItem(Object[] objects) {
        Object oldRelativeId = objects[0];
        ReportItemDescription reportNameChanged = getReportNameChanged(oldRelativeId);
        if (reportNameChanged != null) {
            reportNameChanged.setReportLocation((String) objects[1]);
            tableViewer.refresh(reportNameChanged);
        }
    }

    private ReportItemDescription getReportNameChanged(Object oldRelativeId) {
        for (ReportItemDescription itemDescription : reportCollectionEntity.getReportItemDescriptions()) {
            if (itemDescription.getReportLocation().equals(oldRelativeId)) {
                return itemDescription;
            }
        }
        return null;
    }

    private void setPartLabel(String label) {
        mpart.setLabel(label);
    }
}
