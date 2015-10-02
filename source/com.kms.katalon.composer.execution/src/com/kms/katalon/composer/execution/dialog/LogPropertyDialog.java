package com.kms.katalon.composer.execution.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.core.logging.XmlLogRecord;

public class LogPropertyDialog extends Dialog {

    private XmlLogRecord record;

    public LogPropertyDialog(Shell parentShell, XmlLogRecord record) {
        super(parentShell);
        this.record = record;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        // create a composite with standard margins and spacing
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout glContainer = new GridLayout(1, false);
        glContainer.marginWidth = 0;
        container.setLayout(glContainer);

        Composite mainComposite = new Composite(container, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout glMainComposite = new GridLayout(2, false);
        glMainComposite.marginWidth = 10;
        glMainComposite.horizontalSpacing = 15;
        mainComposite.setLayout(glMainComposite);

        Label lblRecordTime = new Label(mainComposite, SWT.NONE);
        lblRecordTime.setText(StringConstants.DIA_LBL_TIME);
        ControlUtils.setFontToBeBold(lblRecordTime);

        StyledText txtRecordTime = new StyledText(mainComposite, SWT.BORDER | SWT.READ_ONLY);
        GridData gd_txtRecordTime = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_txtRecordTime.heightHint = 18;
        txtRecordTime.setLayoutData(gd_txtRecordTime);
        txtRecordTime.setText(record.getLogTimeString());

        Label lblLevel = new Label(mainComposite, SWT.NONE);
        lblLevel.setText(StringConstants.DIA_LBL_LEVEL);
        ControlUtils.setFontToBeBold(lblLevel);
        
        StyledText txtLevel = new StyledText(mainComposite, SWT.BORDER | SWT.READ_ONLY);
        GridData gd_txtLevel = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        gd_txtLevel.heightHint = 18;
        txtLevel.setLayoutData(gd_txtLevel);
        txtLevel.setText(record.getLevel().toString());

        Label lblMessage = new Label(mainComposite, SWT.NONE);
        lblMessage.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
        lblMessage.setText(StringConstants.DIA_LBL_MESSAGE);
        ControlUtils.setFontToBeBold(lblMessage);
        
        StyledText txtRecordMessage = new StyledText(mainComposite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI
                | SWT.V_SCROLL);
        txtRecordMessage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        txtRecordMessage.setText(record.getMessage());

        Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));

        return container;
    }

    @Override
    protected void setShellStyle(int arg0) {
        super.setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.RESIZE);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 400);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(StringConstants.DIA_TITLE_LOG_PROPERTIES);
    }
}
