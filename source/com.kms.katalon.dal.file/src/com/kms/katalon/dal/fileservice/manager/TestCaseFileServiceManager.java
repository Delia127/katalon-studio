package com.kms.katalon.dal.fileservice.manager;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.FileServiceConstant;
import com.kms.katalon.dal.fileservice.constants.StringConstants;
import com.kms.katalon.dal.fileservice.dataprovider.setting.FileServiceDataProviderSetting;
import com.kms.katalon.entity.dal.exception.DuplicatedFileNameException;
import com.kms.katalon.entity.dal.exception.FilePathTooLongException;
import com.kms.katalon.entity.dal.exception.LengthExceedLimitationException;
import com.kms.katalon.entity.dal.exception.NoEntityException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.util.Util;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.reference.TestArtifactScriptRefactor;
import com.kms.katalon.groovy.util.GroovyUtil;

public class TestCaseFileServiceManager {

    public static String getAvailableName(FolderEntity parentFolder, String name) throws Exception {
        if (parentFolder != null) {
            String newname = name;
            List<String> fileNames = new ArrayList<String>();

            for (FileEntity testCase : FolderFileServiceManager.getChildren(parentFolder)) {
                fileNames.add(testCase.getName().toLowerCase());
            }

            if (fileNames.contains(newname.toLowerCase())) {
                for (int i = 1; fileNames.contains(newname.toLowerCase()); i++) {
                    newname = name + " (" + i + ")";
                }
            }
            return newname;
        }
        return name;
    }

    /**
     * Save a NEW Test Case entity.<br>
     * Please use {@link #updateTestCase(TestCaseEntity)} if you want to save an existing Test Case.
     * 
     * @param newTestCase new Test Case entity which is created by {@link #newTestCaseWithoutSave(FolderEntity, String)}
     * @return {@link TestCaseEntity}
     * @throws Exception
     */
    public static TestCaseEntity saveNewTestCase(TestCaseEntity newTestCase) throws Exception {
        if (newTestCase == null || newTestCase.getProject() == null || newTestCase.getParentFolder() == null) {
            return null;
        }

        EntityService.getInstance().saveEntity(newTestCase);
        FolderEntity parentFolder = newTestCase.getParentFolder();
        GroovyUtil.refreshScriptTestCaseClasspath(newTestCase.getProject(), parentFolder);
        FolderFileServiceManager.refreshFolder(parentFolder);

        return newTestCase;
    }

    public static TestCaseEntity getTestCase(String testCasePk) throws Exception {
        FileEntity entity = EntityFileServiceManager.get(new File(testCasePk));
        if (entity instanceof TestCaseEntity) {
            return (TestCaseEntity) entity;
        }
        return null;
    }

    public static void initTestCase(TestCaseEntity testCase) throws Exception {
        if (testCase != null && testCase.getProject() != null) {
            testCase.getDataFiles().clear();
            String projectFolderLocation = testCase.getProject().getFolderLocation();
            for (String dataFileLocation : testCase.getDataFileLocations()) {
                DataFileEntity dataFile = DataFileFileServiceManager.getDataFile(projectFolderLocation + File.separator
                        + dataFileLocation);
                if (dataFile != null) {
                    testCase.getDataFiles().add(dataFile);
                }
            }
        }
    }

