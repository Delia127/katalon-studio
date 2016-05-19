package com.kms.katalon.composer.testcase.ast.dialogs;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.RangeExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.model.InputValueTypeUtil;
import com.kms.katalon.composer.testcase.providers.AstContentProviderAdapter;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.providers.UneditableTableCellLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;

public class RangeInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private static final String TO_EXPRESSION = "To Expression";

    private static final String FROM_EXPRESSION = "From Expression";

    private final InputValueType[] defaultInputValueTypes = InputValueTypeUtil.getValueTypeOptions(InputValueType.Range);

    private RangeExpressionWrapper rangeExpression;

    public RangeInputBuilderDialog(Shell parentShell, RangeExpressionWrapper rangeExpression) {
        super(parentShell);
        this.rangeExpression = rangeExpression.clone();
    }

    @Override
    public void setInput() {
        tableViewer.setContentProvider(new AstContentProviderAdapter() {
            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement == rangeExpression) {
                    return new Object[] { rangeExpression.getFrom(), rangeExpression.getTo() };
                }
                return new Object[0];
            }
        });
        tableViewer.setInput(rangeExpression);
    }

    @Override
    public RangeExpressionWrapper getReturnValue() {
        return rangeExpression;
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
        tableViewerColumnObject.setLabelProvider(new UneditableTableCellLabelProvider() {
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
                defaultInputValueTypes));

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValue.getColumn().setText(StringConstants.DIA_COL_VALUE);
        tableViewerColumnValue.getColumn().setWidth(300);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider());
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer));

    }
}
