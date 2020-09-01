package com.kms.katalon.selenium.ide.util;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.selenium.ide.model.TestCase;
import com.kms.katalon.selenium.ide.model.TestSuite;

public final class JsonParser {

    public static ParsedResult parse(File file) {
        String projectJsonContent = FileUtils.readFileToString(file.toPath());
        JsonObject convertedProject = JsonUtil.fromJson(projectJsonContent, JsonObject.class);

        Type testCaseListType = new TypeToken<ArrayList<TestCase>>() {
        }.getType();
        List<TestCase> testCases = JsonUtil.fromJson(convertedProject.get("tests").toString(), testCaseListType);
        String baseUrl = convertedProject.get("url").getAsString();

        Type testSuiteListType = new TypeToken<ArrayList<TestSuite>>() {
        }.getType();
        List<TestSuite> testSuites = JsonUtil.fromJson(convertedProject.get("suites").toString(), testSuiteListType);

        Map<String, String> monoSuiteTests = new HashMap<>();
        testSuites.forEach(ts -> {
            ts.setName(toValidFileName(ts.getName()));
            ts.getTests().forEach(id -> {
                if (!monoSuiteTests.containsKey(id))
                    monoSuiteTests.put(id, ts.getName());
                else if (monoSuiteTests.get(id) != null)
                    monoSuiteTests.put(id, null); // already existed in a suite
            });
        });

        testCases.forEach(tc -> {
            tc.setBaseUrl(baseUrl);
            tc.setName(toValidFileName(tc.getName()));
            tc.setFilePath(file.getAbsolutePath());
        });

        return new ParsedResult(testSuites, testCases, monoSuiteTests);
    }

    private static String toValidFileName(String fileName) {
        return fileName.trim().replaceAll("[^A-Za-z-0-9_().\\- ]", "");
    }
}