    public static TestCaseEntity updateTestCase(TestCaseEntity testCase) throws Exception {
        if (testCase != null && testCase.getProject() != null) {
            ProjectEntity project = testCase.getProject();
            TestCaseFileServiceManager.validateData(testCase);

            EntityService.getInstance().validateName(testCase.getName());

            // If test case changed the name, clean up the old one in cache
            // before saving the new one
            String oldTestCaseLocation = EntityService.getInstance().getEntityCache().getKey(testCase);
            if (oldTestCaseLocation == null) {
                oldTestCaseLocation = testCase.getLocation();
            }

            refactorReferencingTestSuites(project, testCase, oldTestCaseLocation);

            boolean testCaseNameChanged = false;
            if (EntityService.getInstance().getEntityCache().contains(testCase)) {
                if (oldTestCaseLocation != null && !oldTestCaseLocation.equals(testCase.getLocation())) {
                    EntityService.getInstance().getEntityCache().remove(testCase, true);
                    testCaseNameChanged = true;
                }
            }

            EntityService.getInstance().saveEntity(testCase);
            String newRelativeTcId = testCase.getRelativePathForUI().replace(File.separator, "/");
            if (testCaseNameChanged) {
                File projectFile = new File(project.getLocation());
                String oldRelativeTcLocation = oldTestCaseLocation.substring(projectFile.getParent().length() + 1);
                String oldRelativeTcId = FilenameUtils.removeExtension(oldRelativeTcLocation).replace(File.separator,
                        "/");

                TestArtifactScriptRefactor.createForTestCaseEntity(oldRelativeTcId).updateReferenceForProject(
                        newRelativeTcId, project);

                IFolder oldScriptFolder = GroovyUtil.getGroovyProject(project).getFolder(
                        GroovyUtil.getScriptPackageRelativePathForTestCase(oldRelativeTcId));

                oldScriptFolder.delete(true, null);

                GroovyUtil.updateTestCasePasted(testCase);

                FolderFileServiceManager.refreshFolder(testCase.getParentFolder());

                GroovyUtil.refreshScriptTestCaseClasspath(testCase.getProject(),
                        FolderFileServiceManager.getFolder(testCase.getParentFolder().getId()));
            }

            FolderFileServiceManager.refreshFolder(testCase.getParentFolder());
            return testCase;
        }
        return null;
    }

    private static void refactorReferencingTestSuites(ProjectEntity project, TestCaseEntity testCase,
            String oldTestCaseLocation) throws Exception {
        // if test case changed its name, update reference Location in test
        // suites that refer to it
        List<TestSuiteEntity> lstTestSuites = FolderFileServiceManager.getDescendantTestSuitesOfFolder(FolderFileServiceManager.getTestSuiteRoot(project));
        File projectFile = new File(project.getLocation());
        String oldRelativeTcLocation = oldTestCaseLocation.substring(projectFile.getParent().length() + 1);
        String oldRelativeTcId = FilenameUtils.removeExtension(oldRelativeTcLocation).replace(File.separator, "/");
        String newRelativeTcId = testCase.getRelativePathForUI().replace(File.separator, "/");

        for (TestSuiteEntity testSuite : lstTestSuites) {
            boolean isTestSuiteUpdated = false;
            for (TestSuiteTestCaseLink testCaseLink : testSuite.getTestSuiteTestCaseLinks()) {
                if (testCaseLink.getTestCaseId().equals(oldRelativeTcId)) {
                    testCaseLink.setTestCaseId(newRelativeTcId);

                    List<VariableLink> retainedVariableLinks = new ArrayList<VariableLink>();

                    // add new variables to test suite's variable links
                    for (VariableEntity variable : testCase.getVariables()) {
                        boolean isNewVariable = true;
                        for (VariableLink variableLink : testCaseLink.getVariableLinks()) {
                            if (variable.getId().equals(variableLink.getVariableId())) {
                                isNewVariable = false;
                                retainedVariableLinks.add(variableLink);
                                break;
                            }
                        }

                        if (isNewVariable) {
                            VariableLink newVariableLink = new VariableLink();
                            newVariableLink.setVariableId(variable.getId());
                            testCaseLink.getVariableLinks().add(newVariableLink);
                            retainedVariableLinks.add(newVariableLink);
                        }
                    }

                    // remove all variable links that do not refer to any
                    // variable
                    testCaseLink.getVariableLinks().retainAll(retainedVariableLinks);
                    isTestSuiteUpdated = true;
                }
            }

            if (isTestSuiteUpdated) {
                EntityService.getInstance().saveEntity(testSuite);
            }
        }
    }

