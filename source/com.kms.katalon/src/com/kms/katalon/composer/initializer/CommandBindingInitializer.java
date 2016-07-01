package com.kms.katalon.composer.initializer;

import java.io.IOException;

import org.eclipse.core.commands.ParameterizedCommand;
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

    @Override
    public void setup() {
        BindingService bindingService = (BindingService) getService(IBindingService.class);

        activeExplorerCommands(bindingService);

        try {
            bindingService.savePreferences(bindingService.getActiveScheme(), bindingService.getActiveBindings()
                    .toArray(new Binding[0]));
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    private ICommandService getCommandService() {
        ICommandService commandService = (ICommandService) getService(ICommandService.class);
        return commandService;
    }

    private void activeExplorerCommands(BindingService bindingService) {
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
                "M1+M2+P");

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
