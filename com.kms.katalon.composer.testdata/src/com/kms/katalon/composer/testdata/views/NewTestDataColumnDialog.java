package com.kms.katalon.composer.testdata.views;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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

import com.kms.katalon.composer.testdata.constants.StringConstants;

public class NewTestDataColumnDialog extends Dialog {

	private String name;
	private String dataType;
	
	Text txtName;
	Combo cbDataType;
	
	public NewTestDataColumnDialog(Shell parentShell) {
		super(parentShell);
	}

	public NewTestDataColumnDialog(Shell parentShell, String name, String dataType) {
		super(parentShell);
		this.name = name;
		this.dataType = dataType;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setLayout(new GridLayout(2, false));
		
		Label theLabel = new Label(container, SWT.NONE);
		theLabel.setText(StringConstants.VIEW_COL_COL_NAME);
		
		txtName = new Text(container, SWT.BORDER);
		txtName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtName.setSize(200, SWT.DEFAULT);
		if(name != null){
			txtName.setText(name);
		}
		
		theLabel = new Label(container, SWT.NONE);
		theLabel.setText(StringConstants.VIEW_COL_DATA_TYPE);
				
		cbDataType = new Combo(container, SWT.NONE);
		
		cbDataType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		String items[] = { StringConstants.VIEW_COMBO_STRING, StringConstants.VIEW_COMBO_NUMBER };
		cbDataType.setItems(items);
		if(dataType != null){
			for(int i=0; i<items.length; i++){
				if(items[i].equalsIgnoreCase(dataType)){
					cbDataType.select(i);
					break;
				}
			}
		} else {
			cbDataType.select(0);
		}
		
		new Label(container, SWT.NONE);

		return area;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(StringConstants.VIEW_SHELL_DATA_COL_DEFINITION);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 200);
	}

	@Override
	protected void okPressed() {
		if(txtName.getText().trim().equals("")){
			MessageDialog.openInformation(getParentShell(), StringConstants.VIEW_INFO_TITLE_INVALID_DATA, 
					StringConstants.VIEW_INFO_MSG_ENTER_COL_NAME);
			return;
		}
		if(cbDataType.getSelectionIndex() < 0){
			MessageDialog.openInformation(getParentShell(), StringConstants.VIEW_INFO_TITLE_INVALID_DATA, 
					StringConstants.VIEW_INFO_MSG_SELECT_DATA_TYPE);
			return;
		}
		name = txtName.getText();
		dataType = cbDataType.getItem(cbDataType.getSelectionIndex());
		super.okPressed();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataSource) {
		this.dataType = dataSource;
	}
}
