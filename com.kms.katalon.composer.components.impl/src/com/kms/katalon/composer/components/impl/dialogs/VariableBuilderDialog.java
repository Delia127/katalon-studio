package com.kms.katalon.composer.components.impl.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.groovy.constant.GroovyConstants;

public abstract class VariableBuilderDialog extends TitleAreaDialog {
	public enum DialogType {
		NEW, EDIT
	}
	protected String dialogTitle;
	protected String dialogMessage;
	protected DialogType type;
	protected Text textVariableName;
	protected Text textDefaultValue;

	public VariableBuilderDialog(Shell parentShell, DialogType type) {
		super(parentShell);
		this.type = type;
		switch (type) {
		case EDIT:
			dialogTitle = StringConstants.DIA_TITLE_EDIT_VAR;
			dialogMessage = StringConstants.DIA_INFO_MSG_EDIT_NEW_VAR;
			break;
		case NEW:
			dialogTitle = StringConstants.DIA_TITLE_NEW_VAR;
			dialogMessage = StringConstants.DIA_INFO_MSG_CREATE_NEW_VAR;
			break;
		
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		container.setLayout(new GridLayout(2, false));

		Label lblVariableName = new Label(container, SWT.NONE);
		lblVariableName.setText(StringConstants.DIA_LBL_NAME);

		textVariableName = new Text(container, SWT.BORDER);
		textVariableName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textVariableName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				String newVariableName = ((Text) e.getSource()).getText();
				if (!GroovyConstants.VARIABLE_NAME_REGEX.matcher(newVariableName).find()) {
					setErrorMessage(StringConstants.DIA_ERROR_MSG_INVALID_VAR_NAME);
					getButton(Dialog.OK).setEnabled(false);
				} else {
					setErrorMessage(null);
					getButton(Dialog.OK).setEnabled(true);
				}
			}
		});

		Label lblDefaultValue = new Label(container, SWT.NONE);
		lblDefaultValue.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblDefaultValue.setText(StringConstants.DIA_LBL_DEFAULT_VALUE);

		textDefaultValue = new Text(container, SWT.BORDER | SWT.MULTI);
		textDefaultValue.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

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
	}
}
