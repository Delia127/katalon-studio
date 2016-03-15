package com.kms.katalon.execution.entity;

import java.util.List;
import java.util.Map;

public interface IExecutedEntity {
    
    /**
     * @return Name of the executed entity, used to write log
     */
    public String getSourceName();

    /**
     * @return 
     */
    public String getSourceId();

    public String getSourceDescription();
    
    public int getTotalTestCases();
    
    public List<TestCaseExecutedEntity> getTestCaseExecutedEntities();
    
    public Map<String, Object> getAttributes();
    
    public int mainTestCaseDepth();
}
