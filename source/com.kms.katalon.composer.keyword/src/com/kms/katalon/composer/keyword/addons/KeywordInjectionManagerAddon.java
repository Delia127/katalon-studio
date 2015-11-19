package com.kms.katalon.composer.keyword.addons;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.explorer.util.TransferTypeCollection;
import com.kms.katalon.composer.keyword.handlers.DeleteKeywordAndPackageHandler;
import com.kms.katalon.composer.keyword.handlers.EditorSavedHandler;
import com.kms.katalon.composer.keyword.handlers.OpenKeywordHandler;
import com.kms.katalon.composer.keyword.handlers.PastePackageHandler;
import com.kms.katalon.composer.keyword.handlers.RefreshKeywordHandler;
import com.kms.katalon.composer.keyword.handlers.RefreshPackageHandler;
import com.kms.katalon.composer.keyword.handlers.RenameKeywordHandler;
import com.kms.katalon.composer.keyword.handlers.RenamePackageHandler;

public class KeywordInjectionManagerAddon {
	
    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(OpenKeywordHandler.class, context);
        ContextInjectionFactory.make(DeleteKeywordAndPackageHandler.class, context);
        ContextInjectionFactory.make(RenamePackageHandler.class, context);
        ContextInjectionFactory.make(RenameKeywordHandler.class, context);
        ContextInjectionFactory.make(PastePackageHandler.class, context);
        ContextInjectionFactory.make(RefreshPackageHandler.class, context);
        ContextInjectionFactory.make(RefreshKeywordHandler.class, context);
        ContextInjectionFactory.make(EditorSavedHandler.class, context);
		TransferTypeCollection.getInstance().addTreeEntityTransferType(TreeEntityTransfer.getInstance());
    }
}
