package com.kms.katalon.composer.webservice.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.webservice.constants.ComposerWebserviceMessageConstants;
import com.kms.katalon.composer.webservice.constants.ImageConstants;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.entity.repository.DraftWebServiceRequestEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class WebServiceAPIControl extends Composite {

    private ToolItem btnSend;

    private GridData layoutData;

    private boolean sendingState;
    
    private Menu menuSend;
    
    private MenuItem mniSendAndVerify;
    
    private ToolItem btnAddRequestToTestCase;
    
    private Menu menuAddRequestToTestCase;
    
    private MenuItem mniAddRequestToNewTestCase;
    
    private MenuItem mniAddRequestToExistingTestCase;
    
    private ToolItem btnSaveDraft;
    
    private WebServiceRequestEntity originalWsObject;
            
    public WebServiceAPIControl(Composite parent, WebServiceRequestEntity requestEntity) {
        super(parent, SWT.NONE);
        originalWsObject = requestEntity;
        boolean isDraft = isDraft();
        setLayout(new GridLayout());
        createControl(isDraft);
    }
    
    private boolean isDraft() {
        return originalWsObject instanceof DraftWebServiceRequestEntity;
    }

    private void createControl(boolean isDraft) {
        ToolBar toolbar = new ToolBar(this, SWT.RIGHT | SWT.RIGHT);
        toolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        btnSend = new ToolItem(toolbar, SWT.DROP_DOWN);
        setSendButtonState(false);
                
        menuSend = new Menu(btnSend.getParent().getShell());
        mniSendAndVerify = new MenuItem(menuSend, SWT.PUSH);
        mniSendAndVerify.setText(StringConstants.MENU_ITEM_TEST_REQUEST_AND_VERIFY);
        mniSendAndVerify.setID(0);
        
        btnSend.setData(menuSend);
        
        if (!isDraft) {
            btnAddRequestToTestCase = new ToolItem(toolbar, SWT.DROP_DOWN);
            btnAddRequestToTestCase.setImage(ImageConstants.WS_ADD_TO_TEST_CASE_24);
            
            menuAddRequestToTestCase = new Menu(btnAddRequestToTestCase.getParent().getShell());
            mniAddRequestToNewTestCase = new MenuItem(menuAddRequestToTestCase, SWT.PUSH);
            mniAddRequestToNewTestCase.setText(StringConstants.MENU_ITEM_ADD_REQUEST_TO_NEW_TEST_CASE);
            mniAddRequestToNewTestCase.setID(0);
            mniAddRequestToExistingTestCase = new MenuItem(menuAddRequestToTestCase, SWT.PUSH);
            mniAddRequestToExistingTestCase.setText(StringConstants.MENU_ITEM_ADD_REQUEST_TO_EXISTING_TEST_CASE);
            mniAddRequestToExistingTestCase.setID(1);
            
            btnAddRequestToTestCase.setData(menuAddRequestToTestCase);
        }
        
        if (isDraft) {
            btnSaveDraft = new ToolItem(toolbar, SWT.PUSH);
            btnSaveDraft.setImage(ImageConstants.IMG_24_SAVE);
            btnSaveDraft.setToolTipText(ComposerWebserviceMessageConstants.BTN_SAVE_DRAFT_REQUEST);
        }
        
        toolbar.setLayoutData(new GridData(SWT.CENTER, SWT.RIGHT, false, true));
    }

    public void addSendSelectionListener(SelectionListener selectionListener) {
        if (selectionListener == null) {
            return;
        }
        btnSend.addSelectionListener(selectionListener);
    }
    
    public void addSendAndVerifySelectionListener(SelectionListener selectionListener) {
        if (selectionListener == null) {
            return;
        }
        mniSendAndVerify.addSelectionListener(selectionListener);
    }
    
    public void addAddRequestToTestCaseSelectionListener(SelectionListener selectionListener) {
        if (selectionListener == null) {
            return;
        }
        btnAddRequestToTestCase.addSelectionListener(selectionListener);
    }
    
    public void addAddRequestToNewTestCaseSelectionListener(SelectionListener selectionListener) {
        if (selectionListener == null) {
            return;
        }
        mniAddRequestToNewTestCase.addSelectionListener(selectionListener);
    }
    
    public void addAddRequestToExistingTestCaseSelectionListener(SelectionListener selectionListener) {
        if (selectionListener == null) {
            return;
        }
        mniAddRequestToExistingTestCase.addSelectionListener(selectionListener);
    }
    
    public void addSaveDraftSelectionListener(SelectionListener selectionListener) {
        if (selectionListener == null) {
            return;
        }
        btnSaveDraft.addSelectionListener(selectionListener);
    }
  
    public void setLayoutDataColumnsSpan(int numberOfColumn) {
        layoutData.horizontalSpan = numberOfColumn;
        layout();
    }

    public ToolItem getSendControl() {
        return btnSend;
    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

    public void setSendButtonState(boolean sendingState) {
        this.sendingState = sendingState;
        if (this.sendingState) {
            btnSend.setToolTipText(StringConstants.STOP);
            btnSend.setImage(ImageConstants.IMG_24_STOP);
        } else {
            btnSend.setToolTipText(ComposerWebserviceMessageConstants.BTN_SEND_TEST_REQUEST);
            btnSend.setImage(ImageConstants.IMG_24_PLAY);
        }
        btnSend.getParent().update();
    }

    public boolean getSendingState() {
        return sendingState;
    }
    
    public Menu getSendMenu() {
        return menuSend;
    }
    
    public Menu getAddRequestToTestCaseMenu() {
        return menuAddRequestToTestCase;
    }
}