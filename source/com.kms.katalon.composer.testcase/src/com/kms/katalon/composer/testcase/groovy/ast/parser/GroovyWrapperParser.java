package com.kms.katalon.composer.testcase.groovy.ast.parser;

import static com.kms.katalon.composer.testcase.preferences.ManualPreferenceValueInitializer.getMaximumLineWidth;
import static com.kms.katalon.composer.testcase.preferences.ManualPreferenceValueInitializer.isLineWrappingEnabled;

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.exceptions.GroovyParsingException;
import com.kms.katalon.composer.testcase.groovy.ast.ASTHasBlock;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.AnnotationNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.CommentWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.GenericsTypeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ImportNodeCollection;
import com.kms.katalon.composer.testcase.groovy.ast.ImportNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.MethodNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ParameterWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.TokenWrapper;
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
import com.kms.katalon.composer.testcase.groovy.ast.statements.CaseStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CatchStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ContinueStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.DoWhileStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ElseIfStatementWrapper;
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

public class GroovyWrapperParser {
    private static final String DEFAULT_INDENT_INCREASEMENT = "    ";

    public static final String[] GROOVY_IMPORTED_PACKAGES = { "java.io", "java.lang", "java.net", "java.util",
            "groovy.lang", "groovy.util" };

    public static final String[] GROOVY_IMPORTED_CLASSES = { "java.math.BigDecimal", "java.math.BigInteger" };

    private Stack<String> classNameStack = new Stack<String>();

    private String currentIndent = "";

    private boolean readyToIndent = false;

    private StringBuilder stringBuilder;

    private boolean needLineBreak = false;

    public GroovyWrapperParser(StringBuilder stringBuilder) {
        this.stringBuilder = stringBuilder;
    }

    public String getValue() {
        if (stringBuilder != null) {
            return stringBuilder.toString();
        }
        return StringUtils.EMPTY;
    }

    public void parse(Object object) {
        if (object instanceof ExpressionWrapper) {
            parseExpression((ExpressionWrapper) object);
        } else if (object instanceof StatementWrapper) {
            parseStatement((StatementWrapper) object);
        } else if (object instanceof ParameterWrapper) {
            parseParameter((ParameterWrapper) object);
        }
    }

    private void parseExpressionList(List<? extends ExpressionWrapper> expressions) {
        boolean first = true;
        for (Object object : expressions) {
            checkWrapLongLine();
            ExpressionWrapper expression = (ExpressionWrapper) object;
            if (!first) {
                print(", ");
            }
            first = false;
            parseExpression(expression);
        }
    }

    private void parseArgumentList(ArgumentListExpressionWrapper argumentListExpressionWrapper, boolean showTypes) {
        preParseASTNode(argumentListExpressionWrapper);
        int count = argumentListExpressionWrapper.getExpressions().size();
        for (ExpressionWrapper expression : argumentListExpressionWrapper.getExpressions()) {
            checkWrapLongLine();
            if (showTypes) {
                parseType(expression.getType());
                print(" ");
            }

            if (expression instanceof VariableExpressionWrapper) {
                parseVariable((VariableExpressionWrapper) expression, false);
            } else if (expression instanceof ConstantExpressionWrapper) {
                parseConstant((ConstantExpressionWrapper) expression, false);
            } else {
                parseExpression(expression);
            }
            count--;
            if (count > 0) {
                print(", ");
            }
        }
        postParseASTNode(argumentListExpressionWrapper);
    }

    private void parseArray(ArrayExpressionWrapper arrayExpressionWrapper) {
        preParseASTNode(arrayExpressionWrapper);
        print("new ");
        parseType(arrayExpressionWrapper.getElementType());
        print("[");
        String lastIndent = increaseIndent();
        parseExpressionList(arrayExpressionWrapper.getExpressions());
        resetIndent(lastIndent);
        print("]");
        postParseASTNode(arrayExpressionWrapper);
    }

    private void parseBoolean(BooleanExpressionWrapper booleanExpressionWrapper) {
        preParseASTNode(booleanExpressionWrapper);
        if (booleanExpressionWrapper.isReverse()) {
            print("!(");
            parseExpression(booleanExpressionWrapper.getExpression());
            print(")");
        } else {
            parseExpression(booleanExpressionWrapper.getExpression());
        }
        postParseASTNode(booleanExpressionWrapper);
    }

    private void parseBinary(BinaryExpressionWrapper binaryExpressionWrapper) {
        preParseASTNode(binaryExpressionWrapper);
        binaryExpressionWrapper.getOperation().getToken().getType();
        final ExpressionWrapper leftExpression = binaryExpressionWrapper.getLeftExpression();
        if (leftExpression instanceof BinaryExpressionWrapper) {
            print("(");
        }
        parseExpression(leftExpression);
        if (leftExpression instanceof BinaryExpressionWrapper) {
            print(")");
        }
        final ExpressionWrapper rightExpression = binaryExpressionWrapper.getRightExpression();
        if (rightExpression != null) {
            TokenWrapper token = binaryExpressionWrapper.getOperation();
            preParseASTNode(token);
            if (token.getText().equals("[")) {
                print(token.getText());
            } else {
                print(" " + token.getText() + " ");
            }
            postParseASTNode(token);
            checkWrapLongLine();
            if (rightExpression instanceof BinaryExpressionWrapper) {
                print("(");
            }
            parseExpression(rightExpression);
            if (rightExpression instanceof BinaryExpressionWrapper) {
                print(")");
            }
            if (binaryExpressionWrapper.getOperation().getText().equals("[")) {
                print("]");
            }
        }
        postParseASTNode(binaryExpressionWrapper);
    }

    private void parseBitwise(BitwiseNegationExpressionWrapper bitwiseNegationExpressionWrapper) {
        preParseASTNode(bitwiseNegationExpressionWrapper);
        print("~(");
        parseExpression(bitwiseNegationExpressionWrapper.getExpression());
        print(") ");
        postParseASTNode(bitwiseNegationExpressionWrapper);
    }

    private void parseCast(CastExpressionWrapper castExpressionWrapper) {
        preParseASTNode(castExpressionWrapper);
        String lastIndent = increaseIndent();
        print("((");
        parseExpression(castExpressionWrapper.getExpression());
        print(") as ");
        parseType(castExpressionWrapper.getType());
        print(")");
        resetIndent(lastIndent);
        postParseASTNode(castExpressionWrapper);
    }

