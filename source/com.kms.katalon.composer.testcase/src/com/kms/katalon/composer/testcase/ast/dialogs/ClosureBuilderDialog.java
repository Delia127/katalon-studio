package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColumnViewerUtil;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.editors.TypeSelectionDialogCellEditor;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ParameterWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClosureExpressionWrapper;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.parts.ITestCasePart;
import com.kms.katalon.composer.testcase.parts.TestStepManualComposite;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;

public class ClosureBuilderDialog extends Dialog implements IAstDialogBuilder, ITestCasePart {

    private static final String NEW_PARAM_DEFAULT_NAME = "param";

    private ClosureBuilderDialog _instance;

    private TableViewer paramTableViewer;

    private TestStepManualComposite testStepManualComposite;

    private List<ParameterWrapper> parameterList = new ArrayList<ParameterWrapper>();

    private ClosureExpressionWrapper tempClosure = null;

    public ClosureBuilderDialog(Shell parentShell, ClosureExpressionWrapper closureExpressionWrapper,
            ASTNodeWrapper parent) {
        super(parentShell);
        initData(closureExpressionWrapper, parent);
        _instance = this;
    }

    protected void initData(ClosureExpressionWrapper closureExpressionWrapper, ASTNodeWrapper parent) {
        tempClosure = closureExpressionWrapper.clone();
        for (ParameterWrapper parameter : closureExpressionWrapper.getParameters()) {
            parameterList.add(parameter);
        }
    }

