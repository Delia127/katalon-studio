package com.kms.katalon.composer.execution.debug;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.core.dom.Message;
import org.eclipse.jdt.debug.core.IJavaBreakpoint;
import org.eclipse.jdt.debug.core.IJavaBreakpointListener;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaType;

public class BreakpointListener implements IJavaBreakpointListener {

	@Override
	public void addingBreakpoint(IJavaDebugTarget target, IJavaBreakpoint breakpoint) {
		target.getDebugTarget();
	}

	@Override
	public int installingBreakpoint(IJavaDebugTarget target, IJavaBreakpoint breakpoint, IJavaType type) {
		return DONT_CARE;
	}

	@Override
	public void breakpointInstalled(IJavaDebugTarget target, IJavaBreakpoint breakpoint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int breakpointHit(IJavaThread thread, IJavaBreakpoint breakpoint) {
//		try {
//			for (AbstractLauncher launcher : LauncherManager.getInstance().getIDELaunchers()) {
//				if (thread.getLaunch().equals(launcher.getLaunch())) {
//					IDELauncher ideLauncher = (IDELauncher) launcher;
//					ideLauncher.breakPointHit();
//					break;
//				}
//			}
//		} catch (Exception ex) {
//
//		}
		return DONT_CARE;
	}

	@Override
	public void breakpointRemoved(IJavaDebugTarget target, IJavaBreakpoint breakpoint) {
		// TODO Auto-generated method stub

	}

	@Override
	public void breakpointHasRuntimeException(IJavaLineBreakpoint breakpoint, DebugException exception) {
		// TODO Auto-generated method stub

	}

	@Override
	public void breakpointHasCompilationErrors(IJavaLineBreakpoint breakpoint, Message[] errors) {
		// TODO Auto-generated method stub

	}

}
