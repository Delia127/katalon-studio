package com.kms.katalon.platform.internal.service.impl;

import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.katalon.platform.api.PlatformException;
import com.katalon.platform.api.model.Folder;
import com.katalon.platform.api.model.TestCase;
import com.katalon.platform.api.service.TestCaseManager;
import com.katalon.platform.api.util.ExceptionUtil;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.util.groovy.GroovyGuiUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class TestCaseManagerImpl implements TestCaseManager {

    private IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
    
    private TestCaseController testCaseController = TestCaseController.getInstance();
    
    private FolderController folderController = FolderController.getInstance();
    
    @Override
    public TestCase newTestCase(TestCase testCaseInfo) throws PlatformException {
        try {
            String folderLocation = testCaseInfo.getFolderLocation();
            FolderEntity parentFolder = getFolderEntity(folderLocation);
            
            String newTestCaseName = testCaseController.getAvailableTestCaseName(parentFolder, testCaseInfo.getName());
            TestCaseEntity testCase = testCaseController.newTestCaseWithoutSave(parentFolder, newTestCaseName);
            testCase.setDescription(StringUtils.defaultString(testCaseInfo.getDescription()));
            testCase.setComment(StringUtils.defaultString(testCaseInfo.getComment()));
            InputStream scriptContent = testCaseInfo.getScriptContent();
            if (scriptContent != null) {
                GroovyGuiUtil.addContentToTestCase(testCase, scriptContent);
            }
        
            UISynchronizeService.syncExec(() -> {
                if (parentFolder != null && testCase != null) {
                    try {
                        FolderTreeEntity parentFolderTreeEntity = getFolderTreeEntity(parentFolder);
                        eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentFolderTreeEntity);
                        eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM,
                                new TestCaseTreeEntity(testCase, parentFolderTreeEntity));
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
                    }
                }
            });
            
        } catch (Exception e) {
            ExceptionUtil.wrapAndThrow(e);
        }
        return testCaseInfo;
    }
    
    @Override
    public String getAvailableTestCaseName(Folder folder, String name) throws PlatformException {
        String testCaseName = null;
        try {
            FolderEntity folderEntity = getFolderEntity(folder);
            testCaseName = testCaseController.getAvailableTestCaseName(folderEntity, name);
        } catch (Exception e) {
            ExceptionUtil.wrapAndThrow(e);
        }
        return testCaseName;
    }
    
    private FolderEntity getFolderEntity(Folder folder) throws Exception {
        String folderLocation = folder.getFolderLocation();
        return getFolderEntity(folderLocation);
    }
    
    private FolderEntity getFolderEntity(String location) throws Exception {
        if (!StringUtils.isBlank(location)) {
            return null;
        } else {
            return folderController.getFolder(location);
        }
    }
    
    private FolderTreeEntity getFolderTreeEntity(FolderEntity folder) throws Exception {
        ProjectEntity project = ProjectController.getInstance().getCurrentProject();
        FolderEntity testCaseRoot = FolderController.getInstance().getTestCaseRoot(project);
        FolderTreeEntity folderTreeEntity = new FolderTreeEntity(folder, 
                TreeEntityUtil.createSelectedTreeEntityHierachy(folder.getParentFolder(), testCaseRoot));
        return folderTreeEntity;
    }
}
