package com.kms.katalon.composer.testcase.groovy.ast.statements;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ParameterWrapper;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;

public class CatchStatementWrapper extends CompositeStatementWrapper {
    private static final String DEFAULT_VARIABLE_NAME = "e";
    private static final Class<?> DEFAULT_EXCEPTION_TYPE = Exception.class;
    private ParameterWrapper variable;
    private BlockStatementWrapper code;

    public CatchStatementWrapper(TryCatchStatementWrapper parentTryCatchStatement) {
        super(parentTryCatchStatement);
        variable = new ParameterWrapper(DEFAULT_EXCEPTION_TYPE, DEFAULT_VARIABLE_NAME, this);
        code = new BlockStatementWrapper(this);
    }

    public CatchStatementWrapper(CatchStatement catchStatement, TryCatchStatementWrapper parentTryCatchStatement) {
        super(catchStatement, parentTryCatchStatement);
        this.variable = new ParameterWrapper(catchStatement.getVariable(), this);
        this.code = new BlockStatementWrapper((BlockStatement) catchStatement.getCode(), this);
    }

    public CatchStatementWrapper(CatchStatementWrapper catchStatementWrapper, TryCatchStatementWrapper parentTryCatchStatement) {
        super(catchStatementWrapper, parentTryCatchStatement);
        this.variable = new ParameterWrapper(catchStatementWrapper.getVariable(), this);
        this.code = new BlockStatementWrapper(catchStatementWrapper.getBlock(), this);
    }

    public ParameterWrapper getVariable() {
        return variable;
    }

    public void setVariable(ParameterWrapper variable) {
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
        astNodeWrappers.add(code);
        return astNodeWrappers;
    }

    @Override
    public BlockStatementWrapper getBlock() {
        return code;
    }

    @Override
    public CatchStatementWrapper clone() {
        return new CatchStatementWrapper(this, getParent());
    }

    public ClassNodeWrapper getExceptionType() {
        return variable.getType();
    }

    public void setExceptionType(ClassNodeWrapper type) {
        variable.setType(type);
    }

    public String getVariableName() {
        return variable.getName();
    }

    public void setVariableName(String name) {
        variable.setName(name);
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
                || AstTreeTableValueUtil.compareAstNode(this.getVariable(),
                ((CatchStatementWrapper) input).getVariable())) {
            return false;
        }
        this.setVariable(((CatchStatementWrapper) input).getVariable());
        return true;

    }

}
