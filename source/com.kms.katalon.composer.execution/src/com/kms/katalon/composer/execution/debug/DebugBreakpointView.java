package com.kms.katalon.composer.execution.debug;

import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.eclipse.editor.GroovyEditor;
import org.codehaus.groovy.eclipse.editor.GroovyExtraInformationHover;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.Breakpoint;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.internal.ui.InstructionPointerManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugEditorPresentation;
import org.eclipse.debug.ui.IInstructionPointerPresentation;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.ui.JDIModelPresentation;
import org.eclipse.jdt.ui.text.java.hover.IJavaEditorTextHover;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import com.kms.katalon.composer.execution.trace.LogExceptionNavigator;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.execution.logging.LogExceptionFilter;

@SuppressWarnings("restriction")
public class DebugBreakpointView extends JDIModelPresentation implements IDebugEditorPresentation {
    private static final String DBG_STRING_TYPE_NAME = "org.eclipse.jdt.debug.core.typeName";

    private static final String DBG_STRING_LINE_NUMBER = "lineNumber";

    public static final String DBG_COMMAND_SUSPEND = "org.eclipse.debug.ui.commands.Suspend";

    public static final String DBG_COMMAND_RESUME = "org.eclipse.debug.ui.commands.Resume";

    private IInstructionPointerPresentation presentation = ((IInstructionPointerPresentation) DebugUITools.newDebugModelPresentation());

    @Override
    public IEditorInput getEditorInput(Object element) {
        try {
            if (element instanceof IFile) {
                IFile file = (IFile) element;
                String fileName = FilenameUtils.getBaseName(file.getName());
                if (LogExceptionFilter.isTestCaseScript(fileName)) {
                    AbstractTextEditor editor = getTestCaseEditorByScriptName(fileName);
                    return (editor != null) ? editor.getEditorInput() : null;
                }

                return IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), file)
                        .getEditorInput();
            }

            if (element instanceof Breakpoint) {
                // open by clicking on item in BreakPointView
                Breakpoint breakpoint = (Breakpoint) element;
                IMarker marker = breakpoint.getMarker();

                Map<String, Object> attributes = marker.getAttributes();
                String className = (String) attributes.get(DBG_STRING_TYPE_NAME);
                if (LogExceptionFilter.isTestCaseScript(className)) {
                    AbstractTextEditor editor = getTestCaseEditorByScriptName(className);
                    if (editor != null) {
                        goToLine(editor, (int) attributes.get(DBG_STRING_LINE_NUMBER));
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
            IEditorDescriptor descriptor = IDE.getEditorDescriptor(input.getName());

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
        try {
            String className = FilenameUtils.getBaseName(editorPart.getEditorInput().getName());
            if (frame instanceof JDIStackFrame) {
                JDIStackFrame jdiStackFrame = (JDIStackFrame) frame;
                className = FilenameUtils.getBaseName(jdiStackFrame.getSourcePath());
            }

            String testCaseId = getTestCaseIdByClassName(className);
            if (StringUtils.isNotEmpty(testCaseId)) {
                TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByDisplayId(testCaseId);
                AbstractTextEditor testScriptEditor = getTestScriptEditor(testCase);
                addTextHover(testScriptEditor, new DebugTextHover());
                positionEditor(testScriptEditor, frame);
            }
        } catch (Exception e) {
            // If an exception occurs that means test case meta data
            // file(.tc) does not exist.
        }
        addAnnotationForEditor((AbstractTextEditor) editorPart, frame);
        return true;
    }

    private void addAnnotationForEditor(AbstractTextEditor textEditor, IStackFrame stackFrame) {
        positionEditor(textEditor, stackFrame);
        InstructionPointerManager.getDefault().removeAnnotations(textEditor);

        Annotation annotation = presentation.getInstructionPointerAnnotation(textEditor, stackFrame);
        InstructionPointerManager.getDefault().addAnnotation(textEditor, stackFrame, annotation);
    }

    private void positionEditor(ITextEditor editor, IStackFrame frame) {
        try {
            int charStart = frame.getCharStart();
            if (charStart >= 0) {
                editor.selectAndReveal(charStart, 0);
                return;
            }

            int lineNumber = frame.getLineNumber() - 1; // Document line numbers are 0-based. Debug line numbers are
                                                        // 1-based.
            IRegion region = goToLine(editor, lineNumber);
            if (region != null) {
                editor.selectAndReveal(region.getOffset(), 0);
            }
        } catch (DebugException ignored) {
            // Cannot select, ignore it
        }
    }

    @Override
    public void removeAnnotations(IEditorPart editorPart, IThread thread) {
        if (!(editorPart instanceof GroovyEditor)) {
            return;
        }

        GroovyEditor groovyEditor = (GroovyEditor) editorPart;
        String className = groovyEditor.getGroovyCompilationUnit().getModuleNode().getMainClassName();
        try {
            AbstractTextEditor testCaseScriptEditor = getTestCaseEditorByScriptName(className);
            if (testCaseScriptEditor != null) {
                addTextHover(testCaseScriptEditor, new GroovyExtraInformationHover(true));
            }
        } catch (Exception ignored) {
            // Ignore it
        }
        addTextHover(editorPart, new GroovyExtraInformationHover(true));
    }

    private AbstractTextEditor getTestCaseEditorByScriptName(String fileName) throws Exception {
        TestCaseEntity testCase = TestCaseController.getInstance().getTestCaseByScriptName(fileName);
        return (testCase != null) ? getTestScriptEditor(testCase) : null;
    }

    private AbstractTextEditor getTestScriptEditor(TestCaseEntity testCase) {
        MPart compatibilityEditorPart = new LogExceptionNavigator().getTestCaseGroovyEditor(testCase);
        CompatibilityEditor compatibilityEditor = (CompatibilityEditor) compatibilityEditorPart.getObject();
        return (AbstractTextEditor) compatibilityEditor.getEditor();
    }

    private void addTextHover(IEditorPart editorPart, IJavaEditorTextHover textHover) {
        if (!(editorPart instanceof GroovyEditor)) {
            return;
        }

        GroovyEditor groovyEditor = (GroovyEditor) editorPart;

        TextViewer textViewer = (TextViewer) groovyEditor.getViewer();
        textHover.setEditor(groovyEditor);
        textViewer.setTextHover(textHover, "__dftl_partition_content_type", 0);
    }
}
