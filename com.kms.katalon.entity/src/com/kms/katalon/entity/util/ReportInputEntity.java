package com.kms.katalon.entity.util;

public class ReportInputEntity
{
	private String startDate;
	private String endDate;
	
	
	public void setEndDate(String endDateInput)
	{
		endDate = endDateInput;
	}
	
	public String getEndDate()
	{
		return endDate;
	}
	
	public void setStartDate(String startDateInput)
	{
		startDate = startDateInput;
	}
	
	public String getStartDate()
	{
		return startDate;
	}
}
