package com.kms.katalon.composer.testcase.ast.dialogs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.ast.editors.TypeInputCellEditor;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.util.AstTreeTableEntityUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.composer.testcase.util.AstTreeTableTextValueUtil;
import com.kms.katalon.core.groovy.GroovyParser;

public class PropertyInputBuilderDialog extends AbstractAstBuilderWithTableDialog {
	private static final String DIALOG_TITLE = StringConstants.DIA_TITLE_PROPERTY_INPUT;

	private static enum PropertyObjectType {
		Class, Variable
	}

	private PropertyExpression propertyExpression;
	private Class<?> type;
	private String field;
	private List<String> fieldNames;

	public PropertyInputBuilderDialog(Shell parentShell, PropertyExpression propertyExpression, ClassNode scriptClass)
			throws Exception {
		super(parentShell, scriptClass);
		field = null;
		type = null;
		fieldNames = new ArrayList<String>();
		if (propertyExpression != null) {
			this.propertyExpression = GroovyParser.clonePropertyExpression(propertyExpression);
			if ((propertyExpression.getObjectExpression() instanceof VariableExpression)
					|| propertyExpression.getObjectExpression() instanceof PropertyExpression) {
				type = AstTreeTableInputUtil.loadType(propertyExpression.getObjectExpression().getText(), scriptClass);
			} else if (propertyExpression.getObjectExpression() instanceof ClassExpression) {
				type = propertyExpression.getObjectExpression().getType().getTypeClass();
			}
			if (type != null) {
				String methodCallSignature = propertyExpression.getPropertyAsString();
				for (Field field : type.getFields()) {
					if (field.getName().equals(methodCallSignature)) {
						this.field = field.getName();
						break;
					}
				}
			}
		} else {
			this.propertyExpression = AstTreeTableEntityUtil.getNewPropertyExpression();
		}
	}

	private PropertyObjectType getObjectTypeFromExpression(PropertyExpression propertyExpression) {
		if (propertyExpression.getObjectExpression() instanceof PropertyExpression) {
			return PropertyObjectType.Class;
		} else if (propertyExpression.getObjectExpression() instanceof VariableExpression) {
			if (type != null) {
				return PropertyObjectType.Class;
			} else {
				return PropertyObjectType.Variable;
			}
		}
		return PropertyObjectType.Class;
	}

	@Override
	public void refresh() {
		tableViewer.setContentProvider(new ArrayContentProvider());
		propertyExpression = new PropertyExpression(propertyExpression.getObjectExpression(),
				field == null ? propertyExpression.getPropertyAsString() : field);
		List<Expression> expressionList = new ArrayList<Expression>();
		expressionList.add(propertyExpression);
		tableViewer.setInput(expressionList);
		tableViewer.refresh();
	}

	@Override
	public PropertyExpression getReturnValue() {
		return propertyExpression;
	}

	@Override
	public void changeObject(Object originalObject, Object newObject) {
		// Do nothing
	}

	@Override
	public String getDialogTitle() {
		return DIALOG_TITLE;
	}

