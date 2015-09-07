package com.kms.katalon.composer.execution.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.execution.constants.StringConstants;
import com.kms.katalon.core.logging.XmlLogRecord;

public class LogPropertyDialog extends Dialog {
	private static final String[] ALL_LOG_LEVEL = new String[] {
			StringConstants.DIA_LOG_LVL_START,
			StringConstants.DIA_LOG_LVL_END,
			StringConstants.DIA_LOG_LVL_INFO,
			StringConstants.DIA_LOG_LVL_PASSED,
			StringConstants.DIA_LOG_LVL_FAILED,
			StringConstants.DIA_LOG_LVL_ERROR,
			StringConstants.DIA_LOG_LVL_WARNING };
	
	private XmlLogRecord record; 
	private Text txtRecordTime;
	private Text txtRecordMessage;

	public LogPropertyDialog(Shell parentShell, XmlLogRecord record) {
		super(parentShell);
		this.record = record;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		// create a composite with standard margins and spacing
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(2, false));
		
		Label lblRecordTime = new Label(composite, SWT.NONE);
		lblRecordTime.setText(StringConstants.DIA_LBL_TIME);
		
		txtRecordTime = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		txtRecordTime.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtRecordTime.setText(record.getLogTimeString());
		
		Label lblLevel = new Label(composite, SWT.NONE);
		lblLevel.setText(StringConstants.DIA_LBL_LEVEL);
		
		Combo cbbLevel = new Combo(composite, SWT.READ_ONLY);
		cbbLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		cbbLevel.setItems(ALL_LOG_LEVEL);
		cbbLevel.setText(record.getLevel().toString());
		
		Label lblMessage = new Label(composite, SWT.NONE);
		GridData gd_lblMessage = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
		gd_lblMessage.verticalIndent = 5;
		lblMessage.setLayoutData(gd_lblMessage);
		lblMessage.setText(StringConstants.DIA_LBL_MESSAGE);
		
		txtRecordMessage = new Text(composite, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		txtRecordMessage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		txtRecordMessage.setText(record.getMessage());
		
		return composite;
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
		newShell.setText(StringConstants.DIA_TITLE_PROPERTIES);
	}
}
