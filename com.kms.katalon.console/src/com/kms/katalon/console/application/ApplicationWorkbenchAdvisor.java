package com.kms.katalon.console.application;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;

import com.kms.katalon.execution.launcher.manager.ConsoleMain;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	private static final String PERSPECTIVE_ID = "com.kms.katalon.console.perspective";

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		IDE.registerAdapters();
	}

	@Override
	public void postStartup() {
		super.postStartup();
		try {
			PlatformUI.getWorkbench().getDisplay().getActiveShell().setVisible(false);
			new ConsoleMain().launch(ApplicationRunningMode.getInstance().getRunArguments());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
