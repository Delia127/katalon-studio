package com.kms.katalon.execution.entity;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;

public class TestSuiteCollectionExecutedEntity extends ExecutedEntity {

    private List<IExecutedEntity> executedItems;
    private TestSuiteCollectionEntity testSuiteCollectionEntity;

    public TestSuiteCollectionExecutedEntity(TestSuiteCollectionEntity entity) {
        super(entity);
        executedItems = new ArrayList<>();
        testSuiteCollectionEntity = entity;
    }

    public void addTestSuiteExecutedEntity(TestSuiteExecutedEntity testSuiteExecuted) {
        executedItems.add(testSuiteExecuted);
    }

    @Override
    public List<IExecutedEntity> getExecutedItems() {
        return executedItems;
    }

    @Override
    public int mainTestCaseDepth() {
        return 2;
    }
    
    public TestSuiteCollectionEntity getEntity() {
        return testSuiteCollectionEntity;
    }

    @Override
    public int getTotalTestCases() {
        int totalTestCases = 0;
        for (IExecutedEntity childItem : getExecutedItems()) {
            totalTestCases += childItem.getTotalTestCases();
        }
        return totalTestCases;
    }
}
