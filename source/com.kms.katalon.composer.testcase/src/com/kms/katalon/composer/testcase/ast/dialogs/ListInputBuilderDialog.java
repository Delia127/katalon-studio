package com.kms.katalon.composer.testcase.ast.dialogs;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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

public class ListInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private final InputValueType[] defaultInputValueTypes = { InputValueType.Constant, InputValueType.Variable,
            InputValueType.GlobalVariable, InputValueType.TestDataValue, InputValueType.MethodCall,
            InputValueType.Property };

    private static final String DIALOG_TITLE = StringConstants.DIA_TITLE_LIST_INPUT;
    private static final String[] COLUMN_NAMES = new String[] { StringConstants.DIA_COL_NO,
            StringConstants.DIA_COL_VALUE_TYPE, StringConstants.DIA_COL_CONSTANT_TYPE, StringConstants.DIA_COL_VALUE };
    private static final String BUTTON_INSERT_LABEL = StringConstants.DIA_BTN_INSERT;
    private static final String BUTTON_REMOVE_LABEL = StringConstants.DIA_BTN_REMOVE;

    private ListExpression listExpression;

    public ListInputBuilderDialog(Shell parentShell, ListExpression listExpression, ClassNode scriptClass) {
        super(parentShell, scriptClass);
        if (listExpression == null) {
            this.listExpression = AstTreeTableEntityUtil.getNewListExpression();
        } else {
            this.listExpression = GroovyParser.cloneListExpression(listExpression);
        }
    }

    protected void createButtonsForButtonBar(Composite parent) {
        Button btnInsert = createButton(parent, 100, BUTTON_INSERT_LABEL, true);
        btnInsert.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = tableViewer.getTable().getSelectionIndex();
                if (selectionIndex < 0 || selectionIndex >= listExpression.getExpressions().size()) {
                    listExpression.getExpressions().add(AstTreeTableEntityUtil.getNewStringConstantExpression());
                } else {
                    listExpression.getExpressions().add(selectionIndex,
                            AstTreeTableEntityUtil.getNewStringConstantExpression());
                }
                tableViewer.refresh();
                tableViewer.getTable().setSelection(selectionIndex + 1);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        Button btnRemove = createButton(parent, 200, BUTTON_REMOVE_LABEL, false);
        btnRemove.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = tableViewer.getTable().getSelectionIndex();
                if (index >= 0 && index < listExpression.getExpressions().size()) {
                    listExpression.getExpressions().remove(index);
                    tableViewer.refresh();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        super.createButtonsForButtonBar(parent);
    }

    @Override
    public ListExpression getReturnValue() {
        return listExpression;
    }

    @Override
    public void changeObject(Object originalObject, Object newObject) {
        int index = listExpression.getExpressions().indexOf(originalObject);
        if (newObject instanceof Expression && listExpression.getExpressions() != null && index >= 0
                && index < listExpression.getExpressions().size()) {
            listExpression.getExpressions().set(index, (Expression) newObject);
            tableViewer.refresh();
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
                    return String.valueOf(listExpression.getExpressions().indexOf(element) + 1);
                }
                return StringUtils.EMPTY;
            }
        });

        TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValueType.getColumn().setWidth(100);
        tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider(scriptClass));
        tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
                defaultInputValueTypes, ICustomInputValueType.TAG_LIST, this, scriptClass));

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
        tableViewer.setInput(listExpression.getExpressions());
        tableViewer.refresh();
    }
}
