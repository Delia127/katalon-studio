package com.kms.katalon.platform.internal.ui;

import java.util.Arrays;

import org.eclipse.swt.widgets.Shell;

import com.katalon.platform.api.exception.PlatformException;
import com.katalon.platform.api.exception.ResourceException;
import com.katalon.platform.api.model.FolderEntity;
import com.katalon.platform.api.ui.DialogActionService;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.FolderEntityTreeViewerFilter;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.platform.internal.entity.FolderEntityImpl;

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
    public void openApplicationPreferences() {
        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.KATALON_PREFERENCES, null);
    }

    @Override
    public void openPluginPreferencePage(String preferenceId) {
        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.PROJECT_SETTINGS_PAGE, preferenceId);
    }

}
