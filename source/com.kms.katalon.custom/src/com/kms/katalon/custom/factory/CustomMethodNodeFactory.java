package com.kms.katalon.custom.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.MethodNode;

import com.kms.katalon.core.annotation.Keyword;

public class CustomMethodNodeFactory {

    /**
     * key: class name value: list of all methods that the class owns
     */
    private Map<String, List<MethodNode>> methodNodesMap;

    private static CustomMethodNodeFactory _instance;

    private CustomMethodNodeFactory() {
        methodNodesMap = new HashMap<String, List<MethodNode>>();
    }

    public static CustomMethodNodeFactory getInstance() {
        if (_instance == null) {
            _instance = new CustomMethodNodeFactory();
        }
        return _instance;
    }

    public void addMethodNodes(String className, List<MethodNode> methodNodes) {
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
    }
    
    private boolean isKeywordAnnotationNode(AnnotationNode annotationNode) {
        String annotaionClassNodeName = annotationNode.getClassNode().getName();
        return (Keyword.class.getName().equals(annotaionClassNodeName) || Keyword.class.getSimpleName().equals(
                annotaionClassNodeName));
    }
    
    public void removeMethodNodes(String className) {
        if (methodNodesMap.containsKey(className)) {
            methodNodesMap.remove(className);
        }
    }

    public void reset() {
        methodNodesMap.clear();
    }
    
    public boolean isCustomKeywordClass(String className) {
        return StringUtils.isNotEmpty(className) && methodNodesMap.containsKey(className);
    }
    
    public Map<String, List<MethodNode>> getMethodNodesMap() {
        return methodNodesMap;
    }
}
