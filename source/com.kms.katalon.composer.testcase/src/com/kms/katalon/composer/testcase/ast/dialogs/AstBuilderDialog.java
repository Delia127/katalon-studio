package com.kms.katalon.composer.testcase.ast.dialogs;

import org.eclipse.jface.window.Window;


public interface AstBuilderDialog {
    /**
     * Refresh the input
     */
    public void refresh();

    /**
     * Get the dialog title for a specific ast node
     * 
     * @return the dialog title for a specific ast node
     */
    public String getDialogTitle();
    
    /**
     * Get the result value
     * @return the result
     */
    public abstract Object getReturnValue();
    
    /**
     * Replace the original ASTNodeWrapper object with new one
     * @param orginalObject
     * @param newObject
     */
    public abstract void replaceObject(Object orginalObject, Object newObject);
    
    /**
     * Open the dialog and wait for user input
     * @return the result close event: {@link Window#OK} or {@link Window#CANCEL}
     */
    public int open();
}
