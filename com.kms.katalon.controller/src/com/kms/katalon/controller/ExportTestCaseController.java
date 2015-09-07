package com.kms.katalon.controller;

import java.util.List;

import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class ExportTestCaseController extends AbstractExportController implements IImportExportController {

	private static final String EXPORT_TEST_CASES_DISPLAY_TEXT = StringConstants.CTRL_TXT_EXPORT_TEST_CASE;
	private List<TestCaseEntity> testCases;
	
	public ExportTestCaseController(String guid, ProjectEntity project, String directory, List<TestCaseEntity> testCases) {
		super(guid, project, directory);
		this.testCases = testCases;
	}

	@Override
	public String getDisplayText() {
		return EXPORT_TEST_CASES_DISPLAY_TEXT;
	}
	@Override
	public boolean execute() throws Exception {
		return dataProviderSetting.getExportDataProvider().exportProject(
				dataProviderSetting.getEntityPk(project), guid, testCases, directory);
	}

}
