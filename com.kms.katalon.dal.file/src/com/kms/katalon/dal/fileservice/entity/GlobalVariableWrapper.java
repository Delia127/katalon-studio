package com.kms.katalon.dal.fileservice.entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.global.GlobalVariableEntity;

@XmlRootElement(name = "GlobalVariableEntities")
public class GlobalVariableWrapper extends FileEntity{

	private static final long serialVersionUID = 1L;
	
	private List<GlobalVariableEntity> globalVariableEntities; 
	
	@Override
	public String getFileExtension() {
		return getGlobalVariableFileExtension();
	}

	@XmlElement(name = "GlobalVariableEntity")
	public List<GlobalVariableEntity> getGlobalVariableEntities() {
		if (globalVariableEntities == null) {
			globalVariableEntities = new ArrayList<GlobalVariableEntity>();
		}
		return globalVariableEntities;
	}

	public void setGlobalVariableEntities(List<GlobalVariableEntity> globalVariableEntities) {
		this.globalVariableEntities = globalVariableEntities;
	}
	
	public static String getGlobalVariableFileExtension() {
		return ".glbl"; 
	}
}
