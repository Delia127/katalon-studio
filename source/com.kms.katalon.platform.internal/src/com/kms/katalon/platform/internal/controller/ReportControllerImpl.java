package com.kms.katalon.platform.internal.controller;

import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.model.Integration;
import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.platform.api.report.TestSuiteRecord;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.report.lookup.LogRecordLookup;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.integration.IntegratedType;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.platform.internal.entity.ReportEntityImpl;
import com.kms.katalon.platform.internal.report.TestSuiteRecordImpl;

public class ReportControllerImpl implements com.katalon.platform.api.controller.ReportController {

    @Override
    public com.katalon.platform.api.model.ReportEntity getReport(ProjectEntity project, String reportId)
            throws ResourceException {
        try {
            ReportEntity reportEntity = ReportController.getInstance().getReportEntityByDisplayId(reportId,
                    ProjectController.getInstance().getCurrentProject());

            return new ReportEntityImpl(reportEntity);
        } catch (ControllerException e) {
            throw new ResourceException(e.getMessage());
        }
    }

    @Override
    public com.katalon.platform.api.model.ReportEntity updateIntegration(
            com.katalon.platform.api.model.ProjectEntity project, com.katalon.platform.api.model.ReportEntity report,
            Integration integration) throws ResourceException {
        try {
            ReportEntity reportEntity = ReportController.getInstance().getReportEntityByDisplayId(report.getId(),
                    ProjectController.getInstance().getCurrentProject());

            IntegratedEntity newIntegrated = new IntegratedEntity();
            newIntegrated.setProductName(integration.getName());
            newIntegrated.setProperties(integration.getProperties());
            newIntegrated.setType(IntegratedType.REPORT);

            reportEntity.updateIntegratedEntity(newIntegrated);

            ReportEntity updatedEntity = ReportController.getInstance().updateReport(reportEntity);

            EventBrokerSingleton.getInstance().getEventBroker()
            .post(EventConstants.REPORT_UPDATED, new Object[] { reportEntity.getId(), updatedEntity });

            return new ReportEntityImpl(updatedEntity);
        } catch (ControllerException e) {
            throw new ResourceException(e.getMessage());
        }
    }

    @Override
    public TestSuiteRecord getTestSuiteRecord(com.katalon.platform.api.model.ProjectEntity project,
            com.katalon.platform.api.model.ReportEntity report) throws ResourceException {
        try {
            ReportEntity reportEntity = ReportController.getInstance().getReportEntityByDisplayId(report.getId(),
                    ProjectController.getInstance().getCurrentProject());
            TestSuiteLogRecord testSuiteLogRecord = LogRecordLookup.getInstance().getTestSuiteLogRecord(reportEntity);

            return new TestSuiteRecordImpl(reportEntity, testSuiteLogRecord);
        } catch (ControllerException e) {
            throw new ResourceException(e.getMessage());
        }
    }

}
