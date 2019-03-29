package com.kms.katalon.dal.fileservice.dataprovider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kms.katalon.dal.IWebElementDataProvider;
import com.kms.katalon.dal.fileservice.manager.FolderFileServiceManager;
import com.kms.katalon.dal.fileservice.manager.WebElementFileServiceManager;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.SaveWebElementInfoEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class WebElementFileServiceDataProvider implements IWebElementDataProvider {

	@Override
	public void importWebElement(List<SaveWebElementInfoEntity> entities) throws Exception {
		List<WebElementEntity> webElements = new ArrayList<WebElementEntity>();
		// Old folder - new folder
		Map<FolderEntity, FolderEntity> foldersMap = new HashMap<>();
		for (SaveWebElementInfoEntity webElementInfoEntity : entities) {
			if (webElementInfoEntity.getFolder() != null) {
				// Save Folder
				FolderEntity folderEntity = FolderFileServiceManager.addNewFolder(webElementInfoEntity.getFolder()
						.getParentFolder(), webElementInfoEntity.getFolder().getName());
				if (folderEntity != null) {
					foldersMap.put(webElementInfoEntity.getFolder(), folderEntity);
					// for (WebElementEntity childWebElement :
					// webElement.getChildWebElements()) {
					// childWebElement.setParentFolder(folderEntity);
					// }
				}
			} else if (webElementInfoEntity.getWebElement() != null) {
				FolderEntity newParentFolder = foldersMap.get(webElementInfoEntity.getWebElement().getParentFolder());
				if (newParentFolder != null) {
					webElementInfoEntity.getWebElement().setParentFolder(newParentFolder);
				}
				webElements.add(webElementInfoEntity.getWebElement());
			}
		}
		for (WebElementEntity webElementEntity : webElements) {
			WebElementFileServiceManager.updateTestObject(webElementEntity);
		}
	}

	@Override
	public FolderEntity importWebElementFolder(FolderEntity folder, FolderEntity parentFolder) throws Exception {
		return WebElementFileServiceManager.importWebElementFolder(folder, parentFolder);
	}
	
	@Override
	public WebElementEntity importWebElement(WebElementEntity webElement, FolderEntity parentFolder) throws Exception {
		return WebElementFileServiceManager.importWebElement(webElement, parentFolder);
	}

	@Override
	public WebElementEntity getWebElement(String webElementValue) throws Exception {
		return WebElementFileServiceManager.getWebElement(webElementValue);
	}

	@Override
	public String getAvailableWebElementName(FolderEntity parentFolder, String name) throws Exception {
		return WebElementFileServiceManager.getAvailableWebElementName(parentFolder, name);
	}

	@Override
	public void deleteWebElement(WebElementEntity webElement) throws Exception {
		WebElementFileServiceManager.deleteWebElement(webElement);
	}

	@Override
	public WebElementEntity updateTestObject(WebElementEntity entity) throws Exception {
		return WebElementFileServiceManager.updateTestObject(entity);
	}

	@Override
	public WebElementEntity copyWebElement(WebElementEntity webElement, FolderEntity destinationFolder)
			throws Exception {
		return WebElementFileServiceManager.copyWebElement(webElement, destinationFolder);
	}

	@Override
	public WebElementEntity moveWebElement(WebElementEntity webElement, FolderEntity destinationFolder)
			throws Exception {
		return WebElementFileServiceManager.moveWebElement(webElement, destinationFolder);
	}

	@Override
	public List<WebElementEntity> getChildWebElementsOfFolder(FolderEntity folder) throws Exception {
		return FolderFileServiceManager.getChildWebElementsOfFolder(folder);
	}

    @Override
    public List<WebElementEntity> getWebElementPropertyByRefElement(String refElement, ProjectEntity project,
            boolean isExactly) throws Exception {
        return WebElementFileServiceManager.getWebElementPropertyByRefElement(refElement, project, isExactly);
    }

    @Override
    public WebElementPropertyEntity getRefElementProperty(WebElementEntity webElement) {
        return WebElementFileServiceManager.getRefElementProperty(webElement);
    }

    @Override
    public WebElementEntity saveNewTestObject(WebElementEntity newTestObject) throws Exception {
        return WebElementFileServiceManager.saveNewTestObject(newTestObject);
    }
}
