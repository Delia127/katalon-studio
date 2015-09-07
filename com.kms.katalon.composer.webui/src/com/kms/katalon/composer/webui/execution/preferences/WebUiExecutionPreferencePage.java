package com.kms.katalon.composer.webui.execution.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.constants.StringConstants;

public class WebUiExecutionPreferencePage extends PreferencePage {
	private Text txtWaitForIEHanging;
	private Button radioBtnFirefox, radioBtnChrome, radioBtnIE, radioBtnSafari;
	private Composite fieldEditorParent;

	public WebUiExecutionPreferencePage() {
	}

	@Override
	protected Control createContents(Composite parent) {
		fieldEditorParent = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 10;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		fieldEditorParent.setLayout(layout);

		Group grpDefaultBrowser = new Group(fieldEditorParent, SWT.NONE);
		grpDefaultBrowser.setText(StringConstants.PREF_GRP_DEFAULT_BROWSER);
		grpDefaultBrowser.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		GridLayout glGrpDefaultBrowser = new GridLayout(1, false);
		glGrpDefaultBrowser.marginWidth = 0;
		glGrpDefaultBrowser.marginLeft = 15;
		grpDefaultBrowser.setLayout(glGrpDefaultBrowser);

		radioBtnFirefox = new Button(grpDefaultBrowser, SWT.RADIO);
		radioBtnFirefox.setText(StringConstants.PREF_RADIO_FIREFOX);

		radioBtnChrome = new Button(grpDefaultBrowser, SWT.RADIO);
		radioBtnChrome.setText(StringConstants.PREF_RADIO_CHROME);

		radioBtnIE = new Button(grpDefaultBrowser, SWT.RADIO);
		radioBtnIE.setText(StringConstants.PREF_RADIO_IE);

		radioBtnSafari = new Button(grpDefaultBrowser, SWT.RADIO);
		radioBtnSafari.setText(StringConstants.PREF_RADIO_SAFARI);

		Composite composite = new Composite(fieldEditorParent, SWT.NONE);
		GridLayout glComposite = new GridLayout(2, false);
		composite.setLayout(glComposite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Label lblWaitForIEHanging = new Label(composite, SWT.NONE);
		lblWaitForIEHanging.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblWaitForIEHanging.setText(StringConstants.PREF_LBL_DEFAULT_WAIT_FOR_IE_HANGING_TIMEOUT);

		txtWaitForIEHanging = new Text(composite, SWT.BORDER);
		txtWaitForIEHanging.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		initialize();

		return fieldEditorParent;
	}

	private void initialize() {
		txtWaitForIEHanging.setText(Integer.toString(getPreferenceStore().getInt(
				PreferenceConstants.WebUiPreferenceConstants.EXECUTION_WAIT_FOR_IE_HANGING)));
		resetBrowserRadioButtonState();
		String defaultBrowserName = getPreferenceStore().getString(
				PreferenceConstants.WebUiPreferenceConstants.EXECUTION_DEFAULT_BROWSER);

		try {
			switch (WebUIDriverType.fromStringValue(defaultBrowserName)) {
			case CHROME_DRIVER:
				radioBtnChrome.setSelection(true);
				break;
			case FIREFOX_DRIVER:
				radioBtnFirefox.setSelection(true);
				break;
			case IE_DRIVER:
				radioBtnIE.setSelection(true);
				break;
			case SAFARI_DRIVER:
				radioBtnSafari.setSelection(true);
				break;
			default:
				break;
			}
		} catch (IllegalArgumentException ex) {
			radioBtnFirefox.setSelection(true);
			getPreferenceStore().setValue(PreferenceConstants.WebUiPreferenceConstants.EXECUTION_DEFAULT_BROWSER,
					WebUIDriverType.FIREFOX_DRIVER.toString());
		}
	}

	@Override
	protected void performDefaults() {
		if (fieldEditorParent == null)
			return;

		txtWaitForIEHanging.setText(Integer.toString(getPreferenceStore().getDefaultInt(
				PreferenceConstants.WebUiPreferenceConstants.EXECUTION_WAIT_FOR_IE_HANGING)));

		resetBrowserRadioButtonState();
		String defaultBrowserName = getPreferenceStore().getDefaultString(
				PreferenceConstants.WebUiPreferenceConstants.EXECUTION_DEFAULT_BROWSER);
		try {
			switch (WebUIDriverType.fromStringValue(defaultBrowserName)) {
			case CHROME_DRIVER:
				radioBtnChrome.setSelection(true);
				break;
			case FIREFOX_DRIVER:
				radioBtnFirefox.setSelection(true);
				break;
			case IE_DRIVER:
				radioBtnIE.setSelection(true);
				break;
			case SAFARI_DRIVER:
				radioBtnSafari.setSelection(true);
				break;
			default:
				break;
			}
		} catch (IllegalArgumentException ex) {
			radioBtnFirefox.setSelection(true);
			getPreferenceStore().setValue(PreferenceConstants.WebUiPreferenceConstants.EXECUTION_DEFAULT_BROWSER,
					WebUIDriverType.FIREFOX_DRIVER.toString());
		}
		super.performDefaults();
	}

	private void resetBrowserRadioButtonState() {
		radioBtnChrome.setSelection(false);
		radioBtnFirefox.setSelection(false);
		radioBtnSafari.setSelection(false);
		radioBtnIE.setSelection(false);
	}

	@Override
	protected void performApply() {
		if (fieldEditorParent == null)
			return;

		if (txtWaitForIEHanging != null) {
			getPreferenceStore().setValue(
					PreferenceConstants.WebUiPreferenceConstants.EXECUTION_WAIT_FOR_IE_HANGING,
					Integer.parseInt(txtWaitForIEHanging.getText()));
		}

		if (radioBtnChrome != null && radioBtnFirefox != null && radioBtnIE != null && radioBtnSafari != null) {
			String browserName = "";
			if (radioBtnChrome.getSelection()) {
				browserName = WebUIDriverType.CHROME_DRIVER.toString();
			} else if (radioBtnFirefox.getSelection()) {
				browserName = WebUIDriverType.FIREFOX_DRIVER.toString();
			} else if (radioBtnIE.getSelection()) {
				browserName = WebUIDriverType.IE_DRIVER.toString();
			} else if (radioBtnSafari.getSelection()) {
				browserName = WebUIDriverType.SAFARI_DRIVER.toString();
			}
			getPreferenceStore().setValue(PreferenceConstants.WebUiPreferenceConstants.EXECUTION_DEFAULT_BROWSER,
					browserName);
		}
	}

	public boolean performOk() {
		boolean result = super.performOk();
		if (result) {
			if (isValid()) {
				performApply();
			}
		}
		return true;
	}
}
