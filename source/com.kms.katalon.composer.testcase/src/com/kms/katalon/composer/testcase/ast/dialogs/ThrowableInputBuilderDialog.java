package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.util.AstTreeTableEntityUtil;
import com.kms.katalon.core.ast.GroovyParser;

public class ThrowableInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private static final String DIALOG_TITLE = StringConstants.DIA_TITLE_THROWABLE_CALL_INPUT;

    private ConstructorCallExpression constructorCallExpression;
    private ClassNode type;
    private Expression message;

    public ThrowableInputBuilderDialog(Shell parentShell, ConstructorCallExpression constructorCallExpression,
            ClassNode scriptClass) {
        super(parentShell, scriptClass);
        if (constructorCallExpression != null) {
            this.constructorCallExpression = GroovyParser.cloneConstructorCallExpression(constructorCallExpression);
        } else {
            this.constructorCallExpression = AstTreeTableEntityUtil.getNewExceptionExpression();
        }
        type = constructorCallExpression.getType();
        message = ((TupleExpression) constructorCallExpression.getArguments()).getExpressions().get(0);
    }

    @Override
    public void refresh() {
        TupleExpression arguments = new TupleExpression();
        arguments.addExpression(message);
        constructorCallExpression = new ConstructorCallExpression(type, message);
        tableViewer.setContentProvider(new ArrayContentProvider());
        List<ASTNode> expressionList = new ArrayList<ASTNode>();
        expressionList.add(type);
        expressionList.add(message);
        tableViewer.setInput(expressionList);
        tableViewer.refresh();
    }

    @Override
    public ConstructorCallExpression getReturnValue() {
        return constructorCallExpression;
    }

    @Override
    public void changeObject(Object originalObject, Object newObject) {
        if (originalObject == type && newObject instanceof ClassNode) {
            type = (ClassNode) newObject;
            refresh();
        } else if (originalObject == message && newObject instanceof Expression) {
            message = (Expression) newObject;
            refresh();
        }
    }

    @Override
    public String getDialogTitle() {
        return DIALOG_TITLE;
    }

    @Override
    protected void addTableColumns() {
        TableViewerColumn tableViewerColumnObject = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnObject = tableViewerColumnObject.getColumn();
        tblclmnNewColumnObject.setWidth(100);
        tblclmnNewColumnObject.setText(StringConstants.DIA_COL_OBJ);
        tableViewerColumnObject.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == type) {
                    return "Throwable type";
                } else if (element == message) {
                    return "Message";
                }
                return StringUtils.EMPTY;
            }
        });

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnValue = tableViewerColumnValue.getColumn();
        tblclmnNewColumnValue.setText(StringConstants.DIA_COL_VALUE);
        tblclmnNewColumnValue.setWidth(500);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider(scriptClass));
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this, scriptClass));
    }
}
