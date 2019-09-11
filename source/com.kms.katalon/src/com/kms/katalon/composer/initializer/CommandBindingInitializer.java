package com.kms.katalon.composer.initializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.ui.bindings.internal.BindingTable;
import org.eclipse.e4.ui.bindings.internal.BindingTableManager;
import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.jface.bindings.keys.KeyBinding;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.internal.keys.BindingService;
import org.eclipse.ui.keys.IBindingService;

import com.kms.katalon.composer.components.impl.handler.WorkbenchUtilizer;
import com.kms.katalon.composer.components.log.LoggerSingleton;

/**
 * Removes all command that has the same {@link TriggerSequence} with ours but remains eclipse's shortcut for editing
 * text as well.
 */
@SuppressWarnings("restriction")
public class CommandBindingInitializer extends WorkbenchUtilizer implements ApplicationInitializer {

    private static final String TEXT_EDITOR_CONTEXT_ID = "org.eclipse.ui.textEditorScope";

    public void resetDeleteKeyBinding() {
        BindingService bindingService = (BindingService) getService(IBindingService.class);
        BindingTableManager tableManager = getService(BindingTableManager.class);
        BindingTable table = tableManager.getTable(TEXT_EDITOR_CONTEXT_ID);
        if (table == null) {
            return;
        }
        Command command = getService(ICommandService.class).getCommand(TextEditorCommand.DELETE.editorCommandId);
        if (command == null) {
            return;
        }
        Binding binding = table.getBestSequenceFor(ParameterizedCommand.generateCommand(command, null));
        if (binding == null) {
            addBindingAndSave(bindingService);
        } else {
            if (binding.getTriggerSequence() == null) {
                bindingService.removeBinding(binding);
                addBindingAndSave(bindingService);
            }
        }
    }

    private void addBindingAndSave(BindingService bindingService) {
        try {
            bindingService.addBinding(TextEditorCommand.DELETE.newKeyBinding(bindingService, getCommandService()));
            saveActiveBinding(bindingService);
        } catch (ParseException e) {
            LoggerSingleton.logError(e);
        }
    }


	private void saveActiveBinding(BindingService bindingService) {
        try {
            bindingService.savePreferences(bindingService.getActiveScheme(),
                   bindingService.getActiveBindings().toArray(new Binding[0]));
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public void setup() {
        BindingService bindingService = (BindingService) getService(IBindingService.class);

        activeExplorerCommands(bindingService);

        saveActiveBinding(bindingService);
    }

    private ICommandService getCommandService() {
        ICommandService commandService = (ICommandService) getService(ICommandService.class);
        return commandService;
    }

    private void activeExplorerCommands(BindingService bindingService) {
    	
    	// Since user-defined bindings won't be considered active 
    	// Manually save them into active bindings
    	ArrayList<Binding> bindingsTobeSaved = new ArrayList<Binding>(bindingService.getActiveBindings());
    	Arrays.stream(bindingService.getBindings())
    	.filter(a -> a.getParameterizedCommand() != null && a.getType() == 1)
    	.forEach(a -> bindingsTobeSaved.add(a));
    	
    	try {
			bindingService.savePreferences(bindingService.getActiveScheme(), 
					bindingsTobeSaved.toArray(new Binding[0]));
		} catch (IOException e) {
            LoggerSingleton.logError(e);
		}
    	
    	// Then remove conflicting commands (with Explorer/Editor) 
        for (TextEditorCommand command : TextEditorCommand.values()) {
            try {
                activeKeyBindingForExplorerCommand(bindingService, command);
            } catch (ParseException e) {
                LoggerSingleton.logError(e);
            }
        }
    }

    private void activeKeyBindingForExplorerCommand(BindingService bindingService, TextEditorCommand command)
            throws ParseException {
        String explorerCommandId = command.getExplorerCommandId();
        TriggerSequence triggerSequence = bindingService.getBestActiveBindingFor(explorerCommandId);
        if (triggerSequence == null) {
            triggerSequence = bindingService.getBestActiveBindingFor(command.getEditorCommandId());
        }

        for (Binding activeBinding : bindingService.getActiveBindings()) {
            if (!(activeBinding instanceof KeyBinding) || !triggerSequence.equals(activeBinding.getTriggerSequence())) {
                continue;
            }

            String overlappedCommandId = activeBinding.getParameterizedCommand().getId();

            if (overlappedCommandId.equals(explorerCommandId)) {
                continue;
            }

            bindingService.removeBinding(activeBinding);

            if (overlappedCommandId.equals(command.getEditorCommandId())) {
                bindingService.addBinding(command.newKeyBinding(bindingService, getCommandService()));
            }
        }
    }

    private enum TextEditorCommand {
        OPEN("com.kms.katalon.composer.explorer.command.open", "org.eclipse.jdt.ui.edit.text.java.open.editor", "F3"),
        RENAME("com.kms.katalon.composer.explorer.command.rename", "", "F2"),
        COPY("com.kms.katalon.composer.explorer.command.copy", "org.eclipse.ui.edit.copy", "M1+C"),
        CUT("com.kms.katalon.composer.explorer.command.cut", "org.eclipse.ui.edit.cut", "M1+X"),
        PASTE("com.kms.katalon.composer.explorer.command.paste", "org.eclipse.ui.edit.paste", "M1+V"),
        DELETE("com.kms.katalon.composer.explorer.command.delete", "org.eclipse.ui.edit.delete", "Delete"),
        GO_TO_MATCHING_BRACKET(
                "org.eclipse.ui.window.preferences",
                "org.eclipse.jdt.ui.edit.text.java.goto.matching.bracket",
                "M1+M2+P"),
        QUICK_MENU("com.kms.katalon.composer.project.command.setting", "org.eclipse.jdt.ui.edit.text.java.source.quickMenu", "M1+M3+S"),
        DISPLAY("com.kms.katalon.composer.execution.command.debug", "org.eclipse.jdt.debug.ui.commands.Display", "M1+M2+D");

        private String editorCommandId;

        private String explorerCommandId;

        private String keySequence;

        TextEditorCommand(String explorerCommandId, String commandId, String keySequence) {
            this.explorerCommandId = explorerCommandId;
            this.editorCommandId = commandId;
            this.keySequence = keySequence;
        }

        public String getExplorerCommandId() {
            return explorerCommandId;
        }

        public String getEditorCommandId() {
            return editorCommandId;
        }

        public KeyBinding newKeyBinding(IBindingService bindingService, ICommandService commandService)
                throws ParseException {
            ParameterizedCommand parameterizedCommand = new ParameterizedCommand(
                    commandService.getCommand(editorCommandId), null);

            return new KeyBinding(KeySequence.getInstance(keySequence), parameterizedCommand,
                    bindingService.getActiveScheme().getId(), TEXT_EDITOR_CONTEXT_ID, null, null, null, Binding.USER);
        }
    }
}
