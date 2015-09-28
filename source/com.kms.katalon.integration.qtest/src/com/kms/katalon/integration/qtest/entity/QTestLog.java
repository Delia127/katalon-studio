package com.kms.katalon.integration.qtest.entity;

import java.util.LinkedHashMap;
import java.util.Map;

public class QTestLog extends QTestEntity {
	private String message;
	private boolean attachmentIncluded;

	public boolean isAttachmentIncluded() {
		return attachmentIncluded;
	}

	public void setAttachmentIncluded(boolean attachmentIncluded) {
		this.attachmentIncluded = attachmentIncluded;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public Map<String, Object> getProperties() {
		Map<String, Object> properties = new LinkedHashMap<String, Object>();
		properties.put(QTestEntity.ID_FIELD, getId());
		properties.put(QTestEntity.NAME_FIELD, getName());
		properties.put("attachmentIncluded", isAttachmentIncluded());
		
		return properties;
	}
}
