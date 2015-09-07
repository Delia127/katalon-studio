package com.kms.katalon.entity.testcase;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.util.Util;
import com.kms.katalon.entity.variable.VariableEntity;

public class TestCaseEntity extends IntegratedFileEntity {

	private static final long serialVersionUID = 1L;
	
	private String comment = "";
	
	private String tag = "";
	
	private List<DataFileEntity> dataFiles;

	private List<String> dataFileLocations;
	
	private List<VariableEntity> variables;

	private String testCaseGuid;
	
	private byte[] scriptContents;
	
	public TestCaseEntity() {
	    variables = new ArrayList<>();
	    dataFiles = new ArrayList<>();
	    dataFileLocations = new ArrayList<>(); 
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTestCaseGuid() {
		return this.testCaseGuid;
	}

	public void setTestCaseGuid(String testCaseGuid) {
		this.testCaseGuid = testCaseGuid;
	}

	
	@Override
	public TestCaseEntity clone() {
		TestCaseEntity newTestCase = (TestCaseEntity)super.clone();
		newTestCase.setTestCaseGuid(Util.generateGuid());
		return newTestCase;
	}


	public List<String> getDataFileLocations() {
		return dataFileLocations;
	}

	public void setDataFileLocations(List<String> dataFileLocations) {
		this.dataFileLocations = dataFileLocations;
	}
	
	@Override
	public String getFileExtension() {
	    return getTestCaseFileExtension();
	}
	
	public static String getTestCaseFileExtension() {
		return ".tc";
	}
	
	public List<DataFileEntity> getDataFiles() {
	    return dataFiles;
	}
	
	public void setDataFiles(List<DataFileEntity> dataFiles) {
		this.dataFiles = dataFiles;
	}

	public byte[] getScriptContents() {
		return scriptContents;
	}

	public void setScriptContents(byte[] scriptContents) {
		this.scriptContents = scriptContents;
	}

    public List<VariableEntity> getVariables() {
        return variables;
    }

    public void setVariables(List<VariableEntity> variables) {
        this.variables = variables;
    }
}
