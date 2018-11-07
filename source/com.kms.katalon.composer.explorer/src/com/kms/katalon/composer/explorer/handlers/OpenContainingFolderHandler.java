package com.kms.katalon.composer.explorer.handlers;

import java.io.File;
import java.util.List;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.impl.handler.CommonExplorerHandler;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.constants.ComposerExplorerMessageConstants;
import com.kms.katalon.constants.GlobalMessageConstants;
import com.kms.katalon.entity.file.FileEntity;

public class OpenContainingFolderHandler extends CommonExplorerHandler {

    @CanExecute
    public boolean canExecute() {
        if (!isExplorerPartActive()) {
            return false;
        }
        Object[] selectedObjects = getExplorerSelection();
        if (selectedObjects.length == 0) {
            return false;
        }
        try {
            ITreeEntity firstTreeEntity = (ITreeEntity) selectedObjects[0];
            String firstParent = getParentFolderLocation(firstTreeEntity);
            for (Object selectedItem : selectedObjects) {
                ITreeEntity treeEntity = (ITreeEntity) selectedItem;
                String parent = getParentFolderLocation(treeEntity);
                if (parent == null || !parent.equals(firstParent)) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Execute
    public void execute() {
        try {
            List<ITreeEntity> treeEntities = getElementSelection(ITreeEntity.class);
            if (treeEntities.isEmpty()) {
                return;
            }
            ITreeEntity treeEntity = treeEntities.get(0);
            String parent = getParentFolderLocation(treeEntity);
            Program.launch(new File(parent).toURI().toString());
        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), GlobalMessageConstants.ERROR,
                    ComposerExplorerMessageConstants.ERROR_CANNOT_FIND_CONTAINING_FOLDER);
        }
    }

    private String getParentFolderLocation(ITreeEntity treeEntity) throws Exception {
        ITreeEntity treeParent = treeEntity.getParent();
        if (treeEntity instanceof KeywordTreeEntity) {
            FileEntity keywordFolder = (FileEntity) treeParent.getParent().getObject();
            String packageName = ((PackageTreeEntity) treeParent).getPackageName();
            if (packageName.equals(PackageTreeEntity.DEFAULT_PACKAGE_LABEL)) {
                return keywordFolder.getLocation();
            }
            return keywordFolder.getLocation() + File.separator + getPathToPackage(packageName, true);
        }
        if (treeEntity instanceof PackageTreeEntity) {
            String packageName = ((PackageTreeEntity) treeEntity).getPackageName();
            return ((FileEntity) treeParent.getObject()).getLocation() + getPathToPackage(packageName, false);
        }
        FileEntity fileEntity = (FileEntity) treeEntity.getObject();
        return fileEntity.getParentFolder() != null ? fileEntity.getParentFolder().getLocation() : null;
    }
    
    private String getPathToPackage(String packageName, boolean insidePackage) {
        StringBuilder builder = new StringBuilder();
        String [] packages = packageName.split("\\.");
        int chainLength = packages.length;
        if (!insidePackage) {
            chainLength--;
        }
        for (int i = 0; i < chainLength; ++i) {
            builder.append(File.separator + packages[i]);
        }
        return builder.toString();
    }
}
