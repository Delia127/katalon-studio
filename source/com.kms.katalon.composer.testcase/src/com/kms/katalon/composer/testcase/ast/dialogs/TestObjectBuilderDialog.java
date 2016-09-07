package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColumnViewerUtil;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;

public class TestObjectBuilderDialog extends TreeEntitySelectionDialog implements IAstDialogBuilder {
    private static final InputValueType[] defaultInputValueTypes = { InputValueType.Variable, InputValueType.GlobalVariable };

    private static final String pluginId = FrameworkUtil.getBundle(TestObjectBuilderDialog.class).getSymbolicName();


    private static final String OBJECT_FINDER_TAB_NAME = TreeEntityUtil.getReadableKeywordName(InputValueType.TestObject.getName());

    private static final String[] TEST_OBJECT_TABS = { OBJECT_FINDER_TAB_NAME, StringConstants.DIA_TAB_OTHER };

    private TestObjectBuilderDialog _instance;

    private TableViewer tableViewer;

    private ExpressionWrapper objectExpressionWrapper;

    private int fWidth = 60;

    private int fHeight = 18;

    private boolean haveOtherTypes;

    private StackLayout stackLayout;

    private Composite objectFinderComposite;

    private Composite otherTypesInputTableComposite;

    private Composite comboComposite;

    private Combo combo;

    public TestObjectBuilderDialog(Shell parentShell, ExpressionWrapper objectExpressionWrapper, boolean haveOtherTypes) {
        super(parentShell, new EntityLabelProvider(), new EntityProvider(),
                new EntityViewerFilter(new EntityProvider()));
        _instance = this;
        this.haveOtherTypes = haveOtherTypes;
        this.objectExpressionWrapper = objectExpressionWrapper.clone();
        setAllowMultiple(false);
        setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | getDefaultOrientation());
        try {
            setInput(TreeEntityUtil.getChildren(
                    null,
                    FolderController.getInstance().getObjectRepositoryRoot(
                            ProjectController.getInstance().getCurrentProject())));
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        refreshSelectionTree();
    }

