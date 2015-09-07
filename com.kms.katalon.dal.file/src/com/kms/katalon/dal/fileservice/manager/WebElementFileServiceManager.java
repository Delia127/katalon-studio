package com.kms.katalon.dal.fileservice.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.FileServiceConstant;
import com.kms.katalon.dal.fileservice.constants.StringConstants;
import com.kms.katalon.dal.fileservice.exception.InvalidFileNameException;
import com.kms.katalon.entity.IEntity;
import com.kms.katalon.entity.dal.exception.DuplicatedFileNameException;
import com.kms.katalon.entity.dal.exception.FilePathTooLongException;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.util.Util;
import com.kms.katalon.groovy.util.GroovyRefreshUtil;

public class WebElementFileServiceManager {
	public static WebElementEntity getWebElement(String webElementFileLocation) throws Exception {
		FileEntity entity = null;
		if (webElementFileLocation != null) {
			entity = EntityFileServiceManager.get(new File(webElementFileLocation));
		}
		if (entity != null && entity instanceof WebElementEntity) {
			return (WebElementEntity) entity;
		}
		return null;
	}

	/**
	 * Create new Test Object
	 * @param parentFolder
	 * @param defaultName Test Object name. Default name (New Element) will be used if this null or empty
	 * @return {@link WebElementEntity}
	 * @throws Exception
	 */
	public static WebElementEntity addNewWebElement(FolderEntity parentFolder, String defaultName) throws Exception {
		if (parentFolder != null) {
			if (defaultName == null || defaultName.trim().equals("")) {
				defaultName = StringConstants.MNG_NEW_ELEMENT;
			}
			String name = getAvailableWebElementName(parentFolder, defaultName);

			WebElementEntity newWebElement = new WebElementEntity();
			newWebElement.setName(name);
			newWebElement.setParentFolder(parentFolder);
			newWebElement.setElementGuidId(Util.generateGuid());
			newWebElement.setProject(parentFolder.getProject());
			EntityService.getInstance().saveEntity(newWebElement);
			
			FolderFileServiceManager.refreshFolder(parentFolder);
			return newWebElement;
		}
		return null;

	}

	public static String getAvailableWebElementName(FolderEntity parentFolder, String name) throws Exception {
		String newname = name;
		int i = 0;

		List<Object> entityList = null;

		if (parentFolder != null) {
			entityList = getAllChildren(parentFolder);
		}

		if (entityList != null && entityList.size() != 0) {
			Boolean flag = false;
			while (flag == false) {
				Boolean duplicate = false;
				for (Object iEntity : entityList) {
					if ((((WebElementEntity) iEntity).getName()).equalsIgnoreCase(newname)) {
						duplicate = true;
					}
				}
				if (duplicate == false)
					flag = true;
				else {
					i++;
					newname = name + " (" + Integer.toString(i) + ")";
				}
			}
		}
		return newname;
	}

	public static void deleteWebElement(WebElementEntity webElement) throws Exception {
		EntityFileServiceManager.delete(webElement);		
		FolderFileServiceManager.refreshFolder(webElement.getParentFolder());
	}