    private void parseClass(ClassExpressionWrapper classExpressionWrapper) {
        preParseASTNode(classExpressionWrapper);
        parseType(classExpressionWrapper.getType());
        postParseASTNode(classExpressionWrapper);
    }

    private void parseClosure(ClosureExpressionWrapper closureExpressionWrapper) {
        preParseASTNode(closureExpressionWrapper);
        print("{ ");
        if (closureExpressionWrapper.getParameters() != null && closureExpressionWrapper.getParameters().length > 0) {
            parseParameters(closureExpressionWrapper.getParameters());
            print(" ->");
        }
        printLineBreak();
        String lastIndent = increaseIndent();
        parseASTHasBlock(closureExpressionWrapper);
        resetIndent(lastIndent);
        printLineBreak();
        print("}");
        postParseASTNode(closureExpressionWrapper);
    }

    private void parseConstant(ConstantExpressionWrapper constantExpressionWrapper, boolean unwrapQuotes) {
        preParseASTNode(constantExpressionWrapper);
        if (constantExpressionWrapper.getValue() instanceof String && !unwrapQuotes) {
            printString((String) constantExpressionWrapper.getValue());
        } else if (constantExpressionWrapper.getValue() instanceof Character) {
            print("'" + constantExpressionWrapper.getValue() + "'");
        } else {
            print(constantExpressionWrapper.getText());
        }
        postParseASTNode(constantExpressionWrapper);
    }

    private void printString(String string) {
        print("'" + escapeJavaString(string) + "'");
    }

    public static String escapeJavaString(String string) {
        return string.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\f", "\\f")
                .replace("'", "\\'");
    }

    public static String unescapeJavaString(String string) {
        return string;
    }

    private void parseConstructorCall(ConstructorCallExpressionWrapper constructorCallExpressionWrapper) {
        preParseASTNode(constructorCallExpressionWrapper);
        if (constructorCallExpressionWrapper.isSuperCall()) {
            print("super");
        } else if (constructorCallExpressionWrapper.isThisCall()) {
            print("this ");
        } else {
            print("new ");
            parseType(constructorCallExpressionWrapper.getType());
        }
        print("(");
        parseExpression(constructorCallExpressionWrapper.getArguments());
        print(")");
        postParseASTNode(constructorCallExpressionWrapper);
    }

    private void parseClosureList(ClosureListExpressionWrapper closureListExpressionWrapper) {
        preParseASTNode(closureListExpressionWrapper);
        boolean first = true;
        for (ExpressionWrapper expression : closureListExpressionWrapper.getExpressions()) {
            if (!first) {
                print("; ");
            }
            first = false;
            parseExpression(expression);
        }
        postParseASTNode(closureListExpressionWrapper);
    }

    private void parseDeclaration(DeclarationExpressionWrapper declarationExpressionWrapper) {
        preParseASTNode(declarationExpressionWrapper);
        if (declarationExpressionWrapper.getRightExpression() instanceof EmptyExpressionWrapper) {
            // This to prevent the problem that is the class
            // EmptyExpressionWrapper is
            // not yet implemented for ASTVisitor
            declarationExpressionWrapper.setRightExpression(
                    new ConstantExpressionWrapper(new ConstantExpression(null), declarationExpressionWrapper));
        }

        if (declarationExpressionWrapper.getLeftExpression() instanceof ArgumentListExpressionWrapper) {
            print("def ");
            parseArgumentList((ArgumentListExpressionWrapper) declarationExpressionWrapper.getLeftExpression(), true);
            print(declarationExpressionWrapper.getOperation().getText());
            parseExpression(declarationExpressionWrapper.getRightExpression());
            if (declarationExpressionWrapper.getOperation().getText().equals("[")) {
                print("]");
            }
        } else {
            if (declarationExpressionWrapper.getLeftExpression() instanceof VariableExpressionWrapper) {
                VariableExpressionWrapper variableExpressionWrapper = (VariableExpressionWrapper) declarationExpressionWrapper
                        .getLeftExpression();
                parseType(variableExpressionWrapper.getOriginType());
                print(" " + variableExpressionWrapper.getName());
            } else {
                parseExpression(declarationExpressionWrapper.getLeftExpression());
            }
            if (declarationExpressionWrapper.getRightExpression() != null) {
                TokenWrapper token = declarationExpressionWrapper.getOperation();
                preParseASTNode(token);
                print(" " + token.getText() + " ");
                postParseASTNode(token);
                parseExpression(declarationExpressionWrapper.getRightExpression());
                if (declarationExpressionWrapper.getOperation().getText().equals("[")) {
                    print("]");
                }
            }
        }
        postParseASTNode(declarationExpressionWrapper);
    }

    private void parseFieldExpression(FieldExpressionWrapper fieldExpressionWrapper) {
        preParseASTNode(fieldExpressionWrapper);
        if (fieldExpressionWrapper.getField() != null) {
            print(fieldExpressionWrapper.getField().getName());
        }
        postParseASTNode(fieldExpressionWrapper);
    }

    private void parseGString(GStringExpressionWrapper gStringExpressionWrapper) {
        preParseASTNode(gStringExpressionWrapper);
        print('"' + gStringExpressionWrapper.getText() + '"');
        postParseASTNode(gStringExpressionWrapper);
    }

    private void parseMap(MapExpressionWrapper mapExpressionWrapper) {
        preParseASTNode(mapExpressionWrapper);
        print("[");
        String lastIndent = increaseIndent();
        if (mapExpressionWrapper.getMapEntryExpressions().size() == 0) {
            print(":");
        } else {
            parseExpressionList(mapExpressionWrapper.getMapEntryExpressions());
        }
        resetIndent(lastIndent);
        print("]");
        postParseASTNode(mapExpressionWrapper);
    }

    private void parseMapEntry(MapEntryExpressionWrapper mapEntryExpressionWrapper) {
        preParseASTNode(mapEntryExpressionWrapper);
        if (mapEntryExpressionWrapper.getKeyExpression() instanceof SpreadMapExpressionWrapper) {
            print("*");
        } else {
            print("(");
            parseExpression(mapEntryExpressionWrapper.getKeyExpression());
            print(")");
        }
        print(" : ");
        parseExpression(mapEntryExpressionWrapper.getValueExpression());
        postParseASTNode(mapEntryExpressionWrapper);
    }

