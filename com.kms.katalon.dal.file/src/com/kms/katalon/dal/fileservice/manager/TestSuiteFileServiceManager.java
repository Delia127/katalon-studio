package com.kms.katalon.dal.fileservice.manager;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.FileServiceConstant;
import com.kms.katalon.dal.fileservice.constants.StringConstants;
import com.kms.katalon.entity.dal.exception.DuplicatedFileNameException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.entity.util.Util;

public class TestSuiteFileServiceManager {
	public static TestSuiteEntity getTestSuite(String testSuitePk) throws Exception {
		FileEntity entity = EntityFileServiceManager.get(new File(testSuitePk));
		if (entity instanceof TestSuiteEntity) {
			return (TestSuiteEntity) entity;
		}
		return null;
	}

	public static TestSuiteEntity initTestSuite(TestSuiteEntity testSuiteEntity) throws Exception {
		return testSuiteEntity;
	}

	public static void deleteTestSuite(TestSuiteEntity testSuite) throws Exception {
		EntityFileServiceManager.delete(testSuite);		
		FolderFileServiceManager.refreshFolder(testSuite.getParentFolder());
	}

	public static TestSuiteEntity updateTestSuite(TestSuiteEntity testSuite) throws Exception {
		validateData(testSuite);	
		testSuite = resetParentForChildElement(testSuite);
		// testSuite.setDateModified(new Date(System.currentTimeMillis()));
		if (nameChanged(testSuite)) {
			// Remove old name in cache, it will be added again when saving
			EntityService.getInstance().getEntityCache().remove(testSuite, true);
		}
		EntityService.getInstance().saveEntity(testSuite);		
		FolderFileServiceManager.refreshFolder(testSuite.getParentFolder());		
		return testSuite;
	}

	/**
	 ** Check duplication of name (if name changed)
	 **/
	public static void validateData(TestSuiteEntity testSuiteEntity) throws Exception {
		if (nameChanged(testSuiteEntity)) {
			// validate name
			EntityService.getInstance().validateName(testSuiteEntity.getName());
			// check duplicate name
			File file = new File(testSuiteEntity.getLocation());
			if (file.exists()) {
				throw new DuplicatedFileNameException(MessageFormat.format(StringConstants.MNG_EXC_EXISTED_TEST_SUITE_NAME, testSuiteEntity.getName()));
			}
		}
	}

	public static TestSuiteEntity resetParentForChildElement(TestSuiteEntity testSuite)
			throws Exception {
	    
		
		return testSuite;
	}

	private static boolean nameChanged(TestSuiteEntity testSuiteEntity) throws Exception {
		String pk = EntityService.getInstance().getEntityCache().getKey(testSuiteEntity);
		if (pk == null) {
		    pk = getTestSuite(testSuiteEntity.getId()).getId();
		}
		String oldName = pk.substring(pk.lastIndexOf(File.separator) + 1,
				pk.indexOf(TestSuiteEntity.getTestSuiteFileExtension()));
		return !oldName.equalsIgnoreCase(testSuiteEntity.getName());
	}

	/**
	 * Create new Test Suite
	 * @param parentFolder
	 * @param defaultName Test Suite name. Default name (New Test Suite) will be used if this null or empty
	 * @param timeOut
	 * @return {@link TestSuiteEntity}
	 * @throws Exception
	 */
	public static TestSuiteEntity addNewTestSuite(FolderEntity parentFolder, String defaultName, short timeOut)
			throws Exception {
		if (parentFolder != null) {
			if (defaultName == null || defaultName.trim().equals("")) {
				defaultName = StringConstants.MNG_NEW_TEST_SUITE;
			}
			String name = getAvailableTestSuiteName(parentFolder, defaultName);
			
			TestSuiteEntity newTestSuite = new TestSuiteEntity();
			newTestSuite.setName(name);
			newTestSuite.setParentFolder(parentFolder);
			newTestSuite.setTestSuiteGuid(Util.generateGuid());
			newTestSuite.setPageLoadTimeout(timeOut);
			newTestSuite.setProject(parentFolder.getProject());
			EntityService.getInstance().saveEntity(newTestSuite);
			
			FolderFileServiceManager.refreshFolder(parentFolder);
			return newTestSuite;
		}
		return null;
	}
	
	public static String getAvailableTestSuiteName(FolderEntity parentFolder, String name) throws Exception {
		return EntityService.getInstance().getAvailableName(parentFolder.getLocation(), name, true);
	}

	public static FolderEntity copyTestSuiteFolder(FolderEntity folder, FolderEntity destinationFolder)
			throws Exception {
		return EntityFileServiceManager.copyFolder(folder, destinationFolder);
	}

	public static TestSuiteEntity copyTestSuite(TestSuiteEntity testSuite, FolderEntity destinationFolder)
			throws Exception {
		return EntityFileServiceManager.copy(testSuite, destinationFolder);
	}

	public static TestSuiteEntity moveTestSuite(TestSuiteEntity testSuite, FolderEntity destinationFolder) throws Exception {
		return EntityFileServiceManager.move(testSuite, destinationFolder);
	}

	public static TestSuiteEntity getTestSuiteByName(FolderEntity parentFolder, String testSuiteName) throws Exception {
		List<TestSuiteEntity> testSuites = FolderFileServiceManager.getChildTestSuitesOfFolder(parentFolder);
		for (TestSuiteEntity testSuite : testSuites) {
			if (testSuite.getName().equals(testSuiteName)) {
				return testSuite;
			}
		}
		return null;
	}

	public static TestSuiteEntity getByGUID(String guid, ProjectEntity project) throws Exception {
		File projectFolder = new File(project.getFolderLocation());
		if (projectFolder.exists() && projectFolder.isDirectory()) {
			File testSuiteFolder = new File(FileServiceConstant.getTestSuiteRootFolderLocation(projectFolder
					.getAbsolutePath()));
			if (testSuiteFolder.exists() && testSuiteFolder.isDirectory()) {
				return getByGUID(testSuiteFolder.getAbsolutePath(), guid, project);
			}
		}
		return null;
	}

	private static TestSuiteEntity getByGUID(String testSuiteFolder, String guid, ProjectEntity project)
			throws Exception {
		File folder = new File(testSuiteFolder);
		if (folder.exists() && folder.isDirectory()) {
			for (File file : folder.listFiles(EntityFileServiceManager.fileFilter)) {
				if (file.isFile()
						&& file.getName().toLowerCase()
								.endsWith(TestSuiteEntity.getTestSuiteFileExtension().toLowerCase())) {
					FileEntity entity = EntityFileServiceManager.get(file);
					if (entity instanceof TestSuiteEntity && ((TestSuiteEntity) entity).getTestSuiteGuid().equals(guid)) {
						return (TestSuiteEntity) entity;
					}
				} else if (file.isDirectory()) {
					TestSuiteEntity result = getByGUID(file.getAbsolutePath(), guid, project);
					if (result != null) {
						return result;
					}
				}
			}
		}
		return null;
	}
}