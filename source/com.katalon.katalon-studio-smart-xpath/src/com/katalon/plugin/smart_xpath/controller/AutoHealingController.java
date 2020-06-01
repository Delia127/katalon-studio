package com.katalon.plugin.smart_xpath.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.katalon.platform.api.model.Entity;
import com.katalon.platform.api.service.ApplicationManager;
import com.katalon.plugin.smart_xpath.constant.SmartXPathConstants;
import com.katalon.plugin.smart_xpath.entity.BrokenTestObject;
import com.katalon.plugin.smart_xpath.entity.BrokenTestObjects;
import com.katalon.plugin.smart_xpath.logger.LoggerSingleton;
import com.katalon.plugin.smart_xpath.util.StringUtils;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebElementSelectorMethod;

public class AutoHealingController {

    private static IEventBroker eventBroker;
    
    public static void setEventBroker(IEventBroker eventBroker) {
        AutoHealingController.eventBroker = eventBroker;
    }
    
	public static Set<BrokenTestObject> autoHealBrokenTestObjects(Shell shell, Set<BrokenTestObject> brokenTestObjects) {
		final Set<BrokenTestObject> approvedButCannotBeHealedEntities = new HashSet<>();
		approvedButCannotBeHealedEntities.clear();
		try {
			new ProgressMonitorDialog(shell).run(true, false, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("Healing broken Test Objects ... ", brokenTestObjects.size());

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

            WebElementSelectorMethod newSelectorMethod = WebElementSelectorMethod.valueOf(
                    brokenTestObject.getProposedLocatorMethod().name());
            testObject.setSelectorMethod(newSelectorMethod);
            testObject.setSelectorValue(
                    newSelectorMethod,
                    brokenTestObject.getProposedLocator());

            ObjectRepositoryController.getInstance().updateTestObject(testObject);

            eventBroker.post(EventConstants.TEST_OBJECT_UPDATED, new Object[] { testObjectId, testObject });
        } catch (ControllerException exception) {
            LoggerSingleton.logError(exception);
        }
    }

	public static Set<BrokenTestObject> readUnapprovedBrokenTestObjects() {
		try {
			Gson gson = new Gson();
			Entity projectEntity = ApplicationManager.getInstance().getProjectManager().getCurrentProject();
			if (projectEntity != null) {
				String projectDir = projectEntity.getFolderLocation();
				String jsonAutoHealingDir = StringUtils
						.getStandardPath(projectDir + SmartXPathConstants.WAITING_FOR_APPROVAL_FILE_SUFFIX);
				JsonReader reader = new JsonReader(
						new InputStreamReader(new FileInputStream(jsonAutoHealingDir), StandardCharsets.UTF_8));
				BrokenTestObjects brokenTestObjects = gson.fromJson(reader, BrokenTestObjects.class);
				Set<BrokenTestObject> unapprovedBrokenTestObjects = brokenTestObjects.getBrokenTestObjects();
				// Remove potential threats
				unapprovedBrokenTestObjects.removeAll(Collections.singleton(null));
				return unapprovedBrokenTestObjects;
			} else {
				System.out.println("Current project directory is not detected, no project is open");
			}
		} catch (FileNotFoundException e) {
			System.out.println(SmartXPathConstants.WAITING_FOR_APPROVAL_FILE_SUFFIX
					+ "is not detected, no broken test objects are loaded");
			e.printStackTrace(System.out);
		}
		return null;
	}

	/**
	 * Set content of the file to a BrokenTestObjects entity which consists of a
	 * list of BrokenTestObjects
	 */
	public static void writeToFilesWithBrokenObjects(Set<BrokenTestObject> brokenTestObjectsToUpdate, String filePath) {
		try {
			BrokenTestObjects brokenTestObjects = new BrokenTestObjects();
			brokenTestObjects.setBrokenTestObjects(brokenTestObjectsToUpdate);
			File file = new File(filePath);
			if (file.exists()) {
				ObjectMapper mapper = new ObjectMapper();
				mapper.enable(SerializationFeature.INDENT_OUTPUT);
				brokenTestObjectsToUpdate.stream().forEach(a -> System.out.println(a.getApproved()));
				mapper.writeValue(file, brokenTestObjects);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace(System.out);
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}

	private static File createSmartXPathFile(Entity projectEntity, String fileName) {
		try {
			String smartXPathDir = StringUtils
					.getStandardPath(projectEntity.getFolderLocation() + SmartXPathConstants.SMART_XPATH_FOLDER_SUFFIX);
			boolean smartXPathFolderExists = new File(smartXPathDir).isDirectory();
			boolean createdSmartXPathFolder = new File(smartXPathDir).mkdirs();
			boolean createdAutoHealingJsonFile = false;
			if (createdSmartXPathFolder || smartXPathFolderExists) {
				String fnAutoHealing = StringUtils.getStandardPath(smartXPathDir + "/" + fileName + ".json");
				File autoHealingFile = new File(fnAutoHealing);
				createdAutoHealingJsonFile = autoHealingFile.createNewFile();
				if (createdAutoHealingJsonFile) {
					BrokenTestObjects emptyBrokenTestObjects = new BrokenTestObjects();
					ObjectMapper mapper = new ObjectMapper();
					mapper.enable(SerializationFeature.INDENT_OUTPUT);
					mapper.writeValue(autoHealingFile, emptyBrokenTestObjects);
					return autoHealingFile;
				}
			} else {
				System.out.println("/" + SmartXPathConstants.SMART_XPATH_FOLDER_SUFFIX
						+ " folder does not exist, no file is created");
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return null;
	}

	public static void writeBrokenTestObjects(BrokenTestObjects brokenTestObjects, String filePath) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			File file = new File(filePath);
			if (file.exists()) {
				mapper.enable(SerializationFeature.INDENT_OUTPUT);
				mapper.writeValue(file, brokenTestObjects);
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public static BrokenTestObjects readExistingBrokenTestObjects(String filePath) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			File file = new File(filePath);
			if (file.exists()) {
				return mapper.readValue(file, BrokenTestObjects.class);
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return null;
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
		createSmartXPathFile(projectEntity, "waiting-for-approval");
		createSmartXPathFile(projectEntity, "approved");
	}
}
