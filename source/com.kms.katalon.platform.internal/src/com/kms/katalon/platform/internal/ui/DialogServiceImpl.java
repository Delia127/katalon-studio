package com.kms.katalon.platform.internal.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.widgets.Shell;

import com.katalon.platform.api.exception.PlatformException;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.model.FolderEntity;
import com.katalon.platform.api.model.TestCaseEntity;
import com.katalon.platform.api.model.TestObjectEntity;
import com.katalon.platform.api.ui.DialogActionService;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.explorer.providers.FolderEntityTreeViewerFilter;
import com.kms.katalon.composer.testcase.dialogs.TestCaseSelectionDialog;
import com.kms.katalon.composer.testcase.dialogs.TestObjectSelectionDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.platform.internal.entity.FolderEntityImpl;
import com.kms.katalon.platform.internal.entity.TestCaseEntityImpl;
import com.kms.katalon.platform.internal.entity.testobject.RestRequestEntityImpl;
import com.kms.katalon.platform.internal.entity.testobject.SoapRequestEntityImpl;
import com.kms.katalon.platform.internal.entity.testobject.WebElementEntityImpl;

public class DialogServiceImpl implements DialogActionService {

    @Override
    public FolderEntity showTestCaseFolderSelectionDialog(Shell parentShell, String dialogTitle)
            throws PlatformException {
        EntityLabelProvider labelProvider = new EntityLabelProvider();
        EntityProvider contentProvider = new EntityProvider();
        TreeEntitySelectionDialog selectionDialog = new TreeEntitySelectionDialog(parentShell, labelProvider,
                contentProvider, new FolderEntityTreeViewerFilter(contentProvider));
        selectionDialog.setTitle(dialogTitle);
        FolderTreeEntity testCaseFolderRoot;
        try {
            testCaseFolderRoot = new FolderTreeEntity(
                    FolderController.getInstance().getTestCaseRoot(ProjectController.getInstance().getCurrentProject()),
                    null);
        } catch (ControllerException e) {
            throw new ResourceException("Could not initialize test case folder", e);
        }
        selectionDialog.setInput(Arrays.asList(testCaseFolderRoot));
        if (selectionDialog.open() != TreeEntitySelectionDialog.OK || selectionDialog.getResult() == null
                || selectionDialog.getResult().length != 1
                || !(selectionDialog.getResult()[0] instanceof FolderTreeEntity)) {
            return null;
        }
        FolderTreeEntity folderTreeEntity = (FolderTreeEntity) selectionDialog.getResult()[0];
        try {
            return new FolderEntityImpl(folderTreeEntity.getObject());
        } catch (Exception e) {
            throw new ResourceException("Could not select test case folder", e);
        }
    }
    
    @Override
    public FolderEntity showTestObjectFolderSelectionDialog(Shell parentShell, String dialogTitle) throws PlatformException {
        EntityLabelProvider labelProvider = new EntityLabelProvider();
        EntityProvider contentProvider = new EntityProvider();
        TreeEntitySelectionDialog selectionDialog = new TreeEntitySelectionDialog(parentShell, labelProvider,
                contentProvider, new FolderEntityTreeViewerFilter(contentProvider));
        selectionDialog.setTitle(dialogTitle);
        FolderTreeEntity testObjectFolderRoot;
        try {
            testObjectFolderRoot = new FolderTreeEntity(
                    FolderController.getInstance().getObjectRepositoryRoot(ProjectController.getInstance().getCurrentProject()),
                    null);
        } catch (Exception e) {
            throw new ResourceException("Could not initialize test object folder", e);
        }
        selectionDialog.setInput(Arrays.asList(testObjectFolderRoot));
        if (selectionDialog.open() != TreeEntitySelectionDialog.OK || selectionDialog.getResult() == null
                || selectionDialog.getResult().length != 1
                || !(selectionDialog.getResult()[0] instanceof FolderTreeEntity)) {
            return null;
        }
        FolderTreeEntity folderTreeEntity = (FolderTreeEntity) selectionDialog.getResult()[0];
        try {
            return new FolderEntityImpl(folderTreeEntity.getObject());
        } catch (Exception e) {
            throw new ResourceException("Could not select test object folder", e);
        }
    }
    
