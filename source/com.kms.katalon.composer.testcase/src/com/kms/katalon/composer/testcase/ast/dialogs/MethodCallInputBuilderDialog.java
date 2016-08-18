package com.kms.katalon.composer.testcase.ast.dialogs;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.testcase.ast.editors.InputCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.MethodComboBoxCellEditor;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.composer.testcase.util.AstInputValueTypeOptionsProvider;

public class MethodCallInputBuilderDialog extends AbstractAstBuilderWithTableDialog {

    private final InputValueType[] defaultObjectValueTypes = AstInputValueTypeOptionsProvider.getInputValueTypeOptions(InputValueType.MethodCall);

    private MethodCallExpressionWrapper methodCallExpression;

    public MethodCallInputBuilderDialog(Shell parentShell, MethodCallExpressionWrapper methodCallExpression) {
        super(parentShell);
        this.methodCallExpression = methodCallExpression.clone();
    }

    @Override
    public ASTNodeWrapper getReturnValue() {
        return methodCallExpression;
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_METHOD_CALL_INPUT;
    }

    @Override
    protected void addTableColumns() {
        addTableColumnObjectType();

        addTableColumnObject();

        addTableColumnMethod();

        addTableColumnInput();
    }

    private void addTableColumnObjectType() {
        TableViewerColumn tableViewerColumnObjectType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnObjectType.getColumn().setText(StringConstants.DIA_COL_OBJ_TYPE);
        tableViewerColumnObjectType.getColumn().setWidth(100);
        tableViewerColumnObjectType.setLabelProvider(new AstInputTypeLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == methodCallExpression) {
                    return super.getText(methodCallExpression.getObjectExpression());
                }
                return "";
            }
        });
        tableViewerColumnObjectType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(
                tableViewer, defaultObjectValueTypes) {
            @Override
            protected Object getValue(Object element) {
                return super.getValue(methodCallExpression.getObjectExpression());
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return super.getCellEditor(methodCallExpression.getObjectExpression());
            }

            @Override
            protected void setValue(Object element, Object value) {
                super.setValue(methodCallExpression.getObjectExpression(), value);
            }

            @Override
            protected boolean canEdit(Object element) {
                return (element == methodCallExpression && super.canEdit(methodCallExpression.getObjectExpression()));
            }
        });
    }

    private void addTableColumnObject() {
        TableViewerColumn tableViewerColumnObject = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnClass = tableViewerColumnObject.getColumn();
        tblclmnNewColumnClass.setText(StringConstants.DIA_COL_OBJ);
        tblclmnNewColumnClass.setWidth(152);
        tableViewerColumnObject.setLabelProvider(new AstInputValueLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == methodCallExpression) {
                    return super.getText(methodCallExpression.getObjectExpression());
                }
                return "";
            }
        });

        tableViewerColumnObject.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer) {
            @Override
            protected Object getValue(Object element) {
                return super.getValue(methodCallExpression.getObjectExpression());
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return super.getCellEditor(methodCallExpression.getObjectExpression());
            }

            @Override
            protected void setValue(Object element, Object value) {
                super.setValue(methodCallExpression.getObjectExpression(), value);
            }

            @Override
            protected boolean canEdit(Object element) {
                return (element == methodCallExpression && super.canEdit(methodCallExpression.getObjectExpression()));
            }

            @Override
            protected void handleUpdateInputSuccessfully() {
                resetDefaultMethod(methodCallExpression);
                super.handleUpdateInputSuccessfully();
            }
        });
    }

    private void addTableColumnMethod() {
        TableViewerColumn tableViewerColumnMethod = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnMethod = tableViewerColumnMethod.getColumn();
        tblclmnNewColumnMethod.setText(StringConstants.DIA_COL_METHOD);
        tblclmnNewColumnMethod.setWidth(152);
        tableViewerColumnMethod.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == methodCallExpression) {
                    return methodCallExpression.getMethodAsString();
                }
                return "";
            }
        });

        tableViewerColumnMethod.setEditingSupport(new EditingSupport(tableViewer) {
            @Override
            protected void setValue(Object element, Object value) {
                if (value instanceof Method) {
                    Method newMethod = (Method) value;
                    if (MethodComboBoxCellEditor.compareMethodAndMethodCall(newMethod, methodCallExpression)) {
                        return;
                    }
                    methodCallExpression.setMethod(newMethod.getName());
                    AstKeywordsInputUtil.generateMethodCallArguments(methodCallExpression, newMethod);
                    getViewer().refresh();
                }
            }

            @Override
            protected Object getValue(Object element) {
                return methodCallExpression;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                MethodComboBoxCellEditor cellEditor = new MethodComboBoxCellEditor(tableViewer.getTable(),
                        getObjectType(methodCallExpression));
                processAutoSuggestion(cellEditor);
                return cellEditor;
            }

            @Override
            protected boolean canEdit(Object element) {
                return (element == methodCallExpression);
            }
        });
    }
    
    private void processAutoSuggestion(MethodComboBoxCellEditor cellEditor) {
        final KeyAdapter keyAdapter = new KeyAdapter() {
            private org.eclipse.swt.widgets.List listItem = null;

            @Override
            public void keyReleased(KeyEvent ke) {
                if (ke.keyCode == SWT.ARROW_UP || ke.keyCode == SWT.ARROW_DOWN) {
                    return;
                }
                CCombo combo = (CCombo) ke.getSource();
                if (!(combo.getDisplay().getFocusControl() instanceof Text)) {
                    return;
                }
                Text textControl = (Text) combo.getDisplay().getFocusControl();

                if (combo.getItemCount() <= 0) {
                    return;
                }
                combo.setListVisible(true);
                String[] items = combo.getItems();
                String curText = textControl.getText();
                int selectIndex = -1;
                for (int i = 0; i < items.length && curText.length() > 0; ++i) {
                    if (getMethodName(items[i]).startsWith(curText)) {
                        selectIndex = i;
                        break;
                    }
                }
                Control focusControl = combo.getDisplay().getFocusControl();
                if (focusControl instanceof org.eclipse.swt.widgets.List) {
                    focusControl.removeListener(SWT.FocusOut, focusControl.getListeners(SWT.FocusOut)[0]);
                    listItem = (org.eclipse.swt.widgets.List) focusControl;
                }
                if (listItem != null) {
                    listItem.setSelection(selectIndex);
                }
                textControl.setFocus();
            }

            private String getMethodName(String item) {
                if (item.contains("(")) {
                    return item.substring(0, item.indexOf("("));
                }
                return item;
            }
        };

        cellEditor.getControl().addKeyListener(keyAdapter);
    }


    private void addTableColumnInput() {
        TableViewerColumn tableViewerColumnInput = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnInput = tableViewerColumnInput.getColumn();
        tblclmnNewColumnInput.setText(StringConstants.DIA_COL_INPUT);
        tblclmnNewColumnInput.setWidth(170);
        tableViewerColumnInput.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element == methodCallExpression) {
                    return methodCallExpression.getArguments().getText();
                }
                return "";
            }
        });

        tableViewerColumnInput.setEditingSupport(new EditingSupport(tableViewer) {
            @Override
            protected void setValue(Object element, Object value) {
                if (!(value instanceof List<?>)) {
                    return;
                }
                List<?> inputParameters = (List<?>) value;
                ArgumentListExpressionWrapper argumentListExpression = new ArgumentListExpressionWrapper(
                        methodCallExpression);
                for (int i = 0; i < inputParameters.size(); i++) {
                    if (!(inputParameters.get(i) instanceof InputParameter)) {
                        continue;
                    }
                    InputParameter inputParameter = (InputParameter) inputParameters.get(i);
                    argumentListExpression.addExpression(inputParameter.getValueAsExpression());
                }
                methodCallExpression.setArguments(argumentListExpression);
                tableViewer.refresh();
            }

            @Override
            protected Object getValue(Object element) {
                Method method = findMethod(getObjectType(methodCallExpression), methodCallExpression);
                if (method == null) {
                    return Collections.emptyList();
                }
                return AstKeywordsInputUtil.generateInputParameters(method,
                        (ArgumentListExpressionWrapper) methodCallExpression.getArguments());
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new InputCellEditor(tableViewer.getTable(), methodCallExpression.getArguments().getText(),
                        methodCallExpression.getArguments());
            }

            @Override
            protected boolean canEdit(Object element) {
                return (element == methodCallExpression && MethodComboBoxCellEditor.getMethodCallParams(methodCallExpression).length > 0);
            }
        });
    }

    public void resetDefaultMethod(MethodCallExpressionWrapper methodCall) {
        Class<?> type = getObjectType(methodCall);
        if (type != null && type.getMethods().length > 0) {
            Method methodNode = type.getMethods()[0];
            methodCall.setMethod(methodNode.getName());
            AstKeywordsInputUtil.generateMethodCallArguments(methodCall, methodNode);
            return;
        }
        methodCall.setMethod(MethodCallExpressionWrapper.TO_STRING_METHOD_NAME);
        methodCall.setArguments(new ArgumentListExpressionWrapper(methodCall));
    }

    public Class<?> getObjectType(MethodCallExpressionWrapper methodCall) {
        if (methodCall.getObjectExpression().getText().equals(MethodCallExpressionWrapper.THIS_VARIABLE)) {
            return null;
        }
        return AstKeywordsInputUtil.loadType(methodCall.getObjectExpression().getText(), methodCall.getScriptClass());
    }

    private static Method findMethod(Class<?> type, MethodCallExpressionWrapper methodCall) {
        if (methodCall == null) {
            return null;
        }
        Method method = null;
        if (type != null) {
            method = findMatchingMethod(type, methodCall);
        }
        if (method == null) {
            return findMatchingMethod(DefaultGroovyMethods.class, methodCall);
        }
        return method;
    }

    private static Method findMatchingMethod(Class<?> type, MethodCallExpressionWrapper methodCall) {
        Method[] methods = type.getMethods();
        for (int index = 0; index < methods.length; index++) {
            Method methodNode = methods[index];
            if (MethodComboBoxCellEditor.compareMethodAndMethodCall(methodNode, methodCall)) {
                return methodNode;
            }
        }
        return null;
    }

    @Override
    public void setInput() {
        List<ExpressionWrapper> expressionList = new ArrayList<ExpressionWrapper>();
        expressionList.add(methodCallExpression);
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(expressionList);
        tableViewer.refresh();
    }
}
