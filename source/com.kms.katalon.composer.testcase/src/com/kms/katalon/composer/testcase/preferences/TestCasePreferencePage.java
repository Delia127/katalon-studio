package com.kms.katalon.composer.testcase.preferences;

import static com.kms.katalon.composer.testcase.preferences.ManualPreferenceValueInitializer.defaultStore;
import static com.kms.katalon.composer.testcase.preferences.ManualPreferenceValueInitializer.enableLineWrapping;
import static com.kms.katalon.composer.testcase.preferences.ManualPreferenceValueInitializer.getMaximumLineWidth;
import static com.kms.katalon.composer.testcase.preferences.ManualPreferenceValueInitializer.isLineWrappingEnabled;
import static com.kms.katalon.composer.testcase.preferences.ManualPreferenceValueInitializer.setMaximumLineWidth;
import static com.kms.katalon.composer.testcase.preferences.ManualPreferenceValueInitializer.updateStore;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.constants.TestCasePreferenceConstants;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.parts.TestCaseCompositePart;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.keyword.internal.IKeywordContributor;
import com.kms.katalon.core.keyword.internal.KeywordContributorCollection;
import com.kms.katalon.custom.keyword.KeywordMethod;

public class TestCasePreferencePage extends PreferencePageWithHelp {

    private Button btnDefaultVariableIsConstant, btnDefaultVariableIsVariable;

    private Button btnGenerateDefaultValue, btnGenerateVariable, btnExportVariable;

    private Button btnDefaultViewManual, btnDefaultViewScript;

    private Group grpTestCaseDefaultView;

    private Group grpDefaultKeyword;

    private ListViewer listViewerKwType, listViewerKwName;

    private Combo comboKeywordType;

    private IKeywordContributor[] contributors;

    private Composite fieldEditorParent;

    private Map<String, String> defaultKeywords;

    private Text txtMaximumLineWidth;

    private Button btnAllowLineWrapping;

    private Composite cpsWrappingLineWidth;

    public TestCasePreferencePage() {
        setTitle(StringConstants.PREF_TITLE_TEST_CASE);
        java.util.List<IKeywordContributor> keywordContributorList = KeywordContributorCollection
                .getKeywordContributors();
        contributors = keywordContributorList.toArray(new IKeywordContributor[keywordContributorList.size()]);
    }

