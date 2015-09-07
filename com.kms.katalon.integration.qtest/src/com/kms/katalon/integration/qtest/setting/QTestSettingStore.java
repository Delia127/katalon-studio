package com.kms.katalon.integration.qtest.setting;

import java.io.File;
import java.io.IOException;

import com.kms.katalon.core.setting.PropertySettingStore;

public class QTestSettingStore {
	private static final String FILE_NAME = "com.kms.katalon.integration.qtest";
	private static final String TOKEN_PROPERTY = "token";
	private static final String USERNAME_PROPERTY = "username";
	private static final String PASSWORD_PROPERTY = "password";
	private static final String SERVER_URL_PROPERTY = "serverUrl";
	private static final String AUTO_SUBMIT_RESULT_PROPERTY = "autoSubmitResult";
	private static final String ENABLE_INTEGRATION_PROPERTY = "enableIntegration";
	private static final String SEND_ATTACHMENTS_PROPERTY = "sendAttachments";
	private static final String CHECK_BEFORE_UPLOADING = "checkBeforeUploading";
	
	public static File getPropertyFile(String projectDir) throws IOException {
		File configFile = new File(projectDir + File.separator + PropertySettingStore.ROOT_FOLDER_NAME
				+ File.separator + FILE_NAME + PropertySettingStore.PROPERTY_FILE_EXENSION);
		if (!configFile.exists()) {
			configFile.createNewFile();
		}
		return configFile;		
	}
	
	public static String getToken(String projectDir) {
		try {
			return PropertySettingStore.getPropertyValue(TOKEN_PROPERTY, getPropertyFile(projectDir));
		} catch (IOException e) {
			return "";
		}
	}
	
	public static void saveToken(String token, String projectDir) throws IOException {
		PropertySettingStore.addNewProperty(TOKEN_PROPERTY, token, getPropertyFile(projectDir));
	}

	public static String getUsername(String projectDir) {
		try {
			return PropertySettingStore.getPropertyValue(USERNAME_PROPERTY, getPropertyFile(projectDir));
		} catch (IOException e) {
			return "";
		}
	}

	public static String getPassword(String projectDir) {
		try {
			return PropertySettingStore.getPropertyValue(PASSWORD_PROPERTY, getPropertyFile(projectDir));
		} catch (IOException e) {
			return "";
		}
	}

	public static String getServerUrl(String projectDir) {
		try {
			return PropertySettingStore.getPropertyValue(SERVER_URL_PROPERTY, getPropertyFile(projectDir));
		} catch (IOException e) {
			return "";
		}
	}
	
	public static QTestAttachmentSendingType getAttachmentSendingType(String projectDir) {
		try {
			String sendingTypeName = PropertySettingStore.getPropertyValue(SEND_ATTACHMENTS_PROPERTY,
					getPropertyFile(projectDir));
			if (sendingTypeName != null && !sendingTypeName.isEmpty()) {
				return QTestAttachmentSendingType.valueOf(sendingTypeName);
			} else {
				return QTestAttachmentSendingType.NOT_SEND;
			}
		} catch (IOException | IllegalArgumentException e) {
			return QTestAttachmentSendingType.NOT_SEND;
		}
		
	}
	
	public static void saveUserProfile(String serverUrl, String username, String password, String projectDir)
			throws IOException {
		PropertySettingStore.addNewProperty(USERNAME_PROPERTY, username, getPropertyFile(projectDir));
		PropertySettingStore.addNewProperty(PASSWORD_PROPERTY, password, getPropertyFile(projectDir));
		
		String savedServerUrl = serverUrl;
		while (!savedServerUrl.isEmpty() && savedServerUrl.endsWith("/")) {
			savedServerUrl = savedServerUrl.substring(0, savedServerUrl.length() - 1);
		}
		
		PropertySettingStore.addNewProperty(SERVER_URL_PROPERTY, savedServerUrl, getPropertyFile(projectDir));
	}

	public static boolean isAutoSubmitResultActive(String projectDir) {
		try {
			return Boolean.parseBoolean(PropertySettingStore.getPropertyValue(AUTO_SUBMIT_RESULT_PROPERTY,
					getPropertyFile(projectDir)));
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean isIntegrationActive(String projectDir) {
		try {
			return Boolean.parseBoolean(PropertySettingStore.getPropertyValue(ENABLE_INTEGRATION_PROPERTY,
					getPropertyFile(projectDir)));
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean isEnableCheckBeforeUploading(String projectDir) {
		try {
			return Boolean.parseBoolean(PropertySettingStore.getPropertyValue(CHECK_BEFORE_UPLOADING,
					getPropertyFile(projectDir)));
		} catch (IOException e) {
			return false;
		}
	}
	
	
	public static void saveAutoSubmit(boolean autoSubmit, String projectDir)
			throws IOException {
		PropertySettingStore.addNewProperty(AUTO_SUBMIT_RESULT_PROPERTY, Boolean.toString(autoSubmit),
				getPropertyFile(projectDir));
	}
	
	
	public static void saveEnableIntegration(boolean isIntegration, String projectDir)
			throws IOException {
		PropertySettingStore.addNewProperty(ENABLE_INTEGRATION_PROPERTY, Boolean.toString(isIntegration),
				getPropertyFile(projectDir));
	}
	
	public static void saveEnableCheckBeforeUploading(boolean isEnableCheck, String projectDir)
			throws IOException {
		PropertySettingStore.addNewProperty(CHECK_BEFORE_UPLOADING, Boolean.toString(isEnableCheck),
				getPropertyFile(projectDir));
	}
	
	public static void saveAttachmentSendingType(QTestAttachmentSendingType type, String projectDir) {
		try {
			PropertySettingStore.addNewProperty(SEND_ATTACHMENTS_PROPERTY, type.name(),
					getPropertyFile(projectDir));
		} catch (IOException | IllegalArgumentException e) {
			// Do nothing
		}
	}
}
