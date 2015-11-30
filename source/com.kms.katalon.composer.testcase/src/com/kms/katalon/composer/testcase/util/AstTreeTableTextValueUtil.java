package com.kms.katalon.composer.testcase.util;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.core.groovy.GroovyParser;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class AstTreeTableTextValueUtil {
	public static String getTextValue(Object object) {
		if (object instanceof Statement) {
			return getTextValue((Statement) object);
		} else if (object instanceof Expression) {
			return getTextValue((Expression) object);
		} else if (object instanceof Token) {
			return ((Token) object).getText();
		} else if (object instanceof Parameter) {
			Parameter parameter = (Parameter) object;
			if (parameter != ForStatement.FOR_LOOP_DUMMY) {
				return parameter.getName();
			}
		} else if (object instanceof ClassNode) {
            return ((ClassNode) object).getName();
		}
		return StringUtils.EMPTY;
	}

	public static String getTextValue(Statement statement) {
		if (statement instanceof ExpressionStatement) {
			return getTextValue((ExpressionStatement) statement);
		} else if (statement instanceof IfStatement) {
			return getTextValue((IfStatement) statement);
		} else if (statement instanceof AssertStatement) {
			return getTextValue((AssertStatement) statement);
		} else if (statement instanceof ForStatement) {
			return getTextValue((ForStatement) statement);
		} else if (statement instanceof WhileStatement) {
			return getTextValue((WhileStatement) statement);
		} else if (statement instanceof CatchStatement) {
			return getTextValue((CatchStatement) statement);
		} else if (statement instanceof SwitchStatement) {
			return getTextValue((SwitchStatement) statement);
		} else if (statement instanceof CaseStatement) {
			return getTextValue((CaseStatement) statement);
		} else if (statement instanceof BreakStatement) {
			return getTextValue((BreakStatement) statement);
		}
		return statement.getText();
	}

	private static String getTextValue(IfStatement ifStatement) {
		return "If " + "(" + getTextValue(ifStatement.getBooleanExpression()) + ")";
	}

	private static String getTextValue(ExpressionStatement expressionStatement) {
		return getTextValue(expressionStatement.getExpression());
	}

	private static String getTextValue(AssertStatement assertStatement) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("assert ");
		stringBuilder.append(getTextValue(assertStatement.getBooleanExpression()));
		if (assertStatement.getMessageExpression() instanceof ConstantExpression
				&& !(((ConstantExpression) assertStatement.getMessageExpression()).getValue() == null)) {
			stringBuilder.append(" : ");
			stringBuilder.append(getTextValue(assertStatement.getMessageExpression()));
		}
		return stringBuilder.toString();
	}

	public static String getInputTextValue(ForStatement forStatement) {
		String value = "";
		if (!(forStatement.getCollectionExpression() instanceof ClosureListExpression)) {
			if (forStatement.getVariable() != ForStatement.FOR_LOOP_DUMMY) {
				StringBuilder stringBuilder = new StringBuilder();
				GroovyParser astUtil = new GroovyParser(stringBuilder);
				astUtil.parse(new Parameter[] { forStatement.getVariable() });
				value += astUtil.getValue();
				value += " : ";
			}
		}
		return value + getTextValue(forStatement.getCollectionExpression());
	}

	private static String getTextValue(ForStatement forStatement) {
		return "For (" + getInputTextValue(forStatement) + ")";
	}

	private static String getTextValue(WhileStatement whileStatement) {
		return "While " + "(" + getTextValue(whileStatement.getBooleanExpression()) + ")";
	}

	private static String getTextValue(CatchStatement catchStatement) {
		StringBuilder stringBuilder = new StringBuilder();
		new GroovyParser(stringBuilder).parse(new Parameter[] { catchStatement.getVariable() });
		return ("catch (" + stringBuilder.toString() + ")");
	}

	private static String getTextValue(SwitchStatement switchStatement) {
		return getTextValue(switchStatement.getExpression());
	}

	private static String getTextValue(CaseStatement caseStatement) {
		return getTextValue(caseStatement.getExpression());
	}

	private static String getTextValue(BreakStatement breakStatement) {
		return "break";
	}

	public static String getTextValue(Expression expression) {
		if (expression instanceof BinaryExpression) {
			return getTextValue((BinaryExpression) expression);
		} else if (expression instanceof ConstantExpression) {
			return getTextValue((ConstantExpression) expression);
		} else if (expression instanceof MethodCallExpression) {
			return getTextValue((MethodCallExpression) expression);
		} else if (expression instanceof TupleExpression) {
			return getTextValue((TupleExpression) expression);
		} else if (expression instanceof PropertyExpression) {
			return getTextValue((PropertyExpression) expression);
		} else if (expression instanceof CastExpression) {
			return getTextValue((CastExpression) expression);
		} else if (expression instanceof ListExpression) {
			return getTextValue((ListExpression) expression);
		} else if (expression instanceof MapExpression) {
			return getTextValue((MapExpression) expression);
		} else if (expression instanceof BooleanExpression) {
			return getTextValue((BooleanExpression) expression);
		} else if (expression instanceof ArgumentListExpression) {
			return getTextValue((ArgumentListExpression) expression);
		} else if (expression != null) {
			return expression.getText();
		} else {
			return "null";
		}
	}

	private static String getTextValue(ArgumentListExpression argumentListExpression) {
		StringBuilder value = new StringBuilder();
		value.append("(");
		int count = argumentListExpression.getExpressions().size();
		for (Expression expression : argumentListExpression.getExpressions()) {
			value.append(getTextValue(expression));
			count--;
			if (count > 0) {
				value.append(", ");
			}
		}
		value.append(")");
		return value.toString();
	}

	private static String getTextValue(CastExpression castExpression) {
		return getTextValue(castExpression.getExpression());
	}

	private static String getTextValue(BooleanExpression booleanExpression) {
		if (booleanExpression instanceof NotExpression) {
			return "!(" + getTextValue(booleanExpression.getExpression()) + ")";
		}
		return getTextValue(booleanExpression.getExpression());
	}

	private static String getTextValue(ConstantExpression constantExpression) {
		if (constantExpression.getValue() instanceof String) {
			return "\"" + constantExpression.getText() + "\"";
		} else if (constantExpression.getValue() instanceof Character) {
			return "'" + constantExpression.getText() + "'";
		}
		return constantExpression.getText();
	}

	private static String getTextValue(BinaryExpression binaryExpression) {
		if (binaryExpression.getOperation().getType() == Types.LEFT_SQUARE_BRACKET) {
			return getTextValue(binaryExpression.getLeftExpression()) + "["
					+ getTextValue(binaryExpression.getRightExpression()) + "]";
		}
		return getTextValue(binaryExpression.getLeftExpression()) + " " + binaryExpression.getOperation().getText()
				+ " " + getTextValue(binaryExpression.getRightExpression());
	}

	private static String getTextValue(MethodCallExpression methodCallExpression) {
		if (AstTreeTableInputUtil.isCallTestCaseArgument(methodCallExpression)
				&& methodCallExpression.getArguments() instanceof ArgumentListExpression) {
			return getTextValueForTestCaseArgument(methodCallExpression);
		} else if (AstTreeTableInputUtil.isCallTestCaseMethod(methodCallExpression)
				&& methodCallExpression.getArguments() instanceof ArgumentListExpression) {
			ArgumentListExpression argumentListExpression = (ArgumentListExpression) methodCallExpression
					.getArguments();
			if (!argumentListExpression.getExpressions().isEmpty()) {
				return getTextValue(argumentListExpression.getExpressions().get(0));
			}
		} else if (AstTreeTableInputUtil.isObjectArgument(methodCallExpression)
				&& methodCallExpression.getArguments() instanceof ArgumentListExpression) {
			return getTextValueForTestObjectArgument(methodCallExpression);
		} else if (AstTreeTableInputUtil.isTestDataArgument(methodCallExpression)
				&& methodCallExpression.getArguments() instanceof ArgumentListExpression) {
			return getTextValueForTestDataArgument(methodCallExpression);
		} else if (AstTreeTableInputUtil.isTestDataValueArgument(methodCallExpression)
				&& methodCallExpression.getArguments() instanceof ArgumentListExpression) {
			return getTextValueForTestDataValueArgument(methodCallExpression);
		} else if (AstTreeTableUtil.isCustomKeywordMethodCall(methodCallExpression)) {
			processCustomKeywordMethodCall(methodCallExpression);
		}

		String object = getTextValue(methodCallExpression.getObjectExpression());
		String meth = methodCallExpression.getMethod().getText();
		String args = getTextValue(methodCallExpression.getArguments());
		return object + "." + meth + args;
	}

	private static void processCustomKeywordMethodCall(MethodCallExpression methodCallExpression) {
		try {
			if (methodCallExpression.getMethod() instanceof ConstantExpression) {
				String meth = KeywordController.getInstance().getCustomKeywordName(
						methodCallExpression.getMethod().getText());
				methodCallExpression.setMethod(new ConstantExpression(meth));
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}

	private static String getTextValueForTestObjectArgument(MethodCallExpression methodCallExpression) {
		ArgumentListExpression argumentListExpression = (ArgumentListExpression) methodCallExpression.getArguments();
		if (!argumentListExpression.getExpressions().isEmpty()) {
			String pk = argumentListExpression.getExpressions().get(0).getText();
			WebElementEntity webElement = null;
			try {
				webElement = ObjectRepositoryController.getInstance().getWebElementByDisplayPk(pk);
			} catch (Exception e) {
				LoggerSingleton.logError(e);
			}
			if (webElement != null) {
				return webElement.getName();
			}
		}
		return "null";
	}

	private static String getTextValueForTestDataArgument(MethodCallExpression methodCallExpression) {
		ArgumentListExpression argumentListExpression = (ArgumentListExpression) methodCallExpression.getArguments();
		if (!argumentListExpression.getExpressions().isEmpty()) {
			String pk = argumentListExpression.getExpressions().get(0).getText();
			DataFileEntity dataFile = null;
			try {
				dataFile = TestDataController.getInstance().getTestDataByDisplayId(pk);
			} catch (Exception e) {
				LoggerSingleton.logError(e);
			}
			if (dataFile != null) {
				return dataFile.getName();
			}
			return pk;
		}
		return "null";
	}

	private static String getTextValueForTestDataValueArgument(MethodCallExpression methodCallExpression) {
		StringBuilder result = new StringBuilder();
		if (methodCallExpression.getObjectExpression() instanceof MethodCallExpression) {
			result.append(getTextValueForTestDataArgument((MethodCallExpression) methodCallExpression
					.getObjectExpression()));
		}
		result.append(getTextValue(methodCallExpression.getArguments()));
		return result.toString();
	}

	private static String getTextValueForTestCaseArgument(MethodCallExpression methodCallExpression) {
		ArgumentListExpression argumentListExpression = (ArgumentListExpression) methodCallExpression.getArguments();
		if (!argumentListExpression.getExpressions().isEmpty()) {
			String pk = argumentListExpression.getExpressions().get(0).getText();
			TestCaseEntity testCase = null;
			try {
				testCase = TestCaseController.getInstance().getTestCaseByDisplayId(pk);
			} catch (Exception e) {
				LoggerSingleton.logError(e);
			}
			if (testCase != null) {
				return testCase.getName();
			}
			return pk;
		}
		return StringUtils.EMPTY;
	}

	private static String getTextValue(TupleExpression tupleExpression) {
		StringBuilder buffer = new StringBuilder("(");
		boolean first = true;
		for (Expression expression : tupleExpression.getExpressions()) {
			if (first) {
				first = false;
			} else {
				buffer.append(", ");
			}

			buffer.append(getTextValue(expression));
		}
		buffer.append(")");
		return buffer.toString();
	}

	private static String getTextValue(PropertyExpression propertyExpression) {
		return propertyExpression.getProperty().getText();
	}

	private static String getTextValue(ListExpression listExpression) {
		StringBuilder buffer = new StringBuilder("[");
		boolean first = true;
		for (Expression expression : listExpression.getExpressions()) {
			if (first) {
				first = false;
			} else {
				buffer.append(", ");
			}

			buffer.append(getTextValue(expression));
		}
		buffer.append("]");
		return buffer.toString();
	}

	private static String getTextValue(MapExpression mapExpression) {
		StringBuilder sb = new StringBuilder(32);
		sb.append("[");
		int size = mapExpression.getMapEntryExpressions().size();
		MapEntryExpression mapEntryExpression = null;
		if (size > 0) {
			mapEntryExpression = mapExpression.getMapEntryExpressions().get(0);
			sb.append(getTextValue(mapEntryExpression.getKeyExpression()) + ":"
					+ getTextValue(mapEntryExpression.getValueExpression()));
			for (int i = 1; i < size; i++) {
				mapEntryExpression = mapExpression.getMapEntryExpressions().get(i);
				sb.append(", " + getTextValue(mapEntryExpression.getKeyExpression()) + ":"
						+ getTextValue(mapEntryExpression.getValueExpression()));
				if (sb.length() > 120 && i < size - 1) {
					sb.append(", ... ");
					break;
				}
			}
		} else {
			sb.append(":");
		}
		sb.append("]");
		return sb.toString();
	}

	public static String getTextValue(FieldNode fieldNode) {
		if (fieldNode.getInitialExpression() != null) {
			return fieldNode.getName() + " = " + getTextValue(fieldNode.getInitialExpression());
		}
		return fieldNode.getName();
	}
}
