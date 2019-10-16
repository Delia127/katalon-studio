package com.kms.katalon.composer.mobile.recorder.components;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kms.katalon.composer.mobile.objectspy.components.MobileAppComposite;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileAppDialog;
import com.kms.katalon.composer.mobile.recorder.constants.MobileRecoderMessagesConstants;
import com.kms.katalon.composer.mobile.recorder.utils.MobileCompositeUtil;

public class MobileConfigurationsComposite extends Composite {

    private Dialog parentDialog;

    private MobileAppComposite mobileComposite;

    private Composite appsComposite;

    public Composite getAppsComposite() {
        return appsComposite;
    }

    public MobileConfigurationsComposite(Dialog parentDialog, Composite parent, int style, MobileAppComposite mobileComposite) {
        super(parent, style | SWT.NONE);
        this.parentDialog = parentDialog;
        this.mobileComposite = mobileComposite;
        this.createComposite(parent);
    }

    public MobileConfigurationsComposite(Dialog parentDialog, Composite parent, MobileAppComposite mobileComposite) {
        this(parentDialog, parent, 0, mobileComposite);
    }

    private void createComposite(Composite parent) {
        setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout glSettingComposite = new GridLayout(2, false);
        glSettingComposite.horizontalSpacing = 10;
        setLayout(glSettingComposite);

        createCompositeLabel(this);
        createConfigurationsComposite(this);
    }

    private void createCompositeLabel(Composite parent) {
        Label lblConfiguration = new Label(parent, SWT.NONE);
        lblConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        lblConfiguration.setFont(MobileCompositeUtil.getFontBold(lblConfiguration));
        lblConfiguration.setText(MobileRecoderMessagesConstants.LBL_CONFIGURATIONS);
    }

    private void createConfigurationsComposite(Composite parent) {
        appsComposite = new Composite(parent, SWT.NONE);
        appsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        appsComposite.setLayout(new FillLayout());

        mobileComposite.createComposite(appsComposite, SWT.NONE, (MobileAppDialog) this.parentDialog);
    }
}
