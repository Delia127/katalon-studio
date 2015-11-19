package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.internal.ui.JavaUIMessages;
import org.eclipse.jdt.internal.ui.dialogs.OpenTypeSelectionDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;

@SuppressWarnings("restriction")
public class TypeInputCellEditor extends AbstractDialogCellEditor {
	public TypeInputCellEditor(Composite parent, String defaultContent) {
		super(parent, defaultContent);
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		OpenTypeSelectionDialog dialog = new OpenTypeSelectionDialog(Display.getCurrent().getActiveShell(), false,
				PlatformUI.getWorkbench().getProgressService(), null, IJavaSearchConstants.TYPE);
		dialog.setTitle(JavaUIMessages.OpenTypeAction_dialogTitle);
		dialog.setMessage(JavaUIMessages.OpenTypeAction_dialogMessage);
		dialog.setInitialPattern(defaultContent);
		if (dialog.open() == Window.OK && dialog.getResult().length == 1) {
			return dialog.getResult()[0];
		} else {
			return null;
		}
	}
}
