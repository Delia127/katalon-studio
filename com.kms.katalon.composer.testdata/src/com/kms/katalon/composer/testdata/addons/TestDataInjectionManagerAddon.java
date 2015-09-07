package com.kms.katalon.composer.testdata.addons;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.explorer.util.TransferTypeCollection;
import com.kms.katalon.composer.testdata.handlers.DeleteTestDataHandler;
import com.kms.katalon.composer.testdata.handlers.NewTestDataHandler;
import com.kms.katalon.composer.testdata.handlers.OpenTestDataHandler;
import com.kms.katalon.composer.testdata.handlers.RefreshTestDataHandler;
import com.kms.katalon.composer.testdata.handlers.RenameTestDataHandler;

public class TestDataInjectionManagerAddon {
    
    @PostConstruct
    public void initHandlers(IEclipseContext context) {
		ContextInjectionFactory.make(NewTestDataHandler.class, context);
		ContextInjectionFactory.make(OpenTestDataHandler.class, context);
		ContextInjectionFactory.make(DeleteTestDataHandler.class, context);
		ContextInjectionFactory.make(RenameTestDataHandler.class, context);
		ContextInjectionFactory.make(RefreshTestDataHandler.class, context);
		TransferTypeCollection.getInstance().addTreeEntityTransferType(TreeEntityTransfer.getInstance());
	}
}
