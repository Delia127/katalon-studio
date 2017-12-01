package com.kms.katalon.composer.components.impl.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.eclipse.editor.GroovyEditor;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;
import org.eclipse.ui.part.FileEditorInput;

import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.part.IComposerPart;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.CheckpointController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestSuiteCollectionController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.groovy.util.GroovyStringUtil;

@SuppressWarnings("restriction")
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

    public static String getTestSuiteCollectionPartId(String testSuiteCollectionId) {
        return IdConstants.TEST_SUITE_COLLECTION_CONTENT_PART_ID_PREFIX + "(" + testSuiteCollectionId + ")";
    }

    public static String getTestDataPartId(String testDataPk) {
        return IdConstants.TESTDATA_CONTENT_PART_ID_PREFIX + "(" + testDataPk + ")";
    }

    public static String getCheckpointPartId(String checkpointId) {
        return IdConstants.CHECKPOINT_CONTENT_PART_ID_PREFIX + "(" + checkpointId + ")";
    }

    public static String getReportPartId(String reportPk) {
        return IdConstants.REPORT_CONTENT_PART_ID_PREFIX + "(" + reportPk + ")";
    }

    public static String getReportCollectionPartId(String reportCollectionId) {
        return IdConstants.REPORT_COLLECTION_CONTENT_PART_ID_PREFIX + "(" + reportCollectionId + ")";
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
            partId = getReportPartId(entityId);
        } else if (entity instanceof TestSuiteCollectionEntity) {
            partId = getTestSuiteCollectionPartId(entityId);
        } else if (entity instanceof ReportCollectionEntity) {
            partId = getReportCollectionPartId(entityId);
        } else if (entity instanceof CheckpointEntity) {
            partId = getCheckpointPartId(entityId);
        } else {
            return;
        }

        EModelService modelService = ModelServiceSingleton.getInstance().getModelService();
        MApplication application = ApplicationSingleton.getInstance().getApplication();

        MPartStack mStackPart = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
        MPart mPart = (MPart) modelService.find(partId, application);
        if (mPart != null) {
            if (mPart.getToolbar() != null) {
                // dispose the help icon
                ((ToolBar) mPart.getToolbar().getWidget()).dispose();
            }
            mStackPart.getChildren().remove(mPart);
        }
    }

    public static IEntity getEntityByPartId(String partElementId) {
        try {
            if (StringUtils.isBlank(partElementId)) {
                return null;
            }

            String testCaseId = getEntityIdFromPartId(partElementId,
                    IdConstants.TEST_CASE_PARENT_COMPOSITE_PART_ID_PREFIX);
            if (testCaseId != null) {
                return TestCaseController.getInstance().getTestCase(testCaseId);
            }

            String testObjectId = getEntityIdFromPartId(partElementId, IdConstants.TESTOBJECT_CONTENT_PART_ID_PREFIX);
            if (testObjectId != null) {
                return ObjectRepositoryController.getInstance().getWebElement(testObjectId);
            }

            String testDataId = getEntityIdFromPartId(partElementId, IdConstants.TESTDATA_CONTENT_PART_ID_PREFIX);
            if (testDataId != null) {
                return TestDataController.getInstance().getTestData(testDataId);
            }

            String testSuiteId = getEntityIdFromPartId(partElementId, IdConstants.TESTSUITE_CONTENT_PART_ID_PREFIX);
            if (testSuiteId != null) {
                return TestSuiteController.getInstance().getTestSuite(testSuiteId);
            }

            String reportCollectionId = getEntityIdFromPartId(partElementId,
                    IdConstants.REPORT_COLLECTION_CONTENT_PART_ID_PREFIX);
            if (reportCollectionId != null) {
                return ReportController.getInstance().getReportCollection(reportCollectionId);
            }

            String reportId = getEntityIdFromPartId(partElementId, IdConstants.REPORT_CONTENT_PART_ID_PREFIX);
            if (reportId != null) {
                return ReportController.getInstance().getReportEntity(reportId);
            }

            String testSuiteCollectionId = getEntityIdFromPartId(partElementId,
                    IdConstants.TEST_SUITE_COLLECTION_CONTENT_PART_ID_PREFIX);
            if (testSuiteCollectionId != null) {
                return TestSuiteCollectionController.getInstance().getTestSuiteCollection(testSuiteCollectionId);
            }

            String checkpointId = getEntityIdFromPartId(partElementId, IdConstants.CHECKPOINT_CONTENT_PART_ID_PREFIX);
            if (checkpointId != null) {
                return CheckpointController.getInstance().getById(checkpointId);
            }

            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    private static String getEntityIdFromPartId(String partElementId, String entityPrefixId) {
        if (!StringUtils.startsWith(partElementId, entityPrefixId)) {
            return null;
        }
        return StringUtils.substringBetween(partElementId, "(", ")");
    }

    /**
     * Get opened entity IDs from available parts
     * 
     * @param parts list of available parts
     * @return opened entity IDs
     */
    public static List<String> getOpenedEntityIds(Collection<MPart> parts) {
        List<String> ids = new ArrayList<String>();
        if (parts == null || parts.isEmpty()) {
            return ids;
        }

        for (MPart part : parts) {
            Object o = part.getObject();
            if (o instanceof IComposerPart) {
                ids.add(((IComposerPart) o).getEntityId());
                continue;
            }

            // for Keywords editor
            if (o instanceof CompatibilityEditor
                    && !StringUtils.startsWith(((CompatibilityEditor) o).getModel().getElementId(),
                            IdConstants.TEST_CASE_PARENT_COMPOSITE_PART_ID_PREFIX)) {
                GroovyEditor editor = (GroovyEditor) ((CompatibilityEditor) o).getEditor();
                String kwFilePath = GroovyStringUtil.getKeywordsRelativeLocation(((FileEditorInput) editor.getEditorInput()).getPath());
                ids.add(kwFilePath);
            }
        }
        return ids;
    }
}