	@Override
	protected void addTableColumns() {
		TableViewerColumn tableViewerColumnType = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumnType = tableViewerColumnType.getColumn();
		tblclmnNewColumnType.setWidth(152);
		tblclmnNewColumnType.setText(StringConstants.DIA_COL_OBJ_TYPE);
		tableViewerColumnType.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element == propertyExpression) {
					return getObjectTypeFromExpression(propertyExpression).toString();
				}
				return StringUtils.EMPTY;
			}
		});

		tableViewerColumnType.setEditingSupport(new EditingSupport(tableViewer) {
			@Override
			protected void setValue(Object element, Object value) {
				if (element == propertyExpression && value instanceof Integer) {
					String[] objectTypes = getPropertyObjectTypeStringList();
					int index = (int) value;
					if (index >= 0 && index < objectTypes.length) {
						PropertyObjectType newObjectType = PropertyObjectType.valueOf(objectTypes[index]);
						if (newObjectType == PropertyObjectType.Class
								&& !(propertyExpression.getObjectExpression() instanceof PropertyExpression)) {
							propertyExpression.setObjectExpression(AstTreeTableEntityUtil.getNewPropertyExpression());
						} else if (newObjectType == PropertyObjectType.Variable
								&& !(propertyExpression.getObjectExpression() instanceof VariableExpression)) {
							propertyExpression.setObjectExpression(AstTreeTableEntityUtil.getNewVariableExpression());
						}
						type = null;
						field = null;
						refresh();
					}
				}
			}

			@Override
			protected Object getValue(Object element) {
				if (element == propertyExpression) {
					return getObjectTypeFromExpression(propertyExpression).ordinal();
				}
				return 0;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return new ComboBoxCellEditor(tableViewer.getTable(), getPropertyObjectTypeStringList());
			}

			protected String[] getPropertyObjectTypeStringList() {
				List<String> values = new ArrayList<String>();
				for (PropertyObjectType objectType : PropertyObjectType.values()) {
					values.add(objectType.toString());
				}
				return values.toArray(new String[values.size()]);
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

		TableViewerColumn tableViewerColumnObject = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumnObject = tableViewerColumnObject.getColumn();
		tblclmnNewColumnObject.setWidth(152);
		tblclmnNewColumnObject.setText(StringConstants.DIA_COL_OBJ);
		tableViewerColumnObject.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element == propertyExpression && propertyExpression.getObjectExpression() != null) {
					return AstTreeTableTextValueUtil.getTextValue(propertyExpression.getObjectExpression());
				}
				return StringUtils.EMPTY;
			}
		});

		tableViewerColumnObject.setEditingSupport(new EditingSupport(tableViewer) {

			@Override
			protected void setValue(Object element, Object value) {
				if (element == propertyExpression) {
					if (value instanceof IType) {
						try {
							Class<?> newType = AstTreeTableInputUtil.loadType(((IType) value).getFullyQualifiedName(),
									scriptClass);
							if (type != newType) {
								type = newType;
								propertyExpression.setObjectExpression(createNewPropertyExpressionFromTypeName(type
										.getName()));
								refresh();
							}
						} catch (Exception e) {
							LoggerSingleton.logError(e);
						}
					} else if (value instanceof String
							&& !propertyExpression.getObjectExpression().getText().equals(value)) {
						propertyExpression.setObjectExpression(new VariableExpression((String) value));
						refresh();
					}
				}
			}

			private Expression createNewPropertyExpressionFromTypeName(String typeName) {
				int index = typeName.lastIndexOf('.');
				if (index != -1 && index < typeName.length()) {
					return new PropertyExpression(
							createNewPropertyExpressionFromTypeName(typeName.substring(0, index)), typeName.substring(
									index + 1, typeName.length()));
				} else {
					return new VariableExpression(typeName);
				}
			}

			@Override
			protected Object getValue(Object element) {
				if (element == propertyExpression && propertyExpression.getObjectExpression() != null) {
					return propertyExpression.getObjectExpression().getText();
				}
				return StringUtils.EMPTY;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (element == propertyExpression) {
					PropertyObjectType type = getObjectTypeFromExpression(propertyExpression);
					if (type == PropertyObjectType.Class) {
						return new TypeInputCellEditor(tableViewer.getTable(), AstTreeTableTextValueUtil
								.getTextValue(propertyExpression.getObjectExpression()));
					} else if (type == PropertyObjectType.Variable) {
						return new TextCellEditor(tableViewer.getTable());
					}
				}
				return null;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

		TableViewerColumn tableViewerColumnProperty = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumnProperty = tableViewerColumnProperty.getColumn();
		tblclmnNewColumnProperty.setText(StringConstants.DIA_COL_PROPERTY);
		tblclmnNewColumnProperty.setWidth(152);
		tableViewerColumnProperty.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element == propertyExpression && field != null) {
					return field.toString();
				} else if (type == null) {
					return propertyExpression.getPropertyAsString();
				}
				return StringUtils.EMPTY;
			}
		});

		tableViewerColumnProperty.setEditingSupport(new EditingSupport(tableViewer) {

			@Override
			protected void setValue(Object element, Object value) {
				if (element == propertyExpression) {
					if (value instanceof Integer && type != null) {
						try {
							int index = (int) value;
							if (index >= 0 && index < fieldNames.size()) {
								String selectedFieldName = fieldNames.get(index);
								if (!selectedFieldName.equals(field)) {
									field = selectedFieldName;
									refresh();
								}
							}
						} catch (Exception e) {
							LoggerSingleton.logError(e);
						}
					} else if (value instanceof String && type == null) {
						field = (String) value;
						refresh();
					}
				}
			}

			@Override
			protected Object getValue(Object element) {
				if (type != null) {
					if (field != null) {
						for (int i = 0; i < fieldNames.size(); i++) {
							if (field.equals(fieldNames.get(i))) {
								return i;
							}
						}
					}
					return 0;
				} else {
					return propertyExpression.getPropertyAsString();
				}
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (type != null) {
					try {
						fieldNames.clear();
						for (Field field : type.getFields()) {
							fieldNames.add(field.getName());
						}
						return new ComboBoxCellEditor(tableViewer.getTable(), fieldNames.toArray(new String[fieldNames
								.size()]));
					} catch (Exception e) {
						LoggerSingleton.logError(e);
					}
				} else {
					return new TextCellEditor(tableViewer.getTable());
				}
				return null;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
	}
}
