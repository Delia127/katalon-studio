package com.kms.katalon.addons;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.internal.keys.BindingService;
import org.eclipse.ui.keys.IBindingService;

import com.kms.katalon.composer.components.impl.handler.WorkbenchUtilizer;
import com.kms.katalon.composer.initializer.ApplicationInitializer;

/**
 * Remove Bindings by Command ID. This will remove the Bindings and Hanlder of specific Command.
 */
@SuppressWarnings("restriction")
public class CommandBindingRemover extends WorkbenchUtilizer implements ApplicationInitializer {

    @Override
    public void setup() {
        BindingService bindingService = (BindingService) getService(IBindingService.class);
        ICommandService commandService = getService(ICommandService.class);

        // Remove bindings for New Wizard (M1+N)
        removeBindingByCommandId(bindingService, commandService, IWorkbenchCommandConstants.FILE_NEW);
    }

    private void removeBindingByCommandId(BindingService bindingService, ICommandService commandService,
            String commandId) {
        if (bindingService == null || commandService == null || StringUtils.isBlank(commandId)) {
            return;
        }

        Command command = commandService.getCommand(commandId);
        // remove handler in this command
        command.setHandler(null);

        // remove bindings
        bindingService.getActiveBindings().remove(command);
    }

}
