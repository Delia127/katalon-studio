package com.kms.katalon.composer.execution.debug.handler;

import com.kms.katalon.composer.execution.constants.StringConstants;
import org.codehaus.groovy.eclipse.editor.GroovyEditor;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;

@SuppressWarnings("restriction")
public class ToggleBreakpointHandler implements IHandler2 {

    @Override
    public void addHandlerListener(IHandlerListener handlerListener) {
        // Do nothing
    }

    @Override
    public void dispose() {
        // Do nothing
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        GroovyEditor editor = getActiveGroovyEditor();

        ISelection selection = getSelection(editor);
        try {
            DebugUITools.getToggleBreakpointsTargetManager()
                    .getToggleBreakpointsTarget(editor, selection)
                    .toggleLineBreakpoints(editor, selection);
        } catch (CoreException e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.HAND_ERROR_MSG_CANNOT_TOGGLE_LINE_BREAKPOINT,
                    e.getMessage());
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        GroovyEditor activeEditor = getActiveGroovyEditor();

        return (activeEditor != null && getSelection(activeEditor) != null);
    }
    
    private ISelection getSelection(GroovyEditor editor) {
        ISelectionProvider selectionProvider = editor.getSelectionProvider();
        return (selectionProvider != null) ? selectionProvider.getSelection() : null;
    }

    private GroovyEditor getActiveGroovyEditor() {
        if (!PlatformUI.isWorkbenchRunning()) {
            return null;
        }

        IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWindow == null) {
            return null;
        }

        IWorkbenchPart activePart = activeWindow.getPartService().getActivePart();
        return (activePart instanceof GroovyEditor) ? (GroovyEditor) activePart : null;
    }

    @Override
    public boolean isHandled() {
        return true;
    }

    @Override
    public void removeHandlerListener(IHandlerListener handlerListener) {
        // Do nothing
    }

    @Override
    public void setEnabled(Object evaluationContext) {
        // Do nothing
    }

}
