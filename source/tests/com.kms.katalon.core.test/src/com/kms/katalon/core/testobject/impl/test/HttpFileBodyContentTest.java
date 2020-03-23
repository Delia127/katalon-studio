package com.kms.katalon.core.testobject.impl.test;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;

import com.kms.katalon.core.testobject.impl.HttpFileBodyContent;

public class HttpFileBodyContentTest {
    
    @Test
    public void constructorTest() throws IOException {
        String windowsFilePath = ".\\.\\sample-file.png";
        File fileToUpload = new File(FilenameUtils.separatorsToSystem(windowsFilePath));
        fileToUpload.createNewFile();
        
        try {
            HttpFileBodyContent fileContent = new HttpFileBodyContent(windowsFilePath);
            assertNotNull("File content must be not null", fileContent);
        } catch (IllegalArgumentException | FileNotFoundException e) {
            Assert.fail("File must be found");
        }
        
        fileToUpload.delete();
    }
}
