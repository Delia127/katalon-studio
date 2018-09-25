package com.kms.katalon.composer.components.impl.command;

import static org.apache.commons.lang.StringUtils.startsWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.impl.constants.ComposerComponentsImplMessageConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.project.ProjectEntity;

public class KatalonCommands {

    private static final String MORE_HELP = ComposerComponentsImplMessageConstants.COMMAND_NAME_MORE_HELP;

    private static final String KATALON_PREFERENCES = ComposerComponentsImplMessageConstants.COMMAND_NAME_KATALON_PREFERENCES;

    private static final String KATALON_QUESTIONS_AND_ANSWERS = ComposerComponentsImplMessageConstants.COMMAND_NAME_KATALON_QUESTIONS_AND_ANSWERS;

    private static final String KATALON_DOCUMENT = ComposerComponentsImplMessageConstants.COMMAND_NAME_KATALON_DOCUMENT;

    private static final String KATALON_WEBSITE = ComposerComponentsImplMessageConstants.COMMAND_NAME_KATALON_WEBSITE;

    private static final String KATALON_QUICK_GUIDE = ComposerComponentsImplMessageConstants.COMMAND_NAME_KATALON_QUICK_GUIDE;

    private static final String KATALON_HELP = ComposerComponentsImplMessageConstants.COMMAND_NAME_KATALON_HELP;

    private static final String KATALON_COMMAND = ComposerComponentsImplMessageConstants.COMMAND_NAME_KATALON_COMMAND;

    private static final String PROJECT_SETTINGS = ComposerComponentsImplMessageConstants.COMMAND_NAME_PROJECT_SETTINGS;

    private static final String COMMAND_NAME_ADD_TEST_CASE_STEP = ComposerComponentsImplMessageConstants.COMMAND_NAME_ADD_TEST_CASE_STEP;

    private static final String COMMAND_NAME_NEW = ComposerComponentsImplMessageConstants.COMMAND_NAME_NEW;

    private static final String COMMAND_NAME_OPEN = ComposerComponentsImplMessageConstants.COMMAND_NAME_OPEN;
    
    private static final String COMMAND_NAME_IMPORT = ComposerComponentsImplMessageConstants.COMMAND_NAME_IMPORT;
    
    private static final String COMMAND_NAME_EXPORT = ComposerComponentsImplMessageConstants.COMMAND_NAME_EXPORT;

    private static final String COMMAND_NAME_SPY_WEB_OBJECT = ComposerComponentsImplMessageConstants.COMMAND_NAME_SPY_WEB_OBJECT;

    private static final String COMMAND_NAME_SPY_MOBILE_OBJECT = ComposerComponentsImplMessageConstants.COMMAND_NAME_SPY_MOBILE_OBJECT;

    private static final String COMMAND_NAME_RECORD = ComposerComponentsImplMessageConstants.COMMAND_NAME_RECORD;

    private static final String COMMAND_NAME_GENERATE_COMMAND = ComposerComponentsImplMessageConstants.COMMAND_NAME_GENERATE_COMMAND;

    private static final String CHECKPOINT = GlobalStringConstants.CHECKPOINT;

    private static final String TEST_DATA = GlobalStringConstants.TEST_DATA;

    private static final String TEST_OBJECT = GlobalStringConstants.TEST_OBJECT;

    private static final String WEBSERVICE_OBJECT = ComposerComponentsImplMessageConstants.COMMAND_NAME_WEBSERVICE_OBJECT;
    
    private static final String WEBSERVICE_OBJECTS_FROM_SWAGGER = ComposerComponentsImplMessageConstants.COMMAND_NAME_WEBSERVICE_OBJECTS_FROM_SWAGGER;
    
    private static final String WEBSERVICE_OBJECTS_FROM_WSDL = ComposerComponentsImplMessageConstants.COMMAND_NAME_WEBSERVICE_OBJECTS_FROM_WSDL;
    
    private static final String TEST_CASE = GlobalStringConstants.TEST_CASE;

    private static final String TEST_SUITE = GlobalStringConstants.TEST_SUITE;

    private static final String TEST_SUITE_COLLECTION = ComposerComponentsImplMessageConstants.COMMAND_NAME_TEST_SUITE_COLLECTION;

    private static final String KEYWORD = GlobalStringConstants.KEYWORD;

    private static final String PACKAGE = GlobalStringConstants.PACKAGE;
    
    private static final String FOLDER = GlobalStringConstants.FOLDER;

    private static final String GIT = GlobalStringConstants.GIT;

    private static final String JAR = GlobalStringConstants.JAR;

    private static final String KATALON_QA_URL = ComposerComponentsImplMessageConstants.KATALON_QA_URL;

    private static final String KATALON_WEBSITE_URL = ComposerComponentsImplMessageConstants.KATALON_WEBSITE_URL;

    private static final String KATALON_DOCUMENT_URL = ComposerComponentsImplMessageConstants.KATALON_DOCUMENT_URL;

