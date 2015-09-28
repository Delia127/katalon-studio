package com.kms.katalon.entity.util;

public class ImportDuplicateEntityResult {
	private ImportType importType;
	private boolean applyToAll;
	
	public ImportDuplicateEntityResult(ImportType importType, boolean applyToAll) {
		this.importType = importType;
		this.applyToAll = applyToAll;
	}
	
	public ImportType getImportType() {
		return importType;
	}
	public void setImportType(ImportType importType) {
		this.importType = importType;
	}
	public boolean isApplyToAll() {
		return applyToAll;
	}
	public void setApplyToAll(boolean applyToAll) {
		this.applyToAll = applyToAll;
	}
	
	
}
