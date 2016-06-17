package com.kms.katalon.dal.fileservice.adapter;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.fileservice.manager.TestSuiteFileServiceManager;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TestSuiteReferenceXmlAdapter extends EntityReferenceXmlAdapter<String, TestSuiteEntity> {

    @Override
    public String marshal(TestSuiteEntity testSuite) {
        return testSuite != null ? testSuite.getIdForDisplay() : StringUtils.EMPTY;
    }

    @Override
    protected TestSuiteEntity safelyUnmarshal(String testSuiteId) throws DALException {
        try {
            return TestSuiteFileServiceManager.getTestSuiteByDisplayId(testSuiteId);
        } catch (Exception e) {
            throw new DALException(e);
        }
    }
}
