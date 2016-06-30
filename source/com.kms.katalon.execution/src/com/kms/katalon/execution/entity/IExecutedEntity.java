package com.kms.katalon.execution.entity;

import java.util.List;
import java.util.Map;

public interface IExecutedEntity {

    String getId();

    /**
     * @return Name of the executed entity, used to write log
     */
    public String getSourceName();

    /**
     * @return
     */
    public String getSourceId();

    public String getSourceDescription();

    public List<IExecutedEntity> getExecutedItems();

    int getTotalTestCases();
    
    public Map<String, Object> getAttributes();
    
    public Map<String, String> getCollectedDataInfo();

    public int mainTestCaseDepth();
}
