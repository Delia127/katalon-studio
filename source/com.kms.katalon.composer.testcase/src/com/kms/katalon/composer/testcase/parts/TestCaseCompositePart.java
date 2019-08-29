package com.kms.katalon.composer.testcase.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.eclipse.editor.GroovyEditor;
import org.codehaus.groovy.eclipse.refactoring.actions.FormatKind;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartSashContainerElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.controls.HelpToolBarForCompositePart;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.TestCaseTreeEntity;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.impl.util.EventUtil;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.part.IComposerPartEvent;
import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.composer.explorer.util.TransferTypeCollection;
import com.kms.katalon.composer.testcase.actions.KatalonFormatAction;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.exceptions.GroovyParsingException;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.model.TestCaseTreeTableInput;
import com.kms.katalon.composer.testcase.preferences.TestCasePreferenceDefaultValueInitializer;
import com.kms.katalon.composer.testcase.providers.TestObjectScriptDropListener;
import com.kms.katalon.composer.testcase.util.TestCaseEntityUtil;
import com.kms.katalon.composer.util.groovy.GroovyGuiUtil;
import com.kms.katalon.composer.util.groovy.editor;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.core.ast.GroovyParser;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.groovy.util.GroovyUtil;
import com.kms.katalon.tracking.service.Trackings;

@SuppressWarnings("restriction")
public class TestCaseCompositePart implements EventHandler, SavableCompositePart, IComposerPartEvent {

    public static final int CHILD_TEST_CASE_EDITOR_PART_INDEX = 1;

    private static final int CHILD_TEST_CASE_MANUAL_PART_INDEX = 0;

    private static final int CHILD_TEST_CASE_VARIABLE_PART_INDEX = 2;

    private static final int CHILD_TEST_CASE_VARIABLE_EDITOR_PART_INDEX = 3;

    private static final int CHILD_TEST_CASE_INTEGRATION_PART_INDEX = 4;

    private static final int CHILD_TEST_CASE_PROPERTIES_PART_INDEX = 5;

    public static final String SCRIPT_TAB_TITLE = StringConstants.PA_TAB_SCRIPT;

    public static final String MANUAL_TAB_TITLE = StringConstants.PA_TAB_MANUAL;

    public static final String VARIABLE_TAB_TITLE = StringConstants.PA_TAB_VARIABLE;

    public static final String VARIABLE_EDITOR_TAB_TITLE = StringConstants.PA_TAB_VARIABLE_EDITOR;

    public static final String INTEGRATION_TAB_TITLE = StringConstants.PA_TAB_INTEGRATION;

    public static final String PROPERTIES_TAB_TITLE = ComposerTestcaseMessageConstants.PA_TAB_PROPERTIES;

    @Inject
    private MDirtyable dirty;

    public MDirtyable getDirty() {
        return dirty;
    }

    private IPropertyListener childPropertyListner;

    private MCompositePart compositePart;

    @Inject
    private EPartService partService;

    @Inject
    private IEventBroker eventBroker;

    @Inject
    protected EModelService modelService;

    @Inject
    protected MApplication application;

    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell parentShell;

    private TestCasePart childTestCasePart;

    private TestCaseVariablePart childTestCaseVariablesPart;

    private TestCaseVariableEditorPart childTestCaseVariableEditorPart;

    private CompatibilityEditor childTestCaseEditorPart;

    private TestCaseIntegrationPart childTestCaseIntegrationPart;

    private TestCasePropertiesPart propertiesPart;

    private GroovyEditor groovyEditor;

    private CTabFolder tabFolder;

    private MPartStack subPartStack;

    private boolean editorLastDirty;

    private boolean isInitialized;

    private boolean isScriptChanged;

    private boolean variableEditorLastDirty = false;

    private TestCaseEntity testCase;

    private TestCaseEntity originalTestCase;

    private ScriptNodeWrapper scriptNode;

    private boolean parsingFailed;

    private boolean disposed;

    private boolean invalidScheme;

    private boolean variableTab = true;

    public boolean isInitialized() {
        return isInitialized;
    }

    @PostConstruct
    public void init(Composite parent, MCompositePart compositePart) {
        this.compositePart = compositePart;
        dirty.setDirty(false);
        isInitialized = false;
        isScriptChanged = false;
        changeOriginalTestCase((TestCaseEntity) compositePart.getObject());
        initListeners();
        createToolBar(compositePart);
        invalidScheme = false;
    }

