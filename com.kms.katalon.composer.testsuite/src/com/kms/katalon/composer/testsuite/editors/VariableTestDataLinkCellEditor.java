package com.kms.katalon.composer.testsuite.editors;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.testsuite.dialogs.TestDataLinkFinderDialog;
import com.kms.katalon.entity.link.TestCaseTestDataLink;

public class VariableTestDataLinkCellEditor extends DialogCellEditor {
	
	private List<TestCaseTestDataLink> testDataLinks;
	private TestCaseTestDataLink selectedTestDataLink;
	
	public VariableTestDataLinkCellEditor(Composite parent, List<TestCaseTestDataLink> testDataLinks, TestCaseTestDataLink selectedTestDataLink) {
		super(parent);
		this.testDataLinks = testDataLinks;
		this.selectedTestDataLink = selectedTestDataLink;
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {		
		TestDataLinkFinderDialog dialog = new TestDataLinkFinderDialog(cellEditorWindow.getShell(), selectedTestDataLink,
				testDataLinks);
		
		if (dialog.open() == Dialog.OK) {
			return dialog.getSelectedTestDataLink();
		}
		return null;
	}

}
