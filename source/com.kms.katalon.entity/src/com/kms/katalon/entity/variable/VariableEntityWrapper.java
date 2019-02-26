package com.kms.katalon.entity.variable;


import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.file.FileEntity;

public class VariableEntityWrapper extends FileEntity {
	private static final long serialVersionUID = -5986525333483337037L;
	List<VariableEntity> variables;
	
	public VariableEntityWrapper(){
		super();
		variables = new ArrayList<VariableEntity>();
	}

	public void setVariables(List<VariableEntity> incomingVriables){
		variables = incomingVriables;
	}
	
	public List<VariableEntity> getVariables(){
		return variables;
	}

	@Override
	public String getFileExtension() {
		return ".var";
	}
}
