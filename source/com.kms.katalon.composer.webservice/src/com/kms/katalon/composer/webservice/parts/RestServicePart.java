package com.kms.katalon.composer.webservice.parts;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.PreDestroy;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

import com.kms.katalon.composer.components.impl.dialogs.ProgressMonitorDialogWithThread;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.editor.HttpBodyEditorComposite;
import com.kms.katalon.composer.webservice.view.ExpandableComposite;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.WebServiceController;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.execution.preferences.ProxyPreferences;

public class RestServicePart extends WebServicePart {

    private URIBuilder uriBuilder;

    private ProgressMonitorDialogWithThread progress;

    @Override
    protected void createAPIControls(Composite parent) {
        super.createAPIControls(parent);
        wsApiControl.addSendSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (dirtyable.isDirty()) {
                    boolean isOK = MessageDialog.openConfirm(null, StringConstants.WARN,
                            ComposerWebserviceMessageConstants.PART_MSG_DO_YOU_WANT_TO_SAVE_THE_CHANGES);
                    if (!isOK) {
                        return;
                    }
                    save();
                }

                // clear previous response
                mirrorEditor.setText("");
                responseBody.setDocument(new Document());

                String requestURL = wsApiControl.getRequestURL().trim();
                if (isInvalidURL(requestURL)) {
                    return;
                }

                if (wsApiControl.getSendingState()) {
                    progress.getProgressMonitor().setCanceled(true);
                    wsApiControl.setSendButtonState(false);
                    return;
                }

                try {
                    wsApiControl.setSendButtonState(true);
                    Shell activeShell = Display.getCurrent().getActiveShell();
                    progress = new ProgressMonitorDialogWithThread(Display.getCurrent().getActiveShell());
                    progress.setOpenOnRun(false);
                    progress.run(true, true, new IRunnableWithProgress() {

                        @Override
                        public void run(IProgressMonitor monitor)
                                throws InvocationTargetException, InterruptedException {
                            try {
                                monitor.beginTask(ComposerWebserviceMessageConstants.PART_MSG_SENDING_TEST_REQUEST,
                                        IProgressMonitor.UNKNOWN);

                                String projectDir = ProjectController.getInstance()
                                        .getCurrentProject()
                                        .getFolderLocation();
                                final ResponseObject responseObject = WebServiceController.getInstance().sendRequest(
                                        getWSRequestObject(), projectDir, ProxyPreferences.getProxyInformation());

                                if (monitor.isCanceled()) {
                                    return;
                                }
                                Display.getDefault().asyncExec(() -> {
                                    setResponseStatus(responseObject);
                                    mirrorEditor.sleepForDocumentReady();
                                    mirrorEditor.setText(getPrettyHeaders(responseObject));
                                    String bodyContent = responseObject.getResponseText();

                                    if (bodyContent == null) {
                                        return;
                                    }

                                    responseBody.setDocument(createDocument(bodyContent));
                                });
                            } catch (Exception e) {
                                LoggerSingleton.logError(e);
                                ErrorDialog.openError(activeShell, StringConstants.ERROR_TITLE,
                                        ComposerWebserviceMessageConstants.PART_MSG_CANNOT_SEND_THE_TEST_REQUEST,
                                        new Status(Status.ERROR, WS_BUNDLE_NAME, e.getMessage(), e));
                            } finally {
                                UISynchronizeService.syncExec(() -> wsApiControl.setSendButtonState(false));
                                monitor.done();
                            }
                        }
                    });
                } catch (InvocationTargetException | InterruptedException ex) {
                    LoggerSingleton.logError(ex);
                }
            }
        });

        wsApiControl.addRequestURLModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                Text text = (Text) e.widget;
                updateParamsTable(text.getText());
            }
        });
    }

    private void updateParamsTable(String newUrl) {
        params = extractRestParameters(newUrl);
        tblParams.setInput(params);
        tblParams.refresh();
    }

    private List<WebElementPropertyEntity> extractRestParameters(String url) {
        List<WebElementPropertyEntity> paramEntities;
        try {
            uriBuilder = new URIBuilder(url);
            List<NameValuePair> params = uriBuilder.getQueryParams();
            paramEntities = params.stream()
                    .map(param -> new WebElementPropertyEntity(param.getName(), param.getValue()))
                    .collect(Collectors.toList());

        } catch (URISyntaxException e) {
            paramEntities = Collections.emptyList();
        }

        return paramEntities;
    }

    @Override
    protected void createParamsComposite(Composite parent) {
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

    @Override
    protected void deleteSelectedParams() {
        int[] selectionIndices = tblParams.getTable().getSelectionIndices();
        Set<Integer> selectionIndexSet = new HashSet<>();
        for (int index : selectionIndices) {
            selectionIndexSet.add(index);
        }

        List<WebElementPropertyEntity> paramProperties = tblParams.getInput();
        List<WebElementPropertyEntity> unselectedParamProperties = new ArrayList<>();
        IntStream.range(0, paramProperties.size()).filter(i -> !selectionIndexSet.contains(i)).forEach(
                i -> unselectedParamProperties.add(paramProperties.get(i)));

        List<NameValuePair> params = unselectedParamProperties.stream()
                .map(pr -> new BasicNameValuePair(pr.getName(), pr.getValue()))
                .collect(Collectors.toList());

        uriBuilder.setParameters(params);
        try {
            String newUrl = uriBuilder.build().toString();
            Text text = wsApiControl.getRequestURLControl();
            // Set new value to RequestURL text control.
            // This will also trigger ModifyEvent for the text control and cause
            // the parameters table to be refreshed.
            text.setText(newUrl);
        } catch (URISyntaxException e) {
            // ignore
        }
    }

    @Override
    protected void handleParamNameChanged(Object element, Object value) {
        if (element != null && element instanceof WebElementPropertyEntity && value != null
                && value instanceof String) {
            WebElementPropertyEntity paramProperty = (WebElementPropertyEntity) element;
            paramProperty.setName((String) value);
            updateRequestUrlWhenParamsChange();
        }
    }

    @Override
    protected void handleParamValueChanged(Object element, Object value) {
        if (element != null && element instanceof WebElementPropertyEntity && value != null
                && value instanceof String) {
            WebElementPropertyEntity paramProperty = (WebElementPropertyEntity) element;
            paramProperty.setValue((String) value);
            updateRequestUrlWhenParamsChange();
        }
    }

    private void updateRequestUrlWhenParamsChange() {
        List<WebElementPropertyEntity> paramProperties = tblParams.getInput();
        List<NameValuePair> params = paramProperties.stream()
                .filter(pr -> !StringUtils.isBlank(pr.getName()))
                .map(pr -> new BasicNameValuePair(pr.getName(), pr.getValue()))
                .collect(Collectors.toList());

        uriBuilder.setParameters(params);
        try {
            String newUrl = uriBuilder.build().toString();
            Text text = wsApiControl.getRequestURLControl();
            // Set new value to RequestURL text control.
            // This will also trigger ModifyEvent for the text control and cause
            // the parameters table to be refreshed.
            text.setText(newUrl);
        } catch (URISyntaxException e) {
            // ignore
        }

    }

    @Override
    protected void addTabBody(CTabFolder parent) {
        super.addTabBody(parent);
        Composite tabComposite = (Composite) tabBody.getControl();
        // requestBody = createSourceViewer(tabComposite, new GridData(SWT.FILL, SWT.FILL, true, true));
        requestBodyEditor = new HttpBodyEditorComposite(tabComposite, SWT.NONE, this);
        requestBodyEditor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    @Override
    protected void createResponseComposite(Composite parent) {
        super.createResponseComposite(parent);
        responseBody = createSourceViewer(responseBodyComposite, new GridData(SWT.FILL, SWT.FILL, true, true));
        responseBody.setEditable(false);
    }

    @Override
    protected SourceViewer createSourceViewer(Composite parent, GridData layoutData) {
        SourceViewer sv = super.createSourceViewer(parent, layoutData);
        sv.configure(new SourceViewerConfiguration());
        return sv;
    }

    private IDocument createDocument(String documentContent) {
        IDocument document = new Document(documentContent);
        document.addDocumentListener(new IDocumentListener() {

            @Override
            public void documentChanged(DocumentEvent event) {
                setDirty();
            }

            @Override
            public void documentAboutToBeChanged(DocumentEvent event) {
                // do nothing
            }
        });
        return document;
    }

    @Override
    protected void preSaving() {
        originalWsObject.setRestUrl(wsApiControl.getRequestURL());
        originalWsObject.setRestRequestMethod(wsApiControl.getRequestMethod());

        tblHeaders.removeEmptyProperty();
        originalWsObject.setHttpHeaderProperties(tblHeaders.getInput());

        // originalWsObject.setHttpBody(requestBody.getTextWidget().getText());
        originalWsObject.setHttpBodyType(requestBodyEditor.getHttpBodyType());
        originalWsObject.setHttpBodyContent(requestBodyEditor.getHttpBodyContent());
    }

    @Override
    protected void populateDataToUI() {
        try {
            WebServiceRequestEntity clone = (WebServiceRequestEntity) originalWsObject.clone();
            String restUrl = clone.getRestUrl();
            uriBuilder = new URIBuilder(restUrl);

            // Fix for back compatibility with already existing project (KAT-2930)
            boolean isOldVersion = !clone.getRestParameters().isEmpty();
            if (isOldVersion) {
                tempPropList = new ArrayList<WebElementPropertyEntity>(clone.getRestParameters());
                List<NameValuePair> params = tempPropList.stream()
                        .map(pr -> new BasicNameValuePair(pr.getName(), pr.getValue()))
                        .collect(Collectors.toList());
                clone.setRestParameters(Collections.emptyList());
                uriBuilder.addParameters(params);
            }

            wsApiControl.getRequestURLControl().setText(uriBuilder.build().toString());

            String restRequestMethod = clone.getRestRequestMethod();
            int index = Arrays.asList(WebServiceRequestEntity.REST_REQUEST_METHODS).indexOf(restRequestMethod);
            wsApiControl.getRequestMethodControl().select(index < 0 ? 0 : index);

            tempPropList = new ArrayList<WebElementPropertyEntity>(clone.getHttpHeaderProperties());
            httpHeaders.clear();
            httpHeaders.addAll(tempPropList);
            tblHeaders.refresh();

            populateBasicAuthFromHeader();
            populateOAuth1FromHeader();
            renderAuthenticationUI(ccbAuthType.getText());

            updateHeaders(clone);

            requestBodyEditor.setInput(clone);

            tabBody.getControl().setEnabled(isBodySupported());
            dirtyable.setDirty(false);

            if (isOldVersion) {
                originalWsObject = clone;
                // save();
            }
        } catch (URISyntaxException e) {
            // ignore
        }
    }

    public void updateHeaders(WebServiceRequestEntity cloneWS) {
        tempPropList = new ArrayList<WebElementPropertyEntity>(cloneWS.getHttpHeaderProperties());
        httpHeaders.clear();
        httpHeaders.addAll(tempPropList);
        tblHeaders.refresh();
    }

    public void updateDirty(boolean dirty) {
        dirtyable.setDirty(dirty);
    }

    @PreDestroy
    public void preClose() {
        if (progress != null) {
            progress.getProgressMonitor().setCanceled(true);
        }
    }
}