    private void parseMethodCall(MethodCallExpressionWrapper methodCallExpressionWrapper) {
        preParseASTNode(methodCallExpressionWrapper);
        ExpressionWrapper objectExp = methodCallExpressionWrapper.getObjectExpression();
        boolean isCustomKeywordMethod = false;
        if (objectExp instanceof VariableExpressionWrapper) {
            if (((VariableExpressionWrapper) objectExp).getName().equals("CustomKeywords")) {
                if (methodCallExpressionWrapper.getMethod() instanceof ConstantExpressionWrapper) {
                    String methodName = methodCallExpressionWrapper.getMethodAsString();
                    if (!methodName.startsWith("'")) {
                        isCustomKeywordMethod = true;
                    }
                }
            }

            if (!((VariableExpressionWrapper) objectExp).getName().equals("this")) {
                parseVariable((VariableExpressionWrapper) objectExp, false);
            }

        } else {
            if (objectExp instanceof BinaryExpressionWrapper) {
                print("(");
            }
            parseExpression(objectExp);
            if (objectExp instanceof BinaryExpressionWrapper) {
                print(")");
            }
        }
        if (methodCallExpressionWrapper.isSpreadSafe()) {
            print("*");
        }
        if (methodCallExpressionWrapper.isSafe()) {
            print("?");
        }
        if (!(objectExp instanceof VariableExpressionWrapper
                && ((VariableExpressionWrapper) objectExp).getName().equals("this"))) {
            print(".");
        }
        ExpressionWrapper method = methodCallExpressionWrapper.getMethod();
        if (method instanceof ConstantExpressionWrapper) {
            preParseASTNode(method);
            if (isCustomKeywordMethod) {
                print("'");
            }
            print(String.valueOf(((ConstantExpressionWrapper) method).getValue()));
            if (isCustomKeywordMethod) {
                print("'");
            }
            print("(");
            String innerLastIndent = increaseIndent();
            postParseASTNode(method);
            parseExpression(methodCallExpressionWrapper.getArguments());
            print(")");
            postParseASTNode(methodCallExpressionWrapper);
            resetIndent(innerLastIndent);
        } else {
            parseExpression(method);
            print("(");
            String innerLastIndent = increaseIndent();
            parseExpression(methodCallExpressionWrapper.getArguments());
            print(")");
            postParseASTNode(methodCallExpressionWrapper);
            resetIndent(innerLastIndent);
        }
    }

    private void parseMethodPointer(MethodPointerExpressionWrapper methodPointerExpressionWrapper) {
        preParseASTNode(methodPointerExpressionWrapper);
        parseExpression(methodPointerExpressionWrapper.getExpression());
        print(".&");
        parseExpression(methodPointerExpressionWrapper.getMethodName());
        postParseASTNode(methodPointerExpressionWrapper);
    }

    private void parsePostfix(PostfixExpressionWrapper postfixExpressionWrapper) {
        preParseASTNode(postfixExpressionWrapper);
        TokenWrapper token = postfixExpressionWrapper.getOperation();
        if (postfixExpressionWrapper.getExpression() instanceof VariableExpressionWrapper) {
            parseExpression(postfixExpressionWrapper.getExpression());
        } else {
            print("(");
            parseExpression(postfixExpressionWrapper.getExpression());
            print(")");
        }
        print(token.getText());
        postParseASTNode(token);
        postParseASTNode(postfixExpressionWrapper);
    }

    private void parsePrefix(PrefixExpressionWrapper prefixExpressionWrapper) {
        preParseASTNode(prefixExpressionWrapper);
        TokenWrapper token = prefixExpressionWrapper.getOperation();
        preParseASTNode(token);
        print(token.getText());
        postParseASTNode(token);
        if (prefixExpressionWrapper.getExpression() instanceof VariableExpressionWrapper) {
            parseExpression(prefixExpressionWrapper.getExpression());
        } else {
            print("(");
            parseExpression(prefixExpressionWrapper.getExpression());
            print(")");
        }
        postParseASTNode(prefixExpressionWrapper);
    }

    private void parseProperty(PropertyExpressionWrapper propertyExpressionWrapper) {
        preParseASTNode(propertyExpressionWrapper);
        parseExpression(propertyExpressionWrapper.getObjectExpression());
        if (propertyExpressionWrapper.isSpreadSafe()) {
            print("*");
        }
        if (propertyExpressionWrapper.isSpreadSafe()) {
            print("*");
        } else if (propertyExpressionWrapper.isSafe()) {
            print("?");
        }
        print(".");
        if (propertyExpressionWrapper.getProperty() instanceof ConstantExpressionWrapper) {
            preParseASTNode(propertyExpressionWrapper.getProperty());
            print(propertyExpressionWrapper.getPropertyAsString());
            postParseASTNode(propertyExpressionWrapper.getProperty());
        } else {
            parseExpression(propertyExpressionWrapper.getProperty());
        }
        postParseASTNode(propertyExpressionWrapper);
    }

    private void parseRange(RangeExpressionWrapper rangeExpressionWrapper) {
        preParseASTNode(rangeExpressionWrapper);
        print("(");
        parseExpression(rangeExpressionWrapper.getFrom());
        print("..");
        parseExpression(rangeExpressionWrapper.getTo());
        print(")");
        postParseASTNode(rangeExpressionWrapper);
    }

    private void parseList(ListExpressionWrapper listExpressionWrapper) {
        preParseASTNode(listExpressionWrapper);
        print("[");
        String lastIndent = increaseIndent();
        parseExpressionList(listExpressionWrapper.getExpressions());
        resetIndent(lastIndent);
        print("]");
        postParseASTNode(listExpressionWrapper);
    }

    private void parseSpread(SpreadExpressionWrapper spreadExpressionWrapper) {
        preParseASTNode(spreadExpressionWrapper);
        print("*");
        parseExpression(spreadExpressionWrapper.getExpression());
        postParseASTNode(spreadExpressionWrapper);
    }

    private void parseSpreadMap(SpreadMapExpressionWrapper spreadMapExpressionWrapper) {
        preParseASTNode(spreadMapExpressionWrapper);
        print("*:");
        parseExpression(spreadMapExpressionWrapper.getExpression());
        postParseASTNode(spreadMapExpressionWrapper);
    }

