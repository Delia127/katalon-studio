package com.kms.katalon.composer.testcase.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstTreeTableEntityUtil;
import com.kms.katalon.core.groovy.GroovyParser;

public class ForInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
	private final InputValueType[] inputValueTypes = { InputValueType.Range, InputValueType.ClosureList,
			InputValueType.Constant, InputValueType.Variable, InputValueType.GlobalVariable, InputValueType.TestDataValue, InputValueType.Property };

	private static final String DIALOG_TITLE = StringConstants.DIA_TITLE_FOR_INPUT;
	private static final String[] COLUMN_NAMES = new String[] {
			StringConstants.DIA_COL_OBJ,
			StringConstants.DIA_COL_VALUE_TYPE,
			StringConstants.DIA_COL_VALUE };

	private Parameter variable;
	private Expression collectionExpression;
	private ForStatement originalForStatement;

	public ForInputBuilderDialog(Shell parentShell, ForStatement forStatement, ClassNode scriptClass) {
		super(parentShell, scriptClass);
		originalForStatement = forStatement;
		if (forStatement.getCollectionExpression() != null) {
			this.collectionExpression = GroovyParser.cloneExpression(forStatement.getCollectionExpression());
		} else {
			this.collectionExpression = AstTreeTableEntityUtil.getNewRangeExpression();
		}
		this.variable = forStatement.getVariable();
	}

	@Override
	public void refresh() {
		List<ASTNode> expressionList = new ArrayList<ASTNode>();
		expressionList.add(variable);
		expressionList.add(collectionExpression);
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(expressionList);
		tableViewer.refresh();
	}

	@Override
	public ForStatement getReturnValue() {
		return new ForStatement(variable, collectionExpression, originalForStatement.getLoopBlock());
	}

	@Override
	public void changeObject(Object originalObject, Object newObject) {
		if (originalObject == collectionExpression && newObject instanceof Expression) {
			collectionExpression = (Expression) newObject;
			if (collectionExpression instanceof ClosureListExpression) {
				variable = ForStatement.FOR_LOOP_DUMMY;
			} else if (variable == ForStatement.FOR_LOOP_DUMMY) {
				variable = new Parameter(new ClassNode(Object.class), "index");
			}
			refresh();
		} else if (originalObject == variable && newObject instanceof Parameter) {
			variable = (Parameter) newObject;
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
				if (element == collectionExpression) {
					return "Expression";
				} else if (element == variable) {
					return "Variable";
				}
				return StringUtils.EMPTY;
			}
		});

		TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumnValueType.getColumn().setWidth(100);
		tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider(scriptClass));

		tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
				inputValueTypes, this, scriptClass));

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
