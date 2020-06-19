package com.katalon.plugin.smart_xpath.settings.composites;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TypedListener;

import com.katalon.plugin.smart_xpath.constant.SmartXPathMessageConstants;
import com.kms.katalon.core.testobject.SelectorMethod;

public class DefaultWebLocatorSelectionComposite extends Group {

    private SelectorMethod selectedSelectorMethod = SelectorMethod.XPATH;
    
    private List<Button> radioSelectorMethods = new ArrayList<Button>();

    @SuppressWarnings("serial")
    private final Map<String, SelectorMethod> defaultLocatorOptions = new LinkedHashMap<String, SelectorMethod>() {
        {
            put("XPath", SelectorMethod.XPATH);
            put("Attributes", SelectorMethod.BASIC);
            put("CSS", SelectorMethod.CSS);
            put("Image", SelectorMethod.IMAGE);
        }
    };

    public DefaultWebLocatorSelectionComposite(Composite parent, int style) {
        super(parent, style);
        createContents();
    }

    @Override
    protected void checkSubclass() {
    }

    private void createContents() {
        RowLayout defaultLocatorRowLayout = new RowLayout(SWT.HORIZONTAL);
        defaultLocatorRowLayout.spacing = 5;
        defaultLocatorRowLayout.marginHeight = 5;
        defaultLocatorRowLayout.marginWidth = 5;
        this.setLayout(defaultLocatorRowLayout);
        this.setText(SmartXPathMessageConstants.GRP_DEFAULT_LOCATOR_SELECTION_AREA);

        defaultLocatorOptions.forEach((label, selectorMethod) -> {
            Button radioDefaultLocator = new Button(this, SWT.RADIO);
            radioDefaultLocator.setText(label);
            radioDefaultLocator.setData(selectorMethod);
            // InputStream input = RadioButtonDemo.class.getResourceAsStream("/org/o7planning/swt/icon/male-16.png");
            // Image image = new Image(null, input);
            // radioButton.setImage(image);

            radioDefaultLocator.setSelection(selectorMethod == selectedSelectorMethod);

            radioDefaultLocator.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    if (radioDefaultLocator.getSelection()) {
                        handleSelectionChange(event);
                    }
                }
            });
            
            radioSelectorMethods.add(radioDefaultLocator);
        });
    }

    public void setInput(SelectorMethod selectorMethod) {
        selectedSelectorMethod = selectorMethod;
        radioSelectorMethods.forEach(radioSelectorMethod -> {
            radioSelectorMethod.setSelection(radioSelectorMethod.getData() == selectedSelectorMethod);
        });
    }

    public SelectorMethod getInput() {
        return selectedSelectorMethod;
    }
    
    public boolean compareInput(SelectorMethod selectorMethod) {
        return selectedSelectorMethod == selectorMethod;
    }

    private void handleSelectionChange(SelectionEvent selectionEvent) {
        selectedSelectorMethod = (SelectorMethod) ((Button) selectionEvent.getSource()).getData();
        dispatchSelectionEvent(selectionEvent);
    }

    private void dispatchSelectionEvent(SelectionEvent selectionEvent) {
        notifyListeners(SWT.Selection, null);
        notifyListeners(SWT.DefaultSelection, null);
    }

    public void addSelectionListener(SelectionListener listener) {
        checkWidget();
        if (listener == null) {
            return;
        }
        TypedListener typedListener = new TypedListener(listener);
        addListener(SWT.Selection, typedListener);
        addListener(SWT.DefaultSelection, typedListener);
    }
}
