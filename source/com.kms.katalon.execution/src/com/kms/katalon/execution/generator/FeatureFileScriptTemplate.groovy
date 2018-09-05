package com.kms.katalon.execution.generator;

import org.apache.commons.lang3.StringUtils

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.driver.internal.DriverCleanerCollector
import com.kms.katalon.core.keyword.internal.IKeywordContributor
import com.kms.katalon.core.keyword.internal.KeywordContributorCollection
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.core.main.TestCaseMain
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCaseBinding
import com.kms.katalon.entity.file.SystemFileEntity
import com.kms.katalon.entity.testcase.WSVerificationTestCaseEntity
import com.kms.katalon.execution.configuration.IRunConfiguration
import com.kms.katalon.execution.util.ExecutionUtil
import com.kms.katalon.groovy.constant.GroovyConstants
import com.kms.katalon.groovy.util.GroovyStringUtil

import groovy.text.GStringTemplateEngine
import groovy.transform.CompileStatic

public class FeatureFileScriptTemplate {
    private final static String tpl =
    '''<% importNames.each { %>import <%= it %>
<% } %>

<% driverCleaners.each { %>DriverCleanerCollector.getInstance().addDriverCleaner(new <%= it %>())
<% } %>

RunConfiguration.setExecutionSettingFile('<%= executionConfigFilePath %>')

TestCaseMain.beforeStart()
TestCaseMain.runFeatureFile('<%= featureFileRelativePath %>')
'''

    @CompileStatic
    def static generateTestCaseScriptFile(File file, SystemFileEntity featureFile, IRunConfiguration config) {
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

        def binding = [
            "importNames"     : importNames,
            "executionConfigFilePath" : GroovyStringUtil.escapeGroovy(config.getExecutionSetting().getSettingFilePath()),
            "isQuitDriversAfterRun" : ExecutionUtil.isQuitDriversAfterExecutingTestCase(),
            "driverCleaners" : driverCleaners,
            "featureFileRelativePath": featureFile.getRelativePath()
        ]

        def engine = new GStringTemplateEngine()
        Writable tempTestCaseContentWritable = engine.createTemplate(tpl).make(binding)
        if (file.canWrite()) {
            file.write(tempTestCaseContentWritable.toString(), GroovyConstants.DF_CHARSET);
        }
    }
}
