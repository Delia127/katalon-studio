package com.kms.katalon.execution.generator;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;

import groovy.lang.GroovyObject;

public class FeatureFileScriptGenerator {
    
    private SystemFileEntity systemFile;
    private IRunConfiguration runConfig;

    public FeatureFileScriptGenerator(SystemFileEntity systemFile, IRunConfiguration runConfig) {
        this.systemFile = systemFile;
        this.runConfig = runConfig;
    }

    public File generateScriptFile() throws CoreException, IOException, ReflectiveOperationException {
        IFolder folder = GroovyUtil.getCustomKeywordLibFolder(systemFile.getProject());
        File file = new File(folder.getRawLocation().toFile().getAbsoluteFile().getAbsolutePath(),
                "TempTempCase" + System.currentTimeMillis() + GroovyConstants.GROOVY_FILE_EXTENSION);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        folder.getFile(file.getName()).refreshLocal(IFolder.DEPTH_ZERO, null);

        Class<?> clazz = Class.forName("com.kms.katalon.execution.generator.FeatureFileScriptTemplate");
        GroovyObject object = (GroovyObject) clazz.newInstance();
        object.invokeMethod("generateTestCaseScriptFile", new Object[] { file, systemFile, runConfig });
        folder.refreshLocal(IResource.DEPTH_ONE, null);
        return file;
    }

}
