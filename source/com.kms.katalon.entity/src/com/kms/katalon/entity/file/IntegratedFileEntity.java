package com.kms.katalon.entity.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.entity.integration.IntegratedEntity;

public abstract class IntegratedFileEntity extends FileEntity {

	private static final long serialVersionUID = -7745543389066504753L;

	private List<IntegratedEntity> integratedEntities;

	public List<IntegratedEntity> getIntegratedEntities() {
		if (integratedEntities == null) {
			integratedEntities = new ArrayList<IntegratedEntity>();
		}
		return integratedEntities;
	}

	public void setIntegratedEntities(List<IntegratedEntity> integratedEntities) {
		this.integratedEntities = integratedEntities;
	}
	
	public IntegratedEntity getIntegratedEntity(String productName) {
		if (productName == null || productName.isEmpty()) {
		    return null;
		}
		
		for (IntegratedEntity integratedEntity : getIntegratedEntities()) {
			if (productName.equals(integratedEntity.getProductName())) {
				return integratedEntity;
			}
		}
		return null;
	}

	public IntegratedFileEntity updateIntegratedEntity(IntegratedEntity integratedEntity) {
	    if (integratedEntity == null || StringUtils.isEmpty(integratedEntity.getProductName())) {
	        return this;
	    }
	    IntegratedEntity oldIntegrated = getIntegratedEntity(integratedEntity.getProductName());
	    if (oldIntegrated == null) {
	        integratedEntities.add(integratedEntity);
	    } else {
	        Map<String, String> properties = integratedEntity.getProperties();
            oldIntegrated.setProperties(properties != null ? new HashMap<>(properties) : Collections.emptyMap());
	    }
	    return this;
	}
}
