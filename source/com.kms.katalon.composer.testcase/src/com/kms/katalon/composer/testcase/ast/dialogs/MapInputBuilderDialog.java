package com.kms.katalon.composer.testcase.ast.dialogs;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
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
import com.kms.katalon.composer.testcase.model.IInputValueType;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstTreeTableEntityUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;
import com.kms.katalon.core.groovy.GroovyParser;

public class MapInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private final InputValueType[] defaultInputValueTypes = { InputValueType.String, InputValueType.Number,
            InputValueType.Boolean, InputValueType.Null, InputValueType.Variable, InputValueType.GlobalVariable,
            InputValueType.TestDataValue, InputValueType.MethodCall, InputValueType.Property };
    private static final String DIALOG_TITLE = StringConstants.DIA_TITLE_MAP_INPUT;
    private static final String BUTTON_INSERT_LABEL = StringConstants.DIA_BTN_INSERT;
    private static final String BUTTON_REMOVE_LABEL = StringConstants.DIA_BTN_REMOVE;

    private MapExpression mapExpression;

    public MapInputBuilderDialog(Shell parentShell, MapExpression mapExpression, ClassNode scriptClass) {
        super(parentShell, scriptClass);
        if (mapExpression == null) {
            this.mapExpression = AstTreeTableEntityUtil.getNewMapExpression();
        } else {
            this.mapExpression = GroovyParser.cloneMapExpression(mapExpression);
        }
    }

    protected void createButtonsForButtonBar(Composite parent) {
        Button btnInsert = createButton(parent, 100, BUTTON_INSERT_LABEL, true);
        btnInsert.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = tableViewer.getTable().getSelectionIndex();
                MapEntryExpression mapEntryExpression = AstTreeTableEntityUtil.getNewMapEntryExpression();

                if (selectionIndex < 0 || selectionIndex >= mapExpression.getMapEntryExpressions().size()) {
                    mapExpression.getMapEntryExpressions().add(mapEntryExpression);
                } else {
                    mapExpression.getMapEntryExpressions().add(selectionIndex, mapEntryExpression);
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
                if (index >= 0 && index < mapExpression.getMapEntryExpressions().size()) {
                    mapExpression.getMapEntryExpressions().remove(index);
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
    public MapExpression getReturnValue() {
        return mapExpression;
    }

    @Override
    public void changeObject(Object originalObject, Object newObject) {
        // Do nothing
    }

    @Override
    public String getDialogTitle() {
        return DIALOG_TITLE;
    }

    @Override
    protected void addTableColumns() {
        TableViewerColumn tableViewerColumnNo = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnColumnNo = tableViewerColumnNo.getColumn();
        tblclmnColumnNo.setText(StringConstants.DIA_COL_NO);
        tblclmnColumnNo.setWidth(40);
        tableViewerColumnNo.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof MapEntryExpression) {
                    return String.valueOf(mapExpression.getMapEntryExpressions().indexOf(element) + 1);
                }
                return StringUtils.EMPTY;
            }
        });

        TableViewerColumn tableViewerColumnKeyType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnKeyType.getColumn().setText(StringConstants.DIA_COL_KEY_TYPE);
        tableViewerColumnKeyType.getColumn().setWidth(100);
        tableViewerColumnKeyType.setLabelProvider(new AstInputTypeLabelProvider(scriptClass) {
            @Override
            public String getText(Object element) {
                if (element instanceof MapEntryExpression) {
                    return super.getText(((MapEntryExpression) element).getKeyExpression());
                }
                return StringUtils.EMPTY;
            }
        });

        tableViewerColumnKeyType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
                defaultInputValueTypes, ICustomInputValueType.TAG_MAP, this, scriptClass) {
            @Override
            protected void setValue(Object element, Object value) {
                if (element instanceof ASTNode && value instanceof Integer && (int) value > -1
                        && (int) value < inputValueTypeNames.size()) {
                    String newValueTypeString = inputValueTypeNames.get((int) value);
                    IInputValueType newValueType = AstTreeTableInputUtil
                            .getInputValueTypeFromString(newValueTypeString);
                    IInputValueType oldValueType = AstTreeTableValueUtil.getTypeValue(
                            ((MapEntryExpression) element).getKeyExpression(), scriptClass);
                    if (newValueType != oldValueType) {
                        ASTNode astNode = (ASTNode) newValueType.getNewValue(element);
                        if (astNode instanceof Expression) {
                            ((MapEntryExpression) element).setKeyExpression((Expression) astNode);
                            tableViewer.refresh();
                        }
                    }
                }
            }

            @Override
            protected Object getValue(Object element) {
                if (element instanceof MapEntryExpression) {
                    return super.getValue(((MapEntryExpression) element).getKeyExpression());
                }
                return 0;
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element instanceof MapEntryExpression) {
                    return super.canEdit(((MapEntryExpression) element).getKeyExpression());
                }
                return false;
            }
        });

        TableViewerColumn tableViewerColumnKeyValue = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnKeyValue = tableViewerColumnKeyValue.getColumn();
        tblclmnNewColumnKeyValue.setText(StringConstants.DIA_COL_KEY);
        tblclmnNewColumnKeyValue.setWidth(170);
        tableViewerColumnKeyValue.setLabelProvider(new AstInputValueLabelProvider(scriptClass) {
            @Override
            public String getText(Object element) {
                if (element instanceof MapEntryExpression) {
                    return super.getText(((MapEntryExpression) element).getKeyExpression());
                }
                return StringUtils.EMPTY;
            }
        });

        tableViewerColumnKeyValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this,
                scriptClass) {

            @Override
            protected void setValue(Object element, Object value) {
                if (element instanceof MapEntryExpression) {
                    MapEntryExpression mapEntryExpression = ((MapEntryExpression) element);
                    Object object = AstTreeTableValueUtil.setValue(mapEntryExpression.getKeyExpression(), value,
                            scriptClass);
                    if (object instanceof Expression) {
                        mapEntryExpression.setKeyExpression((Expression) object);
                        tableViewer.refresh();
                    }
                }
            }

            @Override
            protected Object getValue(Object element) {
                if (element instanceof MapEntryExpression) {
                    return super.getValue(((MapEntryExpression) element).getKeyExpression());
                }
                return StringUtils.EMPTY;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                if (element instanceof MapEntryExpression) {
                    return super.getCellEditor(((MapEntryExpression) element).getKeyExpression());
                }
                return null;
            }
        });

        TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValueType.getColumn().setText(StringConstants.DIA_COL_VALUE_TYPE);
        tableViewerColumnValueType.getColumn().setWidth(100);
        tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider(scriptClass) {
            @Override
            public String getText(Object element) {
                if (element instanceof MapEntryExpression) {
                    return super.getText(((MapEntryExpression) element).getValueExpression());
                }
                return StringUtils.EMPTY;
            }
        });

        tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
                defaultInputValueTypes, ICustomInputValueType.TAG_MAP, this, scriptClass) {
            @Override
            protected void setValue(Object element, Object value) {
                if (element instanceof ASTNode && value instanceof Integer && (int) value > -1
                        && (int) value < inputValueTypeNames.size()) {
                    String newValueTypeString = inputValueTypeNames.get((int) value);
                    IInputValueType newValueType = AstTreeTableInputUtil
                            .getInputValueTypeFromString(newValueTypeString);
                    IInputValueType oldValueType = AstTreeTableValueUtil.getTypeValue(
                            ((MapEntryExpression) element).getValueExpression(), scriptClass);
                    if (newValueType != oldValueType) {
                        ASTNode astNode = (ASTNode) newValueType.getNewValue(element);
                        if (astNode instanceof Expression) {
                            ((MapEntryExpression) element).setValueExpression((Expression) astNode);
                            tableViewer.refresh();
                        }
                    }
                }
            }

            @Override
            protected Object getValue(Object element) {
                if (element instanceof MapEntryExpression) {
                    return super.getValue(((MapEntryExpression) element).getValueExpression());
                }
                return 0;
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element instanceof MapEntryExpression) {
                    return super.canEdit(((MapEntryExpression) element).getValueExpression());
                }
                return false;
            }
        });

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNewColumnValue = tableViewerColumnValue.getColumn();
        tblclmnNewColumnValue.setText(StringConstants.DIA_COL_VALUE);
        tblclmnNewColumnValue.setWidth(170);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider(scriptClass) {
            @Override
            public String getText(Object element) {
                if (element instanceof MapEntryExpression) {
                    return super.getText(((MapEntryExpression) element).getValueExpression());
                }
                return StringUtils.EMPTY;
            }
        });

        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this, scriptClass) {
            @Override
            protected void setValue(Object element, Object value) {
                if (element instanceof MapEntryExpression) {
                    MapEntryExpression mapEntryExpression = ((MapEntryExpression) element);
                    Object object = AstTreeTableValueUtil.setValue(mapEntryExpression.getValueExpression(), value,
                            scriptClass);
                    if (object instanceof Expression) {
                        mapEntryExpression.setValueExpression((Expression) object);
                        tableViewer.refresh();
                    }
                }
            }

            @Override
            protected Object getValue(Object element) {
                if (element instanceof MapEntryExpression) {
                    return super.getValue(((MapEntryExpression) element).getValueExpression());
                }
                return StringUtils.EMPTY;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                if (element instanceof MapEntryExpression) {
                    return super.getCellEditor(((MapEntryExpression) element).getValueExpression());
                }
                return null;
            }
        });
    }

    @Override
    public void refresh() {
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(mapExpression.getMapEntryExpressions());
        tableViewer.refresh();
    }
}
