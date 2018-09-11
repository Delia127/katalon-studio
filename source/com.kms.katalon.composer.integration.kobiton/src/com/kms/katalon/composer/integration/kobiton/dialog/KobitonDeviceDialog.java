package com.kms.katalon.composer.integration.kobiton.dialog;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.integration.kobiton.constants.ComposerIntegrationKobitonMessageConstants;
import com.kms.katalon.composer.integration.kobiton.constants.ComposerKobitonStringConstants;
import com.kms.katalon.integration.kobiton.entity.KobitonDevice;
import com.kms.katalon.integration.kobiton.exceptions.KobitonApiException;
import com.kms.katalon.integration.kobiton.preferences.KobitonPreferencesProvider;
import com.kms.katalon.integration.kobiton.providers.KobitonApiProvider;

public class KobitonDeviceDialog extends TitleAreaDialog {
    private Combo cbbDevices;

    private List<KobitonDevice> devicesList = new ArrayList<>();

    private KobitonDevice selectedDevice;

    public KobitonDeviceDialog(Shell shell) {
        super(shell);
    }

    public KobitonDeviceDialog(Shell shell, KobitonDevice selectedDevice) {
        super(shell);
        this.selectedDevice = selectedDevice;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);

        Composite innerComposite = new Composite(area, SWT.NONE);
        innerComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        GridLayout glContainer = new GridLayout(2, false);
        glContainer.verticalSpacing = 10;
        glContainer.marginTop = 20;
        glContainer.marginLeft = 10;
        glContainer.marginRight = 10;
        innerComposite.setLayout(glContainer);

        Label lblDeviceName = new Label(innerComposite, SWT.NONE);
        lblDeviceName.setText(ComposerIntegrationKobitonMessageConstants.LBL_DLG_DEVICE_NAME);

        cbbDevices = new Combo(innerComposite, SWT.DROP_DOWN);
        cbbDevices.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Link lnkUpdateDeviceList = new Link(innerComposite, SWT.NONE);
        lnkUpdateDeviceList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        lnkUpdateDeviceList.setText(ComposerIntegrationKobitonMessageConstants.LNK_DLG_UPDATE_FAVORITE_DEVICES);
        lnkUpdateDeviceList.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    Program.launch(e.text);
                } catch (IllegalArgumentException ex) {
                    LoggerSingleton.logError(ex);
                }
            }
        });

        // Build the separator line
        Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
        separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addListeners();
        setTitle(ComposerIntegrationKobitonMessageConstants.TITLE_DLG_FAVORITE_DEVICES);
        setMessage(ComposerIntegrationKobitonMessageConstants.MSG_DLG_FAVORITE_DEVICES, IMessageProvider.INFORMATION);
        setInput();
        return area;
    }

    private void addListeners() {
        cbbDevices.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectedDevice = devicesList.get(cbbDevices.getSelectionIndex());
            }
        });
    }

    protected void setInput() {
        // Load devices name
        loadDeviceList();

        // Show full names of device list to show them on combo-box
        cbbDevices.setItems(getDeviceFullNames());

        if (devicesList.size() == 0) {
            selectedDevice = null;
            return;
        }
        int index = 0;
        if (selectedDevice != null) {
            Optional<KobitonDevice> deviceOpt = devicesList.stream()
                    .filter(device -> device.getId() == selectedDevice.getId())
                    .findFirst();
            if (deviceOpt.isPresent()) {
                index = devicesList.indexOf(deviceOpt.get());
            }
        }
        selectedDevice = devicesList.get(index);
        cbbDevices.select(index);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(ComposerIntegrationKobitonMessageConstants.TITLE_WINDOW_DLG_FAVORITE_DEVICES);
    }

    private void loadDeviceList() {
        devicesList.clear();
        try {
            new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, false,
                    new IRunnableWithProgress() {
                        @Override
                        public void run(IProgressMonitor monitor)
                                throws InvocationTargetException, InterruptedException {
                            try {
                                monitor.beginTask(ComposerIntegrationKobitonMessageConstants.JOB_LOADING_DEVICE_LIST,
                                        1);
                                devicesList.addAll(KobitonApiProvider
                                        .getKobitonFavoriteDevices(KobitonPreferencesProvider.getKobitonToken()));
                                Collections.sort(devicesList, new Comparator<KobitonDevice>() {
                                    @Override
                                    public int compare(KobitonDevice device_1, KobitonDevice device_2) {
                                        return device_1.getDisplayString()
                                                .compareToIgnoreCase(device_2.getDisplayString());
                                    }
                                });
                                monitor.worked(1);
                            } catch (URISyntaxException | IOException | KobitonApiException e) {
                                throw new InvocationTargetException(e);
                            } finally {
                                monitor.done();
                            }
                        }
                    });

        } catch (InvocationTargetException exception) {
            final Throwable cause = exception.getCause();
            if (cause instanceof KobitonApiException) {
                MessageDialog.openError(getShell(), ComposerKobitonStringConstants.ERROR, cause.getMessage());
                close();
            } else {
                LoggerSingleton.logError(cause);
            }
        } catch (InterruptedException e) {
            // Ignore this
        }
    }

    private String[] getDeviceFullNames() {
        String[] fullNames = new String[devicesList.size()];
        for (int i = 0; i < devicesList.size(); i++) {
            fullNames[i] = devicesList.get(i).getDisplayString();
        }
        return fullNames;
    }

    public KobitonDevice getSelectedDevice() {
        return selectedDevice;
    }
}
