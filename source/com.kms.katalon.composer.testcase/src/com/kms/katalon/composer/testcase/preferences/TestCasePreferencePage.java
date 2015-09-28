package com.kms.katalon.composer.testcase.preferences;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.keyword.IKeywordContributor;
import com.kms.katalon.core.model.FailureHandling;

public class TestCasePreferencePage extends PreferencePage {

	private Button btnDefaultVariableIsConstant, btnDefaultVariableIsVariable;
	private Button btnGenerateDefaultValue, btnGenerateVariable, btnExportVariable;
	private Group grpDefaultKeyword;
	private ListViewer listViewerKwType, listViewerKwName;
	private Combo comboDefaultFailureHandling;

	private java.util.List<IKeywordContributor> contributors;
	private Composite fieldEditorParent;
	private static final String[] DF_FAILURE_HANDLING_VALUES = FailureHandling.valueStrings();

	public TestCasePreferencePage() {
		setTitle(StringConstants.PREF_TITLE_TEST_CASE);
		contributors = KeywordController.getInstance().getBuiltInKeywordContributors();
	}

	@Override
	protected Control createContents(Composite parent) {
		fieldEditorParent = new Composite(parent, SWT.NONE);
		fieldEditorParent.setLayout(new GridLayout(1, false));

		Group grpDefaultVariableType = new Group(fieldEditorParent, SWT.NONE);
		grpDefaultVariableType.setText(StringConstants.PREF_GRP_DEFAULT_VAR_TYPE);
		GridData gd_grpDefaultVariableType = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_grpDefaultVariableType.exclude = true;
		grpDefaultVariableType.setLayoutData(gd_grpDefaultVariableType);
		grpDefaultVariableType.setLayout(new GridLayout(1, false));

		btnDefaultVariableIsConstant = new Button(grpDefaultVariableType, SWT.RADIO);
		btnDefaultVariableIsConstant.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnDefaultVariableIsConstant.setText(InputValueType.Constant.name());

		btnDefaultVariableIsVariable = new Button(grpDefaultVariableType, SWT.RADIO);
		btnDefaultVariableIsVariable.setText(InputValueType.Variable.name());

		Group grpTestCaseCalling = new Group(fieldEditorParent, SWT.NONE);
		grpTestCaseCalling.setText(StringConstants.PREF_GRP_TEST_CASE_CALLING);
		grpTestCaseCalling.setLayout(new GridLayout(1, false));
		grpTestCaseCalling.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		btnGenerateDefaultValue = new Button(grpTestCaseCalling, SWT.RADIO);
		btnGenerateDefaultValue.setText(StringConstants.PREF_BTN_GEN_VAR_WITH_DEFAULT_VAL);

		Composite compositeGenerateVariable = new Composite(grpTestCaseCalling, SWT.NONE);
		GridLayout gl_compositeGenerateVariable = new GridLayout(1, false);
		gl_compositeGenerateVariable.marginWidth = 0;
		compositeGenerateVariable.setLayout(gl_compositeGenerateVariable);

		btnGenerateVariable = new Button(compositeGenerateVariable, SWT.RADIO);
		btnGenerateVariable.setText(StringConstants.PREF_BTN_GEN_VAR_WITH_THE_SAME_NAME);

		btnExportVariable = new Button(compositeGenerateVariable, SWT.CHECK);
		GridData gd_btnExportVariable = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnExportVariable.horizontalIndent = 20;
		btnExportVariable.setLayoutData(gd_btnExportVariable);
		btnExportVariable.setText(StringConstants.PREF_BTN_EXPOSE_VARS_AUTO);

		Composite compositeDefaultFailureHandling = new Composite(fieldEditorParent, SWT.NONE);
		compositeDefaultFailureHandling.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		compositeDefaultFailureHandling.setLayout(new GridLayout(2, false));

		Label lblDefaultFailureHandling = new Label(compositeDefaultFailureHandling, SWT.NONE);
		lblDefaultFailureHandling.setBounds(0, 0, 55, 15);
		lblDefaultFailureHandling.setText(StringConstants.PREF_LBL_DEFAULT_FAILURE_HANDLING);

		comboDefaultFailureHandling = new Combo(compositeDefaultFailureHandling, SWT.READ_ONLY);
		comboDefaultFailureHandling.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		comboDefaultFailureHandling.setBounds(0, 0, 91, 23);

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
		compositeKwType.setLayout(new GridLayout(1, false));

		Composite compositeKwTypeLabel = new Composite(compositeKwType, SWT.NONE);
		compositeKwTypeLabel.setLayout(new GridLayout(1, false));
		compositeKwTypeLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		compositeKwTypeLabel.setBounds(0, 0, 64, 64);

		Label lblKeywordType = new Label(compositeKwTypeLabel, SWT.NONE);
		lblKeywordType.setAlignment(SWT.CENTER);
		lblKeywordType.setBounds(0, 0, 55, 15);
		lblKeywordType.setText(StringConstants.PREF_LBL_KEYWORK_TYPE);

		Composite compositeKwTypeListView = new Composite(compositeKwType, SWT.NONE);
		GridLayout gl_compositeKwTypeListView = new GridLayout(1, false);
		gl_compositeKwTypeListView.marginWidth = 0;
		gl_compositeKwTypeListView.marginHeight = 0;
		compositeKwTypeListView.setLayout(gl_compositeKwTypeListView);
		compositeKwTypeListView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositeKwTypeListView.setBounds(0, 0, 64, 64);

		listViewerKwType = new ListViewer(compositeKwTypeListView, SWT.BORDER | SWT.V_SCROLL);
		List listKeywordType = listViewerKwType.getList();
		listKeywordType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		listKeywordType.setBounds(0, 0, 88, 68);
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
		compositeKwName.setLayout(new GridLayout(1, false));

		Composite compositeKwNameLabel = new Composite(compositeKwName, SWT.NONE);
		compositeKwNameLabel.setLayout(new GridLayout(1, false));
		compositeKwNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		compositeKwNameLabel.setBounds(0, 0, 64, 64);

		Label lblNewLabel = new Label(compositeKwNameLabel, SWT.NONE);
		lblNewLabel.setBounds(0, 0, 55, 15);
		lblNewLabel.setText(StringConstants.PREF_LBL_KEYWORK_NAME);

		Composite compositeKwNameListView = new Composite(compositeKwName, SWT.NONE);
		GridLayout gl_compositeKwNameListView = new GridLayout(1, false);
		gl_compositeKwNameListView.marginWidth = 0;
		gl_compositeKwNameListView.marginHeight = 0;
		compositeKwNameListView.setLayout(gl_compositeKwNameListView);
		compositeKwNameListView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		compositeKwNameListView.setBounds(0, 0, 64, 64);

		listViewerKwName = new ListViewer(compositeKwNameListView, SWT.BORDER | SWT.V_SCROLL);
		List listKeywordName = listViewerKwName.getList();
		listKeywordName.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		listViewerKwName.setContentProvider(new ArrayContentProvider());

		listViewerKwName.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				if (element instanceof Method && element != null) {
					Method method = (Method) element;
					return method.getName();
				}
				return StringUtils.EMPTY;
			}
		});

		sashFormDefaultKeyword.setWeights(new int[] { 1, 1 });

		initializeValue(false);

		registerControlModifyListeners();

		return fieldEditorParent;
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
					IStructuredSelection selection = (IStructuredSelection) listViewerKwType.getSelection();
					listViewerKwName.getList().removeAll();
					IKeywordContributor contributor = (IKeywordContributor) selection.getFirstElement();
					java.util.List<Method> methods = KeywordController.getInstance().getBuiltInKeywords(
							contributor.getKeywordClass().getName());
					listViewerKwName.setInput(methods);
					if (methods.size() > 0) {
						listViewerKwName.getList().select(0);
					}
				} catch (Exception e) {
					LoggerSingleton.logError(e);
				}
			}
		});
	}

	private void initializeValue(boolean isDefault) {
		try {
			initTestCaseCallingValue(isDefault);
			initDefaultKeywordValue(isDefault);
			initDefaultFailureHandlingValue(isDefault);
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}

	private void initDefaultFailureHandlingValue(boolean isDefault) {
		comboDefaultFailureHandling.setItems(DF_FAILURE_HANDLING_VALUES);
		String cbbDfFailureHandlingText = getPreferenceStore().getString(
				PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_FAILURE_HANDLING);
		if (isDefault) {
			cbbDfFailureHandlingText = getPreferenceStore().getDefaultString(
					PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_FAILURE_HANDLING);
		}

		comboDefaultFailureHandling.setText(cbbDfFailureHandlingText);
	}

	private void initDefaultKeywordValue(boolean isDefault) throws Exception {
		if (contributors.size() <= 0) return;

		listViewerKwType.setInput(contributors);

		String keywordType = getPreferenceStore().getString(
				PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORD_TYPE);
		String keywordName = getPreferenceStore().getString(
				PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORD_NAME);
		if (isDefault) {
			keywordType = getPreferenceStore().getDefaultString(
					PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORD_TYPE);
			keywordName = getPreferenceStore().getDefaultString(
					PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORD_NAME);
		}

		listViewerKwType.getList().deselectAll();
		if (!keywordType.isEmpty()) {
			for (int i = 0; i < contributors.size(); i++) {
				if (contributors.get(i).getKeywordClass().getName().equals(keywordType)) {
					listViewerKwType.getList().select(i);
					break;
				}
			}
		}

		if (listViewerKwType.getSelection() == null || listViewerKwType.getSelection().isEmpty()) {
			listViewerKwType.getList().select(0);
		}

		IKeywordContributor contributor = (IKeywordContributor) ((IStructuredSelection) listViewerKwType.getSelection())
				.getFirstElement();
		java.util.List<Method> methods = KeywordController.getInstance().getBuiltInKeywords(
				contributor.getKeywordClass().getName());
		listViewerKwName.setInput(methods);
		listViewerKwName.getList().deselectAll();

		if (!keywordName.isEmpty()) {
			for (int index = 0; index < methods.size(); index++) {
				if (methods.get(index).getName().equals(keywordName)) {
					listViewerKwName.getList().select(index);
					listViewerKwName.getList().showSelection();
				}
			}
		}

		if (listViewerKwName.getSelection() == null || listViewerKwName.getSelection().isEmpty()) {
			listViewerKwName.getList().select(0);
		}
	}

	private void initTestCaseCallingValue(boolean isDefault) {
		resetAllRadioButtonStates();

		String defaultVariableType = getPreferenceStore().getString(
				PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_VARIABLE_TYPE);
		if (isDefault) {
			defaultVariableType = getPreferenceStore().getDefaultString(
					PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_VARIABLE_TYPE);
		}

		InputValueType valueType = InputValueType.valueOf(defaultVariableType);
		switch (valueType) {
			case Constant:
				btnDefaultVariableIsConstant.setSelection(true);
				break;
			case Variable:
				btnDefaultVariableIsVariable.setSelection(true);
				break;
			default:
				btnDefaultVariableIsConstant.setSelection(true);
				break;
		}

		boolean generateDefaultValue = getPreferenceStore().getBoolean(
				PreferenceConstants.TestCasePreferenceConstants.TESTCASE_GENERATE_DEFAULT_VARIABLE_VALUE);
		if (isDefault) {
			generateDefaultValue = getPreferenceStore().getDefaultBoolean(
					PreferenceConstants.TestCasePreferenceConstants.TESTCASE_GENERATE_DEFAULT_VARIABLE_VALUE);
		}

		if (generateDefaultValue) {
			btnGenerateDefaultValue.setSelection(true);
			btnExportVariable.setEnabled(false);
		} else {
			btnGenerateVariable.setSelection(true);
			btnExportVariable.setEnabled(true);
		}

		boolean exportVariables = getPreferenceStore().getBoolean(
				PreferenceConstants.TestCasePreferenceConstants.TESTCASE_AUTO_EXPORT_VARIABLE);
		if (isDefault) {
			exportVariables = getPreferenceStore().getDefaultBoolean(
					PreferenceConstants.TestCasePreferenceConstants.TESTCASE_AUTO_EXPORT_VARIABLE);
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
		if (fieldEditorParent == null) return;
		resetAllRadioButtonStates();
		initializeValue(true);
	}

	@Override
	protected void performApply() {
		if (fieldEditorParent == null) return;
		// Test Case Calling
		if (btnDefaultVariableIsConstant.getSelection()) {
			getPreferenceStore().setValue(
					PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_VARIABLE_TYPE,
					InputValueType.Constant.name());
		}

		if (btnDefaultVariableIsVariable.getSelection()) {
			getPreferenceStore().setValue(
					PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_VARIABLE_TYPE,
					InputValueType.Variable.name());
		}

		getPreferenceStore().setValue(
				PreferenceConstants.TestCasePreferenceConstants.TESTCASE_GENERATE_DEFAULT_VARIABLE_VALUE,
				btnGenerateDefaultValue.getSelection());

		getPreferenceStore().setValue(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_AUTO_EXPORT_VARIABLE,
				btnExportVariable.getSelection());

		// Default Keyword
		IKeywordContributor selectedContributor = (IKeywordContributor) ((IStructuredSelection) listViewerKwType
				.getSelection()).getFirstElement();
		getPreferenceStore().setValue(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORD_TYPE,
				selectedContributor.getKeywordClass().getName());

		Method method = (Method) ((IStructuredSelection) listViewerKwName.getSelection()).getFirstElement();
		getPreferenceStore().setValue(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORD_NAME,
				method.getName());

		// Default Failure Handling
		getPreferenceStore().setValue(
				PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_FAILURE_HANDLING,
				comboDefaultFailureHandling.getText());

	}

	public boolean performOk() {
		boolean result = super.performOk();
		if (result && isValid()) {
			performApply();
		}
		return true;
	}
}
