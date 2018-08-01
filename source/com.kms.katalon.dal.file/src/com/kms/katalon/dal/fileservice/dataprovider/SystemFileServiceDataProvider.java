package com.kms.katalon.dal.fileservice.dataprovider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.kms.katalon.dal.ISystemFileDataProvider;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.fileservice.manager.EntityFileServiceManager;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

public class SystemFileServiceDataProvider implements ISystemFileDataProvider {
    @Override
    public List<FileEntity> getChildren(FolderEntity parentFolder) throws DALException {
        File folder = new File(parentFolder.getLocation());
        if (!folder.exists() || folder.listFiles() == null) {
            return Collections.emptyList();
        }
        List<FileEntity> listSystemFileEntities = new ArrayList<>();
        for (File file : folder.listFiles(EntityFileServiceManager.fileFilter)) {
            if (file.isFile()) {
                SystemFileEntity systemFileEntity = new SystemFileEntity(file);
                systemFileEntity.setParentFolder(parentFolder);
                systemFileEntity.setProject(parentFolder.getProject());
                
                listSystemFileEntities.add(systemFileEntity);
            } else {
                FolderEntity childFolder = new FolderEntity();
                childFolder.setFolderType(FolderType.INCLUDE);
                childFolder.setName(file.getName());
                childFolder.setParentFolder(parentFolder);
                childFolder.setProject(parentFolder.getProject());
                
                listSystemFileEntities.add(childFolder);
            }
        }
        return listSystemFileEntities;
    }

    @Override
    public List<SystemFileEntity> getFiles(FolderEntity parentFolder) throws DALException {
        File folder = new File(parentFolder.getLocation());
        if (!folder.exists() || folder.listFiles() == null) {
            return Collections.emptyList();
        }
        List<SystemFileEntity> listSystemFileEntities = new ArrayList<>();
        for (File file : folder.listFiles(EntityFileServiceManager.fileFilter)) {
            if (!file.isFile()) {
                continue;
            }
            
            SystemFileEntity systemFileEntity = new SystemFileEntity(file);
            systemFileEntity.setParentFolder(parentFolder);
            systemFileEntity.setProject(parentFolder.getProject());
            
            listSystemFileEntities.add(systemFileEntity);
        }
        return listSystemFileEntities;
    }

    @Override
    public SystemFileEntity newFile(String name, String content, FolderEntity parentFolder) throws DALException {
        File file = new File(parentFolder.getId(), name);
        try {
            file.createNewFile();
            FileUtils.write(file, content);

            SystemFileEntity systemFileEntity = new SystemFileEntity(file);
            systemFileEntity.setParentFolder(parentFolder);
            systemFileEntity.setProject(parentFolder.getProject());

            return systemFileEntity;
        } catch (IOException e) {
            throw new DALException(e);
        }
    }

    @Override
    public void deleteFile(SystemFileEntity systemFileEntity) {
        systemFileEntity.getFile().delete();
    }

    @Override
    public SystemFileEntity renameFile(String newName, SystemFileEntity systemFileEntity) {
        File newFile = new File(systemFileEntity.getParentFolder().getLocation(), newName);
        systemFileEntity.getFile().renameTo(newFile);
        systemFileEntity.setFile(newFile);
        return systemFileEntity;
    }

    @Override
    public SystemFileEntity copyFile(SystemFileEntity sourceFileEntity, FolderEntity folderEntity) throws DALException {
        File sourceFile = sourceFileEntity.getFile();
        String newPotentialName = FilenameUtils.getBaseName(sourceFile.getName()) + " - Copy";
        List<FileEntity> currentFeatures = getChildren(folderEntity);
        int index = 1;
        while (checkNameExist(newPotentialName, currentFeatures)) {
            newPotentialName = String.format("%s %d", newPotentialName, index);
            index++;
        }
        
        File newFile = new File(folderEntity.getLocation(), newPotentialName + sourceFileEntity.getFileExtension());
        try {
            FileUtils.copyFile(sourceFile, newFile);
        } catch (IOException e) {
            throw new DALException(e);
        }

        SystemFileEntity systemFileEntity = new SystemFileEntity(newFile);
        systemFileEntity.setParentFolder(folderEntity);
        systemFileEntity.setProject(folderEntity.getProject());
        return null;
    }
    
    private boolean checkNameExist(String name, List<FileEntity> currentFeatures) {
        return currentFeatures.stream().filter(f -> {
            String fileName = FilenameUtils.getBaseName(f.getName());
            return fileName.equals(name);
        }).findAny().isPresent();
    }

    public FolderEntity copyFolder(FolderEntity folder, FolderEntity destinationFolder) throws DALException {
        String newPotentialName = FilenameUtils.getBaseName(folder.getName()) + " - Copy";
        List<FileEntity> currentFeatures = getChildren(destinationFolder);
        int index = 1;
        while (checkNameExist(newPotentialName, currentFeatures)) {
            newPotentialName = String.format("%s %d", newPotentialName, index);
            index++;
        }

        File newFile = new File(destinationFolder.getLocation(), newPotentialName);
        try {
            FileUtils.copyDirectory(folder.toFile(), newFile);
        } catch (IOException e) {
            throw new DALException(e);
        }
        FolderEntity newFolder = new FolderEntity();
        newFolder.setName(newPotentialName);
        newFolder.setParentFolder(destinationFolder);
        newFolder.setProject(destinationFolder.getProject());
        
        return newFolder;
    }

    @Override
    public SystemFileEntity moveFile(SystemFileEntity systemFile, FolderEntity targetFolder) throws DALException {
        String newPotentialName = FilenameUtils.getBaseName(systemFile.getName());
        List<FileEntity> currentFeatures = getChildren(targetFolder);
        int index = 1;
        while (checkNameExist(newPotentialName, currentFeatures)) {
            newPotentialName = String.format("%s %d", newPotentialName, index);
            index++;
        }

        File newFile = new File(targetFolder.getLocation(), newPotentialName + systemFile.getFileExtension());
        try {
            FileUtils.moveFile(systemFile.getFile(), newFile);
        } catch (IOException e) {
            throw new DALException(e);
        }

        SystemFileEntity newSystemFile = new SystemFileEntity(newFile);
        newSystemFile.setParentFolder(targetFolder);
        newSystemFile.setProject(targetFolder.getProject());
        return systemFile;
    }

    @Override
    public FolderEntity moveFolder(FolderEntity folder, FolderEntity destinationFolder) throws DALException {
        String newPotentialName = FilenameUtils.getBaseName(folder.getName());
        List<FileEntity> currentFeatures = getChildren(destinationFolder);
        int index = 1;
        while (checkNameExist(newPotentialName, currentFeatures)) {
            newPotentialName = String.format("%s %d", newPotentialName, index);
            index++;
        }

        File newFile = new File(destinationFolder.getLocation(), newPotentialName);
        try {
            FileUtils.moveDirectory(folder.toFile(), newFile);
        } catch (IOException e) {
            throw new DALException(e);
        }
        FolderEntity newFolder = new FolderEntity();
        newFolder.setName(newPotentialName);
        newFolder.setParentFolder(destinationFolder);
        newFolder.setProject(destinationFolder.getProject());
        
        return newFolder;
    }
}
