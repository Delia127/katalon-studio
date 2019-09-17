package com.kms.katalon.integration.analytics.entity;

import java.util.Date;

public class AnalyticsLicenseKey {
	
	private Long id;
	
	private String value;
	
	private String machineId;
	
	private Date createAt;
	
	private Date expirationDate;
	
	private AnalyticsOrganization organization;
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}
	
	public String getMachineId() {
		return machineId;
	}
	
	public void setCreateAt(Date createAt) {
		this.createAt = createAt;
	}
	
	public Date getCreateAt() {
		return createAt;
	}
	
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public Date getExpirationDate() {
		return expirationDate;
	}
	
	public void setOrganization(AnalyticsOrganization organization) {
		this.organization = organization;
	}
	
	public AnalyticsOrganization getOrganization() {
		return organization;
	}
}
