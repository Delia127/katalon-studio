package com.kms.katalon.entity.repository;

import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public class SaveWebElementInfoEntity {
	private WebElementEntity webElementEntity;
	private ProjectEntity projectEntity;
	private WebElementEntity webParentEntity;
	private FolderEntity folder;
	
	public FolderEntity getFolder() {
		return folder;
	}

	public void setFolder(FolderEntity folder) {
		this.folder = folder;
	}
	
	public WebElementEntity getParentWebElement(){
		return webParentEntity;
	}
	
	public void setParentWebElement(WebElementEntity webElement){
		webParentEntity = webElement;
	}
	
	public SaveWebElementInfoEntity()
	{
		webElementEntity = null;
		projectEntity = null;
	}
	
	public void setProjectEntity(ProjectEntity prjEntity)
	{
		projectEntity = prjEntity;
	}
	
	public ProjectEntity getProjectEntity()
	{
		return projectEntity;
	}
	
	public void setWebElement(WebElementEntity webElement)
	{
		webElementEntity = webElement;
	}
	
	public WebElementEntity getWebElement()
	{
		return webElementEntity;
	}
	
}
