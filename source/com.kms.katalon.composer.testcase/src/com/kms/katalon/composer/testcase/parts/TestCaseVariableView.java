package com.kms.katalon.composer.testcase.parts;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.providers.TypeCheckStyleCellTableLabelProvider;
import com.kms.katalon.composer.components.impl.support.TypeCheckedEditingSupport;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColumnViewerUtil;
import com.kms.katalon.composer.testcase.ast.variable.operations.ChangeVariableMaskedOperation;
import com.kms.katalon.composer.testcase.ast.variable.operations.ClearVariableOperation;
import com.kms.katalon.composer.testcase.ast.variable.operations.DeleteVariableOperation;
import com.kms.katalon.composer.testcase.ast.variable.operations.DownVariableOperation;
import com.kms.katalon.composer.testcase.ast.variable.operations.NewVariableOperation;
import com.kms.katalon.composer.testcase.ast.variable.operations.UpVariableOperation;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.VariableTableDropTarget;
import com.kms.katalon.composer.testcase.support.VariableDefaultValueEditingSupport;
import com.kms.katalon.composer.testcase.support.VariableDefaultValueTypeEditingSupport;
import com.kms.katalon.composer.testcase.support.VariableDescriptionEditingSupport;
import com.kms.katalon.composer.testcase.support.VariableNameEditingSupport;
import com.kms.katalon.composer.testcase.util.AstValueUtil;
import com.kms.katalon.controller.LocalVariableController;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.entity.variable.VariableEntityWrapper;
import com.kms.katalon.execution.util.SyntaxUtil;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.util.listener.EventListener;
import com.kms.katalon.util.listener.EventManager;

public class TestCaseVariableView implements TableActionOperator, EventManager<TestCaseVariableViewEvent> {
    private static final String DEFAULT_VARIABLE_NAME = "variable";

    private static final InputValueType[] defaultInputValueTypes = { InputValueType.String, InputValueType.Number,
            InputValueType.Boolean, InputValueType.Null, InputValueType.GlobalVariable, InputValueType.TestDataValue,
            InputValueType.TestObject, InputValueType.TestData, InputValueType.Property, InputValueType.List,
            InputValueType.Map };

    private CTableViewer tableViewer;

    private VariableEntityWrapper variableEntityWrapper = new VariableEntityWrapper();

    private IVariablePart variablePart;
    
    private InputValueType[] inputValueTypes = defaultInputValueTypes;
    
    private ITestCasePart testCasePart;

    private Map<TestCaseVariableViewEvent, Set<EventListener<TestCaseVariableViewEvent>>> eventListeners = new HashMap<>();
    
    public TestCaseVariableView(IVariablePart variablePart) {
        this.variablePart = variablePart;
        variableEntityWrapper.setVariables(new ArrayList<VariableEntity>());
    }
    
    public void setInputValueTypes(InputValueType[] inputValueTypes) {
        this.inputValueTypes = inputValueTypes;
    }
    
    public InputValueType[] getInputValueTypes() {
        return inputValueTypes;
    }

    public void setTestCasePart(ITestCasePart testCasePart) {
        this.testCasePart = testCasePart;
    }
     public ITestCasePart getTestCasePart() {
        return testCasePart;
    }

