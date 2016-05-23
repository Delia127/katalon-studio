package com.kms.katalon.composer.explorer.providers;

import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TreeDropTargetEffect;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityProcessingUtil;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;

public class TreeEntityDropListener extends TreeDropTargetEffect {
    private IEventBroker eventBroker;

    private ITreeEntity lastMovedTreeEntity;

    public TreeEntityDropListener(TreeViewer treeViewer, IEventBroker eventBroker) {
        super(treeViewer.getTree());
        this.eventBroker = eventBroker;
    }

    @Override
    public void drop(DropTargetEvent event) {
        try {
            if (event.data instanceof ITreeEntity[]) {
                ITreeEntity[] treeEntities = (ITreeEntity[]) event.data;
                FolderTreeEntity targetTreeEntity = getDropDestinationFolder(event);
                FolderEntity target = (FolderEntity) targetTreeEntity.getObject();
                move(treeEntities, target);
                eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, targetTreeEntity);
                eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM, lastMovedTreeEntity);
            }
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR,
                    MessageFormat.format(StringConstants.LIS_ERROR_MSG_CANNOT_MOVE_THE_SELECTION, e.getMessage()));
        }
    }

    private FolderTreeEntity getDropDestinationFolder(DropTargetEvent event) throws Exception {
        Object dest = event.item.getData();
        if (dest instanceof FolderTreeEntity) {
            return (FolderTreeEntity) dest;
        } else {
            return (FolderTreeEntity) ((ITreeEntity) dest).getParent();
        }
    }

    private void move(ITreeEntity[] treeEntities, FolderEntity targetFolder) throws Exception {
        FolderEntity rootTargetFolder = null;
        if (targetFolder.getFolderType().equals(FolderType.TESTCASE)) {
            rootTargetFolder = FolderController.getInstance().getTestCaseRoot(targetFolder.getProject());
        } else if (targetFolder.getFolderType().equals(FolderType.TESTSUITE)) {
            rootTargetFolder = FolderController.getInstance().getTestSuiteRoot(targetFolder.getProject());
        } else if (targetFolder.getFolderType().equals(FolderType.WEBELEMENT)) {
            rootTargetFolder = FolderController.getInstance().getTestSuiteRoot(targetFolder.getProject());
        } else if (targetFolder.getFolderType().equals(FolderType.DATAFILE)) {
            rootTargetFolder = FolderController.getInstance().getTestSuiteRoot(targetFolder.getProject());
        }

        for (ITreeEntity treeEntity : treeEntities) {
            validateMovingAcrossArea(treeEntity, targetFolder);

            // Prevent inside-duplicated itself
            if (targetFolder.equals(treeEntity.getObject())) continue;

            if (treeEntity.getParent() == null || targetFolder.equals(treeEntity.getParent().getObject())) {
                lastMovedTreeEntity = treeEntity;
                continue;
            }

            if (treeEntity instanceof TestCaseTreeEntity) {
                TestCaseEntity movedTc = EntityProcessingUtil.moveTestCase(
                        (TestCaseEntity) ((TestCaseTreeEntity) treeEntity).getObject(), targetFolder);
                lastMovedTreeEntity = TreeEntityUtil.getTestCaseTreeEntity(movedTc, targetFolder.getProject());
            } else if (treeEntity instanceof FolderTreeEntity) {
                FolderEntity movedFolder = EntityProcessingUtil.moveFolder(
                        (FolderEntity) ((FolderTreeEntity) treeEntity).getObject(), targetFolder);
                lastMovedTreeEntity = TreeEntityUtil.createSelectedTreeEntityHierachy(movedFolder, rootTargetFolder);
            } else if (treeEntity instanceof TestSuiteTreeEntity) {
                TestSuiteEntity movedTs = EntityProcessingUtil.moveTestSuite(
                        (TestSuiteEntity) ((TestSuiteTreeEntity) treeEntity).getObject(), targetFolder);
                lastMovedTreeEntity = TreeEntityUtil.getTestSuiteTreeEntity(movedTs, targetFolder.getProject());
            } else if (treeEntity instanceof TestDataTreeEntity) {
                DataFileEntity movedTd = EntityProcessingUtil.moveTestData(
                        (DataFileEntity) ((TestDataTreeEntity) treeEntity).getObject(), targetFolder);
                lastMovedTreeEntity = TreeEntityUtil.getTestDataTreeEntity(movedTd, targetFolder.getProject());
            } else if (treeEntity instanceof WebElementTreeEntity) {
                WebElementEntity movedTo = EntityProcessingUtil.moveTestObject(
                        (WebElementEntity) ((WebElementTreeEntity) treeEntity).getObject(), targetFolder);
                lastMovedTreeEntity = TreeEntityUtil.getWebElementTreeEntity(movedTo, targetFolder.getProject());
            }
            eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, treeEntity.getParent());
        }
    }

    /**
     * To ensure that the specific entity can move in their region only.
     * <p>
     * Example: Test Case cannot be moved to Test Suite area and so on.
     * 
     * @param treeEntity
     * @param targetFolder
     * @throws Exception
     */
    private void validateMovingAcrossArea(ITreeEntity treeEntity, FolderEntity targetFolder) throws Exception {
        FolderTreeEntity temp = (treeEntity instanceof FolderTreeEntity) ? (FolderTreeEntity) treeEntity
                : (FolderTreeEntity) treeEntity.getParent();
        if (!StringUtils.equals(temp.getCopyTag(), (targetFolder.getFolderType().toString()))) {
            throw new Exception(MessageFormat.format(StringConstants.LIS_ERROR_MSG_CANNOT_MOVE_INTO_DIFF_REGION,
                    treeEntity.getCopyTag(), targetFolder.getFolderType().toString()));
        }
    }

    @Override
    public void dragOver(DropTargetEvent event) {
        event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SELECT;
        super.dragOver(event);
    }

}