    // private static void refactorCallingTestCaseIfRenameTestCase(ProjectEntity
    // project, TestCaseEntity testCase,
    // String oldTestCaseLocation) throws Exception {
    // List<TestCaseEntity> checkList = new ArrayList<TestCaseEntity>();
    // String testCasesRoot =
    // FileServiceConstant.getTestCaseFolder(testCase.getProject().getFolderLocation());
    // getTestCasesRecursively(testCasesRoot, testCase, checkList, project);
    // String newRelativeTcLocation =
    // testCase.getLocation().substring(project.getFolderLocation().length() +
    // 1);
    // for (TestCaseEntity tc : checkList) {
    // boolean isChanged = false;
    // for (TestStepEntity step : tc.getTestSteps()) {
    // if (step.getCallingTestCase() == testCase) {
    // step.setCalledTestCaseLocation(newRelativeTcLocation);
    // isChanged = true;
    // }
    // }
    // if (isChanged) {
    // EntityService.getInstance().saveEntity(tc);
    // }
    // }
    // }

    public static TestCaseEntity mergeTestCase(String testCasePk, String folderPk) throws Exception {

        TestCaseEntity testCase = (TestCaseEntity) EntityService.getInstance().getEntityByPath(testCasePk);
        FolderEntity folder = (FolderEntity) EntityService.getInstance().getEntityByPath(folderPk);
        if (testCase != null && folder != null) {
            // clean up the old test case on cache before saving new one
            if (EntityService.getInstance().getEntityCache().contains(testCase)) {
                EntityService.getInstance().getEntityCache().remove(testCase, false);
            }

            FileUtils.moveFileToDirectory(new File(testCase.getLocation()), new File(folder.getLocation()), false);
            testCase.setParentFolder(folder);
            // if (TestCaseEntity.TestCaseType.Normal ==
            // testCase.getTestCaseType()) {
            // TestCaseFileServiceManager.newFunctionCreateTestCaseParams(testCase);
            // }
            //
            // List<TestStepEntity> lstRemovedStep = new
            // ArrayList<TestStepEntity>();
            //
            // for (TestStepEntity testStep : testCase.getTestSteps()) {
            // if (testStep.getIsActive()) {
            // if (testStep.getCallingTestCase() != null) {
            // testStep.setCalledTestCaseLocation(testStep.getCallingTestCase().getLocation());
            // }
            // } else {
            // testCase.getTestSteps().remove(testStep);
            // }
            // }
            //
            // for (TestStepEntity removedTestStep : lstRemovedStep) {
            // testCase.getTestSteps().remove(removedTestStep);
            // }

            EntityService.getInstance().saveEntity(testCase);
        }
        return testCase;
    }

    private static void validateData(TestCaseEntity testCaseEntity) throws Exception {

        if (testCaseEntity.getTag() != null && testCaseEntity.getTag().length() >= 200) {
            throw new LengthExceedLimitationException();
        }

        EntityService.getInstance().validateName(testCaseEntity.getName());

        String testCaseScriptLocation = testCaseEntity.getProject().getFolderLocation() + File.separator
                + GroovyUtil.getScriptPackageRelativePathForTestCase(testCaseEntity) + File.separator
                + GroovyUtil.getGroovyClassName(testCaseEntity);
        // Check for test case script path
        if (testCaseScriptLocation.length() > FileServiceConstant.MAX_FILE_PATH_LENGTH) {
            throw new FilePathTooLongException(testCaseScriptLocation.length(),
                    FileServiceConstant.MAX_FILE_PATH_LENGTH);
        }

        // check duplicated name
        File file = new File(testCaseEntity.getLocation());

        if (file.exists()) {
            TestCaseEntity oldEntity = (TestCaseEntity) EntityService.getInstance().getEntityByPath(
                    testCaseEntity.getLocation());
            if (!oldEntity.getTestCaseGuid().equals(testCaseEntity.getTestCaseGuid())) {
                throw new DuplicatedFileNameException(MessageFormat.format(
                        StringConstants.MNG_EXC_EXISTED_TEST_CASE_NAME_INSENSITVE, testCaseEntity.getName()));
            }
        }

        if (testCaseEntity.getLocation().length() > FileServiceConstant.MAX_FILE_PATH_LENGTH) {
            throw new FilePathTooLongException(testCaseEntity.getLocation().length(),
                    FileServiceConstant.MAX_FILE_PATH_LENGTH);
        }
    }

