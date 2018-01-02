package com.kms.katalon.composer.mobile.dialog;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

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
import com.kms.katalon.composer.mobile.dialog.provider.MobileDeviceColumnLabelProvider;
import com.kms.katalon.execution.mobile.constants.StringConstants;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;
import com.kms.katalon.execution.mobile.exception.MobileSetupException;

public abstract class MobileDeviceSelectionDialog extends AbstractDialog {

    private final class RealDevicePriority implements Comparator<MobileDeviceInfo> {
        @Override
        public int compare(MobileDeviceInfo deviceA, MobileDeviceInfo deviceB) {
            if (!deviceA.isEmulator()) {
                return -1;
            }
            if (!deviceB.isEmulator()) {
                return 1;
            }
            return 0;
        }
    }

    private List<? extends MobileDeviceInfo> devicesList;

    private MobileDeviceInfo selectedDevice;

    private TableViewer deviceTableViewer;

    private Thread thread;

    private boolean interrupted = false;

    private Composite deviceMainComposite;

    private StackLayout stackLayout;

    private Composite noDeviceComposite;

    private Composite tableComposite;

    private Composite loadingDeviceComposite;

    private Composite notificationComposite;

    private GifCLabel connectingLabel;

    private InputStream loadingImgInputStream;

    public MobileDeviceSelectionDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void registerControlModifyListeners() {
        deviceTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                selectedDevice = (MobileDeviceInfo) deviceTableViewer.getStructuredSelection().getFirstElement();

                getButton(OK).setEnabled(selectedDevice != null);
            }
        });

        deviceTableViewer.addDoubleClickListener(new IDoubleClickListener() {

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

    private boolean devicesChanged(List<? extends MobileDeviceInfo> newDevices,
            List<? extends MobileDeviceInfo> oldDevices) {
        if (newDevices == null || oldDevices == null) {
            return true;
        }
        if (newDevices.size() != oldDevices.size()) {
            return true;
        }
        return newDevices.parallelStream().filter(d -> !oldDevices.contains(d)).findAny().isPresent();
    }

    @Override
    protected void setInput() {
        getButton(OK).setEnabled(false);

        stackLayout.topControl = loadingDeviceComposite;
        deviceMainComposite.layout();

        thread = new Thread(() -> {
            try {
                while (!interrupted) {
                    List<? extends MobileDeviceInfo> newDeviceList = getMobileDevices();
                    boolean devicesChanged = devicesChanged(newDeviceList, devicesList);
                    if (interrupted) {
                        return;
                    }
                    if (devicesChanged) {
                        devicesList = newDeviceList;
                        devicesList.sort(new RealDevicePriority());

                        UISynchronizeService.syncExec(() -> {
                            if (deviceMainComposite.isDisposed()) {
                                return;
                            }
                            notificationComposite.getParent().setRedraw(false);
                            deviceTableViewer.setInput(devicesList);
                            if (devicesList.isEmpty()) {
                                stackLayout.topControl = noDeviceComposite;
                            } else {
                                stackLayout.topControl = tableComposite;
                                if (selectedDevice == null) {
                                    deviceTableViewer.setSelection(new StructuredSelection(devicesList.get(0)));
                                }
                            }
                            deviceMainComposite.layout();
                            cleanNotifications();

                            if (devicesList.isEmpty()) {
                                addNoDeviceNotification();
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

    protected abstract List<? extends MobileDeviceInfo> getMobileDevices()
            throws MobileSetupException, IOException, InterruptedException;

    private void cleanNotifications() {
        while (notificationComposite.getChildren() != null && notificationComposite.getChildren().length > 0) {
            notificationComposite.getChildren()[0].dispose();
        }
    }

    private void addNoDeviceNotification() {
        Composite noDeviceNotificationComposite = new Composite(notificationComposite, SWT.NONE);
        noDeviceNotificationComposite.setLayout(new GridLayout(2, false));
        noDeviceNotificationComposite.setBackground(ColorUtil.getWarningLogBackgroundColor());
        noDeviceNotificationComposite.setBackgroundMode(SWT.INHERIT_FORCE);

        Label lblNoDeviceNotification = new Label(noDeviceNotificationComposite, SWT.NONE);
        lblNoDeviceNotification.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
        lblNoDeviceNotification.setText(ComposerMobileMessageConstants.DIA_LBL_TROUBLESHOOT);

        Link lnkNoDeviceTroubleshoot = new Link(noDeviceNotificationComposite, SWT.NONE);
        lnkNoDeviceTroubleshoot.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        lnkNoDeviceTroubleshoot
                .setText(String.format("<a>%s</a>", ComposerMobileMessageConstants.DIA_LNK_TROUBLESHOOT));
        lnkNoDeviceTroubleshoot.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(getTroubleshootLink());
            }
        });
    }

    protected abstract String getTroubleshootLink();

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());

        notificationComposite = new Composite(container, SWT.NONE);
        notificationComposite.setLayout(new FillLayout(SWT.VERTICAL));
        notificationComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        deviceMainComposite = new Composite(container, SWT.NONE);
        deviceMainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        stackLayout = new StackLayout();
        deviceMainComposite.setLayout(stackLayout);

        loadingDeviceComposite = new Composite(deviceMainComposite, SWT.NONE);
        loadingDeviceComposite.setLayout(new GridLayout(2, false));
        connectingLabel = new GifCLabel(loadingDeviceComposite, SWT.DOUBLE_BUFFERED);
        connectingLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        try {
            loadingImgInputStream = ImageConstants.URL_16_LOADING.openStream();
            connectingLabel.setGifImage(loadingImgInputStream);
        } catch (IOException ignored) {}

        Label lblLoadingDevice = new Label(loadingDeviceComposite, SWT.NONE);
        lblLoadingDevice.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        lblLoadingDevice.setText(ComposerMobileMessageConstants.DIA_LBL_LOADING_DEVICES);

        noDeviceComposite = new Composite(deviceMainComposite, SWT.NONE);
        noDeviceComposite.setLayout(new GridLayout());
        Label lblNoDeviceConnected = new Label(noDeviceComposite, SWT.NONE);
        lblNoDeviceConnected.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        lblNoDeviceConnected.setText(ComposerMobileMessageConstants.DIA_MSG_NO_DEVICES_CONNECTED);

        tableComposite = new Composite(deviceMainComposite, SWT.NONE);
        TableColumnLayout tableLayout = new TableColumnLayout();
        tableComposite.setLayout(tableLayout);

        deviceTableViewer = new CTableViewer(tableComposite, SWT.BORDER);
        deviceTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        TableViewerColumn deviceColumnViewer = new TableViewerColumn(deviceTableViewer, SWT.NONE);
        tableLayout.setColumnData(deviceColumnViewer.getColumn(), new ColumnWeightData(98, 300));
        deviceColumnViewer.setLabelProvider(new MobileDeviceColumnLabelProvider(0));

        stackLayout.topControl = tableComposite;
        deviceMainComposite.layout();
        return deviceMainComposite;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(450, 350);
    }

    public MobileDeviceInfo getDevice() {
        return selectedDevice;
    }

    @Override
    public boolean close() {
        interrupted = true;
        return super.close();
    }
}
