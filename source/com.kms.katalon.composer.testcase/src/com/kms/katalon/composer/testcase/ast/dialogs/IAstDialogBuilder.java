package com.kms.katalon.composer.testcase.ast.dialogs;

import org.eclipse.jface.window.Window;

public interface IAstDialogBuilder {

    /**
     * Get the result value
     * 
     * @return the result
     */
    public abstract Object getReturnValue();

    /**
     * Open the dialog and wait for user input
     * 
     * @return the result close event: {@link Window#OK} or {@link Window#CANCEL}
     */
    public int open();
}
