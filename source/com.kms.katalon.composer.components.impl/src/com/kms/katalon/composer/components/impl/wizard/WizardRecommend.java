package com.kms.katalon.composer.components.impl.wizard;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;


public abstract class WizardRecommend extends Dialog implements IWizardPageChangedListerner {
    private static final int BUTTON_WIDTH = 80;

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
        IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
        eventBroker.send(EventConstants.PROJECT_RECOMMEND_PLUGINS, null);
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
        String text = "      Link to Jira tickets, sync results and submit isses.";
        String text1 = "     Time and effort saving. Auto heal failed execution.";
        String text2= "      This plug-in makes writting automation tests using excel files.";
        
        GridLayout glLeft = new GridLayout(1, false);
        glLeft.marginWidth = 10;
        glLeft.marginHeight = 10;
        glLeft.marginLeft = 10;
        glLeft.horizontalSpacing = 10;
        glLeft.verticalSpacing = 10;
        Button checkBox = new Button(parent,SWT.CHECK| SWT.WRAP);
        checkBox.setText("Jira Integration");
        checkBox.setSelection(true);
        Label lb = new Label(parent,SWT.NONE);
        lb.setText(text);
        
        Button checkBox1 = new Button(parent,SWT.CHECK| SWT.WRAP);
        checkBox1.setText("Auto-healing Smart XPath");
        checkBox1.setSelection(true);
        Label lb1 = new Label(parent,SWT.NONE);
        lb1.setText(text1);
        
        Button checkBox2 = new Button(parent,SWT.CHECK| SWT.WRAP);
        checkBox2.setText("Basic Report");
        checkBox2.setSelection(true);
        Label lb2 = new Label(parent,SWT.NONE);
        lb2.setText(text1);
        
        Button checkBox3 = new Button(parent,SWT.CHECK| SWT.WRAP);
        checkBox3.setText("Write/Read Excel Keywords");
        checkBox3.setSelection(true);
        Label lb3 = new Label(parent,SWT.NONE);
        lb3.setText(text2);
        
        checkBox.addSelectionListener(new SelectionAdapter() {
            @Override

            public void widgetSelected(SelectionEvent e) {
                if(checkBox.getSelection()==true){
                }
                }
            });
        checkBox1.addSelectionListener(new SelectionAdapter() {
            @Override

            public void widgetSelected(SelectionEvent e) {
                if(checkBox1.getSelection()==true){
                    
                }
                }
            });
        checkBox2.addSelectionListener(new SelectionAdapter() {
            @Override

            public void widgetSelected(SelectionEvent e) {
                if(checkBox2.getSelection()==true){
                    
                }
                }
            });
        checkBox3.addSelectionListener(new SelectionAdapter() {
            @Override

            public void widgetSelected(SelectionEvent e) {
                if(checkBox3.getSelection()==true){
                    
                }
                }
            });
        return stepDetailsComposite;

   }
}
