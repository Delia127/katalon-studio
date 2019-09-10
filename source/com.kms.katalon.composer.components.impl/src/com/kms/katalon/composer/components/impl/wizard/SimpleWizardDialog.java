package com.kms.katalon.composer.components.impl.wizard;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
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

import com.kms.katalon.composer.components.impl.constants.StringConstants;

public abstract class SimpleWizardDialog extends Dialog implements IWizardPageChangedListerner {

    private static final int BUTTON_WIDTH = 80;

    public static final int BACK_BUTTON_ID = 0;

    public static final int NEXT_BUTTON_ID = 1;

    public static final int FINISH_BUTTON_ID = 2;

    public static final int CANCEL_BUTTON_ID = 3;

    // Controls
    protected Composite stepDetailsComposite;

    protected Map<Integer, Button> buttonMap;

    // Fields
    protected WizardManager wizardManager;

    protected HashMap<String, Object> sharedData;

    public SimpleWizardDialog(Shell parentShell) {
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

        createButton(buttonBarComposite, BACK_BUTTON_ID, StringConstants.WZ_SETUP_BTN_BACK);
        createButton(buttonBarComposite, NEXT_BUTTON_ID, StringConstants.WZ_SETUP_BTN_NEXT);
        createButton(buttonBarComposite, FINISH_BUTTON_ID, StringConstants.DIA_FINISH);
        createButton(buttonBarComposite, CANCEL_BUTTON_ID, StringConstants.DIA_CANCEL);
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
            case BACK_BUTTON_ID: {
                backPressed();
                break;
            }
            case NEXT_BUTTON_ID: {
                nextPressed();
                break;
            }
            case FINISH_BUTTON_ID: {
                finishPressed();
                break;
            }
            case CANCEL_BUTTON_ID: {
                cancelPressed();
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
        getButton(FINISH_BUTTON_ID).setEnabled(page.canFinish() && page.canFlipToNextPage());

        getButton(BACK_BUTTON_ID).setEnabled(wizardManager.getWizardPages().indexOf(page) > 0);

        getButton(NEXT_BUTTON_ID).setEnabled(
                page.canFlipToNextPage()
                        && wizardManager.getWizardPages().indexOf(page) < wizardManager.getWizardPages().size() - 1);
    }

    protected final void backPressed() {
        Map<String, Object> pageSharedData = wizardManager.getCurrentPage().storeControlStates();
        if (pageSharedData != null) {
            sharedData.putAll(pageSharedData);
        }

        showPage(wizardManager.backPage());
    }

    protected final void nextPressed() {
        Map<String, Object> pageSharedData = wizardManager.getCurrentPage().storeControlStates();
        if (pageSharedData != null) {
            sharedData.putAll(pageSharedData);
        }

        showPage(wizardManager.nextPage());
    }

    protected void finishPressed() {
        super.okPressed();
    }

    protected void cancelPressed() {
        super.cancelPressed();
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
}