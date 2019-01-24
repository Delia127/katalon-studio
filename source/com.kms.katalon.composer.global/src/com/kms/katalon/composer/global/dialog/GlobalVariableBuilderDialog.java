package com.kms.katalon.composer.global.dialog;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.global.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstValueUtil;
import com.kms.katalon.composer.testcase.util.AstInputValueTypeOptionsProvider;
import com.kms.katalon.entity.global.GlobalVariableEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;

public class GlobalVariableBuilderDialog extends AbstractDialog {
    private static final InputValueType[] defaultInputValueTypes = AstInputValueTypeOptionsProvider.getInputValueTypeOptions(InputValueType.GlobalVariable);

    public enum DialogType {
        NEW, EDIT
    }

    private String dialogTitle;

    private GlobalVariableEntity fVariableEntity;

    private Point location;

    private TableViewer tableViewer;

    private List<String> globalVariableNames = new ArrayList<String>();

    private CellEditor cellEditor;

    public GlobalVariableBuilderDialog(Shell parentShell, Point location, List<String> globalVariableNames) {
        this(parentShell, new GlobalVariableEntity("", "''"), DialogType.NEW, location, globalVariableNames);
    }

    public GlobalVariableBuilderDialog(Shell parentShell, GlobalVariableEntity variableEntity, Point location,
            List<String> globalVariableNames) {
        this(parentShell, variableEntity, DialogType.EDIT, location, globalVariableNames);
    }

    private GlobalVariableBuilderDialog(Shell parentShell, GlobalVariableEntity variableEntity, DialogType type,
            Point location, List<String> globalVariableNames) {
        super(parentShell);
        this.fVariableEntity = variableEntity.clone();
        this.location = location;
        switch (type) {
            case EDIT:
                dialogTitle = StringConstants.DIA_TITLE_EDIT_VAR;
                globalVariableNames.remove(variableEntity.getName());
                break;
            case NEW:
                dialogTitle = StringConstants.DIA_TITLE_NEW_VAR;
                break;
        }
        this.globalVariableNames = globalVariableNames;
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout glContainer = new GridLayout(1, false);
        glContainer.horizontalSpacing = ControlUtils.DF_HORIZONTAL_SPACING;
        glContainer.verticalSpacing = ControlUtils.DF_VERTICAL_SPACING;
        container.setLayout(glContainer);

        Composite compositeTable = new Composite(container, SWT.NONE);
        compositeTable.setLayout(new FillLayout(SWT.HORIZONTAL));
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        tableViewer = new TableViewer(compositeTable, SWT.BORDER | SWT.FULL_SELECTION);
        Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        // Enable editing on Tab move
        TableViewerEditor.create(tableViewer, new ColumnViewerEditorActivationStrategy(tableViewer),
                ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);

        ControlDecoration controlDecoration = new ControlDecoration(tableViewer.getTable(), SWT.LEFT | SWT.TOP);
        controlDecoration.setDescriptionText(StringConstants.DIA_CTRL_VAR_INFO);
        controlDecoration.setImage(FieldDecorationRegistry.getDefault()
                .getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION)
                .getImage());

