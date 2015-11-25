package com.kms.katalon.composer.testcase.ast.dialogs;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

import com.kms.katalon.composer.components.impl.dialogs.TreeEntitySelectionDialog;
import com.kms.katalon.composer.components.impl.providers.AbstractEntityViewerFilter;
import com.kms.katalon.composer.components.impl.providers.IEntityLabelProvider;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.model.ICustomInputValueType;
import com.kms.katalon.composer.testcase.model.IInputValueType;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.composer.testcase.providers.AstInputTypeLabelProvider;
import com.kms.katalon.composer.testcase.providers.AstInputValueLabelProvider;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueColumnSupport;
import com.kms.katalon.composer.testcase.support.AstInputBuilderValueTypeColumnSupport;
import com.kms.katalon.composer.testcase.util.AstTreeTableInputUtil;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.groovy.GroovyParser;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.repository.WebElementEntity;

public class TestObjectBuilderDialog extends TreeEntitySelectionDialog implements AstBuilderDialog {
    private final InputValueType[] defaultInputValueTypes = { InputValueType.Variable };

    private static final String DIALOG_TITLE = StringConstants.DIA_TITLE_TEST_OBJ_INPUT;

    private static final String OBJECT_FINDER_TAB_NAME = InputValueType.TestObject.toString();
    private static final String OTHER_TAB_NAME = StringConstants.DIA_TAB_OTHER;
    private static final String[] TEST_OBJECT_TABS = { OBJECT_FINDER_TAB_NAME, OTHER_TAB_NAME };

    private TestObjectBuilderDialog _instance;
    private TableViewer tableViewer;
    private Expression objectExpression;
    private int fWidth = 60;
    private int fHeight = 18;
    private ClassNode scriptClass;
    private boolean haveOtherTypes;
    private Composite comboComposite;
    private Combo combo;

