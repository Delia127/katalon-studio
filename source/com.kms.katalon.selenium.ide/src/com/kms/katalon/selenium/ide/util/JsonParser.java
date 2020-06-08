package com.kms.katalon.selenium.ide.util;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.selenium.ide.model.TestCase;
import com.kms.katalon.selenium.ide.model.TestSuite;

public final class JsonParser {

    public static ParsedResult parse(File file) {
        String projectJsonContent = FileUtils.readFileToString(file.toPath());
        JsonObject convertedProject = JsonUtil.fromJson(projectJsonContent, JsonObject.class);
        JsonArray testSuiteJsonArray = convertedProject.getAsJsonArray("suites");

        Type testCaseListType = new TypeToken<ArrayList<TestCase>>() {
        }.getType();
        List<TestCase> testCases = JsonUtil.fromJson(convertedProject.get("tests").toString(), testCaseListType);
        String baseUrl = convertedProject.get("url").getAsString();

        Map<String, TestCase> testCaseMap = new HashMap<>();
        testCases.forEach(tc -> {
            testCaseMap.put(tc.getId(), tc);
            tc.setBaseUrl(baseUrl);
            tc.setName(toValidFileName(tc.getName()));
            tc.setFilePath(file.getAbsolutePath());
        });

        List<TestSuite> testSuites = new ArrayList<>();
        testSuiteJsonArray.forEach(ts -> {
            TestSuite testSuite = parseTestSuite((JsonObject) ts, testCaseMap);
            testSuites.add(testSuite);
        });

        return new ParsedResult(testSuites, new ArrayList<TestCase>(testCaseMap.values()));
    }

    private static TestSuite parseTestSuite(JsonObject testSuiteJsonObject, Map<String, TestCase> testCaseMap) {
        JsonArray testIds = testSuiteJsonObject.getAsJsonArray("tests");
        List<TestCase> testCases = new ArrayList<>();
        testIds.forEach(id -> {
            TestCase tc = testCaseMap.remove(id.getAsString());
            testCases.add(tc);
        });

        TestSuite testSuite = new TestSuite();
        testSuite.setName(toValidFileName(testSuiteJsonObject.get("name").getAsString()));
        testSuite.setTestCases(testCases);

        return testSuite;
    }

    private static String toValidFileName(String fileName) {
        return fileName.trim().replaceAll("[^A-Za-z-0-9_().\\- ]", "");
    }
}
