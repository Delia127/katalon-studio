package com.kms.katalon.composer.mobile.recorder.composites;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.bindings.keys.IKeyLookup;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.openqa.selenium.Keys;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.control.CTreeViewer;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.KeyEventUtil;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.components.util.ColumnViewerUtil;
import com.kms.katalon.composer.mobile.objectspy.actions.MobileActionMapping;
import com.kms.katalon.composer.mobile.objectspy.element.CapturedMobileElementConverter;
import com.kms.katalon.composer.mobile.objectspy.element.SnapshotMobileElement;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;
import com.kms.katalon.composer.mobile.objectspy.util.MobileActionUtil;
import com.kms.katalon.composer.mobile.objectspy.viewer.CapturedObjectTableViewer;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.testcase.ast.treetable.AstBuiltInKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.components.KeywordTreeViewerToolTipSupport;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants.AddAction;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput.NodeAddType;
import com.kms.katalon.composer.testcase.parts.ITestCasePart;
import com.kms.katalon.composer.testcase.parts.TestCaseVariableView;
import com.kms.katalon.composer.testcase.providers.AstTreeItemLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstTreeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstTreeTableContentProvider;
import com.kms.katalon.composer.testcase.support.DescriptionColumnEditingSupport;
import com.kms.katalon.composer.testcase.support.InputColumnEditingSupport;
import com.kms.katalon.composer.testcase.support.ItemColumnEditingSupport;
import com.kms.katalon.composer.testcase.support.OutputColumnEditingSupport;
import com.kms.katalon.composer.testcase.util.TestCaseMenuUtil;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;

public class MobileRecordedStepsViewComposite extends Composite implements ITestCasePart {

    private Dialog parentDialog;

    private ScriptNodeWrapper wrapper;

    public ScriptNodeWrapper getWrapper() {
        return wrapper;
    }

    public void setWrapper(ScriptNodeWrapper wrapper) {
        this.wrapper = wrapper;
    }

    private CTreeViewer treeViewer;

    private TestCaseTreeTableInput treeTableInput;

    private ToolItem tltmAddStep, tltmRemoveStep, tltmUp, tltmDown;

    private TestCaseVariableView variableView;

    private CapturedObjectTableViewer capturedElementsTableViewer;

    public CapturedObjectTableViewer getCapturedElementsTableViewer() {
        return capturedElementsTableViewer;
    }

    public void setCapturedElementsTableViewer(CapturedObjectTableViewer capturedElementsTableViewer) {
        this.capturedElementsTableViewer = capturedElementsTableViewer;
    }

    public MobileRecordedStepsViewComposite(Dialog parentDialog, Composite parent, int style) {
        super(parent, style | SWT.NONE);
        this.parentDialog = parentDialog;
        wrapper = new ScriptNodeWrapper();
        wrapper.addDefaultImports();
        wrapper.addImport(Keys.class);
        this.createComposite(parent);
    }

    public MobileRecordedStepsViewComposite(Dialog parentDialog, Composite parent) {
        this(parentDialog, parent, 0);
    }

