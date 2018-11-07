package com.kms.katalon.composer.handlers;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.program.Program;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;

public class OpenErrorLogHandler extends AbstractHandler {
	@CanExecute
    public boolean canExecute() {
        return true;
    }

    @Execute
    public void execute() {
        Program.launch(Platform.getLogFileLocation().toString());
    }
}
