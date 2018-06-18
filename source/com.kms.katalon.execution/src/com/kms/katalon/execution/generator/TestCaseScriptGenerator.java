package com.kms.katalon.execution.generator;

import groovy.lang.GroovyObject;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testcase.WSVerificationTestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;

public class TestCaseScriptGenerator {
	private static final String TEMPLATE_CLASS_NAME = IdConstants.KATALON_EXECUTION_BUNDLE_ID
			+ ".generator.TestCaseScriptTemplate";
	private static final String METHOD_GENERATE_TEST_CASE_SCRIPT_NAME = "generateTestCaseScriptFile";
	private static final String TEMP_TEST_CASE_FILE_NAME = "TempTestCase";

	private TestCaseEntity testCase;
	private IRunConfiguration runConfig;

	public TestCaseScriptGenerator(TestCaseEntity testCase, IRunConfiguration runConfig) {
		this.testCase = testCase;
		this.runConfig = runConfig;
	}

	@SuppressWarnings("rawtypes")
	public File generateScriptFile() throws Exception {
		IFolder folder = GroovyUtil.getCustomKeywordLibFolder(testCase.getProject());
		File file = new File(folder.getRawLocation().toFile().getAbsoluteFile().getAbsolutePath(),
				TEMP_TEST_CASE_FILE_NAME + System.currentTimeMillis() + GroovyConstants.GROOVY_FILE_EXTENSION);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();

		Class clazz = Class.forName(TEMPLATE_CLASS_NAME);
		GroovyObject object = (GroovyObject) clazz.newInstance();
		object.invokeMethod(METHOD_GENERATE_TEST_CASE_SCRIPT_NAME, new Object[] { file, testCase,
				createTestCaseBinding(), runConfig });
		folder.refreshLocal(IResource.DEPTH_ONE, null);
		return file;
	}

	public String createTestCaseBinding() throws Exception {
		StringBuilder bindingBuilder = new StringBuilder();
		StringBuilder syntaxErrorCollector = new StringBuilder();
		String testCaseId = testCase.getRelativePathForUI().replace(File.separator, "/");
		bindingBuilder.append("new TestCaseBinding('" + testCaseId + "',");
		
		StringBuilder variableBinding = new StringBuilder();
		if (testCase instanceof WSVerificationTestCaseEntity) {
		    variableBinding.append("[");
		    List<VariableEntity> variables = testCase.getVariables();
		    if (variables.size() > 0) {
                for (int i = 0; i< variables.size(); i++) {
    		        if (i >= 1) {
    		            variableBinding.append(", ");
    		        }
    		        VariableEntity variable = variables.get(i);
    		        variableBinding.append(String.format("'%s': %s", variable.getName(), variable.getDefaultValue()));
    		    }
		    } else {
		        variableBinding.append(":");
		    }

            variableBinding.append("]");
		    
		} else {
		    variableBinding.append("[:]");
		}
		bindingBuilder.append(variableBinding).append(")");

		if (syntaxErrorCollector.toString().isEmpty()) {
			return bindingBuilder.toString();
		} else {
			throw new RuntimeException(syntaxErrorCollector.toString());
		}
	}
}
