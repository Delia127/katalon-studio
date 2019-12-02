package com.kms.katalon.dal.fileservice.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.FileServiceConstant;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.global.GlobalVariableEntity;
import com.kms.katalon.entity.project.ProjectEntity;

public class GlobalVariableFileServiceManager {

    public static List<GlobalVariableEntity> getAll(String projectPk) throws Exception {
        if (projectPk != null) {
            ExecutionProfileEntity wrapper = getWrapper(projectPk);
            if (wrapper != null) {
                return wrapper.getGlobalVariableEntities();
            }
        }
        return Collections.emptyList();
    }

    public static GlobalVariableEntity addNewVariable(String newName, String value, String projectPk) throws Exception {
        ExecutionProfileEntity wrapper = getWrapper(projectPk);
        for (GlobalVariableEntity variable : wrapper.getGlobalVariableEntities()) {
            if (variable.getName().equals(newName)) {
                return null;
            }
        }

        GlobalVariableEntity newVariable = new GlobalVariableEntity(newName, value);
        wrapper.getGlobalVariableEntities().add(newVariable);

        save(wrapper, projectPk);
        return newVariable;
    }

    private static ExecutionProfileEntity getWrapper(String projectPk) throws Exception {
        if (projectPk != null) {
            ProjectEntity project = ProjectFileServiceManager.getProject(projectPk);
            File globalVariableFile = new File(
                    FileServiceConstant.getLegacyGlobalVariableFileLocation(project.getFolderLocation()));
            ExecutionProfileEntity wrapper = null;
            if (globalVariableFile.exists()) {
                wrapper = (ExecutionProfileEntity) EntityService.getInstance()
                        .getEntityByPath(globalVariableFile.getAbsolutePath());

            } else {
                wrapper = new ExecutionProfileEntity();
                save(wrapper, projectPk);
            }
            return wrapper;
        }
        return null;
    }

    public static List<GlobalVariableEntity> updateVariables(List<GlobalVariableEntity> listVariable, String projectPk)
            throws Exception {
        ExecutionProfileEntity wrapper = getWrapper(projectPk);
        wrapper.setGlobalVariableEntities(listVariable);

        save(wrapper, projectPk);
        return wrapper.getGlobalVariableEntities();
    }

    private static void save(ExecutionProfileEntity executionProfile, String projectPk) throws Exception {
        ProjectEntity project = ProjectFileServiceManager.getProject(projectPk);
        EntityService.getInstance().saveEntity(executionProfile,
                FileServiceConstant.getLegacyGlobalVariableFileLocation(project.getFolderLocation()));
    }

    /**
     * Get the execution profile by the given <code>name</code> and <code>parent folder</code>
     * 
     * @param name
     * @param project
     * @return
     * @throws DALException
     */
    public static ExecutionProfileEntity get(String name, ProjectEntity project) throws DALException {
        try {
            FolderEntity parentFolder = FolderFileServiceManager.getProfileRoot(project);
            String path = new File(parentFolder.getLocation(),
                    name + ExecutionProfileEntity.getGlobalVariableFileExtension()).getAbsolutePath();
            ExecutionProfileEntity executionProfile = (ExecutionProfileEntity) EntityService.getInstance()
                    .getEntityByPath(path);
            if (executionProfile == null) {
                return null;
            }
            executionProfile.setProject(parentFolder.getProject());
            executionProfile.setParentFolder(parentFolder);
            return executionProfile;
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    public static ExecutionProfileEntity getById(String id, ProjectEntity project) throws DALException {
        try {
            FolderEntity parentFolder = FolderFileServiceManager.getProfileRoot(project);
            String name = id.replace(parentFolder.getId(), "").replace(File.pathSeparator, "").replace(
                    ExecutionProfileEntity.getGlobalVariableFileExtension(), "");
            return get(name, project);
        } catch (Exception e) {
            throw new DALException(e);
        }
    }

    public static ExecutionProfileEntity newProfile(String newName, boolean defaultProfile, ProjectEntity project)
            throws DALException {
        return newProfile(newName, defaultProfile, Collections.emptyList(), project);
    }

    public static ExecutionProfileEntity newProfile(String newName, boolean defaultProfile,
            List<GlobalVariableEntity> variables, ProjectEntity project) throws DALException {
        ExecutionProfileEntity newProfile = new ExecutionProfileEntity();
        newProfile.setName(newName);
        newProfile.setParentFolder(FolderFileServiceManager.getProfileRoot(project));
        newProfile.setProject(project);
        newProfile.setDefaultProfile(defaultProfile);
        newProfile.setGlobalVariableEntities(new ArrayList<>(variables));
        try {
            EntityService.getInstance().saveEntity(newProfile);
        } catch (Exception e) {
            throw new DALException(e);
        }
        return newProfile;
    }

    public static List<ExecutionProfileEntity> getAll(ProjectEntity project) throws DALException {
        FolderEntity profileFolder = FolderFileServiceManager.getProfileRoot(project);
        File folder = new File(profileFolder.getLocation());
        if (!folder.exists()) {
            return Collections.emptyList();
        }
        File[] files = folder.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(files)
                .filter(f -> f.getName().endsWith(ExecutionProfileEntity.getGlobalVariableFileExtension()))
                .map(f -> {
                    try {
                        return get(FilenameUtils.getBaseName(f.getName()), project);
                    } catch (DALException e) {
                        return null;
                    }
                })
                .filter(p -> p != null)
                .collect(Collectors.toList());

    }
}
