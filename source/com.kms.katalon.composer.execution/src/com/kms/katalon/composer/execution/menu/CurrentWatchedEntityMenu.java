 
package com.kms.katalon.composer.execution.menu;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;

public class CurrentWatchedEntityMenu {
	@Execute
	public void execute() {
		// do nothing for this
	}
	
	
	@CanExecute
	public boolean canExecute() {
		return true;
	}
		
}