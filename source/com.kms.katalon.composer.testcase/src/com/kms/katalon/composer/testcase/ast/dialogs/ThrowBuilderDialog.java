package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ThrowStatementWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;

public class ThrowBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private final InputValueType[] defaultInputValueTypes = { InputValueType.Throwable, InputValueType.Variable };
    
    private ThrowStatementWrapper throwStatement;

    public ThrowBuilderDialog(Shell parentShell, ThrowStatementWrapper throwStatement) {
        super(parentShell);
        if (throwStatement == null) {
            throw new IllegalArgumentException();
        }
        this.throwStatement = throwStatement.clone();
    }

    @Override
    public ThrowStatementWrapper getReturnValue() {
        return throwStatement;
    }

    @Override
    public void replaceObject(Object originalObject, Object newObject) {
        if (originalObject == throwStatement.getExpression() && newObject instanceof ExpressionWrapper) {
            throwStatement.setExpression((ExpressionWrapper) newObject);
            refresh();
        }
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_CATCH_INPUT;
    }

    @Override
    protected void addTableColumns() {
        TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValueType.getColumn().setText(StringConstants.DIA_COL_VALUE_TYPE);
        tableViewerColumnValueType.getColumn().setWidth(100);
        tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider());
        tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
                defaultInputValueTypes, this));

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValue.getColumn().setText(StringConstants.DIA_COL_VALUE);
        tableViewerColumnValue.getColumn().setWidth(500);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider());
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this));
    }

    @Override
    public void refresh() {
        List<ASTNodeWrapper> expressionList = new ArrayList<ASTNodeWrapper>();
        expressionList.add(throwStatement.getExpression());
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(expressionList);
        tableViewer.refresh();
    }
}
