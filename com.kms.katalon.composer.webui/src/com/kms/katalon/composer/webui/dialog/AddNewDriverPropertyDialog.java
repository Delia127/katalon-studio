package com.kms.katalon.composer.webui.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.webui.constants.StringConstants;
import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.webui.setting.DriverProperty;
import com.kms.katalon.core.webui.setting.WebUiSettingStore;

public class AddNewDriverPropertyDialog extends Dialog {
	private Text txtDriverPropertyName;
	private Text txtDriverPropertyValue;
	private DriverType driverType;
	private Combo cbDriverPropertyType;

	private List<String> existingNames;
	private DriverProperty existingProperty;

	public AddNewDriverPropertyDialog(Shell parentShell, DriverType driverType, List<String> existingName,
			DriverProperty existingProperty) {
		super(parentShell);
		this.driverType = driverType;
		this.existingNames = existingName;
		this.existingProperty = existingProperty;
	}

	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) container.getLayout();
		gridLayout.numColumns = 2;

		Label lblName = new Label(container, SWT.NONE);
		lblName.setText(StringConstants.DIA_LBL_NAME);

		txtDriverPropertyName = new Text(container, SWT.BORDER);
		txtDriverPropertyName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblType = new Label(container, SWT.NONE);
		lblType.setText(StringConstants.DIA_LBL_TYPE);

		cbDriverPropertyType = new Combo(container, SWT.READ_ONLY);
		cbDriverPropertyType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblText = new Label(container, SWT.NONE);
		lblText.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblText.setText(StringConstants.DIA_LBL_VALUE);

		txtDriverPropertyValue = new Text(container, SWT.BORDER);
		txtDriverPropertyValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		return container;
	}

	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		initialize();
		addModifyListeners();
		validate();
		return control;
	}

	private void initialize() {
		cbDriverPropertyType.setItems(new String[] { String.class.getSimpleName(), Integer.class.getSimpleName(),
				Boolean.class.getSimpleName() });
		if (existingProperty == null) {
			txtDriverPropertyName.setText("");
			cbDriverPropertyType.select(0);
			txtDriverPropertyValue.setText("");
		} else {
			txtDriverPropertyName.setText(existingProperty.getName());
			txtDriverPropertyValue.setText(String.valueOf(existingProperty.getValue()));

			cbDriverPropertyType.setText(existingProperty.getValue().getClass().getSimpleName());
		}

		txtDriverPropertyName.selectAll();
	}

	private void addModifyListeners() {
		txtDriverPropertyName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		txtDriverPropertyValue.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		});

		cbDriverPropertyType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
			}
		});
	}

	private void validate() {
		enableOKButton(isNameValid() && isTypeValid());
	}

	private boolean isTypeValid() {
		String name = txtDriverPropertyName.getText().trim();
		String value = txtDriverPropertyValue.getText();
		String type = cbDriverPropertyType.getText();
		if (name.isEmpty() || value.isEmpty()) {
			return false;
		} else {
			if (!WebUiSettingStore.isValidPropertyName(name))
				return false;

			if (type.equals(Integer.class.getSimpleName())) {
				if (WebUiSettingStore.getValue(value.trim()) instanceof Integer) {
					return true;
				}
			} else if (type.equals(Boolean.class.getSimpleName())) {
				if (WebUiSettingStore.getValue(value.toLowerCase().trim()) instanceof Boolean) {
					return true;
				}
			} else if (type.equals(String.class.getSimpleName())) {
				return true;
			}
			return false;
		}
	}

	private boolean isNameValid() {
		String name = txtDriverPropertyName.getText();
		if (existingNames != null && existingNames.contains(name)) {
			return false;
		}
		return true;
	}

	private void enableOKButton(boolean enable) {
		getButton(OK).setEnabled(enable);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 300);
	}

	@Override
	protected void configureShell(Shell newShell) {		
		super.configureShell(newShell);
		if (existingProperty == null) {
			newShell.setText(StringConstants.DIA_SHELL_ADD_NEW_PREFERENCE);
		} else {
			newShell.setText(StringConstants.DIA_SHELL_EDIT_PREFERENCE);
		}
	}
	
	@Override
	protected boolean isResizable()  {
	  return true;
	}

	private DriverProperty driverProperty;

	@Override
	protected void okPressed() {
		String rawValue = txtDriverPropertyValue.getText();

		if (cbDriverPropertyType.getText().equals(String.class.getSimpleName())) {
			rawValue = "\"" + rawValue + "\"";
		} else {
			rawValue = rawValue.toLowerCase().trim();
		}

		setDriverProperty(new DriverProperty(txtDriverPropertyName.getText().trim(), WebUiSettingStore.getValue(rawValue),
				driverType));
		super.okPressed();
	}

	public DriverProperty getDriverProperty() {
		return driverProperty;
	}

	private void setDriverProperty(DriverProperty driverProperty) {
		this.driverProperty = driverProperty;
	}

}
