package com.kms.katalon.composer.mobile.dialog;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.control.GifCLabel;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.mobile.constants.ComposerMobileMessageConstants;
import com.kms.katalon.composer.mobile.constants.ImageConstants;
import com.kms.katalon.composer.mobile.dialog.provider.IosIdentityColumnLabelProvider;
import com.kms.katalon.core.util.ConsoleCommandExecutor;
import com.kms.katalon.execution.mobile.constants.StringConstants;
import com.kms.katalon.execution.mobile.device.IosDeviceInfo;
import com.kms.katalon.execution.mobile.exception.MobileSetupException;
import com.kms.katalon.execution.mobile.identity.IosIdentityInfo;

public class IosIdentitySelectionDialog extends AbstractDialog {

    private final String[] LISTING_DEVELOPERS_COMMAND = new String[] { "/bin/sh", "-c",
            "security find-identity -v -p codesigning" };

    private final String DEVELOPMENT_TEAM_SIGNAL = "Apple Distribution: ";

    private List<IosIdentityInfo> identities;

    private IosIdentityInfo selectedIdentity;

    private TableViewer identityTableViewer;

    private Thread thread;

    private boolean interrupted = false;

    private Composite identitySelectionMainComposite;

    private StackLayout stackLayout;

    private Composite noIdentityComposite;

    private Composite tableComposite;

    private Composite loadingIdentitiesComposite;

    private Composite notificationComposite;

    private GifCLabel connectingLabel;

    private InputStream loadingImgInputStream;

