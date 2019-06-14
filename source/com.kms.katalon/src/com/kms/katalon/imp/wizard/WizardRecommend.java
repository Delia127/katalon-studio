package com.kms.katalon.imp.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.wizard.AbstractWizardPage;
import com.kms.katalon.composer.components.impl.wizard.IWizardPage;
import com.kms.katalon.composer.components.impl.wizard.IWizardPageChangedListerner;
import com.kms.katalon.composer.components.impl.wizard.WizardManager;
import com.kms.katalon.composer.components.impl.wizard.WizardPageChangedEvent;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.handlers.RequireAuthorizationHandler;
import com.kms.katalon.plugin.models.KStoreClientAuthException;
import com.kms.katalon.plugin.models.KStoreClientException;
import com.kms.katalon.plugin.models.KStoreProduct;
import com.kms.katalon.plugin.models.KStoreUsernamePasswordCredentials;
import com.kms.katalon.plugin.models.ReloadPluginsException;
import com.kms.katalon.plugin.service.KStoreRestClient;
import com.kms.katalon.plugin.service.PluginService;

public abstract class WizardRecommend extends Dialog implements IWizardPageChangedListerner {
    private static final int BUTTON_WIDTH = 80;

    List<Long> idProduct = new ArrayList<>();

    public static final int INSTALL_BUTTON_ID = 0;

    // Controls
    protected Composite stepDetailsComposite;

    protected Map<Integer, Button> buttonMap;

    // Fields
    protected WizardManager wizardManager;

    protected HashMap<String, Object> sharedData;

    public WizardRecommend(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(getDialogTitle());
    }

    protected String getDialogTitle() {
        return StringUtils.EMPTY;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        return createWizardArea(parent);
    }

    // Remove default button bar
    @Override
    protected final Control createButtonBar(Composite parent) {
        return null;
    }

    protected final Control oldDialogArea(Composite parent) {
        return super.createDialogArea(parent);
    }

    protected Control createWizardArea(Composite parent) {
        Composite mainComposite = (Composite) super.createDialogArea(parent);
        mainComposite.setLayout(new FillLayout());

        createDialogContainer(mainComposite);
        return mainComposite;
    }

