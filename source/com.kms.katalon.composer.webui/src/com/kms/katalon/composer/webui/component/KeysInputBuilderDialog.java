package com.kms.katalon.composer.webui.component;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
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
import org.openqa.selenium.Keys;

import com.kms.katalon.composer.testcase.ast.dialogs.AbstractAstBuilderWithTableDialog;
import com.kms.katalon.composer.testcase.model.IInputValueType;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.webui.constants.StringConstants;
import com.kms.katalon.composer.webui.model.KeyInputValueType;
import com.kms.katalon.composer.webui.model.KeysInputValueType;
import com.kms.katalon.core.groovy.GroovyParser;

public class KeysInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private final IInputValueType[] defaultInputValueTypes = { InputValueType.String, new KeyInputValueType() };

    private static final String DIALOG_TITLE = StringConstants.KEYS_BUILDER_DIALOG_TITLE;
    private static final String[] COLUMN_NAMES = new String[] { StringConstants.NO_, StringConstants.KEYS_BUILDER_TABLE_COLUMN_TYPE_LABEL, StringConstants.VALUE };
    private static final String BUTTON_INSERT_LABEL = StringConstants.INSERT;
    private static final String BUTTON_REMOVE_LABEL = StringConstants.DELETE;

    private MethodCallExpression methodCallExpression;
    private ArgumentListExpression argumentListExpression;

    public KeysInputBuilderDialog(Shell parentShell, MethodCallExpression methodCallExpression, ClassNode scriptClass) {
        super(parentShell, scriptClass);
        if (methodCallExpression == null) {
            argumentListExpression = new ArgumentListExpression();
            this.methodCallExpression = new MethodCallExpression(new ClassExpression(new ClassNode(Keys.class)),
                    KeysInputValueType.KEYS_CHORDS_METHOD_NAME, argumentListExpression);
        } else {
            this.methodCallExpression = GroovyParser.cloneMethodCallExpression(methodCallExpression);
            argumentListExpression = (ArgumentListExpression) this.methodCallExpression.getArguments();
        }
    }

    protected void createButtonsForButtonBar(Composite parent) {
        Button btnInsert = createButton(parent, 100, BUTTON_INSERT_LABEL, true);
        btnInsert.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = tableViewer.getTable().getSelectionIndex();
                Expression newExpression = new PropertyExpression(new ClassExpression(new ClassNode(Keys.class)),
                        KeyInputValueType.ENTER_KEY_ENUM);
                if (selectionIndex < 0 || selectionIndex >= argumentListExpression.getExpressions().size()) {
                    argumentListExpression.getExpressions().add(newExpression);
                    tableViewer.getTable().setSelection(argumentListExpression.getExpressions().size() - 1);
                } else {
                    argumentListExpression.getExpressions().add(selectionIndex, newExpression);
                    tableViewer.getTable().setSelection(selectionIndex + 1);
                }
                tableViewer.refresh();
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
                if (index >= 0 && index < argumentListExpression.getExpressions().size()) {
                    argumentListExpression.getExpressions().remove(index);
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
    public MethodCallExpression getReturnValue() {
        return methodCallExpression;
    }

    @Override
    public void changeObject(Object originalObject, Object newObject) {
        int index = argumentListExpression.getExpressions().indexOf(originalObject);
        if (newObject instanceof Expression && argumentListExpression.getExpressions() != null && index >= 0
                && index < argumentListExpression.getExpressions().size()) {
            argumentListExpression.getExpressions().set(index, (Expression) newObject);
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
                    return String.valueOf(argumentListExpression.getExpressions().indexOf(element) + 1);
                }
                return "";
            }
        });
        
        TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnValueType = tableViewerColumnValueType.getColumn();
        tblclmnNewColumnValueType.setWidth(200);
        tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider(scriptClass));
        tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
                defaultInputValueTypes, KeysInputValueType.TAG_KEYS, this, scriptClass));

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnValue = tableViewerColumnValue.getColumn();
        tblclmnNewColumnValue.setWidth(200);
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
        tableViewer.setInput(argumentListExpression.getExpressions());
        tableViewer.refresh();
    }
}