    private KCommand rootCommand;

    private MApplication application;

    private EModelService modelService;

    private static KatalonCommands instance;

    private ProjectEntity project;

    private KCommand addTestCaseStepCommand;

    private KCommand recordCommand;

    public static KatalonCommands getInstance() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (instance == null || instance.getProject() != currentProject) {
            instance = new KatalonCommands(currentProject);
        }

        instance.initialContextCommands();
        return instance;
    }

    private KatalonCommands(ProjectEntity project) {
        this.project = project;
        application = ApplicationSingleton.getInstance().getApplication();
        modelService = ModelServiceSingleton.getInstance().getModelService();

        rootCommand = KCommand.create(KATALON_COMMAND);

        initialGlobalCommands();

        initialOnlineHelpCommands();

        initialProjectCommands();
    }

    private void initialGlobalCommands() {
        rootCommand.addChild(KCommand.create(KATALON_HELP).setEventName(EventConstants.KATALON_HELP));
        rootCommand.addChild(KCommand.create(KATALON_QUICK_GUIDE).setEventName(EventConstants.KATALON_QUICK_GUIDE));
        rootCommand.addChild(KCommand.create(KATALON_WEBSITE)
                .setEventName(EventConstants.KATALON_OPEN_URL)
                .setEventData(KATALON_WEBSITE_URL));
        rootCommand.addChild(KCommand.create(KATALON_DOCUMENT)
                .setEventName(EventConstants.KATALON_OPEN_URL)
                .setEventData(KATALON_DOCUMENT_URL));
        rootCommand.addChild(KCommand.create(KATALON_QUESTIONS_AND_ANSWERS)
                .setEventName(EventConstants.KATALON_OPEN_URL)
                .setEventData(KATALON_QA_URL));
        rootCommand.addChild(KCommand.create(KATALON_PREFERENCES).setEventName(EventConstants.KATALON_PREFERENCES));
    }

    private void initialOnlineHelpCommands() {
        // More Help
        KCommand moreHelpCommand = KCommand.create(MORE_HELP);
        try {
            // Get cached JSON content
            String data = PlatformUI.getPreferenceStore().getString(PreferenceConstants.GENERAL_ONLINE_HELP_CONTENT);
            if (StringUtils.isNotBlank(data)) {
                KCommand onlineHelpCommand = JsonUtil.fromJson(data, KCommand.class);
                if (onlineHelpCommand != null) {
                    moreHelpCommand = onlineHelpCommand;
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }

        if (!moreHelpCommand.hasChildren()) {
            moreHelpCommand.setEventName(EventConstants.KATALON_LOAD_COMMANDS)
                    .setEventData(EventConstants.KATALON_UPDATE_HELP_CONTENTS);
        }

        rootCommand.addChild(moreHelpCommand);
    }

    private void initialProjectCommands() {
        if (project == null) {
            return;
        }

        // Open
        rootCommand.addChild(KCommand.create(COMMAND_NAME_OPEN + TEST_CASE)
                .setEventName(EventConstants.KATALON_LOAD_COMMANDS)
                .setEventData(EventConstants.TESTCASE_OPEN));
        rootCommand.addChild(KCommand.create(COMMAND_NAME_OPEN + TEST_SUITE)
                .setEventName(EventConstants.KATALON_LOAD_COMMANDS)
                .setEventData(EventConstants.TEST_SUITE_OPEN));
        rootCommand.addChild(KCommand.create(COMMAND_NAME_OPEN + TEST_OBJECT)
                .setEventName(EventConstants.KATALON_LOAD_COMMANDS)
                .setEventData(EventConstants.TEST_OBJECT_OPEN));
        rootCommand.addChild(KCommand.create(COMMAND_NAME_OPEN + TEST_DATA)
                .setEventName(EventConstants.KATALON_LOAD_COMMANDS)
                .setEventData(EventConstants.TEST_DATA_OPEN));
        rootCommand.addChild(KCommand.create(COMMAND_NAME_OPEN + CHECKPOINT)
                .setEventName(EventConstants.KATALON_LOAD_COMMANDS)
                .setEventData(EventConstants.CHECKPOINT_OPEN));

        // New
        rootCommand.addChild(KCommand.create(COMMAND_NAME_NEW + TEST_CASE).setEventName(EventConstants.TESTCASE_NEW));
        rootCommand
                .addChild(KCommand.create(COMMAND_NAME_NEW + TEST_SUITE).setEventName(EventConstants.TEST_SUITE_NEW));
        rootCommand.addChild(KCommand.create(COMMAND_NAME_NEW + TEST_SUITE_COLLECTION)
                .setEventName(EventConstants.TEST_SUITE_COLLECTION_NEW));
        rootCommand
                .addChild(KCommand.create(COMMAND_NAME_NEW + TEST_OBJECT).setEventName(EventConstants.TEST_OBJECT_NEW));
        rootCommand.addChild(KCommand.create(COMMAND_NAME_NEW + WEBSERVICE_OBJECT)
                .setEventName(EventConstants.WEBSERVICE_REQUEST_OBJECT_NEW));
        
        rootCommand.addChild(KCommand.create(COMMAND_NAME_NEW + WEBSERVICE_OBJECTS_FROM_SWAGGER)
                .setEventName(EventConstants.IMPORT_WEB_SERVICE_OBJECTS_FROM_SWAGGER));
        
        rootCommand.addChild(KCommand.create(COMMAND_NAME_NEW + WEBSERVICE_OBJECTS_FROM_WSDL)
                .setEventName(EventConstants.IMPORT_WEB_SERVICE_OBJECTS_FROM_WSDL));
        
        
        rootCommand.addChild(KCommand.create(COMMAND_NAME_NEW + TEST_DATA).setEventName(EventConstants.TEST_DATA_NEW));
        rootCommand
                .addChild(KCommand.create(COMMAND_NAME_NEW + CHECKPOINT).setEventName(EventConstants.CHECKPOINT_NEW));
        rootCommand.addChild(KCommand.create(COMMAND_NAME_NEW + KEYWORD).setEventName(EventConstants.KEYWORD_NEW));
        rootCommand.addChild(KCommand.create(COMMAND_NAME_NEW + PACKAGE).setEventName(EventConstants.PACKAGE_NEW));
        rootCommand.addChild(KCommand.create(COMMAND_NAME_EXPORT + FOLDER)).setEventName(EventConstants.FOLDER_EXPORT);
        rootCommand.addChild(KCommand.create(COMMAND_NAME_IMPORT + FOLDER)).setEventName(EventConstants.FOLDER_IMPORT);

        // Project Settings
        rootCommand.addChild(KCommand.create(PROJECT_SETTINGS).setEventName(EventConstants.PROJECT_SETTINGS));

        // Spy Web Object
        rootCommand.addChild(KCommand.create(COMMAND_NAME_SPY_WEB_OBJECT).setEventName(EventConstants.OBJECT_SPY_WEB));

        // Spy Mobile Object
        rootCommand.addChild(
                KCommand.create(COMMAND_NAME_SPY_MOBILE_OBJECT).setEventName(EventConstants.OBJECT_SPY_MOBILE));

        // Generate Command
        rootCommand.addChild(
                KCommand.create(COMMAND_NAME_GENERATE_COMMAND).setEventName(EventConstants.KATALON_GENERATE_COMMAND));
    }

    @SuppressWarnings("unchecked")
    private void initialContextCommands() {
        if (addTestCaseStepCommand != null && recordCommand != null) {
            List<KCommand> childrenCommands = rootCommand.getChildren();
            childrenCommands.remove(addTestCaseStepCommand);
            childrenCommands.remove(recordCommand);
        }

        // Add related commands for Test Case
        if (!isTestCasePartSelected()) {
            return;
        }

        // Get initial data (com.kms.katalon.composer.testcase.addons.TestCaseManualCommandAddon)
        Object testcaseStepCommands = application.getContext().get(EventConstants.TESTCASE_ADD_STEP);
        if (testcaseStepCommands == null) {
            return;
        }

        if (addTestCaseStepCommand == null) {
            addTestCaseStepCommand = KCommand.create(COMMAND_NAME_ADD_TEST_CASE_STEP)
                    .setChildren(new ArrayList<>((List<KCommand>) testcaseStepCommands));
        }

        if (recordCommand == null) {
            recordCommand = KCommand.create(COMMAND_NAME_RECORD).setEventName(EventConstants.KATALON_RECORD);
        }

        // Add Test Case Step
        rootCommand.addChild(addTestCaseStepCommand);

        // Record
        rootCommand.addChild(recordCommand);
    }

    private boolean isTestCasePartSelected() {
        return startsWith(getSelectedPartIdInComposerContentArea(),
                IdConstants.TEST_CASE_PARENT_COMPOSITE_PART_ID_PREFIX);
    }

    public String getSelectedPartIdInComposerContentArea() {
        MPartStack composerStack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID,
                application);
        if (composerStack == null || !composerStack.isVisible() || composerStack.getSelectedElement() == null) {
            return null;
        }

        return ((MPart) composerStack.getSelectedElement()).getElementId();
    }

    public static List<KCommand> createCommands(List<String> commandNames, String eventName) {
        if (commandNames == null || commandNames.isEmpty()) {
            return Collections.emptyList();
        }
        return commandNames.stream()
                .filter(name -> StringUtils.isNotEmpty(name))
                .map(name -> KCommand.create(name).setEventName(eventName))
                .collect(Collectors.toList());
    }

    public KCommand getRootCommand() {
        return rootCommand;
    }

    private ProjectEntity getProject() {
        return project;
    }
}
