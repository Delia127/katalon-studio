package com.kms.katalon.core.testobject;

import java.util.ArrayList;
import java.util.List;

public class TestObject {
	
	private TestObject parentObject; //Typically is parent Frame 
	private List<TestObjectProperty> properties;
    private String objectId;
    private String imagePath;
	private boolean useRelativeImagePath;
    
    public TestObject(String objectId) {
        properties = new ArrayList<TestObjectProperty>();
        this.objectId = objectId;
    }

    public TestObject() {
        properties = new ArrayList<TestObjectProperty>();
    }

    public List<TestObjectProperty> getProperties() {
        return properties;
    }
    
    public List<TestObjectProperty> getActiveProperties() {
        List<TestObjectProperty> activeProperties = new ArrayList<TestObjectProperty>();
        for (TestObjectProperty property : properties) {
            if (property.isActive()) {
                activeProperties.add(property);
            }
        }
        return activeProperties;
    }

    public void setProperties(List<TestObjectProperty> properties) {
        this.properties = properties;
    }
    
    public TestObject addProperty(TestObjectProperty property) {
        this.properties.add(property);
        return this;
    }
    
    public TestObject addProperty(String name, ConditionType condition, String value) {
        this.properties.add(new TestObjectProperty(name, condition, value));
        return this;
    }
    
    public TestObject addProperty(String name, ConditionType condition, String value, boolean isActive) {
        this.properties.add(new TestObjectProperty(name, condition, value, isActive));
        return this;
    }
    
    public String findPropertyValue(String name) {
        for (TestObjectProperty property : properties) {
            if (property.getName().equals(name)) {
                return property.getValue();
            }
        }
        return "";
    }
    
    public String findPropertyValue(String name, boolean caseSensitive) {
    	if(caseSensitive){
    		return findPropertyValue(name);
    	}
        for (TestObjectProperty property : properties) {
            if (property.getName().equalsIgnoreCase(name)) {
                return property.getValue();
            }
        }
        return "";
    }
    
    public TestObjectProperty findProperty(String name) {
        for (TestObjectProperty property : properties) {
            if (property.getName().equals(name)) {
                return property;
            }
        }
        return null;
    }

    public String getObjectId() {
        return objectId;
    }
    
    public TestObject getParentObject() {
		return parentObject;
	}

	public void setParentObject(TestObject parentObject) {
		this.parentObject = parentObject;
	}

    public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public boolean getUseRelativeImagePath() {
		return useRelativeImagePath;
	}

	public void setUseRelativeImagePath(boolean useRelativeImagePath) {
		this.useRelativeImagePath = useRelativeImagePath;
	}
	
	@Override
	public String toString() {
	    return "TestObject - " + getObjectId();
	}
}
