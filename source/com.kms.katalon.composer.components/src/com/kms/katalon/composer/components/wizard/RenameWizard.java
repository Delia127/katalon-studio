package com.kms.katalon.composer.components.wizard;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;

import com.kms.katalon.composer.components.constants.StringConstants;
import com.kms.katalon.composer.components.tree.ITreeEntity;

public class RenameWizard extends Wizard {
	
	private static final String RENAME_WIZARD_TITLE = StringConstants.WIZ_RENAME_WIZARD_TITLE;
	private ITreeEntity treeEntity;
	private String newNameValue;
	private List<String> existingNames;
	
	public RenameWizard(ITreeEntity treeEntity, List<String> existingNames) {
		super();
		this.treeEntity = treeEntity;
		this.existingNames = existingNames;
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public String getWindowTitle() {
		return RENAME_WIZARD_TITLE;
	}

	@Override
	public void addPages() {
		addPage(new NewNamePage());
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public ITreeEntity getTreeEntity() {
		return treeEntity;
	}

	public void setTreeEntity(ITreeEntity treeEntity) {
		this.treeEntity = treeEntity;
	}

	public String getNewNameValue() {
		return newNameValue;
	}

	public void setNewNameValue(String newNameValue) {
	    if (newNameValue != null) {
            // trim and replace multiple space by single one
            newNameValue = newNameValue.trim().replaceAll("\\s+", " ");
        }
		this.newNameValue = newNameValue;
	}

	public List<String> getExistingNames() {
		return existingNames;
	}

	public void setExistingNames(List<String> existingNames) {
		this.existingNames = existingNames;
	}

}
