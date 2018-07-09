package com.kms.katalon.composer.execution.debug;

import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.eclipse.editor.GroovyEditor;
import org.codehaus.groovy.eclipse.editor.GroovyExtraInformationHover;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.ui.IDebugEditorPresentation;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.ui.JDIModelPresentation;
import org.eclipse.jdt.ui.text.java.hover.IJavaEditorTextHover;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.util.TestCaseEditorUtil;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.execution.logging.LogExceptionFilter;

@SuppressWarnings("restriction")
public class DebugBreakpointView extends JDIModelPresentation implements IDebugEditorPresentation {
    private static final String DBG_STRING_TYPE_NAME = "org.eclipse.jdt.debug.core.typeName";

    private static final String DBG_STRING_LINE_NUMBER = "lineNumber";

    public static final String DBG_COMMAND_SUSPEND = "org.eclipse.debug.ui.commands.Suspend";

    public static final String DBG_COMMAND_RESUME = "org.eclipse.debug.ui.commands.Resume";

    @Override
    public IEditorInput getEditorInput(Object element) {
        try {
            if (element instanceof IFile) {
                IFile file = (IFile) element;
                return new FileEditorInput(file);
            }

            if (element instanceof Breakpoint) {
                // open by clicking on item in BreakPointView
                Breakpoint breakpoint = (Breakpoint) element;
                IMarker marker = breakpoint.getMarker();

                Map<String, Object> attributes = marker.getAttributes();
                String className = (String) attributes.get(DBG_STRING_TYPE_NAME);
                if (LogExceptionFilter.isTestCaseScript(className)) {
                    AbstractTextEditor editor = TestCaseEditorUtil.getTestCaseEditorByScriptName(className);
                    if (editor != null) {
                        IRegion region = goToLine(editor, (int) attributes.get(DBG_STRING_LINE_NUMBER));
                        if (region != null) {
                            editor.selectAndReveal(region.getOffset(), region.getLength());
                        }
                    }
                    // return null because Eclipse always opens default editor.
                    return null;
                }
            }
        } catch (Exception ignored) {
            // Let super do this
        }
        return super.getEditorInput(element);
    }

    @Override
    public String getEditorId(IEditorInput input, Object element) {
        try {
            IEditorDescriptor descriptor = IDE.getEditorDescriptor(input.getName(), true, true);

            return (descriptor != null) ? descriptor.getId() : "";
        } catch (PartInitException e) {
            return null;
        }
    }

    @Override
    public String getText(Object element) {
        String parentText = super.getText(element);
        try {
            if (element instanceof Breakpoint) {
                // for BreakpointView
                return getTextFromBreakPoint((Breakpoint) element);
            }

            if (element instanceof JDIStackFrame) {
                return getTextFromJDIStackFrame((JDIStackFrame) element, parentText);
            }

            if (element instanceof IThread) {
                JDIStackFrame stackFrame = ((JDIStackFrame) ((IThread) element).getTopStackFrame());
                return getTextFromJDIStackFrame(stackFrame, parentText);
            }
        } catch (Exception e) {
            // Eclipse's bug, just ignore it
        }
        return parentText;
    }

    private String getTextFromBreakPoint(Breakpoint element) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        IMarker marker = element.getMarker();

        Map<String, Object> attributes = marker.getAttributes();
        String className = (String) attributes.get(DBG_STRING_TYPE_NAME);
        String testCaseId = getTestCaseIdByClassName(className);
        if (!testCaseId.isEmpty()) {
            stringBuilder.append(testCaseId);
        } else {
            stringBuilder.append(className);
        }
        int lineNumber = (int) attributes.get(DBG_STRING_LINE_NUMBER);
        stringBuilder.append(" [line:").append(Integer.toString(lineNumber)).append("]");
        return stringBuilder.toString();
    }

    private String getTextFromJDIStackFrame(JDIStackFrame element, String parentText) throws Exception {
        String className = element.getDeclaringTypeName();
        String testCaseId = getTestCaseIdByClassName(className);

        return StringUtils.isNotEmpty(testCaseId) ? parentText.replace(className, testCaseId) : parentText;
    }

    private String getTestCaseIdByClassName(String className) throws Exception {
        TestCaseEntity testCase = null;
        if (LogExceptionFilter.isTestCaseScript(className)) {
            testCase = TestCaseController.getInstance().getTestCaseByScriptName(className);
        }
        return testCase != null ? testCase.getIdForDisplay() : "";
    }

    private IRegion goToLine(IEditorPart editorPart, int lineNumber) {
        if (!(editorPart instanceof ITextEditor) || lineNumber <= 0) {
            return null;
        }
        ITextEditor editor = (ITextEditor) editorPart;
        IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());

        try {
            return document != null ? document.getLineInformation(lineNumber - 1) : null;
        } catch (BadLocationException e) {
            return null;
        }
    }

    @Override
    public boolean addAnnotations(IEditorPart editorPart, IStackFrame frame) {
        if (!(editorPart instanceof GroovyEditor)) {
            return false;
        }

        addTextHover(editorPart, new DebugTextHover());
        return false;
    }

    @Override
    public void removeAnnotations(IEditorPart editorPart, IThread thread) {
        if (!(editorPart instanceof GroovyEditor)) {
            return;
        }

        addTextHover(editorPart, new GroovyExtraInformationHover(true));
    }

    private void addTextHover(IEditorPart editorPart, IJavaEditorTextHover textHover) {
        if (!(editorPart instanceof GroovyEditor)) {
            return;
        }
        String className = FilenameUtils.getBaseName(editorPart.getEditorInput().getName());

        try {
            String testCaseId = getTestCaseIdByClassName(className);

            if (StringUtils.isNotEmpty(testCaseId)) {
                GroovyEditor groovyEditor = (GroovyEditor) editorPart;

                TextViewer textViewer = (TextViewer) groovyEditor.getViewer();
                textHover.setEditor(groovyEditor);
                textViewer.setTextHover(textHover, "__dftl_partition_content_type", 0);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }
}
