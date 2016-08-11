package com.kms.katalon.custom.generation

import groovy.text.GStringTemplateEngine
import groovy.transform.CompileStatic;

import org.apache.commons.lang.StringEscapeUtils

import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.custom.parser.GlobalVariableParser;
import com.kms.katalon.entity.global.GlobalVariableEntity

class GlobalVariableTemplate {
    private static final String DEPRECATED_STRING = "@deprecated Please use " + GlobalVariableParser.INTERNAL_PACKAGE_NAME + "." + GlobalVariableParser.GLOBAL_VARIABLE_CLASS_NAME +" instead";
    private static final String PACKAGE_STRING = "package " + GlobalVariableParser.INTERNAL_PACKAGE_NAME;
    
    private static final String tpl = """<% if (!deprecatedFlag) { %>${PACKAGE_STRING}<% } %>
import ${TestDataFactory.class.getName()}
import ${ObjectRepository.class.getName()}
import groovy.transform.CompileStatic


/**
 * This class is generated automatically by Katalon Studio and should not be modified or deleted.
 * <% if (deprecatedFlag) { %>${DEPRECATED_STRING}<% } %>
 */
<% if (deprecatedFlag) { %>@Deprecated<% } %>
@CompileStatic
class GlobalVariable {
	<% globalVariables.each { %> 
    /**
     * <p><%= GlobalVariableTemplate.escapeHtmlForJavadoc(it.getDescription()) %></p>
     */
	public static final Object <%= it.getName() %> = <%= it.getInitValue() %>
	<% } %> 
}
"""

    @CompileStatic
    public static void generateGlobalVariableFile(File file, List<GlobalVariableEntity> globalVariables, boolean isDeprecated) {
        def binding = [
            "deprecatedFlag" : isDeprecated,
            "globalVariables" : globalVariables,
            "GlobalVariableTemplate" : GlobalVariableTemplate.class
        ]

        def engine = new GStringTemplateEngine()
        def tpl = engine.createTemplate(tpl).make(binding)
        if (file.canWrite()) {
            file.write(tpl.toString());
        }
    }

    @CompileStatic
    public static String escapeHtmlForJavadoc(String description) {
        return StringEscapeUtils.escapeHtml(description).replace("/", "&#47;")
    }
}
