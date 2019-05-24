package com.kms.katalon.composer.testcase.ast.dialogs;

import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
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
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColumnViewerUtil;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.editors.TypeSelectionDialogCellEditor;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.AnnotationNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.MethodNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ParameterWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BlockStatementWrapper;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;
import com.kms.katalon.core.annotation.SetUp;
import com.kms.katalon.core.annotation.TearDown;
import com.kms.katalon.core.annotation.TearDownIfError;
import com.kms.katalon.core.annotation.TearDownIfFailed;
import com.kms.katalon.core.annotation.TearDownIfPassed;

@SuppressWarnings("restriction")
public class MethodObjectBuilderDialog extends Dialog implements IAstDialogBuilder {
    private static final String NEW_PARAM_DEFAULT_NAME = "param";

    private MethodObjectBuilderDialog _instance;

    private Text returnTypeText, methodNameText;

    private TableViewer tableViewer;

    private Button btnSetup, btnTearDown, btnTearDownIfFailed, btnTearDownIfError, btnTearDownIfPassed;

    private ClassNodeWrapper returnTypeNode;

    private String methodName;

    private List<ParameterWrapper> parameterList = new ArrayList<ParameterWrapper>();

    private int modifiers;

    private List<ClassNodeWrapper> exceptionList = new ArrayList<ClassNodeWrapper>();

    private BlockStatementWrapper code;

    private boolean isSetup, isTearDown, isTearDownIfFailed, isTearDownIfError, isTearDownIfPassed;

    private MethodNodeWrapper tempMethod = null;

    public MethodObjectBuilderDialog(Shell parentShell, MethodNodeWrapper methodNode, ASTNodeWrapper parent) {
        super(parentShell);
        initData(methodNode, parent);
        _instance = this;
    }

