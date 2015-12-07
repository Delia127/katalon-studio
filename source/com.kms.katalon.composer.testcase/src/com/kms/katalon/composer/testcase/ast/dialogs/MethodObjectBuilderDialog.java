package com.kms.katalon.composer.testcase.ast.dialogs;

import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.internal.ui.JavaUIMessages;
import org.eclipse.jdt.internal.ui.dialogs.OpenTypeSelectionDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.composer.testcase.ast.editors.TypeInputCellEditor;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.core.annotation.SetUp;
import com.kms.katalon.core.annotation.TearDown;
import com.kms.katalon.core.annotation.TearDownIfError;
import com.kms.katalon.core.annotation.TearDownIfFailed;
import com.kms.katalon.core.annotation.TearDownIfPassed;

@SuppressWarnings("restriction")
public class MethodObjectBuilderDialog extends Dialog implements AstBuilderDialog {
	private static final String NEW_PARAM_DEFAULT_NAME = "param";
	private static final String DIALOG_TITLE = StringConstants.DIA_TITLE_METHOD_BUILDER;
	private MethodObjectBuilderDialog _instance;
	private Text returnTypeText, methodNameText;
	private TableViewer tableViewer;
	private Button btnSetup, btnTearDown, btnTearDownIfFailed, btnTearDownIfError, btnTearDownIfPassed;
	private ClassNode returnTypeNode;
	private String methodName;
	private List<Parameter> parameterList = new ArrayList<Parameter>();
	private int modifiers;
	private List<ClassNode> exceptionList = new ArrayList<ClassNode>();
	private Statement code;
	private boolean isSetup, isTearDown, isTearDownIfFailed, isTearDownIfError, isTearDownIfPassed;

	public MethodObjectBuilderDialog(Shell parentShell, MethodNode methodNode) {
		super(parentShell);
		initData(methodNode);
		_instance = this;
	}

	protected void initData(MethodNode methodNode) {
		if (methodNode != null) {
			methodName = methodNode.getName();
			returnTypeNode = methodNode.getReturnType();
			for (Parameter parameter : methodNode.getParameters()) {
				parameterList.add(parameter);
			}
			modifiers = methodNode.getModifiers();
			for (ClassNode exceptionNode : methodNode.getExceptions()) {
				exceptionList.add(exceptionNode);
			}
			code = methodNode.getCode();
			for (AnnotationNode annotation : methodNode.getAnnotations()) {
				if (annotation.getClassNode().getName().equals(SetUp.class.getName())) {
					isSetup = true;
				} else if (annotation.getClassNode().getName().equals(TearDown.class.getName())) {
					isTearDown = true;
				} else if (annotation.getClassNode().getName().equals(TearDownIfFailed.class.getName())) {
					isTearDownIfFailed = true;
				} else if (annotation.getClassNode().getName().equals(TearDownIfError.class.getName())) {
					isTearDownIfError = true;
				} else if (annotation.getClassNode().getName().equals(TearDownIfPassed.class.getName())) {
					isTearDownIfPassed = true;
				}
			}
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));

		Composite infoComposite = new Composite(container, SWT.NONE);
		infoComposite.setLayout(new GridLayout(2, false));
		infoComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite nameComposite = new Composite(infoComposite, SWT.NONE);
		GridData namGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		namGridData.minimumWidth = 250;
		nameComposite.setLayoutData(namGridData);
		GridLayout nameGridLayout = new GridLayout(2, false);
		nameGridLayout.verticalSpacing = 20;
		nameComposite.setLayout(nameGridLayout);

		Label methodNameLabel = new Label(nameComposite, SWT.NONE);
		methodNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		methodNameLabel.setText(StringConstants.PA_LBL_NAME);

