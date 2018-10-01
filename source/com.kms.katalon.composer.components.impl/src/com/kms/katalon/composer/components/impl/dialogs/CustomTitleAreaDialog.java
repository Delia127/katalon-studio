package com.kms.katalon.composer.components.impl.dialogs;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

public abstract class CustomTitleAreaDialog extends AbstractDialog {

    protected Link messageLabel;
    private Label imageLabel;

    public CustomTitleAreaDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        container.setLayout(gridLayout);
        
        Composite titleComposite = new Composite(container, SWT.NONE);
        titleComposite.setLayout(new GridLayout(2, false));
        titleComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        imageLabel = new Label(titleComposite, SWT.NONE);
        imageLabel.setLayoutData(new GridData(SWT.TOP, SWT.TOP, false, false));
        messageLabel = new Link(titleComposite, SWT.WRAP);
        messageLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        Composite area = createContentArea(container);
        area.setLayoutData(new GridData(GridData.FILL_BOTH));

        return container;
    }
    
    protected abstract Composite createContentArea(Composite parent);

    public void setMessage(String newMessage, int newType) {
        Image newImage = null;
        if (newMessage != null) {
            switch (newType) {
            case IMessageProvider.NONE:
                break;
            case IMessageProvider.INFORMATION:
                newImage = JFaceResources.getImage(DLG_IMG_MESSAGE_INFO);
                break;
            case IMessageProvider.WARNING:
                newImage = JFaceResources.getImage(DLG_IMG_MESSAGE_WARNING);
                break;
            case IMessageProvider.ERROR:
                newImage = JFaceResources.getImage(DLG_IMG_MESSAGE_ERROR);
                break;
            }
        }
        showMessage(newMessage, newImage);
    }

    private void showMessage(String newMessage, Image newImage) {
        messageLabel.setText(newMessage);
        imageLabel.setImage(newImage);
        messageLabel.requestLayout();
        messageLabel.getParent().layout(true);
    }

}
