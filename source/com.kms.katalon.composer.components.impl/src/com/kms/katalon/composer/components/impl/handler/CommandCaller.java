package com.kms.katalon.composer.components.impl.handler;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.CommandException;
import org.eclipse.ui.handlers.IHandlerService;

public class CommandCaller extends WorkbenchUtilizer {
    public void call(String commandId) throws CommandException {
        getService(IHandlerService.class).executeCommand(commandId, null);
    }
    
    public void call(ParameterizedCommand command) throws CommandException {
        getService(IHandlerService.class).executeCommand(command, null);
    }
}
