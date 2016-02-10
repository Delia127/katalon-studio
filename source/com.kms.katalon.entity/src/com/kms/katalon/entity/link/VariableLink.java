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
        DATA_COLUMN_NAME("Data Column Name"), DATA_COLUMN_INDEX("Data Column Index"), SCRIPT("Script Variable");

        private final String text;

        private VariableType(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }

        public static VariableType fromValue(String v) {
            if (v.equals(DATA_COLUMN_NAME.toString())) return DATA_COLUMN_NAME;
            if (v.equals(DATA_COLUMN_INDEX.toString())) return DATA_COLUMN_INDEX;
            if (v.equals(SCRIPT.toString())) return SCRIPT;
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
