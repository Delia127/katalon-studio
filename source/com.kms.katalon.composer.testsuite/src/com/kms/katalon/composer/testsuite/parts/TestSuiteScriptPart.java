package com.kms.katalon.composer.testsuite.parts;

import org.codehaus.groovy.eclipse.editor.GroovyEditor;
import org.codehaus.groovy.eclipse.refactoring.actions.FormatKind;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.part.SavableCompositePart;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.testcase.actions.KatalonFormatAction;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.util.groovy.GroovyEditorUtil;

@SuppressWarnings("restriction")
public class TestSuiteScriptPart {

    private CompatibilityEditor editorPart;

    private GroovyEditor groovyEditor;

    private ShowProblemAction showProblemAction;

    private SavableCompositePart parentPart;

    public TestSuiteScriptPart(SavableCompositePart parentPart, CompatibilityEditor editorPart) {
        this.parentPart = parentPart;
        this.editorPart = editorPart;
        this.groovyEditor = (GroovyEditor) editorPart.getEditor();
    }
    
    public MPart getMPart() {
        return editorPart.getModel();
    }

    public void initEditorAction() {
        addFormatAction();

        addSyntaxCheckingAction();
    }

    private void addSyntaxCheckingAction() {
        this.showProblemAction = new ShowProblemAction(groovyEditor);

        groovyEditor.getViewer().getDocument().addDocumentListener(new IDocumentListener() {
            @Override
            public void documentChanged(DocumentEvent event) {
                showProblemAction.startAction();
                
                getMPart().setDirty(true);
                parentPart.setDirty(true);
            }

            @Override
            public void documentAboutToBeChanged(DocumentEvent event) {
            }
        });
    }

    private void addFormatAction() {
        if (groovyEditor.getAction(StringConstants.PA_ACTION_FORMAT) instanceof KatalonFormatAction) {
            return;
        }

        IAction formatAction = new KatalonFormatAction(groovyEditor.getSite(), FormatKind.FORMAT);
        formatAction.setActionDefinitionId(IJavaEditorActionDefinitionIds.FORMAT);
        groovyEditor.setAction(StringConstants.PA_ACTION_FORMAT, formatAction);
    }
    
    public void save() {
        groovyEditor.doSave(new NullProgressMonitor());
    }

    private class ShowProblemAction {
        private static final long MAX_WAITING_TIME = 200L;

        private static final long TICK = 50L;

        private long countdown;

        private Thread showProblemThread;

        private GroovyEditor editor;

        private Thread createShowProblemThread() {
            return new Thread(() -> {
                while (countdown < MAX_WAITING_TIME) {
                    try {
                        Thread.sleep(TICK);
                    } catch (InterruptedException ignored) {}
                    countdown += TICK;
                }
                UISynchronizeService.syncExec(() -> {
                    try {
                        GroovyEditorUtil.showProblems(editor);
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
                    }
                });
            });
        }

        public ShowProblemAction(GroovyEditor editor) {
            this.editor = editor;
        }

        public void startAction() {
            countdown = 0L;
            if (showProblemThread == null || !showProblemThread.isAlive()) {
                showProblemThread = createShowProblemThread();
                showProblemThread.start();
            }
        }

        public void endAction() {
            if (showProblemThread != null && showProblemThread.isAlive()) {
                try {
                    showProblemThread.wait();
                } catch (InterruptedException ignored) {}
            }
        }
    }
}
