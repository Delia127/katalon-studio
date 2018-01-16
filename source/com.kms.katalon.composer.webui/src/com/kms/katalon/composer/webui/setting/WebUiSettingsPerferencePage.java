package com.kms.katalon.composer.webui.setting;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.FontDescriptor;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webui.constants.ComposerWebuiMessageConstants;
import com.kms.katalon.composer.webui.constants.ImageConstants;
import com.kms.katalon.composer.webui.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;
import com.kms.katalon.util.collections.Pair;

public class WebUiSettingsPerferencePage extends PreferencePage {

    private static final String MSG_PROPERTY_NAME_IS_EXISTED = ComposerWebuiMessageConstants.MSG_PROPERTY_NAME_IS_EXISTED;

    private static final String GRP_LBL_DEFAULT_SELECTED_PROPERTIES_FOR_CAPTURED_TEST_OBJECT = ComposerWebuiMessageConstants.GRP_LBL_DEFAULT_SELECTED_PROPERTIES_FOR_CAPTURED_TEST_OBJECT;

    private static final String COL_LBL_DETECT_OBJECT_BY = ComposerWebuiMessageConstants.COL_LBL_DETECT_OBJECT_BY;

    public static final short TIMEOUT_MIN_VALUE = 0;

    public static final short TIMEOUT_MAX_VALUE = 9999;

    private static final int INPUT_WIDTH = 60;

    private WebUiExecutionSettingStore store;

    private Text txtDefaultPageLoadTimeout, txtActionDelay, txtDefaultIEHangTimeout;

    private Composite container;

    private Button radioNotUsePageLoadTimeout, radioUsePageLoadTimeout, chckIgnorePageLoadTimeoutException;

    ToolItem tiAdd, tiDelete, tiClear;

    private Table tProperty;

    private TableViewer tvProperty;

    private TableViewerColumn cvName, cvSelected;

    private TableColumn cName, cSelected;

    private List<Pair<String, Boolean>> defaultSelectingCapturedObjectProperties;

    public WebUiSettingsPerferencePage() {
        store = new WebUiExecutionSettingStore(ProjectController.getInstance().getCurrentProject());
        defaultSelectingCapturedObjectProperties = Collections.emptyList();
    }

    @Override
    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.verticalSpacing = 10;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        container.setLayout(layout);

        createTimeoutSettings(container);
        createTestObjectLocatorSettings(container);

        try {
            initialize();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        registerListeners();

        return container;
    }

    private void createTimeoutSettings(Composite container) {
        Label lblActionDelay = new Label(container, SWT.NONE);
        lblActionDelay.setText(ComposerWebuiMessageConstants.LBL_ACTION_DELAY);
        GridData gdLblActionDelay = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        lblActionDelay.setLayoutData(gdLblActionDelay);

        txtActionDelay = new Text(container, SWT.BORDER);
        GridData ldActionDelay = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        ldActionDelay.widthHint = INPUT_WIDTH;
        txtActionDelay.setLayoutData(ldActionDelay);

        Label lblDefaultIEHangTimeout = new Label(container, SWT.NONE);
        lblDefaultIEHangTimeout.setText(StringConstants.PREF_LBL_DEFAULT_WAIT_FOR_IE_HANGING_TIMEOUT);
        lblDefaultIEHangTimeout.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

        txtDefaultIEHangTimeout = new Text(container, SWT.BORDER);
        GridData gdTxtDefaultIEHangTimeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdTxtDefaultIEHangTimeout.widthHint = INPUT_WIDTH;
        txtDefaultIEHangTimeout.setLayoutData(gdTxtDefaultIEHangTimeout);

        Label lblDefaultPageLoadTimeout = new Label(container, SWT.NONE);
        lblDefaultPageLoadTimeout.setText(StringConstants.PREF_LBL_DEFAULT_PAGE_LOAD_TIMEOUT);
        lblDefaultPageLoadTimeout.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        Composite compPageLoad = new Composite(container, SWT.NONE);
        GridLayout glCompPageLoad = new GridLayout(2, false);
        glCompPageLoad.marginWidth = 0;
        glCompPageLoad.marginHeight = 0;
        glCompPageLoad.marginLeft = 15;
        compPageLoad.setLayout(glCompPageLoad);
        compPageLoad.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

        radioNotUsePageLoadTimeout = new Button(compPageLoad, SWT.RADIO);
        radioNotUsePageLoadTimeout.setText(StringConstants.PREF_LBL_ENABLE_DEFAULT_PAGE_LOAD_TIMEOUT);
        radioNotUsePageLoadTimeout.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        radioUsePageLoadTimeout = new Button(compPageLoad, SWT.RADIO);
        radioUsePageLoadTimeout.setText(StringConstants.PREF_LBL_CUSTOM_PAGE_LOAD_TIMEOUT);
        GridData gdRadioPageLoadTimeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        radioUsePageLoadTimeout.setLayoutData(gdRadioPageLoadTimeout);

        txtDefaultPageLoadTimeout = new Text(compPageLoad, SWT.BORDER);
        GridData gdDefaultPageLoadTimeout = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        gdDefaultPageLoadTimeout.widthHint = INPUT_WIDTH;
        txtDefaultPageLoadTimeout.setLayoutData(gdDefaultPageLoadTimeout);

        new Label(compPageLoad, SWT.NONE);
        chckIgnorePageLoadTimeoutException = new Button(compPageLoad, SWT.CHECK);
        chckIgnorePageLoadTimeoutException.setText(StringConstants.PREF_LBL_IGNORE_DEFAULT_PAGE_LOAD_TIMEOUT_EXCEPTION);
        chckIgnorePageLoadTimeoutException.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    }

