package com.kms.katalon.composer.testcase.groovy.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.Parameter;

import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;

public class ParameterWrapper extends AnnonatedNodeWrapper {
    private int modifiers;
    private ClassNodeWrapper type = new ClassNodeWrapper(ClassHelper.DYNAMIC_TYPE, this);
    private String name;
    private ExpressionWrapper initialExpression;

    public ParameterWrapper(Class<?> type, String name, ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.type = new ClassNodeWrapper(type, this);
        this.name = name;
    }

    public ParameterWrapper(ParameterWrapper parameterWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(parameterWrapper, parentNodeWrapper);
        this.modifiers = parameterWrapper.getModifiers();
        this.type = new ClassNodeWrapper(parameterWrapper.getType(), this);
        this.name = parameterWrapper.getName();
        if (parameterWrapper.getInitialExpression() != null) {
            initialExpression = parameterWrapper.getInitialExpression().copy(this);
        }
    }
    
    public ParameterWrapper(Parameter parameter, ASTNodeWrapper parentNodeWrapper) {
        super(parameter, parentNodeWrapper);
        this.name = parameter.getName();
        this.modifiers = parameter.getModifiers();
        this.type = new ClassNodeWrapper(parameter.getType(), this);
        this.initialExpression = ASTNodeWrapHelper.getExpressionNodeWrapperFromExpression(
                parameter.getInitialExpression(), this);
    }

    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public ClassNodeWrapper getType() {
        return type;
    }

    public void setType(ClassNodeWrapper type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExpressionWrapper getInitialExpression() {
        return initialExpression;
    }

    public void setInitialExpression(ExpressionWrapper initialExpression) {
        this.initialExpression = initialExpression;
    }

    @Override
    public String getText() {
        return name;
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        if (initialExpression == null) {
            return Collections.emptyList();
        }
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.addAll(super.getAstChildren());
        astNodeWrappers.add(initialExpression);
        return astNodeWrappers;
    }
}
