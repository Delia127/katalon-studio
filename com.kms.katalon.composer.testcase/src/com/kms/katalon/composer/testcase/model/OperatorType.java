package com.kms.katalon.composer.testcase.model;

public enum OperatorType {
    IS("Is"), IS_NOT("Is Not"), EQUALS("Equals"), CONTAINS("Contains"), LESS_THAN("<"), GREATER_THAN(">"), IS_NULL(
            "Is Null"), IS_NOT_NULL("Is Not Null");

    private String realName;

    private OperatorType(String realName) {
        this.realName = realName;
    }

    public String getRealName() {
        return realName;
    }

}
