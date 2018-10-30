package com.kms.katalon.composer.util.groovy;

import org.codehaus.groovy.ast.builder.AstBuilder;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.eclipse.dsl.GroovyDSLCoreActivator;
import org.codehaus.groovy.eclipse.editor.GroovyEditor;
import org.codehaus.groovy.syntax.SyntaxException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.EditorHistory;
import org.eclipse.ui.internal.EditorReference;
import org.eclipse.ui.internal.NavigationHistory;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;
import org.eclipse.ui.internal.registry.EditorDescriptor;
import org.eclipse.ui.part.FileEditorInput;

import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.groovy.util.GroovyUtil;

@SuppressWarnings("restriction")
public class GroovyEditorUtil {

    private static final String GROOVY_EDITOR_URI = "org.codehaus.groovy.eclipse.editor.GroovyEditor";

    public static MPart createTestCaseEditorPart(IFile scriptFile, MPartStack parentPartStack, String testCaseEditorId,
            EPartService partService, int index) {
        MPart editor = createEditorPart(scriptFile, partService);

        editor.setElementId(testCaseEditorId);

        parentPartStack.getChildren().add(index, editor);
        return editor;
    }

    public static MPart createEditorPart(ProjectEntity projectEntity, String filePath, EPartService partService)
            throws CoreException {
        IFile scriptFile = GroovyUtil.getGroovyProject(projectEntity).getFile(Path.fromOSString(filePath));
        scriptFile.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
        return createEditorPart(scriptFile, partService);
    }

    public static MPart createEditorPart(IFile scriptFile, EPartService partService) {
        MPart editor = partService.createPart(CompatibilityEditor.MODEL_ELEMENT_ID);

        IEditorInput input = new FileEditorInput(scriptFile);
        editor.getTags().add(GROOVY_EDITOR_URI);
        createEditorReferenceForPart(editor, input, GROOVY_EDITOR_URI, null);
        updateActiveEditorSources(editor);
        EditorDescriptor descriptor = (EditorDescriptor) PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow()
                .getActivePage()
                .getWorkbenchWindow()
                .getWorkbench()
                .getEditorRegistry()
                .findEditor(GROOVY_EDITOR_URI);
        recordEditor(input, descriptor);

        editor.getTags().add(IPresentationEngine.NO_MOVE);
        return editor;
    }

    private static void recordEditor(IEditorInput input, IEditorDescriptor descriptor) {
        WorkbenchPage page = (WorkbenchPage) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        EditorHistory history = ((Workbench) page.getWorkbenchWindow().getWorkbench()).getEditorHistory();
        history.add(input, descriptor);
    }

    public static IEditorPart getEditor(MPart part) {
        if (part != null) {
            Object clientObject = part.getObject();
            if (clientObject instanceof CompatibilityEditor) {
                return ((CompatibilityEditor) clientObject).getEditor();
            }
        }
        return null;
    }

    public static void saveEditor(MPart part) {
        if (!part.isDirty()) {
            return;
        }
        IEditorPart editor = getEditor(part);
        if (editor instanceof GroovyEditor) {
            GroovyEditor groovyEditor = (GroovyEditor) editor;
            ICompilationUnit unit = (ICompilationUnit) groovyEditor.getGroovyCompilationUnit();
            try {
                if (!unit.isWorkingCopy()) {
                    unit.becomeWorkingCopy(null);
                }

                unit.commitWorkingCopy(true, null);
            } catch (JavaModelException e) {
                // User typing error, don't care about it.
            } finally {
                part.setDirty(false);
            }
            return;
        }
        editor.doSave(new NullProgressMonitor());
    }

    public static boolean isGroovyEditorPart(MPart part) {
        IEditorPart editor = getEditor(part);
        return editor instanceof GroovyEditor;
    }

    private static void updateActiveEditorSources(MPart part) {
        IEditorPart editor = getEditor(part);
        WorkbenchPage page = (WorkbenchPage) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        MWindow window = page.getWindowModel();
        window.getContext().set(ISources.ACTIVE_EDITOR_ID_NAME, editor == null ? null : editor.getSite().getId());
        window.getContext().set(ISources.ACTIVE_EDITOR_NAME, editor);
        window.getContext().set(ISources.ACTIVE_EDITOR_INPUT_NAME, editor == null ? null : editor.getEditorInput());

        if (editor != null) {
            NavigationHistory navigationHistory = (NavigationHistory) page.getNavigationHistory();
            navigationHistory.markEditor(editor);
        }
    }

    private static EditorReference createEditorReferenceForPart(final MPart part, IEditorInput input, String editorId,
            IMemento memento) {
        IEditorRegistry registry = PlatformUI.getWorkbench().getEditorRegistry();
        EditorDescriptor descriptor = (EditorDescriptor) registry.findEditor(editorId);
        WorkbenchPage page = (WorkbenchPage) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        final EditorReference ref = new EditorReference(page.getWindowModel().getContext(), page, part, input,
                descriptor, memento);
        page.addEditorReference(ref);
        ref.subscribe();
        return ref;
    }

    public static void showProblems(GroovyEditor editor) {
        IResource resource = null;
        try {
            resource = editor.getGroovyCompilationUnit().getResource();
            clearEditorProblems(editor);
            String testScriptContent = editor.getGroovyCompilationUnit().getSource();
            if (testScriptContent == null || testScriptContent.isEmpty())
                return;
            new AstBuilder().buildFromString(CompilePhase.CONVERSION, testScriptContent);
        } catch (MultipleCompilationErrorsException ex) {
            try {
                SyntaxErrorMessage message = (SyntaxErrorMessage) ex.getErrorCollector().getError(0);
                SyntaxException syntaxException = message.getCause();
                int lineOffset = editor.getViewer().getDocument().getLineOffset(syntaxException.getLine() - 1);
                IMarker marker = resource.createMarker(GroovyDSLCoreActivator.MARKER_ID);
                marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
                marker.setAttribute(IMarker.MESSAGE, syntaxException.getMessage());
                marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                marker.setAttribute(IMarker.LINE_NUMBER, syntaxException.getLine());
                marker.setAttribute(IMarker.CHAR_START, lineOffset + syntaxException.getStartColumn() - 1);
                marker.setAttribute(IMarker.CHAR_END, lineOffset + syntaxException.getEndColumn() - 1);
                marker.setAttribute(IMarker.LOCATION, String.format("line %d", syntaxException.getLine()));
            } catch (Exception e) {
                // Don't throw or log because this is a user typing error.
            }
        } catch (Exception e) {
            // Don't throw or log because this is a user typing error.
        }
    }

    public static void clearEditorProblems(GroovyEditor editor) throws CoreException {
        IResource resource = editor.getGroovyCompilationUnit().getResource();
        if (resource != null && resource.exists()) {
            resource.deleteMarkers(IMarker.PROBLEM, true, IMarker.SEVERITY_ERROR);
        }
    }
    
    public static void insertScript(GroovyEditor editor, int offset, String script) 
            throws MalformedTreeException, BadLocationException {
        
        IEditorInput editorInput = editor.getEditorInput();
        IDocument document = editor.getDocumentProvider().getDocument(editorInput);
        
        InsertEdit insertEdit = new InsertEdit(offset, script);
        insertEdit.apply(document);
        editor.selectAndReveal(offset, script.length());
    }

}
