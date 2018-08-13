package com.kms.katalon.execution.generator

import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.entity.variable.VariableEntity
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

Map<String, String> evaluatedVariables = [:]

<% rawVariables.each { entry -> %>
evaluatedVariables.put("<%= entry.key %>", <%= entry.value %>.toString())
<% } %>

FileOutputStream fos = null
ObjectOutputStream oos = null
try {
   fos = new FileOutputStream(new File("<%= resultFile %>"))
   oos = new ObjectOutputStream(fos);
   oos.writeObject(evaluatedVariables)
} catch (Exception e) {
   e.printStackTrace()
} finally {
   if (fos != null) {
       fos.close()
   }

   if (oos != null) {
       oos.close()
   }
}
 
"""
   
   @CompileStatic
   def static generateEvaluationScript(String resultFile, Map<String, String> variables) {
       
       def binding = [
           "rawVariables": variables,
           "resultFile": GroovyStringUtil.escapeGroovy(resultFile) 
       ]
       
       def engine = new GStringTemplateEngine()
       def template = engine.createTemplate(tpl).make(binding)
       
       template.toString()
   }
}
