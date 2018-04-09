package com.kms.katalon.entity.global;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

import com.kms.katalon.entity.file.FileEntity;

@XmlRootElement(name = "GlobalVariableEntities")
public class ExecutionProfileEntity extends FileEntity {
    
    public static final String DF_PROFILE_NAME = "default";

    private static final long serialVersionUID = 1L;

    private List<GlobalVariableEntity> globalVariableEntities;
    
    private boolean defaultProfile = false;
    
    @Override
    public String getName() {
        String name = super.getName();
        if (StringUtils.isEmpty(name)) {
            return DF_PROFILE_NAME;
        }
        return name;
    }

    @Override
    public String getFileExtension() {
        return getGlobalVariableFileExtension();
    }

    @XmlElement(name = "GlobalVariableEntity")
    public List<GlobalVariableEntity> getGlobalVariableEntities() {
        if (globalVariableEntities == null) {
            globalVariableEntities = new ArrayList<GlobalVariableEntity>();
        }
        return globalVariableEntities;
    }

    public void setGlobalVariableEntities(List<GlobalVariableEntity> globalVariableEntities) {
        this.globalVariableEntities = globalVariableEntities;
    }

    public static String getGlobalVariableFileExtension() {
        return ".glbl";
    }

    public boolean isDefaultProfile() {
        return defaultProfile;
    }

    public void setDefaultProfile(boolean defaultProfile) {
        this.defaultProfile = defaultProfile;
    }
}
