package com.kms.katalon.dal;

import java.util.List;

import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.SaveWebElementInfoEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

/**
 * @author duyluong
 * 
 */
public interface IWebElementDataProvider {
	public void importWebElement(List<SaveWebElementInfoEntity> entities) throws Exception;
	
	public FolderEntity importWebElementFolder(FolderEntity folder, FolderEntity parentFolder) throws Exception;

	public WebElementEntity importWebElement(WebElementEntity webElement, FolderEntity parentFolder) throws Exception;
	
	public WebElementEntity getWebElement(String webElementValue) throws Exception;

	public WebElementEntity addNewWebElement(FolderEntity parentFolder, String elementName) throws Exception;

	public String getAvailableWebElementName(FolderEntity parentFolder, String name) throws Exception;

	public void deleteWebElement(WebElementEntity webElement) throws Exception;
	
	public void updateWebElement(WebElementEntity webElement) throws Exception;
	
	public List<WebElementEntity> getChildWebElementsOfFolder(FolderEntity folder) throws Exception;

	public WebElementEntity copyWebElement(WebElementEntity webElement, FolderEntity destinationFolder)
			throws Exception;

	public WebElementEntity moveWebElement(WebElementEntity webElement, FolderEntity destinationFolder)
			throws Exception;

	public String getIdForDisplay(WebElementEntity entity) throws Exception;
	
	public WebServiceRequestEntity addNewRequest(FolderEntity parentFolder, WebServiceRequestEntity request) throws Exception;
}
