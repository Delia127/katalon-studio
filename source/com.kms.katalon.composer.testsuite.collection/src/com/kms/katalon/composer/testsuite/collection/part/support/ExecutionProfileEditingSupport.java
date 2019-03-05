package com.kms.katalon.composer.testsuite.collection.part.support;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.execution.collection.dialog.ExecutionProfileSelectionDialog;
import com.kms.katalon.composer.testsuite.collection.constant.ComposerTestsuiteCollectionMessageConstants;
import com.kms.katalon.composer.testsuite.collection.part.provider.TableViewerProvider;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.global.ExecutionProfileEntity;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.entity.testsuite.TestSuiteRunConfiguration;

public class ExecutionProfileEditingSupport extends EditingSupportWithTableProvider {

    public ExecutionProfileEditingSupport(TableViewerProvider provider) {
        super(provider);
    }

    @Override
    protected CellEditor getCellEditorByElement(TestSuiteRunConfiguration element) {
        return new ExectionProfileDialogCellEditor(getComposite(), element.getConfiguration().getProfileName());
    }

    @Override
    protected boolean canEditElement(TestSuiteRunConfiguration element) {
        return true;
    }

    @Override
    protected Object getElementValue(TestSuiteRunConfiguration element) {
        return element.getConfiguration().getProfileName();
    }

    @Override
    protected void setElementValue(TestSuiteRunConfiguration element, Object profileName) {
        RunConfigurationDescription configuration = element.getConfiguration();
        if (!configuration.getProfileName().equals(profileName)) {
            configuration.setProfileName((String) profileName);
            refreshElementAndMarkDirty(element);
        }
    }

    private class ExectionProfileDialogCellEditor extends AbstractDialogCellEditor {

        private String profileName;

        protected ExectionProfileDialogCellEditor(Composite parent, String profileName) {
            super(parent);
            this.profileName = profileName;
        }

        @Override
        protected Object openDialogBox(Control cellEditorWindow) {
            try {
                List<ExecutionProfileEntity> profiles = GlobalVariableController.getInstance()
                        .getAllGlobalVariableCollections(ProjectController.getInstance().getCurrentProject());
                ExecutionProfileEntity selectedProfile = profiles.stream()
                        .filter(p -> p.getName().equals(profileName))
                        .findFirst()
                        .get();
                ExecutionProfileSelectionDialog dialog = new ExecutionProfileSelectionDialog(getParentShell(), profiles,
                        selectedProfile);
                if (dialog.open() != ExecutionProfileSelectionDialog.OK) {
                    return profileName;
                }
                return dialog.getSelectedProfile().getName();
            } catch (ControllerException e) {
                MultiStatusErrorDialog.showErrorDialog(
                        ComposerTestsuiteCollectionMessageConstants.PA_MSG_UNABLE_TO_SELECT_EXECUTION_PROFILES,
                        e.getMessage(), ExceptionsUtil.getMessageForThrowable(e));
            }
            return profileName;
        }
    }

}
