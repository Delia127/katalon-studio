package com.kms.katalon.composer.objectrepository.handler;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.objectrepository.constant.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.objectspy.dialog.ObjectSpyDialog;

public class SpyObjectHandler {

    @Inject
    private IEventBroker eventBroker;

    private ObjectSpyDialog objectSpyDialog;

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell activeShell) {
        try {
            if (objectSpyDialog != null) {
                objectSpyDialog.dispose();
                objectSpyDialog.close();
            }
            objectSpyDialog = new ObjectSpyDialog(activeShell, LoggerSingleton.getInstance().getLogger(), eventBroker);
            objectSpyDialog.open();
        } catch (Exception e) {
            if (objectSpyDialog != null) {
                objectSpyDialog.dispose();
                objectSpyDialog.close();
            }
            LoggerSingleton.logError(e);
            MessageDialog.openError(activeShell, StringConstants.ERROR_TITLE, e.getMessage());
        }
    }

    // Temporary Comment out for old object spy for ie
    // String os = System.getProperty("os.name").toLowerCase();
    // // If windows
    // if ((os.contains("win"))) {
    // try {
    // String exePath = Util.getPhysicalLocation("tools/spytool/" +
    // Util.SPY_EXEC_FILE_NAME);
    // if (Util.isFileExist(exePath) &&
    // !Util.isProcessRunning(Util.SPY_EXEC_FILE_NAME)) {
    // String elementFile = exePath.replace(Util.SPY_EXEC_FILE_NAME,
    // "elements.csv");
    // String propertyFile = exePath.replace(Util.SPY_EXEC_FILE_NAME,
    // "properties.csv");
    //
    // Files.deleteIfExists(Paths.get(elementFile));
    // Files.deleteIfExists(Paths.get(propertyFile));
    // String[] cmdarray = { exePath };
    // Process process = Runtime.getRuntime().exec(cmdarray, null, null);
    // process.waitFor();
    // // Read captured objects if any
    // if (Util.isFileExist(elementFile) && Util.isFileExist(propertyFile))
    // {
    // Util.importElements(project, parentFolder, elementFile, propertyFile,
    // ObjectRepositoryController.getInstance());
    // // Refresh tree
    // if (eventBroker != null) {
    // eventBroker.post(EventConstants.EXPLORER_REFRESH, null);
    // }
    // }
    // }
    // } catch (Exception e1) {
    // MessageDialog.openError(Display.getCurrent().getActiveShell(),
    // "Error", e1.getMessage());
    // }
    // }
    @CanExecute
    private boolean canExecute() throws Exception {
        if (ProjectController.getInstance().getCurrentProject() != null) {
            return true;
        } else {
            return false;
        }
    }

    public static ITreeEntity getParentTreeEntity(Object[] selectedObjects) throws Exception {
        for (Object object : selectedObjects) {
            if (object instanceof ITreeEntity) {
                if (((ITreeEntity) object).getObject() instanceof FolderEntity) {
                    FolderEntity folder = (FolderEntity) ((ITreeEntity) object).getObject();
                    if (folder.getFolderType() == FolderType.WEBELEMENT) {
                        return (ITreeEntity) object;
                    }
                } else if (((ITreeEntity) object).getObject() instanceof WebElementEntity) {
                    return (ITreeEntity) ((ITreeEntity) object).getParent();
                }
            }
        }
        return null;
    }
}
