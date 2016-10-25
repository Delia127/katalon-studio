package com.kms.katalon.composer.testcase.parts;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MGenericTile;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.part.IComposerPart;
import com.kms.katalon.composer.parts.CPart;
import com.kms.katalon.composer.testcase.ast.treetable.AstBuiltInKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCallTestCaseKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput.NodeAddType;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;

public class TestCasePart extends CPart implements IComposerPart, EventHandler, ITestCasePart {

    private TestStepManualComposite testStepManualComposite;

    private MPart mPart;

    public MPart getMPart() {
        return mPart;
    }

    @Inject
    private IEventBroker eventBroker;
    
    @Inject
    private EPartService partService;

    private TestCaseCompositePart parentTestCaseCompositePart;

    @PostConstruct
    public void init(Composite parent, MPart mpart) {
        this.mPart = mpart;

        if (mpart.getParent().getParent() instanceof MGenericTile
                && ((MGenericTile<?>) mpart.getParent().getParent()) instanceof MCompositePart) {
            MCompositePart compositePart = (MCompositePart) (MGenericTile<?>) mpart.getParent().getParent();
            if (compositePart.getObject() instanceof TestCaseCompositePart) {
                parentTestCaseCompositePart = ((TestCaseCompositePart) compositePart.getObject());
            }
        }
        initialize(mpart, partService);

        registerEventBrokerListeners();
        createControls(parent);
    }

    @Focus
    public void setFocus() {
        testStepManualComposite.setFocus();
    }

    @PreDestroy
    public void preDestroy() {
        eventBroker.unsubscribe(this);
        setDirty(false);
    }

    private void registerEventBrokerListeners() {
        eventBroker.subscribe(EventConstants.TESTCASE_SETTINGS_FAILURE_HANDLING_UPDATED, this);
    }

    private void createControls(Composite parent) {
        parent.setLayout(new FillLayout(SWT.HORIZONTAL));
        testStepManualComposite = new TestStepManualComposite(this, parent);
    }

    public void setDirty(boolean isDirty) {
        if (mPart != null) {
            mPart.setDirty(isDirty);
        }
        parentTestCaseCompositePart.checkDirty();
    }

    public boolean isManualScriptChanged() {
        if (getTreeTableInput() != null) {
            return getTreeTableInput().isChanged();
        }
        return false;
    }

    public void setManualScriptChanged(boolean change) {
        if (getTreeTableInput() != null) {
            getTreeTableInput().setChanged(change);
        }
    }

