package com.kms.katalon.composer.components.impl.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractDialog extends Dialog {

    protected Composite mainComposite;

    public AbstractDialog(Shell parentShell) {
        super(parentShell);
    }
    
    /**
     * Creates parent container that includes child container and horizontal line. 
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        mainComposite = (Composite) super.createDialogArea(parent);
        mainComposite.setLayout(new GridLayout(1, false));

        Composite mainContainer = new Composite(mainComposite, SWT.NONE);
        mainContainer.setLayout(new FillLayout(SWT.HORIZONTAL));
        mainContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        
        createDialogContainer(mainContainer);

        Label label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        return mainComposite;
    }

    @Override
    public void create() {
        super.create();
        setInput();
        registerControlModifyListeners();
    }

    /**
     * Used for children can register event listener of their control after creating them.
     */
    protected abstract void registerControlModifyListeners();
    
    /**
     * Used for children set value to their control after creating them.
     */
    protected abstract void setInput();

    /**
     * Let children create its control.
     * @param parent main container.
     * @return main area of children.
     */
    protected abstract Control createDialogContainer(Composite parent);

    @Override
    protected void setShellStyle(int arg) {
        super.setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.RESIZE);
    }
    
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(getDialogTitle());
    }

    /**
     * @return Title of the dialog.
     */
    protected String getDialogTitle() {
        return "";
    }
    
}
