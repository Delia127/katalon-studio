package com.kms.katalon.custom.generation

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat

import org.apache.commons.lang.StringEscapeUtils
import org.apache.commons.lang.StringUtils

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
import com.kms.katalon.core.main.ScriptEngine
import com.kms.katalon.core.main.TestCaseMain
import com.kms.katalon.core.logging.KeywordLogger


/**
 * This class is generated automatically by Katalon Studio and should not be modified or deleted.
 */
public class GlobalVariable {
    private static KeywordLogger logger = KeywordLogger.getInstance(TestCaseMain.class)

    <% globalVariables.each { entry -> %> 
    /**
     * <p><%= GlobalVariableTemplate.escapeHtmlForJavadoc(entry.value.getDescription()) %></p>
     */
    public static Object <%= entry.value.getName() %>
    <% } %> 

    static {
        String profileName = RunConfiguration.getExecutionProfile()
        try {
            def selectedVariables = [:]
    
            ScriptEngine sce = TestCaseMain.getScriptEngine()
            def variables = new XmlParser().parse(new File(RunConfiguration.getProjectDir(), "Profiles/" + profileName + ".glbl"))
            for (globalVariable in variables.GlobalVariableEntity) {
                String variableName = globalVariable.name.text()
                String defaultValue = globalVariable.initValue.text()
                try {
                    selectedVariables.put(variableName, sce.runScriptWithoutLogging(defaultValue, new Binding()))
                } catch (Exception e) {
                    logger.logError(String.format(
                        "Could not evaluate default value of variable: %s in profile: %s. Details: %s", variableName, profileName, e.getMessage()))
                }
            }
    		
            selectedVariables += RunConfiguration.getOverridingParameters()
    
            <% globalVariables.each { entry -> %>\
<%=entry.value.getName()%> = selectedVariables["<%=entry.value.getName()%>"]
            <% } %>
        } catch (Exception e) {
            logger.logError(String.format(
                        "Could not evaluate variables in profile: %s. Details: %s", profileName, e.getMessage()))
        }
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
            "globalVariables": declaredGlobalVariables
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
