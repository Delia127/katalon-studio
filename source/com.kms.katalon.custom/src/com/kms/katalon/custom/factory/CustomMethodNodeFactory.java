package com.kms.katalon.custom.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.MethodNode;

import com.kms.katalon.core.annotation.Keyword;
import com.kms.katalon.custom.parser.MethodUtils;
import com.kms.katalon.util.groovy.MethodNodeUtil;

public class CustomMethodNodeFactory {

    /**
     * key: class name value: list of all methods that the class owns
     */
    private Map<String, List<MethodNode>> methodNodesMap;
    
    private Map<String, List<String>> methodParameterNamesMap;
    
    /**
     * key: file path, value: class name;
     */
    private Map<String, Set<String>> classPathMap;
        
    private Map<String, String> javadocMap;

    private static CustomMethodNodeFactory _instance;

    private CustomMethodNodeFactory() {
        methodNodesMap = new HashMap<String, List<MethodNode>>();
        classPathMap = new HashMap<>();
        javadocMap = new HashMap<>();
        methodParameterNamesMap = new HashMap<>();
    }

    public static CustomMethodNodeFactory getInstance() {
        if (_instance == null) {
            _instance = new CustomMethodNodeFactory();
        }
        return _instance;
    }

    public void addMethodNodes(String className, List<MethodNode> methodNodes, String filePath) {
        List<MethodNode> customKeywordMethods = new ArrayList<MethodNode>();
        for (MethodNode method : methodNodes) {
            if (method.getAnnotations() == null) {
                continue;
            }
            for (AnnotationNode annotationNode : method.getAnnotations()) {
                if (isKeywordAnnotationNode(annotationNode)) {
                    customKeywordMethods.add(method);
                    break;
                }
            }
        }
        methodNodesMap.put(className, customKeywordMethods);
        addClassToClassPath(className, filePath);
    }
    
    public void addJavadoc(MethodNode methodNode, String htmlJavadoc) {
        String descriptor = MethodNodeUtil.getDescriptor(methodNode);
        javadocMap.put(descriptor, htmlJavadoc);
    }
    
    public String getJavadoc(MethodNode methodNode) {
        String descriptor = MethodNodeUtil.getDescriptor(methodNode);
        return StringUtils.defaultIfBlank(javadocMap.get(descriptor), "");
    }
    
    public void addPluginMethodNodes(String className, List<MethodNode> methodNodes, String filePath, Map<String, List<String>> parameterMaps) {
        List<MethodNode> customKeywordMethods = new ArrayList<MethodNode>();
        for (MethodNode method : methodNodes) {
            if (method.getAnnotations() == null) {
                continue;
            }
            for (AnnotationNode annotationNode : method.getAnnotations()) {
                if (isKeywordAnnotationNode(annotationNode)) {
                    customKeywordMethods.add(method);
                    String typesName = MethodUtils.getParametersDescriptor(method);
                    String methodName = method.getName() + "#" + typesName;
                    List<String> parameterNames = parameterMaps.get(methodName);
                    this.methodParameterNamesMap.put(className + '#' + methodName, parameterNames);
                    break;
                }
            }
        }
        methodNodesMap.put(className, customKeywordMethods);
        addClassToClassPath(className, filePath);
    }

    private void addClassToClassPath(String className, String filePath) {
        Set<String> classes = classPathMap.get(filePath);
        if (classes == null) {
            classes = new HashSet<>();
        }
        classes.add(className);
        classPathMap.put(filePath, classes);
    }

    private boolean isKeywordAnnotationNode(AnnotationNode annotationNode) {
        String annotaionClassNodeName = annotationNode.getClassNode().getName();
        return (Keyword.class.getName().equals(annotaionClassNodeName) || Keyword.class.getSimpleName().equals(
                annotaionClassNodeName));
    }

    public void removeMethodNodes(String filePath) {
        Set<String> classes = classPathMap.get(filePath);
        if (classes == null) {
            return;
        }
        classes.stream().forEach(clazz -> {
            if (methodNodesMap.containsKey(clazz)) {
                methodNodesMap.remove(clazz);
            }
        });
        classPathMap.remove(filePath);
    }

    public void reset() {
        methodNodesMap.clear();
        classPathMap.clear();
        methodParameterNamesMap.clear();
        javadocMap.clear();
    }
    
    public boolean isCustomKeywordClass(String className) {
        return StringUtils.isNotEmpty(className) && methodNodesMap.containsKey(className);
    }
    
    public boolean isCustomKeywordMethod(MethodNode method) {
        if (method.getAnnotations() != null) {
            for (AnnotationNode annotationNode : method.getAnnotations()) {
                if (isKeywordAnnotationNode(annotationNode)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public MethodNode findBestMatch(String className, String methodName, String[] parameterTypes) {
        MethodNode methodNode = find(className, methodName, parameterTypes);
        if (methodNode == null) {
            methodNode = find(className, methodName, parameterTypes.length);
        }
        return methodNode;
    }

    private MethodNode find(String className, String methodName, String[] parameterTypes) {
        List<MethodNode> methodNodes = getMethodNodesMap().get(className);
        for (MethodNode methodNode : methodNodes) {
            if (MethodNodeUtil.matchMethodWithLooseParamTypesChecking(methodNode, className, methodName,
                    parameterTypes)) {
                return methodNode;
            }
        }
        return null;
    }

    private MethodNode find(String className, String methodName, int numberOfParameters) {
        List<MethodNode> methodNodes = getMethodNodesMap().get(className);
        for (MethodNode methodNode : methodNodes) {
            if (MethodNodeUtil.matchMethod(methodNode, className, methodName, numberOfParameters)) {
                return methodNode;
            }
        }
        return null;
    }
    
    public Map<String, List<MethodNode>> getMethodNodesMap() {
        return methodNodesMap;
    }
    
    public Map<String, List<String>> getMethodParameterNamesMap() {
        return methodParameterNamesMap;
    }
}
