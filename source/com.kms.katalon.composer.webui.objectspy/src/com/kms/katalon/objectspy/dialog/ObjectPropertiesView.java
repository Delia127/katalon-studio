package com.kms.katalon.objectspy.dialog;

import static com.kms.katalon.composer.components.impl.util.ControlUtils.isReady;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
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
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.dialogs.AddTestObjectPropertyDialog;
import com.kms.katalon.composer.components.impl.editors.StringComboBoxCellEditor;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.objectspy.constants.ImageConstants;
import com.kms.katalon.objectspy.constants.ObjectSpyEventConstants;
import com.kms.katalon.objectspy.constants.ObjectspyMessageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebPage;

public class ObjectPropertiesView extends Composite implements EventHandler {

    private static final String WARN_MSG_OBJECT_PROPERTY_NAME_IS_EXISTED = ObjectspyMessageConstants.WARN_MSG_OBJECT_PROPERTY_NAME_IS_EXISTED;

    private static final String LBL_OBJECT_SELECTION_METHOD = ObjectspyMessageConstants.DIA_LBL_OBJECT_SELECTION_METHOD;

    private static final String RADIO_LABEL_CSS = ObjectspyMessageConstants.DIA_RADIO_LABEL_CSS;

    private static final String RADIO_LABEL_XPATH = ObjectspyMessageConstants.DIA_RADIO_LABEL_XPATH;

    private static final String RADIO_LABEL_BASIC = ObjectspyMessageConstants.DIA_RADIO_LABEL_BASIC;

    private static final String COL_LABEL_CONDITION = ObjectspyMessageConstants.DIA_COL_LABEL_CONDITION;

    private Table tProperty;

    private TableViewer tvProperty;

    private TableViewerColumn cvName, cvCondition, cvValue, cvSelected;

    private TableColumn cName, cCondition, cValue, cSelected;

    private Text txtName;

    private Button radioBasic, radioXpath, radioCss;

    private ToolItem btnAdd, btnDelete, btnClear;

    private Label lblHelp;

    private Composite radioBtnComposite;

    private ToolBar toolbar;

    private WebElement webElement;

    private Runnable refreshTreeRunnable;

    private Shell shell;

    private IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();

    private Map<SelectorMethod, Button> selectorButtons = new HashMap<>();

    private Composite tableAndButtonsComposite;

    private int lastHeight = -1;

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

        createToolbarButtons(tableAndButtonsComposite);

        createPropertyTable(tableAndButtonsComposite);

        addControlListeners();

        enableControls();

