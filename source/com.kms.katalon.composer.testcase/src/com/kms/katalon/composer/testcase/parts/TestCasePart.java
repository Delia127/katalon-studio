package com.kms.katalon.composer.testcase.parts;

import java.util.ArrayList;
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
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.bindings.keys.IKeyLookup;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.TreeViewerFocusCellManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.control.CTreeViewer;
import com.kms.katalon.composer.components.impl.util.KeyEventUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.part.IComposerPart;
import com.kms.katalon.composer.components.viewer.CustomEditorActivationStrategy;
import com.kms.katalon.composer.components.viewer.FocusCellOwnerDrawHighlighterForMultiSelection;
import com.kms.katalon.composer.explorer.util.TransferTypeCollection;
import com.kms.katalon.composer.testcase.ast.treetable.AstBuiltInKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCallTestCaseKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstMethodTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testcase.components.KeywordTreeViewerToolTipSupport;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants.AddAction;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.keywords.KeywordBrowserTreeEntityTransfer;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput.NodeAddType;
import com.kms.katalon.composer.testcase.providers.AstTreeItemLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstTreeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstTreeTableContentProvider;
import com.kms.katalon.composer.testcase.providers.TestCaseSelectionListener;
import com.kms.katalon.composer.testcase.providers.TestStepTableDropListener;
import com.kms.katalon.composer.testcase.support.DescriptionColumnEditingSupport;
import com.kms.katalon.composer.testcase.support.InputColumnEditingSupport;
import com.kms.katalon.composer.testcase.support.ItemColumnEditingSupport;
import com.kms.katalon.composer.testcase.support.OutputColumnEditingSupport;
import com.kms.katalon.composer.testcase.support.TestObjectEditingSupport;
import com.kms.katalon.composer.testcase.treetable.transfer.ScriptTransfer;
import com.kms.katalon.composer.testcase.treetable.transfer.ScriptTransferData;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.TestCaseMenuUtil;
import com.kms.katalon.composer.testcase.views.FocusCellOwnerDrawForManualTestcase;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;

public class TestCasePart implements IComposerPart, EventHandler {

    private Composite compositeManual;

    private ToolItem tltmRecord, tltmAddStep, tltmInsertStep, tltmRemoveStep, tltmUp, tltmDown;

    TreeViewer treeTable;

    private MPart mPart;

    private TestCaseTreeTableInput treeTableInput;

    public MPart getMPart() {
        return mPart;
    }

    @Inject
    private IEventBroker eventBroker;

    private TestCaseSelectionListener selectionListener;

    private TestCaseCompositePart parentTestCaseCompositePart;

    private List<AstTreeTableNode> dragNodes;

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

        selectionListener = new TestCaseSelectionListener(this);

