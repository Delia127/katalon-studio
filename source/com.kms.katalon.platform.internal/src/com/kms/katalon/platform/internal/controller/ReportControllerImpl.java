package com.kms.katalon.platform.internal.controller;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.model.Integration;
import com.katalon.platform.api.model.ProjectEntity;
import com.katalon.platform.api.report.TestSuiteRecord;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.core.logging.XMLParserException;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.reporting.ReportUtil;
import com.kms.katalon.entity.integration.IntegratedEntity;
import com.kms.katalon.entity.integration.IntegratedType;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.platform.internal.InternalPlatformPlugin;
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

            InternalPlatformPlugin.getInstance().getEventBroker().post(EventConstants.REPORT_UPDATED,
                    new Object[] { reportEntity.getId(), updatedEntity });

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
            //TODO: Lookup
            TestSuiteLogRecord testSuiteLogRecord = ReportUtil.generate(reportEntity.getLocation());

            return new TestSuiteRecordImpl(reportEntity, testSuiteLogRecord);
        } catch (ControllerException | XMLParserException | IOException | XMLStreamException e) {
            throw new ResourceException(e.getMessage());
        }
    }

}
