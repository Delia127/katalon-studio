package com.kms.katalon.composer.testsuite.addons;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.explorer.util.TransferTypeCollection;
import com.kms.katalon.composer.testsuite.handlers.DeleteTestSuiteHandler;
import com.kms.katalon.composer.testsuite.handlers.EvaluateIntegrationContributionViewHandler;
import com.kms.katalon.composer.testsuite.handlers.OpenTestSuiteHandler;
import com.kms.katalon.composer.testsuite.handlers.RefreshTestSuiteHandler;
import com.kms.katalon.composer.testsuite.handlers.RenameTestSuiteHandler;

public class TestSuiteInjectionManagerAddon {
	
	@PostConstruct
	public void initHandlers(IEclipseContext context) {
		ContextInjectionFactory.make(OpenTestSuiteHandler.class, context);
		ContextInjectionFactory.make(DeleteTestSuiteHandler.class, context);
		ContextInjectionFactory.make(RenameTestSuiteHandler.class, context);
		ContextInjectionFactory.make(RefreshTestSuiteHandler.class, context);
		ContextInjectionFactory.make(EvaluateIntegrationContributionViewHandler.class, context);
		TransferTypeCollection.getInstance().addTreeEntityTransferType(TreeEntityTransfer.getInstance());
	}
}
