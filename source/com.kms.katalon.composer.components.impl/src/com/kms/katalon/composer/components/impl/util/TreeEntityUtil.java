package com.kms.katalon.composer.components.impl.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;

import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.tree.CheckpointTreeEntity;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.IncludeTreeRootEntity;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ProfileRootTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ProfileTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ReportCollectionTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.impl.tree.SystemFileTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestListenerFolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestListenerTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteCollectionTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.tree.UserFileTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.controller.CheckpointController;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.ReportController;
import com.kms.katalon.controller.SystemFileController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.controller.TestListenerController;
import com.kms.katalon.controller.TestSuiteCollectionController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.entity.file.TestListenerEntity;
import com.kms.katalon.entity.file.UserFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
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
    public static ITreeEntity[] getChildren(FolderTreeEntity folderTreeEntity) throws Exception {
        if (folderTreeEntity.getObject() instanceof FolderEntity) {
            return getChildren(folderTreeEntity, folderTreeEntity.getObject());
        }
        return new ITreeEntity[0];
    }

    public static ITreeEntity[] getChildren(FolderTreeEntity folderTreeEntity, FolderEntity folder) throws Exception {
        FileEntity[] childrenEntities = FolderController.getInstance().getChildren(folder).toArray(new FileEntity[0]);

        List<ITreeEntity> treeEntities = new ArrayList<>();
        if (childrenEntities != null) {
            for (int i = 0; i < childrenEntities.length; i++) {
                if (childrenEntities[i] instanceof FolderEntity) {
                    treeEntities.add(new FolderTreeEntity((FolderEntity) childrenEntities[i], folderTreeEntity));
                } else if (childrenEntities[i] instanceof TestCaseEntity) {
                    treeEntities.add(new TestCaseTreeEntity((TestCaseEntity) childrenEntities[i],
                            folderTreeEntity));
                } else if (childrenEntities[i] instanceof TestSuiteEntity) {
                    treeEntities.add(new TestSuiteTreeEntity((TestSuiteEntity) childrenEntities[i],
                            folderTreeEntity));
                } else if (childrenEntities[i] instanceof DataFileEntity) {
                    treeEntities.add(new TestDataTreeEntity((DataFileEntity) childrenEntities[i],
                            folderTreeEntity));
                } else if (childrenEntities[i] instanceof WebElementEntity) {
                    treeEntities.add(new WebElementTreeEntity((WebElementEntity) childrenEntities[i],
                            folderTreeEntity));
                } else if (childrenEntities[i] instanceof ReportEntity) {
                    treeEntities.add(new ReportTreeEntity((ReportEntity) childrenEntities[i], folderTreeEntity));
                } else if (childrenEntities[i] instanceof TestSuiteCollectionEntity) {
                    treeEntities.add(new TestSuiteCollectionTreeEntity(
                            (TestSuiteCollectionEntity) childrenEntities[i], folderTreeEntity));
                } else if (childrenEntities[i] instanceof ReportCollectionEntity) {
                    treeEntities.add(new ReportCollectionTreeEntity((ReportCollectionEntity) childrenEntities[i],
                            folderTreeEntity));
                } else if (childrenEntities[i] instanceof CheckpointEntity) {
                    treeEntities.add(new CheckpointTreeEntity((CheckpointEntity) childrenEntities[i],
                            folderTreeEntity));
                } else if (childrenEntities[i] instanceof SystemFileEntity) {
                    treeEntities.add(new SystemFileTreeEntity((SystemFileEntity) childrenEntities[i],
                            folderTreeEntity));
                } else if (childrenEntities[i] instanceof UserFileEntity) {
                    treeEntities.add(new UserFileTreeEntity((UserFileEntity) childrenEntities[i],
                            folderTreeEntity));
                }
            }
        }
        return treeEntities.toArray(new ITreeEntity[0]);
    }
    
    public static FolderTreeEntity getFolderTreeEntity(FolderEntity folderEntity) {
        if (folderEntity == null) {
            return null;
        }
        if (folderEntity.getParentFolder() == null) {
            return new FolderTreeEntity(folderEntity, null);
        }
        return new FolderTreeEntity(folderEntity,
                getFolderTreeEntity(folderEntity.getParentFolder()));
    }

    public static FolderTreeEntity createSelectedTreeEntityHierachy(FolderEntity folderEntity,
            FolderEntity rootFolder) {
        if (folderEntity == null || folderEntity.equals(rootFolder)) {
            return new FolderTreeEntity(rootFolder, null);
        }
        return new FolderTreeEntity(folderEntity,
                createSelectedTreeEntityHierachy(folderEntity.getParentFolder(), rootFolder));
    }

    public static FolderTreeEntity getTestCaseFolderTreeEntity(ProjectEntity project) throws Exception {
        FolderEntity testCaseRoot = FolderController.getInstance().getTestCaseRoot(project);
        return new FolderTreeEntity(testCaseRoot, null);
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

    public static ProfileTreeEntity getProfileTreeEntity(ExecutionProfileEntity profile, FolderEntity parent) {
        return new ProfileTreeEntity(profile, new ProfileRootTreeEntity(parent, null));
    }

    public static TestListenerTreeEntity getTestListenerTreeEntity(TestListenerEntity testListener,
            FolderEntity parent) {
        return new TestListenerTreeEntity(testListener, new TestListenerFolderTreeEntity(parent, null));
    }

    public static SystemFileTreeEntity getSystemFileTreeEntity(SystemFileEntity systemFile,
            FolderEntity parent) {
        return new SystemFileTreeEntity(systemFile, new FolderTreeEntity(parent, null));
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
                    || StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_CHECKPOINT)
                    || StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_PROFILES)
                    || StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_TEST_LISTENER)
                    || StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_INCLUDE)) {
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
                } else if (FolderType.PROFILE.equals(folder.getFolderType())) {
                    rootFolder = FolderController.getInstance().getProfileRoot(project);
                } else if (FolderType.TESTLISTENER.equals(folder.getFolderType())) {
                    rootFolder = FolderController.getInstance().getTestListenerRoot(project);
                } else if (FolderType.INCLUDE.equals(folder.getFolderType())) {
                    rootFolder = FolderController.getInstance().getIncludeRoot(project);
                }

                if (rootFolder != null) {
                    treeEntities.add(TreeEntityUtil.createSelectedTreeEntityHierachy(folder, rootFolder));
                }

                continue;
            }

            // Keyword Package
            // treeEntities.add(TreeEntityUtil.getPackageTreeEntity(id, project));
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

            if (StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_PROFILES)) {
                // Checkpoint
                ExecutionProfileEntity profile = GlobalVariableController.getInstance().getExecutionProfile(
                        id.replaceFirst(StringConstants.ROOT_FOLDER_NAME_PROFILES + "/", ""), project);
                if (profile != null) {
                    treeEntities
                            .add(getProfileTreeEntity(profile, FolderController.getInstance().getProfileRoot(project)));
                }
            }

            if (StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_TEST_LISTENER)) {
                FolderEntity rootTestListenerFolder = FolderController.getInstance().getTestListenerRoot(project);
                String testListenerName = id.replaceFirst(StringConstants.ROOT_FOLDER_NAME_TEST_LISTENER + "/", "");
                TestListenerEntity testListenerEntity = TestListenerController.getInstance()
                        .getTestListener(testListenerName, rootTestListenerFolder);
                if (testListenerEntity != null) {
                    treeEntities.add(getTestListenerTreeEntity(testListenerEntity, rootTestListenerFolder));
                }
            }

            if (StringUtils.startsWith(id, StringConstants.ROOT_FOLDER_NAME_INCLUDE)) {
                SystemFileEntity systemFileEntity = SystemFileController.getInstance().getSystemFile(
                        new File(project.getFolderLocation(), id).getAbsolutePath(), project);
                treeEntities.add(getSystemFileTreeEntity(systemFileEntity, systemFileEntity.getParentFolder()));
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
        treeEntities.add(new ProfileRootTreeEntity(folderController.getProfileRoot(project), null));
        treeEntities.add(new FolderTreeEntity(folderController.getTestCaseRoot(project), null));
        treeEntities.add(new FolderTreeEntity(folderController.getObjectRepositoryRoot(project), null));
        treeEntities.add(new FolderTreeEntity(folderController.getTestSuiteRoot(project), null));
        treeEntities.add(new FolderTreeEntity(folderController.getTestDataRoot(project), null));
        treeEntities.add(new FolderTreeEntity(folderController.getCheckpointRoot(project), null));
        treeEntities.add(new FolderTreeEntity(folderController.getKeywordRoot(project), null));
        treeEntities.add(new TestListenerFolderTreeEntity(folderController.getTestListenerRoot(project), null));
//        treeEntities.add(new FolderTreeEntity(folderController.getReportRoot(project), null));
        treeEntities.add(new IncludeTreeRootEntity(folderController.getIncludeRoot(project)));
        
        List<FileEntity> fileEntities = folderController.getRootUserFilesOrFolders(project);
        for (FileEntity fileEntity : fileEntities) {
            if (fileEntity instanceof FolderEntity) {
                treeEntities.add(new FolderTreeEntity((FolderEntity) fileEntity, null));
            } else if (fileEntity instanceof UserFileEntity) {
                treeEntities.add(new UserFileTreeEntity((UserFileEntity) fileEntity, null));
            }
        }
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