    @Override
    protected Control createContents(Composite parent) {
        fieldEditorParent = new Composite(parent, SWT.NONE);
        fieldEditorParent.setLayout(new GridLayout(1, false));

        Group grpDefaultVariableType = new Group(fieldEditorParent, SWT.NONE);
        grpDefaultVariableType.setText(StringConstants.PREF_GRP_DEFAULT_VAR_TYPE);
        GridData gdGrpDefaultVariableType = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdGrpDefaultVariableType.exclude = true;
        grpDefaultVariableType.setLayoutData(gdGrpDefaultVariableType);
        grpDefaultVariableType.setLayout(new GridLayout(1, false));

        btnDefaultVariableIsConstant = new Button(grpDefaultVariableType, SWT.RADIO);
        btnDefaultVariableIsConstant.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnDefaultVariableIsConstant.setText(InputValueType.String.name());

        btnDefaultVariableIsVariable = new Button(grpDefaultVariableType, SWT.RADIO);
        btnDefaultVariableIsVariable.setText(InputValueType.Variable.name());

        Group grpTestCaseCalling = new Group(fieldEditorParent, SWT.NONE);
        grpTestCaseCalling.setText(StringConstants.PREF_GRP_TEST_CASE_CALLING);
        grpTestCaseCalling.setLayout(new GridLayout(1, false));
        grpTestCaseCalling.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        btnGenerateDefaultValue = new Button(grpTestCaseCalling, SWT.RADIO);
        btnGenerateDefaultValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnGenerateDefaultValue.setText(StringConstants.PREF_BTN_GEN_VAR_WITH_DEFAULT_VAL);

        Composite compositeGenerateVariable = new Composite(grpTestCaseCalling, SWT.NONE);
        compositeGenerateVariable.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout glCompositeGenerateVariable = new GridLayout(1, false);
        glCompositeGenerateVariable.marginWidth = 0;
        compositeGenerateVariable.setLayout(glCompositeGenerateVariable);

        btnGenerateVariable = new Button(compositeGenerateVariable, SWT.RADIO);
        btnGenerateVariable.setText(StringConstants.PREF_BTN_GEN_VAR_WITH_THE_SAME_NAME);

        btnExportVariable = new Button(compositeGenerateVariable, SWT.CHECK);
        GridData gd_btnExportVariable = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_btnExportVariable.horizontalIndent = 20;
        btnExportVariable.setLayoutData(gd_btnExportVariable);
        btnExportVariable.setText(StringConstants.PREF_BTN_EXPOSE_VARS_AUTO);

        createTestCaseDefaultViewGroup();

        Composite compositeDefaultFailureHandling = new Composite(fieldEditorParent, SWT.NONE);
        compositeDefaultFailureHandling.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        compositeDefaultFailureHandling.setLayout(new GridLayout(2, false));

        Composite compositeDefaultKeywordType = new Composite(fieldEditorParent, SWT.NONE);
        compositeDefaultKeywordType.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        compositeDefaultKeywordType.setLayout(new GridLayout(2, false));

        Label lblDefaultKeywordType = new Label(compositeDefaultKeywordType, SWT.NONE);
        lblDefaultKeywordType.setText(StringConstants.PREF_LBL_DEFAULT_KEYWORD_TYPE);

        comboKeywordType = new Combo(compositeDefaultKeywordType, SWT.READ_ONLY);
        comboKeywordType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));

        grpDefaultKeyword = new Group(fieldEditorParent, SWT.NONE);
        grpDefaultKeyword.setText(StringConstants.PREF_GRP_DEFAULT_KEYWORD);
        GridData gd_grpDefaultKeyword = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_grpDefaultKeyword.heightHint = 300;
        grpDefaultKeyword.setLayoutData(gd_grpDefaultKeyword);
        grpDefaultKeyword.setLayout(new GridLayout(1, false));

        SashForm sashFormDefaultKeyword = new SashForm(grpDefaultKeyword, SWT.NONE);
        sashFormDefaultKeyword.setSashWidth(5);
        sashFormDefaultKeyword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        sashFormDefaultKeyword.setLocation(0, 0);

        Composite compositeKwType = new Composite(sashFormDefaultKeyword, SWT.BORDER);
        GridLayout glCompositeKwType = new GridLayout(1, false);
        glCompositeKwType.verticalSpacing = 0;
        glCompositeKwType.marginHeight = 0;
        glCompositeKwType.marginWidth = 0;
        compositeKwType.setLayout(glCompositeKwType);

        Composite compositeKwTypeLabel = new Composite(compositeKwType, SWT.NONE);
        compositeKwTypeLabel.setLayout(new GridLayout(1, false));
        compositeKwTypeLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Label lblKeywordType = new Label(compositeKwTypeLabel, SWT.NONE);
        lblKeywordType.setAlignment(SWT.CENTER);
        lblKeywordType.setText(StringConstants.PREF_LBL_KEYWORK_TYPE);

        Label lblNewLabel_1 = new Label(compositeKwType, SWT.SEPARATOR | SWT.HORIZONTAL);
        lblNewLabel_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Composite compositeKwTypeListView = new Composite(compositeKwType, SWT.NONE);
        GridLayout glCompositeKwTypeListView = new GridLayout(1, false);
        glCompositeKwTypeListView.marginWidth = 0;
        glCompositeKwTypeListView.marginHeight = 0;
        compositeKwTypeListView.setLayout(glCompositeKwTypeListView);
        compositeKwTypeListView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        listViewerKwType = new ListViewer(compositeKwTypeListView, SWT.V_SCROLL);
        List listKeywordType = listViewerKwType.getList();
        listKeywordType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        listViewerKwType.setContentProvider(new ArrayContentProvider());
        listViewerKwType.setLabelProvider(new LabelProvider() {
            public String getText(Object element) {
                if (element instanceof IKeywordContributor && element != null) {
                    IKeywordContributor contributor = (IKeywordContributor) element;
                    return contributor.getLabelName();
                }
                return StringUtils.EMPTY;
            }
        });

        Composite compositeKwName = new Composite(sashFormDefaultKeyword, SWT.BORDER);
        GridLayout gl_compositeKwName = new GridLayout(1, false);
        gl_compositeKwName.verticalSpacing = 0;
        gl_compositeKwName.marginHeight = 0;
        gl_compositeKwName.marginWidth = 0;
        compositeKwName.setLayout(gl_compositeKwName);

        Composite compositeKwNameLabel = new Composite(compositeKwName, SWT.NONE);
        compositeKwNameLabel.setLayout(new GridLayout(1, false));
        compositeKwNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Label lblNewLabel = new Label(compositeKwNameLabel, SWT.NONE);
        lblNewLabel.setText(StringConstants.PREF_LBL_KEYWORK_NAME);

        Label lblSeparator = new Label(compositeKwName, SWT.SEPARATOR | SWT.HORIZONTAL);
        lblSeparator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Composite compositeKwNameListView = new Composite(compositeKwName, SWT.NONE);
        GridLayout glCompositeKwNameListView = new GridLayout(1, false);
        glCompositeKwNameListView.marginWidth = 0;
        glCompositeKwNameListView.marginHeight = 0;
        compositeKwNameListView.setLayout(glCompositeKwNameListView);
        compositeKwNameListView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        listViewerKwName = new ListViewer(compositeKwNameListView, SWT.V_SCROLL);
        List listKeywordName = listViewerKwName.getList();
        listKeywordName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        listViewerKwName.setContentProvider(new ArrayContentProvider());

        listViewerKwName.setLabelProvider(new LabelProvider() {
            public String getText(Object element) {
                if (element instanceof KeywordMethod && element != null) {
                    KeywordMethod method = (KeywordMethod) element;
                    return TreeEntityUtil.getReadableKeywordName(method.getName());
                }
                return StringUtils.EMPTY;
            }
        });

        sashFormDefaultKeyword.setWeights(new int[] { 1, 1 });

        initializeValue(false);
        registerControlModifyListeners();
        Group grpLineWrappingSettings = new Group(fieldEditorParent, SWT.NONE);
        grpLineWrappingSettings.setText(StringConstants.PREF_MANUAL_GRP_LINE_WRAPPING);
        grpLineWrappingSettings.setLayout(new GridLayout(1, false));
        grpLineWrappingSettings.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        btnAllowLineWrapping = new Button(grpLineWrappingSettings, SWT.CHECK);
        btnAllowLineWrapping.setText(StringConstants.PREF_MANUAL_BTN_ENABLE_LINE_WRAPPING);

        cpsWrappingLineWidth = new Composite(grpLineWrappingSettings, SWT.NONE);
        GridData gdCpsWrappingLineWidth = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdCpsWrappingLineWidth.horizontalIndent = 10;
        cpsWrappingLineWidth.setLayoutData(gdCpsWrappingLineWidth);
        GridLayout glCpsWrappingLineWidth = new GridLayout(2, false);
        glCpsWrappingLineWidth.marginHeight = 0;
        glCpsWrappingLineWidth.marginWidth = 0;
        cpsWrappingLineWidth.setLayout(glCpsWrappingLineWidth);

        Label lblNewLabel1 = new Label(cpsWrappingLineWidth, SWT.NONE);
        lblNewLabel1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblNewLabel1.setText(StringConstants.PREF_MANUAL_LBL_LINE_WIDTH);

        txtMaximumLineWidth = new Text(cpsWrappingLineWidth, SWT.BORDER);
        GridData gdTxtMaximumLineWidth = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        gdTxtMaximumLineWidth.widthHint = 50;
        txtMaximumLineWidth.setLayoutData(gdTxtMaximumLineWidth);

        updateInput();
        registerControlModifyListenersforManual();

        return fieldEditorParent;
    }

    private void checkButtonAndNotifyToListener(Button btn, boolean selected) {
        btn.setSelection(selected);
        btn.notifyListeners(SWT.Selection, new Event());
    }

    private void updateInput() {
        checkButtonAndNotifyToListener(btnAllowLineWrapping, isLineWrappingEnabled());

        txtMaximumLineWidth.setText(Integer.toString(getMaximumLineWidth()));
    }

    private void createTestCaseDefaultViewGroup() {
        grpTestCaseDefaultView = new Group(fieldEditorParent, SWT.NONE);
        grpTestCaseDefaultView.setText(StringConstants.PREF_GRP_TEST_CASE_DEFAULT_VIEW);
        GridData gd_grpTestCaseDefaultView = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        grpTestCaseDefaultView.setLayoutData(gd_grpTestCaseDefaultView);
        grpTestCaseDefaultView.setLayout(new GridLayout(1, false));

        btnDefaultViewManual = new Button(grpTestCaseDefaultView, SWT.RADIO);
        btnDefaultViewManual.setText(StringConstants.PREF_BTN_DEFAULT_VIEW_MANUAL);

        btnDefaultViewScript = new Button(grpTestCaseDefaultView, SWT.RADIO);
        btnDefaultViewScript.setText(StringConstants.PREF_BTN_DEFAULT_VIEW_SCRIPT);
    }

    private void registerControlModifyListeners() {
        btnGenerateDefaultValue.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (btnGenerateDefaultValue.getSelection()) {
                    btnExportVariable.setEnabled(false);
                    btnGenerateVariable.setSelection(false);
                }
            }
        });

        btnGenerateVariable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (btnGenerateVariable.getSelection()) {
                    btnExportVariable.setEnabled(true);
                    btnGenerateDefaultValue.setSelection(false);
                }
            }
        });

        listViewerKwType.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                try {
                    updateListViewKwName();
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }
            }
        });

        listViewerKwName.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IKeywordContributor contributor = (IKeywordContributor) ((IStructuredSelection) listViewerKwType
                        .getSelection()).getFirstElement();

                KeywordMethod method = (KeywordMethod) ((IStructuredSelection) event.getSelection()).getFirstElement();

                defaultKeywords.put(contributor.getKeywordClass().getName(), method.getName());
            }
        });

        registerControlModifyListenersForTestCaseStartView();
    }

    private void enableWrappingLineComposite(boolean enabled) {
        ControlUtils.recursiveSetEnabled(cpsWrappingLineWidth, enabled);
    }

    private void registerControlModifyListenersforManual() {
        btnAllowLineWrapping.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                enableWrappingLineComposite(btnAllowLineWrapping.getSelection());
            }
        });

        // Prevent user enter invalid line width
        txtMaximumLineWidth.addVerifyListener(new VerifyListener() {
            @Override
            public void verifyText(VerifyEvent e) {
                final String oldS = txtMaximumLineWidth.getText();
                final String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);
                if (StringUtils.isEmpty(newS)) {
                    return;
                }
                e.doit = isPositive(newS);
            }

            private boolean isPositive(String s) {
                try {
                    return Integer.parseInt(s) >= 1;
                } catch (NumberFormatException ex) {
                    return false;
                }
            }
        });

        txtMaximumLineWidth.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String lineWidth = txtMaximumLineWidth.getText();
                if (StringUtils.isEmpty(lineWidth)) {
                    txtMaximumLineWidth.setText(Integer.toString(getMaximumLineWidth()));
                }
            }
        });
    }

    private void registerControlModifyListenersForTestCaseStartView() {
        SelectionListener selectionListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                btnDefaultViewManual.setSelection(e.widget == btnDefaultViewManual);
                btnDefaultViewScript.setSelection(e.widget == btnDefaultViewScript);
            }
        };
        btnDefaultViewManual.addSelectionListener(selectionListener);
        btnDefaultViewScript.addSelectionListener(selectionListener);
    }

    private void initializeValue(boolean isDefault) {
        try {
            initTestCaseCallingValue(isDefault);
            initDefaultKeywordType(isDefault);
            initDefaultKeywordValue(isDefault);
            initDefaultTestCaseView(isDefault);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    private void initDefaultTestCaseView(boolean isDefault) {
        IPreferenceStore preferenceStore = getPreferenceStore();
        String defaultTestCaseView = (isDefault)
                ? preferenceStore.getDefaultString(TestCasePreferenceConstants.TESTCASE_PART_DEFAULT_START_VIEW)
                : preferenceStore.getString(TestCasePreferenceConstants.TESTCASE_PART_DEFAULT_START_VIEW);
        btnDefaultViewManual
                .setSelection(StringUtils.equals(defaultTestCaseView, TestCaseCompositePart.MANUAL_TAB_TITLE));
        btnDefaultViewScript
                .setSelection(StringUtils.equals(defaultTestCaseView, TestCaseCompositePart.SCRIPT_TAB_TITLE));
    }

    private void initDefaultKeywordType(boolean isDefault) {
        IPreferenceStore preferenceStore = getPreferenceStore();
        String defaultKeywordTypeString = preferenceStore
                .getString(TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORD_TYPE);
        if (isDefault) {
            defaultKeywordTypeString = preferenceStore
                    .getDefaultString(TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORD_TYPE);
        }
        int selectedIndex = 0;
        String[] keywordTypeStringArray = new String[contributors.length];
        for (int i = 0; i < contributors.length; i++) {
            if (contributors[i].getKeywordClass().getName().equalsIgnoreCase(defaultKeywordTypeString)) {
                selectedIndex = i;
            }
            keywordTypeStringArray[i] = contributors[i].getLabelName();
        }

        comboKeywordType.setItems(keywordTypeStringArray);
        comboKeywordType.setText(keywordTypeStringArray[selectedIndex]);
    }

    private void initDefaultKeywordValue(boolean isDefault) throws Exception {
        if (contributors.length <= 0)
            return;

        listViewerKwType.setInput(contributors);

        defaultKeywords = TestCasePreferenceDefaultValueInitializer.getDefaultKeywords();

        listViewerKwType.getList().deselectAll();

        if (listViewerKwType.getSelection() == null || listViewerKwType.getSelection().isEmpty()) {
            listViewerKwType.getList().select(0);
        }

        updateListViewKwName();
    }

    private void updateListViewKwName() throws Exception {
        IKeywordContributor contributor = (IKeywordContributor) ((IStructuredSelection) listViewerKwType.getSelection())
                .getFirstElement();
        java.util.List<KeywordMethod> methods = KeywordController.getInstance()
                .getBuiltInKeywords(contributor.getKeywordClass().getSimpleName(), true);

        listViewerKwName.setInput(methods);
        listViewerKwName.getList().deselectAll();

        String keywordName = defaultKeywords.get(contributor.getKeywordClass().getName());
        if (!StringUtils.isBlank(keywordName)) {
            for (KeywordMethod method : methods) {
                if (method.getName().equals(keywordName)) {
                    listViewerKwName.getList().select(methods.indexOf(method));
                    listViewerKwName.getList().showSelection();
                    break;
                }
            }
        }

        if (listViewerKwName.getSelection() == null || listViewerKwName.getSelection().isEmpty()) {
            listViewerKwName.getList().select(0);
        }
    }

    private void initTestCaseCallingValue(boolean isDefault) {
        resetAllRadioButtonStates();

        String defaultVariableType = getPreferenceStore()
                .getString(TestCasePreferenceConstants.TESTCASE_DEFAULT_VARIABLE_TYPE);
        if (isDefault) {
            defaultVariableType = getPreferenceStore()
                    .getDefaultString(TestCasePreferenceConstants.TESTCASE_DEFAULT_VARIABLE_TYPE);
        }

        InputValueType valueType = InputValueType.valueOf(defaultVariableType);
        switch (valueType) {
            case Variable:
                btnDefaultVariableIsVariable.setSelection(true);
                break;
            default:
                btnDefaultVariableIsConstant.setSelection(true);
                break;
        }

        boolean generateDefaultValue = getPreferenceStore()
                .getBoolean(TestCasePreferenceConstants.TESTCASE_GENERATE_DEFAULT_VARIABLE_VALUE);
        if (isDefault) {
            generateDefaultValue = getPreferenceStore()
                    .getDefaultBoolean(TestCasePreferenceConstants.TESTCASE_GENERATE_DEFAULT_VARIABLE_VALUE);
        }

        if (generateDefaultValue) {
            btnGenerateDefaultValue.setSelection(true);
            btnExportVariable.setEnabled(false);
        } else {
            btnGenerateVariable.setSelection(true);
            btnExportVariable.setEnabled(true);
        }

        boolean exportVariables = getPreferenceStore()
                .getBoolean(TestCasePreferenceConstants.TESTCASE_AUTO_EXPORT_VARIABLE);
        if (isDefault) {
            exportVariables = getPreferenceStore()
                    .getDefaultBoolean(TestCasePreferenceConstants.TESTCASE_AUTO_EXPORT_VARIABLE);
        }

        if (exportVariables) {
            btnExportVariable.setSelection(true);
        }
    }

    private void resetAllRadioButtonStates() {
        btnDefaultVariableIsConstant.setSelection(false);
        btnDefaultVariableIsVariable.setSelection(false);

        btnGenerateDefaultValue.setSelection(false);
        btnGenerateVariable.setSelection(false);
        btnExportVariable.setSelection(false);
        btnExportVariable.setEnabled(true);
    }

    @Override
    protected void performDefaults() {
        if (fieldEditorParent == null) {
            return;
        }
        resetAllRadioButtonStates();
        initializeValue(true);

        if (isNotAbleToUpdate()) {
            return;
        }
        defaultStore();
        updateInput();
    }

    private boolean isNotAbleToUpdate() {
        return fieldEditorParent == null || fieldEditorParent.isDisposed();
    }

    @Override
    protected void performApply() {
        if (fieldEditorParent == null) {
            return;
        }
        // Test Case Calling
        if (btnDefaultVariableIsConstant.getSelection()) {
            getPreferenceStore().setValue(TestCasePreferenceConstants.TESTCASE_DEFAULT_VARIABLE_TYPE,
                    InputValueType.String.name());
        }

        if (btnDefaultVariableIsVariable.getSelection()) {
            getPreferenceStore().setValue(TestCasePreferenceConstants.TESTCASE_DEFAULT_VARIABLE_TYPE,
                    InputValueType.Variable.name());
        }

        getPreferenceStore().setValue(TestCasePreferenceConstants.TESTCASE_GENERATE_DEFAULT_VARIABLE_VALUE,
                btnGenerateDefaultValue.getSelection());

        getPreferenceStore().setValue(TestCasePreferenceConstants.TESTCASE_AUTO_EXPORT_VARIABLE,
                btnExportVariable.getSelection());

        // Default keyword type
        String selectedKeywordType = comboKeywordType.getText();
        for (int i = 0; i < contributors.length; i++) {
            if (contributors[i].getLabelName().equalsIgnoreCase(selectedKeywordType)) {
                getPreferenceStore().setValue(TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORD_TYPE,
                        contributors[i].getKeywordClass().getName());
                break;
            }
        }

        // Default Keyword
        TestCasePreferenceDefaultValueInitializer.storeDefaultKeywords(defaultKeywords);

        applyValueForTestCaseStartView();
    }

    private void applyValueForTestCaseStartView() {
        String defaultTestCaseViewValue = null;
        if (btnDefaultViewManual.getSelection()) {
            defaultTestCaseViewValue = TestCaseCompositePart.MANUAL_TAB_TITLE;
        } else if (btnDefaultViewScript.getSelection()) {
            defaultTestCaseViewValue = TestCaseCompositePart.SCRIPT_TAB_TITLE;
        }
        if (defaultTestCaseViewValue == null) {
            return;
        }
        getPreferenceStore().setValue(TestCasePreferenceConstants.TESTCASE_PART_DEFAULT_START_VIEW,
                defaultTestCaseViewValue);
    }

    public boolean performOk() {
        try {
            boolean result = super.performOk();
            if (result && isValid()) {
                performApply();
            }
            if (isNotAbleToUpdate()) {
                return true;
            }

            enableLineWrapping(btnAllowLineWrapping.getSelection());
            setMaximumLineWidth(Integer.valueOf(txtMaximumLineWidth.getText()));

            updateStore();
            return true;
        } catch (IOException e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PREF_MANUAL_MSG_UNABLE_TO_UPDATE, e.getMessage());
            LoggerSingleton.logError(e);
            return false;
        }
    }

    @Override
    public boolean hasDocumentation() {
        return true;
    }

    @Override
    public String getDocumentationUrl() {
        return DocumentationMessageConstants.PREFERENCE_TEST_CASE;
    }
}