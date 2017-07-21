package com.kms.katalon.entity.repository;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;

public class WebElementPropertyEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String defaultMatchCondition = "equals";

    public static final String TYPE_MAIN = "Main";

    public static final String TYPE_RELATION = "Relation";

    public static final String TYPE_ADDITION = "Additional";
    
    public static final String TYPE_PROPERTY = "type";
    
    public static final String TAG_PROPERTY = "tag";

    private String name;

    private String type = TYPE_MAIN; // Default property type

    private String value;

    private String matchCondition = defaultMatchCondition;

    private boolean isSelected = false;

    public WebElementPropertyEntity() {
    }

    public WebElementPropertyEntity(String name, String value) {
        this.name = name;
        this.value = value;
        this.isSelected = true;
    }

    public WebElementPropertyEntity(String name, String type, String value, String matchCondition, boolean isSelected) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.matchCondition = matchCondition;
        this.isSelected = isSelected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMatchCondition() {
        return this.matchCondition;
    }

    public void setMatchCondition(String matchCondition) {
        if (matchCondition.equals("is exactly")) {
            this.matchCondition = MATCH_CONDITION.EQUAL.getText();
        } else {
            this.matchCondition = matchCondition;
        }
    }

    public Boolean getIsSelected() {
        return this.isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    public WebElementPropertyEntity clone() {
        return new WebElementPropertyEntity(name, type, value, matchCondition, isSelected);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WebElementPropertyEntity)) {
            return false;
        }
        WebElementPropertyEntity other = (WebElementPropertyEntity) obj;
        // compare on name only
        return new EqualsBuilder().append(this.getName(), other.getName()).isEquals();
    }

    public enum MATCH_CONDITION {
        EQUAL("equals"), NOT_EQUAL("not equal"), CONTAINS("contains"), NOT_CONTAIN("not contain"), STARTS_WITH(
                "starts with"), ENDS_WITH("ends with"), MATCH_REGEX("matches regex"), NOT_MATCH_REGEX("not match regex");

        private String text;

        private MATCH_CONDITION(String value) {
            this.text = value;
        }

        public String getText() {
            return text;
        }

        @Override
        public String toString() {
            return this.text;
        }

        public static String[] getTextVlues() {
            String[] values = new String[MATCH_CONDITION.values().length];
            for (int i = 0; i < MATCH_CONDITION.values().length; i++) {
                MATCH_CONDITION con = MATCH_CONDITION.values()[i];
                values[i] = con.getText();
            }
            return values;
        }

        public static int indexOf(String text) {
            for (int i = 0; i < MATCH_CONDITION.values().length; i++) {
                MATCH_CONDITION con = MATCH_CONDITION.values()[i];
                if (con.getText().equals(text)) {
                    return i;
                }
            }
            return -1;
        }
    }
}
