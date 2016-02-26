package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.RangeExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;

public class RangeInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private static final String TO_EXPRESSION = "To Expression";

    private static final String FROM_EXPRESSION = "From Expression";

    private final InputValueType[] defaultInputValueTypes = { InputValueType.String, InputValueType.Number,
            InputValueType.Boolean, InputValueType.Null, InputValueType.Variable, InputValueType.GlobalVariable,
            InputValueType.TestDataValue, InputValueType.MethodCall, InputValueType.Binary, InputValueType.Property };

    private RangeExpressionWrapper rangeExpression;

    public RangeInputBuilderDialog(Shell parentShell, RangeExpressionWrapper rangeExpression) {
        super(parentShell);
        if (rangeExpression == null) {
            throw new IllegalArgumentException();
        }
        this.rangeExpression = rangeExpression.clone();
    }

    @Override
    public void refresh() {
        List<Object> expressionList = new ArrayList<Object>();
        expressionList.add(rangeExpression.getFrom());
        expressionList.add(rangeExpression.getTo());
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(expressionList);
    }

    @Override
    public RangeExpressionWrapper getReturnValue() {
        return rangeExpression;
    }

    @Override
    public void replaceObject(Object originalObject, Object newObject) {
        if (!(newObject instanceof ExpressionWrapper)) {
            return;
        }
        if (originalObject == rangeExpression.getFrom()) {
            rangeExpression.setFrom((ExpressionWrapper) newObject);
            refresh();
        } else if (originalObject == rangeExpression.getTo()) {
            rangeExpression.setTo((ExpressionWrapper) newObject);
            refresh();
        }
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_RANGE_INPUT;
    }

    @Override
    protected void addTableColumns() {
        TableViewerColumn tableViewerColumnObject = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnObject.getColumn().setText(StringConstants.DIA_COL_OBJ);
        tableViewerColumnObject.getColumn().setWidth(100);
        tableViewerColumnObject.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == rangeExpression.getFrom()) {
                    return FROM_EXPRESSION;
                } else if (element == rangeExpression.getTo()) {
                    return TO_EXPRESSION;
                }
                return StringUtils.EMPTY;
            }
        });

        TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValueType.getColumn().setText(StringConstants.DIA_COL_VALUE_TYPE);
        tableViewerColumnValueType.getColumn().setWidth(100);
        tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider());
        tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
                defaultInputValueTypes, this));

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValue.getColumn().setText(StringConstants.DIA_COL_VALUE);
        tableViewerColumnValue.getColumn().setWidth(300);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider());
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this));

    }
}
