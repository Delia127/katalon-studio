package com.kms.katalon.custom.generation

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat

import org.apache.commons.lang.StringEscapeUtils
import org.apache.commons.lang.StringUtils

import com.kms.katalon.core.testcase.TestCaseFactory
import com.kms.katalon.core.testdata.TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository
import com.kms.katalon.custom.parser.GlobalVariableParser
import com.kms.katalon.entity.global.ExecutionProfileEntity
import com.kms.katalon.entity.global.GlobalVariableEntity

import groovy.text.GStringTemplateEngine

class GlobalVariableTemplate {
    private static final String PACKAGE_STRING = "package " + GlobalVariableParser.INTERNAL_PACKAGE_NAME;

    private static final String FIND_TEST_CASE_METHOD_NAME = "findTestCase";

    private static final String FIND_TEST_DATA_METHOD_NAME = "findTestData";

    private static final String FIND_TEST_OBJECT_METHOD_NAME = "findTestObject";

    private static final String tpl = """\
${PACKAGE_STRING}

import com.kms.katalon.core.configuration.RunConfiguration
import ${ObjectRepository.class.getName()} as ${ObjectRepository.class.getSimpleName()}
import ${TestDataFactory.class.getName()} as ${TestDataFactory.class.getSimpleName()}
import ${TestCaseFactory.class.getName()} as ${TestCaseFactory.class.getSimpleName()}
import static ${ObjectRepository.class.getName()}.${FIND_TEST_OBJECT_METHOD_NAME}
import static ${TestDataFactory.class.getName()}.${FIND_TEST_DATA_METHOD_NAME}
import static ${TestCaseFactory.class.getName()}.${FIND_TEST_CASE_METHOD_NAME}

/**
 * This class is generated automatically by Katalon Studio and should not be modified or deleted.
 */
public class GlobalVariable {
    <% globalVariables.each { entry -> %> 
    /**
     * <p><%= GlobalVariableTemplate.escapeHtmlForJavadoc(entry.value.getDescription()) %></p>
     */
    public static Object <%= entry.value.getName() %>
    <% } %> 

    static {
        def allVariables = [:]\
        <% executionProfiles.each { %>
        allVariables.put(\
'<%= it.getName() %>', \
<% if (!it.isDefaultProfile()) { %>allVariables['default'] + <% } %>\
[<% it.getGlobalVariableEntities().eachWithIndex { v, i -> %>\
<% if (i>0) { %>, <% } %>'<%=v.getName()%>' : <%=v.getInitValue()%><% } %>\
<% if (it.getGlobalVariableEntities().isEmpty()) {%>:<% } %>\
]\
)<% } %>
        
        String profileName = RunConfiguration.getExecutionProfile()
        def selectedVariables = allVariables[profileName]
		
		for(object in selectedVariables){
			String overridingGlobalVariable = RunConfiguration.getOverridingGlobalVariable(object.key)
			if(overridingGlobalVariable != null){
				selectedVariables.put(object.key, overridingGlobalVariable)
			}
		}

        <% globalVariables.each { entry -> %>\
<%=entry.value.getName()%> = selectedVariables["<%=entry.value.getName()%>"]
        <% } %>
    }
}
"""

    private static String concatDescriptions(String oldDes, String newDes, ExecutionProfileEntity profile) {
        if (StringUtils.isEmpty(newDes)) {
            return oldDes
        }
        String newDesForProfile = MessageFormat.format("Profile {0} : {1}", profile.getName(), newDes)
        if (StringUtils.isEmpty(oldDes)) {
            return newDesForProfile
        }
        return oldDes + "\n" + newDesForProfile
    }

    public static void generateGlobalVariableFile(File file, List<ExecutionProfileEntity> profiles) {
        Map declaredGlobalVariables = [:]
        profiles.each { p ->
            p.getGlobalVariableEntities().each {
                String variableName = it.getName()
                GlobalVariableEntity variable
                if (declaredGlobalVariables.containsKey(variableName)) {
                    variable = declaredGlobalVariables.get(variableName)
                } else {
                    variable = new GlobalVariableEntity()
                    variable.setName(variableName)
                }

                String concatDes = concatDescriptions(variable.getDescription(), it.getDescription(), p)
                variable.setDescription(concatDes)

                declaredGlobalVariables.put(variableName, variable)
            }
        }
        def binding = [
            "GlobalVariableTemplate" : GlobalVariableTemplate.class,
            "globalVariables": declaredGlobalVariables,
            "executionProfiles" : profiles
        ]

        def engine = new GStringTemplateEngine()
        def template = engine.createTemplate(tpl).make(binding)

        def out
        try {
            out = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)
            template.writeTo(out)
        } catch (Exception e) {
            e.printStackTrace()
        } finally {
            out.close();
        }
    }

    public static String escapeHtmlForJavadoc(String description) {
        return StringEscapeUtils.escapeHtml(StringUtils.defaultString(description)).replace("/", "&#47;")
    }
}
