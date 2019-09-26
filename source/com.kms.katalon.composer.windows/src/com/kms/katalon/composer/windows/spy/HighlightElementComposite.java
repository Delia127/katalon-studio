package com.kms.katalon.composer.windows.spy;

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
import com.kms.katalon.composer.windows.dialog.WindowsObjectDialog;
import com.kms.katalon.composer.windows.element.CapturedWindowsElement;
import com.kms.katalon.core.testobject.WindowsTestObject;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.core.windows.keyword.helper.WindowsActionHelper;
import com.kms.katalon.entity.repository.WindowsElementEntity.LocatorStrategy;

public class HighlightElementComposite {

    private WindowsObjectDialog parentDialog;

    private Label lblMessageVerifyObject;

    private CapturedWindowsElement editingElement;

    private Button btnHighlight;

    public HighlightElementComposite(WindowsObjectDialog parentDialog) {
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
        clearMessage();
        refreshButtonsState();

        if (editingElement == null) {
            return;
        }

        LocatorStrategy locatorStrategy = editingElement.getLocatorStrategy();
        String locatorStrategyName = locatorStrategy.getLocatorStrategy();
        String locator = editingElement.getLocator();
        try {
            List<WebElement> webElements = findElements(editingElement);
            WebElement firstElement = webElements.size() > 0 ? webElements.get(0) : null;
            if (firstElement != null) {
                org.openqa.selenium.Point location = firstElement.getLocation();
                parentDialog.setSelectedElementByLocation(location.x, location.y);
            }

            List<Rectangle> elementRects = (List<Rectangle>) webElements.stream().map(webElement -> {
                org.openqa.selenium.Point location = webElement.getLocation();
                Dimension size = webElement.getSize();
                return new Rectangle(location.x, location.y, size.width, size.height);
            }).collect(Collectors.toList());

            parentDialog.highlightElementRects(elementRects);

            if (webElements.size() > 0) {
                displayFoundMessageSync(webElements.size(), locatorStrategyName, locator);
            } else {
                displayNotFoundMessageSync(locatorStrategyName, locator);
            }
        } catch (NoSuchElementException e) {
            displayNotFoundMessageSync(locatorStrategyName, locator);
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog("Unable to highlight element!", e.getMessage(),
                    ExceptionsUtil.getStackTraceForThrowable(e));
        } finally {
            refreshButtonsState();
        }
    }

    private List<WebElement> findElements(CapturedWindowsElement targetElement) {
        if (targetElement == null) {
            return null;
        }
        WindowsTestObject windowsTestObject = new WindowsTestObject(targetElement.getName());
        windowsTestObject.setLocator(targetElement.getLocator());
        windowsTestObject.setLocatorStrategy(com.kms.katalon.core.testobject.WindowsTestObject.LocatorStrategy
                .valueOf(editingElement.getLocatorStrategy().name()));

        WindowsActionHelper windowsActionHelper = new WindowsActionHelper(
                parentDialog.getInspectorController().getWindowsSession());

        return windowsActionHelper.findElements(windowsTestObject);
    }

    private void displaySuccessfulMessageSync(String message) {
        lblMessageVerifyObject.setForeground(ColorUtil.getTextSuccessfulColor());
        lblMessageVerifyObject.setText(message);
        lblMessageVerifyObject.getParent().layout(true);
    }

    private void displayErrorMessageSync(String message) {
        lblMessageVerifyObject.setForeground(ColorUtil.getTextErrorColor());
        lblMessageVerifyObject.setText(message);
        lblMessageVerifyObject.getParent().layout(true);
    }

    private void displayFoundMessageSync(int numElements, String strategy, String selector) {
        String pluralPostfix = numElements > 1 ? "s" : "";
        displaySuccessfulMessageSync(MessageFormat.format(StringConstants.DIA_MSG_ELEMENT_FOUND, numElements,
                pluralPostfix, strategy, selector));
    }

    private void displayNotFoundMessageSync(String strategy, String selector) {
        displayErrorMessageSync(MessageFormat.format(StringConstants.DIA_MSG_ELEMENT_NOT_FOUND, strategy, selector));
    }

    private void clearMessage() {
        lblMessageVerifyObject.setText(StringUtils.EMPTY);
        lblMessageVerifyObject.getParent().layout(true);
    }

    public CapturedWindowsElement getEditingElement() {
        return editingElement;
    }

    public void setEditingElement(CapturedWindowsElement editingElement) {
        this.editingElement = editingElement;
        refreshButtonsState();
        clearMessage();
    }

    private void refreshButtonsState() {
        boolean isEnableHightlightButton = editingElement != null;
        btnHighlight.setEnabled(isEnableHightlightButton);
    }
}
