package com.kms.katalon.entity.util;

public class ImportDuplicateEntityParameter {
	private ImportType[] availableImportTypes;
	private String message;
	
	public ImportDuplicateEntityParameter(ImportType[] availableImportTypes, String message) {
		this.setAvailableImportTypes(availableImportTypes);
		this.setMessage(message);
	}

	public ImportType[] getAvailableImportTypes() {
		return availableImportTypes;
	}

	private void setAvailableImportTypes(ImportType[] availableImportTypes) {
		this.availableImportTypes = availableImportTypes;
	}

	public String getMessage() {
		return message;
	}

	private void setMessage(String message) {
		this.message = message;
	}

	
	
}