    public IosIdentitySelectionDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void registerControlModifyListeners() {
        identityTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                selectedIdentity = (IosIdentityInfo) identityTableViewer.getStructuredSelection().getFirstElement();

                getButton(OK).setEnabled(selectedIdentity != null);
            }
        });

        identityTableViewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                okPressed();
            }
        });

        connectingLabel.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
                // TODO Auto-generated method stub
                try {
                    loadingImgInputStream.close();
                } catch (IOException ignored) {}
            }
        });
    }

    private boolean identitiesChanged(List<IosIdentityInfo> newIdentities, List<IosIdentityInfo> oldIdentities) {
        if (newIdentities == null || oldIdentities == null) {
            return true;
        }
        if (newIdentities.size() != oldIdentities.size()) {
            return true;
        }
        return newIdentities.parallelStream().filter(d -> !oldIdentities.contains(d)).findAny().isPresent();
    }

    @Override
    protected void setInput() {
        getButton(OK).setEnabled(false);

        stackLayout.topControl = loadingIdentitiesComposite;
        identitySelectionMainComposite.layout();

        thread = new Thread(() -> {
            try {
                while (!interrupted) {
                    List<IosIdentityInfo> newIdentities = getIdentities();
                    boolean identitiesChanged = identitiesChanged(newIdentities, identities);
                    if (interrupted) {
                        return;
                    }
                    if (identitiesChanged) {
                        identities = newIdentities;

                        UISynchronizeService.syncExec(() -> {
                            if (identitySelectionMainComposite.isDisposed()) {
                                return;
                            }
                            notificationComposite.getParent().setRedraw(false);
                            identityTableViewer.setInput(identities);
                            if (identities.isEmpty()) {
                                stackLayout.topControl = noIdentityComposite;
                            } else {
                                stackLayout.topControl = tableComposite;
                                if (selectedIdentity == null) {
                                    identityTableViewer.setSelection(new StructuredSelection(identities.get(0)));
                                }
                            }
                            identitySelectionMainComposite.layout();
                            cleanNotifications();

                            if (identities.isEmpty()) {
                                addNoIdentityNotification();
                            }

                            notificationComposite.getParent().setRedraw(true);
                            notificationComposite.getParent().layout(true, true);
                        });
                    }
                    Thread.sleep(2000L);
                }
            } catch (MobileSetupException | InterruptedException ignored) {

            } catch (IOException e) {
                UISynchronizeService.syncExec(() -> {
                    MessageDialog.openError(getShell(), StringConstants.ERROR, e.getMessage());
                });
            }
        });
        thread.start();
    }

    private List<IosIdentityInfo> getIdentities() throws MobileSetupException, IOException, InterruptedException {
        List<IosIdentityInfo> appleIdentities = new ArrayList<IosIdentityInfo>();
        Map<String, String> iosAdditionalEnvironmentVariables = IosDeviceInfo.getIosAdditionalEnvironmentVariables();

        List<String> identityLines = ConsoleCommandExecutor.runConsoleCommandAndCollectResults(
                LISTING_DEVELOPERS_COMMAND, iosAdditionalEnvironmentVariables, true);

        for (String developer : identityLines) {
            if (StringUtils.isEmpty(developer) || !developer.contains(DEVELOPMENT_TEAM_SIGNAL)) {
                continue;
            }
            appleIdentities.add(new IosIdentityInfo(developer));
        }
        return appleIdentities;
    }

    private void cleanNotifications() {
        while (notificationComposite.getChildren() != null && notificationComposite.getChildren().length > 0) {
            notificationComposite.getChildren()[0].dispose();
        }
    }

    private void addNoIdentityNotification() {
        Composite noIdentityNotificationComposite = new Composite(notificationComposite, SWT.NONE);
        noIdentityNotificationComposite.setLayout(new GridLayout(2, false));
        noIdentityNotificationComposite.setBackground(ColorUtil.getWarningLogBackgroundColor());
        noIdentityNotificationComposite.setBackgroundMode(SWT.INHERIT_FORCE);

        Label lblNoDeviceNotification = new Label(noIdentityNotificationComposite, SWT.NONE);
        lblNoDeviceNotification.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        lblNoDeviceNotification.setText(ComposerMobileMessageConstants.DIA_LBL_IDENTITY_TROUBLESHOOT);

        Link lnkNoDeviceTroubleshoot = new Link(noIdentityNotificationComposite, SWT.NONE);
        lnkNoDeviceTroubleshoot.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        lnkNoDeviceTroubleshoot
                .setText(String.format("<a>%s</a>", ComposerMobileMessageConstants.DIA_LNK_IDENTITY_TROUBLESHOOT));
        lnkNoDeviceTroubleshoot.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(getTroubleshootLink());
            }
        });
    }

    private String getTroubleshootLink() {
        return "";
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());

        notificationComposite = new Composite(container, SWT.NONE);
        notificationComposite.setLayout(new FillLayout(SWT.VERTICAL));
        notificationComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        identitySelectionMainComposite = new Composite(container, SWT.NONE);
        identitySelectionMainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        stackLayout = new StackLayout();
        identitySelectionMainComposite.setLayout(stackLayout);

        loadingIdentitiesComposite = new Composite(identitySelectionMainComposite, SWT.NONE);
        loadingIdentitiesComposite.setLayout(new GridLayout(2, false));
        connectingLabel = new GifCLabel(loadingIdentitiesComposite, SWT.DOUBLE_BUFFERED);
        connectingLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        try {
            loadingImgInputStream = ImageConstants.URL_16_LOADING.openStream();
            connectingLabel.setGifImage(loadingImgInputStream);
        } catch (IOException ignored) {}

        Label lblLoadingIdentities = new Label(loadingIdentitiesComposite, SWT.NONE);
        lblLoadingIdentities.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        lblLoadingIdentities.setText(ComposerMobileMessageConstants.DIA_LBL_LOADING_IDENTITIES);

        noIdentityComposite = new Composite(identitySelectionMainComposite, SWT.NONE);
        noIdentityComposite.setLayout(new GridLayout());
        Label lblNoIdentityFound = new Label(noIdentityComposite, SWT.NONE);
        lblNoIdentityFound.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        lblNoIdentityFound.setText(ComposerMobileMessageConstants.DIA_MSG_NO_IDENTITY_FOUND);

        tableComposite = new Composite(identitySelectionMainComposite, SWT.NONE);
        TableColumnLayout tableLayout = new TableColumnLayout();
        tableComposite.setLayout(tableLayout);

        identityTableViewer = new CTableViewer(tableComposite, SWT.BORDER);
        identityTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        TableViewerColumn identityColumnViewer = new TableViewerColumn(identityTableViewer, SWT.NONE);
        tableLayout.setColumnData(identityColumnViewer.getColumn(), new ColumnWeightData(98, 300));
        identityColumnViewer.setLabelProvider(new IosIdentityColumnLabelProvider(0));

        stackLayout.topControl = tableComposite;
        identitySelectionMainComposite.layout();
        return identitySelectionMainComposite;
    }

    @Override
    public String getDialogTitle() {
        return ComposerMobileMessageConstants.DIA_TITLE_IOS_IDENTITIES;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(450, 350);
    }

    public IosIdentityInfo getIdentity() {
        return selectedIdentity;
    }

    @Override
    public boolean close() {
        interrupted = true;
        return super.close();
    }
}
