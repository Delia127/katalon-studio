package com.kms.katalon.composer.webservice.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.view.ExpandableComposite;
import com.kms.katalon.composer.webservice.view.ParameterTable;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class SoapRequestObjectPart extends RequestObjectPart {

    private Text txtWSDL;

    private Text txtSoapHeader;

    private Text txtSoapBody;

    private Text txtServiceFunction;

    private Combo cbbSoapRequestMethod;

    private ParameterTable tblSoapParams;

    private List<WebElementPropertyEntity> listSoapParams = new ArrayList<WebElementPropertyEntity>();

    @PostConstruct
    public void createComposite(Composite parent, MPart part) {
        super.createComposite(parent, part);
    }

    @Override
    public void createServiceInfoComposite(Composite mainComposite) {
        ExpandableComposite soapComposite = new ExpandableComposite(mainComposite, StringConstants.PA_TITLE_SOAP, 1,
                true);
        Composite compositeDetails = soapComposite.createControl();

        Composite soapDetailsComposite = new Composite(compositeDetails, SWT.NONE);
        soapDetailsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        soapDetailsComposite.setLayout(new GridLayout(2, false));

        GridData gdData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdData.heightHint = 20;

        // WSDL
        Label lblWsdl = new Label(soapDetailsComposite, SWT.LEFT | SWT.WRAP);
        lblWsdl.setText(StringConstants.PA_LBL_WSDL_ADDR);
        lblWsdl.setLayoutData(labelGridData);

        txtWSDL = new Text(soapDetailsComposite, SWT.BORDER);
        txtWSDL.setLayoutData(gdData);
        txtWSDL.addModifyListener(modifyListener);

        // Request Method
        Label lblRequestMethod = new Label(soapDetailsComposite, SWT.LEFT | SWT.WRAP);
        lblRequestMethod.setText(StringConstants.PA_LBL_REQ_METHOD);
        lblRequestMethod.setLayoutData(labelGridData);

        cbbSoapRequestMethod = new Combo(soapDetailsComposite, SWT.NONE);
        cbbSoapRequestMethod.setLayoutData(gdData);
        cbbSoapRequestMethod.setItems(WebServiceRequestEntity.SOAP_REQUEST_METHODS);
        cbbSoapRequestMethod.select(0);
        cbbSoapRequestMethod.addModifyListener(modifyListener);

        // Service Function
        Label lblServiceFunction = new Label(soapDetailsComposite, SWT.LEFT | SWT.WRAP);
        lblServiceFunction.setText(StringConstants.PA_LBL_SERVICE_FUNCTION);
        lblServiceFunction.setLayoutData(labelGridData);

        txtServiceFunction = new Text(soapDetailsComposite, SWT.BORDER);
        txtServiceFunction.setLayoutData(gdData);
        txtServiceFunction.addModifyListener(modifyListener);

        // Service Function Parameters
        Label lblFunctionParams = new Label(soapDetailsComposite, SWT.LEFT | SWT.WRAP);
        lblFunctionParams.setText(StringConstants.PA_LBL_PARAMS);
        lblFunctionParams.setLayoutData(labelGridData);

        tblSoapParams = createParamsTable(soapDetailsComposite);
        tblSoapParams.setInput(listSoapParams);

        // SOAP Header
        Label lblSoapHeader = new Label(soapDetailsComposite, SWT.LEFT | SWT.WRAP);
        lblSoapHeader.setText(StringConstants.PA_LBL_SOAP_HEADER_OPT);
        lblSoapHeader.setLayoutData(labelGridData);

        txtSoapHeader = new Text(soapDetailsComposite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        GridData newGdData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        newGdData.heightHint = 45;
        txtSoapHeader.setLayoutData(newGdData);
        txtSoapHeader.addModifyListener(modifyListener);

        // SOAP Body
        Label lblSoapBody = new Label(soapDetailsComposite, SWT.LEFT | SWT.WRAP);
        lblSoapBody.setText(StringConstants.PA_LBL_SOAP_BODY);
        lblSoapBody.setLayoutData(labelGridData);

        txtSoapBody = new Text(soapDetailsComposite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        txtSoapBody.setLayoutData(newGdData);
        txtSoapBody.addModifyListener(modifyListener);
    }

    @Override
    protected void updateEntityBeforeSaved() {
        super.updateEntityBeforeSaved();
        // Update object properties
        originalWsObject.setWsdlAddress(txtWSDL.getText());
        originalWsObject.setSoapRequestMethod(cbbSoapRequestMethod.getText());
        originalWsObject.setSoapServiceFunction(txtServiceFunction.getText());
        originalWsObject.setSoapParameters(listSoapParams);
        originalWsObject.setSoapHeader(txtSoapHeader.getText());
        originalWsObject.setSoapBody(txtSoapBody.getText());

        tblSoapParams.removeEmptyProperty();
    }

    @Override
    protected void showEntityFieldsToUi() {
        super.showEntityFieldsToUi();
        txtWSDL.setText(originalWsObject.getWsdlAddress());
        int index = Arrays.asList(WebServiceRequestEntity.SOAP_REQUEST_METHODS).indexOf(
                originalWsObject.getSoapRequestMethod());
        cbbSoapRequestMethod.select(index < 0 ? 0 : index);
        txtServiceFunction.setText(originalWsObject.getSoapServiceFunction());
        txtSoapHeader.setText(originalWsObject.getSoapHeader());
        txtSoapBody.setText(originalWsObject.getSoapBody());
        tempPropList = new ArrayList<WebElementPropertyEntity>(originalWsObject.getSoapParameters());
        listSoapParams.clear();
        listSoapParams.addAll(tempPropList);
        tblSoapParams.refresh();
        dirtyable.setDirty(false);
    }

    @Persist
    public void save() {
        super.save();
    }

    @PreDestroy
    public void destroy() {
    }
}
