package com.kms.katalon.composer.testcase.handlers;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.preferences.TestCaseSettingStore;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.EntityTagController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class CollectTestCaseTagHandler {
    
    @Inject
    private IEventBroker eventBroker;
    
    private EntityTagController tagController;

    @PostConstruct
    public void initialize() {
        tagController = EntityTagController.getInstance();
        registerEventListeners();
    }
    
    private void registerEventListeners() {
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, new EventServiceAdapter() {
            @Override
            public void handleEvent(Event event) {
                try {
                    ProjectEntity project = getCurrentProject();
                    Set<String> allTagsInProject = tagController.collectTagsFromAllTestCases(project);
                    getStore(project).saveTestCaseTags(allTagsInProject);
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }
            }
        });
        
        eventBroker.subscribe(EventConstants.TESTCASE_UPDATED, new EventServiceAdapter() {
            @Override
            public void handleEvent(Event event) {
                try {
                    ProjectEntity project = getCurrentProject();
                    TestCaseSettingStore store = getStore(project);
                    TestCaseEntity updatedTestCase = (TestCaseEntity) getObjects(event)[1];
                    Set<String> allTagsInProject = store.getTestCaseTags();
                    Set<String> updatedTestCaseTags = tagController.collectTestCaseTags(updatedTestCase);
                    allTagsInProject.addAll(updatedTestCaseTags);
                    store.saveTestCaseTags(allTagsInProject);
                } catch (IOException | GeneralSecurityException e) {
                    LoggerSingleton.logError(e);
                }
            }
        });
    }
    
    private TestCaseSettingStore getStore(ProjectEntity project) {
        TestCaseSettingStore store = new TestCaseSettingStore(project.getFolderLocation());
        return store;
    }
    
    private ProjectEntity getCurrentProject() {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        return project;
    }
}
