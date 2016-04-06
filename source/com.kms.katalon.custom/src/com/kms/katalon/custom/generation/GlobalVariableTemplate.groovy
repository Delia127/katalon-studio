package com.kms.katalon.custom.generation

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import groovy.text.GStringTemplateEngine

import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.entity.global.GlobalVariableEntity

class GlobalVariableTemplate {
    def static tpl = """
import ${TestDataFactory.class.getName()}
import ${ObjectRepository.class.getName()}
import groovy.transform.CompileStatic


@CompileStatic
class GlobalVariable {
	<% globalVariables.each { %> 
    /**
     * <p><%= GlobalVariableTemplate.escapeHtmlForJavadoc(it.getDescription()) %></p>
     */
	def static <%= it.getName() %> = <%= it.getInitValue() %>
	<% } %> 
}
"""
    def generateGlobalVarialbeFile(File file, List<GlobalVariableEntity> globalVariables) {
        def binding = [
            "globalVariables" : globalVariables,
            "GlobalVariableTemplate" : GlobalVariableTemplate.class
        ]
        
        def engine = new GStringTemplateEngine()        
        def tpl = engine.createTemplate(tpl).make(binding)
        if (file.canWrite()) {
            file.write(tpl.toString());
        }
    }
    
    def static escapeHtmlForJavadoc(String description) {
        return StringEscapeUtils.escapeHtml(description).replace("/", "&#47;")
    }
}
