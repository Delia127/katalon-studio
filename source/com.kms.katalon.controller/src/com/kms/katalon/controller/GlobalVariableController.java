package com.kms.katalon.controller;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.custom.parser.GlobalVariableParser;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
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
        return getDataProviderSetting().getGlobalVariableDataProvider()
                .getAll(project)
                .stream()
                .map(p -> p.getGlobalVariableEntities())
                .flatMap(v -> v.stream())
                .collect(Collectors.toList());
    }

    public String[] getAllGlobalVariableNames(ProjectEntity project) throws Exception {
        return getAllGlobalVariables(project).stream()
                .map(v -> v.getName())
                .collect(Collectors.toSet()) // remove duplicated name
                .toArray(new String[0]);
    }

    public void generateGlobalVariableLibFile(ProjectEntity project, IProgressMonitor monitor) throws Exception {
        try {
            if (monitor != null) {
                monitor.beginTask("Generating global variables...", 1);
            }
            IFolder libFolder = GroovyUtil.getCustomKeywordLibFolder(project);
            GlobalVariableParser.getInstance().generateGlobalVariableLibFile(libFolder,
                    getAllGlobalVariableCollections(project));
            libFolder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
            waitForGlobalVariableClassFileAvailable(project);
        } finally {
            if (monitor != null) {
                monitor.done();
            }
        }
    }
    
    private void waitForGlobalVariableClassFileAvailable(ProjectEntity project) throws InterruptedException {
        File globalVariableClassFile = new File(project.getFolderLocation(), "bin/lib/internal/GlobalVariable.class");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Object> callable = new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                while (!globalVariableClassFile.exists()) {
                    Thread.sleep(300);
                }
                return null;
           }
        };
            
        executor.invokeAll(Arrays.asList(callable), 2, TimeUnit.SECONDS);
    }

    public void deleteExecutionProfile(ExecutionProfileEntity profile) throws DALException {
        getDataProviderSetting().getGlobalVariableDataProvider().delete(profile);
    }
    
    public ExecutionProfileEntity getExecutionProfile(String name, ProjectEntity project) throws DALException {
        return getDataProviderSetting().getGlobalVariableDataProvider().get(name, project);
    }

    public ExecutionProfileEntity newExecutionProfile(String newName, ProjectEntity project) throws Exception {
        ExecutionProfileEntity newProfile = getDataProviderSetting().getGlobalVariableDataProvider().newProfile(newName,
                project);
        generateGlobalVariableLibFile(project, null);
        return newProfile;
    }

    public ExecutionProfileEntity renameExecutionProfile(String newName, ExecutionProfileEntity profile)
            throws Exception {
        ExecutionProfileEntity updated = getDataProviderSetting().getGlobalVariableDataProvider().rename(newName,
                profile);
        generateGlobalVariableLibFile(ProjectController.getInstance().getCurrentProject(), null);
        return updated;
    }

    public ExecutionProfileEntity updateExecutionProfile(ExecutionProfileEntity profile) throws Exception {
        ExecutionProfileEntity updated = getDataProviderSetting().getGlobalVariableDataProvider().update(profile);
        generateGlobalVariableLibFile(ProjectController.getInstance().getCurrentProject(), null);
        return updated;
    }

    public ExecutionProfileEntity getGlobalVariableCollection(String name, ProjectEntity project) throws DALException {
        return getDataProviderSetting().getGlobalVariableDataProvider().get(name, project);
    }

    public List<ExecutionProfileEntity> getAllGlobalVariableCollections(ProjectEntity project) throws DALException {
        List<ExecutionProfileEntity> profiles = getDataProviderSetting().getGlobalVariableDataProvider()
                .getAll(project);
        profiles.sort(new Comparator<ExecutionProfileEntity>() {

            @Override
            public int compare(ExecutionProfileEntity profileA, ExecutionProfileEntity profileB) {
                if (profileA.isDefaultProfile()) {
                    return -1;
                }
                if (profileB.isDefaultProfile()) {
                    return 1;
                }
                return profileA.getName().compareTo(profileB.getName());
            }
        });
        return profiles;
    }
    
    public ExecutionProfileEntity toExecutionProfileEntity(String xmlString) throws DALException{
    	return getDataProviderSetting().getEntityDataProvider().toEntity(xmlString, ExecutionProfileEntity.class);
    }

    public ExecutionProfileEntity copyProfile(ExecutionProfileEntity profileEntity) throws Exception {
        String newName = EntityNameController.getInstance().getAvailableName(profileEntity.getName() + " - Copy",
                profileEntity.getParentFolder(), false);
        return getDataProviderSetting().getGlobalVariableDataProvider().copyProfile(newName, profileEntity);
    }
}
