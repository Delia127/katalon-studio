package com.kms.katalon.composer.handlers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.e4.ui.internal.workbench.PartServiceSaveHandler;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.dialogs.YesNoCancel;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.util.groovy.editor;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;

@SuppressWarnings("restriction")
public class WorkbenchSaveHandler extends PartServiceSaveHandler {

    @Override
    public boolean save(MPart dirtyPart, boolean confirm) {
        if (dirtyPart.getObject() instanceof CompatibilityEditor) {
            if (confirm) {
                switch (promptToSave(dirtyPart)) {
                    case NO:
                        refreshKeywordRootTreeEntity();
                        return true;
                    case CANCEL:
                        return false;
                    case YES:
                        break;
                }
            }

            editor.saveEditor(dirtyPart);

            EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.ECLIPSE_EDITOR_SAVED, dirtyPart);
            return true;
        } else {
            return super.save(dirtyPart, confirm);
        }
    }

    private void refreshKeywordRootTreeEntity() {
        FolderTreeEntity keywordRootFolder = null;
        try {
            keywordRootFolder = new FolderTreeEntity(
                    FolderController.getInstance().getKeywordRoot(ProjectController.getInstance().getCurrentProject()),
                    null);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        } finally {
            EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY,
                    keywordRootFolder);
        }
    }

    @Override
    public Save promptToSave(MPart dirtyPart) {
        String message = MessageFormat.format(WorkbenchMessages.EditorManager_saveChangesQuestion,
                dirtyPart.getLabel());
        MessageDialog dialog = new MessageDialog(null, "Save Part", null, message,
                MessageDialog.QUESTION_WITH_CANCEL, YesNoCancel.valuesString(), 0);

        YesNoCancel selection = YesNoCancel.getOption(dialog.open());
        switch (selection) {
            case CANCEL: {
                return Save.CANCEL;
            }
            case NO: {
                //Marks the part has No selection to be not dirty
                dirtyPart.setDirty(false);
                return Save.NO;
            }
            case YES: {
                return Save.YES;
            }
        }
        return Save.NO;
    }

    @Override
    public Save[] promptToSave(Collection<MPart> dirtyParts) {
        LabelProvider labelProvider = new LabelProvider() {
            @Override
            public String getText(Object element) {
                return ((MPart) element).getLocalizedLabel();
            }
        };
        List<MPart> parts = new ArrayList<MPart>(dirtyParts);
        ListSelectionDialog dialog = new ListSelectionDialog(null, parts,
                ArrayContentProvider.getInstance(), labelProvider,
                WorkbenchMessages.EditorManager_saveResourcesMessage);
        dialog.setInitialSelections(parts.toArray());
        dialog.setTitle(WorkbenchMessages.EditorManager_saveResourcesTitle);
        if (dialog.open() == IDialogConstants.CANCEL_ID) {
            return new Save[] { Save.CANCEL };
        }

        Object[] toSave = dialog.getResult();
        Save[] retSaves = new Save[parts.size()];
        Arrays.fill(retSaves, Save.NO);
        for (int i = 0; i < parts.size(); i++) {
            MPart part = parts.get(i);
            for (Object o : toSave) {
                if (o == part) {
                    retSaves[i] = Save.YES;
                    break;
                }
            }
            //Marks the part has No selection to be not dirty
            part.setDirty(false);
        }
        return retSaves;
    }
}
