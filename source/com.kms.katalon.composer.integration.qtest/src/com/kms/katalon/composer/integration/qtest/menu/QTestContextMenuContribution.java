package com.kms.katalon.composer.integration.qtest.menu;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.composer.integration.qtest.handler.QTestDisintegrateTestCaseHandler;
import com.kms.katalon.composer.integration.qtest.handler.QTestDisintegrateTestSuiteHandler;
import com.kms.katalon.composer.integration.qtest.handler.QTestDownloadTestCaseHandler;
import com.kms.katalon.composer.integration.qtest.handler.QTestUploadTestCaseHandler;
import com.kms.katalon.composer.integration.qtest.handler.QTestUploadTestSuiteHandler;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.integration.qtest.constants.QTestStringConstants;

public class QTestContextMenuContribution {

    private static final String CONTRIBUTOR_URI = FrameworkUtil.getBundle(QTestContextMenuContribution.class)
            .getSymbolicName();

    @Inject
    private ESelectionService selectionService;

    @Inject
    private EModelService modelService;

    /**
     * Creates a {@link MMenu} that has label qTest and provides some qTest integration's function.
     * 
     * @param menuItems
     */
    @AboutToShow
    public void aboutToShow(List<MMenuElement> menuItems) {
        try {
            Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
            if (selectedObjects == null || selectedObjects.length != 1) {
                return;
            }

            Object selectedObject = selectedObjects[0];
            MMenu qTestMenu = getQTestMenu();

            MDirectMenuItem uploadMenuItem = getUploadMenuItem();
            MDirectMenuItem downloadMenuItem = getDownloadMenuItem();
            MDirectMenuItem disintegrateMenuItem = getDisintegrateMenuItem();

            if (selectedObject instanceof TestCaseTreeEntity) {
                // Add Upload menu item for test case
                uploadMenuItem.setContributionURI(StringConstants.CM_QTEST_COMPOSER_BUNDLE_URI
                        + QTestUploadTestCaseHandler.class.getName());
                qTestMenu.getChildren().add(uploadMenuItem);

                // Add Disintegrate menu item for test case
                disintegrateMenuItem.setContributionURI(StringConstants.CM_QTEST_COMPOSER_BUNDLE_URI
                        + QTestDisintegrateTestCaseHandler.class.getName());
                qTestMenu.getChildren().add(disintegrateMenuItem);
            } else if (selectedObject instanceof TestSuiteTreeEntity) {
                // Add Upload menu item for test suite
                uploadMenuItem.setContributionURI(StringConstants.CM_QTEST_COMPOSER_BUNDLE_URI
                        + QTestUploadTestSuiteHandler.class.getName());
                qTestMenu.getChildren().add(uploadMenuItem);

                // Add Disintegrate menu item for test suite
                disintegrateMenuItem.setContributionURI(StringConstants.CM_QTEST_COMPOSER_BUNDLE_URI
                        + QTestDisintegrateTestSuiteHandler.class.getName());
                qTestMenu.getChildren().add(disintegrateMenuItem);
            } else if (selectedObject instanceof FolderTreeEntity
                    && ((FolderTreeEntity) selectedObject).getObject() instanceof FolderEntity) {
                FolderEntity folderEntity = (FolderEntity) ((FolderTreeEntity) selectedObject).getObject();
                if (folderEntity.getFolderType() != FolderType.TESTCASE) {
                    return;
                }

                // Add Upload menu item for test folder
                uploadMenuItem.setContributionURI(StringConstants.CM_QTEST_COMPOSER_BUNDLE_URI
                        + QTestUploadTestCaseHandler.class.getName());
                qTestMenu.getChildren().add(uploadMenuItem);
                
                // Add Download menu item for test folder
                downloadMenuItem.setContributionURI(StringConstants.CM_QTEST_COMPOSER_BUNDLE_URI
                        + QTestDownloadTestCaseHandler.class.getName());
                qTestMenu.getChildren().add(downloadMenuItem);

                // Add Disintegrate menu item for test case
                disintegrateMenuItem.setContributionURI(StringConstants.CM_QTEST_COMPOSER_BUNDLE_URI
                        + QTestDisintegrateTestCaseHandler.class.getName());
                qTestMenu.getChildren().add(disintegrateMenuItem);
            }

            if (qTestMenu.getChildren().size() > 0) {
                menuItems.add(qTestMenu);
            }

        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private MDirectMenuItem getDisintegrateMenuItem() {
        MDirectMenuItem dynamicItem = modelService.createModelElement(MDirectMenuItem.class);
        dynamicItem.setLabel(StringConstants.CM_DISINTEGRATE);
        dynamicItem.setContributorURI(FrameworkUtil.getBundle(QTestContextMenuContribution.class).getSymbolicName());

        return dynamicItem;
    }

    private MDirectMenuItem getUploadMenuItem() {
        MDirectMenuItem dynamicItem = modelService.createModelElement(MDirectMenuItem.class);
        dynamicItem.setLabel(StringConstants.CM_UPLOAD);
        dynamicItem.setContributorURI(CONTRIBUTOR_URI);
        return dynamicItem;
    }

    private MDirectMenuItem getDownloadMenuItem() {
        MDirectMenuItem dynamicItem = modelService.createModelElement(MDirectMenuItem.class);
        dynamicItem.setLabel(StringConstants.CM_DONWLOAD);
        dynamicItem.setContributorURI(CONTRIBUTOR_URI);

        return dynamicItem;
    }

    private MMenu getQTestMenu() {
        MMenu dynamicItem = modelService.createModelElement(MMenu.class);
        dynamicItem.setLabel(QTestStringConstants.PRODUCT_NAME);
        dynamicItem.setContributorURI(CONTRIBUTOR_URI);

        return dynamicItem;
    }
}
