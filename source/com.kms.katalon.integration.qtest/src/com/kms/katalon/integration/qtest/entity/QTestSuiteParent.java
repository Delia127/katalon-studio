package com.kms.katalon.integration.qtest.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.qas.qtest.api.internal.model.ArtifactLevel;

public abstract class QTestSuiteParent extends QTestEntity {
    public static final int RELEASE_ROOT_TYPE = 6;
    public static final int RELEASE_TYPE = 8;
    public static final int CYCLE_TYPE = 4;

    public abstract List<QTestSuiteParent> getChildren();

    public abstract QTestSuiteParent getParent();

    public abstract int getType();

    public abstract String getTypeName();

    public Map<String, Object> getPropertyMap() {
        Map<String, Object> properties = new HashMap<String, Object>();

        properties.put(QTestEntity.ID_FIELD, id);
        properties.put(QTestEntity.NAME_FIELD, name);
        properties.put("type", getType());

        QTestSuiteParent parent = getParent();
        if (parent != null) {
            properties.put("parentId", parent.getId());
            properties.put("parentName", parent.getName());
        } else {
            properties.put("parentId", 0);
            properties.put("parentName", "");
        }

        return properties;
    }

    public static QTestSuiteParent getTestSuiteParent(long id, int type, String name) {
        QTestSuiteParent parent = null;
        switch (type) {
            case RELEASE_ROOT_TYPE:
                parent = new QTestReleaseRoot();
                break;
            case RELEASE_TYPE:
                parent = new QTestRelease();
                break;
            case CYCLE_TYPE:
                parent = new QTestCycle();
                break;
        }

        if (parent != null) {
            parent.setId(id);
            parent.setName(name);
        }
        return parent;
    }

    public ArtifactLevel getArtifactLevel() {
        switch (this.getType()) {
            case QTestSuiteParent.RELEASE_ROOT_TYPE:
                return ArtifactLevel.ROOT;
            case QTestSuiteParent.RELEASE_TYPE:
                return ArtifactLevel.RELEASE;
            case QTestSuiteParent.CYCLE_TYPE:
                return ArtifactLevel.TEST_CYCLE;
            default:
                return ArtifactLevel.ROOT;
        }
    }
}
