package com.kms.katalon.execution.mobile.driver;

public class MobileDevice {
    
    private String id;
    
    private String name;
    
    private String model;

    private String version;
    
    public MobileDevice(String id) {
        setId(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
    
    public String getFullName() {
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(getName()).append(" - ").append(getModel()).append(" - ").append(getVersion());
        return nameBuilder.toString();
    }
}
