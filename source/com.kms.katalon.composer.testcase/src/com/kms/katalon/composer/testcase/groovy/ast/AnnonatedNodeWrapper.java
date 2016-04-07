package com.kms.katalon.composer.testcase.groovy.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;

public abstract class AnnonatedNodeWrapper extends ASTNodeWrapper {
    protected List<AnnotationNodeWrapper> annotations = new ArrayList<AnnotationNodeWrapper>();

    public AnnonatedNodeWrapper() {
        this(null);
    }
    
    public AnnonatedNodeWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }

    public AnnonatedNodeWrapper(AnnotatedNode annonatedNode, ASTNodeWrapper parentNodeWrapper) {
        super(annonatedNode, parentNodeWrapper);
        for (AnnotationNode annotationNode : annonatedNode.getAnnotations()) {
            annotations.add(new AnnotationNodeWrapper(annotationNode, this));
        }
    }

    public AnnonatedNodeWrapper(AnnonatedNodeWrapper annonatedNodeWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(annonatedNodeWrapper, parentNodeWrapper);
        annotations.clear();
        for (AnnotationNodeWrapper annotationNodeWrapper : annonatedNodeWrapper.getAnnotations()) {
            annotations.add(new AnnotationNodeWrapper(annotationNodeWrapper, this));
        }
    }

    public AnnonatedNodeWrapper(AnnonatedNodeWrapper annonatedNodeWrapper) {
        this(annonatedNodeWrapper, annonatedNodeWrapper.getParent());
    }

    public void setAnnotations(List<AnnotationNodeWrapper> annotations) {
        if (annotations == null) {
            return;
        }
        for (AnnotationNodeWrapper annotationNode : annotations) {
            annotationNode.setParent(this);
        }
        this.annotations = annotations;
    }

    public void addAnnotation(AnnotationNodeWrapper annotationNodeWrapper) {
        if (annotationNodeWrapper == null) {
            return;
        }
        annotationNodeWrapper.setParent(this);
        annotations.add(annotationNodeWrapper);
    }
    
    public void removeAnnotation(AnnotationNodeWrapper annotationNodeWrapper) {
        if (annotationNodeWrapper == null) {
            return;
        }
        annotations.remove(annotationNodeWrapper);
    }

    public List<AnnotationNodeWrapper> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        return annotations;
    }

    public AnnotationNodeWrapper getAnnotationByClass(Class<?> clazzz) {
        if (clazzz == null) {
            return null;
        }
        for (AnnotationNodeWrapper annotationNodeWrapper : getAnnotations()) {
            if (annotationNodeWrapper.getClassNode().getName().equals(clazzz.getName())
                    || annotationNodeWrapper.getClassNode().getName().equals(clazzz.getSimpleName())) {
                return annotationNodeWrapper;
            }
        }
        return null;
    }
}