    @Override
    public void handleEvent(Event event) {
        switch (event.getTopic()) {
            case EventConstants.TESTCASE_SETTINGS_FAILURE_HANDLING_UPDATED:
                try {
                    if (getTreeTableInput() != null) {
                        loadASTNodesToTreeTable(getTreeTableInput().getMainClassNode());
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }
                break;
            default:
                break;
        }
    }

    @Persist
    public boolean doSave() {
        try {
            parentTestCaseCompositePart.save();
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    public void addStatements(List<StatementWrapper> statements, NodeAddType addType) {
        getTreeTableInput().addNewAstObjects(statements,
                getTreeTableInput().getSelectedNode(), addType);
    }

    public TestCaseEntity getTestCase() {
        return parentTestCaseCompositePart.getTestCase();
    }

    public TestCaseTreeTableInput getTreeTableInput() {
        return testStepManualComposite.getTreeTableInput();
    }

    public void addVariables(VariableEntity[] variables) {
        parentTestCaseCompositePart.addVariables(variables);
    }
    
    public void deleteVariables(List<VariableEntity> variables) {
        parentTestCaseCompositePart.deleteVariables(variables);
    }

    public VariableEntity[] getVariables() {
        return parentTestCaseCompositePart.getVariables();
    }

    @Override
    public String getEntityId() {
        return getTestCase().getIdForDisplay();
    }

    public void addDefaultImports() {
        getTreeTableInput().addDefaultImports();
    }

    public TreeViewer getTestCaseTreeTable() {
        return testStepManualComposite.getTreeTable();
    }

    public List<AstTreeTableNode> getDragNodes() {
        return testStepManualComposite.getDragNodes();
    }

    public void loadASTNodesToTreeTable(ScriptNodeWrapper scriptNode) throws Exception {
        testStepManualComposite.loadASTNodesToTreeTable(scriptNode);
    }
    
    public boolean isTestCaseEmpty() {
        for (TreeItem item : getTestCaseTreeTable().getTree().getItems()) {
            if (item.getText().matches("\\d+.*")) {
                return false;
            }
        }
        return true;
    }
    
    public void createDynamicGotoMenu(Menu menu) {
        IStructuredSelection selection = (IStructuredSelection) getTestCaseTreeTable().getSelection();
        if (selection.size() == 0) {
            return;
        }
        MenuItem openMenuItem = new MenuItem(menu, SWT.CASCADE);
        openMenuItem.setText(ComposerTestcaseMessageConstants.MENU_OPEN);
        Menu subMenu = new Menu(openMenuItem);
        for (Object object : selection.toList()) {
            if (object instanceof AstCallTestCaseKeywordTreeTableNode) {
                createGotoTestCaseMenuItem((AstCallTestCaseKeywordTreeTableNode) object, subMenu);
            } else if (object instanceof AstBuiltInKeywordTreeTableNode) {
                createGotoTestObjectMenuItem((AstBuiltInKeywordTreeTableNode) object, subMenu);
            }
        }
        if (subMenu.getItemCount() == 0) {
            openMenuItem.setEnabled(false);
            return;
        }
        openMenuItem.setMenu(subMenu);
    }

    private void createGotoTestCaseMenuItem(AstCallTestCaseKeywordTreeTableNode node, Menu subMenu) {
        Object testObject = node.getTestObject();
        if (!(testObject instanceof TestCaseEntity)) {
            return;
        }
        TestCaseEntity testCaseEntity = (TestCaseEntity) testObject;
        MenuItem menuItem = new MenuItem(subMenu, SWT.PUSH);
        menuItem.setText(testCaseEntity.getIdForDisplay());
        menuItem.setData(testCaseEntity);
        menuItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Object object = e.getSource();
                if (!(object instanceof MenuItem)) {
                    return;
                }
                TestCaseEntity testCaseEntity = getTestCaseFromMenuItem((MenuItem) object);
                if (testCaseEntity != null) {
                    eventBroker.send(EventConstants.TESTCASE_OPEN, testCaseEntity);
                }
            }
        });
    }

    private void createGotoTestObjectMenuItem(AstBuiltInKeywordTreeTableNode node, Menu subMenu) {
        WebElementEntity testObject = getTestObjectFromMethod(node);
        if (testObject == null) {
            return;
        }
        MenuItem menuItem = new MenuItem(subMenu, SWT.PUSH);
        menuItem.setText(testObject.getIdForDisplay());
        menuItem.setData(testObject);
        menuItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Object object = e.getSource();
                if (!(object instanceof MenuItem)) {
                    return;
                }
                WebElementEntity webElementEntity = getWebElementFromMenuItem((MenuItem) object);
                if (webElementEntity != null) {
                    eventBroker.send(EventConstants.TEST_OBJECT_OPEN, webElementEntity);
                }
            }
        });
    }

    private WebElementEntity getTestObjectFromMethod(AstBuiltInKeywordTreeTableNode node) {
        Object findTestObjectMethodCall = node.getTestObject();
        if (!(findTestObjectMethodCall instanceof MethodCallExpressionWrapper)) {
            return null;
        }
        String testObjectId = AstEntityInputUtil.findTestObjectIdFromFindTestObjectMethodCall((MethodCallExpressionWrapper) findTestObjectMethodCall);
        if (testObjectId == null) {
            return null;
        }
        try {
            return ObjectRepositoryController.getInstance().getWebElementByDisplayPk(testObjectId);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    private WebElementEntity getWebElementFromMenuItem(MenuItem menuItem) {
        WebElementEntity webElementEntity = null;
        if (menuItem.getData() instanceof WebElementEntity) {
            webElementEntity = (WebElementEntity) menuItem.getData();
        }
        return webElementEntity;
    }

    private TestCaseEntity getTestCaseFromMenuItem(MenuItem menuItem) {
        TestCaseEntity testCaseEntity = null;
        if (menuItem.getData() instanceof TestCaseEntity) {
            testCaseEntity = (TestCaseEntity) menuItem.getData();
        }
        return testCaseEntity;
    }
}
