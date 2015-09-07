package com.kms.katalon.composer.objectrepository.providers;
import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class PropertyGroup {

	private String name;
	
	private List<WebElementPropertyEntity> properties;

	public List<WebElementPropertyEntity> getProperties() {
		if(properties == null){
			properties = new ArrayList<WebElementPropertyEntity>();
		}
		return properties;
	}

	public void setProperties(List<WebElementPropertyEntity> properties) {
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
}
