package com.kms.katalon.about.dialog;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.core.runtime.IBundleGroupProvider;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.IWorkbenchHelpContextIds;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.about.AboutBundleGroupData;
import org.eclipse.ui.internal.about.InstallationDialog;

import com.kms.katalon.application.KatalonApplicationActivator;
import com.kms.katalon.application.constants.ApplicationStringConstants;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.application.utils.ApplicationInfo;
import com.kms.katalon.application.utils.VersionInfo;
import com.kms.katalon.application.utils.VersionUtil;
import com.kms.katalon.composer.components.ComponentBundleActivator;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.constants.MessageConstants;
import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.integration.analytics.constants.IntegrationAnalyticsMessages;
import com.kms.katalon.integration.analytics.entity.AnalyticsTokenInfo;
import com.kms.katalon.integration.analytics.exceptions.AnalyticsApiExeception;
import com.kms.katalon.integration.analytics.providers.AnalyticsApiProvider;
import com.kms.katalon.license.models.LicenseType;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.util.CryptoUtil;

/**
 * Displays information about the product.
 */
@SuppressWarnings({ "restriction" })
public class KatalonAboutDialog extends TrayDialog {
    private String productName;

    private Image logo;

    private IProduct product;

    protected static final String VERSION_UPDATE = "KatalonVersionUpdate";

    private static final String KATALON_NAME_INFO = "Katalon Studio";

    private static final String KSE_NAME_INFO = "Katalon Studio Enterprise";

    private boolean isTrial = false;

    private boolean isKSE = false;

    private boolean isLatestVersion = true;

    private String latestVersion;

    private String expirationDate;

    private static LicenseType licenseType;

    private Link lnkCopyright;

    private Button btnInstalltionDetails, btnOk;

    private Label lblNotice, notice, expiration;

    /**
     * Create an instance of the AboutDialog for the given window.
     * 
     * @param parentShell The parent of the dialog.
     */
    public KatalonAboutDialog(Shell parentShell) {
        super(parentShell);
        licenseType = ActivationInfoCollector.getLicenseType();
        expirationDate = ActivationInfoCollector.getExpirationDate();
        product = Platform.getProduct();
        if (product != null) {
            productName = getProductNameBasedOnLicenseType();
        }
        if (productName == null) {
            productName = WorkbenchMessages.AboutDialog_defaultProductName;
        }

        // create a descriptive object for each BundleGroup
        IBundleGroupProvider[] providers = Platform.getBundleGroupProviders();
        LinkedList<AboutBundleGroupData> groups = new LinkedList<>();
        if (providers != null) {
            for (int i = 0; i < providers.length; ++i) {
                IBundleGroup[] bundleGroups = providers[i].getBundleGroups();
                for (int j = 0; j < bundleGroups.length; ++j) {
                    groups.add(new AboutBundleGroupData(bundleGroups[j]));
                }
            }
        }
    }