    public TestCaseTreeTableInput getTreeTableInput() {
        return testStepManualComposite.getTreeTableInput();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));

        Composite paramComposite = new Composite(container, SWT.NONE);
        paramComposite.setLayout(new GridLayout(2, false));
        paramComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        createParamTableComposite(paramComposite);
        createParamButtonComposite(paramComposite);
        refreshParamTable();

        Composite childComposite = new Composite(container, SWT.NONE);
        childComposite.setLayout(new GridLayout(1, false));
        childComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        testStepManualComposite = new TestStepManualComposite(this, childComposite);
        ScriptNodeWrapper scriptNodeWrapper = new ScriptNodeWrapper();
        scriptNodeWrapper.setMainBlock(tempClosure.getBlock());

        testStepManualComposite.loadASTNodesToTreeTable(scriptNodeWrapper);
        

        return container;
    }

    private void createParamButtonComposite(Composite paramComposite) {
        Composite paramButtonComposite = new Composite(paramComposite, SWT.NONE);
        paramButtonComposite.setLayout(new GridLayout());
        paramButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.RIGHT, false, false, 1, 1));

        new Label(paramButtonComposite, SWT.NONE);
        createParamInsertButton(paramButtonComposite);
        createParamRemoveButton(paramButtonComposite);
        createParamMoveUpButton(paramButtonComposite);
        createParamMoveDownButton(paramButtonComposite);
    }

    protected Class<?> getClassFromIType(IType type) {
        return AstKeywordsInputUtil.loadType(type.getFullyQualifiedName(), tempClosure.getScriptClass());
    }

    protected void createParamTableComposite(Composite container) {
        Composite tableComposite = new Composite(container, SWT.NONE);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        tableComposite.setLayout(new GridLayout());

        Label paramLabel = new Label(tableComposite, SWT.NONE);
        paramLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        paramLabel.setText(StringConstants.LABEL_PARAMETER_LIST);

        paramTableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        Table table = paramTableViewer.getTable();
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gridData.heightHint = 50;
        table.setLayoutData(gridData);
        table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));
        table.setHeaderVisible(true);

        ColumnViewerUtil.setTableActivation(paramTableViewer);

        addTableColumnParamType();
        addTableColumnParamName();
    }

    private void addTableColumnParamName() {
        TableViewerColumn tableViewerColumnParamName = new TableViewerColumn(paramTableViewer, SWT.NONE);
        tableViewerColumnParamName.getColumn().setText(StringConstants.DIA_COL_PARAM_NAME);
        tableViewerColumnParamName.getColumn().setWidth(365);
        tableViewerColumnParamName.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof ParameterWrapper) {
                    ParameterWrapper parameter = (ParameterWrapper) element;
                    return parameter.getName();
                }
                return StringUtils.EMPTY;
            }
        });
        tableViewerColumnParamName.setEditingSupport(new EditingSupport(paramTableViewer) {
            @Override
            protected void setValue(Object element, Object value) {
                if (!(element instanceof ParameterWrapper && value instanceof String)) {
                    return;
                }
                ParameterWrapper oldParameterWrapper = (ParameterWrapper) element;
                int parameterIndex = parameterList.indexOf(oldParameterWrapper);
                if (parameterIndex >= 0 && parameterIndex < parameterList.size()) {
                    parameterList.set(parameterIndex, new ParameterWrapper(oldParameterWrapper.getType().getTypeClass(),
                            (String) value, tempClosure));
                    refreshParamTable();
                }
            }

            @Override
            protected Object getValue(Object element) {
                if (element instanceof ParameterWrapper) {
                    return ((ParameterWrapper) element).getName();
                }
                return StringUtils.EMPTY;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor(paramTableViewer.getTable());
            }

            @Override
            protected boolean canEdit(Object element) {
                return element instanceof ParameterWrapper;
            }
        });
    }

    private void addTableColumnParamType() {
        TableViewerColumn tableViewerColumnParamType = new TableViewerColumn(paramTableViewer, SWT.NONE);
        tableViewerColumnParamType.getColumn().setText(StringConstants.DIA_COL_PARAM_TYPE);
        tableViewerColumnParamType.getColumn().setWidth(365);
        tableViewerColumnParamType.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof ParameterWrapper) {
                    ParameterWrapper parameter = (ParameterWrapper) element;
                    return parameter.getType().getName();
                }
                return StringUtils.EMPTY;
            }
        });
        tableViewerColumnParamType.setEditingSupport(new EditingSupport(paramTableViewer) {
            @Override
            protected void setValue(Object element, Object value) {
                if (!(element instanceof ParameterWrapper && value instanceof IType)) {
                    return;
                }
                ParameterWrapper oldParameterWrapper = (ParameterWrapper) element;
                Class<?> newClass = getClassFromIType((IType) value);
                int parameterIndex = parameterList.indexOf(oldParameterWrapper);
                if ((!(parameterIndex >= 0 && parameterIndex < parameterList.size())) || newClass == null) {
                    return;
                }
                parameterList.set(parameterIndex,
                        new ParameterWrapper(newClass, oldParameterWrapper.getName(), tempClosure));
                refreshParamTable();
            }

            @Override
            protected Object getValue(Object element) {
                if (element instanceof ParameterWrapper) {
                    return ((ParameterWrapper) element).getType().getName();
                }
                return StringUtils.EMPTY;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                if (element instanceof ParameterWrapper) {
                    return new TypeSelectionDialogCellEditor(paramTableViewer.getTable(),
                            ((ParameterWrapper) element).getType().getName());
                }
                return null;
            }

            @Override
            protected boolean canEdit(Object element) {
                return element instanceof ParameterWrapper;
            }
        });
    }

    public void refreshParamTable() {
        paramTableViewer.setContentProvider(new ArrayContentProvider());
        paramTableViewer.setInput(parameterList);
        paramTableViewer.refresh();
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createOKButton(parent);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    private void createOKButton(Composite parent) {
        Button btnOK = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        btnOK.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _instance.close();
            }
        });
    }

    private void createParamRemoveButton(Composite parent) {
        Button btnRemove = new Button(parent, SWT.PUSH);
        btnRemove.setText(StringConstants.DIA_BTN_REMOVE);
        setButtonLayoutData(btnRemove);
        btnRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                for (int index : paramTableViewer.getTable().getSelectionIndices()) {
                    if (index >= 0 && index < parameterList.size()) {
                        parameterList.remove(index);
                    }
                }
                paramTableViewer.refresh();
            }
        });
    }

    private void createParamInsertButton(Composite parent) {
        Button btnInsert = new Button(parent, SWT.PUSH);
        btnInsert.setText(StringConstants.DIA_BTN_INSERT);
        setButtonLayoutData(btnInsert);
        btnInsert.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = paramTableViewer.getTable().getSelectionIndex();
                ParameterWrapper parameter = new ParameterWrapper(Object.class, NEW_PARAM_DEFAULT_NAME, tempClosure);
                if (selectionIndex < 0 || selectionIndex >= parameterList.size()) {
                    parameterList.add(parameter);
                } else {
                    parameterList.add(selectionIndex + 1, parameter);
                }
                paramTableViewer.refresh();
                paramTableViewer.getTable().setSelection(selectionIndex + 1);
            }
        });
    }

    private void createParamMoveUpButton(Composite parent) {
        Button btnMoveUp = new Button(parent, SWT.PUSH);
        btnMoveUp.setText(StringConstants.PA_BTN_TIP_MOVE_UP);
        setButtonLayoutData(btnMoveUp);
        btnMoveUp.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = paramTableViewer.getTable().getSelectionIndex();
                if (selectionIndex == 0) {
                    return;
                }
                Collections.swap(parameterList, selectionIndex, selectionIndex - 1);
                paramTableViewer.refresh();
                paramTableViewer.getTable().setSelection(selectionIndex - 1);
            }
        });
    }

    private void createParamMoveDownButton(Composite parent) {
        Button btnMoveDown = new Button(parent, SWT.PUSH);
        btnMoveDown.setText(StringConstants.PA_BTN_TIP_MOVE_DOWN);
        setButtonLayoutData(btnMoveDown);
        btnMoveDown.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = paramTableViewer.getTable().getSelectionIndex();
                if (selectionIndex == parameterList.size() - 1) {
                    return;
                }
                Collections.swap(parameterList, selectionIndex, selectionIndex + 1);
                paramTableViewer.refresh();
                paramTableViewer.getTable().setSelection(selectionIndex + 1);
            }
        });
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(getDialogTitle());
    }

    public ClosureExpressionWrapper getReturnValue() {
        tempClosure.setParameters(parameterList.toArray(new ParameterWrapper[parameterList.size()]));
        tempClosure.setBlock(getTreeTableInput().getMainClassNode().getBlock());
        return tempClosure;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(900, 700);
    }

    public String getDialogTitle() {
        return StringConstants.TREE_CLOSURE;
    }

    @Override
    public TestCaseEntity getTestCase() {
        // no testcase in closure
        return null;
    }

    @Override
    public void setDirty(boolean isDirty) {
        // no need to set dirty for dialog
    }

    @Override
    public void addVariables(VariableEntity[] variables) {
        // not handling variables
    }

    @Override
    public VariableEntity[] getVariables() {
        // not handling variables
        return new VariableEntity[0];
    }

    @Override
    public List<AstTreeTableNode> getDragNodes() {
        // drag and drop is not supported
        return new ArrayList<AstTreeTableNode>();
    }

    @Override
    public void createDynamicGotoMenu(Menu menu) {
        // cannot have go to menu in dialog
    }

    @Override
    public void deleteVariables(List<VariableEntity> variableList) {
        // not handling variables
    }

}
