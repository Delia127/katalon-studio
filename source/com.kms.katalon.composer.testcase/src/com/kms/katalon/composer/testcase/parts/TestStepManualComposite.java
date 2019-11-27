package com.kms.katalon.composer.testcase.parts;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.bindings.keys.IKeyLookup;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.control.CTreeViewer;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestSuiteTreeEntity;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.KeyEventUtil;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.components.viewer.CustomEditorActivationStrategy;
import com.kms.katalon.composer.components.viewer.CustomTreeViewerFocusCellManager;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.util.TransferTypeCollection;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.testcase.ast.dialogs.ClosureBuilderDialog;
import com.kms.katalon.composer.testcase.ast.treetable.AstMethodTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.components.KeywordTreeViewerToolTipSupport;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants.AddAction;
import com.kms.katalon.composer.testcase.dialogs.TestSuiteSelectionDialog;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.keywords.KeywordBrowserTreeEntityTransfer;
import com.kms.katalon.composer.testcase.model.ExecuteFromTestStepEntity;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput.NodeAddType;
import com.kms.katalon.composer.testcase.parts.decoration.DecoratedKeyword;
import com.kms.katalon.composer.testcase.parts.decoration.KeywordDecorationService;
import com.kms.katalon.composer.testcase.preferences.StoredKeyword;
import com.kms.katalon.composer.testcase.preferences.TestCasePreferenceDefaultValueInitializer;
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
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.composer.testcase.util.TestCaseMenuUtil;
import com.kms.katalon.composer.testcase.views.FocusCellOwnerDrawForManualTestcase;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.keyword.internal.KeywordContributorCollection;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.webservice.support.UrlEncoder;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.project.ProjectType;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.session.ExecutionSession;
import com.kms.katalon.integration.analytics.constants.AnalyticsStringConstants;
import com.kms.katalon.integration.analytics.entity.AnalyticsProject;
import com.kms.katalon.integration.analytics.entity.AnalyticsTeam;
import com.kms.katalon.integration.analytics.report.AnalyticsReportService;
import com.kms.katalon.integration.analytics.setting.AnalyticsSettingStore;
import com.kms.katalon.tracking.service.Trackings;

public class TestStepManualComposite {
	private ITestCasePart parentPart;

	private Composite compositeManual;

	private TreeViewer treeTable;

	private Tree childTableTree;

	private ToolItem tltmAddWSKeywordStep, tltmAddStep, tltmRemoveStep, tltmUp, tltmDown, tltmRecent, tltmAddTestSuite, tltmEditTag;

	private Label spacer;

	private Button btnViewHistory;

	private TestCaseSelectionListener selectionListener;

	private TestCaseTreeTableInput treeTableInput;

	private List<AstTreeTableNode> dragNodes;

	private CustomTreeViewerFocusCellManager focusCellManager;

	private AnalyticsReportService analyticsReportService = new AnalyticsReportService();

	private AnalyticsSettingStore analyticsSettingStore = new AnalyticsSettingStore(
			ProjectController.getInstance().getCurrentProject().getFolderLocation());

	private Menu recentMenu;
	
	private TestCaseCompositePart parentTestCaseCompositePart;

	public TestStepManualComposite(ITestCasePart parentPart, Composite parent){
		this(parentPart, parent, null);
	}
	
	public TestStepManualComposite(ITestCasePart parentPart, Composite parent, TestCaseCompositePart parentTestCaseCompositePart) {
		this.parentPart = parentPart;
		this.parentTestCaseCompositePart = parentTestCaseCompositePart;
		selectionListener = new TestCaseSelectionListener(this);
		// for ClosureDialog
		if (parentPart instanceof ClosureBuilderDialog) {
			createTestCaseManualTableControls(parent);
			return;
		}
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
		
		registerEventBrokerListeners();
	}

	public void setFocus() {
		compositeManual.setFocus();
	}

