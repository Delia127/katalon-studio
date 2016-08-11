package com.kms.katalon.custom.parser;

import groovy.lang.GroovyObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.global.GlobalVariableEntity;

public class GlobalVariableParser {
    public static final String GLOBAL_VARIABLE_CLASS_NAME = "GlobalVariable";
    public static final String GLOBAL_VARIABLE_FILE_NAME = GLOBAL_VARIABLE_CLASS_NAME + ".groovy";
    private static GlobalVariableParser _instance;
    private final static String TEMPLATE_CLASS_NAME = IdConstants.KATALON_CUSTOM_BUNDLE_ID
            + ".generation.GlobalVariableTemplate";
    private final static String GENERATED_GLOBAL_VARIABLE_LIB_FILE_METHOD_NAME = "generateGlobalVariableFile";
    public final static String INTERNAL_PACKAGE_NAME = "internal";

    private GlobalVariableParser() {
    }

    public static GlobalVariableParser getInstance() {
        if (_instance == null) {
            _instance = new GlobalVariableParser();
        }
        return _instance;
    }

    public void generateGlobalVariableLibFile(IFolder libFolder, List<GlobalVariableEntity> globalVariables)
            throws Exception {
        String libFolderPath = libFolder.getRawLocation().toString();
        File internalPackageFolder = new File(libFolderPath, INTERNAL_PACKAGE_NAME);
        File internalGlobalVariableFile = new File(internalPackageFolder, GLOBAL_VARIABLE_FILE_NAME);
        File globalVariableFile = new File(libFolderPath, GLOBAL_VARIABLE_FILE_NAME);
        
        generateGlobalVariableFile(globalVariables, globalVariableFile, true);
        generateGlobalVariableFile(globalVariables, internalGlobalVariableFile, false);
        libFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
    }

    private void generateGlobalVariableFile(List<GlobalVariableEntity> globalVariables, File globalVariableFile, boolean isDeprecated) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, IOException {
        if (!globalVariableFile.exists()) {
            globalVariableFile.getParentFile().mkdirs();
            globalVariableFile.createNewFile();
        }
        Class<?> clazz = Class.forName(TEMPLATE_CLASS_NAME);
        GroovyObject object = (GroovyObject) clazz.newInstance();
        object.invokeMethod(GENERATED_GLOBAL_VARIABLE_LIB_FILE_METHOD_NAME, new Object[] { globalVariableFile, globalVariables, isDeprecated });
    }
}
