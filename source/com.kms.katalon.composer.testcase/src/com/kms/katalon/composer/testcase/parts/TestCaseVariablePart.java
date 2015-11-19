package com.kms.katalon.composer.testcase.parts;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MGenericTile;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.dialogs.TestCaseVariableBuilderDialog;
import com.kms.katalon.composer.testcase.support.VariableDefaultValueEditingSupport;
import com.kms.katalon.composer.testcase.support.VariableNameEditingSupport;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.execution.util.SyntaxUtil;
import com.kms.katalon.groovy.constant.GroovyConstants;

public class TestCaseVariablePart {
    private Composite parent;
    private MPart mpart;
    private TableViewer tableViewer;
    private TestCaseCompositePart parentTestCaseCompositePart;
    private List<VariableEntity> variables;

    @PostConstruct
    public void init(Composite parent, MPart mpart) {
        this.parent = parent;

        this.mpart = mpart;

        if (mpart.getParent().getParent() instanceof MGenericTile
                && ((MGenericTile<?>) mpart.getParent().getParent()) instanceof MCompositePart) {
            MCompositePart compositePart = (MCompositePart) (MGenericTile<?>) mpart.getParent().getParent();
            if (compositePart.getObject() instanceof TestCaseCompositePart) {
                parentTestCaseCompositePart = ((TestCaseCompositePart) compositePart.getObject());
            }
        }

        createComponents();
    }

