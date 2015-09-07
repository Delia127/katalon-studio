package com.kms.katalon.code.refactoring.setting;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.core.setting.PropertySettingStore;

public class CodeRefactoringSettingStore {
	private static final String FILE_NAME = "com.kms.katalon.code.refactoring";
	private static final String MIGRATED_PROPERTY = "migrated";
	
	public static File getPropertyFile(String projectDir) throws IOException {
		File configFile = new File(projectDir + File.separator + PropertySettingStore.ROOT_FOLDER_NAME
				+ File.separator + FILE_NAME + PropertySettingStore.PROPERTY_FILE_EXENSION);
		if (!configFile.exists()) {
			configFile.createNewFile();
		}
		return configFile;		
	}
	
	public static boolean isMigrated(String projectDir) {		
		try {
			return Boolean.parseBoolean(PropertySettingStore.getPropertyValue(MIGRATED_PROPERTY,
					getPropertyFile(projectDir)));
		} catch (IOException e) {
			return false;
		}
	}
	
	public static void saveMigrated(String projectDir) {		
		try {
			PropertySettingStore.addNewProperty(MIGRATED_PROPERTY, Boolean.toString(true),
					getPropertyFile(projectDir));
		} catch (IOException | IllegalArgumentException e) {
			// Do nothing
		}
	}

}
