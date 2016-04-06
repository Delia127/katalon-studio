package com.kms.katalon.composer.testcase.groovy.ast;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.MethodPointerExpression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.SpreadMapExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.UnaryMinusExpression;
import org.codehaus.groovy.ast.expr.UnaryPlusExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;

import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArrayExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BinaryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BitwiseNegationExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BooleanExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.CastExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClassExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClosureExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClosureListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstructorCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.DeclarationExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.EmptyExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.FieldExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.GStringExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MapEntryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MapExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodPointerExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PostfixExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PrefixExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PropertyExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.RangeExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.SpreadExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.SpreadMapExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.StaticMethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.TernaryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.TupleExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.UnaryMinusExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.UnaryPlusExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.VariableExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.AssertStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BlockStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BreakStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ContinueStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.DoWhileStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.EmptyStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ForStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.IfStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ReturnStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.SwitchStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.SynchronizedStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ThrowStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.TryCatchStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.WhileStatementWrapper;

public class ASTNodeWrapHelper {
    private static final String UNKNOWN = "<unknown>";

    public static boolean containsComment(ASTNodeWrapper astObject, CommentWrapper comment) {
        return ((astObject.getLineNumber() < comment.getLineNumber() || (astObject.getLineNumber() == comment
                .getLineNumber() && astObject.getColumnNumber() < comment.getColumnNumber())) && (astObject
                .getLastLineNumber() > comment.getLastLineNumber() || (astObject.getLastLineNumber() == comment
                .getLastLineNumber() && astObject.getLastColumnNumber() >= comment.getLastColumnNumber())));
    }

    public static boolean isInlineTrailingComment(ASTNodeWrapper astObject, CommentWrapper comment) {
        return (astObject.getLastLineNumber() == comment.getLineNumber() && astObject.getLastColumnNumber() <= comment
                .getColumnNumber());
    }

    public static boolean isTrailingComment(ASTNodeWrapper astObject, CommentWrapper comment) {
        return (astObject.getLastLineNumber() < comment.getLineNumber());
    }

    public static boolean isLeadingComment(ASTNodeWrapper astObject, CommentWrapper comment) {
        return (astObject.getLineNumber() > comment.getLastLineNumber())
                || (astObject.getLineNumber() == comment.getLastLineNumber() && astObject.getColumnNumber() >= comment
                        .getLastColumnNumber());
    }

    public static boolean isDescriptionStatement(Statement statement) {
        return (statement instanceof ExpressionStatement
                && ((ExpressionStatement) statement).getExpression() instanceof ConstantExpression && ((ConstantExpression) ((ExpressionStatement) statement)
                    .getExpression()).getValue() instanceof String);
    }

    public static String getDecriptionStatementValue(Statement statement) {
        return ((ConstantExpression) ((ExpressionStatement) statement).getExpression()).getValue().toString();
    }

    public static List<StatementWrapper> getStatementNodeWrappersFromBlockStatement(BlockStatement blockStatement,
            ASTNodeWrapper parentNode) {
        List<StatementWrapper> statements = new ArrayList<StatementWrapper>();
        Statement pendingDescriptionStatement = null;
        for (Statement statement : blockStatement.getStatements()) {
            if (blockStatement.getStatements().indexOf(statement) < blockStatement.getStatements().size()
                    && ASTNodeWrapHelper.isDescriptionStatement(statement)) {
                if (pendingDescriptionStatement != null) {
                    statements.add(getStatementNodeWrapperFromStatement(pendingDescriptionStatement, parentNode));
                    pendingDescriptionStatement = statement;
                } else {
                    pendingDescriptionStatement = statement;
                }
            } else {
                StatementWrapper statementWrapper = getStatementNodeWrapperFromStatement(statement, parentNode);
                if (statementWrapper == null) {
                    continue;
                }
                if (pendingDescriptionStatement != null) {
                    statementWrapper.setDescription(getDecriptionStatementValue(pendingDescriptionStatement));
                    pendingDescriptionStatement = null;
                }
                statements.add(statementWrapper);
            }
        }
        if (pendingDescriptionStatement != null) {
            statements.add(getStatementNodeWrapperFromStatement(pendingDescriptionStatement, parentNode));
        }
        return statements;
    }

