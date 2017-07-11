package com.kms.katalon.composer.testcase.preferences;

import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.kms.katalon.composer.testcase.constants.TestCasePreferenceConstants;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.parts.TestCaseCompositePart;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.keyword.internal.IKeywordContributor;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.custom.keyword.KeywordClass;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

public class TestCasePreferenceDefaultValueInitializer extends AbstractPreferenceInitializer {
    private static final String CALL_TEST_CASE = "callTestCase";

    private static final int MAXIMUM_RECENT_KEYWORDS_NUM = 10;

    private static ScopedPreferenceStore getStore() {
        return getPreferenceStore(TestCasePreferenceDefaultValueInitializer.class);
    }

    @Override
    public void initializeDefaultPreferences() {
        ScopedPreferenceStore store = getStore();

        // Default value type of variable
        store.setDefault(TestCasePreferenceConstants.TESTCASE_DEFAULT_VARIABLE_TYPE, InputValueType.String.name());

        // Test Case Calling
        store.setDefault(TestCasePreferenceConstants.TESTCASE_GENERATE_DEFAULT_VARIABLE_VALUE, true);
        store.setDefault(TestCasePreferenceConstants.TESTCASE_AUTO_EXPORT_VARIABLE, false);

        // Default Added Keyword
        store.setDefault(TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORD_TYPE,
                KeywordController.getInstance().getBuiltInKeywordClasses().get(0).getName());
        Map<String, String> defaultKeywords = new HashMap<String, String>();
        for (KeywordClass keywordClass : KeywordController.getInstance().getBuiltInKeywordClasses()) {
            defaultKeywords.put(keywordClass.getName(), KeywordController.getInstance()
                    .getBuiltInKeywords(keywordClass.getSimpleName(), true)
                    .get(0)
                    .getName());
        }

        store.setDefault(TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORDS,
                convertKeywordMapToJsonArray(defaultKeywords).toString());

        // Default Failure Handling
        store.setDefault(TestCasePreferenceConstants.TESTCASE_DEFAULT_FAILURE_HANDLING,
                FailureHandling.STOP_ON_FAILURE.name());

        // Default view for test case part
        store.setDefault(TestCasePreferenceConstants.TESTCASE_PART_DEFAULT_START_VIEW,
                TestCaseCompositePart.MANUAL_TAB_TITLE);

        // Default recent keywords
        Type listType = new TypeToken<List<String>>() {}.getType();
        store.setDefault(TestCasePreferenceConstants.TESTCASE_RECENT_KEYWORDS,
                JsonUtil.toJson(Collections.emptyList(), listType, false));
    }

    public static boolean isSetGenerateVariableDefaultValue() {
        return getStore().getBoolean(TestCasePreferenceConstants.TESTCASE_GENERATE_DEFAULT_VARIABLE_VALUE);
    }

    public static boolean isSetAutoExportVariables() {
        return getStore().getBoolean(TestCasePreferenceConstants.TESTCASE_AUTO_EXPORT_VARIABLE);
    }

    protected static String callTestCaseMethodName() {
        return CALL_TEST_CASE;
    }

    public static Map<String, String> getDefaultKeywords() {
        String defaultKeywordJsonString = getStore().getString(TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORDS);
        Map<String, String> defaultKeywords = new HashMap<String, String>();
        if (!StringUtils.isBlank(defaultKeywordJsonString)) {
            JsonArray parser = (JsonArray) new JsonParser().parse(defaultKeywordJsonString);
            for (int i = 0; i < parser.size(); i++) {
                JsonObject jsonObject = (JsonObject) parser.get(i);
                defaultKeywords.put(jsonObject.get("keywordType").getAsString(),
                        jsonObject.get("keywordName").getAsString());
            }
        }

        return defaultKeywords;
    }

    public static KeywordClass getDefaultKeywordType() {
        String keywordType = getStore().getString(TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORD_TYPE);
        KeywordClass contributor = KeywordController.getInstance().getBuiltInKeywordClassByName(keywordType);
        if (contributor == null) {
            contributor = KeywordController.getInstance().getBuiltInKeywordClasses().get(0);
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
        JsonArray keywordArray = convertKeywordMapToJsonArray(defaultKeywords);
        getStore().setValue(TestCasePreferenceConstants.TESTCASE_DEFAULT_KEYWORDS, keywordArray.toString());
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
        String failureHandlingName = getStore()
                .getString(TestCasePreferenceConstants.TESTCASE_DEFAULT_FAILURE_HANDLING);
        return FailureHandling.valueOf(failureHandlingName);
    }

    public static String getTestCasePartStartView() {
        return getStore().getString(TestCasePreferenceConstants.TESTCASE_PART_DEFAULT_START_VIEW);
    }

    public static List<StoredKeyword> getRecentKeywords() {
        String recentKeywordsAsJSONString = getStore().getString(TestCasePreferenceConstants.TESTCASE_RECENT_KEYWORDS);
        Type listType = new TypeToken<List<StoredKeyword>>() {}.getType();
        return JsonUtil.fromJson(recentKeywordsAsJSONString, listType);
    }

    public static void addNewRecentKeywords(StoredKeyword recent) {
        List<StoredKeyword> recentKeywords = new LinkedList<>(getRecentKeywords());
        if (recentKeywords.contains(recent)) {
            recentKeywords.remove(recent);
        }
        recentKeywords.add(0, recent);
        String recentKeywordsAsJSONString = JsonUtil
                .toJson(recentKeywords.subList(0, Math.min(MAXIMUM_RECENT_KEYWORDS_NUM, recentKeywords.size())), false);
        getStore().setValue(TestCasePreferenceConstants.TESTCASE_RECENT_KEYWORDS, recentKeywordsAsJSONString);
    }

    public static void saveStore() throws IOException {
        if (getStore().needsSaving()) {
            getStore().save();
        }
    }
}
