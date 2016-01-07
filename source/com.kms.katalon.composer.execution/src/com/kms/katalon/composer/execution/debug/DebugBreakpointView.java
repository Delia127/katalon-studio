package com.kms.katalon.composer.execution.debug;

import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.ui.IDebugEditorPresentation;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.ui.JDIModelPresentation;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.composer.execution.trace.LogExceptionNavigator;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.execution.logging.LogExceptionFilter;

@SuppressWarnings("restriction")
public class DebugBreakpointView extends JDIModelPresentation implements IDebugEditorPresentation {

    @Override
    public IEditorInput getEditorInput(Object element) {
        try {
            if (element instanceof IFile) {
                IFile file = (IFile) element;
                String fileName = FilenameUtils.getBaseName(file.getName());
                if (LogExceptionFilter.isTestCaseScript(fileName)) {
                    TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByScriptName(fileName);
                    if (testCase != null) {
                        return getTestScriptEditor(testCase).getEditorInput();
                    }
                }

            } else if (element instanceof Breakpoint) {
                // open by clicking on item in BreakPointView
                Breakpoint breakpoint = (Breakpoint) element;
                IMarker marker = breakpoint.getMarker();

                Map<String, Object> attributes = marker.getAttributes();
                String className = (String) attributes.get(StringConstants.DBG_STRING_TYPE_NAME);
                if (LogExceptionFilter.isTestCaseScript(className)) {
                    TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByScriptName(className);
                    if (testCase != null) {
                        AbstractTextEditor editor = getTestScriptEditor(testCase);

                        goToLine(editor, (int) attributes.get(StringConstants.DBG_STRING_LINE_NUMBER));

                        // return null because Eclipse always opens default
                        // editor.
                        return null;
                    }
                }

            }
        } catch (Exception e) {

        }
        return super.getEditorInput(element);
    }

    @Override
    public String getEditorId(IEditorInput input, Object element) {
        try {
            IEditorDescriptor descriptor = IDE.getEditorDescriptor(input.getName());

            String id = descriptor.getId();

            return id;
        } catch (PartInitException e) {
            return null;
        }
    }

    @Override
    public String getText(Object element) {
        try {
            if (element instanceof Breakpoint) {
                // for BreakpointView
                StringBuilder stringBuilder = new StringBuilder();
                Breakpoint breakpoint = (Breakpoint) element;
                IMarker marker = breakpoint.getMarker();

                Map<String, Object> attributes = marker.getAttributes();
                String className = (String) attributes.get(StringConstants.DBG_STRING_TYPE_NAME);
                String testCaseId = getTestCaseIdByClassName(className);
                if (!testCaseId.isEmpty()) {
                    stringBuilder.append(testCaseId);
                } else {
                    stringBuilder.append(className);
                }
                int lineNumber = (int) attributes.get(StringConstants.DBG_STRING_LINE_NUMBER);
                stringBuilder.append(" [line:").append(Integer.toString(lineNumber)).append("]");
                return stringBuilder.toString();
            } else if (element instanceof JDIStackFrame) {
                JDIStackFrame stackFrame = (JDIStackFrame) element;
                String className = stackFrame.getDeclaringTypeName();
                String testCaseId = getTestCaseIdByClassName(className);
                if (testCaseId != null && !testCaseId.isEmpty()) {
                    String parentText = super.getText(element);
                    return parentText.replace(className, testCaseId);
                } else {
                    return super.getText(element);
                }
            } else if (element instanceof IThread) {
                IThread iThread = (IThread) element;
                JDIStackFrame stackFrame = (JDIStackFrame) iThread.getTopStackFrame();
                String className = stackFrame.getDeclaringTypeName();
                String testCaseId = getTestCaseIdByClassName(className);

                if (testCaseId != null && !testCaseId.isEmpty()) {
                    String parentText = super.getText(element);
                    return parentText.replace(className, testCaseId);
                }
            }
        } catch (Exception e) {
            // Eclipse's bug, just ignore it
        }
        String debugText = super.getText(element);
        return debugText;
    }

    private String getTestCaseIdByClassName(String className) throws Exception {
        if (LogExceptionFilter.isTestCaseScript(className)) {
            TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByScriptName(className);
            if (testCase != null) {
                return testCase.getIdForDisplay();
            }
        }
        return "";
    }

    private static void goToLine(IEditorPart editorPart, int lineNumber) {
        if (!(editorPart instanceof ITextEditor) || lineNumber <= 0) {
            return;
        }
        ITextEditor editor = (ITextEditor) editorPart;
        IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
        if (document != null) {
            IRegion lineInfo = null;
            try {
                // line count internally starts with 0, and not with 1 like in
                // GUI
                lineInfo = document.getLineInformation(lineNumber - 1);
            } catch (BadLocationException e) {
                // ignored because line number may not really exist in document,
                // we guess this...
            }
            if (lineInfo != null) {
                editor.selectAndReveal(lineInfo.getOffset(), lineInfo.getLength());
            }
        }
    }

    @Override
    public boolean addAnnotations(IEditorPart editorPart, IStackFrame frame) {
        try {
            String className = "";
            if (editorPart.getEditorInput() instanceof FileEditorInput) {
                className = FilenameUtils.getBaseName(editorPart.getEditorInput().getName());
            } else if (frame instanceof JDIStackFrame) {
                JDIStackFrame jdiStackFrame = (JDIStackFrame) frame;
                className = FilenameUtils.getBaseName(jdiStackFrame.getSourcePath());
            }

            String testCaseId = getTestCaseIdByClassName(className);
            if (testCaseId == null || testCaseId.isEmpty()) return false;

            TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByDisplayId(testCaseId);
            getTestScriptEditor(testCase);
        } catch (Exception e) {
            // If an exception occurs that means test case meta data
            // file(.tc) does not exist.
        }
        return false;
    }

    @Override
    public void removeAnnotations(IEditorPart editorPart, IThread thread) {
        // do nothing
    }

    private AbstractTextEditor getTestScriptEditor(TestCaseEntity testCase) {
        LogExceptionNavigator navigator = new LogExceptionNavigator();
        MPart compabilityEditorPart = navigator.getTestCaseGroovyEditor(testCase);
        CompatibilityEditor compabilityEditor = (CompatibilityEditor) compabilityEditorPart.getObject();
        AbstractTextEditor editor = (AbstractTextEditor) compabilityEditor.getEditor();
        return editor;
    }

}
