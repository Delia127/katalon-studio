package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ParameterWrapper;

public class CatchStatementWrapper extends ComplexChildStatementWrapper {
    private static final String DEFAULT_VARIABLE_NAME = "e";

    private static final Class<?> DEFAULT_EXCEPTION_TYPE = Exception.class;

    private ParameterWrapper variable;

    public CatchStatementWrapper(TryCatchStatementWrapper parentTryCatchStatement) {
        super(parentTryCatchStatement);
        variable = new ParameterWrapper(DEFAULT_EXCEPTION_TYPE, DEFAULT_VARIABLE_NAME, this);
    }

    public CatchStatementWrapper(CatchStatement catchStatement, TryCatchStatementWrapper parentTryCatchStatement) {
        super(catchStatement, (BlockStatement) catchStatement.getCode(), parentTryCatchStatement);
        this.variable = new ParameterWrapper(catchStatement.getVariable(), this);
    }

    public CatchStatementWrapper(CatchStatementWrapper catchStatementWrapper,
            TryCatchStatementWrapper parentTryCatchStatement) {
        super(catchStatementWrapper, parentTryCatchStatement);
        this.variable = new ParameterWrapper(catchStatementWrapper.getVariable(), this);
    }

    public CatchStatementWrapper() {
        this(null);
    }

    public ParameterWrapper getVariable() {
        return variable;
    }

    public void setVariable(ParameterWrapper variable) {
        if (variable == null) {
            return;
        }
        variable.setParent(this);
        this.variable = variable;
    }

    @Override
    public String getText() {
        return ("catch (" + getVariable().getText() + ")");
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public TryCatchStatementWrapper getParent() {
        return (TryCatchStatementWrapper) super.getParent();
    }

    @Override
    public List<? extends ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.add(variable);
        astNodeWrappers.addAll(super.getAstChildren());
        return astNodeWrappers;
    }

    @Override
    public CatchStatementWrapper clone() {
        return new CatchStatementWrapper(this, getParent());
    }

    public ClassNodeWrapper getExceptionType() {
        return variable.getType();
    }

    public void setExceptionType(ClassNodeWrapper type) {
        if (type == null) {
            return;
        }
        type.setParent(variable);
        variable.setType(type);
    }

    public String getVariableName() {
        return variable.getName();
    }

    public void setVariableName(String name) {
        variable.setName(name);
    }

    @Override
    public boolean isInputEditatble() {
        return true;
    }

    @Override
    public ASTNodeWrapper getInput() {
        return this;
    }

    @Override
    public String getInputText() {
        return variable.getType().getNameWithoutPackage() + " " + variable.getName();
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (!(input instanceof CatchStatementWrapper)
                || this.getVariable().isEqualsTo(((CatchStatementWrapper) input).getVariable())) {
            return false;
        }
        setVariable(((CatchStatementWrapper) input).getVariable());
        return true;
    }

    @Override
    protected boolean isAstNodeBelongToParentComplex(ASTNodeWrapper astNode) {
        return astNode instanceof CatchStatementWrapper || astNode instanceof FinallyStatementWrapper;
    }

    @Override
    public boolean replaceChild(ASTNodeWrapper oldChild, ASTNodeWrapper newChild) {
        if (oldChild == getExceptionType() && newChild instanceof ClassNodeWrapper) {
            setExceptionType((ClassNodeWrapper) newChild);
            return true;
        } else if (oldChild == getVariable() && newChild instanceof ParameterWrapper) {
            setVariable((ParameterWrapper) newChild);
        }
        return false;
    }
}
