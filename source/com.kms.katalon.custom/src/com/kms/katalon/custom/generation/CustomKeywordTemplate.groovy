package com.kms.katalon.custom.generation

import groovy.text.GStringTemplateEngine
import groovy.transform.CompileStatic

import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.GenericsType
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter

import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.keyword.IKeywordContributor
import com.kms.katalon.core.keyword.KeywordContributorCollection
import com.kms.katalon.core.keyword.KeywordExceptionHandler
import com.kms.katalon.core.logging.ErrorCollector
import com.kms.katalon.core.logging.KeywordLogger
import com.kms.katalon.custom.factory.CustomMethodNodeFactory

class CustomKeywordTemplate{

    private static final String tpl =
    '''<% classNames.each { %>import <%= it %>
<% } %>
<% methodNodes.each { %>
/**
 * @see <%= it.getDeclaringClass().getName() %>#<%= it.getName() %>(
 * <% it.getParameters().eachWithIndex { item, index -> %> <% if (index > 0) { %> ,<% }%> 
 * <%= CustomKeywordTemplate.toString(item.getType()) %> <% } %>)
 */
def static "<%= it.getDeclaringClass().getName() %>.<%= it.getName() %>" (<% it.getParameters().eachWithIndex { item, index -> %>
    <% if (index > 0) { %> , <% }%>	<%= CustomKeywordTemplate.toString(item.getType()) %>  <%= item.getName() %>	<% } %>) {
    (new <%= it.getDeclaringClass().getName() %>()).<%= it.getName() %>(<% it.getParameters().eachWithIndex { item, index -> %>
        <% if (index > 0) { %> , <% }%>	<%= item.getName() %><% } %>)
}
<% } %>'''

    @CompileStatic
    def generateCustomKeywordFile(File file) {
        Set<String> classNames = new HashSet<String>()
        classNames.add(KeywordLogger.class.getName())
        classNames.add(KeywordExceptionHandler.class.getName())
        classNames.add(StepFailedException.class.getName())
        classNames.add(ErrorCollector.class.getName())
        for (IKeywordContributor keywordContribution : KeywordContributorCollection.getKeywordContributors()) {
            classNames.add(keywordContribution.getKeywordClass().getName());
        }
        List<MethodNode> methodNodes = CustomMethodNodeFactory.getInstance().getAllMethodNodes()


        for (MethodNode methodNode : methodNodes) {
            for (Parameter param : methodNode.getParameters()) {
                String[] className = getImportNames(param.getOriginType())
                if (className != null) {
                    classNames.addAll(className)
                }
            }
        }

        def binding = [
            "classNames": classNames,
            "methodNodes":  methodNodes,
            "CustomKeywordTemplate": CustomKeywordTemplate
        ]

        def engine = new GStringTemplateEngine()
        def tpl = engine.createTemplate(tpl).make(binding)
        if (file.canWrite()) {
            file.write(tpl.toString());
        }
    }

    @CompileStatic
    private String[] getImportNames(ClassNode classNode) {
        if (canImportClassNode(classNode)) {
            List<String> names = new ArrayList<String>()
            if (classNode.isArray()) {
                names.addAll(getImportNames(classNode.getComponentType()))
            } else {
                names.add(classNode.getName())
            }

            if (classNode.getGenericsTypes() != null && classNode.getGenericsTypes().length > 0) {
                for (GenericsType childNode in classNode.getGenericsTypes()) {
                    names.addAll(getImportNames(childNode.getType()))
                }
            }
            return names.toArray(new String[names.size()]);
        }
        return Collections.emptyList().toArray()
    }

    @CompileStatic
    private boolean canImportClassNode(ClassNode classNode) {
        if (classNode.isResolved() || classNode.isPrimaryClassNode()) {
            return false
        }

        return true
    }

    public static String toString(ClassNode classNode) {
        try {
            if (classNode.isArray()) {
                return toString(classNode.getComponentType()) + "[]"
            }

            String ret = classNode.getName()
            if (!classNode.isPrimitive()) {
                if (classNode.placeholder) ret = classNode.getUnresolvedName()
                if (!classNode.placeholder && classNode.getGenericsTypes() != null) {
                    ret += " <"
                    for (int i = 0; i < classNode.getGenericsTypes().length; i++) {
                        if (i != 0) ret += ", "
                        GenericsType genericsType = classNode.getGenericsTypes()[i]
                        ret += toString(genericsType.getType())
                    }
                    ret += ">"
                }
            }
            return ret
        } catch (Exception e) {
            return "def";
        }
    }
}
