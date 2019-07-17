package com.kms.katalon.objectspy.dialog;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.controls.HelpCompositeForDialog;
import com.kms.katalon.composer.components.impl.control.GifCLabel;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.core.webui.common.WebUiCommonHelper;
import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.objectspy.constants.ImageConstants;
import com.kms.katalon.objectspy.constants.ObjectspyMessageConstants;
import com.kms.katalon.objectspy.core.InspectSession;
import com.kms.katalon.objectspy.element.WebElement;
import com.kms.katalon.objectspy.element.WebElement.WebElementType;
import com.kms.katalon.objectspy.element.WebFrame;
import com.kms.katalon.objectspy.element.WebPage;
import com.kms.katalon.objectspy.highlight.HighlightRequest;
import com.kms.katalon.objectspy.util.WebElementUtils;
import com.kms.katalon.objectspy.websocket.AddonCommand;
import com.kms.katalon.objectspy.websocket.AddonSocket;
import com.kms.katalon.objectspy.websocket.messages.AddonMessage;
import com.kms.katalon.util.listener.EventListener;
import com.kms.katalon.util.listener.EventManager;

public class ObjectVerifyAndHighlightView implements EventListener<ObjectSpyEvent>, EventManager<ObjectSpyEvent> {

	private static final String HIGHLIGHT_JS_PATH = "/resources/js/highlight.js";

    private Label lblMessageVerifyObject;

    private Button btnVerifyAndHighlight;

    private Button btnAddScreenShotForElement;
    
    private Composite connectingComposite;

    private GifCLabel connectingLabel;

    private InputStream inputStream;

    private Label lblConnecting;

    private String highlightJS;

    private WebElement webElement;

    private InspectSession seleniumSession;

    private AddonSocket activeBrowserSession;

    private String getHighlightJS() {
        if (highlightJS == null) {
            highlightJS = loadHighlightJSFromResource();
        }
        return highlightJS;
    }

    private String loadHighlightJSFromResource() {
        try {
            URL url = FileLocator.find(FrameworkUtil.getBundle(ObjectVerifyAndHighlightView.class),
                    new Path(HIGHLIGHT_JS_PATH), null);
            return StringUtils.join(IOUtils.readLines(new BufferedInputStream(url.openStream()),
                            GlobalStringConstants.DF_CHARSET), "\n");
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
        return "";
    }

    /**
     * @wbp.parser.entryPoint
     */
    public Composite createVerifyAndHighlightView(Composite parent, int layoutStyle) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(layoutStyle));
        GridLayout gdVerifyView = new GridLayout(5, false);
        gdVerifyView.marginWidth = 0;
        composite.setLayout(gdVerifyView);

        connectingComposite = new Composite(composite, SWT.NONE);
        connectingComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
        connectingComposite.setLayout(new GridLayout(2, false));

