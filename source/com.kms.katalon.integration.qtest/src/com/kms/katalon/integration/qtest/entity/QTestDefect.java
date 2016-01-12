package com.kms.katalon.integration.qtest.entity;

import java.util.ArrayList;
import java.util.List;

public class QTestDefect extends QTestEntity {
    private String gid;

    private List<QTestDefectField> properties;

    public QTestDefect(long id, String name) {
        super(id, name);
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public List<QTestDefectField> getProperties() {
        if (properties == null) {
            properties = new ArrayList<QTestDefectField>();
        }
        return properties;
    }

    public void setProperties(List<QTestDefectField> properties) {
        this.properties = properties;
    }

    public QTestDefectField getPropertyByName(String name) {
        for (QTestDefectField property : properties) {
            if (property.getName().equalsIgnoreCase(name)) {
                return property;
            }
        }
        return null;
    }
}
