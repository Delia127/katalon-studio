package com.kms.katalon.composer.keyword.handlers;

import javax.inject.Inject;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.dialogs.MessageDialog;

import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.keyword.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;

public class DeletePackageHandler extends DeleteKeywordHandler {

    @Inject
    private IEventBroker eventBroker;

    @Override
    public Class<? extends ITreeEntity> entityType() {
        return PackageTreeEntity.class;
    }

    @Override
    public boolean execute(ITreeEntity treeEntity, IProgressMonitor monitor) {
        if (treeEntity == null || !(treeEntity instanceof PackageTreeEntity)) {
            return false;
        }

        try {
            if (treeEntity.getObject() instanceof IPackageFragment) {
                String taskName = "Deleting " + treeEntity.getTypeName() + " '" + treeEntity.getText() + "'...";
                Object[] children = treeEntity.getChildren();
                monitor.beginTask(taskName, children.length + 1);

                for (Object child : children) {
                    if (monitor.isCanceled()) {
                        return false;
                    }
                    super.execute((ITreeEntity) child, new SubProgressMonitor(monitor, 1));
                }

                IPackageFragment packageFragment = (IPackageFragment) treeEntity.getObject();
                String parentPath = packageFragment.getParent().getElementName() + IPath.SEPARATOR;
                String packageName = packageFragment.getElementName().isEmpty() ? StringConstants.DEFAULT_PACKAGE_NAME
                        : packageFragment.getElementName();
                packageFragment.delete(true, null);

                eventBroker.post(EventConstants.EXPLORER_DELETED_SELECTED_ITEM, parentPath + packageName);
                monitor.worked(1);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.HAND_ERROR_MSG_UNABLE_TO_DELETE_KEYWORD);
            return false;
        } finally {
            monitor.done();
        }
    }
}
