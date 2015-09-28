package com.kms.katalon.dal.fileservice.manager;

import java.io.File;
import java.util.Collections;
import java.util.List;

import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.FileServiceConstant;
import com.kms.katalon.dal.fileservice.entity.GlobalVariableWrapper;
import com.kms.katalon.entity.global.GlobalVariableEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public class GlobalVariableFileServiceManager {

	public static List<GlobalVariableEntity> getAll(String projectPk) throws Exception {
		if (projectPk != null) {
			GlobalVariableWrapper wrapper = getWrapper(projectPk);
			if (wrapper != null) {
				return wrapper.getGlobalVariableEntities();
			}
		}
		return Collections.emptyList();
	}

	public static GlobalVariableEntity addNewVariable(String newName, String value, String projectPk) throws Exception {
		GlobalVariableWrapper wrapper = getWrapper(projectPk);
		for (GlobalVariableEntity variable : wrapper.getGlobalVariableEntities()) {
			if (variable.getName().equals(newName)) {
				return null;
			}
		}
		
		GlobalVariableEntity newVariable = new GlobalVariableEntity(newName, value);
		wrapper.getGlobalVariableEntities().add(newVariable);
		
		saveWrapper(wrapper, projectPk);
		return newVariable;
	}

	private static GlobalVariableWrapper getWrapper(String projectPk) throws Exception {
		if (projectPk != null) {
			ProjectEntity project = ProjectFileServiceManager.getProject(projectPk);
			File globalVariableFile = new File(FileServiceConstant.getGlobalVariableFileLocation(project
					.getFolderLocation()));
			GlobalVariableWrapper wrapper = null;
			if (globalVariableFile.exists()) {
				wrapper = (GlobalVariableWrapper) EntityService.getInstance().getEntityByPath(
						globalVariableFile.getAbsolutePath());

			} else {
				wrapper = new GlobalVariableWrapper();
				saveWrapper(wrapper, projectPk);
			}
			return wrapper;
		}
		return null;
	}

	public static List<GlobalVariableEntity> updateVariables(List<GlobalVariableEntity> listVariable, String projectPk) throws Exception {
		GlobalVariableWrapper wrapper = getWrapper(projectPk);
		wrapper.setGlobalVariableEntities(listVariable);

		saveWrapper(wrapper, projectPk);
		return wrapper.getGlobalVariableEntities();
	}
	
	private static void saveWrapper(GlobalVariableWrapper wrapper, String projectPk) throws Exception {
		ProjectEntity project = ProjectFileServiceManager.getProject(projectPk);
		EntityService.getInstance().saveEntity(wrapper,
				FileServiceConstant.getGlobalVariableFileLocation(project.getFolderLocation()));
	}

}
