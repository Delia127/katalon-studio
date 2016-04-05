package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.ast.editors.EnumPropertyComboBoxCellEditor;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PropertyExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.model.InputParameterClass;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testcase.TestCase;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class ArgumentInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private final InputValueType[] defaultInputValueTypes = { InputValueType.String, InputValueType.Number,
            InputValueType.Boolean, InputValueType.Null, InputValueType.Variable, InputValueType.GlobalVariable,
            InputValueType.TestDataValue, InputValueType.Binary, InputValueType.Condition, InputValueType.TestCase,
            InputValueType.TestData, InputValueType.TestObject, InputValueType.MethodCall, InputValueType.Property,
            InputValueType.List, InputValueType.Map, InputValueType.Keys };

    private List<InputParameter> inputParameters;
    private List<InputParameter> originalParameters;
    private ASTNodeWrapper parent;

    public ArgumentInputBuilderDialog(Shell parentShell, List<InputParameter> inputParameters, ASTNodeWrapper parent) {
        super(parentShell);
        originalParameters = inputParameters;
        this.parent = parent;
        this.inputParameters = new ArrayList<InputParameter>();
        for (InputParameter inputParameter : inputParameters) {
            if (isEditableParameter(inputParameter)) {
                continue;
            }
            this.inputParameters.add(inputParameter);
        }
    }

    // Skip adding test object and test case to input editor
    private boolean isEditableParameter(InputParameter inputParameter) {
        return inputParameter.getParamType() != null
                && (TestCaseEntityUtil.isClassChildOf(TestObject.class.getName(), inputParameter.getParamType()
                        .getFullName()) || TestCaseEntityUtil.isClassChildOf(TestCase.class.getName(), inputParameter
                        .getParamType().getFullName()));
    }

    protected void updateTestCaseBindingInputParameters(MethodCallExpressionWrapper testCaseArgument) {
        ExpressionWrapper objectExpression = AstEntityInputUtil.getCallTestCaseParam(testCaseArgument);
        if (objectExpression == null) {
            return;
        }
        TestCaseEntity testCase = null;
        try {
            testCase = TestCaseController.getInstance().getTestCaseByDisplayId(objectExpression.getText());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (testCase == null) {
            return;
        }
        for (InputParameter input : inputParameters) {
            if (input.getParamType() != null && input.getParamType().getFullName().equals(Map.class.getName())) {
                input.setValue(AstEntityInputUtil.generateTestCaseVariableBindingExpression(testCase, parent));
                tableViewer.update(input, null);
            }
        }
    }

    @Override
    public List<InputParameter> getReturnValue() {
        return originalParameters;
    }

    @Override
    public void replaceObject(Object originalObject, Object newObject) {
        // do nothing for this dialog
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_INPUT;
    }

    @Override
    protected void addTableColumns() {
        TableViewerColumn tableViewerColumnNo = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnNo.getColumn().setText(StringConstants.DIA_COL_NO);
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
        tableViewerColumnParamType.getColumn().setText(StringConstants.DIA_COL_PARAM_TYPE);
        tableViewerColumnParamType.getColumn().setWidth(100);
        tableViewerColumnParamType.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (!(element instanceof InputParameter) || ((InputParameter) element).getParamType() == null) {
                    return StringUtils.EMPTY;
                }
                InputParameterClass paramType = ((InputParameter) element).getParamType();
                if (!paramType.isArray()) {
                    return paramType.getSimpleName();
                }
                if (paramType.getComponentType() != null) {
                    return paramType.getComponentType().getSimpleName() + "[]";
                } else {
                    return Object.class.getSimpleName() + "[]";
                }
            }
        });

        TableViewerColumn tableViewerColumnParam = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnParam.getColumn().setText(StringConstants.DIA_COL_PARAM);
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
        tableViewerColumnValueType.getColumn().setText(StringConstants.DIA_COL_VALUE_TYPE);
        tableViewerColumnValueType.getColumn().setWidth(100);
        tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof InputParameter) {
                    return super.getText(((InputParameter) element).getValue());
                }
                return StringUtils.EMPTY;
            }
        });

        tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
                defaultInputValueTypes, this) {
            @Override
            protected void setValue(Object element, Object value) {
                if (!(value instanceof Integer) || (int) value < 0 || (int) value >= inputValueTypes.length) {
                    return;
                }
                InputParameter inputParameter = (InputParameter) element;
                InputValueType newValueType = inputValueTypes[(int) value];
                InputValueType oldValueType = AstTreeTableValueUtil.getTypeValue(inputParameter.getValue());
                if (newValueType == oldValueType) {
                    return;
                }
                ASTNodeWrapper newAstNode = (ASTNodeWrapper) newValueType.getNewValue(parent);
                if (newValueType == InputValueType.Property) {
                    newAstNode = AstTreeTableInputUtil.createPropertyExpressionForClass(inputParameter.getParamType()
                            .getSimpleName(), parent);
                }
                if (newAstNode == null) {
                    return;
                }
                if (inputParameter.getValue() instanceof ASTNodeWrapper) {
                    ASTNodeWrapper oldAstNode = (ASTNodeWrapper) inputParameter.getValue();
                    newAstNode.copyProperties(oldAstNode);
                    newAstNode.setParent(oldAstNode.getParent());
                }
                inputParameter.setValue(newAstNode);
                if (newAstNode instanceof MethodCallExpressionWrapper
                        && AstEntityInputUtil.isCallTestCaseArgument((MethodCallExpressionWrapper) newAstNode)) {
                    updateTestCaseBindingInputParameters((MethodCallExpressionWrapper) newAstNode);
                }
                getViewer().refresh();
            }

            @Override
            protected Object getValue(Object element) {
                return super.getValue(((InputParameter) element).getValue());
            }

            @Override
            protected boolean canEdit(Object element) {
                return (element instanceof InputParameter && !isFailureHandlingInputParameter((InputParameter) element) && super
                        .canEdit(((InputParameter) element).getValue()));
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return super.getCellEditor(((InputParameter) element).getValue());
            }
        });

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValue.getColumn().setText(StringConstants.DIA_COL_VALUE);
        tableViewerColumnValue.getColumn().setWidth(300);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof InputParameter) {
                    if (isFailureHandlingInputParameter((InputParameter) element)) {
                        return ((PropertyExpressionWrapper) (((InputParameter) element)).getValue()).getPropertyAsString();
                    }
                    return super.getText(((InputParameter) element).getValue());
                }
                return StringUtils.EMPTY;
            }
        });

        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this) {
            @Override
            protected void setValue(Object element, Object value) {
                Object object = inputValueType.changeValue(((InputParameter) element).getValue(), value);
                if (object == null) {
                    return;
                }
                if (!object.equals(element)) {
                    if (object instanceof ASTNodeWrapper
                            && ((InputParameter) element).getValue() instanceof ASTNodeWrapper) {
                        ASTNodeWrapper oldAstNode = (ASTNodeWrapper) ((InputParameter) element).getValue();
                        ASTNodeWrapper newAstNode = (ASTNodeWrapper) object;
                        newAstNode.copyProperties(oldAstNode);
                    }
                    ((InputParameter) element).setValue(object);
                }
                getViewer().refresh();
            }

            @Override
            protected Object getValue(Object element) {
                if (isFailureHandlingInputParameter((InputParameter) element)) {
                    return ((InputParameter) element).getValue();
                }
                return super.getValue(((InputParameter) element).getValue());
            }

            @Override
            protected boolean canEdit(Object element) {
                return (element instanceof InputParameter && super.canEdit(((InputParameter) element).getValue()));
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                if (isFailureHandlingInputParameter((InputParameter) element)) {
                    return new EnumPropertyComboBoxCellEditor((Composite) getViewer().getControl(),
                            FailureHandling.class);
                }
                return super.getCellEditor(((InputParameter) element).getValue());
            }
        });
    }

    private boolean isFailureHandlingInputParameter(InputParameter inputParam) {
        return (inputParam.getParamType().getFullName().equals(FailureHandling.class.getName())
                && inputParam.getValue() instanceof PropertyExpressionWrapper && AstTreeTableInputUtil
                    .isFailureHandlingPropertyExpression((PropertyExpressionWrapper) inputParam.getValue()));
    }

    @Override
    public void refresh() {
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(inputParameters);
        tableViewer.refresh();
    }
}
