package com.kms.katalon.imp.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
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

import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.handlers.ReloadPluginsHandler;
import com.kms.katalon.composer.handlers.RequireAuthorizationHandler;
import com.kms.katalon.constants.GlobalMessageConstants;
import com.kms.katalon.plugin.models.KStoreClientAuthException;
import com.kms.katalon.plugin.models.KStoreClientException;
import com.kms.katalon.plugin.models.KStoreProduct;
import com.kms.katalon.plugin.models.KStoreUsernamePasswordCredentials;
import com.kms.katalon.plugin.models.ReloadPluginsException;
import com.kms.katalon.plugin.service.KStoreRestClient;
import com.kms.katalon.plugin.service.PluginService;

public class RecommendPluginsDialog extends AbstractDialog {
    List<Long> idProduct = new ArrayList<>();
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
    protected void registerControlModifyListeners() {

    }

    @Override
    protected void setInput() {

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
        composite.setSize(parent.computeSize(680, 600));
        Composite compositeHeader = new Composite(composite, SWT.NONE);
        GridData gridDataHeader = new GridData(SWT.CENTER);
        GridLayout layoutHeader = new GridLayout();
        layout.marginHeight = 10;
        layout.marginWidth = 20;
        layout.verticalSpacing = 20;
        compositeHeader.setLayout(layoutHeader);
        compositeHeader.setLayoutData(gridDataHeader);
        compositeHeader.setSize(700, 100);
        applyDialogFont(compositeHeader);
        Label lb = new Label(compositeHeader, SWT.NONE);
        lb.setText("\n\t\t Most recommended plugins\n");
        org.eclipse.swt.graphics.Font defaultFont = new org.eclipse.swt.graphics.Font(null, "Aria", 10, SWT.BOLD);
        lb.setFont(defaultFont);
        // initialize the dialog units
        initializeDialogUnits(compositeHeader);
        // create the dialog area and button bar
        Composite compositeBody = new Composite(composite, SWT.BORDER);
        GridData gridDataBD = new GridData(SWT.NONE);
        GridLayout layoutBD = new GridLayout();
        layout.marginHeight = 10;
        layout.marginWidth = 10;
        layout.verticalSpacing = 10;
        compositeBody.setLayout(layoutBD);
        compositeBody.setLayoutData(gridDataBD);
        gridDataBD.widthHint = 640;
        gridDataBD.heightHint = 400;
        createDialogContainer(compositeBody);
       // scrolledComposite.setMinHeight(800);

        return composite;
    }
    @Override
    protected Control createButtonBar(Composite parent) {
        Composite buttonBarComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginLeft = 585;
        buttonBarComposite.setLayout(layout);
        Button btnInstall = new Button(buttonBarComposite, SWT.NONE);
        btnInstall.setText(GlobalMessageConstants.WZ_SETUP_BTN_INSTALL);
        btnInstall.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                installPressed();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
        return buttonBarComposite;
    }

    protected final void installPressed() {
        KStoreUsernamePasswordCredentials credentials = new KStoreUsernamePasswordCredentials();

        try {
            credentials = RequireAuthorizationHandler.getUsernamePasswordCredentials();
            KStoreRestClient res = new KStoreRestClient(credentials);
            res.postRecommended(idProduct);
            PluginService.getInstance().reloadPlugins(credentials, new NullProgressMonitor());
            ReloadPluginsHandler r = new ReloadPluginsHandler();
            r.reloadPlugins(false);
        } catch (KStoreClientAuthException e) {
            LoggerSingleton.logError(e);
        } catch (KStoreClientException e) {
            LoggerSingleton.logError(e);
        } catch (ReloadPluginsException | InterruptedException e) {
            LoggerSingleton.logError(e);
        }
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
                gdLb.widthHint = 580;
                lb.setLayoutData(gdLb);
                lb.setText("    " + recommendList.get(i).getDescription());
                lb.setFont(JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT));
                idProduct.add(recommendList.get(i).getId());
                // save the button
                buttons.add(newButton);
                buttons.get(i).addSelectionListener(new SelectionListener() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        for (int i = 0; i < buttons.size(); i++) {
                        if (buttons.get(i).getSelection() != true) {
                                idProduct.remove(idProduct.get(i));
                        } else {
                                idProduct.add(idProduct.get(i));
                            }
                        }

                    }

                    @Override
                    public void widgetDefaultSelected(SelectionEvent e) {

                    }
                });
            }
        } catch (KStoreClientException e1) {
            e1.printStackTrace();
        } catch (KStoreClientAuthException e1) {
            e1.printStackTrace();
        }

        return stepDetailsComposite;

    }

    @Override
    protected Point getInitialSize() {
        return new Point(680, 600);
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
