package com.kms.katalon.composer.testcase.model;

public enum ConditionType {
    IF("If"), END_IF("End If"), WHILE("While"), END_WHILE("End While");

    private String realName;

    private ConditionType(String realName) {
        this.realName = realName;
    }

    public String getRealName() {
        return realName;
    }
    
    public static boolean isCondition(String name) {
        for (int i = 0 ; i< values().length; i++) {
            if (values()[i].getRealName().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
