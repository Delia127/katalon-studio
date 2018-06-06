package com.kms.katalon.composer.webui.recorder.dialog;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.control.CTreeViewer;
import com.kms.katalon.composer.components.util.ColumnViewerUtil;
import com.kms.katalon.composer.testcase.ast.treetable.AstBuiltInKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput.NodeAddType;
import com.kms.katalon.composer.testcase.parts.ITestCasePart;
import com.kms.katalon.composer.testcase.providers.AstTreeItemLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstTreeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstTreeTableContentProvider;
import com.kms.katalon.composer.testcase.support.InputColumnEditingSupport;
import com.kms.katalon.composer.testcase.support.ItemColumnEditingSupport;
import com.kms.katalon.composer.testcase.support.OutputColumnEditingSupport;
import com.kms.katalon.composer.webui.recorder.action.HTMLAction;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionMapping;
import com.kms.katalon.composer.webui.recorder.ast.RecordedElementMethodCallWrapper;
import com.kms.katalon.composer.webui.recorder.dialog.provider.CapturedElementEditingSupport;
import com.kms.katalon.composer.webui.recorder.util.HTMLActionUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.objectspy.dialog.CapturedObjectsView;
import com.kms.katalon.objectspy.dialog.ObjectSpyEvent;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.util.listener.EventListener;

public class RecordedStepsView implements ITestCasePart, EventListener<ObjectSpyEvent> {

    private TestCaseTreeTableInput treeTableInput;

    private ScriptNodeWrapper wrapper;

    private CTreeViewer treeViewer;

    private CapturedObjectsView capturedObjectsView;

    private String windowId;

    public CTreeViewer getTreeTable() {
        return treeViewer;
    }

    public RecordedStepsView() {
        wrapper = new ScriptNodeWrapper();
        wrapper.addDefaultImports();
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
        childTableTree.setLinesVisible(true);
        childTableTree.setHeaderVisible(true);

        TreeColumnLayout treeColumnLayout = new TreeColumnLayout();
        compositeTable.setLayout(treeColumnLayout);

        addTreeTableColumn(treeViewer, treeColumnLayout, StringConstants.PA_COL_ITEM, 150, 30,
                new AstTreeItemLabelProvider(), new ItemColumnEditingSupport(treeViewer, this));
        addTreeTableColumn(treeViewer, treeColumnLayout,
                StringConstants.PA_COL_OBJ, 150, 30, new AstTreeLabelProvider(),
                new CapturedElementEditingSupport(treeViewer, this));
        addTreeTableColumn(treeViewer, treeColumnLayout, StringConstants.PA_COL_INPUT, 150, 30,
                new AstTreeLabelProvider(), new InputColumnEditingSupport(treeViewer, this));
        addTreeTableColumn(treeViewer, treeColumnLayout, StringConstants.PA_COL_OUTPUT, 80, 8,
                new AstTreeLabelProvider(), new OutputColumnEditingSupport(treeViewer, this));

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
        return null;
    }

    @Override
    public void setDirty(boolean isDirty) {
        // do nothing
    }

    @Override
    public void addVariables(VariableEntity[] variables) {
        // TODO Auto-generated method stub

    }

    @Override
    public VariableEntity[] getVariables() {
        // TODO Auto-generated method stub
        return null;
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
                && HTMLAction.SetText.getName().equals(newActionName);
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

    @Override
    public void handleEvent(ObjectSpyEvent event, Object object) {
        switch (event) {
            case ELEMENT_NAME_CHANGED:
                refreshTree();
                break;
            case SELENIUM_SESSION_STARTED:
            case ADDON_SESSION_STARTED:
                //addOpenBrowserKeyword();
                //reset window ID
                windowId = "";
                break;
            default:
                break;
        }
    }
}