	// private static boolean checkWebElementIsReferencedByTestSteps(String
	// testCaseFolderLocation,
	// List<WebElementEntity> webElements, Map<WebElementEntity,
	// List<TestCaseEntity>> foundReferencedMap,
	// ProjectEntity project) throws Exception {
	// boolean isReferenced = false;
	// List<TestCaseEntity> testCases =
	// FolderFileServiceManager.getChildTestCasesOfFolder(testCaseFolderLocation,
	// project);
	// for (TestCaseEntity testCase : testCases) {
	// for (TestStepEntity testStep : testCase.getTestSteps()) {
	// for (WebElementEntity webElement : webElements) {
	// if (testStep.getWebElement() != null
	// &&
	// testStep.getWebElement().getLocation().equals(webElement.getLocation()))
	// {
	// foundReferencedMap.get(webElement).add(testCase);
	// isReferenced = true;
	// break;
	// }
	// }
	// }
	// }
	//
	// List<FolderEntity> folders =
	// FolderFileServiceManager.getChildFoldersOfFolder(testCaseFolderLocation);
	// for (FolderEntity folder : folders) {
	// isReferenced |=
	// checkWebElementIsReferencedByTestSteps(folder.getLocation(), webElements,
	// foundReferencedMap, project);
	// }
	// return isReferenced;
	// }
	//
	// private static void checkWebElementIsReferenced(List<WebElementEntity>
	// webElements, ProjectEntity project)
	// throws Exception {
	// Map<WebElementEntity, List<TestCaseEntity>> foundReferencedMap = new
	// HashMap<WebElementEntity, List<TestCaseEntity>>();
	// for (WebElementEntity webElement : webElements) {
	// foundReferencedMap.put(webElement, new ArrayList<TestCaseEntity>());
	// }
	// boolean isReferenced = checkWebElementIsReferencedByTestSteps(
	// FileServiceConstant.getTestCaseFolder(project.getFolderLocation()),
	// webElements, foundReferencedMap,
	// project);
	//
	// if (isReferenced) {
	// StringBuilder webElementMessage = new
	// StringBuilder("Cannot delete object: ");
	//
	// int webElementCount = 0;
	// for (WebElementEntity webElement : webElements) {
	// if (foundReferencedMap.get(webElement).size() > 0) {
	// webElementMessage.append(getRelativePath(webElement));
	// webElementMessage.append(" is referenced by the following test case");
	// webElementMessage.append((foundReferencedMap.get(webElement).size() > 1)
	// ? "s: " : ": ");
	// int testCaseCount = 0;
	// for (TestCaseEntity testCase : foundReferencedMap.get(webElement)) {
	// webElementMessage.append(TestCaseFileServiceManager.getRelativePath(testCase));
	// testCaseCount++;
	// if (testCaseCount < foundReferencedMap.get(webElement).size()) {
	// webElementMessage.append(", ");
	// }
	// }
	// webElementCount++;
	// if (webElementCount < webElements.size()) {
	// webElementMessage.append("; ");
	// } else {
	// webElementMessage.append(".");
	// }
	// }
	// }
	// throw new EntityIsReferencedException(webElementMessage.toString());
	// }
	// }

	public static List<Object> getAllChildren(FolderEntity parentFolder) throws Exception {
		if (parentFolder != null) {
			File folder = new File(parentFolder.getLocation());
			if (folder.exists() && folder.isDirectory()) {
				List<Object> result = new ArrayList<Object>();
				for (File file : folder.listFiles(EntityFileServiceManager.fileFilter)) {
					if (file.isFile() && file.getName().endsWith(WebElementEntity.getWebElementFileExtension())) {
						WebElementEntity webElement = getWebElement(file.getAbsolutePath());
						webElement.setParentFolder(parentFolder);
						result.add(webElement);
					}
				}
				return result;
			}
		}
		return new ArrayList<Object>();
	}

	public static List<Object> getAllChildren(String folderLocation, WebElementEntity parentWebElement,
			FolderEntity parentFolder) throws Exception {
		File folder = new File(folderLocation);
		if (folder.exists() && folder.isDirectory()) {
			List<Object> result = new ArrayList<Object>();
			for (File file : folder.listFiles(EntityFileServiceManager.fileFilter)) {
				if (file.isFile() && file.getName().endsWith(WebElementEntity.getWebElementFileExtension())) {
					WebElementEntity webElement = getWebElement(file.getAbsolutePath());
					webElement.setParentFolder(parentFolder);
					result.add(webElement);
				}
			}
			return result;
		}
		return new ArrayList<Object>();
	}

	private static List<IEntity> getWebElementByName(FolderEntity parentFolder, String name) throws Exception {
		List<IEntity> children = new ArrayList<IEntity>();
		List<Object> childWebElements = getAllChildren(parentFolder);

		for (Object object : childWebElements) {
			if (object instanceof WebElementEntity) {
				WebElementEntity webElement = ((WebElementEntity) object);
				if (webElement.getName().toLowerCase().contains(name.toLowerCase())) {
					children.add(webElement);
				}
			}
		}
		return children;
	}

	public static List<IEntity> getElement(FolderEntity rootFolder, String webElementName, int num) throws Exception {
		List<IEntity> childWebElements = getWebElementByName(rootFolder, webElementName);
		return (childWebElements.size() > num) ? childWebElements.subList(0, num) : childWebElements;
	}

