package com.kms.katalon.composer.testcase.menu;

import java.util.List;

import org.codehaus.groovy.eclipse.editor.GroovyEditor;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.PartServiceSingleton;
import com.kms.katalon.composer.testcase.constants.ComposerTestcaseMessageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.exceptions.GroovyParsingException;
import com.kms.katalon.composer.testcase.groovy.ast.ASTHasBlock;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.MethodNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ScriptNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ComplexChildStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ComplexStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.model.ExecuteFromTestStepEntity;
import com.kms.katalon.composer.testcase.parts.TestCaseCompositePart;
import com.kms.katalon.composer.testcase.providers.AstTestScriptGeneratorProvider;
import com.kms.katalon.composer.testcase.util.TestCaseMenuUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.execution.session.ExecutionSession;

@SuppressWarnings("restriction")
public class ExecuteFromTestStepContributionItem extends ContributionItem implements SelectionListener {

    public ExecuteFromTestStepContributionItem() {
        super();
    }

    public ExecuteFromTestStepContributionItem(String id) {
        super(id);
    }

    @Override
    public void fill(Menu menu, int index) {
        if (findCompositeParentPart(getActiveGroovyEditor()) == null) {
            return;
        }
        TestCaseMenuUtil.generateExecuteFromTestStepSubMenu(menu, this, index);
    }

    private void executeFromTestStep(ExecutionSession executionSession) {
        GroovyEditor groovyEditor = getActiveGroovyEditor();
        if (groovyEditor == null) {
            return;
        }
        TestCaseCompositePart testCaseCompositePart = findCompositeParentPart(groovyEditor);
        if (testCaseCompositePart == null) {
            return;
        }
        String rawScript = generateRawScriptFromSelectedStep(groovyEditor);
        if (rawScript == null) {
            return;
        }
        ExecuteFromTestStepEntity executeFromTestStepEntity = new ExecuteFromTestStepEntity();
        executeFromTestStepEntity.setDriverTypeName(executionSession.getDriverTypeName());
        executeFromTestStepEntity.setRawScript(rawScript);
        executeFromTestStepEntity.setRemoteServerUrl(executionSession.getRemoteUrl());
        executeFromTestStepEntity.setTestCase(testCaseCompositePart.getTestCase());
        executeFromTestStepEntity.setSessionId(executionSession.getSessionId());
        EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.EXECUTE_FROM_TEST_STEP,
                executeFromTestStepEntity);
    }

    protected GroovyEditor getActiveGroovyEditor() {
        IEditorPart activeEditor = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow()
                .getActivePage()
                .getActiveEditor();
        if (!(activeEditor instanceof GroovyEditor)) {
            return null;
        }
        return (GroovyEditor) activeEditor;
    }

    private String generateRawScriptFromSelectedStep(GroovyEditor groovyEditor) {
        TextViewer viewer = (TextViewer) groovyEditor.getViewer();
        ITextSelection textSelection = (ITextSelection) viewer.getSelection();
        final String scriptContent = viewer.getDocument().get();
        try {
            ScriptNodeWrapper scriptNode = GroovyWrapperParser.parseGroovyScriptIntoNodeWrapper(scriptContent, null);
            StatementWrapper selectedStatement = findSelectedStatement(textSelection.getStartLine() + 1,
                    textSelection.getEndLine() + 1, scriptNode);
            if (selectedStatement == null) {
                MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                        ComposerTestcaseMessageConstants.ERR_MSG_UNABLE_TO_EXECUTE_FROM_TEST_STEP_STEP_NOT_SELECTED);
                return null;
            }
            return AstTestScriptGeneratorProvider.generateScriptForExecuteFromTestStep(scriptNode, selectedStatement);
        } catch (GroovyParsingException e) {
            LoggerSingleton.logError(e);
        }
        return null;
    }

    private StatementWrapper findSelectedStatement(int startLine, int endLine, ScriptNodeWrapper scriptNode) {
        MethodNodeWrapper resultMethod = null;
        for (MethodNodeWrapper method : scriptNode.getMethods()) {
            if (isNodeWrapperSelected(startLine, method)) {
                resultMethod = method;
            }
        }
        if (resultMethod == null) {
            return null;
        }
        return findSelectedStatement(startLine, resultMethod);
    }

    protected StatementWrapper findSelectedStatement(int startLine, ASTHasBlock astBlock) {
        if (astBlock == null) {
            return null;
        }
        StatementWrapper bestFit = null;
        List<StatementWrapper> statements = astBlock.getBlock().getStatements();
        for (StatementWrapper statement : statements) {
            if (statement.getLineNumber() >= startLine && bestFit == null) {
                bestFit = statement;
            }
            if (isNodeWrapperSelected(startLine, statement)) {
                if (!isChildNodeSelected(startLine, statement)) {
                    return statement;
                }
                if (statement instanceof ComplexStatementWrapper) {
                    return findSelectedElementInComplexStatement(startLine, (ComplexStatementWrapper<?, ?>) statement);
                }
                StatementWrapper childStatement = findSelectedStatement(startLine, (ASTHasBlock) statement);
                if (childStatement != null) {
                    return childStatement;
                }
                return statement;
            }
        }
        return bestFit;
    }

    protected StatementWrapper findSelectedElementInComplexStatement(int startLine,
            ComplexStatementWrapper<?, ?> complexStatement) {
        for (ComplexChildStatementWrapper complexChildStatement : complexStatement.getComplexChildStatements()) {
            StatementWrapper childStatement = findSelectedStatement(startLine, complexChildStatement);
            if (childStatement != null) {
                return childStatement;
            }
        }
        StatementWrapper childStatement = findSelectedStatement(startLine, complexStatement.getLastStatement());
        if (childStatement != null) {
            return childStatement;
        }
        childStatement = findSelectedStatement(startLine, (ASTHasBlock) complexStatement);
        if (childStatement != null) {
            return childStatement;
        }
        return complexStatement;
    }

    protected boolean isChildNodeSelected(int startLine, StatementWrapper statement) {
        return (statement instanceof ASTHasBlock || statement instanceof ComplexStatementWrapper)
                && isNodeWrapperContainLine(startLine, statement);
    }

    private static boolean isNodeWrapperSelected(int lineNumber, ASTNodeWrapper node) {
        return node.getLineNumber() == lineNumber || isNodeWrapperContainLine(lineNumber, node)
                || node.getLastLineNumber() == lineNumber;
    }

    private static boolean isNodeWrapperContainLine(int lineNumber, ASTNodeWrapper node) {
        return node.getLineNumber() < lineNumber && node.getLastLineNumber() > lineNumber;
    }

    private TestCaseCompositePart findCompositeParentPart(GroovyEditor groovyEditor) {
        if (groovyEditor == null) {
            return null;
        }
        EPartService partService = PartServiceSingleton.getInstance().getPartService();
        for (MPart dirtyPart : partService.getParts()) {
            if (!(dirtyPart.getObject() instanceof TestCaseCompositePart)) {
                continue;
            }
            TestCaseCompositePart compositePart = (TestCaseCompositePart) dirtyPart.getObject();
            if (groovyEditor.equals(compositePart.getChildGroovyEditor())) {
                return compositePart;
            }
        }
        return null;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        Object item = e.getSource();
        if (!(item instanceof MenuItem)) {
            return;
        }
        MenuItem menuItem = (MenuItem) item;
        if (!(menuItem.getData() instanceof ExecutionSession)) {
            return;
        }
        executeFromTestStep((ExecutionSession) menuItem.getData());
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        widgetSelected(e);
    }
}