	private void createTestCaseManualToolbar(Composite parent) {
		Composite compositeToolbar = new Composite(parent, SWT.NONE);
		compositeToolbar.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_compositeToolbar = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd_compositeToolbar.exclude = true;
		compositeToolbar.setLayoutData(gd_compositeToolbar);

		ToolBar toolBar = new ToolBar(compositeToolbar, SWT.FLAT | SWT.RIGHT);
		toolBar.setForeground(ColorUtil.getToolBarForegroundColor());
		
		ToolItem tltmRecord = new ToolItem(toolBar, SWT.NONE);
		tltmRecord.setText(StringConstants.PA_TOOLBAR_RECORD);
		tltmRecord.setImage(ImageConstants.IMG_16_RECORD);
		tltmRecord.setToolTipText(StringConstants.PA_TOOLBAR_TIP_RECORD_TEST);
	}

	private void createTestCaseManualTableControls(Composite parent) {
		Composite compositeSteps = null;
		ToolBar toolbar, toolbarTwo;
		if (parentPart instanceof TestCasePart) {
			compositeSteps = new Composite(parent, SWT.NONE);
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
			GridLayout glCompositeTableButtons = new GridLayout(5, false);
			glCompositeTableButtons.marginHeight = 0;
			glCompositeTableButtons.marginWidth = 0;
			compositeTableButtons.setLayout(glCompositeTableButtons);
			compositeTableButtons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

			ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT | SWT.RIGHT);

			toolbar = toolBarManager.createControl(compositeTableButtons);
			toolbar.setForeground(ColorUtil.getToolBarForegroundColor());
			toolbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

			spacer = new Label(compositeTableButtons, SWT.None);
			spacer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

			ToolBarManager toolBarManager2 = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
			toolbarTwo = toolBarManager2.createControl(compositeTableButtons);
			toolbarTwo.setForeground(ColorUtil.getToolBarForegroundColor());
			toolbarTwo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
//			toolbarTwo.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
			
			btnViewHistory = new Button(compositeTableButtons, SWT.NONE);
			btnViewHistory.setText(ComposerTestcaseMessageConstants.BTN_TESTCASEHISTORY);
			btnViewHistory.setImage(ImageManager.getImage(IImageKeys.KATALON_TESTOPS_16));
			btnViewHistory.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					try {
						if (analyticsReportService.isIntegrationEnabled()
								&& analyticsSettingStore.getProject() != null) {
							Program.launch(createPath(analyticsSettingStore.getServerEndpoint(), 
									analyticsSettingStore.getTeam(),
									analyticsSettingStore.getProject(), parentPart.getTestCase().getIdForDisplay(),
									analyticsSettingStore.getToken()));
						} else {
							Program.launch(ApplicationInfo.getTestOpsServer());
						}
						Trackings.trackOpenKAIntegration("testCase");
					} catch (IOException | GeneralSecurityException e1) {
						LoggerSingleton.logError(e1);
					}
				}
			});

		} else { // for ClosureDialog
			ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
			toolbar = toolBarManager.createControl(parent);
			toolbar.setForeground(ColorUtil.getToolBarForegroundColor());
			toolbarTwo = toolBarManager.createControl(parent);
			toolbarTwo.setForeground(ColorUtil.getToolBarForegroundColor());
		}

		ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
		if (currentProject.getType() == ProjectType.WEBSERVICE) {
			tltmAddWSKeywordStep = new ToolItem(toolbar, SWT.NONE);
			tltmAddWSKeywordStep.setText(StringConstants.PA_BTN_ADD_WEB_SERVICE_KEYWORD);
			tltmAddWSKeywordStep.setImage(ImageConstants.IMG_16_ADD);
			tltmAddWSKeywordStep.addSelectionListener(selectionListener);
		}

		tltmAddStep = new ToolItem(toolbar, SWT.DROP_DOWN);
		tltmAddStep.setText(StringConstants.PA_BTN_TIP_ADD);
		tltmAddStep.setImage(ImageConstants.IMG_16_ADD);
		tltmAddStep.addSelectionListener(selectionListener);

		Menu addMenu = new Menu(tltmAddStep.getParent().getShell());
		tltmAddStep.setData(addMenu);
		TestCaseMenuUtil.fillActionMenu(TreeTableMenuItemConstants.AddAction.Add, selectionListener, addMenu);

		tltmRecent = new ToolItem(toolbar, SWT.DROP_DOWN);
		tltmRecent.setText(ComposerTestcaseMessageConstants.PA_BTN_TIP_RECENT);
		tltmRecent.setImage(ImageConstants.IMG_16_RECENT);
		tltmRecent.addSelectionListener(selectionListener);
		setRecentKeywordItemState();

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
		
		if(parentTestCaseCompositePart != null){
			tltmEditTag = new ToolItem(toolbar, SWT.NONE);
			tltmEditTag.setText(StringConstants.PA_BTN_EDIT_TAG);
			tltmEditTag.setImage(ImageConstants.IMG_16_EDIT);
			tltmEditTag.addSelectionListener(new SelectionAdapter() {
				@Override
			    public void widgetSelected(SelectionEvent e) {
					parentTestCaseCompositePart.getTabFolder().setSelection(5);
				}
			});
		}

		// --------------------
		tltmAddTestSuite = new ToolItem(toolbarTwo, SWT.DROP_DOWN);

		tltmAddTestSuite.setText(StringConstants.CONS_MENU_CONTEXT_ADD_TO_TEST_SUITE);
		tltmAddTestSuite.setImage(ImageConstants.IMG_16_ADD);
		tltmAddTestSuite.addSelectionListener(selectionListener);

		Menu createMenu = new Menu(tltmAddTestSuite.getParent().getShell());
		tltmAddTestSuite.setData(createMenu);
		TestCaseMenuUtil.fillActionMenuAddTestSuite(TreeTableMenuItemConstants.AddAction.Add, selectionListener,
				createMenu);

		Composite compositeTable = new Composite(compositeSteps == null ? parent : compositeSteps, SWT.NONE);
		compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		treeTable = new CTreeViewer(compositeTable, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		childTableTree = treeTable.getTree();
		childTableTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		childTableTree.setHeaderVisible(true);
		childTableTree.setLinesVisible(ControlUtils.shouldLineVisble(childTableTree.getDisplay()));

		TreeColumnLayout treeColumnLayout = new TreeColumnLayout();
		compositeTable.setLayout(treeColumnLayout);

		addTreeTableColumn(treeTable, treeColumnLayout, StringConstants.PA_COL_ITEM,
				parentPart instanceof TestCasePart ? 200 : 210, 0, new AstTreeItemLabelProvider(),
				new ItemColumnEditingSupport(treeTable, parentPart));
		addTreeTableColumn(treeTable, treeColumnLayout, StringConstants.PA_COL_OBJ,
				parentPart instanceof TestCasePart ? 200 : 140, 0, new AstTreeLabelProvider(),
				new TestObjectEditingSupport(treeTable, parentPart));
		addTreeTableColumn(treeTable, treeColumnLayout, StringConstants.PA_COL_INPUT,
				parentPart instanceof TestCasePart ? 200 : 140, 0, new AstTreeLabelProvider(),
				new InputColumnEditingSupport(treeTable, parentPart));
		addTreeTableColumn(treeTable, treeColumnLayout, StringConstants.PA_COL_OUTPUT,
				parentPart instanceof TestCasePart ? 200 : 140, 0, new AstTreeLabelProvider(),
				new OutputColumnEditingSupport(treeTable, parentPart));
		addTreeTableColumn(treeTable, treeColumnLayout, StringConstants.PA_COL_DESCRIPTION,
				parentPart instanceof TestCasePart ? 400 : 240, 100, new AstTreeLabelProvider(),
				new DescriptionColumnEditingSupport(treeTable, parentPart));

		treeTable.setContentProvider(new AstTreeTableContentProvider());

		setTreeTableActivation();

		treeTable.getControl().addListener(SWT.MeasureItem, new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				// do nothing to prevent double click to expand tree items
			}
		});

		// Enable tool-tip support for treeTable
		childTableTree.setToolTipText(StringUtils.EMPTY);
		KeywordTreeViewerToolTipSupport.enableFor(treeTable);

		createContextMenu();
		addTreeTableKeyListener();
		if (parentPart instanceof TestCasePart) {
			hookDragEvent();
			hookDropEvent();
		}
	}

	private String createPath(String server, AnalyticsTeam team, AnalyticsProject project, String path, String tokenInfo) {
		String result = "";
		result = ApplicationInfo.getTestOpsServer() + AnalyticsStringConstants.ANALYTICS_API_FROM_KS + "teamId=" + team.getId() + "&projectId="
				+ project.getId() + "&type=TEST_CASE" + "&path=" + UrlEncoder.encode(path) + "&token=" + tokenInfo;
		return result;

	}

	private void addTreeTableColumn(TreeViewer parent, TreeColumnLayout treeColumnLayout, String headerText, int width,
			int weight, CellLabelProvider labelProvider, EditingSupport editingSupport) {
		TreeViewerColumn treeTableColumn = new TreeViewerColumn(parent, SWT.NONE);
		TreeColumn treeColumn = treeTableColumn.getColumn();
		treeColumn.setWidth(width);
		treeColumn.setMoveable(true);
		treeColumn.setText(headerText);
		treeTableColumn.setLabelProvider(labelProvider);
		treeTableColumn.setEditingSupport(editingSupport);
		treeColumnLayout.setColumnData(treeTableColumn.getColumn(),
				new ColumnWeightData(weight, treeColumn.getWidth()));
	}

	private void setTreeTableActivation() {
		int activationBitMask = ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
				| ColumnViewerEditor.KEYBOARD_ACTIVATION;
		FocusCellOwnerDrawForManualTestcase focusCellHighlighter = new FocusCellOwnerDrawForManualTestcase(treeTable);
		focusCellManager = new CustomTreeViewerFocusCellManager(treeTable, focusCellHighlighter);
		CustomEditorActivationStrategy editorActivationStrategy = new CustomEditorActivationStrategy(treeTable,
				focusCellHighlighter);
		TreeViewerEditor.create(treeTable, focusCellManager, editorActivationStrategy, activationBitMask);
	}

	private void createContextMenu() {
		childTableTree.addListener(SWT.MenuDetect, new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				Menu menu = childTableTree.getMenu();
				if (menu != null) {
					menu.dispose();
				}
				menu = new Menu(childTableTree);

				if (childTableTree.getSelectionCount() == 1) {
					TestCaseMenuUtil.generateExecuteFromTestStepSubMenu(menu, selectionListener);

					new MenuItem(menu, SWT.SEPARATOR);

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
						KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, "C" }))); //$NON-NLS-1$
				copyMenuItem.addSelectionListener(selectionListener);
				copyMenuItem.setID(TreeTableMenuItemConstants.COPY_MENU_ITEM_ID);

				MenuItem cutMenuItem = new MenuItem(menu, SWT.PUSH);
				cutMenuItem.setText(createMenuItemLabel(StringConstants.ADAP_MENU_CONTEXT_CUT,
						KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, "X" }))); //$NON-NLS-1$
				cutMenuItem.addSelectionListener(selectionListener);
				cutMenuItem.setID(TreeTableMenuItemConstants.CUT_MENU_ITEM_ID);

				MenuItem pasteMenuItem = new MenuItem(menu, SWT.PUSH);
				pasteMenuItem.setText(createMenuItemLabel(StringConstants.ADAP_MENU_CONTEXT_PASTE,
						KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, "V" }))); //$NON-NLS-1$
				pasteMenuItem.addSelectionListener(selectionListener);
				pasteMenuItem.setID(TreeTableMenuItemConstants.PASTE_MENU_ITEM_ID);

				new MenuItem(menu, SWT.SEPARATOR);

				addFailureHandlingSubMenu(menu);

				MenuItem disableMenuItem = new MenuItem(menu, SWT.PUSH);
				disableMenuItem.setText(createMenuItemLabel(StringConstants.ADAP_MENU_CONTEXT_DISABLE,
						KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.M1_NAME, "/" }))); //$NON-NLS-1$
				disableMenuItem.addSelectionListener(selectionListener);
				disableMenuItem.setID(TreeTableMenuItemConstants.DISABLE_MENU_ITEM_ID);

				MenuItem enableMenuItem = new MenuItem(menu, SWT.PUSH);
				enableMenuItem.setText(createMenuItemLabel(StringConstants.ADAP_MENU_CONTEXT_ENABLE,
						KeyEventUtil.geNativeKeyLabel(new String[] { IKeyLookup.ALT_NAME, IKeyLookup.M1_NAME, "/" }))); //$NON-NLS-1$
				enableMenuItem.addSelectionListener(selectionListener);
				enableMenuItem.setID(TreeTableMenuItemConstants.ENABLE_MENU_ITEM_ID);
				parentPart.createDynamicGotoMenu(menu);
				childTableTree.setMenu(menu);
			}
		});
	}

	private String createMenuItemLabel(String text, String keyCombination) {
		return text + "\t" + keyCombination; //$NON-NLS-1$
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

	/**
	 * Add KeyListener to TreeTable. Handle Tab, Shift Tab, Delete, Ctrl + c,
	 * Ctrl + x, Ctrl + v, Ctrl + /, Ctrl + Alt + / for test steps
	 */
	private void addTreeTableKeyListener() {
		treeTable.getControl().addKeyListener(new KeyAdapter() {
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
					}

					// Disable
					if (e.keyCode == '/') {
						treeTableInput.disable();
						return;
					}
				}
			}
		});

		childTableTree.addListener(SWT.Traverse, new Listener() {

			@Override
			public void handleEvent(Event event) {
				switch (event.detail) {
				case SWT.TRAVERSE_TAB_NEXT:
					focusCellManager.focusNextCell();
					focusTree();
					return;
				case SWT.TRAVERSE_TAB_PREVIOUS:
					focusCellManager.focusPreviousCell();
					focusTree();
					return;
				default:
					return;
				}
			}

			private void focusTree() {
				childTableTree.getDisplay().timerExec(0, new Runnable() {
					@Override
					public void run() {
						UISynchronizeService.syncExec(new Runnable() {
							@Override
							public void run() {
								childTableTree.setFocus();
							}
						});
					}
				});
			}
		});
	}

	public void removeTestStep() {
		treeTableInput.removeSelectedRows();
	}

	public void upStep() {
		treeTableInput.moveUp();
	}

	public void downStep() {
		treeTableInput.moveDown();
	}

	public void copyTestStep() {
		treeTableInput.copy(treeTableInput.getSelectedNodes());
	}

	public void cutTestStep() {
		treeTableInput.cut(treeTableInput.getSelectedNodes());
	}

	public void pasteTestStep() {
		treeTableInput.paste(treeTableInput.getSelectedNode(), NodeAddType.Add);
	}

	public void loadASTNodesToTreeTable(ScriptNodeWrapper scriptNode) {
		treeTableInput = new TestCaseTreeTableInput(scriptNode, treeTable, parentPart);
		treeTableInput.refresh();
	}

	public void addToAnExistingTestSuiteStep() {
		try {
			List<TestSuiteEntity> chosenTestSuites;

			chosenTestSuites = getSelectedTestSuitesOnDialog();
			if (chosenTestSuites.isEmpty()) {
				return;
			}

			TestCaseEntity testCase = parentPart.getTestCase();
			for (TestSuiteEntity selectedTestSuite : chosenTestSuites) {
				EventBrokerSingleton.getInstance().getEventBroker().send(EventConstants.TEST_SUITE_OPEN,
						selectedTestSuite);
				EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.ADD_TEST_CASE_FROM_TEST_CASE,
						new Object[] { testCase, selectedTestSuite.getId() });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addToANewTestSuiteStep() {
		EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.TEST_SUITE_NEW_FROM_TEST_CASE,
				parentPart.getTestCase());
	}

	private List<TestSuiteEntity> getSelectedTestSuitesOnDialog() throws Exception {
		EntityProvider entityProvider = new EntityProvider();
		TestSuiteSelectionDialog dialog = new TestSuiteSelectionDialog(Display.getCurrent().getActiveShell(),
				new EntityLabelProvider(), new EntityProvider(), new TestSuiteViewerFilter(entityProvider));
		ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
		dialog.setInput(
				TreeEntityUtil.getChildren(null, FolderController.getInstance().getTestSuiteRoot(currentProject)));
		if (dialog.open() != Dialog.OK) {
			return Collections.emptyList();
		}
		return flattenDialogResult(dialog.getResult());
	}

	private List<TestSuiteEntity> flattenDialogResult(Object[] dialogResult) throws Exception {
		if (dialogResult == null) {
			return Collections.emptyList();
		}

		List<TestSuiteEntity> selectedTestSuites = new ArrayList<>();
		for (Object eachResult : dialogResult) {
			if (eachResult instanceof TestSuiteTreeEntity) {
				selectedTestSuites.add((TestSuiteEntity) ((TestSuiteTreeEntity) eachResult).getObject());
			}

			if (!(eachResult instanceof FolderTreeEntity)) {
				continue;
			}
			selectedTestSuites.addAll(flattenDialogResult(((FolderTreeEntity) eachResult).getChildren()));
		}

		return selectedTestSuites;
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
					scriptSnippets.append("\n"); //$NON-NLS-1$
				}
				if (scriptSnippets.length() > 0) {
					ScriptTransferData transferData = new ScriptTransferData(scriptSnippets.toString(),
							parentPart.getTestCase().getId());
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
		dt.addDropListener(new TestStepTableDropListener(treeTable, parentPart));
	}

	public List<AstTreeTableNode> getDragNodes() {
		return dragNodes;
	}

	public TestCaseTreeTableInput getTreeTableInput() {
		return treeTableInput;
	}

	public TreeViewer getTreeTable() {
		return treeTable;
	}

	public void performToolItemSelected(ToolItem toolItem, SelectionEvent selectionEvent) {
		getTreeTable().applyEditorValue();
		if (toolItem.equals(tltmAddWSKeywordStep)) {
			addDefaultWebServiceKeyword();
			return;
		}
		if (toolItem.equals(tltmAddStep)) {
			openToolItemMenu(toolItem, selectionEvent);
			return;
		}
		if (toolItem.equals(tltmRemoveStep)) {
			removeTestStep();
			return;
		}
		if (toolItem.equals(tltmUp)) {
			upStep();
			return;
		}
		if (toolItem.equals(tltmDown)) {
			downStep();
		}
		if (toolItem.equals(tltmRecent)) {
			openRecentKeywordItems();
		}
		if (toolItem.equals(tltmAddTestSuite)) {
			openToolAddTestSuite(toolItem, selectionEvent);
			return;
		}
	}

	private void addDefaultWebServiceKeyword() {
		MethodCallExpressionWrapper sendRequestMethodCall = AstKeywordsInputUtil.generateBuiltInKeywordExpression("WS",
				"sendRequest", treeTableInput.getMainClassNode());
		treeTableInput.addNewAstObject(new ExpressionStatementWrapper(sendRequestMethodCall),
				treeTableInput.getSelectedNode(), NodeAddType.Add);
	}

	private void openRecentKeywordItems() {
		List<StoredKeyword> recentKeywords = TestCasePreferenceDefaultValueInitializer.getRecentKeywords()
		        .stream().filter(k -> k.getKeywordClass() != null || 
		        KeywordContributorCollection.getContributor(k.getKeywordClass()) != null).collect(Collectors.toList());
		if (recentKeywords.isEmpty()) {
			return;
		}
		if (recentMenu != null && !recentMenu.isDisposed()) {
			recentMenu.dispose();
		}
		recentMenu = new Menu(tltmRecent.getParent());
		recentKeywords.forEach(keyword -> {
			addRecentMenuItem(keyword);
		});
		Rectangle rect = tltmRecent.getBounds();
		Point pt = tltmRecent.getParent().toDisplay(new Point(rect.x, rect.y));
		recentMenu.setLocation(pt.x, pt.y + rect.height);
		recentMenu.setVisible(true);
	}

	private void addRecentMenuItem(StoredKeyword keyword) {
		MenuItem recentMenuItem = new MenuItem(recentMenu, SWT.PUSH);
		final DecoratedKeyword decoratedKeyword = KeywordDecorationService.getDecoratedKeyword(keyword);
		recentMenuItem.setText(decoratedKeyword.getLabel());
		recentMenuItem.setToolTipText(decoratedKeyword.getTooltip());
		recentMenuItem.setImage(decoratedKeyword.getImage());

		recentMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AstTreeTableNode destination = treeTableInput.getSelectedNode();
				treeTableInput.addNewAstObject(
						decoratedKeyword.newStep(treeTableInput.getParentNodeForNewMethodCall(destination)),
						destination, NodeAddType.Add);
			}
		});
	}

	private void registerEventBrokerListeners() {
		IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
		eventBroker.subscribe(EventConstants.TESTCASE_RECENT_KEYWORD_ADDED, new EventHandler() {

			@Override
			public void handleEvent(org.osgi.service.event.Event event) {
				if (compositeManual == null || compositeManual.isDisposed()) {
					return;
				}
				setRecentKeywordItemState();
			}
		});
	}

	private void setRecentKeywordItemState() {
		tltmRecent.setEnabled(!TestCasePreferenceDefaultValueInitializer.getRecentKeywords().isEmpty());
	}

	public void performMenuItemSelected(MenuItem menuItem) {
		getTreeTable().applyEditorValue();
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
		case TreeTableMenuItemConstants.EXECUTE_FROM_TEST_STEP_MENU_ITEM_ID:
			executeFromTestStep((ExecutionSession) menuItem.getData());
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
			getTreeTableInput().enable();
			break;
		case TreeTableMenuItemConstants.DISABLE_MENU_ITEM_ID:
			getTreeTableInput().disable();
			break;
		case TreeTableMenuItemConstants.ADD_TO_AN_EXISTING_TEST_SUITE_ID:
			addToAnExistingTestSuiteStep();
			break;
		case TreeTableMenuItemConstants.ADD_TO_A_NEW_TEST_SUITE_ID:
			addToANewTestSuiteStep();
			break;
		default:
			getTreeTableInput().addNewAstObject(menuItem.getID(), treeTableInput.getSelectedNode(), addType);
			break;
		}
	}

	public void addStepByActionID(int id) {
		getTreeTableInput().addNewAstObject(id, treeTableInput.getSelectedNode(), NodeAddType.Add);
	}

	public void addCallTestCaseStep(TestCaseEntity testCase) {
		if (!getTreeTableInput().validateTestCase(testCase)) {
			return;
		}
		getTreeTableInput().addCallTestCases(treeTableInput.getSelectedNode(), NodeAddType.Add,
				new TestCaseEntity[] { testCase });
	}

	private void executeFromTestStep(ExecutionSession executionSession) {
		String rawScript = getTreeTableInput().generateRawScriptFromSelectedStep();
		if (rawScript == null) {
			return;
		}
		ExecuteFromTestStepEntity executeFromTestStepEntity = new ExecuteFromTestStepEntity();
		executeFromTestStepEntity.setDriverTypeName(executionSession.getDriverTypeName());
		executeFromTestStepEntity.setRawScript(rawScript);
		executeFromTestStepEntity.setRemoteServerUrl(executionSession.getRemoteUrl());
		executeFromTestStepEntity.setTestCase(parentPart.getTestCase());
		executeFromTestStepEntity.setSessionId(executionSession.getSessionId());
		EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXECUTE_FROM_TEST_STEP,
				executeFromTestStepEntity);
	}

	private void openToolItemMenu(ToolItem toolItem, SelectionEvent selectionEvent) {
		if (selectionEvent.detail == SWT.ARROW && toolItem.getData() instanceof Menu) {
			Rectangle rect = toolItem.getBounds();
			Point pt = toolItem.getParent().toDisplay(new Point(rect.x, rect.y));
			Menu menu = (Menu) toolItem.getData();
			menu.setLocation(pt.x, pt.y + rect.height);
			menu.setVisible(true);
		} else {
			treeTableInput.addNewDefaultBuiltInKeyword(NodeAddType.Add);
		}
	}

	private void openToolAddTestSuite(ToolItem toolItem, SelectionEvent selectionEvent) {
		Rectangle rect = toolItem.getBounds();
		Point pt = toolItem.getParent().toDisplay(new Point(rect.x, rect.y));

		Menu menu = (Menu) toolItem.getData();
		menu.setLocation(pt.x, pt.y + rect.height);
		menu.setVisible(true);
	}

	private void changeKeywordFailureHandling(FailureHandling failureHandling) {
		treeTableInput.changeFailureHandling(failureHandling);
	}

	public IStructuredSelection getTreeTableSelection() {
		return (IStructuredSelection) treeTable.getSelection();
	}

}
