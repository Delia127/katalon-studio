package com.kms.katalon.dal;

import java.util.List;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.FeatureEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public interface IFeatureDataProvider {
    public List<FeatureEntity> getFeatures(FolderEntity parentFolder) throws DALException;

    public FeatureEntity newFeature(String name, FolderEntity parentFolder) throws DALException;

    public void deleteFeature(FeatureEntity feature);

    public FeatureEntity renameFeature(String newName, FeatureEntity feature);

    FeatureEntity copyFeature(FeatureEntity feature, FolderEntity folderEntity) throws DALException;
}
