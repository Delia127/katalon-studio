package com.kms.katalon.composer.testcase.util;

import com.kms.katalon.composer.testcase.ast.editors.*;
import com.kms.katalon.composer.testcase.groovy.ast.statements.*;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

import java.util.HashMap;

/**
 * Created by taittle on 3/24/16.
 */
public class CellEditorProvider {
    private static HashMap<String, Provider> inputClasses;

    private static Provider caseCellEditorProvider = new Provider() {
        @Override
        public CellEditor getEditor(Composite parent, String inputText) {
            return new CaseCellEditor(parent, inputText);
        }
    };

    private static Provider forCellEditorProvider = new Provider() {
        @Override
        public CellEditor getEditor(Composite parent, String inputText) {
            return new ForInputCellEditor(parent, inputText);
        }
    };

    private static Provider whileCellEditorProvider = new Provider() {
        @Override
        public CellEditor getEditor(Composite parent, String inputText) {
            return new BooleanCellEditor(parent, inputText);
        }
    };

    private static Provider ifCellEditorProvider = new Provider() {
        @Override
        public CellEditor getEditor(Composite parent, String inputText) {
            return new BooleanCellEditor(parent, inputText);
        }
    };

    private static Provider elseIfCellEditorProvider = new Provider() {
        @Override
        public CellEditor getEditor(Composite parent, String inputText) {
            return new BooleanCellEditor(parent, inputText);
        }
    };

    private static Provider throwCellEditorProvider = new Provider() {
        @Override
        public CellEditor getEditor(Composite parent, String inputText) {
            return new ThrowInputCellEditor(parent, inputText);
        }
    };

    private static Provider switchCellEditorProvider = new Provider() {
        @Override
        public CellEditor getEditor(Composite parent, String inputText) {
            return new SwitchCellEditor(parent, inputText);
        }
    };

    private static Provider catchCellEditorProvider = new Provider() {
        @Override
        public CellEditor getEditor(Composite parent, String inputText) {
            return new CatchCellEditor(parent, inputText);
        }
    };

    private static Provider assertCellEditorProvider = new Provider() {
        @Override
        public CellEditor getEditor(Composite parent, String inputText) {
            return new BooleanCellEditor(parent, inputText);
        }
    };


    static {
        inputClasses = new HashMap<>();
        inputClasses.put(CaseStatementWrapper.class.getSimpleName(), caseCellEditorProvider);
        inputClasses.put(ForStatementWrapper.class.getSimpleName(), forCellEditorProvider);
        inputClasses.put(WhileStatementWrapper.class.getSimpleName(), whileCellEditorProvider);
        inputClasses.put(ThrowStatementWrapper.class.getSimpleName(), throwCellEditorProvider);
        inputClasses.put(IfStatementWrapper.class.getSimpleName(), ifCellEditorProvider);
        inputClasses.put(SwitchStatementWrapper.class.getSimpleName(), switchCellEditorProvider);
        inputClasses.put(ElseIfStatementWrapper.class.getSimpleName(), elseIfCellEditorProvider);
        inputClasses.put(CatchStatementWrapper.class.getSimpleName(), catchCellEditorProvider);
        inputClasses.put(AssertStatementWrapper.class.getSimpleName(), assertCellEditorProvider);
    }

    public static CellEditor getEditorForInput(Composite parent, String inputClassName, String inputText) {
        Provider provider = inputClasses.get(inputClassName);
        if (provider != null) {
            return provider.getEditor(parent, inputText);
        }

        return null;
    }

    interface Provider {
        CellEditor getEditor(Composite parent, String inputText);
    }
}
