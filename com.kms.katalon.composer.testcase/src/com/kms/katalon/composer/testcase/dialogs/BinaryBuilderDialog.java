package com.kms.katalon.composer.testcase.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.syntax.Token;
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
import com.kms.katalon.core.groovy.GroovyParser;

public class BinaryBuilderDialog extends AbstractAstBuilderWithTableDialog {
	private final InputValueType[] inputValueTypes = { InputValueType.Constant, InputValueType.Variable,
			InputValueType.MethodCall, InputValueType.Binary, InputValueType.GlobalVariable,
			InputValueType.TestDataValue, InputValueType.Property };

	private static final String DIALOG_TITLE = StringConstants.DIA_TITLE_BINARY_INPUT;
	private static final String[] COLUMN_NAMES = new String[] { StringConstants.DIA_COL_OBJ,
			StringConstants.DIA_COL_VALUE_TYPE, StringConstants.DIA_COL_CONSTANT_TYPE, StringConstants.DIA_COL_VALUE };

	private BinaryExpression binaryExpression;
	private Expression leftExpression;
	private Expression rightExpression;
	private Token token;

	public BinaryBuilderDialog(Shell parentShell, BinaryExpression binaryExpression, ClassNode scriptClass) {
		super(parentShell, scriptClass);
		if (binaryExpression != null) {
			this.binaryExpression = GroovyParser.cloneBinaryExpression(binaryExpression);
		} else {
			this.binaryExpression = AstTreeTableEntityUtil.getNewBinaryExpression();
		}
		leftExpression = binaryExpression.getLeftExpression();
		rightExpression = binaryExpression.getRightExpression();
		token = binaryExpression.getOperation();
	}

	@Override
	public void refresh() {
		List<Object> expressionList = new ArrayList<Object>();
		expressionList.add(leftExpression);
		expressionList.add(token);
		expressionList.add(rightExpression);
		binaryExpression = new BinaryExpression(leftExpression, token, rightExpression);

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(expressionList);
	}

	@Override
	public BinaryExpression getReturnValue() {
		return binaryExpression;
	}

	@Override
	public void changeObject(Object orginalObject, Object newObject) {
		if (orginalObject == leftExpression && newObject instanceof Expression) {
			leftExpression = (Expression) newObject;
			refresh();
		} else if (orginalObject == rightExpression && newObject instanceof Expression) {
			rightExpression = (Expression) newObject;
			refresh();
		} else if (orginalObject == token && newObject instanceof Token) {
			token = (Token) newObject;
			refresh();
		}
	}

	@Override
	protected void addTableColumns() {
		TableViewerColumn tableViewerColumnObject = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumnObject.getColumn().setWidth(100);
		tableViewerColumnObject.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element == leftExpression) {
					return "Left Expression";
				} else if (element == rightExpression) {
					return "Right Expression";
				} else if (element == token) {
					return "Operator";
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

	@Override
	public String getDialogTitle() {
		return DIALOG_TITLE;
	}
}
