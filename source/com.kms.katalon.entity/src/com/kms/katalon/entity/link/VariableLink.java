package com.kms.katalon.entity.link;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.entity.file.ClonableObject;

public class VariableLink extends ClonableObject {
    private static final long serialVersionUID = 1L;

    private String variableId;

    private String value;

    private VariableType type;

    private String testDataLinkId;

    public VariableLink() {
        variableId = "";
        value = "";
        setType(VariableType.DEFAULT);
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
        DATA_COLUMN("Data Column"), DATA_COLUMN_INDEX("Data Column Index"), DEFAULT("Default"), SCRIPT_VARIABLE(
                "Script Variable");

        private final String text;

        private VariableType(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        public static VariableType fromValue(String v) {
            if (v == null) {
                return DEFAULT;
            }
            for (VariableType type : values()) {
                if (type.toString().equals(v)) {
                    return type;
                }
            }
            return valueOf(v);
        }

        public static List<String> getValueStrings() {
            List<String> valueStrings = new ArrayList<String>();
            for (VariableType type : values()) {
                valueStrings.add(type.toString());
            }
            return valueStrings;
        }
    }

}
