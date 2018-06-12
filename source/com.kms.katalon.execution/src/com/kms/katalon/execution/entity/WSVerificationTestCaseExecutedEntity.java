package com.kms.katalon.execution.entity;

import com.kms.katalon.entity.testcase.WSVerificationTestCaseEntity;


public class WSVerificationTestCaseExecutedEntity extends TestCaseExecutedEntity {

    private WSVerificationTestCaseEntity testCase;
    
    public WSVerificationTestCaseExecutedEntity(WSVerificationTestCaseEntity testCase) {
        super(testCase);
        this.testCase = testCase;
    }

    @Override
    public String getSourceId() {
        return testCase.getId();
    }
}