    private void parseStaticMethodCall(StaticMethodCallExpressionWrapper staticMethodCallExpressionWrapper) {
        preParseASTNode(staticMethodCallExpressionWrapper);
        print(staticMethodCallExpressionWrapper.getOwnerType().getName() + "."
                + staticMethodCallExpressionWrapper.getMethod());
        print("(");
        parseExpression(staticMethodCallExpressionWrapper.getArguments());
        print(")");
        postParseASTNode(staticMethodCallExpressionWrapper);
    }

    private void parseTenary(TernaryExpressionWrapper ternaryExpressionWrapper) {
        preParseASTNode(ternaryExpressionWrapper);
        parseBoolean(ternaryExpressionWrapper.getBooleanExpression());
        print(" ? ");
        parseExpression(ternaryExpressionWrapper.getTrueExpression());
        print(" : ");
        parseExpression(ternaryExpressionWrapper.getFalseExpression());
        postParseASTNode(ternaryExpressionWrapper);
    }

    private void parseTuple(TupleExpressionWrapper tupleExpressionWrapper) {
        preParseASTNode(tupleExpressionWrapper);
        print("(");
        String lastIndent = increaseIndent();
        parseExpressionList(tupleExpressionWrapper.getExpressions());
        print(")");
        resetIndent(lastIndent);
        postParseASTNode(tupleExpressionWrapper);
    }

    private void parseUnaryMinus(UnaryMinusExpressionWrapper unaryMinusExpressionWrapper) {
        preParseASTNode(unaryMinusExpressionWrapper);
        print("-(");
        parseExpression(unaryMinusExpressionWrapper.getExpression());
        print(")");
        postParseASTNode(unaryMinusExpressionWrapper);
    }

    private void parseUnaryPlus(UnaryPlusExpressionWrapper unaryPlusExpressionWrapper) {
        preParseASTNode(unaryPlusExpressionWrapper);
        print("+(");
        parseExpression(unaryPlusExpressionWrapper.getExpression());
        print(")");
        postParseASTNode(unaryPlusExpressionWrapper);
    }

    private void parseVariable(VariableExpressionWrapper variableExpressionWrapper, boolean spacePad) {
        preParseASTNode(variableExpressionWrapper);
        if (spacePad) {
            print(' ' + variableExpressionWrapper.getName() + ' ');
        } else {
            print(variableExpressionWrapper.getName());
        }
        postParseASTNode(variableExpressionWrapper);
    }

    private void parseExpression(ExpressionWrapper expression) {
        if (expression instanceof ArgumentListExpressionWrapper) {
            parseArgumentList((ArgumentListExpressionWrapper) expression, false);
        } else if (expression instanceof ArrayExpressionWrapper) {
            parseArray((ArrayExpressionWrapper) expression);
        } else if (expression instanceof BooleanExpressionWrapper) {
            parseBoolean((BooleanExpressionWrapper) expression);
        } else if (expression instanceof BitwiseNegationExpressionWrapper) {
            parseBitwise((BitwiseNegationExpressionWrapper) expression);
        } else if (expression instanceof CastExpressionWrapper) {
            parseCast((CastExpressionWrapper) expression);
        } else if (expression instanceof ClosureExpressionWrapper) {
            parseClosure((ClosureExpressionWrapper) expression);
        } else if (expression instanceof ConstructorCallExpressionWrapper) {
            parseConstructorCall((ConstructorCallExpressionWrapper) expression);
        } else if (expression instanceof DeclarationExpressionWrapper) {
            parseDeclaration((DeclarationExpressionWrapper) expression);
        } else if (expression instanceof BinaryExpressionWrapper) {
            parseBinary((BinaryExpressionWrapper) expression);
        } else if (expression instanceof FieldExpressionWrapper) {
            parseFieldExpression((FieldExpressionWrapper) expression);
        } else if (expression instanceof VariableExpressionWrapper) {
            parseVariable((VariableExpressionWrapper) expression, false);
        } else if (expression instanceof GStringExpressionWrapper) {
            parseGString((GStringExpressionWrapper) expression);
        } else if (expression instanceof MapExpressionWrapper) {
            parseMap((MapExpressionWrapper) expression);
        } else if (expression instanceof MapEntryExpressionWrapper) {
            parseMapEntry((MapEntryExpressionWrapper) expression);
        } else if (expression instanceof MethodCallExpressionWrapper) {
            parseMethodCall((MethodCallExpressionWrapper) expression);
        } else if (expression instanceof MethodPointerExpressionWrapper) {
            parseMethodPointer((MethodPointerExpressionWrapper) expression);
        } else if (expression instanceof ConstantExpressionWrapper) {
            parseConstant((ConstantExpressionWrapper) expression, false);
        } else if (expression instanceof PostfixExpressionWrapper) {
            parsePostfix((PostfixExpressionWrapper) expression);
        } else if (expression instanceof PrefixExpressionWrapper) {
            parsePrefix((PrefixExpressionWrapper) expression);
        } else if (expression instanceof RangeExpressionWrapper) {
            parseRange((RangeExpressionWrapper) expression);
        } else if (expression instanceof PropertyExpressionWrapper) {
            parseProperty((PropertyExpressionWrapper) expression);
        } else if (expression instanceof ClassExpressionWrapper) {
            parseClass((ClassExpressionWrapper) expression);
        } else if (expression instanceof ClosureListExpressionWrapper) {
            parseClosureList((ClosureListExpressionWrapper) expression);
        } else if (expression instanceof ListExpressionWrapper) {
            parseList((ListExpressionWrapper) expression);
        } else if (expression instanceof SpreadExpressionWrapper) {
            parseSpread((SpreadExpressionWrapper) expression);
        } else if (expression instanceof SpreadMapExpressionWrapper) {
            parseSpreadMap((SpreadMapExpressionWrapper) expression);
        } else if (expression instanceof StaticMethodCallExpressionWrapper) {
            parseStaticMethodCall((StaticMethodCallExpressionWrapper) expression);
        } else if (expression instanceof TernaryExpressionWrapper) {
            parseTenary((TernaryExpressionWrapper) expression);
        } else if (expression instanceof TupleExpressionWrapper) {
            parseTuple((TupleExpressionWrapper) expression);
        } else if (expression instanceof UnaryMinusExpressionWrapper) {
            parseUnaryMinus((UnaryMinusExpressionWrapper) expression);
        } else if (expression instanceof UnaryPlusExpressionWrapper) {
            parseUnaryPlus((UnaryPlusExpressionWrapper) expression);
        } else if (expression instanceof EmptyExpressionWrapper) {
            parseExpression((EmptyExpressionWrapper) expression);
        }
    }

