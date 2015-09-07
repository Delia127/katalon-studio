package com.kms.katalon.composer.testsuite.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.entity.link.IterationEntity;
import com.kms.katalon.entity.link.IterationType;

public class DataIterationDialog extends Dialog {

	private IterationEntity iterationEntity;
	private Spinner spinnerFrom;
	private Spinner spinnerTo;
	private Button btnRunAllRows;
	private Button btnRunFromRow;
	private Button btnRunSpecificRows;
	private Text textSpecificRow;
	public DataIterationDialog(Shell parentShell, IterationEntity iterationEntity) {
		super(parentShell);
		this.iterationEntity = iterationEntity;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText(StringConstants.DIA_SHELL_DATA_ITERATION);
		registerListeners();
		initSelection();
	}

	private void initSelection() {
		switch (iterationEntity.getIterationType()) {
		case ALL:
			btnRunAllRows.setSelection(true);
			spinnerFrom.setEnabled(false);
			spinnerTo.setEnabled(false);
			textSpecificRow.setEnabled(false);
			break;
		case RANGE:
			btnRunFromRow.setSelection(true);
			spinnerFrom.setEnabled(true);
			spinnerTo.setEnabled(true);
			textSpecificRow.setEnabled(false);

			spinnerFrom.setSelection(iterationEntity.getFrom());
			spinnerTo.setSelection(iterationEntity.getTo());
			break;
		case SPECIFIC:
			btnRunSpecificRows.setSelection(true);
			spinnerFrom.setEnabled(false);
			spinnerTo.setEnabled(false);
			
			textSpecificRow.setEnabled(true);
			textSpecificRow.setText(iterationEntity.getValue());
			break;
		}
	}

	private void registerListeners() {
		btnRunAllRows.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				spinnerFrom.setEnabled(false);
				spinnerTo.setEnabled(false);
				textSpecificRow.setEnabled(false);
			}

		});

		btnRunFromRow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				spinnerFrom.setEnabled(true);
				spinnerTo.setEnabled(true);
				textSpecificRow.setEnabled(false);
			}
		});
		
		spinnerFrom.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				validateIteration();
			}
		});
		
		spinnerTo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				validateIteration();
			}
		});
		

		btnRunSpecificRows.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textSpecificRow.setEnabled(true);
				spinnerFrom.setEnabled(false);
				spinnerTo.setEnabled(false);
			}
		});
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 4;

		btnRunAllRows = new Button(container, SWT.RADIO);
		btnRunAllRows.setText(StringConstants.DIA_BTN_RUN_ALL_ROWS);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

		btnRunFromRow = new Button(container, SWT.RADIO);
		btnRunFromRow.setText(StringConstants.DIA_BTN_RUN_FROM_ROW);

		spinnerFrom = new Spinner(container, SWT.BORDER);
		spinnerFrom.setMinimum(1);
		spinnerFrom.setIncrement(1);

		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setText(StringConstants.DIA_LBL_TO_ROW);

		spinnerTo = new Spinner(container, SWT.BORDER);
		spinnerTo.setMinimum(1);
		spinnerTo.setIncrement(1);
		
		btnRunSpecificRows = new Button(container, SWT.RADIO);
		btnRunSpecificRows.setText(StringConstants.DIA_BTN_RUN_SPECIFIC_ROWS);
		
		textSpecificRow = new Text(container, SWT.BORDER);
		textSpecificRow.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		return container;
	}

	@Override
	protected void okPressed() {
		prepareReturnValue();
		super.okPressed();
	}

	private void prepareReturnValue() {
		if (btnRunAllRows.getSelection()) {
			iterationEntity.setIterationType(IterationType.ALL);
		}
		
		if (btnRunFromRow.getSelection()) {
			iterationEntity.setIterationType(IterationType.RANGE);
			iterationEntity.setRangeValue(spinnerFrom.getSelection(), spinnerTo.getSelection());
		}
		
		if (btnRunSpecificRows.getSelection()) {
			iterationEntity.setIterationType(IterationType.SPECIFIC);
			iterationEntity.setSpecificValue(textSpecificRow.getText());
		}
	}
	
	private void validateIteration() {
		if (spinnerFrom.getSelection() > spinnerTo.getSelection()) {
			getButton(Dialog.OK).setEnabled(false);
		} else {
			getButton(Dialog.OK).setEnabled(true);
		}
	}

	public IterationEntity getIterationEntity() {
		return iterationEntity;
	}
}
