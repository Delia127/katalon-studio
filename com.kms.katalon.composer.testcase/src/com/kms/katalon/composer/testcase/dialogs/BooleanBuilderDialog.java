package com.kms.katalon.composer.testcase.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstTreeTableEntityUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableValueUtil;
import com.kms.katalon.core.groovy.GroovyParser;

public class BooleanBuilderDialog extends AbstractAstBuilderWithTableDialog {
	private static final String REVERSE_BUTTON_LABEL = StringConstants.DIA_BTN_REVERSE;

	private final InputValueType[] inputValueTypes = { InputValueType.Constant, InputValueType.Variable,
			InputValueType.GlobalVariable, InputValueType.TestDataValue, InputValueType.MethodCall,
			InputValueType.Binary, InputValueType.Property };

	private static final String DIALOG_TITLE = StringConstants.DIA_TITLE_CONDITION_INPUT;
	private static final String[] COLUMN_NAMES = new String[] { StringConstants.DIA_COL_VALUE_TYPE,
			StringConstants.DIA_COL_VALUE };

	private BooleanExpression booleanExpression;
	private Button btnReverse;

	public BooleanBuilderDialog(Shell parentShell, BooleanExpression booleanExpression, ClassNode scriptClass) {
		super(parentShell, scriptClass);
		if (booleanExpression != null) {
			this.booleanExpression = GroovyParser.cloneBooleanExpression(booleanExpression);
		} else {
			this.booleanExpression = AstTreeTableEntityUtil.getNewBooleanExpression();
		}
	}

	@Override
	protected TableViewer createTable(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		btnReverse = new Button(parent, SWT.CHECK);
		btnReverse.setText(REVERSE_BUTTON_LABEL);
		if (booleanExpression instanceof NotExpression) {
			btnReverse.setSelection(true);
		}

		btnReverse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnReverse.getSelection()) {
					booleanExpression = new NotExpression(booleanExpression.getExpression());
				} else {
					booleanExpression = new BooleanExpression(booleanExpression.getExpression());
				}
				refresh();
			}
		});

		TableViewer tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		return tableViewer;
	}

	@Override
	public void refresh() {
		List<Expression> expressionList = new ArrayList<Expression>();
		expressionList.add(booleanExpression);
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(expressionList);
	}

	@Override
	public BooleanExpression getReturnValue() {
		return booleanExpression;
	}

	@Override
	public void changeObject(Object orginalObject, Object newObject) {
		if (orginalObject == booleanExpression.getExpression() && newObject instanceof Expression) {
			if (booleanExpression instanceof NotExpression) {
				booleanExpression = new NotExpression((Expression) newObject);
			} else {
				booleanExpression = new BooleanExpression((Expression) newObject);
			}
			refresh();
		}
	}

	@Override
	public String getDialogTitle() {
		return DIALOG_TITLE;
	}

	@Override
	protected void addTableColumns() {
		TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumnValueType.getColumn().setWidth(100);
		tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider(scriptClass) {
			@Override
			public String getText(Object element) {
				if (element == booleanExpression) {
					return super.getText(booleanExpression.getExpression());
				}
				return "";
			}
		});
		tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
				inputValueTypes, this, scriptClass) {
			@Override
			protected void setValue(Object element, Object value) {
				if (element == booleanExpression && value instanceof Integer && (int) value > -1
						&& (int) value < inputValueTypes.length) {
					InputValueType newValueType = inputValueTypes[(int) value];
					InputValueType oldValueType = AstTreeTableValueUtil.getTypeValue(booleanExpression.getExpression(),
							scriptClass);
					if (newValueType != oldValueType) {
						ASTNode newValue = null;
						if (newValueType == InputValueType.Constant) {
							newValue = AstTreeTableEntityUtil.getNewBooleanConstantExpression();
						} else {
							newValue = AstTreeTableInputUtil.generateNewExpression(booleanExpression.getExpression(),
									newValueType);
						}
						parentDialog.changeObject(booleanExpression.getExpression(), newValue);
						getViewer().refresh();
					}
				}
			}

			@Override
			protected Object getValue(Object element) {
				if (element == booleanExpression) {
					return super.getValue(booleanExpression.getExpression());
				}
				return 0;
			}
		});

		TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumnValue.getColumn().setWidth(300);
		tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element == booleanExpression) {
					return super.getText(booleanExpression.getExpression());
				}
				return "";
			}
		});
		tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this, scriptClass) {
			@Override
			protected void setValue(Object element, Object value) {
				if (element == booleanExpression) {
					super.setValue(booleanExpression.getExpression(), value);
				}
			}

			@Override
			protected Object getValue(Object element) {
				if (element == booleanExpression) {
					return super.getValue(booleanExpression.getExpression());
				}
				return null;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (element == booleanExpression) {
					return super.getCellEditor(booleanExpression.getExpression());
				}
				return null;
			}
		});

		// set column's name
		for (int i = 0; i < tableViewer.getTable().getColumnCount(); i++) {
			tableViewer.getTable().getColumn(i).setText(COLUMN_NAMES[i]);
		}
	}
}