    protected void refreshSelectionTree() {
        String testObjectId = null;
        if (isObjectExpressionFindTestObjectMethodCall()) {
            testObjectId = AstEntityInputUtil.findTestObjectIdFromFindTestObjectMethodCall((MethodCallExpressionWrapper) objectExpressionWrapper);
        }
        if (testObjectId == null) {
            return;
        }
        WebElementEntity selectedWebElement = null;
        FolderEntity objectRepositoryRoot = null;
        try {
            selectedWebElement = ObjectRepositoryController.getInstance().getWebElementByDisplayPk(testObjectId);
            objectRepositoryRoot = FolderController.getInstance().getObjectRepositoryRoot(
                    ProjectController.getInstance().getCurrentProject());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (selectedWebElement == null || objectRepositoryRoot == null) {
            return;
        }
        setInitialSelection(new WebElementTreeEntity(selectedWebElement, createSelectedTreeEntityHierachy(
                selectedWebElement.getParentFolder(), objectRepositoryRoot)));
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        parent.setLayoutData(new GridData(GridData.FILL_BOTH));
        parent.setLayout(new GridLayout(1, false));

        if (haveOtherTypes) {
            comboComposite = new Composite(parent, SWT.NONE);
            comboComposite.setLayout(new GridLayout(2, false));
            Label comboLabel = new Label(comboComposite, SWT.NONE);
            comboLabel.setText(StringConstants.DIA_LBL_OBJ_TYPE);

            combo = new Combo(comboComposite, SWT.DROP_DOWN);
            combo.setItems(TEST_OBJECT_TABS);
        }

        final Composite stackComposite = new Composite(parent, SWT.NONE);
        applyDialogFont(stackComposite);
        stackLayout = new StackLayout();
        stackComposite.setLayout(stackLayout);

        objectFinderComposite = new Composite(stackComposite, SWT.NONE);
        objectFinderComposite.setLayout(new GridLayout(1, false));

        TreeViewer treeViewer = createTreeViewer(objectFinderComposite);

        GridData data = new GridData(GridData.FILL_BOTH);
        data.widthHint = convertWidthInCharsToPixels(fWidth);
        data.heightHint = convertHeightInCharsToPixels(fHeight);

        Tree treeWidget = treeViewer.getTree();
        treeWidget.setLayoutData(data);
        treeWidget.setFont(parent.getFont());
        setValidator(new ISelectionStatusValidator() {

            @Override
            public IStatus validate(Object[] selection) {
                if (TreeEntityUtil.isValidTreeEntitySelectionType(selection,
                        com.kms.katalon.composer.components.impl.constants.StringConstants.TREE_OBJECT_TYPE_NAME)) {
                    return new Status(IStatus.OK, pluginId, IStatus.OK, null, null);
                }
                return new Status(IStatus.ERROR, pluginId, IStatus.ERROR, null, null);
            }
        });

        treeWidget.setEnabled(true);
        stackLayout.topControl = objectFinderComposite;

        if (haveOtherTypes) {
            createOtherTypesTab(stackComposite);

            if (isObjectExpressionFindTestObjectMethodCall()) {
                combo.select(0);
                stackLayout.topControl = objectFinderComposite;
            } else {
                combo.select(1);
                stackLayout.topControl = otherTypesInputTableComposite;
            }
        }

        refresh();
        return stackComposite;
    }

    private boolean isObjectExpressionFindTestObjectMethodCall() {
        return objectExpressionWrapper instanceof MethodCallExpressionWrapper
                && ((MethodCallExpressionWrapper) objectExpressionWrapper).isFindTestObjectMethodCall();
    }

    private void createOtherTypesTab(final Composite parent) {
        otherTypesInputTableComposite = new Composite(parent, SWT.NONE);
        otherTypesInputTableComposite.setLayout(new GridLayout(1, false));

        tableViewer = new TableViewer(otherTypesInputTableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        Table table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        ColumnViewerUtil.setTableActivation(tableViewer);

        createTableColumns(parent);

        addSwitchTypeComboListener(parent);
    }

    private void addSwitchTypeComboListener(final Composite parent) {
        combo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int index = combo.getSelectionIndex();
                if (index < 0 || index >= TEST_OBJECT_TABS.length) {
                    return;
                }
                String tabName = TEST_OBJECT_TABS[index];
                ExpressionWrapper newExpression = null;
                if (tabName.equals(OBJECT_FINDER_TAB_NAME)) {
                    newExpression = (ExpressionWrapper) InputValueType.TestObject.getNewValue(objectExpressionWrapper.getParent());
                    stackLayout.topControl = objectFinderComposite;
                } else {
                    newExpression = (ExpressionWrapper) defaultInputValueTypes[0].getNewValue(objectExpressionWrapper.getParent());
                    stackLayout.topControl = otherTypesInputTableComposite;
                    setSelectionResult(null);
                }
                newExpression.copyProperties(objectExpressionWrapper);
                objectExpressionWrapper = newExpression;

                refresh();
                parent.layout();
            }
        });
    }

    private void createTableColumns(final Composite parent) {
        TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValueType.getColumn().setText(StringConstants.DIA_COL_VALUE_TYPE);
        tableViewerColumnValueType.getColumn().setWidth(100);
        tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider());

        tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(
                tableViewer, defaultInputValueTypes) {
            @Override
            protected void setValue(Object element, Object value) {
                if (!(value instanceof Integer) || (int) value < 0 || (int) value >= inputValueTypes.length) {
                    return;
                }
                ASTNodeWrapper newAstNode = getNewAstNode(value, objectExpressionWrapper);
                if (newAstNode == null || !(newAstNode instanceof ExpressionWrapper)) {
                    return;
                }
                newAstNode.copyProperties(objectExpressionWrapper);
                objectExpressionWrapper = (ExpressionWrapper) newAstNode;
                refresh();
            }
        });

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnValue.getColumn().setText(StringConstants.DIA_COL_VALUE);
        tableViewerColumnValue.getColumn().setWidth(300);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider());
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer));
    }

    private static FolderTreeEntity createSelectedTreeEntityHierachy(FolderEntity folderEntity, FolderEntity rootFolder) {
        if (folderEntity == null || folderEntity.equals(rootFolder)) {
            return null;
        }
        return new FolderTreeEntity(folderEntity, createSelectedTreeEntityHierachy(folderEntity.getParentFolder(),
                rootFolder));
    }

    public void refresh() {
        if (!haveOtherTypes) {
            return;
        }
        List<ExpressionWrapper> objectExpressionWrapperList = new ArrayList<ExpressionWrapper>();
        objectExpressionWrapperList.add(objectExpressionWrapper);
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setInput(objectExpressionWrapperList);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        Button btnOK = createButton(parent, 102, IDialogConstants.OK_LABEL, true);
        btnOK.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                _instance.close();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    protected void convertSelectionToObjectMethodCall() {
        if (!(getFirstResult() instanceof WebElementTreeEntity)) {
            return;
        }
        WebElementTreeEntity webElementTreeEntity = (WebElementTreeEntity) getFirstResult();
        String objectPk = null;
        try {
            if (!(webElementTreeEntity.getObject() instanceof WebElementEntity)) {
                return;
            }
            objectPk = ((WebElementEntity) webElementTreeEntity.getObject()).getIdForDisplay();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (objectPk == null) {
            return;
        }
        ExpressionWrapper newMethodCall = AstEntityInputUtil.createNewFindTestObjectMethodCall(objectPk,
                objectExpressionWrapper.getParent());
        newMethodCall.copyProperties(objectExpressionWrapper);
        objectExpressionWrapper = newMethodCall;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(getDialogTitle());
    }

    @Override
    public ExpressionWrapper getReturnValue() {
        if (stackLayout.topControl == objectFinderComposite) {
            convertSelectionToObjectMethodCall();
        }
        return objectExpressionWrapper;
    }

    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_TEST_OBJ_INPUT;
    }
}
