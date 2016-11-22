package com.kms.katalon.composer.webservice.parts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.kms.katalon.composer.components.impl.dialogs.ProgressMonitorDialogWithThread;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.view.ExpandableComposite;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class SoapRequestObjectPart extends RequestObjectPart {

    private Text txtWSDL;

    private Combo cbbSoapServiceFunction;

    private Text txtSoapBody;

    private Combo cbbSoapRequestMethod;

    @Override
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
        soapDetailsComposite.setLayout(new GridLayout(3, false));

        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        gridData.heightHint = 20;

        GridData lblGridData = new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1);

        // WSDL
        Label lblWsdl = new Label(soapDetailsComposite, SWT.LEFT | SWT.WRAP);
        lblWsdl.setText(StringConstants.PA_LBL_URL);
        lblWsdl.setLayoutData(lblGridData);

        txtWSDL = new Text(soapDetailsComposite, SWT.BORDER);
        txtWSDL.setLayoutData(gridData);
        txtWSDL.addModifyListener(modifyListener);

        // Request Method
        Label lblRequestMethod = new Label(soapDetailsComposite, SWT.LEFT | SWT.WRAP);
        lblRequestMethod.setText(StringConstants.PA_LBL_REQ_METHOD);
        lblRequestMethod.setLayoutData(lblGridData);

        cbbSoapRequestMethod = new Combo(soapDetailsComposite, SWT.NONE);
        gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
        gridData.widthHint = 100;
        cbbSoapRequestMethod.setLayoutData(gridData);
        cbbSoapRequestMethod.setItems(WebServiceRequestEntity.SOAP_REQUEST_METHODS);
        cbbSoapRequestMethod.select(0);
        cbbSoapRequestMethod.addModifyListener(modifyListener);

        // SOAP Function name
        Label lblFunctionName = new Label(soapDetailsComposite, SWT.LEFT | SWT.WRAP);
        lblFunctionName.setText("Function Name");
        lblFunctionName.setLayoutData(lblGridData);

        cbbSoapServiceFunction = new Combo(soapDetailsComposite, SWT.NONE);
        gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gridData.widthHint = 250;
        cbbSoapServiceFunction.setLayoutData(gridData);
        cbbSoapServiceFunction.addModifyListener(modifyListener);

        Button btnLoad = new Button(soapDetailsComposite, SWT.NONE);
        gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gridData.widthHint = 120;
        btnLoad.setText(StringConstants.LBL_LOAD_FROM_WSDL);
        btnLoad.setLayoutData(gridData);
        btnLoad.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent arg0) {
                if (!txtWSDL.getText().trim().isEmpty()) {
                    try {
                        new ProgressMonitorDialogWithThread(Display.getCurrent().getActiveShell()).run(true, true,
                                new IRunnableWithProgress() {
                                    @Override
                                    public void run(IProgressMonitor monitor)
                                            throws InvocationTargetException, InterruptedException {
                                        monitor.beginTask(StringConstants.MSG_FETCHING_FROM_WSDL,
                                                IProgressMonitor.UNKNOWN);
                                        Display.getDefault().asyncExec(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    List<String> servFuncs = getServiceMethods(
                                                            txtWSDL.getText().trim());
                                                    if (servFuncs.size() > 0) {
                                                        cbbSoapServiceFunction.setItems(
                                                                servFuncs.toArray(new String[servFuncs.size()]));
                                                        cbbSoapServiceFunction.select(0);
                                                    }
                                                } catch (IOException ex) {
                                                    LoggerSingleton.logError(ex);
                                                    MessageDialog.openError(Display.getCurrent().getActiveShell(),
                                                            StringConstants.ERROR_TITLE,
                                                            StringConstants.MSG_CANNOT_LOAD_WS);
                                                } catch (SAXException | ParserConfigurationException
                                                        | XPathExpressionException ex) {
                                                    LoggerSingleton.logError(ex);
                                                    MessageDialog.openError(Display.getCurrent().getActiveShell(),
                                                            StringConstants.ERROR_TITLE,
                                                            StringConstants.MSG_CANNOT_PARSE_WSDL);
                                                }

                                            }
                                        });
                                        monitor.done();
                                    }
                                });
                    } catch (InvocationTargetException | InterruptedException ex) {
                        LoggerSingleton.logError(ex);
                    }
                }
            }

            public void widgetDefaultSelected(SelectionEvent arg0) {
            }
        });
    }

    @Override
    public void createHttpComposite(Composite mainComposite) {
        ExpandableComposite soapComposite = new ExpandableComposite(mainComposite, StringConstants.PA_LBL_XML_REQ_MSG,
                1, true);
        Composite compositeDetails = soapComposite.createControl();

        Composite httpContainerComposite = new Composite(compositeDetails, SWT.NONE);
        httpContainerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        httpContainerComposite.setLayout(new GridLayout(2, false));

        txtSoapBody = new Text(httpContainerComposite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
        GridData gdData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gdData.heightHint = 150;
        txtSoapBody.setLayoutData(gdData);
        txtSoapBody.addModifyListener(modifyListener);
    }

    @Override
    protected void updateEntityBeforeSaved() {
        originalWsObject.setWsdlAddress(txtWSDL.getText());
        originalWsObject.setSoapRequestMethod(cbbSoapRequestMethod.getText());
        originalWsObject.setSoapServiceFunction(cbbSoapServiceFunction.getText());
        originalWsObject.setSoapBody(txtSoapBody.getText());
    }

    @Override
    protected void showEntityFieldsToUi() {
        txtWSDL.setText(originalWsObject.getWsdlAddress());
        int index = Arrays.asList(WebServiceRequestEntity.SOAP_REQUEST_METHODS)
                .indexOf(originalWsObject.getSoapRequestMethod());
        cbbSoapRequestMethod.select(index < 0 ? 0 : index);
        cbbSoapServiceFunction.setText(originalWsObject.getSoapServiceFunction());
        txtSoapBody.setText(originalWsObject.getSoapBody());
        dirtyable.setDirty(false);
    }

    @Override
    @Persist
    public void save() {
        super.save();
    }

    private List<String> getServiceMethods(String wsdl)
            throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {

        URL obj = new URL(wsdl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");

        String xml = processResponse(con);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        XPath xPath = XPathFactory.newInstance().newXPath();

        String httpMethod = cbbSoapRequestMethod.getItem(cbbSoapRequestMethod.getSelectionIndex());
        NodeList bindingNodes = (NodeList) xPath.evaluate("//*[name()='wsdl:binding']", doc, XPathConstants.NODESET);
        for (int i = 0; i < bindingNodes.getLength(); i++) {
            Node bindingNode = bindingNodes.item(i);
            NodeList childNodes = bindingNode.getChildNodes();
            List<String> methods = new ArrayList<String>();
            boolean found = false;
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node child = childNodes.item(j);
                if (child.getNodeName().equals("http:binding")
                        && httpMethod.equalsIgnoreCase(child.getAttributes().getNamedItem("verb").getNodeValue())) {
                    found = true;
                } else if (child.getNodeName().equals("wsdl:operation")) {
                    methods.add(child.getAttributes().getNamedItem("name").getNodeValue());
                }
            }
            if (found) {
                return methods;
            }
            methods.clear();
        }
        return new ArrayList<String>();
    }

    private String processResponse(HttpURLConnection conn) throws IOException {
        if (conn == null) {
            return null;
        }
        int statusCode = conn.getResponseCode();
        StringBuffer sb = new StringBuffer();
        try (InputStream inputStream = (statusCode >= 400) ? conn.getErrorStream() : conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                sb.append(inputLine);
            }
        }
        conn.disconnect();
        return sb.toString();
    }

}
