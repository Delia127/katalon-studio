package com.kms.katalon.dal.fileservice.dataprovider;

import java.util.List;

import com.kms.katalon.dal.IGlobalVariableDataProvider;
import com.kms.katalon.dal.fileservice.manager.GlobalVariableFileServiceManager;
import com.kms.katalon.entity.global.GlobalVariableEntity;

public class GlobalVariableFileServiceDataProvider implements IGlobalVariableDataProvider {

	@Override
	public List<GlobalVariableEntity> getAll(String projectPk) throws Exception {
		return GlobalVariableFileServiceManager.getAll(projectPk);
	}

	@Override
	public GlobalVariableEntity getVariable(String name, String projectPk) throws Exception {
		for (GlobalVariableEntity variable: GlobalVariableFileServiceManager.getAll(projectPk)) {
			if (variable.getName().equals(name)) {
				return variable;
			}
		}
		return null;
	}

	@Override
	public GlobalVariableEntity addNewVariable(String newName, String value, String projectPk) throws Exception {
		return GlobalVariableFileServiceManager.addNewVariable(newName, value, projectPk);
	}

	@Override
	public List<GlobalVariableEntity> updateVariables(List<GlobalVariableEntity> variables, String projectPk)
			throws Exception {
		return GlobalVariableFileServiceManager.updateVariables(variables, projectPk);
	}
}