	public static WebElementEntity saveWebElement(WebElementEntity webElement) throws Exception {
		validateToRename(webElement);
		FolderFileServiceManager.refreshFolder(webElement.getParentFolder());
		return webElement;
	}

	// private static void validateObjectRepositoryFolderPathLength(String
	// newPath, WebElementEntity parentWebElement,
	// String relativePath) throws Exception {
	// List<Object> webELements =
	// WebElementFileServiceManager.getAllChildren(parentWebElement);
	// for (Object object : webELements) {
	// WebElementEntity webElement = (WebElementEntity) object;
	// String newWebElementPath = newPath + File.separator +
	// webElement.getName()
	// + WebElementEntity.getWebElementFileExtension();
	// if (newWebElementPath.length() >
	// FileServiceConstant.MAX_FILE_PATH_LENGTH) {
	// throw new FilePathTooLongException(newWebElementPath.length(),
	// relativePath
	// + (relativePath.isEmpty() ? "" : File.separator) + webElement.getName(),
	// FileServiceConstant.MAX_FILE_PATH_LENGTH);
	// }
	//
	// String newFolderPath = newPath + File.separator + webElement.getName()
	// + WebElementEntity.getFolderExtension();
	// if (newFolderPath.length() > FileServiceConstant.MAX_FILE_PATH_LENGTH) {
	// throw new FilePathTooLongException(newFolderPath.length(), relativePath
	// + (relativePath.isEmpty() ? "" : File.separator) + webElement.getName(),
	// FileServiceConstant.MAX_FILE_PATH_LENGTH);
	// }
	// validateObjectRepositoryFolderPathLength(newFolderPath, webElement,
	// relativePath
	// + (relativePath.isEmpty() ? "" : File.separator) + webElement.getName());
	// }
	// }

	private static void validateToRename(WebElementEntity webElement) throws Exception {
		if (isNameChanged(webElement)) {
			try {
				EntityService.getInstance().validateName(webElement.getName());
			} catch (InvalidFileNameException ex) {
				String oldPk = EntityService.getInstance().getEntityCache().getKey(webElement);
				webElement.setName(FilenameUtils.getExtension(oldPk));
				throw ex;
			}

			File duplicatedWebElementFile = new File(webElement.getLocation());
			if (duplicatedWebElementFile.exists()) {
				throw new DuplicatedFileNameException(webElement.getName());
			}

			if (webElement.getLocation().length() > FileServiceConstant.MAX_FILE_PATH_LENGTH) {
				throw new FilePathTooLongException(webElement.getLocation().length(),
						FileServiceConstant.MAX_FILE_PATH_LENGTH);
			}
			String oldPk = EntityService.getInstance().getEntityCache().getKey(webElement);

			File webElementFile = new File(EntityService.getInstance().getEntityCache().getKey(webElement));

			EntityService.getInstance().getEntityCache().remove(webElement, false);
			if (webElementFile.exists() && webElementFile.isFile()
					&& webElementFile.getName().endsWith(WebElementEntity.getWebElementFileExtension())) {
				webElementFile.renameTo(new File(webElement.getLocation()));
				EntityService.getInstance().saveEntity(webElement);
				updateTestObjectReferences(webElement, oldPk);
			}
		} else {
			EntityService.getInstance().saveEntity(webElement);
		}

		
	}

	private static void updateTestObjectReferences(WebElementEntity webElement, String oldPk) throws Exception {	
		//update test object in script files (both test case's script and custom keyword's script)
		ProjectEntity project = webElement.getProject();
		String oldRelativeToLocation = oldPk.substring(project.getFolderLocation().length() + 1);
		String oldRelativeToId = FilenameUtils.removeExtension(oldRelativeToLocation).replace(File.separator, "/");
		String newRelativeToId = webElement.getRelativePathForUI().replace(File.separator, "/");
		GroovyRefreshUtil.updateStringScriptReferences(oldRelativeToId, newRelativeToId, project);
		
		//update ref_element
		for (WebElementEntity referenceEntity : getWebElementPropertyByRefELement(oldRelativeToId, project, true)) {
			for (WebElementPropertyEntity webElementProperty : referenceEntity.getWebElementProperties()) {
				if (webElementProperty.getName().equalsIgnoreCase(WebElementEntity.ref_element)
						&& oldRelativeToId.equals(webElementProperty.getValue())) {
					webElementProperty.setValue(newRelativeToId);
					
					EntityService.getInstance().saveEntity(referenceEntity);					
					GroovyRefreshUtil.refreshFile(referenceEntity.getRelativePath(), project);
					break;
				}
			}
		}		
	}
	
