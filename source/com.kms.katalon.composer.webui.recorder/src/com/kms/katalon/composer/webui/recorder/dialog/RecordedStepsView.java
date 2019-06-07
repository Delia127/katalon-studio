package com.kms.katalon.composer.webui.recorder.dialog;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.bindings.keys.IKeyLookup;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.openqa.selenium.Keys;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.control.CTreeViewer;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.KeyEventUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColumnViewerUtil;
import com.kms.katalon.composer.testcase.ast.treetable.AstBuiltInKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.components.KeywordTreeViewerToolTipSupport;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
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
import com.kms.katalon.composer.webui.recorder.action.HTMLAction;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionMapping;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionParamValueType;
import com.kms.katalon.composer.webui.recorder.ast.RecordedElementMethodCallWrapper;
import com.kms.katalon.composer.webui.recorder.constants.ComposerWebuiRecorderMessageConstants;
import com.kms.katalon.composer.webui.recorder.dialog.provider.CapturedElementEditingSupport;
import com.kms.katalon.composer.webui.recorder.util.HTMLActionUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.objectspy.dialog.CapturedObjectsView;
import com.kms.katalon.objectspy.dialog.ObjectSpyEvent;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.util.CryptoUtil;
import com.kms.katalon.util.listener.EventListener;

public class RecordedStepsView implements ITestCasePart, EventListener<ObjectSpyEvent> {

    private IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();

    private TestCaseTreeTableInput treeTableInput;

    private ScriptNodeWrapper wrapper;

    private CTreeViewer treeViewer;

    private CapturedObjectsView capturedObjectsView;

    private String windowId;

    private TestCaseVariableView variableView;

    public CTreeViewer getTreeTable() {
        return treeViewer;
    }

    public RecordedStepsView() {
        wrapper = new ScriptNodeWrapper();
        wrapper.addDefaultImports();
        wrapper.addImport(Keys.class);
    }

    public void setCapturedObjectsView(CapturedObjectsView capturedObjectsView) {
        this.capturedObjectsView = capturedObjectsView;
    }

    public CapturedObjectsView getCapturedObjectsView() {
        return capturedObjectsView;
    }

