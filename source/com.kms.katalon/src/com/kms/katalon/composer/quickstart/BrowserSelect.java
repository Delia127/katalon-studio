package com.kms.katalon.composer.quickstart;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.impl.control.Dropdown;
import com.kms.katalon.composer.components.impl.control.DropdownGroup;
import com.kms.katalon.composer.components.impl.control.DropdownItemSelectionListener;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.resources.constants.IImageKeys;
import com.kms.katalon.composer.resources.image.ImageManager;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class BrowserSelect extends ToolBar {

    interface BrowserSelectionCallback {
        void call(WebUIDriverType browser);
    }

    private WebUIDriverType preferredBrowser = WebUIDriverType.CHROME_DRIVER;

    private ToolItem dropdownSelectedItem;

    public WebUIDriverType getInput() {
        return preferredBrowser;
    }

    public void setInput(WebUIDriverType preferredBrowser) {
        this.preferredBrowser = preferredBrowser;
        dropdownSelectedItem.setImage(getBrowserIcon(preferredBrowser));
    }

    public BrowserSelect(Composite parent, int style) {
        super(parent, style | SWT.FLAT | SWT.RIGHT);
        createBrowserSelect(this);
    }

    private void createBrowserSelect(ToolBar toolbar) {
        toolbar.setBackground(ColorUtil.getToolBarBackgroundColor());
        toolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        toolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

        Dropdown dropdown = new Dropdown(toolbar.getShell());
        dropdownSelectedItem = new ToolItem(toolbar, SWT.DROP_DOWN);
        dropdownSelectedItem.setToolTipText("");
        dropdownSelectedItem.setImage(getBrowserIcon(preferredBrowser));

        createDropdownContent(dropdown, dropdownSelectedItem, browser -> {
            dropdownSelectedItem.setImage(getBrowserIcon(browser));
        });

        dropdownSelectedItem.addSelectionListener(new DropdownItemSelectionListener(dropdown) {
            @Override
            public void itemSelected(SelectionEvent event) {
                Point pos = toolbar.toDisplay(0, 0);
                Rectangle rect = toolbar.getBounds();
                dropdown.setLocation(pos.x, pos.y + rect.height);
                dropdown.resizeToFitContent();
                dropdown.setVisible(true);
            }
        });
    }

    private void createDropdownContent(Dropdown dropdown, ToolItem selectedItem,
            BrowserSelectionCallback itemSelectionListener) {
        DropdownGroup newBrowser = dropdown.addDropdownGroupItem("Browsers",
                ImageManager.getImage(IImageKeys.NEW_BROWSER_24));

        addNewBrowserItem(newBrowser, WebUIDriverType.CHROME_DRIVER, itemSelectionListener);
        addNewBrowserItem(newBrowser, WebUIDriverType.FIREFOX_DRIVER, itemSelectionListener);
        addNewBrowserItem(newBrowser, WebUIDriverType.EDGE_CHROMIUM_DRIVER, itemSelectionListener);

        if (Platform.OS_WIN32.equals(Platform.getOS())) {
            addNewBrowserItem(newBrowser, WebUIDriverType.IE_DRIVER, itemSelectionListener);
        }
    }

    private void addNewBrowserItem(DropdownGroup newBrowserGroup, WebUIDriverType webUIDriverType,
            BrowserSelectionCallback itemSelectionListener) {
        String itemText = webUIDriverType == WebUIDriverType.CHROME_DRIVER
                ? webUIDriverType.toString() + " (Recommended)"
                : webUIDriverType.toString();

        newBrowserGroup.addItem(itemText, getBrowserIcon(webUIDriverType), new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                preferredBrowser = webUIDriverType;
                itemSelectionListener.call(webUIDriverType);
            }
        });
    }

    private Image getBrowserIcon(WebUIDriverType webUIDriverType) {
        if (webUIDriverType == null) {
            return ImageManager.getImage(IImageKeys.CHROME_24);
        }
        switch (webUIDriverType) {
            case CHROME_DRIVER:
                return ImageManager.getImage(IImageKeys.CHROME_24);
            case FIREFOX_DRIVER:
                return ImageManager.getImage(IImageKeys.FIREFOX_24);
            case IE_DRIVER:
                return ImageManager.getImage(IImageKeys.IE_24);
            case EDGE_CHROMIUM_DRIVER:
                return ImageManager.getImage(IImageKeys.EDGE_CHROMIUM_24);
            default:
                return null;
        }
    }

    @Override
    protected void checkSubclass() {
    }
}
