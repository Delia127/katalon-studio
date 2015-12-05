package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.util.AstTreeTableEntityUtil;
import com.kms.katalon.core.ast.AstTextValueUtil;
import com.kms.katalon.core.ast.GroovyParser;

public class CatchInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private static final String DIALOG_TITLE = StringConstants.DIA_TITLE_CATCH_INPUT;
    private CatchStatement catchStatement;
    private ClassNode scriptClass;

    public CatchInputBuilderDialog(Shell parentShell, CatchStatement catchStatement, ClassNode scriptClass) {
        super(parentShell, scriptClass);
        this.scriptClass = scriptClass;
        if (catchStatement != null) {
            this.catchStatement = GroovyParser.cloneCatchStatement(catchStatement);
        } else {
            this.catchStatement = AstTreeTableEntityUtil.getNewCatchStatement();
        }
    }

    @Override
    public CatchStatement getReturnValue() {
        return catchStatement;
    }

    @Override
    public void changeObject(Object originalObject, Object newObject) {
        if (originalObject == catchStatement.getExceptionType() && newObject instanceof ClassNode) {
            catchStatement = new CatchStatement(new Parameter((ClassNode) newObject, catchStatement.getVariable()
                    .getName()), catchStatement.getCode());
            refresh();
        } else if (originalObject == catchStatement.getVariable() && newObject instanceof Parameter) {
            catchStatement = new CatchStatement((Parameter) newObject, catchStatement.getCode());
            refresh();
        }
    }

    @Override
    public String getDialogTitle() {
        return DIALOG_TITLE;
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
                    return AstTextValueUtil.getTextValue(catchStatement.getExceptionType());
                }
                return StringUtils.EMPTY;
            }
        });
        tableViewerColumnExeptionType.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this,
                scriptClass) {
            @Override
            protected Object getValue(Object element) {
                return super.getValue(catchStatement.getVariable().getType());
            }

            @Override
            protected void setValue(Object element, Object value) {
                super.setValue(catchStatement.getVariable().getType(), value);
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element == catchStatement && catchStatement.getExceptionType() != null) {
                    return true;
                }
                return false;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return super.getCellEditor(catchStatement.getVariable().getType());
            }
        });

        TableViewerColumn tableViewerVariable = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerVariable.getColumn().setText(StringConstants.DIA_COL_VARIABLE_NAME);
        tableViewerVariable.getColumn().setWidth(200);
        tableViewerVariable.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == catchStatement && catchStatement.getExceptionType() != null) {
                    return AstTextValueUtil.getTextValue(catchStatement.getVariable());
                }
                return StringUtils.EMPTY;
            }
        });

        tableViewerVariable.setEditingSupport(new EditingSupport(tableViewer) {
            @Override
            protected void setValue(Object element, Object value) {
                if (value instanceof String) {
                    Parameter newVariable = new Parameter(catchStatement.getVariable()
                            .getType(), (String) value);
                    changeObject(catchStatement.getVariable(), newVariable);
                    getViewer().refresh();
                }
            }

            @Override
            protected Object getValue(Object element) {
                return catchStatement.getVariable().getName();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor(tableViewer.getTable());
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element == catchStatement && catchStatement.getVariable() != null) {
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void refresh() {
        List<ASTNode> expressionList = new ArrayList<ASTNode>();
        expressionList.add(catchStatement);
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(expressionList);
        tableViewer.refresh();
    }
}
