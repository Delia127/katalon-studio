package com.kms.katalon.composer.project.addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.project.handlers.CloseProjectHandler;

public class ProjectInjectionManagerAddon {
	@PostConstruct
	public void initHandlers(IEclipseContext context) {
		ContextInjectionFactory.make(CloseProjectHandler.class, context);
	}
}
