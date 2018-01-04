package com.kms.katalon.dal.fileservice.dataprovider;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;

import com.kms.katalon.dal.ITestListenerDataProvider;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.TestListenerEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public class TestListenerFileServiceDataProvider implements ITestListenerDataProvider {
    @Override
    public List<TestListenerEntity> getTestListeners(FolderEntity folderEntity) throws DALException {
        File folder = new File(folderEntity.getLocation());
        if (!folder.exists()) {
            return Collections.emptyList();
        }
        File[] files = folder.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(files)
                .filter(f -> f.getName().endsWith(TestListenerEntity.FILE_EXTENSION))
                .map(f -> getTestListener(f.getName(), folderEntity))
                .collect(Collectors.toList());
    }

    private TestListenerEntity getTestListener(String fileName, FolderEntity folderEntity) {
        TestListenerEntity listenerEntity = new TestListenerEntity();
        listenerEntity.setName(FilenameUtils.getBaseName(fileName));
        listenerEntity.setParentFolder(folderEntity);
        listenerEntity.setProject(folderEntity.getProject());
        return listenerEntity;
    }

    @Override
    public void deleteTestListener(TestListenerEntity testListener) {
        File testListenerFile = new File(testListener.getLocation());
        if (testListenerFile.exists()) {
            testListenerFile.delete();
        }
    }

    @Override
    public TestListenerEntity renameTestListener(String newName, TestListenerEntity testListener) {
        File testListenerFile = new File(testListener.getLocation());

        if (testListenerFile.exists()) {
            File newDest = new File(testListener.getParentFolder().getLocation(),
                    newName + testListener.getFileExtension());
            testListenerFile.renameTo(newDest);
            testListener.setName(newName);
        }
        return testListener;
    }

    @Override
    public TestListenerEntity newTestListener(String name, FolderEntity parentFolder) throws DALException {
        TestListenerEntity listenerEntity = new TestListenerEntity();
        listenerEntity.setName(name);
        listenerEntity.setParentFolder(parentFolder);
        listenerEntity.setProject(parentFolder.getProject());

        File testListenerFile = new File(listenerEntity.getLocation());
        try {
            testListenerFile.createNewFile();
        } catch (IOException e) {
            throw new DALException(e);
        }

        return listenerEntity;
    }

}
