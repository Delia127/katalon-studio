package com.kms.katalon.integration.qtest.entity;

public abstract class QTestEntity {
    public static final String ID_FIELD = "id";
    public static final String OBJECT_ID_FIELD = "objId";
    public static final String NAME_FIELD = "name";
    public static final String PARENT_ID_FIELD = "parentId";
    public static final String TYPE_FIELD = "type";
    public static final String PID_FIELD = "pid";
    public static final String DESCRIPTION_FIELD = "description";

    protected long id;
    protected String name;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected QTestEntity(long id, String name) {
        this.id = id;
        this.name = name;
    }

    protected QTestEntity() {
    }
}