		methodNameText = new Text(nameComposite, SWT.BORDER);
		GridData methodNameGridData = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		methodNameGridData.heightHint = 20;
		methodNameText.setLayoutData(methodNameGridData);
		methodNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				methodName = methodNameText.getText();
			}
		});

		btnSetup = new Button(nameComposite, SWT.CHECK);
		btnSetup.setText(StringConstants.PA_LBL_SET_UP_BUTTON);
		btnSetup.setSelection(isSetup);
		btnSetup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isSetup = btnSetup.getSelection();
			}
		});

		btnTearDown = new Button(nameComposite, SWT.CHECK);
		btnTearDown.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnTearDown.setText(StringConstants.PA_LBL_TEAR_DOWN_BUTTON);
		btnTearDown.setSelection(isTearDown);
		btnTearDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isTearDown = btnTearDown.getSelection();
			}
		});

		Composite returnTypeComposite = new Composite(infoComposite, SWT.NONE);
		GridData returnTypeGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		returnTypeGridData.minimumWidth = 250;
		returnTypeComposite.setLayoutData(returnTypeGridData);
		GridLayout returnTypeGridLayout = new GridLayout(3, false);
		returnTypeGridLayout.verticalSpacing = 20;
		returnTypeComposite.setLayout(returnTypeGridLayout);

		Label returnTypeLabel = new Label(returnTypeComposite, SWT.NONE);
		returnTypeLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		returnTypeLabel.setText(StringConstants.PA_LBL_RETURN_TYPE);

		returnTypeText = new Text(returnTypeComposite, SWT.BORDER);
		GridData gd_returnTypeText = new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1);
		gd_returnTypeText.heightHint = 20;
		returnTypeText.setEditable(false);
		returnTypeText.setLayoutData(gd_returnTypeText);

		Button returnTypeBrowseButton = new Button(returnTypeComposite, SWT.NONE);
		returnTypeBrowseButton.setText(StringConstants.PA_LBL_RETURN_TYPE_BROWSE_BUTTON);
		GridData gd_returnTypeBrowseButton = new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1);
		gd_returnTypeBrowseButton.minimumWidth = 100;
		gd_returnTypeBrowseButton.widthHint = 30;
		returnTypeBrowseButton.setLayoutData(gd_returnTypeBrowseButton);
		returnTypeBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				OpenTypeSelectionDialog dialog = new OpenTypeSelectionDialog(Display.getCurrent().getActiveShell(),
						false, PlatformUI.getWorkbench().getProgressService(), null, IJavaSearchConstants.TYPE);
				dialog.setTitle(JavaUIMessages.OpenTypeAction_dialogTitle);
				dialog.setMessage(JavaUIMessages.OpenTypeAction_dialogMessage);
				if (returnTypeNode != null) {
					dialog.setInitialPattern(returnTypeNode.getName());
				}
				if (dialog.open() == Window.OK && dialog.getResult().length == 1) {
					Object object = dialog.getResult()[0];
					if (object instanceof IType) {
						Class<?> newClass = getNewClassFromIType((IType) object);
						if (newClass != null) {
							returnTypeNode = new ClassNode(newClass);
							refresh();
						}
					}
				}
			}

		});

		btnTearDownIfFailed = new Button(returnTypeComposite, SWT.CHECK);
		btnTearDownIfFailed.setText(StringConstants.PA_LBL_TEAR_DOWN_FAILED_BUTTON);
		btnTearDownIfFailed.setSelection(isTearDownIfFailed);
		btnTearDownIfFailed.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isTearDownIfFailed = btnTearDownIfFailed.getSelection();
			}
		});

		btnTearDownIfError = new Button(returnTypeComposite, SWT.CHECK);
		btnTearDownIfError.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		btnTearDownIfError.setText(StringConstants.PA_LBL_TEAR_DOWN_ERROR_BUTTON);
		btnTearDownIfError.setSelection(isTearDownIfError);
		btnTearDownIfError.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isTearDownIfError = btnTearDownIfError.getSelection();
			}
		});

		// add blank label for layout
		new Label(returnTypeComposite, SWT.NONE);
		new Label(infoComposite, SWT.NONE);

		btnTearDownIfPassed = new Button(infoComposite, SWT.CHECK);
		GridData gd_btnCheckButton = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnCheckButton.horizontalIndent = 5;
		btnTearDownIfPassed.setLayoutData(gd_btnCheckButton);
		btnTearDownIfPassed.setText(StringConstants.PA_LBL_TEAR_DOWN_PASSED_BUTTON);
		btnTearDownIfPassed.setSelection(isTearDownIfPassed);
		btnTearDownIfPassed.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isTearDownIfPassed = btnTearDownIfPassed.getSelection();
			}
		});

		createTableComposite(container);

		refresh();
		return container;
	}

	protected Class<?> getNewClassFromIType(IType type) {
		Class<?> newClass = null;
		try {
			newClass = Class.forName(type.getFullyQualifiedName());
			return newClass;
		} catch (ClassNotFoundException e1) {
			// do nothing
		}
		return null;
	}

	protected void createTableComposite(Composite container) {
		Composite tableComposite = new Composite(container, SWT.NONE);
		tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tableComposite.setLayout(new GridLayout(1, false));

		tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(tableViewer,
				new FocusCellOwnerDrawHighlighter(tableViewer));

		ColumnViewerEditorActivationStrategy activationSupport = new ColumnViewerEditorActivationStrategy(tableViewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				if (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION) {
					EventObject source = event.sourceEvent;
					if (source instanceof MouseEvent && ((MouseEvent) source).button == 3) return false;

					return true;
				} else if (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR) {
					return true;
				}
				return false;
			}
		};

		TableViewerEditor.create(tableViewer, focusCellManager, activationSupport,
				ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
						| ColumnViewerEditor.KEYBOARD_ACTIVATION);

		TableViewerColumn tableViewerColumnParamType = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumnParamType.getColumn().setText(StringConstants.DIA_COL_PARAM_TYPE);
		tableViewerColumnParamType.getColumn().setWidth(335);
		tableViewerColumnParamType.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Parameter) {
					Parameter parameter = (Parameter) element;
					return parameter.getType().getName();
				}
				return StringUtils.EMPTY;
			}
		});
		tableViewerColumnParamType.setEditingSupport(new EditingSupport(tableViewer) {
			@Override
			protected void setValue(Object element, Object value) {
				if (element instanceof Parameter && value instanceof IType) {
					Parameter oldParameter = (Parameter) element;
					int parameterIndex = parameterList.indexOf(oldParameter);
					if (parameterIndex >= 0 && parameterIndex < parameterList.size()) {
						Class<?> newClass = getNewClassFromIType((IType) value);
						if (newClass != null) {
							parameterList.set(parameterIndex,
									new Parameter(new ClassNode(newClass), oldParameter.getName()));
							refresh();
						}
					}
				}
			}

			@Override
			protected Object getValue(Object element) {
				if (element instanceof Parameter) {
					return ((Parameter) element).getType().getName();
				}
				return "";
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (element instanceof Parameter) {
					return new TypeInputCellEditor(tableViewer.getTable(), ((Parameter) element).getType().getName());
				}
				return null;
			}

			@Override
			protected boolean canEdit(Object element) {
				if (element instanceof Parameter) {
					return true;
				}
				return false;
			}
		});

		TableViewerColumn tableViewerColumnParamName = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumnParamName.getColumn().setText(StringConstants.DIA_COL_PARAM_NAME);
		tableViewerColumnParamName.getColumn().setWidth(335);
		tableViewerColumnParamName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Parameter) {
					Parameter parameter = (Parameter) element;
					return parameter.getName();
				}
				return StringUtils.EMPTY;
			}
		});
		tableViewerColumnParamName.setEditingSupport(new EditingSupport(tableViewer) {
			@Override
			protected void setValue(Object element, Object value) {
				if (element instanceof Parameter && value instanceof String) {
					Parameter oldParameter = (Parameter) element;
					int parameterIndex = parameterList.indexOf(oldParameter);
					if (parameterIndex >= 0 && parameterIndex < parameterList.size()) {
						parameterList.set(parameterIndex, new Parameter(oldParameter.getType(), (String) value));
						refresh();
					}
				}
			}

			@Override
			protected Object getValue(Object element) {
				if (element instanceof Parameter) {
					return ((Parameter) element).getName();
				}
				return "";
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return new TextCellEditor(tableViewer.getTable());
			}

			@Override
			protected boolean canEdit(Object element) {
				if (element instanceof Parameter) {
					return true;
				}
				return false;
			}
		});
	}

	public void refresh() {
		methodNameText.setText(methodName != null ? methodName : "");
		returnTypeText.setText(returnTypeNode != null ? returnTypeNode.getName() : "");
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(parameterList);
		tableViewer.refresh();
	}

	protected void createButtonsForButtonBar(Composite parent) {
		Button btnInsert = createButton(parent, 100, StringConstants.DIA_BTN_INSERT, true);
		btnInsert.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int selectionIndex = tableViewer.getTable().getSelectionIndex();
				Parameter parameter = new Parameter(new ClassNode(Object.class), NEW_PARAM_DEFAULT_NAME);

				if (selectionIndex < 0 || selectionIndex >= parameterList.size()) {
					parameterList.add(parameter);
				} else {
					parameterList.add(selectionIndex + 1, parameter);
				}
				tableViewer.refresh();
				tableViewer.getTable().setSelection(selectionIndex + 1);
			}
		});

		Button btnRemove = createButton(parent, 200, StringConstants.DIA_BTN_REMOVE, false);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<Parameter> removeParameters = new ArrayList<Parameter>();
				for (int index : tableViewer.getTable().getSelectionIndices()) {
					if (index >= 0 && index < parameterList.size()) {
						removeParameters.add(parameterList.get(index));
					}
				}
				for (Parameter parameter : removeParameters) {
					parameterList.remove(parameter);
				}
				tableViewer.refresh();
			}
		});
		Button btnOK = createButton(parent, 102, IDialogConstants.OK_LABEL, true);
		btnOK.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (verify()) {
					_instance.close();
				}
			}
		});
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	protected boolean verify() {
		if (methodName == null || methodName.isEmpty()) {
			MessageDialog.openError(getShell(), StringConstants.ERROR_TITLE,
					StringConstants.DIA_ERROR_METHOD_NAME_EMPTY);
			return false;
		}
		if (isJavaKeyword(methodName)) {
			MessageDialog.openError(getShell(), StringConstants.ERROR_TITLE,
					MessageFormat.format(StringConstants.DIA_ERROR_METHOD_NAME_X_INVALID_JAVA_KEYWORD, methodName));
			return false;
		}
		return true;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(DIALOG_TITLE);
	}

	public MethodNode getReturnValue() {
		MethodNode newMethod = new MethodNode(methodName, modifiers, returnTypeNode != null ? returnTypeNode
				: new ClassNode(Object.class), parameterList.toArray(new Parameter[parameterList.size()]),
				exceptionList.toArray(new ClassNode[exceptionList.size()]), code != null ? code : new BlockStatement());
		addAnnotation(newMethod);
		// set line number to differentiate from auto-generated method
		newMethod.setLineNumber(1);
		return newMethod;
	}

	protected void addAnnotation(MethodNode newMethod) {
		if (isSetup) {
			newMethod.addAnnotation(new AnnotationNode(new ClassNode(SetUp.class)));
		}
		if (isTearDown) {
			newMethod.addAnnotation(new AnnotationNode(new ClassNode(TearDown.class)));
		}
		if (isTearDownIfFailed) {
			newMethod.addAnnotation(new AnnotationNode(new ClassNode(TearDownIfFailed.class)));
		}
		if (isTearDownIfError) {
			newMethod.addAnnotation(new AnnotationNode(new ClassNode(TearDownIfError.class)));
		}
		if (isTearDownIfPassed) {
			newMethod.addAnnotation(new AnnotationNode(new ClassNode(TearDownIfPassed.class)));
		}
	}

	@Override
	public void changeObject(Object originalObject, Object newObject) {
		// Do nothing for this dialog
	}

	@Override
	public String getDialogTitle() {
		return DIALOG_TITLE;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(700, 500);
	}

	static final Collator englishCollator = Collator.getInstance(Locale.ENGLISH);
	static final String keywords[] = { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
			"class", "const", "continue", "default", "do", "double", "else", "extends", "false", "final", "finally",
			"float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
			"new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
			"super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "void",
			"volatile", "while" };

	private static boolean isJavaKeyword(String keyword) {
		return (Arrays.binarySearch(keywords, keyword, englishCollator) >= 0);
	}
}