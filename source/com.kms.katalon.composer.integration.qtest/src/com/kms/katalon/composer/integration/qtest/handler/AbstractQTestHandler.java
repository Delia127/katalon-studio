package com.kms.katalon.composer.integration.qtest.handler;

import org.eclipse.e4.ui.workbench.modeling.ESelectionService;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

public class AbstractQTestHandler {
    
    /**
     * @param selectionService
     * @return the first selected object on Explorer. 
     */
    protected Object getFirstSelectedObject(ESelectionService selectionService) {
        try {
            ProjectEntity projectEntity = ProjectController.getInstance().getCurrentProject();

            if (!QTestIntegrationUtil.isIntegrationEnable(projectEntity)) {
                return null;
            }

            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
            if (selectedObjects == null || selectedObjects.length != 1) {
                return null;
            }

            if (selectedObjects[0] instanceof ITreeEntity) {
                return ((ITreeEntity) selectedObjects[0]).getObject();
            }
        } catch (Exception ex) {
            LoggerSingleton.logError(ex);
        }
        return null;
    }
}