    private void parseStatement(StatementWrapper statement) {
        if (statement instanceof ExpressionStatementWrapper) {
            parseExpressionStatement((ExpressionStatementWrapper) statement);
        } else if (statement instanceof ReturnStatementWrapper) {
            parseReturn((ReturnStatementWrapper) statement);
        } else if (statement instanceof AssertStatementWrapper) {
            parseAssert((AssertStatementWrapper) statement);
        } else if (statement instanceof BreakStatementWrapper) {
            parseBreak((BreakStatementWrapper) statement);
        } else if (statement instanceof SwitchStatementWrapper) {
            parseSwitch((SwitchStatementWrapper) statement);
        } else if (statement instanceof ThrowStatementWrapper) {
            parseThrow((ThrowStatementWrapper) statement);
        } else if (statement instanceof TryCatchStatementWrapper) {
            parseTryCatch((TryCatchStatementWrapper) statement);
        } else if (statement instanceof IfStatementWrapper) {
            parseIf((IfStatementWrapper) statement);
        } else if (statement instanceof ForStatementWrapper) {
            parseFor((ForStatementWrapper) statement);
        } else if (statement instanceof WhileStatementWrapper) {
            parseWhile((WhileStatementWrapper) statement);
        } else if (statement instanceof DoWhileStatementWrapper) {
            parseDoWhile((DoWhileStatementWrapper) statement);
        } else if (statement instanceof SynchronizedStatementWrapper) {
            parseSynchronized((SynchronizedStatementWrapper) statement);
        } else if (statement instanceof ContinueStatementWrapper) {
            parseContinue((ContinueStatementWrapper) statement);
        } else if (statement instanceof CaseStatementWrapper || statement instanceof CatchStatementWrapper) {
            // do nothing
        } else if (statement instanceof BlockStatementWrapper) {
            parseBlock((BlockStatementWrapper) statement);
        }
    }

    private void parseDescription(StatementWrapper statementWrapper) {
        if (statementWrapper.hasDescription()) {
            printString(statementWrapper.getDescription());
            printLineBreak();
        }
    }

    private void parseStatements(List<? extends StatementWrapper> statements) {
        int count = 0;
        for (StatementWrapper statement : statements) {
            count++;
            parseStatement(statement);
            if (count < statements.size()) {
                printDoubleLineBreak();
            }
        }
    }

    private void parseASTHasBlock(ASTHasBlock astHasBlock) {
        parseBlock(astHasBlock.getBlock());
    }

    private void parseBlock(BlockStatementWrapper blockStatementWrapper) {
        preParseASTNode(blockStatementWrapper);
        if (blockStatementWrapper.getStatements().isEmpty() && !blockStatementWrapper.getInsideComments().isEmpty()) {
            parseComments(blockStatementWrapper.getInsideComments());
        } else {
            parseStatements(blockStatementWrapper.getStatements());
        }
        postParseASTNode(blockStatementWrapper);
    }

    private void parseExpressionStatement(ExpressionStatementWrapper expressionStatementWrapper) {
        preParseASTNode(expressionStatementWrapper);
        parseExpression(expressionStatementWrapper.getExpression());
        postParseASTNode(expressionStatementWrapper);
    }

    private void parseReturn(ReturnStatementWrapper returnStatementWrapper) {
        preParseASTNode(returnStatementWrapper);
        print("return ");
        parseExpression(returnStatementWrapper.getExpression());
        postParseASTNode(returnStatementWrapper);
        printLineBreak();
    }

    private void parseAssert(AssertStatementWrapper assertStatementWrapper) {
        preParseASTNode(assertStatementWrapper);
        print("assert ");
        parseBoolean(assertStatementWrapper.getBooleanExpression());
        if (assertStatementWrapper.getMessageExpression() instanceof ConstantExpressionWrapper
                && !(((ConstantExpressionWrapper) assertStatementWrapper.getMessageExpression()).getValue() == null)) {
            print(" : ");
            parseExpression(assertStatementWrapper.getMessageExpression());
        }
        postParseASTNode(assertStatementWrapper);
    }

    private void parseBreak(BreakStatementWrapper breakStatementWrapper) {
        preParseASTNode(breakStatementWrapper);
        print("break");
        postParseASTNode(breakStatementWrapper);
        printLineBreak();
    }

    private void parseContinue(ContinueStatementWrapper continueStatementWrapper) {
        preParseASTNode(continueStatementWrapper);
        print("continue");
        postParseASTNode(continueStatementWrapper);
        printLineBreak();
    }

    private void parseSwitch(SwitchStatementWrapper switchStatementWrapper) {
        preParseASTNode(switchStatementWrapper);
        print("switch (");
        parseExpression(switchStatementWrapper.getExpression());
        print(") {");
        printLineBreak();

        String lastIndent = increaseIndent();
        for (CaseStatementWrapper caseStatementWrapper : switchStatementWrapper.getComplexChildStatements()) {
            parseCase(caseStatementWrapper);
            printLineBreak();
        }

        if (switchStatementWrapper.hasLastStatement()) {
            preParseASTNode(switchStatementWrapper.getLastStatement());
            print("default:");
            printLineBreak();

            String lastInnerIndent = increaseIndent();
            parseASTHasBlock(switchStatementWrapper.getLastStatement());
            postParseASTNode(switchStatementWrapper.getLastStatement());
            resetIndent(lastInnerIndent);
        }
        resetIndent(lastIndent);

        print("}");
        postParseASTNode(switchStatementWrapper);
        printLineBreak();
    }

    private void parseCase(CaseStatementWrapper caseStatementWrapper) {
        preParseASTNode(caseStatementWrapper);
        print("case ");
        parseExpression(caseStatementWrapper.getExpression());
        print(":");
        printLineBreak();

        String lastIndent = increaseIndent();
        parseASTHasBlock(caseStatementWrapper);
        postParseASTNode(caseStatementWrapper);
        resetIndent(lastIndent);
        printLineBreak();
    }

