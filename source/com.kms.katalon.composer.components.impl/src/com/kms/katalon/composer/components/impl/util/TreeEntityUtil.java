package com.kms.katalon.composer.components.impl.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;

import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.groovy.util.GroovyStringUtil;
import com.kms.katalon.groovy.util.GroovyUtil;

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

    public static PackageTreeEntity getPackageTreeEntity(String packageRelativeLocation, ProjectEntity projectEntity)
            throws Exception {
        IPackageFragment packageFragment = GroovyUtil.getPackageFragmentFromLocation(packageRelativeLocation, false,
                projectEntity);
        if (packageFragment != null && packageFragment.exists()) {
            return new PackageTreeEntity(packageFragment, null);
        }
        return null;
    }

    public static KeywordTreeEntity getKeywordTreeEntity(String keywordRelativeLocation, ProjectEntity projectEntity)
            throws Exception {
        String packageLocation = StringUtils.substringBeforeLast(keywordRelativeLocation,
                StringConstants.ENTITY_ID_SEPERATOR);
        String keywordName = StringUtils.substringAfterLast(keywordRelativeLocation,
                StringConstants.ENTITY_ID_SEPERATOR);
        PackageTreeEntity packageTreeEntity = getPackageTreeEntity(packageLocation, projectEntity);
        if (packageTreeEntity != null) {
            ICompilationUnit keywordFile = ((IPackageFragment) packageTreeEntity.getObject())
                    .getCompilationUnit(keywordName);
            if (keywordFile != null && keywordFile.exists()) {
                return new KeywordTreeEntity(keywordFile, packageTreeEntity);
            }
        }
        return null;
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

    /**
     * Get TreeEntity ID.
     * <p>
     * Note: This is only used for PersistedState purpose.
     * 
     * @see #getTreeEntitiesFromIds(String)
     * @param entities TreeEntity[]
     * @return TreeEntity IDs
     * @throws Exception
     */
    public static String getTreeEntityIds(Object[] entities) throws Exception {
        if (entities == null) return StringConstants.EMPTY;
        if (entities.length == 0) return StringConstants.EMPTY;
        StringBuilder ids = new StringBuilder();
        for (int i = 0; i < entities.length; i++) {
            if (entities[i] instanceof ITreeEntity) {
                ITreeEntity entity = (ITreeEntity) entities[i];
                Object o = entity.getObject();
                String id = (o instanceof FileEntity) ? ((FileEntity) o).getIdForDisplay()
                        : ((o instanceof IPackageFragment) ? GroovyStringUtil
                                .getKeywordsRelativeLocation(((IPackageFragment) o).getPath()) : GroovyStringUtil
                                .getKeywordsRelativeLocation(((ICompilationUnit) o).getPath()));

                // By default, leave empty for folder and package
                String kw = StringConstants.EMPTY;
                if (o instanceof TestCaseEntity) {
                    kw = StringConstants.ENTITY_KW_TEST_CASE;
                } else if (o instanceof TestSuiteEntity) {
                    kw = StringConstants.ENTITY_KW_TEST_SUITE;
                } else if (o instanceof WebElementEntity) {
                    kw = StringConstants.ENTITY_KW_TEST_OBJECT;
                } else if (o instanceof DataFileEntity) {
                    kw = StringConstants.ENTITY_KW_TEST_DATA;
                } else if (o instanceof ReportEntity) {
                    kw = StringConstants.ENTITY_KW_REPORT;
                } else if (o instanceof ICompilationUnit) {
                    kw = StringConstants.ENTITY_KW_KEYWORD;
                } else if (o instanceof IPackageFragment) {
                    kw = "pk";
                }

                ids.append(id + PreferenceConstants.ProjectPreferenceConstants.RECENT_ENTITY_KW_SEPARATOR + kw);
                if (i != entities.length - 1) {
                    ids.append(PreferenceConstants.ProjectPreferenceConstants.RECENT_ENTITY_SEPARATOR);
                }
            }
        }
        return ids.toString();
    }

    /**
     * Get TreeEntity[] from IDs
     * <p>
     * Note: This is only used for PersistedState purpose.
     * 
     * @param ids TreeEntity IDs which is generated by {@link #getTreeEntityIds(Object[])}
     * @return ITreeEntity[]
     * @throws Exception
     */
    public static ITreeEntity[] getTreeEntitiesFromIds(String ids) throws Exception {
        if (StringUtils.isBlank(ids)) return new ITreeEntity[] {};
        String[] entityIds = StringUtils.split(ids,
                PreferenceConstants.ProjectPreferenceConstants.RECENT_ENTITY_SEPARATOR);
        List<ITreeEntity> treeEntities = new ArrayList<ITreeEntity>();
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();

        for (String idNType : entityIds) {
            String[] idType = StringUtils.split(idNType,
                    PreferenceConstants.ProjectPreferenceConstants.RECENT_ENTITY_KW_SEPARATOR);
            String id = idType[0];
            String type = (idType.length > 1) ? idType[1] : StringConstants.EMPTY;

            if (type.isEmpty()) {
                // should be folder
                FolderEntity folder = FolderController.getInstance().getFolderByDisplayId(project, id);
                if (folder != null) {
                    if (folder.getFolderType().equals(FolderType.TESTCASE)) {
                        treeEntities.add(TreeEntityUtil.createSelectedTreeEntityHierachy(folder, FolderController
                                .getInstance().getTestCaseRoot(project)));
                    } else if (folder.getFolderType().equals(FolderType.WEBELEMENT)) {
                        treeEntities.add(TreeEntityUtil.createSelectedTreeEntityHierachy(folder, FolderController
                                .getInstance().getObjectRepositoryRoot(project)));
                    } else if (folder.getFolderType().equals(FolderType.DATAFILE)) {
                        treeEntities.add(TreeEntityUtil.createSelectedTreeEntityHierachy(folder, FolderController
                                .getInstance().getTestDataRoot(project)));
                    } else if (folder.getFolderType().equals(FolderType.TESTSUITE)) {
                        treeEntities.add(TreeEntityUtil.createSelectedTreeEntityHierachy(folder, FolderController
                                .getInstance().getTestSuiteRoot(project)));
                    } else if (folder.getFolderType().equals(FolderType.KEYWORD)) {
                        treeEntities.add(TreeEntityUtil.createSelectedTreeEntityHierachy(folder, FolderController
                                .getInstance().getKeywordRoot(project)));
                    } else if (folder.getFolderType().equals(FolderType.REPORT)) {
                        treeEntities.add(TreeEntityUtil.createSelectedTreeEntityHierachy(folder, FolderController
                                .getInstance().getReportRoot(project)));
                    }
                }
            } else {
                if (StringUtils.equals(type, StringConstants.ENTITY_KW_TEST_CASE)) {
                    // Test Case
                    TestCaseEntity tc = TestCaseController.getInstance().getTestCaseByDisplayId(id);
                    if (tc != null) {
                        treeEntities.add(TreeEntityUtil.getTestCaseTreeEntity(tc, project));
                    }
                } else if (StringUtils.equals(type, StringConstants.ENTITY_KW_TEST_OBJECT)) {
                    // Test Object
                    WebElementEntity to = ObjectRepositoryController.getInstance().getWebElementByDisplayPk(id);
                    if (to != null) {
                        treeEntities.add(TreeEntityUtil.getWebElementTreeEntity(to, project));
                    }
                } else if (StringUtils.equals(type, StringConstants.ENTITY_KW_TEST_DATA)) {
                    // Test Data
                    DataFileEntity td = TestDataController.getInstance().getTestDataByDisplayId(id);
                    if (td != null) {
                        treeEntities.add(TreeEntityUtil.getTestDataTreeEntity(td, project));
                    }
                } else if (StringUtils.equals(type, StringConstants.ENTITY_KW_TEST_SUITE)) {
                    // Test Suite
                    TestSuiteEntity ts = TestSuiteController.getInstance().getTestSuiteByDisplayId(id, project);
                    if (ts != null) {
                        treeEntities.add(TreeEntityUtil.getTestSuiteTreeEntity(ts, project));
                    }
                } else if (StringUtils.equals(type, StringConstants.ENTITY_KW_REPORT)) {
                    // Report
                    ReportEntity rp = ReportController.getInstance().getReportEntityByDisplayId(id, project);
                    if (rp != null) {
                        treeEntities.add(TreeEntityUtil.getReportTreeEntity(rp, project));
                    }
                } else if (StringUtils.equals(type, StringConstants.ENTITY_KW_KEYWORD)) {
                    // Keyword
                    treeEntities.add(TreeEntityUtil.getKeywordTreeEntity(id, project));
                } else if (StringUtils.equals(type, "pk")) {
                    // Package
                    PackageTreeEntity packageTreeEntity = TreeEntityUtil.getPackageTreeEntity(id, project);
                    if (packageTreeEntity != null) {
                        treeEntities.add(packageTreeEntity);
                    }
                }
            }
        }
        return treeEntities.toArray(new ITreeEntity[] {});
    }
}
