package com.kms.katalon.composer.testcase.parts;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.stmt.Statement;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MGenericTile;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.control.ImageButton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.part.IComposerPart;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.explorer.util.TransferTypeCollection;
import com.kms.katalon.composer.testcase.ast.treetable.AstMethodTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.components.FocusCellOwnerDrawHighlighterForMultiSelection;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.constants.TestCaseEventConstant;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants.AddAction;
import com.kms.katalon.composer.testcase.keywords.KeywordBrowserTreeEntityTransfer;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput.NodeAddType;
import com.kms.katalon.composer.testcase.preferences.TestCasePreferenceDefaultValueInitializer;
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
import com.kms.katalon.composer.testcase.util.AstTreeTableEntityUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.core.ast.GroovyParser;
import com.kms.katalon.core.keyword.IKeywordContributor;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testcase.TestCaseFactory;
import com.kms.katalon.core.testobject.ObjectRepository;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;

public class TestCasePart implements IComposerPart, EventHandler {

    private Text textTestCaseName;

    private Text textTestCaseDescription;

    private Text textTestCaseComment;

    private Text textTestCaseTag;

    private Text lblTestCaseIDContain;

    private Composite compositeManual, compositeToolbar, compositeMain, compositeInformation, compositeDetails,
            compositeInformationHeader, compositeTable, compositeVisualizer, compositeInformationDetails,
            compositeSteps;

    private ImageButton btnExpandVisualizer, btnExpandGeneralInformation;

    private ToolItem tltmRecord, tltmAddStep, tltmInsertStep, tltmRemoveStep, tltmUp, tltmDown;

    private boolean isInfoExpanded, isVisualExpanded;

    // private Table table;
    // private TestStepTableViewer checkboxTableViewer;
    TreeViewer treeTable;

    private MPart mPart;

    private TestCaseTreeTableInput treeTableInput;

    private Composite composite;

    public MPart getMPart() {
        return mPart;
    }

    @Inject
    private IEventBroker eventBroker;

    private TestCaseSelectionListener selectionListener;

    private TestCaseCompositePart parentTestCaseCompositePart;

    private Listener layoutGeneralCompositeListener = new Listener() {

        @Override
        public void handleEvent(org.eclipse.swt.widgets.Event event) {
            layoutGeneralComposite();
        }
    };

