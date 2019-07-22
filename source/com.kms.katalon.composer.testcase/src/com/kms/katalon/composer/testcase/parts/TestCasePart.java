package com.kms.katalon.composer.testcase.parts;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
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

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.EventUtil;
import com.kms.katalon.composer.components.impl.util.MenuUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.parts.CPart;
import com.kms.katalon.composer.testcase.ast.treetable.AstBuiltInKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCallTestCaseKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput.NodeAddType;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.file.FileEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;

public class TestCasePart extends CPart implements EventHandler, ITestCasePart {

    private TestStepManualComposite testStepManualComposite;

    private MPart mPart;

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EPartService partService;

    private TestCaseCompositePart parentTestCaseCompositePart;
    
    private boolean scriptLoaded = false;

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

    public MPart getMPart() {
        return mPart;
    }

    @Override
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
        eventBroker.subscribe(EventConstants.TESTCASE_ADD_STEP, this);
        eventBroker.subscribe(EventConstants.TESTCASE_ADD_STEP_CALL_TESTCASE, this);
    }

    private void createControls(Composite parent) {
        parent.setLayout(new FillLayout(SWT.HORIZONTAL));
        testStepManualComposite = new TestStepManualComposite(this, parent, parentTestCaseCompositePart);
    }

    @Override
    public void setDirty(boolean isDirty) {
        if (mPart != null) {
            mPart.setDirty(isDirty);
        }
        parentTestCaseCompositePart.updateDirty();
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
            case EventConstants.TESTCASE_SETTINGS_FAILURE_HANDLING_UPDATED: {
                try {
                    if (getTreeTableInput() != null) {
                        loadASTNodesToTreeTable(getTreeTableInput().getMainClassNode());
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }
                return;
            }

            case EventConstants.TESTCASE_ADD_STEP: {
                Object eventData = EventUtil.getData(event);
                if (isInvalidTargetPart(eventData)) {
                    return;
                }

                switchToManualView();

                Object actionId = ((Object[]) eventData)[1];
                testStepManualComposite.addStepByActionID((int) actionId);
                return;
            }

            case EventConstants.TESTCASE_ADD_STEP_CALL_TESTCASE: {
                Object eventData = EventUtil.getData(event);
                if (isInvalidTargetPart(eventData)) {
                    return;
                }

                Object testcase = ((Object[]) eventData)[1];
                if (!(testcase instanceof TestCaseEntity)) {
                    return;
                }

                switchToManualView();

                testStepManualComposite.addCallTestCaseStep((TestCaseEntity) testcase);
                return;
            }

            default:
                return;
        }
    }

    private void switchToManualView() {
        if (mPart.equals(parentTestCaseCompositePart.getSelectedPart())) {
            return;
        }
        parentTestCaseCompositePart.setSelectedPart(mPart);
    }

    private boolean isInvalidTargetPart(Object eventData) {
        return !eventData.getClass().isArray() || ((Object[]) eventData).length != 2
                || !mPart.getElementId().startsWith((String) ((Object[]) eventData)[0]);
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

    public void clearStatements() {
        getTreeTableInput().getMainClassNode().getRunMethod().getBlock().clearStaments();
        try {
            getTreeTableInput().reloadTreeTableNodes();
        } catch (InvocationTargetException | InterruptedException e) {
            LoggerSingleton.logError(e);
        }
    }

    public void addStatements(List<StatementWrapper> statements, NodeAddType addType) {
        getTreeTableInput().addNewAstObjects(statements, getTreeTableInput().getSelectedNode(), addType);
    }
    
    public void addStatements(List<StatementWrapper> statements, NodeAddType addType, boolean commitEditting) {
        getTreeTableInput().addNewAstObjects(statements, getTreeTableInput().getSelectedNode(), addType);
        if (commitEditting) {
            getTestCaseTreeTable().applyEditorValue();
        }
    }


    @Override
    public TestCaseEntity getTestCase() {
        return parentTestCaseCompositePart.getTestCase();
    }

    @Override
    public TestCaseTreeTableInput getTreeTableInput() {
        return testStepManualComposite.getTreeTableInput();
    }

    @Override
    public void addVariables(VariableEntity[] variables) {
        parentTestCaseCompositePart.addVariables(variables);
    }

    @Override
    public void deleteVariables(List<VariableEntity> variables) {
        parentTestCaseCompositePart.deleteVariables(variables);
    }

    @Override
    public VariableEntity[] getVariables() {
        return parentTestCaseCompositePart.getVariables();
    }

    public void addDefaultImports() {
        getTreeTableInput().addDefaultImports();
    }

    public TreeViewer getTestCaseTreeTable() {
        return testStepManualComposite.getTreeTable();
    }

    @Override
    public List<AstTreeTableNode> getDragNodes() {
        return testStepManualComposite.getDragNodes();
    }

    public void loadASTNodesToTreeTable(ScriptNodeWrapper scriptNode) {
        scriptLoaded = true;
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

    public List<String> getCommentSteps() {
        return Arrays.asList(getTestCaseTreeTable().getTree().getItems())
                .stream()
                .filter(item -> item.getText(0).contains(StringConstants.COMMENT))
                .map(item -> getCommentText(item.getText(2)))
                .collect(Collectors.toList());
    }

    private String getCommentText(String comment) {
        String doubleQuote = "\"";
        comment = StringUtils.removeStart(comment, doubleQuote);
        comment = StringUtils.removeEnd(comment, doubleQuote);
        return comment;
    }

    @Override
    public void createDynamicGotoMenu(Menu menu) {
        if (menu == null) {
            return;
        }
        ControlUtils.removeOldOpenMenuItem(menu);
        IStructuredSelection selection = testStepManualComposite.getTreeTableSelection();
        if (selection.size() == 0) {
            return;
        }
        List<FileEntity> testObjects = getListTestObjectFromSelection(selection);
        if (testObjects.size() == 0) {
            return;
        }
        SelectionAdapter openTestCase = new SelectionAdapter() {

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
        };
        SelectionAdapter openTestObject = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Object object = e.getSource();
                if (!(object instanceof MenuItem)) {
                    return;
                }
                WebElementEntity webElementEntity = getWebElementFromMenuItem((MenuItem) object);
                if (webElementEntity == null) {
                    return;
                }
                String openEventName = webElementEntity instanceof WebServiceRequestEntity
                        ? EventConstants.WEBSERVICE_REQUEST_OBJECT_OPEN : EventConstants.TEST_OBJECT_OPEN;
                eventBroker.send(openEventName, webElementEntity);
            }
        };

        new MenuItem(menu, SWT.SEPARATOR);
        if (testObjects.size() == 1) {
            handleWhenSelectOnlyOne(menu, testObjects.get(0), openTestCase, openTestObject);
            return;
        }
        MenuUtils.createOpenTestArtifactsMenu(
                getMapFileEntityToSelectionAdapter(testObjects, openTestCase, openTestObject), menu);
    }

    private void handleWhenSelectOnlyOne(Menu menu, FileEntity entity, SelectionAdapter openTestCaseAdapter,
            SelectionAdapter openTestObjectAdapter) {
        String name = ComposerTestcaseMessageConstants.MENU_OPEN + " " + entity.getName();
        if (entity instanceof TestCaseEntity) {
            ControlUtils.createSubMenuOpen(menu, entity, openTestCaseAdapter, name);
        } else if (entity instanceof WebElementEntity) {
            ControlUtils.createSubMenuOpen(menu, entity, openTestObjectAdapter, name);
        }
    }

    private WebElementEntity getTestObjectFromMethod(AstBuiltInKeywordTreeTableNode node) {
        Object findTestObjectMethodCall = node.getTestObject();
        if (!(findTestObjectMethodCall instanceof MethodCallExpressionWrapper)) {
            return null;
        }
        String testObjectId = AstEntityInputUtil
                .findTestObjectIdFromFindTestObjectMethodCall((MethodCallExpressionWrapper) findTestObjectMethodCall);
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

    private List<FileEntity> getListTestObjectFromSelection(IStructuredSelection selection) {
        List<FileEntity> testObjects = new ArrayList<FileEntity>();
        for (Object object : selection.toList()) {
            if (object instanceof AstCallTestCaseKeywordTreeTableNode) {
                TestCaseEntity testObject = ((AstCallTestCaseKeywordTreeTableNode) object).getTestObject();
                if (testObject != null && !testObjects.contains(testObject)) {
                    testObjects.add(testObject);
                }

            } else if (object instanceof AstBuiltInKeywordTreeTableNode) {
                WebElementEntity testObject = getTestObjectFromMethod((AstBuiltInKeywordTreeTableNode) object);
                if (testObject != null && !testObjects.contains(testObject)) {
                    testObjects.add(testObject);
                }
            }
        }
        return testObjects;
    }

    private HashMap<FileEntity, SelectionAdapter> getMapFileEntityToSelectionAdapter(
            List<? extends FileEntity> fileEntities, SelectionAdapter openTestCase, SelectionAdapter openTestObject) {
        HashMap<FileEntity, SelectionAdapter> map = new HashMap<>();
        for (FileEntity fileEntity : fileEntities) {
            if (fileEntity instanceof TestCaseEntity) {
                map.put(fileEntity, openTestCase);
            } else if (fileEntity instanceof WebElementEntity) {
                map.put(fileEntity, openTestObject);
            }
        }
        return map;
    }

    public boolean isScriptLoaded() {
        return scriptLoaded;
    }
}
