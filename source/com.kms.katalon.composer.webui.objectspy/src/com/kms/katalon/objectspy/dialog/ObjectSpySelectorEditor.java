package com.kms.katalon.objectspy.dialog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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

import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.core.webui.common.WebUiCommonHelper;
import com.kms.katalon.objectspy.constants.ObjectspyMessageConstants;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebPage;
import com.kms.katalon.objectspy.util.WebElementUtils;
import com.kms.katalon.util.listener.EventListener;
import com.kms.katalon.util.listener.EventManager;

public class ObjectSpySelectorEditor implements EventListener<ObjectSpyEvent>, EventManager<ObjectSpyEvent> {

    private static final int EDITOR_LINE_HEIGHT = 4;

    private StyledText txtSelector;

    private WebElement webElement;

    private Map<ObjectSpyEvent, Set<EventListener<ObjectSpyEvent>>> eventListeners = new HashMap<>();

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

        GridData gdTxtSelector = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        GC graphicContext = new GC(txtSelector);
        FontMetrics fm = graphicContext.getFontMetrics();
        gdTxtSelector.heightHint = EDITOR_LINE_HEIGHT * fm.getHeight();
        gdTxtSelector.widthHint  = 300; 
        txtSelector.setLayoutData(gdTxtSelector);

        registerControlListeners();

        return composite;
    }

    private void registerControlListeners() {
        txtSelector.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (webElement == null) {
                    return;
                }
                webElement.setSelectorValue(webElement.getSelectorMethod(), txtSelector.getText());
                invoke(ObjectSpyEvent.SELECTOR_HAS_CHANGED, webElement);
            }
        });
    }

    private void changeEditorStatus(String text, boolean editable) {
        txtSelector.setText(StringUtils.defaultString(text));
        txtSelector.setEditable(editable);
        if (editable) {
            txtSelector.forceFocus();
            txtSelector.selectAll();
            txtSelector.setBackground(null);
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
            case XPATH:			
            	TestObject testObject2 = WebElementUtils.buildTestObject(webElement);
				String textToSet = WebUiCommonHelper.getSelectorValue(testObject2);
				textToSet = (textToSet == null ) ? StringUtils.EMPTY : textToSet;
            	changeEditorStatus(textToSet, true);
				break;
            default:
                changeEditorStatus(webElement.getSelectorCollection().get(selectorMethod), true);
        }
    }

    public String getSelectorValue() {
        return txtSelector.getText();
    }

    @Override
    public void handleEvent(ObjectSpyEvent event, Object object) {
        switch (event) {
            case ELEMENT_PROPERTIES_CHANGED:
                webElement = (WebElement) object;

                if (webElement instanceof WebPage) {
                    webElement = null;
                }
                onWebElementChanged();
                return;
            default:
                return;
        }
    }

    @Override
    public Iterable<EventListener<ObjectSpyEvent>> getListeners(ObjectSpyEvent event) {
        return eventListeners.get(event);
    }

    @Override
    public void addListener(EventListener<ObjectSpyEvent> listener, Iterable<ObjectSpyEvent> events) {
        events.forEach(e -> {
            Set<EventListener<ObjectSpyEvent>> listenerOnEvent = eventListeners.get(e);
            if (listenerOnEvent == null) {
                listenerOnEvent = new HashSet<>();
            }
            listenerOnEvent.add(listener);
            eventListeners.put(e, listenerOnEvent);
        });
    }
}
