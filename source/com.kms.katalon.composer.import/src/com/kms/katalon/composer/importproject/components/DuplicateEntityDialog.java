package com.kms.katalon.composer.importproject.components;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.importproject.constants.StringConstants;
import com.kms.katalon.entity.util.ImportDuplicateEntityParameter;
import com.kms.katalon.entity.util.ImportType;

public class DuplicateEntityDialog extends Dialog {

	private static final String DIALOG_MESSAGE_SUFFIX = StringConstants.COMP_DIALOG_MSG_SUFFIX;
	private static final String DIALOG_MESSAGE_PREFIX = StringConstants.COMP_DIALOG_MSG_PREFIX;
	
	private static final String MERGE_BUTTON_LABEL = StringConstants.COMP_BTN_MERGE;
	private static final String OVERRIDE_BUTTON_LABEL = StringConstants.COMP_BTN_OVERRIDE;
	private static final String CREATE_NEW_BUTTON_LABEL = StringConstants.COMP_BTN_CREATE_NEW;

	public static final int MERGE_BUTTON_ID = 4;
	public static final int OVERRIDE_BUTTON_ID = 3;
	public static final int CREAT_NEW_BUTTON_ID = 2;

	private static final String DIALOG_TITLE = StringConstants.COMP_DIALOG_TITLE_IMPORT_PROJECT;
	private static final String APPLY_TO_ALL_CHECKBOX_TEXT = StringConstants.COMP_CHKBOX_APPLY_TO_ALL;

	private ImportDuplicateEntityParameter importParams;

	private Button applyToAllCb;
	private boolean isApplyToAll;

	public DuplicateEntityDialog(Shell parentShell, ImportDuplicateEntityParameter importParams) {
		super(parentShell);
		this.importParams = importParams;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(DIALOG_MESSAGE_PREFIX).append(importParams.getMessage()).append(DIALOG_MESSAGE_SUFFIX);
		
		Label label = new Label(container, SWT.NONE);
		label.setText(stringBuilder.toString());
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		label.pack();
		
		container.layout(true, true);
		final Point newSize = container.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);  
		container.setSize(newSize);
		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button newButton = createButton(parent, CREAT_NEW_BUTTON_ID, CREATE_NEW_BUTTON_LABEL, true);
		newButton.setEnabled(false);
		
		Button overrideButton = createButton(parent, OVERRIDE_BUTTON_ID, OVERRIDE_BUTTON_LABEL, false);
		overrideButton.setEnabled(false);
		
		Button mergeButton = createButton(parent, MERGE_BUTTON_ID, MERGE_BUTTON_LABEL, false);
		mergeButton.setEnabled(false);
		
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		
		for (ImportType importType : importParams.getAvailableImportTypes()) {
			switch (importType) {
				case New:
					newButton.setEnabled(true);
					break;
				case Override:
					overrideButton.setEnabled(true);
					break;
				case Merge:
					mergeButton.setEnabled(true);
					break;
				default:
					break;
			}
		}
	}
	
	@Override
	protected Control createButtonBar(Composite parent) {
		createApplyToAllComposite(parent);
		
		return super.createButtonBar(parent);
	}

	private void createApplyToAllComposite(Composite parent) {
		Composite applyToAllComposite = new Composite(parent, SWT.NONE);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginLeft = 10;
		applyToAllComposite.setLayout(gridLayout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING
				| GridData.VERTICAL_ALIGN_BEGINNING);
		applyToAllComposite.setLayoutData(data);

		applyToAllCb = new Button(applyToAllComposite, SWT.CHECK);
		applyToAllCb.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		applyToAllCb.setText(APPLY_TO_ALL_CHECKBOX_TEXT);

		isApplyToAll = false;
		applyToAllCb.setSelection(isApplyToAll);
		applyToAllCb.pack();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(DIALOG_TITLE);
	}

	@Override
	protected void buttonPressed(int buttonId) {
		setReturnCode(buttonId);
		isApplyToAll = applyToAllCb.getSelection();
		close();
	}

	public boolean getIsApplyToAll() {
		return isApplyToAll;
	}
}