    public void createComposite(Composite parent) {
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glContainer = new GridLayout();
        glContainer.marginWidth = 0;
        glContainer.marginHeight = 0;
        setLayout(glContainer);

        createStepButtons(this);

        Composite compositeTable = new Composite(this, SWT.NONE);
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        treeViewer = new CTreeViewer(compositeTable, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        Tree childTableTree = treeViewer.getTree();
        childTableTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        childTableTree.setLinesVisible(ControlUtils.shouldLineVisble(childTableTree.getDisplay()));
        childTableTree.setHeaderVisible(true);

        TreeColumnLayout treeColumnLayout = new TreeColumnLayout();
        compositeTable.setLayout(treeColumnLayout);

        addTreeTableColumn(treeViewer, treeColumnLayout, StringConstants.PA_COL_ITEM, 175, 0,
                new AstTreeItemLabelProvider(), new ItemColumnEditingSupport(treeViewer, this));
        addTreeTableColumn(treeViewer, treeColumnLayout, StringConstants.PA_COL_OBJ, 120, 0, new AstTreeLabelProvider(),
                new CapturedMobileElementEdittingSupport(treeViewer, this));
        addTreeTableColumn(treeViewer, treeColumnLayout, StringConstants.PA_COL_INPUT, 150, 0,
                new AstTreeLabelProvider(), new InputColumnEditingSupport(treeViewer, this));
        addTreeTableColumn(treeViewer, treeColumnLayout, StringConstants.PA_COL_OUTPUT, 80, 0,
                new AstTreeLabelProvider(), new OutputColumnEditingSupport(treeViewer, this));
        addTreeTableColumn(treeViewer, treeColumnLayout, StringConstants.PA_COL_DESCRIPTION, 100, 0,
                new AstTreeLabelProvider(), new DescriptionColumnEditingSupport(treeViewer, this));

        treeViewer.setContentProvider(new AstTreeTableContentProvider());

        setTreeTableActivation();

        treeTableInput = new TestCaseTreeTableInput(wrapper, treeViewer, this);
        treeViewer.setInput(treeTableInput.getMainClassNode().getAstChildren());

        createTreeTableMenu();

        KeywordTreeViewerToolTipSupport.enableFor(treeViewer);
    }

    private void createStepButtons(Composite compositeSteps) {
        Composite compositeToolbars = new Composite(compositeSteps, SWT.NONE);
        compositeToolbars.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        compositeToolbars.setLayout(layout);

        ToolBar toolbar = new ToolBar(compositeToolbars, SWT.FLAT | SWT.RIGHT);
        toolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        toolbar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        SelectionListener selectionListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Object item = e.getSource();
                if (item instanceof ToolItem) {
                    performToolItemSelected((ToolItem) e.getSource(), e);
                    return;
                }
                if (item instanceof MenuItem) {
                    performMenuItemSelected((MenuItem) e.getSource());
                }
            }
        };

        tltmAddStep = new ToolItem(toolbar, SWT.DROP_DOWN);
        tltmAddStep.setText(StringConstants.ADD);
        tltmAddStep.setImage(ImageManager.getImage(IImageKeys.ADD_16));
        tltmAddStep.addSelectionListener(selectionListener);

        Menu addMenu = new Menu(tltmAddStep.getParent().getShell());
        tltmAddStep.setData(addMenu);
        TestCaseMenuUtil.fillActionMenu(TreeTableMenuItemConstants.AddAction.Add, selectionListener, addMenu,
                new int[] { TreeTableMenuItemConstants.METHOD_MENU_ITEM_ID,
                        TreeTableMenuItemConstants.getBuildInKeywordID("Mobile"),
                        TreeTableMenuItemConstants.getBuildInKeywordID("WS"),
                        TreeTableMenuItemConstants.getBuildInKeywordID("Windows") });

        tltmRemoveStep = new ToolItem(toolbar, SWT.NONE);
        tltmRemoveStep.setText(StringConstants.REMOVE);
        tltmRemoveStep.setImage(ImageManager.getImage(IImageKeys.DELETE_16));
        tltmRemoveStep.addSelectionListener(selectionListener);

        tltmUp = new ToolItem(toolbar, SWT.NONE);
        tltmUp.setText("Move Up");
        tltmUp.setImage(ImageManager.getImage(IImageKeys.MOVE_UP_16));
        tltmUp.addSelectionListener(selectionListener);

