package com.kms.katalon.composer.explorer.handlers;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.handler.CommonExplorerHandler;
import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.transfer.TransferMoveFlag;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

public class CopyHandler extends CommonExplorerHandler {

    private static CopyHandler _instance;

    public static CopyHandler getInstance() {
        if (_instance == null) {
            _instance = new CopyHandler();
        }
        return _instance;
    }

    @Override
    public boolean canExecute() {
        if (!isExplorerPartActive()) {
            return false;
        }

        Object[] selectedObjects = getExplorerSelection();
        if (selectedObjects.length == 0) {
            return false;
        }

        try {
            // All selections should be on the same level, and may not be the Root nodes
            String entityTag = null;
            for (Object entity : selectedObjects) {
                if (!(entity instanceof ITreeEntity)) {
                    // Not allow to copy entity that is not tree entity
                    return false;
                }
                ITreeEntity treeEntity = (ITreeEntity) entity;

                // Not allow to copy Root Folders
                // Not allow keyword package from copy
                if (treeEntity.getParent() == null 
                        || treeEntity instanceof PackageTreeEntity || 
                        StringUtils.isEmpty(treeEntity.getCopyTag())) {
                    return false;
                }

                String copyTag = treeEntity.getCopyTag();

                // Not allow Report or its folder from copy
                if (StringUtils.equals(copyTag, FolderType.REPORT.toString())) {
                    return false;
                }
                
                if (StringUtils.equals(copyTag, FolderType.USER.toString())) {
                    return false;
                }
                
                if (entityTag == null) {
                    entityTag = copyTag;
                    continue;
                }

                // Not allow to copy multiple types of tree entity
                if (!equalsIgnoreCase(copyTag, entityTag)) {
                    return false;
                }
            }
        } catch (Exception e) {
            logError(e);
            return false;
        }
        return true;
    }

    @Override
    public void execute() {
        if (!isExplorerPartActive()) {
            return;
        }

        Object[] selectedObjects = getExplorerSelection();

        if (selectedObjects.length == 0) {
            return;
        }

        int numberOfSelectedObject = selectedObjects.length;

        Clipboard cb = new Clipboard(Display.getCurrent());
        TransferMoveFlag.setMove(false);
        try {
            String[] keywordData = new String[numberOfSelectedObject];
            ITreeEntity[] entityData = new ITreeEntity[numberOfSelectedObject];
            // Only one entity type is allowed to copy at certain time
            boolean isKeyword = (selectedObjects[0] instanceof KeywordTreeEntity);
            for (int i = 0; i < numberOfSelectedObject; i++) {
                ITreeEntity treeEntity = (ITreeEntity) selectedObjects[i];

                // Load descendant entities
                treeEntity.loadAllDescentdantEntities();

                // handle keyword type
                if (isKeyword) {
                    keywordData[i] = ((ICompilationUnit) ((KeywordTreeEntity) treeEntity).getObject()).getPath()
                            .toString();
                    continue;
                }

                entityData[i] = treeEntity;
            }

            Transfer[] transferType = new Transfer[] { TreeEntityTransfer.getInstance() };
            Object[] transferData = new Object[] { entityData };

            if (isKeyword) {
                transferType[0] = FileTransfer.getInstance();
                transferData[0] = keywordData;
            }

            // set content to clipboard
            cb.setContents(transferData, transferType);
        } catch (Exception e) {
            logError(e);
        }
    }
}
