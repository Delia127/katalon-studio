package com.kms.katalon.composer.checkpoint.menu;

import com.kms.katalon.composer.checkpoint.handlers.EditCheckpointPropertiesHandler;
import com.kms.katalon.composer.components.impl.menu.AbstractPropertiesMenuContribution;

public class CheckpointPropertiesPopupMenu extends AbstractPropertiesMenuContribution {

    @Override
    protected boolean canShow() {
        return EditCheckpointPropertiesHandler.getInstance().canExecute();
    }

    @Override
    protected Class<?> getHandlerClass() {
        return EditCheckpointPropertiesHandler.class;
    }

}
