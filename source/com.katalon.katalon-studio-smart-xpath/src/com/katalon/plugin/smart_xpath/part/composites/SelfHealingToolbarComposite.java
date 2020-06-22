package com.katalon.plugin.smart_xpath.part.composites;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.katalon.plugin.smart_xpath.constant.SmartXPathMessageConstants;
import com.kms.katalon.composer.components.util.ColorUtil;

public class SelfHealingToolbarComposite extends Composite {

    private Button btnApprove, btnDiscard;

    private Label lblHealingStatus;

    public SelfHealingToolbarComposite(Composite parent, int style) {
        super(parent, style);
        createContents(this);
    }

    protected void createContents(Composite container) {
        configContainer(container);
        createStatusComposite(container);
        Composite buttonsComposite = createButtonsComposite(container);
        btnDiscard = createDiscardButton(buttonsComposite);
        btnApprove = createApproveButton(buttonsComposite);
    }

    protected Composite configContainer(Composite container) {
        GridData toolbarLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
        container.setLayoutData(toolbarLayoutData);
        GridLayout toolbarLayout = new GridLayout(2, false);
        toolbarLayout.marginHeight = 0;
        toolbarLayout.marginWidth = 0;
        container.setLayout(toolbarLayout);
        return container;
    }

    protected Composite createStatusComposite(Composite container) {
        Composite statusComposite = new Composite(container, SWT.NONE);
        statusComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        GridLayout statusLayout = new GridLayout(2, false);
        statusComposite.setLayout(statusLayout);

        lblHealingStatus = new Label(statusComposite, SWT.WRAP);
        lblHealingStatus.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));

        return statusComposite;
    }

    protected Composite createButtonsComposite(Composite container) {
        Composite buttonsComposite = new Composite(container, SWT.NONE);
        buttonsComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false, 1, 1));
        RowLayout buttonsLayout = new RowLayout();
        buttonsLayout.wrap = true;
        buttonsLayout.pack = true;
        buttonsLayout.spacing = 5;
        buttonsLayout.marginHeight = 0;
        buttonsComposite.setLayout(buttonsLayout);

        return buttonsComposite;
    }

    protected Button createApproveButton(Composite buttonsComposite) {
        Button btnApprove = new Button(buttonsComposite, SWT.PUSH);
        btnApprove.setText(SmartXPathMessageConstants.BTN_APPROVE_PROPOSED_LOCATORS);

        Shell shell = btnApprove.getShell();
        if (shell != null) {
            shell.setDefaultButton(btnApprove);
        }

        return btnApprove;
    }

    protected Button createDiscardButton(Composite buttonsComposite) {
        Button btnDiscard = new Button(buttonsComposite, SWT.PUSH);
        btnDiscard.setText(SmartXPathMessageConstants.BTN_DISCARD_PROPOSED_LOCATORS);
        return btnDiscard;
    }

    public void setSuccessMessage(String message) {
        lblHealingStatus.setText(message);
        lblHealingStatus.setForeground(ColorUtil.getTextSuccessfulColor());
        lblHealingStatus.requestLayout();
    }

    public void setErrorMessage(String message) {
        lblHealingStatus.setText(message);
        lblHealingStatus.setForeground(ColorUtil.getTextSuccessfulColor());
        lblHealingStatus.requestLayout();
    }

    public void notifyRecoverSucceeded(int numberRecovered) {
        setSuccessMessage(MessageFormat.format(numberRecovered == 1
                ? SmartXPathMessageConstants.MSG_RECOVER_BROKEN_TEST_OBJECTS_SUCCEEDED_SINGULAR
                : SmartXPathMessageConstants.MSG_RECOVER_BROKEN_TEST_OBJECTS_SUCCEEDED_PLURAL, numberRecovered));
    }

    public void notifyRecoverFailed() {
        setSuccessMessage(SmartXPathMessageConstants.MSG_RECOVER_BROKEN_TEST_OBJECTS_FAILED);
    }

    public void notifyDiscardSucceeded(int numberDiscarded) {
        setSuccessMessage(MessageFormat.format(numberDiscarded == 1
                ? SmartXPathMessageConstants.MSG_DISCARD_BROKEN_TEST_OBJECTS_SUCCEEDED_SINGULAR
                : SmartXPathMessageConstants.MSG_DISCARD_BROKEN_TEST_OBJECTS_SUCCEEDED_PLURAL, numberDiscarded));
    }

    public void clearStatusMessage() {
        lblHealingStatus.setText(StringUtils.EMPTY);
        lblHealingStatus.requestLayout();
    }

    public void addApproveListener(SelectionListener listener) {
        btnApprove.addSelectionListener(listener);
    }

    public void addDiscardListener(SelectionListener listener) {
        btnDiscard.addSelectionListener(listener);
    }
}
