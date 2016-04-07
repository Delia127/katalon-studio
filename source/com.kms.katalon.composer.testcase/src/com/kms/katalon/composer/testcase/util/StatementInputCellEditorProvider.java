package com.kms.katalon.composer.testcase.util;

import java.util.HashMap;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.ast.editors.BooleanCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.CaseCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.CatchCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.ForInputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.SwitchCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.ThrowInputCellEditor;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.AssertStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CaseStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CatchStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ElseIfStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ForStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.IfStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.SwitchStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ThrowStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.WhileStatementWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;

/**
 * Created by taittle on 3/24/16.
 */
public class StatementInputCellEditorProvider {
    private static HashMap<String, ASTCellEditorProvider<? extends StatementWrapper>> inputClasses;

    private static final ASTCellEditorProvider<CaseStatementWrapper> caseCellEditorProvider = new ASTCellEditorProvider<CaseStatementWrapper>() {
        @Override
        public CaseCellEditor getEditor(Composite parent, CaseStatementWrapper caseStatement) {
            return new CaseCellEditor(parent, caseStatement.getInputText());
        }
    };

    private static final ASTCellEditorProvider<ForStatementWrapper> forCellEditorProvider = new ASTCellEditorProvider<ForStatementWrapper>() {
        @Override
        public ForInputCellEditor getEditor(Composite parent, ForStatementWrapper forStatement) {
            return new ForInputCellEditor(parent, forStatement.getInputText());
        }
    };

    private static final ASTCellEditorProvider<WhileStatementWrapper> whileCellEditorProvider = new ASTCellEditorProvider<WhileStatementWrapper>() {
        @Override
        public BooleanCellEditor getEditor(Composite parent, WhileStatementWrapper whileStatement) {
            return new BooleanCellEditor(parent, whileStatement.getInputText());
        }
    };

    private static final ASTCellEditorProvider<IfStatementWrapper> ifCellEditorProvider = new ASTCellEditorProvider<IfStatementWrapper>() {
        @Override
        public BooleanCellEditor getEditor(Composite parent, IfStatementWrapper ifStatement) {
            return new BooleanCellEditor(parent, ifStatement.getInputText());
        }
    };

    private static final ASTCellEditorProvider<ElseIfStatementWrapper> elseIfCellEditorProvider = new ASTCellEditorProvider<ElseIfStatementWrapper>() {
        @Override
        public BooleanCellEditor getEditor(Composite parent, ElseIfStatementWrapper elseIfStatement) {
            return new BooleanCellEditor(parent, elseIfStatement.getInputText());
        }
    };

    private static final ASTCellEditorProvider<ThrowStatementWrapper> throwCellEditorProvider = new ASTCellEditorProvider<ThrowStatementWrapper>() {
        @Override
        public ThrowInputCellEditor getEditor(Composite parent, ThrowStatementWrapper throwStatement) {
            return new ThrowInputCellEditor(parent, throwStatement.getInputText());
        }
    };

    private static final ASTCellEditorProvider<SwitchStatementWrapper> switchCellEditorProvider = new ASTCellEditorProvider<SwitchStatementWrapper>() {
        @Override
        public SwitchCellEditor getEditor(Composite parent, SwitchStatementWrapper switchStatement) {
            return new SwitchCellEditor(parent, switchStatement.getInputText());
        }
    };

    private static final ASTCellEditorProvider<CatchStatementWrapper> catchCellEditorProvider = new ASTCellEditorProvider<CatchStatementWrapper>() {
        @Override
        public CatchCellEditor getEditor(Composite parent, CatchStatementWrapper catchStatement) {
            return new CatchCellEditor(parent, catchStatement.getInputText());
        }
    };

    private static final ASTCellEditorProvider<AssertStatementWrapper> assertCellEditorProvider = new ASTCellEditorProvider<AssertStatementWrapper>() {
        @Override
        public BooleanCellEditor getEditor(Composite parent, AssertStatementWrapper assertStatement) {
            return new BooleanCellEditor(parent, assertStatement.getInputText());
        }
    };

    private static final ASTCellEditorProvider<ExpressionStatementWrapper> expressionStatementCellEditorProvider = new ASTCellEditorProvider<ExpressionStatementWrapper>() {
        @Override
        public CellEditor getEditor(Composite parent, ExpressionStatementWrapper expressionStatement) {
            ExpressionWrapper expression = expressionStatement.getExpression();
            InputValueType inputValueType = AstInputValueTypeProvider.getInputValueTypeForASTNode(expression);
            if (inputValueType != null) {
                return inputValueType.getCellEditorForValue(parent, expression);
            }
            return null;
        }
    };

    static {
        inputClasses = new HashMap<>();
        inputClasses.put(IfStatementWrapper.class.getSimpleName(), ifCellEditorProvider);
        inputClasses.put(ElseIfStatementWrapper.class.getSimpleName(), elseIfCellEditorProvider);
        inputClasses.put(SwitchStatementWrapper.class.getSimpleName(), switchCellEditorProvider);
        inputClasses.put(CaseStatementWrapper.class.getSimpleName(), caseCellEditorProvider);
        inputClasses.put(ForStatementWrapper.class.getSimpleName(), forCellEditorProvider);
        inputClasses.put(WhileStatementWrapper.class.getSimpleName(), whileCellEditorProvider);
        inputClasses.put(ThrowStatementWrapper.class.getSimpleName(), throwCellEditorProvider);
        inputClasses.put(CatchStatementWrapper.class.getSimpleName(), catchCellEditorProvider);
        inputClasses.put(AssertStatementWrapper.class.getSimpleName(), assertCellEditorProvider);
        inputClasses.put(ExpressionStatementWrapper.class.getSimpleName(), expressionStatementCellEditorProvider);
    }

    @SuppressWarnings("unchecked")
    public static <T extends StatementWrapper> CellEditor getEditorForInput(Composite parent, T statement) {
        ASTCellEditorProvider<T> provider = (ASTCellEditorProvider<T>) inputClasses.get(statement.getClass()
                .getSimpleName());
        if (provider != null) {
            return provider.getEditor(parent, statement);
        }
        return null;
    }
    
    interface ASTCellEditorProvider<T extends ASTNodeWrapper> {
        CellEditor getEditor(Composite parent, T input);
    }
}
