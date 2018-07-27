package com.kms.katalon.dal.fileservice.dataprovider;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.kms.katalon.dal.IFeatureDataProvider;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.FeatureEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public class FeatureFileServiceDataProvider implements IFeatureDataProvider {

    @Override
    public List<FeatureEntity> getFeatures(FolderEntity folderEntity) throws DALException {
        File folder = new File(folderEntity.getLocation());
        if (!folder.exists()) {
            return Collections.emptyList();
        }
        File[] files = folder.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(files)
                .filter(f -> f.getName().endsWith(FeatureEntity.FILE_EXTENSION))
                .map(f -> getFeature(f.getName(), folderEntity))
                .collect(Collectors.toList());
    }

    @Override
    public FeatureEntity newFeature(String name, FolderEntity parentFolder) throws DALException {
        FeatureEntity featureEntity = new FeatureEntity();
        featureEntity.setName(name);
        featureEntity.setParentFolder(parentFolder);
        featureEntity.setProject(parentFolder.getProject());

        File testListenerFile = new File(featureEntity.getLocation());
        try {
            testListenerFile.createNewFile();
        } catch (IOException e) {
            throw new DALException(e);
        }

        return featureEntity;
    }

    @Override
    public void deleteFeature(FeatureEntity feature) {
        File featureFile = new File(feature.getLocation());
        if (featureFile.exists()) {
            featureFile.delete();
        }
    }

    @Override
    public FeatureEntity renameFeature(String newName, FeatureEntity feature) {
        File featureFile = new File(feature.getLocation());

        if (featureFile.exists()) {
            File newDest = new File(feature.getParentFolder().getLocation(), newName + feature.getFileExtension());
            featureFile.renameTo(newDest);
            feature.setName(newName);
        }
        return feature;
    }

    private FeatureEntity getFeature(String fileName, FolderEntity folderEntity) {
        FeatureEntity feature = new FeatureEntity();
        feature.setName(FilenameUtils.getBaseName(fileName));
        feature.setParentFolder(folderEntity);
        feature.setProject(folderEntity.getProject());
        return feature;
    }

    @Override
    public FeatureEntity copyFeature(FeatureEntity feature, FolderEntity folderEntity) throws DALException {
        String newPotentialName = feature.getName() + " - Copy";
        List<FeatureEntity> currentFeatures = getFeatures(folderEntity);
        int index = 1;
        while (checkNameExist(newPotentialName, currentFeatures)) {
            newPotentialName = String.format("%s %d", newPotentialName, index);
            index++;
        }

        FeatureEntity coppied = new FeatureEntity();
        coppied.setName(newPotentialName);
        coppied.setParentFolder(folderEntity);
        coppied.setProject(folderEntity.getProject());

        try {
            FileUtils.copyFile(new File(feature.getLocation()), new File(coppied.getLocation()));
        } catch (IOException e) {
            throw new DALException(e);
        }

        return coppied;
    }

    private boolean checkNameExist(String name, List<FeatureEntity> currentFeatures) {
        return currentFeatures.stream().filter(f -> f.getName().equals(name)).findAny().isPresent();
    }
}
