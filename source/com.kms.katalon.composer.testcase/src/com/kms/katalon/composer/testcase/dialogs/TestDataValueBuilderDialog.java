package com.kms.katalon.composer.testcase.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputConstantTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderConstantTypeColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstTreeTableEntityUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.core.groovy.GroovyParser;

public class TestDataValueBuilderDialog extends AbstractAstBuilderWithTableDialog {
	private final InputValueType[] inputValueTypes = { InputValueType.Constant, InputValueType.Variable,
			InputValueType.TestData };

	private static final String DIALOG_TITLE = StringConstants.DIA_TITLE_TEST_DATA_VALUE_INPUT;
	private static final String[] COLUMN_NAMES = new String[] { StringConstants.DIA_COL_OBJ,
			StringConstants.DIA_COL_VALUE_TYPE, StringConstants.DIA_COL_CONSTANT_TYPE, StringConstants.DIA_COL_VALUE };

	private MethodCallExpression methodCallExpression;
	private Expression testDataExpression;
	private Expression columnExpression;
	private Expression rowExpression;

	public TestDataValueBuilderDialog(Shell parentShell, MethodCallExpression methodCallExpression,
			ClassNode scriptClass) {
		super(parentShell, scriptClass);
		if (methodCallExpression != null) {
			this.methodCallExpression = GroovyParser.cloneMethodCallExpression(methodCallExpression);
		} else {
			this.methodCallExpression = (MethodCallExpression) AstTreeTableInputUtil.generateNewExpression(null,
					InputValueType.TestDataValue);
		}
		testDataExpression = this.methodCallExpression.getObjectExpression();

		ArgumentListExpression arguments = AstTreeTableInputUtil.getTestDataValueArgument(this.methodCallExpression);
		if (arguments.getExpressions().size() >= 2) {
			columnExpression = arguments.getExpression(0);
			rowExpression = arguments.getExpression(1);
		}
	}

	@Override
	public void refresh() {
		List<Object> expressionList = new ArrayList<Object>();
		expressionList.add(testDataExpression);
		expressionList.add(columnExpression);
		expressionList.add(rowExpression);

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(expressionList);
	}

	@Override
	public MethodCallExpression getReturnValue() {
		return AstTreeTableEntityUtil
				.getNewTestDataValueExpression(testDataExpression, columnExpression, rowExpression);
	}

	@Override
	public void changeObject(Object orginalObject, Object newObject) {
		if (orginalObject == testDataExpression && newObject instanceof Expression) {
			testDataExpression = (Expression) newObject;
			refresh();
		} else if (orginalObject == columnExpression) {
			columnExpression = (Expression) newObject;
			refresh();
		} else if (orginalObject == rowExpression) {
			rowExpression = (Expression) newObject;
			refresh();
		}
	}

	@Override
	public String getDialogTitle() {
		return DIALOG_TITLE;
	}

	@Override
	protected void addTableColumns() {
		TableViewerColumn tableViewerColumnObject = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumnObject.getColumn().setWidth(100);
		tableViewerColumnObject.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element == testDataExpression) {
					return "Test Data";
				} else if (element == columnExpression) {
					return "Column";
				} else if (element == rowExpression) {
					return "Row";
				}
				return StringUtils.EMPTY;
			}
		});

		TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumnValueType.getColumn().setWidth(100);
		tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider(scriptClass));
		tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
				inputValueTypes, this, scriptClass));

		TableViewerColumn tableViewerColumnConstantType = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumnConstantType.getColumn().setWidth(100);
		tableViewerColumnConstantType.setLabelProvider(new AstInputConstantTypeLabelProvider());
		tableViewerColumnConstantType
				.setEditingSupport(new AstInputBuilderConstantTypeColumnSupport(tableViewer, this));

		TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumnValue.getColumn().setWidth(300);
		tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider());

		tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this, scriptClass));

		// set column's name
		for (int i = 0; i < tableViewer.getTable().getColumnCount(); i++) {
			tableViewer.getTable().getColumn(i).setText(COLUMN_NAMES[i]);
		}
	}
}
