package com.kms.katalon.util;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.logging.LogUtil;

public class CommandUtil {
    
    public static final String DEFAULT_ERROR_TITLE = "Execute command exception";

    public static Object autoHandleExecuteCommand(String commandId) {
        return autoHandleExecuteCommand(commandId, null, DEFAULT_ERROR_TITLE);
    }

    public static Object autoHandleExecuteCommand(String commandId, String errorTitle) {
        return autoHandleExecuteCommand(commandId, null, errorTitle);
    }

    public static Object autoHandleExecuteCommand(String commandId, Event event) {
        return autoHandleExecuteCommand(commandId, event, DEFAULT_ERROR_TITLE);
    }

    public static Object autoHandleExecuteCommand(String commandId, Event event, String errorTitle) {
        try {
            return executeCommand(commandId);
        } catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException error) {
            MultiStatusErrorDialog.showErrorDialog(error, errorTitle, error.getMessage(),
                    Display.getCurrent().getActiveShell());
        }
        return null;
    }

    public static Object safeExecuteCommand(String commandId) {
        return safeExecuteCommand(commandId, null);
    }

    public static Object safeExecuteCommand(String commandId, Event event) {
        try {
            return executeCommand(commandId, event);
        } catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException error) {
            LogUtil.logError(error);
        }
        return null;
    }

    public static Object executeCommand(String commandId)
            throws ExecutionException, NotDefinedException, NotEnabledException, NotHandledException {
        return executeCommand(commandId, null);
    }

    public static Object executeCommand(String commandId, Event event)
            throws ExecutionException, NotDefinedException, NotEnabledException, NotHandledException {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        IHandlerService handlerService = (IHandlerService) window.getService(IHandlerService.class);
        return handlerService.executeCommand(commandId, event);
    }

    public static Object autoHandleExecuteCommand(ParameterizedCommand parameterizedCommand) {
        return autoHandleExecuteCommand(parameterizedCommand, null, DEFAULT_ERROR_TITLE);
    }

    public static Object autoHandleExecuteCommand(ParameterizedCommand parameterizedCommand, String errorTitle) {
        return autoHandleExecuteCommand(parameterizedCommand, null, errorTitle);
    }

    public static Object autoHandleExecuteCommand(ParameterizedCommand parameterizedCommand, Event event) {
        return autoHandleExecuteCommand(parameterizedCommand, event, DEFAULT_ERROR_TITLE);
    }

    public static Object autoHandleExecuteCommand(ParameterizedCommand parameterizedCommand, Event event,
            String errorTitle) {
        try {
            return executeCommand(parameterizedCommand);
        } catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException error) {
            MultiStatusErrorDialog.showErrorDialog(error, errorTitle, error.getMessage(),
                    Display.getCurrent().getActiveShell());
        }
        return null;
    }

    public static Object safeExecuteCommand(ParameterizedCommand parameterizedCommand) {
        return safeExecuteCommand(parameterizedCommand, null);
    }

    public static Object safeExecuteCommand(ParameterizedCommand parameterizedCommand, Event event) {
        try {
            return executeCommand(parameterizedCommand, event);
        } catch (ExecutionException | NotDefinedException | NotEnabledException | NotHandledException error) {
            LogUtil.logError(error);
        }
        return null;
    }

    public static Object executeCommand(ParameterizedCommand parameterizedCommand)
            throws ExecutionException, NotDefinedException, NotEnabledException, NotHandledException {
        return executeCommand(parameterizedCommand, null);
    }

    public static Object executeCommand(ParameterizedCommand parameterizedCommand, Event event)
            throws ExecutionException, NotDefinedException, NotEnabledException, NotHandledException {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        IHandlerService handlerService = (IHandlerService) window.getService(IHandlerService.class);
        return handlerService.executeCommand(parameterizedCommand, event);
    }
}