    public static StatementWrapper getStatementNodeWrapperFromStatement(Statement statement, ASTNodeWrapper parent) {
        if (statement instanceof BlockStatement) {
            return new BlockStatementWrapper((BlockStatement) statement, parent);
        } else if (statement instanceof ExpressionStatement) {
            return new ExpressionStatementWrapper((ExpressionStatement) statement, parent);
        } else if (statement instanceof ReturnStatement) {
            return new ReturnStatementWrapper((ReturnStatement) statement, parent);
        } else if (statement instanceof AssertStatement) {
            return new AssertStatementWrapper((AssertStatement) statement, parent);
        } else if (statement instanceof SwitchStatement) {
            return new SwitchStatementWrapper((SwitchStatement) statement, parent);
        } else if (statement instanceof ThrowStatement) {
            return new ThrowStatementWrapper((ThrowStatement) statement, parent);
        } else if (statement instanceof TryCatchStatement) {
            return new TryCatchStatementWrapper((TryCatchStatement) statement, parent);
        } else if (statement instanceof IfStatement) {
            return new IfStatementWrapper((IfStatement) statement, parent);
        } else if (statement instanceof ForStatement) {
            return new ForStatementWrapper((ForStatement) statement, parent);
        } else if (statement instanceof WhileStatement) {
            return new WhileStatementWrapper((WhileStatement) statement, parent);
        } else if (statement instanceof DoWhileStatement) {
            return new DoWhileStatementWrapper((DoWhileStatement) statement, parent);
        } else if (statement instanceof SynchronizedStatement) {
            return new SynchronizedStatementWrapper((SynchronizedStatement) statement, parent);
        } else if (statement instanceof ContinueStatement) {
            return new ContinueStatementWrapper((ContinueStatement) statement, parent);
        } else if (statement instanceof BreakStatement) {
            return new BreakStatementWrapper((BreakStatement) statement, parent);
        } else if (statement instanceof EmptyStatement) {
            return new EmptyStatementWrapper((EmptyStatement) statement, parent);
        } else {
            return null;
        }
    }

