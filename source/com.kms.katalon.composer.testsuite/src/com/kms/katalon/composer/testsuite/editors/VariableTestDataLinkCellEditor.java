package com.kms.katalon.composer.testsuite.editors;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;
import com.kms.katalon.composer.testsuite.dialogs.TestDataLinkFinderDialog;
import com.kms.katalon.entity.link.TestCaseTestDataLink;

public class VariableTestDataLinkCellEditor extends AbstractDialogCellEditor {
	
	private List<TestCaseTestDataLink> testDataLinks;
	private TestCaseTestDataLink selectedTestDataLink;
	
	public VariableTestDataLinkCellEditor(Composite parent, List<TestCaseTestDataLink> testDataLinks, TestCaseTestDataLink selectedTestDataLink) {
		super(parent, null);
		this.testDataLinks = testDataLinks;
		this.selectedTestDataLink = selectedTestDataLink;
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {		
		TestDataLinkFinderDialog dialog = new TestDataLinkFinderDialog(Display.getCurrent().getActiveShell(), selectedTestDataLink,
				testDataLinks);
		if (dialog.open() == Dialog.OK) {
			return dialog.getSelectedTestDataLink();
		}
		return null;
	}

}
