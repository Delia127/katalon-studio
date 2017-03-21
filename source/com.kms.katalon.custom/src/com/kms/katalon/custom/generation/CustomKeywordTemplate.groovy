package com.kms.katalon.custom.generation

import groovy.text.GStringTemplateEngine
import groovy.transform.CompileStatic

import org.apache.commons.lang.ClassUtils
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.GenericsType
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter

import com.kms.katalon.core.ast.GroovyParser
import com.kms.katalon.custom.factory.CustomMethodNodeFactory

class CustomKeywordTemplate {
    private static final String[] GROOVY_DF_IMPORTED = [
        "java.lang",
        "java.util",
        "java.io",
        "java.net",
        "groovy.lang",
        "groovy.util",
        "java.math.BigInteger",
        "java.math.BigDecimal"
    ];

    private static final String IMMUTABLE_CLASS_NAME = "org.codehaus.groovy.ast.ImmutableClassNode"
    
    private static final String JAVA_AS_STRING = "java."
    
    private static final String GROOVY_AS_STRING = "groovy."
    
    private static final String DOT = "."
    
    private static Map<String, String> shortClassNameLookup = [:]
    
    private static final String tpl =
    '''
<% importClassNames.each {%>
import <%= it %>
<% } %>
<% methodNodesMap.each { key, value ->
    value.each { %>
def static "<%= key %>.<%= it.getName() %>"(<% it.getParameters().eachWithIndex { item, index -> %>
    <% if (index > 0) { %> , <% }%>	<%= CustomKeywordTemplate.getInitialExpression(item) %>	<% } %>) {
    (new <%= key %>()).<%= it.getName() %>(<% it.getParameters().eachWithIndex { item, index -> %>
        <% if (index > 0) { %> , <% }%>	<%= item.getName() %><% } %>)
}
<% }
} %>'''

    @CompileStatic
    def generateCustomKeywordFile(File file) {
        shortClassNameLookup.clear()
        Map<String, List<MethodNode>> methodNodesMap = CustomMethodNodeFactory.getInstance().getMethodNodesMap()
        String[] importClassNames = getImportClassNames(methodNodesMap)
        def binding = [
            "importClassNames": importClassNames,
            "methodNodesMap":  methodNodesMap,
            "CustomKeywordTemplate": CustomKeywordTemplate
        ]

        def engine = new GStringTemplateEngine()
        def tpl = engine.createTemplate(tpl).make(binding)
        if (file.canWrite()) {
            file.write(tpl.toString());
        }
    }

    private static String[] getImportClassNames(Map<String, List<MethodNode>> methodNodesMap) {
        def importClassNames = [] as Set<String>
        methodNodesMap.each { key, methodNodes ->
            methodNodes.each  { methodNode ->
                methodNode.getParameters().each { param ->
                    String className = resolveClassName(param.getType(), true)
                    if (canBeImported(className)) {
                        importClassNames.add(getFullClassName(param.getType()))
                        shortClassNameLookup.put(className, ClassUtils.getShortCanonicalName(className))
                    }
                }
            }
        }
        return importClassNames as String[]
    }

    private static boolean canBeImported(String className) {
        return StringUtils.isNotEmpty(className) && className.contains(DOT);
    }

    public static String resolveClassName(ClassNode classNode, boolean resolveGeneric) {
        try {
            if (classNode.isArray()) {
                return resolveClassName(classNode.getComponentType(), resolveGeneric) + "[]"
            }

            if (IMMUTABLE_CLASS_NAME.equals(classNode.getClass().getName())
                || classNode.isGenericsPlaceHolder()) {
                return ClassUtils.getShortCanonicalName(classNode.getUnresolvedName())
            }

            String fullClassName = getFullClassName(classNode)
            if (shortClassNameLookup.containsKey(fullClassName)) {
                return shortClassNameLookup.get(fullClassName)
            }

            GenericsType[] genericsTypes = classNode.getGenericsTypes();
            if (resolveGeneric && !classNode.isGenericsPlaceHolder() && genericsTypes != null && genericsTypes.length > 0) {
                StringBuilder classNameBuilder = new StringBuilder(fullClassName)
                classNameBuilder.append("<")
                genericsTypes.eachWithIndex { genericType, index ->
                    classNameBuilder.append((index > 0) ? ", " : "")
                            .append(resolveClassName(genericType.getType(), resolveGeneric))
                }
                classNameBuilder.append(">")
                return classNameBuilder.toString()
            }
            return fullClassName;
        } catch (Exception ignored) {
            return classNode.getUnresolvedName()
        }
    }

    private static boolean isGroovyImportedClassName(String className) {
        if (StringUtils.isEmpty(className) || !(className.startsWith(JAVA_AS_STRING) || className.startsWith(GROOVY_AS_STRING))) {
            return false
        }
        return GROOVY_DF_IMPORTED.any {  className.startsWith(it)  }
    }

    private static String getFullClassName(ClassNode classNode) {
        return getPackageNamePlusDot(classNode) + classNode.getNameWithoutPackage().replace('$', '.')
    }

    private static String getPackageNamePlusDot(ClassNode classNode) {
        String packageName = classNode.getPackageName()
        return StringUtils.isNotEmpty(packageName) ? packageName + DOT : "";
    }

    public static String getInitialExpression(Parameter param) {
        StringBuilder initializedString = new StringBuilder(resolveClassName(param.getType(), true))
                .append(" ").append(param.getName())

        if (param.hasInitialExpression()) {
            initializedString.append(" = ")
            GroovyParser parser = new GroovyParser(new StringBuilder())
            parser.parse(param.getInitialExpression())
            initializedString.append(parser.getValue())
        }

        return initializedString.toString()
    }
}
