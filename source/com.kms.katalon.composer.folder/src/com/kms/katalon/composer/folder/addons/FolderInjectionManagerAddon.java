package com.kms.katalon.composer.folder.addons;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.explorer.util.TransferTypeCollection;
import com.kms.katalon.composer.folder.handlers.NewFolderHandler;
import com.kms.katalon.composer.folder.handlers.PasteFolderHandler;
import com.kms.katalon.composer.folder.handlers.RefreshFolderHandler;
import com.kms.katalon.composer.folder.handlers.RenameFolderHandler;
import com.kms.katalon.composer.folder.handlers.deletion.DeleteFolderHandlerRegister;

public class FolderInjectionManagerAddon {
    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(RenameFolderHandler.class, context);
        ContextInjectionFactory.make(NewFolderHandler.class, context);
        ContextInjectionFactory.make(PasteFolderHandler.class, context);
        ContextInjectionFactory.make(RefreshFolderHandler.class, context);
        ContextInjectionFactory.make(DeleteFolderHandlerRegister.class, context);
        TransferTypeCollection.getInstance().addTreeEntityTransferType(TreeEntityTransfer.getInstance());
    }
}