    private void createTestObjectLocatorSettings(Composite container) {
        Group locatorGroup = new Group(container, SWT.NONE);
        locatorGroup.setText(GRP_LBL_DEFAULT_SELECTED_PROPERTIES_FOR_CAPTURED_TEST_OBJECT);
        locatorGroup.setLayout(new GridLayout());
        locatorGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

        Composite locatorContainer = new Composite(locatorGroup, SWT.NONE);
        locatorContainer.setLayout(new GridLayout());
        locatorContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        ToolBar tb = new ToolBar(locatorContainer, SWT.FLAT | SWT.RIGHT);
        tiAdd = new ToolItem(tb, SWT.PUSH);
        tiAdd.setText(StringConstants.ADD);
        tiAdd.setImage(ImageConstants.IMG_16_ADD);

        tiDelete = new ToolItem(tb, SWT.PUSH);
        tiDelete.setText(StringConstants.DELETE);
        tiDelete.setImage(ImageConstants.IMG_16_DELETE);
        tiDelete.setEnabled(false);

        tiClear = new ToolItem(tb, SWT.PUSH);
        tiClear.setText(StringConstants.CLEAR);
        tiClear.setImage(ImageConstants.IMG_16_CLEAR);

        createPropertyTable(locatorContainer);
    }

