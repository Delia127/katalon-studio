package com.kms.katalon.composer.integration.slack.addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.integration.slack.handlers.SlackSendMsgHandler;

public class SlackInjectionManagerAddon {
	@PostConstruct
	public void initHandlers(IEclipseContext context) {
		ContextInjectionFactory.make(SlackSendMsgHandler.class, context);
	}
}
