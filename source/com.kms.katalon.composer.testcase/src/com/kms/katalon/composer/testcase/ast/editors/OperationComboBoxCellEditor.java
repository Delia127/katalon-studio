package com.kms.katalon.composer.testcase.ast.editors;

import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.groovy.ast.TokenWrapper;

public class OperationComboBoxCellEditor extends ComboBoxCellEditor {
    private TokenWrapper token;

    public static final int[] OPERATION_CODES = new int[] { Types.COMPARE_EQUAL, Types.COMPARE_GREATER_THAN,
            Types.COMPARE_LESS_THAN, Types.COMPARE_GREATER_THAN_EQUAL, Types.COMPARE_LESS_THAN_EQUAL, Types.COMPARE_NOT_EQUAL,
            Types.LOGICAL_AND, Types.LOGICAL_OR, Types.EQUALS, Types.PLUS, Types.MINUS, Types.MULTIPLY, Types.DIVIDE,
            Types.PLUS_EQUAL, Types.MINUS_EQUAL, Types.MULTIPLY_EQUAL, Types.DIVIDE_EQUAL, Types.LOGICAL_AND_EQUAL,
            Types.LOGICAL_OR_EQUAL, Types.INTDIV, Types.MOD, Types.STAR_STAR, Types.INTDIV_EQUAL, Types.MOD_EQUAL,
            Types.POWER_EQUAL, Types.COMPARE_IDENTICAL, Types.COMPARE_NOT_IDENTICAL, Types.COMPARE_TO, Types.LEFT_SHIFT,
            Types.LEFT_SHIFT_EQUAL, Types.RIGHT_SHIFT, Types.RIGHT_SHIFT_UNSIGNED, Types.RIGHT_SHIFT_EQUAL,
            Types.RIGHT_SHIFT_UNSIGNED_EQUAL };

    public OperationComboBoxCellEditor(Composite parent) {
        super(parent, new String[0]);
        String[] operationCodeStrings = new String[OPERATION_CODES.length];

        for (int i = 0; i < OPERATION_CODES.length; i++) {
            operationCodeStrings[i] = Types.getText(OPERATION_CODES[i]);
        }
        setItems(operationCodeStrings);
    }

    /**
     * Accept a {@link TokenWrapper} object
     * 
     * @param a
     *            {@link TokenWrapper} object
     */
    @Override
    protected void doSetValue(Object value) {
        Assert.isTrue(value instanceof TokenWrapper);
        token = (TokenWrapper) value;
        for (int index = 0; index < OPERATION_CODES.length; index++) {
            if (OPERATION_CODES[index] == token.getType()) {
                super.doSetValue(index);
                return;
            }
        }
        super.doSetValue(0);
    }

    /**
     * @return the selected {@link TokenWrapper}
     */
    @Override
    protected TokenWrapper doGetValue() {
        Integer selectionIndex = (Integer) super.doGetValue();
        if (selectionIndex < 0 || selectionIndex >= OPERATION_CODES.length) {
            return null;
        }
        token.setToken(Token.newSymbol(OPERATION_CODES[selectionIndex], -1, -1));
        return token;
    }
}