    private void parseThrow(ThrowStatementWrapper throwStatementWrapper) {
        preParseASTNode(throwStatementWrapper);
        print("throw ");
        parseExpression(throwStatementWrapper.getExpression());
        postParseASTNode(throwStatementWrapper);
        printLineBreak();
    }

    private void parseTryCatch(TryCatchStatementWrapper tryCatchStatementWrapper) {
        preParseASTNode(tryCatchStatementWrapper);
        print("try {");
        printLineBreak();

        String lastIndent = increaseIndent();
        parseASTHasBlock(tryCatchStatementWrapper);
        resetIndent(lastIndent);

        printLineBreak();
        print("}");
        printLineBreak();
        for (CatchStatementWrapper catchStatementWrapper : tryCatchStatementWrapper.getComplexChildStatements()) {
            parseCatch(catchStatementWrapper);
        }
        if (tryCatchStatementWrapper.hasLastStatement()) {
            preParseASTNode(tryCatchStatementWrapper.getLastStatement());
            print("finally { ");
            printLineBreak();

            lastIndent = increaseIndent();
            parseASTHasBlock(tryCatchStatementWrapper.getLastStatement());
            resetIndent(lastIndent);
            printLineBreak();
            print("}");
            postParseASTNode(tryCatchStatementWrapper.getLastStatement());
            printLineBreak();
        }
        postParseASTNode(tryCatchStatementWrapper);
        printLineBreak();
    }

    private void parseCatch(CatchStatementWrapper catchStatementWrapper) {
        preParseASTNode(catchStatementWrapper);
        print("catch (");
        parseParameters(new ParameterWrapper[] { catchStatementWrapper.getVariable() });
        print(") {");
        printLineBreak();

        String lastIndent = increaseIndent();
        parseASTHasBlock(catchStatementWrapper);
        resetIndent(lastIndent);
        printLineBreak();
        print("} ");
        postParseASTNode(catchStatementWrapper);
        printLineBreak();
    }

    private void parseBooleanForStatement(BooleanExpressionWrapper booleanExpressionWrapper) {
        preParseASTNode(booleanExpressionWrapper);
        printBlankSpace();
        print("(");
        if (booleanExpressionWrapper.isReverse()) {
            print("!(");
            parseExpression(booleanExpressionWrapper.getExpression());
            print(")");
        } else {
            parseExpression(booleanExpressionWrapper.getExpression());
        }
        print(")");
        postParseASTNode(booleanExpressionWrapper);
    }

    private void parseIf(IfStatementWrapper ifStatementWrapper) {
        preParseASTNode(ifStatementWrapper);
        print("if");
        parseBooleanForStatement(ifStatementWrapper.getBooleanExpression());
        printBlankSpace();
        print("{");
        printLineBreak();
        String lastIndent = increaseIndent();
        parseASTHasBlock(ifStatementWrapper);
        resetIndent(lastIndent);
        printLineBreak();
        print("}");
        postParseASTNode(ifStatementWrapper);

        for (ElseIfStatementWrapper elseIfStatement : ifStatementWrapper.getComplexChildStatements()) {
            preParseASTNode(elseIfStatement);
            printBlankSpace();
            print("else if");
            parseBooleanForStatement(elseIfStatement.getBooleanExpression());
            printBlankSpace();
            print("{");

            printLineBreak();
            lastIndent = increaseIndent();
            parseASTHasBlock(elseIfStatement);
            resetIndent(lastIndent);

            printLineBreak();
            print("}");
            postParseASTNode(elseIfStatement);
        }
        if (ifStatementWrapper.hasLastStatement()) {
            preParseASTNode(ifStatementWrapper.getLastStatement());
            printBlankSpace();
            print("else {");
            printLineBreak();

            lastIndent = increaseIndent();
            parseASTHasBlock(ifStatementWrapper.getLastStatement());
            resetIndent(lastIndent);

            printLineBreak();
            print("}");
            postParseASTNode(ifStatementWrapper.getLastStatement());
        }
        printLineBreak();
    }

    private void parseFor(ForStatementWrapper forStatementWrapper) {
        preParseASTNode(forStatementWrapper);
        print("for (");
        if (!(forStatementWrapper.getCollectionExpression() instanceof ClosureListExpressionWrapper)) {
            if (!ForStatementWrapper.isForLoopDummy(forStatementWrapper.getVariable())) {
                parseParameters(new ParameterWrapper[] { forStatementWrapper.getVariable() });
                print(" : ");
            }
        }
        parseExpression(forStatementWrapper.getCollectionExpression());
        print(") {");
        printLineBreak();

        String lastIndent = increaseIndent();
        parseASTHasBlock(forStatementWrapper);
        resetIndent(lastIndent);

        printLineBreak();
        print("}");
        postParseASTNode(forStatementWrapper);
        printLineBreak();
    }

    private void parseWhile(WhileStatementWrapper whileStatementWrapper) {
        preParseASTNode(whileStatementWrapper);
        print("while");
        parseBooleanForStatement(whileStatementWrapper.getBooleanExpression());
        printBlankSpace();
        print("{");
        printLineBreak();

        String lastIndent = increaseIndent();
        parseASTHasBlock(whileStatementWrapper);
        resetIndent(lastIndent);

        printLineBreak();
        print("}");
        postParseASTNode(whileStatementWrapper);
        printLineBreak();
    }

    private void parseDoWhile(DoWhileStatementWrapper doWhileStatementWrapper) {
        preParseASTNode(doWhileStatementWrapper);
        print("do {");
        printLineBreak();

        String lastIndent = increaseIndent();
        parseASTHasBlock(doWhileStatementWrapper);
        resetIndent(lastIndent);

        print("} while");
        parseBooleanForStatement(doWhileStatementWrapper.getBooleanExpression());
        postParseASTNode(doWhileStatementWrapper);
        printLineBreak();
    }

