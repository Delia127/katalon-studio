package com.kms.katalon.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.TestListenerEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public class TestListenerController extends EntityController {

    private static TestListenerController _instance;

    public static TestListenerController getInstance() {
        if (_instance == null) {
            _instance = new TestListenerController();
        }
        return (TestListenerController) _instance;
    }

    public TestListenerEntity getTestListener(String name, FolderEntity folder) throws DALException {
        return getTestListeners(folder).stream().filter(l -> l.getName().equals(name)).findFirst().orElse(null);
    }

    public List<TestListenerEntity> getTestListeners(FolderEntity folder) throws DALException {
        return getDataProviderSetting().getTestListenerDataProvider().getTestListeners(folder);
    }

    public List<TestListenerEntity> getSiblingTestListeners(TestListenerEntity listener, FolderEntity folder)
            throws DALException {
        List<TestListenerEntity> childrenOfParent = getDataProviderSetting().getTestListenerDataProvider()
                .getTestListeners(folder);
        return childrenOfParent.stream().filter(l -> !l.equals(listener)).collect(Collectors.toList());
    }

    public TestListenerEntity newTestListener(String newName, FolderEntity folder) throws DALException {
        return getDataProviderSetting().getTestListenerDataProvider().newTestListener(newName, folder);
    }

    public void deleteTestListener(TestListenerEntity testListener) {
        getDataProviderSetting().getTestListenerDataProvider().deleteTestListener(testListener);
    }

    public TestListenerEntity renameTestListener(String newName, TestListenerEntity testListener) {
        return getDataProviderSetting().getTestListenerDataProvider().renameTestListener(newName, testListener);
    }
}
