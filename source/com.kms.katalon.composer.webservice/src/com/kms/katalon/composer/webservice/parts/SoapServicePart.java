package com.kms.katalon.composer.webservice.parts;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.wsdl.WSDLException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.dialogs.ProgressMonitorDialogWithThread;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.composer.webservice.constants.ImageConstants;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.editor.SoapRequestMessageEditor;
import com.kms.katalon.composer.webservice.soap.response.body.SoapResponseBodyEditorsComposite;
import com.kms.katalon.composer.webservice.util.WebServiceUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.WebServiceController;
import com.kms.katalon.core.testobject.RequestObject;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.core.webservice.common.BasicRequestor;
import com.kms.katalon.core.webservice.common.HarLogger;
import com.kms.katalon.core.webservice.constants.WsdlLocatorParams;
import com.kms.katalon.core.webservice.helper.WsdlLocatorProvider;
import com.kms.katalon.core.webservice.wsdl.support.wsdl.WsdlDefinitionLocator;
import com.kms.katalon.core.webservice.wsdl.support.wsdl.WsdlImporter;
import com.kms.katalon.core.webservice.wsdl.support.wsdl.WsdlParser;
import com.kms.katalon.entity.repository.DraftWebServiceRequestEntity;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.webservice.RequestHistoryEntity;
import com.kms.katalon.execution.preferences.ProxyPreferences;
import com.kms.katalon.tracking.service.Trackings;

public class SoapServicePart extends WebServicePart {

    protected SoapResponseBodyEditorsComposite soapResponseBodyEditor;

    private ProgressMonitorDialogWithThread progress;

    protected SoapRequestMessageEditor requestBodyEditor;

    private CCombo cbbRequestMethod;

    private Text txtWsdlLocation;

    private CCombo cbbServiceFunction;

    private Button btnLoadServiceFunctions;

    private Button btnLoadContent;

    private Button cbUseOldMechanism;
    
    private boolean useOldMechanism;

    private Label lblHelp;

    private Text txtServiceEndpoint;

    @Override
    protected void createServiceInfoComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(3, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;

        composite.setLayout(gridLayout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        cbbRequestMethod = new CCombo(composite, SWT.BORDER);
        cbbRequestMethod.setBackground(ColorUtil.getWhiteBackgroundColor());
        GridData gdRequestMethod = new GridData(SWT.FILL, SWT.CENTER, false, false);
        gdRequestMethod.widthHint = 100;
        gdRequestMethod.heightHint = 22;
        cbbRequestMethod.setLayoutData(gdRequestMethod);
        cbbRequestMethod.setEditable(true);
        cbbRequestMethod.setItems(WebServiceRequestEntity.SOAP_REQUEST_METHODS);
        cbbRequestMethod.setText(originalWsObject.getSoapRequestMethod());

        createApiControls(composite);
        wsApiControl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));

        Label lblWsdlEndpoint = new Label(composite, SWT.NONE);
        lblWsdlEndpoint.setText("WSDL URL"); //$NON-NLS-1$

        txtWsdlLocation = new Text(composite, SWT.BORDER);
        GridData gdWsdlEndpoint = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdWsdlEndpoint.heightHint = 20;
        txtWsdlLocation.setLayoutData(gdWsdlEndpoint);
        txtWsdlLocation.setText(originalWsObject.getWsdlAddress());

