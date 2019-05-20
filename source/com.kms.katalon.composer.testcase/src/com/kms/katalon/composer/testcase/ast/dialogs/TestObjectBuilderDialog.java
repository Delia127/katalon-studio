package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.impl.control.Dropdown;
import com.kms.katalon.composer.components.impl.control.DropdownGroup;
import com.kms.katalon.composer.components.impl.control.ImageButton;
import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.components.util.ColumnViewerUtil;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MapEntryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MapExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.parts.ITestCasePart;
import com.kms.katalon.composer.testcase.parts.IVariablePart;
import com.kms.katalon.composer.testcase.parts.TestCaseVariableView;
import com.kms.katalon.composer.testcase.preferences.RecentObjectStorage;
import com.kms.katalon.composer.testcase.preferences.TestCasePreferenceDefaultValueInitializer;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstEntityInputUtil;
import com.kms.katalon.composer.testcase.util.AstInputValueTypeOptionsProvider;
import com.kms.katalon.constants.GlobalMessageConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.variable.VariableEntity;

public class TestObjectBuilderDialog extends TreeEntitySelectionDialog implements IAstDialogBuilder {
    private static final int TOOL_ITEM_SIZE = 50;

    private static final InputValueType[] defaultInputValueTypes = { InputValueType.Variable,
            InputValueType.GlobalVariable };

    private static final InputValueType[] defaultVariableInputValueTypes = AstInputValueTypeOptionsProvider
            .getInputValueTypeOptions(InputValueType.Map);

    private static final InputValueType[] webServiceRequestVariableInputValueTypes = { InputValueType.String,
            InputValueType.Number, InputValueType.Boolean, InputValueType.Null, InputValueType.Variable, InputValueType.GlobalVariable,
            InputValueType.TestDataValue, InputValueType.Property, InputValueType.List, InputValueType.Map };

    private static final String DEFAULT_VARIABLE_NAME = "variable";

    private static final String pluginId = FrameworkUtil.getBundle(TestObjectBuilderDialog.class).getSymbolicName();

    private static final String OBJECT_FINDER_TAB_NAME = TreeEntityUtil
            .getReadableKeywordName(InputValueType.TestObject.getName());

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

    private Composite webServiceVariablesComposite;

    private Composite testObjectVariablesComposite;

    private Combo combo;

    private Label lblVariables;

    private Composite compositeVariables, compositeVariablesDetails;

    private StackLayout compositeVariablesDetailsLayout;

    private ImageButton btnExpandVariablesComposite;

    private TableViewer variableTableViewer;

    private IVariablePart webServiceRequestVariablesPart;

    private TestCaseVariableView webServiceRequestVariablesView;

    private MapExpressionWrapper variableMaps = null;

    private WebElementEntity initialSelectedTestObject = null;

    private WebElementEntity selectedWebElement = null;

    private List<VariableEntity> initialRequestVariables = null;

    private List<MapEntryExpressionWrapper> initialVariableMapEntries = null;

    private boolean isVariablesCompositeExpanded = true;

    private Listener layoutExecutionCompositeListener = new Listener() {

        @Override
        public void handleEvent(org.eclipse.swt.widgets.Event event) {
            isVariablesCompositeExpanded = !isVariablesCompositeExpanded;
            layoutExecutionInfo();
        }
    };

    private ITestCasePart testCasePart;

    private ToolItem recentTestObjectItem;

    public TestObjectBuilderDialog(Shell parentShell, ExpressionWrapper objectExpressionWrapper,
            boolean haveOtherTypes) {
        super(parentShell, new EntityLabelProvider(), new EntityProvider(),
                new EntityViewerFilter(new EntityProvider()));
        _instance = this;
        this.haveOtherTypes = haveOtherTypes;
        this.objectExpressionWrapper = objectExpressionWrapper.clone();
        initVariableMap();
        setAllowMultiple(false);
        setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | getDefaultOrientation());
        try {
            setInput(TreeEntityUtil.getChildren(null, FolderController.getInstance()
                    .getObjectRepositoryRoot(ProjectController.getInstance().getCurrentProject())));
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        refreshSelectionTree();

        if (initialSelectedTestObject instanceof WebServiceRequestEntity) {
            List<MapEntryExpressionWrapper> variables = variableMaps.getMapEntryExpressions();
            initialRequestVariables = variables.stream().map(variable -> {
                VariableEntity variableEntity = new VariableEntity();
                String variableName = variable.getKeyExpression().getText();
                variableName = variableName.replaceAll("^\"|\"$", ""); // remove redundant double quotes in variable name
                variableEntity.setName(variableName);
                String variableValue = variable.getValueExpression().getText();
                variableEntity.setDefaultValue(variableValue);
                return variableEntity;
            }).collect(Collectors.toList());
        } else {
            initialVariableMapEntries = new ArrayList<>(variableMaps != null ? variableMaps.getMapEntryExpressions() : new ArrayList<>());
        }
    }