        tltmDown = new ToolItem(toolbar, SWT.NONE);
        tltmDown.setText("Move Down");
        tltmDown.setImage(ImageManager.getImage(IImageKeys.MOVE_DOWN_16));
        tltmDown.addSelectionListener(selectionListener);
    }

    private TreeViewerColumn addTreeTableColumn(TreeViewer parent, TreeColumnLayout treeColumnLayout, String headerText,
            int width, int weight, CellLabelProvider labelProvider, EditingSupport editingSupport) {
        TreeViewerColumn treeTableColumn = new TreeViewerColumn(parent, SWT.NONE);
        TreeColumn treeColumn = treeTableColumn.getColumn();
        treeColumn.setWidth(width);
        treeColumn.setMoveable(true);
        treeColumn.setText(headerText);
        treeTableColumn.setLabelProvider(labelProvider);
        treeTableColumn.setEditingSupport(editingSupport);
        treeColumnLayout.setColumnData(treeTableColumn.getColumn(),
                new ColumnWeightData(weight, treeColumn.getWidth()));

        return treeTableColumn;
    }

    private void setTreeTableActivation() {
        ColumnViewerUtil.setTreeTableActivation(treeViewer);
    }

    public void performToolItemSelected(ToolItem toolItem, SelectionEvent selectionEvent) {
        treeViewer.applyEditorValue();
        if (toolItem.equals(tltmAddStep)) {
            openToolItemForAddMenu(toolItem, selectionEvent);
            return;
        }
        if (toolItem.equals(tltmRemoveStep)) {
            getTreeTableInput().removeSelectedRows();
            return;
        }
        if (toolItem.equals(tltmUp)) {
            getTreeTableInput().moveUp();
            return;
        }
        if (toolItem.equals(tltmDown)) {
            getTreeTableInput().moveDown();
            return;
        }
    }

    private void openToolItemForAddMenu(ToolItem toolItem, SelectionEvent selectionEvent) {
        if (selectionEvent.detail == SWT.ARROW && toolItem.getData() instanceof Menu) {
            Rectangle rect = toolItem.getBounds();
            Point pt = toolItem.getParent().toDisplay(new Point(rect.x, rect.y));
            Menu menu = (Menu) toolItem.getData();
            menu.setLocation(pt.x, pt.y + rect.height);
            menu.setVisible(true);
        } else {
            getTreeTableInput().addNewDefaultBuiltInKeyword(NodeAddType.Add);
        }
    }

    public void performMenuItemSelected(MenuItem menuItem) {
        treeViewer.applyEditorValue();
        NodeAddType addType = NodeAddType.Add;
        Object value = menuItem.getData(TreeTableMenuItemConstants.MENU_ITEM_ACTION_KEY);
        if (value instanceof AddAction) {
            switch ((AddAction) value) {
                case Add:
                    addType = NodeAddType.Add;
                    break;
                case InsertAfter:
                    addType = NodeAddType.InserAfter;
                    break;
                case InsertBefore:
                    addType = NodeAddType.InserBefore;
                    break;
            }
        }
        switch (menuItem.getID()) {
            case TreeTableMenuItemConstants.CHANGE_FAILURE_HANDLING_MENU_ITEM_ID:
                Object failureHandlingValue = menuItem.getData(TreeTableMenuItemConstants.FAILURE_HANDLING_KEY);
                if (failureHandlingValue instanceof FailureHandling) {
                    getTreeTableInput().changeFailureHandling((FailureHandling) failureHandlingValue);
                }
                break;
            case TreeTableMenuItemConstants.COPY_MENU_ITEM_ID:
                getTreeTableInput().copy(getTreeTableInput().getSelectedNodes());
                break;
            case TreeTableMenuItemConstants.CUT_MENU_ITEM_ID:
                getTreeTableInput().cut(getTreeTableInput().getSelectedNodes());
                break;
            case TreeTableMenuItemConstants.PASTE_MENU_ITEM_ID:
                getTreeTableInput().paste(getTreeTableInput().getSelectedNode(), addType);
                break;
            case TreeTableMenuItemConstants.REMOVE_MENU_ITEM_ID:
                getTreeTableInput().removeSelectedRows();
                break;
            case TreeTableMenuItemConstants.ENABLE_MENU_ITEM_ID:
                getTreeTableInput().enable();
                break;
            case TreeTableMenuItemConstants.DISABLE_MENU_ITEM_ID:
                getTreeTableInput().disable();
                break;
            default:
                getTreeTableInput().addNewAstObject(menuItem.getID(), getTreeTableInput().getSelectedNode(), addType);
                break;
        }
    }

    private void createTreeTableMenu() {
        final Tree tree = treeViewer.getTree();
        tree.addListener(SWT.MenuDetect, new Listener() {

            private SelectionListener selectionListener = new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (!(e.widget instanceof MenuItem)) {
                        return;
                    }
                    MenuItem item = (MenuItem) e.widget;
                    switch (item.getID()) {
                        case TreeTableMenuItemConstants.COPY_MENU_ITEM_ID:
                            copyTestStep();
                            break;
                        case TreeTableMenuItemConstants.CUT_MENU_ITEM_ID:
                            cutTestStep();
                            break;
                        case TreeTableMenuItemConstants.PASTE_MENU_ITEM_ID:
                            pasteTestStep();
                            break;
                        case TreeTableMenuItemConstants.REMOVE_MENU_ITEM_ID:
                            removeTestStep();
                            break;
                        case TreeTableMenuItemConstants.ENABLE_MENU_ITEM_ID:
                            getTreeTableInput().enable();
                            break;
                        case TreeTableMenuItemConstants.DISABLE_MENU_ITEM_ID:
                            getTreeTableInput().disable();
                            break;
                    }
                }
            };

            private String createMenuItemLabel(String text, String keyCombination) {
                return text + "\t" + keyCombination; //$NON-NLS-1$
            }

            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                Menu menu = tree.getMenu();
                if (menu != null) {
                    menu.dispose();
                }

                boolean hasSelection = tree.getSelectionCount() > 0;
                menu = new Menu(tree);

                MenuItem removeMenuItem = new MenuItem(menu, SWT.PUSH);
                removeMenuItem.setText(createMenuItemLabel(GlobalStringConstants.DELETE,
                        KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.DEL_NAME })));
                removeMenuItem.addSelectionListener(selectionListener);
                removeMenuItem.setID(TreeTableMenuItemConstants.REMOVE_MENU_ITEM_ID);
                removeMenuItem.setEnabled(hasSelection);

                MenuItem copyMenuItem = new MenuItem(menu, SWT.PUSH);
                copyMenuItem.setText(createMenuItemLabel(GlobalStringConstants.COPY,
                        KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, "C" }))); //$NON-NLS-1$
                copyMenuItem.addSelectionListener(selectionListener);
                copyMenuItem.setID(TreeTableMenuItemConstants.COPY_MENU_ITEM_ID);
                copyMenuItem.setEnabled(hasSelection);

                MenuItem cutMenuItem = new MenuItem(menu, SWT.PUSH);
                cutMenuItem.setText(createMenuItemLabel(GlobalStringConstants.CUT,
                        KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, "X" }))); //$NON-NLS-1$
                cutMenuItem.addSelectionListener(selectionListener);
                cutMenuItem.setID(TreeTableMenuItemConstants.CUT_MENU_ITEM_ID);
                cutMenuItem.setEnabled(hasSelection);

                MenuItem pasteMenuItem = new MenuItem(menu, SWT.PUSH);
                pasteMenuItem.setText(createMenuItemLabel(GlobalStringConstants.PASTE,
                        KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, "V" }))); //$NON-NLS-1$
                pasteMenuItem.addSelectionListener(selectionListener);
                pasteMenuItem.setID(TreeTableMenuItemConstants.PASTE_MENU_ITEM_ID);
                pasteMenuItem.setEnabled(getTreeTableInput().canPaste());

                tree.setMenu(menu);

                // addFailureHandlingSubMenu(menu);
                // ;
                new MenuItem(menu, SWT.SEPARATOR);

                MenuItem disableMenuItem = new MenuItem(menu, SWT.PUSH);
                disableMenuItem.setText(createMenuItemLabel(StringConstants.ADAP_MENU_CONTEXT_DISABLE,
                        KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, "/" }))); //$NON-NLS-1$
                disableMenuItem.addSelectionListener(selectionListener);
                disableMenuItem.setID(TreeTableMenuItemConstants.DISABLE_MENU_ITEM_ID);
                disableMenuItem.setEnabled(hasSelection);

                MenuItem enableMenuItem = new MenuItem(menu, SWT.PUSH);
                enableMenuItem.setText(createMenuItemLabel(StringConstants.ADAP_MENU_CONTEXT_ENABLE,
                        KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.ALT_NAME, IKeyLookup.M1_NAME, "/" }))); //$NON-NLS-1$
                enableMenuItem.addSelectionListener(selectionListener);
                enableMenuItem.setID(TreeTableMenuItemConstants.ENABLE_MENU_ITEM_ID);
                enableMenuItem.setEnabled(hasSelection);
            }
        });

        tree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Delete
                if (e.keyCode == SWT.DEL) {
                    removeTestStep();
                    return;
                }

                if (((e.stateMask & SWT.MOD1) == SWT.MOD1)) {
                    // Copy
                    if (e.keyCode == 'c') {
                        copyTestStep();
                        return;
                    }

                    // Cut
                    if (e.keyCode == 'x') {
                        cutTestStep();
                        return;
                    }

                    // Paste
                    if (e.keyCode == 'v') {
                        pasteTestStep();
                        return;
                    }

                    // Enable
                    if (e.keyCode == '/' && ((e.stateMask & SWT.ALT) == SWT.ALT)) {
                        treeTableInput.enable();
                        return;
                    }

                    // Disable
                    if (e.keyCode == '/') {
                        treeTableInput.disable();
                        return;
                    }
                }
            }
        });
    }

    public void removeTestStep() {
        boolean hasSelection = !treeViewer.getStructuredSelection().isEmpty();
        if (hasSelection) {
            treeTableInput.removeSelectedRows();
        }
    }

    public void copyTestStep() {
        boolean hasSelection = !treeViewer.getStructuredSelection().isEmpty();
        if (hasSelection) {
            treeTableInput.copy(treeTableInput.getSelectedNodes());
        }
    }

    public void cutTestStep() {
        boolean hasSelection = !treeViewer.getStructuredSelection().isEmpty();
        if (hasSelection) {
            treeTableInput.cut(treeTableInput.getSelectedNodes());
        }
    }

    public void pasteTestStep() {
        if (treeTableInput.canPaste()) {
            treeTableInput.paste(treeTableInput.getSelectedNode(), NodeAddType.Add);
        }
    }

    public void addNode(MobileActionMapping newAction) throws ClassNotFoundException, InvocationTargetException, InterruptedException {
        refreshTree();
        CapturedMobileElement targetElement = newAction.getTargetElement();
        CapturedMobileElementConverter converter = new CapturedMobileElementConverter();
        ExpressionStatementWrapper wrapper = (ExpressionStatementWrapper) MobileActionUtil
                .generateMobileTestStep(newAction, converter.convert(targetElement), treeTableInput.getMainClassNode());
        treeTableInput.addNewAstObject(wrapper, null, NodeAddType.Add);
        treeViewer.refresh();
        treeViewer.setSelection(new StructuredSelection(getLatestNode()));
    }

    public AstBuiltInKeywordTreeTableNode getLatestNode() {
        TreeItem[] items = treeViewer.getTree().getItems();
        if (items == null || items.length == 0) {
            return null;
        }
        TreeItem latestItem = items[items.length - 1];
        if (latestItem.getData() instanceof AstBuiltInKeywordTreeTableNode) {
            return (AstBuiltInKeywordTreeTableNode) latestItem.getData();
        }
        return null;
    }

    public AstBuiltInKeywordTreeTableNode getNodeItem(int offset) {
        TreeItem[] items = treeViewer.getTree().getItems();
        if (items == null || items.length == 0) {
            return null;
        }
        TreeItem item = items[offset];
        if (item.getData() instanceof AstBuiltInKeywordTreeTableNode) {
            return (AstBuiltInKeywordTreeTableNode) item.getData();
        }
        return null;
    }

    public List<AstTreeTableNode> getNodes() {
        TreeItem[] items = treeViewer.getTree().getItems();
        if (items == null || items.length == 0) {
            return Collections.emptyList();
        }

        List<AstTreeTableNode> nodes = new ArrayList<>();
        for (TreeItem i : items) {
            if (i.getData() instanceof AstTreeTableNode) {
                nodes.add((AstTreeTableNode) i.getData());
            }
        }

        return nodes;
    }

    public void addSimpleKeyword(String keywordName, boolean hasParam) {
        String webUiKwAliasName = MobileActionUtil.getMobileKeywordClass().getAliasName();
        MethodCallExpressionWrapper methodCallExpressionWrapper = new MethodCallExpressionWrapper(webUiKwAliasName,
                keywordName, treeTableInput.getMainClassNode());
        if (hasParam) {
            ArgumentListExpressionWrapper arguments = methodCallExpressionWrapper.getArguments();
            arguments.addExpression(new ConstantExpressionWrapper(""));
        }
        ExpressionStatementWrapper openBrowserStmt = new ExpressionStatementWrapper(methodCallExpressionWrapper);
        treeTableInput.addNewAstObject(openBrowserStmt, null, NodeAddType.Add);
        treeViewer.refresh();
        treeViewer.setSelection(new StructuredSelection(getLatestNode()));
    }

    public void refreshTree() throws InvocationTargetException, InterruptedException {
        treeTableInput.reloadTreeTableNodes();
    }

    @Override
    public void setDirty(boolean isDirty) {
        treeTableInput.reloadTestCaseVariables(getVariables());
    }

    @Override
    public void addVariables(VariableEntity[] variables) {
        variableView.addVariable(variables);
    }

    @Override
    public VariableEntity[] getVariables() {
        return new VariableEntity[0];
    }

    @Override
    public void deleteVariables(List<VariableEntity> variableList) {
        // TODO Auto-generated method stub

    }

    @Override
    public TestCaseEntity getTestCase() {
        return new TestCaseEntity();
    }

    @Override
    public TestCaseTreeTableInput getTreeTableInput() {
        return treeTableInput;
    }

    @Override
    public List<AstTreeTableNode> getDragNodes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void createDynamicGotoMenu(Menu menu) {
        // TODO Auto-generated method stub

    }
}
