package com.kms.katalon.entity.variable;

import java.io.Serializable;

import com.kms.katalon.entity.util.Util;

public class VariableEntity implements Serializable {

    protected static final long serialVersionUID = 1L;

    protected String defaultValue; // raw string of default value of the variable

    protected String name;

    protected String id;

    protected String description;

    /**
     * A flag that marks the variable's default value will be masked in logs.
     */
    private boolean masked;

    public VariableEntity() {
        id = Util.generateGuid();
        name = "";
        defaultValue = "";
        description = "";
        masked = false;
    }
    
    public VariableEntity(String name, String defaultValue){
    	this.id = Util.generateGuid();
        this.name = name;
        this.defaultValue = defaultValue;
        this.description = "";
        this.masked = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultValue() {
        if (defaultValue.isEmpty()) {
            return "null";
        }
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public VariableEntity clone() {
        VariableEntity newVariable = new VariableEntity();
        newVariable.setName(getName());
        newVariable.setDefaultValue(getDefaultValue());
        newVariable.setDescription(getDescription());
        newVariable.setMasked(isMasked());
        return newVariable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isMasked() {
        return masked;
    }

    public void setMasked(boolean masked) {
        this.masked = masked;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + (masked ? 1231 : 1237);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        VariableEntity other = (VariableEntity) obj;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (masked != other.masked)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
