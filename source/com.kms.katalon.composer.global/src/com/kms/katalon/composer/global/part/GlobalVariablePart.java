package com.kms.katalon.composer.global.part;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.global.constants.StringConstants;
import com.kms.katalon.composer.global.dialog.GlobalVariableBuilderDialog;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.global.GlobalVariableEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.constant.GroovyConstants;
import com.kms.katalon.groovy.util.GroovyRefreshUtil;

public class GlobalVariablePart implements EventHandler {

    private Table table;

    @Inject
    private IEventBroker eventBroker;

    private TableViewer tableViewer;

    private ToolItem tltmAdd, tltmRemove;

    private MPart mpart;

    private Composite composite;

    private ToolItem tltmClear;
    private ToolItem tltmEdit;
    private ToolItem tltmRefresh;

    @PostConstruct
    public void init(Composite parent, MPart mpart) {
        this.mpart = mpart;
        createComposite(parent);
        registerEventListeners();
    }

    private void registerEventListeners() {
        eventBroker.subscribe(EventConstants.GLOBAL_VARIABLE_REFRESH, this);
    }

    private void createComposite(Composite parent) {
        composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        composite.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());

        Composite compositeToolbar = new Composite(composite, SWT.NONE);
        compositeToolbar.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout gl_compositeToolbar = new GridLayout(1, false);
        gl_compositeToolbar.marginWidth = 0;
        gl_compositeToolbar.marginHeight = 0;
        compositeToolbar.setLayout(gl_compositeToolbar);
        compositeToolbar.setBackground(ColorUtil.getCompositeBackgroundColor());

        ToolBar toolBar = new ToolBar(compositeToolbar, SWT.FLAT);

        tltmAdd = new ToolItem(toolBar, SWT.NONE);
        tltmAdd.setText(StringConstants.PA_BTN_TIP_ADD);
        tltmAdd.setToolTipText(StringConstants.PA_BTN_TIP_ADD);
        tltmAdd.setImage(ImageConstants.IMG_16_ADD);

        tltmRemove = new ToolItem(toolBar, SWT.NONE);
        tltmRemove.setText(StringConstants.PA_BTN_TIP_REMOVE);
        tltmRemove.setToolTipText(StringConstants.PA_BTN_TIP_REMOVE);
        tltmRemove.setImage(ImageConstants.IMG_16_REMOVE);

        tltmClear = new ToolItem(toolBar, SWT.NONE);
        tltmClear.setText(StringConstants.PA_BTN_TIP_CLEAR);
        tltmClear.setToolTipText(StringConstants.PA_BTN_TIP_CLEAR);
        tltmClear.setImage(ImageConstants.IMG_16_CLEAR);

        tltmEdit = new ToolItem(toolBar, SWT.NONE);
        tltmEdit.setText(StringConstants.PA_BTN_TIP_EDIT);
        tltmEdit.setToolTipText(StringConstants.PA_BTN_TIP_EDIT);
        tltmEdit.setImage(ImageConstants.IMG_16_EDIT);

        tltmRefresh = new ToolItem(toolBar, SWT.NONE);
        tltmRefresh.setText(StringConstants.PA_BTN_TIP_REFRESH);
        tltmRefresh.setToolTipText(StringConstants.PA_BTN_TIP_REFRESH);
        tltmRefresh.setImage(ImageConstants.IMG_16_REFRESH);

        Composite compositeTable = new Composite(composite, SWT.NONE);
        GridLayout gl_compositeTable = new GridLayout(1, false);
        gl_compositeTable.marginWidth = 0;
        gl_compositeTable.marginHeight = 0;
        compositeTable.setLayout(gl_compositeTable);
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        compositeTable.setBounds(0, 0, 64, 64);

