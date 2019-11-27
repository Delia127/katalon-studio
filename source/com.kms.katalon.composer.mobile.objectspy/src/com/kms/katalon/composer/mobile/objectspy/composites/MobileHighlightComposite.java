package com.kms.katalon.composer.mobile.objectspy.composites;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileElementInspectorDialog;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;
import com.kms.katalon.composer.mobile.objectspy.util.MobileActionHelper;
import com.kms.katalon.core.testobject.MobileTestObject;
import com.kms.katalon.core.testobject.MobileTestObject.MobileLocatorStrategy;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.repository.MobileElementEntity.LocatorStrategy;

public class MobileHighlightComposite {

    private MobileElementInspectorDialog parentDialog;

    private Label lblMessageVerifyObject;

    private CapturedMobileElement editingElement;

    private Button btnHighlight;

    private boolean isFinding;

    public MobileHighlightComposite(MobileElementInspectorDialog parentDialog) {
        this.parentDialog = parentDialog;
    }

    public Composite createComposite(Composite parent) {
        Composite elementToolsComposite = new Composite(parent, SWT.RIGHT_TO_LEFT);
        elementToolsComposite.setLayout(new GridLayout(2, false));
        elementToolsComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        btnHighlight = new Button(elementToolsComposite, SWT.NONE);
        btnHighlight.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
        btnHighlight.setText(StringConstants.DIA_BTN_HIGHLIGHT);
        btnHighlight.setToolTipText(StringConstants.DIA_TOOLTIP_HIGHLIGHT_BUTTON);
        btnHighlight.setEnabled(false);
        btnHighlight.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                findAndHighlightEditingElement();
            }
        });

        lblMessageVerifyObject = new Label(elementToolsComposite, SWT.WRAP | SWT.RIGHT);
        lblMessageVerifyObject.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

        return elementToolsComposite;
    }

    private void findAndHighlightEditingElement() {
        isFinding = true;
        clearMessage();
        refreshButtonStates();

        if (editingElement == null) {
            return;
        }

        LocatorStrategy locatorStrategy = editingElement.getLocatorStrategy();
        String locatorStrategyName = locatorStrategy.getLocatorStrategy();
        String locator = editingElement.getLocator();
        displayWaitMessage(locator, locatorStrategyName);

        // TODO: Run searching task in another thread to prevent UI block
        try {
            List<WebElement> webElements = findElements(editingElement);

            if (webElements != null && webElements.size() > 0) {
                List<Rectangle> elementRects = (List<Rectangle>) webElements.stream().map(webElement -> {
                    org.openqa.selenium.Point location = webElement.getLocation();
                    Dimension size = webElement.getSize();
                    return new Rectangle(location.x * 2, location.y * 2, size.width * 2, size.height * 2);
                }).collect(Collectors.toList());

                parentDialog.highlightElementRects(elementRects);
            }

            if (webElements != null && webElements.size() > 0) {
                displayFoundMessage(webElements.size(), locatorStrategyName, locator);
            } else {
                displayNotFoundMessage(locatorStrategyName, locator);
            }
        } catch (NoSuchElementException e) {
            displayNotFoundMessage(locatorStrategyName, locator);
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog("Unable to highlight element!", e.getMessage(),
                    ExceptionsUtil.getStackTraceForThrowable(e));
        } finally {
            isFinding = false;
            refreshButtonStates();
        }
    }

    private List<WebElement> findElements(CapturedMobileElement targetElement) throws Exception {
        if (targetElement == null) {
            return null;
        }
        MobileTestObject windowsTestObject = new MobileTestObject(targetElement.getName());
        windowsTestObject.setMobileLocator(targetElement.getLocator());
        windowsTestObject
                .setMobileLocatorStrategy(MobileLocatorStrategy.valueOf(editingElement.getLocatorStrategy().name()));

        MobileActionHelper mobileActionHelper = new MobileActionHelper(
                parentDialog.getInspectorController().getDriver());

        return mobileActionHelper.findElements(windowsTestObject);
    }

    private void displaySuccessfulMessage(String message) {
        lblMessageVerifyObject.setForeground(ColorUtil.getTextSuccessfulColor());
        lblMessageVerifyObject.setText(message);
        lblMessageVerifyObject.getParent().layout(true);
    }

    private void displayWarningMessage(String message) {
        lblMessageVerifyObject.setForeground(ColorUtil.getWarningForegroudColor());
        lblMessageVerifyObject.setText(message);
        lblMessageVerifyObject.getParent().layout(true);
    }

    private void displayErrorMessage(String message) {
        lblMessageVerifyObject.setForeground(ColorUtil.getTextErrorColor());
        lblMessageVerifyObject.setText(message);
        lblMessageVerifyObject.getParent().layout(true);
    }

    private void displayFoundMessage(int numElements, String strategy, String selector) {
        String pluralPostfix = numElements > 1 ? "s" : "";
        displaySuccessfulMessage(MessageFormat.format(StringConstants.DIA_MSG_ELEMENT_FOUND, numElements, pluralPostfix,
                strategy, selector));
    }

    private void displayWaitMessage(String strategy, String selector) {
        displayWarningMessage(MessageFormat.format(StringConstants.DIA_MSG_SEARCHING_FOR_ELEMENTS, strategy, selector));
    }

    private void displayNotFoundMessage(String strategy, String selector) {
        displayErrorMessage(MessageFormat.format(StringConstants.DIA_MSG_ELEMENT_NOT_FOUND, strategy, selector));
    }

    private void clearMessage() {
        lblMessageVerifyObject.setText(StringUtils.EMPTY);
        lblMessageVerifyObject.getParent().layout(true);
    }

    public CapturedMobileElement getEditingElement() {
        return editingElement;
    }

    public void setEditingElement(CapturedMobileElement editingElement) {
        this.editingElement = editingElement;
        refreshButtonStates();
        clearMessage();
    }

    private void refreshButtonStates() {
        boolean isEnableHightlightButton = !isFinding && editingElement != null;
        btnHighlight.setEnabled(isEnableHightlightButton);
    }
}