    public static TestCaseEntity copyTestCase(TestCaseEntity testCase, FolderEntity destinationFolder) throws Exception {
        TestCaseEntity newTestCase = EntityFileServiceManager.copy(testCase, destinationFolder);
        GroovyUtil.updateTestCasePasted(newTestCase);
        return newTestCase;
    }

    public static TestCaseEntity moveTestCase(TestCaseEntity testCase, FolderEntity destinationFolder) throws Exception {

        EntityService.getInstance().validateName(testCase.getName());
        String oldTestCaseLocation = testCase.getLocation();
        String oldTestCaseRelativeLocation = testCase.getIdForDisplay();
        File oldTestCaseScriptFolder = new File(testCase.getProject().getFolderLocation() + File.separator
                + GroovyUtil.getScriptPackageRelativePathForTestCase(testCase));

        TestCaseEntity newTestCase = EntityFileServiceManager.move(testCase, destinationFolder);

        // If move .tc file success
        if (!newTestCase.getLocation().equals(oldTestCaseLocation)) {
            refactorReferencingTestSuites(destinationFolder.getProject(), testCase, oldTestCaseLocation);

            TestArtifactScriptRefactor.createForTestCaseEntity(oldTestCaseRelativeLocation).updateReferenceForProject(
                    newTestCase.getIdForDisplay(), newTestCase.getProject());
            
            File newTestCaseScriptFolder = new File(newTestCase.getProject().getFolderLocation() + File.separator
                    + GroovyUtil.getScriptPackageRelativePathForTestCase(newTestCase));
            // Ensure new folder for script file created
            newTestCaseScriptFolder.mkdirs();
            for (File groovyFile : oldTestCaseScriptFolder.listFiles()) {
                if (groovyFile.isFile() && groovyFile.getName().endsWith(GroovyConstants.GROOVY_FILE_EXTENSION)) {
                    FileUtils.moveFileToDirectory(groovyFile, newTestCaseScriptFolder, false);
                }
            }
            // Delete old script folder
            FileUtils.deleteQuietly(oldTestCaseScriptFolder);
        }
        return testCase;
    }

    private static void copyTestCaseFolder(FolderEntity srcFolderEntity, FolderEntity destFolderEntity,
            List<TestCaseEntity> pastedTestCases) throws Exception {

        for (Object childObject : srcFolderEntity.getChildrenEntities()) {
            if (childObject instanceof TestCaseEntity) {
                TestCaseEntity pastedTestCase = copyTestCase((TestCaseEntity) childObject, destFolderEntity);
                pastedTestCases.add(pastedTestCase);
            } else if (childObject instanceof FolderEntity) {
                FolderEntity folderEntity = (FolderEntity) childObject;
                FolderEntity newFolderEntity = folderEntity.clone();
                newFolderEntity.setProject(destFolderEntity.getProject());
                newFolderEntity.setParentFolder(destFolderEntity);
                EntityService.getInstance().saveEntity(newFolderEntity);
                copyTestCaseFolder(folderEntity, newFolderEntity, pastedTestCases);
            }
        }
    }

