package com.kms.katalon.composer.testsuite.collection.addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.explorer.util.TransferTypeCollection;
import com.kms.katalon.composer.testsuite.collection.handler.OpenTestSuiteCollectionHandler;
import com.kms.katalon.composer.testsuite.collection.handler.RenameTestSuiteCollectionHandler;

public class TestSuiteCollectionDependencyInjectionAddon {
    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(OpenTestSuiteCollectionHandler.class, context);
        ContextInjectionFactory.make(RenameTestSuiteCollectionHandler.class, context);
        TransferTypeCollection.getInstance().addTreeEntityTransferType(TreeEntityTransfer.getInstance());
    }
}
