package com.kms.katalon.usagetracking;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonObject;
import com.kms.katalon.composer.components.util.FileUtil;
import com.kms.katalon.console.utils.ApplicationInfo;
import com.kms.katalon.console.utils.ServerAPICommunicationUtil;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.logging.LogUtil;

public class UsageInfoCollector {
    private static final String TEST_CASE_FOLDER = "Test Cases";

    private static final String REPORT_FOLDER = "Reports";

    private static final String ORG_TIME_KEY = "orgTime";

    private static final String NUM_TEST_CASE_KEY = "ntc";

    private static final String NUM_TEST_RUN_KEY = "ntr";

    private static final String EMAIL_KEY = "email";

    public static void colllect() {
        UsageInformation usageInfo = getUsageInfo();
        JsonObject jsObject = new JsonObject();
        JsonObject jsTraits = new JsonObject();
        jsTraits.addProperty("kat_version", usageInfo.getVersion());
        jsTraits.addProperty("project", usageInfo.getProjectCount());
        jsTraits.addProperty("test_case", usageInfo.getTestCaseCount());
        jsTraits.addProperty("test_run", usageInfo.getTestCaseRunCount());
        jsTraits.addProperty("new_project", usageInfo.getNewProjectCount());
        jsTraits.addProperty("new_test_case", usageInfo.getNewTestCaseCount());
        jsTraits.addProperty("new_test_run", usageInfo.getNewTestRunCount());

        jsObject.add("traits", jsTraits);
        jsObject.addProperty("userId", usageInfo.getEmail());
        if (usageInfo.getTestCaseCount() > 0 && usageInfo.getTestCaseRunCount() > 0) {
            sendUsageInfo(jsObject, usageInfo);
        }
    }

    private static void sendUsageInfo(JsonObject jsObject, UsageInformation usageInfo) {
        try {
            ServerAPICommunicationUtil.post("/product/usage", jsObject.toString());
            LogUtil.logErrorMessage(jsObject.toString());
            ApplicationInfo.setAppProperty(NUM_TEST_CASE_KEY, usageInfo.getTestCaseCount() + "", true);
            ApplicationInfo.setAppProperty(NUM_TEST_RUN_KEY, usageInfo.getTestCaseRunCount() + "", true);
            ApplicationInfo.setAppProperty(ORG_TIME_KEY, new Date().getTime() + "", true);
        } catch (Exception ex) {
            LogUtil.logError(ex);
        }
    }

    private static Date restorePreviousUsageInfo(UsageInformation usageInfo) {
        try {
            String sTime = ApplicationInfo.getAppProperty(ORG_TIME_KEY);
            if (sTime == null) {
                return new Date(0);
            }
            usageInfo.setTestCaseCount(Integer.parseInt(ApplicationInfo.getAppProperty(NUM_TEST_CASE_KEY)));
            usageInfo.setTestCaseRunCount(Integer.parseInt(ApplicationInfo.getAppProperty(NUM_TEST_RUN_KEY)));
            return new Date(Long.parseLong(sTime));
        } catch (Exception ex) {
            LogUtil.logError(ex);
            usageInfo.setTestCaseCount(0);
            usageInfo.setTestCaseRunCount(0);
            return new Date(0);
        }
    }

    public static UsageInformation getUsageInfo() {
        UsageInformation usageInfo = new UsageInformation();
        Date orgTime = restorePreviousUsageInfo(usageInfo);
        List<String> projectPaths = getRecentProjects();
        collectGeneralInfo(usageInfo);
        for (String prjPath : projectPaths) {
            collectUsageProjectInfo(prjPath, usageInfo, orgTime);
        }
        return usageInfo;
    }

    private static void collectGeneralInfo(UsageInformation usageInfo) {
        usageInfo.setEmail(ApplicationInfo.getAppProperty(EMAIL_KEY));
        usageInfo.setVersion(ApplicationInfo.versionNo() + " build " + ApplicationInfo.buildNo());
    }

    private static void collectUsageProjectInfo(String prjPath, UsageInformation usageInfo, Date orgTime) {
        File reportFolder = new File(prjPath + File.separator + REPORT_FOLDER);
        File testcaseFolder = new File(prjPath + File.separator + TEST_CASE_FOLDER);
        File[] csvFiles = FileUtil.getFiles(reportFolder, ".csv", orgTime);
        int newTestRunCount = 0, newTestcaseCount = 0;

        if (FileUtil.isFileCreateAfter(new File(prjPath), orgTime)) {
            usageInfo.setNewProjectCount(usageInfo.getNewProjectCount() + 1);
        }
        usageInfo.setProjectCount(usageInfo.getProjectCount() + 1);
        newTestcaseCount = FileUtil.countAllFiles(testcaseFolder, ".tc", orgTime);
        usageInfo.setNewTestCaseCount(usageInfo.getNewTestCaseCount() + newTestcaseCount);
        usageInfo.setTestCaseCount(usageInfo.getTestCaseCount() + newTestcaseCount);
        for (File csvfile : csvFiles) {
            newTestRunCount += getNumberTestRun(csvfile);
        }
        usageInfo.setNewTestRunCount(usageInfo.getNewTestRunCount() + newTestRunCount);
        usageInfo.setTestCaseRunCount(usageInfo.getTestCaseRunCount() + newTestRunCount);
    }

    private static int getNumberTestRun(File csvFile) {
        int count = 0;
        try {
            List<String> lines = Files.readAllLines(csvFile.toPath(), Charset.forName("UTF-8"));
            for (String line : lines) {
                if (line.startsWith(TEST_CASE_FOLDER + "/")) {
                    count++;
                }
            }
        } catch (Exception ex) {
            LogUtil.logError(ex);
        }
        return count;
    }

    private static List<String> getRecentProjects() {
        List<String> projectPaths = new ArrayList<>();
        try {
            for (ProjectEntity prEntity : ProjectController.getInstance().getRecentProjects()) {
                projectPaths.add(prEntity.getFolderLocation());
            }
        } catch (Exception ex) {
            LogUtil.logError(ex);
        }
        return projectPaths;
    }
}
