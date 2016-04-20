package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.List;

import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.TokenWrapper;

public class ClosureListExpressionWrapper extends ListExpressionWrapper {
    private static final int DEFAULT_INCREASEMENT = 1;

    private static final int DEFAULT_END_NUMBER = 0;

    private static final int DEFAULT_START_NUMBER = 0;

    private static final int DEFAULT_SOURCE_NUMBER = -1;

    private static final String DEFAULT_VARIABLE_NAME = "index";

    public ClosureListExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);

        BinaryExpressionWrapper binaryExpressionWrapper = new BinaryExpressionWrapper(this);
        binaryExpressionWrapper.setLeftExpression(new VariableExpressionWrapper(DEFAULT_VARIABLE_NAME,
                binaryExpressionWrapper));
        binaryExpressionWrapper.setOperation(new TokenWrapper(Token.newSymbol(Types.EQUAL, DEFAULT_SOURCE_NUMBER,
                DEFAULT_SOURCE_NUMBER), binaryExpressionWrapper));
        binaryExpressionWrapper.setRightExpression(new ConstantExpressionWrapper(DEFAULT_START_NUMBER,
                binaryExpressionWrapper));
        expressions.add(binaryExpressionWrapper);

        binaryExpressionWrapper = new BinaryExpressionWrapper(this);
        binaryExpressionWrapper.setLeftExpression(new VariableExpressionWrapper(DEFAULT_VARIABLE_NAME,
                binaryExpressionWrapper));
        binaryExpressionWrapper.setOperation(new TokenWrapper(Token.newSymbol(Types.COMPARE_LESS_THAN,
                DEFAULT_SOURCE_NUMBER, DEFAULT_SOURCE_NUMBER), binaryExpressionWrapper));
        binaryExpressionWrapper.setRightExpression(new ConstantExpressionWrapper(DEFAULT_END_NUMBER,
                binaryExpressionWrapper));
        expressions.add(binaryExpressionWrapper);

        binaryExpressionWrapper = new BinaryExpressionWrapper(this);
        binaryExpressionWrapper.setLeftExpression(new VariableExpressionWrapper(DEFAULT_VARIABLE_NAME,
                binaryExpressionWrapper));
        binaryExpressionWrapper.setOperation(new TokenWrapper(Token.newSymbol(Types.PLUS, DEFAULT_SOURCE_NUMBER,
                DEFAULT_SOURCE_NUMBER), binaryExpressionWrapper));
        binaryExpressionWrapper.setRightExpression(new ConstantExpressionWrapper(DEFAULT_INCREASEMENT,
                binaryExpressionWrapper));
        expressions.add(binaryExpressionWrapper);
    }

    public ClosureListExpressionWrapper(List<ExpressionWrapper> expressions, ASTNodeWrapper parentNodeWrapper) {
        super(expressions, parentNodeWrapper);
    }

    public ClosureListExpressionWrapper(ClosureListExpressionWrapper closureListExpressionWrapper,
            ASTNodeWrapper parentNodeWrapper) {
        super(closureListExpressionWrapper, parentNodeWrapper);
    }

    public ClosureListExpressionWrapper(ClosureListExpression closureListExpression, ASTNodeWrapper parentNodeWrapper) {
        super(closureListExpression, parentNodeWrapper);
    }

    @Override
    public String getText() {
        StringBuilder buffer = new StringBuilder("(");
        boolean first = true;
        for (ExpressionWrapper expression : expressions) {
            if (first) {
                first = false;
            } else {
                buffer.append("; ");
            }

            buffer.append(expression.getText());
        }
        buffer.append(")");
        return buffer.toString();
    }

    @Override
    public ClosureListExpressionWrapper clone() {
        return new ClosureListExpressionWrapper(this, getParent());
    }
}
