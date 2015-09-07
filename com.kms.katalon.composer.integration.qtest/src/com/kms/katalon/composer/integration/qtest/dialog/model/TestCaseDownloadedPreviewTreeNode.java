package com.kms.katalon.composer.integration.qtest.dialog.model;

import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.integration.qtest.entity.QTestTestCase;

public class TestCaseDownloadedPreviewTreeNode implements DownloadedPreviewTreeNode {
	private QTestTestCase testCase;
	private boolean isSelected;
	private ModuleDownloadedPreviewTreeNode parent;
	
	public TestCaseDownloadedPreviewTreeNode(ModuleDownloadedPreviewTreeNode parent, QTestTestCase testCase) {
		setTestCase(testCase);
		this.parent = parent;
	}
	
	public QTestTestCase getTestCase() {
		return testCase;
	}
	public void setTestCase(QTestTestCase testCase) {
		this.testCase = testCase;
	}

	@Override
	public ModuleDownloadedPreviewTreeNode getParent() {
		return parent;
	}

	@Override
	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	@Override
	public String getName() {
		return testCase.getName();
	}

	@Override
	public String getStatus() {		
		FolderEntity parentFolderEntity = parent.getFolderEntity();
		if (parentFolderEntity == null) return "New";
		
		try {
			if (getName().equalsIgnoreCase(
					TestCaseController.getInstance().getAvailableTestCaseName(parentFolderEntity, getName()))) {
				return "New";
			} else {
				return "New but Duplicated";
			}
		} catch (Exception e) {
			return "New";
		}
	}

	@Override
	public String getType() {
		return "Test Case";
	}
	
}
