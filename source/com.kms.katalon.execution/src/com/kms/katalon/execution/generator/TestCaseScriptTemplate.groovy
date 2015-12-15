package com.kms.katalon.execution.generator

import groovy.text.GStringTemplateEngine
import groovy.transform.CompileStatic

import com.kms.katalon.controller.TestCaseController
import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.driver.DriverCleanerCollector
import com.kms.katalon.core.keyword.IKeywordContributor
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.main.TestCaseMain
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCaseBinding
import com.kms.katalon.custom.factory.BuiltInMethodNodeFactory
import com.kms.katalon.entity.testcase.TestCaseEntity
import com.kms.katalon.execution.configuration.IRunConfiguration;
import com.kms.katalon.execution.util.ExecutionUtil

@CompileStatic
class TestCaseScriptTemplate {
    private final static String tpl =
    '''<% importNames.each { %>import <%= it %>
<% } %>

<% driverCleaners.each { %>DriverCleanerCollector.getInstance().addDriverCleaner(new <%= it %>())
<% } %>

RunConfiguration.setLogFile("<%= logFilePath %>")
RunConfiguration.setExecutionSettingFile("<%= executionConfigFilePath %>")

TestCaseMain.beforeStart()
try {
	TestCaseMain.runTestCase('<%= testCaseId %>', 
								<%= testCaseBinding %>, FailureHandling.STOP_ON_FAILURE)
} catch (Exception e) {
    TestCaseMain.logError('<%= testCaseId %>', e)
}
DriverCleanerCollector.getInstance().cleanDriversAfterRunningTestCase()
'''
    @CompileStatic
    def static generateTestCaseScriptFile(File file, TestCaseEntity testCase, String testCaseBinding, IRunConfiguration config) {
        def importNames = [
            TestCaseMain.class.getName(),
            KeywordLogger.class.getName(),
            MissingPropertyException.class.getName(),
            TestCaseBinding.class.getName(),
            DriverCleanerCollector.class.getName(),
            FailureHandling.class.getName(),
            RunConfiguration.class.getName()
        ]


        def driverCleaners = []
        for (IKeywordContributor contributor in BuiltInMethodNodeFactory.getInstance().getKeywordContributors()) {
            if (contributor.getDriverCleaner() != null) {
                driverCleaners.add(contributor.getDriverCleaner().getName())
            }
        }

        importNames.addAll(driverCleaners)

        String testCaseId = TestCaseController.getInstance().getIdForDisplay(testCase)

        def binding = [
            "importNames"     : importNames,
            "testCaseId"      : testCaseId,
            "testCaseBinding" : testCaseBinding,
            "executionConfigFilePath" : config.getExecutionSettingFilePath(),
            "logFilePath" : config.getLogFilePath(),
            "driverCleaners" : driverCleaners
        ]

        def engine = new GStringTemplateEngine()
        def tpl = engine.createTemplate(tpl).make(binding)
        if (file.canWrite()) {
            file.write(tpl.toString());
        }
    }
}