        TableViewerColumn tableViewerColumnName = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnName.setEditingSupport(new EditingSupport(tableViewer) {
            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor((Composite) this.getViewer().getControl());
            }

            @Override
            protected boolean canEdit(Object element) {
                return true;
            }

            @Override
            protected Object getValue(Object element) {
                if (element != null && element instanceof GlobalVariableEntity) {
                    return ((GlobalVariableEntity) element).getName();
                }
                return "";
            }

            @Override
            protected void setValue(Object element, Object value) {
                if (element != null && element instanceof GlobalVariableEntity && value != null
                        && value instanceof String) {
                    GlobalVariableEntity param = (GlobalVariableEntity) element;
                    String newParamName = (String) value;
                    if (!newParamName.equals(param.getName())) {
                        param.setName(newParamName);
                        getViewer().update(element, null);
                        refresh();
                    }
                }
            }
        });
        tableViewerColumnName.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element != null && element instanceof GlobalVariableEntity) {
                    return ((GlobalVariableEntity) element).getName();
                }
                return "";
            }
        });
        TableColumn tblclmnName = tableViewerColumnName.getColumn();
        tblclmnName.setWidth(100);
        tblclmnName.setText(StringConstants.PA_COL_NAME);

        TableViewerColumn tableViewerColumnDefaultValueType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnDefaultValueType.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (!(element instanceof GlobalVariableEntity)) {
                    return "";
                }
                InputValueType valueType = AstValueUtil.getTypeValue(GroovyWrapperParser.parseGroovyScriptAndGetFirstExpression(((GlobalVariableEntity) element).getInitValue()));
                if (valueType != null) {
                    return TreeEntityUtil.getReadableKeywordName(valueType.getName());
                }
                return "";
            }
        });

        tableViewerColumnDefaultValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(
                tableViewer, defaultInputValueTypes) {
            private ExpressionWrapper expression;

            private Object oldValueTypeIndex;

            @Override
            protected boolean canEdit(Object element) {
                return (element instanceof GlobalVariableEntity);
            }

            @Override
            protected Object getValue(Object element) {
                oldValueTypeIndex = super.getValue(GroovyWrapperParser.parseGroovyScriptAndGetFirstExpression(((GlobalVariableEntity) element).getInitValue()));
                return oldValueTypeIndex;
            }

            @Override
            protected void setValue(Object element, Object value) {
                if (!(value instanceof Integer) || (int) value < 0 || (int) value >= inputValueTypes.length) {
                    return;
                }
                InputValueType newValueType = inputValueTypes[(int) value];
                if (newValueType == inputValueTypes[(int) oldValueTypeIndex]) {
                    return;
                }
                oldValueTypeIndex = value;
                ASTNodeWrapper newAstNode = (ASTNodeWrapper) newValueType.getNewValue(expression != null
                        ? expression.getParent() : null);
                if (newAstNode == null) {
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder();
                GroovyWrapperParser groovyParser = new GroovyWrapperParser(stringBuilder);
                groovyParser.parse(newAstNode);
                ((GlobalVariableEntity) element).setInitValue(stringBuilder.toString());
                this.getViewer().update(element, null);
                refresh();
            }
        });
        TableColumn tblclmnDefaultValueType = tableViewerColumnDefaultValueType.getColumn();
        tblclmnDefaultValueType.setWidth(100);
        tblclmnDefaultValueType.setText(StringConstants.PA_COL_DEFAULT_VALUE_TYPE);

        TableViewerColumn tableViewerColumnDefaultValue = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnDefaultValue.setEditingSupport(new EditingSupport(tableViewer) {
            private ExpressionWrapper expression;

            @Override
            protected CellEditor getCellEditor(Object element) {
                cellEditor = null;
                expression = GroovyWrapperParser.parseGroovyScriptAndGetFirstExpression(((GlobalVariableEntity) element).getInitValue());
                if (expression == null) {
                    return null;
                }
                InputValueType inputValueType = AstValueUtil.getTypeValue(expression);
                if (inputValueType != null) {
                    cellEditor = inputValueType.getCellEditorForValue((Composite) getViewer().getControl(), expression);
                    return cellEditor;
                }
                return null;
            }

            @Override
            protected boolean canEdit(Object element) {
                return (element instanceof GlobalVariableEntity);
            }

            @Override
            protected Object getValue(Object element) {
                InputValueType inputValueType = AstValueUtil.getTypeValue(expression);
                if (inputValueType != null) {
                    return inputValueType.getValueToEdit(expression);
                }
                return null;
            }

            @Override
            protected void setValue(Object element, Object value) {
                if (value == null) {
                    return;
                }
                InputValueType inputValueType = AstValueUtil.getTypeValue(expression);
                if (inputValueType == null) {
                    return;
                }
                Object object = inputValueType.changeValue(expression, value);
                if (!(object instanceof ASTNodeWrapper)) {
                    return;
                }
                ASTNodeWrapper newAstNode = (ASTNodeWrapper) object;
                StringBuilder stringBuilder = new StringBuilder();
                GroovyWrapperParser groovyParser = new GroovyWrapperParser(stringBuilder);
                groovyParser.parse(newAstNode);
                ((GlobalVariableEntity) element).setInitValue(stringBuilder.toString());
                this.getViewer().update(element, null);
                refresh();
            }
        });
        TableColumn tblclmnDefaultValue = tableViewerColumnDefaultValue.getColumn();
        tblclmnDefaultValue.setWidth(200);
        tblclmnDefaultValue.setText(StringConstants.PA_COL_DEFAULT_VALUE);
        tableViewerColumnDefaultValue.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (!(element instanceof GlobalVariableEntity)
                        || ((GlobalVariableEntity) element).getInitValue() == null) {
                    return "";
                }
                ExpressionWrapper expression = GroovyWrapperParser.parseGroovyScriptAndGetFirstExpression(((GlobalVariableEntity) element).getInitValue());
                if (expression == null) {
                    return "";
                }
                return expression.getText();
            }
        });

        TableViewerColumn tableViewerColumnDescription = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnDescription.setEditingSupport(new EditingSupport(tableViewer) {
            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor((Composite) this.getViewer().getControl());
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element instanceof GlobalVariableEntity) {
                    return true;
                }
                return false;
            }

            @Override
            protected Object getValue(Object element) {
                if (element instanceof GlobalVariableEntity) {
                    return ((GlobalVariableEntity) element).getDescription();
                }
                return StringConstants.EMPTY;
            }

            @Override
            protected void setValue(Object element, Object value) {
                if (element instanceof GlobalVariableEntity && value instanceof String) {
                    GlobalVariableEntity param = (GlobalVariableEntity) element;
                    String newParamDesc = (String) value;
                    if (!newParamDesc.equals(param.getDescription())) {
                        param.setDescription(newParamDesc);
                        getViewer().update(element, null);
                        refresh();
                    }
                }
            }
        });
        tableViewerColumnDescription.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element != null && element instanceof GlobalVariableEntity) {
                    return ((GlobalVariableEntity) element).getDescription();
                }
                return StringConstants.EMPTY;
            }
        });
        TableColumn tblColumnDescription = tableViewerColumnDescription.getColumn();
        tblColumnDescription.setWidth(200);
        tblColumnDescription.setText(StringConstants.PA_COL_DESCRIPTION);

        tableViewer.setContentProvider(new ArrayContentProvider());

        return container;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(650, 250);
    }

    private void refresh() {
        tableViewer.setInput(new Object[] { fVariableEntity });
        getButton(OK).setEnabled(validate());
    }

    private boolean validate() {
        String newVariableName = fVariableEntity.getName();
        if (!GroovyConstants.VARIABLE_NAME_REGEX.matcher(newVariableName).find()) {
            return false;
        }
        if (globalVariableNames.contains(newVariableName)) {
            MessageDialog.openWarning(getShell(), getDialogTitle(), StringConstants.PA_MSG_VARIABLE_NAME_EXIST);
            return false;
        }
        return true;
    }

    public String getDialogTitle() {
        return dialogTitle != null ? dialogTitle : StringConstants.EMPTY;
    }

    @Override
    protected final void setInput() {
        refresh();
        tableViewer.editElement(tableViewer.getElementAt(0), 0);
    }

    public GlobalVariableEntity getVariableEntity() {
        return fVariableEntity;
    }

    @Override
    public Point getInitialLocation(Point initialSize) {
        if (location != null) {
            return new Point(this.location.x - initialSize.x - 10, this.location.y);
        }
        return super.getInitialLocation(initialSize);
    }

    @Override
    protected void registerControlModifyListeners() {
        // Do nothing

    }

    @Override
    protected void okPressed() {
        if (cellEditor != null) {
            AstValueUtil.applyEditingValue(cellEditor);
        }
        super.okPressed();
    }
    private class MultilineTextCellEditor extends TextCellEditor {

        public MultilineTextCellEditor(Composite parent) {
            super(parent, SWT.WRAP | SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        }

        @Override
        public LayoutData getLayoutData() {
            LayoutData data = new LayoutData();
            data.minimumHeight = 100;
            data.verticalAlignment = SWT.TOP;
            return data;
        }
    }
 private class CustomTextCellEditor extends TextCellEditor {
    	
        public CustomTextCellEditor(Composite parent) {
            super(parent);
        }

        @Override
        public LayoutData getLayoutData() {
            LayoutData result = super.getLayoutData();
            result.minimumHeight =10;
            return result;
        }
    }
 
	private class CustomComboBoxCellEditor extends ComboBoxCellEditor {

		public CustomComboBoxCellEditor(Composite parent, String[] items) {
			super(parent, items);
		}

		@Override
		public LayoutData getLayoutData() {
			LayoutData result = super.getLayoutData();
			result.minimumHeight = tableViewer.getTable().getItemHeight();
			return result;
		}
	}
}
