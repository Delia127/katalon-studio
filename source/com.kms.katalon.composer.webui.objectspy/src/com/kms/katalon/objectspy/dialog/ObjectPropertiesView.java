package com.kms.katalon.objectspy.dialog;

import static com.kms.katalon.composer.components.impl.util.ControlUtils.isReady;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.dialogs.AddTestObjectPropertyDialog;
import com.kms.katalon.composer.components.impl.editors.StringComboBoxCellEditor;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebElementXpathEntity;
import com.kms.katalon.objectspy.constants.ImageConstants;
import com.kms.katalon.objectspy.constants.ObjectspyMessageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebPage;
import com.kms.katalon.util.listener.EventListener;
import com.kms.katalon.util.listener.EventManager;

public class ObjectPropertiesView extends Composite
        implements EventListener<ObjectSpyEvent>, EventManager<ObjectSpyEvent> {

    private static final String WARN_MSG_OBJECT_PROPERTY_NAME_IS_EXISTED = ObjectspyMessageConstants.WARN_MSG_OBJECT_PROPERTY_NAME_IS_EXISTED;

    private static final String LBL_OBJECT_SELECTION_METHOD = ObjectspyMessageConstants.DIA_LBL_OBJECT_SELECTION_METHOD;

    private static final String RADIO_LABEL_CSS = ObjectspyMessageConstants.DIA_RADIO_LABEL_CSS;

    private static final String RADIO_LABEL_XPATH = ObjectspyMessageConstants.DIA_RADIO_LABEL_XPATH;

    private static final String RADIO_LABEL_ATTRIBUTES = ObjectspyMessageConstants.DIA_RADIO_LABEL_ATTRIBUTES;

    private static final String COL_LABEL_CONDITION = ObjectspyMessageConstants.DIA_COL_LABEL_CONDITION;

    private Table tProperty, tXpath;

    private TableViewer tvProperty, tvXpath;

    private TableViewerColumn cvProperty, cvCondition, cvValue, cvSelected;
    
    private TableViewerColumn cvXpathName, cvXpathValue;

    private TableColumn cName, cCondition, cValue, cSelected;
    
    private TableColumn cXpathName, cXpathValue;

    private Text txtName;

    private Button radioAttributes, radioXpath, radioCss;

    private ToolItem btnAdd, btnDelete, btnClear;

    private Label lblHelp;

    private Composite radioBtnComposite;
    
    private Composite compositeAttributeToolbar;

    private ToolBar toolbar;

    private WebElement webElement;

    private Runnable refreshTreeRunnable;

    private Shell shell;

    private Map<SelectorMethod, Button> selectorButtons = new HashMap<>();

    private Composite tableAndButtonsComposite;
    
    private Composite xpathTableComposite, propertyTableComposite;

    private int lastHeight = -1;

    private Map<ObjectSpyEvent, Set<EventListener<ObjectSpyEvent>>> eventListeners = new HashMap<>();

    public ObjectPropertiesView(Composite parent, int style) {
        super(parent, style);

        this.shell = parent.getShell();

        setLayoutAndLayoutData();

        createHeaderLabel();

        createObjectFields();

        tableAndButtonsComposite = new Composite(this, SWT.NONE);
        tableAndButtonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout ldTableAndButtons = new GridLayout(1, false);
        ldTableAndButtons.marginWidth = 0;
        ldTableAndButtons.marginHeight = 0;
        tableAndButtonsComposite.setLayout(ldTableAndButtons);

        createAttributeToolbarButtons(tableAndButtonsComposite);

        createPropertyTable(tableAndButtonsComposite);
        
        createXpathTable(tableAndButtonsComposite);        
        
        showComposite(propertyTableComposite, false);
        
        showComposite(xpathTableComposite, true);
        
        showComposite(compositeAttributeToolbar, false);

        addControlListeners();

        enableControls();
    }
    
    
    @SuppressWarnings("unused")
	private void displayPropertiesTableComposite(boolean visible) {
        GridData gdPropertiesComposite = (GridData) tableAndButtonsComposite.getLayoutData();
        gdPropertiesComposite.exclude = !visible;
        gdPropertiesComposite.heightHint = lastHeight;
        if (!visible) {
            lastHeight = tableAndButtonsComposite.getBounds().height;
        }
        tableAndButtonsComposite.setVisible(visible);
        tableAndButtonsComposite.pack();

        GridData gdView = new GridData(SWT.FILL, SWT.FILL, true, true);
        if (!visible) {
            gdView = new GridData(SWT.FILL, SWT.TOP, true, false);
        }
        this.setLayoutData(gdView);
        tableAndButtonsComposite.getParent().getParent().layout(true, true);

        invoke(ObjectSpyEvent.REQUEST_DIALOG_RESIZE, null);
    }

    public WebElement getWebElement() {
        return webElement;
    }

    private void setWebElement(WebElement webElement) {
        this.webElement = webElement;
        enableControls();
        txtName.setText(webElement != null ? webElement.getName() : StringUtils.EMPTY);
        populateSelectionMethod();
        updateWebObjectProperties();
        updateWebObjectXpaths();
        sendPropertiesChangedEvent();
    }

    private void setLayoutAndLayoutData() {
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        setLayout(gridLayout);
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    private void createHeaderLabel() {
        Label lblObjectsProperties = new Label(this, SWT.NONE);
        lblObjectsProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        lblObjectsProperties.setText(StringConstants.DIA_LBL_OBJECT_PROPERTIES);
        ControlUtils.setFontToBeBold(lblObjectsProperties);
    }

    private void createObjectFields() {
        Composite fieldsComposite = new Composite(this, SWT.NONE);
        fieldsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glFieldsComposite = new GridLayout(2, false);
        glFieldsComposite.marginWidth = 0;
        glFieldsComposite.marginHeight = 0;
        fieldsComposite.setLayout(glFieldsComposite);

        Label lblName = new Label(fieldsComposite, SWT.NONE);
        lblName.setText(StringConstants.DIA_LBL_NAME);
        lblName.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        txtName = new Text(fieldsComposite, SWT.BORDER);
        txtName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        createObjectSelectionMethodOptions(fieldsComposite);
    }

    private void createObjectSelectionMethodOptions(Composite parent) {
        Composite methodComposite = new Composite(parent, SWT.NONE);
        methodComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glMethodComposite = new GridLayout(2, false);
        glMethodComposite.marginWidth = 0;
        glMethodComposite.marginHeight = 0;
        glMethodComposite.marginRight = 10;
        methodComposite.setLayout(glMethodComposite);

        Label lblObjectDetectMethod = new Label(methodComposite, SWT.NONE);
        lblObjectDetectMethod.setText(LBL_OBJECT_SELECTION_METHOD);

        lblHelp = new Label(methodComposite, SWT.NONE);
        GridData gdLblHelp = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
        gdLblHelp.heightHint = 20;
        lblHelp.setLayoutData(gdLblHelp);
        lblHelp.setImage(ImageConstants.IMG_16_HELP);
        lblHelp.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_HAND));

        radioBtnComposite = new Composite(parent, SWT.NONE);
        radioBtnComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
        RowLayout rlRadioBtnComposite = new RowLayout(SWT.HORIZONTAL);
        rlRadioBtnComposite.marginTop = 0;
        rlRadioBtnComposite.marginRight = 0;
        rlRadioBtnComposite.marginLeft = 0;
        rlRadioBtnComposite.marginBottom = 0;
        rlRadioBtnComposite.spacing = 5;
        rlRadioBtnComposite.fill = true;
        radioBtnComposite.setLayout(rlRadioBtnComposite);

        radioXpath = new Button(radioBtnComposite, SWT.FLAT | SWT.RADIO);
        radioXpath.setText(RADIO_LABEL_XPATH);
        radioXpath.setSelection(true);
        selectorButtons.put(SelectorMethod.XPATH, radioXpath);

        radioAttributes = new Button(radioBtnComposite, SWT.FLAT | SWT.RADIO);
        radioAttributes.setText(RADIO_LABEL_ATTRIBUTES);        
        selectorButtons.put(SelectorMethod.BASIC, radioAttributes);


        radioCss = new Button(radioBtnComposite, SWT.FLAT | SWT.RADIO);
        radioCss.setText(RADIO_LABEL_CSS);
        selectorButtons.put(SelectorMethod.CSS, radioCss);
    }

    private void createAttributeToolbarButtons(Composite parent) {
		compositeAttributeToolbar = new Composite(parent, SWT.NONE);
		compositeAttributeToolbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		compositeAttributeToolbar.setLayout(new FillLayout(SWT.HORIZONTAL));
		
        toolbar = new ToolBar(compositeAttributeToolbar, SWT.FLAT | SWT.RIGHT);
        toolbar.setForeground(ColorUtil.getToolBarForegroundColor());
       
        btnAdd = new ToolItem(toolbar, SWT.NONE);
        btnAdd.setText(StringConstants.ADD);
        btnAdd.setImage(ImageConstants.IMG_16_ADD);

        btnDelete = new ToolItem(toolbar, SWT.NONE);
        btnDelete.setText(StringConstants.DELETE);
        btnDelete.setImage(ImageConstants.IMG_16_DELETE);
        btnDelete.setDisabledImage(ImageConstants.IMG_16_DELETE_DISABLED);

        btnClear = new ToolItem(toolbar, SWT.NONE);
        btnClear.setText(StringConstants.CLEAR);
        btnClear.setImage(ImageConstants.IMG_16_CLEAR);
        btnClear.setDisabledImage(ImageConstants.IMG_16_CLEAR_DISABLED);
    }

    private void createPropertyTable(Composite parent) {
    	propertyTableComposite = new Composite(parent, SWT.NONE);
        GridData ldTableComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        ldTableComposite.heightHint = 100;
        propertyTableComposite.setLayoutData(ldTableComposite);
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        propertyTableComposite.setLayout(tableColumnLayout);

        tvProperty = new TableViewer(propertyTableComposite,
                SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        tvProperty.setContentProvider(ArrayContentProvider.getInstance());
        tProperty = tvProperty.getTable();
        tProperty.setHeaderVisible(true);
        tProperty.setLinesVisible(ControlUtils.shouldLineVisble(tProperty.getDisplay()));
        tvProperty.setInput(Collections.emptyList());

        cvProperty = new TableViewerColumn(tvProperty, SWT.LEFT);
        cName = cvProperty.getColumn();
        cName.setText(StringConstants.DIA_COL_NAME);
        cvProperty.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                return ((WebElementPropertyEntity) element).getName();
            }
        });

        cvProperty.setEditingSupport(new EditingSupport(cvProperty.getViewer()) {

            @Override
            protected void setValue(Object element, Object value) {
                if (!canEdit(element)) {
                    return;
                }
                ((WebElementPropertyEntity) element).setName(String.valueOf(value));
                tvProperty.update(element, null);
                sendPropertiesChangedEvent();
                refreshCapturedObjectsTree();
            }

            @Override
            protected Object getValue(Object element) {
                if (!canEdit(element)) {
                    return StringConstants.EMPTY;
                }
                return ((WebElementPropertyEntity) element).getName();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor(tProperty);
            }

            @Override
            protected boolean canEdit(Object element) {
                return isWebElementProperty(element);
            }
        });

        cvCondition = new TableViewerColumn(tvProperty, SWT.LEFT);
        cCondition = cvCondition.getColumn();
        cCondition.setText(COL_LABEL_CONDITION);
        cvCondition.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                return ((WebElementPropertyEntity) element).getMatchCondition();
            }
        });
        cvCondition.setEditingSupport(new EditingSupport(cvCondition.getViewer()) {

            @Override
            protected void setValue(Object element, Object value) {
                if (!canEdit(element)) {
                    return;
                }
                ((WebElementPropertyEntity) element).setMatchCondition(String.valueOf(value));
                tvProperty.update(element, null);
                sendPropertiesChangedEvent();
                refreshCapturedObjectsTree();
            }

            @Override
            protected Object getValue(Object element) {
                if (!canEdit(element)) {
                    return StringConstants.EMPTY;
                }
                return ((WebElementPropertyEntity) element).getMatchCondition();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                StringComboBoxCellEditor comboBoxCellEditor = new StringComboBoxCellEditor(tProperty,
                        WebElementPropertyEntity.MATCH_CONDITION.getTextVlues(), SWT.READ_ONLY);
                return comboBoxCellEditor;
            }

            @Override
            protected boolean canEdit(Object element) {
                return isWebElementProperty(element);
            }
        });

        cvValue = new TableViewerColumn(tvProperty, SWT.LEFT);
        cValue = cvValue.getColumn();
        cValue.setText(StringConstants.DIA_COL_VALUE);
        cvValue.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                return ((WebElementPropertyEntity) element).getValue();
            }
        });
        cvValue.setEditingSupport(new EditingSupport(cvValue.getViewer()) {

            @Override
            protected void setValue(Object element, Object value) {
                if (!canEdit(element)) {
                    return;
                }
                WebElementPropertyEntity webElementProp = (WebElementPropertyEntity) element;
                String newPropValue = String.valueOf(value);
                webElementProp.setValue(newPropValue);
                tvProperty.update(element, null);
                sendPropertiesChangedEvent();
                refreshCapturedObjectsTree();
            }

            @Override
            protected Object getValue(Object element) {
                if (!canEdit(element)) {
                    return StringConstants.EMPTY;
                }
                return ((WebElementPropertyEntity) element).getValue();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor(tProperty);
            }

            @Override
            protected boolean canEdit(Object element) {
                return isWebElementProperty(element);
            }
        });

        cvSelected = new TableViewerColumn(tvProperty, SWT.CENTER);
        cSelected = cvSelected.getColumn();
        cSelected.setText(getCheckboxIcon(isAllPropetyEnabled()));
        cSelected.setResizable(false);
        cSelected.pack();
        cSelected.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (webElement == null || getProperties() == null || getProperties().isEmpty()) {
                    return;
                }
                boolean isAllPropetyEnabled = isAllPropetyEnabled();
                cSelected.setText(getCheckboxIcon(!isAllPropetyEnabled));
                setAllProperty(!isAllPropetyEnabled);
                sendPropertiesChangedEvent();
            }
        });

        cvSelected.setLabelProvider(new CellLabelProvider() {

            @Override
            public void update(ViewerCell cell) {
                Object property = cell.getElement();
                if (!isWebElementProperty(property)) {
                    return;
                }
                Boolean isSelected = ((WebElementPropertyEntity) property).getIsSelected();
                // cell.setFont(ControlUtils.getFontStyle(tProperty, SWT.NORMAL, 10));
                cell.setText(getCheckboxIcon(isSelected));
                cSelected.setText(getCheckboxIcon(isAllPropetyEnabled()));
            }
        });
        cvSelected.setEditingSupport(new EditingSupport(cvSelected.getViewer()) {

            @Override
            protected void setValue(Object element, Object value) {
                if (!canEdit(element)) {
                    return;
                }
                ((WebElementPropertyEntity) element).setIsSelected((boolean) value);
                tvProperty.update(element, null);
                sendPropertiesChangedEvent();
            }

            @Override
            protected Object getValue(Object element) {
                return canEdit(element) && ((WebElementPropertyEntity) element).getIsSelected();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new CheckboxCellEditor();
            }

            @Override
            protected boolean canEdit(Object element) {
                return isWebElementProperty(element);
            }
        });

        tableColumnLayout.setColumnData(cName, new ColumnWeightData(20, 100));
        tableColumnLayout.setColumnData(cCondition, new ColumnWeightData(20, 100));
        tableColumnLayout.setColumnData(cValue, new ColumnWeightData(50, 150));
        tableColumnLayout.setColumnData(cSelected, new ColumnWeightData(5, 30, false));       
    }
    
    
    private void createXpathTable(Composite parent) {
    	xpathTableComposite = new Composite(parent, SWT.NONE);
        GridData ldTableComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        ldTableComposite.heightHint = 100;
        xpathTableComposite.setLayoutData(ldTableComposite);
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        xpathTableComposite.setLayout(tableColumnLayout);

        tvXpath = new TableViewer(xpathTableComposite,
                SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
       		
        tvXpath.setContentProvider(ArrayContentProvider.getInstance());
        tXpath = tvXpath.getTable();
        tXpath.setHeaderVisible(true);
        tXpath.setLinesVisible(ControlUtils.shouldLineVisble(tXpath.getDisplay()));
        tvXpath.setInput(Collections.emptyList());

        
        cvXpathName = new TableViewerColumn(tvXpath, SWT.LEFT);
        cXpathName = cvXpathName.getColumn();
        cXpathName.setText(StringConstants.DIA_COL_NAME);
        cvXpathName.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                return ((WebElementXpathEntity) element).getName();
            }
        });
       
        
        cvXpathValue = new TableViewerColumn(tvXpath, SWT.LEFT);
        cXpathValue = cvXpathValue.getColumn();
        cXpathValue.setText(StringConstants.DIA_COL_VALUE);
        cvXpathValue.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                return ((WebElementXpathEntity) element).getValue();
            }
        });
        cvXpathValue.setEditingSupport(new EditingSupport(cvXpathValue.getViewer()) {
            @Override	
            protected void setValue(Object element, Object value) {
                if (!canEdit(element)) {
                    return;
                }
                WebElementXpathEntity webElementXpath = (WebElementXpathEntity) element;
                String newXpathValue = String.valueOf(value);
                webElementXpath.setValue(newXpathValue);
                tvXpath.update(element, null);
                sendPropertiesChangedEvent();
                refreshCapturedObjectsTree();
            }

            @Override
            protected Object getValue(Object element) {
                if (!canEdit(element)) {
                    return StringConstants.EMPTY;
                }
                return ((WebElementXpathEntity) element).getValue();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor(tXpath);
            }

            @Override
            protected boolean canEdit(Object element) {
                return isWebElementXpath(element);
            }
        });
        
        tableColumnLayout.setColumnData(cXpathName, new ColumnWeightData(50, 150));
        tableColumnLayout.setColumnData(cXpathValue, new ColumnWeightData(50, 150));

    }
    
	private void showComposite(Composite composite, boolean isVisible) {
		composite.setVisible(isVisible);
		((GridData) composite.getLayoutData()).exclude = !isVisible;
		composite.getParent().layout();
	}
    

    private boolean isWebElementProperty(Object element) {
        return element != null
                && WebElementPropertyEntity.class.getSimpleName().equals(element.getClass().getSimpleName());
    }
    
    private boolean isWebElementXpath(Object element) {
        return element != null
                && WebElementXpathEntity.class.getSimpleName().equals(element.getClass().getSimpleName());
    }
    
    private void addControlListeners() {
        txtName.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String text = txtName.getText();
                if (webElement != null && !webElement.getName().equals(text)) {
                    webElement.setName(text);
                    sendNameChangedEvent();
                    refreshCapturedObjectsTree();
                }
            }
        });

        /** Object selection method selection listeners */
        radioAttributes.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (webElement == null || !radioAttributes.getSelection()) {
                    return;
                }
                enableControls();
                webElement.setSelectorMethod(SelectorMethod.BASIC);
                showComposite(propertyTableComposite, true);
                showComposite(xpathTableComposite, false);         
                showComposite(compositeAttributeToolbar, true);
                sendPropertiesChangedEvent();
            }
        });

        radioXpath.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (webElement == null || !radioXpath.getSelection()) {
                    return;
                }
                disableControls();
                webElement.setSelectorMethod(SelectorMethod.XPATH);
                showComposite(propertyTableComposite, false);
                showComposite(xpathTableComposite, true);      
                showComposite(compositeAttributeToolbar, false);
                sendPropertiesChangedEvent();
            }
        });

        radioCss.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (webElement == null || !radioCss.getSelection()) {
                    return;
                }
                webElement.setSelectorMethod(SelectorMethod.CSS);
                showComposite(propertyTableComposite, false);
                showComposite(xpathTableComposite, false);       
                showComposite(compositeAttributeToolbar, false);
                sendPropertiesChangedEvent();
            }
        });

        /** Toolbar button selection listeners */
        btnAdd.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (webElement == null) {
                    return;
                }

                WebElementPropertyEntity property = openAddPropertyDialog();
                if (property == null) {
                    return;
                }

                if (webElement.getProperty(property.getName()) != null) {
                    MessageDialog.openWarning(getShell(), StringConstants.WARN,
                            WARN_MSG_OBJECT_PROPERTY_NAME_IS_EXISTED);
                    return;
                }

                webElement.addProperty(property);
                updateWebObjectProperties();
                updateWebObjectXpaths();
                refreshCapturedObjectsTree();
                sendPropertiesChangedEvent();
            }
        });

        btnDelete.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (webElement == null || !webElement.hasProperty()) {                	
                    return;
                }

                int[] selectedPropertyIndices = tProperty.getSelectionIndices();
                if (selectedPropertyIndices.length == 0) {
                    return;
                }

                List<WebElementPropertyEntity> properties = getProperties();
                List<WebElementPropertyEntity> selectedProperties = Arrays.stream(selectedPropertyIndices)
                        .boxed()
                        .map(i -> properties.get(i))
                        .collect(Collectors.toList());
                properties.removeAll(selectedProperties);
                
                updateWebObjectProperties();
                updateWebObjectXpaths();
                refreshCapturedObjectsTree();
                sendPropertiesChangedEvent();
            }
        });

        btnClear.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (webElement != null) {
                    getProperties().clear();
                }
                updateWebObjectProperties();
                updateWebObjectXpaths();
                refreshCapturedObjectsTree();
                sendPropertiesChangedEvent();
            }
        });

        lblHelp.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                Program.launch(DocumentationMessageConstants.DIALOG_OBJECT_SPY_WEB_UI_SELECTION_METHOD);
            }
        });

        tvProperty.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                btnDelete.setEnabled(hasPropertySelected());
            }
        });
        
        tvXpath.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                btnDelete.setEnabled(hasXpathSelected());
            }
        });
        
        
    }

    private void sendPropertiesChangedEvent() {
        invoke(ObjectSpyEvent.ELEMENT_PROPERTIES_CHANGED, webElement);
    }
    
    private void sendNameChangedEvent() {
        invoke(ObjectSpyEvent.ELEMENT_NAME_CHANGED, webElement);
    }

    public void setRefreshCapturedObjectsTree(Runnable refreshTreeRunnable) {
        this.refreshTreeRunnable = refreshTreeRunnable;
    }

    private void refreshCapturedObjectsTree() {
        if (refreshTreeRunnable == null) {
            return;
        }
        Display.getDefault().asyncExec(refreshTreeRunnable);
    }

    private String getCheckboxIcon(boolean isChecked) {
        // Unicode symbols
        // Checked box: \u2611
        // Unchecked box: \u2610
        return isChecked ? "\u2611" : "\u2610";
    }

    private boolean isAllPropetyEnabled() {
        if (webElement == null) {
            return false;
        }

        List<WebElementPropertyEntity> properties = getProperties();
        if (properties == null || properties.isEmpty()) {
            return false;
        }

        return properties.stream().filter(property -> property.getIsSelected()).count() == properties.size();
    }
    

    private void setAllProperty(boolean isSelected) {
        if (webElement == null) {
            return;
        }

        List<WebElementPropertyEntity> properties = getProperties();
        if (properties == null || properties.isEmpty()) {
            return;
        }

        properties.forEach(property -> property.setIsSelected(isSelected));
        updateWebObjectProperties();
    }
    
    private WebElementPropertyEntity openAddPropertyDialog() {
        AddTestObjectPropertyDialog dialog = new AddTestObjectPropertyDialog(shell);

        if (dialog.open() != Window.OK) {
            return null;
        }

        WebElementPropertyEntity prop = new WebElementPropertyEntity(dialog.getName(), dialog.getValue());
        prop.setMatchCondition(dialog.getCondition());

        return prop;
    }

    private void populateSelectionMethod() {
        if (webElement == null) {
            return;
        }
        selectorButtons.entrySet().forEach(entry -> {
            entry.getValue().setSelection(entry.getKey() == webElement.getSelectorMethod());
        });
    }

	private void enableControls() {
		boolean isEnabled = webElement != null;
		boolean isEnabledForNoneWebPage = isEnabled
				&& !webElement.getClass().getSimpleName().equals(WebPage.class.getSimpleName());
		if (radioBtnComposite != null && !radioBtnComposite.isDisposed()) {

			Arrays.stream(radioBtnComposite.getChildren())
					.forEach(radioBtn -> radioBtn.setEnabled(isEnabledForNoneWebPage));
		}

		if (isReady(toolbar)) {
			btnAdd.setEnabled(isEnabledForNoneWebPage);
			btnDelete.setEnabled(isEnabled);
			btnClear.setEnabled(isEnabled);
		}
		if (isReady(txtName)) {
			txtName.setEditable(isEnabled);
		}
	}
    
    private void disableControls(){
    	
        btnAdd.setEnabled(false);
        btnDelete.setEnabled(false);
        btnClear.setEnabled(false);
    }

    public void refreshTable(WebElement selectedElement) {
        if (selectedElement != null) {
            txtName.setEditable(true);
            txtName.setText(selectedElement.getName());
        } else {
            txtName.setEditable(false);
            txtName.setText(StringConstants.EMPTY);
        }
        setWebElement(selectedElement);
        tvProperty.refresh();
        tvXpath.refresh();
    }

    protected void updateWebObjectProperties() {
        tProperty.removeAll();
        List<WebElementPropertyEntity> properties = webElement == null ? Collections.emptyList() : getProperties();
        tvProperty.setInput(properties);
        if (webElement == null) {
            showComposite(propertyTableComposite, true);
            showComposite(xpathTableComposite, false);          
            showComposite(compositeAttributeToolbar, false);
        } else {
            showComposite(propertyTableComposite, webElement.getSelectorMethod() == SelectorMethod.BASIC);
            showComposite(xpathTableComposite, webElement.getSelectorMethod() == SelectorMethod.XPATH);  
            showComposite(compositeAttributeToolbar,  webElement.getSelectorMethod() == SelectorMethod.BASIC);
        }
        cSelected.setText(getCheckboxIcon(isAllPropetyEnabled()));
        boolean hasProperty = !properties.isEmpty();
        btnDelete.setEnabled(hasProperty && hasPropertySelected());
        btnClear.setEnabled(hasProperty);
        refreshCapturedObjectsTree();
        // TODO Use eventbroker to send out the objectSelectionMethod so that selection editor can repopulate data
    }
    
    protected void updateWebObjectXpaths(){
    	tXpath.removeAll();
        List<WebElementXpathEntity> xpaths = webElement == null ? Collections.emptyList() : getXpaths();
        tvXpath.setInput(xpaths);
        if (webElement == null) {
        	  showComposite(propertyTableComposite, false);
              showComposite(xpathTableComposite, true);     
              showComposite(compositeAttributeToolbar, false);
        } else {
	           showComposite(propertyTableComposite, webElement.getSelectorMethod() == SelectorMethod.BASIC);
	           showComposite(xpathTableComposite, webElement.getSelectorMethod() == SelectorMethod.XPATH);     
	           showComposite(compositeAttributeToolbar,  webElement.getSelectorMethod() == SelectorMethod.BASIC);
        }       
       
        boolean hasXpath = !xpaths.isEmpty();
        btnDelete.setEnabled(hasXpath && hasXpathSelected());
        btnClear.setEnabled(hasXpath);
        refreshCapturedObjectsTree();
        // TODO Use eventbroker to send out the objectSelectionMethod so that selection editor can repopulate data
    }

    private List<WebElementPropertyEntity> getProperties() {
        return webElement.getProperties();
    }

    private List<WebElementXpathEntity> getXpaths() {
        return webElement.getXpaths();
    }
    
    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    private boolean hasPropertySelected() {
        StructuredSelection selection = (StructuredSelection) tvProperty.getSelection();
        return selection != null && selection.getFirstElement() != null;
    }
    
    private boolean hasXpathSelected() {
        StructuredSelection selection = (StructuredSelection) tvXpath.getSelection();
        return selection != null && selection.getFirstElement() != null;
    }

    @Override
    public void handleEvent(ObjectSpyEvent event, Object object) {
        switch (event) {
            case SELECTED_ELEMENT_CHANGED:
                setWebElement((WebElement) object);
                return;
            case ELEMENT_PROPERTIES_CHANGED:
            	Display.getDefault().syncExec(() -> {
                	setWebElement((WebElement) object);
            	});
            	return;
            default:
                return;
        }
    }

    @Override
    public Iterable<EventListener<ObjectSpyEvent>> getListeners(ObjectSpyEvent event) {
        return eventListeners.get(event);
    }

    @Override
    public void addListener(EventListener<ObjectSpyEvent> listener, Iterable<ObjectSpyEvent> events) {
        events.forEach(e -> {
            Set<EventListener<ObjectSpyEvent>> listenerOnEvent = eventListeners.get(e);
            if (listenerOnEvent == null) {
                listenerOnEvent = new HashSet<>();
            }
            listenerOnEvent.add(listener);
            eventListeners.put(e, listenerOnEvent);
        });
    }
}
