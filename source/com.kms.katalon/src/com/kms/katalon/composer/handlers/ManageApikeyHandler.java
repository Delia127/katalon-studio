package com.kms.katalon.composer.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.program.Program;

public class ManageApikeyHandler {

    @CanExecute
    public boolean canExecute() {
        return true;
    }

    @Execute
    public void execute() {
        Program.launch("https://store.katalon.com/settings");
    }
}
