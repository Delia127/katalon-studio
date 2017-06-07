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

public class CustomMethodNodeFactory {

    /**
     * key: class name value: list of all methods that the class owns
     */
    private Map<String, List<MethodNode>> methodNodesMap;
    
    /**
     * key: file path, value: class name;
     */
    private Map<String, Set<String>> classPathMap;

    private static CustomMethodNodeFactory _instance;

    private CustomMethodNodeFactory() {
        methodNodesMap = new HashMap<String, List<MethodNode>>();
        classPathMap = new HashMap<>();
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
    }
    
    public boolean isCustomKeywordClass(String className) {
        return StringUtils.isNotEmpty(className) && methodNodesMap.containsKey(className);
    }
    
    public Map<String, List<MethodNode>> getMethodNodesMap() {
        return methodNodesMap;
    }
}
