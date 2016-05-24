package com.kms.katalon.composer.testcase.ast.dialogs;

import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ThrowStatementWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstContentProviderAdapter;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstInputValueTypeOptionsProvider;

public class ThrowBuilderDialog extends AbstractAstBuilderWithTableDialog {
    
    private final InputValueType[] defaultInputValueTypes = AstInputValueTypeOptionsProvider.getInputValueTypeOptions(AstInputValueTypeOptionsProvider.THROW_OPTIONS);
    private ThrowStatementWrapper throwStatement;

    public ThrowBuilderDialog(Shell parentShell, ThrowStatementWrapper throwStatement) {
        super(parentShell);
        this.throwStatement = throwStatement.clone();
    }

    @Override
    public ASTNodeWrapper getReturnValue() {
        return throwStatement;
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
                defaultInputValueTypes));

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValue.getColumn().setText(StringConstants.DIA_COL_VALUE);
        tableViewerColumnValue.getColumn().setWidth(500);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider());
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer));
    }

    @Override
    public void setInput() {
        tableViewer.setContentProvider(new AstContentProviderAdapter() {
            @Override
            public Object[] getElements(Object inputElement) {
                if (inputElement == throwStatement) {
                    return new Object[] { throwStatement.getExpression() };
                }
                return new Object[0];
            }
        });
        tableViewer.setInput(throwStatement);
    }
}
