package com.kms.katalon.integration.qtest.entity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;


public class QTestProject extends QTestEntity {
	private String token;
	private String host;
	private List<String> testCaseFolderIds;
	private List<String> testSuiteFolderIds;
	
	public QTestProject(long id, String name) {
		super(id, name);
	}
	
	public QTestProject() {}
	
	@Override
	public String toString() {
    	return this.name;
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public List<String> getTestCaseFolderIds() {
		if (testCaseFolderIds == null) {
			testCaseFolderIds = new ArrayList<String>();
		}
		return testCaseFolderIds;
	}

	public void setTestCaseFolderIds(List<String> testCaseFolderIds) {		
		this.testCaseFolderIds = testCaseFolderIds;
	}

	public List<String> getTestSuiteFolderIds() {
		if (testSuiteFolderIds == null) {
			testSuiteFolderIds = new ArrayList<String>();
		}
		return testSuiteFolderIds;
	}

	public void setTestSuiteFolderIds(List<String> testSuiteFolderIds) {	
		this.testSuiteFolderIds = testSuiteFolderIds;
	}
	
	public Map<String, Object> getProperties() {
		Map<String, Object> properties = new LinkedHashMap<String, Object>();
		properties.put(QTestEntity.ID_FIELD, getId());
		properties.put(QTestEntity.NAME_FIELD, getName());
		properties.put("testCaseFolderMappeds", getTestCaseFolderIds());
		properties.put("testSuiteFolderMappeds", getTestSuiteFolderIds());
		
		return properties;
	}
	

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof QTestProject)) {
            return false;
        }
        QTestProject that = (QTestProject) obj;
        return new EqualsBuilder().append(this.getId(), that.getId()).append(this.getName(), that.getName()).isEquals();
    }

}
