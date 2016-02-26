package com.kms.katalon.composer.testcase.ast.editors;

import java.util.List;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.ast.dialogs.ArgumentInputBuilderDialog;
import com.kms.katalon.composer.testcase.ast.dialogs.AstBuilderDialog;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.model.InputParameter;

public class InputCellEditor extends AstDialogCellEditor {
    private ASTNodeWrapper parentNode;

    public InputCellEditor(Composite parent, String defaultContent, ASTNodeWrapper parentNode) {
        super(parent, defaultContent, null);
        this.parentNode = parentNode;
        this.setValidator(new ICellEditorValidator() {
            @Override
            public String isValid(Object value) {
                if (value instanceof List<?>) {
                    List<?> list = (List<?>) value;
                    boolean isValid = true;
                    for (Object object : list) {
                        if (!(object instanceof InputParameter)) {
                            isValid = false;
                        }
                    }
                    if (isValid) {
                        return null;
                    }
                }
                return getValidatorMessage("java.util.List<" + InputParameter.class.getName() + ">");
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    protected AstBuilderDialog getDialog(Shell shell) {
        return new ArgumentInputBuilderDialog(shell, (List<InputParameter>) getValue(), parentNode);
    }
}
