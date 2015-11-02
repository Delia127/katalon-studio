package com.kms.katalon.composer.explorer.handlers.deletion;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.impl.dialogs.YesNoAllOptions;

public abstract class AbstractDeleteEntityDialog extends AbstractDialog {

    private AbstractDeleteReferredEntityHandler fHandler;

    public AbstractDeleteEntityDialog(Shell parentShell, AbstractDeleteReferredEntityHandler deleteHandler) {
        super(parentShell);
        fHandler = deleteHandler;
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite mainComposite = new Composite(parent, SWT.NONE);
        mainComposite.setLayout(new GridLayout(1, false));

        Control composite = createDialogComposite(mainComposite);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        return mainComposite;
    }

    protected abstract Control createDialogComposite(Composite parent);

    @Override
    protected void buttonPressed(int buttonId) {
        fHandler.setDeletePreferenceOption(YesNoAllOptions.getOption(buttonId));
        super.okPressed();
    }

    protected final void createButtonsForButtonBar(Composite parent) {
        // create OK and Cancel buttons by default
        for (YesNoAllOptions option : fHandler.getAvailableDeletionOptions()) {
            createButton(parent, option.ordinal(), option.toString(), true);
        }
    }
}
