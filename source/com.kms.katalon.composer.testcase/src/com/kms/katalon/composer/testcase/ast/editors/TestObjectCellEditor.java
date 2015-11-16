package com.kms.katalon.composer.testcase.ast.editors;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.testcase.ast.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.TestObjectBuilderDialog;

public class TestObjectCellEditor extends AbstractAstDialogCellEditor {
	private boolean haveOtherTypes;
	public TestObjectCellEditor(Composite parent, String defaultContent, ClassNode scriptClass, boolean haveOtherTypes) {
		super(parent, defaultContent, scriptClass);
		this.haveOtherTypes = haveOtherTypes;
	}

	@Override
	protected AstBuilderDialog getDialog(Shell shell) {
		return new TestObjectBuilderDialog(shell, getValue() == null ? null : (Expression) getValue(),
				new EntityLabelProvider(), new EntityProvider(), new EntityViewerFilter(new EntityProvider()),
				scriptClass, haveOtherTypes);
	}

}
