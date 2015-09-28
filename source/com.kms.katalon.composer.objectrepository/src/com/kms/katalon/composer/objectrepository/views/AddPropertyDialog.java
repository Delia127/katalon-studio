package com.kms.katalon.composer.objectrepository.views;

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

import com.kms.katalon.composer.objectrepository.constants.StringConstants;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class AddPropertyDialog extends Dialog {

	private String name;
	private String value;
	private String condition;
	
	Text txtName, txtValue;
	Combo cbbConditions;
	
	public AddPropertyDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		container.setLayout(new GridLayout(2, false));
		
		Label l = new Label(container, SWT.NONE);
		l.setText(StringConstants.VIEW_LBL_NAME);
		
		txtName = new Text(container, SWT.BORDER);
		txtName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		l = new Label(container, SWT.NONE);
		l.setText(StringConstants.VIEW_LBL_MATCH_COND);
		
		cbbConditions = new Combo(container, SWT.NONE);
		cbbConditions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cbbConditions.setItems(WebElementPropertyEntity.MATCH_CONDITION.getTextVlues());
		cbbConditions.select(0);
		
		l = new Label(container, SWT.NONE);
		l.setText(StringConstants.VIEW_LBL_VALUE);
				
		txtValue = new Text(container, SWT.BORDER);
		txtValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		return area;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(StringConstants.VIEW_LBL_ADD_PROPERTY);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 230);
	}

	@Override
	protected void okPressed() {
		name = txtName.getText();
		value = txtValue.getText();
		condition = cbbConditions.getItem(cbbConditions.getSelectionIndex());
		if (name.trim().equals("")){
			MessageDialog.openWarning(getParentShell(), StringConstants.WARN_TITLE, 
					StringConstants.VIEW_WARN_MSG_PROPERTY_CANNOT_BE_BLANK);
		}
		else{
			super.okPressed();
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
}