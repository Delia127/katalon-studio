package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.model.ConstantValueType;
import com.kms.katalon.composer.testcase.model.ICustomInputValueType;
import com.kms.katalon.composer.testcase.model.IInputValueType;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.model.InputParameterClass;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputConstantTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderConstantTypeColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.testcase.TestCase;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class InputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private final InputValueType[] defaultInputValueTypes = { InputValueType.Constant, InputValueType.Variable,
            InputValueType.GlobalVariable, InputValueType.TestDataValue, InputValueType.Binary, InputValueType.Boolean,
            InputValueType.TestCase, InputValueType.TestData, InputValueType.TestObject, InputValueType.MethodCall,
            InputValueType.Property, InputValueType.List, InputValueType.Map };
    private static final String DIALOG_TITLE = StringConstants.DIA_TITLE_INPUT;
    private static final String[] COLUMN_NAMES = new String[] { StringConstants.DIA_COL_NO,
            StringConstants.DIA_COL_PARAM_TYPE, StringConstants.DIA_COL_PARAM, StringConstants.DIA_COL_VALUE_TYPE,
            StringConstants.DIA_COL_CONSTANT_TYPE, StringConstants.DIA_COL_VALUE };

    private List<InputParameter> inputParameters;
    private List<InputParameter> originalParameters;

    public InputBuilderDialog(Shell parentShell, List<InputParameter> inputParameters, ClassNode scriptClass) {
        super(parentShell, scriptClass);
        originalParameters = inputParameters;
        this.inputParameters = new ArrayList<InputParameter>();
        for (InputParameter inputParameter : inputParameters) {
            if (inputParameter.getParamType() != null) {
                if (TestCaseEntityUtil.isClassChildOf(TestObject.class.getName(), inputParameter.getParamType()
                        .getFullName())) {
                    continue;
                }

                if (TestCaseEntityUtil.isClassChildOf(TestCase.class.getName(), inputParameter.getParamType()
                        .getFullName())) {
                    continue;
                }
                this.inputParameters.add(inputParameter);
            } else {
                this.inputParameters.add(inputParameter);
            }
        }
    }

    protected void updateTestCaseBindingInputParameters(MethodCallExpression testCaseArgument) {
        Expression objectExpression = AstTreeTableInputUtil.getCallTestCaseParam(testCaseArgument);
        if (objectExpression != null) {
            try {
                TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByDisplayId(
                        objectExpression.getText());
                if (testCase != null) {
                    for (InputParameter input : inputParameters) {
                        if (input.getParamType() != null) {
                            if (input.getParamType().getFullName().equals(Map.class.getName())) {
                                input.setValue(AstTreeTableInputUtil
                                        .generateTestCaseVariableBindingExpression(testCase));
                                tableViewer.update(input, null);
                            }
                        }

                    }
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }

    }

    @Override
    public List<InputParameter> getReturnValue() {
        return originalParameters;
    }

    @Override
    public void changeObject(Object originalObject, Object newObject) {
        int inputIndex = inputParameters.indexOf(originalObject);
        if (inputIndex > -1 && inputIndex < inputParameters.size()) {
            InputParameter inputParameter = inputParameters.get(inputIndex);
            inputParameter.setValue(newObject);
            if (newObject instanceof MethodCallExpression
                    && AstTreeTableInputUtil.isCallTestCaseArgument((MethodCallExpression) newObject)) {
                updateTestCaseBindingInputParameters((MethodCallExpression) newObject);
            }
            tableViewer.update(newObject, null);
        }
    }

    @Override
    public String getDialogTitle() {
        return DIALOG_TITLE;
    }

    @Override
    protected void addTableColumns() {
        TableViewerColumn tableViewerColumnNo = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnNo.getColumn().setWidth(40);
        tableViewerColumnNo.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof InputParameter) {
                    return Integer.toString(inputParameters.indexOf(element) + 1);
                }
                return StringUtils.EMPTY;
            }
        });

        TableViewerColumn tableViewerColumnParamType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnParamType.getColumn().setWidth(100);
        tableViewerColumnParamType.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof InputParameter) {
                    if (((InputParameter) element).getParamType() != null) {
                        InputParameterClass paramType = ((InputParameter) element).getParamType();
                        if (paramType.isArray()) {
                            if (paramType.getComponentType() != null) {
                                return paramType.getComponentType().getSimpleName() + "[]";
                            } else {
                                return Object.class.getSimpleName() + "[]";
                            }
                        }
                        return paramType.getSimpleName();
                    }
                }
                return StringUtils.EMPTY;
            }
        });

        TableViewerColumn tableViewerColumnParam = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnParam.getColumn().setWidth(100);
        tableViewerColumnParam.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof InputParameter && ((InputParameter) element).getParamName() != null) {
                    return ((InputParameter) element).getParamName();
                }
                return StringUtils.EMPTY;
            }
        });

        TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValueType.getColumn().setWidth(100);
        tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider(scriptClass) {
            @Override
            public String getText(Object element) {
                if (element instanceof InputParameter) {
                    return super.getText(((InputParameter) element).getValue());
                }
                return StringUtils.EMPTY;
            }
        });

        tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer, defaultInputValueTypes, 
                ICustomInputValueType.TAG_KEYWORD_INPUT, this, scriptClass) {
            @Override
            protected void setValue(Object element, Object value) {
                if (element instanceof InputParameter && value instanceof Integer && (int) value > -1
                        && (int) value < inputValueTypeNames.size()) {
                    String newValueTypeString = inputValueTypeNames.get((int) value);
                    IInputValueType newValueType = AstTreeTableInputUtil
                            .getInputValueTypeFromString(newValueTypeString);
                    IInputValueType oldValueType = AstTreeTableValueUtil.getTypeValue(
                            ((InputParameter) element).getValue(), scriptClass);
                    if (newValueType != oldValueType) {
                        ASTNode astNode = null;
                        if (newValueType == InputValueType.Property) {
                            astNode = AstTreeTableInputUtil.createPropertyExpressionForClass(((InputParameter) element)
                                    .getParamType().getSimpleName());
                        } else {
                            astNode = (ASTNode) newValueType.getNewValue(element);
                        }
                        parentDialog.changeObject(element, astNode);
                        getViewer().refresh();
                    }
                }
            }

            @Override
            protected Object getValue(Object element) {
                if (element instanceof InputParameter) {
                    return super.getValue(((InputParameter) element).getValue());
                }
                return 0;
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element instanceof InputParameter) {
                    return super.canEdit(((InputParameter) element).getValue());
                }
                return false;
            }
        });

        TableViewerColumn tableViewerColumnConstantType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnConstantType.getColumn().setWidth(100);
        tableViewerColumnConstantType.setLabelProvider(new AstInputConstantTypeLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof InputParameter) {
                    return super.getText(((InputParameter) element).getValue());
                }
                return StringUtils.EMPTY;
            }
        });
        tableViewerColumnConstantType
                .setEditingSupport(new AstInputBuilderConstantTypeColumnSupport(tableViewer, this) {
                    @Override
                    protected void setValue(Object element, Object value) {
                        if (element instanceof InputParameter
                                && ((InputParameter) element).getValue() instanceof ConstantExpression) {
                            ConstantValueType newConstantValueType = ConstantValueType.values()[(int) value];
                            ConstantValueType oldConstantValueType = AstTreeTableInputUtil
                                    .getConstantValueTypeFromConstantExpression((ConstantExpression) ((InputParameter) element)
                                            .getValue());
                            if (newConstantValueType != oldConstantValueType) {
                                Expression newExpression = AstTreeTableInputUtil
                                        .generateNewConstantExpression(newConstantValueType);
                                if (newExpression != null) {
                                    parentDialog.changeObject(element, newExpression);
                                    getViewer().refresh();
                                }
                            }
                        }

                    }

                    @Override
                    protected Object getValue(Object element) {
                        if (element instanceof InputParameter) {
                            return super.getValue(((InputParameter) element).getValue());
                        }
                        return 0;
                    }

                    @Override
                    protected boolean canEdit(Object element) {
                        if (element instanceof InputParameter) {
                            return super.canEdit(((InputParameter) element).getValue());
                        }
                        return false;
                    }
                });

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValue.getColumn().setWidth(300);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider(scriptClass) {
            @Override
            public String getText(Object element) {
                if (element instanceof InputParameter) {
                    return super.getText(((InputParameter) element).getValue());
                }
                return StringUtils.EMPTY;
            }
        });

        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this, scriptClass) {
            @Override
            protected void setValue(Object element, Object value) {
                if (element instanceof InputParameter) {
                    Object object = AstTreeTableValueUtil.setValue(((InputParameter) element).getValue(), value,
                            scriptClass);
                    if (object != null) {
                        parentDialog.changeObject(element, object);
                        getViewer().refresh();
                    }
                }
            }

            @Override
            protected Object getValue(Object element) {
                if (element instanceof InputParameter) {
                    return super.getValue(((InputParameter) element).getValue());
                }
                return 0;
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element instanceof InputParameter) {
                    return super.canEdit(((InputParameter) element).getValue());
                }
                return false;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                if (element instanceof InputParameter) {
                    return super.getCellEditor(((InputParameter) element).getValue());
                }
                return null;
            }
        });

        // set column's name
        for (int i = 0; i < tableViewer.getTable().getColumnCount(); i++) {
            tableViewer.getTable().getColumn(i).setText(COLUMN_NAMES[i]);
        }
    }

    @Override
    public void refresh() {
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(inputParameters);
        tableViewer.refresh();
    }
}
