package com.kms.katalon.composer.testcase.ast.editors;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.AbstractAstBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.ArgumentInputBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.IAstDialogBuilder;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputParameterBuilder;

public class InputCellEditor extends AstDialogCellEditor {
    private ASTNodeWrapper parentNode;

    public InputCellEditor(Composite parent, String defaultContent, ASTNodeWrapper parentNode) {
        super(parent, defaultContent, null);
        this.parentNode = parentNode;
        this.setValidator(new ICellEditorValidator() {
            @Override
            public String isValid(Object value) {
                if (!(value instanceof InputParameterBuilder)) {
                    return getValidatorMessage(InputParameterBuilder.class.getName()) ;
                }
                return null;
            }
        });
    }

    @Override
    protected AbstractAstBuilderDialog getDialog(Shell shell) {
        return new ArgumentInputBuilderDialog(shell, (InputParameterBuilder) getValue(), parentNode);
    }
    
    @Override
    protected Object getReturnValue(IAstDialogBuilder dialog) {
        return doGetValue();
    }
}
