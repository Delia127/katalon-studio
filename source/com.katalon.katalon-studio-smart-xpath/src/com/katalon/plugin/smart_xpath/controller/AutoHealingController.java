package com.katalon.plugin.smart_xpath.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.katalon.platform.api.model.Entity;
import com.katalon.plugin.smart_xpath.constant.SmartXPathConstants;
import com.katalon.plugin.smart_xpath.constant.SmartXPathMessageConstants;
import com.katalon.plugin.smart_xpath.entity.BrokenTestObject;
import com.katalon.plugin.smart_xpath.entity.BrokenTestObjects;
import com.katalon.plugin.smart_xpath.logger.LoggerSingleton;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementSelectorMethod;

public class AutoHealingController {

    public static String getDataFilePath(ProjectEntity project) {
        return getDataFilePath(project.getFolderLocation());
    }

    public static String getDataFilePath(Entity project) {
        return getDataFilePath(project.getFolderLocation());
    }

    public static String getDataFilePath(String projectDir) {
        if (StringUtils.isBlank(projectDir)) {
            return null;
        }
        String rawBrokenTestObjectsPath = FilenameUtils.concat(projectDir,
                SmartXPathConstants.SELF_HEALING_DATA_FILE_PATH);
        return FilenameUtils.separatorsToSystem(rawBrokenTestObjectsPath);
    }

    public static Set<BrokenTestObject> autoHealBrokenTestObjects(Shell shell,
            Set<BrokenTestObject> brokenTestObjects) {
        final Set<BrokenTestObject> approvedButCannotBeHealedEntities = new HashSet<>();
        approvedButCannotBeHealedEntities.clear();
        try {
            new ProgressMonitorDialog(shell).run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Healing broken test objects ... ", brokenTestObjects.size());

                    for (BrokenTestObject brokenTestObject : brokenTestObjects) {
                        healBrokenTestObject(brokenTestObject);
                        monitor.worked(1);
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        return approvedButCannotBeHealedEntities;
    }

    public static void healBrokenTestObject(BrokenTestObject brokenTestObject) {
        try {
            String testObjectId = brokenTestObject.getTestObjectId();
            WebElementEntity testObject = ObjectRepositoryController.getInstance()
                    .getWebElementByDisplayPk(testObjectId);

            WebElementSelectorMethod newSelectorMethod = WebElementSelectorMethod
                    .valueOf(brokenTestObject.getProposedLocatorMethod().name());
            testObject.setSelectorMethod(newSelectorMethod);
            testObject.setSelectorValue(newSelectorMethod, brokenTestObject.getProposedLocator());

            ObjectRepositoryController.getInstance().updateTestObject(testObject);

            IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
            eventBroker.post(EventConstants.TEST_OBJECT_UPDATED, new Object[] { testObjectId, testObject });
        } catch (ControllerException exception) {
            LoggerSingleton.logError(exception);
        }
    }

    public static Set<BrokenTestObject> readUnapprovedBrokenTestObjects(ProjectEntity project) {
        try {
            if (project == null) {
                return null;
            }

            String projectDir = project.getFolderLocation();
            File selfHealingFile = createBrokenTestObjectsFile(projectDir);
            JsonReader reader = new JsonReader(
                    new InputStreamReader(new FileInputStream(selfHealingFile), StandardCharsets.UTF_8));

            BrokenTestObjects brokenTestObjects = new Gson().fromJson(reader, BrokenTestObjects.class);
            if (brokenTestObjects == null) {
                return Collections.emptySet();
            }
            Set<BrokenTestObject> unapprovedBrokenTestObjects = brokenTestObjects.getBrokenTestObjects();
            unapprovedBrokenTestObjects.removeAll(Collections.singleton(null));

            return unapprovedBrokenTestObjects;
        } catch (FileNotFoundException e) {
            System.out.println(SmartXPathConstants.SELF_HEALING_DATA_FILE_PATH
                    + " is not detected, no broken test objects are loaded");
            e.printStackTrace(System.out);
        }
        return null;
    }

    private static File createBrokenTestObjectsFile(String projectDir) {
        String rawSelfHealingDir = FilenameUtils.concat(projectDir,
                SmartXPathConstants.SELF_HEALING_FOLDER_PATH);

        String selfHealingDir = FilenameUtils.separatorsToSystem(rawSelfHealingDir);

        File selfHealingDirectory = new File(selfHealingDir);
        if (!selfHealingDirectory.exists()) {
            boolean isCreateSelfHealingFolderSucceeded = selfHealingDirectory.mkdirs();
            if (!isCreateSelfHealingFolderSucceeded) {
                LoggerSingleton.logError(MessageFormat
                        .format(SmartXPathMessageConstants.MSG_CANNOT_CREATE_SELF_HEALING_FOLDER, selfHealingDir));
                return null;
            }
        }

        File autoHealingFile = new File(getDataFilePath(projectDir));

        if (!autoHealingFile.exists()) {
            try {
                if (autoHealingFile.createNewFile()) {
                    BrokenTestObjects emptyBrokenTestObjects = new BrokenTestObjects();
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.enable(SerializationFeature.INDENT_OUTPUT);
                    mapper.writeValue(autoHealingFile, emptyBrokenTestObjects);
                    return autoHealingFile;
                }
            } catch (IOException exception) {
                LoggerSingleton.logError(exception);
                return null;
            }
        }
        return autoHealingFile;
    }

    public static void writeBrokenTestObjects(BrokenTestObjects brokenTestObjects, ProjectEntity project) {
        if (project == null) {
            return;
        }
        try {
            createBrokenTestObjectsFile(project.getFolderLocation());
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(getDataFilePath(project));
            if (file.exists()) {
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                mapper.writeValue(file, brokenTestObjects);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    @SuppressWarnings("unused")
    private static boolean removeFile(File fileToRemove) {
        try {
            return Files.deleteIfExists(fileToRemove.toPath());
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        return false;
    }

    public static void createXPathFilesIfNecessary(Entity projectEntity) {
        if (projectEntity == null) {
            return;
        }
        String location = projectEntity.getFolderLocation();
        createBrokenTestObjectsFile(location);
    }
}