    private void createToolBar(MPart part) {
        new HelpToolBarForCompositePart(part, partService) {
            @Override
            protected String getDocumentationUrlForPartObject(Object partObject) {
                if (partObject instanceof TestCasePart) {
                    return DocumentationMessageConstants.TEST_CASE_MANUAL;
                }
                if (partObject instanceof TestCaseVariablePart) {
                    return DocumentationMessageConstants.TEST_CASE_VARIABLE;
                }

                if (partObject instanceof TestCaseVariableEditorPart) {
                    return DocumentationMessageConstants.TEST_CASE_VARIABLE_EDITOR;
                }
                if (partObject instanceof CompatibilityEditor) {
                    return DocumentationMessageConstants.TEST_CASE_SCRIPT;
                }

                if (partObject instanceof TestCaseIntegrationPart) {
                    return ((TestCaseIntegrationPart) partObject).getDocumentationUrl();
                }
                if (partObject instanceof TestCasePropertiesPart) {
                    return DocumentationMessageConstants.TEST_CASE_PROPERTIES;
                }
                return null;
            }
        };
    }

    public void initDefaultSelectedPart() {
        String defaultTestCaseView = TestCasePreferenceDefaultValueInitializer.getTestCasePartStartView();
        if (StringUtils.equals(defaultTestCaseView, MANUAL_TAB_TITLE)) {
            if (tabFolder.getSelectionIndex() != CHILD_TEST_CASE_MANUAL_PART_INDEX) {
                setSelectedPart(getChildManualPart());
            } else if (isScriptChanged) {
                setScriptContentToManual();
            }
        } else if (StringUtils.equals(defaultTestCaseView, SCRIPT_TAB_TITLE)) {
            if (tabFolder.getSelectionIndex() != CHILD_TEST_CASE_EDITOR_PART_INDEX) {
                setSelectedPart(getChildCompatibilityPart());
            } else if (childTestCasePart.isManualScriptChanged()) {
              setChildEditorContents(scriptNode);
            }
        }

    }