    private String getProductNameBasedOnLicenseType() {
        String name;
        switch (licenseType) {
            case ENTERPRISE:
                name = KSE_NAME_INFO;
                logo = ImageManager.getImage(IImageKeys.LOGO_KSE);
                isTrial = false;
                isKSE = true;
                break;
            case TRIAL:
                name = KSE_NAME_INFO;
                logo = ImageManager.getImage(IImageKeys.LOGO_KSE);
                isTrial = true;
                isKSE = true;
                break;
            case FREE:
                name = KATALON_NAME_INFO;
                logo = ImageManager.getImage(IImageKeys.LOGO_KATALON_STUDIO);
                isTrial = false;
                isKSE = false;
                break;
            default:
                name = product.getName();
                logo = ImageManager.getImage(IImageKeys.LOGO_KATALON_STUDIO);
                isTrial = false;
                isKSE = false;
                break;
        }
        return name;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {

    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    @Override
    protected void buttonPressed(int buttonId) {

    }

    @Override
    public boolean close() {
        return super.close();
    }

    /*
     * (non-Javadoc) Method declared on Window.
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(NLS.bind(WorkbenchMessages.AboutDialog_shellTitle, productName));
        PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, IWorkbenchHelpContextIds.ABOUT_DIALOG);
    }

    private String getExpirationDate() {
        try {
            if (productName.equals(KSE_NAME_INFO)) {
                if (ActivationInfoCollector.isOfflineLicense()) {
                    return ActivationInfoCollector.getExpirationDate();
                }

                String serverUrl = ApplicationInfo.getTestOpsServer();
                String email = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_EMAIL);
                String encryptedPassword = ApplicationInfo.getAppProperty(ApplicationStringConstants.ARG_PASSWORD);

                if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(encryptedPassword)) {
                    String password = CryptoUtil.decode(CryptoUtil.getDefault(encryptedPassword));
                    AnalyticsTokenInfo token = AnalyticsApiProvider.requestToken(serverUrl, email, password);
                    if (token != null) {
                        Date expDate = null;
                        if (!isTrial) {
                            Long orgId = ApplicationInfo.getOrganization().getId();
                            expDate = AnalyticsApiProvider.getExpirationOnline(serverUrl, token.getAccess_token(), orgId);
                        } else if (isTrial) {
                            expDate = AnalyticsApiProvider.getExpirationTrial(serverUrl, token.getAccess_token());
                        }
                        DateFormat formatter = new SimpleDateFormat("MMMMM dd, yyyy HH:mm");
                        return formatter.format(expDate);
                    }
                }
            } 
        } catch (GeneralSecurityException | IOException | AnalyticsApiExeception e) {
            if (e instanceof AnalyticsApiExeception) {
                try {
                    String message = KatalonApplicationActivator.getFeatureActivator().getTestOpsMessage(e.getMessage());
                    LogUtil.logError(MessageFormat.format(IntegrationAnalyticsMessages.MSG_ERROR_WITH_REASON, message));
                } catch (Exception ex) {
                    //ignore ex
                    LogUtil.logError(e);
                }
            } else {
                LogUtil.logError(e);
            }
        }
        return StringConstants.About_MSG_CANNOT_GET_EXPIRATION_DATE;
    }

    private void updateExpirationDate() {
        Thread getExpiration = new Thread(() -> {
            expirationDate = getExpirationDate();
            UISynchronizeService.asyncExec(() -> {
                if (expirationDate.equals(StringConstants.About_MSG_CANNOT_GET_EXPIRATION_DATE)) {
                    expiration.setForeground(ColorUtil.getTextErrorColor());
                    ControlUtils.setFontStyle(expiration, SWT.ITALIC, 10);
                } else {
                    expiration.setForeground(ColorUtil.getDefaultTextColor());
                    ControlUtils.setFontStyle(expiration, SWT.NONE, 10);
                }
                expiration.setText(expirationDate);
            });
        });
        getExpiration.start();
    }

    private void updateVersionInfo() {
        Thread checkLastestVersion = new Thread(() -> {
            VersionInfo lastestVersion = VersionUtil.getLatestVersion();
            if (VersionUtil.isNewer(lastestVersion.getVersion(), VersionUtil.getCurrentVersion().getVersion())) {
                isLatestVersion = false;
                latestVersion = lastestVersion.getVersion();
            } else {
                isLatestVersion = true;
                latestVersion = MessageConstants.HAND_MSG_UP_TO_DATE;
            }
            UISynchronizeService.asyncExec(() -> {
                notice.setForeground(ColorUtil.getDefaultTextColor());
                notice.setText(latestVersion);
                ControlUtils.setFontStyle(notice, SWT.ITALIC, 10);
                if (!isLatestVersion) {
                    lblNotice.setText(MessageConstants.NEW_VERSION_AVAIABLE);
                    ControlUtils.setFontStyle(notice, SWT.NONE, 10);
                }
            });
        });
        checkLastestVersion.start();
    }

    /**
     * Creates and returns the contents of the upper part
     * of the dialog (above the button bar).
     *
     * Subclasses should overide.
     *
     * @param parent the parent composite to contain the dialog area
     * @return the dialog area control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Image bg = getBackground();
        parent.setBackgroundImage(bg);
        GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        gridData.widthHint = bg.getBounds().width;
        gridData.heightHint = bg.getBounds().height;
        parent.setLayoutData(gridData);
        GridLayout gridLayout = new GridLayout(2, false);
        parent.setLayout(gridLayout);
        parent.setBackgroundMode(SWT.INHERIT_DEFAULT);

        Composite logoComposite = new Composite(parent, SWT.TRANSPARENT);
        GridLayout glLogo = new GridLayout(1, false);
        glLogo.marginTop = 50;
        glLogo.marginLeft = 30;
        logoComposite.setLayout(glLogo);
        logoComposite.setBackgroundMode(SWT.INHERIT_DEFAULT);

        Label lblLogo = new Label(logoComposite, SWT.TRANSPARENT);
        lblLogo.setImage(logo);

        Composite contentComposite = new Composite(parent, SWT.TRANSPARENT);
        GridLayout contentContent = new GridLayout(1, false);
        contentContent.marginTop = 50;
        contentContent.marginLeft = 0;
        contentComposite.setLayout(contentContent);

        Composite titleComposite = new Composite(contentComposite, SWT.TRANSPARENT);
        GridLayout glTitle = new GridLayout(2, false);
        glTitle.marginTop = 0;
        glTitle.marginLeft = 0;
        glTitle.marginRight = 0;
        glTitle.marginBottom = 0;
        titleComposite.setLayout(glTitle);
        GridData gdTitle = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
        titleComposite.setLayoutData(gdTitle);

        Label lblProductName = new Label(titleComposite, SWT.NONE);
        GridData gdProductName = new GridData(SWT.LEFT, SWT.CENTER, true, true);
        lblProductName.setLayoutData(gdProductName);
        lblProductName.setText(productName);
        ControlUtils.setFontStyle(lblProductName, SWT.BOLD, 17);

        Label lblTrial = new Label(titleComposite, SWT.BOTTOM);
        GridData gdTrial = new GridData(SWT.LEFT, SWT.CENTER, true, true);
        lblTrial.setLayoutData(gdTrial);
        ControlUtils.setFontStyle(lblTrial, SWT.ITALIC, 10);
        if (isTrial) {
            lblTrial.setText(StringConstants.ABOUT_LBL_TRIAL);
        }

        Composite infoComposite = new Composite(contentComposite, SWT.TRANSPARENT);
        GridLayout infoContent = new GridLayout(2, false);
        infoContent.marginTop = 0;
        infoContent.marginLeft = 0;
        infoComposite.setLayout(infoContent);
        
        GridData gdLabel = new GridData(SWT.LEFT, SWT.CENTER, true, true);
        gdLabel.widthHint = 200;

        Label lblversion = new Label(infoComposite, SWT.NONE);
        lblversion.setText(StringConstants.ABOUT_LBL_VERSION);
        lblversion.setLayoutData(gdLabel);
        ControlUtils.setFontStyle(lblversion, SWT.BOLD, 10);

        Label version = new Label(infoComposite, SWT.NONE);
        version.setText(VersionUtil.getCurrentVersion().getVersion());
        ControlUtils.setFontStyle(version, SWT.NONE, 10);

        Label lblBuild = new Label(infoComposite, SWT.NONE);
        lblBuild.setLayoutData(gdLabel);
        lblBuild.setText(StringConstants.ABOUT_LBL_BUILD);
        ControlUtils.setFontStyle(lblBuild, SWT.BOLD, 10);

        Label build = new Label(infoComposite, SWT.NONE);
        build.setText(Integer.toString(VersionUtil.getCurrentVersion().getBuildNumber()));
        ControlUtils.setFontStyle(version, SWT.NONE, 10);

        if (isKSE) {
            Label lblExpirationDate = new Label(infoComposite, SWT.NONE);
            lblExpirationDate.setLayoutData(gdLabel);
            lblExpirationDate.setText(StringConstants.ABOUT_LBL_EXPIRATION_DATE);
            ControlUtils.setFontStyle(lblExpirationDate, SWT.BOLD, 10);

            expiration = new Label(infoComposite, SWT.NONE);
            expiration.setForeground(ColorUtil.getTextRunningColor());
            GridData gdExpiration = new GridData(SWT.FILL, SWT.FILL, true, false);
            gdExpiration.widthHint = 200;
            expiration.setLayoutData(gdExpiration);
            expiration.setText(StringConstants.About_MSG_CHECKING_EXPIRATION_DATE);

            updateExpirationDate();
        }

        lblNotice = new Label(infoComposite, SWT.NONE);
        lblNotice.setLayoutData(gdLabel);
        ControlUtils.setFontStyle(lblNotice, SWT.BOLD, 10);

        notice = new Label(infoComposite, SWT.NONE);
        notice.setForeground(ColorUtil.getTextRunningColor());
        notice.setText(StringConstants.ABOUT_MSG_CHECKING_VERSION);
        updateVersionInfo();


        Label lblCopyright = new Label(infoComposite, SWT.NONE);
        lblCopyright.setLayoutData(gdLabel);
        lblCopyright.setText(StringConstants.ABOUT_LBL_COPYRIGHT);
        ControlUtils.setFontStyle(lblCopyright, SWT.BOLD, 10);

        lnkCopyright = new Link(infoComposite, SWT.NONE);
        lnkCopyright.setText(String.format("<a>%s</a>", "https://www.katalon.com"));
        ControlUtils.setFontStyle(lnkCopyright, SWT.NONE, 10);

        Composite buttonBarComposite = new Composite(parent, SWT.TRANSPARENT);
        buttonBarComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, true, true, 2, 1));
        GridLayout glbtn = new GridLayout(2, false);
        glbtn.marginTop = 40;
        glbtn.marginBottom = 0;
        glbtn.marginRight = 40;
        buttonBarComposite.setLayout(glbtn);
        buttonBarComposite.setBackgroundMode(SWT.INHERIT_FORCE);

        GridData gdBtn = new GridData(SWT.RIGHT, SWT.RIGHT, true, true);
        gdBtn.heightHint = 40;
        gdBtn.widthHint = 200;
        btnInstalltionDetails = new Button(buttonBarComposite, SWT.NONE);
        btnInstalltionDetails.setLayoutData(gdBtn);
        btnInstalltionDetails.setText(StringConstants.ABOUT_BTN_INSTALLATION_DETAILS);
        ControlUtils.setFontStyle(btnInstalltionDetails, SWT.BOLD, 10);

        btnOk = new Button(buttonBarComposite, SWT.NONE);
        GridData gdBtno = new GridData(SWT.RIGHT, SWT.RIGHT, true, true);
        gdBtno.heightHint = 40;
        gdBtno.widthHint = 100;
        btnOk.setLayoutData(gdBtno);
        btnOk.setText(StringConstants.ABOUT_BTN_OK);
        ControlUtils.setFontStyle(btnOk, SWT.BOLD, 10);

        addListener();

        return parent;
    }

    private void addListener() {
        lnkCopyright.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch("https://www.katalon.com");
            }
        });

        btnInstalltionDetails.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
                    @Override
                    public void run() {
                        IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                        InstallationDialog dialog = new InstallationDialog(getShell(), workbenchWindow);
                        dialog.setModalParent(KatalonAboutDialog.this);
                        dialog.open();
                    }
                });
            }
        });

        btnOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                BusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {
                    @Override
                    public void run() {
                        close();
                    }
                });
            }
        });
    }

    private Image getBackground() {
        if (ComponentBundleActivator.isDarkTheme(getShell().getDisplay())) {
            return ImageManager.getImage(IImageKeys.IMG_ABOUT_BG_DRANK);
        }
        return ImageManager.getImage(IImageKeys.IMG_ABOUT_BG_LIGHT);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#isResizable()
     */
    @Override
    protected boolean isResizable() {
        return false;
    }
}
