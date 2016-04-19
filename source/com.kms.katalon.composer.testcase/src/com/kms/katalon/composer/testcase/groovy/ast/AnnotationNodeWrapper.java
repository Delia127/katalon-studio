package com.kms.katalon.composer.testcase.groovy.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.expr.Expression;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;

public class AnnotationNodeWrapper extends ASTNodeWrapper {
    private ClassNodeWrapper classNode;
    private Map<String, ExpressionWrapper> members = new HashMap<String, ExpressionWrapper>();

    public AnnotationNodeWrapper(Class<?> type, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.classNode = new ClassNodeWrapper(type, this);
    }

    public AnnotationNodeWrapper(AnnotationNodeWrapper annotationNodeWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(annotationNodeWrapper, parentNodeWrapper);
        this.classNode = new ClassNodeWrapper(annotationNodeWrapper.getClassNode(), this);
        members.clear();
        for (Entry<String, ExpressionWrapper> member : annotationNodeWrapper.getMembers().entrySet()) {
            members.put(member.getKey(), member.getValue().copy(this));
        }
    }

    public AnnotationNodeWrapper(AnnotationNode annotationNode, ASTNodeWrapper parentNodeWrapper) {
        super(annotationNode, parentNodeWrapper);
        this.classNode = new ClassNodeWrapper(annotationNode.getClassNode(), this);
        if (annotationNode.getMembers() == null || annotationNode.getMembers().isEmpty()) {
            return;
        }
        for (Entry<String, Expression> member : annotationNode.getMembers().entrySet()) {
            members.put(member.getKey(),
                    ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(member.getValue(), this));
        }
    }

    public ClassNodeWrapper getClassNode() {
        return classNode;
    }

    public void setClassNode(ClassNodeWrapper classNode) {
        if (classNode == null) {
            return;
        }
        classNode.setParent(this);
        this.classNode = classNode;
    }

    public Map<String, ExpressionWrapper> getMembers() {
        return Collections.unmodifiableMap(members);
    }

    @Override
    public String getText() {
        StringBuilder stringBuilder = new StringBuilder('@');
        stringBuilder.append(classNode.getName());
        if (!hasMembers()) {
            return stringBuilder.toString();
        }
        stringBuilder.append("(");
        stringBuilder.append(StringUtils.join(
                Iterables.transform(members.entrySet(), new Function<Entry<String, ExpressionWrapper>, String>() {
                    @Override
                    public String apply(Entry<String, ExpressionWrapper> member) {
                        return member.getKey() + " = " + member.getValue();
                    }
                }).iterator(), ","));
        stringBuilder.append(")");
        return stringBuilder.toString();
    }

    public boolean hasMembers() {
        return members != null && !members.isEmpty();
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> children = new ArrayList<ASTNodeWrapper>();
        children.add(classNode);
        if (!hasMembers()) {
            return children;
        }
        children.addAll(members.values());
        return children;
    }

    @Override
    public AnnotationNodeWrapper clone() {
        return new AnnotationNodeWrapper(this, getParent());
    }
}
