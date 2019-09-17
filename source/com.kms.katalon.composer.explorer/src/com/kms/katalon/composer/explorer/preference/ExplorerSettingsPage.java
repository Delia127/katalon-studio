package com.kms.katalon.composer.explorer.preference;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.constants.StringConstants;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.setting.ExplorerSettingStore;

public class ExplorerSettingsPage extends PreferencePageWithHelp {

    private Composite container;

    private List<Button> buttons;

    private List<ITreeEntity> folderTreeEntities;

    private ExplorerSettingStore store;

    @Inject
    private IEventBroker eventBroker;

    public ExplorerSettingsPage() {
        super();
        noDefaultButton();
        store = new ExplorerSettingStore(ProjectController.getInstance().getCurrentProject());
        folderTreeEntities = getFolderEntities();
        buttons = new ArrayList<>();
    }

	private List<ITreeEntity> getFolderEntities() {
        List<ITreeEntity> folderTreeEntities = new ArrayList<>();
        try {
            List<ITreeEntity> treeEntities = TreeEntityUtil.getAllTreeEntity(ProjectController.getInstance()
                    .getCurrentProject());
            if (treeEntities == null) {
                treeEntities = new ArrayList<ITreeEntity>();
            }
            folderTreeEntities = treeEntities.stream()
                    .filter(folderEntity -> folderEntity instanceof FolderTreeEntity)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PAGE_ERROR_MSG_UNABLE_TO_READ_SETTINGS,
	                  e.getMessage());
		}
        return folderTreeEntities;
	}

    @Override
    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        container.setLayout(new GridLayout(1, false));

        try {
            for (ITreeEntity folderEntity : folderTreeEntities) {   
                String entityName = folderEntity.getText();
                Button button = new Button(container, SWT.CHECK);
                button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
                button.setText(entityName);
                buttons.add(button);   
            }
        } catch (Exception e) {
                MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PAGE_ERROR_MSG_UNABLE_TO_READ_SETTINGS, 
                        e.getMessage());
                LoggerSingleton.logError(e);
        }

        updateInput();

        return container;
    }

    private void updateInput() {
        try {
            for (Button button : buttons) {
                String entityName = button.getText();
                boolean isShow = store.isItemShow(entityName);	
                button.setSelection(isShow);
            }
        } catch(Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PAGE_ERROR_MSG_UNABLE_TO_READ_SETTINGS,
	                  e.getMessage());
        }
    }

    @Override
    public boolean performOk() {
        if (container == null || container.isDisposed()) {
            return true;
        }
        try {
            for (Button button : buttons) {
                String entityName = button.getText();
                boolean isShow = button.getSelection();
                store.setItemShow(entityName, isShow);
	    	}

            refreshExplorer();
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PAGE_ERROR_MSG_UNABLE_TO_UPDATE_SETTINGS,
                    e.getMessage());
            return false;
        }
    }

    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.SETTING_REPORT;
    }

    private void refreshExplorer() {
        try {
            List<ITreeEntity> treeEntities = TreeEntityUtil.getAllTreeEntity(ProjectController.getInstance()
                    .getCurrentProject());
            eventBroker.post(EventConstants.EXPLORER_RELOAD_INPUT, treeEntities);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PAGE_ERROR_MSG_UNABLE_TO_REFESH_EXPLORER,
                    e.getMessage());
        }	
    }
}
