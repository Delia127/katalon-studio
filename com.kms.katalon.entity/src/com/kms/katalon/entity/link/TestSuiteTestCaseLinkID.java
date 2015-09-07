package com.kms.katalon.entity.link;

import org.apache.commons.lang.SerializationUtils;

import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestSuiteTestCaseLinkID implements java.io.Serializable {
	private static final long serialVersionUID = 3769599357122159182L;
	
	private TestSuiteEntity testSuiteEntity;
    private TestCaseEntity testCaseEntity;

    //@JsonBackReference("TestSuiteEntity-TestSuiteTestCaseLinks")
    public TestSuiteEntity getTestSuite() {
        return testSuiteEntity;
    }

    //@JsonBackReference("TestSuiteEntity-TestSuiteTestCaseLinks")
    public void setTestSuite(TestSuiteEntity testSuiteEntity) {
        this.testSuiteEntity= testSuiteEntity;
    }

	public TestCaseEntity getTestCase() {
		return testCaseEntity;
	}

	public void setTestCase(TestCaseEntity testCaseEntity) {
		this.testCaseEntity = testCaseEntity;
	}
	
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
 
        TestSuiteTestCaseLinkID that = (TestSuiteTestCaseLinkID) o;
 
        if (testSuiteEntity != null ? !testSuiteEntity.equals(that.testSuiteEntity) : that.testSuiteEntity != null) return false;
        if (testCaseEntity != null ? !testCaseEntity.equals(that.testCaseEntity) : that.testCaseEntity != null)
            return false;
 
        return true;
    }
 
    public int hashCode() {
        int result;
        result = (testSuiteEntity != null ? testSuiteEntity.hashCode() : 0);
        result = 31 * result + (testCaseEntity != null ? testCaseEntity.hashCode() : 0);
        return result;
    }
    
	public TestSuiteTestCaseLinkID clone() {
		return (TestSuiteTestCaseLinkID) SerializationUtils.clone(this);
	}
	
}