        registerEventBrokerListeners();
        createControls(parent);
    }

    @Focus
    public void setFocus() {
        compositeManual.setFocus();
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
        createTabManual(parent);
    }

    private void createTabManual(Composite parent) {
        compositeManual = new Composite(parent, SWT.NONE);
        compositeManual.setLayout(new GridLayout(1, false));

        createTestCaseManualToolbar(compositeManual);

        Composite compositeMain = new Composite(compositeManual, SWT.NONE);
        GridLayout glCompositeMain = new GridLayout(1, false);
        glCompositeMain.marginWidth = 0;
        glCompositeMain.marginHeight = 0;
        compositeMain.setLayout(glCompositeMain);
        compositeMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Composite compositeDetails = new Composite(compositeMain, SWT.NONE);
        compositeDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        compositeDetails.setLayout(new FormLayout());

        createTestCaseManualTableControls(compositeDetails);
    }

    private void createTestCaseManualToolbar(Composite parent) {
        Composite compositeToolbar = new Composite(parent, SWT.NONE);
        compositeToolbar.setLayout(new FillLayout(SWT.HORIZONTAL));
        GridData gd_compositeToolbar = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
        gd_compositeToolbar.exclude = true;
        compositeToolbar.setLayoutData(gd_compositeToolbar);

        ToolBar toolBar = new ToolBar(compositeToolbar, SWT.FLAT | SWT.RIGHT);

        tltmRecord = new ToolItem(toolBar, SWT.NONE);
        tltmRecord.setText(StringConstants.PA_TOOLBAR_RECORD);
        tltmRecord.setImage(ImageConstants.IMG_16_RECORD);
        tltmRecord.setToolTipText(StringConstants.PA_TOOLBAR_TIP_RECORD_TEST);
    }

    private void createTestCaseManualTableControls(Composite parent) {
        Composite compositeSteps = new Composite(parent, SWT.NONE);
        compositeSteps.setLayoutData(new FormData());
        GridLayout glCompositeSteps = new GridLayout(1, false);
        glCompositeSteps.marginWidth = 0;
        glCompositeSteps.marginHeight = 0;
        compositeSteps.setLayout(glCompositeSteps);

        FormData fd_compositeSteps = new FormData();
        fd_compositeSteps.right = new FormAttachment(100);
        fd_compositeSteps.bottom = new FormAttachment(100);
        fd_compositeSteps.top = new FormAttachment(0);
        fd_compositeSteps.left = new FormAttachment(0);
        compositeSteps.setLayoutData(fd_compositeSteps);

        Composite compositeTableButtons = new Composite(compositeSteps, SWT.NONE);
        GridLayout glCompositeTableButtons = new GridLayout(4, false);
        glCompositeTableButtons.marginHeight = 0;
        glCompositeTableButtons.marginWidth = 0;
        compositeTableButtons.setLayout(glCompositeTableButtons);
        compositeTableButtons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
        ToolBar toolbar = toolBarManager.createControl(compositeTableButtons);

        tltmAddStep = new ToolItem(toolbar, SWT.DROP_DOWN);
        tltmAddStep.setText(StringConstants.PA_BTN_TIP_ADD);
        tltmAddStep.setImage(ImageConstants.IMG_16_ADD);
        tltmAddStep.addSelectionListener(selectionListener);

        Menu addMenu = new Menu(tltmAddStep.getParent().getShell());
        tltmAddStep.setData(addMenu);
        TestCaseMenuUtil.fillActionMenu(TreeTableMenuItemConstants.AddAction.Add, selectionListener, addMenu);

        tltmInsertStep = new ToolItem(toolbar, SWT.DROP_DOWN);
        tltmInsertStep.setText(StringConstants.PA_BTN_TIP_INSERT);
        tltmInsertStep.setImage(ImageConstants.IMG_16_INSERT);
        tltmInsertStep.addSelectionListener(selectionListener);

        Menu insertMenu = new Menu(tltmInsertStep.getParent().getShell());
        tltmInsertStep.setData(insertMenu);

        // Add step before
        TestCaseMenuUtil.addActionSubMenu(insertMenu, TreeTableMenuItemConstants.AddAction.InsertBefore,
                StringConstants.PA_MENU_SUB_BEFORE, selectionListener);

        // Add step after
        TestCaseMenuUtil.addActionSubMenu(insertMenu, TreeTableMenuItemConstants.AddAction.InsertAfter,
                StringConstants.PA_MENU_SUB_AFTER, selectionListener);

        tltmRemoveStep = new ToolItem(toolbar, SWT.NONE);
        tltmRemoveStep.setText(StringConstants.PA_BTN_TIP_REMOVE);
        tltmRemoveStep.setImage(ImageConstants.IMG_16_REMOVE);
        tltmRemoveStep.addSelectionListener(selectionListener);

        tltmUp = new ToolItem(toolbar, SWT.NONE);
        tltmUp.setText(StringConstants.PA_BTN_TIP_MOVE_UP);
        tltmUp.setImage(ImageConstants.IMG_16_MOVE_UP);
        tltmUp.addSelectionListener(selectionListener);

        tltmDown = new ToolItem(toolbar, SWT.NONE);
        tltmDown.setText(StringConstants.PA_BTN_TIP_MOVE_DOWN);
        tltmDown.setImage(ImageConstants.IMG_16_MOVE_DOWN);
        tltmDown.addSelectionListener(selectionListener);

        Composite compositeTable = new Composite(compositeSteps, SWT.NONE);
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        treeTable = new CTreeViewer(compositeTable, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        treeTable.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        treeTable.getTree().setLinesVisible(true);
        treeTable.getTree().setHeaderVisible(true);

        TreeColumnLayout treeColumnLayout = new TreeColumnLayout();
        compositeTable.setLayout(treeColumnLayout);

        addTreeTableColumn(treeTable, treeColumnLayout, StringConstants.PA_COL_ITEM, 200, 0,
                new AstTreeItemLabelProvider(), new ItemColumnEditingSupport(treeTable, this));

        addTreeTableColumn(treeTable, treeColumnLayout, StringConstants.PA_COL_OBJ, 200, 0, new AstTreeLabelProvider(),
                new TestObjectEditingSupport(treeTable, this));
        addTreeTableColumn(treeTable, treeColumnLayout, StringConstants.PA_COL_INPUT, 200, 0,
                new AstTreeLabelProvider(), new InputColumnEditingSupport(treeTable, this));
        addTreeTableColumn(treeTable, treeColumnLayout, StringConstants.PA_COL_OUTPUT, 200, 0,
                new AstTreeLabelProvider(), new OutputColumnEditingSupport(treeTable, this));
        addTreeTableColumn(treeTable, treeColumnLayout, StringConstants.PA_COL_DESCRIPTION, 400, 100,
                new AstTreeLabelProvider(), new DescriptionColumnEditingSupport(treeTable, this));

        treeTable.setContentProvider(new AstTreeTableContentProvider());

        setTreeTableActivation();

        treeTable.getControl().addListener(SWT.MeasureItem, new Listener() {
            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                // do nothing to prevent double click to expand tree items
            }
        });

        // Enable tool-tip support for treeTable
        treeTable.getTree().setToolTipText("");
        KeywordTreeViewerToolTipSupport.enableFor(treeTable);

        createContextMenu();
        addTreeTableKeyListener();
        hookDragEvent();
        hookDropEvent();
    }

    private void setTreeTableActivation() {
        int activationBitMask = ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
                | ColumnViewerEditor.KEYBOARD_ACTIVATION;
        FocusCellOwnerDrawHighlighterForMultiSelection focusCellHighlighter = new FocusCellOwnerDrawForManualTestcase(
                treeTable);
        TreeViewerEditor.create(treeTable, new TreeViewerFocusCellManager(treeTable, focusCellHighlighter),
                new CustomEditorActivationStrategy(treeTable, focusCellHighlighter), activationBitMask);

    }

    public void addFailureHandlingSubMenu(Menu menu) {
        MenuItem failureHandlingMenuItem = new MenuItem(menu, SWT.CASCADE);
        failureHandlingMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_CHANGE_FAILURE_HANDLING);
        failureHandlingMenuItem.addSelectionListener(selectionListener);

        Menu failureHandlingMenu = new Menu(menu);

        MenuItem failureStopMenuItem = new MenuItem(failureHandlingMenu, SWT.NONE);
        failureStopMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_STOP_ON_FAILURE);
        failureStopMenuItem.addSelectionListener(selectionListener);
        failureStopMenuItem.setID(TreeTableMenuItemConstants.CHANGE_FAILURE_HANDLING_MENU_ITEM_ID);
        failureStopMenuItem.setData(TreeTableMenuItemConstants.FAILURE_HANDLING_KEY, FailureHandling.STOP_ON_FAILURE);

        MenuItem failureContinueMenuItem = new MenuItem(failureHandlingMenu, SWT.NONE);
        failureContinueMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_CONTINUE_ON_FAILURE);
        failureContinueMenuItem.addSelectionListener(selectionListener);
        failureContinueMenuItem.setID(TreeTableMenuItemConstants.CHANGE_FAILURE_HANDLING_MENU_ITEM_ID);
        failureContinueMenuItem.setData(TreeTableMenuItemConstants.FAILURE_HANDLING_KEY,
                FailureHandling.CONTINUE_ON_FAILURE);

        MenuItem optionalMenuItem = new MenuItem(failureHandlingMenu, SWT.NONE);
        optionalMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_OPTIONAL);
        optionalMenuItem.addSelectionListener(selectionListener);
        optionalMenuItem.setID(TreeTableMenuItemConstants.CHANGE_FAILURE_HANDLING_MENU_ITEM_ID);
        optionalMenuItem.setData(TreeTableMenuItemConstants.FAILURE_HANDLING_KEY, FailureHandling.OPTIONAL);

        failureHandlingMenuItem.setMenu(failureHandlingMenu);
    }

    private void createContextMenu() {
        treeTable.getTree().addListener(SWT.MenuDetect, new Listener() {
            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                Menu menu = treeTable.getTree().getMenu();
                if (menu != null) {
                    menu.dispose();
                }
                menu = new Menu(treeTable.getTree());

                if (treeTable.getTree().getSelectionCount() == 1) {
                    // Add step add
                    TestCaseMenuUtil.addActionSubMenu(menu, TreeTableMenuItemConstants.AddAction.Add,
                            StringConstants.ADAP_MENU_CONTEXT_ADD, selectionListener);

                    MenuItem insertMenuItem = new MenuItem(menu, SWT.CASCADE);
                    insertMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_INSERT);

                    Menu insertMenu = new Menu(menu);
                    insertMenuItem.setMenu(insertMenu);

                    // Add step before
                    TestCaseMenuUtil.addActionSubMenu(insertMenu, TreeTableMenuItemConstants.AddAction.InsertBefore,
                            StringConstants.ADAP_MENU_CONTEXT_INSERT_BEFORE, selectionListener);

                    // Add step after
                    TestCaseMenuUtil.addActionSubMenu(insertMenu, TreeTableMenuItemConstants.AddAction.InsertAfter,
                            StringConstants.ADAP_MENU_CONTEXT_INSERT_AFTER, selectionListener);
                }

                MenuItem removeMenuItem = new MenuItem(menu, SWT.PUSH);
                removeMenuItem.setText(createMenuItemLabel(StringConstants.ADAP_MENU_CONTEXT_REMOVE,
                        KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.DEL_NAME })));
                removeMenuItem.addSelectionListener(selectionListener);
                removeMenuItem.setID(TreeTableMenuItemConstants.REMOVE_MENU_ITEM_ID);

                MenuItem copyMenuItem = new MenuItem(menu, SWT.PUSH);
                copyMenuItem.setText(createMenuItemLabel(StringConstants.ADAP_MENU_CONTEXT_COPY,
                        KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, "C" })));
                copyMenuItem.addSelectionListener(selectionListener);
                copyMenuItem.setID(TreeTableMenuItemConstants.COPY_MENU_ITEM_ID);

                MenuItem cutMenuItem = new MenuItem(menu, SWT.PUSH);
                cutMenuItem.setText(createMenuItemLabel(StringConstants.ADAP_MENU_CONTEXT_CUT,
                        KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, "X" })));
                cutMenuItem.addSelectionListener(selectionListener);
                cutMenuItem.setID(TreeTableMenuItemConstants.CUT_MENU_ITEM_ID);

                MenuItem pasteMenuItem = new MenuItem(menu, SWT.PUSH);
                pasteMenuItem.setText(createMenuItemLabel(StringConstants.ADAP_MENU_CONTEXT_PASTE,
                        KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, "V" })));
                pasteMenuItem.addSelectionListener(selectionListener);
                pasteMenuItem.setID(TreeTableMenuItemConstants.PASTE_MENU_ITEM_ID);

                addFailureHandlingSubMenu(menu);

                MenuItem disableMenuItem = new MenuItem(menu, SWT.PUSH);
                disableMenuItem.setText(createMenuItemLabel(StringConstants.ADAP_MENU_CONTEXT_DISABLE,
                        KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, "D" })));
                disableMenuItem.addSelectionListener(selectionListener);
                disableMenuItem.setID(TreeTableMenuItemConstants.DISABLE_MENU_ITEM_ID);

                MenuItem enableMenuItem = new MenuItem(menu, SWT.PUSH);
                enableMenuItem.setText(createMenuItemLabel(StringConstants.ADAP_MENU_CONTEXT_ENABLE,
                        KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, "E" })));
                enableMenuItem.addSelectionListener(selectionListener);
                enableMenuItem.setID(TreeTableMenuItemConstants.ENABLE_MENU_ITEM_ID);
                createDynamicGotoMenu(menu);
                treeTable.getTree().setMenu(menu);
            }
        });
    }

    private String createMenuItemLabel(String text, String keyCombination) {
        return text + "\t" + keyCombination;
    }

    /**
     * Add KeyListener to TreeTable.
     * Handle Delete, Ctrl + c, Ctrl + x, Ctrl + v, Ctrl + d, Ctrl + e for test steps
     */
    private void addTreeTableKeyListener() {
        treeTable.getControl().addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
            }

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

                    // Disable
                    if (e.keyCode == 'd') {
                        treeTableInput.disable();
                        return;
                    }

                    // Enable
                    if (e.keyCode == 'e') {
                        treeTableInput.enable();
                    }
                }
            }
        });
    }

    private void addTreeTableColumn(TreeViewer parent, TreeColumnLayout treeColumnLayout, String headerText, int width,
            int weight, CellLabelProvider labelProvider, EditingSupport editingSupport) {
        TreeViewerColumn treeTableColumn = new TreeViewerColumn(parent, SWT.NONE);
        treeTableColumn.getColumn().setWidth(width);
        treeTableColumn.getColumn().setMoveable(true);
        treeTableColumn.getColumn().setText(headerText);
        treeTableColumn.setLabelProvider(labelProvider);
        treeTableColumn.setEditingSupport(editingSupport);
        treeColumnLayout.setColumnData(treeTableColumn.getColumn(), new ColumnWeightData(weight,
                treeTableColumn.getColumn().getWidth()));
    }

    private void hookDragEvent() {
        int operations = DND.DROP_COPY | DND.DROP_MOVE;

        DragSource dragSource = new DragSource(treeTable.getTree(), operations);
        dragSource.setTransfer(new Transfer[] { new ScriptTransfer() });
        dragSource.addDragListener(new DragSourceListener() {
            @Override
            public void dragStart(DragSourceEvent event) {
                dragNodes = getKeywordScriptFromTree();
                if (dragNodes.size() > 0) {
                    event.doit = true;
                } else {
                    event.doit = false;
                }
            }

            @Override
            public void dragSetData(DragSourceEvent event) {
                StringBuilder scriptSnippets = new StringBuilder();
                for (AstTreeTableNode astTreeTableNode : dragNodes) {
                    StringBuilder stringBuilder = new StringBuilder();
                    GroovyWrapperParser groovyParser = new GroovyWrapperParser(stringBuilder);
                    groovyParser.parse(astTreeTableNode.getASTObject());
                    scriptSnippets.append(stringBuilder.toString());
                    scriptSnippets.append("\n");
                }
                if (scriptSnippets.length() > 0) {
                    ScriptTransferData transferData = new ScriptTransferData(scriptSnippets.toString(),
                            getTestCase().getId());
                    event.data = new ScriptTransferData[] { transferData };
                }
            }

            @Override
            public void dragFinished(DragSourceEvent event) {
                try {
                    if (event.detail == DND.DROP_MOVE) {
                        treeTableInput.removeRows(dragNodes);
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }
                dragNodes.clear();
            }

            private List<AstTreeTableNode> getKeywordScriptFromTree() {
                TreeItem[] selection = treeTable.getTree().getSelection();
                List<AstTreeTableNode> treeEntities = new ArrayList<AstTreeTableNode>();
                for (TreeItem item : selection) {
                    Object treeItemData = item.getData();
                    if (treeItemData instanceof AstTreeTableNode && !(treeItemData instanceof AstMethodTreeTableNode)
                            && TestCaseTreeTableInput.isNodeMoveable((AstTreeTableNode) treeItemData)) {
                        treeEntities.add((AstTreeTableNode) treeItemData);
                    }
                }
                return treeEntities;
            };

        });
    }

    private void hookDropEvent() {
        DropTarget dt = new DropTarget(treeTable.getTree(), DND.DROP_MOVE | DND.DROP_COPY);
        List<Transfer> treeEntityTransfers = TransferTypeCollection.getInstance().getTreeEntityTransfer();
        treeEntityTransfers.add(new KeywordBrowserTreeEntityTransfer());
        treeEntityTransfers.add(new ScriptTransfer());
        treeEntityTransfers.add(TextTransfer.getInstance());
        dt.setTransfer(treeEntityTransfers.toArray(new Transfer[treeEntityTransfers.size()]));
        dt.addDropListener(new TestStepTableDropListener(treeTable, this));
    }

    public void setDirty(boolean isDirty) {
        if (mPart != null) {
            mPart.setDirty(isDirty);
        }
        parentTestCaseCompositePart.checkDirty();
    }

    public boolean isManualScriptChanged() {
        if (treeTableInput != null) {
            return treeTableInput.isChanged();
        }
        return false;
    }

    public void setManualScriptChanged(boolean change) {
        if (treeTableInput != null) {
            treeTableInput.setChanged(change);
        }
    }

    @Override
    public void handleEvent(Event event) {
        switch (event.getTopic()) {
            case EventConstants.TESTCASE_SETTINGS_FAILURE_HANDLING_UPDATED:
                try {
                    if (treeTableInput != null) {
                        loadASTNodesToTreeTable(treeTableInput.getMainClassNode());
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

    public void performToolItemSelected(ToolItem toolItem, SelectionEvent selectionEvent) {
        if (toolItem.equals(tltmAddStep) || toolItem.equals(tltmInsertStep)) {
            openToolItemMenu(toolItem, selectionEvent);
        } else if (toolItem.equals(tltmRemoveStep)) {
            removeTestStep();
        } else if (toolItem.equals(tltmUp)) {
            upStep();
        } else if (toolItem.equals(tltmDown)) {
            downStep();
        }
    }

    private void openToolItemMenu(ToolItem toolItem, SelectionEvent selectionEvent) {
        if (selectionEvent.detail == SWT.ARROW) {
            if (toolItem.getData() instanceof Menu) {
                Rectangle rect = toolItem.getBounds();
                Point pt = toolItem.getParent().toDisplay(new Point(rect.x, rect.y));
                Menu menu = (Menu) toolItem.getData();
                menu.setLocation(pt.x, pt.y + rect.height);
                menu.setVisible(true);
            }
        } else {
            treeTableInput.addNewDefaultBuiltInKeyword(NodeAddType.Add);
        }
    }

    public void performMenuItemSelected(MenuItem menuItem) {
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
                    changeKeywordFailureHandling((FailureHandling) failureHandlingValue);
                }
                break;
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
                treeTableInput.enable();
                break;
            case TreeTableMenuItemConstants.DISABLE_MENU_ITEM_ID:
                treeTableInput.disable();
                break;
            default:
                treeTableInput.addNewAstObject(menuItem.getID(), treeTableInput.getSelectedNode(), addType);
                break;
        }
    }

    public void addStatements(List<StatementWrapper> statements, NodeAddType addType) {
        treeTableInput.addNewAstObjects(statements, treeTableInput.getSelectedNode(), addType);
    }

    private void changeKeywordFailureHandling(FailureHandling failureHandling) {
        treeTableInput.changeFailureHandling(failureHandling);
    }

    private void removeTestStep() {
        treeTableInput.removeSelectedRows();
    }

    private void upStep() {
        treeTableInput.moveUp();
    }

    private void downStep() {
        treeTableInput.moveDown();
    }

    private void copyTestStep() {
        treeTableInput.copy(treeTableInput.getSelectedNodes());
    }

    private void cutTestStep() {
        treeTableInput.cut(treeTableInput.getSelectedNodes());
    }

    private void pasteTestStep() {
        treeTableInput.paste(treeTableInput.getSelectedNode(), NodeAddType.Add);
    }

    public TestCaseEntity getTestCase() {
        return parentTestCaseCompositePart.getTestCase();
    }

    public TestCaseTreeTableInput getTreeTableInput() {
        return treeTableInput;
    }

    public void loadASTNodesToTreeTable(ScriptNodeWrapper scriptNode) throws Exception {
        treeTableInput = new TestCaseTreeTableInput(scriptNode, treeTable, this);
        treeTableInput.refresh();
    }

    public void addVariables(VariableEntity[] variables) {
        parentTestCaseCompositePart.addVariables(variables);
    }

    public VariableEntity[] getVariables() {
        return parentTestCaseCompositePart.getVariables();
    }

    @Override
    public String getEntityId() {
        return getTestCase().getIdForDisplay();
    }

    public void addDefaultImports() {
        treeTableInput.addDefaultImports();
    }

    public TreeViewer getTestCaseTreeTable() {
        return treeTable;
    }

    public List<AstTreeTableNode> getDragNodes() {
        return dragNodes;
    }

    public boolean isTestCaseEmpty() {
        TreeItem treeItems[] = treeTable.getTree().getItems();
        for (TreeItem item : treeItems) {
            if (item.getText().matches("\\d+.*")) {
                return false;
            }
        }
        return true;
    }
    private void createDynamicGotoMenu(Menu menu) {
        IStructuredSelection selection = (IStructuredSelection) treeTable.getSelection();
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
