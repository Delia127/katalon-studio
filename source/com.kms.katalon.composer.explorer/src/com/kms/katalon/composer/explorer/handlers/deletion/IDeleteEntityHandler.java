package com.kms.katalon.composer.explorer.handlers.deletion;

import org.eclipse.core.runtime.IProgressMonitor;

import com.kms.katalon.composer.components.tree.ITreeEntity;

public interface IDeleteEntityHandler {
    Class<? extends ITreeEntity> entityType();

    boolean execute(ITreeEntity treeEntity, IProgressMonitor monitor);
}