    private void parseSynchronized(SynchronizedStatementWrapper synchronizedStatementWrapper) {
        preParseASTNode(synchronizedStatementWrapper);
        print("synchronized (");
        parseExpression(synchronizedStatementWrapper.getExpression());
        print(") {");
        printLineBreak();

        String lastIndent = increaseIndent();
        parseExpression(synchronizedStatementWrapper.getExpression());
        resetIndent(lastIndent);
        print("}");
        postParseASTNode(synchronizedStatementWrapper);
        printLineBreak();
    }

    private void parseParameter(ParameterWrapper parameter) {
        preParseASTNode(parameter);
        for (AnnotationNodeWrapper annotation : parameter.getAnnotations()) {
            parseAnnotationNode(annotation);
            print(" ");
        }

        parseModifiers(parameter.getModifiers());
        parseType(parameter.getType());

        print(" " + parameter.getName());
        if (parameter.getInitialExpression() != null
                && !(parameter.getInitialExpression() instanceof EmptyExpressionWrapper)) {
            print(" = ");
            parseExpression(parameter.getInitialExpression());
        }
        postParseASTNode(parameter);
    }

    private void parseParameters(ParameterWrapper[] parameters) {
        boolean first = true;
        for (ParameterWrapper parameter : parameters) {
            if (!first) {
                print(", ");
            }
            first = false;

            parseParameter(parameter);
        }
    }

    private void parseModifiers(int modifiers) {
        if (Modifier.isAbstract(modifiers)) {
            print("abstract ");
        }
        if (Modifier.isFinal(modifiers)) {
            print("final ");
        }
        if (Modifier.isInterface(modifiers)) {
            print("interface ");
        }
        if (Modifier.isNative(modifiers)) {
            print("native ");
        }
        if (Modifier.isPrivate(modifiers)) {
            print("private ");
        }
        if (Modifier.isProtected(modifiers)) {
            print("protected ");
        }
        if (Modifier.isPublic(modifiers)) {
            // do nothing
        }
        if (Modifier.isStatic(modifiers)) {
            print("static ");
        }
        if (Modifier.isSynchronized(modifiers)) {
            print("synchronized ");
        }
        if (Modifier.isTransient(modifiers)) {
            print("transient ");
        }
        if (Modifier.isVolatile(modifiers)) {
            print("volatile ");
        }
    }

