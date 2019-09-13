package com.kms.katalon.dal.fileservice.manager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.FileServiceConstant;
import com.kms.katalon.dal.fileservice.constants.StringConstants;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.SourceFolderConfiguration;
import com.kms.katalon.entity.project.SystemFolderConfiguration;
import com.kms.katalon.entity.util.Util;
import com.kms.katalon.groovy.util.GroovyUtil;
import com.kms.katalon.util.VersionUtil;

public class ProjectFileServiceManager {

    private static final String MIGRATE_LEGACY_GLOBALVARIABLE_VS = "5.4.0";

    private static final String MIGRATE_SOURCE_CONTENT_VS = "5.7.0";

    private static final String MIGRATE_INTERNAL_LOGGING_VS = "5.9.0";

    public static ProjectEntity addNewProject(String name, String description, short pageLoadTimeout,
            String projectLocation) throws Exception {

        // remove the "\\" post-fix
        if (projectLocation.endsWith(File.separator)) {
            projectLocation = projectLocation.substring(0, projectLocation.length() - 1);
        }
        File projectFolder = new File(projectLocation + File.separator + name);
        if (!projectFolder.exists()) {
            projectFolder.mkdirs();
        }

        ProjectEntity project = newProjectEntity(name, description, projectLocation, false);
        FolderFileServiceManager.initRootEntityFolders(project);
        createSettingFolder(project);

        GlobalVariableFileServiceManager.newProfile(ExecutionProfileEntity.DF_PROFILE_NAME, true,
                Collections.emptyList(), project);

        migrateNewIncludeFolder(project);
        migrateNewConfigFolder(project);
        return project;
    }

    public static ProjectEntity getProject(String projectFileLocation) throws Exception {
        File projectFile = new File(projectFileLocation);
        if (projectFile.isFile() && projectFile.exists()) {
            ProjectEntity project = (ProjectEntity) EntityService.getInstance().getEntityByPath(projectFileLocation);
            project.setFolderLocation(projectFile.getParent());
            createSettingFolder(project);
            return project;
        }
        return null;
    }

    public static ProjectEntity openProjectWithoutClasspath(String projectFileLocation) throws Exception {
        File projectFile = new File(projectFileLocation);
        if (projectFile.isFile() && projectFile.exists()) {
            ProjectEntity project = (ProjectEntity) EntityService.getInstance().getEntityByPath(projectFileLocation);
            project.setFolderLocation(projectFile.getParent());
            project.setProjectFileLocation(projectFile.getAbsolutePath());
            createSettingFolder(project);
            FolderFileServiceManager.initRootEntityFolders(project);

            String migratedVersion = project.getMigratedVersion();
            if (StringUtils.isEmpty(migratedVersion)
                    || VersionUtil.isNewer(MIGRATE_LEGACY_GLOBALVARIABLE_VS, migratedVersion)) {
                migrateLegacyGlobalVariable(project);
                project.setMigratedVersion(MIGRATE_LEGACY_GLOBALVARIABLE_VS);

                EntityService.getInstance().saveEntity(project);
            }

            if (StringUtils.isEmpty(migratedVersion)
                    || VersionUtil.isNewer(MIGRATE_SOURCE_CONTENT_VS, migratedVersion)) {
                migrateNewIncludeFolder(project);
            }

            if (StringUtils.isEmpty(migratedVersion)
                    || VersionUtil.isNewer(MIGRATE_INTERNAL_LOGGING_VS, migratedVersion)) {
                migrateNewConfigFolder(project);
            }

            if (GlobalVariableFileServiceManager.getAll(project).isEmpty()) {
                GlobalVariableFileServiceManager.newProfile(ExecutionProfileEntity.DF_PROFILE_NAME, true,
                        Collections.emptyList(), project);
            }

            return project;
        }
        return null;
    }

    private static void migrateNewIncludeFolder(ProjectEntity project) throws Exception {
        project.getSourceContent()
                .addSourceFolder(new SourceFolderConfiguration(FileServiceConstant.GROOVY_SCRIPTS_INCLUDE_FOLDER));

        project.getSourceContent()
                .addSystemFolder(new SystemFolderConfiguration(FileServiceConstant.GROOVY_SCRIPTS_INCLUDE_FOLDER));
        project.getSourceContent()
                .addSystemFolder(new SystemFolderConfiguration(FileServiceConstant.FEATURES_INCLUDE_FOLDER));

        project.setMigratedVersion(MIGRATE_SOURCE_CONTENT_VS);

        EntityService.getInstance().saveEntity(project);
    }

