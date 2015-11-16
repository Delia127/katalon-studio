package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.model.ICustomInputValueType;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputConstantTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderConstantTypeColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstTreeTableEntityUtil;
import com.kms.katalon.core.groovy.GroovyParser;

public class ClosureListInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private final InputValueType[] defaultInputValueTypes = { InputValueType.Constant, InputValueType.Variable,
            InputValueType.GlobalVariable, InputValueType.TestDataValue, InputValueType.MethodCall,
            InputValueType.Binary, InputValueType.Property };
    
    private static final String DIALOG_TITLE = StringConstants.DIA_TITLE_CLOSURE_LIST_INPUT;
    private static final String[] COLUMN_NAMES = new String[] { StringConstants.DIA_COL_NO,
            StringConstants.DIA_COL_VALUE_TYPE, StringConstants.DIA_COL_CONSTANT_TYPE, StringConstants.DIA_COL_VALUE };

    private ClosureListExpression closureListExpression;
    private List<Expression> expressionLinkedList;

    public ClosureListInputBuilderDialog(Shell parentShell, ClosureListExpression closureListExpression,
            ClassNode scriptClass) {
        super(parentShell, scriptClass);
        if (closureListExpression != null) {
            this.closureListExpression = GroovyParser.cloneClosureListExpression(closureListExpression);
        } else {
            this.closureListExpression = AstTreeTableEntityUtil.getNewClosureListExpression();
        }
        this.expressionLinkedList = this.closureListExpression.getExpressions();
    }

    @Override
    public ClosureListExpression getReturnValue() {
        return closureListExpression;
    }

    @Override
    public void changeObject(Object originalObject, Object newObject) {
        int index = expressionLinkedList.indexOf(originalObject);
        if (newObject instanceof Expression && closureListExpression != null && index >= 0
                && index < expressionLinkedList.size()) {
            expressionLinkedList.set(index, (Expression) newObject);
            refresh();
        }
    }

    @Override
    public String getDialogTitle() {
        return DIALOG_TITLE;
    }

    @Override
    protected void addTableColumns() {
        TableViewerColumn tableViewerColumnNo = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnColumnNo = tableViewerColumnNo.getColumn();
        tblclmnColumnNo.setWidth(40);
        tableViewerColumnNo.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof Expression) {
                    return String.valueOf(expressionLinkedList.indexOf(element) + 1);
                }
                return StringUtils.EMPTY;
            }
        });

        TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValueType.getColumn().setWidth(100);
        tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider(scriptClass));
        tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
                defaultInputValueTypes, ICustomInputValueType.TAG_CLOSURE_LIST, this, scriptClass));

        TableViewerColumn tableViewerColumnConstantType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnConstantType.getColumn().setWidth(100);
        tableViewerColumnConstantType.setLabelProvider(new AstInputConstantTypeLabelProvider());
        tableViewerColumnConstantType
                .setEditingSupport(new AstInputBuilderConstantTypeColumnSupport(tableViewer, this));

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnValue = tableViewerColumnValue.getColumn();
        tblclmnNewColumnValue.setWidth(170);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider(scriptClass));
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this, scriptClass));

        // set column's name
        for (int i = 0; i < tableViewer.getTable().getColumnCount(); i++) {
            tableViewer.getTable().getColumn(i).setText(COLUMN_NAMES[i]);
        }

    }

    @Override
    public void refresh() {
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(expressionLinkedList);
        tableViewer.refresh();
    }
}