    private void parseAnnotationNode(AnnotationNodeWrapper annotationNodeWrapper) {
        preParseASTNode(annotationNodeWrapper);
        print('@' + annotationNodeWrapper.getClassNode().getName());
        if (annotationNodeWrapper.getMembers() != null && !annotationNodeWrapper.getMembers().isEmpty()) {
            print("(");
            boolean first = true;

            Iterator<Entry<String, ExpressionWrapper>> it = annotationNodeWrapper.getMembers().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, ExpressionWrapper> pairs = (Map.Entry<String, ExpressionWrapper>) it.next();
                if (first) {
                    first = false;
                } else {
                    print(", ");
                }
                print(pairs.getKey() + " = ");
                parseExpression(pairs.getValue());
            }
            print(")");
        }
        postParseASTNode(annotationNodeWrapper);
    }

    private void parseType(ClassNodeWrapper classNode) {
        String name = classNode.getName();
        if (name.startsWith("[")) {
            int numDimensions = 0;
            while (name.charAt(numDimensions) == '[') {
                numDimensions++;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(classNode.getComponentType() == null ? Object.class.getSimpleName()
                    : classNode.getComponentType().getNameWithoutPackage());
            for (int i = 0; i < numDimensions; i++) {
                stringBuilder.append("[]");
            }
            print(stringBuilder.toString());
        } else if (classNode.getName().equals(Object.class.getName())) {
            print("def");
        } else {
            ImportNodeCollection importNodeCollection = classNode.getScriptClass().getImportNodeCollection();
            if (importNodeCollection != null && importNodeCollection.hasAlias(name)) {
                print(importNodeCollection.getBestMatchForAliasName(name));
            } else {
                boolean isImported = false;
                for (String groovyImportedClass : GROOVY_IMPORTED_CLASSES) {
                    if (name.equals(groovyImportedClass)) {
                        isImported = true;
                        break;
                    }
                }
                for (String groovyImportedPackage : GROOVY_IMPORTED_PACKAGES) {
                    if (name.startsWith(groovyImportedPackage)) {
                        isImported = true;
                        break;
                    }
                }
                if (isImported) {
                    print(classNode.getNameWithoutPackage());
                } else {
                    print(name);
                }
            }
        }
        parseGenericTypes(classNode.getGenericsTypes());
    }

    private void parseGenericTypes(GenericsTypeWrapper[] generics) {
        if (generics != null && generics.length > 0) {
            print("<");
            boolean first = true;
            for (GenericsTypeWrapper generic : generics) {
                if (!first) {
                    print(", ");
                }
                first = false;
                print(generic.getName());
                if (generic.getUpperBounds() != null && generic.getUpperBounds().length > 0) {
                    print(" extends ");
                    boolean innerFirst = true;
                    for (ClassNodeWrapper upperBound : generic.getUpperBounds()) {
                        if (!innerFirst) {
                            print(" & ");
                        }
                        innerFirst = false;
                        parseType(upperBound);
                    }
                }
                if (generic.getLowerBound() != null) {
                    print(" super ");
                    parseType(generic.getLowerBound());
                }
            }
            print(">");
        }
    }

    private void parseMethod(MethodNodeWrapper methodNodeWrapper) {
        preParseASTNode(methodNodeWrapper);
        for (AnnotationNodeWrapper annotationNodeWrapper : methodNodeWrapper.getAnnotations()) {
            parseAnnotationNode(annotationNodeWrapper);
            printLineBreak();
        }
        parseModifiers(methodNodeWrapper.getModifiers());
        if (methodNodeWrapper.getName().equals("<init>")) {
            print(classNameStack.peek() + "(");
            parseParameters(methodNodeWrapper.getParameters());
            print(") {");
            printLineBreak();
        } else if (methodNodeWrapper.getName().equals("<clinit>")) {
            print("{ "); // will already have 'static' from modifiers
            printLineBreak();
        } else {
            parseType(methodNodeWrapper.getReturnType());
            print(" " + methodNodeWrapper.getName() + "(");
            parseParameters(methodNodeWrapper.getParameters());
            print(")");
            if (methodNodeWrapper.getExceptions() != null && methodNodeWrapper.getExceptions().length > 0) {
                boolean first = true;
                print(" throws ");
                for (ClassNodeWrapper exceptionClassNode : methodNodeWrapper.getExceptions()) {
                    if (!first) {
                        print(", ");
                    }
                    first = false;
                    parseType(exceptionClassNode);
                }
            }
            print(" {");
            printLineBreak();
        }

        String lastIndent = increaseIndent();
        parseASTHasBlock(methodNodeWrapper);
        resetIndent(lastIndent);
        printLineBreak();
        print("}");
        printDoubleLineBreak();
        postParseASTNode(methodNodeWrapper);
    }

    private String increaseIndent() {
        String lastIndent = currentIndent;
        currentIndent = currentIndent + DEFAULT_INDENT_INCREASEMENT;
        return lastIndent;
    }

    private void resetIndent(String lastIndent) {
        currentIndent = lastIndent;
    }

    private void parseImport(ImportNodeWrapper importNodeWrapper) {
        if (importNodeWrapper == null) {
            return;
        }
        preParseASTNode(importNodeWrapper);
        for (AnnotationNodeWrapper annotationNodeWrapper : importNodeWrapper.getAnnotations()) {
            parseAnnotationNode(annotationNodeWrapper);
            printLineBreak();
        }
        print(importNodeWrapper.getText());
        postParseASTNode(importNodeWrapper);
        printLineBreak();
    }

    private void parseComments(List<CommentWrapper> comments) {
        for (CommentWrapper comment : comments) {
            printBlankSpace();
            print(comment.getComment());
            if (!comment.isMultiLine() || (comment.getParent() instanceof StatementWrapper)) {
                needLineBreak = true;
            }
            printBlankSpace();
        }
    }

    private void preParseASTNode(ASTNodeWrapper node) {
        if (!node.getPreceddingComments().isEmpty()) {
            parseComments(node.getPreceddingComments());
        }
        if (node instanceof StatementWrapper) {
            StatementWrapper statement = (StatementWrapper) node;
            parseDescription(statement);
            if (statement.canHaveLabel() && !StringUtils.isEmpty(statement.getLabel())) {
                print(statement.getLabel() + ": ");
            }
        }
    }

    private void postParseASTNode(ASTNodeWrapper node) {
        if (!node.getFollowingComments().isEmpty()) {
            parseComments(node.getFollowingComments());
        }
    }

    public static ScriptNodeWrapper parseGroovyScriptIntoNodeWrapper(String scriptContent)
            throws GroovyParsingException {
        if (scriptContent == null) {
            return null;
        }
        scriptContent = scriptContent.trim();
        if (scriptContent.isEmpty()) {
            return null;
        }
        try {
            List<ASTNode> resultNodes = new AstBuilder().buildFromString(CompilePhase.CONVERSION, false, scriptContent);
            for (ASTNode resultNode : resultNodes) {
                if (resultNode instanceof ClassNode && ((ClassNode) resultNode).isScript()) {
                    return new ScriptNodeWrapper((ClassNode) resultNode);
                }
            }
        } catch (CompilationFailedException e) {
            throw new GroovyParsingException(e);
        }
        return null;
    }

    public static StatementWrapper parseGroovyScriptAndGetFirstStatement(String scriptContent) {
        ScriptNodeWrapper script;
        try {
            script = parseGroovyScriptIntoNodeWrapper(scriptContent);
        } catch (GroovyParsingException e) {
            LoggerSingleton.logError(e);
            return null;
        }
        if (script == null) {
            return null;
        }
        if (script.getBlock().getStatements().size() > 0) {
            return script.getBlock().getStatements().get(0);
        }
        return null;
    }

    public static ExpressionWrapper parseGroovyScriptAndGetFirstExpression(String scriptContent) {
        ScriptNodeWrapper script;
        try {
            script = parseGroovyScriptIntoNodeWrapper(scriptContent);
        } catch (GroovyParsingException e) {
            LoggerSingleton.logError(e);
            return null;
        }
        if (script == null) {
            return null;
        }
        script.addDefaultImports();
        List<StatementWrapper> statementList = script.getBlock().getStatements();
        if (statementList.size() > 0 && statementList.get(0) instanceof ExpressionStatementWrapper) {
            return ((ExpressionStatementWrapper) statementList.get(0)).getExpression();
        }
        return null;
    }

    private void print(String string) {
        if (needLineBreak) {
            printLineBreak();
        }
        if (readyToIndent) {
            stringBuilder.append(currentIndent);
            readyToIndent = false;
            stringBuilder.trimToSize();
        }
        stringBuilder.append(string);
    }

    private void printLineBreak() {
        needLineBreak = false;
        if (!isEndWithNewLine()) {
            print("\n");
        }
        readyToIndent = true;
    }

    private void printBlankSpace() {
        if (!stringBuilder.toString().endsWith(" ") && !needLineBreak && !isEndWithNewLine()) {
            print(" ");
        }
    }

    private boolean isEndWithNewLine() {
        return stringBuilder.toString().endsWith("\n");
    }

    private void printDoubleLineBreak() {
        needLineBreak = false;
        String output = stringBuilder.toString();
        if (output.endsWith("\n\n")) {
            // do nothing
        } else if (output.endsWith("\n")) {
            print("\n");
        } else {
            print("\n\n");
        }
        readyToIndent = true;
    }

    private void checkWrapLongLine() {
        if (isLineWrappingEnabled() && getLastLineLength() > getMaximumLineWidth()) {
            needLineBreak = true;
        }
    }

    private int getLastLineLength() {
        String output = stringBuilder.toString();
        int lastLineBreakIndex = output.lastIndexOf("\n");
        if (lastLineBreakIndex == -1) {
            return 0;
        }
        String lastLine = output.substring(lastLineBreakIndex, output.length());
        return lastLine.length();
    }

    public void parseGroovyAstIntoScript(ScriptNodeWrapper script) {
        for (ImportNodeWrapper importNodeWrapper : script.getImports()) {
            parseImport(importNodeWrapper);
        }
        printDoubleLineBreak();
        parseASTHasBlock(script);
        printDoubleLineBreak();
        for (MethodNodeWrapper methodNodeWrapper : script.getMethods()) {
            if (methodNodeWrapper.getName() != "run") {
                parseMethod(methodNodeWrapper);
            }
        }
    }
}
