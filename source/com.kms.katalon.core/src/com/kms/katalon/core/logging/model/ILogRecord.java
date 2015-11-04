package com.kms.katalon.core.logging.model;



public interface ILogRecord {
	public String getName();

	public void setName(String name);

	public String getId();

	public void setId(String id);

	public long getStartTime();

	public void setStartTime(long startTime);

	public long getEndTime();

	public void setEndTime(long endTime);

	public String getSource();

	public void setSource(String source);

	public TestStatus getStatus();

	public String getDescription();

	public void setDescription(String description);
	
	public boolean hasChildRecords();
	
	public ILogRecord[] getChildRecords();
	
	public void addChildRecord(ILogRecord childRecord);
	
	public void removeChildRecord(ILogRecord childRecord);
	
	public String getMessage();

	public void setMessage(String message);
	
	public ILogRecord getParentLogRecord();

    public void setParentLogRecord(ILogRecord parentLogRecord);
}
