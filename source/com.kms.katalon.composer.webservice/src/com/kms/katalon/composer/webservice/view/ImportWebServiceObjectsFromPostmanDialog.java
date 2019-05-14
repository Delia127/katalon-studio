package com.kms.katalon.composer.webservice.view;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.impl.dialogs.CustomTitleAreaDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.parser.PostmanParseUtils;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.tracking.service.Trackings;

public class ImportWebServiceObjectsFromPostmanDialog extends CustomTitleAreaDialog {

    private FolderEntity parentFolder;

    private List<WebServiceRequestEntity> webServiceRequestEntities;

    private String directory = "";

    public ImportWebServiceObjectsFromPostmanDialog(Shell parentShell, FolderEntity parentFolder) {
        super(parentShell);
        Trackings.trackOpenImportingPostman();
        this.parentFolder = parentFolder;
    }

    public void createWebServiceRequestEntities() throws Exception {
        webServiceRequestEntities = PostmanParseUtils.newWSTestObjectsFromPostman(parentFolder, directory);
    }

    public List<WebServiceRequestEntity> getWebServiceRequestEntities() {
        return webServiceRequestEntities;
    }

    @Override
    protected void okPressed() {
        Button ok = getButton(IDialogConstants.OK_ID);
        boolean closeTheDialog = true;
        try {
            ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
            dialog.run(true, false, new IRunnableWithProgress() {
                
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        monitor.beginTask("Importing Web Service Requests from Postman...", SubMonitor.UNKNOWN);
                        createWebServiceRequestEntities();
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
                        throw new InvocationTargetException(e);
                    } finally {
                        monitor.done();
                    }
                }
            });
        } catch (Exception e) {
            closeTheDialog = false;
            setMessage(e.getMessage(), IMessageProvider.ERROR);
            ok.setEnabled(false);
        } finally {
            if (closeTheDialog == true) {
                super.okPressed();
            }
        }
    }

    @Override
    protected boolean isResizable() {
        return false;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(StringConstants.VIEW_DIA_TITLE_WEBSERVICE_REQ_POSTMAN);
    }

    @Override
    protected Composite createContentArea(Composite parent) {

        setDialogTitle(StringConstants.VIEW_DIA_TITLE_WEBSERVICE_REQ_POSTMAN);
        setMessage(StringConstants.DIA_MSG_IMPORT_WEBSERVICE_REQ_POSTMAN, IMessageProvider.INFORMATION);

        // create a composite with standard margins and spacing
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label label = new Label(composite, SWT.NONE);
        label.setText("File location or URL: ");

        Text text = new Text(composite, SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite methodComposite = new Composite(composite, SWT.NONE);
        GridLayout glMethodComposite = new GridLayout();
        methodComposite.setLayout(glMethodComposite);

        Button button = new Button(methodComposite, SWT.PUSH);
        button.setText(StringConstants.BROWSE);
        button.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                FileDialog directoryDialog = new FileDialog(getParentShell());
                String filePath = directoryDialog.open();
                text.setText(filePath);
                directory = filePath;
            }
        });

        ModifyListener listener = new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                Button ok = getButton(IDialogConstants.OK_ID);
                if (ok.isEnabled() == false) {
                    ok.setEnabled(true);
                }

                directory = ((Text) e.widget).getText();
            }
        };

        text.addModifyListener(listener);

        messageLabel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch("https://schema.getpostman.com/collection/json/v2.1.0/draft-07/docs/index.html");
            }
        });
        return composite;
    }

    @Override
    protected void registerControlModifyListeners() {
    }

    @Override
    protected void setInput() {
    }

    @Override
    protected Point getInitialSize() {
        final Point size = super.getInitialSize();
        size.x = convertWidthInCharsToPixels(75);
        return size;
    }

    public String getPostmanSpecLocation() {
        return directory;
    }
}
