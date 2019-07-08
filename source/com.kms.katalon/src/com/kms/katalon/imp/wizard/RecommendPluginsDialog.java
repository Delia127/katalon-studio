package com.kms.katalon.imp.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.handlers.RequireAuthorizationHandler;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.plugin.models.KStoreClientAuthException;
import com.kms.katalon.plugin.models.KStoreClientException;
import com.kms.katalon.plugin.models.KStoreProduct;
import com.kms.katalon.plugin.models.KStoreUsernamePasswordCredentials;
import com.kms.katalon.plugin.service.KStoreRestClient;

public class RecommendPluginsDialog extends Dialog {
    List<Long> idProduct = new ArrayList<>();

    public static final int NEW_PROJECT_ID = 1025;
    
    private static final long DIALOG_CLOSED_DELAY_MILLIS = 500L;

    public static final int OPEN_PROJECT_ID = 1026;
    protected Button newButton;
    // Controls
    protected Composite stepDetailsComposite;

    // Fields
    protected ScrolledComposite scrolledComposite ;
    protected HashMap<String, Object> sharedData;

    public RecommendPluginsDialog(Shell parentShell) {
        super(parentShell);
        

    }
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Most recommended plugins");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData(SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        layout.verticalSpacing = 10;
        composite.setLayout(layout);
        composite.setLayoutData(gridData);
        Composite compositeHeader = new Composite(composite, SWT.NONE);
        GridData gridDataHeader = new GridData(SWT.CENTER, SWT.TOP, true, false);
        GridLayout layoutHeader = new GridLayout();
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        layout.verticalSpacing = 10;
        compositeHeader.setLayout(layoutHeader);
        compositeHeader.setLayoutData(gridDataHeader);
        gridDataHeader.heightHint = 40;
        gridDataHeader.widthHint = 540;
        applyDialogFont(compositeHeader);
        Label lb = new Label(compositeHeader, SWT.CENTER);
        lb.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
        lb.setText("Most recommended plugins\n");
        //layoutHeader.marginLeft = (gridDataHeader.widthHint - lb.getText().length() *16);
        org.eclipse.swt.graphics.Font defaultFont = new org.eclipse.swt.graphics.Font(null, "Aria", 12, SWT.BOLD);
        lb.setFont(defaultFont);
        // initialize the dialog units
        initializeDialogUnits(compositeHeader);
        // create the dialog area and button bar
        Composite compositeBody = new Composite(composite, SWT.BORDER);
        GridData gridDataBD = new GridData(SWT.NONE);
        GridLayout layoutBD = new GridLayout();
        layoutBD.marginHeight = 10;
        layoutBD.marginWidth = 10;
        layoutBD.verticalSpacing = 10;
        compositeBody.setLayout(layoutBD);
        compositeBody.setLayoutData(gridDataBD);
        gridDataBD.widthHint = 540;
        gridDataBD.heightHint = 380;
        createDialogContainer(compositeBody);
       // scrolledComposite.setMinHeight(800);

        return composite;
    }
    @Override
    protected Control createButtonBar(Composite parent) {
        Composite buttonBarComposite = new Composite(parent, SWT.RIGHT);
        GridLayout layout = new GridLayout();
        GridData gridDataBD = new GridData(SWT.RIGHT , SWT.BOTTOM, true, false);
        gridDataBD.widthHint = 540;
        gridDataBD.heightHint = 40;
        layout.marginRight = 10;
        buttonBarComposite.setLayout(layout);
        buttonBarComposite.setLayoutData(new GridData(SWT.RIGHT , SWT.BOTTOM, true, false));
        createButton(buttonBarComposite, OPEN_PROJECT_ID, StringConstants.DIA_OPEN_PROJECT, false);
        createButton(buttonBarComposite, NEW_PROJECT_ID, StringConstants.DIA_NEW_PROJECT, true);
        return buttonBarComposite;
    }
    public final void installPressed() {
        
        Job reloadPluginsJob = new Job("Reloading plugins...") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                    KStoreUsernamePasswordCredentials[] credentials = new KStoreUsernamePasswordCredentials[1];
                    UISynchronizeService.syncExec(() -> {
                        try {
                            credentials[0] = RequireAuthorizationHandler.getUsernamePasswordCredentials();
                            KStoreRestClient res = new KStoreRestClient(credentials[0]);
                            res.postRecommended(idProduct);
                        } catch (KStoreClientAuthException e) {
                            LoggerSingleton.logError(e);
                        } catch (KStoreClientException e) {
                            LoggerSingleton.logError(e);
                        }
                    });
                LoggerSingleton.logInfo("Reloaded plugins successfully.");
                return Status.OK_STATUS;
            }
        };
        reloadPluginsJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.WORKSPACE_PLUGIN_LOADED, null);

                if (!reloadPluginsJob.getResult().isOK()) {
                    LoggerSingleton.logError("Failed to reload plugins.");
                    return;
                }

                Executors.newSingleThreadExecutor().submit(() -> {
                    try {
                        // wait for Reloading Plugins dialog to close
                        TimeUnit.MILLISECONDS.sleep(DIALOG_CLOSED_DELAY_MILLIS);
                    } catch (InterruptedException ignored) {}
                });
            }
        });

        reloadPluginsJob.setUser(true);
        reloadPluginsJob.schedule();
    }
    
    @Override
    protected void buttonPressed(int buttonId) {
        setReturnCode(buttonId);
        close();
    }
    @Override
    public void create() {
        super.create();
    }
    protected void createSeparator(Composite parent) {
        Label label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    }

    public Control createStepArea(Composite parent) {
        try {
            KStoreUsernamePasswordCredentials credentials = new KStoreUsernamePasswordCredentials();
            credentials = RequireAuthorizationHandler.getUsernamePasswordCredentials();
            KStoreRestClient res = new KStoreRestClient(credentials);
            List<KStoreProduct> recommendList = res.getRecommendPlugins();
            List<Button> buttons = new ArrayList<Button>();
            for (int i = 0; i < recommendList.size(); i++) {
                newButton = new Button(parent, SWT.CHECK | SWT.WRAP);
                newButton.setText(recommendList.get(i).getName());
                newButton.setSelection(true);
                Label lb = new Label(parent, SWT.WRAP);
                GridData gdLb = new GridData(SWT.FILL, SWT.TOP, false, false);
                gdLb.widthHint = 530;
                lb.setLayoutData(gdLb);
                lb.setText("    " + recommendList.get(i).getDescription());
                lb.setFont(JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT));
                idProduct.add(recommendList.get(i).getId());
                // save the button
                buttons.add(newButton);
                buttons.get(i).addSelectionListener(new SelectionListener() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        for (int i = 0; i < idProduct.size(); i++) {
                        if (buttons.get(i).getSelection() != true) {
                                idProduct.remove(idProduct.get(i));
                        } else if(buttons.get(i).getSelection() == true && !idProduct.contains(recommendList.get(i).getId())) {
                                idProduct.add(idProduct.get(i));
                            }
                        }
                    }
                    @Override
                    public void widgetDefaultSelected(SelectionEvent e) {

                    }
                });
            }
        } catch (KStoreClientException e) {
            LoggerSingleton.logError(e);
        } catch (KStoreClientAuthException e) {
            LoggerSingleton.logError(e);
        } 

        return stepDetailsComposite;

    }

    @Override
    protected Point getInitialSize() {
        return new Point(580, 550);
    }


    protected Control createDialogContainer(Composite parent) {
        scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        Composite composite = new Composite(scrolledComposite, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        createStepArea(composite);
        scrolledComposite.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        scrolledComposite.setContent(composite);
        return composite;
    }
}
