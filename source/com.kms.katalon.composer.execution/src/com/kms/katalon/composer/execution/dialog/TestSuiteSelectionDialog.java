package com.kms.katalon.composer.execution.dialog;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.providers.AbstractEntityViewerFilter;
import com.kms.katalon.composer.components.impl.providers.IEntityLabelProvider;
import com.kms.katalon.composer.components.impl.tree.TestSuiteCollectionTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.execution.constants.StringConstants;

public class TestSuiteSelectionDialog extends TreeEntitySelectionDialog {

    private static final String PLUGIN_ID = "com.kms.katalon.composer.execution";

    public TestSuiteSelectionDialog(Shell parent, IEntityLabelProvider labelProvider,
            ITreeContentProvider contentProvider, AbstractEntityViewerFilter entityViewerFilter) {
        super(parent, labelProvider, contentProvider, entityViewerFilter);
        setTitle(StringConstants.DIA_TITLE_TEST_SUITE_BROWSER);
        setAllowMultiple(false);
        setValidator(new ISelectionStatusValidator() {

            @Override
            public IStatus validate(Object[] selection) {
                if (isTestSuiteSelected(selection)) {
                    return new Status(IStatus.OK, PLUGIN_ID, IStatus.OK, null, null);
                }
                return new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, null, null);
            }
        });
    }

    private boolean isTestSuiteSelected(Object[] selection) {
        try {
            return selection != null
                    && selection.length > 0
                    && (((ITreeEntity) selection[0]) instanceof TestSuiteTreeEntity ||
                    ((ITreeEntity) selection[0]) instanceof TestSuiteCollectionTreeEntity);
        } catch (Exception e) {
            logError(e);
        }
        return false;
    }

    @Override
    public void setAllowMultiple(boolean allowMultiple) {
        // Ensure multiple selection is not allowed
        super.setAllowMultiple(false);
    }
}