    public Composite createContent(Composite parent) {
        Composite compositeTable = new Composite(parent, SWT.NONE);
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        treeViewer = new CTreeViewer(compositeTable, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        Tree childTableTree = treeViewer.getTree();
        childTableTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        childTableTree.setLinesVisible(ControlUtils.shouldLineVisble(childTableTree.getDisplay()));
        childTableTree.setHeaderVisible(true);

        TreeColumnLayout treeColumnLayout = new TreeColumnLayout();
        compositeTable.setLayout(treeColumnLayout);

        addTreeTableColumn(treeViewer, treeColumnLayout, StringConstants.PA_COL_ITEM, 200, 0,
                new AstTreeItemLabelProvider(), new ItemColumnEditingSupport(treeViewer, this));
        addTreeTableColumn(treeViewer, treeColumnLayout, StringConstants.PA_COL_OBJ, 150, 0, new AstTreeLabelProvider(),
                new CapturedElementEditingSupport(treeViewer, this));
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

        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                AstTreeTableNode node = (AstTreeTableNode) treeViewer.getStructuredSelection().getFirstElement();
                if (node instanceof AstBuiltInKeywordTreeTableNode) {
                    AstBuiltInKeywordTreeTableNode kwNode = (AstBuiltInKeywordTreeTableNode) node;
                    Object testObjectMethodCall = kwNode.getTestObject();
                    if (testObjectMethodCall instanceof RecordedElementMethodCallWrapper) {
                        RecordedElementMethodCallWrapper methodCallExprs = (RecordedElementMethodCallWrapper) testObjectMethodCall;
                        EventBrokerSingleton.getInstance().getEventBroker().post(
                                EventConstants.RECORDER_ACTION_SELECTED, methodCallExprs.getWebElement());
                    }
                }
            }
        });

        createTreeTableMenu();

        KeywordTreeViewerToolTipSupport.enableFor(treeViewer);

        return compositeTable;
    }

    public void refreshTree() {
        treeViewer.refresh();
    }

    public ScriptNodeWrapper getWrapper() {
        return wrapper;
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

    @Override
    public TestCaseEntity getTestCase() {
        return new TestCaseEntity();
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
        return variableView.getVariables();
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

    @Override
    public void deleteVariables(List<VariableEntity> variableList) {
        // TODO Auto-generated method stub

    }

    public void addNewStep(HTMLActionMapping newAction) throws ClassNotFoundException {
        if (StringUtils.isNotEmpty(windowId) && !windowId.equals(newAction.getWindowId())) {
            addNode(HTMLActionUtil.createNewSwitchToWindowAction(HTMLActionUtil.getPageTitleForAction(newAction)));
        }
        windowId = newAction.getWindowId();
        addNode(newAction);
    }

    private void addNode(HTMLActionMapping newAction) throws ClassNotFoundException {
        AstBuiltInKeywordTreeTableNode latestNode = getLatestNode();
        WebElement targetElement = newAction.getTargetElement();
        if (targetElement != null) {
            WebElementPropertyEntity property = targetElement.getProperty("type");
            if (property != null && "password".equals(property.getValue())) {
                secureSetTextAction(newAction);
            }
        }
        ExpressionStatementWrapper wrapper = (ExpressionStatementWrapper) HTMLActionUtil
                .generateWebUiTestStep(newAction, targetElement, treeTableInput.getMainClassNode());
        if (targetElement != null && latestNode instanceof AstBuiltInKeywordTreeTableNode
                && preventDuplicatedActions(newAction, latestNode, targetElement, wrapper)) {
            return;
        }
        treeTableInput.addNewAstObject(wrapper, null, NodeAddType.Add);
        treeViewer.refresh();
        treeViewer.setSelection(new StructuredSelection(getLatestNode()));
    }

    private void secureSetTextAction(HTMLActionMapping newAction) {
    	if(newAction.getAction() != HTMLAction.SendKeys){
    		 newAction.setAction(HTMLAction.SetEncryptedText);
    	        HTMLActionParamValueType passwordParam = newAction.getData()[0];
    	        ConstantExpressionWrapper stringWrapper = (ConstantExpressionWrapper) passwordParam.getValue();
    	        String password = stringWrapper.getValueAsString();
    	        try {
    	            stringWrapper.setValue(CryptoUtil.encode(CryptoUtil.getDefault(password)));
    	        } catch (UnsupportedEncodingException | GeneralSecurityException e) {
    	            LoggerSingleton.logError(e);
    	        }
    	}       
    }

    private boolean preventDuplicatedActions(HTMLActionMapping newAction, AstBuiltInKeywordTreeTableNode latestNode,
            WebElement targetElement, ExpressionStatementWrapper wrapper) {
        String objectId = latestNode.getTestObjectText();
        String latestKeywordName = latestNode.getKeywordName();
        String newActionName = newAction.getAction().getName();
        if (objectId.equals(targetElement.getName())) {
            if (preventAddMultiSetTextAction(latestKeywordName, newActionName)) {
                modifyStep(wrapper, latestNode);
                return true;
            }
            if (preventAddMultiClickAction(latestKeywordName, newActionName)) {
                AstBuiltInKeywordTreeTableNode twoStepBefore = getNodeItem(treeViewer.getTree().getItemCount() - 2);
                treeTableInput.removeRows(Arrays.asList(latestNode));
                modifyStep(wrapper, twoStepBefore);
                return true;
            }
        }
        return false;
    }

    private void modifyStep(ExpressionStatementWrapper wrapper, AstBuiltInKeywordTreeTableNode twoStepBefore) {
        ExpressionStatementWrapper oldWrapper = (ExpressionStatementWrapper) twoStepBefore.getASTObject();
        oldWrapper.setExpression(wrapper.getExpression());
        treeViewer.refresh();
        treeViewer.setSelection(new StructuredSelection(twoStepBefore));
    }

    private boolean preventAddMultiClickAction(String latestKeywordName, String newActionName) {
        AstBuiltInKeywordTreeTableNode twoStepBefore = getNodeItem(treeViewer.getTree().getItemCount() - 2);
        return HTMLAction.LeftClick.getMappedKeywordMethod().equals(twoStepBefore.getKeywordName())
                && HTMLAction.LeftClick.getMappedKeywordMethod().equals(latestKeywordName)
                && HTMLAction.DoubleClick.getName().equals(newActionName);
    }

    private boolean preventAddMultiSetTextAction(String latestKeywordName, String newActionName) {
        return HTMLAction.SetText.getMappedKeywordMethod().equals(latestKeywordName)
                && HTMLAction.SetText.getName().equals(newActionName)
                || HTMLAction.SetEncryptedText.getMappedKeywordMethod().equals(latestKeywordName)
                        && HTMLAction.SetEncryptedText.getName().equals(newActionName);
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
        String webUiKwAliasName = HTMLActionUtil.getWebUiKeywordClass().getAliasName();
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

    @Override
    public void handleEvent(ObjectSpyEvent event, Object object) {
        switch (event) {
            case ELEMENT_NAME_CHANGED:
                refreshTree();
                break;
            case SELENIUM_SESSION_STARTED:
            case ADDON_SESSION_STARTED:
                // reset window ID
                windowId = "";
                break;
            default:
                break;
        }
    }

    public Composite createVariableTab(Composite parent) {
        variableView = new TestCaseVariableView(this);
        Composite component = variableView.createComponents(parent);
        GridLayout gl = (GridLayout) component.getLayout();
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        return component;
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

    private void runFromFirstSelectedStep() {
        boolean hasSelection = !treeViewer.getStructuredSelection().isEmpty();
        if (hasSelection) {
            eventBroker.post(EventConstants.WEBUI_VERIFICATION_RUN_FROM_STEP_CMD, null);
        }
    }

    private void runSelectedSteps() {
        boolean hasSelection = !treeViewer.getStructuredSelection().isEmpty();
        if (hasSelection) {
            eventBroker.post(EventConstants.WEBUI_VERIFICATION_RUN_SELECTED_STEPS_CMD, null);
        }
    }

    private void runAllSteps() {
        eventBroker.post(EventConstants.WEBUI_VERIFICATION_RUN_ALL_STEPS_CMD, null);
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
                        case TreeTableMenuItemConstants.RUN_FROM_THIS_STEP_ID:
                            runFromFirstSelectedStep();
                            break;
                        case TreeTableMenuItemConstants.RUN_SELECTED_STEPS_ID:
                            runSelectedSteps();
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

                MenuItem runFromThisStepMenuItem = new MenuItem(menu, SWT.PUSH);
                runFromThisStepMenuItem.setText(
                        createMenuItemLabel(ComposerWebuiRecorderMessageConstants.DIA_ITEM_RUN_FROM_HERE, KeyEventUtil
                                .geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, IKeyLookup.SHIFT_NAME, "E" })));
                runFromThisStepMenuItem.addSelectionListener(selectionListener);
                runFromThisStepMenuItem.setID(TreeTableMenuItemConstants.RUN_FROM_THIS_STEP_ID);
                runFromThisStepMenuItem.setEnabled(hasSelection);

                MenuItem runSelectedStepsMenuItem = new MenuItem(menu, SWT.PUSH);
                runSelectedStepsMenuItem.setText(createMenuItemLabel(
                        ComposerWebuiRecorderMessageConstants.DIA_ITEM_RUN_SELECTED_STEPS,
                        KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, IKeyLookup.ALT_NAME, "E" })));
                runSelectedStepsMenuItem.addSelectionListener(selectionListener);
                runSelectedStepsMenuItem.setID(TreeTableMenuItemConstants.RUN_SELECTED_STEPS_ID);
                runSelectedStepsMenuItem.setEnabled(hasSelection);

                new MenuItem(menu, SWT.SEPARATOR);

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

                    // Run selected steps
                    if (e.keyCode == 'e' && ((e.stateMask & SWT.ALT) == SWT.ALT)) {
                        runSelectedSteps();
                        return;
                    }

                    // Run from first selected steps
                    if (e.keyCode == 'e' && ((e.stateMask & SWT.SHIFT) == SWT.SHIFT)) {
                        runFromFirstSelectedStep();
                        return;
                    }

                    if (e.keyCode == 'e') {
                        runAllSteps();
                    }
                }
            }
        });

    }
}
