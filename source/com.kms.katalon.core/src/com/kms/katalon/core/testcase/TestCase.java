package com.kms.katalon.core.testcase;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.kms.katalon.core.testdata.TestData;

public class TestCase {
    private String testCaseId;
    private String description;
    private TestData testData;
    private List<Variable> variables;
    private String tag;
    private boolean isTemp;
    
    /*package*/ TestCase(String testCaseId) {
        this.setTestCaseId(testCaseId);
    }
    
    public TestData getTestData() {
        return testData;
    }

    /*package*/ void setTestData(TestData testData) {
        this.testData = testData;
    }

    public String getGroovyScriptPath() throws IOException {
        if (!isTemp()) {
            return TestCaseFactory.getScriptPathByTestCaseId(testCaseId);
        } else {
            return TestCaseFactory.getScriptPathByTempTestCaseId(testCaseId);
        }
    }
    
    public String getGroovyScriptClassName() throws IOException {
        if (!isTemp()) {
            return TestCaseFactory.getScriptClassNameByTestCaseId(testCaseId);
        } else {
            return TestCaseFactory.getScriptClassNameByTempTestCaseId(testCaseId);
        }
       
    }
    
    public String getTestCaseId() {
        return testCaseId;
    }
    
    /*package*/ void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getName() {
    	return FilenameUtils.getName(testCaseId);
    }
    
    public String getMetaFilePath() {
        StringBuilder pathBuilder = new StringBuilder();
        if (!isTemp) {
            return pathBuilder
                    .append(TestCaseFactory.getProjectDirPath())
                    .append(File.separator)
                    .append(testCaseId)
                    .append(TestCaseFactory.TEST_CASE_META_FILE_EXTENSION)
                    .toString()
                    .replace("/", "\\");
        } else {
            return pathBuilder
                    .append(TestCaseFactory.getProjectDirPath())
                    .append(File.separator) 
                    .append(TestCaseFactory.TEMP_TEST_CASE_META_ROOT_FOLDER_NAME)
                    .append(File.separator)
                    .append(testCaseId)
                    .append(TestCaseFactory.TEST_CASE_META_FILE_EXTENSION)
                    .toString();
        }
    }

	public List<Variable> getVariables() {
		return variables;
	}

	/*package*/ void setVariables(List<Variable> variables) {
		this.variables = variables;
	}

	public String getDescription() {
		return description;
	}

	/*package*/ void setDescription(String description) {
		this.description = description;
	}
	
    public String getTag() {
        return tag;
    }

    /* package */ void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean isTemp) {
        this.isTemp = isTemp;
    }
}