    public TestObjectBuilderDialog(Shell parentShell, Expression objectExpression, IEntityLabelProvider labelProvider,
            ITreeContentProvider contentProvider, AbstractEntityViewerFilter entityViewerFilter, ClassNode scriptClass,
            boolean haveOtherTypes) {
        super(parentShell, labelProvider, contentProvider, entityViewerFilter);
        this.scriptClass = scriptClass;
        _instance = this;
        this.haveOtherTypes = haveOtherTypes;
        if (objectExpression != null) {
            this.objectExpression = GroovyParser.cloneExpression(objectExpression);
        } else {
            this.objectExpression = AstTreeTableInputUtil.generateObjectMethodCall(null);
        }
        setAllowMultiple(false);
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        if (currentProject != null) {
            try {
                setInput(TreeEntityUtil.getChildren(null,
                        FolderController.getInstance().getObjectRepositoryRoot(currentProject)));
                refreshSelectionTree();
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
    }

    protected void refreshSelectionTree() {
        try {
            Expression testObjectExpression = null;
            if (objectExpression instanceof MethodCallExpression) {
                testObjectExpression = AstTreeTableInputUtil.getObjectParam((MethodCallExpression) objectExpression);
            }
            if (objectExpression != null && testObjectExpression != null) {
                WebElementEntity selectedWebElement = ObjectRepositoryController.getInstance()
                        .getWebElementByDisplayPk(testObjectExpression.getText());
                if (selectedWebElement != null) {
                    setInitialSelection(new WebElementTreeEntity(selectedWebElement, createSelectedTreeEntityHierachy(
                            selectedWebElement.getParentFolder(), FolderController.getInstance()
                                    .getObjectRepositoryRoot(ProjectController.getInstance().getCurrentProject()))));
                }
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        parent.setLayoutData(new GridData(GridData.FILL_BOTH));
        parent.setLayout(new GridLayout(1, false));

        if (haveOtherTypes) {
            comboComposite = new Composite(parent, SWT.NONE);
            comboComposite.setLayout(new GridLayout(2, false));
            Label comboLabel = new Label(comboComposite, SWT.NONE);
            comboLabel.setText(StringConstants.DIA_LBL_OBJ_TYPE);

            combo = new Combo(comboComposite, SWT.DROP_DOWN);
            combo.setItems(TEST_OBJECT_TABS);
        }

        final Composite stackComposite = new Composite(parent, SWT.NONE);
        applyDialogFont(stackComposite);
        final StackLayout stackLayout = new StackLayout();
        stackComposite.setLayout(stackLayout);

        final Composite objectFinderComposite = new Composite(stackComposite, SWT.NONE);
        objectFinderComposite.setLayout(new GridLayout(1, false));

        TreeViewer treeViewer = createTreeViewer(objectFinderComposite);

        GridData data = new GridData(GridData.FILL_BOTH);
        data.widthHint = convertWidthInCharsToPixels(fWidth);
        data.heightHint = convertHeightInCharsToPixels(fHeight);

        Tree treeWidget = treeViewer.getTree();
        treeWidget.setLayoutData(data);
        treeWidget.setFont(parent.getFont());

        treeWidget.setEnabled(true);
        stackLayout.topControl = objectFinderComposite;

        if (haveOtherTypes) {
            final Composite inputTableComposite = new Composite(stackComposite, SWT.NONE);
            inputTableComposite.setLayout(new GridLayout(1, false));

            tableViewer = new TableViewer(inputTableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
            Table table = tableViewer.getTable();
            table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            table.setLinesVisible(true);
            table.setHeaderVisible(true);

            TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(tableViewer,
                    new FocusCellOwnerDrawHighlighter(tableViewer));

            ColumnViewerEditorActivationStrategy activationSupport = new ColumnViewerEditorActivationStrategy(
                    tableViewer) {
                protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
                    if (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION) {
                        EventObject source = event.sourceEvent;
                        if (source instanceof MouseEvent && ((MouseEvent) source).button == 3)
                            return false;

                        return true;
                    } else if (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED
                            && event.keyCode == SWT.CR) {
                        return true;
                    }
                    return false;
                }
            };

            TableViewerEditor.create(tableViewer, focusCellManager, activationSupport,
                    ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
                            | ColumnViewerEditor.KEYBOARD_ACTIVATION);

            TableViewerColumn tableViewerColumnValueType = new TableViewerColumn(tableViewer, SWT.NONE);
            tableViewerColumnValueType.getColumn().setText(StringConstants.DIA_COL_VALUE_TYPE);
            tableViewerColumnValueType.getColumn().setWidth(100);
            tableViewerColumnValueType.setLabelProvider(new AstInputTypeLabelProvider(scriptClass));

            tableViewerColumnValueType.setEditingSupport(new AstInputBuilderValueTypeColumnSupport(tableViewer,
                    defaultInputValueTypes, ICustomInputValueType.TAG_TEST_OBJECT, this, scriptClass));

            TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
            tableViewerColumnValue.getColumn().setText(StringConstants.DIA_COL_VALUE);
            tableViewerColumnValue.getColumn().setWidth(300);
            tableViewerColumnValue.setLabelProvider(new AstInputValueLabelProvider(scriptClass));
            tableViewerColumnValue.setEditingSupport(new AstInputBuilderValueColumnSupport(tableViewer, this,
                    scriptClass));

            if (objectExpression instanceof MethodCallExpression
                    && AstTreeTableInputUtil.isObjectArgument((MethodCallExpression) objectExpression)) {
                combo.select(0);
                stackLayout.topControl = objectFinderComposite;
            } else {
                combo.select(1);
                stackLayout.topControl = inputTableComposite;
            }
            combo.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    int index = combo.getSelectionIndex();
                    if (index >= 0 && index < TEST_OBJECT_TABS.length) {
                        String tabName = TEST_OBJECT_TABS[index];
                        if (tabName.equals(OBJECT_FINDER_TAB_NAME)) {
                            objectExpression = (Expression) InputValueType.TestObject.getNewValue(null);
                            stackLayout.topControl = objectFinderComposite;
                        } else {
                            List<IInputValueType> valueTypes = AstTreeTableInputUtil.getInputValueTypeList(
                                    defaultInputValueTypes, ICustomInputValueType.TAG_TEST_OBJECT);
                            if (valueTypes.size() > 0) {
                                objectExpression = (Expression) valueTypes.get(0).getNewValue(null);
                                stackLayout.topControl = inputTableComposite;
                                setSelectionResult(null);
                            }
                        }
                        refresh();
                        stackComposite.layout();
                    }
                }
            });
        }

        refresh();
        return stackComposite;
    }

    private FolderTreeEntity createSelectedTreeEntityHierachy(FolderEntity folderEntity, FolderEntity rootFolder) {
        if (folderEntity == null || folderEntity.equals(rootFolder)) {
            return null;
        }
        return new FolderTreeEntity(folderEntity, createSelectedTreeEntityHierachy(folderEntity.getParentFolder(),
                rootFolder));
    }

    @Override
    public void refresh() {
        List<Expression> objectExpressionList = new ArrayList<Expression>();
        objectExpressionList.add(objectExpression);
        if (haveOtherTypes) {
            tableViewer.setContentProvider(new ArrayContentProvider());
            tableViewer.setInput(objectExpressionList);
        }
    }

    protected void createButtonsForButtonBar(Composite parent) {
        Button btnOK = createButton(parent, 102, IDialogConstants.OK_LABEL, true);
        btnOK.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                _instance.close();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    protected void convertSelectionToObjectMethodCall() {
        Object selectedObject = getFirstResult();
        if (selectedObject instanceof WebElementTreeEntity) {
            try {
                WebElementTreeEntity webElementTreeEntity = (WebElementTreeEntity) selectedObject;
                if (webElementTreeEntity.getObject() instanceof WebElementEntity) {
                    String objectPk = ObjectRepositoryController.getInstance().getIdForDisplay(
                            (WebElementEntity) webElementTreeEntity.getObject());
                    objectExpression = AstTreeTableInputUtil.generateObjectMethodCall(objectPk);
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(getDialogTitle());
    }

    @Override
    public Expression getReturnValue() {
        convertSelectionToObjectMethodCall();
        return objectExpression;
    }

    @Override
    public void changeObject(Object originalObject, Object newObject) {
        if (originalObject == objectExpression && newObject instanceof Expression) {
            objectExpression = (Expression) newObject;
            refresh();
        }
    }

    @Override
    public String getDialogTitle() {
        return DIALOG_TITLE;
    }
}
