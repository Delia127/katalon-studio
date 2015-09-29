package com.kms.katalon.composer.integration.qtest.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.integration.qtest.constant.StringConstants;
import com.kms.katalon.core.logging.model.TestCaseLogRecord;
import com.kms.katalon.integration.qtest.entity.QTestLog;

public class ReportUploadingPreviewDialog extends Dialog {

    private Composite container;
    private Text txtMessage;
    private Button btnAttachment;

    private TestCaseLogRecord testCaseLogRecord;
    private Text txtStatus;
    private QTestLog testLog;

    public ReportUploadingPreviewDialog(Shell parentShell, TestCaseLogRecord testCaseLogRecord) {
        super(parentShell);
        this.testCaseLogRecord = testCaseLogRecord;
    }

    protected Control createDialogArea(Composite parent) {
        container = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) container.getLayout();
        gridLayout.horizontalSpacing = 15;
        gridLayout.numColumns = 2;

        Label lblStatus = new Label(container, SWT.NONE);
        lblStatus.setText(StringConstants.STATUS);

        txtStatus = new Text(container, SWT.BORDER | SWT.READ_ONLY);
        txtStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblAttachment = new Label(container, SWT.NONE);
        lblAttachment.setText(StringConstants.ATTACHMENT);

        btnAttachment = new Button(container, SWT.CHECK);

        Label lblMessage = new Label(container, SWT.NONE);
        GridData gdLblMessage = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        gdLblMessage.verticalIndent = 5;
        lblMessage.setLayoutData(gdLblMessage);
        lblMessage.setText(StringConstants.MESSAGE);

        txtMessage = new Text(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
        txtMessage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        return container;
    }

    @Override
    public void create() {
        super.create();
        initialize();
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 350);
    }

    @Override
    protected void setShellStyle(int arg) {
        super.setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.RESIZE);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(StringConstants.DIA_TITLE_TEST_LOG_UPLOADING_PREVIEW);
    }

    protected void okPressed() {
        testLog = new QTestLog();
        testLog.setAttachmentIncluded(btnAttachment.getSelection());
        testLog.setMessage(txtMessage.getText());
        super.okPressed();
    }

    private void initialize() {
        txtStatus.setText(testCaseLogRecord.getStatus().getStatusValue().name());
        btnAttachment.setSelection(true);
        if (testCaseLogRecord.getMessage() != null) {
            txtMessage.setText(testCaseLogRecord.getMessage());
        }
    }

    public QTestLog getPreparedQTestLog() {
        return testLog;
    }
}
