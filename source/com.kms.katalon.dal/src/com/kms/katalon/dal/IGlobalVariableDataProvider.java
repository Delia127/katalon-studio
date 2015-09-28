package com.kms.katalon.dal;

import java.util.List;

import com.kms.katalon.entity.global.GlobalVariableEntity;

public interface IGlobalVariableDataProvider {
	public List<GlobalVariableEntity> getAll(String projectPk) throws Exception;
	public GlobalVariableEntity getVariable(String name, String projectPk) throws Exception;
	public GlobalVariableEntity addNewVariable(String newName, String value, String projectPk) throws Exception;
	public List<GlobalVariableEntity> updateVariables(List<GlobalVariableEntity> variables, String projectPk) throws Exception;
}
