package com.kms.katalon.composer.explorer.providers;

import java.awt.dnd.DragSourceEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragment;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TreeDropTargetEffect;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeItem;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.tree.CheckpointTreeEntity;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.impl.tree.SystemFileTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestDataTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteCollectionTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityProcessingUtil;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.SystemFileController;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.file.SystemFileEntity;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testsuite.TestSuiteCollectionEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyUtil;

public class TreeEntityDropListener extends TreeDropTargetEffect {
    private IEventBroker eventBroker;

    private ITreeEntity lastMovedTreeEntity;
    
    TreeViewer treeViewer = null;

    public TreeEntityDropListener(TreeViewer treeViewer, IEventBroker eventBroker) {
        super(treeViewer.getTree());
        this.eventBroker = eventBroker;
        this.treeViewer = treeViewer;
    }

    @Override
    public void drop(DropTargetEvent event) {
        try {
            if (event.data instanceof ITreeEntity[]) {
                ITreeEntity[] treeEntities = (ITreeEntity[]) event.data;
                FolderTreeEntity targetTreeEntity = getDropDestinationFolder(event);
                FolderEntity target = (FolderEntity) targetTreeEntity.getObject();
                move(treeEntities, target, targetTreeEntity);
                eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, targetTreeEntity);
                eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM, lastMovedTreeEntity);
            }
            else {
                TreeItem[] selection = treeViewer.getTree().getSelection();
                List<ITreeEntity> treeEntity = new ArrayList<ITreeEntity>();
                for (TreeItem item : selection) {
                    treeEntity.add((ITreeEntity) item.getData());
                }
                event.data = treeEntity.toArray(new ITreeEntity[treeEntity.size()]);
                ITreeEntity[] treeEntities = (ITreeEntity[]) event.data;
                PackageTreeEntity targetTreeEntity = getDragDestinationPackage(event);
                PackageFragment packageFragment = (PackageFragment) targetTreeEntity.getObject();
                IFile file = null;
                for (int i = 0; i < treeEntities.length; i++) {
                    ICompilationUnit unit = (ICompilationUnit) treeEntities[i].getObject();
                    file = (IFile) unit.getResource();
                }
                moveKeyword(file, packageFragment, null);
                eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, targetTreeEntity.getParent());
                eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM, lastMovedTreeEntity);
            }
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR,
                    MessageFormat.format(StringConstants.LIS_ERROR_MSG_CANNOT_MOVE_THE_SELECTION, e.getMessage()));
        }
    }

    public static IFile moveKeyword(IFile keywordFile, IPackageFragment targetPackageFragment, String newName)
            throws JavaModelException {
        if (keywordFile == null || targetPackageFragment == null) {
            return null;
        }
        String oldRelativeKwLocation = keywordFile.getLocation().toString();
        String cutKeywordFilePath = getPastedFilePath(keywordFile, targetPackageFragment, newName);

        GroovyUtil.moveKeyword(keywordFile, targetPackageFragment, newName);

        if (!oldRelativeKwLocation.equals(cutKeywordFilePath)) {
            EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXPLORER_CUT_PASTED_SELECTED_ITEM,
                    new Object[] { keywordFile.getProjectRelativePath().toString(), cutKeywordFilePath });
            EventBrokerSingleton.getInstance().getEventBroker().send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY,
                    targetPackageFragment);
        }
        return keywordFile;
    }

    private static String getPastedFilePath(IFile keywordFile, IPackageFragment targetPackageFragment, String newName) {
        String keywordRootPath = targetPackageFragment.getParent().getElementName() + IPath.SEPARATOR;
        String packageName = targetPackageFragment.getElementName();
        String packagePath = keywordRootPath + (packageName.isEmpty() ? packageName
                : packageName.replaceAll("[.]", String.valueOf(IPath.SEPARATOR)) + IPath.SEPARATOR);
        String kwFileName = (newName != null) ? newName + GroovyConstants.GROOVY_FILE_EXTENSION : keywordFile.getName();
        String copiedKeywordFilePath = packagePath + kwFileName;
        return copiedKeywordFilePath;
    }

    private PackageTreeEntity getDragDestinationPackage(DropTargetEvent event) throws Exception {
        Object dest = event.item.getData();
        if (dest instanceof PackageTreeEntity) {
            return (PackageTreeEntity) dest;
        } else {
            return (PackageTreeEntity) ((ITreeEntity) dest).getParent();
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

    private void move(ITreeEntity[] treeEntities, FolderEntity targetFolder, FolderTreeEntity targetTreeEntity) throws Exception {
        FolderEntity rootTargetFolder = null;
        if (targetFolder.getFolderType().equals(FolderType.TESTCASE)) {
            rootTargetFolder = FolderController.getInstance().getTestCaseRoot(targetFolder.getProject());
        } else if (targetFolder.getFolderType().equals(FolderType.TESTSUITE)) {
            rootTargetFolder = FolderController.getInstance().getTestSuiteRoot(targetFolder.getProject());
        } else if (targetFolder.getFolderType().equals(FolderType.WEBELEMENT)) {
            rootTargetFolder = FolderController.getInstance().getObjectRepositoryRoot(targetFolder.getProject());
        } else if (targetFolder.getFolderType().equals(FolderType.DATAFILE)) {
            rootTargetFolder = FolderController.getInstance().getTestDataRoot(targetFolder.getProject());
        } else if (targetFolder.getFolderType().equals(FolderType.CHECKPOINT)) {
            rootTargetFolder = FolderController.getInstance().getCheckpointRoot(targetFolder.getProject());
        }

        for (ITreeEntity treeEntity : treeEntities) {
            if (!treeEntity.isRemoveable()) {
                continue;
            }
            validateMovingAcrossArea(treeEntity, targetFolder);
            
            if (treeEntity instanceof FolderTreeEntity) {
                validateMovingToSubFolder((FolderEntity) treeEntity.getObject(), targetFolder);
            }

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
            } else if (treeEntity instanceof TestSuiteCollectionTreeEntity) {
                TestSuiteCollectionEntity movedTo = EntityProcessingUtil.moveTestSuiteCollection(
                        (TestSuiteCollectionEntity) ((TestSuiteCollectionTreeEntity) treeEntity).getObject(), targetFolder);
                lastMovedTreeEntity = TreeEntityUtil.getTestSuiteCollectionTreeEntity(movedTo, targetFolder.getProject());
            } else if (treeEntity instanceof CheckpointTreeEntity) {
                CheckpointEntity movedCheckpoint = EntityProcessingUtil.moveCheckpoint(
                        ((CheckpointTreeEntity) treeEntity).getObject(), targetFolder);
                lastMovedTreeEntity = TreeEntityUtil.getCheckpointTreeEntity(movedCheckpoint);
            } else if (treeEntity instanceof SystemFileTreeEntity) {
                SystemFileTreeEntity systemFileTreeEntity = (SystemFileTreeEntity) treeEntity;
                SystemFileEntity newSystemFile = 
                        SystemFileController.getInstance().moveSystemFile(systemFileTreeEntity.getObject(), targetFolder);
               if (newSystemFile != null) {
                   lastMovedTreeEntity = new SystemFileTreeEntity(newSystemFile, targetTreeEntity);
               }

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
    
    private void validateMovingToSubFolder(FolderEntity folderEntity, FolderEntity targetFolder) throws Exception {
        FolderEntity parent = targetFolder.getParentFolder();
        while (parent != null) {
            if (parent.equals(folderEntity)) {
                throw new Exception(MessageFormat.format(StringConstants.LIS_ERROR_MSG_CANNOT_MOVE_TO_SUBFOLDER,
                        folderEntity.getName(), targetFolder.getName()));
            }
            parent = parent.getParentFolder();
        }
    }
    
    @Override
    public void dragOver(DropTargetEvent event) {
        event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SELECT;
        super.dragOver(event);
    }

}