    public static ExpressionWrapper getExpressionNodeWrapperFromExpression(Expression expression, ASTNodeWrapper parent) {
        if (expression instanceof ArgumentListExpression) {
            return new ArgumentListExpressionWrapper((ArgumentListExpression) expression, parent);
        } else if (expression instanceof ArrayExpression) {
            return new ArrayExpressionWrapper((ArrayExpression) expression, parent);
        } else if (expression instanceof BooleanExpression) {
            return new BooleanExpressionWrapper((BooleanExpression) expression, parent);
        } else if (expression instanceof BitwiseNegationExpression) {
            return new BitwiseNegationExpressionWrapper((BitwiseNegationExpression) expression, parent);
        } else if (expression instanceof CastExpression) {
            return new CastExpressionWrapper((CastExpression) expression, parent);
        } else if (expression instanceof ClosureExpression) {
            return new ClosureExpressionWrapper((ClosureExpression) expression, parent);
        } else if (expression instanceof ConstructorCallExpression) {
            return new ConstructorCallExpressionWrapper((ConstructorCallExpression) expression, parent);
        } else if (expression instanceof DeclarationExpression) {
            return new DeclarationExpressionWrapper((DeclarationExpression) expression, parent);
        } else if (expression instanceof BinaryExpression) {
            return new BinaryExpressionWrapper((BinaryExpression) expression, parent);
        } else if (expression instanceof FieldExpression) {
            return new FieldExpressionWrapper((FieldExpression) expression, parent);
        } else if (expression instanceof VariableExpression) {
            return new VariableExpressionWrapper((VariableExpression) expression, parent);
        } else if (expression instanceof GStringExpression) {
            return new GStringExpressionWrapper((GStringExpression) expression, parent);
        } else if (expression instanceof MapExpression) {
            return new MapExpressionWrapper((MapExpression) expression, parent);
        } else if (expression instanceof MapEntryExpression) {
            return new MapEntryExpressionWrapper((MapEntryExpression) expression, parent);
        } else if (expression instanceof MethodCallExpression) {
            return new MethodCallExpressionWrapper((MethodCallExpression) expression, parent);
        } else if (expression instanceof MethodPointerExpression) {
            return new MethodPointerExpressionWrapper((MethodPointerExpression) expression, parent);
        } else if (expression instanceof ConstantExpression) {
            return new ConstantExpressionWrapper((ConstantExpression) expression, parent);
        } else if (expression instanceof PostfixExpression) {
            return new PostfixExpressionWrapper((PostfixExpression) expression, parent);
        } else if (expression instanceof PrefixExpression) {
            return new PrefixExpressionWrapper((PrefixExpression) expression, parent);
        } else if (expression instanceof RangeExpression) {
            return new RangeExpressionWrapper((RangeExpression) expression, parent);
        } else if (expression instanceof PropertyExpression) {
            return new PropertyExpressionWrapper((PropertyExpression) expression, parent);
        } else if (expression instanceof ClassExpression) {
            return new ClassExpressionWrapper((ClassExpression) expression, parent);
        } else if (expression instanceof ClosureListExpression) {
            return new ClosureListExpressionWrapper((ClosureListExpression) expression, parent);
        } else if (expression instanceof ListExpression) {
            return new ListExpressionWrapper((ListExpression) expression, parent);
        } else if (expression instanceof SpreadExpression) {
            return new SpreadExpressionWrapper((SpreadExpression) expression, parent);
        } else if (expression instanceof SpreadMapExpression) {
            return new SpreadMapExpressionWrapper((SpreadMapExpression) expression, parent);
        } else if (expression instanceof StaticMethodCallExpression) {
            return new StaticMethodCallExpressionWrapper((StaticMethodCallExpression) expression, parent);
        } else if (expression instanceof TernaryExpression) {
            return new TernaryExpressionWrapper((TernaryExpression) expression, parent);
        } else if (expression instanceof TupleExpression) {
            return new TupleExpressionWrapper((TupleExpression) expression, parent);
        } else if (expression instanceof UnaryMinusExpression) {
            return new UnaryMinusExpressionWrapper((UnaryMinusExpression) expression, parent);
        } else if (expression instanceof UnaryPlusExpression) {
            return new UnaryPlusExpressionWrapper((UnaryPlusExpression) expression, parent);
        } else if (expression instanceof EmptyExpression) {
            return new EmptyExpressionWrapper((EmptyExpression) expression, parent);
        } else {
            return null;
        }
    }

    public static String getParameterText(ParameterWrapper node) {
        if (node == null)
            return UNKNOWN;

        String name = node.getName() == null ? UNKNOWN : node.getName();
        String type = node.getType() == null || node.getType().getName() == null ? UNKNOWN : node.getType()
                .getName();
        if (node.getInitialExpression() != null) {
            return type + " " + name + " = " + node.getInitialExpression().getText();
        }
        return type + " " + name;
    }

    public static String getParametersText(ParameterWrapper[] parameters) {
        if (parameters == null)
            return "";
        if (parameters.length == 0)
            return "";
        StringBuilder result = new StringBuilder();
        int max = parameters.length;
        for (int x = 0; x < max; x++) {
            result.append(getParameterText(parameters[x]));
            if (x < (max - 1)) {
                result.append(", ");
            }
        }
        return result.toString();
    }
}
