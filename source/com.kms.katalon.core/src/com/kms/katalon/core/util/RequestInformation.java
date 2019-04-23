package com.kms.katalon.core.util;

import java.io.File;

public class RequestInformation {

    private String name;
    
    private String testObjectId;
    
    private String harId;
    
    private File harFile;
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getHarId() {
		return harId;
	}

	public void setHarId(String harId) {
		this.harId = harId;
	}

	public String getTestObjectId() {
        return testObjectId;
    }
    
    public void setTestObjectId(String testObjectId) {
        this.testObjectId = testObjectId;
    }

    public File getHarFile() {
        return harFile;
    }

    public void setHarFile(File harFile) {
        this.harFile = harFile;
    }
}
