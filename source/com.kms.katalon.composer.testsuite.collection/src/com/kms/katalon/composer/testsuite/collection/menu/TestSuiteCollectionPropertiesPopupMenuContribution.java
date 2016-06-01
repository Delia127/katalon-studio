package com.kms.katalon.composer.testsuite.collection.menu;

import javax.inject.Inject;

import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.impl.menu.AbstractPropertiesMenuContribution;
import com.kms.katalon.composer.components.impl.tree.TestSuiteCollectionTreeEntity;
import com.kms.katalon.composer.testsuite.collection.handler.EditTestSuiteCollectionPropertiesHandler;
import com.kms.katalon.constants.IdConstants;

public class TestSuiteCollectionPropertiesPopupMenuContribution extends AbstractPropertiesMenuContribution {

    @Inject
    private ESelectionService selectionService;

    private boolean isTestSuiteCollectionSelected() {
        Object selection = selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
        if (selection == null || !selection.getClass().isArray()) {
            return false;
        }

        Object[] selectedObjects = (Object[]) selection;
        if (selectedObjects.length != 1) {
            return false;
        }

        return selectedObjects[0] instanceof TestSuiteCollectionTreeEntity;
    }

    @Override
    protected boolean canShow() {
        return isTestSuiteCollectionSelected();
    }

    @Override
    protected Class<?> getHandlerClass() {
        return EditTestSuiteCollectionPropertiesHandler.class;
    }
}
