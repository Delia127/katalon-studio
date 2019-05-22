package com.kms.katalon.execution.generator

import com.kms.katalon.core.configuration.RunConfiguration
import com.kms.katalon.core.main.TestCaseMain
import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.entity.variable.VariableEntity
import com.kms.katalon.execution.configuration.IRunConfiguration
import com.kms.katalon.groovy.util.GroovyStringUtil
import groovy.text.GStringTemplateEngine
import groovy.transform.CompileStatic

@CompileStatic
class VariableEvaluationScriptTemplate {
    
   private static final String tpl = """\
    
import ${TestDataFactory.class.getName()} as ${TestDataFactory.class.getSimpleName()}
import ${ObjectRepository.class.getName()} as ${ObjectRepository.class.getSimpleName()}
import ${TestCaseFactory.class.getName()} as ${TestCaseFactory.class.getSimpleName()}
import static ${TestDataFactory.class.getName()}.findTestData
import static ${ObjectRepository.class.getName()}.findTestObject
import static ${TestCaseFactory.class.getName()}.findTestCase
import internal.GlobalVariable as GlobalVariable
import ${RunConfiguration.class.getName()}
import ${TestCaseMain.class.getName()}

RunConfiguration.setExecutionSettingFile('<%= executionConfigFilePath %>')

TestCaseMain.beforeStart()

Map<String, String> evaluatedVariables = [:]

<% rawVariables.each { entry -> %>
evaluatedVariables.put("<%= entry.key %>", <%= entry.value %>.toString())
<% } %>

return evaluatedVariables
 
"""
   
   @CompileStatic
   def static generateEvaluationScript(Map<String, String> variables, IRunConfiguration config) {
       
       def binding = [
           "rawVariables": variables,
           "executionConfigFilePath": GroovyStringUtil.escapeGroovy(config.getExecutionSetting().getSettingFilePath())
       ]
       
       def engine = new GStringTemplateEngine()
       def template = engine.createTemplate(tpl).make(binding)
       
       template.toString()
   }
}
