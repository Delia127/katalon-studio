package com.kms.katalon.execution.generator;

import groovy.text.GStringTemplateEngine
import groovy.transform.CompileStatic

import static com.kms.katalon.constants.GlobalStringConstants.CR_SPACE
import static com.kms.katalon.constants.GlobalStringConstants.CR_DOT
import static com.kms.katalon.core.constants.StringConstants.METHOD_FIND_TEST_CASE
import static com.kms.katalon.core.constants.StringConstants.METHOD_FIND_TEST_OBJECT
import static com.kms.katalon.core.constants.StringConstants.METHOD_FIND_TEST_DATA

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.driver.internal.DriverCleanerCollector
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.internal.IKeywordContributor
import com.kms.katalon.core.keyword.internal.KeywordContributorCollection
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.main.TestCaseMain
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.reporting.ReportUtil
import com.kms.katalon.core.testcase.TestCaseBinding
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestDataColumn
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.entity.testsuite.TestSuiteEntity
import com.kms.katalon.execution.configuration.IRunConfiguration
import com.kms.katalon.execution.entity.IExecutedEntity
import com.kms.katalon.execution.entity.TestCaseExecutedEntity
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity
import com.kms.katalon.execution.util.ExecutionUtil
import com.kms.katalon.groovy.util.GroovyStringUtil

@CompileStatic
public class TestSuiteScriptTemplate {
    private static final String tpl ='''<% importNames.each { %>import <%= it %>
<% } %>
Map<String, String> suiteProperties = new HashMap<String, String>();

<% configProperties.each { k, v -> %>
suiteProperties.put('<%= k %>', '<%= v %>')
<% } %> 

<% driverCleaners.each { %>DriverCleanerCollector.getInstance().addDriverCleaner(new <%= it %>())
<% } %>


RunConfiguration.setExecutionSettingFile("<%= executionConfigFilePath %>")

TestCaseMain.beforeStart()

TestCaseMain.startTestSuite('<%= testSuite.getIdForDisplay() %>', suiteProperties, new File("<%=  testCaseBindingFile %>"))
'''
    private static final String STATIC = "static";

    def static buildStaticImportName(String className, String methodName) {
        STATIC + CR_SPACE + className + CR_DOT + methodName
    }

    @CompileStatic
    def static generateTestSuiteScriptFile(File file, TestSuiteEntity testSuite, File testCaseBindingFile,
            IRunConfiguration runConfig, TestSuiteExecutedEntity testSuiteExecutedEntity) {

        def importNames = [
            KeywordLogger.class.getName(),
            StepFailedException.class.getName(),
            ReportUtil.class.getName(),
            TestCaseMain.class.getName(),
            TestDataColumn.class.getName(),
            TestCaseBinding.class.getName(),
            DriverCleanerCollector.class.getName(),
            FailureHandling.class.getName(),
            RunConfiguration.class.getName(),
            buildStaticImportName(TestCaseFactory.class.getName(), METHOD_FIND_TEST_CASE),
            buildStaticImportName(ObjectRepository.class.getName(), METHOD_FIND_TEST_OBJECT),
            buildStaticImportName(TestDataFactory.class.getName(), METHOD_FIND_TEST_DATA),
            "internal.GlobalVariable as GlobalVariable"
        ]

        def driverCleaners = []
        for (IKeywordContributor contributor in KeywordContributorCollection.getKeywordContributors()) {
            if (contributor.getDriverCleaner() != null) {
                driverCleaners.add(contributor.getDriverCleaner().getName())
            }
        }

        List<String> testCaseIds = new ArrayList<String>();
        for (IExecutedEntity testCaseExecutedEntity in testSuiteExecutedEntity.getExecutedItems()) {
            for (int index = 0; index < ((TestCaseExecutedEntity) testCaseExecutedEntity).getLoopTimes(); index++) {
                testCaseIds.add(testCaseExecutedEntity.getSourceId())
            }
        }

        def binding = [
            "importNames": importNames,
            "testSuite" : testSuite,
            "testCaseIds": testCaseIds,
            "testCaseBindingFile": GroovyStringUtil.escapeGroovy(testCaseBindingFile.getAbsolutePath()),
            "configProperties" : ExecutionUtil.escapeGroovy(testSuiteExecutedEntity.getAttributes()),
            "executionConfigFilePath" : GroovyStringUtil.escapeGroovy(runConfig.getExecutionSetting().getSettingFilePath()),
            "driverCleaners" : driverCleaners,
            "isQuitDriversAfterTestCase" : ExecutionUtil.isQuitDriversAfterExecutingTestCase(),
            "isQuitDriversAfterRun" : ExecutionUtil.isQuitDriversAfterExecutingTestSuite(),
            "trigger": 'runTestCase_${it}'
        ]

        def template = new GStringTemplateEngine()
                            .createTemplate(tpl)
                            .make(binding)
                            .toString()

        if (file != null && file.canWrite()) {
             file.write(template)
        }
        return template
    }
}
