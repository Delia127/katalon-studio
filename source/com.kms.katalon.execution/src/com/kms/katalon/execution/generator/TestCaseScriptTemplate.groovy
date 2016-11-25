package com.kms.katalon.execution.generator

import groovy.text.GStringTemplateEngine
import groovy.transform.CompileStatic

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.driver.DriverCleanerCollector
import com.kms.katalon.core.keyword.IKeywordContributor
import com.kms.katalon.core.keyword.KeywordContributorCollection
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.main.TestCaseMain
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCaseBinding
import com.kms.katalon.entity.testcase.TestCaseEntity
import com.kms.katalon.execution.configuration.IRunConfiguration
import com.kms.katalon.execution.util.ExecutionUtil;
import com.kms.katalon.groovy.util.GroovyStringUtil;
import com.kms.katalon.groovy.util.GroovyUtil;

@CompileStatic
class TestCaseScriptTemplate {
    private final static String executeRawTpl = "'''<%= rawScript %>'''";
    private final static String tpl =
    '''<% importNames.each { %>import <%= it %>
<% } %>

<% driverCleaners.each { %>DriverCleanerCollector.getInstance().addDriverCleaner(new <%= it %>())
<% } %>

RunConfiguration.setExecutionSettingFile('<%= executionConfigFilePath %>')

TestCaseMain.beforeStart()
try {
    <% if (rawScript == null) { %>
	    TestCaseMain.runTestCase('<%= testCaseId %>', <%= testCaseBinding %>, FailureHandling.STOP_ON_FAILURE <%= isQuitDriversAfterRun ? ", true" : "" %>)
    <% } else { %>
        TestCaseMain.runTestCaseRawScript(
''' + executeRawTpl + ''', '<%= testCaseId %>', <%= testCaseBinding %>, FailureHandling.STOP_ON_FAILURE <%= isQuitDriversAfterRun ? ", true" : "" %>)
    <% } %>
} catch (Exception e) {
    TestCaseMain.logError(e, '<%= testCaseId %>')
}
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
        for (IKeywordContributor contributor in KeywordContributorCollection.getKeywordContributors()) {
            if (contributor.getDriverCleaner() != null) {
                driverCleaners.add(contributor.getDriverCleaner().getName())
            }
        }

        importNames.addAll(driverCleaners)

        String testCaseId = testCase.getIdForDisplay()

        def binding = [
            "importNames"     : importNames,
            "testCaseId"      : testCaseId,
            "testCaseBinding" : testCaseBinding,
            "executionConfigFilePath" : GroovyStringUtil.escapeGroovy(config.getExecutionSetting().getSettingFilePath()),
            "isQuitDriversAfterRun" : ExecutionUtil.isQuitDriversAfterExecuting(),
            "driverCleaners" : driverCleaners,
            "rawScript" : config.getExecutionSetting().getRawScript()
        ]

        def engine = new GStringTemplateEngine()
        Writable tempTestCaseContentWritable = engine.createTemplate(tpl).make(binding)
        if (file.canWrite()) {
            file.write(tempTestCaseContentWritable.toString());
        }
    }
}
