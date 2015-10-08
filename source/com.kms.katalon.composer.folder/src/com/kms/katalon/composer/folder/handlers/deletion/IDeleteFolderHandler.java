package com.kms.katalon.composer.folder.handlers.deletion;

import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;

public interface IDeleteFolderHandler {
    FolderType getFolderType();
    boolean execute(FolderTreeEntity folderTreeEntity, IProgressMonitor monitor);
}
