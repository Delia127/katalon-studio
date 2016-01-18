package com.kms.katalon.composer.components.impl.util;

import java.util.Collections;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TreeEntityUtil {
    public static Object[] getChildren(FolderTreeEntity folderTreeEntity) throws Exception {
        if (folderTreeEntity.getObject() instanceof FolderEntity) {
            return getChildren(folderTreeEntity, (FolderEntity) folderTreeEntity.getObject());
        }
        return Collections.emptyList().toArray();
    }

    public static Object[] getChildren(FolderTreeEntity folderTreeEntity, FolderEntity folder) throws Exception {
        Object[] childrenEntities = FolderController.getInstance().getChildren(folder).toArray();

        if (childrenEntities != null) {
            for (int i = 0; i < childrenEntities.length; i++) {
                if (childrenEntities[i] instanceof FolderEntity) {
                    childrenEntities[i] = new FolderTreeEntity((FolderEntity) childrenEntities[i], folderTreeEntity);
                } else if (childrenEntities[i] instanceof TestCaseEntity) {
                    childrenEntities[i] = new TestCaseTreeEntity((TestCaseEntity) childrenEntities[i], folderTreeEntity);
                } else if (childrenEntities[i] instanceof TestSuiteEntity) {
                    childrenEntities[i] = new TestSuiteTreeEntity((TestSuiteEntity) childrenEntities[i],
                            folderTreeEntity);
                } else if (childrenEntities[i] instanceof DataFileEntity) {
                    childrenEntities[i] = new TestDataTreeEntity((DataFileEntity) childrenEntities[i], folderTreeEntity);
                } else if (childrenEntities[i] instanceof WebElementEntity) {
                    childrenEntities[i] = new WebElementTreeEntity((WebElementEntity) childrenEntities[i],
                            folderTreeEntity);
                } else if (childrenEntities[i] instanceof ReportEntity) {
                    childrenEntities[i] = new ReportTreeEntity((ReportEntity) childrenEntities[i], folderTreeEntity);
                }
            }
            return childrenEntities;
        }
        return Collections.emptyList().toArray();
    }

    public static FolderTreeEntity createSelectedTreeEntityHierachy(FolderEntity folderEntity, FolderEntity rootFolder) {
        if (folderEntity == null || folderEntity.equals(rootFolder)) {
            return new FolderTreeEntity(rootFolder, null);
        }
        return new FolderTreeEntity(folderEntity, createSelectedTreeEntityHierachy(folderEntity.getParentFolder(),
                rootFolder));
    }

    public static TestCaseTreeEntity getTestCaseTreeEntity(TestCaseEntity testCaseEntity, ProjectEntity projectEntity)
            throws Exception {
        FolderEntity testCaseRootFolder = FolderController.getInstance().getTestCaseRoot(projectEntity);
        return new TestCaseTreeEntity(testCaseEntity, createSelectedTreeEntityHierachy(
                testCaseEntity.getParentFolder(), testCaseRootFolder));
    }

    public static WebElementTreeEntity getWebElementTreeEntity(WebElementEntity testObjectEntity,
            ProjectEntity projectEntity) throws Exception {
        FolderEntity testCaseRootFolder = FolderController.getInstance().getObjectRepositoryRoot(projectEntity);
        return new WebElementTreeEntity(testObjectEntity, createSelectedTreeEntityHierachy(
                testObjectEntity.getParentFolder(), testCaseRootFolder));
    }

    public static TestDataTreeEntity getTestDataTreeEntity(DataFileEntity testDataEntity, ProjectEntity projectEntity)
            throws Exception {
        FolderEntity testCaseRootFolder = FolderController.getInstance().getTestDataRoot(projectEntity);
        return new TestDataTreeEntity(testDataEntity, createSelectedTreeEntityHierachy(
                testDataEntity.getParentFolder(), testCaseRootFolder));
    }

    public static TestSuiteTreeEntity getTestSuiteTreeEntity(TestSuiteEntity testSuiteEntity,
            ProjectEntity projectEntity) throws Exception {
        FolderEntity testCaseRootFolder = FolderController.getInstance().getTestSuiteRoot(projectEntity);
        return new TestSuiteTreeEntity(testSuiteEntity, createSelectedTreeEntityHierachy(
                testSuiteEntity.getParentFolder(), testCaseRootFolder));
    }

    public static ReportTreeEntity getReportTreeEntity(ReportEntity reportEntity, ProjectEntity projectEntity)
            throws Exception {
        FolderEntity testCaseRootFolder = FolderController.getInstance().getReportRoot(projectEntity);
        return new ReportTreeEntity(reportEntity, createSelectedTreeEntityHierachy(reportEntity.getParentFolder(),
                testCaseRootFolder));
    }

    /**
     * Get readable keyword name by capitalized and separated the words.
     * <p>
     * Example: getReadableKeywordName("getDeviceOSVersion") will be "Get Device OS Version"
     * 
     * @param keywordMethodName
     *            keyword name (also known as method name)
     * @return Readable Keyword Name
     */
    public static String getReadableKeywordName(String keywordMethodName) {
        if (keywordMethodName == null) {
            return keywordMethodName;
        }
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(StringUtils.capitalize(keywordMethodName)),
                " ");
    }
}
