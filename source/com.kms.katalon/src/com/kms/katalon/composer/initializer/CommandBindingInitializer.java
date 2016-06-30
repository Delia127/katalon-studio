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

    private static final String[] OVERRIDEN_COMMAND_IDS = new String[] {
            "com.kms.katalon.composer.explorer.command.open", "com.kms.katalon.composer.explorer.command.rename",
            "com.kms.katalon.composer.explorer.command.copy", "com.kms.katalon.composer.explorer.command.cut",
            "com.kms.katalon.composer.explorer.command.paste", "com.kms.katalon.composer.explorer.command.delete",
            "org.eclipse.ui.window.preferences" };

    @Override
    public void setup() {
        BindingService bindingService = (BindingService) getService(IBindingService.class);
        ICommandService commandService = (ICommandService) getService(ICommandService.class);

        removeOverlapingCommands(bindingService);

        restoreTextEditorCommand(bindingService, commandService);
        try {
            bindingService.savePreferences(bindingService.getActiveScheme(), bindingService.getActiveBindings()
                    .toArray(new Binding[0]));
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    private void restoreTextEditorCommand(BindingService bindingService, ICommandService commandService) {
        for (TextEditorCommand editorCommand : TextEditorCommand.values()) {
            try {
                bindingService.addBinding(editorCommand.newKeyBinding(bindingService, commandService));
            } catch (ParseException e) {
                LoggerSingleton.logError(e);
            }
        }
    }

    private void removeOverlapingCommands(BindingService bindingService) {
        for (String commandId : OVERRIDEN_COMMAND_IDS) {
            TriggerSequence triggerSequence = bindingService.getBestActiveBindingFor(commandId);

            for (Binding binding : bindingService.getActiveBindings()) {
                if ((binding instanceof KeyBinding) && binding.getTriggerSequence().equals(triggerSequence)
                        && (!binding.getParameterizedCommand().getId().equals(commandId))) {
                    bindingService.removeBinding(binding);
                }
            }
        }
    }

    private enum TextEditorCommand {
        OPEN("org.eclipse.jdt.ui.edit.text.java.open.editor", "F3"),
        COPY("org.eclipse.ui.edit.copy", "M1+C"),
        CUT("org.eclipse.ui.edit.cut", "M1+X"),
        PASTE("org.eclipse.ui.edit.paste", "M1+V"),
        DELETE("org.eclipse.ui.edit.delete", "Delete"),
        GO_TO_MATCHING_BRACKET("org.eclipse.jdt.ui.edit.text.java.goto.matching.bracket", "M1+M2+P");

        private String commandId;

        private String keySequence;

        TextEditorCommand(String commandId, String keySequence) {
            this.commandId = commandId;
            this.keySequence = keySequence;
        }

        public KeyBinding newKeyBinding(IBindingService bindingService, ICommandService commandService)
                throws ParseException {
            ParameterizedCommand parameterizedCommand = new ParameterizedCommand(commandService.getCommand(commandId),
                    null);

            return new KeyBinding(KeySequence.getInstance(keySequence), parameterizedCommand,
                    bindingService.getActiveScheme().getId(), TEXT_EDITOR_CONTEXT_ID, null, null, null, Binding.USER);
        }
    }
}
