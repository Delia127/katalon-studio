package com.kms.katalon.composer.integration.qtest.dialog.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.entity.folder.FolderEntity;

public class TestCaseFolderEntityProvider extends EntityProvider {

    private List<String> registeredFolderIds;

    public TestCaseFolderEntityProvider(List<String> folderIds) {
        super();
        this.registeredFolderIds = folderIds;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        try {
            Object[] parentInput = super.getElements(inputElement);

            List<Object> validatedInput = new ArrayList<Object>();
            for (Object element : parentInput) {
                if (!(element instanceof ITreeEntity)) continue;

                if (((ITreeEntity) element).getObject() instanceof FolderEntity) {
                    FolderEntity folderEntity = (FolderEntity) ((ITreeEntity) element).getObject();
                    if (isFolderQualified(folderEntity)) {
                        validatedInput.add(element);
                    }
                }
            }
            return validatedInput.toArray(new Object[validatedInput.size()]);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return Collections.emptyList().toArray();
        }
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        try {
            if (!(parentElement instanceof ITreeEntity)) return Collections.emptyList().toArray();

            List<Object> children = new ArrayList<Object>();
            for (Object childTreeEntity : ((ITreeEntity) parentElement).getChildren()) {
                if (((ITreeEntity) childTreeEntity).getObject() instanceof FolderEntity) {
                    FolderEntity childFolderEntity = (FolderEntity) ((ITreeEntity) childTreeEntity).getObject();
                    if (isFolderQualified(childFolderEntity)) {
                        children.add(childTreeEntity);
                    }
                }
            }

            return children.toArray(new Object[children.size()]);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return Collections.emptyList().toArray();
    }

    private boolean isFolderQualified(FolderEntity folderEntity) {
        try {
            String folderId = folderEntity.getIdForDisplay();
            if (folderId == null || folderId.isEmpty()) return false;

            for (String existedFolderIds : registeredFolderIds) {
                if (folderId.startsWith(existedFolderIds + "/") || folderId.equals(existedFolderIds)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