    private void createComponents() {
        parent.setLayout(new FillLayout(SWT.HORIZONTAL));

        final Composite container = new Composite(parent, SWT.NONE);
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
        tltmAddVariable.setToolTipText(StringConstants.PA_BTN_TIP_ADD);
        tltmAddVariable.setImage(ImageConstants.IMG_24_ADD);

        ToolItem tltmRemove = new ToolItem(toolBar, SWT.NONE);
        tltmRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                removeVariables();
            }
        });
        tltmRemove.setText(StringConstants.PA_BTN_TIP_REMOVE);
        tltmRemove.setToolTipText(StringConstants.PA_BTN_TIP_REMOVE);
        tltmRemove.setImage(ImageConstants.IMG_24_REMOVE);

        ToolItem tltmClear = new ToolItem(toolBar, SWT.NONE);
        tltmClear.setText(StringConstants.PA_BTN_TIP_CLEAR);
        tltmClear.setToolTipText(StringConstants.PA_BTN_TIP_CLEAR);
        tltmClear.setImage(ImageConstants.IMG_24_CLEAR);

        tltmClear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                clearVariables();
            }
        });

        ToolItem tltmEdit = new ToolItem(toolBar, SWT.NONE);
        tltmEdit.setText(StringConstants.PA_BTN_TIP_EDIT);
        tltmEdit.setToolTipText(StringConstants.PA_BTN_TIP_EDIT);
        tltmEdit.setImage(ImageConstants.IMG_24_EDIT);

        tltmEdit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
                if (selection.getFirstElement() == null) return;
                VariableEntity selectedVariable = (VariableEntity) selection.getFirstElement();

                TestCaseVariableBuilderDialog dialog = new TestCaseVariableBuilderDialog(container.getShell(),
                        selectedVariable);
                if (dialog.open() == Dialog.OK) {
                    VariableEntity variable = dialog.getParam();
                    tableViewer.refresh(variable);
                    setDirty(true);
                }
            }
        });

        ToolItem tltmUp = new ToolItem(toolBar, SWT.NONE);
        tltmUp.setText(StringConstants.PA_BTN_TIP_MOVE_UP);
        tltmUp.setToolTipText(StringConstants.PA_BTN_TIP_MOVE_UP);
        tltmUp.setImage(ImageConstants.IMG_24_UP);
        tltmUp.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                upVariable();
            }
        });

        ToolItem tltmDown = new ToolItem(toolBar, SWT.NONE);
        tltmDown.setText(StringConstants.PA_BTN_TIP_MOVE_DOWN);
        tltmDown.setToolTipText(StringConstants.PA_BTN_TIP_MOVE_DOWN);
        tltmDown.setImage(ImageConstants.IMG_24_DOWN);
        tltmDown.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                downVariable();
            }
        });

        Composite compositeTable = new Composite(container, SWT.NONE);
        compositeTable.setLayout(new FillLayout(SWT.HORIZONTAL));
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        tableViewer = new TableViewer(compositeTable, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
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
                    return Integer.toString(variables.indexOf(element) + 1);
                }
                return StringUtils.EMPTY;
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
                return StringUtils.EMPTY;
            }
        });
        TableColumn tblclmnName = tableViewerColumnName.getColumn();
        tblclmnName.setWidth(300);
        tblclmnName.setText(StringConstants.PA_COL_NAME);

        TableViewerColumn tableViewerColumnDefaultValue = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnDefaultValue.setEditingSupport(new VariableDefaultValueEditingSupport(tableViewer, this));
        TableColumn tblclmnDefaultValue = tableViewerColumnDefaultValue.getColumn();
        tblclmnDefaultValue.setWidth(500);
        tblclmnDefaultValue.setText(StringConstants.PA_COL_DEFAULT_VALUE);
        tableViewerColumnDefaultValue.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element != null && element instanceof VariableEntity) {
                    return ((VariableEntity) element).getDefaultValue();
                }
                return StringUtils.EMPTY;
            }
        });

        tableViewer.setContentProvider(new ArrayContentProvider());
    }

    private void addVariable() {
        TestCaseVariableBuilderDialog dialog = new TestCaseVariableBuilderDialog(tableViewer.getTable().getDisplay()
                .getActiveShell());
        if (dialog.open() == Dialog.OK) {
            variables.add(dialog.getParam());
            tableViewer.refresh();
            setDirty(true);
        }
    }

    public void addVariable(VariableEntity[] variablesArray) {
        for (VariableEntity addedVariable : variablesArray) {
            boolean exists = false;
            for (VariableEntity currentVariable : getVariables()) {
                if (currentVariable.getName().equals(addedVariable.getName())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                variables.add(addedVariable);
                tableViewer.refresh();
                setDirty(true);
            }
        }
    }

    private void removeVariables() {
        StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
        Object[] selectionElements = selection.toArray();
        if (selectionElements.length > 0) {
            for (Object object : selectionElements) {
                if (object instanceof VariableEntity) {
                    variables.remove((VariableEntity) object);
                }
            }
            tableViewer.refresh();
            setDirty(true);
        }
    }

    private void clearVariables() {
        variables.clear();
        tableViewer.refresh();
        setDirty(true);
    }

    private void upVariable() {
        StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
        if (selection == null || selection.getFirstElement() == null) return;
        VariableEntity variable = (VariableEntity) selection.getFirstElement();
        int index = variables.indexOf(variable);
        if (index > 0) {
            Collections.swap(variables, index, index - 1);
            tableViewer.refresh();
            setDirty(true);
        }
    }

    private void downVariable() {
        StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
        if (selection == null || selection.getFirstElement() == null) return;
        VariableEntity variable = (VariableEntity) selection.getFirstElement();
        int index = variables.indexOf(variable);
        if (index < variables.size() - 1) {
            Collections.swap(variables, index, index + 1);
            tableViewer.refresh();
            setDirty(true);
        }
    }

    public void setDirty(boolean isDirty) {
        mpart.setDirty(isDirty);
        parentTestCaseCompositePart.checkDirty();
    }

    public void loadVariables() {
        TestCaseEntity testCase = parentTestCaseCompositePart.getTestCase();
        if (testCase != null && testCase.getVariables() != null) {
            variables = testCase.getVariables();
            tableViewer.setInput(variables);
            tableViewer.refresh();
        }
    }

    public MPart getMPart() {
        return this.mpart;
    }

    public List<VariableEntity> getVariables() {
        return variables;
    }

    public boolean validateVariables() {
        StringBuilder errorCollector = new StringBuilder();
        List<String> names = new ArrayList<String>();
        for (VariableEntity variable : variables) {
            int index = variables.indexOf(variable) + 1;
            String variableName = variable.getName();
            String variableDefaultValue = variable.getDefaultValue();
            if (variableDefaultValue == null || variableDefaultValue.isEmpty()) variableDefaultValue = null;

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
                errorCollector.append(MessageFormat.format(
                        StringConstants.PA_ERROR_MSG_INVALID_DEFAULT_VAR_VAL_AT_INDEX, index, e.getMessage()));
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

    @Persist
    private boolean doSave() {
        try {
            parentTestCaseCompositePart.save();
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }
}
