package com.kms.katalon.entity.testsuite;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.util.Util;

public class TestSuiteEntity extends IntegratedFileEntity {
	private static final long serialVersionUID = 1L;

	private List<TestSuiteTestCaseLink> testSuiteTestCaseLinks = new ArrayList<TestSuiteTestCaseLink>();

	private String testSuiteGuid;

	private short pageLoadTimeout;

	private boolean isRerun;

	private int numberOfRerun;

	private Date lastRun;

	private String mailRecipient;

	private boolean isPageLoadTimeoutDefault;
	
	private boolean rerunFailedTestCasesOnly;


	public TestSuiteEntity() {
		super();
		setPageLoadTimeoutDefault(true);
	}

	public String getTestSuiteGuid() {
		return this.testSuiteGuid;
	}

	public void setTestSuiteGuid(String testSuiteGuid) {
		this.testSuiteGuid = testSuiteGuid;
	}

	public List<TestSuiteTestCaseLink> getTestSuiteTestCaseLinks() {
		return testSuiteTestCaseLinks;
	}

	public void setTestSuiteTestCaseLinks(List<TestSuiteTestCaseLink> testSuiteTestCaseLinks) {
		this.testSuiteTestCaseLinks = testSuiteTestCaseLinks;
	}

	public boolean getIsRerun() {
		return this.isRerun;
	}

	public void setIsRerun(boolean isRerun) {
		this.isRerun = isRerun;
	}

	public int getNumberOfRerun() {
		return this.numberOfRerun;
	}

	public void setNumberOfRerun(int numberOfRerun) {
		this.numberOfRerun = numberOfRerun;
	}

	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}

	public Date getLastRun() {
		return lastRun;
	}

	public TestSuiteEntity clone() {
		TestSuiteEntity newTestSuite = (TestSuiteEntity) super.clone();
		newTestSuite.setTestSuiteGuid(Util.generateGuid());
		newTestSuite.setLastRun(null);
		return newTestSuite;
	}

	public Short getPageLoadTimeout() {
		return this.pageLoadTimeout;
	}

	public void setPageLoadTimeout(Short pageLoadTimeout) {
		this.pageLoadTimeout = pageLoadTimeout;
	}

	public static String getTestSuiteFileExtension() {
		return ".ts";
	}

	@Override
	public String getFileExtension() {
		return getTestSuiteFileExtension();
	}

	public String getRelativePathForUI() {
		return getParentFolder().getRelativePath() + File.separator + getName();
	}

	public String getMailRecipient() {
		return mailRecipient;
	}

	public void setMailRecipient(String mailRecipient) {
		this.mailRecipient = mailRecipient;
	}

	public boolean isPageLoadTimeoutDefault() {
		return isPageLoadTimeoutDefault;
	}

	public void setPageLoadTimeoutDefault(boolean isPageLoadTimeoutDefault) {
		this.isPageLoadTimeoutDefault = isPageLoadTimeoutDefault;
	}

    public boolean isRerunFailedTestCasesOnly() {
        return rerunFailedTestCasesOnly;
    }

    public void setRerunFailedTestCasesOnly(boolean rerunFailedTestCasesOnly) {
        this.rerunFailedTestCasesOnly = rerunFailedTestCasesOnly;
    }
    
    @Override
    public boolean equals(Object that) {
        boolean isEquals = super.equals(that);
        if (!(that instanceof TestSuiteEntity)) {
            return false;
        }
        TestSuiteEntity ts = (TestSuiteEntity) that;
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(this.getDateCreated(), ts.getDateCreated())
                .append(this.getDateModified(), ts.getDateModified())
                .append(this.isPageLoadTimeoutDefault(), ts.isPageLoadTimeoutDefault())
                .append(this.getMailRecipient(), ts.getMailRecipient())
                .append(this.getTestSuiteTestCaseLinks(), ts.getTestSuiteTestCaseLinks());
        if (!this.isPageLoadTimeoutDefault() && equalsBuilder.isEquals()) {
            // page load timeout is not default
            equalsBuilder.append(this.getPageLoadTimeout(), ts.getPageLoadTimeout());
        }
        return isEquals && equalsBuilder.isEquals();
    }
}