        subscribeEvents();
    }

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
        // eventBroker.post(ObjectSpyEventConstants.DIALOG_SIZE_CHANGED, null);
    }

    public WebElement getWebElement() {
        return webElement;
    }

    public void setWebElement(WebElement webElement) {
        this.webElement = webElement;
        enableControls();
        txtName.setText(webElement != null ? webElement.getName() : StringUtils.EMPTY);
        populateSelectionMethod();
        updateWebObjectProperties();
        eventBroker.post(ObjectSpyEventConstants.SELECTED_OBJECT_CHANGED, this.webElement);
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
        methodComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        GridLayout glMethodComposite = new GridLayout(2, false);
        glMethodComposite.marginWidth = 0;
        glMethodComposite.marginHeight = 0;
        glMethodComposite.marginRight = 10;
        methodComposite.setLayout(glMethodComposite);

        Label lblObjectDetectMethod = new Label(methodComposite, SWT.NONE);
        lblObjectDetectMethod.setText(LBL_OBJECT_SELECTION_METHOD);

        lblHelp = new Label(methodComposite, SWT.NONE);
        GridData gdLblHelp = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        gdLblHelp.heightHint = 20;
        lblHelp.setLayoutData(gdLblHelp);
        lblHelp.setImage(ImageConstants.IMG_16_HELP);
        lblHelp.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_HAND));

        radioBtnComposite = new Composite(parent, SWT.NONE);
        radioBtnComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
        RowLayout rlRadioBtnComposite = new RowLayout(SWT.HORIZONTAL);
        rlRadioBtnComposite.marginTop = 0;
        rlRadioBtnComposite.marginRight = 0;
        rlRadioBtnComposite.marginLeft = 0;
        rlRadioBtnComposite.marginBottom = 0;
        rlRadioBtnComposite.spacing = 5;
        rlRadioBtnComposite.fill = true;
        radioBtnComposite.setLayout(rlRadioBtnComposite);

        radioBasic = new Button(radioBtnComposite, SWT.FLAT | SWT.RADIO);
        radioBasic.setText(RADIO_LABEL_BASIC);
        radioBasic.setSelection(true);
        selectorButtons.put(SelectorMethod.BASIC, radioBasic);

        radioXpath = new Button(radioBtnComposite, SWT.FLAT | SWT.RADIO);
        radioXpath.setText(RADIO_LABEL_XPATH);
        selectorButtons.put(SelectorMethod.XPATH, radioXpath);

        radioCss = new Button(radioBtnComposite, SWT.FLAT | SWT.RADIO);
        radioCss.setText(RADIO_LABEL_CSS);
        selectorButtons.put(SelectorMethod.CSS, radioCss);
    }

    private void createToolbarButtons(Composite parent) {
        toolbar = new ToolBar(parent, SWT.FLAT | SWT.WRAP | SWT.RIGHT);
        toolbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        btnAdd = new ToolItem(toolbar, SWT.FLAT);
        btnAdd.setText(StringConstants.ADD);
        btnAdd.setImage(ImageConstants.IMG_16_ADD);

        btnDelete = new ToolItem(toolbar, SWT.FLAT);
        btnDelete.setText(StringConstants.DELETE);
        btnDelete.setImage(ImageConstants.IMG_16_DELETE);
        btnDelete.setDisabledImage(ImageConstants.IMG_16_DELETE_DISABLED);

        btnClear = new ToolItem(toolbar, SWT.FLAT);
        btnClear.setText(StringConstants.CLEAR);
        btnClear.setImage(ImageConstants.IMG_16_CLEAR);
        btnClear.setDisabledImage(ImageConstants.IMG_16_CLEAR_DISABLED);
    }

    private void createPropertyTable(Composite parent) {
        Composite tableComposite = new Composite(parent, SWT.NONE);
        GridData ldTableComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        ldTableComposite.heightHint = 100;
        tableComposite.setLayoutData(ldTableComposite);
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tableComposite.setLayout(tableColumnLayout);

        tvProperty = new TableViewer(tableComposite,
                SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        tvProperty.setContentProvider(ArrayContentProvider.getInstance());
        tProperty = tvProperty.getTable();
        tProperty.setHeaderVisible(true);
        tProperty.setLinesVisible(true);
        tvProperty.setInput(Collections.emptyList());

        cvName = new TableViewerColumn(tvProperty, SWT.LEFT);
        cName = cvName.getColumn();
        cName.setText(StringConstants.DIA_COL_NAME);
        cvName.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                return ((WebElementPropertyEntity) element).getName();
            }
        });

        cvName.setEditingSupport(new EditingSupport(cvName.getViewer()) {

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

    private boolean isWebElementProperty(Object element) {
        return element != null
                && WebElementPropertyEntity.class.getSimpleName().equals(element.getClass().getSimpleName());
    }

    private void addControlListeners() {
        txtName.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (webElement != null) {
                    webElement.setName(txtName.getText());
                    refreshCapturedObjectsTree();
                }
            }
        });

        /** Object selection method selection listeners */
        radioBasic.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (webElement == null || !radioBasic.getSelection()) {
                    return;
                }
                webElement.setSelectorMethod(SelectorMethod.BASIC);
                displayPropertiesTableComposite(true);
                sendPropertiesChangedEvent();
            }
        });

        radioXpath.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (webElement == null || !radioXpath.getSelection()) {
                    return;
                }
                webElement.setSelectorMethod(SelectorMethod.XPATH);
                displayPropertiesTableComposite(false);
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
                displayPropertiesTableComposite(false);
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
                refreshCapturedObjectsTree();
                sendPropertiesChangedEvent();
            }
        });

        lblHelp.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                Program.launch(DocumentationMessageConstants.DIALOG_OBJECT_SPY_WEB_UI);
            }
        });

        tvProperty.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                btnDelete.setEnabled(hasPropertySelected());
            }
        });
    }

    private void sendPropertiesChangedEvent() {
        eventBroker.post(ObjectSpyEventConstants.OBJECT_PROPERTIES_CHANGED, webElement);
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
        // shell.setSize(0, 0);
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
    }

    protected void updateWebObjectProperties() {

        tProperty.removeAll();
        List<WebElementPropertyEntity> properties = webElement == null ? Collections.emptyList() : getProperties();
        tvProperty.setInput(properties);
        if (webElement != null && webElement.getSelectorMethod() == SelectorMethod.BASIC) {
            displayPropertiesTableComposite(true);
        } else {
            displayPropertiesTableComposite(false);
        }
        cSelected.setText(getCheckboxIcon(isAllPropetyEnabled()));
        boolean hasProperty = !properties.isEmpty();
        btnDelete.setEnabled(hasProperty && hasPropertySelected());
        btnClear.setEnabled(hasProperty);
        refreshCapturedObjectsTree();
        // TODO Use eventbroker to send out the objectSelectionMethod so that selection editor can repopulate data
    }

    private List<WebElementPropertyEntity> getProperties() {
        return webElement.getProperties();
    }

    private void subscribeEvents() {
        // TODO Subscribe events

    }

    private void unsubscribeEvents() {
        // TODO Unsubscribe events

    }

    @Override
    public void handleEvent(Event event) {
        // TODO Handle subscribed events

    }

    @Override
    public void dispose() {
        unsubscribeEvents();
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
}
