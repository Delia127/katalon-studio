package com.kms.katalon.composer.explorer.handlers;

import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.transfer.TransferMoveFlag;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

public class CutHandler extends CopyHandler {
    
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
                        || !treeEntity.isRemoveable()
                        || !treeEntity.isRenamable()
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
        super.execute();
        TransferMoveFlag.setMove(true);
    }

}