    public Composite createComponents(Composite parent) {
        final Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        container.setLayout(new GridLayout(1, false));

        Composite compositeToolbar = new Composite(container, SWT.NONE);
        compositeToolbar.setLayout(new FillLayout(SWT.HORIZONTAL));
        compositeToolbar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        ToolBar toolBar = new ToolBar(compositeToolbar, SWT.FLAT | SWT.RIGHT);

        ToolItem tltmAddVariable = new ToolItem(toolBar, SWT.NONE);
        tltmAddVariable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addVariable();
            }
        });
        tltmAddVariable.setText(StringConstants.PA_BTN_TIP_ADD);
        tltmAddVariable.setImage(ImageConstants.IMG_16_ADD);

        final ToolItem tltmRemove = new ToolItem(toolBar, SWT.NONE);
        tltmRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                removeVariables();
            }
        });
        tltmRemove.setText(StringConstants.PA_BTN_TIP_REMOVE);
        tltmRemove.setImage(ImageConstants.IMG_16_REMOVE);
        tltmRemove.setEnabled(false);

        ToolItem tltmClear = new ToolItem(toolBar, SWT.NONE);
        tltmClear.setText(StringConstants.PA_BTN_TIP_CLEAR);
        tltmClear.setImage(ImageConstants.IMG_16_CLEAR);

        tltmClear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                clearVariables();
            }
        });

        ToolItem tltmUp = new ToolItem(toolBar, SWT.NONE);
        tltmUp.setText(StringConstants.PA_BTN_TIP_MOVE_UP);
        tltmUp.setImage(ImageConstants.IMG_16_MOVE_UP);
        tltmUp.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                upVariable();
            }
        });

        ToolItem tltmDown = new ToolItem(toolBar, SWT.NONE);
        tltmDown.setText(StringConstants.PA_BTN_TIP_MOVE_DOWN);
        tltmDown.setImage(ImageConstants.IMG_16_MOVE_DOWN);
        tltmDown.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                downVariable();
            }
        });

        Composite compositeTable = new Composite(container, SWT.NONE);
        compositeTable.setLayout(new FillLayout(SWT.HORIZONTAL));
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        tableViewer = new CTableViewer(compositeTable, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        ColumnViewerUtil.setTableActivation(tableViewer);

        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                // Should disable Delete button if there is no selection
                tltmRemove.setEnabled(!tableViewer.getSelection().isEmpty());
            }
        });

        tableViewer.addDragSupport(DND.DROP_MOVE, new Transfer[] { TextTransfer.getInstance() },
                new DragSourceAdapter() {

                    @Override
                    public void dragSetData(DragSourceEvent event) {
                        StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
                        VariableEntity variable = (VariableEntity) selection.getFirstElement();
                        event.data = String.valueOf(variableEntityWrapper.getVariables().indexOf(variable));
                    }
                });
        tableViewer.addDropSupport(DND.DROP_MOVE, new Transfer[] { TextTransfer.getInstance() },
                new VariableTableDropTarget(this));
        Table table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        TableViewerColumn tableViewerColumnNo = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnNo = tableViewerColumnNo.getColumn();
        tblclmnNo.setWidth(40);
        tblclmnNo.setText(StringConstants.PA_COL_NO);
        tableViewerColumnNo.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element != null && element instanceof VariableEntity) {
                    return Integer.toString(variableEntityWrapper.getVariables().indexOf(element) + 1);
                }
                return "";
            }
        });

        TableViewerColumn tableViewerColumnName = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnName.setEditingSupport(new VariableNameEditingSupport(tableViewer, this));
        tableViewerColumnName.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element != null && element instanceof VariableEntity) {
                    return ((VariableEntity) element).getName();
                }
                return "";
            }
        });
        TableColumn tblclmnName = tableViewerColumnName.getColumn();
        tblclmnName.setWidth(100);
        tblclmnName.setText(StringConstants.PA_COL_NAME);

        TableViewerColumn tableViewerColumnDefaultValueType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnDefaultValueType
                .setEditingSupport(new VariableDefaultValueTypeEditingSupport(tableViewer, this, inputValueTypes));
        TableColumn tblclmnDefaultValueType = tableViewerColumnDefaultValueType.getColumn();
        tblclmnDefaultValueType.setWidth(100);
        tblclmnDefaultValueType.setText(StringConstants.PA_COL_DEFAULT_VALUE_TYPE);
        tableViewerColumnDefaultValueType.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (!(element instanceof VariableEntity)) {
                    return "";
                }
                try {
                    ExpressionWrapper expression = GroovyWrapperParser
                            .parseGroovyScriptAndGetFirstExpression(((VariableEntity) element).getDefaultValue());
                    if (expression == null) {
                        return null;
                    }
                    InputValueType valueType = AstValueUtil.getTypeValue(expression);
                    if (valueType != null) {
                        return TreeEntityUtil.getReadableKeywordName(valueType.getName());
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }
                return "";
            }
        });

        TableViewerColumn tableViewerColumnDefaultValue = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnDefaultValue.setEditingSupport(new VariableDefaultValueEditingSupport(tableViewer, this, getTestCasePart()));
        TableColumn tblclmnDefaultValue = tableViewerColumnDefaultValue.getColumn();
        tblclmnDefaultValue.setWidth(150);
        tblclmnDefaultValue.setText(StringConstants.PA_COL_DEFAULT_VALUE);
        tableViewerColumnDefaultValue.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (!(element instanceof VariableEntity) || ((VariableEntity) element).getDefaultValue() == null) {
                    return "";
                }
                ExpressionWrapper expression = GroovyWrapperParser
                        .parseGroovyScriptAndGetFirstExpression(((VariableEntity) element).getDefaultValue());
                if (expression == null) {
                    return "";
                }
                return expression.getText();
            }
        });

        TableViewerColumn tableViewerColumnDescription = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnDescription.setEditingSupport(new VariableDescriptionEditingSupport(tableViewer, this));
        tableViewerColumnDescription.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element != null && element instanceof VariableEntity) {
                    return ((VariableEntity) element).getDescription();
                }
                return "";
            }
        });
        TableColumn tblColumnDescription = tableViewerColumnDescription.getColumn();
        tblColumnDescription.setWidth(120);
        tblColumnDescription.setText(StringConstants.PA_COL_DESCRIPTION);

        TableViewerColumn tableViewerColumnLogged = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnLogged.setLabelProvider(new TypeCheckStyleCellTableLabelProvider<VariableEntity>(5) {
            @Override
            protected Class<VariableEntity> getElementType() {
                return VariableEntity.class;
            }

            @Override
            protected Image getImage(VariableEntity element) {
                return element.isMasked() ? ImageConstants.IMG_16_CHECKBOX_CHECKED
                        : ImageConstants.IMG_16_CHECKBOX_UNCHECKED;
            }

            @Override
            protected String getText(VariableEntity element) {
                return "";
            }
        });
        tableViewerColumnLogged.setEditingSupport(new TypeCheckedEditingSupport<VariableEntity>(tableViewer) {

            @Override
            protected Class<VariableEntity> getElementType() {
                return VariableEntity.class;
            }

            @Override
            protected CellEditor getCellEditorByElement(VariableEntity element) {
                return new CheckboxCellEditor(getComposite());
            }

            @Override
            protected boolean canEditElement(VariableEntity element) {
                return element != null;
            }

            @Override
            protected Object getElementValue(VariableEntity element) {
                return element.isMasked();
            }

            @Override
            protected void setElementValue(VariableEntity element, Object value) {
                if (!(value instanceof Boolean)) {
                    return;
                }
                boolean newMasked = (boolean) value;
                if (element.isMasked() == newMasked) {
                    return;
                }
                executeOperation(new ChangeVariableMaskedOperation(TestCaseVariableView.this, element, newMasked));
            }
        });
        TableColumn tblColumnLogged = tableViewerColumnLogged.getColumn();
        tblColumnLogged.setWidth(50);
        tblColumnLogged.setText(ComposerTestcaseMessageConstants.PA_COL_MASKED);
        tblColumnLogged.setToolTipText(ComposerTestcaseMessageConstants.PA_COL_MASKED_TOOLTIP);

        tableViewer.setContentProvider(new ArrayContentProvider());
        loadVariables(Collections.emptyList());

        return container;
    }

    private void addVariable() {
        VariableEntity newVariable = new VariableEntity();
        newVariable.setName(generateNewPropertyName());
        newVariable.setDefaultValue("''");

        executeOperation(new NewVariableOperation(this, newVariable));
        invoke(TestCaseVariableViewEvent.ADD_VARIABLE, null);
    }

    private String generateNewPropertyName() {
        String name = DEFAULT_VARIABLE_NAME;
        int index = 0;
        boolean isUnique = false;
        String newName = name;
        while (!isUnique) {
            isUnique = true;
            for (VariableEntity variable : variableEntityWrapper.getVariables()) {
                if (variable.getName().equals(newName)) {
                    isUnique = false;
                    break;
                }
            }
            if (isUnique) {
                return newName;
            }
            newName = name + "_" + index;
            index++;
        }
        return newName;
    }

    public void addVariable(VariableEntity[] variablesArray) {
        boolean isAdded = false;
        for (VariableEntity addedVariable : variablesArray) {
            boolean exists = false;
            for (VariableEntity currentVariable : variableEntityWrapper.getVariables()) {
                if (currentVariable.getName().equals(addedVariable.getName())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                variableEntityWrapper.getVariables().add(addedVariable);
                isAdded = true;
            }
        }
        if (isAdded) {
            tableViewer.refresh();
            setDirty(true);
        }
    }

    public void deleteVariables(List<VariableEntity> variableList) {
        if (variableEntityWrapper.getVariables().removeAll(variableList)) {
            tableViewer.refresh();
            setDirty(true);
        }
    }

    private void removeVariables() {
        executeOperation(new DeleteVariableOperation(this));
    }

    private void clearVariables() {
        executeOperation(new ClearVariableOperation(this));
    }

    private void upVariable() {
        executeOperation(new UpVariableOperation(this));
    }

    private void downVariable() {
        executeOperation(new DownVariableOperation(this));
    }

    public void loadVariables(List<VariableEntity> newVariables) {
        variableEntityWrapper.getVariables().clear();
        variableEntityWrapper.getVariables().addAll(newVariables);
        tableViewer.setInput(variableEntityWrapper.getVariables());
        tableViewer.refresh();
    }

    public VariableEntity[] getVariables() {
        if (variableEntityWrapper.getVariables() == null) {
            return new VariableEntity[0];
        }
        return variableEntityWrapper.getVariables().toArray(new VariableEntity[variableEntityWrapper.getVariables().size()]);
    }

    public List<VariableEntity> getVariablesList() {
        return variableEntityWrapper.getVariables();
    }

    public TableViewer getTableViewer() {
        return tableViewer;
    }

    public boolean validateVariables() {
        StringBuilder errorCollector = new StringBuilder();
        List<String> names = new ArrayList<String>();
        for (VariableEntity variable : variableEntityWrapper.getVariables()) {
            int index = variableEntityWrapper.getVariables().indexOf(variable) + 1;
            String variableName = variable.getName();
            String variableDefaultValue = variable.getDefaultValue();
            if (variableDefaultValue == null || variableDefaultValue.isEmpty())
                variableDefaultValue = null;

            if (variableName == null || variableName.isEmpty()) {
                errorCollector.append(MessageFormat.format(
                        StringConstants.PA_ERROR_MSG_VAR_AT_INDEX_CANNOT_BE_NULL_OR_EMPTY, Integer.toString(index)));
            }

            if (!GroovyConstants.isValidVariableName(variableName)) {
                errorCollector.append(MessageFormat.format(StringConstants.PA_ERROR_MSG_INVALID_VAR, variableName));
            }

            if (names.contains(variableName)) {
                errorCollector.append(MessageFormat.format(StringConstants.PA_ERROR_MSG_DUPLICATE_VAR, variableName));
            } else {
                names.add(variableName);
            }

            try {
                SyntaxUtil.checkVariableSyntax(variableName, variableDefaultValue);
            } catch (IllegalArgumentException e) {
                errorCollector.append(MessageFormat
                        .format(StringConstants.PA_ERROR_MSG_INVALID_DEFAULT_VAR_VAL_AT_INDEX, index, e.getMessage()));
            }
        }
        String errorString = errorCollector.toString();
        if (errorString.isEmpty()) {
            return true;
        } else {
            MultiStatusErrorDialog.showErrorDialog(new IllegalArgumentException(errorString),
                    StringConstants.PA_ERROR_MSG_UNABLE_TO_SAVE_TEST_CASE,
                    StringConstants.PA_ERROR_REASON_INVALID_TEST_CASE);
            return false;
        }
    }

    @Override
    public IStatus executeOperation(IUndoableOperation operation) {
        try {
            return operation.execute(new NullProgressMonitor(), null);
        } catch (ExecutionException e) {
            return null;
        }
    }

    @Override
    public void setDirty(boolean dirty) {
        variablePart.setDirty(dirty);
    }

    @Override
    public Iterable<EventListener<TestCaseVariableViewEvent>> getListeners(TestCaseVariableViewEvent event) {
        return eventListeners.get(event);
    }

    @Override
    public void addListener(EventListener<TestCaseVariableViewEvent> listener,
            Iterable<TestCaseVariableViewEvent> events) {
        events.forEach(e -> {
            Set<EventListener<TestCaseVariableViewEvent>> listenerOnEvent = eventListeners.get(e);
            if (listenerOnEvent == null) {
                listenerOnEvent = new HashSet<>();
            }
            listenerOnEvent.add(listener);
            eventListeners.put(e, listenerOnEvent);
        });

    }

    public TestCaseVariableView() {
        super();
        // TODO Auto-generated constructor stub
    }

    public VariableEntityWrapper getVariableEntityWrapper() {
        return this.variableEntityWrapper;
    }
    
    public void setVariablesFromScriptContent(String scriptContent) throws Exception {
        VariableEntityWrapper newVariableEntityWrapper = getVariableEntityWrapperFromScriptContent(scriptContent);
        if (newVariableEntityWrapper != null) {
            variableEntityWrapper.setVariables(newVariableEntityWrapper.getVariables());
        }else{
            newVariableEntityWrapper = new VariableEntityWrapper();
            newVariableEntityWrapper.setVariables(new ArrayList<VariableEntity>());
        }
        tableViewer.setInput(newVariableEntityWrapper.getVariables());
        tableViewer.refresh();
    }
    
    public VariableEntityWrapper getVariableEntityWrapperFromScriptContent(String scriptContent) throws Exception{
        VariableEntityWrapper newVariableEntityWrapper = null;
        if (scriptContent != null && scriptContent != StringUtils.EMPTY) {
            newVariableEntityWrapper = LocalVariableController.getInstance().toVariableEntityWrapper(scriptContent);
            return newVariableEntityWrapper;
        }
        return newVariableEntityWrapper;
    }
}
