package com.kms.katalon.composer.webui.component;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.editors.AbstractAstDialogCellEditor;

public class KeysInputCellEditor extends AbstractAstDialogCellEditor {

    public KeysInputCellEditor(Composite parent, String defaultContent, ClassNode scriptClass) {
        super(parent, defaultContent, scriptClass);
        this.setValidator(new ICellEditorValidator() {
            @Override
            public String isValid(Object value) {
                if (value instanceof MethodCallExpression || value == null) {
                    return null;
                }
                return getValidatorMessage(MethodCallExpression.class.getName());
            }
        });
    }

    @Override
    protected AstBuilderDialog getDialog(Shell shell) {
        return new KeysInputBuilderDialog(shell, (getValue() == null) ? null : (MethodCallExpression) getValue(), scriptClass);
    }

}