    public static FolderEntity copyTestCaseFolder(FolderEntity folder, FolderEntity destinationFolder) throws Exception {
        if (folder != null && destinationFolder != null) {
            FolderEntity newFolder = folder.clone();
            File fFolder = new File(destinationFolder.getLocation() + File.separator + folder.getName());
            if (fFolder.exists()) {
                // if folder existed, put a prefix "- Copy" into its name
                String name = EntityService.getInstance().getAvailableName(destinationFolder.getLocation(),
                        folder.getName() + Util.STRING_COPY_OF_NAME, false);
                newFolder.setName(name);
            }
            newFolder.setProject(destinationFolder.getProject());
            newFolder.setParentFolder(destinationFolder);

            EntityService.getInstance().saveEntity(newFolder);
            List<TestCaseEntity> pastedTestCases = new ArrayList<TestCaseEntity>();

            copyTestCaseFolder(folder, newFolder, pastedTestCases);
            for (TestCaseEntity pastedTestCase : pastedTestCases) {
                GroovyUtil.updateTestCasePasted(pastedTestCase);
            }
            GroovyUtil.getGroovyProject(destinationFolder.getProject()).refreshLocal(IResource.DEPTH_INFINITE, null);
            return newFolder;
        }
        return null;
    }

    public static void deleteTestCase(TestCaseEntity testCase) throws Exception {
        if (testCase != null) {
            EntityFileServiceManager.delete(testCase);
            FolderEntity testCaseRootFolder = FolderFileServiceManager.loadAllTestCaseDescendants(FolderFileServiceManager.getTestCaseRoot(testCase.getProject()));
            GroovyUtil.updateTestCaseDeleted(testCase, testCaseRootFolder);
            FolderFileServiceManager.refreshFolder(testCase.getParentFolder());
        } else {
            throw new NoEntityException("");
        }
    }

    public static void deleteTestCaseFolder(FolderEntity folderEntity) throws Exception {
        if (folderEntity != null) {
            FolderEntity testCaseRootFolder = FolderFileServiceManager.loadAllTestCaseDescendants(FolderFileServiceManager.getTestCaseRoot(folderEntity.getProject()));
            deleteTestCaseAndFolderRecursively(folderEntity, testCaseRootFolder);
            FolderFileServiceManager.refreshFolder(folderEntity.getParentFolder());
        }
    }

    private static void deleteTestCaseAndFolderRecursively(FolderEntity folder, FolderEntity testCaseRootFolder)
            throws Exception {
        for (TestCaseEntity childTestCase : FolderFileServiceManager.getChildTestCasesOfFolder(folder)) {
            EntityFileServiceManager.delete(childTestCase);
            GroovyUtil.updateTestCaseDeleted(childTestCase, testCaseRootFolder);
        }

        for (FolderEntity childFolder : FolderFileServiceManager.getChildFoldersOfFolder(folder)) {
            deleteTestCaseAndFolderRecursively(childFolder, testCaseRootFolder);
        }
        EntityFileServiceManager.delete(folder);
        GroovyUtil.updateTestCaseFolderDeleted(folder, testCaseRootFolder);
    }

    public static TestCaseEntity getTestCaseByName(FolderEntity parentFolder, String testCaseName) throws Exception {
        List<TestCaseEntity> testCases = FolderFileServiceManager.getChildTestCasesOfFolder(parentFolder);
        for (TestCaseEntity testCase : testCases) {
            if (testCase.getName().equals(testCaseName)) {
                return testCase;
            }
        }
        return null;
    }

    public static TestCaseEntity getByGUID(String guid, ProjectEntity project) throws Exception {
        File projectFolder = new File(project.getFolderLocation());
        if (projectFolder.exists() && projectFolder.isDirectory()) {
            File testCaseFolder = new File(
                    FileServiceConstant.getTestCaseRootFolderLocation(projectFolder.getAbsolutePath()));
            if (testCaseFolder.exists() && testCaseFolder.isDirectory()) {
                return getByGUID(testCaseFolder.getAbsolutePath(), guid, project);
            }
        }
        return null;
    }