    @SuppressWarnings("unchecked")
    private void createPropertyTable(Composite parent) {
        Composite tableComposite = new Composite(parent, SWT.NONE);
        GridData ldTableComposite = new GridData(SWT.FILL, SWT.FILL, true, true);
        ldTableComposite.minimumHeight = 100;
        ldTableComposite.heightHint = 200;
        tableComposite.setLayoutData(ldTableComposite);
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tableComposite.setLayout(tableColumnLayout);

        tvProperty = new TableViewer(tableComposite,
                SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        tvProperty.setContentProvider(ArrayContentProvider.getInstance());
        tProperty = tvProperty.getTable();
        tProperty.setHeaderVisible(true);
        tProperty.setLinesVisible(true);

        cvName = new TableViewerColumn(tvProperty, SWT.LEFT);
        cName = cvName.getColumn();
        cName.setText(StringConstants.NAME);
        cvName.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                return ((Pair<String, Boolean>) element).getLeft();
            }
        });

        cvName.setEditingSupport(new EditingSupport(cvName.getViewer()) {

            @Override
            protected void setValue(Object element, Object value) {
                String newName = String.valueOf(value);

                if (StringUtils.isBlank(newName)) {
                    defaultSelectingCapturedObjectProperties.remove(element);
                    tvProperty.refresh();
                    return;
                }

                if (StringUtils.equals(((Pair<String, Boolean>) element).getLeft(), newName)) {
                    return;
                }

                boolean isExisted = defaultSelectingCapturedObjectProperties.stream()
                        .filter(i -> i.getLeft().equals(newName))
                        .count() > 0;

                if (isExisted) {
                    MessageDialog.openWarning(getShell(), StringConstants.WARN, MSG_PROPERTY_NAME_IS_EXISTED);
                    tvProperty.refresh();
                    return;
                }
                ((Pair<String, Boolean>) element).setLeft(newName);
                tvProperty.update(element, null);
            }

            @Override
            protected Object getValue(Object element) {
                return ((Pair<String, Boolean>) element).getLeft();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor(tProperty);
            }

            @Override
            protected boolean canEdit(Object element) {
                return true;
            }
        });

        cvSelected = new TableViewerColumn(tvProperty, SWT.CENTER);
        cSelected = cvSelected.getColumn();
        cSelected.setText(COL_LBL_DETECT_OBJECT_BY);

        cvSelected.setLabelProvider(new CellLabelProvider() {

            @Override
            public void update(ViewerCell cell) {
                Object property = cell.getElement();
                if (!(property instanceof Pair)) {
                    return;
                }
                Boolean isSelected = ((Pair<String, Boolean>) property).getRight();
                FontDescriptor fontDescriptor = FontDescriptor.createFrom(cell.getFont());
                Font font = fontDescriptor.setStyle(SWT.NORMAL).setHeight(10).createFont(tProperty.getDisplay());
                cell.setFont(font);
                cell.setText(getCheckboxSymbol(isSelected));
            }
        });
        cvSelected.setEditingSupport(new EditingSupport(cvSelected.getViewer()) {

            @Override
            protected void setValue(Object element, Object value) {
                ((Pair<String, Boolean>) element).setRight((boolean) value);
                tvProperty.update(element, null);
            }

            @Override
            protected Object getValue(Object element) {
                return ((Pair<String, Boolean>) element).getRight();
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new CheckboxCellEditor();
            }

            @Override
            protected boolean canEdit(Object element) {
                return true;
            }
        });

        tableColumnLayout.setColumnData(cName, new ColumnWeightData(80, 100));
        tableColumnLayout.setColumnData(cSelected, new ColumnWeightData(20, 100));
    }

    private String getCheckboxSymbol(boolean isChecked) {
        // Unicode symbols
        // Checked box: \u2611
        // Unchecked box: \u2610
        return isChecked ? "\u2611" : "\u2610";
    }

    private void addNumberVerification(Text txtInput, final int min, final int max) {
        if (txtInput == null || txtInput.isDisposed()) {
            return;
        }
        txtInput.addVerifyListener(new VerifyListener() {

            @Override
            public void verifyText(VerifyEvent e) {
                String oldValue = ((Text) e.getSource()).getText();
                String enterValue = e.text;
                String newValue = oldValue.substring(0, e.start) + enterValue + oldValue.substring(e.end);
                if (!newValue.matches("\\d+")) {
                    e.doit = false;
                    return;
                }
                try {
                    int val = Integer.parseInt(newValue);
                    e.doit = val >= min && val <= max;
                } catch (NumberFormatException ex) {
                    e.doit = false;
                }
            }
        });
        txtInput.addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                ((Text) e.getSource()).selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {
                Text inputField = (Text) e.getSource();
                String value = inputField.getText();
                if (value.length() <= 1 || !value.startsWith("0")) {
                    return;
                }
                try {
                    int val = Integer.parseInt(value);
                    inputField.setText(String.valueOf(val));
                } catch (NumberFormatException ex) {
                    // Do nothing
                }
            }
        });
        txtInput.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseUp(MouseEvent e) {
                ((Text) e.getSource()).selectAll();
            }
        });
    }

    protected void registerListeners() {
        radioUsePageLoadTimeout.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean usePageLoadTimeout = radioUsePageLoadTimeout.getSelection();
                txtDefaultPageLoadTimeout.setEnabled(usePageLoadTimeout);
                chckIgnorePageLoadTimeoutException.setEnabled(usePageLoadTimeout);
            }
        });
        addNumberVerification(txtActionDelay, TIMEOUT_MIN_VALUE, TIMEOUT_MAX_VALUE);
        addNumberVerification(txtDefaultIEHangTimeout, TIMEOUT_MIN_VALUE, TIMEOUT_MAX_VALUE);
        addNumberVerification(txtDefaultPageLoadTimeout, TIMEOUT_MIN_VALUE, TIMEOUT_MAX_VALUE);
        tiAdd.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Pair<String, Boolean> element = Pair.of(StringConstants.EMPTY, false);
                defaultSelectingCapturedObjectProperties.add(element);
                tvProperty.refresh();
                tvProperty.editElement(element, 0);
            }

        });
        tiDelete.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int[] selectedPropertyIndices = tProperty.getSelectionIndices();
                if (selectedPropertyIndices.length == 0) {
                    return;
                }

                List<Pair<String, Boolean>> selectedProperties = Arrays.stream(selectedPropertyIndices)
                        .boxed()
                        .map(i -> defaultSelectingCapturedObjectProperties.get(i))
                        .collect(Collectors.toList());
                defaultSelectingCapturedObjectProperties.removeAll(selectedProperties);
                tvProperty.refresh();
            }

        });

        tiClear.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                defaultSelectingCapturedObjectProperties.clear();
                tvProperty.refresh();
            }

        });

        tvProperty.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                StructuredSelection selection = (StructuredSelection) tvProperty.getSelection();
                tiDelete.setEnabled(selection != null && selection.getFirstElement() != null);
            }
        });
    }

    private void initialize() throws IOException {
        Boolean usePageLoadTimeout = store.getEnablePageLoadTimeout();
        radioUsePageLoadTimeout.setSelection(usePageLoadTimeout);
        radioNotUsePageLoadTimeout.setSelection(!usePageLoadTimeout);
        txtDefaultPageLoadTimeout.setText(String.valueOf(store.getPageLoadTimeout()));
        txtDefaultPageLoadTimeout.setEnabled(usePageLoadTimeout);
        chckIgnorePageLoadTimeoutException.setSelection(store.getIgnorePageLoadTimeout());
        chckIgnorePageLoadTimeoutException.setEnabled(usePageLoadTimeout);
        txtActionDelay.setText(String.valueOf(store.getActionDelay()));
        txtDefaultIEHangTimeout.setText(Integer.toString(store.getIEHangTimeout()));
        setInputForCapturedObjectPropertySetting(store.getCapturedTestObjectLocators());
    }

    private void setInputForCapturedObjectPropertySetting(List<Pair<String, Boolean>> input) {
        defaultSelectingCapturedObjectProperties = input;
        tvProperty.setInput(defaultSelectingCapturedObjectProperties);
    }

    @Override
    protected void performDefaults() {
        if (container == null) {
            return;
        }
        radioUsePageLoadTimeout.setSelection(WebUiExecutionSettingStore.EXECUTION_DEFAULT_ENABLE_PAGE_LOAD_TIMEOUT);
        Boolean usePageLoadTimeout = WebUiExecutionSettingStore.EXECUTION_DEFAULT_ENABLE_PAGE_LOAD_TIMEOUT;
        radioUsePageLoadTimeout.setSelection(usePageLoadTimeout);
        radioNotUsePageLoadTimeout.setSelection(!usePageLoadTimeout);
        txtDefaultPageLoadTimeout
                .setText(String.valueOf(WebUiExecutionSettingStore.EXECUTION_DEFAULT_PAGE_LOAD_TIMEOUT));
        txtDefaultPageLoadTimeout.setEnabled(usePageLoadTimeout);
        chckIgnorePageLoadTimeoutException
                .setSelection(WebUiExecutionSettingStore.EXECUTION_DEFAULT_IGNORE_PAGELOAD_TIMEOUT_EXCEPTION);
        chckIgnorePageLoadTimeoutException.setEnabled(usePageLoadTimeout);
        txtActionDelay.setText(String.valueOf(WebUiExecutionSettingStore.EXECUTION_DEFAULT_ACTION_DELAY));
        txtDefaultIEHangTimeout
                .setText(String.valueOf(WebUiExecutionSettingStore.EXECUTION_DEFAULT_WAIT_FOR_IE_HANGING));
        try {
            store.setDefaultCapturedTestObjectLocators();
            setInputForCapturedObjectPropertySetting(store.getCapturedTestObjectLocators());
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected void performApply() {
        if (container == null) {
            return;
        }
        try {
            if (radioUsePageLoadTimeout != null) {
                store.setEnablePageLoadTimeout(radioUsePageLoadTimeout.getSelection());
            }
            if (txtDefaultPageLoadTimeout != null) {
                store.setPageLoadTimeout(Integer.parseInt(txtDefaultPageLoadTimeout.getText()));
            }
            if (chckIgnorePageLoadTimeoutException != null) {
                store.setIgnorePageLoadTimeout(chckIgnorePageLoadTimeoutException.getSelection());
            }
            if (txtActionDelay != null) {
                store.setActionDelay(Integer.parseInt(txtActionDelay.getText()));
            }
            if (txtDefaultIEHangTimeout != null) {
                store.setIEHangTimeout(Integer.parseInt(txtDefaultIEHangTimeout.getText()));
            }
            if (tvProperty != null) {
                List<Pair<String, Boolean>> emptyItems = defaultSelectingCapturedObjectProperties.stream()
                        .filter(i -> i.getLeft().isEmpty())
                        .collect(Collectors.toList());
                defaultSelectingCapturedObjectProperties.removeAll(emptyItems);
                store.setCapturedTestObjectLocators(defaultSelectingCapturedObjectProperties);
            }
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public boolean performOk() {
        if (super.performOk() && isValid()) {
            performApply();
        }
        return true;
    }

}
