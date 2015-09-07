package com.kms.katalon.code.refactoring.addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.code.refactoring.handler.CodeRefactoringHandler;

public class CodeRefactoringDependencyInjectionAddon {
	@PostConstruct
	public void initHandlers(IEclipseContext context) {
		ContextInjectionFactory.make(CodeRefactoringHandler.class, context);
	}
}
