package com.kms.katalon.composer.testcase.editors;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.model.ContentProposalCheck;

public class ComboBoxCellEditorWithContentProposal extends ComboBoxCellEditor {
	private ContentProposalCheck contentProposalCheck;
	
	
	public ComboBoxCellEditorWithContentProposal(Composite parent, String[] items, ContentProposalCheck contentProposalCheck) {
		super(parent, items);
		this.contentProposalCheck = contentProposalCheck;
	}
	
	@Override
	protected void focusLost() {
		if (contentProposalCheck.isProposing()) {
			// Do Nothing
		} else {
			super.focusLost();
		}
	}

	@Override
	protected boolean dependsOnExternalFocusListener() {
		return false;
	}
	
	public void loseFocus() {
		focusLost();
	}
	
	
}
