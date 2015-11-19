package com.kms.katalon.integration.qtest.entity;

import org.apache.commons.lang.builder.EqualsBuilder;

public class QTestTestCase extends QTestEntity {	
	private long versionId;
    private long parentId;
    private String pid; //Display ID, Ex: TC-93...    
    private String description;
    
    public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public QTestTestCase(long id, String name, long parentId, String pid) {
        super(id, name);
        this.parentId = parentId;
        this.pid = pid;
    }

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	public static int getType() {
		return 1;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (parentId ^ (parentId >>> 32));
		result = prime * result + ((pid == null) ? 0 : pid.hashCode());
		return result;
	}

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof QTestTestCase)) {
            return false;
        }
        QTestTestCase that = (QTestTestCase) obj;
        return new EqualsBuilder().append(this.getParentId(), that.getParentId()).append(this.getId(), that.getId())
                .isEquals();
    }

}