    private Label lblGeneralInformation;

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
        updateInput();
        registerControlModifyListener();
    }

    private void layoutGeneralComposite() {
        updateCompositeInfoLayOut();
    }

    private void registerControlModifyListener() {
        lblGeneralInformation.addListener(SWT.MouseDown, layoutGeneralCompositeListener);
        btnExpandGeneralInformation.addListener(SWT.MouseDown, layoutGeneralCompositeListener);

        textTestCaseName.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String text = ((Text) e.getSource()).getText();
                getTestCase().setName(text);
                setDirty(true);
            }
        });

        textTestCaseTag.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String text = ((Text) e.getSource()).getText();
                getTestCase().setTag(text);
                setDirty(true);
            }
        });

        textTestCaseDescription.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String text = ((Text) e.getSource()).getText();
                getTestCase().setDescription(text);
                setDirty(true);
            }
        });

        textTestCaseComment.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String text = ((Text) e.getSource()).getText();
                getTestCase().setComment(text);
                setDirty(true);
            }
        });
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
        eventBroker.subscribe(TestCaseEventConstant.TESTCASE_UPDATE_TABLE_ITEM_BACKGROUND, this);
        eventBroker.subscribe(TestCaseEventConstant.TESTCASE_UPDATE_DIRTY, this);
        eventBroker.subscribe(TestCaseEventConstant.TESTCASE_RESET_DIRTY, this);
        eventBroker.subscribe(TestCaseEventConstant.TESTCASE_BUTTON_SELECTED, this);
        eventBroker.subscribe(TestCaseEventConstant.TESTCASE_TOOL_ITEM_SELECTED, this);
        eventBroker.subscribe(TestCaseEventConstant.TESTCASE_MENU_ITEM_SELECTED, this);
        eventBroker.subscribe(EventConstants.TEST_OBJECT_UPDATED, this);
    }

    private void createControls(Composite parent) {
        isInfoExpanded = true;
        isVisualExpanded = true;

        parent.setLayout(new FillLayout(SWT.HORIZONTAL));
        createTabManual(parent);
    }

    private void createTabManual(Composite parent) {
        compositeManual = new Composite(parent, SWT.NONE);
        compositeManual.setLayout(new GridLayout(1, false));

        createTestCaseManualToolbar();

        compositeMain = new Composite(compositeManual, SWT.NONE);
        GridLayout glCompositeMain = new GridLayout(1, false);
        glCompositeMain.marginWidth = 0;
        glCompositeMain.marginHeight = 0;
        compositeMain.setLayout(glCompositeMain);
        compositeMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        createTestCaseManualInfoControls();

        createTestCaseManualDetailsControls();
    }

    private void createTestCaseManualToolbar() {
        compositeToolbar = new Composite(compositeManual, SWT.NONE);
        compositeToolbar.setBackground(ColorUtil.getCompositeBackgroundColor());
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

    private void createTestCaseManualInfoControls() {

        compositeInformation = new Composite(compositeMain, SWT.NONE);
        compositeInformation.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        compositeInformation.setBackground(ColorUtil.getCompositeBackgroundColor());
        GridLayout glCompositeInformation = new GridLayout(1, false);
        glCompositeInformation.marginHeight = 0;
        glCompositeInformation.marginWidth = 0;
        glCompositeInformation.verticalSpacing = 0;
        compositeInformation.setLayout(glCompositeInformation);

        compositeInformationHeader = new Composite(compositeInformation, SWT.NONE);
        GridLayout glCompositeInformationHeader = new GridLayout(2, false);
        glCompositeInformationHeader.marginWidth = 0;
        glCompositeInformationHeader.marginHeight = 0;
        compositeInformationHeader.setLayout(glCompositeInformationHeader);
        compositeInformationHeader.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        compositeInformationHeader.setCursor(compositeInformationHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        btnExpandGeneralInformation = new ImageButton(compositeInformationHeader, SWT.NONE);
        btnExpandGeneralInformation.setImage(ImageConstants.IMG_16_ARROW_UP_BLACK);

        lblGeneralInformation = new Label(compositeInformationHeader, SWT.NONE);
        lblGeneralInformation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblGeneralInformation.setText(StringConstants.PA_LBL_GENERAL_INFO);
        lblGeneralInformation.setFont(JFaceResources.getFontRegistry().getBold(""));

        compositeInformationDetails = new Composite(compositeInformation, SWT.NONE);
        GridLayout glCompositeInformationDetails = new GridLayout(3, true);
        glCompositeInformationDetails.marginWidth = 40;
        glCompositeInformationDetails.horizontalSpacing = 30;
        compositeInformationDetails.setLayout(glCompositeInformationDetails);
        compositeInformationDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        compositeInformationDetails.setBounds(0, 0, 64, 64);

        Composite compositeIDAndName = new Composite(compositeInformationDetails, SWT.NONE);
        GridData gd_compositeIDAndName = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_compositeIDAndName.minimumWidth = 250;
        compositeIDAndName.setLayoutData(gd_compositeIDAndName);
        GridLayout glCompositeIDAndName = new GridLayout(2, false);
        glCompositeIDAndName.verticalSpacing = 10;
        compositeIDAndName.setLayout(glCompositeIDAndName);

        Label lblTestCaseID = new Label(compositeIDAndName, SWT.NONE);
        lblTestCaseID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblTestCaseID.setText(StringConstants.PA_LBL_ID);

        lblTestCaseIDContain = new Text(compositeIDAndName, SWT.BORDER);
        lblTestCaseIDContain.setEditable(false);
        GridData gd_lblTestCaseIDContain = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
        gd_lblTestCaseIDContain.heightHint = 20;
        lblTestCaseIDContain.setLayoutData(gd_lblTestCaseIDContain);

        Label lblTestCaseName = new Label(compositeIDAndName, SWT.NONE);
        lblTestCaseName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        lblTestCaseName.setText(StringConstants.PA_LBL_NAME);

        textTestCaseName = new Text(compositeIDAndName, SWT.BORDER);
        GridData gdTextTestCaseName = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
        gdTextTestCaseName.heightHint = 20;
        textTestCaseName.setLayoutData(gdTextTestCaseName);

        Composite compositeCommendAndTag = new Composite(compositeInformationDetails, SWT.NONE);
        GridData gd_compositeCommendAndTag = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_compositeCommendAndTag.minimumWidth = 250;
        compositeCommendAndTag.setLayoutData(gd_compositeCommendAndTag);
        GridLayout glCompositeCommendAndTag = new GridLayout(2, false);
        glCompositeCommendAndTag.verticalSpacing = 10;
        compositeCommendAndTag.setLayout(glCompositeCommendAndTag);

        Label lblTestDataId = new Label(compositeCommendAndTag, SWT.NONE);
        lblTestDataId.setText(StringConstants.PA_LBL_COMMENT);

        composite = new Composite(compositeCommendAndTag, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glComposite = new GridLayout(1, false);
        glComposite.marginWidth = 0;
        glComposite.marginHeight = 0;
        glComposite.horizontalSpacing = 1;
        composite.setLayout(glComposite);

        textTestCaseComment = new Text(composite, SWT.BORDER);
        GridData gdTextTestCaseComment = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdTextTestCaseComment.heightHint = 20;
        textTestCaseComment.setLayoutData(gdTextTestCaseComment);

        Label lblTestCaseTag = new Label(compositeCommendAndTag, SWT.NONE);
        lblTestCaseTag.setText(StringConstants.PA_LBL_TAG);

        textTestCaseTag = new Text(compositeCommendAndTag, SWT.BORDER);
        GridData gdTextTag = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
        gdTextTag.heightHint = 20;
        textTestCaseTag.setLayoutData(gdTextTag);

        Composite compositeDescription = new Composite(compositeInformationDetails, SWT.NONE);
        compositeDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        compositeDescription.setLayout(new GridLayout(2, false));

        Label lblDescription = new Label(compositeDescription, SWT.NONE);
        lblDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
        lblDescription.setText(StringConstants.PA_LBL_DESCRIPTION);

        textTestCaseDescription = new Text(compositeDescription, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
        GridData gdTextDescription = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
        gdTextDescription.heightHint = 60;
        textTestCaseDescription.setLayoutData(gdTextDescription);

        Label lblSupport = new Label(compositeDescription, SWT.NONE);
        lblSupport.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
    }

    private void createTestCaseManualDetailsControls() {
        compositeDetails = new Composite(compositeMain, SWT.NONE);
        compositeDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        compositeDetails.setLayout(new FormLayout());
        compositeDetails.setBackground(ColorUtil.getCompositeBackgroundColor());

        createTestCaseManualTableControls();

        // createTestCaseManualVisualizer();
    }

    private void createTestCaseManualTableControls() {
        compositeSteps = new Composite(compositeDetails, SWT.NONE);
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
        tltmAddStep.setToolTipText(StringConstants.PA_BTN_TIP_ADD);
        tltmAddStep.setImage(ImageConstants.IMG_24_ADD);
        tltmAddStep.addSelectionListener(selectionListener);

        Menu addMenu = new Menu(tltmAddStep.getParent().getShell());
        tltmAddStep.setData(addMenu);
        AstTreeTableEntityUtil.fillActionMenu(TreeTableMenuItemConstants.AddAction.Add, selectionListener, addMenu);

        tltmInsertStep = new ToolItem(toolbar, SWT.DROP_DOWN);
        tltmInsertStep.setText(StringConstants.PA_BTN_TIP_INSERT);
        tltmInsertStep.setToolTipText(StringConstants.PA_BTN_TIP_INSERT);
        tltmInsertStep.setImage(ImageConstants.IMG_24_INSERT);
        tltmInsertStep.addSelectionListener(selectionListener);

        Menu insertMenu = new Menu(tltmInsertStep.getParent().getShell());
        tltmInsertStep.setData(insertMenu);

        // Add step before
        AstTreeTableEntityUtil.addActionSubMenu(insertMenu, TreeTableMenuItemConstants.AddAction.InsertBefore,
                StringConstants.PA_MENU_SUB_BEFORE, selectionListener);

        // Add step after
        AstTreeTableEntityUtil.addActionSubMenu(insertMenu, TreeTableMenuItemConstants.AddAction.InsertAfter,
                StringConstants.PA_MENU_SUB_AFTER, selectionListener);

        tltmRemoveStep = new ToolItem(toolbar, SWT.NONE);
        tltmRemoveStep.setText(StringConstants.PA_BTN_TIP_REMOVE);
        tltmRemoveStep.setToolTipText(StringConstants.PA_BTN_TIP_REMOVE);
        tltmRemoveStep.setImage(ImageConstants.IMG_24_REMOVE);
        tltmRemoveStep.addSelectionListener(selectionListener);

        tltmUp = new ToolItem(toolbar, SWT.NONE);
        tltmUp.setText(StringConstants.PA_BTN_TIP_MOVE_UP);
        tltmUp.setToolTipText(StringConstants.PA_BTN_TIP_MOVE_UP);
        tltmUp.setImage(ImageConstants.IMG_24_UP);
        tltmUp.addSelectionListener(selectionListener);

        tltmDown = new ToolItem(toolbar, SWT.NONE);
        tltmDown.setText(StringConstants.PA_BTN_TIP_MOVE_DOWN);
        tltmDown.setToolTipText(StringConstants.PA_BTN_TIP_MOVE_DOWN);
        tltmDown.setImage(ImageConstants.IMG_24_DOWN);
        tltmDown.addSelectionListener(selectionListener);

        compositeTable = new Composite(compositeSteps, SWT.NONE);
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        treeTable = new TreeViewer(compositeTable, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        treeTable.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        treeTable.getTree().setLinesVisible(true);
        treeTable.getTree().setHeaderVisible(true);

        TreeColumnLayout treeColumnLayout = new TreeColumnLayout();
        compositeTable.setLayout(treeColumnLayout);

        addTreeTableColumn(treeTable, treeColumnLayout, StringConstants.PA_COL_ITEM, 200, 0,
                new AstTreeLabelProvider(), new ItemColumnEditingSupport(treeTable, this));

        addTreeTableColumn(treeTable, treeColumnLayout, StringConstants.PA_COL_OBJ, 200, 0, new AstTreeLabelProvider(),
                new TestObjectEditingSupport(treeTable, this));
        addTreeTableColumn(treeTable, treeColumnLayout, StringConstants.PA_COL_INPUT, 200, 0,
                new AstTreeLabelProvider(), new InputColumnEditingSupport(treeTable, this));
        addTreeTableColumn(treeTable, treeColumnLayout, StringConstants.PA_COL_OUTPUT, 200, 0,
                new AstTreeLabelProvider(), new OutputColumnEditingSupport(treeTable, this));
        addTreeTableColumn(treeTable, treeColumnLayout, StringConstants.PA_COL_DESCRIPTION, 400, 100,
                new AstTreeLabelProvider(), new DescriptionColumnEditingSupport(treeTable, this));

        treeTable.setContentProvider(new AstTreeTableContentProvider());

        TreeViewerFocusCellManager focusCellManager = new TreeViewerFocusCellManager(treeTable,
                new FocusCellOwnerDrawHighlighterForMultiSelection(treeTable));

        ColumnViewerEditorActivationStrategy activationSupport = new ColumnViewerEditorActivationStrategy(treeTable) {
            protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
                if (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION) {
                    EventObject source = event.sourceEvent;
                    if (source instanceof MouseEvent && ((MouseEvent) source).button == 3)
                        return false;

                    return true;
                } else if (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR) {
                    return true;
                }
                return false;
            }
        };

        TreeViewerEditor.create(treeTable, focusCellManager, activationSupport, ColumnViewerEditor.TABBING_HORIZONTAL
                | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.KEYBOARD_ACTIVATION);

        treeTable.getControl().addListener(SWT.MeasureItem, new Listener() {

            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                // do nothing to prevent double click to expand tree items
            }
        });

        // Enable tool-tip support for treeTable
        treeTable.getTree().setToolTipText("");
        ColumnViewerToolTipSupport.enableFor(treeTable);

        addTreeTableKeyListener();
        createContextMenu();
        hookDragEvent();
        hookDropEvent();
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
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                Menu menu = treeTable.getTree().getMenu();
                if (menu != null) {
                    menu.dispose();
                }
                menu = new Menu(treeTable.getTree());

                if (treeTable.getTree().getSelectionCount() == 1) {
                    // Add step add
                    AstTreeTableEntityUtil.addActionSubMenu(menu, TreeTableMenuItemConstants.AddAction.Add,
                            StringConstants.ADAP_MENU_CONTEXT_ADD, selectionListener);

                    MenuItem insertMenuItem = new MenuItem(menu, SWT.CASCADE);
                    insertMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_INSERT);

                    Menu insertMenu = new Menu(menu);
                    insertMenuItem.setMenu(insertMenu);

                    // Add step before
                    AstTreeTableEntityUtil.addActionSubMenu(insertMenu,
                            TreeTableMenuItemConstants.AddAction.InsertBefore,
                            StringConstants.ADAP_MENU_CONTEXT_INSERT_BEFORE, selectionListener);

                    // Add step after
                    AstTreeTableEntityUtil.addActionSubMenu(insertMenu,
                            TreeTableMenuItemConstants.AddAction.InsertAfter,
                            StringConstants.ADAP_MENU_CONTEXT_INSERT_AFTER, selectionListener);
                }

                MenuItem removeMenuItem = new MenuItem(menu, SWT.PUSH);
                removeMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_REMOVE);
                removeMenuItem.addSelectionListener(selectionListener);
                removeMenuItem.setID(TreeTableMenuItemConstants.REMOVE_MENU_ITEM_ID);

                MenuItem copyMenuItem = new MenuItem(menu, SWT.PUSH);
                copyMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_COPY);
                copyMenuItem.addSelectionListener(selectionListener);
                copyMenuItem.setID(TreeTableMenuItemConstants.COPY_MENU_ITEM_ID);

                MenuItem cutMenuItem = new MenuItem(menu, SWT.PUSH);
                cutMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_CUT);
                cutMenuItem.addSelectionListener(selectionListener);
                cutMenuItem.setID(TreeTableMenuItemConstants.CUT_MENU_ITEM_ID);

                MenuItem pasteMenuItem = new MenuItem(menu, SWT.PUSH);
                pasteMenuItem.setText(StringConstants.ADAP_MENU_CONTEXT_PASTE);
                pasteMenuItem.addSelectionListener(selectionListener);
                pasteMenuItem.setID(TreeTableMenuItemConstants.PASTE_MENU_ITEM_ID);

                addFailureHandlingSubMenu(menu);
                treeTable.getTree().setMenu(menu);
            }
        });
    }

    /**
     * Add KeyListener to TreeTable. Handle Delete, Ctrl + c, Ctrl + x, Ctrl + v for test steps
     */
    private void addTreeTableKeyListener() {
        treeTable.getControl().addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.DEL) {
                    removeTestStep();
                } else if (((e.stateMask & SWT.CTRL) == SWT.CTRL)) {
                    if (e.keyCode == 'c') {
                        copyTestStep();
                    } else if (e.keyCode == 'x') {
                        cutTestStep();
                    } else if (e.keyCode == 'v') {
                        pasteTestStep();
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
        treeColumnLayout.setColumnData(treeTableColumn.getColumn(), new ColumnWeightData(weight, treeTableColumn
                .getColumn().getWidth()));
    }

    private void hookDragEvent() {
        int operations = DND.DROP_COPY | DND.DROP_MOVE;

        DragSource dragSource = new DragSource(treeTable.getTree(), operations);
        dragSource.setTransfer(new Transfer[] { new ScriptTransfer() });

        dragSource.addDragListener(new DragSourceListener() {
            List<AstTreeTableNode> selectedNodes;

            public void dragStart(DragSourceEvent event) {
                selectedNodes = getKeywordScriptFromTree();
                if (selectedNodes.size() > 0) {
                    event.doit = true;
                } else {
                    event.doit = false;
                }
            }

            public void dragSetData(DragSourceEvent event) {
                StringBuilder scriptSnippets = new StringBuilder();
                for (AstTreeTableNode astTreeTableNode : selectedNodes) {
                    if (astTreeTableNode instanceof AstStatementTreeTableNode) {
                        AstStatementTreeTableNode statementNode = (AstStatementTreeTableNode) astTreeTableNode;
                        if (statementNode.getDescription() != null) {
                            StringBuilder stringBuilder = new StringBuilder();
                            GroovyParser groovyParser = new GroovyParser(stringBuilder);
                            groovyParser.parse(statementNode.getDescription());
                            scriptSnippets.append(stringBuilder.toString());
                            scriptSnippets.append("\n");
                        }
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    GroovyParser groovyParser = new GroovyParser(stringBuilder);
                    groovyParser.parse(astTreeTableNode.getASTObject());
                    scriptSnippets.append(stringBuilder.toString());
                    scriptSnippets.append("\n");
                }
                if (scriptSnippets.length() > 0) {
                    ScriptTransferData transferData = new ScriptTransferData(scriptSnippets.toString(), getTestCase()
                            .getId());
                    event.data = new ScriptTransferData[] { transferData };
                }
            }

            public void dragFinished(DragSourceEvent event) {
                try {
                    if (event.detail == DND.DROP_MOVE) {
                        treeTableInput.removeRows(selectedNodes);
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }
                selectedNodes.clear();
            }

        });
    }

    private List<AstTreeTableNode> getKeywordScriptFromTree() {
        TreeItem[] selection = treeTable.getTree().getSelection();
        List<AstTreeTableNode> treeEntities = new ArrayList<AstTreeTableNode>();
        for (TreeItem item : selection) {
            if (item.getData() instanceof AstTreeTableNode && !(item.getData() instanceof AstMethodTreeTableNode)) {
                treeEntities.add((AstTreeTableNode) item.getData());
            }
        }
        return treeEntities;
    };

    private void hookDropEvent() {
        DropTarget dt = new DropTarget(treeTable.getTree(), DND.DROP_MOVE | DND.DROP_COPY);
        List<Transfer> treeEntityTransfers = TransferTypeCollection.getInstance().getTreeEntityTransfer();
        treeEntityTransfers.add(new KeywordBrowserTreeEntityTransfer());
        treeEntityTransfers.add(new ScriptTransfer());
        treeEntityTransfers.add(TextTransfer.getInstance());
        dt.setTransfer(treeEntityTransfers.toArray(new Transfer[treeEntityTransfers.size()]));
        dt.addDropListener(new TestStepTableDropListener(treeTable, this));
    }

    // private void createTestCaseManualVisualizer() {
    // compositeVisualizer = new Composite(compositeDetails, SWT.BORDER);
    // GridLayout glCompositeVisualizer = new GridLayout(1, false);
    // glCompositeVisualizer.marginHeight = 0;
    // glCompositeVisualizer.marginWidth = 0;
    // compositeVisualizer.setLayout(glCompositeVisualizer);
    // FormData fd_compositeVisualizer = new FormData();
    // fd_compositeVisualizer.bottom = new FormAttachment(100);
    // fd_compositeVisualizer.right = new FormAttachment(100);
    // fd_compositeVisualizer.top = new FormAttachment(0);
    // fd_compositeVisualizer.left = new FormAttachment(compositeSteps);
    //
    // compositeVisualizer.setLayoutData(fd_compositeVisualizer);
    //
    // Composite compositeVisualizerHeader = new Composite(compositeVisualizer,
    // SWT.NONE);
    // compositeVisualizerHeader.setBackground(new Color(Display.getCurrent(),
    // 67, 81, 90));
    // compositeVisualizerHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP,
    // false, false, 1, 1));
    // GridLayout glCompositeVisualizerHeader = new GridLayout(2, false);
    // glCompositeVisualizerHeader.marginHeight = 0;
    // glCompositeVisualizerHeader.marginWidth = 0;
    // compositeVisualizerHeader.setLayout(glCompositeVisualizerHeader);
    //
    // btnExpandVisualizer = new Button(compositeVisualizerHeader, SWT.FLAT);
    // btnExpandVisualizer.setText(">>");
    // btnExpandVisualizer.setForeground(ColorUtil.getTextWhiteColor());
    // btnExpandVisualizer.setBackground(new Color(Display.getCurrent(), 67, 81,
    // 90));
    //
    // Label lblVisualizerHeader = new Label(compositeVisualizerHeader,
    // SWT.NONE);
    // lblVisualizerHeader.setForeground(ColorUtil.getTextWhiteColor());
    // lblVisualizerHeader.setText("Visualizer");
    //
    // Composite compositeVisualizerContent = new Composite(compositeVisualizer,
    // SWT.NONE);
    // compositeVisualizerContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
    // true, true, 1, 1));
    //
    // btnExpandVisualizer.addSelectionListener(selectionListener);
    // }

    public void updateInput() {
        try {
            TestCaseEntity testCase = getTestCase();
            // update info of TestCase
            String dispID = testCase.getIdForDisplay().replace("\\", "/");
            lblTestCaseIDContain.setText(dispID);

            if (testCase.getName() != null) {
                textTestCaseName.setText(testCase.getName());
            }

            if (testCase.getDescription() != null) {
                textTestCaseDescription.setText(testCase.getDescription());
            }

            if (testCase.getComment() != null) {
                textTestCaseComment.setText(testCase.getComment());
            }

            if (testCase.getTag() != null) {
                textTestCaseTag.setText(testCase.getTag());
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_FAILED_TO_LOAD_TEST_CASE);
        }
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
        SelectionEvent selectionEvent = null;
        if (event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME) instanceof SelectionEvent) {
            selectionEvent = (SelectionEvent) event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
        }
        if (event.getTopic().equals(TestCaseEventConstant.TESTCASE_UPDATE_TABLE_ITEM_BACKGROUND)) {
            // int index = (int)
            // event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
            // for (int i = index; i < table.getItemCount(); i++) {
            // setTableItemBackGroundColor(i);
            // }
        } else if (event.getTopic().equals(TestCaseEventConstant.TESTCASE_RESET_DIRTY) && selectionEvent != null
                && selectionEvent.getSource() instanceof TestCaseEntity) {
            TestCaseEntity entity = (TestCaseEntity) selectionEvent.getSource();
            if (getTestCase().equals(entity)) {
                setDirty(false);
            }
        } else if (event.getTopic().equals(TestCaseEventConstant.TESTCASE_UPDATE_DIRTY)) {
            // TestStepTableViewer viewer = (TestStepTableViewer) event
            // .getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
            // if (viewer == this.checkboxTableViewer) {
            setDirty(true);
            // }
        } else if (event.getTopic().equals(EventConstants.TEST_OBJECT_UPDATED) && selectionEvent != null) {
            Object object = selectionEvent.getSource();
            if (object != null && object instanceof Object[]) {
                // String oldPk = (String) ((Object[]) object)[0];
                // WebElementEntity objectRepo = (WebElementEntity) ((Object[])
                // object)[1];
                // checkboxTableViewer.refreshObjectRepository(oldPk,
                // objectRepo);
            }
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
            addNewDefaultBuiltInKeyword(NodeAddType.Add);
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
        default:
            treeTableInput.addNewAstObject(menuItem.getID(), treeTableInput.getSelectedNode(), addType);
            break;
        }
    }

    public void addStatements(List<Statement> statements, NodeAddType addType) {
        try {
            treeTableInput.addNewAstObjects(statements, treeTableInput.getSelectedNode(), addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_ADD_STATEMENTS);
        }
    }

    private void addNewDefaultBuiltInKeyword(NodeAddType addType) {
        try {
            IKeywordContributor defaultBuiltinKeywordContributor = TestCasePreferenceDefaultValueInitializer
                    .getDefaultKeywordType();
            String defaultSettingKeywordName = TestCasePreferenceDefaultValueInitializer.getDefaultKeywords().get(
                    defaultBuiltinKeywordContributor.getKeywordClass().getName());
            treeTableInput.addImport(defaultBuiltinKeywordContributor.getKeywordClass());
            treeTableInput.addImport(ObjectRepository.class);
            treeTableInput.addImport(TestCaseFactory.class);
            treeTableInput.addImport(FailureHandling.class);
            treeTableInput.addNewAstObject(AstTreeTableInputUtil.createBuiltInKeywordMethodCall(
                    defaultBuiltinKeywordContributor.getKeywordClass().getSimpleName(), defaultSettingKeywordName),
                    treeTableInput.getSelectedNode(), addType);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, StringConstants.PA_ERROR_MSG_CANNOT_ADD_KEYWORD);
        }
    }

    public void performButtonSelected(Button button) {
        if (button.equals(btnExpandGeneralInformation)) {
            updateCompositeInfoLayOut();
        } else if (button.equals(btnExpandVisualizer)) {
            updateCompositeDetailsLayout();
        }
    }

    private void changeKeywordFailureHandling(FailureHandling failureHandling) {
        try {
            treeTableInput.changeFailureHandling(failureHandling);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_CHANGE_FAILURE_HANDLING);
        }
    }

    private void removeTestStep() {
        try {
            treeTableInput.removeSelectedRows();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_REMOVE_STATEMENT);
        }
    }

    private void upStep() {
        try {
            treeTableInput.moveUp();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_MOVE_STATEMENT_UP);
        }
    }

    private void downStep() {
        try {
            treeTableInput.moveDown();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_CANNOT_MOVE_STATEMENT_DOWN);
        }
    }

    private void copyTestStep() {
        try {
            treeTableInput.copy(treeTableInput.getSelectedNodes());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, StringConstants.PA_ERROR_MSG_CANNOT_COPY);
        }
    }

    private void cutTestStep() {
        try {
            treeTableInput.cut(treeTableInput.getSelectedNodes());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, StringConstants.PA_ERROR_MSG_CANNOT_CUT);
        }
    }

    private void pasteTestStep() {
        try {
            treeTableInput.paste(treeTableInput.getSelectedNode(), NodeAddType.Add);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(null, StringConstants.ERROR_TITLE, StringConstants.PA_ERROR_MSG_CANNOT_PASTE);
        }
    }

    private void updateCompositeDetailsLayout() {
        if (isVisualExpanded) {
            ((FormData) compositeSteps.getLayoutData()).right = new FormAttachment(100, 100,
                    -btnExpandVisualizer.getBounds().width - compositeVisualizer.getBorderWidth());
        } else {
            ((FormData) compositeSteps.getLayoutData()).right = new FormAttachment(100, 100, -250);

        }
        Display.getDefault().timerExec(50, new Runnable() {

            @Override
            public void run() {
                compositeDetails.layout();
            }
        });
        isVisualExpanded = !isVisualExpanded;
    }

    private void updateCompositeInfoLayOut() {
        Display.getDefault().timerExec(50, new Runnable() {
            @Override
            public void run() {
                compositeInformationDetails.setVisible(!isInfoExpanded);
                if (isInfoExpanded) {
                    ((GridData) compositeInformationDetails.getLayoutData()).exclude = true;
                    compositeInformation.setSize(compositeInformation.getSize().x, compositeInformation.getSize().y
                            - compositeDetails.getSize().y);
                } else {
                    ((GridData) compositeInformationDetails.getLayoutData()).exclude = false;
                }
                compositeInformation.layout(true, true);
                compositeInformation.getParent().layout();
                isInfoExpanded = !isInfoExpanded;

                if (isInfoExpanded) {
                    btnExpandGeneralInformation.setImage(ImageConstants.IMG_16_ARROW_UP_BLACK);
                } else {
                    btnExpandGeneralInformation.setImage(ImageConstants.IMG_16_ARROW_DOWN_BLACK);
                }
            }
        });
    }

    public TestCaseEntity getTestCase() {
        return parentTestCaseCompositePart.getTestCase();
    }

    public TestCaseTreeTableInput getTreeTableInput() {
        return treeTableInput;
    }

    public void loadASTNodesToTreeTable(List<ASTNode> astNodes) throws Exception {
        treeTableInput = new TestCaseTreeTableInput(astNodes, treeTable, this);
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
}
