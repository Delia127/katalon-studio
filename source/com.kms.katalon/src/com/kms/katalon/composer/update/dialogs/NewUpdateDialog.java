package com.kms.katalon.composer.update.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.update.jobs.CheckForUpdatesJob.CheckForUpdateResult;

public class NewUpdateDialog extends AbstractDialog {

    public static final int DOWNLOAD_ID = 1025;

    public static final int REMIND_LATER_ID = 1026;

    public static final int IGNORE_UPDATE_ID = 1027;

    private Label lblCurrentVersionDetails;

    private Label lblNewVersionDetails;

    private Link lnkRealeaseNotes;

    private Composite container;

    private CheckForUpdateResult result;

    public NewUpdateDialog(Shell parentShell, CheckForUpdateResult result) {
        super(parentShell);
        this.result = result;
    }

    @Override
    protected void registerControlModifyListeners() {
        lnkRealeaseNotes.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(result.getLatestVersionInfo().getReleaseNotesLink());
            }
        });
    }

    @Override
    protected void setInput() {
        lblCurrentVersionDetails.setText(result.getCurrentAppInfo().getVersion());
        lblNewVersionDetails.setText(result.getLatestAppInfo().getVersion());

        container.layout(true);
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout glContainer = new GridLayout(2, false);
        glContainer.marginWidth = 10;
        glContainer.horizontalSpacing = 30;
        container.setLayout(glContainer);

        Label lblCurrentVersion = new Label(container, SWT.NONE);
        lblCurrentVersion.setText("Current version");

        lblCurrentVersionDetails = new Label(container, SWT.NONE);

        Label lblNewVersion = new Label(container, SWT.NONE);
        lblNewVersion.setText("New version");

        Composite newVersionComposite = new Composite(container, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        newVersionComposite.setLayout(gridLayout);

        lblNewVersionDetails = new Label(newVersionComposite, SWT.NONE);

        lnkRealeaseNotes = new Link(newVersionComposite, SWT.NONE);
        lnkRealeaseNotes.setText("<a>Release Notes</a>");

        return container;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, DOWNLOAD_ID, "Download", true);
        createButton(parent, REMIND_LATER_ID, "Remind Me Later", false);
        createButton(parent, IGNORE_UPDATE_ID, "Ignore This Update", false);
    }

    @Override
    public String getDialogTitle() {
        return "Katalon Studio New Update";
    }

    @Override
    protected void buttonPressed(int buttonId) {
        super.buttonPressed(buttonId);

        setReturnCode(buttonId);

        super.close();
    }
}