        tableViewer = new TableViewer(compositeTable, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        tableViewer.setContentProvider(ArrayContentProvider.getInstance());

        Menu popupMenu = new Menu(table);
        MenuItem showPreferenceMenuItem = new MenuItem(popupMenu, SWT.CASCADE);
        showPreferenceMenuItem.setText(StringConstants.PA_MENU_CONTEXT_SHOW_PREFERENCES);
        showPreferenceMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();

                if (selection == null || selection.getFirstElement() == null) return;

                eventBroker.post(EventConstants.GLOBAL_VARIABLE_SHOW_REFERENCES, selection.getFirstElement());
            }
        });
        table.setMenu(popupMenu);

        TableViewerColumn tableViewerColumnName = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnName = tableViewerColumnName.getColumn();
        tblclmnName.setWidth(100);
        tblclmnName.setText(StringConstants.PA_COL_NAME);
        tableViewerColumnName.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element != null && element instanceof GlobalVariableEntity) {
                    return ((GlobalVariableEntity) element).getName();
                }
                return "";
            }
        });

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tblclmnValue = tableViewerColumnValue.getColumn();
        tblclmnValue.setWidth(150);
        tblclmnValue.setText(StringConstants.PA_COL_VALUE);
        tableViewerColumnValue.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element != null && element instanceof GlobalVariableEntity) {
                    return ((GlobalVariableEntity) element).getInitValue();
                }
                return "";
            }
        });

        setInput();
        registerControlModifyListeners();
    }

    private void registerControlModifyListeners() {
        tltmAdd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Point pt = composite.toDisplay(1, 1);
                GlobalVariableBuilderDialog dialog = new GlobalVariableBuilderDialog(composite.getShell(), pt);
                if (dialog.open() == Dialog.OK) {
                    GlobalVariableEntity variable = dialog.getVariable();
                    tableViewer.add(variable);
                    tableViewer.refresh(variable);
                    setDirty(true);
                }
            }
        });

        tltmEdit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
                if (selection.getFirstElement() == null) return;
                GlobalVariableEntity selectedVariable = (GlobalVariableEntity) selection.getFirstElement();

                Point pt = composite.toDisplay(1, 1);
                GlobalVariableBuilderDialog dialog = new GlobalVariableBuilderDialog(composite.getShell(),
                        selectedVariable, pt);
                String variableName = selectedVariable.getName();
                String variableValue = selectedVariable.getInitValue();
                if (dialog.open() == Dialog.OK) {
                    GlobalVariableEntity variable = dialog.getVariable();
                    tableViewer.refresh(variable);

                    boolean needToUpdateReferences = false;

                    if (!variableValue.equals(variable.getInitValue())) {
                        setDirty(true);
                    }

                    if (!variableName.equals(variable.getName())) {
                        setDirty(true);
                        needToUpdateReferences = true;
                    }

                    try {
                        if (mpart.isDirty()) {
                            save(mpart);
                        }
                        
                        if (needToUpdateReferences) {
                            GroovyRefreshUtil.updateScriptReferencesInTestCaseAndCustomScripts("GlobalVariable."
                                    + variableName, "GlobalVariable." + variable.getName(), ProjectController
                                    .getInstance().getCurrentProject());
                        }
                    } catch (Exception ex) {
                        MessageDialog.openWarning(null, StringConstants.ERROR_TITLE,
                                StringConstants.PA_ERROR_MSG_UNABLE_TO_UPDATE_VAR_REFERENCES);
                    }

                }
            }
        });

        tltmRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
                if (selection.size() > 0) {
                    tableViewer.remove(selection.toArray());
                    setDirty(true);
                }
            }
        });

        tltmClear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (tableViewer.getTable().getItemCount() > 0) {
                    tableViewer.getTable().removeAll();
                    setDirty(true);
                }
            }
        });

        tltmRefresh.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings({ "restriction" })
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    ProjectEntity project = ProjectController.getInstance().getCurrentProject();
                    if (project != null) {
                        if (mpart.isDirty()) {
                            MessageDialog.openInformation(tltmRefresh.getDisplay().getActiveShell(),
                                    StringConstants.PA_INFO_TITLE, StringConstants.PA_INFO_MSG_REQUIRE_SAVE_B4_REFRESH);
                            return;
                        }
                        List<GlobalVariableEntity> globalVariables = GlobalVariableController.getInstance()
                                .getAllGlobalVariables(project);

                        if (globalVariables == tableViewer.getInput()) return;

                        GlobalVariableController.getInstance().updateVariables(globalVariables, project);
                        setInput();
                    }
                } catch (Exception ex) {
                    LoggerSingleton.getInstance().getLogger().error(ex);
                }

            }
        });
    }

    @SuppressWarnings("restriction")
    private void setInput() {
        try {
            ProjectEntity project = ProjectController.getInstance().getCurrentProject();
            if (project != null) {
                composite.setVisible(true);
                List<GlobalVariableEntity> globalVariables = GlobalVariableController.getInstance()
                        .getAllGlobalVariables(project);
                tableViewer.setInput(globalVariables);
                tableViewer.refresh();
            } else {
                composite.setVisible(false);
            }
        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
        }
    }

    @Override
    public void handleEvent(Event event) {
        if (event.getTopic().equals(EventConstants.GLOBAL_VARIABLE_REFRESH)) {
            setInput();
        }
    }

    private void setDirty(boolean isDirty) {
        mpart.setDirty(isDirty);
    }

    @SuppressWarnings("restriction")
    @Persist
    public void save(MDirtyable dirty) {
        try {
            if (dirty.isDirty()) {
                List<GlobalVariableEntity> variables = new ArrayList<GlobalVariableEntity>();
                List<String> names = new ArrayList<String>();
                for (TableItem item : tableViewer.getTable().getItems()) {
                    GlobalVariableEntity globalVariable = (GlobalVariableEntity) item.getData();
                    if (!isValidName(globalVariable.getName())) {
                        MessageDialog.openWarning(
                                null,
                                StringConstants.PA_WARN_TITLE_INVALID_VAR,
                                MessageFormat.format(StringConstants.PA_WARN_MSG_INVALID_VAR_NAME,
                                        globalVariable.getName()));
                        return;
                    }
                    if (names.contains(globalVariable.getName())) {
                        MessageDialog.openWarning(
                                null,
                                StringConstants.PA_WARN_TITLE_INVALID_VAR,
                                MessageFormat.format(StringConstants.PA_WARN_MSG_DUPLICATE_VAR_NAME,
                                        globalVariable.getName()));
                        return;
                    }
                    if (globalVariable.getInitValue().isEmpty()) {
                        globalVariable.setInitValue("''");
                    }
                    List<ASTNode> nodes = null;
                    try {
                        nodes = new AstBuilder().buildFromString(globalVariable.getInitValue());
                    } catch (MultipleCompilationErrorsException e) {
                        globalVariable.setInitValue("'" + globalVariable.getInitValue().replace("'", "\\'") + "'");
                    }
                    if (nodes != null && nodes.size() == 1) {
                        BlockStatement statement = (BlockStatement) nodes.get(0);
                        if (statement.getStatements().size() == 1
                                && statement.getStatements().get(0) instanceof ReturnStatement) {
                            ReturnStatement returnStatement = (ReturnStatement) statement.getStatements().get(0);
                            if (!(returnStatement.getExpression() instanceof ConstantExpression)) {
                                globalVariable.setInitValue("'" + globalVariable.getInitValue() + "'");
                            }
                        }
                    }
                    names.add(globalVariable.getName());
                    variables.add(globalVariable);

                }
                GlobalVariableController.getInstance().updateVariables(variables,
                        ProjectController.getInstance().getCurrentProject());
                setInput();
                setDirty(false);
            }
        } catch (Exception e) {
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_UNABLE_TO_SAVE_ALL_VAR);
            LoggerSingleton.getInstance().getLogger().error(e);
        }
    }

    private boolean isValidName(String name) {
        return GroovyConstants.isValidVariableName(name);
    }
}
