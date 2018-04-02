package com.kms.katalon.composer.components.impl.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;

import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.tree.CheckpointTreeEntity;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ReportCollectionTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ProfileRootTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestListenerFolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteCollectionTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.controller.CheckpointController;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestSuiteCollectionController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.groovy.util.GroovyStringUtil;
import com.kms.katalon.groovy.util.GroovyUtil;

public class TreeEntityUtil {
    public static Object[] getChildren(FolderTreeEntity folderTreeEntity) throws Exception {
        if (folderTreeEntity.getObject() instanceof FolderEntity) {
            return getChildren(folderTreeEntity, folderTreeEntity.getObject());
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
                    childrenEntities[i] = new TestCaseTreeEntity((TestCaseEntity) childrenEntities[i],
                            folderTreeEntity);
                } else if (childrenEntities[i] instanceof TestSuiteEntity) {
                    childrenEntities[i] = new TestSuiteTreeEntity((TestSuiteEntity) childrenEntities[i],
                            folderTreeEntity);
                } else if (childrenEntities[i] instanceof DataFileEntity) {
                    childrenEntities[i] = new TestDataTreeEntity((DataFileEntity) childrenEntities[i],
                            folderTreeEntity);
                } else if (childrenEntities[i] instanceof WebElementEntity) {
                    childrenEntities[i] = new WebElementTreeEntity((WebElementEntity) childrenEntities[i],
                            folderTreeEntity);
                } else if (childrenEntities[i] instanceof ReportEntity) {
                    childrenEntities[i] = new ReportTreeEntity((ReportEntity) childrenEntities[i], folderTreeEntity);
                } else if (childrenEntities[i] instanceof TestSuiteCollectionEntity) {
                    childrenEntities[i] = new TestSuiteCollectionTreeEntity(
                            (TestSuiteCollectionEntity) childrenEntities[i], folderTreeEntity);
                } else if (childrenEntities[i] instanceof ReportCollectionEntity) {
                    childrenEntities[i] = new ReportCollectionTreeEntity((ReportCollectionEntity) childrenEntities[i],
                            folderTreeEntity);
                } else if (childrenEntities[i] instanceof CheckpointEntity) {
                    childrenEntities[i] = new CheckpointTreeEntity((CheckpointEntity) childrenEntities[i],
                            folderTreeEntity);
                }
            }
            return childrenEntities;
        }
        return Collections.emptyList().toArray();
    }

    public static FolderTreeEntity createSelectedTreeEntityHierachy(FolderEntity folderEntity,
            FolderEntity rootFolder) {
        if (folderEntity == null || folderEntity.equals(rootFolder)) {
            return new FolderTreeEntity(rootFolder, null);
        }
        return new FolderTreeEntity(folderEntity,
                createSelectedTreeEntityHierachy(folderEntity.getParentFolder(), rootFolder));
    }

    public static FolderTreeEntity getWebElementFolderTreeEntity(FolderEntity folderEntity, ProjectEntity projectEntity)
            throws Exception {
        FolderEntity webElementRoot = FolderController.getInstance().getObjectRepositoryRoot(projectEntity);
        return new FolderTreeEntity(folderEntity,
                createSelectedTreeEntityHierachy(folderEntity.getParentFolder(), webElementRoot));
    }

    public static TestCaseTreeEntity getTestCaseTreeEntity(TestCaseEntity testCaseEntity, ProjectEntity projectEntity)
            throws Exception {
        FolderEntity testCaseRootFolder = FolderController.getInstance().getTestCaseRoot(projectEntity);
        return new TestCaseTreeEntity(testCaseEntity,
                createSelectedTreeEntityHierachy(testCaseEntity.getParentFolder(), testCaseRootFolder));
    }

    public static WebElementTreeEntity getWebElementTreeEntity(WebElementEntity testObjectEntity,
            ProjectEntity projectEntity) throws Exception {
        FolderEntity testCaseRootFolder = FolderController.getInstance().getObjectRepositoryRoot(projectEntity);
        return new WebElementTreeEntity(testObjectEntity,
                createSelectedTreeEntityHierachy(testObjectEntity.getParentFolder(), testCaseRootFolder));
    }

    public static TestDataTreeEntity getTestDataTreeEntity(DataFileEntity testDataEntity, ProjectEntity projectEntity)
            throws Exception {
        FolderEntity testCaseRootFolder = FolderController.getInstance().getTestDataRoot(projectEntity);
        return new TestDataTreeEntity(testDataEntity,
                createSelectedTreeEntityHierachy(testDataEntity.getParentFolder(), testCaseRootFolder));
    }

    public static TestSuiteTreeEntity getTestSuiteTreeEntity(TestSuiteEntity testSuiteEntity,
            ProjectEntity projectEntity) throws Exception {
        FolderEntity testCaseRootFolder = FolderController.getInstance().getTestSuiteRoot(projectEntity);
        return new TestSuiteTreeEntity(testSuiteEntity,
                createSelectedTreeEntityHierachy(testSuiteEntity.getParentFolder(), testCaseRootFolder));
    }

    public static ReportTreeEntity getReportTreeEntity(ReportEntity reportEntity, ProjectEntity projectEntity)
            throws Exception {
        FolderEntity testCaseRootFolder = FolderController.getInstance().getReportRoot(projectEntity);
        return new ReportTreeEntity(reportEntity,
                createSelectedTreeEntityHierachy(reportEntity.getParentFolder(), testCaseRootFolder));
    }

    public static ReportCollectionTreeEntity getReportCollectionTreeEntity(
            ReportCollectionEntity reportCollectionEntity, ProjectEntity projectEntity) throws Exception {
        FolderEntity reportRootFolder = FolderController.getInstance().getReportRoot(projectEntity);
        return new ReportCollectionTreeEntity(reportCollectionEntity,
                createSelectedTreeEntityHierachy(reportCollectionEntity.getParentFolder(), reportRootFolder));
    }

    public static CheckpointTreeEntity getCheckpointTreeEntity(CheckpointEntity checkpointEntity) throws Exception {
        FolderEntity checkpointRootFolder = FolderController.getInstance().getReportRoot(checkpointEntity.getProject());
        return new CheckpointTreeEntity(checkpointEntity,
                createSelectedTreeEntityHierachy(checkpointEntity.getParentFolder(), checkpointRootFolder));
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
                StringConstants.ENTITY_ID_SEPARATOR);
        String keywordName = StringUtils.substringAfterLast(keywordRelativeLocation,
                StringConstants.ENTITY_ID_SEPARATOR);
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

    public static TestSuiteCollectionTreeEntity getTestSuiteCollectionTreeEntity(
            TestSuiteCollectionEntity testSuiteCollectionEntity, ProjectEntity projectEntity) throws Exception {
        FolderEntity testSuiteRootFolder = FolderController.getInstance().getTestSuiteRoot(projectEntity);
        return new TestSuiteCollectionTreeEntity(testSuiteCollectionEntity,
                createSelectedTreeEntityHierachy(testSuiteCollectionEntity.getParentFolder(), testSuiteRootFolder));
    }

    /**
     * Get readable keyword name by capitalized and separated the words.
     * <p>
     * Example: getReadableKeywordName("getDeviceOSVersion") will be "Get Device OS Version"
     * 
     * @param keywordMethodName
     * keyword name (also known as method name)
     * @return Readable Keyword Name
     */
    public static String getReadableKeywordName(String keywordMethodName) {
        if (keywordMethodName == null) {
            return keywordMethodName;
        }
        if ("uncheck".equals(keywordMethodName)) {
            return "Un-check";
        }
        if ("waitForJQueryLoad".equals(keywordMethodName)) {
            return "Wait For jQuery Load";
        }
        if ("executeJavaScript".equals(keywordMethodName)) {
            return "Execute JavaScript";
        }
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(StringUtils.capitalize(keywordMethodName)),
                " ");
    }

    /**
     * Get Entity IDs from TreeEntity.
     * <p>
     * Note: This is only used for PersistedState purpose.
     * 
     * @see #getExpandedTreeEntitiesFromIds(List)
     * @param entities TreeEntity[]
     * @return List of TreeEntity ID
     * @throws Exception
     */
    public static List<String> getTreeEntityIds(Object[] entities) throws Exception {
        List<String> ids = new ArrayList<String>();
        if (entities == null)
            return ids;
        if (entities.length == 0)
            return ids;
        for (Object o : entities) {
            if (o instanceof ITreeEntity) {
                ITreeEntity treeEntity = (ITreeEntity) o;
                Object entity = treeEntity.getObject();
                String id = (entity instanceof FileEntity) ? ((FileEntity) entity).getIdForDisplay()
                        : ((entity instanceof IPackageFragment)
                                ? GroovyStringUtil.getKeywordsRelativeLocation(((IPackageFragment) entity).getPath())
                                : GroovyStringUtil.getKeywordsRelativeLocation(((ICompilationUnit) entity).getPath()));
                ids.add(id);
            }
        }
        return ids;
    }

    /**
     * Get list of TreeEntity from IDs
     * <p>
     * Note: This is only used for PersistedState purpose.
     * 
     * @param ids
     * TreeEntity IDs which is generated by {@link #getTreeEntityIds(Object[])}
     * @return List of ITreeEntity
     * @throws Exception
     */
    public static List<ITreeEntity> getExpandedTreeEntitiesFromIds(List<String> ids) throws Exception {
        List<ITreeEntity> treeEntities = new ArrayList<ITreeEntity>();
        if (ids == null || ids.isEmpty()) {
            return treeEntities;
        }
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        // Folder/Package Tree Entity
        // Minor issue: Cannot detect default keyword package and keyword root folder
        for (String id : ids) {
            if (StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_TEST_CASE)
                    || StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_OBJECT_REPOSITORY)
                    || StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_DATA_FILE)
                    || StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_TEST_SUITE)
                    || StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_REPORT)
                    || StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_KEYWORD)
                    || StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_CHECKPOINT)) {
                // Folder
                FolderEntity folder = FolderController.getInstance().getFolderByDisplayId(project, id);
                if (folder == null) {
                    continue;
                }

                FolderEntity rootFolder = null;
                if (FolderType.TESTCASE.equals(folder.getFolderType())) {
                    rootFolder = FolderController.getInstance().getTestCaseRoot(project);
                } else if (FolderType.WEBELEMENT.equals(folder.getFolderType())) {
                    rootFolder = FolderController.getInstance().getObjectRepositoryRoot(project);
                } else if (FolderType.DATAFILE.equals(folder.getFolderType())) {
                    rootFolder = FolderController.getInstance().getTestDataRoot(project);
                } else if (FolderType.TESTSUITE.equals(folder.getFolderType())) {
                    rootFolder = FolderController.getInstance().getTestSuiteRoot(project);
                } else if (FolderType.KEYWORD.equals(folder.getFolderType())) {
                    rootFolder = FolderController.getInstance().getKeywordRoot(project);
                } else if (FolderType.REPORT.equals(folder.getFolderType())) {
                    rootFolder = FolderController.getInstance().getReportRoot(project);
                } else if (FolderType.CHECKPOINT.equals(folder.getFolderType())) {
                    rootFolder = FolderController.getInstance().getCheckpointRoot(project);
                }

                if (rootFolder != null) {
                    treeEntities.add(TreeEntityUtil.createSelectedTreeEntityHierachy(folder, rootFolder));
                }

                continue;
            }

            // Keyword Package
            treeEntities.add(TreeEntityUtil.getPackageTreeEntity(id, project));
        }
        return treeEntities;
    }

    /**
     * Get list of TreeEntity from IDs
     * <p>
     * Note: This is only used for PersistedState purpose.
     * 
     * @param ids
     * TreeEntity IDs which is generated by
     * {@link com.kms.katalon.composer.components.impl.util.EntityPartUtil#getOpenedEntityIds(java.util.Collection)}
     * @return List of ITreeEntity
     * @throws Exception
     */
    public static List<ITreeEntity> getOpenedTreeEntitiesFromIds(List<String> ids) throws Exception {
        List<ITreeEntity> treeEntities = new ArrayList<ITreeEntity>();
        if (ids == null || ids.isEmpty())
            return treeEntities;
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        // Non-Folder Tree Entity
        for (String id : ids) {
            if (StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_TEST_CASE)) {
                // Test Case
                TestCaseEntity tc = TestCaseController.getInstance().getTestCaseByDisplayId(id);
                if (tc != null) {
                    treeEntities.add(TreeEntityUtil.getTestCaseTreeEntity(tc, project));
                }
                continue;
            }

            if (StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_OBJECT_REPOSITORY)) {
                // Test Object
                WebElementEntity to = ObjectRepositoryController.getInstance().getWebElementByDisplayPk(id);
                if (to != null) {
                    treeEntities.add(TreeEntityUtil.getWebElementTreeEntity(to, project));
                }
                continue;
            }

            if (StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_DATA_FILE)) {
                // Test Data
                DataFileEntity td = TestDataController.getInstance().getTestDataByDisplayId(id);
                if (td != null) {
                    treeEntities.add(TreeEntityUtil.getTestDataTreeEntity(td, project));
                }
                continue;
            }

            if (StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_TEST_SUITE)) {
                // Test Suite
                TestSuiteEntity ts = TestSuiteController.getInstance().getTestSuiteByDisplayId(id, project);

                if (ts != null) {
                    treeEntities.add(TreeEntityUtil.getTestSuiteTreeEntity(ts, project));
                    continue;
                }

                // Reason 1: This Test Suite does not exist -> nothing to deal with
                // Reason 2: This is Test Suite Collection, not Test Suite
                TestSuiteCollectionEntity tsc = TestSuiteCollectionController.getInstance().getTestRunByDisplayId(id);
                if (tsc != null) {
                    treeEntities.add(TreeEntityUtil.getTestSuiteCollectionTreeEntity(tsc, project));
                }
                continue;
            }

            if (StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_REPORT)) {
                String reportCollectionPath = project.getFolderLocation() + File.separator + id
                        + ReportCollectionEntity.FILE_EXTENSION;
                // Report Collection
                if (new File(reportCollectionPath).exists()) {
                    ReportCollectionEntity reportCollectionEntity = ReportController.getInstance()
                            .getReportCollection(reportCollectionPath);
                    if (reportCollectionEntity != null) {
                        treeEntities.add(TreeEntityUtil.getReportCollectionTreeEntity(reportCollectionEntity, project));
                    }
                    continue;
                }

                // Report
                ReportEntity rp = ReportController.getInstance().getReportEntityByDisplayId(id, project);
                if (rp != null) {
                    treeEntities.add(TreeEntityUtil.getReportTreeEntity(rp, project));
                }
                continue;
            }

            if (StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_KEYWORD)) {
                // Keyword
                treeEntities.add(TreeEntityUtil.getKeywordTreeEntity(id, project));
                continue;
            }

            if (StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_CHECKPOINT)) {
                // Checkpoint
                CheckpointEntity cp = CheckpointController.getInstance().getByDisplayedId(id);
                if (cp != null) {
                    treeEntities.add(getCheckpointTreeEntity(cp));
                }
            }
        }
        return treeEntities;
    }

    public static List<ITreeEntity> getAllTreeEntity(ProjectEntity project) throws Exception {
        List<ITreeEntity> treeEntities = new ArrayList<>();
        if (project == null) {
            return treeEntities;
        }

        FolderController folderController = FolderController.getInstance();
        treeEntities.add(new FolderTreeEntity(folderController.getTestCaseRoot(project), null));
        treeEntities.add(new FolderTreeEntity(folderController.getObjectRepositoryRoot(project), null));
        treeEntities.add(new FolderTreeEntity(folderController.getTestSuiteRoot(project), null));
        treeEntities.add(new FolderTreeEntity(folderController.getTestDataRoot(project), null));
        treeEntities.add(new FolderTreeEntity(folderController.getCheckpointRoot(project), null));
        treeEntities.add(new FolderTreeEntity(folderController.getKeywordRoot(project), null));
        treeEntities.add(new TestListenerFolderTreeEntity(folderController.getTestListenerRoot(project), null));
        treeEntities.add(new FolderTreeEntity(folderController.getReportRoot(project), null));
        treeEntities.add(new ProfileRootTreeEntity(folderController.getProfileRoot(project), null));
        return treeEntities;
    }

    public static boolean isValidTreeEntitySelectionType(Object[] selection, String typeName) {
        try {
            return selection != null && selection.length > 0
                    && StringUtils.equals(typeName, ((ITreeEntity) selection[0]).getTypeName());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return false;
        }
    }

}
