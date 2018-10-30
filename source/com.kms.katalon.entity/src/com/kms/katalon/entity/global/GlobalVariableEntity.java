package com.kms.katalon.entity.global;

import java.io.Serializable;

public class GlobalVariableEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String initValue;
    private String description;

    public GlobalVariableEntity() {
        this("", "''");
    }

    public GlobalVariableEntity(String newName, String value) {
        setName(newName);
        setInitValue(value);
        description = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInitValue() {
        return initValue;
    }

    public void setInitValue(String initValue) {
        this.initValue = initValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public GlobalVariableEntity clone() {
        GlobalVariableEntity newGlobalVariableEntity = new GlobalVariableEntity(getName(), getInitValue());
        newGlobalVariableEntity.setDescription(getDescription());
        return newGlobalVariableEntity;
    }
    
    public boolean equals(GlobalVariableEntity entity){
    	return getName().equals(entity.getName()) 
    			&& getInitValue().equals(entity.getInitValue())
    			&& getDescription().equals(entity.getDescription());
    			
    }
    
    public boolean equalsWithoutName(GlobalVariableEntity entity){
    	return  getInitValue().equals(entity.getInitValue())
    			&& getDescription().equals(entity.getDescription());
    }
}
