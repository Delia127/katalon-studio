package com.kms.katalon.composer.webservice.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.view.ExpandableComposite;
import com.kms.katalon.composer.webservice.view.ParameterTable;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class RestRequestObjectPart extends RequestObjectPart {

    private Text txtRestUrl;

    private Combo cbbRestRequestMethod;

    private ParameterTable tblRestParams;

    private List<WebElementPropertyEntity> listRestParams = new ArrayList<WebElementPropertyEntity>();

    private List<WebElementPropertyEntity> listHttpHeaderProps = new ArrayList<WebElementPropertyEntity>();

    private List<WebElementPropertyEntity> tempPropList = new ArrayList<WebElementPropertyEntity>();

    @Override
    @PostConstruct
    public void createComposite(Composite parent, MPart part) {
        this.mPart = part;
        this.originalWsObject = (WebServiceRequestEntity) part.getObject();

        parent.setLayout(new FillLayout());

        sComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        sComposite.setExpandHorizontal(true);
        sComposite.setExpandVertical(true);
        sComposite.setBackground(ColorUtil.getCompositeBackgroundColor());
        sComposite.setBackgroundMode(SWT.INHERIT_DEFAULT);
        sComposite.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                sComposite.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
            }
        });

        mainComposite = new Composite(sComposite, SWT.NONE);
        GridLayout glMainComposite = new GridLayout(1, false);
        mainComposite.setLayout(glMainComposite);
        sComposite.setContent(mainComposite);

        createModifyListener();

        // Init UI
        createServiceInfoComposite(mainComposite);

        createHttpComposite(mainComposite);

        showEntityFieldsToUi();

        dirtyable.setDirty(false);

        registerListeners();
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

        GridData lblGridData = new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1);
        // labelGridData.widthHint = 100;

        // Service Url
        Label lblRestUrl = new Label(restContainerComposite, SWT.LEFT | SWT.WRAP);
        lblRestUrl.setText(StringConstants.PA_LBL_URL);
        lblRestUrl.setLayoutData(lblGridData);

        txtRestUrl = new Text(restContainerComposite, SWT.BORDER);
        txtRestUrl.setLayoutData(gridData);
        txtRestUrl.addModifyListener(modifyListener);

        // Request Method
        Label lblRequestMethod = new Label(restContainerComposite, SWT.LEFT | SWT.WRAP);
        lblRequestMethod.setText(StringConstants.PA_LBL_REQ_METHOD);
        lblRequestMethod.setLayoutData(lblGridData);

        cbbRestRequestMethod = new Combo(restContainerComposite, SWT.NONE);
        gridData = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
        gridData.widthHint = 100;
        cbbRestRequestMethod.setLayoutData(gridData);

        cbbRestRequestMethod.setItems(WebServiceRequestEntity.REST_REQUEST_METHODS);
        cbbRestRequestMethod.select(0);
        cbbRestRequestMethod.addModifyListener(modifyListener);

        ExpandableComposite paramsComposite = new ExpandableComposite(mainComposite, "Params", 1, true);
        Composite paramsCompositeDetail = paramsComposite.createControl();
        tblRestParams = createParamsTable(paramsCompositeDetail, false);
        tblRestParams.setInput(listRestParams);

        ExpandableComposite headerComposite = new ExpandableComposite(mainComposite, "Headers", 1, true);
        Composite headerCompositeDetail = headerComposite.createControl();
        tblHttpHeader = createParamsTable(headerCompositeDetail, false);
        tblHttpHeader.setInput(listHttpHeaderProps);
    }

    @Override
    public void createHttpComposite(Composite mainComposite) {
        Composite httpContainerComposite = new Composite(mainComposite, SWT.NONE);
        httpContainerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        httpContainerComposite.setLayout(new GridLayout(1, false));

        ExpandableComposite bodyComposite = new ExpandableComposite(mainComposite, "Body", 1, true);
        Composite bodyCompositeDetail = bodyComposite.createControl();

        txtHttpBody = new Text(bodyCompositeDetail, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        GridData gdData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gdData.heightHint = 150;
        txtHttpBody.setLayoutData(gdData);
        txtHttpBody.addModifyListener(modifyListener);
    }

    @Override
    protected void updateEntityBeforeSaved() {
        // Update object properties
        originalWsObject.setHttpBody(txtHttpBody.getText());

        tblHttpHeader.removeEmptyProperty();
        originalWsObject.setHttpHeaderProperties(tblHttpHeader.getInput());

        // Update object properties
        originalWsObject.setRestUrl(txtRestUrl.getText());
        originalWsObject.setRestRequestMethod(cbbRestRequestMethod.getText());
        originalWsObject.setRestParameters(listRestParams);

        tblRestParams.removeEmptyProperty();
    }

    @Override
    protected void showEntityFieldsToUi() {
        txtHttpBody.setText(originalWsObject.getHttpBody());

        tempPropList = new ArrayList<WebElementPropertyEntity>(originalWsObject.getHttpHeaderProperties());
        listHttpHeaderProps.clear();
        listHttpHeaderProps.addAll(tempPropList);
        tblHttpHeader.refresh();

        txtRestUrl.setText(originalWsObject.getRestUrl());
        int index = Arrays.asList(WebServiceRequestEntity.REST_REQUEST_METHODS)
                .indexOf(originalWsObject.getRestRequestMethod());
        cbbRestRequestMethod.select(index < 0 ? 0 : index);
        tempPropList = new ArrayList<WebElementPropertyEntity>(originalWsObject.getRestParameters());
        listRestParams.clear();
        listRestParams.addAll(tempPropList);
        tblRestParams.refresh();
        dirtyable.setDirty(false);
    }

    @Override
    @Persist
    public void save() {
        try {
            updateEntityBeforeSaved();
            ObjectRepositoryController.getInstance().updateTestObject(originalWsObject);

            eventBroker.post(EventConstants.TEST_OBJECT_UPDATED,
                    new Object[] { originalWsObject.getId(), originalWsObject });
            eventBroker.post(EventConstants.EXPLORER_REFRESH, null);
            dirtyable.setDirty(false);
        } catch (Exception e1) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    e1.getMessage());
        }
    }

    private void createModifyListener() {
        modifyListener = new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                dirtyable.setDirty(true);
            }
        };
    }

    private void registerListeners() {
        eventBroker.subscribe(EventConstants.TEST_OBJECT_UPDATED, this);
        eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, this);
    }
}
