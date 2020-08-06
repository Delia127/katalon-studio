package com.kms.katalon.composer.project.sample;

public abstract class SampleProject {
    
    protected SampleProjectType type;
    
    protected String name;
    
    protected String suggestedName;

    public SampleProjectType getType() {
        return type;
    }

    public void setType(SampleProjectType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuggestedName() {
        return suggestedName;
    }

    public void setSuggestedName(String suggestedName) {
        this.suggestedName = suggestedName;
    }
}
