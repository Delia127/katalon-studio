package com.kms.katalon.composer.testcase.ast.dialogs;

import java.awt.JobAttributes.DialogType;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.testcase.constants.StringConstants;

public class TestStepDescriptionBuilderDialog extends TitleAreaDialog {
    protected String dialogTitle;
    protected String dialogMessage;
    protected DialogType type;
    protected Text textDescription;
    protected String description;

    public TestStepDescriptionBuilderDialog(Shell parentShell, String defaltContent) {
        super(parentShell);
        dialogTitle = StringConstants.DIA_TITLE_EDIT_DESCRIPTION;
        dialogMessage = StringConstants.DIA_MESSAGE_EDIT_DESCRIPTION;
        description = defaltContent;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        container.setLayout(new GridLayout(2, false));

        textDescription = new Text(container, SWT.BORDER | SWT.MULTI);
        textDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        // Build the separator line
        Label separator = new Label(area, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return area;
    }

    @Override
    protected Point getInitialSize() {
        Point initSize = super.getInitialSize();
        return new Point(initSize.x, 250);
    }

    @Override
    public void create() {
        super.create();
        setTitle(dialogTitle);
        setMessage(dialogMessage, IMessageProvider.INFORMATION);
        setInput();
    }

    @Override
    protected void okPressed() {
        description = textDescription.getText();
        super.okPressed();
    }

    private void setInput() {
        if (description != null) {
            textDescription.setText(description);
        }
    }

    public String getDescription() {
        return description == null ? "" : description;
    }
}
