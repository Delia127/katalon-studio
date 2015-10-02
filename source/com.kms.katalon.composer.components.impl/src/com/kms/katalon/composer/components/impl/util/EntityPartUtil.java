package com.kms.katalon.composer.components.impl.util;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class EntityPartUtil {
    public static String getTestCaseCompositePartId(String testCasePk) {
        return IdConstants.TEST_CASE_PARENT_COMPOSITE_PART_ID_PREFIX + "(" + testCasePk + ")";
    }

    public static String getTestObjectPartId(String testObjectPk) {
        return IdConstants.TESTOBJECT_CONTENT_PART_ID_PREFIX + "(" + testObjectPk + ")";
    }

    public static String getTestSuiteCompositePartId(String testSuitePk) {
        return IdConstants.TESTSUITE_CONTENT_PART_ID_PREFIX + "(" + testSuitePk + ")";
    }

    public static String getTestDataPartId(String testDataPk) {
        return IdConstants.TESTDATA_CONTENT_PART_ID_PREFIX + "(" + testDataPk + ")";
    }

    public static String getReportPartId(String reportPk) {
        return IdConstants.REPORT_CONTENT_PART_ID_PREFIX + "(" + reportPk + ")";
    }

    public static void closePart(IEntity entity) {

        if (entity == null) {
            return;
        }

        String partId = "";
        String entityId = entity.getId();

        if (entity instanceof TestCaseEntity) {
            partId = getTestCaseCompositePartId(entityId);
        } else if (entity instanceof WebElementEntity) {
            partId = getTestObjectPartId(entityId);
        } else if (entity instanceof DataFileEntity) {
            partId = getTestDataPartId(entityId);
        } else if (entity instanceof TestSuiteEntity) {
            partId = getTestSuiteCompositePartId(entityId);
        } else if (entity instanceof ReportEntity) {
            partId = getReportPartId(entity.getId());
        } else {
            return;
        }

        EModelService modelService = ModelServiceSingleton.getInstance().getModelService();
        MApplication application = ApplicationSingleton.getInstance().getApplication();

        MPartStack mStackPart = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
        MPart mPart = (MPart) modelService.find(partId, application);
        if (mPart != null) {
            mStackPart.getChildren().remove(mPart);
        }
    }

}
