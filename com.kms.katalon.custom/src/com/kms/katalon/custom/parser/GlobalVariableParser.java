package com.kms.katalon.custom.parser;

import groovy.lang.GroovyObject;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;

import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.global.GlobalVariableEntity;

public class GlobalVariableParser {
    private static GlobalVariableParser _instance;
    private final static String TEMPLATE_CLASS_NAME = IdConstants.KATALON_CUSTOM_BUNDLE_ID
            + ".generation.GlobalVariableTemplate";
    private final static String GENERATED_GLOBAL_VARIABLE_LIB_FILE_METHOD_NAME = "generateGlobalVarialbeFile";

    private GlobalVariableParser() {
    }

    public static GlobalVariableParser getInstance() {
        if (_instance == null) {
            _instance = new GlobalVariableParser();
        }
        return _instance;
    }

    @SuppressWarnings("rawtypes")
    public void generateGlobalVariableLibFile(IFolder libFolder, List<GlobalVariableEntity> globalVariables)
            throws Exception {
        File file = new File(libFolder.getRawLocation().toString(), "GlobalVariable.groovy");
        if (!file.exists()) {
            file.createNewFile();
        }

        Class clazz = Class.forName(TEMPLATE_CLASS_NAME);
        GroovyObject object = (GroovyObject) clazz.newInstance();
        object.invokeMethod(GENERATED_GLOBAL_VARIABLE_LIB_FILE_METHOD_NAME, new Object[] { file, globalVariables });
        IFile iFile = libFolder.getFile(file.getName());
        iFile.refreshLocal(IResource.DEPTH_ZERO, null);
    }
}
