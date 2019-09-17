package com.kms.katalon.dal.fileservice.manager;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.kms.katalon.dal.IReportDataProvider;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.FileServiceConstant;
import com.kms.katalon.dal.fileservice.constants.StringConstants;
import com.kms.katalon.dal.fileservice.dataprovider.SystemFileServiceDataProvider;
import com.kms.katalon.dal.fileservice.dataprovider.setting.FileServiceDataProviderSetting;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.dal.exception.DuplicatedFolderException;
import com.kms.katalon.entity.dal.exception.FilePathTooLongException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.UserFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.report.ReportCollectionEntity;
import com.kms.katalon.entity.report.ReportEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.groovy.reference.TestArtifactScriptRefactor;
import com.kms.katalon.groovy.util.GroovyRefreshUtil;
import com.kms.katalon.groovy.util.GroovyUtil;

public class FolderFileServiceManager {

    private static final String[] SYSTEM_FOLDERS = {
            StringConstants.ROOT_FOLDER_NAME_TEST_CASE,
            StringConstants.ROOT_FOLDER_NAME_OBJECT_REPOSITORY,
            StringConstants.ROOT_FOLDER_NAME_DATA_FILE,
            StringConstants.ROOT_FOLDER_NAME_TEST_SUITE,
            StringConstants.ROOT_FOLDER_NAME_REPORT,
            StringConstants.ROOT_FOLDER_NAME_KEYWORD,
            StringConstants.ROOT_FOLDER_NAME_CHECKPOINT,
            StringConstants.ROOT_FOLDER_NAME_PROFILES,
            StringConstants.ROOT_FOLDER_NAME_TEST_LISTENER,
            StringConstants.ROOT_FOLDER_NAME_INCLUDE,
            StringConstants.SYSTEM_FOLDER_NAME_BIN,
            StringConstants.SYSTEM_FOLDER_NAME_DRIVER,
            StringConstants.SYSTEM_FOLDER_NAME_LIB,
            StringConstants.SYSTEM_FOLDER_NAME_SETTINGS,
            StringConstants.SYSTEM_FOLDER_NAME_SCRIPT,
            ".git",
            ".settings"
    };

