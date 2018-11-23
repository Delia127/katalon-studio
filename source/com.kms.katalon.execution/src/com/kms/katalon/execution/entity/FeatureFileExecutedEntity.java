package com.kms.katalon.execution.entity;

import java.util.Arrays;
import java.util.List;

import com.kms.katalon.entity.file.SystemFileEntity;

public class FeatureFileExecutedEntity extends ExecutedEntity {
    
    public FeatureFileExecutedEntity(SystemFileEntity systemFileEntity) {
        super(systemFileEntity);
    }

    @Override
    public List<IExecutedEntity> getExecutedItems() {
        return Arrays.asList(this);
    }

    @Override
    public int getTotalTestCases() {
        return 1;
    }

    @Override
    public int mainTestCaseDepth() {
        return 0;
    }

}
