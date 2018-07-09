package com.kms.katalon.dal;

import java.util.List;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.TestListenerEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public interface ITestListenerDataProvider {
    public List<TestListenerEntity> getTestListeners(FolderEntity parentFolder) throws DALException;

    public TestListenerEntity newTestListener(String name, FolderEntity parentFolder) throws DALException;

    public void deleteTestListener(TestListenerEntity testListener);

    public TestListenerEntity renameTestListener(String newName, TestListenerEntity testListener);
}
