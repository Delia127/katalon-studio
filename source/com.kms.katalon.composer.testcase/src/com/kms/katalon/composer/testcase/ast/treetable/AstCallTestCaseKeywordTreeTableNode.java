package com.kms.katalon.composer.testcase.ast.treetable;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.editors.CallTestCaseCellEditor;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.ast.AstTextValueUtil;
import com.kms.katalon.core.model.FailureHandling;
import com.kms.katalon.core.testcase.TestCase;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;

public class AstCallTestCaseKeywordTreeTableNode extends AstBuiltInKeywordTreeTableNode {
	private String testCasePk;

	public AstCallTestCaseKeywordTreeTableNode(ExpressionStatement methodCallStatement, AstTreeTableNode parentNode,
			ASTNode parentObject, ClassNode scriptClass) {
		super(methodCallStatement, parentNode, parentObject, scriptClass);
		internallySetTestCasePk();
	}

	@Override
	public boolean isItemEditable() {
		return false;
	}

	private void internallySetTestCasePk() {
		try {
			ArgumentListExpression arguments = (ArgumentListExpression) methodCall.getArguments();
			Expression objectExpression = AstTreeTableInputUtil.getCallTestCaseParam((MethodCallExpression) arguments
					.getExpression(0));
			if (objectExpression != null) {
				TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByDisplayId(
						objectExpression.getText());
				testCasePk = TestCaseController.getInstance().getIdForDisplay(testCase);
				return;
			}
		} catch (Exception e) {
			// Do nothing
		}
		testCasePk = StringUtils.EMPTY;
	}

	protected void changeMapExpression(MapExpression mapExprs) {
		ArgumentListExpression arguments = (ArgumentListExpression) methodCall.getArguments();
		arguments.getExpressions().remove(1);
		arguments.getExpressions().add(1, mapExprs);
	}

	protected void changeTestCasePk(TestCaseEntity testCase) {
		try {
			MethodCallExpression testCaseMethodCallEprs = AstTreeTableInputUtil
					.generateTestCaseMethodCall(TestCaseController.getInstance().getIdForDisplay(testCase));
			ArgumentListExpression arguments = (ArgumentListExpression) methodCall.getArguments();
			arguments.getExpressions().remove(0);
			arguments.getExpressions().add(0, testCaseMethodCallEprs);

			internallySetTestCasePk();
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}

	@Override
	public String getInputText() {
		ArgumentListExpression arguments = (ArgumentListExpression) methodCall.getArguments();
		if (arguments.getExpressions().size() > 0) {
			try {
				StringBuilder displayString = new StringBuilder();
				Method keywordMethod = KeywordController.getInstance().getBuiltInKeywordByName(
						getBuiltInKWClassSimpleName(), getKeyword());
				if (keywordMethod != null) {
					int count = 0;
					List<Class<?>> paramClasses = AstTreeTableInputUtil.getParamClasses(keywordMethod);
					for (int i = 0; i < paramClasses.size(); i++) {
						if (!TestCase.class.isAssignableFrom(paramClasses.get(i))
								&& paramClasses.get(i) != FailureHandling.class) {
							if (i < arguments.getExpressions().size()) {
								if (count > 0) {
									displayString.append("; ");
								}
								Expression inputExpression = arguments.getExpression(i);
								displayString.append(AstTextValueUtil.getTextValue(inputExpression));
								count++;
							}
						}
					}
				}
				return displayString.toString();
			} catch (Exception e) {
				LoggerSingleton.logError(e);
			}

		}
		return "";
	}

	@Override
	public boolean isInputEditable() {
		return true;
	}

	public List<VariableEntity> getCallTestCaseVariables() {
		try {
			return TestCaseEntityUtil.getCallTestCaseVariables((ArgumentListExpression) methodCall.getArguments());
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
		return Collections.emptyList();
	}

	@Override
	public boolean isOutputEditatble() {
		return false;
	}

	@Override
	public boolean isTestObjectEditable() {
		return true;
	}

	@Override
	public String getTestObjectText() {
		try {
			TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByDisplayId(testCasePk);
			if (testCase != null) {
				return testCase.getName();
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
		return "";
	}

	@Override
	public CellEditor getCellEditorForTestObject(Composite parent) {
		return new CallTestCaseCellEditor(parent, getTestObjectText(), testCasePk);
	}

	@Override
	public boolean setTestObject(Object object) {
		try {
			if (object instanceof TestCaseTreeEntity
					&& ((TestCaseTreeEntity) object).getObject() instanceof TestCaseEntity) {
				TestCaseEntity newTestCase = (TestCaseEntity) ((TestCaseTreeEntity) object).getObject();
				if (!testCasePk.equals(TestCaseController.getInstance().getIdForDisplay(newTestCase))) {
					changeTestCasePk(newTestCase);
					changeMapExpression(AstTreeTableInputUtil.generateTestCaseVariableBindingExpression(newTestCase));
					return true;
				}
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
		return false;
	}

	@Override
	public Image getNodeIcon() {
		return ImageConstants.IMG_16_CALL_TEST_CASE;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}
}
