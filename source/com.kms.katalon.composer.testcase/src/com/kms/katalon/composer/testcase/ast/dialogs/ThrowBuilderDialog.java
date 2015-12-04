package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.model.ICustomInputValueType;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstTreeTableEntityUtil;
import com.kms.katalon.core.ast.GroovyParser;

public class ThrowBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private static final String DIALOG_TITLE = StringConstants.DIA_TITLE_CATCH_INPUT;
    private final InputValueType[] defaultInputValueTypes = { InputValueType.Throwable, InputValueType.Variable };
    
    private ThrowStatement throwStatement;

    public ThrowBuilderDialog(Shell parentShell, ThrowStatement throwStatement, ClassNode scriptClass) {
        super(parentShell, scriptClass);
        this.scriptClass = scriptClass;
        if (throwStatement != null) {
            this.throwStatement = GroovyParser.cloneThrowStatement(throwStatement);
        } else {
            this.throwStatement = AstTreeTableEntityUtil.getNewThrowStatement();
        }
    }

    @Override
    public ThrowStatement getReturnValue() {
        return throwStatement;
    }

    @Override
    public void changeObject(Object originalObject, Object newObject) {
        if (originalObject == throwStatement.getExpression() && newObject instanceof Expression) {
            throwStatement.setExpression((Expression) newObject);
            refresh();
        }
    }

    @Override
    public String getDialogTitle() {
        return DIALOG_TITLE;
    }

    @Override
    protected void addTableColumns() {
        TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValueType.getColumn().setText(StringConstants.DIA_COL_VALUE_TYPE);
        tableViewerColumnValueType.getColumn().setWidth(100);
        tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider(scriptClass));
        tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
                defaultInputValueTypes, ICustomInputValueType.TAG_THROW, this, scriptClass));

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValue.getColumn().setText(StringConstants.DIA_COL_VALUE);
        tableViewerColumnValue.getColumn().setWidth(500);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider(scriptClass));
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this, scriptClass));
    }

    @Override
    public void refresh() {
        List<ASTNode> expressionList = new ArrayList<ASTNode>();
        expressionList.add(throwStatement.getExpression());
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(expressionList);
        tableViewer.refresh();
    }
}
