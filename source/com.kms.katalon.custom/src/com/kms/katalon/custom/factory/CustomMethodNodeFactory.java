package com.kms.katalon.custom.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.MethodNode;

import com.kms.katalon.core.annotation.Keyword;

public class CustomMethodNodeFactory {

    /**
     * key: class name value: list of all methods that the class owns
     */
    private Map<String, List<MethodNode>> mapMethodNodes;

    private static CustomMethodNodeFactory _instance;

    private CustomMethodNodeFactory() {
        mapMethodNodes = new HashMap<String, List<MethodNode>>();
    }

    public static CustomMethodNodeFactory getInstance() {
        if (_instance == null) {
            _instance = new CustomMethodNodeFactory();
        }
        return _instance;
    }

    /**
     * Returns all methods that are in custom keyword file, not in CustomKeywords.groovy
     */
    public List<MethodNode> getAllMethodNodes() {
        List<MethodNode> output = new ArrayList<MethodNode>();
        for (Entry<String, List<MethodNode>> entry : mapMethodNodes.entrySet()) {
            if (entry.getValue() instanceof List) {
                List<MethodNode> entryMethods = entry.getValue();
                for (MethodNode methodNode : entryMethods) {
                    output.add(methodNode);
                }
            }
        }
        return output;
    }

    public void addMethodNodes(String className, List<MethodNode> methodNodes) {
        List<MethodNode> qualfiliers = new ArrayList<MethodNode>();
        for (MethodNode method : methodNodes) {
            if (method.getAnnotations() != null) {
                for (AnnotationNode annotationNode : method.getAnnotations()) {
                    if (annotationNode.getClassNode().getName().equals(Keyword.class.getName()) ||
                            annotationNode.getClassNode().getName().equals(Keyword.class.getSimpleName())) {
                        qualfiliers.add(method);
                        break;
                    }
                }
            }
        }
        mapMethodNodes.put(className, qualfiliers);
    }
    
    public void removeMethodNodes(String className) {
        if (mapMethodNodes.containsKey(className)) {
            mapMethodNodes.remove(className);
        }
    }

    public void reset() {
        mapMethodNodes.clear();
    }
    
    public boolean isCustomKeywordClass(String className) {
    	if ((className == null) || className.isEmpty()) return false; 
    	return mapMethodNodes.containsKey(className);
    }
}