    protected Composite createDialogContainer(Composite parent) {
        Composite mainArea = new Composite(parent, SWT.NONE);
        GridLayout glMainArea = new GridLayout(1, false);
        glMainArea.marginHeight = 0;
        glMainArea.marginWidth = 0;
        mainArea.setLayout(glMainArea);

        Composite stepAreaComposite = createStepAreaComposite(mainArea);
        stepAreaComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

        Composite separatorAndButtonComposite = new Composite(mainArea, SWT.NONE);
        GridLayout glSeparatorAndButtonComposite = new GridLayout();
        glSeparatorAndButtonComposite.marginWidth = 0;
        glSeparatorAndButtonComposite.marginHeight = 0;
        separatorAndButtonComposite.setLayout(glSeparatorAndButtonComposite);
        separatorAndButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
        createSeparator(separatorAndButtonComposite);

        Composite buttonBarComposite = createButtonBarComposite(separatorAndButtonComposite);
        buttonBarComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 1));
        return mainArea;
    }

    protected Composite createStepAreaComposite(Composite mainComposite) {
        Composite stepAreaComposite = new Composite(mainComposite, SWT.NONE);
        stepAreaComposite.setLayout(new FillLayout());

        stepDetailsComposite = new Composite(stepAreaComposite, SWT.NONE);
        stepDetailsComposite.setLayout(new FillLayout());
        return stepAreaComposite;
    }

    protected Composite createButtonBarComposite(Composite parent) {
        Composite buttonBarComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        buttonBarComposite.setLayout(layout);

        createButton(buttonBarComposite, INSTALL_BUTTON_ID, StringConstants.WZ_SETUP_BTN_INSTALL);
        layout.numColumns = buttonMap.size();
        return buttonBarComposite;
    }

    protected final Button createButton(Composite buttonBarComposite, int id, String text) {
        Button button = new Button(buttonBarComposite, SWT.FLAT);
        button.setLayoutData(getButtonGridData());
        button.setText(text);
        buttonMap.put(id, button);
        button.setData(StringConstants.ID, id);
        return button;
    }

    @Override
    public void create() {
        buttonMap = new HashMap<>();
        super.create();
        setInput();
        registerControlModifyListeners();
    }

    protected void registerControlModifyListeners() {
        ButtonClickedListener buttonClickedListener = new ButtonClickedListener();
        for (Entry<Integer, Button> buttonEntry : buttonMap.entrySet()) {
            buttonEntry.getValue().addSelectionListener(buttonClickedListener);
        }
    }

    protected void wizardButtonPress(int id) {
        switch (id) {
            case INSTALL_BUTTON_ID: {
                installPressed();
                break;
            }
        }
    }

    protected void setInput() {
        sharedData = new HashMap<>();

        wizardManager = new WizardManager();
        for (IWizardPage wizardPage : getWizardPages()) {
            wizardManager.addPage(wizardPage);
        }
        showPage(wizardManager.getCurrentPage());
    }

    protected abstract Collection<IWizardPage> getWizardPages();

    protected void showPage(IWizardPage page) {
        if (page == null) {
            return;
        }

        if (page instanceof AbstractWizardPage) {
            ((AbstractWizardPage) page).addChangedListeners(this);
        }

        updateStepArea(page);

        updateButtonBar(page);

        page.setInput(sharedData);
        page.registerControlModifyListeners();
    }

    private void updateStepArea(IWizardPage page) {
        while (stepDetailsComposite.getChildren().length > 0) {
            stepDetailsComposite.getChildren()[0].dispose();
        }

        page.createStepArea(stepDetailsComposite);
        stepDetailsComposite.layout(true, true);
    }

    private void updateButtonBar(IWizardPage page) {

    }

    protected final void installPressed() {
        KStoreUsernamePasswordCredentials credentials = new KStoreUsernamePasswordCredentials();

        try {
            credentials = RequireAuthorizationHandler.getUsernamePasswordCredentials();
            KStoreRestClient res = new KStoreRestClient(credentials);
                res.postRecommended(idProduct);
            PluginService.getInstance().reloadPlugins(credentials, new NullProgressMonitor());
        } catch (ReloadPluginsException | InterruptedException e) {
            LoggerSingleton.logError(e);
        } catch (KStoreClientAuthException e) {
            LoggerSingleton.logError(e);
        } catch (KStoreClientException e) {
            LoggerSingleton.logError(e);
        }
    }

    private GridData getButtonGridData() {
        GridData buttonGridData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
        buttonGridData.widthHint = BUTTON_WIDTH;
        return buttonGridData;
    }

    protected void createSeparator(Composite parent) {
        Label label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    }

    @Override
    public void handlePageChanged(WizardPageChangedEvent event) {
        if (event.getWizardPage() == null || !event.getWizardPage().equals(wizardManager.getCurrentPage())) {
            return;
        }
        updateButtonBar(event.getWizardPage());
    }

    protected Button getButton(int id) {
        return buttonMap.get(id);
    }

    private class ButtonClickedListener extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent e) {
            if (e.widget.isDisposed()) {
                return;
            }
            wizardButtonPress((int) e.widget.getData(StringConstants.ID));
        }
    }

    public Control createStepArea(Composite parent) {
        List<Object> name = new ArrayList<>();
        List<Object> decription = new ArrayList<>();
        try {

            KStoreUsernamePasswordCredentials credentials = new KStoreUsernamePasswordCredentials();

            credentials = RequireAuthorizationHandler.getUsernamePasswordCredentials();
            KStoreRestClient res = new KStoreRestClient(credentials);
            List<KStoreProduct> names = res.getRecommendPlugins();
            for (int i = 0; i < names.size(); i++) {
                name.add(res.getRecommendPlugins().get(i).getName());
                decription.add(res.getRecommendPlugins().get(i).getDescription());
            }

            GridLayout glLeft = new GridLayout(1, false);
            glLeft.marginWidth = 10;
            glLeft.marginHeight = 10;
            glLeft.marginLeft = 10;
            glLeft.horizontalSpacing = 10;
            glLeft.verticalSpacing = 10;
            Button checkBox = new Button(parent, SWT.CHECK | SWT.WRAP);
            checkBox.setText(name.get(0).toString());
            checkBox.setSelection(true);
            idProduct.add(res.getRecommendPlugins().get(0).getId());
            Label lb = new Label(parent, SWT.WRAP);
            lb.setText(decription.get(0).toString());

            Button checkBox1 = new Button(parent, SWT.CHECK | SWT.WRAP);
            checkBox1.setText(name.get(1).toString());
            checkBox1.setSelection(true);
            idProduct.add(res.getRecommendPlugins().get(1).getId());
            Label lb1 = new Label(parent, SWT.WRAP);
            lb1.setText(decription.get(1).toString());

            Button checkBox2 = new Button(parent, SWT.CHECK | SWT.WRAP);
            checkBox2.setText(name.get(2).toString());
            checkBox2.setSelection(true);
            idProduct.add(res.getRecommendPlugins().get(2).getId());
            Label lb2 = new Label(parent, SWT.NONE | SWT.WRAP);
            lb2.setText(decription.get(2).toString());
            checkBox.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    try {
                        if (checkBox.getSelection() == true) {

                            idProduct.add(res.getRecommendPlugins().get(0).getId());

                        } else {
                            idProduct.remove(res.getRecommendPlugins().get(0).getId());
                        }
                    } catch (KStoreClientException e1) {
                        e1.printStackTrace();
                    }

                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {

                }
            });
            checkBox1.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    try {
                        if (checkBox.getSelection() == true) {

                            idProduct.add(res.getRecommendPlugins().get(1).getId());

                        } else {
                            idProduct.remove(res.getRecommendPlugins().get(1).getId());
                        }
                    } catch (KStoreClientException e1) {
                        e1.printStackTrace();
                    }
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {

                }
            });
            checkBox2.addSelectionListener(new SelectionListener() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    try {
                        if (checkBox.getSelection() == true) {

                            idProduct.add(res.getRecommendPlugins().get(2).getId());
                        } else {
                            idProduct.remove(res.getRecommendPlugins().get(2).getId());
                        }
                    } catch (KStoreClientException e1) {
                        e1.printStackTrace();
                    }

                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {

                }
            });
        } catch (KStoreClientException e1) {
            e1.printStackTrace();
        } catch (KStoreClientAuthException e1) {
            e1.printStackTrace();
        }
        return stepDetailsComposite;

    }
}
