package com.kms.katalon.composer.webservice.view;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.webservice.constants.ImageConstants;
import com.kms.katalon.composer.webservice.constants.StringConstants;

public class ApiQuickStartDialog extends Dialog {

    private static final Point ITEM_IMG_SIZE = new Point(64, 64);
    
    public ApiQuickStartDialog(Shell parentShell) {
        super(parentShell);
        
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite body = new Composite(parent, SWT.NONE);
        GridData gdBody = new GridData(SWT.FILL, SWT.FILL, true, true);
        gdBody.widthHint = 400;
        body.setLayoutData(gdBody);
        body.setLayout(new GridLayout(2, false));
        
        createNewRestRequestItem(body);
        createNewSoapRequestItem(body);
        createImportRestRequestItem(body);
        createImportSoapRequestItem(body);
        
        return super.createDialogArea(parent);
    }
    
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(StringConstants.DIA_TITLE_QUICKSTART);
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CLOSE_LABEL, true);
    }
       
    private Composite createNewRestRequestItem(Composite parent) {
        Composite item = createQuickStartItem(parent, ImageConstants.WS_NEW_REST_REQUEST_64);
        addLinkTextToQuickStartItem(item, StringConstants.QUICKSTART_NEW_REST_REQUEST, e -> {
            createNewRestRequest();
        });
        return item;
    }
    
    private Composite createNewSoapRequestItem(Composite parent) {
        Composite item = createQuickStartItem(parent, ImageConstants.WS_NEW_SOAP_REQUEST_64);
        addLinkTextToQuickStartItem(item, StringConstants.QUICKSTART_NEW_SOAP_REQUEST, e -> {
            createNewSoapRequest();
        });
        return item;
    }
    
    private Composite createImportRestRequestItem(Composite parent) {
        Composite item = createQuickStartItem(parent, ImageConstants.WS_IMPORT_REST_REQUEST_64);
        addLinkTextToQuickStartItem(item, StringConstants.QUICKSTART_IMPORT_SWAGGER_FROM_FILE, e -> {
            importSwaggerFromFile();
        });
        addLinkTextToQuickStartItem(item, StringConstants.QUICKSTART_IMPORT_SWAGGER_FROM_URL, e -> {
            importSwaggerFromUrl();
        });
        return item;
    }
    
    private Composite createImportSoapRequestItem(Composite parent) {
        Composite item = createQuickStartItem(parent, ImageConstants.WS_IMPORT_SOAP_REQUEST_64);
        addLinkTextToQuickStartItem(item, StringConstants.QUICKSTART_IMPORT_WSDL_FROM_FILE, e -> {
            importWsdlFromFile();
        });
        addLinkTextToQuickStartItem(item, StringConstants.QUICKSTART_IMPORT_WSDL_FROM_URL, e -> {
            importWsdlFromUrl();
        });
        return item;
    }
    
    private Composite createQuickStartItem(Composite parent, Image image) {
        Composite item = new Composite(parent, SWT.NONE);
        item.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout glItem = new GridLayout(1, false);
        glItem.marginTop = 10;
        item.setLayout(glItem);
        item.setBackground(parent.getBackground());
        
        Composite imgComposite = new Composite(item, SWT.NONE);
        GridData gdImg = new GridData(SWT.CENTER, SWT.FILL, true, true);
        gdImg.widthHint = ITEM_IMG_SIZE.x;
        gdImg.heightHint = ITEM_IMG_SIZE.y;
        imgComposite.setLayoutData(gdImg);
        GridLayout glImg = new GridLayout(1, false);
        imgComposite.setLayout(glImg);
        imgComposite.addPaintListener(e -> {
            e.gc.drawImage(image, 0, 0);
        });
        
        return item;
    }
    
    private void addLinkTextToQuickStartItem(Composite item, String text, Listener selectionListener) {
        Color textColor = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
        
        CLabel lblItemText = new CLabel(item, SWT.NONE);
        lblItemText.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true));
        lblItemText.setText(text);
        lblItemText.setCursor(Display.getCurrent().getSystemCursor(SWT.CURSOR_HAND));
        lblItemText.setForeground(textColor);
        lblItemText.setBackground(item.getBackground());
        lblItemText.addListener(SWT.MouseDown, selectionListener);
    }
    
    private void createNewRestRequest() {
        
    }
    
    private void createNewSoapRequest() {
        
    }
    
    private void importSwaggerFromFile() {
        
    }
    
    private void importSwaggerFromUrl() {
        
    }
    
    private void importWsdlFromFile() {
        
    }
    
    private void importWsdlFromUrl() {
        
    }
}
