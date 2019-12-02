package com.kms.katalon.controller;

import java.io.File;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.controller.constants.ControllerMessageConstants;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.custom.parser.GlobalVariableParser;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.global.GlobalVariableEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;
import com.kms.katalon.logging.LogUtil;

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

    public List<GlobalVariableEntity> getAllGlobalVariables(ProjectEntity project) throws ControllerException {
        try {
            return getDataProviderSetting().getGlobalVariableDataProvider()
                    .getAll(project)
                    .stream()
                    .map(p -> p.getGlobalVariableEntities())
                    .flatMap(v -> v.stream())
                    .collect(Collectors.toList());
        } catch (DALException e) {
            throw new ControllerException(e);
        }
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
                String taskName = "Generating global variables...";
                monitor.beginTask(taskName, 1);
            }

            IFolder libFolder = GroovyUtil.getCustomKeywordLibFolder(project);
            GlobalVariableParser.getInstance().generateGlobalVariableLibFile(libFolder,
                    getAllGlobalVariableCollections(project));
            waitForGlobalVariableClassFileAvailable(project);
        } finally {
            if (monitor != null) {
                monitor.done();
            }
        }
    }
    
    /**
     * @see https://github.com/kms-technology/katalon/issues/3600
     * 
     * @param project Current Project
     * @throws Exception
     * @throws ControllerException
     */
    public void generateGlobalVariableLibFileFromRawTextTemplate(ProjectEntity project, IProgressMonitor monitor)
            throws ControllerException, Exception {
        try {
            if (monitor != null) {
                String taskName = "Generating global variables...";
                monitor.beginTask(taskName, 1);
            }
            File libFolder = new File(project.getFolderLocation() + File.separator + "/Libs/");
            if (!libFolder.exists()) {
                libFolder.mkdirs();
            }
            GlobalVariableParser.getInstance().generateGlobalVariableLibFileV2(libFolder,
                    getAllGlobalVariableCollections(project));
        } finally {
            if (monitor != null) {
                monitor.done();
            }
        }
    }

    private void waitForGlobalVariableClassFileAvailable(ProjectEntity project) throws InterruptedException {
        File globalVariableClassFile = new File(project.getFolderLocation(), "bin/lib/internal/GlobalVariable.class");
        long time = System.currentTimeMillis();
        while (System.currentTimeMillis() - time < TimeUnit.MINUTES.toMillis(5) && !globalVariableClassFile.exists()) {
            Thread.sleep(300L);
        }
        if (!globalVariableClassFile.exists()) {
            throw new InterruptedException(MessageFormat.format(
                    ControllerMessageConstants.GlobalVariableController_MSG_COULD_NOT_GENERATE_GLOBALVARIABLE,
                    project.getFolderLocation()));
        }
    }

    public void deleteExecutionProfile(ExecutionProfileEntity profile) throws DALException {
        getDataProviderSetting().getGlobalVariableDataProvider().delete(profile);
    }

    public ExecutionProfileEntity getExecutionProfile(String name, ProjectEntity project) throws ControllerException {
        try {
            return getDataProviderSetting().getGlobalVariableDataProvider().get(name, project);
        } catch (DALException e) {
            throw new ControllerException(e);
        }
    }

    public ExecutionProfileEntity getExecutionProfileById(String id, ProjectEntity project) throws ControllerException {
        try {
            return getDataProviderSetting().getGlobalVariableDataProvider().getById(id, project);
        } catch (DALException e) {
            throw new ControllerException(e);
        }
    }

    public ExecutionProfileEntity newExecutionProfile(String newName, ProjectEntity project) throws ControllerException {
        ExecutionProfileEntity newProfile;
        try {
            newProfile = getDataProviderSetting().getGlobalVariableDataProvider().newProfile(newName,
                    project);

            generateGlobalVariableLibFile(project, null);
            return newProfile;
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }

    public ExecutionProfileEntity renameExecutionProfile(String newName, ExecutionProfileEntity profile)
            throws Exception {
        ExecutionProfileEntity updated = getDataProviderSetting().getGlobalVariableDataProvider().rename(newName,
                profile);
        generateGlobalVariableLibFile(ProjectController.getInstance().getCurrentProject(), null);
        return updated;
    }

    public ExecutionProfileEntity updateExecutionProfile(ExecutionProfileEntity profile) throws ControllerException {
        try {
            ExecutionProfileEntity updated = getDataProviderSetting().getGlobalVariableDataProvider().update(profile);

            generateGlobalVariableLibFile(ProjectController.getInstance().getCurrentProject(), null);
            return updated;
        } catch (Exception e) {
            throw new ControllerException(e);
        }
    }

    public ExecutionProfileEntity getGlobalVariableCollection(String name, ProjectEntity project) throws DALException {
        return getDataProviderSetting().getGlobalVariableDataProvider().get(name, project);
    }

    public List<ExecutionProfileEntity> getAllGlobalVariableCollections(ProjectEntity project) throws ControllerException {
        try {
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
        } catch (DALException e) {
            throw new ControllerException(e);
        }
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