        btnLoadServiceFunctions = new Button(composite, SWT.FLAT);
        btnLoadServiceFunctions.setText(StringConstants.LBL_LOAD_SERVICE_FUNCTION);
        btnLoadServiceFunctions.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));

        Label lblServiceFunction = new Label(composite, SWT.NONE);
        lblServiceFunction.setText(StringConstants.PA_LBL_SERVICE_FUNCTION);
        lblServiceFunction.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        cbbServiceFunction = new CCombo(composite, SWT.BORDER | SWT.FLAT | SWT.READ_ONLY);
        GridData gdServiceFunction = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdServiceFunction.heightHint = 22;
        cbbServiceFunction.setLayoutData(gdServiceFunction);

        btnLoadContent = new Button(composite, SWT.FLAT);
        btnLoadContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        btnLoadContent.setText(ComposerWebserviceMessageConstants.SoapServicePart_BTN_LOAD_NEW_CONTENT);

        Label lblEmpty = new Label(composite, SWT.NONE);
        lblEmpty.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        Composite oldMechanismComp = new Composite(composite, SWT.NONE);
        GridLayout glWsdlComp = new GridLayout(2, false);
        glWsdlComp.marginWidth = 0;
        glWsdlComp.marginHeight = 0;
        oldMechanismComp.setLayout(glWsdlComp);
        oldMechanismComp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        cbUseOldMechanism = new Button(oldMechanismComp, SWT.CHECK);
        cbUseOldMechanism.setText(ComposerWebserviceMessageConstants.SoapServicePart_CB_USE_SERVICE_INFO_FROM_WSDL);

        lblHelp = new Label(oldMechanismComp, SWT.NONE);
        GridData gdLblHelp = new GridData(SWT.RIGHT, SWT.CENTER, false, true, 1, 1);
        gdLblHelp.heightHint = 20;
        lblHelp.setLayoutData(gdLblHelp);
        lblHelp.setImage(ImageConstants.IMG_16_HELP);
        lblHelp.setCursor(Display.getDefault().getSystemCursor(SWT.CURSOR_HAND));

        Label lblServiceEndpoint = new Label(composite, SWT.NONE);
        lblServiceEndpoint.setText(ComposerWebserviceMessageConstants.SoapServicePart_LBL_SERVICE_ENDPOINT);

        txtServiceEndpoint = new Text(composite, SWT.BORDER);
        GridData gdServiceEndpoint = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        gdServiceEndpoint.heightHint = 20;
        txtServiceEndpoint.setLayoutData(gdServiceEndpoint);

        if (!originalWsObject.isCreatedBeforeV7_4_5()) {
            ((GridData) lblEmpty.getLayoutData()).exclude = true;
            lblEmpty.setVisible(false);
            ((GridData) oldMechanismComp.getLayoutData()).exclude = true;
            oldMechanismComp.setVisible(false);
        }

        registerControlListeners();

        txtServiceEndpoint.setEnabled(useCustomServiceEndpoint());
    }

    private void registerControlListeners() {
        txtWsdlLocation.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setDirty(true);
            }
        });

        btnLoadServiceFunctions.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                loadServiceFunctions();
            }
        });

        cbbServiceFunction.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setDirty(true);
            }
        });

        btnLoadContent.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                loadContent();
            }
        });

        cbbRequestMethod.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                cbbServiceFunction.removeAll();
                setDirty(true);
            }
        });

        cbUseOldMechanism.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                txtServiceEndpoint.setEnabled(useCustomServiceEndpoint());
                useOldMechanism = cbUseOldMechanism.getSelection();
                setDirty(true);
            }
        });

        txtServiceEndpoint.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                setDirty(true);
            }
        });

        lblHelp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                Program.launch("https://docs.katalon.com/katalon-studio/docs/import-soap-requests-from-wsdl.html"); //$NON-NLS-1$
            }
        });
    }

    private boolean useCustomServiceEndpoint() {
        return !cbUseOldMechanism.getSelection();
    }

    private void loadServiceFunctions() {
        String wsdlLocation = txtWsdlLocation.getText().trim();
        String method = cbbRequestMethod.getText();
        Shell activeShell = Display.getCurrent().getActiveShell();

        if (StringUtils.isBlank(wsdlLocation)) {
            MessageDialog.openError(activeShell, StringConstants.ERROR,
                    ComposerWebserviceMessageConstants.SoapServicePart_MSG_WSDL_LOCATION_UNDEFINED); // $NON-NLS-1$
            return;
        }

        try {

            new ProgressMonitorDialogWithThread(activeShell).run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask(StringConstants.MSG_FETCHING_FROM_WSDL, IProgressMonitor.UNKNOWN);
                    try {
                        WsdlParser parser = getWsdlParser(wsdlLocation);
                        List<String> servFuncs = parser.getOperationNamesByRequestMethod(method);
                        UISynchronizeService.asyncExec(() -> {
                            cbbServiceFunction.setItems(servFuncs.toArray(new String[0]));
                            if (servFuncs.size() > 0) {
                                cbbServiceFunction.select(0);
                            }
                            setDirty(true);
                        });
                    } catch (WSDLException | IOException e) {
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
        } catch (InvocationTargetException ex) {
            LoggerSingleton.logError(ex);
            MultiStatusErrorDialog.showErrorDialog(
                    ComposerWebserviceMessageConstants.SoapServicePart_MSG_UNABLE_TO_LOAD_SERVICE_FUNCTION
                            + wsdlLocation,
                    ex.getTargetException().getMessage(),
                    ExceptionsUtil.getStackTraceForThrowable(ex.getTargetException()));
        } catch (InterruptedException ex) {
            LoggerSingleton.logError(ex);
        }
    }

    private void loadContent() {
        String wsdlLocation = txtWsdlLocation.getText().trim();
        String method = cbbRequestMethod.getText().trim();
        String operation = cbbServiceFunction.getText();
        Shell shell = Display.getCurrent().getActiveShell();
        
        if (!MessageDialog.openConfirm(shell, StringConstants.WARN,
                "Service endpoint, Header, and Request message of this SOAP request will be overridden. Are you ok to proceed?")) {
            return;
        }

        if (StringUtils.isBlank(wsdlLocation)) {
            MessageDialog.openError(shell, StringConstants.ERROR,
                    ComposerWebserviceMessageConstants.SoapServicePart_MSG_WSDL_LOCATION_UNDEFINED); // $NON-NLS-1$
            return;
        }

        if (StringUtils.isBlank(operation)) {
            MessageDialog.openError(shell, StringConstants.ERROR,
                    ComposerWebserviceMessageConstants.SoapServicePart_MSG_SERVICE_FUNCTION_EMPTY); // $NON-NLS-1$
            return;
        }

        try {
            new ProgressMonitorDialogWithThread(shell).run(true, true, new IRunnableWithProgress() {

                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    WebServiceRequestEntity newContentEntity = getNewContentEntity(wsdlLocation, method, operation);
                    
                    UISynchronizeService.syncExec(() -> {
                        txtServiceEndpoint.setText(newContentEntity.getSoapServiceEndpoint());
                        httpHeaders.clear();
                        httpHeaders.addAll(newContentEntity.getHttpHeaderProperties());
                        tblHeaders.refresh();
                        requestBodyEditor.setInput(newContentEntity.getSoapBody());
                        setDirty(true);
                    });
                }

            });
        } catch (InvocationTargetException e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(
                    ComposerWebserviceMessageConstants.SoapServicePart_MSG_UNABLE_TO_LOAD_CONTENT_FROM_URL
                            + wsdlLocation,
                    e.getTargetException().getMessage(),
                    ExceptionsUtil.getStackTraceForThrowable(e.getTargetException()));
        } catch (InterruptedException e) {
            LoggerSingleton.logError(e);
        }
    }

    private WebServiceRequestEntity getNewContentEntity(String wsdlLocation, String method, String operation) {
        try {
            WsdlDefinitionLocator wsdlLocator = getWsdlLocator(wsdlLocation);
            WsdlImporter importer = new WsdlImporter(wsdlLocator);
            WebServiceRequestEntity entity = importer.getImportedEntity(method, operation,
                    originalWsObject instanceof DraftWebServiceRequestEntity);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WsdlParser getWsdlParser(String wsdlLocation) throws IOException {
        WsdlDefinitionLocator wsdlLocator = getWsdlLocator(wsdlLocation);
        return new WsdlParser(wsdlLocator);
    }

    private WsdlDefinitionLocator getWsdlLocator(String wsdlLocation) throws IOException {
        Map<String, Object> locatorParams = new HashMap<>();
        locatorParams.put(WsdlLocatorParams.HTTP_HEADERS, getAuthorizationHeaderMap());
        WsdlDefinitionLocator wsdlLocator = WsdlLocatorProvider.getLocator(wsdlLocation, locatorParams);
        return wsdlLocator;
    }

    private Map<String, String> getAuthorizationHeaderMap() {
        String authorizationHeaderValue = getAuthorizationHeaderValue();
        Map<String, String> headers = new HashMap<>();
        if (StringUtils.isNotBlank(authorizationHeaderValue)) {
            headers.put(HttpHeaders.AUTHORIZATION, authorizationHeaderValue);
        }
        return headers;
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

        if (cbUseOldMechanism.getSelection()) {
            if (StringUtils.isBlank(txtWsdlLocation.getText())) {
                MessageDialog.openError(null, StringConstants.ERROR,
                        ComposerWebserviceMessageConstants.SoapServicePart_MSG_WSDL_LOCATION_UNDEFINED); // $NON-NLS-1$
                return;
            }
            
            if (cbbServiceFunction.getText().isEmpty()) {
                LoggerSingleton.logError(ComposerWebserviceMessageConstants.SoapServicePart_MSG_SERVICE_FUNCTION_EMPTY);
                MessageDialog.openError(null, StringConstants.ERROR,
                        ComposerWebserviceMessageConstants.SoapServicePart_MSG_SERVICE_FUNCTION_EMPTY);
                return;
            }
        }
            

        if (wsApiControl.getSendingState()) {
            progress.getProgressMonitor().setCanceled(true);
            wsApiControl.setSendButtonState(false);
            return;
        }

        if (!cbUseOldMechanism.getSelection() && StringUtils.isBlank(txtServiceEndpoint.getText())) {
            MessageDialog.openError(null, StringConstants.ERROR,
                    ComposerWebserviceMessageConstants.SoapServicePart_MSG_SERVICE_ENDPOINT_UNDEFINED); // $NON-NLS-1$
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

                        Map<String, Object> evaluatedVariables = evaluateRequestVariables();

                        HarLogger harLogger = new HarLogger();
                        harLogger.initHarFile();
                        ResponseObject responseObject = WebServiceController.getInstance().sendRequest(requestEntity,
                                projectDir, ProxyPreferences.getSystemProxyInformation(),
                                Collections.<String, Object> unmodifiableMap(evaluatedVariables), false);

                        deleteTempHarFile();

                        RequestObject requestObject = WebServiceController.getRequestObject(requestEntity, projectDir,
                                Collections.<String, Object>unmodifiableMap(evaluatedVariables));
                        String logFolder = Files.createTempDirectory("har").toFile().getAbsolutePath(); //$NON-NLS-1$
                        harFile = harLogger.logHarFile(requestObject, responseObject, logFolder);

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
                            soapResponseBodyEditor.setInput(responseObject);

                        });

                        if (runVerificationScript) {
                            executeVerificationScript(responseObject);
                        }

                        RequestHistoryEntity requestHistoryEntity = new RequestHistoryEntity(new Date(),
                                (WebServiceRequestEntity) getWSRequestObject().clone());
                        eventBroker.post(EventConstants.WS_VERIFICATION_FINISHED,
                                new Object[] { requestHistoryEntity });
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

    @Override
    protected void addTabBody(CTabFolder parent) {
        super.addTabBody(parent);
        tabBody.setText(StringConstants.PA_LBL_XML_REQ_MSG);
        Composite tabComposite = (Composite) tabBody.getControl();
        requestBodyEditor = new SoapRequestMessageEditor(tabComposite, SWT.NONE, this);
        requestBodyEditor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    @Override
    protected void createResponseComposite(Composite parent) {
        super.createResponseComposite(parent);
        soapResponseBodyEditor = new SoapResponseBodyEditorsComposite(responseBodyComposite, SWT.NONE, this);
        soapResponseBodyEditor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    @Override
    protected void preSaving() {
        originalWsObject.setWsdlAddress(txtWsdlLocation.getText());
        originalWsObject.setSoapRequestMethod(cbbRequestMethod.getText());
        originalWsObject.setSoapServiceFunction(cbbServiceFunction.getText());
        originalWsObject.setUseServiceInfoFromWsdl(cbUseOldMechanism.getSelection());
        originalWsObject.setSoapServiceEndpoint(txtServiceEndpoint.getText());

        tblHeaders.removeEmptyProperty();
        originalWsObject.setHttpHeaderProperties(httpHeaders);

        originalWsObject.setSoapBody(requestBodyEditor.getHttpBodyContent());

        updateIconURL(WebServiceUtil.getRequestMethodIcon(originalWsObject.getServiceType(),
                originalWsObject.getSoapRequestMethod()));
    }

    @Override
    protected void populateDataToUI() {
        txtWsdlLocation.setText(originalWsObject.getWsdlAddress());
        String soapRequestMethod = originalWsObject.getSoapRequestMethod();
        int index = Arrays.asList(WebServiceRequestEntity.SOAP_REQUEST_METHODS).indexOf(soapRequestMethod);
        cbbRequestMethod.select(index < 0 ? 0 : index);
        cbbServiceFunction.setText(originalWsObject.getSoapServiceFunction());
        cbUseOldMechanism.setSelection(originalWsObject.isUseServiceInfoFromWsdl());
        txtServiceEndpoint.setText(originalWsObject.getSoapServiceEndpoint());
        txtServiceEndpoint.setEnabled(useCustomServiceEndpoint());

        tempPropList = new ArrayList<WebElementPropertyEntity>(originalWsObject.getHttpHeaderProperties());
        httpHeaders.clear();
        httpHeaders.addAll(tempPropList);
        tblHeaders.refresh();

        populateBasicAuthFromHeader();
        populateOAuth1FromHeader();
        renderAuthenticationUI(ccbAuthType.getText());

        // requestBody.setDocument(createXMLDocument(originalWsObject.getSoapBody()));
        requestBodyEditor.setInput(originalWsObject.getSoapBody());

        cbFollowRedirects.setSelection(originalWsObject.isFollowRedirects());

        populateVariableManualView();

        populateVariableScriptView();

        reloadVerificationScript();

        setDirty(false);
    }

    private String getAuthorizationHeaderValue() {
        Optional<WebElementPropertyEntity> definedAuthorization = httpHeaders.stream()
                .filter(header -> HttpHeaders.AUTHORIZATION.equals(header.getName())).findFirst();
        if (definedAuthorization.isPresent()) {
            return definedAuthorization.get().getValue();
        }

        Map<String, String> map = oauth1Headers.stream()
                .collect(Collectors.toMap(WebElementPropertyEntity::getName, WebElementPropertyEntity::getValue));
        String authType = map.get(AUTHORIZATION_TYPE);
        if (StringUtils.isBlank(authType)) {
            return null;
        }

        if (OAUTH_1_0.equals(authType)) {
            try {
                String oauth1AuthorizationHeader = BasicRequestor
                        .createOAuth1AuthorizationHeaderValue(txtWsdlLocation.getText().trim(), map);
                return StringUtils.isBlank(oauth1AuthorizationHeader) ? null : oauth1AuthorizationHeader;
            } catch (GeneralSecurityException e) {
                LoggerSingleton.logError(e);
            } catch (IOException e) {
                LoggerSingleton.logError(e);
            }
        }

        return null;
    }

    @Override
    protected void updatePartImage() {
        updateIconURL(WebServiceUtil.getRequestMethodIcon(originalWsObject.getServiceType(),
                originalWsObject.getSoapRequestMethod()));
    }

    @Override
    public boolean isDirty() {
        return mPart.isDirty();
    }

}
