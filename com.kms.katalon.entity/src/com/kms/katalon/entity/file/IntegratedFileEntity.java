package com.kms.katalon.entity.file;

import java.util.ArrayList;
import java.util.List;

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
		if (productName == null || productName.isEmpty()) return null;
		
		for (IntegratedEntity integratedEntity : getIntegratedEntities()) {
			if (productName.equals(integratedEntity.getProductName())) {
				return integratedEntity;
			}
		}
		return null;
	}
}
