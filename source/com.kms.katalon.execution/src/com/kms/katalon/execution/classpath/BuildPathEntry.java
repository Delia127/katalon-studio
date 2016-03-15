package com.kms.katalon.execution.classpath;

public class BuildPathEntry implements IBuildPath {
    
    private String builtPathLoc;
    
    public BuildPathEntry(String location) {
         builtPathLoc = location; 
    }

    @Override
    public String getBuildPathLocation() {
        return builtPathLoc;
    }

}
