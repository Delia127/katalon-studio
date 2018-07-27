package com.kms.katalon.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.file.FeatureEntity;
import com.kms.katalon.entity.folder.FolderEntity;

public class FeatureController extends EntityController {

    private static FeatureController _instance;

    public static FeatureController getInstance() {
        if (_instance == null) {
            _instance = new FeatureController();
        }
        return (FeatureController) _instance;
    }

    public FeatureEntity getFeature(String name, FolderEntity folder) throws DALException {
        return getFeatures(folder).stream().filter(l -> l.getName().equals(name)).findFirst().orElse(null);
    }

    public List<FeatureEntity> getFeatures(FolderEntity folder) throws DALException {
        return getDataProviderSetting().getFeatureDataProvider().getFeatures(folder);
    }

    public List<FeatureEntity> getSiblingFeatures(FeatureEntity feature, FolderEntity folder)
            throws DALException {
        List<FeatureEntity> childrenOfParent = getDataProviderSetting().getFeatureDataProvider()
                .getFeatures(folder);
        return childrenOfParent.stream().filter(l -> !l.equals(feature)).collect(Collectors.toList());
    }

    public FeatureEntity newFeature(String newName, FolderEntity folder) throws DALException {
        return getDataProviderSetting().getFeatureDataProvider().newFeature(newName, folder);
    }

    public void deleteFeature(FeatureEntity feature) {
        getDataProviderSetting().getFeatureDataProvider().deleteFeature(feature);
    }

    public FeatureEntity renameFeature(String newName, FeatureEntity feature) {
        return getDataProviderSetting().getFeatureDataProvider().renameFeature(newName, feature);
    }

    public FeatureEntity copyFeature(FeatureEntity feature, FolderEntity folder) throws DALException {
        return getDataProviderSetting().getFeatureDataProvider().copyFeature(feature, folder);
    }
}
