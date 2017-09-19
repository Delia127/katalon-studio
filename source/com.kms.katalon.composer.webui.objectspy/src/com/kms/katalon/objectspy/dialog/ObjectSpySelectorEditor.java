package com.kms.katalon.objectspy.dialog;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.core.webui.common.WebUiCommonHelper;
import com.kms.katalon.objectspy.constants.ObjectSpyEventConstants;
import com.kms.katalon.objectspy.constants.ObjectspyMessageConstants;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebPage;
import com.kms.katalon.objectspy.util.WebElementUtils;

public class ObjectSpySelectorEditor {

    private static final int EDITOR_LINE_HEIGHT = 4;

    private StyledText txtSelector;

    private IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();

    private WebElement webElement;

    /**
     * @wbp.parser.entryPoint
     */
    public Composite createObjectSelectorEditor(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);

        Label lblSelectorEditor = new Label(composite, SWT.NONE);
        lblSelectorEditor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblSelectorEditor.setText(ObjectspyMessageConstants.LBL_DLG_SELECTOR_EDITOR);
        ControlUtils.setFontToBeBold(lblSelectorEditor);

        txtSelector = new StyledText(composite, SWT.BORDER | SWT.WRAP | SWT.VERTICAL);
        txtSelector.setEditable(false);

        GridData gdTxtSelector = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        GC graphicContext = new GC(txtSelector);
        FontMetrics fm = graphicContext.getFontMetrics();
        gdTxtSelector.heightHint = EDITOR_LINE_HEIGHT * fm.getHeight();
        txtSelector.setLayoutData(gdTxtSelector);

        registerEventListeners();

        registerControlListeners();

        return composite;
    }

    private void registerEventListeners() {
        eventBroker.subscribe(ObjectSpyEventConstants.OBJECT_PROPERTIES_CHANGED, objectSpyEventHandler);
        eventBroker.subscribe(ObjectSpyEventConstants.SELECTED_OBJECT_CHANGED, objectSpyEventHandler);
    }

    private void registerControlListeners() {
        txtSelector.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (webElement == null) {
                    return;
                }
                webElement.setSelectorValue(webElement.getSelectorMethod(), txtSelector.getText());
                eventBroker.post(ObjectSpyEventConstants.SELECTOR_VALUE_CHANGED, webElement);
            }
        });
    }

    @SuppressWarnings("unused")
    private void onHTMLElementChangeMethod(HTMLElement htmlElement) {
        if (htmlElement.getSelectorMethod() == SelectorMethod.BASIC) {
            changeEditorStatus(htmlElement.getXpath(), false);
            return;
        }
        String selectorValue = htmlElement.getSelectorCollection().getOrDefault(htmlElement.getSelectorMethod(),
                StringUtils.EMPTY);
        changeEditorStatus(selectorValue, true);
    }

    private void changeEditorStatus(String text, boolean editable) {
        txtSelector.setText(StringUtils.defaultString(text));
        txtSelector.setEditable(editable);
        if (editable) {
            txtSelector.forceFocus();
            txtSelector.selectAll();
            txtSelector.setBackground(ColorUtil.getWhiteBackgroundColor());
            return;
        }
        txtSelector.setBackground(ColorUtil.getDisabledItemBackgroundColor());
    }

    private void onWebElementChanged() {
        if (txtSelector.isDisposed()) {
            return;
        }
        if (webElement == null) {
            txtSelector.setEnabled(false);
            txtSelector.setText(StringUtils.EMPTY);
            return;
        }
        txtSelector.setEnabled(true);
        SelectorMethod selectorMethod = webElement.getSelectorMethod();
        switch (selectorMethod) {
            case BASIC:
                TestObject testObject = WebElementUtils.buildTestObject(webElement);
                changeEditorStatus(WebUiCommonHelper.getSelectorValue(testObject), false);
                return;
            default:
                changeEditorStatus(webElement.getSelectorCollection().get(selectorMethod), true);
        }
    }

    public String getSelectorValue() {
        return txtSelector.getText();
    }

    private EventServiceAdapter objectSpyEventHandler = new EventServiceAdapter() {

        @Override
        public void handleEvent(Event event) {
            switch (event.getTopic()) {
                case ObjectSpyEventConstants.OBJECT_PROPERTIES_CHANGED:
                case ObjectSpyEventConstants.SELECTED_OBJECT_CHANGED:
                    webElement = (WebElement) getObject(event);

                    if (webElement instanceof WebPage) {
                        webElement = null;
                    }
                    onWebElementChanged();
                    return;
            }
        }
    };
}
