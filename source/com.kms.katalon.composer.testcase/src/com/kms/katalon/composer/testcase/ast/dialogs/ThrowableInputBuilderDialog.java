package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstructorCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.TupleExpressionWrapper;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;

public class ThrowableInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private static final String MESSAGE = "Message";

    private static final String THROWABLE_TYPE = "Throwable type";

    private ConstructorCallExpressionWrapper constructorCallExpression;

    public ThrowableInputBuilderDialog(Shell parentShell, ConstructorCallExpressionWrapper constructorCallExpression) {
        super(parentShell);
        if (constructorCallExpression == null
                || !(constructorCallExpression.getArguments() instanceof TupleExpressionWrapper)
                || ((TupleExpressionWrapper) constructorCallExpression.getArguments()).getExpressions().isEmpty()) {
            throw new IllegalArgumentException();
        }
        this.constructorCallExpression = constructorCallExpression.clone();
    }

    @Override
    public void refresh() {
        tableViewer.setContentProvider(new ArrayContentProvider());
        List<ASTNodeWrapper> expressionList = new ArrayList<ASTNodeWrapper>();
        expressionList.add(constructorCallExpression.getType());
        expressionList.add(((TupleExpressionWrapper) this.constructorCallExpression.getArguments()).getExpressions()
                .get(0));
        tableViewer.setInput(expressionList);
        tableViewer.refresh();
    }

    @Override
    public ConstructorCallExpressionWrapper getReturnValue() {
        return constructorCallExpression;
    }

    @Override
    public void replaceObject(Object originalObject, Object newObject) {
        if (originalObject == constructorCallExpression.getType() && newObject instanceof ClassNodeWrapper) {
            constructorCallExpression.setType((ClassNodeWrapper) newObject);
            refresh();
        } else if (originalObject == ((TupleExpressionWrapper) this.constructorCallExpression.getArguments())
                .getExpressions().get(0) && newObject instanceof ExpressionWrapper) {
            ((TupleExpressionWrapper) constructorCallExpression.getArguments()).getExpressions().set(0,
                    (ExpressionWrapper) newObject);
            refresh();
        }
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_THROWABLE_CALL_INPUT;
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
                if (element == constructorCallExpression.getType()) {
                    return THROWABLE_TYPE;
                } else if (element == ((TupleExpressionWrapper) constructorCallExpression.getArguments())
                        .getExpressions().get(0)) {
                    return MESSAGE;
                }
                return StringUtils.EMPTY;
            }
        });

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnValue = tableViewerColumnValue.getColumn();
        tblclmnNewColumnValue.setText(StringConstants.DIA_COL_VALUE);
        tblclmnNewColumnValue.setWidth(500);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider());
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this));
    }
}
