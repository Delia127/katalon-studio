package com.kms.katalon.composer.testcase.preferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.keyword.IKeywordContributor;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class TestCasePreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.TestCasePreferenceConstants.QUALIFIER);

        // Default value type of variable
        store.setDefault(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_VARIABLE_TYPE,
                InputValueType.String.name());

        // Test Case Calling
        store.setDefault(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_GENERATE_DEFAULT_VARIABLE_VALUE, true);
        store.setDefault(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_AUTO_EXPORT_VARIABLE, false);

        // Default Added Keyword
        store.setDefault(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORD_TYPE,
                KeywordController.getInstance().getBuiltInKeywordContributors()[0].getKeywordClass().getName());
        Map<String, String> defaultKeywords = new HashMap<String, String>();
        for (IKeywordContributor keywordContributor : KeywordController.getInstance().getBuiltInKeywordContributors()) {
            try {
                defaultKeywords.put(keywordContributor.getKeywordClass().getName(), KeywordController.getInstance()
                        .getBuiltInKeywords(keywordContributor.getKeywordClass().getName()).get(0).getName());
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }

        store.setDefault(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORDS,
                convertKeywordMapToJsonArray(defaultKeywords).toString());

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

    protected static String callTestCaseMethodName() {
        return "callTestCase";
    }

    public static Map<String, String> getDefaultKeywords() {
        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.TestCasePreferenceConstants.QUALIFIER);
        String defaultKeywordJsonString = store
                .getString(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORDS);
        Map<String, String> defaultKeywords = new HashMap<String, String>();
        if (!StringUtils.isBlank(defaultKeywordJsonString)) {
            JsonArray parser = (JsonArray) new JsonParser().parse(defaultKeywordJsonString);
            for (int i = 0; i < parser.size(); i++) {
                JsonObject jsonObject = (JsonObject) parser.get(i);
                defaultKeywords.put(jsonObject.get("keywordType").getAsString(), jsonObject.get("keywordName")
                        .getAsString());
            }
        }

        return defaultKeywords;
    }

    public static IKeywordContributor getDefaultKeywordType() throws Exception {
        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.TestCasePreferenceConstants.QUALIFIER);
        String keywordType = store
                .getString(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORD_TYPE);
        IKeywordContributor contributor = KeywordController.getInstance().getBuiltInKeywordContributor(keywordType);
        if (contributor == null) {
            contributor = KeywordController.getInstance().getBuiltInKeywordContributors()[0];
        }
        return contributor;
    }

    public static String getDefaultMethodName(IKeywordContributor contributor) {
        Map<String, String> defaultKeywords = getDefaultKeywords();
        String contributingClassName = contributor.getKeywordClass().getName();
        String defaultMethodName = defaultKeywords.get(contributingClassName);
        return defaultMethodName;
    }

    public static void storeDefaultKeywords(Map<String, String> defaultKeywords) {
        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.TestCasePreferenceConstants.QUALIFIER);
        JsonArray keywordArray = convertKeywordMapToJsonArray(defaultKeywords);
        store.setValue(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORDS,
                keywordArray.toString());
    }

    private static JsonArray convertKeywordMapToJsonArray(Map<String, String> defaultKeywords) {
        JsonArray keywordArray = new JsonArray();
        for (Entry<String, String> entry : defaultKeywords.entrySet()) {
            JsonObject keywordJsonObject = new JsonObject();
            keywordJsonObject.add("keywordType", new JsonPrimitive(entry.getKey()));
            keywordJsonObject.add("keywordName", new JsonPrimitive(entry.getValue()));

            keywordArray.add(keywordJsonObject);
        }
        return keywordArray;
    }

    public static FailureHandling getDefaultFailureHandling() {
        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.TestCasePreferenceConstants.QUALIFIER);
        String failureHandlingName = store
                .getString(PreferenceConstants.TestCasePreferenceConstants.TESTCASE_DEFAULT_FAILURE_HANDLING);
        return FailureHandling.valueOf(failureHandlingName);
    }
}
