package com.kms.katalon.composer.components.impl.dialogs;

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
import com.kms.katalon.dal.fileservice.EntityService;
import com.kms.katalon.dal.fileservice.exception.InvalidFileNameException;
import com.kms.katalon.entity.folder.FolderEntity;

public class NewEntityDialog extends TitleAreaDialog {

	private String name;

	private String lblName = StringConstants.DIA_LBL_NAME;

	private String dialogTitle = "";

	private String dialogMsg = StringConstants.DIA_LBL_CREATE_NEW;

	private Text txtName;

	private Composite container;

	private FolderEntity parentFolder;

	private boolean isFileCreating = true;

	public NewEntityDialog(Shell parentShell, FolderEntity parentFolder) {
		super(parentShell);
		this.parentFolder = parentFolder;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// Set window title for dialog
		if (getShell() != null) getShell().setText(StringConstants.DIA_WINDOW_TITLE_NEW);

		Composite area = (Composite) super.createDialogArea(parent);
		container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setLayout(new GridLayout(2, false));
		Label labelName = new Label(container, SWT.NONE);
		labelName.setText(getLblName());

		txtName = new Text(container, SWT.BORDER);
		txtName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		updateName();
		txtName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				validateName();
				updateStatus();
			}
		});

		// Build the separator line
		Label separator = new Label(area, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return area;
	}

	private void updateStatus() {
		super.getButton(OK).setEnabled(validateName());
	}

	private void updateName() {
		txtName.setText(name);
		txtName.selectAll();
	}

	private boolean validateName() {
		String entityName = txtName.getText();
		try {
			if (entityName == null || entityName.trim().isEmpty()) {
				throw new InvalidFileNameException(StringConstants.DIA_NAME_CANNOT_BE_BLANK_OR_EMPTY);
			}
			if (!EntityService.getInstance().getAvailableName(parentFolder.getLocation(), entityName, isFileCreating())
					.equalsIgnoreCase(entityName)) {
				throw new InvalidFileNameException(StringConstants.DIA_NAME_EXISTED);
			}
			EntityService.getInstance().validateName(entityName);
			setErrorMessage(null);
			return true;
		} catch (Exception e) {
			setErrorMessage(e.getMessage());
			return false;
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		updateStatus();
	}

	@Override
	protected Point getInitialSize() {
		Point initSize = super.getInitialSize();
		return new Point(initSize.x, 250);
	}

	@Override
	public void create() {
		super.create();
		setTitle(getDialogTitle());
		setMessage(getDialogMsg(), IMessageProvider.INFORMATION);
	}

	@Override
	protected void okPressed() {
		name = txtName.getText();
		super.okPressed();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLblName() {
		return lblName;
	}

	public void setLblName(String lblName) {
		this.lblName = lblName;
	}

	public String getDialogTitle() {
		return dialogTitle;
	}

	public void setDialogTitle(String dialogTitle) {
		this.dialogTitle = dialogTitle;
	}

	public String getDialogMsg() {
		return dialogMsg;
	}

	public void setDialogMsg(String dialogMsg) {
		this.dialogMsg = dialogMsg;
	}

	public boolean isFileCreating() {
		return isFileCreating;
	}

	public void setFileCreating(boolean isFileCreating) {
		this.isFileCreating = isFileCreating;
	}

}