    @Override
    public TestCaseEntity[] showTestCaseSelectionDialog(Shell parentShell, String dialogTitle)
            throws PlatformException {
        EntityLabelProvider labelProvider = new EntityLabelProvider();
        EntityProvider contentProvider = new EntityProvider();
        TestCaseSelectionDialog selectionDialog = new TestCaseSelectionDialog(
                parentShell, labelProvider, contentProvider, new EntityViewerFilter(contentProvider));
        selectionDialog.setTitle(dialogTitle);
        selectionDialog.setAllowMultiple(false);
        FolderTreeEntity testCaseFolderRoot;
        try {
            testCaseFolderRoot = new FolderTreeEntity(
                    FolderController.getInstance().getTestCaseRoot(ProjectController.getInstance().getCurrentProject()),
                    null);
        } catch (ControllerException e) {
            throw new ResourceException("Could not initialize test case folder", e);
        }
        selectionDialog.setInput(Arrays.asList(testCaseFolderRoot));
        if (selectionDialog.open() != TreeEntitySelectionDialog.OK || selectionDialog.getResult() == null) {
            return null;
        }
        try {
            TestCaseTreeEntity[] selectedTreeEntities = selectionDialog.getSelectedTestCases();
            List<TestCaseEntity> selectedTestCases = new ArrayList<>();
            for (TestCaseTreeEntity selectedTreeEntity : selectedTreeEntities) {
                com.kms.katalon.entity.testcase.TestCaseEntity testCase = selectedTreeEntity.getObject();
                selectedTestCases.add(new TestCaseEntityImpl(testCase));
            }
            return selectedTestCases.toArray(new TestCaseEntity[selectedTestCases.size()]);
        } catch (Exception e) {
            throw new PlatformException(e);
        }
    }

    @Override
    public TestObjectEntity[] showTestObjectSelectionDialog(Shell parentShell, String dialogTitle)
            throws PlatformException {
        EntityLabelProvider labelProvider = new EntityLabelProvider();
        EntityProvider contentProvider = new EntityProvider();
        TestObjectSelectionDialog selectionDialog = new TestObjectSelectionDialog(
                parentShell, labelProvider, contentProvider, new EntityViewerFilter(contentProvider));
        selectionDialog.setTitle(dialogTitle);
        selectionDialog.setAllowMultiple(false);
        FolderTreeEntity testObjectFolderRoot;
        try {
            testObjectFolderRoot = new FolderTreeEntity(FolderController.getInstance()
                    .getObjectRepositoryRoot(ProjectController.getInstance().getCurrentProject()), null);
        } catch (Exception e) {
            throw new ResourceException("Could not initialize test object folder", e);
        }
        selectionDialog.setInput(Arrays.asList(testObjectFolderRoot));
        if (selectionDialog.open() != TreeEntitySelectionDialog.OK || selectionDialog.getResult() == null) {
            return null;
        }
        try {
            WebElementTreeEntity[] selectedTreeEntities = selectionDialog.getSelectedTestObjects();
            List<TestObjectEntity> selectedTestObjects = new ArrayList<>();
            for (WebElementTreeEntity selectedTreeEntity : selectedTreeEntities) {
                com.kms.katalon.entity.repository.WebElementEntity testObject = (WebElementEntity) selectedTreeEntity.getObject();
                selectedTestObjects.add(mapTestObjectToPlatformModel(testObject));
            }
            return selectedTestObjects.toArray(new TestObjectEntity[selectedTestObjects.size()]);
        } catch (Exception e) {
            throw new PlatformException(e);
        }
    }

    private TestObjectEntity mapTestObjectToPlatformModel(
            com.kms.katalon.entity.repository.WebElementEntity testObject) {
        if (testObject instanceof WebServiceRequestEntity) {
            WebServiceRequestEntity requestEntity = (WebServiceRequestEntity) testObject;
            if (requestEntity.getServiceType().equals(WebServiceRequestEntity.SOAP)) {
                return new SoapRequestEntityImpl(requestEntity);
            } else {
                return new RestRequestEntityImpl(requestEntity);
            }
        } else {
            return new WebElementEntityImpl(testObject);
        }
    }
    
    @Override
    public void openApplicationPreferences() {
        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.KATALON_PREFERENCES, null);
    }

    @Override
    public void openPluginPreferencePage(String preferenceId) {
        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.PROJECT_SETTINGS_PAGE, preferenceId);
    }
}
