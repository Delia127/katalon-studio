package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CatchStatementWrapper;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;

public class CatchInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private CatchStatementWrapper catchStatement;

    public CatchInputBuilderDialog(Shell parentShell, CatchStatementWrapper catchStatement) {
        super(parentShell);
        if (catchStatement == null) {
            throw new IllegalArgumentException();
        }
        this.catchStatement = catchStatement.clone();
    }

    @Override
    public CatchStatementWrapper getReturnValue() {
        return catchStatement;
    }

    @Override
    public void replaceObject(Object originalObject, Object newObject) {
        if (originalObject == catchStatement.getExceptionType() && newObject instanceof ClassNodeWrapper) {
            catchStatement.setExceptionType((ClassNodeWrapper) newObject);
            refresh();
        } else if (originalObject == catchStatement.getVariableName() && newObject instanceof String) {
            catchStatement.setVariableName((String) newObject);
            refresh();
        }
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_CATCH_INPUT;
    }

    @Override
    protected void addTableColumns() {
        TableViewerColumn tableViewerColumnExeptionType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnExeptionType.getColumn().setText(StringConstants.DIA_COL_EXCEPTION_TYPE);
        tableViewerColumnExeptionType.getColumn().setWidth(200);
        tableViewerColumnExeptionType.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == catchStatement && catchStatement.getExceptionType() != null) {
                    return catchStatement.getExceptionType().getNameWithoutPackage();
                }
                return StringUtils.EMPTY;
            }
        });
        tableViewerColumnExeptionType.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this) {
            @Override
            protected Object getValue(Object element) {
                return super.getValue(catchStatement.getExceptionType());
            }

            @Override
            protected void setValue(Object element, Object value) {
                super.setValue(catchStatement.getExceptionType(), value);
            }

            @Override
            protected boolean canEdit(Object element) {
                return (element == catchStatement && catchStatement.getExceptionType() != null && super.canEdit(catchStatement.getExceptionType()));
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return super.getCellEditor(catchStatement.getExceptionType());
            }
        });

        TableViewerColumn tableViewerVariable = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerVariable.getColumn().setText(StringConstants.DIA_COL_VARIABLE_NAME);
        tableViewerVariable.getColumn().setWidth(200);
        tableViewerVariable.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == catchStatement && catchStatement.getVariableName() != null) {
                    return catchStatement.getVariableName();
                }
                return StringUtils.EMPTY;
            }
        });

        tableViewerVariable.setEditingSupport(new EditingSupport(tableViewer) {
            @Override
            protected void setValue(Object element, Object value) {
                if (value instanceof String) {
                    replaceObject(catchStatement.getVariableName(), value);
                    getViewer().refresh();
                }
            }

            @Override
            protected Object getValue(Object element) {
                return catchStatement.getVariableName();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor(tableViewer.getTable());
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element == catchStatement && catchStatement.getVariable() != null
                        && catchStatement.getVariableName() != null) {
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void refresh() {
        List<ASTNodeWrapper> expressionList = new ArrayList<ASTNodeWrapper>();
        expressionList.add(catchStatement);
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(expressionList);
        tableViewer.refresh();
    }
}
