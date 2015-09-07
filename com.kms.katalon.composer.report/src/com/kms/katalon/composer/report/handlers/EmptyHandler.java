 
package com.kms.katalon.composer.report.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.CanExecute;

public class EmptyHandler {
	@Execute
	public void execute() {
		// do nothing
	}
	
	
	@CanExecute
	public boolean canExecute() {
		return false;
	}
		
}