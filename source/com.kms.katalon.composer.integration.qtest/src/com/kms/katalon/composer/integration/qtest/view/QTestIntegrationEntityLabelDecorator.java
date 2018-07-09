package com.kms.katalon.composer.integration.qtest.view;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.integration.IntegrationLabelDecorator;
import com.kms.katalon.composer.integration.qtest.QTestIntegrationUtil;
import com.kms.katalon.composer.integration.qtest.constant.ImageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.file.IntegratedFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class QTestIntegrationEntityLabelDecorator implements IntegrationLabelDecorator {

    @Override
    public Image getOverlayImage(ITreeEntity treeEntity) {
        try {
            Object element = treeEntity.getObject();
            if (!(element instanceof IntegratedFileEntity)) {
                return null;
            }
            ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
            if (!QTestIntegrationUtil.isIntegrationEnable(currentProject)) {
                return null;
            }
            IntegratedFileEntity integratedFileEntity = (IntegratedFileEntity) element;

            boolean integrated = QTestIntegrationUtil.isIntegrated(integratedFileEntity, currentProject);
            if (!integrated) {
                return null;
            }
            if (integratedFileEntity instanceof TestCaseEntity) {
                return ImageConstants.IMG_16_QTEST_TEST_CASE;
            }
            if (integratedFileEntity instanceof TestSuiteEntity) {
                return ImageConstants.IMG_16_QTEST_TEST_SUITE;
            }
            if (integratedFileEntity instanceof FolderEntity) {
                return ImageConstants.IMG_16_QTEST_FOLDER;
            }
            return null;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return null;
        }
    }

    @Override
    public int getPreferredOrder() {
        return 0;
    }
}
