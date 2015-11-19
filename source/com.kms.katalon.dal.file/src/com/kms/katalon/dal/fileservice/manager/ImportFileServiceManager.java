package com.kms.katalon.dal.fileservice.manager;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.FileServiceConstant;
import com.kms.katalon.dal.fileservice.dataprovider.setting.FileServiceDataProviderSetting;
import com.kms.katalon.dal.state.DataProviderState;
import com.kms.katalon.entity.dal.exception.CancelTaskException;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testdata.DataFilePropertyInputEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.util.ImportDuplicateEntityParameter;
import com.kms.katalon.entity.util.ImportDuplicateEntityResult;
import com.kms.katalon.entity.util.ImportType;
import com.kms.katalon.entity.util.Util;

public class ImportFileServiceManager {
	public final static String PROGRESS = "progress";
	public final static String DUPLICATE_ENTITY = "duplicateentity";
	public final static long timeSleep = 100;

	private int progress;
	private PropertyChangeSupport propertyChangeSupport;
	private ImportDuplicateEntityResult importDuplicateEntityResult;
	private ImportDuplicateEntityResult importDuplicateEntityResultFolder;
	private ImportDuplicateEntityResult importDuplicateEntityResultEntity;

	private boolean isCreateNewProject;
	private String projectName;
	private ProjectEntity currentProject;
	private ProjectEntity importProject;
	private ProjectEntity originalProject;

	private ImportType[] dataFileImportType;
	private ImportType[] folderImportType;
	private ImportType[] testObjectParentImportType;
	private ImportType[] testObjectChildrenImportType;
	private ImportType[] testCaseImportType;
	private ImportType[] testSuiteImportType;

	private Map<String, String> badTestCases;
	private boolean isCancelImportTask;
	private boolean isWaitingConfirmation;

	private Map<String, String> nameChangedDataFileFolders;
	private Map<String, String> nameChangedTestCaseFolders;
	private Map<String, String> nameChangedTestCases;
	private Map<String, String> nameChangedDataFiles;
	private Map<String, String> nameChangedWebElements;

	private List<TestCaseEntity> importedTestCase;

