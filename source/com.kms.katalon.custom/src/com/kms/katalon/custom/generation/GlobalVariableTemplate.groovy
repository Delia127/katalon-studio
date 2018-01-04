package com.kms.katalon.custom.generation

import groovy.text.GStringTemplateEngine
import groovy.transform.CompileStatic

import org.apache.commons.lang.StringEscapeUtils

import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.custom.parser.GlobalVariableParser
import com.kms.katalon.entity.global.GlobalVariableEntity

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

class GlobalVariableTemplate {
    private static final String DEPRECATED_STRING = "@deprecated Please use " + GlobalVariableParser.INTERNAL_PACKAGE_NAME + "." + GlobalVariableParser.GLOBAL_VARIABLE_CLASS_NAME +" instead";
    private static final String PACKAGE_STRING = "package " + GlobalVariableParser.INTERNAL_PACKAGE_NAME;
    private static final String FIND_TEST_CASE_METHOD_NAME = "findTestCase";

    private static final String FIND_TEST_DATA_METHOD_NAME = "findTestData";

    private static final String FIND_TEST_OBJECT_METHOD_NAME = "findTestObject";

    private static final String tpl = """<% if (!deprecatedFlag) { %>${PACKAGE_STRING}<% } %>
import ${ObjectRepository.class.getName()} as ${ObjectRepository.class.getSimpleName()}
import ${TestDataFactory.class.getName()} as ${TestDataFactory.class.getSimpleName()}
import ${TestCaseFactory.class.getName()} as ${TestCaseFactory.class.getSimpleName()}
import static ${ObjectRepository.class.getName()}.${FIND_TEST_OBJECT_METHOD_NAME}
import static ${TestDataFactory.class.getName()}.${FIND_TEST_DATA_METHOD_NAME}
import static ${TestCaseFactory.class.getName()}.${FIND_TEST_CASE_METHOD_NAME}
import groovy.transform.CompileStatic


/**
 * This class is generated automatically by Katalon Studio and should not be modified or deleted.
 * <% if (deprecatedFlag) { %>${DEPRECATED_STRING}<% } %>
 */
<% if (deprecatedFlag) { %>@Deprecated<% } %>
@CompileStatic
public class GlobalVariable {
	<% globalVariables.each { %> 
    /**
     * <p><%= GlobalVariableTemplate.escapeHtmlForJavadoc(it.getDescription()) %></p>
     */
	public static Object <%= it.getName() %> = <%= it.getInitValue() %>
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
        
        def out
        try {
            out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)
            tpl.writeTo(out)
        } catch (Exception e) {
            
        } finally {
            out.close();
        }
        
//        
//        if (file.canWrite()) {
//            file.write(tpl.toString());
//        }
    }

    @CompileStatic
    public static String escapeHtmlForJavadoc(String description) {
        return StringEscapeUtils.escapeHtml(description).replace("/", "&#47;")
    }
}
