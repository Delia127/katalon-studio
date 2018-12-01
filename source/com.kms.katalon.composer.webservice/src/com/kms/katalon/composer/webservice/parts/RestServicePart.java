package com.kms.katalon.composer.webservice.parts;

//import java.awt.Label;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.dialogs.ProgressMonitorDialogWithThread;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.editor.HttpBodyEditorComposite;
import com.kms.katalon.composer.webservice.response.body.ResponseBodyEditorsComposite;
import com.kms.katalon.composer.webservice.util.WebServiceUtil;
import com.kms.katalon.composer.webservice.view.ExpandableComposite;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.WebServiceController;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.core.webservice.helper.RestRequestMethodHelper;
import com.kms.katalon.entity.repository.DraftWebServiceRequestEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.webservice.RequestHistoryEntity;
import com.kms.katalon.execution.preferences.ProxyPreferences;
import com.kms.katalon.tracking.service.Trackings;
import com.kms.katalon.util.URLBuilder;
import com.kms.katalon.util.collections.NameValuePair;

public class RestServicePart extends WebServicePart {

    protected HttpBodyEditorComposite requestBodyEditor;

    private URLBuilder urlBuilder;

    protected ResponseBodyEditorsComposite responseBodyEditor;

    private ProgressMonitorDialogWithThread progress;

    private Label lblBodyNotSupported;

    private ModifyListener requestURLModifyListener;

    private boolean allowEditParamsTable = true;

