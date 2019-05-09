package com.kms.katalon.composer.project.dialog;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;

public class PermissionAccessAnalyticsDialog extends AbstractDialog {

    private Label lblTitle;

    private Link lblDescription;

    private String title;

    private String errorDescription;

    private PermissionAccessAnalyticsDialog(Shell parentShell, String errorMessage, String errorReason) {
        super(parentShell);
        this.title = errorMessage;
        this.errorDescription = errorReason;
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        
        Composite imageComposite = new Composite(composite, SWT.NONE);
        imageComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
        imageComposite.setLayout(new GridLayout(1, false));
        
        Label lblImage = new Label(imageComposite, SWT.NONE);
        lblImage.setImage(getShell().getDisplay().getSystemImage(SWT.ICON_WARNING));

        Composite container = new Composite(composite, SWT.NONE);
        GridLayout glContainer = new GridLayout(1, false);
        glContainer.marginHeight = 0;
        glContainer.marginWidth = 0;
        container.setLayout(glContainer);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        lblTitle = new Label(container, SWT.WRAP);
        lblTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        lblDescription = new Link(container, SWT.WRAP);
        lblDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));
        return composite;
    }

    @Override
    protected void registerControlModifyListeners() {
    	lblDescription.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(e.text);
            }
        });
    }

    @Override
    public String getDialogTitle() {
        return StringConstants.WARN;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(700, super.getInitialSize().y);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL,
                true);
    }

    @Override
    protected void setInput() {
        lblTitle.setText(StringUtils.defaultString(title));
        lblDescription.setText(StringUtils.defaultString(errorDescription));
    }

   
    public static void showErrorDialog(String message, String reason) {
    	PermissionAccessAnalyticsDialog dialog = new PermissionAccessAnalyticsDialog(Display.getCurrent().getActiveShell(), message,
                reason);
        dialog.open();
    }

}
