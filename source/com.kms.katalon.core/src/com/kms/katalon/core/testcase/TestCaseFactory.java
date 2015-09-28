package com.kms.katalon.core.testcase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.exception.ExceptionsUtil;

public class TestCaseFactory {
	private static final String TEST_CASE_META_ROOT_FOLDER_NAME = "Test Cases";
	private static final String TEST_CASE_SCRIPT_ROOT_FOLDER_NAME = "Scripts";
	private static final String TEST_CASE_SCRIPT_FILE_EXTENSION = "groovy";
	/* package */static final String TEST_CASE_META_FILE_EXTENSION = ".tc";
	private static final String DESCRIPTION_NODE_NAME = "description";
	private static final String VARIABLE_NODE_NAME = "variable";
	private static final String VARIABLE_NAME_PROPERTY = "name";
	private static final String VARIABLE_DEFAULTVALUE_PROPERTY = "defaultValue";

	public static TestCase findTestCase(String testCaseId) throws IllegalArgumentException {
		if (testCaseId == null) {
			throw new IllegalArgumentException("Test case id is null");
		}
		File testCaseMetaFile = new File(getProjectDirPath() + File.separator + testCaseId
				+ TEST_CASE_META_FILE_EXTENSION);
		if (testCaseMetaFile.exists()) {
			try {
				SAXReader reader = new SAXReader();
				Document document = reader.read(testCaseMetaFile);
				Element rootElement = document.getRootElement();
				TestCase testCase = new TestCase(testCaseId);
				testCase.setDescription(rootElement.element(DESCRIPTION_NODE_NAME).getText());
				List<Variable> variables = new ArrayList<Variable>();
				for (Object variableObject : rootElement.elements(VARIABLE_NODE_NAME)) {
					Element variableElement = (Element) variableObject;
					Variable variable = new Variable();
					variable.setName(variableElement.elementText(VARIABLE_NAME_PROPERTY));
					variable.setDefaultValue(variableElement.elementText(VARIABLE_DEFAULTVALUE_PROPERTY));
					variables.add(variable);
				}
				testCase.setVariables(variables);
				return testCase;
			} catch (Exception e) {
				throw new IllegalArgumentException("Cannot find test case with id '" + testCaseId + "' because (of) "
						+ ExceptionsUtil.getMessageForThrowable(e));
			}
		} else {
			throw new IllegalArgumentException("Test case with id '" + testCaseId + "' does not exist");
		}
	}

	/* package */static String getTestCaseIdByScriptPath(String scriptPath) throws IOException {

		File testCaseScriptFolder = new File(scriptPath).getParentFile();

		return testCaseScriptFolder.getAbsolutePath().substring(getProjectDirPath().length())
				.replaceFirst(TEST_CASE_SCRIPT_ROOT_FOLDER_NAME, TEST_CASE_META_ROOT_FOLDER_NAME);
	}

	/* package */static String getProjectDirPath() {
		File currentDirFile = new File(RunConfiguration.getProjectDir());
		String currentDirFilePath = currentDirFile.getAbsolutePath();
		return currentDirFilePath;
	}

	/* package */static String getScriptPathByTestCaseId(String testCaseId) throws IOException {
		String testCaseScriptRelativePath = testCaseId.replace("/", File.separator).replaceFirst(
				TEST_CASE_META_ROOT_FOLDER_NAME, TEST_CASE_SCRIPT_ROOT_FOLDER_NAME);
		String testCaseScriptFolderPath = getProjectDirPath() + File.separator + testCaseScriptRelativePath;
		for (File file : new File(testCaseScriptFolderPath).listFiles()) {
			if (FilenameUtils.getExtension(file.getName()).equals(TEST_CASE_SCRIPT_FILE_EXTENSION)) {
				return file.getAbsolutePath();
			}
		}
		return StringUtils.EMPTY;
	}

	/* package */static String getScriptClassNameByTestCaseId(String testCaseId) throws IOException {
		return FilenameUtils.getBaseName(getScriptPathByTestCaseId(testCaseId));
	}
}
