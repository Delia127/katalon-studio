package com.kms.katalon.composer.execution.debug;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.execution.launcher.AbstractLauncher;
import com.kms.katalon.execution.launcher.IDELauncher;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;

@SuppressWarnings("restriction")
public class LaunchDelegate extends JavaLaunchDelegate implements IExecutionListener {

	private ILaunch launch;

	
	public LaunchDelegate() {
		DebugUIPlugin.getDefault().getPreferenceStore()
				.putValue(IInternalDebugUIConstants.PREF_ACTIVATE_DEBUG_VIEW, Boolean.toString(false));
		JDIDebugModel.addJavaBreakpointListener(new BreakpointListener());
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		commandService.addExecutionListener(this);
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		this.launch = launch;
		super.launch(configuration, mode, launch, monitor);
	}

	@Override
	public void notHandled(String commandId, NotHandledException exception) {

	}

	@Override
	public void postExecuteFailure(String commandId, ExecutionException exception) {

	}

	@Override
	public void postExecuteSuccess(String commandId, Object returnValue) {
		try {
			if (launch == null || !LaunchMode.DEBUG.toString().equals(launch.getLaunchMode())) return;
			if (commandId.equals(StringConstants.DBG_COMMAND_RESUME)) {
				for (AbstractLauncher launcher : LauncherManager.getInstance().getIDELaunchers()) {
					if (launch.equals(launcher.getLaunch())) {
						IDELauncher ideLauncher = (IDELauncher) launcher;
						ideLauncher.resume();
						return;
					}
				}
			} else if (commandId.equals(StringConstants.DBG_COMMAND_SUSPEND)) {
				for (AbstractLauncher launcher : LauncherManager.getInstance().getIDELaunchers()) {
					if (launch.equals(launcher.getLaunch())) {
						IDELauncher ideLauncher = (IDELauncher) launcher;
						ideLauncher.suspend();
						return;
					}
				}
			}
		} catch (Exception ex) {
			LoggerSingleton.getInstance().getLogger().error(ex);
		}
	}

	@Override
	public void preExecute(String commandId, ExecutionEvent event) {
	}
}