        connectingLabel = new GifCLabel(connectingComposite, SWT.DOUBLE_BUFFERED);
        connectingLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));

        lblConnecting = new Label(connectingComposite, SWT.NONE);
        lblConnecting.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lblConnecting.setText(ObjectspyMessageConstants.DIA_MSG_VERIFYING);

        connectingComposite.setVisible(false);

        lblMessageVerifyObject = new Label(composite, SWT.NONE);
        lblMessageVerifyObject.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        btnVerifyAndHighlight = new Button(composite, SWT.FLAT);
        btnVerifyAndHighlight.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        btnVerifyAndHighlight.setText(ObjectspyMessageConstants.DIA_LBL_VERIFY_AND_HIGHLIGHT);
        
        btnAddScreenShotForElement = new Button(composite, SWT.FLAT);
        btnAddScreenShotForElement.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        btnAddScreenShotForElement.setText(ObjectspyMessageConstants.DIA_LBL_ADD_SCREENSHOT);
        
        new HelpCompositeForDialog(composite, DocumentationMessageConstants.DIALOG_OBJECT_SPY_WEB_UI) {
     
            @Override
            protected GridData createGridData() {
                return new GridData(SWT.LEFT, SWT.CENTER, false, false);
            }
            
            @Override
            protected GridLayout createLayout() {
                GridLayout layout = new GridLayout();
                layout.marginBottom = 0;
                layout.marginWidth = 0;
                return layout;
            }
        };

        registerControlModifyListeners();

        onElementChanged();

        return composite;
    }

    private void registerControlModifyListeners() {
        btnVerifyAndHighlight.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                clearMessage();

                Thread highlightThread = new Thread(() -> {
                    try {
                        setConnectingCompositeVisibleSync(true);
                        if (seleniumSession != null) {
                            highlightElementUsingSelenium();
                            return;
                        }
                        if (activeBrowserSession != null) {
                            highlightElementUsingAddon();
                            return;
                        }
                    } finally {
                        setConnectingCompositeVisibleSync(false);
                    }
                });

                highlightThread.start();
            }

            private void highlightElementUsingAddon() {
                String xpathExpression = new HighlightRequest(webElement).getData();
                activeBrowserSession.sendMessage(new AddonMessage(AddonCommand.HIGHLIGHT_OBJECT, xpathExpression));
            }

            private void highlightElementUsingSelenium() {
                WebDriver webDriver = null;
                String selector = "";
                try {
                    webDriver = seleniumSession.getWebDriver();
                    if (webDriver == null || isBrowserClosed(webDriver)) {
                        displayErrorMessageSync(ObjectspyMessageConstants.DIA_MSG_BROWSER_NOT_STARTED);
                        return;
                    }

                    By seleniumSelector = getSeleniumSelector(webElement);
                    if (seleniumSelector == null) {
                        return;
                    }
                    selector = getSelectorName(seleniumSelector);

                    webDriver.manage().timeouts().implicitlyWait(100, TimeUnit.MILLISECONDS);
                    WebUiCommonHelper.switchToWindowUsingTitle(webDriver, webDriver.getTitle(), 1);

                    switchToFrame(webDriver, webElement);

                    List<org.openqa.selenium.WebElement> webElements = webDriver.findElements(seleniumSelector);

                    if (webElements.size() == 0) {
                        displayErrorMessageSync(MessageFormat.format(
                                ObjectspyMessageConstants.DIA_UNABLE_TO_FIND_OBJECT_USING_Y_SELECTOR, selector));
                        return;
                    }
                    displayWebElementsFoundMessageSync(webElements.size(), selector);

                    UISynchronizeService.syncExec(() -> {
                        lblConnecting.setText(ObjectspyMessageConstants.DIA_MSG_HIGHLIGHTING);
                        connectingComposite.getParent().layout(true, true);
                    });

                    highlightElements(webDriver, webElements);
                } catch (WebDriverException | InterruptedException ex) {
                    displayErrorMessageSync(MessageFormat
                            .format(ObjectspyMessageConstants.DIA_UNABLE_TO_FIND_OBJECT_USING_Y_SELECTOR, selector));
                } finally {
                    if (webDriver != null || isBrowserClosed(webDriver)) {
                        webDriver.switchTo().defaultContent();
                    }
                }
            }

            private void highlightElements(WebDriver webDriver, List<org.openqa.selenium.WebElement> webElements) {
                if (webDriver instanceof JavascriptExecutor) {
                    JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;
                    
                    //scroll to first element
                    org.openqa.selenium.WebElement firstElement = webElements.get(0);
                    boolean isInViewPort = (Boolean)((JavascriptExecutor)webDriver).executeScript(
                            "var elem = arguments[0],                 " +
                            "  box = elem.getBoundingClientRect(),    " +
                            "  cx = box.left + box.width / 2,         " +
                            "  cy = box.top + box.height / 2,         " +
                            "  e = document.elementFromPoint(cx, cy); " +
                            "for (; e; e = e.parentElement) {         " +
                            "  if (e === elem)                        " +
                            "    return true;                         " +
                            "}                                        " +
                            "return false;                            "
                            , firstElement);
                    if (!isInViewPort) {
                            jsExecutor.executeScript("arguments[0].scrollIntoView({" +
                                                       " behavior: 'auto',         " + 
                                                       " block: 'center',          " +
                                                       " inline: 'center'          " +
                                                       " });                       "
                            , firstElement);
                    }
                    
                    // highlight all elements
                    String highlightJS = getHighlightJS();
                    webElements.parallelStream().forEach(element -> {
                        jsExecutor.executeScript(highlightJS, element);
                    });
                }
            }

            private void switchToFrame(WebDriver webDriver, WebElement katalonElement) {
                WebFrame parentElement = katalonElement.getParent();
                if (parentElement != null && parentElement.getType() == WebElementType.FRAME) {
                    switchToFrame(webDriver, parentElement);
                    org.openqa.selenium.WebElement parentSeleniumElement = webDriver
                            .findElement(getSeleniumSelector(parentElement));
                    webDriver.switchTo().frame(parentSeleniumElement);
                }
            }
        });
        
        btnAddScreenShotForElement.addSelectionListener(new SelectionAdapter() {
            @Override
			public void widgetSelected(SelectionEvent e) {
				Thread addScreenShotThread = new Thread(() -> {
					try {
						setConnectingCompositeVisibleSync(true);
						WebDriver driver = null;
						if (seleniumSession != null) {
							driver = seleniumSession.getWebDriver();
						} else {
							displayErrorMessageSync(ObjectspyMessageConstants.FAIL_TO_TAKE_SCREENSHOT);
							return;
						}
						String pathToImage = WebElementUtils.takeScreenShot(driver, webElement);
						if (pathToImage.equals("")) {
							displayErrorMessageSync(ObjectspyMessageConstants.FAIL_TO_TAKE_SCREENSHOT);
							return;
						}
						webElement.getProperties().removeIf(screenshot -> screenshot.getName().equals("screenshot"));
						webElement.addProperty(new WebElementPropertyEntity("screenshot", pathToImage, false));
						displaySuccessfulMessageSync(ObjectspyMessageConstants.SCREENSHOT_TAKEN);
						invoke(ObjectSpyEvent.ELEMENT_PROPERTIES_CHANGED, webElement);
					} catch (Exception ex) {
						LoggerSingleton.logError(ex);
					} finally {
						setConnectingCompositeVisibleSync(false);
					}
				});
				addScreenShotThread.start();
			}
		});
    }

    /**
     * Handles the captured element changed
     */
	private void onElementChanged() {
		changeBtnAddScreenShotState();
		changeBtnVerifyAndHighlightState();
	}

    private void changeBtnVerifyAndHighlightState() {
        if (btnVerifyAndHighlight.isDisposed()) {
            return;
        }

        boolean allowEnable = false;
        if (webElement != null) {
            SelectorMethod selectorMethod = webElement.getSelectorMethod();
            String selectorValue = webElement.getSelectorCollection().get(selectorMethod);
            if (!StringUtils.isBlank(selectorValue)) {
                allowEnable = true;
            }
        }

        if(seleniumSession == null && activeBrowserSession == null) {
        	allowEnable = false;
        }
        
        btnVerifyAndHighlight.setEnabled(allowEnable);
    }
    
    private void changeBtnAddScreenShotState() {
        if (btnAddScreenShotForElement.isDisposed()) {
            return;
        }

        boolean allowEnable = false;
        if (webElement != null) {
        	allowEnable = true;
        }
		if (seleniumSession == null) {
			allowEnable = false;
		}
        btnAddScreenShotForElement.setEnabled(allowEnable);
    }

    private By getSeleniumSelector(WebElement webElement) {
        SelectorMethod selectorMethod = webElement.getSelectorMethod();
        String selectorValue = webElement.getSelectorCollection().get(selectorMethod);
        switch (selectorMethod) {
            case BASIC:
                TestObject testObject = WebElementUtils.buildTestObject(webElement);
                return WebUiCommonHelper.buildLocator(testObject);
            case CSS:
                if (StringUtils.isEmpty(selectorValue)) {
                    return null;
                }
                return By.cssSelector(selectorValue);
            case XPATH:
                if (StringUtils.isEmpty(selectorValue)) {
                    return null;
                }
                return By.xpath(selectorValue);
        }
        return null;
    }

    private String getSelectorName(By by) {
        if (by instanceof By.ByCssSelector) {
            return "CSS";
        }
        return "XPath";
    }

    private void displaySuccessfulMessageSync(String message) {
        UISynchronizeService.syncExec(() -> {
            lblMessageVerifyObject.setForeground(ColorUtil.getTextSuccessfulColor());
            lblMessageVerifyObject.setText(message);
            lblMessageVerifyObject.getParent().getParent().layout(true);
        });
    }

    private void displayErrorMessageSync(String message) {
        UISynchronizeService.syncExec(() -> {
            lblMessageVerifyObject.setForeground(ColorUtil.getTextErrorColor());
            lblMessageVerifyObject.setText(message);
            lblMessageVerifyObject.getParent().getParent().layout(true);
        });
    }

    private void displayWebElementsFoundMessageSync(int numElements, String selector) {
        String pluralPostfix = numElements > 1 ? "s" : "";
        displaySuccessfulMessageSync(
                MessageFormat.format(ObjectspyMessageConstants.DIA_MSG_FOUND_X_ELEMENTS_USING_Y_SELECTOR, numElements,
                        pluralPostfix, selector));
    }

    private void clearMessage() {
        lblMessageVerifyObject.setText(StringUtils.EMPTY);
        lblMessageVerifyObject.getParent().layout(true);
    }

    private boolean isBrowserClosed(WebDriver webDriver) {
        try {
            webDriver.getTitle();
            return false;
        } catch (WebDriverException ex) {
            return true;
        }
    }

    private void setConnectingCompositeVisibleSync(boolean isConnectingCompositeVisible) {
        UISynchronizeService.syncExec(() -> {
            if (isConnectingCompositeVisible) {
                try {
                    inputStream = ImageConstants.URL_16_LOADING.openStream();
                    connectingLabel.setGifImage(inputStream);
                } catch (IOException ex) {} finally {
                    if (inputStream != null) {
                        IOUtils.closeQuietly(inputStream);
                        inputStream = null;
                    }
                }
            } else {
                if (inputStream != null) {
                    IOUtils.closeQuietly(inputStream);
                    inputStream = null;
                }
            }

            connectingComposite.setVisible(isConnectingCompositeVisible);
            btnVerifyAndHighlight.setEnabled(!isConnectingCompositeVisible);
            btnAddScreenShotForElement.setEnabled(!isConnectingCompositeVisible);
            connectingComposite.getParent().layout(true, true);
        });
    }

    @Override
    public void handleEvent(ObjectSpyEvent event, Object object) {
        switch (event) {
            case SELENIUM_SESSION_STARTED:
                seleniumSession = (InspectSession) object;
                activeBrowserSession = null;
                onElementChanged();
                return;
            case ADDON_SESSION_STARTED:
                activeBrowserSession = (AddonSocket) object;
                seleniumSession = null;
                onElementChanged();
                break;
            case ELEMENT_PROPERTIES_CHANGED:
            case SELECTED_ELEMENT_CHANGED:
            case SELECTOR_HAS_CHANGED:
                webElement = (WebElement) object;

                if (webElement instanceof WebPage) {
                    webElement = null;
                }
                onElementChanged();
                return;
            default:
                return;
        }
    }

    private Map<ObjectSpyEvent, Set<EventListener<ObjectSpyEvent>>> eventListeners = new HashMap<>();

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
