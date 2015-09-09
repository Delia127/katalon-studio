package com.kms.katalon.entity.link;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VariableLink implements Serializable {
    private static final long serialVersionUID = 1L;
    private String variableId;
    private String value;
    private VariableType type;
    private String testDataLinkId;

    public VariableLink() {
        variableId = "";
        value = "";
        setType(VariableType.SCRIPT);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public VariableType getType() {
        return type;
    }

    public void setType(VariableType type) {
        this.type = type;
    }

    public String getTestDataLinkId() {
        if (testDataLinkId == null) {
            testDataLinkId = "";
        }
        return testDataLinkId;
    }

    public void setTestDataLinkId(String testDataLinkId) {
        this.testDataLinkId = testDataLinkId;
    }

    public enum VariableType {
        DATA_COLUMN, SCRIPT;

        public String value() {
            return name();
        }

        public static VariableType fromValue(String v) {
            if (v.equals("Data Column")) return DATA_COLUMN;
            if (v.equals("Script")) return SCRIPT;
            return valueOf(v);
        }

        public static List<String> getValueStrings() {
            List<String> valueStrings = new ArrayList<>();
            for (VariableType type : values()) {
                valueStrings.add(type.getDisplayName());
            }
            return valueStrings;
        }

        public String getDisplayName() {
            String valueName = "";
            if (this == DATA_COLUMN) {
                valueName = "Data Column";
            } else if (this == SCRIPT) {
                valueName = "Script";
            }
            return valueName;
        }
    }

}
