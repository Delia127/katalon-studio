package com.kms.katalon.composer.webservice.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class WebServiceAPIControl extends Composite {

    private static final int DEFAULT_HEIGHT = 20;

    private static final int DEFAULT_REQUEST_METHOD_SELECTION_INDEX = 0;

    private CCombo cbRequestMethod;

    private Text txtRequestURL;

    private Button btnSend;

    private GridData layoutData;

    public WebServiceAPIControl(Composite parent, boolean isSOAP) {
        super(parent, SWT.NONE);
        createControl();
        setInput(isSOAP);
    }

    private void createControl() {
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.verticalSpacing = 0;
        setLayout(gridLayout);
        setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Composite fieldsComposite = new Composite(this, SWT.BORDER);
        GridLayout glFieldsComp = new GridLayout(3, false);
        glFieldsComp.marginHeight = 0;
        glFieldsComp.marginWidth = 0;
        glFieldsComp.verticalSpacing = 0;
        fieldsComposite.setLayout(glFieldsComp);
        fieldsComposite.setBackground(ColorUtil.getWhiteBackgroundColor());
        GridData gdFieldsComp = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdFieldsComp.heightHint = DEFAULT_HEIGHT;
        fieldsComposite.setLayoutData(gdFieldsComp);

        cbRequestMethod = new CCombo(fieldsComposite, SWT.FLAT | SWT.READ_ONLY);
        GridData gdRequestMethod = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        gdRequestMethod.widthHint = 100;
        cbRequestMethod.setLayoutData(gdRequestMethod);

        Label separator = new Label(fieldsComposite, SWT.SEPARATOR | SWT.VERTICAL);
        GridData gdSeparator = new GridData(SWT.CENTER, SWT.CENTER, false, true);
        gdSeparator.heightHint = 18;
        separator.setLayoutData(gdSeparator);

        txtRequestURL = new Text(fieldsComposite, SWT.NONE);
        txtRequestURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        txtRequestURL.setMessage(StringConstants.PA_LBL_URL);

        btnSend = new Button(this, SWT.FLAT);
        btnSend.setText(ComposerWebserviceMessageConstants.BTN_SEND_TEST_REQUEST);
        GridData gdBtnSend = new GridData(SWT.CENTER, SWT.FILL, false, true);
        gdBtnSend.widthHint = 100;
        btnSend.setLayoutData(gdBtnSend);
    }

    public void addRequestMethodModifyListener(ModifyListener modifyListener) {
        if (modifyListener == null) {
            return;
        }
        cbRequestMethod.addModifyListener(modifyListener);
    }

    public void addRequestMethodSelectionListener(SelectionListener selectionListener) {
        if (selectionListener == null) {
            return;
        }
        cbRequestMethod.addSelectionListener(selectionListener);
    }

    public void addRequestURLModifyListener(ModifyListener modifyListener) {
        if (modifyListener == null) {
            return;
        }
        txtRequestURL.addModifyListener(modifyListener);
    }

    public void addSendSelectionListener(SelectionListener selectionListener) {
        if (selectionListener == null) {
            return;
        }
        btnSend.addSelectionListener(selectionListener);
    }

    private void setInput(boolean isSOAP) {
        cbRequestMethod.setItems(
                isSOAP ? WebServiceRequestEntity.SOAP_REQUEST_METHODS : WebServiceRequestEntity.REST_REQUEST_METHODS);
        cbRequestMethod.select(DEFAULT_REQUEST_METHOD_SELECTION_INDEX);
    }

    public void setRequestMethodSelection(int index) {
        cbRequestMethod.select(index);
    }

    public int getSelectedRequestMethodIndex() {
        return cbRequestMethod.getSelectionIndex();
    }

    public String getRequestMethod() {
        return cbRequestMethod.getText();
    }

    public String getRequestURL() {
        return txtRequestURL.getText();
    }

    public void setLayoutDataColumnsSpan(int numberOfColumn) {
        layoutData.horizontalSpan = numberOfColumn;
        layout();
    }

    public CCombo getRequestMethodControl() {
        return cbRequestMethod;
    }

    public Text getRequestURLControl() {
        return txtRequestURL;
    }

    public Button getSendControl() {
        return btnSend;
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

}
