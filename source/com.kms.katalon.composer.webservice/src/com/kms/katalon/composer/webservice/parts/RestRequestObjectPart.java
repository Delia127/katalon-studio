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

public class RestRequestObjectPart extends RequestObjectPart {

    private Text txtRestUrl;

    private Combo cbbRestRequestMethod;

    private ParameterTable tblRestParams;

    private List<WebElementPropertyEntity> listRestParams = new ArrayList<WebElementPropertyEntity>();

    @PostConstruct
    public void createComposite(Composite parent, MPart part) {
        super.createComposite(parent, part);
    }

    @Override
    protected void createServiceInfoComposite(Composite mainComposite) {

        ExpandableComposite restComposite = new ExpandableComposite(mainComposite, StringConstants.PA_TITLE_REST, 1,
                true);
        Composite compositeDetails = restComposite.createControl();

        Composite restContainerComposite = new Composite(compositeDetails, SWT.NONE);
        restContainerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        restContainerComposite.setLayout(new GridLayout(2, false));

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gridData.heightHint = 20;

        // Service Url
        Label lblRestUrl = new Label(restContainerComposite, SWT.LEFT | SWT.WRAP);
        lblRestUrl.setText(StringConstants.PA_LBL_REST_URL);
        lblRestUrl.setLayoutData(labelGridData);

        txtRestUrl = new Text(restContainerComposite, SWT.BORDER);
        txtRestUrl.setLayoutData(gridData);
        txtRestUrl.addModifyListener(modifyListener);

        // Request Method
        Label lblRequestMethod = new Label(restContainerComposite, SWT.LEFT | SWT.WRAP);
        lblRequestMethod.setText(StringConstants.PA_LBL_REQ_METHOD);
        lblRequestMethod.setLayoutData(labelGridData);

        cbbRestRequestMethod = new Combo(restContainerComposite, SWT.NONE);
        cbbRestRequestMethod.setLayoutData(gridData);
        cbbRestRequestMethod.setItems(WebServiceRequestEntity.REST_REQUEST_METHODS);
        cbbRestRequestMethod.select(0);
        cbbRestRequestMethod.addModifyListener(modifyListener);

        // REST Parameters
        Label lblRestParam = new Label(restContainerComposite, SWT.LEFT | SWT.WRAP);
        lblRestParam.setText(StringConstants.PA_LBL_REST_PARAMS);
        lblRestParam.setLayoutData(labelGridData);

        tblRestParams = createParamsTable(restContainerComposite);
        tblRestParams.setInput(listRestParams);
    }

    @Override
    protected void updateEntityBeforeSaved() {
        super.updateEntityBeforeSaved();
        // Update object properties
        originalWsObject.setRestUrl(txtRestUrl.getText());
        originalWsObject.setRestRequestMethod(cbbRestRequestMethod.getText());
        originalWsObject.setRestParameters(listRestParams);

        tblRestParams.removeEmptyProperty();
    }

    @Override
    protected void showEntityFieldsToUi() {
        super.showEntityFieldsToUi();
        txtRestUrl.setText(originalWsObject.getRestUrl());
        int index = Arrays.asList(WebServiceRequestEntity.REST_REQUEST_METHODS).indexOf(
                originalWsObject.getRestRequestMethod());
        cbbRestRequestMethod.select(index < 0 ? 0 : index);
        listRestParams.addAll(originalWsObject.getRestParameters());
        tblRestParams.refresh();
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
