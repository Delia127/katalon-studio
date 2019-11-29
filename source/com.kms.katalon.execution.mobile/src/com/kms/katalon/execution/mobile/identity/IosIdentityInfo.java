package com.kms.katalon.execution.mobile.identity;

public class IosIdentityInfo {

    private String name;
    
    private String id;
    
    public IosIdentityInfo() {
        
    }

    public IosIdentityInfo(String name, String id) {
        this.setName(name);
        this.setId(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    @Override
    public boolean equals(Object obj) {
        return this.id.equals(((IosIdentityInfo) obj).getId());
    }
}