    public void initComponent() {
        List<MPartSashContainerElement> compositePartChildren = compositePart.getChildren();
        if (compositePartChildren.size() == 1 && compositePartChildren.get(0) instanceof MPartStack) {
            subPartStack = (MPartStack) compositePartChildren.get(0);
            if (subPartStack.getChildren().size() == 6) {
                for (MStackElement stackElement : subPartStack.getChildren()) {
                    if (!(stackElement instanceof MPart)) {
                        continue;
                    }

                    Object partObject = ((MPart) stackElement).getObject();

                    if (partObject instanceof TestCasePart) {
                        childTestCasePart = (TestCasePart) partObject;
                        continue;
                    }

                    if (partObject instanceof CompatibilityEditor) {
                        initChildEditorPart((CompatibilityEditor) partObject);
                        continue;
                    }

                    if (partObject instanceof TestCaseVariablePart) {
                        childTestCaseVariablesPart = (TestCaseVariablePart) partObject;
                        continue;
                    }

                    if (partObject instanceof TestCaseVariableEditorPart) {
                        childTestCaseVariableEditorPart = (TestCaseVariableEditorPart) partObject;
                        continue;
                    }

                    if (partObject instanceof TestCaseIntegrationPart) {
                        childTestCaseIntegrationPart = (TestCaseIntegrationPart) partObject;
                        continue;
                    }

                    if (partObject instanceof TestCasePropertiesPart) {
                        propertiesPart = (TestCasePropertiesPart) partObject;
                    }
                }
            }

            if (subPartStack.getWidget() instanceof CTabFolder) {
                tabFolder = (CTabFolder) subPartStack.getWidget();

                tabFolder.setTabPosition(SWT.BOTTOM);
                tabFolder.setBorderVisible(false);
                tabFolder.setMaximizeVisible(false);
                tabFolder.setMinimizeVisible(false);

                if (tabFolder.getItemCount() == 6) {
                    CTabItem testCasePartTab = tabFolder.getItem(CHILD_TEST_CASE_MANUAL_PART_INDEX);
                    testCasePartTab.setText(MANUAL_TAB_TITLE);
                    testCasePartTab.setImage(ImageConstants.IMG_16_MANUAL);
                    testCasePartTab.setShowClose(false);

                    CTabItem groovyEditorPartTab = tabFolder.getItem(CHILD_TEST_CASE_EDITOR_PART_INDEX);
                    groovyEditorPartTab.setText(SCRIPT_TAB_TITLE);
                    groovyEditorPartTab.setImage(ImageConstants.IMG_16_SCRIPT);
                    groovyEditorPartTab.setShowClose(false);

                    CTabItem variablePartTab = tabFolder.getItem(CHILD_TEST_CASE_VARIABLE_PART_INDEX);
                    variablePartTab.setText(VARIABLE_TAB_TITLE);
                    variablePartTab.setImage(ImageConstants.IMG_16_VARIABLE);
                    variablePartTab.setShowClose(false);

                    CTabItem variableEditorPartTab = tabFolder.getItem(CHILD_TEST_CASE_VARIABLE_EDITOR_PART_INDEX);
                    variableEditorPartTab.setText(VARIABLE_EDITOR_TAB_TITLE);
                    variableEditorPartTab.setImage(ImageConstants.IMG_16_SCRIPT);
                    variableEditorPartTab.setShowClose(false);

                    CTabItem integrationPartTab = tabFolder.getItem(CHILD_TEST_CASE_INTEGRATION_PART_INDEX);
                    integrationPartTab.setText(INTEGRATION_TAB_TITLE);
                    integrationPartTab.setImage(ImageConstants.IMG_16_INTEGRATION);
                    integrationPartTab.setShowClose(false);

                    CTabItem propertiesPartTab = tabFolder.getItem(CHILD_TEST_CASE_PROPERTIES_PART_INDEX);
                    propertiesPartTab.setText(PROPERTIES_TAB_TITLE);
                    propertiesPartTab.setImage(ImageConstants.IMG_16_PROPERTIES);
                    propertiesPartTab.setShowClose(false);
                }

                tabFolder.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        if (tabFolder == null || childTestCasePart == null) {
                            return;
                        }
                        if (tabFolder.getSelectionIndex() == CHILD_TEST_CASE_MANUAL_PART_INDEX
                                && (isScriptChanged || scriptNode == null || !childTestCasePart.isScriptLoaded())) {
                            setScriptContentToManual();
                            return;
                        }

                        if (tabFolder.getSelectionIndex() == CHILD_TEST_CASE_EDITOR_PART_INDEX) {
                            Trackings.trackOpenObject("testCaseScript");
                            if (childTestCasePart.isManualScriptChanged()) {
                                setChildEditorContents(scriptNode);
                            }
                            return;
                        }

                        if (tabFolder.getSelectionIndex() == CHILD_TEST_CASE_VARIABLE_PART_INDEX) {
                            if (dirty.isDirty() && variableEditorLastDirty) {
                                updateVariableManualView();
                            }
                            variableEditorLastDirty = false;
                            Trackings.trackOpenObject("testCaseVariable");
                            variableTab = true;
                            return;
                        }

                        if (tabFolder.getSelectionIndex() == CHILD_TEST_CASE_VARIABLE_EDITOR_PART_INDEX) {
                            if (dirty.isDirty() && !variableEditorLastDirty) {
                                updateVariableScriptView();
                            }
                            variableEditorLastDirty = true;
                            variableTab = false;
                            return;
                        }

                        if (tabFolder.getSelectionIndex() == CHILD_TEST_CASE_PROPERTIES_PART_INDEX) {
                            if (isScriptChanged || scriptNode == null) {
                                setScriptContentToManual();
                            }
                            propertiesPart.loadInput();
                            return;
                        }
                    }

                });
                tabFolder.layout();
            }
            childTestCaseVariablesPart.loadVariables();
            // Initialize editor's content
            updateVariableScriptView();
            childTestCaseIntegrationPart.loadInput();
            initDefaultSelectedPart();
            if (tabFolder.getSelectionIndex() == CHILD_TEST_CASE_MANUAL_PART_INDEX) {
                setScriptContentToManual();
            } else {
                if (scriptNode == null) {
                    scriptNode = new ScriptNodeWrapper();
                }
            }
            propertiesPart.loadInput();
            isInitialized = true;
        }
    }

    private void updateVariableManualView() {
        try {
            childTestCaseVariablesPart
                    .setVariablesFromScriptContent(childTestCaseVariableEditorPart.getScriptContent());
            setInvalidScheme(false);
        } catch (Exception e) {
            setInvalidScheme(true);
        }
    }

    private void updateVariableScriptView() {
        try {
            childTestCaseVariableEditorPart.setScriptContentFrom(childTestCaseVariablesPart.getVariableEntityWrapper());
            setInvalidScheme(false);
        } catch (Exception e) {
            setInvalidScheme(true);
        }
    }

    private void initChildEditorPart(CompatibilityEditor compatibilityEditor) {
        childTestCaseEditorPart = compatibilityEditor;
        groovyEditor = (GroovyEditor) childTestCaseEditorPart.getEditor();
        addFormatAction();
        groovyEditor.getViewer().getDocument().addDocumentListener(new IDocumentListener() {
            @Override
            public void documentChanged(DocumentEvent event) {
                try {
                    if (!childTestCasePart.isManualScriptChanged()) {
                        if (!subPartStack.getSelectedElement().equals(partService.getActivePart())) {
                            setScriptContentToManual();
                        } else {
                            if (!isScriptChanged) {
                                isScriptChanged = true;
                            }
                            childTestCaseEditorPart.getModel().setDirty(true);
                        }

                        editor.showProblems(groovyEditor);
                    }
                    updateDirty();
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }

            }

            @Override
            public void documentAboutToBeChanged(DocumentEvent event) {
                // TODO Auto-generated method stub
            }
        });
        addTestObjectDropListener();
    }

    private void addFormatAction() {
        if (groovyEditor.getAction(StringConstants.PA_ACTION_FORMAT) instanceof KatalonFormatAction) {
            return;
        }

        IAction formatAction = new KatalonFormatAction(groovyEditor.getSite(), FormatKind.FORMAT);
        formatAction.setActionDefinitionId(IJavaEditorActionDefinitionIds.FORMAT);
        groovyEditor.setAction(StringConstants.PA_ACTION_FORMAT, formatAction);
    }

    public void changeOriginalTestCase(TestCaseEntity testCase) {
        originalTestCase = testCase;
        cloneTestCase();
    }

    private void cloneTestCase() {
        testCase = new TestCaseEntity();
        TestCaseEntityUtil.copyTestCaseProperties(originalTestCase, testCase, false);
    }

    public boolean setScriptContentToManual() {
        try {
            if (groovyEditor == null || childTestCasePart == null) {
                return false;
            }
            parsingFailed = false;
            Shell activeShell = Display.getCurrent().getActiveShell();
            final String scriptContent = groovyEditor.getViewer().getDocument().get();
            new ProgressMonitorDialog(activeShell).run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) {
                    // TODO: find a way to calculate progress for parsing groovy script
                    monitor.beginTask(StringConstants.PARSING_SCRIPT_PROGRESS_NAME, IProgressMonitor.UNKNOWN);
                    try {
                        scriptNode = GroovyWrapperParser.parseGroovyScriptIntoNodeWrapper(scriptContent);
                    } catch (GroovyParsingException exception) {
                        parsingFailed = true;
                    } catch (Exception e) {
                        parsingFailed = true;
                        LoggerSingleton.logError(e);
                    }
                    monitor.done();
                }
            });

            if (parsingFailed) {
                MessageDialog.openError(activeShell, StringConstants.ERROR_TITLE,
                        StringConstants.PA_ERROR_MSG_PLS_FIX_ERROR_IN_SCRIPT);
                subPartStack.setSelectedElement(childTestCaseEditorPart.getModel());
                isScriptChanged = true;
                editor.showProblems(groovyEditor);
                return false;
            }

            if (scriptNode == null) {
                scriptNode = new ScriptNodeWrapper();
            }

            childTestCasePart.loadASTNodesToTreeTable(scriptNode);
            isScriptChanged = false;
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
        return false;
    }

    public String getScriptContent() {
        try {
            return groovyEditor.getViewer().getDocument().get();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return StringUtils.EMPTY;
        }
    }

    public List<ASTNode> getAstNodesFromScript() throws Exception {
        if (groovyEditor != null) {
            return GroovyParser.parseGroovyScriptIntoAstNodes(groovyEditor.getViewer().getDocument().get());
        }
        return Collections.emptyList();
    }

    private boolean setChildEditorContents(ScriptNodeWrapper scriptNode) {
        while (groovyEditor.getViewer() == null) {
            // wait for groovy Editor appears
        }

        if (groovyEditor.getViewer() != null) {
            try {
                StringBuilder stringBuilder = new StringBuilder();
                new GroovyWrapperParser(stringBuilder).parseGroovyAstIntoScript(scriptNode);
                groovyEditor.getViewer().getDocument().set(stringBuilder.toString());
                childTestCasePart.setManualScriptChanged(false);
                childTestCaseEditorPart.getModel().setDirty(true);
                updateDirty();
                return true;
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
        return false;
    }

    private void initListeners() {
        eventBroker.subscribe(EventConstants.TESTCASE_UPDATED, this);
        eventBroker.subscribe(EventConstants.ECLIPSE_EDITOR_CLOSED, this);
        eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, this);

        childPropertyListner = new IPropertyListener() {
            @Override
            public void propertyChanged(Object source, int propId) {
                if (source instanceof GroovyEditor && propId == ISaveablePart.PROP_DIRTY) {
                    editorLastDirty = childTestCaseEditorPart.getModel().isDirty();
                }
            }
        };
    }

    public TestCasePart getChildTestCasePart() {
        return childTestCasePart;
    }

    public void loadTreeTableInput() {
        setScriptContentToManual();
    }

    public MPart getChildManualPart() {
        return childTestCasePart.getMPart();
    }

    public MPart getChildCompatibilityPart() {
        return childTestCaseEditorPart.getModel();
    }

    public GroovyEditor getChildGroovyEditor() {
        return (GroovyEditor) childTestCaseEditorPart.getEditor();
    }

    public MPart getChildVariablesPart() {
        return childTestCaseVariablesPart.getMPart();
    }

    public MPart getChildVariableEditorPart() {
        return childTestCaseVariableEditorPart.getMPart();
    }

    public MPart getChildIntegrationPart() {
        return childTestCaseIntegrationPart.getMPart();
    }

    public MPart getPropertiesPart() {
        return propertiesPart.getMPart();
    }

    public TestCaseEntity getTestCase() {
        return testCase;
    }

    public TestCaseEntity getOriginalTestCase() {
        return originalTestCase;
    }

    private void setInvalidScheme(boolean value) {
        invalidScheme = value;
    }

    @Override
    public void save() throws Exception {
        // If VariableView is switched from VariableEditorView
        // then they are already in sync. If user only interact on VariableView so far
        // then update VariableEditorView (vice versa)
        if (variableTab == true) {
            updateVariableScriptView();
        } else {
            updateVariableManualView();
        }

        if (childTestCasePart.isManualScriptChanged()) {
            setChildEditorContents(scriptNode);
        }

        saveTestScript();
        saveTestCase();
        updateDirty();
    }

    public void addVariables(VariableEntity[] variables) {
        childTestCaseVariablesPart.addVariable(variables);
    }

    public void deleteVariables(List<VariableEntity> variables) {
        childTestCaseVariablesPart.deleteVariables(variables);
    }

    public VariableEntity[] getVariables() {
        return childTestCaseVariablesPart.getVariables();
    }

    private boolean validateInput() {
        IStatus status = ResourcesPlugin.getWorkspace().validateName(testCase.getName(), IResource.FOLDER);
        if (status.isOK()) {
            return childTestCaseVariablesPart.validateVariables();
        } else {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    status.getMessage());
            return false;
        }
    }

    public boolean saveTestCase() throws Exception {
        if (validateInput()) {
            // preSave
            List<VariableEntity> variableList = testCase.getVariables();
            variableList.clear();
            variableList.addAll(childTestCaseVariablesPart.getVariablesList());
            propertiesPart.preSave();

            childTestCaseIntegrationPart.getEditingIntegrated().entrySet().forEach(entry -> {
                testCase.updateIntegratedEntity(entry.getValue());
            });
            // back-up
            String oldPk = originalTestCase.getId();
            String oldIdForDisplay = originalTestCase.getIdForDisplay();
            TestCaseEntity temp = new TestCaseEntity();
            TestCaseEntityUtil.copyTestCaseProperties(originalTestCase, temp, false);
            TestCaseEntityUtil.copyTestCaseProperties(testCase, originalTestCase, false);
            try {
                boolean nameChanged = !originalTestCase.getName().equals(temp.getName());
                if (nameChanged) {
                    GroovyUtil.loadScriptContentIntoTestCase(temp);
                    originalTestCase.setScriptContents(temp.getScriptContents());
                    temp.setScriptContents(originalTestCase.getScriptContents());
                }
                TestCaseController.getInstance().updateTestCase(originalTestCase);
                // Send event if Test Case name has changed
                if (nameChanged) {
                    eventBroker.post(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM,
                            new Object[] { oldIdForDisplay, originalTestCase.getIdForDisplay() });

                }

                // refresh TreeExplorer
                TestCaseTreeEntity testCaseTreeEntity = TreeEntityUtil.getTestCaseTreeEntity(originalTestCase,
                        ProjectController.getInstance().getCurrentProject());
                eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, testCaseTreeEntity);

                // raise Event to update Test Suite Part and others Test Case
                // Part
                // which refer to test case
                eventBroker.post(EventConstants.TESTCASE_UPDATED, new Object[] { oldPk, originalTestCase });

                EventUtil.send(EventConstants.PROPERTIES_ENTITY, null);
                EventUtil.send(EventConstants.PROPERTIES_ENTITY, originalTestCase);

                originalTestCase.setScriptContents(new byte[0]);
                temp.setScriptContents(new byte[0]);
                
                childTestCaseIntegrationPart.onSaveSuccess(originalTestCase);
                return true;
            } catch (Exception e) {
                // revert
                TestCaseEntityUtil.copyTestCaseProperties(temp, originalTestCase);
                originalTestCase.setScriptContents(temp.getScriptContents());

                LoggerSingleton.logError(e);
                
                childTestCaseIntegrationPart.onSaveFailure(e);
                MessageDialog.openWarning(Display.getCurrent().getActiveShell(), StringConstants.WARN_TITLE,
                        e.getMessage());
            }
        }
        return false;
    }

    public boolean saveTestScript() {
        try {
            groovyEditor.doSave(null);
            return true;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MessageDialog.openError(parentShell, StringConstants.ERROR_TITLE, e.getMessage());
        }
        return false;
    }

    public void setDirty(boolean isDirty) {
        dirty.setDirty(isDirty);
    }

    public void updateDirty() {
        updateDirty(isAnyChildDirty());
    }

    private void updateDirty(boolean isDirty) {
        dirty.setDirty(isDirty);
        childTestCasePart.getMPart().setDirty(false);
        childTestCaseVariablesPart.getMPart().setDirty(false);
        childTestCaseVariableEditorPart.getMPart().setDirty(false);
        childTestCaseEditorPart.getModel().setDirty(false);
        childTestCaseIntegrationPart.getMPart().setDirty(false);
        getPropertiesPart().setDirty(false);
    }

    private boolean isAnyChildDirty() {
        return childTestCasePart.getMPart().isDirty() || childTestCaseEditorPart.getModel().isDirty()
                || childTestCaseVariablesPart.getMPart().isDirty()
                || childTestCaseVariableEditorPart.getMPart().isDirty()
                || childTestCaseIntegrationPart.getMPart().isDirty() || propertiesPart.isDirty();
    }

    @Persist
    public void onSave() {
        try {
            save();
        } catch (Exception e) {
            MessageDialog.openError(null, StringConstants.ERROR_TITLE,
                    StringConstants.PA_ERROR_MSG_UNABLE_TO_SAVE_PART);
            LoggerSingleton.logError(e);
        }
    }

    public IPropertyListener getChildPropertyListner() {
        return childPropertyListner;
    }

    @Override
    public void handleEvent(Event event) {
        if (event.getTopic().equals(EventConstants.TESTCASE_UPDATED)) {
            try {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof Object[]) {
                    String elementId = EntityPartUtil.getTestCaseCompositePartId((String) ((Object[]) object)[0]);
                    if (elementId.equalsIgnoreCase(compositePart.getElementId())) {
                        TestCaseEntity testCase = (TestCaseEntity) ((Object[]) object)[1];
                        changeOriginalTestCase(testCase);
                        updatePart(testCase);
                    }
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        } else if (event.getTopic().equals(EventConstants.ECLIPSE_EDITOR_CLOSED)) {
            try {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object != null && object instanceof GroovyEditor && object.equals(groovyEditor)) {
                    editor.clearEditorProblems(groovyEditor);
                    if (!editorLastDirty) {
                        if (partService.savePart(compositePart, false)) {
                            partService.hidePart(compositePart);
                        }
                    } else {
                        partService.hidePart(compositePart, true);
                    }
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        } else if (event.getTopic().equals(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM)) {
            try {
                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (object instanceof TestCaseTreeEntity) {
                    handleTestCaseRefreshed(((TestCaseTreeEntity) object).getObject());
                } else if (object instanceof FolderTreeEntity) {
                    handleFolderRefreshed(((FolderTreeEntity) object).getObject());
                }
            } catch (Exception e) {
                LoggerSingleton.logError(e);
            }
        }
    }

    private void handleTestCaseRefreshed(TestCaseEntity testCase) throws Exception {
        TestCaseEntity currentTestCase = getTestCase();
        if (testCase == null || !testCase.getId().equals(currentTestCase.getId())) {
            return;
        }
        if (isTestCaseDisposed(testCase)) {
            dispose();
            return;
        }
        refresh(testCase);
    }

    private void refresh(TestCaseEntity testCase) throws Exception {
        if (dirty.isDirty()) {
            // do not refresh the modifying test case(s)
            return;
        }
        changeOriginalTestCase(testCase);
        TestCaseTreeTableInput treeTableInput = childTestCasePart.getTreeTableInput();
        if (treeTableInput != null) {
            treeTableInput.reloadTestCaseVariables(childTestCasePart.getVariables());
        }
        updatePart(testCase);
        childTestCaseIntegrationPart.loadInput();
        propertiesPart.loadInput();
        updateDirty(false);
        clearChildsUndoRedoHistory();
    }

    private void clearChildsUndoRedoHistory() {
        childTestCasePart.clearHistory();
        childTestCaseVariablesPart.clearHistory();
        propertiesPart.clearHistory();
    }

    private boolean isTestCaseDisposed(TestCaseEntity testCase) throws Exception {
        return TestCaseController.getInstance().getTestCase(testCase.getId()) == null;
    }

    private void handleFolderRefreshed(FolderEntity folder) throws Exception {
        TestCaseEntity currentTestCase = getTestCase();
        if (folder != null && FolderController.getInstance().isFolderAncestorOfEntity(folder, currentTestCase)
                && isTestCaseDisposed(currentTestCase)) {
            dispose();
        }
    }

    private void dispose() {
        MPartStack mStackPart = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
        mStackPart.getChildren().remove(compositePart);
        eventBroker.unsubscribe(this);
        disposed = true;
    }

    public boolean isDisposed() {
        return disposed;
    }

    @PreDestroy
    public void preDestroy() {
        try {
            if (groovyEditor != null && getChildCompatibilityPart() != null) {
                editor.clearEditorProblems(groovyEditor);
            }

            dispose();
        } catch (CoreException e) {
            LoggerSingleton.logError(e);
        }
    }

    private void updatePart(TestCaseEntity testCase) throws Exception {
        String newCompositePartId = EntityPartUtil.getTestCaseCompositePartId(testCase.getId());
        changeOriginalTestCase(testCase);
        if (!newCompositePartId.equals(compositePart.getElementId())) {

            compositePart.setLabel(testCase.getName());
            compositePart.setElementId(newCompositePartId);

            if (compositePart.getChildren().size() == 1 && compositePart.getChildren().get(0) instanceof MPartStack) {
                MPartStack partStack = (MPartStack) compositePart.getChildren().get(0);
                partStack.setElementId(newCompositePartId + IdConstants.TEST_CASE_SUB_PART_STACK_ID_SUFFIX);

                childTestCasePart.getMPart()
                        .setElementId(newCompositePartId + IdConstants.TEST_CASE_GENERAL_PART_ID_SUFFIX);
                childTestCaseVariablesPart.getMPart()
                        .setElementId(newCompositePartId + IdConstants.TEST_CASE_VARIABLES_PART_ID_SUFFIX);
                getPropertiesPart().setElementId(newCompositePartId + IdConstants.TEST_CASE_PROPERTIES_PART_ID_SUFFIX);

                childTestCaseVariableEditorPart.getMPart()
                        .setElementId(newCompositePartId + IdConstants.TEST_CASE_VARIABLE_EDITOR_PART_ID_SUFFIX);

                partService.hidePart(getChildCompatibilityPart(), true);
                String testCaseEditorId = newCompositePartId + IdConstants.TEST_CASE_EDITOR_PART_ID_SUFFIX;
                MPart editorPart = editor.createTestCaseEditorPart(
                        ResourcesPlugin.getWorkspace().getRoot().getFile(
                                GroovyGuiUtil.getOrCreateGroovyScriptForTestCase(testCase).getPath()),
                        partStack, testCaseEditorId, partService, CHILD_TEST_CASE_EDITOR_PART_INDEX);
                partService.activate(editorPart);
                initComponent();
                partStack.setSelectedElement(getChildManualPart());
                setScriptContentToManual();
                childTestCaseEditorPart.getEditor().addPropertyListener(getChildPropertyListner());
                updateDirty();
            }
        }
        boolean isAnyChildDirty = isAnyChildDirty();

        // refresh child parts
        childTestCaseVariablesPart.loadVariables();
        childTestCaseVariableEditorPart.setScriptContentFrom(childTestCaseVariablesPart.getVariableEntityWrapper());
        if (childTestCasePart.getTreeTableInput() == null) {
            setScriptContentToManual();
        }
        childTestCasePart.getTreeTableInput().reloadTestCaseVariables(childTestCasePart.getVariables());
        childTestCaseIntegrationPart.reloadInput();

        updateDirty();
        setDirty(isAnyChildDirty);
    }

    public MPart getSelectedPart() {
        return (MPart) subPartStack.getSelectedElement();
    }

    public void setSelectedPart(MPart partToSelect) {
        if (subPartStack.getChildren().contains(partToSelect)) {
            subPartStack.setSelectedElement(partToSelect);
            if (partToSelect == getChildManualPart() && (isScriptChanged || scriptNode == null)) {
                setScriptContentToManual();
            } else if (partToSelect == getChildCompatibilityPart() && childTestCasePart.isManualScriptChanged()) {
                setChildEditorContents(scriptNode);
            }

        }
    }

    public void refreshScript() {
        if (!isInitialized) {
            return;
        }

        if (subPartStack.getSelectedElement() == getChildCompatibilityPart()) {
            setChildEditorContents(scriptNode);
        }
    }

    @Override
    public List<MPart> getChildParts() {
        List<MPart> childrenParts = new ArrayList<MPart>();
        childrenParts.add(getChildManualPart());
        childrenParts.add(getChildCompatibilityPart());
        childrenParts.add(getChildVariablesPart());
        childrenParts.add(getChildVariableEditorPart());
        childrenParts.add(getChildIntegrationPart());
        childrenParts.add(getPropertiesPart());
        return childrenParts;
    }

    public boolean isTestCaseEmpty() {
        return GroovyWrapperParser
                .parseGroovyScriptAndGetFirstStatement(groovyEditor.getViewer().getDocument().get()) == null;
    }

    public void validateScriptErrors() throws Exception {
        try {
            GroovyWrapperParser.parseGroovyScriptIntoNodeWrapper(groovyEditor.getViewer().getDocument().get());
        } catch (Exception e) {
            editor.showProblems(groovyEditor);
            throw e;
        }
    }

    private void addTestObjectDropListener() {
        Control control = (Control) groovyEditor.getAdapter(Control.class);
        if (!(control instanceof StyledText)) {
            return;
        }
        DropTarget dropTarget = null;
        Object existingDropTarget = control.getData(DND.DROP_TARGET_KEY);
        if (existingDropTarget != null) {
            dropTarget = (DropTarget) existingDropTarget;
        } else {
            dropTarget = new DropTarget(control, DND.DROP_COPY);
        }
        Transfer[] transfers = dropTarget.getTransfer();
        List<Transfer> treeEntityTransfers = TransferTypeCollection.getInstance().getTreeEntityTransfer();
        if (transfers.length != 0) {
            treeEntityTransfers.addAll(Arrays.asList(transfers));
        }
        dropTarget.setTransfer(treeEntityTransfers.toArray(new Transfer[treeEntityTransfers.size()]));
        dropTarget.addDropListener(new TestObjectScriptDropListener(groovyEditor.getViewer().getTextWidget()));
    }

    public MCompositePart getCompositePart() {
        return compositePart;
    }

    @Override
    public String getEntityId() {
        return getTestCase().getIdForDisplay();
    }

    @Override
    @Inject
    @Optional
    public void onSelect(@UIEventTopic(UIEvents.UILifeCycle.BRINGTOTOP) Event event) {
        if (originalTestCase == null) {
            return;
        }
        MPart part = EventUtil.getPart(event);
        if (part == null || !StringUtils.startsWith(part.getElementId(),
                EntityPartUtil.getTestCaseCompositePartId(originalTestCase.getId()))) {
            return;
        }

        EventUtil.post(EventConstants.PROPERTIES_ENTITY, originalTestCase);
    }

    @Override
    @Inject
    @Optional
    public void onChangeEntityProperties(@UIEventTopic(EventConstants.PROPERTIES_ENTITY_UPDATED) Event event) {
        Object eventData = EventUtil.getData(event);
        if (!(eventData instanceof TestCaseEntity)) {
            return;
        }

        TestCaseEntity updatedEntity = (TestCaseEntity) eventData;
        if (!StringUtils.equals(updatedEntity.getIdForDisplay(), getEntityId())) {
            return;
        }
        String newTag = updatedEntity.getTag();
        String newDescription = updatedEntity.getDescription();
        testCase.setTag(newTag);
        testCase.setDescription(newDescription);
        originalTestCase.setTag(newTag);
        originalTestCase.setDescription(newDescription);
        propertiesPart.loadInput();
    }

    @Override
    @PreDestroy
    public void onClose() {
        EventUtil.post(EventConstants.PROPERTIES_ENTITY, null);
    }

    public void setInvalidSchemeDetected(boolean b) {
        this.invalidScheme = b;
    }

    public CTabFolder getTabFolder() {
        return tabFolder;
    }

    @Override
    public boolean isDirty() {
        return compositePart.isDirty();
    }
    
    public void changeScriptNode(ScriptNodeWrapper scriptNode) {
        this.scriptNode = scriptNode;
    }
}
