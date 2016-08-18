package com.kms.katalon.composer.testcase.ast.dialogs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.ast.editors.EnumPropertyComboBoxCellEditor;
import com.kms.katalon.composer.testcase.ast.editors.StringConstantCellEditor;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PropertyExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputParameter;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.providers.UneditableTableCellLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.AstInputValueTypeOptionsProvider;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.composer.testcase.util.AstValueUtil;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.entity.testcase.TestCaseEntity;

public class ArgumentInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
    private List<InputParameter> inputParameters;

    private List<InputParameter> originalParameters;

    private ASTNodeWrapper parent;

    protected StringConstantCellEditor valueCellEditor;

    public ArgumentInputBuilderDialog(Shell parentShell, List<InputParameter> inputParameters, ASTNodeWrapper parent) {
        super(parentShell);
        originalParameters = inputParameters;
        this.parent = parent;
        this.inputParameters = new ArrayList<InputParameter>();
        filterInputParameters(inputParameters);
    }

    public void filterInputParameters(List<InputParameter> inputParameters) {
        boolean hasTestObjectParam = false;
        for (InputParameter inputParameter : inputParameters) {
            if (inputParameter.isTestObjectInputParameter() && !hasTestObjectParam) {
                hasTestObjectParam = true;
                continue;
            }
            if (!inputParameter.isEditable()) {
                continue;
            }
            this.inputParameters.add(inputParameter);
        }
    }

    protected void updateTestCaseBindingInputParameters(MethodCallExpressionWrapper testCaseArgument) {
        String testCaseId = AstEntityInputUtil.findTestCaseIdArgumentFromFindTestCaseMethodCall(testCaseArgument);
        if (testCaseId == null) {
            return;
        }
        TestCaseEntity testCase = null;
        try {
            testCase = TestCaseController.getInstance().getTestCaseByDisplayId(testCaseId);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (testCase == null) {
            return;
        }
        for (InputParameter input : inputParameters) {
            if (isVariablesBindingMapArgument(input)) {
                input.setValue(AstEntityInputUtil.generateTestCaseVariableBindingMapExpression(testCase, parent));
                tableViewer.update(input, null);
            }
        }
    }

    private boolean isVariablesBindingMapArgument(InputParameter input) {
        return input.getParamType() != null && input.getParamType().getFullName().equals(Map.class.getName());
    }

    @Override
    public List<InputParameter> getReturnValue() {
        return originalParameters;
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_INPUT;
    }

    @Override
    protected void addTableColumns() {
        addTableColumnNo();

        addTableColumnParam();

        addTableColumnParamType();

        addTableColumnValueType();

        addTableColumnValue();
    }

    private void addTableColumnValue() {
        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValue.getColumn().setText(StringConstants.DIA_COL_VALUE);
        tableViewerColumnValue.getColumn().setWidth(300);
        tableViewerColumnValue.setLabelProvider(new ArgumentInputValueLabelProvider());
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer) {
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
                    getViewer().refresh();
                }
            }

            @Override
            protected Object getValue(Object element) {
                if (((InputParameter) element).isFailureHandlingInputParameter()) {
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
                if (((InputParameter) element).isFailureHandlingInputParameter()) {
                    return new EnumPropertyComboBoxCellEditor((Composite) getViewer().getControl(),
                            FailureHandling.class);
                }
                CellEditor cellEditor = super.getCellEditor(((InputParameter) element).getValue());
                valueCellEditor = null;
                if (cellEditor instanceof StringConstantCellEditor) {
                    valueCellEditor = (StringConstantCellEditor)cellEditor;
                }

                return cellEditor;
            }
        });
    }
    
    @Override
    protected void processEditingValueWhenOKPressed() {
        if (tableViewer.isCellEditorActive() && valueCellEditor != null) {
            valueCellEditor.applyEditingValue();
        }
    }

    private void addTableColumnValueType() {
        TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValueType.getColumn().setText(StringConstants.DIA_COL_VALUE_TYPE);
        tableViewerColumnValueType.getColumn().setWidth(100);
        tableViewerColumnValueType.setLabelProvider(new ArgumentInputValueTypeLabelProvider());
        tableViewerColumnValueType.setEditingSupport(new MethodArgumentInputBuilderValueTypeColumnSupport(tableViewer));
    }

    private void addTableColumnParam() {
        TableViewerColumn tableViewerColumnParam = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnParam.getColumn().setText(StringConstants.DIA_COL_PARAM);
        tableViewerColumnParam.getColumn().setWidth(100);
        tableViewerColumnParam.setLabelProvider(new UneditableTableCellLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof InputParameter && ((InputParameter) element).getParamName() != null) {
                    return ((InputParameter) element).getParamName();
                }
                return StringUtils.EMPTY;
            }
        });
    }

    private void addTableColumnParamType() {
        TableViewerColumn tableViewerColumnParamType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnParamType.getColumn().setText(StringConstants.DIA_COL_PARAM_TYPE);
        tableViewerColumnParamType.getColumn().setWidth(100);
        tableViewerColumnParamType.setLabelProvider(new UneditableTableCellLabelProvider() {
            @Override
            public String getText(Object element) {
                if (!(element instanceof InputParameter) || ((InputParameter) element).getParamType() == null) {
                    return StringUtils.EMPTY;
                }
                return ((InputParameter) element).getParamType().getDisplayText();
            }
        });
    }

    private void addTableColumnNo() {
        TableViewerColumn tableViewerColumnNo = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnNo.getColumn().setText(StringConstants.DIA_COL_NO);
        tableViewerColumnNo.getColumn().setWidth(40);
        tableViewerColumnNo.setLabelProvider(new UneditableTableCellLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof InputParameter) {
                    return Integer.toString(inputParameters.indexOf(element) + 1);
                }
                return StringUtils.EMPTY;
            }
        });
    }

    @Override
    public void setInput() {
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(inputParameters);
        tableViewer.refresh();
    }

    private final class ArgumentInputValueLabelProvider extends AstInputValueLabelProvider {
        @Override
        public String getText(Object element) {
            if (!(element instanceof InputParameter)) {
                return StringUtils.EMPTY;
            }
            InputParameter inputParameter = (InputParameter) element;
            if (inputParameter.isFailureHandlingInputParameter()) {
                return ((PropertyExpressionWrapper) inputParameter.getValue()).getPropertyAsString();
            }
            return super.getText(inputParameter.getValue());
        }
    }

    private final class ArgumentInputValueTypeLabelProvider extends StyledCellLabelProvider {
        protected String getText(Object element) {
            if (!(element instanceof InputParameter)) {
                return StringUtils.EMPTY;
            }
            InputValueType typeValue = AstValueUtil.getTypeValue(((InputParameter) element).getValue());
            if (typeValue != null) {
                return TreeEntityUtil.getReadableKeywordName(typeValue.getName());
            }
            return StringUtils.EMPTY;
        }

        @Override
        public void update(ViewerCell cell) {
            cell.setText(getText(cell.getElement()));
            cell.setImage(getImage(cell.getElement()));
            super.update(cell);
        }

        private Image getImage(Object element) {
            if (!(element instanceof InputParameter)) {
                return null;
            }
            InputParameter inputParameter = (InputParameter) element;
            if (isMissingInput(inputParameter) || isInputHaveInvalidType(inputParameter)) {
                return ImageConstants.IMG_16_WARN_TABLE_ITEM;
            }
            return null;
        }

        private boolean isMissingInput(InputParameter inputParameter) {
            return inputParameter.getValue() == null;
        }

        private boolean isInputHaveInvalidType(InputParameter inputParameter) {
            InputValueType inputValueType = AstValueUtil.getTypeValue(inputParameter.getValue());
            InputValueType[] possibleInputValueTypes = AstInputValueTypeOptionsProvider.getAssignableInputValueTypes(inputParameter.getParamType());
            for (InputValueType possibleInputValueType : possibleInputValueTypes) {
                if (inputValueType == possibleInputValueType) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String getToolTipText(Object element) {
            if (!(element instanceof InputParameter)) {
                return null;
            }
            InputParameter inputParameter = (InputParameter) element;
            if (isMissingInput(inputParameter)) {
                return StringConstants.LP_WARN_MISSING_ARGUMENT_FOR_METHOD_CALL;
            }
            if (isInputHaveInvalidType(inputParameter)) {
                return MessageFormat.format(
                        StringConstants.LP_WARN_INVALID_ARGUMENT_X_FOR_METHOD_CALL_AVAILALE_ARE_Y,
                        AstValueUtil.getTypeValue(inputParameter.getValue()),
                        ArrayUtils.toString(AstInputValueTypeOptionsProvider.getAssignableInputValueTypes(inputParameter.getParamType())));
            }
            return null;
        }
    }

    private final class MethodArgumentInputBuilderValueTypeColumnSupport extends AstInputBuilderValueTypeColumnSupport {
        public MethodArgumentInputBuilderValueTypeColumnSupport(ColumnViewer viewer) {
            super(viewer);
        }

        @Override
        protected void setValue(Object element, Object value) {
            if (!(value instanceof Integer) || (int) value < 0 || (int) value >= inputValueTypes.length) {
                return;
            }
            InputParameter inputParameter = (InputParameter) element;
            InputValueType newValueType = inputValueTypes[(int) value];
            InputValueType oldValueType = AstValueUtil.getTypeValue(inputParameter.getValue());
            if (newValueType == oldValueType) {
                return;
            }
            ASTNodeWrapper newAstNode = (ASTNodeWrapper) newValueType.getNewValue(parent);
            if (newValueType == InputValueType.Property && inputParameter.getParamType().isFailureHandlingTypeClass()) {
                newAstNode = AstKeywordsInputUtil.createPropertyExpressionForClass(inputParameter.getParamType()
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
                    && ((MethodCallExpressionWrapper) newAstNode).isFindTestCaseMethodCall()) {
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
            return (element instanceof InputParameter && !((InputParameter) element).isFailureHandlingInputParameter());
        }

        @Override
        protected CellEditor getCellEditor(Object element) {
            InputParameter inputParameter = (InputParameter) element;
            inputValueTypes = AstInputValueTypeOptionsProvider.getAssignableInputValueTypes(inputParameter.getParamType());
            initReadableValueTypeNamesList();
            return super.getCellEditor(inputParameter.getValue());
        }
    }
}
