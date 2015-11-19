package com.kms.katalon.entity.link;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

import com.kms.katalon.entity.util.Util;

public class TestSuiteTestCaseLink implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean isRun;
    private boolean isReuseDriver;
    private List<VariableLink> variableLinks;
    private String testCaseId;
    private String guid;

    private List<TestCaseTestDataLink> testDataLinks;

    public TestSuiteTestCaseLink() {
        isRun = true;
        isReuseDriver = false;
    }

    public boolean getIsRun() {
        return this.isRun;
    }

    public void setIsRun(boolean isRun) {
        this.isRun = isRun;
    }

    /**
     * @return the isReuseDriver
     */
    public boolean getIsReuseDriver() {
        return isReuseDriver;
    }

    /**
     * @param isReuseDriver
     *            the isReuseDriver to set
     */
    public void setIsReuseDriver(boolean isReuseDriver) {
        this.isReuseDriver = isReuseDriver;
    }

    public TestSuiteTestCaseLink clone() {
        return (TestSuiteTestCaseLink) SerializationUtils.clone(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isReuseDriver ? 1231 : 1237);
        result = prime * result + (isRun ? 1231 : 1237);
        result = prime * result + ((guid == null) ? 0 : guid.hashCode());
        result = prime * result + ((testCaseId == null) ? 0 : testCaseId.hashCode());
        result = prime * result + ((testDataLinks == null) ? 0 : testDataLinks.hashCode());
        result = prime * result + ((variableLinks == null) ? 0 : variableLinks.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TestSuiteTestCaseLink)) {
            return false;
        }
        TestSuiteTestCaseLink that = (TestSuiteTestCaseLink) obj;
        return new EqualsBuilder().append(this.getTestCaseId(), that.getTestCaseId())
                .append(this.getGuid(), that.getGuid()).isEquals();
    }

    public List<VariableLink> getVariableLinks() {
        if (variableLinks == null) {
            variableLinks = new ArrayList<VariableLink>();
        }
        return variableLinks;
    }

    public void setVariableLinks(List<VariableLink> variableLinks) {
        this.variableLinks = variableLinks;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public List<TestCaseTestDataLink> getTestDataLinks() {
        if (testDataLinks == null) {
            testDataLinks = new ArrayList<TestCaseTestDataLink>();
        }
        return testDataLinks;
    }

    public void setTestDataLinks(List<TestCaseTestDataLink> testDataLinks) {
        this.testDataLinks = testDataLinks;
    }

    public String getGuid() {
        if (guid == null) {
            guid = Util.generateGuid();
        }
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

}
