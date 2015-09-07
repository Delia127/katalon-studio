package com.kms.katalon.dal;

import java.util.List;

import com.kms.katalon.entity.testcase.TestCaseEntity;


public interface IExportDataProvider {
	public boolean exportProject(String projectValue, String exportGUID, String exportFolder) throws Exception;
	public boolean exportProject(String projectValue, String exportGUID, List<TestCaseEntity> testCases, String exportFolder) throws Exception;
	public Integer getExportProgress(String exportGUID) throws Exception;
	public void cancelExport(String exportGUID) throws Exception;
}
