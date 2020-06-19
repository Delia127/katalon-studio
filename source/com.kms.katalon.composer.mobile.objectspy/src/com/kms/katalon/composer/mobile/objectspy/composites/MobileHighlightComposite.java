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
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileElementInspectorDialog;
import com.kms.katalon.composer.mobile.objectspy.element.impl.CapturedMobileElement;
import com.kms.katalon.composer.mobile.objectspy.util.MobileActionHelper;
import com.kms.katalon.core.testobject.MobileTestObject;
import com.kms.katalon.core.testobject.MobileTestObject.MobileLocatorStrategy;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.repository.MobileElementEntity.LocatorStrategy;

import io.appium.java_client.ios.IOSDriver;

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

        lblMessageVerifyObject = new Label(elementToolsComposite, SWT.WRAP | SWT.LEFT);
        lblMessageVerifyObject.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));

        btnHighlight = new Button(elementToolsComposite, SWT.RIGHT);
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

        return elementToolsComposite;
    }

    private void findAndHighlightEditingElement() {
        LocatorStrategy locatorStrategy = editingElement.getLocatorStrategy();
        String locatorStrategyName = locatorStrategy.getLocatorStrategy();
        String locator = editingElement.getLocator();
        Thread thread = new Thread(() -> {
            isFinding = true;
            UISynchronizeService.syncExec(() -> {
                clearMessage();
                refreshButtonStates();
            });

            if (editingElement == null) {
                return;
            }
            UISynchronizeService.syncExec(() -> {
                displayWaitMessage(locator, locatorStrategyName);
            });

            try {
                long startTime = System.currentTimeMillis();
                List<WebElement> webElements = findElements(editingElement);
                long elapsedTime = System.currentTimeMillis() - startTime;

                if (webElements != null && webElements.size() > 0) {
                    List<Rectangle> elementRects = (List<Rectangle>) webElements.stream().map(webElement -> {
                        org.openqa.selenium.Point location = webElement.getLocation();
                        Dimension size = webElement.getSize();
                        double ratio = parentDialog.getInspectorController().getDriver() instanceof IOSDriver ? 2 : 1;
                        return new Rectangle(safeRoundDouble(location.x * ratio), safeRoundDouble(location.y * ratio),
                                safeRoundDouble(size.width * ratio), safeRoundDouble(size.height * ratio));
                    }).collect(Collectors.toList());

                    UISynchronizeService.syncExec(() -> {
                        parentDialog.highlightElementRects(elementRects);
                    });
                }

                UISynchronizeService.syncExec(() -> {
                    if (webElements != null && webElements.size() > 0) {
                        displayFoundMessage(webElements.size(), locatorStrategyName, locator, elapsedTime);
                    } else {
                        displayNotFoundMessage(locatorStrategyName, locator);
                    }
                });
            } catch (NoSuchElementException e) {
                UISynchronizeService.syncExec(() -> {
                    displayNotFoundMessage(locatorStrategyName, locator);
                });
            } catch (Exception e) {
                UISynchronizeService.syncExec(() -> {
                    displayNotFoundMessage(locatorStrategyName, locator);
                });
            } finally {
                UISynchronizeService.syncExec(() -> {
                    isFinding = false;
                    refreshButtonStates();
                });
            }
        });
        thread.start();
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

    private void displayFoundMessage(int numElements, String strategy, String selector, long elapsedTime) {
        String pluralPostfix = numElements > 1 ? "s" : "";
        displaySuccessfulMessage(MessageFormat.format(StringConstants.DIA_MSG_ELEMENT_FOUND, numElements, pluralPostfix, strategy, selector, elapsedTime));
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

    private static int safeRoundDouble(double d) {
        long rounded = Math.round(d);
        return (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, rounded));
    }
}
