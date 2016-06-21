package com.kms.katalon.composer.initializer;

import java.io.IOException;

import org.eclipse.jface.bindings.Binding;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.ui.internal.keys.BindingService;
import org.eclipse.ui.keys.IBindingService;

import com.kms.katalon.composer.components.impl.handler.WorkbenchUtilizer;
import com.kms.katalon.composer.components.log.LoggerSingleton;

@SuppressWarnings("restriction")
public class CommandBindingInitializer extends WorkbenchUtilizer implements ApplicationInitializer {
    private static final String[] OVERRIDEN_COMMAND_ID = new String[] { "com.kms.katalon.composer.explorer.command.open" };

    @Override
    public void setup() {
        BindingService bindingService = (BindingService) getService(IBindingService.class);

        for (String commandId : OVERRIDEN_COMMAND_ID) {
            removeOverlapingCommands(bindingService, commandId);
        }

        try {
            bindingService.savePreferences(bindingService.getActiveScheme(), bindingService.getActiveBindings()
                    .toArray(new Binding[0]));
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }

    private void removeOverlapingCommands(BindingService bindingService, String commandId) {
        TriggerSequence triggerSequence = bindingService.getBestActiveBindingFor(commandId);
        Binding overlappingBinding = getActiveBinding(bindingService, triggerSequence);

        while (overlappingBinding != null && overlappingBinding.getParameterizedCommand() != null
                && !commandId.equals(overlappingBinding.getParameterizedCommand().getId())) {
            bindingService.removeBinding(overlappingBinding);

            overlappingBinding = getActiveBinding(bindingService, triggerSequence);
        }
    }

    private Binding getActiveBinding(BindingService bindingService, TriggerSequence triggerSequence) {
        return bindingService.getPerfectMatch(triggerSequence);
    }

}
