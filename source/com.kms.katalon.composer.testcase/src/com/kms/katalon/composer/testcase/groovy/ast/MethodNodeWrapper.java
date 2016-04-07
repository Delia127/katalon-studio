package com.kms.katalon.composer.testcase.groovy.ast;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.eclipse.jdt.core.dom.Modifier;

import com.kms.katalon.composer.testcase.groovy.ast.statements.BlockStatementWrapper;

public class MethodNodeWrapper extends AnnonatedNodeWrapper implements ASTHasBlock {
    private BlockStatementWrapper code;

    private String name;

    private int modifiers;

    private ClassNodeWrapper returnType;

    private ParameterWrapper[] parameters;

    private ClassNodeWrapper[] exceptions;

    public MethodNodeWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        name = "";
        modifiers = Modifier.PUBLIC;
        returnType = new ClassNodeWrapper(Void.class, parentNodeWrapper);
        parameters = new ParameterWrapper[0];
        code = new BlockStatementWrapper(this);
    }

    public MethodNodeWrapper(MethodNodeWrapper methodNodeWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(methodNodeWrapper, parentNodeWrapper);
        name = methodNodeWrapper.getName();
        modifiers = methodNodeWrapper.getModifiers();
        returnType = new ClassNodeWrapper(methodNodeWrapper.getReturnType(), this);
        parameters = new ParameterWrapper[methodNodeWrapper.getParameters().length];
        for (int index = 0; index < parameters.length; index++) {
            parameters[index] = new ParameterWrapper(methodNodeWrapper.getParameters()[index], this);
        }
        exceptions = new ClassNodeWrapper[methodNodeWrapper.getExceptions().length];
        for (int index = 0; index < methodNodeWrapper.getExceptions().length; index++) {
            exceptions[index] = new ClassNodeWrapper(methodNodeWrapper.getExceptions()[index], this);
        }
        this.code = new BlockStatementWrapper(methodNodeWrapper.getBlock(), this);
    }

    public MethodNodeWrapper(MethodNode methodNode, ASTNodeWrapper parentNodeWrapper) {
        super(methodNode, parentNodeWrapper);
        if (methodNode.getCode() instanceof BlockStatement) {
            this.code = new BlockStatementWrapper((BlockStatement) methodNode.getCode(), this);
        } else {
            this.code = new BlockStatementWrapper(this);
        }
        this.name = methodNode.getName();
        this.modifiers = methodNode.getModifiers();
        this.returnType = new ClassNodeWrapper(methodNode.getReturnType(), this);
        parameters = new ParameterWrapper[methodNode.getParameters().length];
        for (int index = 0; index < parameters.length; index++) {
            parameters[index] = new ParameterWrapper(methodNode.getParameters()[index], this);
        }
        exceptions = new ClassNodeWrapper[methodNode.getExceptions().length];
        for (int index = 0; index < methodNode.getExceptions().length; index++) {
            exceptions[index] = new ClassNodeWrapper(methodNode.getExceptions()[index], this);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public ClassNodeWrapper getReturnType() {
        return returnType;
    }

    public void setReturnType(ClassNodeWrapper returnType) {
        if (returnType == null) {
            return;
        }
        returnType.setParent(this);
        this.returnType = returnType;
    }

    public ParameterWrapper[] getParameters() {
        return parameters;
    }

    public void setParameters(ParameterWrapper[] parameters) {
        if (parameters == null) {
            return;
        }
        for (ParameterWrapper parameter : parameters) {
            parameter.setParent(this);
        }
        this.parameters = parameters;
    }

    public ClassNodeWrapper[] getExceptions() {
        return exceptions;
    }

    public void setExceptions(ClassNodeWrapper[] exceptions) {
        if (exceptions == null) {
            return;
        }
        for (ClassNodeWrapper exception : exceptions) {
            exception.setParent(this);
        }
        this.exceptions = exceptions;
    }

    @Override
    public BlockStatementWrapper getBlock() {
        return code;
    }

    public void setBlock(BlockStatementWrapper block) {
        if (block == null) {
            return;
        }
        block.setParent(this);
        this.code = block;
    }

    @Override
    public String getText() {
        return name + "()";
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.addAll(super.getAstChildren());
        for (ParameterWrapper parameter : parameters) {
            astNodeWrappers.add(parameter);
        }
        astNodeWrappers.add(code);
        return astNodeWrappers;
    }

    @Override
    public MethodNodeWrapper clone() {
        return new MethodNodeWrapper(this, getParent());
    }
}
