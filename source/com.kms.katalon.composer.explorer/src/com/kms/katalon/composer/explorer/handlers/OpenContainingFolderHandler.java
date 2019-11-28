package com.kms.katalon.composer.explorer.handlers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.jface.util.Util;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.handler.CommonExplorerHandler;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.constants.ComposerExplorerMessageConstants;
import com.kms.katalon.constants.GlobalMessageConstants;
import com.kms.katalon.entity.file.FileEntity;
@SuppressWarnings("restriction")

public class OpenContainingFolderHandler extends CommonExplorerHandler {

	/**
	 * Parameter, which can optionally be passed to the command.
	 */
	private static final String VARIABLE_RESOURCE = "${selected_resource_loc}"; //$NON-NLS-1$
	private static final String VARIABLE_RESOURCE_URI = "${selected_resource_uri}"; //$NON-NLS-1$
	private static final String VARIABLE_FOLDER = "${selected_resource_parent_loc}"; //$NON-NLS-1$
	
    @CanExecute
    public boolean canExecute() {
        if (!isExplorerPartActive()) {
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
			FileEntity fileEntity = (FileEntity) treeEntity.getObject();
			String launchCmd = formShowInSytemExplorerCommand(new File(fileEntity.getLocation()));
			File dir = new File(parent);

			Process p;
			if (Util.isLinux() || Util.isMac()) {
				p = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", launchCmd }, null, dir); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				p = Runtime.getRuntime().exec(launchCmd, null, dir);
			}
			int retCode = p.waitFor();
			if (retCode != 0 && !Util.isWindows()) {
				LoggerSingleton.logError("Failed with return code in process" +  retCode);
			}
		} catch (Exception e) {
			MultiStatusErrorDialog.showErrorDialog(e, GlobalMessageConstants.ERROR,
					ComposerExplorerMessageConstants.ERROR_CANNOT_FIND_CONTAINING_FOLDER);
		}
    }
    
    // method of class ShowInSystemExplorerHanlder.class
	private String formShowInSytemExplorerCommand(File path) throws IOException {
		String command = IDEWorkbenchPlugin.getDefault().getPreferenceStore()
				.getString(IDEInternalPreferences.WORKBENCH_SYSTEM_EXPLORER);

		command = Util.replaceAll(command, VARIABLE_RESOURCE, quotePath(path.getCanonicalPath()));
		command = Util.replaceAll(command, VARIABLE_RESOURCE_URI, path.getCanonicalFile().toURI().toString());
		File parent = path.getParentFile();
		if (parent != null) {
			command = Util.replaceAll(command, VARIABLE_FOLDER, quotePath(parent.getCanonicalPath()));
		}
		return command;
	}

	// method of class ShowInSystemExplorerHanlder.class
	private String quotePath(String path) {
		if (Util.isLinux() || Util.isMac()) {
			// Quote for usage inside "", man sh, topic QUOTING:
			path = path.replaceAll("[\"$`]", "\\\\$0"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		// Windows: Can't quote, since explorer.exe has a very special command
		// line parsing strategy.
		return path;
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
		return fileEntity.getParentFolder() != null ? fileEntity.getParentFolder().getLocation()
				: fileEntity.getLocationProjectFolder();
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