    @Override
    protected void createAPIControls(Composite parent) {
        super.createAPIControls(parent);

        requestURLModifyListener = new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                Text text = (Text) e.widget;
                updateParamsTable(text.getText());
            }
        };

        wsApiControl.addRequestURLModifyListener(requestURLModifyListener);

        wsApiControl.addRequestMethodSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setTabBodyContentBasedOnRequestMethod();
            }
        });

        wsApiControl.addRequestMethodSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                setTabBodyContentBasedOnRequestMethod();
            }
        });
        
        wsApiControl.addRequestMethodModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                setTabBodyContentBasedOnRequestMethod();
            }
        });
        
        wsApiControl.addRequestMethodFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (StringUtils.isBlank(wsApiControl.getRequestMethod())) {
                    wsApiControl.setRequestMethodSelection(0);
                }
            }
        });
    }

    @Override
    protected void sendRequest(boolean runVerificationScript) {
        if (dirtyable.isDirty()) {
            boolean isOK = MessageDialog.openConfirm(null, StringConstants.WARN,
                    ComposerWebserviceMessageConstants.PART_MSG_DO_YOU_WANT_TO_SAVE_THE_CHANGES);
            if (!isOK) {
                return;
            }
            save();
        }

        clearPreviousResponse();

        String requestURL = wsApiControl.getRequestURL().trim();
        // if (isInvalidURL(requestURL)) {
        // return;
        // }

        if (wsApiControl.getSendingState()) {
            progress.getProgressMonitor().setCanceled(true);
            wsApiControl.setSendButtonState(false);
            return;
        }

        try {
            Trackings.trackTestWebServiceObject(runVerificationScript,
                    getOriginalWsObject() instanceof DraftWebServiceRequestEntity);
            wsApiControl.setSendButtonState(true);
            progress = new ProgressMonitorDialogWithThread(Display.getCurrent().getActiveShell());
            progress.setOpenOnRun(false);
            displayResponseContentBasedOnSendingState(true);
            progress.run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask(ComposerWebserviceMessageConstants.PART_MSG_SENDING_TEST_REQUEST,
                                IProgressMonitor.UNKNOWN);

                        String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();

                        WebServiceRequestEntity requestEntity = getWSRequestObject();

                        Map<String, String> evaluatedVariables = evaluateRequestVariables();

                        ResponseObject responseObject = WebServiceController.getInstance().sendRequest(requestEntity,
                                projectDir, ProxyPreferences.getProxyInformation(),
                                Collections.<String, Object>unmodifiableMap(evaluatedVariables));

                        if (monitor.isCanceled()) {
                            return;
                        }

                        String bodyContent = responseObject.getResponseText();

                        Display.getDefault().asyncExec(() -> {
                            setResponseStatus(responseObject);

                            mirrorEditor.setText(getPrettyHeaders(responseObject));

                            if (bodyContent == null) {
                                return;
                            }
                            responseBodyEditor.setInput(responseObject);

                        });

                        if (runVerificationScript) {
                            executeVerificationScript(responseObject);
                        }
                        
                        RequestHistoryEntity requestHistoryEntity = new RequestHistoryEntity(
                                new Date(), (WebServiceRequestEntity) getWSRequestObject().clone());
                        eventBroker.post(EventConstants.WS_VERIFICATION_FINISHED, new Object[] { requestHistoryEntity });
                    } catch (Exception e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        UISynchronizeService.syncExec(() -> wsApiControl.setSendButtonState(false));
                        monitor.done();
                    }
                }
            });

        } catch (InvocationTargetException ex) {
            Throwable target = ex.getTargetException();
            if (target == null) {
                return;
            }
            LoggerSingleton.logError(target);
            MultiStatusErrorDialog.showErrorDialog(
                    ComposerWebserviceMessageConstants.PART_MSG_CANNOT_SEND_THE_TEST_REQUEST, target.getMessage(),
                    ExceptionsUtil.getStackTraceForThrowable(target));
        } catch (InterruptedException ignored) {
        }
        displayResponseContentBasedOnSendingState(false);
    }

    private void setTabBodyContentBasedOnRequestMethod() {
        GridData gdLblBodyNotSupported = (GridData) lblBodyNotSupported.getLayoutData();
        GridData gdRequestBodyEditor = (GridData) requestBodyEditor.getLayoutData();

        if (isBodySupported()) {
            gdLblBodyNotSupported.exclude = true;
            lblBodyNotSupported.setVisible(false);
            gdRequestBodyEditor.exclude = false;
            requestBodyEditor.setVisible(true);
        } else {
            gdLblBodyNotSupported.exclude = false;
            lblBodyNotSupported.setVisible(true);
            lblBodyNotSupported.setText(String.format(ComposerWebserviceMessageConstants.LBL_BODY_NOT_SUPPORTED,
                    wsApiControl.getRequestMethod()));
            gdRequestBodyEditor.exclude = true;
            requestBodyEditor.setVisible(false);
        }

        lblBodyNotSupported.getParent().requestLayout();
    }

    private void updateParamsTable(String newUrl) {
        try {
            params = extractRestParameters(newUrl);
            allowEditParamsTable = true;
        } catch (MalformedURLException e) {
            urlBuilder = null;
            params = new ArrayList<>();
            allowEditParamsTable = false;
        }
        tblParams.setInput(params);
        tblParams.refresh();
    }
    
    private void updateParamsTable(WebServiceRequestEntity entity){
        tblParams.setInput(entity.getRestParameters());
        tblParams.refresh();
    }
    
    private List<WebElementPropertyEntity> extractRestParameters(String url) throws MalformedURLException {
        List<WebElementPropertyEntity> paramEntities;
        urlBuilder = new URLBuilder(url);
        List<NameValuePair> params = urlBuilder.getQueryParams();
        paramEntities = params.stream().map(param -> new WebElementPropertyEntity(param.getName(), param.getValue()))
                .collect(Collectors.toList());

        return paramEntities;
    }

    @Override
    protected void createParamsComposite(Composite parent) {
        createCustomizeApiMethodsLink(parent);
        ExpandableComposite paramsExpandableComposite = new ExpandableComposite(parent, StringConstants.PA_LBL_PARAMS,
                1, true);
        Composite paramsComposite = paramsExpandableComposite.createControl();
        GridLayout glParams = (GridLayout) paramsComposite.getLayout();
        glParams.marginLeft = 0;
        glParams.marginRight = 0;
        ToolBar toolbar = createAddRemoveToolBar(paramsComposite, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                tblParams.addRow();
            }
        }, new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                // tblParams.deleteSelections();
                deleteSelectedParams();
            }
        });

        tblParams = createKeyValueTable(paramsComposite, false);
        tblParams.setInput(params);
        tblParams.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                toolbar.getItem(1).setEnabled(tblParams.getTable().getSelectionCount() > 0);
            }
        });
    }
    
    private void createCustomizeApiMethodsLink(Composite parent) {
        Link lnkCustomizeApiMethods = new Link(parent, SWT.NONE);
        lnkCustomizeApiMethods.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));
        lnkCustomizeApiMethods.setText(StringConstants.LINK_CUSTOMIZE_API_METHODS);
        lnkCustomizeApiMethods.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventBroker.post(EventConstants.PROJECT_SETTINGS_PAGE,StringConstants.WEBSERVICE_METHOD_SETTING_PAGE);
            }
        });
    }

    @Override
    protected void deleteSelectedParams() {
        int[] selectionIndices = tblParams.getTable().getSelectionIndices();
        Set<Integer> selectionIndexSet = new HashSet<>();
        for (int index : selectionIndices) {
            selectionIndexSet.add(index);
        }

        List<WebElementPropertyEntity> paramProperties = tblParams.getInput();
        List<WebElementPropertyEntity> unselectedParamProperties = new ArrayList<>();
        IntStream.range(0, paramProperties.size()).filter(i -> !selectionIndexSet.contains(i))
                .forEach(i -> unselectedParamProperties.add(paramProperties.get(i)));
        tblParams.setInput(unselectedParamProperties);
        tblParams.refresh();

        if (allowEditParamsTable) {
            updateRequestUrlWithNewParams(unselectedParamProperties);
        }
    }

    private void updateRequestUrlWithNewParams(List<WebElementPropertyEntity> paramProperties) {
        List<NameValuePair> params = toNameValuePair(paramProperties);
        urlBuilder = new URLBuilder();
        urlBuilder.setParameters(params);
        try {
            String newUrl = urlBuilder.build().toString();
            Text text = wsApiControl.getRequestURLControl();
            text.removeModifyListener(requestURLModifyListener);
            text.setText(newUrl);
            text.addModifyListener(requestURLModifyListener);
        } catch (MalformedURLException ignored) {

        }
    }

    private List<NameValuePair> toNameValuePair(List<WebElementPropertyEntity> propertyEntities) {
        return propertyEntities.stream().map(pr -> new NameValuePair(pr.getName(), pr.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    protected void handleRequestParamNameChanged(Object element, Object value) {
        if (element != null && element instanceof WebElementPropertyEntity && value != null && value instanceof String
                && allowEditParamsTable) {

            WebElementPropertyEntity paramProperty = (WebElementPropertyEntity) element;
            paramProperty.setName((String) value);
            tblParams.refresh();

            List<WebElementPropertyEntity> paramProperties = tblParams.getInput();
            updateRequestUrlWithNewParams(paramProperties);
        }
    }

    @Override
    protected void handleRequestParamValueChanged(Object element, Object value) {
        if (element != null && element instanceof WebElementPropertyEntity && value != null && value instanceof String
                && allowEditParamsTable) {

            WebElementPropertyEntity paramProperty = (WebElementPropertyEntity) element;
            paramProperty.setValue((String) value);
            tblParams.refresh();

            List<WebElementPropertyEntity> paramProperties = tblParams.getInput();
            updateRequestUrlWithNewParams(paramProperties);
        }
    }

    @Override
    protected void addTabBody(CTabFolder parent) {
        super.addTabBody(parent);
        Composite tabComposite = (Composite) tabBody.getControl();

        Composite tabBodyComposite = new Composite(tabComposite, SWT.NONE);
        tabBodyComposite.setLayout(new GridLayout());

        // requestBody = createSourceViewer(tabComposite, new GridData(SWT.FILL,
        // SWT.FILL, true, true));
        requestBodyEditor = new HttpBodyEditorComposite(tabBodyComposite, SWT.NONE, this);
        requestBodyEditor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        lblBodyNotSupported = new Label(tabBodyComposite, SWT.NONE);
        // requestBodyEditor.setInput(originalWsObject);

        lblBodyNotSupported.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    }

    @Override
    protected void createResponseComposite(Composite parent) {
        super.createResponseComposite(parent);
        responseBodyEditor = new ResponseBodyEditorsComposite(responseBodyComposite, SWT.NONE, this);
        responseBodyEditor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    @Override
    protected SourceViewer createSourceViewer(Composite parent, GridData layoutData) {
        SourceViewer sv = super.createSourceViewer(parent, layoutData);
        sv.configure(new SourceViewerConfiguration());
        return sv;
    }

    @Override
    protected void preSaving() {
        tblParams.removeEmptyProperty();
        updateRequestUrlWithNewParams(tblParams.getInput());
        
        originalWsObject.setRestUrl(wsApiControl.getRequestURL());
        String requestMethod = wsApiControl.getRequestMethod();
        originalWsObject.setRestRequestMethod(requestMethod);

        tblHeaders.removeEmptyProperty();
        originalWsObject.setHttpHeaderProperties(tblHeaders.getInput());

        if (isBodySupported(requestMethod) && requestBodyEditor.getHttpBodyType() != null) {
            originalWsObject.setHttpBodyContent(requestBodyEditor.getHttpBodyContent());
            originalWsObject.setHttpBodyType(requestBodyEditor.getHttpBodyType());
        }

        updatePartImage();
    }

    private boolean isBodySupported(String requestMethod) {
        return RestRequestMethodHelper.isBodySupported(requestMethod);
    }

    @Override
    protected void populateDataToUI() {
        WebServiceRequestEntity clone = (WebServiceRequestEntity) originalWsObject.clone();

        String restUrl = clone.getRestUrl();

        wsApiControl.getRequestURLControl().setText(restUrl);

        updateParamsTable(restUrl);

        String restRequestMethod = clone.getRestRequestMethod();
        int index = Arrays.asList(WebServiceRequestEntity.REST_REQUEST_METHODS).indexOf(restRequestMethod);
        wsApiControl.getRequestMethodControl();

        tempPropList = new ArrayList<WebElementPropertyEntity>(clone.getHttpHeaderProperties());
        httpHeaders.clear();
        httpHeaders.addAll(tempPropList);
        tblHeaders.refresh();

        populateBasicAuthFromHeader();
        populateOAuth1FromHeader();
        renderAuthenticationUI(ccbAuthType.getText());

        updateHeaders(clone);

        requestBodyEditor.setInput(clone);

        setTabBodyContentBasedOnRequestMethod();

        dirtyable.setDirty(false);

        // if (isOldVersion) {
        // originalWsObject = clone;
        // // save();
        // }
    }

    public void updateHeaders(WebServiceRequestEntity cloneWS) {
        tempPropList = new ArrayList<WebElementPropertyEntity>(cloneWS.getHttpHeaderProperties());
        httpHeaders.clear();
        httpHeaders.addAll(tempPropList);
        tblHeaders.refresh();
    }

    @PreDestroy
    public void preClose() {
        if (progress != null) {
            progress.getProgressMonitor().setCanceled(true);
        }
    }

    @Override
    protected void updatePartImage() {
        updateIconURL(WebServiceUtil.getRequestMethodIcon(originalWsObject.getServiceType(),
                originalWsObject.getRestRequestMethod()));
    }
}
