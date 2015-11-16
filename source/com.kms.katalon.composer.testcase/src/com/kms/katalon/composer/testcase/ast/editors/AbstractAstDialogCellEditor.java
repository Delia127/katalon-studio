package com.kms.katalon.composer.testcase.ast.editors;

import org.codehaus.groovy.ast.ClassNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.ast.dialogs.AstBuilderDialog;

public abstract class AbstractAstDialogCellEditor extends AbstractDialogCellEditor {
	protected ClassNode scriptClass;

	public AbstractAstDialogCellEditor(Composite parent, String defaultContent, ClassNode scriptClass) {
		super(parent, defaultContent);
		this.scriptClass = scriptClass;
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		try {
			AstBuilderDialog dialog = getDialog(Display.getCurrent().getActiveShell());
			if (dialog != null && dialog.open() == Window.OK) {
				return dialog.getReturnValue();
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
		return null;
	}

	protected abstract AstBuilderDialog getDialog(Shell shell);
}