    private static TestCaseEntity getByGUID(String testCaseFolder, String guid, ProjectEntity project) throws Exception {
        File folder = new File(testCaseFolder);
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles(EntityFileServiceManager.fileFilter)) {
                if (file.isFile()
                        && file.getName()
                                .toLowerCase()
                                .endsWith(TestCaseEntity.getTestCaseFileExtension().toLowerCase())) {
                    TestCaseEntity testCase = getTestCase(file.getAbsolutePath());
                    if (testCase.getTestCaseGuid().equals(guid)) {
                        return testCase;
                    }
                } else if (file.isDirectory()) {
                    TestCaseEntity result = getByGUID(file.getAbsolutePath(), guid, project);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    public static void updateReferencesTestCaseFolder(String oldFolderLocation, FolderEntity folder,
            List<TestSuiteEntity> testSuiteEntities) throws Exception {
        String oldFolderDisplayId = oldFolderLocation.replace(File.separator, "/") + "/";
        String folderDisplayId = folder.getRelativePath().replace(File.separator, "/") + "/";
        for (TestSuiteEntity testSuite : testSuiteEntities) {
            boolean save = false;

            for (TestSuiteTestCaseLink testCaseLink : testSuite.getTestSuiteTestCaseLinks()) {
                if (testCaseLink.getTestCaseId().startsWith(oldFolderDisplayId)) {
                    testCaseLink.setTestCaseId(testCaseLink.getTestCaseId().replaceFirst(oldFolderDisplayId,
                            folderDisplayId));
                    save = true;
                }
            }

            if (save) {
                TestSuiteFileServiceManager.updateTestSuite(testSuite);
            }
        }

        FolderFileServiceManager.refreshFolderScriptReferences(oldFolderDisplayId, folder);
    }

    public static TestCaseEntity getTestCaseByScriptFileName(String scriptFileName, ProjectEntity projectEntity)
            throws Exception {
        FolderEntity testCaseRootFolder = FolderFileServiceManager.getTestCaseRoot(projectEntity);
        for (TestCaseEntity testCaseEntity : FolderFileServiceManager.getDescendantTestCasesOfFolder(testCaseRootFolder)) {
            if (scriptFileName.equals(GroovyUtil.getScriptNameForTestCase(testCaseEntity))) {
                return testCaseEntity;
            }
        }
        return null;
    }

    public static TestCaseEntity getTestCaseByScriptFilePath(String scriptFilePath, ProjectEntity projectEntity)
            throws Exception {
        String testCaseId = GroovyUtil.getTestCaseIdByScriptPath(scriptFilePath, projectEntity);
        if (testCaseId == null || testCaseId.isEmpty())
            return null;

        return getTestCaseByDisplayId(testCaseId, projectEntity);
    }

    public static TestCaseEntity getTestCaseByDisplayId(String testCaseDisplayId, ProjectEntity projectEntity)
            throws Exception {
        String projectLocation = projectEntity.getFolderLocation();
        String testCasePk = projectLocation + File.separator + testCaseDisplayId
                + TestCaseEntity.getTestCaseFileExtension();
        return getTestCase(testCasePk);
    }

    public static List<TestSuiteEntity> getTestCaseReferences(TestCaseEntity testCase) throws Exception {
        List<TestSuiteEntity> testCaseReferences = new ArrayList<TestSuiteEntity>();

        String testCaseId = testCase.getRelativePathForUI().replace(File.separator, "/");

        FileServiceDataProviderSetting dataProviderSetting = new FileServiceDataProviderSetting();

        List<TestSuiteEntity> allTestSuites = FolderFileServiceManager.getDescendantTestSuitesOfFolder(FolderFileServiceManager.getTestSuiteRoot(testCase.getProject()));

        for (TestSuiteEntity testSuite : allTestSuites) {
            if (dataProviderSetting.getTestSuiteDataProvider().getTestCaseLink(testSuite, testCaseId) != null) {
                testCaseReferences.add(testSuite);
            }
        }

        return testCaseReferences;
    }

}