    private static void migrateNewConfigFolder(ProjectEntity project) throws Exception {
        project.getSourceContent()
                .addSystemFolder(new SystemFolderConfiguration(FileServiceConstant.CONFIG_INCLUDE_FOLDER));

        String projectLocation = project.getFolderLocation();
        File configFolder = new File(FileServiceConstant.getConfigFolderLocation(projectLocation));
        createDefaultLogConfigFile(configFolder);

        project.setMigratedVersion(MIGRATE_INTERNAL_LOGGING_VS);
        EntityService.getInstance().saveEntity(project);
    }

    private static void createDefaultLogConfigFile(File configFolder) {
        if (configFolder == null || !configFolder.exists()) {
            return;
        }
        try {
            File configFile = new File(configFolder, "log.properties");
            if (!configFile.exists()) {
                configFile.createNewFile();
                Bundle bundle = FrameworkUtil.getBundle(ProjectFileServiceManager.class);
                Path templateFilePath = new Path("/res/config/log.properties");
                URL templateFileUrl = FileLocator.find(bundle, templateFilePath, null);
                FileUtils.copyURLToFile(FileLocator.toFileURL(templateFileUrl), configFile);
            }
        } catch (IOException ignored) {}
    }

    private static void migrateLegacyGlobalVariable(ProjectEntity project) throws Exception {
        ExecutionProfileEntity legacyGlobalVariable = (ExecutionProfileEntity) EntityService.getInstance()
                .getEntityByPath(FileServiceConstant.getLegacyGlobalVariableFileLocation(project.getFolderLocation()));
        if (legacyGlobalVariable == null) {
            return;
        }

        GlobalVariableFileServiceManager.newProfile(ExecutionProfileEntity.DF_PROFILE_NAME, true,
                legacyGlobalVariable.getGlobalVariableEntities(), project);
    }

    public static ProjectEntity updateProject(String name, String description, String projectFileLocation,
            short pageLoadTimeout) throws Exception {
        ProjectEntity project = getProject(projectFileLocation);

        IProject oldGroovyProject = GroovyUtil.getGroovyProject(project);

        project.setName(name);
        project.setDescription(description);
        project.setPageLoadTimeout(pageLoadTimeout);

        // name changed
        if (!project.getLocation().equals(projectFileLocation)) {
            EntityService.getInstance().getEntityCache().remove(project, true);
            try {
                GroovyUtil.updateGroovyProject(project, oldGroovyProject);
            } catch (CoreException ex) {
                throw new DALException(StringConstants.MNG_EXC_FAILED_TO_UPDATE_PROJ);
            }
        }

        EntityService.getInstance().saveEntity(project);

        return project;
    }

    public static boolean isDuplicationProjectName(String name, String projectFolderLocation) throws Exception {
        EntityService.getInstance().validateName(name);
        return (getProject(projectFolderLocation + File.separator + name + File.separator + name
                + ProjectEntity.getProjectFileExtension()) != null);
    }

    private static void createSettingFolder(ProjectEntity project) throws IOException {
        File settingFolder = new File(project.getFolderLocation() + File.separator + FileServiceConstant.SETTING_DIR);
        if (!settingFolder.exists()) {
            settingFolder.mkdir();
        }

        File externalSettingFolder = new File(
                project.getFolderLocation() + File.separator + FileServiceConstant.EXTERNAL_SETTING_DIR);
        if (!externalSettingFolder.exists()) {
            externalSettingFolder.mkdir();
        }

        File internalSettingFolder = new File(
                project.getFolderLocation() + File.separator + FileServiceConstant.INTERNAL_SETTING_DIR);
        if (!internalSettingFolder.exists()) {
            internalSettingFolder.mkdir();
        }
    }

    public static ProjectEntity newProjectEntity(String name, String description, String projectLocation,
            boolean legacy) throws DALException {
        // remove the "\\" post-fix
        if (projectLocation.endsWith(File.separator)) {
            projectLocation = projectLocation.substring(0, projectLocation.length() - 1);
        }
        File projectFolder = new File(projectLocation + File.separator + name);
        if (!projectFolder.exists()) {
            projectFolder.mkdirs();
        }

        ProjectEntity project = new ProjectEntity();
        project.setUUID(Util.generateGuid());
        project.setFolderLocation(projectFolder.getAbsolutePath());
        project.setName(name);
        project.setDescription(description);
        if (!legacy) {
            project.setMigratedVersion(MIGRATE_LEGACY_GLOBALVARIABLE_VS);
        }

        try {
            EntityService.getInstance().saveEntity(project);
        } catch (Exception e) {
            throw new DALException(e);
        }

        return project;
    }
}
