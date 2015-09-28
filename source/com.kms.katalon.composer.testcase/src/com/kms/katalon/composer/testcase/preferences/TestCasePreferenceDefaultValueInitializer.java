package com.kms.katalon.composer.testcase.preferences;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;

import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.keyword.IKeywordContributor;
import com.kms.katalon.core.model.FailureHandling;

public class TestCasePreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
				PreferenceConstants.TestCasePreferenceConstants.QUALIFIER);

		// Default value type of variable
		store.setDefault(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_VARIABLE_TYPE,
				InputValueType.Constant.name());

		// Test Case Calling
		store.setDefault(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_GENERATE_DEFAULT_VARIABLE_VALUE, true);
		store.setDefault(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_AUTO_EXPORT_VARIABLE, false);

		// Default Added Keyword
		store.setDefault(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORD_TYPE, "");
		store.setDefault(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORD_NAME, "");

		// Default Failure Handling
		store.setDefault(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_FAILURE_HANDLING,
				FailureHandling.STOP_ON_FAILURE.name());
	}

	public static boolean isSetGenerateVariableDefaultValue() {
		IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
				PreferenceConstants.TestCasePreferenceConstants.QUALIFIER);

		return store
				.getBoolean(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_GENERATE_DEFAULT_VARIABLE_VALUE);
	}

	public static boolean isSetAutoExportVariables() {
		IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
				PreferenceConstants.TestCasePreferenceConstants.QUALIFIER);
		return store.getBoolean(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_AUTO_EXPORT_VARIABLE);
	}

	public static Method getDefaultKeyword() throws Exception {
		IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
				PreferenceConstants.TestCasePreferenceConstants.QUALIFIER);
		String keywordName = store
				.getString(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORD_NAME);

		IKeywordContributor contributor = getDefaultKeywordContributor();

		Method method = KeywordController.getInstance().getBuiltInKeywordByName(
				contributor.getKeywordClass().getName(), keywordName);
		int i = 0;
		while (method == null || method.getName().equals(callTestCaseMethodName())) {
			method = KeywordController.getInstance().getBuiltInKeywords(contributor.getKeywordClass().getName()).get(i);
			i++;
		}
		return method;
	}

	protected static String callTestCaseMethodName() {
		return "callTestCase";
	}

	public static IKeywordContributor getDefaultKeywordContributor() throws Exception {
		IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
				PreferenceConstants.TestCasePreferenceConstants.QUALIFIER);
		String keywordType = store
				.getString(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORD_TYPE);
		IKeywordContributor contributor = KeywordController.getInstance().getBuiltInKeywordContributor(keywordType);
		if (contributor == null) {
			contributor = KeywordController.getInstance().getBuiltInKeywordContributors().get(0);
		}
		return contributor;
	}

	public static FailureHandling getDefaultFailureHandling() {
		IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
				PreferenceConstants.TestCasePreferenceConstants.QUALIFIER);
		String failureHandlingName = store
				.getString(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_FAILURE_HANDLING);
		return FailureHandling.valueOf(failureHandlingName);
	}
}
