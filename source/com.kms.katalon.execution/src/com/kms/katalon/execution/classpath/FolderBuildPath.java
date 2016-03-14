package com.kms.katalon.execution.classpath;

import java.io.File;

public abstract class FolderBuildPath implements IBuildPath {
    private String projectLocation;
    
    /* package */ FolderBuildPath(String prjLoc) {
        setProjectLocation(prjLoc);
    }
    
    public abstract String getInputLocation();
    
    @Override
    public String getBuildPathLocation() {
        return getOutputLocation(projectLocation);
    }

    public static String getOutputLocation(String projectLocation) {
        return new File(projectLocation, ProjectBuildPath.DF_OUT_PUT_LOC).getAbsolutePath();
    }
    
    public abstract File[] getBuildableFiles();

    public String getProjectLocation() {
        return projectLocation;
    }

    private void setProjectLocation(String projectLocation) {
        this.projectLocation = projectLocation;
    }
}
