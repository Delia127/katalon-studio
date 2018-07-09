package com.kms.katalon.composer.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.preferences.internal.LoadedPreferenceStore;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class ImportSettingsHandler extends AbstractHandler {

	private static final String CONFIGS_FOLDER = "config";
	
	private static final String CORE_RUNTIME_SETTINGS_FOLDER = File.separator + ".metadata"+
																File.separator +".plugins"+
																File.separator +"org.eclipse.core.runtime"+
																File.separator +".settings";
	
	private static final String KATALON_PREFS_FILE_PATTERN = "(com.kms.katalon[^\\s]+(\\.(?i)(prefs))$)";

	@Override
	public boolean canExecute() {
		return true;
	}

	@Execute
	public void execute() {
		try {
			Shell shell = Display.getCurrent().getActiveShell();
			DirectoryDialog directoryDialog = new DirectoryDialog(shell, SWT.SYSTEM_MODAL);
			directoryDialog.setMessage(MessageConstants.HAND_IMPORT_SETTINGS_MSG);
			directoryDialog.setFilterPath(Platform.getLocation().toString());
			String selectedFolder = directoryDialog.open();
			boolean shouldBeBack = true;
			while (shouldBeBack) {
				if (selectedFolder.endsWith(CONFIGS_FOLDER)) {
					File configDirectory = new File(selectedFolder);
					if (configDirectory != null && configDirectory.exists() && configDirectory.isDirectory()) {
						copyConfigDirectorEventHandler(shell, configDirectory.getAbsolutePath());
					}
					shouldBeBack = false;
				} else {									
					MessageDialog.openError(shell, StringConstants.ERROR_TITLE,
		                    MessageConstants.HAND_IMPORT_SETTINGS_MSG_ERROR);
					directoryDialog.setFilterPath(Platform.getLocation().toString());
					selectedFolder = directoryDialog.open();
					shouldBeBack = true;
				}
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}
	
	@Inject
    @Optional
    private void copyConfigDirectorEventHandler(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell,
            @UIEventTopic(EventConstants.PROJECT_OPEN) final String configDirectory) 
            		throws Exception {		
		List<Path> files = scanFiles(configDirectory, KATALON_PREFS_FILE_PATTERN);
		String configFolder = Platform.getLocation().toString() + CORE_RUNTIME_SETTINGS_FOLDER;
		
		if (files.size() > 0) { 
			files.forEach(f -> {
				try {
					String destFile = configFolder + File.separator + f.getFileName();
					Files.copy(f, new File(destFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
					
					String prefs = f.getFileName().toString();
					String qualifier = prefs.substring(0, prefs.length() - 6);
					
					ScopedPreferenceStore scopedPreferenceStore = PreferenceStoreManager.getPreferenceStore(qualifier);
					importProperties(scopedPreferenceStore, new FileInputStream(f.toFile()));
					
					LoadedPreferenceStore.getInstance().put(scopedPreferenceStore);
					
				} catch (Exception e) {
					LoggerSingleton.logError(e);
				}
			});
			
			MessageDialog.openInformation(shell, StringConstants.INFO,
                    MessageConstants.HAND_IMPORT_SETTINGS_MSG_SUCCESSFULL);
		}
		
    }
	
	private List<Path> scanFiles(String path, String extensionPattern) throws IOException {
        List<Path> filePaths = new LinkedList<>();
        Path rootPath = createPath(path);
        Files.walkFileTree(rootPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path visitedFile, BasicFileAttributes attrs) throws IOException {
                String fileName = visitedFile.toFile().getName();
                boolean matched = Pattern.matches(extensionPattern, fileName);
                if (matched) {
                    filePaths.add(visitedFile);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return filePaths;
    }
	
	private Path createPath(String path) {
        FileSystem fileSystem = FileSystems.getDefault();
        return fileSystem.getPath(path);
    }
	
	private void importProperties(ScopedPreferenceStore store, InputStream input) {
		// read the file into a properties object
		Properties properties = new Properties();
		try {
			properties.load(input);
		} catch (Exception e) {
			// ignore
		} finally {
			try {
				input.close();
			} catch (IOException e) {
				// ignore
			}
		}
		
		for (Iterator<?> i = properties.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();
			String value = properties.getProperty(key);
			store.putValue(key, value);
		}
	}
}
