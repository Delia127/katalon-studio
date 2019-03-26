package com.kms.katalon.composer.view;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


import com.kms.katalon.composer.components.impl.dialogs.AddMailRecipientDialog;
import com.kms.katalon.composer.testsuite.constants.StringConstants;
import com.kms.katalon.composer.testsuite.parts.AbstractTestSuiteUIDescriptionView;

public class KatalonTestSuiteUIView extends AbstractTestSuiteUIDescriptionView {

	private static final int MINIMUM_COMPOSITE_SIZE = 300;

	private Button btnAddMailRcp;

	private Button btnDeleteMailRcp;

	private Button btnClearMailRcp;

	private ListViewer listMailRcpViewer;

	private Label lblReRun;

	private Text txtRerun;

	private Label lblReRunTestCaseOnly;

	private Button rerunTestCaseOnly;

	private Button radioUserDefinePageLoadTimeout;

	private Button radioUseDefaultPageLoadTimeout;

	private Text txtUserDefinePageLoadTimeout;

	private Label lblMailRecipients;
	
	private MPart mpart;

	public KatalonTestSuiteUIView(com.kms.katalon.entity.testsuite.TestSuiteEntity testSuiteEntity,
			MPart mpart) {
		super(testSuiteEntity, mpart);
		this.mpart = mpart;
	}

	
    private void registerControlListeners() {
        btnAddMailRcp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Shell shell = Display.getDefault().getActiveShell();
                AddMailRecipientDialog addMailDialog = new AddMailRecipientDialog(shell, listMailRcpViewer.getList()
                        .getItems());
                addMailDialog.open();

                if (addMailDialog.getReturnCode() == Dialog.OK) {
                    String[] emails = addMailDialog.getEmails();
                    if (emails.length > 0) {
                        listMailRcpViewer.add(addMailDialog.getEmails());
                        setDirty(true);
                    }
                }
            }
        });

        btnClearMailRcp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (listMailRcpViewer.getList().getItemCount() > 0) {
                    listMailRcpViewer.setInput(new String[0]);
                    setDirty(true);
                }
            }
        });

        btnDeleteMailRcp.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Object[] emails = ((IStructuredSelection) listMailRcpViewer.getSelection()).toArray();
                if (emails.length > 0) {
                    listMailRcpViewer.remove(emails);
                    setDirty(true);
                }
            }
        });
        
        radioUseDefaultPageLoadTimeout.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                txtUserDefinePageLoadTimeout.setEnabled(false);
                // testSuiteEntity.setPageLoadTimeoutDefault(true);
                setDirty(true);
            }
        });

        radioUserDefinePageLoadTimeout.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                txtUserDefinePageLoadTimeout.setEnabled(true);
                // testSuiteEntity.setPageLoadTimeoutDefault(false);
                setDirty(true);
            }
        });

        // Number input only
        VerifyListener verifyNumberListener = new VerifyListener() {

            @Override
            public void verifyText(VerifyEvent e) {
                String string = e.text;
                char[] chars = new char[string.length()];
                string.getChars(0, chars.length, chars, 0);
                for (int i = 0; i < chars.length; i++) {
                    if (!('0' <= chars[i] && chars[i] <= '9')) {
                        e.doit = false;
                        return;
                    }
                }
                setDirty(true);
            }
        };

        txtUserDefinePageLoadTimeout.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String text = ((Text) e.getSource()).getText();
                try {
                    Integer.parseInt(text);
                } catch (NumberFormatException ex) {}
            }
        });
        txtUserDefinePageLoadTimeout.addVerifyListener(verifyNumberListener);

        txtRerun.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                String text = ((Text) e.getSource()).getText();
                try {
                    int rerun = Integer.parseInt(text);
                    // limit to 100 times only
                    if (rerun > 100) {
                        rerun = 100;
                        ((Text) e.getSource()).setText(String.valueOf(rerun));
                    }
                    // getTestSuite().setNumberOfRerun(rerun);
                } catch (NumberFormatException ex) {}
            }
        });
        txtRerun.addVerifyListener(verifyNumberListener);

        rerunTestCaseOnly.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	// testSuiteEntity.setRerunFailedTestCasesOnly(rerunTestCaseOnly.getSelection());
            	setDirty(true);
            }
        });
    }

	@Override
	public Composite createContainer(Composite parent) {
		
		Composite compositeExecutionDetails = new Composite(parent, SWT.NONE);
        compositeExecutionDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glCompositeExecutionDetail = new GridLayout(2, false);
        glCompositeExecutionDetail.marginLeft = 45;
        glCompositeExecutionDetail.horizontalSpacing = 40;
        compositeExecutionDetails.setLayout(glCompositeExecutionDetail);

        Composite compositePageLoadTimeout = new Composite(compositeExecutionDetails, SWT.NONE);
        GridData gdCompositePageLoadTimeout = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
        gdCompositePageLoadTimeout.minimumWidth = MINIMUM_COMPOSITE_SIZE;
        compositePageLoadTimeout.setLayoutData(gdCompositePageLoadTimeout);
        GridLayout glCompositePageLoadTimeout = new GridLayout(1, false);
        glCompositePageLoadTimeout.marginWidth = 0;
        glCompositePageLoadTimeout.marginHeight = 0;
        glCompositePageLoadTimeout.horizontalSpacing = 10;
        compositePageLoadTimeout.setLayout(glCompositePageLoadTimeout);

        Group grpPageLoadTimeout = new Group(compositePageLoadTimeout, SWT.NONE);
        grpPageLoadTimeout.setText(StringConstants.PA_LBL_PAGE_LOAD_TIMEOUT);
        grpPageLoadTimeout.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        GridLayout gl_grpPageLoadTimeout = new GridLayout(2, false);
        gl_grpPageLoadTimeout.marginLeft = 50;
        gl_grpPageLoadTimeout.marginWidth = 0;
        grpPageLoadTimeout.setLayout(gl_grpPageLoadTimeout);

        radioUseDefaultPageLoadTimeout = new Button(grpPageLoadTimeout, SWT.RADIO);
        radioUseDefaultPageLoadTimeout.setText(StringConstants.PA_LBL_USE_DEFAULT);
        new Label(grpPageLoadTimeout, SWT.NONE);

        radioUserDefinePageLoadTimeout = new Button(grpPageLoadTimeout, SWT.RADIO);
        GridData gd_radioUserDefinePageLoadTimeout = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        radioUserDefinePageLoadTimeout.setLayoutData(gd_radioUserDefinePageLoadTimeout);
        radioUserDefinePageLoadTimeout.setText(StringConstants.PA_LBL_USER_DEFINE);

        txtUserDefinePageLoadTimeout = new Text(grpPageLoadTimeout, SWT.BORDER);
        GridData gdTxtUserDefinePageLoadTimeout = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTxtUserDefinePageLoadTimeout.heightHint = 20;
        txtUserDefinePageLoadTimeout.setLayoutData(gdTxtUserDefinePageLoadTimeout);
        txtUserDefinePageLoadTimeout.setTextLimit(4);

        Composite compositeLastRunAndReRun = new Composite(compositePageLoadTimeout, SWT.NONE);
        GridData gdCompositeTestDataAndLastRun = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gdCompositeTestDataAndLastRun.minimumWidth = MINIMUM_COMPOSITE_SIZE;
        compositeLastRunAndReRun.setLayoutData(gdCompositeTestDataAndLastRun);
        GridLayout glCompositeTestDataAndLastRun = new GridLayout(4, false);
        glCompositeTestDataAndLastRun.verticalSpacing = 10;
        compositeLastRunAndReRun.setLayout(glCompositeTestDataAndLastRun);

        lblReRun = new Label(compositeLastRunAndReRun, SWT.NONE);
        GridData gdLblReRun = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdLblReRun.widthHint = 85;
        lblReRun.setLayoutData(gdLblReRun);
        lblReRun.setText(StringConstants.PA_LBL_RETRY);
        lblReRun.setToolTipText(StringConstants.PA_LBL_TOOLTIP_RETRY);

        txtRerun = new Text(compositeLastRunAndReRun, SWT.BORDER);
        GridData gdTxtRerun = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTxtRerun.heightHint = 20;
        gdTxtRerun.widthHint = 40;
        txtRerun.setLayoutData(gdTxtRerun);
        txtRerun.setToolTipText(StringConstants.PA_LBL_TOOLTIP_RETRY);
        txtRerun.setTextLimit(3);

        lblReRunTestCaseOnly = new Label(compositeLastRunAndReRun, SWT.NONE);
        GridData gdLblReRunTestCaseOnly = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
        gdLblReRunTestCaseOnly.widthHint = 150;
        lblReRunTestCaseOnly.setLayoutData(gdLblReRunTestCaseOnly);
        lblReRunTestCaseOnly.setText(StringConstants.PA_LBL_TEST_CASE_ONLY);
        lblReRunTestCaseOnly.setToolTipText(StringConstants.PA_LBL_TOOLTIP_TEST_CASE_ONLY);

        rerunTestCaseOnly = new Button(compositeLastRunAndReRun, SWT.CHECK);
        GridData gdRerunTestCase = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
        gdRerunTestCase.heightHint = 20;
        gdRerunTestCase.minimumWidth = 20;
        rerunTestCaseOnly.setLayoutData(gdRerunTestCase);
        rerunTestCaseOnly.setToolTipText(StringConstants.PA_LBL_TOOLTIP_TEST_CASE_ONLY);

        Composite compositeMailRecipients = new Composite(compositeExecutionDetails, SWT.NONE);
        GridData gdCompositeMailRecipients = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        compositeMailRecipients.setLayoutData(gdCompositeMailRecipients);
        GridLayout glCompositeMailRecipients = new GridLayout(3, false);
        compositeMailRecipients.setLayout(glCompositeMailRecipients);

        lblMailRecipients = new Label(compositeMailRecipients, SWT.NONE);
        GridData gdLblMailRecipients = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
        gdLblMailRecipients.verticalIndent = 5;
        lblMailRecipients.setLayoutData(gdLblMailRecipients);
        lblMailRecipients.setText(StringConstants.PA_LBL_MAIL_RECIPIENTS);

        listMailRcpViewer = new ListViewer(compositeMailRecipients, SWT.BORDER | SWT.V_SCROLL);
        org.eclipse.swt.widgets.List  listMailRcp = listMailRcpViewer.getList();
        listMailRcp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        listMailRcpViewer.setContentProvider(ArrayContentProvider.getInstance());

        Composite compositeMailRcpButtons = new Composite(compositeMailRecipients, SWT.NONE);
        compositeMailRcpButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        GridLayout glCompositeMailRcpButtons = new GridLayout(1, false);
        glCompositeMailRcpButtons.marginWidth = 0;
        glCompositeMailRcpButtons.marginHeight = 0;
        compositeMailRcpButtons.setLayout(glCompositeMailRcpButtons);

        btnAddMailRcp = new Button(compositeMailRcpButtons, SWT.FLAT);
        btnAddMailRcp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        btnAddMailRcp.setText(StringConstants.PA_BTN_ADD);

        btnDeleteMailRcp = new Button(compositeMailRcpButtons, SWT.FLAT);
        btnDeleteMailRcp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnDeleteMailRcp.setText(StringConstants.PA_BTN_DEL);

        btnClearMailRcp = new Button(compositeMailRcpButtons, SWT.FLAT);
        btnClearMailRcp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnClearMailRcp.setText(StringConstants.PA_BTN_CLEAR);
        
        registerControlListeners();
        
        return compositeExecutionDetails;
	}

}