    protected void initData(MethodNodeWrapper methodNode, ASTNodeWrapper parent) {
        if (methodNode == null) {
            tempMethod = new MethodNodeWrapper(parent);
            return;
        }
        tempMethod = methodNode.clone();
        methodName = methodNode.getName();
        returnTypeNode = methodNode.getReturnType();
        for (ParameterWrapper parameter : methodNode.getParameters()) {
            parameterList.add(parameter);
        }
        modifiers = methodNode.getModifiers();
        for (ClassNodeWrapper exceptionNode : methodNode.getExceptions()) {
            exceptionList.add(exceptionNode);
        }
        code = methodNode.getBlock();
        isSetup = methodNode.getAnnotationByClass(SetUp.class) != null;
        isTearDown = methodNode.getAnnotationByClass(TearDown.class) != null;
        isTearDownIfFailed = methodNode.getAnnotationByClass(TearDownIfFailed.class) != null;
        isTearDownIfError = methodNode.getAnnotationByClass(TearDownIfError.class) != null;
        isTearDownIfPassed = methodNode.getAnnotationByClass(TearDownIfPassed.class) != null;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));

        createInfoComposite(container);

        createTableComposite(container);

        refresh();
        return container;
    }

    private void createInfoComposite(Composite container) {
        Composite infoComposite = new Composite(container, SWT.NONE);
        infoComposite.setLayout(new GridLayout(2, false));
        infoComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        createNameComposite(infoComposite);

        Composite returnTypeComposite = addReturnTypeComposite(infoComposite);

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
    }

    private Composite addReturnTypeComposite(Composite parent) {
        Composite returnTypeComposite = new Composite(parent, SWT.NONE);
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
                if (dialog.open() != Window.OK || dialog.getResult().length != 1
                        || !(dialog.getResult()[0] instanceof IType)) {
                    return;
                }

                Class<?> newClass = getClassFromIType((IType) dialog.getResult()[0]);
                if (newClass != null) {
                    returnTypeNode = new ClassNodeWrapper(newClass, tempMethod);
                    refresh();
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
        return returnTypeComposite;
    }

    private void createNameComposite(Composite parent) {
        Composite nameComposite = new Composite(parent, SWT.NONE);
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
    }

    protected Class<?> getClassFromIType(IType type) {
        return AstKeywordsInputUtil.loadType(type.getFullyQualifiedName(), tempMethod.getScriptClass());
    }

    protected void createTableComposite(Composite container) {
        Composite tableComposite = new Composite(container, SWT.NONE);
        tableComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        tableComposite.setLayout(new GridLayout(1, false));

        tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        Table table = tableViewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.setLinesVisible(ControlUtils.shouldLineVisble(table.getDisplay()));
        table.setHeaderVisible(true);

        ColumnViewerUtil.setTableActivation(tableViewer);

        addTableColumns();
    }

    private void addTableColumns() {
        addTableColumnParamType();

        addTableColumnParamName();
    }

    private void addTableColumnParamName() {
        TableViewerColumn tableViewerColumnParamName = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnParamName.getColumn().setText(StringConstants.DIA_COL_PARAM_NAME);
        tableViewerColumnParamName.getColumn().setWidth(335);
        tableViewerColumnParamName.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof ParameterWrapper) {
                    ParameterWrapper parameter = (ParameterWrapper) element;
                    return parameter.getName();
                }
                return StringUtils.EMPTY;
            }
        });
        tableViewerColumnParamName.setEditingSupport(new EditingSupport(tableViewer) {
            @Override
            protected void setValue(Object element, Object value) {
                if (element instanceof ParameterWrapper && value instanceof String) {
                    ParameterWrapper oldParameterWrapper = (ParameterWrapper) element;
                    int parameterIndex = parameterList.indexOf(oldParameterWrapper);
                    if (parameterIndex >= 0 && parameterIndex < parameterList.size()) {
                        parameterList.set(parameterIndex, new ParameterWrapper(oldParameterWrapper.getType()
                                .getTypeClass(), (String) value, tempMethod));
                        refresh();
                    }
                }
            }

            @Override
            protected Object getValue(Object element) {
                if (element instanceof ParameterWrapper) {
                    return ((ParameterWrapper) element).getName();
                }
                return "";
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                return new TextCellEditor(tableViewer.getTable());
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element instanceof ParameterWrapper) {
                    return true;
                }
                return false;
            }
        });
    }

    private void addTableColumnParamType() {
        TableViewerColumn tableViewerColumnParamType = new TableViewerColumn(tableViewer, SWT.NONE);
        tableViewerColumnParamType.getColumn().setText(StringConstants.DIA_COL_PARAM_TYPE);
        tableViewerColumnParamType.getColumn().setWidth(335);
        tableViewerColumnParamType.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof ParameterWrapper) {
                    ParameterWrapper parameter = (ParameterWrapper) element;
                    return parameter.getType().getName();
                }
                return StringUtils.EMPTY;
            }
        });
        tableViewerColumnParamType.setEditingSupport(new EditingSupport(tableViewer) {
            @Override
            protected void setValue(Object element, Object value) {
                if (element instanceof ParameterWrapper && value instanceof IType) {
                    ParameterWrapper oldParameterWrapper = (ParameterWrapper) element;
                    int parameterIndex = parameterList.indexOf(oldParameterWrapper);
                    if (parameterIndex >= 0 && parameterIndex < parameterList.size()) {
                        Class<?> newClass = getClassFromIType((IType) value);
                        if (newClass != null) {
                            parameterList.set(parameterIndex,
                                    new ParameterWrapper(newClass, oldParameterWrapper.getName(), tempMethod));
                            refresh();
                        }
                    }
                }
            }

            @Override
            protected Object getValue(Object element) {
                if (element instanceof ParameterWrapper) {
                    return ((ParameterWrapper) element).getType().getName();
                }
                return "";
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                if (element instanceof ParameterWrapper) {
                    return new TypeSelectionDialogCellEditor(tableViewer.getTable(),
                            ((ParameterWrapper) element).getType().getName());
                }
                return null;
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element instanceof ParameterWrapper) {
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
        createInsertButton(parent);

        createRemoveButton(parent);
        createOKButton(parent);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    private void createOKButton(Composite parent) {
        Button btnOK = createButton(parent, 102, IDialogConstants.OK_LABEL, true);
        btnOK.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (verify()) {
                    _instance.close();
                }
            }
        });
    }

    private void createRemoveButton(Composite parent) {
        Button btnRemove = createButton(parent, 200, StringConstants.DIA_BTN_REMOVE, false);
        btnRemove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                List<ParameterWrapper> removeParameterWrappers = new ArrayList<ParameterWrapper>();
                for (int index : tableViewer.getTable().getSelectionIndices()) {
                    if (index >= 0 && index < parameterList.size()) {
                        removeParameterWrappers.add(parameterList.get(index));
                    }
                }
                for (ParameterWrapper parameter : removeParameterWrappers) {
                    parameterList.remove(parameter);
                }
                tableViewer.refresh();
            }
        });
    }

    private void createInsertButton(Composite parent) {
        Button btnInsert = createButton(parent, 100, StringConstants.DIA_BTN_INSERT, true);
        btnInsert.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectionIndex = tableViewer.getTable().getSelectionIndex();
                ParameterWrapper parameter = new ParameterWrapper(Object.class, NEW_PARAM_DEFAULT_NAME, tempMethod);

                if (selectionIndex < 0 || selectionIndex >= parameterList.size()) {
                    parameterList.add(parameter);
                } else {
                    parameterList.add(selectionIndex + 1, parameter);
                }
                tableViewer.refresh();
                tableViewer.getTable().setSelection(selectionIndex + 1);
            }
        });
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
        newShell.setText(getDialogTitle());
    }

    public MethodNodeWrapper getReturnValue() {
        tempMethod.setName(methodName);
        tempMethod.setModifiers(modifiers);
        tempMethod.setReturnType(returnTypeNode != null ? returnTypeNode : new ClassNodeWrapper(Object.class,
                tempMethod));
        tempMethod.setParameters(parameterList.toArray(new ParameterWrapper[parameterList.size()]));
        tempMethod.setExceptions(exceptionList.toArray(new ClassNodeWrapper[exceptionList.size()]));
        tempMethod.setBlock((BlockStatementWrapper) (code != null ? code : tempMethod.getBlock()));
        processAnnotation(tempMethod, isSetup, SetUp.class);
        processAnnotation(tempMethod, isTearDown, TearDown.class);
        processAnnotation(tempMethod, isTearDownIfFailed, TearDownIfFailed.class);
        processAnnotation(tempMethod, isTearDownIfError, TearDownIfError.class);
        processAnnotation(tempMethod, isTearDownIfPassed, TearDownIfPassed.class);
        return tempMethod;
    }

    private static void processAnnotation(MethodNodeWrapper method, boolean toogleValue, Class<?> annotationClass) {
        if (toogleValue && method.getAnnotationByClass(annotationClass) == null) {
            method.addAnnotation(new AnnotationNodeWrapper(annotationClass, method));
        } else if (!toogleValue && method.getAnnotationByClass(annotationClass) != null) {
            method.removeAnnotation(method.getAnnotationByClass(annotationClass));
        }
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

    public String getDialogTitle() {
        return StringConstants.DIA_TITLE_METHOD_BUILDER;
    }
}
