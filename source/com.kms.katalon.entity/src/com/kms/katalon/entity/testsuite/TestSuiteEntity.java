package com.kms.katalon.entity.testsuite;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.link.TestSuiteTestCaseLink;
import com.kms.katalon.entity.util.Util;

public class TestSuiteEntity extends IntegratedFileEntity {
	private static final long serialVersionUID = 1L;

	private String comment;

	private List<TestSuiteTestCaseLink> testSuiteTestCaseLinks = new ArrayList<TestSuiteTestCaseLink>();

	private String testSuiteGuid;

	private short pageLoadTimeout;

	private boolean isRerun;

	private int numberOfRerun;

	private Date lastRun;

	private String mailRecipient;

	private String browserString;

	private boolean isPageLoadTimeoutDefault;


	public TestSuiteEntity() {
		super();
		setPageLoadTimeoutDefault(true);
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public String getBrowserString() {
		return browserString;
	}

	public void setBrowserString(String browserString) {
		this.browserString = browserString;
	}

	public boolean isPageLoadTimeoutDefault() {
		return isPageLoadTimeoutDefault;
	}

	public void setPageLoadTimeoutDefault(boolean isPageLoadTimeoutDefault) {
		this.isPageLoadTimeoutDefault = isPageLoadTimeoutDefault;
	}
    
    @Override
    public boolean equals(Object that) {
        boolean equals = super.equals(that);
        if (equals) {
            TestSuiteEntity anotherTestSuite = (TestSuiteEntity) that;
            
            if (!getDateCreated().equals(anotherTestSuite.getDateCreated())) {
                return false;
            }
            
            if (!getDateModified().equals(anotherTestSuite.getDateModified())) {
                return false;
            }
           
            if (getComment() == null) {
                if (anotherTestSuite.getComment() != null) {
                    return false;
                }
            } else {
                if (anotherTestSuite.getComment() != null) {
                    if (!getComment().equals(anotherTestSuite.getComment())) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            
            if (isPageLoadTimeoutDefault() != anotherTestSuite.isPageLoadTimeoutDefault()) {
                return false;
            } else {
                if (!isPageLoadTimeoutDefault()) {
                    if (getPageLoadTimeout() == null) {
                        if (anotherTestSuite.getPageLoadTimeout() != null) {
                            return false;
                        }
                    } else {
                        if (anotherTestSuite.getPageLoadTimeout() != null) {
                            if (getPageLoadTimeout() != anotherTestSuite.getPageLoadTimeout()) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }
            }
            
            if (getMailRecipient() == null) {
                if (anotherTestSuite.getMailRecipient() != null) {
                    return false;
                }
            } else {
                if (anotherTestSuite.getMailRecipient() != null) {
                    if (!getMailRecipient().equals(anotherTestSuite.getMailRecipient())) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            
            if (!getTestSuiteTestCaseLinks().equals(getTestSuiteTestCaseLinks())) {
                return false;
            }
        }
        return equals;
    }
}