    private static void initRootFolder(String folderPath) throws Exception {
        File rootFolder = new File(folderPath);
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }
    }

    public static void initRootEntityFolders(ProjectEntity project) throws Exception {
        if (project != null) {
            String projectFolderLocation = project.getFolderLocation();
            initRootFolder(FileServiceConstant.getTestCaseRootFolderLocation(projectFolderLocation));
            initRootFolder(FileServiceConstant.getTestSuiteRootFolderLocation(projectFolderLocation));
            initRootFolder(FileServiceConstant.getDataFileRootFolderLocation(projectFolderLocation));
            initRootFolder(FileServiceConstant.getObjectRepositoryRootFolderLocation(projectFolderLocation));
            initRootFolder(FileServiceConstant.getKeywordRootFolderLocation(projectFolderLocation));
            initRootFolder(FileServiceConstant.getReportRootFolderLocation(projectFolderLocation));
            initRootFolder(FileServiceConstant.getCheckpointRootFolderLocation(projectFolderLocation));
            initRootFolder(FileServiceConstant.getTestListenerRootFolderLocation(projectFolderLocation));
            initRootFolder(FileServiceConstant.getProfileFolderLocation(projectFolderLocation));

            initRootFolder(FileServiceConstant.getSourceFolderLocation(projectFolderLocation));
            initRootFolder(FileServiceConstant.getGroovyScriptFolderLocation(projectFolderLocation));
            initRootFolder(FileServiceConstant.getFeatureFolderLocation(projectFolderLocation));
            initRootFolder(FileServiceConstant.getConfigFolderLocation(projectFolderLocation));
        }
    }

    public static FolderEntity getTestSuiteRoot(ProjectEntity project) throws Exception {
        if (project != null) {
            FolderEntity folder = getFolder(
                    FileServiceConstant.getTestSuiteRootFolderLocation(project.getFolderLocation()));
            if (folder != null) {
                folder.setProject(project);
                return folder;
            }
        }
        return null;
    }

    public static FolderEntity getTestCaseRoot(ProjectEntity project) throws Exception {
        if (project != null) {
            FolderEntity folder = getFolder(
                    FileServiceConstant.getTestCaseRootFolderLocation(project.getFolderLocation()));
            if (folder != null) {
                folder.setProject(project);
                return folder;
            }
        }
        return null;
    }

    public static FolderEntity getTestDataRoot(ProjectEntity project) throws Exception {
        if (project != null) {
            FolderEntity folder = getFolder(
                    FileServiceConstant.getDataFileRootFolderLocation(project.getFolderLocation()));
            if (folder != null) {
                folder.setProject(project);
                return folder;
            }
        }
        return null;
    }

    public static FolderEntity getObjectRepositoryRoot(ProjectEntity project) throws Exception {
        if (project != null) {
            FolderEntity folder = getFolder(
                    FileServiceConstant.getObjectRepositoryRootFolderLocation(project.getFolderLocation()));
            if (folder != null) {
                folder.setProject(project);
                return folder;
            }
        }
        return null;
    }

    public static FolderEntity getKeywordRoot(ProjectEntity project) throws Exception {
        if (project != null) {
            FolderEntity folder = getFolder(
                    FileServiceConstant.getKeywordRootFolderLocation(project.getFolderLocation()));
            if (folder != null) {
                folder.setProject(project);
                return folder;
            }
        }
        return null;
    }

    public static FolderEntity getReportRoot(ProjectEntity project) throws Exception {
        if (project != null) {
            FolderEntity folder = getFolder(
                    FileServiceConstant.getReportRootFolderLocation(project.getFolderLocation()), project);
            if (folder != null) {
                folder.setProject(project);
                return folder;
            }
        }
        return null;
    }

    public static FolderEntity getCheckpointRoot(ProjectEntity project) throws Exception {
        if (project == null) {
            return null;
        }

        FolderEntity folder = getFolder(
                FileServiceConstant.getCheckpointRootFolderLocation(project.getFolderLocation()));
        if (folder == null) {
            return null;
        }

        folder.setProject(project);
        return folder;
    }

    public static FolderEntity getTestListenerRoot(ProjectEntity project) throws Exception {
        if (project == null) {
            return null;
        }

        FolderEntity folder = getFolder(
                FileServiceConstant.getTestListenerRootFolderLocation(project.getFolderLocation()));
        if (folder == null) {
            return null;
        }

        folder.setFolderType(FolderType.TESTLISTENER);
        folder.setProject(project);
        return folder;
    }

    public static FolderEntity getFeatureRoot(ProjectEntity project) throws DALException {
        if (project == null) {
            return null;
        }
        try {
            FolderEntity folder = getFolder(
                    FileServiceConstant.getFeatureFolderLocation(project.getFolderLocation()));

            if (folder == null) {
                return null;
            }

            folder.setFolderType(FolderType.FEATURE);
            folder.setProject(project);
            return folder;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    public static FolderEntity getSourceRoot(ProjectEntity project) throws DALException {
        if (project == null) {
            return null;
        }
        try {
            FolderEntity folder = getFolder(
                    FileServiceConstant.getSourceFolderLocation(project.getFolderLocation()));

            if (folder == null) {
                return null;
            }

            folder.setFolderType(FolderType.INCLUDE);
            folder.setProject(project);
            return folder;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }
    
    public static FolderEntity getGroovyScriptRoot(ProjectEntity project) throws DALException {
    	if (project == null) {
    		return null;
    	}
    	 try {
             FolderEntity folder = getFolder(
                     FileServiceConstant.getGroovyScriptFolderLocation(project.getFolderLocation()));

             if (folder == null) {
                 return null;
             }

             folder.setFolderType(FolderType.INCLUDE);
             folder.setProject(project);
             return folder;
         } catch (Exception e) {
             throw new DALException(e);
         }
    }
    
    public static List<FileEntity> getRootUserFilesOrFolders(ProjectEntity project) throws DALException {
        if (project == null) {
            return null;
        }
        List<FileEntity> fileEntities = new ArrayList<>();
        List<String> systemFolders = Arrays.asList(SYSTEM_FOLDERS);
        try {
            File projectFolder = new File(project.getFolderLocation());
            File[] files = projectFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory() && !systemFolders.contains(file.getName())) {
                        FolderEntity folderEntity = getFolder(file.getAbsolutePath());
                        folderEntity.setFolderType(FolderType.USER);
                        folderEntity.setProject(project);
                        fileEntities.add(folderEntity);
                    } else if (file.isFile()) {
                        String fileName = file.getName();
                        if (!(fileName.endsWith(".classpath") ||
                            fileName.endsWith(".project") ||
                            fileName.endsWith(".prj") ||
                            fileName.endsWith(".DS_Store"))) {

                            UserFileEntity fileEntity = new UserFileEntity(file);
                            fileEntity.setProject(project);
                            fileEntities.add(fileEntity);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new DALException(e);
        }
        fileEntities.sort(new Comparator<FileEntity>() {

            @Override
            public int compare(FileEntity fileA, FileEntity fileB) {
                if (fileA instanceof FolderEntity && fileB instanceof UserFileEntity) { 
                    return -1;
                }
                if (fileB instanceof FolderEntity && fileA instanceof UserFileEntity) { 
                    return 1;
                }
                return fileA.getName().compareToIgnoreCase(fileB.getName());
            }
        });
        return fileEntities;
    }

    /**
     * Use to create new Folders (Test Case folder, Test Suite folder, Test Data folder...)
     * 
     * @param parentFolder
     * @param defaultName Folder name. Default name (New Folder) will be used if this null or empty
     * @return {@link FolderEntity}
     * @throws Exception
     */
    public static FolderEntity addNewFolder(FolderEntity parentFolder, String defaultName) throws Exception {
        if (parentFolder != null) {

            if (defaultName == null || defaultName.trim().equals("")) {
                defaultName = StringConstants.MNG_NEW_FOLDER;
            }
            String name = EntityService.getInstance().getAvailableName(parentFolder.getLocation(), defaultName, false);
            // Parent of new folder should be in cache
            FolderEntity newFolder = new FolderEntity();
            newFolder.setName(name);
            newFolder.setParentFolder(parentFolder);
            newFolder.setDescription("folder");
            newFolder.setFolderType(parentFolder.getFolderType());
            newFolder.setProject(parentFolder.getProject());
            EntityService.getInstance().saveEntity(newFolder);

            FolderFileServiceManager.refreshFolder(parentFolder);

            if (newFolder.getFolderType() == FolderType.TESTCASE) {
                GroovyUtil.initTestCaseScriptFolder(newFolder);
                GroovyRefreshUtil.refreshFolder(GroovyUtil.getRelativePathForFolder(parentFolder),
                        parentFolder.getProject(), null);
            }

            return newFolder;
        }
        return null;
    }
    
    public static FolderEntity addNewRootFolder(ProjectEntity project, String name) throws Exception {
        FolderEntity newFolder = new FolderEntity();
        newFolder.setName(name);
        newFolder.setFolderType(FolderType.USER);
        newFolder.setProject(project);
        EntityService.getInstance().saveEntity(newFolder);
        return newFolder;
    }

    public static FolderEntity getFolder(String path) throws Exception {
        FileEntity entity = EntityFileServiceManager.get(new File(path));
        if (entity instanceof FolderEntity) {
            return (FolderEntity) entity;
        }
        return null;
    }

    public static FolderEntity getFolder(String path, ProjectEntity project) throws Exception {
        return EntityFileServiceManager.getFolder(new File(path));
    }

    public static List<FileEntity> getChildren(FolderEntity folder) throws Exception {
        return EntityFileServiceManager.getChildren(folder, FileEntity.class);
    }

    public static List<File> getFileChildren(FolderEntity folder) throws Exception {
        return EntityFileServiceManager.getFileChildren(folder);
    }

    public static List<FolderEntity> getChildFoldersOfFolder(FolderEntity folder) throws Exception {
        return EntityFileServiceManager.getChildren(folder, FolderEntity.class);
    }

    public static List<TestCaseEntity> getChildTestCasesOfFolder(FolderEntity folder) throws Exception {
        return EntityFileServiceManager.getChildren(folder, TestCaseEntity.class);
    }

    public static List<TestSuiteEntity> getChildTestSuitesOfFolder(FolderEntity folder) throws Exception {
        return EntityFileServiceManager.getChildren(folder, TestSuiteEntity.class);
    }

    public static List<DataFileEntity> getChildDataFilesOfFolder(FolderEntity folder) throws Exception {
        return EntityFileServiceManager.getChildren(folder, DataFileEntity.class);
    }

    public static List<WebElementEntity> getChildWebElementsOfFolder(FolderEntity folder) throws Exception {
        return EntityFileServiceManager.getChildren(folder, WebElementEntity.class);
    }

    public static List<CheckpointEntity> getChildCheckpointsOfFolder(FolderEntity folder) throws DALException {
        try {
            return EntityFileServiceManager.getChildren(folder, CheckpointEntity.class);
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    public static List<FileEntity> getChildReportsOfFolder(FolderEntity parentFolderEntity) throws Exception {
        if (parentFolderEntity == null) {
            return Collections.emptyList();
        }

        File parentFolderFile = new File(parentFolderEntity.getLocation());
        if (!parentFolderFile.exists() || !parentFolderFile.isDirectory()) {
            return Collections.emptyList();
        }

        List<FileEntity> list = new ArrayList<FileEntity>();
        boolean sortByDate = false;
        IReportDataProvider reportDataProvider = new FileServiceDataProviderSetting().getReportDataProvider();
        for (File file : parentFolderFile.listFiles(EntityFileServiceManager.fileFilter)) {
            if (!file.isDirectory()) {
                continue;
            }

            String folderName = FilenameUtils.getBaseName(file.getName());

            if (new File(file, ReportEntity.DF_LOG_FILE_NAME).exists()) {
                ReportEntity report = reportDataProvider.getReportEntity(file.getAbsolutePath());
                list.add(report);
                sortByDate = true;
                continue;
            }

            File reportCollectionFile = new File(file, folderName + ReportCollectionEntity.FILE_EXTENSION);
            if (reportCollectionFile.exists()) {
                ReportCollectionEntity report = reportDataProvider
                        .getReportCollectionEntity(reportCollectionFile.getAbsolutePath());
                list.add(report);
                sortByDate = true;
                continue;
            }

            FileEntity fileEntity = EntityFileServiceManager.get(file);
            if (fileEntity != null && !getChildReportsOfFolder((FolderEntity) fileEntity).isEmpty()) {
                list.add(fileEntity);
            }
        }
        if (sortByDate) {
            ReportFileServiceManager.sortListByCreatedDate(list, true);
        }
        return list;
    }

    public static List<TestCaseEntity> getDescendantTestCasesOfFolder(FolderEntity folder) throws Exception {
        return EntityFileServiceManager.getDescendants(folder, TestCaseEntity.class);
    }

    public static List<TestSuiteEntity> getDescendantTestSuitesOfFolder(FolderEntity folder) throws Exception {
        return EntityFileServiceManager.getDescendants(folder, TestSuiteEntity.class);
    }

    public static List<DataFileEntity> getDescendantDataFilesOfFolder(FolderEntity folder) throws Exception {
        return EntityFileServiceManager.getDescendants(folder, DataFileEntity.class);
    }

    public static List<ReportCollectionEntity> getDescendantReportCollectionOfFolder(FolderEntity folder)
            throws Exception {
        return EntityFileServiceManager.getDescendants(folder, ReportCollectionEntity.class);
    }

    public static void updateFolderName(FolderEntity folderEntity, String newName) throws Exception {
        // validate name
        if (folderEntity != null) {
            EntityService.getInstance().validateName(newName);
            ProjectEntity project = folderEntity.getProject();

            String location = folderEntity.getLocation();
            File folder = new File(location);
            if (folder.isDirectory()) {
                String newPath = folder.getParent() + File.separator + newName;
                validateFolderPathLength(newPath, folderEntity, project);
                File newFolder = new File(newPath);
                if (newFolder.exists() && !newName.equalsIgnoreCase(folderEntity.getName())) {
                    throw new DuplicatedFolderException(
                            MessageFormat.format(StringConstants.MNG_EXC_EXISTED_FOLDER_NAME, newName));
                }

                if (folder.list().length > 0) {
                    String oldRelativeFolderLocation = folderEntity.getRelativePath();
                    // Collect entities which will be affected if rename folder
                    FolderEntity testSuiteRoot = FolderFileServiceManager.getTestSuiteRoot(project);
                    List<TestSuiteEntity> allTestSuites = FolderFileServiceManager
                            .getDescendantTestSuitesOfFolder(testSuiteRoot);

                    List<TestCaseEntity> descendantTestCases = FolderFileServiceManager
                            .getDescendantTestCasesOfFolder(folderEntity);
                    for (TestCaseEntity testCase : descendantTestCases) {
                        GroovyUtil.loadScriptContentIntoTestCase(testCase);
                    }

                    IFolder oldScriptFolder = GroovyUtil.getGroovyProject(project)
                            .getFolder(GroovyUtil.getScriptPackageRelativePathForFolder(folderEntity));

                    boolean updated = folder.renameTo(newFolder);
                    if (updated) {
                        folderEntity.setName(newName);
                        EntityService.getInstance().getEntityCache().replaceKeys(location, folderEntity.getLocation());

                        // update children references of renamed folder
                        switch (folderEntity.getFolderType()) {
                            case DATAFILE:
                                DataFileFileServiceManager.saveAllReferencesToDataFileAfterFolderRenamed(
                                        oldRelativeFolderLocation, folderEntity, allTestSuites);
                                break;
                            case KEYWORD:
                                // Keyword folder is package folder
                                break;
                            case REPORT:
                                // Report folder cannot be renamed
                                break;
                            case TESTCASE:
                                for (TestCaseEntity testCase : descendantTestCases) {
                                    GroovyUtil.updateTestCasePasted(testCase);
                                }

                                TestCaseFileServiceManager.updateReferencesTestCaseFolder(oldRelativeFolderLocation,
                                        folderEntity, allTestSuites);

                                if (oldScriptFolder.exists()) {
                                    oldScriptFolder.delete(true, false, null);
                                    GroovyUtil.getTestCaseScriptSourceFolder(project)
                                            .refreshLocal(IResource.DEPTH_INFINITE, null);
                                }

                                GroovyUtil.refreshInfiniteScriptTestCaseClasspath(folderEntity.getProject(),
                                        folderEntity.getParentFolder(), null);

                                break;
                            case TESTSUITE:
                                // test suite folder has no reference.
                                break;
                            case WEBELEMENT:
                                WebElementFileServiceManager.updateFolderTestObjectReferences(folderEntity,
                                        oldRelativeFolderLocation);
                                break;
                            case CHECKPOINT:
                                CheckpointFileServiceManager.updateFolderCheckpointReferences(folderEntity,
                                        oldRelativeFolderLocation);
                                break;
                            default:
                                break;
                        }
                    }
                } else {
                    if (folder.renameTo(newFolder)) {
                        folderEntity.setName(newName);
                    }
                }
                String oldKey = EntityService.getInstance().getEntityCache().getKey(folderEntity);
                if (oldKey != null) {
                    EntityService.getInstance().getEntityCache().remove(oldKey);
                }
                EntityService.getInstance().getEntityCache().put(folderEntity.getLocation(), folderEntity);
                EntityService.getInstance().saveIntergratedFolderMetadataEntity(folderEntity);
                refreshFolder(folderEntity.getParentFolder());
            }
        }
    }

    private static void validateFolderPathLength(String newPath, FolderEntity folder, ProjectEntity project)
            throws Exception {
        if (newPath.length() > FileServiceConstant.MAX_FILE_PATH_LENGTH) {
            throw new FilePathTooLongException(newPath.length(), FileServiceConstant.MAX_FILE_PATH_LENGTH);
        }
        if (folder.getLocation()
                .contains(FileServiceConstant.getDataFileRootFolderLocation(project.getFolderLocation()))) {
            validateDataFileFolderPathLength(newPath, folder, "");
        }
        if (folder.getLocation()
                .contains(FileServiceConstant.getTestCaseRootFolderLocation(project.getFolderLocation()))) {
            validateTestCaseFolderPathLength(newPath, folder, "", project);
        }
        if (folder.getLocation()
                .contains(FileServiceConstant.getTestSuiteRootFolderLocation(project.getFolderLocation()))) {
            validateTestSuiteFolderPathLength(newPath, folder, "");
        }
    }

    private static void validateDataFileFolderPathLength(String newPath, FolderEntity folder, String relativePath)
            throws Exception {
        List<DataFileEntity> dataFileEntities = getChildDataFilesOfFolder(folder);
        for (DataFileEntity dataFile : dataFileEntities) {
            String newDataFilePath = newPath + File.separator + dataFile.getName();
            if (newDataFilePath.length() > FileServiceConstant.MAX_FILE_PATH_LENGTH) {
                throw new FilePathTooLongException(newDataFilePath.length(),
                        relativePath + (relativePath.isEmpty() ? "" : File.separator) + dataFile.getName(),
                        FileServiceConstant.MAX_FILE_PATH_LENGTH);
            }
        }
        List<FolderEntity> foderEntities = getChildFoldersOfFolder(folder);
        for (FolderEntity childFolder : foderEntities) {
            String newFolderPath = validateFolderEntityPathLength(newPath, relativePath, childFolder);
            validateDataFileFolderPathLength(newFolderPath, childFolder,
                    relativePath + (relativePath.isEmpty() ? "" : File.separator) + folder.getName());
        }
    }

    private static String validateFolderEntityPathLength(String newPath, String relativePath, FolderEntity folder)
            throws FilePathTooLongException {
        String newFolderPath = newPath + File.separator + folder.getName();
        if (newFolderPath.length() > FileServiceConstant.MAX_FILE_PATH_LENGTH) {
            throw new FilePathTooLongException(newFolderPath.length(),
                    relativePath + (relativePath.isEmpty() ? "" : File.separator) + folder.getName(),
                    FileServiceConstant.MAX_FILE_PATH_LENGTH);
        }
        return newFolderPath;
    }

    private static void validateTestCaseFolderPathLength(String newPath, FolderEntity folder, String relativePath,
            ProjectEntity project) throws Exception {
        List<TestCaseEntity> testCaseEntities = getChildTestCasesOfFolder(folder);
        for (TestCaseEntity testCase : testCaseEntities) {
            String newTestCasePath = newPath + File.separator + testCase.getName()
                    + TestCaseEntity.getTestCaseFileExtension();
            if (newTestCasePath.length() > FileServiceConstant.MAX_FILE_PATH_LENGTH) {
                throw new FilePathTooLongException(newTestCasePath.length(),
                        relativePath + (relativePath.isEmpty() ? "" : File.separator) + testCase.getName(),
                        FileServiceConstant.MAX_FILE_PATH_LENGTH);
            }
        }
        List<FolderEntity> foderEntities = getChildFoldersOfFolder(folder);
        for (FolderEntity childFolder : foderEntities) {
            String newFolderPath = validateFolderEntityPathLength(newPath, relativePath, childFolder);
            validateTestCaseFolderPathLength(newFolderPath, childFolder,
                    relativePath + (relativePath.isEmpty() ? "" : File.separator) + childFolder.getName(), project);
        }
    }

    private static void validateTestSuiteFolderPathLength(String newPath, FolderEntity folder, String relativePath)
            throws Exception {
        List<TestSuiteEntity> testSuiteEntities = getChildTestSuitesOfFolder(folder);
        for (TestSuiteEntity testSuite : testSuiteEntities) {
            String newTestCasePath = newPath + File.separator + testSuite.getName()
                    + TestSuiteEntity.getTestSuiteFileExtension();
            if (newTestCasePath.length() > FileServiceConstant.MAX_FILE_PATH_LENGTH) {
                throw new FilePathTooLongException(newTestCasePath.length(),
                        relativePath + (relativePath.isEmpty() ? "" : File.separator) + testSuite.getName(),
                        FileServiceConstant.MAX_FILE_PATH_LENGTH);
            }
        }
        List<FolderEntity> foderEntities = getChildFoldersOfFolder(folder);
        for (FolderEntity childFolder : foderEntities) {
            String newFolderPath = validateFolderEntityPathLength(newPath, relativePath, childFolder);
            validateTestSuiteFolderPathLength(newFolderPath, childFolder,
                    relativePath + (relativePath.isEmpty() ? "" : File.separator) + childFolder.getName());
        }
    }

    public static FolderEntity getFolderByName(FolderEntity parentFolder, String name, ProjectEntity project)
            throws Exception {
        List<FolderEntity> folders = getChildFoldersOfFolder(parentFolder);
        for (FolderEntity folder : folders) {
            if (folder.getName().equals(name)) {
                return folder;
            }
        }
        return null;
    }

    public static String getAvailableFolderName(FolderEntity parentFolder, String name) throws Exception {
        String newname = name;
        int i = 0;
        List<FolderEntity> folders = getChildFoldersOfFolder(parentFolder);

        if (folders != null && folders.size() != 0) {
            Boolean flag = false;
            while (flag == false) {
                Boolean duplicate = false;
                for (FolderEntity folder : folders) {
                    if ((folder.getName()).equals(newname)) {
                        duplicate = true;
                    }
                }
                if (duplicate == false)
                    flag = true;
                else {
                    i++;
                    newname = name + i;
                }
            }
        }
        return newname;
    }

    public static void deleteFolder(FolderEntity folder) throws Exception {
        if (folder != null) {
            switch (folder.getFolderType()) {
                case TESTCASE:
                    TestCaseFileServiceManager.deleteTestCaseFolder(folder);
                    break;
                case DATAFILE:
                    DataFileFileServiceManager.deteleDataFileFolder(folder);
                    break;
                default:
                    EntityFileServiceManager.deleteFolder(folder);
                    break;
            }
            FolderFileServiceManager.refreshFolder(folder);
        }
    }

    public static FolderEntity loadAllTestCaseDescendants(FolderEntity folder) throws Exception {
        List<FileEntity> childrenEntities = new ArrayList<FileEntity>();
        childrenEntities.addAll(getChildTestCasesOfFolder(folder));
        childrenEntities.addAll(getChildFoldersOfFolder(folder));
        if (childrenEntities != null) {
            folder.setChildrenEntities(childrenEntities);
            for (Object object : childrenEntities) {
                if (object instanceof FolderEntity) {
                    loadAllTestCaseDescendants((FolderEntity) object);
                }
            }
        }
        return folder;
    }

    public static FolderEntity copyFolder(FolderEntity folder, FolderEntity destinationFolder) throws Exception {
        switch (folder.getFolderType()) {
            case TESTCASE:
                return TestCaseFileServiceManager.copyTestCaseFolder(folder, destinationFolder);
            case WEBELEMENT:
            case TESTSUITE:
            case DATAFILE:
            case CHECKPOINT:
                return EntityFileServiceManager.copyFolder(folder, destinationFolder);
            case KEYWORD:
                return EntityFileServiceManager.copyKeywordFolder(folder, destinationFolder);
            case INCLUDE:
                return new SystemFileServiceDataProvider().copyFolder(folder, destinationFolder);
            default:
                break;
        }
        return null;
    }

    public static FolderEntity moveFolder(FolderEntity folder, FolderEntity destinationFolder) throws Exception {
        return EntityFileServiceManager.moveFolder(folder, destinationFolder);
    }

    public static void refreshFolder(FolderEntity folder) throws CoreException {
        GroovyRefreshUtil.refreshFolder(folder.getRelativePath(), folder.getProject(), null);
    }

    public static void refreshFolderScriptReferences(String oldFolderId, FolderEntity folder)
            throws CoreException, IOException {
        new TestArtifactScriptRefactor(folder.getFolderType(), oldFolderId, false, false, false)
                .updateReferenceForProject(folder.getIdForDisplay() + StringConstants.ENTITY_ID_SEPARATOR,
                        folder.getProject());
    }

    public static FolderEntity saveFolder(FolderEntity folder) throws Exception {
        EntityService.getInstance().saveIntergratedFolderMetadataEntity(folder);
        return folder;
    }

    public static FolderEntity getProfileRoot(ProjectEntity project) throws DALException {
        try {
            FolderEntity folder = getFolder(FileServiceConstant.getProfileFolderLocation(project.getFolderLocation()),
                    project);

            if (folder != null) {
                folder.setProject(project);
                folder.setFolderType(FolderType.PROFILE);
                return folder;
            }

            return null;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }
}
