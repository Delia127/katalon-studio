package com.kms.katalon.execution.entity;

import java.util.List;
import java.util.Map;

public interface IExecutedEntity {

    String getId();

    /**
     * Get name of the executed source, used to write log
     * @return name of the executed source
     */
    public String getSourceName();

    /**
     * Get id of the executed source
     * @return id of the executed source
     */
    public String getSourceId();

    /**
     * Get description of the executed source
     * @return description of the executed source
     */
    public String getSourceDescription();
    
    /**
     * Get full path to the executed source
     * @return full path to the executed source
     */
    public String getSourcePath();

    public List<IExecutedEntity> getExecutedItems();

    int getTotalTestCases();
    
    public Map<String, Object> getAttributes();
    
    public Map<String, String> getCollectedDataInfo();

    public int mainTestCaseDepth();
}