	private FolderEntity importWebElementRootFolder;

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		propertyChangeSupport.firePropertyChange(PROGRESS, this.progress, progress);
		this.progress = progress;
	}

	public Map<String, String> getBadTestCases() {
		return badTestCases;
	}

	private void raiseDuplicateEntityEvent(ImportType[] availableImportTypes, String message) {
		ImportDuplicateEntityParameter importDuplicateEntityParameter = new ImportDuplicateEntityParameter(
				availableImportTypes, message);
		propertyChangeSupport.firePropertyChange(DUPLICATE_ENTITY, null, importDuplicateEntityParameter);
	}

	private ImportDuplicateEntityResult getImportDuplicateEntityResult() {
		return importDuplicateEntityResult;
	}

	public void setImportDuplicateEntityResult(ImportDuplicateEntityResult importDuplicateEntityResult) {
		this.importDuplicateEntityResult = importDuplicateEntityResult;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	private boolean isCreateNewProject() {
		return isCreateNewProject;
	}

	private void setCreateNewProject(boolean isCreateNewProject) {
		this.isCreateNewProject = isCreateNewProject;
	}

	private String getProjectName() {
		return projectName;
	}

	private void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	private ProjectEntity getCurrentProject() {
		return currentProject;
	}

	private void setCurrentProject(ProjectEntity currentProject) {
		this.currentProject = currentProject;
	}

	public ImportFileServiceManager(boolean isCreateNewProject, String projectName, ProjectEntity currentProject) {
		this.setCreateNewProject(isCreateNewProject);
		this.setCurrentProject(currentProject);
		setCancelImportTask(false);
		setProjectName(projectName);
		setWaitingConfirmation(false);
		folderImportType = new ImportType[] { ImportType.New, ImportType.Merge };
		dataFileImportType = new ImportType[] { ImportType.New, ImportType.Override };
		testObjectParentImportType = new ImportType[] { ImportType.New, ImportType.Merge };
		testObjectChildrenImportType = new ImportType[] { ImportType.New, ImportType.Override };
		testCaseImportType = new ImportType[] { ImportType.New, ImportType.Override };
		testSuiteImportType = new ImportType[] { ImportType.New, ImportType.Override, ImportType.Merge };
		propertyChangeSupport = new PropertyChangeSupport(this);
		setImportDuplicateEntityResult(null);

		importDuplicateEntityResultFolder = null;
		importDuplicateEntityResultEntity = null;
		badTestCases = new HashMap<String, String>();
		nameChangedDataFileFolders = new HashMap<String, String>();
		nameChangedTestCaseFolders = new HashMap<String, String>();
		nameChangedTestCases = new HashMap<String, String>();
		nameChangedDataFiles = new HashMap<String, String>();
		nameChangedWebElements = new HashMap<String, String>();

		importedTestCase = new ArrayList<TestCaseEntity>();
	}

	public ProjectEntity executeImport(File importDirectory) throws Exception {
		int importProcess = 0;
		File tempProjectDirectory = null;
		originalProject = currentProject;
		try {
			// Clear Cache before import
			EntityService.getInstance().getEntityCache().clear();

			// Import Project and Project Versions
			ProjectEntity project = null;
			if (isCreateNewProject()) {
				project = ProjectFileServiceManager.getProject(findEntityFile(ProjectEntity.getProjectFileExtension(),
						importDirectory).getAbsolutePath());

				importProject = project;
				// Backup original project folder
				tempProjectDirectory = importDirectory;

				project = ProjectFileServiceManager.addNewProject(getProjectName(), project.getDescription(),
						project.getPageLoadTimeout(), importDirectory.getParent());

				setCurrentProject(project);
			} else {
				project = getCurrentProject();
				importProject = ProjectFileServiceManager.getProject(findEntityFile(
						ProjectEntity.getProjectFileExtension(), importDirectory).getAbsolutePath());

				// Backup original project folder
				tempProjectDirectory = new File(project.getFolderLocation() + "_Temp_" + System.currentTimeMillis());
				FileUtils.copyDirectory(new File(project.getFolderLocation()), tempProjectDirectory);
			}

			checkCancelImportTask();
			setProgress(importProcess += 3);
			DataProviderState.getInstance().setCurrentProject(currentProject);

			// Import Data File
			for (File file : importDirectory.listFiles(EntityFileServiceManager.fileFilter)) {
				if (file.getName().equalsIgnoreCase(FileServiceConstant.DATA_FILE_ROOT_FOLDER_NAME)
						&& file.isDirectory()) {
					importDataFile(FolderFileServiceManager.getFolder(FileServiceConstant
							.getDataFileRootFolderLocation(project.getFolderLocation())), project, file);
					break;
				}
			}
			importDuplicateEntityResultEntity = null;

			checkCancelImportTask();
			setProgress(importProcess += 4);
			DataProviderState.getInstance().setCurrentProject(currentProject);

			// Import Test Object
			for (File file : importDirectory.listFiles(EntityFileServiceManager.fileFilter)) {
				if (file.getName().equalsIgnoreCase(FileServiceConstant.OBJECT_REPOSITORY_ROOT_FOLDER_NAME)
						&& file.isDirectory()) {
					importWebElementRootFolder = FolderFileServiceManager.getFolder(FileServiceConstant
							.getObjectRepositoryRootFolderLocation(project.getFolderLocation()));
					importTestObject(importWebElementRootFolder, project, null, file);
					break;
				}
			}
			importDuplicateEntityResultEntity = null;

			checkCancelImportTask();
			setProgress(importProcess += 3);
			DataProviderState.getInstance().setCurrentProject(currentProject);

			// Import Test Case
			for (File file : importDirectory.listFiles(EntityFileServiceManager.fileFilter)) {
				if (file.getName().equalsIgnoreCase(FileServiceConstant.TEST_CASE_ROOT_FOLDER_NAME)
						&& file.isDirectory()) {
					importTestCase(FolderFileServiceManager.getFolder(FileServiceConstant
							.getTestCaseRootFolderLocation(project.getFolderLocation())), project, file);
					break;
				}
			}
			importDuplicateEntityResultEntity = null;

			checkCancelImportTask();
			setProgress(importProcess += 6);
			DataProviderState.getInstance().setCurrentProject(currentProject);

			// Import Test Suite
			for (File file : importDirectory.listFiles(EntityFileServiceManager.fileFilter)) {
				if (file.getName().equalsIgnoreCase(FileServiceConstant.TEST_SUITE_ROOT_FOLDER_NAME)
						&& file.isDirectory()) {
					importTestSuite(FolderFileServiceManager.getFolder(FileServiceConstant
							.getTestSuiteRootFolderLocation(project.getFolderLocation())), project, file);
					break;
				}
			}
			importDuplicateEntityResultEntity = null;

			checkCancelImportTask();
			setProgress(importProcess += 3);

			// Clear Cache after import
			EntityService.getInstance().getEntityCache().clear();

			return project;
		} catch (Exception e) {
			if (tempProjectDirectory != null && tempProjectDirectory.exists()) {
				File projectFolder = new File(getCurrentProject().getFolderLocation());
				if (projectFolder.exists() && projectFolder.isDirectory()) {
					FileUtils.cleanDirectory(projectFolder);
				}
				if (!isCreateNewProject) {
					FileUtils.copyDirectory(tempProjectDirectory, projectFolder);
				}
			}
			throw e;
		} finally {
			if (tempProjectDirectory != null && tempProjectDirectory.isDirectory() && !isCreateNewProject) {
				FileUtils.deleteDirectory(tempProjectDirectory);
			}
			DataProviderState.getInstance().setCurrentProject(originalProject);
		}
	}

	private void checkCancelImportTask() throws CancelTaskException {
		if (isCancelImportTask()) {
			throw new CancelTaskException();
		}
	}

	public File findEntityFile(String fileExtension, File directory) {
		for (int i = 0; i < directory.listFiles(EntityFileServiceManager.fileFilter).length; i++) {
			if (directory.listFiles(EntityFileServiceManager.fileFilter)[i].getName().toLowerCase().endsWith(fileExtension.toLowerCase())
					&& directory.listFiles(EntityFileServiceManager.fileFilter)[i].isFile()) {
				return directory.listFiles(EntityFileServiceManager.fileFilter)[i];
			}
		}
		return null;
	}

	public File findFolder(String folderName, File directory) {
		for (int i = 0; i < directory.listFiles(EntityFileServiceManager.fileFilter).length; i++) {
			if (directory.listFiles(EntityFileServiceManager.fileFilter)[i].getName().toLowerCase().equals(folderName.toLowerCase())
					&& directory.listFiles(EntityFileServiceManager.fileFilter)[i].isDirectory()) {
				return directory.listFiles(EntityFileServiceManager.fileFilter)[i];
			}
		}
		return null;
	}

	private FolderEntity importFolder(FolderEntity parentFolder, FolderEntity folder, ProjectEntity project,
			String rootFolderName, Map<String, String> nameChangedFolders) throws Exception, InterruptedException {
		checkCancelImportTask();
		DataProviderState.getInstance().setCurrentProject(currentProject);
		FolderEntity duplicateFolder = FolderFileServiceManager
				.getFolderByName(parentFolder, folder.getName(), project);

		if (duplicateFolder != null) {
			ImportDuplicateEntityResult result;
			if (!folder.getName().equals(rootFolderName)) {
				if (importDuplicateEntityResultFolder == null) {
					StringBuilder folderStringBuilder = new StringBuilder("Folder: ");
					folderStringBuilder.append(folder.getName());
					importDuplicateEntityResultFolder = getDuplicateDialogResult(folderImportType, folderStringBuilder);
				}
				result = new ImportDuplicateEntityResult(importDuplicateEntityResultFolder.getImportType(),
						importDuplicateEntityResultFolder.isApplyToAll());
			} else {
				result = new ImportDuplicateEntityResult(ImportType.Merge, false);
			}

			if (result.getImportType() == ImportType.New) {
				String newFolderName = FolderFileServiceManager.getAvailableFolderName(parentFolder, folder.getName());
				if (nameChangedFolders != null) {
					nameChangedFolders.put(folder.getRelativePath(), newFolderName);
				}
				folder.setName(newFolderName);

				folder = saveFolderEntity(parentFolder, folder);
			} else if (result.getImportType() == ImportType.Merge) {
				folder = duplicateFolder;
			}
			if (importDuplicateEntityResultFolder != null && !importDuplicateEntityResultFolder.isApplyToAll()) {
				importDuplicateEntityResultFolder = null;
			}
		} else {
			folder = saveFolderEntity(parentFolder, folder);
		}
		return folder;
	}

	private FolderEntity saveFolderEntity(FolderEntity parentFolder, FolderEntity folder) throws Exception {
		DataProviderState.getInstance().setCurrentProject(currentProject);
		return FolderFileServiceManager.addNewFolder(parentFolder, folder.getName());
	}

	private FolderEntity getFolderFromImportProject(File file) throws Exception {
		DataProviderState.getInstance().setCurrentProject(importProject);
		return FolderFileServiceManager.getFolder(file.getAbsolutePath());
	}

	public void importDataFile(FolderEntity parentFolder, ProjectEntity project, File importDirectory) throws Exception {
		checkCancelImportTask();

		for (File file : importDirectory.listFiles(EntityFileServiceManager.fileFilter)) {
			if (file.isDirectory()) {
				FolderEntity folderEntity = getFolderFromImportProject(file);
				folderEntity = importFolder(parentFolder, folderEntity, project,
						FileServiceConstant.DATA_FILE_ROOT_FOLDER_NAME, nameChangedDataFileFolders);
				importDataFile(folderEntity, project, file);
			} else if (file.isFile()
					&& file.getName().toLowerCase().endsWith(DataFileEntity.getTestDataFileExtension().toLowerCase())) {
				DataFileEntity dataFile = createDataFile(file);
				dataFile = importDataFileEntity(parentFolder, dataFile, project);
			}
		}
	}

	private DataFileEntity importDataFileEntity(FolderEntity parentFolder, DataFileEntity dataFile,
			ProjectEntity project) throws Exception {
		DataProviderState.getInstance().setCurrentProject(currentProject);
		DataFileEntity duplicateDataFile = DataFileFileServiceManager.getDuplicatedDataFile(parentFolder,
				dataFile.getName());
		updateDataFileGUID(project, dataFile);

		if (duplicateDataFile != null) {
			if (importDuplicateEntityResultEntity == null) {
				StringBuilder dataFileStringBuilder = new StringBuilder("Data File: ");
				dataFileStringBuilder.append(dataFile.getName());
				importDuplicateEntityResultEntity = getDuplicateDialogResult(dataFileImportType, dataFileStringBuilder);
			}

			if (importDuplicateEntityResultEntity.getImportType() == ImportType.New) {
				String oldLocation = dataFile.getRelativePath();

				dataFile.setName(DataFileFileServiceManager.getAvailableDataFileName(parentFolder,
						dataFile.getName()));
				saveDataFileEntity(parentFolder, dataFile);

				nameChangedDataFiles.put(oldLocation, dataFile.getName());
			} else if (importDuplicateEntityResultEntity.getImportType() == ImportType.Override) {
				DataFilePropertyInputEntity dataFileInputProperties = new DataFilePropertyInputEntity();

				dataFileInputProperties.setPk(duplicateDataFile.getLocation());
				dataFileInputProperties.setName(duplicateDataFile.getName());
				dataFileInputProperties.setDescription(dataFile.getDescription());
				dataFileInputProperties.setDataFileDriver(dataFile.getDriver().toString());
				dataFileInputProperties.setdataSourceURL(dataFile.getDataSourceUrl());
				dataFileInputProperties.setSheetName(dataFile.getSheetName());
				dataFileInputProperties.setIsInternalPath(dataFile.getIsInternalPath());

				dataFile = DataFileFileServiceManager.updateDataFileProperty(dataFileInputProperties, project);
			}
			if (!importDuplicateEntityResultEntity.isApplyToAll()) {
				importDuplicateEntityResultEntity = null;
			}
		} else {
			saveDataFileEntity(parentFolder, dataFile);
		}
		return dataFile;
	}

	private void saveDataFileEntity(FolderEntity parentFolder, DataFileEntity dataFile) throws Exception {
		DataFileEntity newDataFile = dataFile.clone();
		newDataFile.setParentFolder(parentFolder);
		newDataFile.setProject(parentFolder.getProject());
		newDataFile.setDataFileGUID(Util.generateGuid());
		EntityService.getInstance().saveEntity(newDataFile);
	}

	private boolean updateDataFileGUID(ProjectEntity project, DataFileEntity dataFile) throws Exception {
		DataFileEntity duplicatedDataFile = DataFileFileServiceManager.getByGUID(dataFile.getDataFileGUID(), project);
		if (duplicatedDataFile != null) {
			dataFile.setDataFileGUID(UUID.randomUUID().toString());
			return true;
		}
		return false;
	}

	public void importTestObject(FolderEntity parentFolder, ProjectEntity project, WebElementEntity parentWebElement,
			File importDirectory) throws Exception {
		checkCancelImportTask();

		for (File file : importDirectory.listFiles(EntityFileServiceManager.fileFilter)) {
			if (file.isDirectory()) {
				FolderEntity folderEntity = getFolderFromImportProject(file);
				folderEntity = importFolder(parentFolder, folderEntity, project,
						FileServiceConstant.OBJECT_REPOSITORY_ROOT_FOLDER_NAME, null);
				importTestObject(folderEntity, project, parentWebElement, file);
			} else if (file.isFile()
					&& file.getName().toLowerCase()
							.endsWith(WebElementEntity.getWebElementFileExtension().toLowerCase())) {
				boolean isParent = false;
				WebElementEntity webElement = createWebElement(file);

				importWebElementEntity(parentFolder, parentWebElement, webElement, project, isParent);

				if (importDuplicateEntityResultEntity != null && !importDuplicateEntityResultEntity.isApplyToAll()) {
					importDuplicateEntityResultEntity = null;
				}
				if (importDuplicateEntityResultFolder != null && !importDuplicateEntityResultFolder.isApplyToAll()) {
					importDuplicateEntityResultFolder = null;
				}
			}
		}

	}

	private WebElementEntity importWebElementEntity(FolderEntity parentFolder, WebElementEntity parentWebElement,
			WebElementEntity webElement, ProjectEntity project, boolean isParent) throws Exception {
		DataProviderState.getInstance().setCurrentProject(currentProject);
		WebElementEntity duplicateWebElement = WebElementFileServiceManager.getDuplicateWebElement(parentWebElement,
				parentFolder, webElement.getName(), project);
		WebElementEntity newWebElement = null;

		if (duplicateWebElement != null) {
			StringBuilder webElementStringBuilder = new StringBuilder("Test Object: ");
			webElementStringBuilder.append(webElement.getName());
			if (importDuplicateEntityResultFolder == null && isParent) {
				webElementStringBuilder.append(" (which have children test objects)");

				importDuplicateEntityResultFolder = getDuplicateDialogResult(testObjectParentImportType,
						webElementStringBuilder);
			} else if (importDuplicateEntityResultEntity == null && !isParent) {
				importDuplicateEntityResultEntity = getDuplicateDialogResult(testObjectChildrenImportType,
						webElementStringBuilder);
			}

			if ((!isParent && importDuplicateEntityResultEntity.getImportType() == ImportType.New)
					|| (isParent && importDuplicateEntityResultFolder.getImportType() == ImportType.New)) {
				updateWebElementGUID(project, webElement.getElementGuidId());
				newWebElement = saveWebElementEntity(parentFolder, parentWebElement, webElement, true);

			} else if ((isParent && importDuplicateEntityResultFolder.getImportType() == ImportType.Merge)
					|| (!isParent && importDuplicateEntityResultEntity.getImportType() == ImportType.Override)) {
				duplicateWebElement.setDescription(webElement.getDescription());
				String oldGuid = duplicateWebElement.getElementGuidId();
				duplicateWebElement.setElementGuidId(webElement.getElementGuidId());

				newWebElement = WebElementFileServiceManager.saveWebElement(duplicateWebElement);

				updateWebElementPropertyRefElement(project, oldGuid, webElement);
			}
		} else {
			updateWebElementGUID(project, webElement.getElementGuidId());

			newWebElement = saveWebElementEntity(parentFolder, parentWebElement, webElement, false);
		}
		return newWebElement;
	}

	private void renameWebElement(FolderEntity parentFolder, WebElementEntity parentWebElement,
			WebElementEntity webElement) throws Exception {
		String oldLocation = webElement.getLocation();
		webElement.setName(WebElementFileServiceManager.getAvailableWebElementName(parentFolder, webElement.getName()));
		nameChangedWebElements.put(oldLocation, webElement.getName());
	}

	private WebElementEntity saveWebElementEntity(FolderEntity parentFolder, WebElementEntity parentWebElement,
			WebElementEntity webElement, boolean isRenamed) throws Exception {

		WebElementEntity newWebELement = webElement.clone();
		if (isRenamed) {
			renameWebElement(parentFolder, parentWebElement, newWebELement);
		}
		newWebELement.setParentFolder(parentFolder);
		newWebELement.setProject(currentProject);

		EntityService.getInstance().saveEntity(newWebELement);
		return newWebELement;
	}

	private boolean updateWebElementGUID(ProjectEntity project, String guid) throws Exception {
		WebElementEntity webElementEntity = WebElementFileServiceManager.getByGUID(guid, project);
		if (webElementEntity != null) {
			webElementEntity.setElementGuidId(Util.generateGuid());
			WebElementFileServiceManager.saveWebElement(webElementEntity);

			updateWebElementPropertyRefElement(project, guid, webElementEntity);
			return true;
		}
		return false;
	}

	private void updateWebElementPropertyRefElement(ProjectEntity project, String guid,
			WebElementEntity webElementEntity) throws Exception {
		for (WebElementEntity webElementWithRefElements : WebElementFileServiceManager
				.getWebElementPropertyByRefELement(guid, project, true)) {
			for (WebElementPropertyEntity property : webElementWithRefElements.getWebElementProperties()) {
				if (property.getName().equalsIgnoreCase(WebElementEntity.ref_element)) {
					property.setValue(webElementEntity.getElementGuidId());
				}
			}
			WebElementFileServiceManager.saveWebElement(webElementWithRefElements);
		}
	}

	public void importTestCase(FolderEntity parentFolder, ProjectEntity project, File importDirectory) throws Exception {
		checkCancelImportTask();

		for (File file : importDirectory.listFiles(EntityFileServiceManager.fileFilter)) {
			if (file.isDirectory()) {
				FolderEntity folderEntity = getFolderFromImportProject(file);
				folderEntity = importFolder(parentFolder, folderEntity, project,
						FileServiceConstant.TEST_CASE_ROOT_FOLDER_NAME, nameChangedTestCaseFolders);
				importTestCase(folderEntity, project, file);
			} else if (file.isFile()
					&& file.getName().toLowerCase().endsWith(TestCaseEntity.getTestCaseFileExtension().toLowerCase())) {

				TestCaseEntity testCase = createTestCase(file);
				testCase = importTestCaseEntity(parentFolder, testCase, project);

			}
		}
	}

	private TestCaseEntity importTestCaseEntity(FolderEntity parentFolder, TestCaseEntity testCase,
			ProjectEntity project) throws Exception {
		DataProviderState.getInstance().setCurrentProject(currentProject);
		TestCaseEntity duplicateTestCase = TestCaseFileServiceManager.getTestCaseByName(parentFolder,
				testCase.getName());

		updateTestCaseGUID(project, testCase);

		if (duplicateTestCase != null) {
			if (importDuplicateEntityResultEntity == null) {
				StringBuilder testCaseStringBuilder = new StringBuilder("Test Case: ");
				testCaseStringBuilder.append(testCase.getName());
				importDuplicateEntityResultEntity = getDuplicateDialogResult(testCaseImportType, testCaseStringBuilder);
			}

			if (importDuplicateEntityResultEntity.getImportType() == ImportType.New) {
				String oldLocation = testCase.getRelativePath();

				String newName = TestCaseFileServiceManager.getAvailableName(parentFolder, duplicateTestCase.getName());
				testCase.setName(newName);

				testCase = saveTestCaseEntity(parentFolder, testCase, project);

				nameChangedTestCases.put(oldLocation, testCase.getName());

			} else if (importDuplicateEntityResultEntity.getImportType() == ImportType.Override) {
				duplicateTestCase.setComment(testCase.getComment());
				duplicateTestCase.setDescription(testCase.getDescription());
				duplicateTestCase.setTag(testCase.getTag());
				duplicateTestCase.setTestCaseGuid(testCase.getTestCaseGuid());
				duplicateTestCase.setDataFiles(testCase.getDataFiles());

				updateTestCaseDataFileLink(project, duplicateTestCase);

				testCase = TestCaseFileServiceManager.updateTestCase(duplicateTestCase);
			}
		} else {
			testCase = saveTestCaseEntity(parentFolder, testCase, project);
		}
		importedTestCase.add(testCase);
		return testCase;
	}

	private void updateTestCaseDataFileLink(ProjectEntity project, TestCaseEntity newTestCase) {
		for (DataFileEntity dataFile : newTestCase.getDataFiles()) {
			checkDataFileNameChanged(dataFile);
			setProject(dataFile.getParentFolder(), project);
		}
	}

	private boolean updateTestCaseGUID(ProjectEntity project, TestCaseEntity testCase) throws Exception {
		TestCaseEntity duplicatedTestCase = TestCaseFileServiceManager.getByGUID(testCase.getTestCaseGuid(), project);
		if (duplicatedTestCase != null) {
			testCase.setTestCaseGuid(UUID.randomUUID().toString());
			return true;
		}
		return false;
	}

	private void setProject(FolderEntity folder, ProjectEntity project) {
		if (folder.getParentFolder() == null) {
			folder.setProject(project);
		} else {
			setProject(folder.getParentFolder(), project);
		}
	}

	private TestCaseEntity saveTestCaseEntity(FolderEntity parentFolder, TestCaseEntity testCase, ProjectEntity project)
			throws Exception {
		TestCaseEntity newTestCase = testCase.clone();
		newTestCase.setParentFolder(parentFolder);
		newTestCase.setProject(currentProject);

		updateTestCaseDataFileLink(project, newTestCase);
		EntityService.getInstance().saveEntity(newTestCase);
		return newTestCase;
	}

	public void importTestSuite(FolderEntity parentFolder, ProjectEntity project, File importDirectory)
			throws Exception {
		checkCancelImportTask();

		for (File file : importDirectory.listFiles(EntityFileServiceManager.fileFilter)) {
			if (file.isDirectory()) {
				FolderEntity folderEntity = getFolderFromImportProject(file);
				folderEntity = importFolder(parentFolder, folderEntity, project,
						FileServiceConstant.TEST_SUITE_ROOT_FOLDER_NAME, null);
				importTestSuite(folderEntity, project, file);
			} else if (file.isFile()
					&& file.getName().toLowerCase().endsWith(TestSuiteEntity.getTestSuiteFileExtension().toLowerCase())) {

				TestSuiteEntity testSuite = createTestSuite(file);
				importTestSuiteEntity(testSuite, parentFolder, project);
			}
		}
	}

	private TestSuiteEntity importTestSuiteEntity(TestSuiteEntity testSuite, FolderEntity parentFolder,
			ProjectEntity project) throws Exception {
		DataProviderState.getInstance().setCurrentProject(currentProject);
		updateTestSuiteGUID(project, testSuite);

		TestSuiteEntity duplicateTestSuite = TestSuiteFileServiceManager.getTestSuiteByName(parentFolder,
				testSuite.getName());

		if (duplicateTestSuite != null) {
			if (importDuplicateEntityResultEntity == null) {
				StringBuilder testSuiteStringBuilder = new StringBuilder("Test Suite: ");
				testSuiteStringBuilder.append(testSuite.getName());
				importDuplicateEntityResultEntity = getDuplicateDialogResult(testSuiteImportType,
						testSuiteStringBuilder);
			}

			if (importDuplicateEntityResultEntity.getImportType() == ImportType.New) {
				testSuite.setName(TestSuiteFileServiceManager.getAvailableTestSuiteName(parentFolder,
						testSuite.getName()));
				saveTestSuiteEntity(testSuite, parentFolder, project);

			} else if (importDuplicateEntityResultEntity.getImportType() == ImportType.Override) {
				duplicateTestSuite.setDescription(testSuite.getDescription());
				duplicateTestSuite.setIsRerun(testSuite.getIsRerun());
				duplicateTestSuite.setNumberOfRerun(testSuite.getNumberOfRerun());
				duplicateTestSuite.setPageLoadTimeout(testSuite.getPageLoadTimeout());
				duplicateTestSuite.setTestSuiteGuid(testSuite.getTestSuiteGuid());

				duplicateTestSuite.setTestSuiteTestCaseLinks(testSuite.getTestSuiteTestCaseLinks());
				saveTestSuiteEntity(duplicateTestSuite, duplicateTestSuite.getParentFolder(), project);

			} else if (importDuplicateEntityResultEntity.getImportType() == ImportType.Merge) {
				if (duplicateTestSuite.getDescription() == null || duplicateTestSuite.getDescription().isEmpty()) {
					duplicateTestSuite.setDescription(testSuite.getDescription());
				}

				for (TestSuiteTestCaseLink testSuiteTestCaseLink : testSuite.getTestSuiteTestCaseLinks()) {
                    TestCaseEntity testCase = (new FileServiceDataProviderSetting()).getTestCaseDataProvider()
                            .getTestCaseByDisplayId(testSuiteTestCaseLink.getTestCaseId());
					checkTestCaseNameChanged(testCase);
					setProject(testCase.getParentFolder(), project);
					boolean isExisted = false;
					for (TestSuiteTestCaseLink oldtestSuiteTestCaseLink : duplicateTestSuite
							.getTestSuiteTestCaseLinks()) {
						if (oldtestSuiteTestCaseLink.getTestCaseId().equals(testCase.getRelativePathForUI())) {
							isExisted = true;
							break;
						}
					}
					if (!isExisted) {
						TestSuiteTestCaseLink newTestSuiteTestCaseLink = new TestSuiteTestCaseLink();
						newTestSuiteTestCaseLink.setTestCaseId(testSuiteTestCaseLink.getTestCaseId());
						newTestSuiteTestCaseLink.setIsReuseDriver(testSuiteTestCaseLink.getIsReuseDriver());
						newTestSuiteTestCaseLink.setIsRun(testSuiteTestCaseLink.getIsRun());
						duplicateTestSuite.getTestSuiteTestCaseLinks().add(newTestSuiteTestCaseLink);
					}
				}

				TestSuiteFileServiceManager.resetParentForChildElement(duplicateTestSuite);
				EntityService.getInstance().saveEntityWithoutCache(duplicateTestSuite);
			}
		} else {
			saveTestSuiteEntity(testSuite, parentFolder, project);
		}
		return testSuite;
	}

	private boolean updateTestSuiteGUID(ProjectEntity project, TestSuiteEntity testSuite) throws Exception {
		TestSuiteEntity duplicatedTestSuite = TestSuiteFileServiceManager.getByGUID(testSuite.getTestSuiteGuid(),
				project);
		if (duplicatedTestSuite != null) {
			testSuite.setTestSuiteGuid(UUID.randomUUID().toString());
			return true;
		}
		return false;
	}

	private void checkTestCaseNameChanged(TestCaseEntity testCase) {
		String newName = nameChangedTestCases.get(testCase.getRelativePath());
		if (newName != null) {
			testCase.setName(newName);
		}
		checkFolderNameChanged(testCase.getParentFolder(), nameChangedTestCaseFolders);
	}

	private void checkDataFileNameChanged(DataFileEntity dataFile) {
		String newName = nameChangedDataFiles.get(dataFile.getRelativePath());
		if (newName != null) {
			dataFile.setName(newName);
		}
		checkFolderNameChanged(dataFile.getParentFolder(), nameChangedDataFileFolders);
	}

	private void checkFolderNameChanged(FolderEntity folder, Map<String, String> nameChangedFolderMap) {
		if (folder != null) {
			String newName = nameChangedFolderMap.get(folder.getRelativePath());
			if (newName != null) {
				folder.setName(newName);
			}
			checkFolderNameChanged(folder.getParentFolder(), nameChangedFolderMap);
		}
	}

	private void saveTestSuiteEntity(TestSuiteEntity testSuite, FolderEntity parentFolder, ProjectEntity project)
			throws Exception {
		TestSuiteEntity newTestSuite = testSuite.clone();
		newTestSuite.setParentFolder(parentFolder);
		newTestSuite.setProject(project);

		FileServiceDataProviderSetting dataProviderSetting = new FileServiceDataProviderSetting();
		for (TestSuiteTestCaseLink testSuiteTestCaseLink : newTestSuite.getTestSuiteTestCaseLinks()) {
            TestCaseEntity testCase = dataProviderSetting.getTestCaseDataProvider().getTestCaseByDisplayId(
                    testSuiteTestCaseLink.getTestCaseId());
			checkTestCaseNameChanged(testCase);
			setProject(testCase.getParentFolder(), project);
		}
		TestSuiteFileServiceManager.resetParentForChildElement(newTestSuite);
		EntityService.getInstance().saveEntity(newTestSuite);
	}

	public DataFileEntity createDataFile(File xmlFile) throws Exception {
		DataProviderState.getInstance().setCurrentProject(importProject);
		return DataFileFileServiceManager.getDataFile(xmlFile.getAbsolutePath());
	}

	public WebElementEntity createWebElement(File xmlFile) throws Exception {
		DataProviderState.getInstance().setCurrentProject(importProject);
		return WebElementFileServiceManager.getWebElement(xmlFile.getAbsolutePath());
	}

	public TestCaseEntity createTestCase(File xmlFile) throws Exception {
		DataProviderState.getInstance().setCurrentProject(importProject);
		return TestCaseFileServiceManager.getTestCase(xmlFile.getAbsolutePath());
	}

	public TestSuiteEntity createTestSuite(File xmlFile) throws Exception {
		DataProviderState.getInstance().setCurrentProject(importProject);
		return TestSuiteFileServiceManager.getTestSuite(xmlFile.getAbsolutePath());
	}

	private ImportDuplicateEntityResult getDuplicateDialogResult(ImportType[] importTypes,
			StringBuilder folderStringBuilder) throws InterruptedException, CancelTaskException {
		checkCancelImportTask();
		while (isWaitingConfirmation()) {
			Thread.sleep(timeSleep);
		}
		setImportDuplicateEntityResult(null);
		raiseDuplicateEntityEvent(importTypes, folderStringBuilder.toString());
		while (getImportDuplicateEntityResult() == null) {
			Thread.sleep(timeSleep);
		}
		if (getImportDuplicateEntityResult().getImportType() == ImportType.Cancel) {
			throw new CancelTaskException();
		}
		return getImportDuplicateEntityResult();
	}

	private boolean isCancelImportTask() {
		return isCancelImportTask;
	}

	public void setCancelImportTask(boolean cancelImportTask) {
		this.isCancelImportTask = cancelImportTask;
	}

	public boolean isWaitingConfirmation() {
		return isWaitingConfirmation;
	}

	public void setWaitingConfirmation(boolean isWaitingConfirmation) {
		this.isWaitingConfirmation = isWaitingConfirmation;
	}
}
