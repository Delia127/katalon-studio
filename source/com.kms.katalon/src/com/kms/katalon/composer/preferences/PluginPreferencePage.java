package com.kms.katalon.composer.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class PluginPreferencePage extends PreferencePage {

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        
        Label lblPluginRepoLocation = new Label(composite, SWT.NONE);
        lblPluginRepoLocation.setText("Repository location ");
        
        Text txtPluginRepoLocation = new Text(composite, SWT.BORDER);
        txtPluginRepoLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        Button btnSelectFolder = new Button(composite, SWT.PUSH);
        btnSelectFolder.setText("Browse");
        btnSelectFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog directoryDialog = new DirectoryDialog(Display.getCurrent().getActiveShell());
                String filePath = directoryDialog.open();
                txtPluginRepoLocation.setText(filePath);
            }
        });
        return composite;
    }

}
