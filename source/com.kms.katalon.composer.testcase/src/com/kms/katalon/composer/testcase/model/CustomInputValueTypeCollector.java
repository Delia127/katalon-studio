package com.kms.katalon.composer.testcase.model;

import java.util.ArrayList;
import java.util.List;

public class CustomInputValueTypeCollector {
    private static CustomInputValueTypeCollector instance;
    private List<ICustomInputValueType> customInputValueTypeList;

    private CustomInputValueTypeCollector() {
        customInputValueTypeList = new ArrayList<ICustomInputValueType>();
    }

    public static CustomInputValueTypeCollector getInstance() {
        if (instance == null) {
            instance = new CustomInputValueTypeCollector();
        }
        return instance;
    }

    public void addCustomInputValueType(ICustomInputValueType customInputValueType) {
        customInputValueTypeList.add(customInputValueType);
    }

    public ICustomInputValueType[] getAllCustomInputValueTypes() {
        return customInputValueTypeList.toArray(new ICustomInputValueType[customInputValueTypeList.size()]);
    }
}