	public static void updateFolderTestObjectReferences(FolderEntity webElementFolder, String oldRelativeToLocation)
			throws Exception {
		//update test object in script files (both test case's script and custom keyword's script)
		ProjectEntity project = webElementFolder.getProject();	
		String oldRelativeToId = FilenameUtils.removeExtension(oldRelativeToLocation).replace(File.separator, "/") + "/";		
		String newRelativeToId = webElementFolder.getRelativePathForUI().replace(File.separator, "/") + "/";
		
		FolderFileServiceManager.refreshFolderScriptReferences(oldRelativeToLocation, webElementFolder);
		
		//update ref_element
		for (WebElementEntity referenceEntity : getWebElementPropertyByRefELement(oldRelativeToId, project, false)) {
			for (WebElementPropertyEntity webElementProperty : referenceEntity.getWebElementProperties()) {
				if (webElementProperty.getName().equalsIgnoreCase(WebElementEntity.ref_element)
						&& webElementProperty.getValue() != null && webElementProperty.getValue().startsWith(oldRelativeToId)) {
					String newValue = webElementProperty.getValue().replace(oldRelativeToId, newRelativeToId);
					webElementProperty.setValue(newValue);
					
					EntityService.getInstance().saveEntity(referenceEntity);					
					GroovyRefreshUtil.refreshFile(referenceEntity.getRelativePath(), project);
					break;
				}
			}
		}		
	}

	private static boolean isNameChanged(WebElementEntity webElement) throws Exception {
		String webElementLocation = EntityService.getInstance().getEntityCache().getKey(webElement);
		if (webElementLocation == null)
			return false;
		return !webElementLocation.toLowerCase().equals(webElement.getLocation().toLowerCase());
	}

	public static WebElementEntity copyWebElement(WebElementEntity webElement, FolderEntity destinationFolder)
			throws Exception {
		return EntityFileServiceManager.copy(webElement, destinationFolder);
	}

	public static FolderEntity copyWebElementFolder(FolderEntity folder, FolderEntity destinationFolder)
			throws Exception {
		return EntityFileServiceManager.copyFolder(folder, destinationFolder);

	}

	public static WebElementEntity moveWebElement(WebElementEntity webElement, FolderEntity destinationFolder)
			throws Exception {
		return EntityFileServiceManager.move(webElement, destinationFolder);
	}

	public static WebElementEntity getDuplicateWebElement(WebElementEntity parentWebElement, FolderEntity parentFolder,
			String name, ProjectEntity project) throws Exception {
		List<Object> webElements = null;
		if (parentFolder != null) {
			webElements = getAllChildren(parentFolder);
		}
		if (webElements != null) {
			for (Object object : webElements) {
				if (object instanceof WebElementEntity) {
					WebElementEntity webElement = (WebElementEntity) object;
					if (webElement.getName().equals(name)) {
						return webElement;
					}
				}
			}
		}
		return null;
	}

	public static WebElementEntity getByGUID(String guid, ProjectEntity project) throws Exception {
		File projectFolder = new File(project.getFolderLocation());
		if (projectFolder.exists() && projectFolder.isDirectory()) {
			File webElementFolder = new File(FileServiceConstant.getObjectRepositoryRootFolderLocation(projectFolder
					.getAbsolutePath()));
			if (webElementFolder.exists() && webElementFolder.isDirectory()) {
				return getByGUID(webElementFolder.getAbsolutePath(), guid);
			}
		}
		return null;
	}

