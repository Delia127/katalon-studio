package com.kms.katalon.controller;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.custom.parser.GlobalVariableParser;
import com.kms.katalon.entity.global.GlobalVariableEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

public class GlobalVariableController extends EntityController {

    private static GlobalVariableController _instance;

    private GlobalVariableController() {

    }

    public static GlobalVariableController getInstance() {
        if (_instance == null) {
            _instance = new GlobalVariableController();
        }
        return (GlobalVariableController) _instance;
    }

    public List<GlobalVariableEntity> getAllGlobalVariables(ProjectEntity project) throws Exception {
        return getDataProviderSetting().getGlobalVariableDataProvider().getAll(project.getLocation());
    }

    public String[] getAllGlobalVariableNames(ProjectEntity project) throws Exception {
        List<GlobalVariableEntity> variables = getAllGlobalVariables(project);
        List<String> variableNames = new ArrayList<String>();
        for (GlobalVariableEntity variable : variables) {
            variableNames.add(variable.getName());
        }
        return variableNames.toArray(new String[0]);
    }

    public void updateVariables(List<GlobalVariableEntity> glbVariableEntities, ProjectEntity project) throws Exception {
        getDataProviderSetting().getGlobalVariableDataProvider().updateVariables(glbVariableEntities, project.getLocation());
        generateGlobalVariableLibFile(project, null);
    }

    public void generateGlobalVariableLibFile(ProjectEntity project, IProgressMonitor monitor) throws Exception {
        try {
            if (monitor != null) {
                monitor.beginTask("Generating global variables...", 1);
            }
            IFolder libFolder = GroovyUtil.getCustomKeywordLibFolder(project);
            GlobalVariableParser.getInstance().generateGlobalVariableLibFile(libFolder, getAllGlobalVariables(project));
            libFolder.refreshLocal(IResource.DEPTH_ONE, monitor);
        } finally {
            if (monitor != null) {
                monitor.done();
            }
        }
    }

}
