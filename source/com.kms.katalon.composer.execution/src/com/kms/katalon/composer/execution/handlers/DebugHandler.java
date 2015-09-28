package com.kms.katalon.composer.execution.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;

public class DebugHandler extends ExecuteHandler {

	@CanExecute
	public boolean canExecute() {
		return super.canExecute();
	}

	@Execute
	public void execute(@Optional MHandledMenuItem item) {		
//		execute(item, LaunchMode.DEBUG, new HashMap<String, String>());
	}

}
