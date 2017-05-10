package com.kms.katalon.composer.webservice.parts;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.dialogs.ProgressMonitorDialogWithThread;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.view.ExpandableComposite;
import com.kms.katalon.console.utils.ProxyUtil;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.WebServiceController;
import com.kms.katalon.core.testobject.ResponseObject;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class RestServicePart extends WebServicePart {

    private static final String[] FILTER_EXTS = new String[] { "*.json; *.xml; *.txt" };

    private static final String[] FILTER_NAMES = new String[] { "Text-based files (*.json, *.xml, *.txt)" };

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
                responseHeader.setDocument(new Document());
                responseBody.setDocument(new Document());

                String requestURL = wsApiControl.getRequestURL().trim();
                if (isInvalidURL(requestURL)) {
                    return;
                }

                try {
                    Shell activeShell = Display.getCurrent().getActiveShell();
                    new ProgressMonitorDialogWithThread(activeShell).run(true, true, new IRunnableWithProgress() {

                        @Override
                        public void run(IProgressMonitor monitor)
                                throws InvocationTargetException, InterruptedException {
                            monitor.beginTask(ComposerWebserviceMessageConstants.PART_MSG_SENDING_TEST_REQUEST,
                                    IProgressMonitor.UNKNOWN);
                            Display.getDefault().asyncExec(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        tabResponse.getParent().setSelection(tabResponse);

                                        String projectDir = ProjectController.getInstance().getCurrentProject().getFolderLocation();
                                        ResponseObject responseObject = WebServiceController.getInstance().sendRequest(
                                                getWSRequestObject(), projectDir, ProxyUtil.getProxyInformation());

                                        responseHeader.setDocument(createDocument(getPrettyHeaders(responseObject)));

                                        String bodyContent = responseObject.getResponseText();

                                        if (bodyContent == null) {
                                            return;
                                        }

                                        responseBody.setDocument(createDocument(bodyContent));
                                    } catch (Exception e) {
                                        LoggerSingleton.logError(e);
                                        ErrorDialog.openError(activeShell, StringConstants.ERROR_TITLE,
                                                ComposerWebserviceMessageConstants.PART_MSG_CANNOT_SEND_THE_TEST_REQUEST,
                                                new Status(Status.ERROR, WS_BUNDLE_NAME, e.getMessage(), e));
                                    } finally {
                                        monitor.done();
                                    }
                                }
                            });
                        }
                    });
                } catch (InvocationTargetException | InterruptedException ex) {
                    LoggerSingleton.logError(ex);
                }
            }
        });
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
                tblParams.deleteSelections();
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
    protected void addTabBody(TabFolder parent) {
        super.addTabBody(parent);
        Composite tabComposite = (Composite) tabBody.getControl();
        ToolBar toolbar = new ToolBar(tabComposite, SWT.FLAT | SWT.RIGHT);
        ToolItem tiLoadFromFile = new ToolItem(toolbar, SWT.PUSH);
        tiLoadFromFile.setText(ComposerWebserviceMessageConstants.BTN_LOAD_FROM_FILE);
        tiLoadFromFile.setImage(ImageManager.getImage(IImageKeys.ATTACHMENT_16));
        tiLoadFromFile.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!warningIfBodyNotEmpty()) {
                    return;
                }
                // Load body template from file
                FileDialog dialog = new FileDialog(toolbar.getShell());
                dialog.setFilterNames(FILTER_NAMES);
                dialog.setFilterExtensions(FILTER_EXTS);
                dialog.setFilterPath(ProjectController.getInstance().getCurrentProject().getFolderLocation());
                String filePath = dialog.open();
                if (StringUtils.isEmpty(filePath)) {
                    return;
                }
                try {
                    String bodyContent = FileUtils.readFileToString(new File(filePath));
                    requestBody.setDocument(createDocument(bodyContent));
                    setDirty();
                } catch (IOException ex) {
                    LoggerSingleton.logError(ex);
                }
            }
        });
        requestBody = createSourceViewer(tabComposite, new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    @Override
    protected void addTabResponse(TabFolder parent) {
        super.addTabResponse(parent);
        Composite tabComposite = (Composite) tabResponse.getControl();
        responseBody = createSourceViewer(tabComposite, new GridData(SWT.FILL, SWT.FILL, true, true));
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

        tblParams.removeEmptyProperty();
        originalWsObject.setRestParameters(tblParams.getInput());

        tblHeaders.removeEmptyProperty();
        originalWsObject.setHttpHeaderProperties(tblHeaders.getInput());

        originalWsObject.setHttpBody(requestBody.getTextWidget().getText());
    }

    @Override
    protected void populateDataToUI() {
        wsApiControl.getRequestURLControl().setText(originalWsObject.getRestUrl());
        String restRequestMethod = originalWsObject.getRestRequestMethod();
        int index = Arrays.asList(WebServiceRequestEntity.REST_REQUEST_METHODS).indexOf(restRequestMethod);
        wsApiControl.getRequestMethodControl().select(index < 0 ? 0 : index);

        tempPropList = new ArrayList<WebElementPropertyEntity>(originalWsObject.getRestParameters());
        params.clear();
        params.addAll(tempPropList);
        tblParams.refresh();

        tempPropList = new ArrayList<WebElementPropertyEntity>(originalWsObject.getHttpHeaderProperties());
        httpHeaders.clear();
        httpHeaders.addAll(tempPropList);
        tblHeaders.refresh();

        populateBasicAuthFromHeader();

        requestBody.setDocument(createDocument(originalWsObject.getHttpBody()));
        tabBody.getControl().setEnabled(isBodySupported());
        dirtyable.setDirty(false);
    }

}
