package com.kms.katalon.composer.checkpoint.addon;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.checkpoint.handlers.DeleteCheckpointFolderHandler;
import com.kms.katalon.composer.checkpoint.handlers.DeleteCheckpointHandler;
import com.kms.katalon.composer.checkpoint.handlers.EditCheckpointPropertiesHandler;
import com.kms.katalon.composer.checkpoint.handlers.NewCheckpointHandler;
import com.kms.katalon.composer.checkpoint.handlers.OpenCheckpointHandler;
import com.kms.katalon.composer.checkpoint.handlers.RefreshCheckpointHandler;
import com.kms.katalon.composer.checkpoint.handlers.RenameCheckpointHandler;
import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.explorer.util.TransferTypeCollection;

public class CheckpointInjectionManagerAddon {

    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(NewCheckpointHandler.class, context);
        ContextInjectionFactory.make(OpenCheckpointHandler.class, context);
        ContextInjectionFactory.make(DeleteCheckpointHandler.class, context);
        ContextInjectionFactory.make(DeleteCheckpointFolderHandler.class, context);
        ContextInjectionFactory.make(RenameCheckpointHandler.class, context);
        ContextInjectionFactory.make(RefreshCheckpointHandler.class, context);
        ContextInjectionFactory.make(EditCheckpointPropertiesHandler.class, context);
        TransferTypeCollection.getInstance().addTreeEntityTransferType(TreeEntityTransfer.getInstance());
    }

}
