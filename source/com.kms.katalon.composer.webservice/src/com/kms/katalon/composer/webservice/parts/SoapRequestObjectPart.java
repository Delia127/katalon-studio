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

        Composite compositeDetailsInfo = new Composite(compositeDetails, SWT.NONE);
        compositeDetailsInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        compositeDetailsInfo.setLayout(new GridLayout(2, false));

        // WSDL
        Label lblWsdl = new Label(compositeDetailsInfo, SWT.NONE);
        lblWsdl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
        lblWsdl.setText(StringConstants.PA_LBL_WSDL_ADDR);

        txtWSDL = new Text(compositeDetailsInfo, SWT.BORDER);
        GridData gdData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 2);
        gdData.heightHint = 20;
        txtWSDL.setLayoutData(gdData);
        txtWSDL.addModifyListener(modifyListener);

        Label lblSupporter = new Label(compositeDetailsInfo, SWT.NONE);
        lblSupporter.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));

        // Request Method
        Label lblRequestMethod = new Label(compositeDetailsInfo, SWT.NONE);
        lblRequestMethod.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
        lblRequestMethod.setText(StringConstants.PA_LBL_REQ_METHOD);

        cbbSoapRequestMethod = new Combo(compositeDetailsInfo, SWT.NONE);
        gdData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 2);
        gdData.heightHint = 20;
        cbbSoapRequestMethod.setLayoutData(gdData);
        cbbSoapRequestMethod.setItems(WebServiceRequestEntity.SOAP_REQUEST_METHODS);
        cbbSoapRequestMethod.select(0);
        cbbSoapRequestMethod.addModifyListener(modifyListener);

        lblSupporter = new Label(compositeDetailsInfo, SWT.NONE);
        lblSupporter.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));

        // Service Function
        Label lblServiceFunction = new Label(compositeDetailsInfo, SWT.NONE);
        lblServiceFunction.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
        lblServiceFunction.setText(StringConstants.PA_LBL_SERVICE_FUNCTION);

        txtServiceFunction = new Text(compositeDetailsInfo, SWT.BORDER);
        gdData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 2);
        gdData.heightHint = 20;
        txtServiceFunction.setLayoutData(gdData);
        txtServiceFunction.addModifyListener(modifyListener);

        lblSupporter = new Label(compositeDetailsInfo, SWT.NONE);
        lblSupporter.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));

        // Service Function Parameters
        Label lblFunctionParams = new Label(compositeDetailsInfo, SWT.NONE);
        lblFunctionParams.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
        lblFunctionParams.setText(StringConstants.PA_LBL_PARAMS);

        tblSoapParams = createParamsTable(compositeDetailsInfo);
        tblSoapParams.setInput(listSoapParams);

        lblSupporter = new Label(compositeDetailsInfo, SWT.NONE);
        lblSupporter.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));

        // SOAP Header
        Label lblSoapHeader = new Label(compositeDetailsInfo, SWT.NONE);
        lblSoapHeader.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
        lblSoapHeader.setText(StringConstants.PA_LBL_SOAP_HEADER_OPT);

        txtSoapHeader = new Text(compositeDetailsInfo, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        gdData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
        gdData.heightHint = 45;
        txtSoapHeader.setLayoutData(gdData);
        txtSoapHeader.addModifyListener(modifyListener);

        lblSupporter = new Label(compositeDetailsInfo, SWT.NONE);
        lblSupporter.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));

        // SOAP Body
        Label lblSoapBody = new Label(compositeDetailsInfo, SWT.NONE);
        lblSoapBody.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
        lblSoapBody.setText(StringConstants.PA_LBL_SOAP_BODY);

        txtSoapBody = new Text(compositeDetailsInfo, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        gdData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
        gdData.heightHint = 45;
        txtSoapBody.setLayoutData(gdData);
        txtSoapBody.addModifyListener(modifyListener);

        lblSupporter = new Label(compositeDetailsInfo, SWT.NONE);
        lblSupporter.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
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
        listSoapParams.addAll(originalWsObject.getSoapParameters());
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
