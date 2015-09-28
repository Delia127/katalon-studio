package com.kms.katalon.entity.webservice;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testdata.InternalDataColumnEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class WsEntities {

	private List<TestSuiteEntity> testSuiteEntities;
	private List<DataFileEntity> dataFileEntities;
	private List<TestCaseEntity> testCaseEntities;
	private List<FolderEntity> folderEntities;
	private List<InternalDataColumnEntity> internalDataColEntities;
	private List<WebElementEntity> webElementEntity;
	private List<Object> internalDataValues;
	
	public WsEntities() {
		testSuiteEntities = new ArrayList<TestSuiteEntity>();
		testCaseEntities = new ArrayList<TestCaseEntity>();
		folderEntities = new ArrayList<FolderEntity>();
		dataFileEntities = new ArrayList<DataFileEntity>();
		internalDataColEntities = new ArrayList<InternalDataColumnEntity>();
		webElementEntity = new ArrayList<WebElementEntity>();
		internalDataValues = new ArrayList<Object>();
	}
	
	public List<WebElementEntity> getWebElementEntity()
	{
		return this.webElementEntity;
	}
	
	public void setWebElementEntity(List<WebElementEntity> input)
	{
		this.webElementEntity = input;
	}
	
	public List<InternalDataColumnEntity> getInternalDataColumnEntities()
	{
		return this.internalDataColEntities;
	}
	
	public void setInternalDataColumnEntities(List<InternalDataColumnEntity> input)
	{
		this.internalDataColEntities = input;
	}
	
	public List<DataFileEntity> getDataFileEntities() {
		return dataFileEntities;
	}
	
	public void setDataFileEntities(List<DataFileEntity> dataFileEntities) {
		this.dataFileEntities = dataFileEntities;
	}
	
	public List<TestSuiteEntity> getTestSuiteEntities() {
		return testSuiteEntities;
	}
	
	public void setTestSuiteEntities(List<TestSuiteEntity> testSuiteEntities) {
		this.testSuiteEntities = testSuiteEntities;
	}
	
	public List<FolderEntity> getFolderEntities() {
		return folderEntities;
	}
	
	public void setFolderEntities(List<FolderEntity> folderEntities) {
		this.folderEntities = folderEntities;
	}
	
	public List<TestCaseEntity> getTestCaseEntities() {
		return testCaseEntities;
	}
	
	public void setTestCaseEntities(List<TestCaseEntity> testCaseEntities) {
		this.testCaseEntities = testCaseEntities;
	}
	
	public List<Object> getInternalDataValues() {
		return internalDataValues;
	}

	public void setInternalDataValues(List<Object> internalDataValues) {
		this.internalDataValues = internalDataValues;
	}
}
