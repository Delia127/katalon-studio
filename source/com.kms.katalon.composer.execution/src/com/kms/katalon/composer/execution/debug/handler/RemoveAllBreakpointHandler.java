package com.kms.katalon.composer.execution.debug.handler;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.actions.ActionMessages;
import org.eclipse.debug.internal.ui.actions.breakpoints.RemoveAllBreakpointsAction;
import org.eclipse.debug.internal.ui.preferences.IDebugPreferenceConstants;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchWindow;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.controller.ProjectController;

@SuppressWarnings("restriction")
public class RemoveAllBreakpointHandler {

    @CanExecute
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null
                && DebugPlugin.getDefault().getBreakpointManager().hasBreakpoints();
    }

    /**
     * @see {@link RemoveAllBreakpointsAction#run(org.eclipse.jface.action.IAction)}
     */
    @Execute
    public void execute() {
        final IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
        final IBreakpoint[] breakpoints = breakpointManager.getBreakpoints();
        if (breakpoints.length < 1) {
            return;
        }

        IWorkbenchWindow window = DebugUIPlugin.getActiveWorkbenchWindow();
        if (window == null) {
            return;
        }
        IPreferenceStore store = DebugUIPlugin.getDefault().getPreferenceStore();
        boolean prompt = store.getBoolean(IDebugPreferenceConstants.PREF_PROMPT_REMOVE_ALL_BREAKPOINTS);
        
        if (prompt) {
            MessageDialogWithToggle mdwt = MessageDialogWithToggle.openYesNoQuestion(window.getShell(),
                    ActionMessages.RemoveAllBreakpointsAction_0, ActionMessages.RemoveAllBreakpointsAction_1,
                    ActionMessages.RemoveAllBreakpointsAction_3, false, null, null);
            if (mdwt.getReturnCode() != IDialogConstants.YES_ID) {
                return;
            }
            store.setValue(IDebugPreferenceConstants.PREF_PROMPT_REMOVE_ALL_BREAKPOINTS, !mdwt.getToggleState());
        }

        startRemoveAllBreakpointJob(breakpoints);
    }

    private void startRemoveAllBreakpointJob(final IBreakpoint[] breakpoints) {
        new Job(ActionMessages.RemoveAllBreakpointsAction_2) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    DebugUITools.deleteBreakpoints(breakpoints, null, monitor);
                } catch (CoreException e) {
                    LoggerSingleton.logError(e);
                    return Status.CANCEL_STATUS;
                }
                return Status.OK_STATUS;
            }
        }.schedule();
    }
}
