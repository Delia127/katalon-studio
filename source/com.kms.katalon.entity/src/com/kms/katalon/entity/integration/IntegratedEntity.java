package com.kms.katalon.entity.integration;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class IntegratedEntity implements Serializable, Cloneable {

	private static final long serialVersionUID = -5272475033476141937L;

	//Examples: qTest, QTP, Jira...
	private String productName;  
	
	private IntegratedType type;
	
	private Map<String, String> properties;

	public Map<String, String> getProperties() {
		if (properties == null) {
			properties = new LinkedHashMap<String, String>();
		}
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		
		this.properties = properties;
	}

	public IntegratedType getType() {
		return type;
	}

	public void setType(IntegratedType type) {
		this.type = type;
	}

	public String getProductName() {
		if (productName == null) {
			return "";
		}
		return productName;
	}

	public void setProductName(String productName) {		
		this.productName = productName;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((productName == null) ? 0 : productName.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntegratedEntity other = (IntegratedEntity) obj;
		if (productName == null) {
			if (other.productName != null)
				return false;
		} else if (!productName.equals(other.productName))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