    public void setTestCasePart(ITestCasePart testCasePart) {
        this.testCasePart = testCasePart;
    }

    public ITestCasePart getTestCasePart() {
        return testCasePart;
    }

    private void initVariableMap() {
        if (!isObjectExpressionFindTestObjectMethodCall()) {
            return;
        }
        MethodCallExpressionWrapper methodCallObjectExpression = (MethodCallExpressionWrapper) objectExpressionWrapper;
        if (methodCallObjectExpression.getArguments().getExpressions().size() <= 1) {
            variableMaps = new MapExpressionWrapper(methodCallObjectExpression.getArguments());
            return;
        }
        ExpressionWrapper secondParam = methodCallObjectExpression.getArguments().getExpressions().get(1);
        if (!(secondParam instanceof MapExpressionWrapper)) {
            variableMaps = new MapExpressionWrapper(methodCallObjectExpression.getArguments());
            return;
        }
        variableMaps = (MapExpressionWrapper) secondParam;
    }

    protected void refreshSelectionTree() {
        String testObjectId = null;
        if (isObjectExpressionFindTestObjectMethodCall()) {
            testObjectId = AstEntityInputUtil.findTestObjectIdFromFindTestObjectMethodCall(
                    (MethodCallExpressionWrapper) objectExpressionWrapper);
        }
        if (testObjectId == null) {
            return;
        }
        WebElementEntity selectedWebElement = null;
        FolderEntity objectRepositoryRoot = null;
        try {
            selectedWebElement = ObjectRepositoryController.getInstance().getWebElementByDisplayPk(testObjectId);
            objectRepositoryRoot = FolderController.getInstance()
                    .getObjectRepositoryRoot(ProjectController.getInstance().getCurrentProject());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        if (selectedWebElement == null || objectRepositoryRoot == null) {
            return;
        }
        setInitialSelection(new WebElementTreeEntity(selectedWebElement,
                createSelectedTreeEntityHierachy(selectedWebElement.getParentFolder(), objectRepositoryRoot)));

        initialSelectedTestObject = selectedWebElement;
    }

    private void createTopCompiste(Composite parent) {
        Composite topComposite = new Composite(parent, SWT.NONE);
        topComposite.setLayout(new GridLayout(2, false));
        topComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        comboComposite = new Composite(topComposite, SWT.NONE);
        comboComposite.setLayout(new GridLayout(2, false));
        Label comboLabel = new Label(comboComposite, SWT.NONE);
        comboLabel.setText(StringConstants.DIA_LBL_OBJ_TYPE);

        combo = new Combo(comboComposite, SWT.DROP_DOWN);
        combo.setItems(TEST_OBJECT_TABS);

        comboComposite.setVisible(haveOtherTypes);

        final ToolBar topToolbar = new ToolBar(topComposite, SWT.FLAT | SWT.RIGHT);
        topToolbar.setForeground(ColorUtil.getToolBarForegroundColor());

        topToolbar.setTextDirection(SWT.LEFT_TO_RIGHT);
        topToolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(true, false).applyTo(topToolbar);
        recentTestObjectItem = new ToolItem(topToolbar, SWT.DROP_DOWN);
        recentTestObjectItem.setText(GlobalMessageConstants.RECENT);
        recentTestObjectItem.setImage(ImageConstants.IMG_16_RECENT);

        recentTestObjectDropdown = new Dropdown(getShell());
        createRecentDropdownItem();
        recentTestObjectDropdown.resizeToFitContent();
        recentTestObjectDropdown.setVisible(false);

        recentTestObjectItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Rectangle bounds = recentTestObjectItem.getBounds();
                Point recentTestObjectItemLocation = topToolbar.toDisplay(new Point(bounds.x, bounds.y));
                recentTestObjectDropdown.setLocation(recentTestObjectItemLocation.x,
                        recentTestObjectItemLocation.y + bounds.height);
                recentTestObjectDropdown.setVisible(true);
            }
        });

        setTestObjectItemState();
    }

    private void createRecentDropdownItem() {
        RecentObjectStorage recentObjectStorage = getRecentObjectStorage();
        List<String> recentFolderIds = recentObjectStorage.getRecentFolderIds();
        if (!recentFolderIds.isEmpty()) {
            DropdownGroup recentFolderDropdownGrp = recentTestObjectDropdown
                    .addDropdownGroupItem(GlobalMessageConstants.OBJECT_FOLDER, ImageConstants.IMG_16_FOLDER);
            recentFolderIds.forEach(recentFolderId -> {
                createRecentFolderItem(recentFolderDropdownGrp, recentFolderId);
            });
        }

        List<String> recentObjectIds = recentObjectStorage.getRecentObjectIds();
        if (!recentObjectIds.isEmpty()) {
            DropdownGroup recentObjectDropdownGrp = recentTestObjectDropdown
                    .addDropdownGroupItem(GlobalMessageConstants.TEST_OBJECT, ImageConstants.IMG_16_TEST_OBJECT);
            recentObjectIds.forEach(recentTestObjectId -> {
                createRecentTestObjectItem(recentObjectDropdownGrp, recentTestObjectId);
            });
        }
    }

    private String getToolItemName(String rawName) {
        return StringUtils.substring(rawName, 0, TOOL_ITEM_SIZE);
    }

    private void createRecentTestObjectItem(DropdownGroup recentObjectDropdownGrp, String recentTestObjectId) {
        try {
            WebElementEntity testObject = ObjectRepositoryController.getInstance()
                    .getWebElementByDisplayPk(recentTestObjectId);

            WebElementTreeEntity webElementTreeEntity = TreeEntityUtil.getWebElementTreeEntity(testObject,
                    getCurrentProject());

            ToolItem toolItem = recentObjectDropdownGrp.addItem(getToolItemName(testObject.getName()),
                    webElementTreeEntity.getImage(), new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            try {
                                getTreeViewer().setSelection(new StructuredSelection(webElementTreeEntity));
                            } catch (Exception ignored) {}
                        }
                    });
            toolItem.setToolTipText(recentTestObjectId);
        } catch (Exception ignored) {}
    }

    private void createRecentFolderItem(DropdownGroup recentFolderDropdownGrp, String recentFolderId) {
        try {
            FolderEntity folder = FolderController.getInstance().getFolderByDisplayId(getCurrentProject(),
                    recentFolderId);

            final FolderTreeEntity folderTreeEntity = TreeEntityUtil.getWebElementFolderTreeEntity(folder,
                    getCurrentProject());

            ToolItem toolItem = recentFolderDropdownGrp.addItem(getToolItemName(folder.getName()),
                    folderTreeEntity.getImage(), new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            try {
                                TreeViewer treeViewer = getTreeViewer();
                                treeViewer.setSelection(new StructuredSelection(folderTreeEntity));
                                treeViewer.setExpandedState(folderTreeEntity, true);
                            } catch (Exception ignored) {}
                        }
                    });
            toolItem.setToolTipText(recentFolderId);
        } catch (Exception ignored) {}
    }

    private Dropdown recentTestObjectDropdown;

    private void setTestObjectItemState() {
        recentTestObjectItem.setEnabled(!getRecentObjectStorage().isEmpty());
    }

    private RecentObjectStorage getRecentObjectStorage() {
        return TestCasePreferenceDefaultValueInitializer.getRecentObjectStorage(getCurrentProject());
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));

        createTopCompiste(container);

        final Composite stackComposite = new Composite(container, SWT.NONE);
        applyDialogFont(stackComposite);
        stackLayout = new StackLayout();
        stackComposite.setLayout(stackLayout);
        stackComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

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

        createVariableComposite();

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

        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (!(event.getSelection() instanceof TreeSelection)) {
                    return;
                }

                TreeSelection treeSelection = (TreeSelection) event.getSelection();
                Object selection = treeSelection.getFirstElement();
                if (!(selection instanceof ITreeEntity)) {
                    return;
                }

                if (!(selection instanceof WebElementTreeEntity)) {
                    selectedWebElement = null;
                    compositeVariablesDetailsLayout.topControl = null;
                } else {
                    WebElementEntity webElement;
                    try {
                        webElement = (WebElementEntity) ((WebElementTreeEntity) selection).getObject();
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
                        return;
                    }

                    WebElementEntity previousSelectedElement = selectedWebElement;
                    selectedWebElement = webElement;
                    if (previousSelectedElement == selectedWebElement) {
                        return;
                    }
                    changeVariablesCompositeForSelectedEntity((WebElementTreeEntity) selection);
                }
            }
        });

        refresh();
        return container;
    }

    private void createVariableComposite() {
        compositeVariables = new Composite(objectFinderComposite, SWT.NONE);
        compositeVariables.setBackground(ColorUtil.getCompositeBackgroundColor());
        GridLayout glCompositeExecution = new GridLayout(1, true);
        glCompositeExecution.verticalSpacing = 0;
        glCompositeExecution.horizontalSpacing = 0;
        glCompositeExecution.marginHeight = 0;
        glCompositeExecution.marginWidth = 0;
        compositeVariables.setLayout(glCompositeExecution);
        compositeVariables.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Composite compositeExecutionCompositeHeader = new Composite(compositeVariables, SWT.NONE);
        compositeExecutionCompositeHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glCompositeExecutionCompositeHeader = new GridLayout(3, false);
        glCompositeExecutionCompositeHeader.marginHeight = 0;
        glCompositeExecutionCompositeHeader.marginWidth = 0;
        compositeExecutionCompositeHeader.setLayout(glCompositeExecutionCompositeHeader);
        compositeExecutionCompositeHeader
                .setCursor(compositeExecutionCompositeHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));
        compositeExecutionCompositeHeader
                .setCursor(compositeExecutionCompositeHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        btnExpandVariablesComposite = new ImageButton(compositeExecutionCompositeHeader, SWT.NONE);
        redrawBtnExpandExecutionInfo();

        lblVariables = new Label(compositeExecutionCompositeHeader, SWT.NONE);
        lblVariables.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        lblVariables.setFont(JFaceResources.getFontRegistry().getBold(""));
        lblVariables.setText(ComposerTestcaseMessageConstants.OBJECT_VARIABLE_LABEL);

        lblVariables.addListener(SWT.MouseDown, layoutExecutionCompositeListener);

        btnExpandVariablesComposite.addListener(SWT.MouseDown, layoutExecutionCompositeListener);

        compositeVariablesDetails = new Composite(compositeVariables, SWT.NONE);
        compositeVariablesDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        compositeVariablesDetailsLayout = new StackLayout();
        compositeVariablesDetails.setLayout(compositeVariablesDetailsLayout);

        testObjectVariablesComposite = createTestObjectVariablesComposite(compositeVariablesDetails);

        webServiceVariablesComposite = createWebServiceObjectVariablesComposite(compositeVariablesDetails);
    }

    private void changeVariablesCompositeForSelectedEntity(WebElementTreeEntity treeEntity) {
        WebElementEntity entity;
        try {
            entity = (WebElementEntity) treeEntity.getObject();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return;
        }

        if (entity instanceof WebServiceRequestEntity) {
            changeVariablesCompositeForWebServiceObject((WebServiceRequestEntity) entity);
        } else {
            changeVariablesCompositeForTestObject(entity);
        }
        compositeVariablesDetails.getParent().layout(true, true);
    }

    private void changeVariablesCompositeForWebServiceObject(WebServiceRequestEntity requestEntity) {
        List<VariableEntity> variables = new ArrayList<>();
        if (initialSelectedTestObject == requestEntity && !initialRequestVariables.isEmpty()) {
            variables = initialRequestVariables.stream().map(VariableEntity::clone).collect(Collectors.toList());
        } else {
            variables = requestEntity.getVariables().stream().map(VariableEntity::clone).collect(Collectors.toList());
        }

        webServiceRequestVariablesPart.deleteVariables(Arrays.asList(webServiceRequestVariablesPart.getVariables()));
        webServiceRequestVariablesPart.addVariables(variables.toArray(new VariableEntity[variables.size()]));

        compositeVariablesDetailsLayout.topControl = webServiceVariablesComposite;
    }

    private void changeVariablesCompositeForTestObject(WebElementEntity entity) {
        variableMaps.clearExpressions();
        if (initialSelectedTestObject == entity) {
            List<MapEntryExpressionWrapper> variableMapEntries = initialVariableMapEntries.stream()
                    .map(MapEntryExpressionWrapper::clone)
                    .collect(Collectors.toList());
            variableMaps.addExpressions(variableMapEntries);
        }
        compositeVariablesDetailsLayout.topControl = testObjectVariablesComposite;
        variableTableViewer.refresh();
    }

    private Composite createWebServiceObjectVariablesComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        composite.setLayout(gridLayout);

        webServiceRequestVariablesPart = new IVariablePart() {

            @Override
            public void setDirty(boolean isDirty) {
            	
            }

            @Override
            public void addVariables(VariableEntity[] variables) {
                webServiceRequestVariablesView.addVariable(variables);
            }

            @Override
            public VariableEntity[] getVariables() {
                return webServiceRequestVariablesView.getVariables();
            }

            @Override
            public void deleteVariables(List<VariableEntity> variableList) {
                webServiceRequestVariablesView.deleteVariables(variableList);
            }

        };

        webServiceRequestVariablesView = new TestCaseVariableView(webServiceRequestVariablesPart);
        webServiceRequestVariablesView.setTestCasePart(testCasePart);
        webServiceRequestVariablesView.setInputValueTypes(webServiceRequestVariableInputValueTypes);
        webServiceRequestVariablesView.createComponents(composite);

        // hide "Masked" column
        TableColumn[] tableColumns = webServiceRequestVariablesView.getTableViewer().getTable().getColumns();
        for (TableColumn tableColumn : tableColumns) {
            if (ComposerTestcaseMessageConstants.PA_COL_MASKED.equals(tableColumn.getText())) {
                tableColumn.setWidth(0);
                tableColumn.setResizable(false);
            }
        }

        return composite;
    }

    private Composite createTestObjectVariablesComposite(Composite parent) {
        Composite composite = new Composite(compositeVariablesDetails, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        composite.setLayout(new GridLayout(1, false));

        ToolBar toolBar = new ToolBar(composite, SWT.FLAT | SWT.RIGHT);
        toolBar.setForeground(ColorUtil.getToolBarForegroundColor());

        ToolItem tltmAddVariable = new ToolItem(toolBar, SWT.NONE);
        tltmAddVariable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addVariable();
            }
        });
        tltmAddVariable.setText(StringConstants.PA_BTN_TIP_ADD);
        tltmAddVariable.setImage(ImageConstants.IMG_16_ADD);

        final ToolItem tltmRemove = new ToolItem(toolBar, SWT.NONE);
        tltmRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                removeVariables();
            }
        });
        tltmRemove.setText(StringConstants.PA_BTN_TIP_REMOVE);
        tltmRemove.setImage(ImageConstants.IMG_16_REMOVE);
        tltmRemove.setEnabled(false);

        ToolItem tltmClear = new ToolItem(toolBar, SWT.NONE);
        tltmClear.setText(StringConstants.PA_BTN_TIP_CLEAR);
        tltmClear.setImage(ImageConstants.IMG_16_CLEAR);

        tltmClear.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                clearVariables();
            }
        });

        variableTableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        ColumnViewerUtil.setTableActivation(variableTableViewer);

        variableTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                // Should disable Delete button if there is no selection
                tltmRemove.setEnabled(!variableTableViewer.getSelection().isEmpty());
            }
        });

        Table table = variableTableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));
        GridData variableTableLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        variableTableLayoutData.minimumHeight = 100;
        table.setLayoutData(variableTableLayoutData);

        TableViewerColumn tableViewerColumnParamType = new TableViewerColumn(variableTableViewer, SWT.NONE);
        tableViewerColumnParamType.setEditingSupport(
                new AstInputBuilderValueTypeColumnSupport(variableTableViewer, defaultVariableInputValueTypes) {
                    @Override
                    protected void setValue(Object element, Object value) {
                        super.setValue(((MapEntryExpressionWrapper) element).getKeyExpression(), value);
                    }

                    @Override
                    protected Object getValue(Object element) {
                        return super.getValue(((MapEntryExpressionWrapper) element).getKeyExpression());
                    }

                    @Override
                    protected boolean canEdit(Object element) {
                        if (element instanceof MapEntryExpressionWrapper) {
                            return super.canEdit(((MapEntryExpressionWrapper) element).getKeyExpression());
                        }
                        return false;
                    }

                    @Override
                    protected CellEditor getCellEditor(Object element) {
                        return super.getCellEditor(((MapEntryExpressionWrapper) element).getKeyExpression());
                    }
                });
        TableColumn tblclmnParamType = tableViewerColumnParamType.getColumn();
        tblclmnParamType.setWidth(100);
        tblclmnParamType.setText(ComposerTestcaseMessageConstants.OBJECT_VARIABLE_TABLE_COL_PARAM_TYPE);
        tableViewerColumnParamType.setLabelProvider(new AstInputTypeLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof MapEntryExpressionWrapper) {
                    return super.getText(((MapEntryExpressionWrapper) element).getKeyExpression());
                }
                return StringUtils.EMPTY;
            }
        });

        TableViewerColumn tableViewerColumnName = new TableViewerColumn(variableTableViewer, SWT.NONE);
        tableViewerColumnName.setEditingSupport(new AstInputBuilderValueColumnSupport(variableTableViewer) {
            @Override
            protected boolean canEdit(Object element) {
                if (element instanceof MapEntryExpressionWrapper) {
                    return super.canEdit(((MapEntryExpressionWrapper) element).getKeyExpression());
                }
                return false;
            }

            @Override
            protected void setValue(Object element, Object value) {
                super.setValue(((MapEntryExpressionWrapper) element).getKeyExpression(), value);
            }

            @Override
            protected Object getValue(Object element) {
                return super.getValue(((MapEntryExpressionWrapper) element).getKeyExpression());
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return super.getCellEditor(((MapEntryExpressionWrapper) element).getKeyExpression());
            }
        });
        tableViewerColumnName.setLabelProvider(new AstInputValueLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof MapEntryExpressionWrapper) {
                    return super.getText(((MapEntryExpressionWrapper) element).getKeyExpression());
                }
                return StringUtils.EMPTY;
            }
        });
        TableColumn tblclmnName = tableViewerColumnName.getColumn();
        tblclmnName.setWidth(100);
        tblclmnName.setText(ComposerTestcaseMessageConstants.OBJECT_VARIABLE_TABLE_COL_PARAM);

        TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(variableTableViewer, SWT.NONE);
        tableViewerColumnValueType.setEditingSupport(
                new AstInputBuilderValueTypeColumnSupport(variableTableViewer, defaultVariableInputValueTypes) {
                    @Override
                    protected void setValue(Object element, Object value) {
                        super.setValue(((MapEntryExpressionWrapper) element).getValueExpression(), value);
                    }

                    @Override
                    protected Object getValue(Object element) {
                        return super.getValue(((MapEntryExpressionWrapper) element).getValueExpression());
                    }

                    @Override
                    protected boolean canEdit(Object element) {
                        if (element instanceof MapEntryExpressionWrapper) {
                            return super.canEdit(((MapEntryExpressionWrapper) element).getValueExpression());
                        }
                        return false;
                    }

                    @Override
                    protected CellEditor getCellEditor(Object element) {
                        return super.getCellEditor(((MapEntryExpressionWrapper) element).getValueExpression());
                    }
                });
        TableColumn tblclmnValueType = tableViewerColumnValueType.getColumn();
        tblclmnValueType.setWidth(100);
        tblclmnValueType.setText(ComposerTestcaseMessageConstants.OBJECT_VARIABLE_TABLE_COL_VALUE_TYPE);
        tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof MapEntryExpressionWrapper) {
                    return super.getText(((MapEntryExpressionWrapper) element).getValueExpression());
                }
                return StringUtils.EMPTY;
            }
        });

        TableViewerColumn tableViewerColumnValue = new TableViewerColumn(variableTableViewer, SWT.NONE);
        tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(variableTableViewer) {
            @Override
            protected void setValue(Object element, Object value) {
                super.setValue(((MapEntryExpressionWrapper) element).getValueExpression(), value);
            }

            @Override
            protected Object getValue(Object element) {
                return super.getValue(((MapEntryExpressionWrapper) element).getValueExpression());
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element instanceof MapEntryExpressionWrapper) {
                    return super.canEdit(((MapEntryExpressionWrapper) element).getValueExpression());
                }
                return false;
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return super.getCellEditor(((MapEntryExpressionWrapper) element).getValueExpression());
            }
        });
        TableColumn tblclmnValue = tableViewerColumnValue.getColumn();
        tblclmnValue.setWidth(200);
        tblclmnValue.setText(ComposerTestcaseMessageConstants.OBJECT_VARIABLE_TABLE_COL_VALUE);
        tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof MapEntryExpressionWrapper) {
                    return super.getText(((MapEntryExpressionWrapper) element).getValueExpression());
                }
                return StringUtils.EMPTY;
            }
        });

        variableTableViewer.setContentProvider(new ArrayContentProvider());
        variableTableViewer
                .setInput(variableMaps != null ? variableMaps.getMapEntryExpressions() : Collections.emptyMap());

        return composite;
    }

    private void layoutExecutionInfo() {
        Display.getDefault().timerExec(10, new Runnable() {
            @Override
            public void run() {
                compositeVariablesDetails.setVisible(isVariablesCompositeExpanded);
                if (!isVariablesCompositeExpanded) {
                    ((GridData) compositeVariablesDetails.getLayoutData()).exclude = true;
                } else {
                    ((GridData) compositeVariablesDetails.getLayoutData()).exclude = false;
                }
                compositeVariables.layout(true, true);
                compositeVariables.getParent().layout();
                redrawBtnExpandExecutionInfo();
                showSelectedTestObject();
            }

            private void showSelectedTestObject() {
                IStructuredSelection selection = getTreeViewer().getStructuredSelection();
                if (selection != null) {
                    getTreeViewer().getTree().showSelection();
                }
            }
        });
    }

    private void redrawBtnExpandExecutionInfo() {
        btnExpandVariablesComposite.getParent().setRedraw(false);
        if (isVariablesCompositeExpanded) {
            btnExpandVariablesComposite.setImage(ImageConstants.IMG_16_ARROW_DOWN);
        } else {
            btnExpandVariablesComposite.setImage(ImageConstants.IMG_16_ARROW);
        }
        btnExpandVariablesComposite.getParent().setRedraw(true);
    }

    private void clearVariables() {
        variableMaps.clearExpressions();
        variableTableViewer.refresh();
    }

    private void removeVariables() {
        StructuredSelection selection = (StructuredSelection) variableTableViewer.getSelection();
        Object[] selectionElements = selection.toArray();
        if (selectionElements.length == 0) {
            return;
        }
        for (Object object : selectionElements) {
            if (object instanceof MapEntryExpressionWrapper) {
                variableMaps.removeExpression((MapEntryExpressionWrapper) object);
            }
        }
        variableTableViewer.refresh();
    }

    private void addVariable() {
        MapEntryExpressionWrapper newMapEntry = new MapEntryExpressionWrapper(variableMaps);
        newMapEntry.setKeyExpression(new ConstantExpressionWrapper(generateNewPropertyName(), newMapEntry));
        newMapEntry.setValueExpression(new ConstantExpressionWrapper("", newMapEntry));
        variableMaps.addExpression(newMapEntry);
        variableTableViewer.refresh();
    }

    private String generateNewPropertyName() {
        String name = DEFAULT_VARIABLE_NAME;
        int index = 0;
        boolean isUnique = false;
        String newName = name;
        while (!isUnique) {
            isUnique = true;
            for (MapEntryExpressionWrapper mapEntry : variableMaps.getMapEntryExpressions()) {
                if (!(mapEntry.getKeyExpression() instanceof ConstantExpressionWrapper)) {
                    continue;
                }
                if (newName.equals(((ConstantExpressionWrapper) mapEntry.getKeyExpression()).getValue())) {
                    isUnique = false;
                    break;
                }
            }
            if (isUnique) {
                return newName;
            }
            newName = name + "_" + index;
            index++;
        }
        return newName;
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
        table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));
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
                    newExpression = (ExpressionWrapper) InputValueType.TestObject
                            .getNewValue(objectExpressionWrapper.getParent());
                    stackLayout.topControl = objectFinderComposite;
                } else {
                    newExpression = (ExpressionWrapper) defaultInputValueTypes[0]
                            .getNewValue(objectExpressionWrapper.getParent());
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

        tableViewerColumnValueType
                .setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer, defaultInputValueTypes) {
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

    private static FolderTreeEntity createSelectedTreeEntityHierachy(FolderEntity folderEntity,
            FolderEntity rootFolder) {
        if (folderEntity == null || folderEntity.equals(rootFolder)) {
            return null;
        }
        return new FolderTreeEntity(folderEntity,
                createSelectedTreeEntityHierachy(folderEntity.getParentFolder(), rootFolder));
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
                okPressed();
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

        MethodCallExpressionWrapper newMethodCall;
        try {

            if ((WebElementEntity) webElementTreeEntity.getObject() instanceof WebServiceRequestEntity) {
                newMethodCall = convertRequestObjectSelectionToObjectMethodCall(
                        (WebServiceRequestEntity) webElementTreeEntity.getObject());
            } else {
                newMethodCall = AstEntityInputUtil.createNewFindTestObjectMethodCall(objectPk,
                        objectExpressionWrapper.getParent());
                newMethodCall.copyProperties(objectExpressionWrapper);
                if (variableMaps != null && variableMaps.getMapEntryExpressions().size() > 0) {
                    newMethodCall.getArguments().addExpression(variableMaps);
                }
            }

            objectExpressionWrapper = newMethodCall;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private MethodCallExpressionWrapper convertRequestObjectSelectionToObjectMethodCall(
            WebServiceRequestEntity requestEntity) {

        MapExpressionWrapper variableMapExpression;

        MethodCallExpressionWrapper methodCallExpression = (MethodCallExpressionWrapper) objectExpressionWrapper;
        if (methodCallExpression.getArguments().getExpressions().size() <= 1) {
            variableMapExpression = new MapExpressionWrapper(methodCallExpression.getArguments());
        } else {
            ExpressionWrapper secondParam = methodCallExpression.getArguments().getExpressions().get(1);
            if (!(secondParam instanceof MapExpressionWrapper)) {
                variableMapExpression = new MapExpressionWrapper(methodCallExpression.getArguments());
            } else {
                variableMapExpression = (MapExpressionWrapper) secondParam;
            }
        }

        MethodCallExpressionWrapper newMethodCallExpression = AstEntityInputUtil.createNewFindTestObjectMethodCall(
                requestEntity.getIdForDisplay(), objectExpressionWrapper.getParent());
        newMethodCallExpression.copyProperties(objectExpressionWrapper);

        variableMapExpression.clearExpressions();

        VariableEntity[] requestVariables = webServiceRequestVariablesPart.getVariables();
        for (VariableEntity variable : requestVariables) {
            MapEntryExpressionWrapper newMapEntry = new MapEntryExpressionWrapper(variableMapExpression);
            newMapEntry.setKeyExpression(new ConstantExpressionWrapper(variable.getName(), newMapEntry));

            ExpressionWrapper valueExpression = GroovyWrapperParser
                    .parseGroovyScriptAndGetFirstExpression(variable.getDefaultValue());
            newMapEntry.setValueExpression(valueExpression);
            variableMapExpression.addExpression(newMapEntry);
        }

        if (variableMapExpression.getMapEntryExpressions().size() > 0) {
            newMethodCallExpression.getArguments().addExpression(variableMapExpression);
        }

        return newMethodCallExpression;
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

    private ProjectEntity getCurrentProject() {
        return ProjectController.getInstance().getCurrentProject();
    }

    protected void okPressed() {
        try {
            super.okPressed();
        } finally {
            try {
                TestCasePreferenceDefaultValueInitializer.addRecentObject(
                        ProjectController.getInstance().getCurrentProject(),
                        (WebElementEntity) ((WebElementTreeEntity) getFirstResult()).getObject());
            } catch (Exception ignored) {}
        }
    };
}