	private static WebElementEntity getByGUID(String webElementFolder, String guid) throws Exception {
		File folder = new File(webElementFolder);
		if (folder.exists() && folder.isDirectory()) {
			for (File file : folder.listFiles(EntityFileServiceManager.fileFilter)) {
				if (file.isFile()
						&& file.getName().toLowerCase()
								.endsWith(WebElementEntity.getWebElementFileExtension().toLowerCase())) {
					WebElementEntity webElement = getWebElement(file.getAbsolutePath());
					if (webElement.getElementGuidId().equals(guid)) {
						return webElement;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Find all web elements in project that each one contains property with name =
	 * "refelement", value equals withs the given refElement.
	 * 
	 * @param refElement
	 *            : refElement value (Id of referred web element)
	 * @param project
	 * @param isExactly
	 *            : true if property value of qualified web element equals with
	 *            the given refElement, false if starts with the given
	 *            refElement.
	 */
	public static List<WebElementEntity> getWebElementPropertyByRefELement(String refElement, ProjectEntity project,
			boolean isExactly) throws Exception {

		List<WebElementEntity> returnList = new ArrayList<WebElementEntity>();

		File projectFolder = new File(project.getFolderLocation());
		if (projectFolder.exists() && projectFolder.isDirectory()) {
			File webElementFolder = new File(FileServiceConstant.getObjectRepositoryRootFolderLocation(projectFolder
					.getAbsolutePath()));
			if (webElementFolder.exists() && webElementFolder.isDirectory()) {
				getWebElementPropertyByRefELement(returnList, webElementFolder.getAbsolutePath(), refElement, isExactly);
			}
		}
		return returnList;
	}

	private static void getWebElementPropertyByRefELement(List<WebElementEntity> webElementList,
			String webElementFolder, String refElement, boolean isExactly) throws Exception {
		File folder = new File(webElementFolder);
		if (!folder.exists() || !folder.isDirectory())
			return;
		for (File file : folder.listFiles(EntityFileServiceManager.fileFilter)) {
			if (file.isDirectory()) {
				getWebElementPropertyByRefELement(webElementList, file.getAbsolutePath(), refElement, isExactly);
			} else if (file.isFile()
					&& file.getName().toLowerCase()
							.endsWith(WebElementEntity.getWebElementFileExtension().toLowerCase())) {
				WebElementEntity webElement = getWebElement(file.getAbsolutePath());
				for (WebElementPropertyEntity webElementProperty : webElement.getWebElementProperties()) {
					if (!webElementProperty.getName().equalsIgnoreCase(WebElementEntity.ref_element)
							|| webElementProperty.getValue() == null) continue;
					
					if ( (isExactly && webElementProperty.getValue().equalsIgnoreCase(refElement))
						|| (!isExactly && webElementProperty.getValue().startsWith(refElement)) ) {
						webElementList.add(webElement);
						break;
					}
				}
			}

		}
	}

	public static FolderEntity importWebElementFolder(FolderEntity folder, FolderEntity parentFolder) throws Exception {
		folder.setParentFolder(parentFolder);

		String newName = EntityService.getInstance().getAvailableName(parentFolder.getLocation(),
				folder.getName(), false);
		if (!newName.equals(folder.getName())) {
			folder.setName(newName);
		}
		
		folder.setProject(parentFolder.getProject());
		EntityService.getInstance().saveEntity(folder);

		return folder;
	}
	
	public static WebElementEntity importWebElement(WebElementEntity webElement, FolderEntity parentFolder)
			throws Exception {
		webElement.setParentFolder(parentFolder);
		String newName = EntityService.getInstance().getAvailableName(parentFolder.getLocation(), webElement.getName(),
				true);
		if (!newName.equals(webElement.getName())) {
			webElement.setName(newName);
		}

		EntityService.getInstance().saveEntity(webElement);
		return webElement;
	}

	public static WebElementPropertyEntity getRefElementProperty(WebElementEntity webElement) {
		for (WebElementPropertyEntity webElementProperty : webElement.getWebElementProperties()) {
			if (webElementProperty.getName().equalsIgnoreCase(WebElementEntity.ref_element)) {
				return webElementProperty;
			}
		}
		return null;
	}
	
	public static WebServiceRequestEntity addNewRequest(FolderEntity parentFolder, WebServiceRequestEntity request) throws Exception {
		if (parentFolder != null) {
			if(request == null){
				request = new WebServiceRequestEntity();
			}
			if(request.getName() == null || request.getName().equals("")){
				String name = getAvailableWebElementName(parentFolder, StringConstants.MNG_NEW_REQUEST);	
				request.setName(name);
			}
			request.setParentFolder(parentFolder);
			request.setProject(parentFolder.getProject());
			EntityService.getInstance().saveEntity(request);
			return request;
		}
		return null;
	}
}
