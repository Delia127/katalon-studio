package com.kms.katalon.objectspy.dialog;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.BindException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.framework.Bundle;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kms.katalon.composer.components.controls.HelpCompositeForDialog;
import com.kms.katalon.composer.components.impl.control.Dropdown;
import com.kms.katalon.composer.components.impl.control.DropdownGroup;
import com.kms.katalon.composer.components.impl.control.DropdownItemSelectionListener;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.WebElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.objectspy.components.CapturedHTMLElementsComposite;
import com.kms.katalon.objectspy.constants.ImageConstants;
import com.kms.katalon.objectspy.constants.ObjectSpyPreferenceConstants;
import com.kms.katalon.objectspy.constants.ObjectspyMessageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.core.HTMLElementCaptureServer;
import com.kms.katalon.objectspy.core.InspectSession;
import com.kms.katalon.objectspy.core.KatalonRequestHandler;
import com.kms.katalon.objectspy.core.RequestFailedListener;
import com.kms.katalon.objectspy.dialog.AddToObjectRepositoryDialog.AddToObjectRepositoryDialogResult;
import com.kms.katalon.objectspy.element.DomElementXpath;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLElement.HTMLStatus;
import com.kms.katalon.objectspy.element.HTMLElement.MatchedStatus;
import com.kms.katalon.objectspy.element.HTMLFrameElement;
import com.kms.katalon.objectspy.element.HTMLPageElement;
import com.kms.katalon.objectspy.element.HTMLRawElement;
import com.kms.katalon.objectspy.element.tree.HTMLElementTreeContentProvider;
import com.kms.katalon.objectspy.element.tree.HTMLRawElementLabelProvider;
import com.kms.katalon.objectspy.element.tree.HTMLRawElementTreeContentProvider;
import com.kms.katalon.objectspy.element.tree.HTMLRawElementTreeViewerFilter;
import com.kms.katalon.objectspy.exception.DOMException;
import com.kms.katalon.objectspy.exception.IEAddonNotInstalledException;
import com.kms.katalon.objectspy.highlight.HighlightRequest;
import com.kms.katalon.objectspy.util.BrowserUtil;
import com.kms.katalon.objectspy.util.DOMUtils;
import com.kms.katalon.objectspy.util.HTMLElementUtil;
import com.kms.katalon.objectspy.util.UtilitiesAddonUtil;
import com.kms.katalon.objectspy.util.Win32Helper;
import com.kms.katalon.objectspy.util.WinRegistry;
import com.kms.katalon.objectspy.websocket.AddonCommand;
import com.kms.katalon.objectspy.websocket.AddonSocket;
import com.kms.katalon.objectspy.websocket.AddonSocketServer;
import com.kms.katalon.objectspy.websocket.messages.AddonMessage;
import com.kms.katalon.objectspy.websocket.messages.StartInspectAddonMessage;
import com.kms.katalon.preferences.internal.PreferenceStoreManager;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;

@SuppressWarnings("restriction")
public class ObjectSpyDialog extends Dialog implements EventHandler {
    private static final String IE_WINDOW_CLASS = "IEFrame"; //$NON-NLS-1$

    private static final String relativePathToIEAddonSetup = File.separator + "extensions" + File.separator + "IE" //$NON-NLS-1$ //$NON-NLS-2$
            + File.separator + "Object Spy" + File.separator + "setup.exe"; //$NON-NLS-1$ //$NON-NLS-2$

    private static final String RESOURCES_FOLDER_NAME = "resources"; //$NON-NLS-1$

    private static final String IE_ADDON_BHO_KEY = "{8CB0FB3A-8EFA-4F94-B605-F3427688F8C7}"; //$NON-NLS-1$

    public static final String IE_WINDOWS_32BIT_BHO_REGISTRY_KEY = "SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\explorer\\Browser Helper Objects"; //$NON-NLS-1$

    public static final String IE_WINDOWS_BHO_REGISTRY_KEY = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\explorer\\Browser Helper Objects"; //$NON-NLS-1$

    public static final String OBJECT_SPY_CHROME_ADDON_URL = "https://chrome.google.com/webstore/detail/katalon-utilities/ljdobmomdgdljniojadhoplhkpialdid"; //$NON-NLS-1$

    public static final String OBJECT_SPY_FIREFOX_ADDON_URL = "https://addons.mozilla.org/en-US/firefox/addon/katalon-object-spy"; //$NON-NLS-1$

    private List<HTMLPageElement> elements;

    private CapturedHTMLElementsComposite capturedObjectComposite;

    private Logger logger;

    private HTMLElementCaptureServer server;

    private InspectSession session;

    private ToolBar mainToolbar;

    private ToolItem addPageElementToolItem;

    private ToolItem addFrameElementToolItem;

    private ToolItem addElementToolItem;

    private ToolItem removeElementToolItem;

    private ToolItem addElmtToObjRepoToolItem;

    private ToolItem highlightObjectToolItem;

    private ToolItem startBrowser;

    private IEventBroker eventBroker;

    private boolean isDisposed;

    private TreeViewer domTreeViewer;

    private HTMLRawElementLabelProvider domTreeViewerLabelProvider;

    private HTMLRawElementTreeViewerFilter domTreeViewerFilter;

    private Document currentHTMLDocument;

    private Text txtXpathInput;

    private WebUIDriverType defaultBrowser;

    private boolean isInstant = false;

    private Text txtStartUrl;

    private AddonSocket currentInstantSocket;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     * @param logger
     * @param eventBroker
     * @throws Exception
     */
    public ObjectSpyDialog(Shell parentShell, Logger logger, IEventBroker eventBroker) throws Exception {
        super(parentShell);
        setShellStyle(SWT.SHELL_TRIM | SWT.NONE);
        this.logger = logger;
        this.eventBroker = eventBroker;
        eventBroker.subscribe(EventConstants.OBJECT_SPY_HTML_ELEMENT_CAPTURED, this);
        eventBroker.subscribe(EventConstants.OBJECT_SPY_HTML_DOM_CAPTURED, this);
        isDisposed = false;
        elements = new ArrayList<HTMLPageElement>();
        // set default browser
        defaultBrowser = getWebUIDriver();
        startSocketServer();
    }

    private void startSocketServer() {
        try {
            new ProgressMonitorDialog(getParentShell()).run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask(ObjectspyMessageConstants.MSG_DLG_INIT_OBJECT_SPY, 1);
                    AddonSocketServer.getInstance().start(AddonSocket.class);
                }
            });
        } catch (InvocationTargetException e) {
            LoggerSingleton.logError(e.getTargetException());
        } catch (InterruptedException e) {
            // Ignore this
        }
    }

    /**
     * Create contents of the dialog.
     * 
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite mainContainer = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = (GridLayout) mainContainer.getLayout();
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        gridLayout.verticalSpacing = 0;
        gridLayout.horizontalSpacing = 0;

        Composite toolbarComposite = new Composite(mainContainer, SWT.NONE);
        toolbarComposite.setLayout(new GridLayout(2, false));
        toolbarComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        addElementTreeToolbar(toolbarComposite);
        addStartBrowserToolbar(toolbarComposite);

        Composite bodyComposite = (Composite) super.createDialogArea(mainContainer);
        bodyComposite.setLayout(new FillLayout(SWT.VERTICAL));

        SashForm hSashForm = new SashForm(bodyComposite, SWT.NONE);
        hSashForm.setSashWidth(5);

        Composite objectComposite = new Composite(hSashForm, SWT.NONE);
        GridLayout gl_objectComposite = new GridLayout();
        gl_objectComposite.marginLeft = 5;
        gl_objectComposite.marginWidth = 0;
        gl_objectComposite.marginBottom = 0;
        gl_objectComposite.marginHeight = 0;
        gl_objectComposite.verticalSpacing = 0;
        gl_objectComposite.horizontalSpacing = 0;
        objectComposite.setLayout(gl_objectComposite);

        createLeftPanel(objectComposite);

        Composite htmlDomComposite = new Composite(hSashForm, SWT.NONE);
        GridLayout gl_htmlDomComposite = new GridLayout();
        gl_htmlDomComposite.marginBottom = 5;
        gl_htmlDomComposite.marginRight = 5;
        gl_htmlDomComposite.marginWidth = 0;
        gl_htmlDomComposite.marginHeight = 0;
        gl_htmlDomComposite.horizontalSpacing = 0;
        htmlDomComposite.setLayout(gl_htmlDomComposite);

        createRightPanel(htmlDomComposite);

        hSashForm.setWeights(new int[] { 3, 8 });
        mainContainer.pack();

        txtStartUrl.setFocus();
        txtStartUrl.selectAll();

        new HelpCompositeForDialog(mainContainer, DocumentationMessageConstants.DIALOG_OBJECT_SPY_WEB_UI);
        return mainContainer;
    }

    public void addStartBrowserToolbar(Composite toolbarComposite) {
        Composite toolbarRightSideComposite = new Composite(toolbarComposite, SWT.NONE);
        toolbarRightSideComposite.setLayout(new GridLayout(3, false));
        toolbarRightSideComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblStartUrl = new Label(toolbarRightSideComposite, SWT.NONE);
        lblStartUrl.setText(ObjectspyMessageConstants.LBL_DLG_START_URL);
        txtStartUrl = new Text(toolbarRightSideComposite, SWT.BORDER);
        GridData gdTxtStartUrl = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTxtStartUrl.heightHint = 20;
        gdTxtStartUrl.widthHint = 300;
        txtStartUrl.setLayoutData(gdTxtStartUrl);
        txtStartUrl.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.character == SWT.CR) {
                    startBrowser();
                }
            }
        });
        txtStartUrl.setText(
                getPreferenceStore().getString(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_DEFAULT_STARTING_URL));

        final ToolBar startBrowserToolbar = new ToolBar(toolbarRightSideComposite, SWT.FLAT | SWT.RIGHT);
        startBrowserToolbar.setLayout(new FillLayout(SWT.HORIZONTAL));
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(false, false).applyTo(startBrowserToolbar);
        startBrowser = new ToolItem(startBrowserToolbar, SWT.DROP_DOWN);
        startBrowser.setText(StringConstants.DIA_BTN_START_BROWSER);
        startBrowser.setImage(getWebUIDriverToolItemImage(getWebUIDriver()));
        Dropdown dropdown = new Dropdown(getShell());
        createDropdownContent(dropdown);

        startBrowser.addSelectionListener(new DropdownItemSelectionListener(dropdown) {

            @Override
            public void itemSelected(SelectionEvent event) {
                startBrowser();
            }
        });
    }

    private void createDropdownContent(Dropdown dropdown) {
        DropdownGroup newBrowser = dropdown.addDropdownGroupItem(StringConstants.MENU_ITEM_NEW_BROWSERS,
                ImageConstants.IMG_16_NEW_BROWSER);
        addNewBrowserItem(newBrowser, WebUIDriverType.FIREFOX_DRIVER);
        addNewBrowserItem(newBrowser, WebUIDriverType.CHROME_DRIVER);

        DropdownGroup activeBrowser = dropdown.addDropdownGroupItem(StringConstants.MENU_ITEM_ACTIVE_BROWSERS,
                ImageConstants.IMG_16_ACTIVE_BROWSER);
        addActiveBrowserItem(activeBrowser, WebUIDriverType.CHROME_DRIVER);

        if (Platform.OS_WIN32.equals(Platform.getOS())) {
            addNewBrowserItem(newBrowser, WebUIDriverType.IE_DRIVER);
        }
    }

    private void addNewBrowserItem(DropdownGroup newBrowserGroup, WebUIDriverType webUIDriverType) {
        newBrowserGroup.addItem(webUIDriverType.toString(), getWebUIDriverDropdownImage(webUIDriverType),
                new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        isInstant = false;
                        defaultBrowser = webUIDriverType;
                        startBrowser.setImage(getWebUIDriverToolItemImage(webUIDriverType));
                        startBrowser();
                    }
                });
    }

    private void addActiveBrowserItem(DropdownGroup activeBrowserGroup, WebUIDriverType webUIDriverType) {
        activeBrowserGroup.addItem(webUIDriverType.toString(), getWebUIDriverDropdownImage(webUIDriverType),
                new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        try {
                            if (!UtilitiesAddonUtil.isNotShowingInstantBrowserDialog() && !showInstantBrowserDialog()) {
                                return;
                            }
                            endInspectSession();
                            defaultBrowser = webUIDriverType;
                            startBrowser.setImage(getWebUIDriverToolItemImage(webUIDriverType));
                            isInstant = true;
                            startBrowser();
                        } catch (Exception exception) {
                            LoggerSingleton.logError(exception);
                        }
                    }

                    protected void showMessageForStartingInstantIE() {
                        UISynchronizeService.syncExec(new Runnable() {
                            @Override
                            public void run() {
                                MessageDialogWithToggle messageDialogWithToggle = MessageDialogWithToggle
                                        .openInformation(Display.getCurrent().getActiveShell(),
                                                StringConstants.HAND_ACTIVE_BROWSERS_DIA_TITLE,
                                                StringConstants.DIALOG_RUNNING_INSTANT_IE_MESSAGE,
                                                StringConstants.HAND_INSTANT_BROWSERS_DIA_TOOGLE_MESSAGE, false, null,
                                                null);
                                UtilitiesAddonUtil
                                        .setNotShowingInstantBrowserDialog(messageDialogWithToggle.getToggleState());
                            }
                        });
                    }

                    private boolean showInstantBrowserDialog() throws IOException, URISyntaxException {
                        if (webUIDriverType == WebUIDriverType.IE_DRIVER) {
                            showMessageForStartingInstantIE();
                            return true;
                        }
                        MessageDialogWithToggle messageDialogWithToggle = new GoToAddonStoreMessageDialog(
                                getParentShell(), StringConstants.HAND_ACTIVE_BROWSERS_DIA_TITLE,
                                MessageFormat.format(StringConstants.HAND_ACTIVE_BROWSERS_DIA_MESSAGE,
                                        webUIDriverType.toString()),
                                StringConstants.HAND_INSTANT_BROWSERS_DIA_TOOGLE_MESSAGE);
                        int returnCode = messageDialogWithToggle.open();
                        UtilitiesAddonUtil.setNotShowingInstantBrowserDialog(messageDialogWithToggle.getToggleState());
                        if (returnCode == IDialogConstants.NO_ID) {
                            return true;
                        }
                        if (returnCode != IDialogConstants.YES_ID) {
                            return false;
                        }
                        openBrowserToAddonUrl();
                        return true;
                    }

                    private void openBrowserToAddonUrl() throws IOException, URISyntaxException {
                        String url = getAddonUrl();
                        if (url == null || !Desktop.isDesktopSupported()) {
                            return;
                        }
                        Desktop.getDesktop().browse(new URI(url));
                    }

                    private String getAddonUrl() {
                        if (webUIDriverType == WebUIDriverType.CHROME_DRIVER) {
                            return OBJECT_SPY_CHROME_ADDON_URL;
                        }
                        if (webUIDriverType == WebUIDriverType.FIREFOX_DRIVER) {
                            return OBJECT_SPY_FIREFOX_ADDON_URL;
                        }
                        return null;
                    }
                });
    }

    private WebUIDriverType getWebUIDriver() {
        return WebUIDriverType.fromStringValue(
                getPreferenceStore().getString(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_DEFAULT_BROWSER));
    }

    private ScopedPreferenceStore getPreferenceStore() {
        return PreferenceStoreManager.getPreferenceStore(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_QUALIFIER);
    }

    private void createRightPanel(Composite htmlDomComposite) {
        Label lblHtmlDom = new Label(htmlDomComposite, SWT.NONE);
        lblHtmlDom.setFont(getFontBold(lblHtmlDom));
        lblHtmlDom.setText(StringConstants.DIA_LBL_HTML_DOM);

        Composite searchComposite = new Composite(htmlDomComposite, SWT.BORDER);
        searchComposite.setBackground(ColorUtil.getWhiteBackgroundColor());
        GridLayout gl_searchComposite = new GridLayout(6, false);
        gl_searchComposite.verticalSpacing = 0;
        gl_searchComposite.horizontalSpacing = 0;
        gl_searchComposite.marginWidth = 0;
        gl_searchComposite.marginHeight = 0;
        searchComposite.setLayout(gl_searchComposite);
        GridData gd_searchComposite = new GridData(GridData.FILL_HORIZONTAL);
        gd_searchComposite.heightHint = 24;
        searchComposite.setLayoutData(gd_searchComposite);

        txtXpathInput = new Text(searchComposite, SWT.NONE);
        txtXpathInput.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        txtXpathInput.setMessage(StringConstants.DIA_TXT_SEARCH_PLACE_HOLDER);
        GridData gd_txtXpathInput = new GridData(GridData.FILL_HORIZONTAL);
        gd_txtXpathInput.grabExcessVerticalSpace = true;
        gd_txtXpathInput.verticalAlignment = SWT.CENTER;
        txtXpathInput.setLayoutData(gd_txtXpathInput);
        txtXpathInput.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    if (txtXpathInput == null || txtXpathInput.isDisposed()) {
                        return;
                    }
                    if (currentHTMLDocument == null) {
                        MessageDialog.openWarning(Display.getCurrent().getActiveShell(), StringConstants.WARN,
                                StringConstants.WARNING_NO_PAGE_LOADED);
                        return;
                    }
                    setDomTreeXpath(txtXpathInput.getText());
                }
            }
        });

        domTreeViewer = new TreeViewer(htmlDomComposite, SWT.BORDER | SWT.MULTI);
        Tree tree = domTreeViewer.getTree();
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));

        domTreeViewer.setContentProvider(new HTMLRawElementTreeContentProvider());
        domTreeViewerLabelProvider = new HTMLRawElementLabelProvider();
        domTreeViewer.setLabelProvider(domTreeViewerLabelProvider);
        domTreeViewerFilter = new HTMLRawElementTreeViewerFilter();
        domTreeViewer.setFilters(new ViewerFilter[] { domTreeViewerFilter });
    }

    private void createLeftPanel(Composite parent) {
        capturedObjectComposite = new CapturedHTMLElementsComposite(parent, SWT.NONE);
        capturedObjectComposite.getElementTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (event.getSelection() instanceof TreeSelection) {
                    TreeSelection treeSelection = (TreeSelection) event.getSelection();
                    Object selectedObject = treeSelection.getFirstElement();
                    enableToolItem(removeElementToolItem, selectedObject instanceof HTMLElement);
                    enableToolItem(addFrameElementToolItem, selectedObject instanceof HTMLElement);
                    enableToolItem(addElementToolItem, selectedObject instanceof HTMLElement);
                    enableToolItem(highlightObjectToolItem, selectedObject instanceof HTMLElement
                            && ((HTMLElement) selectedObject).getParentPageElement() != null);
                }
            }
        });

        addContextMenuForElementTree(capturedObjectComposite.getElementTreeViewer());
        capturedObjectComposite.getElementTreeViewer().setInput(elements);
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        return parent;
    }

    protected void addContextMenuForElementTree(final TreeViewer elementTreeViewer) {
        elementTreeViewer.getTree().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent event) {
                if (event.button == 3) {
                    Menu menu = elementTreeViewer.getTree().getMenu();
                    if (menu != null) {
                        menu.dispose();
                    }
                    final TreeItem treeItem = elementTreeViewer.getTree().getItem(new Point(event.x, event.y));
                    if (treeItem == null) {
                        return;
                    } else {
                        menu = new Menu(elementTreeViewer.getTree());
                    }

                    MenuItem verifyMenuItem = new MenuItem(menu, SWT.PUSH);
                    verifyMenuItem.setText(StringConstants.DIA_MENU_CONTEXT_VERIFY);
                    verifyMenuItem.addSelectionListener(new SelectionListener() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            if (elementTreeViewer.getSelection() instanceof StructuredSelection) {
                                StructuredSelection selection = (StructuredSelection) elementTreeViewer.getSelection();
                                verifyElementExistsInDomTree(selection.toList());
                            }
                        }

                        @Override
                        public void widgetDefaultSelected(SelectionEvent e) {
                        }
                    });
                    elementTreeViewer.getTree().setMenu(menu);
                }
            }
        });
    }

    private void verifyElementExistsInDomTree(List<?> elements) {
        boolean isFirst = true;
        StringBuilder xpathQueryString = new StringBuilder();
        for (Object object : elements) {
            if (object instanceof HTMLElement) {
                HTMLElement element = (HTMLElement) object;
                MatchedStatus matchedStatus = element.getMatchedStatus();
                matchedStatus.reset();
                String elementXpath = HTMLElementUtil.buildXpathForHTMLElement(element);
                try {
                    NodeList nodeList = evaluateXpath(elementXpath);
                    if (nodeList == null) {
                        matchedStatus.setStatus(HTMLStatus.Invalid);
                        continue;
                    }
                    if (nodeList.getLength() <= 0) {
                        matchedStatus.setStatus(HTMLStatus.Missing);
                    } else if (nodeList.getLength() == 1) {
                        Element matchedItem = (Element) nodeList.item(0);
                        try {
                            DOMUtils.compareNodeAttributes(element, matchedItem);
                            matchedStatus.setStatus(HTMLStatus.Exists);
                        } catch (DOMException exception) {
                            matchedStatus.setStatus(HTMLStatus.Changed);
                        }
                        matchedStatus.setMatchedElement(matchedItem);
                    } else {
                        matchedStatus.setStatus(HTMLStatus.Multiple);
                    }
                    if (!isFirst) {
                        xpathQueryString.append(" | "); //$NON-NLS-1$
                    }
                    xpathQueryString.append(elementXpath);
                    isFirst = false;
                } catch (XPathExpressionException e) {
                    matchedStatus.setStatus(HTMLStatus.Invalid);
                }
            }
        }
        txtXpathInput.setText(xpathQueryString.toString());
        setDomTreeXpath(xpathQueryString.toString());
        refreshTree(capturedObjectComposite.getElementTreeViewer(), null);
        capturedObjectComposite.refreshAttributesTable();
    }

    private NodeList evaluateXpath(final String xpath) throws XPathExpressionException {
        if (currentHTMLDocument != null) {
            XPathExpression xPathExpression = XPathFactory.newInstance().newXPath().compile(xpath);
            return (NodeList) xPathExpression.evaluate(currentHTMLDocument.getDocumentElement(),
                    XPathConstants.NODESET);
        }
        return null;
    }

    private void setDomTreeXpath(final String xpath) {
        if (xpath.isEmpty()) {
            refreshDomTree();
        } else {
            Job job = new Job(StringConstants.DIA_FINDING_ELEMENTS) {
                @Override
                protected IStatus run(final IProgressMonitor monitor) {
                    try {
                        NodeList nodeList = evaluateXpath(xpath);
                        monitor.beginTask(MessageFormat.format(StringConstants.DIA_FINDING_ELEMENTS_W_XPATH, xpath),
                                nodeList.getLength());
                        final List<DomElementXpath> selectedDOMElementXpaths = new ArrayList<DomElementXpath>();
                        for (int i = 0; i < nodeList.getLength(); i++) {
                            selectedDOMElementXpaths.add(DOMUtils.getDOMElementXpathForNode(nodeList.item(i)));
                        }
                        if (selectedDOMElementXpaths.isEmpty()) {
                            return Status.CANCEL_STATUS;
                        }
                        Display.getDefault().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                domTreeViewer.getControl().setRedraw(false);
                                domTreeViewer.collapseAll();
                                domTreeViewerLabelProvider.setFilteredElements(selectedDOMElementXpaths);
                                refreshTree(domTreeViewer, null);
                            }
                        });
                        for (final DomElementXpath selectedDOMElementXpath : selectedDOMElementXpaths) {
                            if (monitor.isCanceled()) {
                                throw new OperationCanceledException();
                            }
                            Display.getDefault().syncExec(new Runnable() {
                                @Override
                                public void run() {
                                    domTreeViewer
                                            .expandToLevel(new TreePath(selectedDOMElementXpath.getXpathTreePath()), 1);
                                    monitor.worked(1);
                                }
                            });
                        }
                        Display.getDefault().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                showFirstFoundDomElement(selectedDOMElementXpaths);
                            }
                        });
                        return Status.OK_STATUS;

                    } catch (final XPathExpressionException exception) {
                        Display.getDefault().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                MultiStatusErrorDialog.showErrorDialog(exception,
                                        StringConstants.DIA_ERROR_MSG_CANNOT_PARSE_XPATH,
                                        MessageFormat.format(StringConstants.DIA_ERROR_REASON_INVALID_XPATH,
                                                exception.getCause().getMessage()));
                            }
                        });
                        return Status.CANCEL_STATUS;

                    } catch (OperationCanceledException e) {
                        refreshDomTree();
                        return Status.CANCEL_STATUS;
                    } catch (final Exception e) {
                        LoggerSingleton.logError(e);
                        return Status.CANCEL_STATUS;
                    } finally {
                        Display.getDefault().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                domTreeViewer.getControl().setRedraw(true);
                            }
                        });
                        monitor.done();
                    }
                }
            };
            job.setUser(true);
            job.schedule();
        }
    }

    private void showFirstFoundDomElement(final List<DomElementXpath> selectedDOMElementXpaths) {
        if (selectedDOMElementXpaths == null || selectedDOMElementXpaths.isEmpty()) {
            return;
        }

        Tree tree = domTreeViewer.getTree();
        TreeItem foundItem = findTreeItem(selectedDOMElementXpaths.get(0), tree.getItems(), 0);
        if (foundItem != null) {
            tree.setTopItem(foundItem);
        }
    }

    private TreeItem findTreeItem(DomElementXpath neededToBeRevealed, TreeItem[] items, int segment) {
        String[] xpathTreePath = neededToBeRevealed.getXpathTreePath();
        if (xpathTreePath == null || items == null) {
            return null;
        }

        for (TreeItem item : items) {
            HTMLRawElement rawElement = (HTMLRawElement) item.getData();

            if (!xpathTreePath[segment].equals(rawElement.getAbsoluteXpath())) {
                continue;
            }

            if (segment != xpathTreePath.length - 1) {
                return findTreeItem(neededToBeRevealed, item.getItems(), segment + 1);
            }

            String selectedXpath = neededToBeRevealed.getXpath();
            if (selectedXpath == null) {
                return item;
            }

            for (TreeItem childItem : item.getItems()) {
                HTMLRawElement rawChildElement = (HTMLRawElement) childItem.getData();
                if (selectedXpath.equals(rawChildElement.getAbsoluteXpath())) {
                    return childItem;
                }
            }
            return item;
        }
        return null;
    }

    private void refreshDomTree() {
        Job job = new Job(StringConstants.DIA_REFRESHING_DOM_EXPLORER) {
            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                try {
                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            txtXpathInput.setText(""); //$NON-NLS-1$
                            domTreeViewerLabelProvider.setFilteredElements(null);
                            refreshTree(domTreeViewer, null);
                            domTreeViewer.collapseAll();
                        }
                    });
                    return Status.OK_STATUS;
                } catch (final Exception e) {
                    LoggerSingleton.logError(e);
                    return Status.CANCEL_STATUS;
                } finally {
                    monitor.done();
                }
            }
        };
        job.setUser(true);
        job.schedule();
    }

    private void refreshTree(TreeViewer treeViewer, Object object) {
        treeViewer.getControl().setRedraw(false);
        Object[] expandedElements = treeViewer.getExpandedElements();
        if (object == null) {
            treeViewer.refresh();
        } else {
            treeViewer.refresh(object);
        }
        for (Object element : expandedElements) {
            treeViewer.setExpandedState(element, true);
        }
        treeViewer.getControl().setRedraw(true);
    }

    private void setSelectedTreeItem(Object object) {
        HTMLElementTreeContentProvider dataProvider = (HTMLElementTreeContentProvider) capturedObjectComposite
                .getElementTreeViewer().getContentProvider();
        capturedObjectComposite.getElementTreeViewer().setSelection(new TreeSelection(dataProvider.getTreePath(object)),
                true);
        capturedObjectComposite.getElementTreeViewer().setExpandedState(object, true);
    }

    private HTMLFrameElement getParentElement(ITreeSelection selection) {
        HTMLElement element = (HTMLElement) selection.getFirstElement();
        HTMLFrameElement parentElement = null;
        if (element instanceof HTMLFrameElement) {
            parentElement = (HTMLFrameElement) element;
        } else if (element.getParentElement() != null) {
            parentElement = element.getParentElement();
        }
        return parentElement;
    }

    private void addElementTreeToolbar(Composite explorerComposite) {
        mainToolbar = new ToolBar(explorerComposite, SWT.FLAT | SWT.RIGHT);
        mainToolbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));

        addPageElementToolItem = new ToolItem(mainToolbar, SWT.NONE);
        addPageElementToolItem.setImage(ImageConstants.IMG_24_NEW_PAGE_ELEMENT);
        addPageElementToolItem.setDisabledImage(ImageConstants.IMG_24_NEW_PAGE_ELEMENT_DISABLED);
        addPageElementToolItem.setText(StringConstants.DIA_TOOLITEM_NEW_PAGE);
        addPageElementToolItem.setToolTipText(StringConstants.DIA_TOOLITEM_TIP_NEW_PAGE_ELEMENT);
        addPageElementToolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (capturedObjectComposite.getElementTreeViewer().getSelection() instanceof ITreeSelection) {
                    HTMLPageElement newPageElement = HTMLElementUtil.generateNewPageElement();
                    elements.add(newPageElement);
                    capturedObjectComposite.refreshElementTree(null);
                    setSelectedTreeItem(newPageElement);
                }
            }
        });

        addFrameElementToolItem = new ToolItem(mainToolbar, SWT.NONE);
        addFrameElementToolItem.setImage(ImageConstants.IMG_24_NEW_FRAME_ELEMENT);
        addFrameElementToolItem.setDisabledImage(ImageConstants.IMG_24_NEW_FRAME_ELEMENT_DISABLED);
        addFrameElementToolItem.setText(StringConstants.DIA_TOOLITE_NEW_FRAME);
        addFrameElementToolItem.setToolTipText(StringConstants.DIA_TOOLITEM_TIP_NEW_FRAME_ELEMENT);
        addFrameElementToolItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (capturedObjectComposite.getElementTreeViewer().getSelection() instanceof ITreeSelection) {
                    ITreeSelection selection = (ITreeSelection) capturedObjectComposite.getElementTreeViewer()
                            .getSelection();
                    if (selection.getFirstElement() instanceof HTMLElement) {
                        HTMLFrameElement parentElement = getParentElement(selection);
                        if (parentElement != null) {
                            HTMLFrameElement newFrameElement = HTMLElementUtil.generateNewFrameElement(parentElement);
                            capturedObjectComposite.refreshElementTree(parentElement);
                            setSelectedTreeItem(newFrameElement);
                        }
                    }
                }
            }
        });
        addFrameElementToolItem.setEnabled(false);

        addElementToolItem = new ToolItem(mainToolbar, SWT.NONE);
        addElementToolItem.setImage(ImageConstants.IMG_24_NEW_ELEMENT);
        addElementToolItem.setDisabledImage(ImageConstants.IMG_24_NEW_ELEMENT_DISABLED);
        addElementToolItem.setText(StringConstants.DIA_TOOLITEM_NEW_OBJECT);
        addElementToolItem.setToolTipText(StringConstants.DIA_TOOLITEM_TIP_NEW_ELEMENT);
        addElementToolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (capturedObjectComposite.getElementTreeViewer().getSelection() instanceof ITreeSelection) {
                    ITreeSelection selection = (ITreeSelection) capturedObjectComposite.getElementTreeViewer()
                            .getSelection();
                    if (selection.getFirstElement() instanceof HTMLElement) {
                        HTMLFrameElement parentElement = getParentElement(selection);
                        if (parentElement != null) {
                            HTMLElement newElement = HTMLElementUtil.generateNewElement(parentElement);
                            capturedObjectComposite.refreshElementTree(parentElement);
                            setSelectedTreeItem(newElement);
                        }
                    }
                }
            }
        });
        addElementToolItem.setEnabled(false);

        new ToolItem(mainToolbar, SWT.SEPARATOR);

        removeElementToolItem = new ToolItem(mainToolbar, SWT.NONE);
        removeElementToolItem.setImage(ImageConstants.IMG_24_DELETE);
        removeElementToolItem.setDisabledImage(ImageConstants.IMG_24_DELETE_DISABLED);
        removeElementToolItem.setText(StringConstants.DIA_TOOLITEM_DELETE);
        removeElementToolItem.setToolTipText(StringConstants.DIA_TOOLITEM_TIP_REMOVE_ELEMENT);
        removeElementToolItem.setEnabled(false);
        removeElementToolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (capturedObjectComposite.getElementTreeViewer().getSelection() instanceof ITreeSelection) {
                    ITreeSelection selection = (ITreeSelection) capturedObjectComposite.getElementTreeViewer()
                            .getSelection();
                    for (TreePath treePath : selection.getPaths()) {
                        if (treePath.getLastSegment() instanceof HTMLElement) {
                            HTMLElement element = (HTMLElement) treePath.getLastSegment();
                            HTMLFrameElement frameElement = element.getParentElement();
                            if (frameElement != null) {
                                frameElement.getChildElements().remove(element);
                                capturedObjectComposite.refreshElementTree(frameElement);
                            } else if (element instanceof HTMLPageElement) {
                                elements.remove(element);
                                capturedObjectComposite.refreshElementTree(null);
                            }

                            if (element.equals(capturedObjectComposite.getSelectedElement())) {
                                capturedObjectComposite.refreshAttributesTable(null);
                            }
                        }
                    }
                }
            }
        });

        new ToolItem(mainToolbar, SWT.SEPARATOR);

        addElmtToObjRepoToolItem = new ToolItem(mainToolbar, SWT.NONE);
        addElmtToObjRepoToolItem.setImage(ImageConstants.IMG_24_ADD_TO_OBJECT_REPOSITORY);
        addElmtToObjRepoToolItem.setDisabledImage(ImageConstants.IMG_24_ADD_TO_OBJECT_REPOSITORY_DISABLED);
        addElmtToObjRepoToolItem.setText(StringConstants.DIA_TOOLITEM_TIP_ADD_ELEMENT_TO_OBJECT_REPO);
        addElmtToObjRepoToolItem.setToolTipText(StringConstants.DIA_TOOLITEM_TIP_ADD_ELEMENT_TO_OBJECT_REPO);
        addElmtToObjRepoToolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    addElementToObjectRepository();
                } catch (Exception exception) {
                    logger.error(exception);
                    MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, exception.getMessage());
                }
            }
        });

        new ToolItem(mainToolbar, SWT.SEPARATOR);

        highlightObjectToolItem = new ToolItem(mainToolbar, SWT.NONE);
        highlightObjectToolItem.setText(StringConstants.DIA_TOOLITEM_HIGHLIGHT);
        highlightObjectToolItem.setToolTipText(StringConstants.DIA_TOOLITEM_TIP_HIGHLIGHT);
        highlightObjectToolItem.setImage(ImageConstants.IMG_24_HIGHLIGHT);
        highlightObjectToolItem.setDisabledImage(ImageConstants.IMG_24_HIGHLIGHT_DISABLED);
        highlightObjectToolItem.setEnabled(false);

        highlightObjectToolItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    if (capturedObjectComposite.getElementTreeViewer().getSelection() instanceof ITreeSelection) {
                        ITreeSelection selection = (ITreeSelection) capturedObjectComposite.getElementTreeViewer()
                                .getSelection();
                        if (selection.getPaths().length != 1) {
                            return;
                        }
                        HTMLElement element = (HTMLElement) selection.getPaths()[0].getLastSegment();
                        highlighObject(element);
                    }

                } catch (Exception exception) {
                    logger.error(exception);
                    MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, exception.getMessage());
                }
            }
        });

    }

    private void addElementToObjectRepository() throws Exception {
        AddToObjectRepositoryDialog addToObjectRepositoryDialog = new AddToObjectRepositoryDialog(getParentShell(),
                true, elements, capturedObjectComposite.getElementTreeViewer().getExpandedElements());
        if (addToObjectRepositoryDialog.open() != Window.OK) {
            return;
        }

        Set<ITreeEntity> newSelectionOnExplorer = new HashSet<>();
        AddToObjectRepositoryDialogResult folderSelectionResult = addToObjectRepositoryDialog.getDialogResult();
        for (HTMLPageElement pageElement : addToObjectRepositoryDialog.getHtmlElements()) {
            FolderTreeEntity pageElementTreeFolder = folderSelectionResult.createTreeFolderForPageElement(pageElement);
            newSelectionOnExplorer.add(pageElementTreeFolder);
            for (HTMLElement childElement : pageElement.getChildElements()) {
                newSelectionOnExplorer.addAll(addCheckedElements(childElement, pageElementTreeFolder, null));
            }
        }

        // Refresh tree explorer
        eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, folderSelectionResult.getSelectedParentFolder());
        eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEMS, newSelectionOnExplorer.toArray());
    }

    private Collection<ITreeEntity> addCheckedElements(HTMLElement element, FolderTreeEntity parentTreeFolder,
            WebElementEntity refElement) throws Exception {
        FolderEntity parentFolder = (FolderEntity) parentTreeFolder.getObject();
        WebElementEntity importedElement = ObjectRepositoryController.getInstance().importWebElement(
                HTMLElementUtil.convertElementToWebElementEntity(element, refElement, parentFolder), parentFolder);

        List<ITreeEntity> newTreeWebElements = new ArrayList<>();
        newTreeWebElements.add(new WebElementTreeEntity(importedElement, parentTreeFolder));
        if (element instanceof HTMLFrameElement) {
            for (HTMLElement childElement : ((HTMLFrameElement) element).getChildElements()) {
                newTreeWebElements.addAll(addCheckedElements(childElement, parentTreeFolder, importedElement));
            }
        }
        return newTreeWebElements;
    }

    @Override
    protected void handleShellCloseEvent() {
        super.handleShellCloseEvent();
        stop();
    }

    public void stop() {
        if (server != null && server.isRunning()) {
            try {
                server.stop();
            } catch (Exception e) {
                logger.error(e);
            }
        }
        endInspectSession();
        isDisposed = true;
        eventBroker.unsubscribe(this);
        AddonSocketServer.getInstance().stop();
    }

    private void endInspectSession() {
        if (session != null && session.isRunning()) {
            session.stop();
        }
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(1280, 720);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setMinimumSize(getInitialSize());
        shell.setText(StringConstants.DIA_TITLE_OBJ_SPY);
    }

    public void setHTMLDOMDocument(final HTMLRawElement bodyElement, Document document) {
        currentHTMLDocument = document;
        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                setTreeInput(domTreeViewer, new HTMLRawElement[] { bodyElement });
                refreshDomTree();
            }
        });
    }

    public void addObjectsFromObjectRepository(Object[] selectedObjects) {
        HTMLPageElement generatingPageElement = null;
        Map<String, HTMLElement> generatingElementMap = new HashMap<String, HTMLElement>();
        for (Object selectedObject : selectedObjects) {
            if (selectedObject instanceof WebElementEntity) {
                if (generatingPageElement == null) {
                    generatingPageElement = HTMLElementUtil.generateNewPageElement();
                    elements.add(generatingPageElement);
                }
                HTMLElementUtil.createHTMLElementFromWebElement((WebElementEntity) selectedObject, false,
                        generatingPageElement, generatingElementMap);
                continue;
            }
            if (selectedObject instanceof FolderEntity) {
                elements.addAll(HTMLElementUtil.createHTMLElementFromFolder((FolderEntity) selectedObject,
                        generatingElementMap));
            }
        }
        TreeViewer elementTreeViewer = capturedObjectComposite.getElementTreeViewer();
        refreshTree(elementTreeViewer, null);
        elementTreeViewer.setSelection(new StructuredSelection(generatingElementMap.values().toArray()));
    }

    public void addNewElement(HTMLElement newElement) {
        if (newElement == null || newElement.getParentPageElement() == null) {
            return;
        }
        HTMLPageElement parentPageElement = newElement.getParentPageElement();
        if (elements.contains(parentPageElement)) {
            addNewElement(elements.get(elements.indexOf(parentPageElement)),
                    parentPageElement.getChildElements().get(0), parentPageElement);
        } else {
            elements.add(parentPageElement);
        }
        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                TreeViewer capturedElementTreeViewer = capturedObjectComposite.getElementTreeViewer();
                refreshTree(capturedElementTreeViewer, null);
                setExpandForParentElement(parentPageElement, capturedElementTreeViewer);
            }
        });
    }

    private void setExpandForParentElement(HTMLFrameElement pageElement, TreeViewer capturedElementTreeViewer) {
        List<HTMLElement> childElements = pageElement.getChildElements();
        if (childElements.isEmpty()) {
            return;
        }
        capturedElementTreeViewer.setExpandedState(pageElement, true);
        for (HTMLElement element : childElements) {
            if (element instanceof HTMLFrameElement) {
                setExpandForParentElement((HTMLFrameElement) element, capturedElementTreeViewer);
            }
        }
    }

    private void addNewElement(HTMLFrameElement parentElement, HTMLElement newElement, HTMLPageElement pageElement) {
        if (parentElement.getChildElements().contains(newElement)) {
            if (newElement instanceof HTMLFrameElement) {
                HTMLFrameElement frameElement = (HTMLFrameElement) newElement;
                HTMLFrameElement existingFrameElement = (HTMLFrameElement) (parentElement.getChildElements()
                        .get(parentElement.getChildElements().indexOf(newElement)));
                addNewElement(existingFrameElement, frameElement.getChildElements().get(0), pageElement);
            }
        } else {
            parentElement.getChildElements().add(newElement);
            newElement.setParentElement(parentElement);
            return;
        }
    }

    private void setTreeInput(TreeViewer treeViewer, Object newInput) {
        if (newInput == null) {
            return;
        }
        treeViewer.getControl().setRedraw(false);
        Object[] expandedElements = treeViewer.getExpandedElements();
        treeViewer.setInput(newInput);
        for (Object element : expandedElements) {
            treeViewer.setExpandedState(element, true);
        }
        treeViewer.getControl().setRedraw(true);
    }

    public boolean isDisposed() {
        return isDisposed;
    }

    private Font getFontBold(Label label) {
        FontDescriptor boldDescriptor = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);
        return boldDescriptor.createFont(label.getDisplay());
    }

    private Image getWebUIDriverDropdownImage(WebUIDriverType webUIDriverType) {
        switch (webUIDriverType) {
            case FIREFOX_DRIVER:
                return ImageConstants.IMG_16_BROWSER_FIREFOX;

            case CHROME_DRIVER:
                return ImageConstants.IMG_16_BROWSER_CHROME;

            case IE_DRIVER:
                return ImageConstants.IMG_16_BROWSER_IE;

            default:
                return null;
        }
    }

    private Image getWebUIDriverToolItemImage(WebUIDriverType webUIDriverType) {
        switch (webUIDriverType) {
            case FIREFOX_DRIVER:
                return ImageConstants.IMG_24_FIREFOX;

            case CHROME_DRIVER:
                return ImageConstants.IMG_24_CHROME;

            case IE_DRIVER:
                return ImageConstants.IMG_24_IE;

            default:
                return null;
        }
    }

    private void startObjectSpy(WebUIDriverType browser, boolean isInstant) {
        if (!BrowserUtil.isBrowserInstalled(browser)) {
            MessageDialog.openError(getShell(), StringConstants.ERROR_TITLE,
                    ObjectspyMessageConstants.DIA_MSG_CANNOT_START_BROWSER);
            return;
        }
        try {
            if (browser == WebUIDriverType.IE_DRIVER) {
                checkIEAddon();
            }
            changeBrowserToolItemName();
            if (isInstant) {
                startInstantSession(browser);
                return;
            }
            startServerWithPort(0);
            startInspectSession(browser);
        } catch (final IEAddonNotInstalledException e) {
            stop();
            showMessageForMissingIEAddon();
            try {
                runIEAddonInstaller();
            } catch (IOException iOException) {
                LoggerSingleton.logError(iOException);
            }
        } catch (Exception ex) {
            logger.error(ex);
            MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, ex.getMessage());
        }
    }

    private void startInstantSession(WebUIDriverType browser) throws Exception {
        if (browser == WebUIDriverType.IE_DRIVER) {
            runInstantIE();
        }
        currentInstantSocket = AddonSocketServer.getInstance().getAddonSocketByBrowserName(defaultBrowser.toString());
        if (currentInstantSocket == null) {
            return;
        }
        Win32Helper.switchFocusToBrowser(browser);
        currentInstantSocket.sendMessage(new StartInspectAddonMessage());
    }

    private void closeInstantSession() {
        if (currentInstantSocket != null && currentInstantSocket.isConnected()) {
            currentInstantSocket.close();
        }
        currentInstantSocket = null;
    }

    protected void runInstantIE() throws Exception {
        session = new InspectSession(server, WebUIDriverType.IE_DRIVER,
                ProjectController.getInstance().getCurrentProject(), logger);
        session.setupIE();
        HWND hwnd = User32.INSTANCE.FindWindow(IE_WINDOW_CLASS, null);
        if (hwnd == null) {
            return;
        }
        shiftFocusToWindow(hwnd);
    }

    private void shiftFocusToWindow(HWND hwnd) {
        User32.INSTANCE.ShowWindow(hwnd, 9);        // SW_RESTORE
        User32.INSTANCE.SetForegroundWindow(hwnd);   // bring to front
    }

    private void startInspectSession(WebUIDriverType browser) throws Exception {
        if (session != null) {
            session.stop();
        }
        session = new InspectSession(server, browser, ProjectController.getInstance().getCurrentProject(), logger,
                txtStartUrl.getText());
        new Thread(session).start();
    }

    private void startBrowser() {
        startObjectSpy(defaultBrowser, isInstant);
    }

    public void startServerWithPort(int port) throws Exception {
        if (server != null && server.isStarted() && isCurrentServerPortUsable(port)) {
            return;
        }
        if (server != null && server.isRunning()) {
            server.stop();
        }
        try {
            closeInstantSession();
            server = new HTMLElementCaptureServer(port, logger, this);
            server.start();
        } catch (BindException e) {
            MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE,
                    MessageFormat.format(ObjectspyMessageConstants.ERR_DLG_OBJECT_SPY_PORT_IN_USE, port));
            server = null;
        }
    }

    public boolean isCurrentServerPortUsable(int port) {
        return port == 0 || port == server.getServerPort();
    }

    private void changeBrowserToolItemName() {
        String string = StringConstants.DIA_BTN_START_BROWSER;
        if (isInstant) {
            string = StringConstants.ACTIVE_BROWSER_PREFIX;
        }
        changeBrowserName(string);
    }

    private void changeBrowserName(final String string) {
        UISynchronizeService.getInstance().getSync().asyncExec(new Runnable() {
            @Override
            public void run() {
                // Set browser name into toolbar item label
                startBrowser.setText(string);
                // reload layout
                startBrowser.getParent().getParent().layout(true, true);
            }
        });
    }

    private void enableToolItem(ToolItem toolItem, boolean isEnabled) {
        toolItem.setEnabled(isEnabled);
    }

    private void checkIEAddon() throws IllegalAccessException, InvocationTargetException, IEAddonNotInstalledException {
        if (checkRegistryKey(IE_WINDOWS_32BIT_BHO_REGISTRY_KEY) || checkRegistryKey(IE_WINDOWS_BHO_REGISTRY_KEY)) {
            return;
        }
        throw new IEAddonNotInstalledException(InspectSession.OBJECT_SPY_ADD_ON_NAME);
    }

    private boolean checkRegistryKey(String parentKey) throws IllegalAccessException, InvocationTargetException {
        List<String> bhos = WinRegistry.readStringSubKeys(WinRegistry.HKEY_LOCAL_MACHINE, parentKey);
        if (bhos == null || bhos.isEmpty()) {
            return false;
        }
        for (String bho : bhos) {
            if (bho.toLowerCase().equals(IE_ADDON_BHO_KEY.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private void showMessageForMissingIEAddon() {
        UISynchronizeService.syncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialog.openInformation(Display.getCurrent().getActiveShell(), StringConstants.INFO,
                        StringConstants.DIALOG_CANNOT_START_IE_MESSAGE);
            }
        });
    }

    private File getResourcesDirectory() throws IOException {
        Bundle bundleExec = Platform.getBundle(IdConstants.KATALON_WEB_UI_OBJECT_SPY_BUNDLE_ID);
        File bundleFile = FileLocator.getBundleFile(bundleExec);
        if (bundleFile.isDirectory()) { // run by IDE
            return new File(bundleFile + File.separator + RESOURCES_FOLDER_NAME);
        }
        // run as product
        return new File(ClassPathResolver.getConfigurationFolder() + File.separator + RESOURCES_FOLDER_NAME);
    }

    private void runIEAddonInstaller() throws IOException {
        String ieAddonSetupPath = getResourcesDirectory().getAbsolutePath() + relativePathToIEAddonSetup;
        Desktop desktop = Desktop.getDesktop();
        if (!Desktop.isDesktopSupported()) {
            return;
        }
        desktop.open(new File(ieAddonSetupPath));
    }

    private void highlighObject(HTMLElement element) {
        if (isInstant) {
            highlightObjectInInstantMode(element);
            return;
        }
        HighlightRequest request = new HighlightRequest(element);

        KatalonRequestHandler.getInstance().setRequest(request, new RequestFailedListener() {
            @Override
            public void requestFailed(final String errorMessage) {
                ObjectSpyDialog.this.getShell().getDisplay().syncExec(new Runnable() {
                    public void run() {
                        MessageDialog.openError(getParentShell(), StringConstants.ERROR, errorMessage);
                    }
                });

            }
        });
    }

    private void highlightObjectInInstantMode(HTMLElement element) {
        if (currentInstantSocket == null) {
            return;
        }
        String xpathExpression = new HighlightRequest(element).getData();
        currentInstantSocket.sendMessage(new AddonMessage(AddonCommand.HIGHLIGHT_OBJECT, xpathExpression));
    }

    @Override
    public int open() {
        if (Platform.OS_WIN32.equals(Platform.getOS())) {
            if (getShell() == null) {
                create();
            }
            Point size = getShell().getSize();
            getShell().setSize(size.x + 65, size.y);
            getShell().setMinimumSize(size.x + 65, size.y);
        }
        return super.open();
    }

    @Override
    public boolean close() {
        getPreferenceStore().setValue(ObjectSpyPreferenceConstants.WEBUI_OBJECTSPY_DEFAULT_STARTING_URL,
                txtStartUrl.getText());
        return super.close();
    }

    @Override
    public void handleEvent(Event event) {
        Object dataObject = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
        switch (event.getTopic()) {
            case EventConstants.OBJECT_SPY_HTML_ELEMENT_CAPTURED:
                if (!(dataObject instanceof HTMLElement)) {
                    return;
                }
                addNewElement((HTMLElement) dataObject);
                return;
            case EventConstants.OBJECT_SPY_HTML_DOM_CAPTURED:
                if (!(dataObject instanceof Object[])) {
                    return;
                }
                Object[] dataObjects = (Object[]) dataObject;
                if (dataObjects.length != 2 || !(dataObjects[0] instanceof HTMLRawElement)
                        || !(dataObjects[1] instanceof Document)) {
                    return;
                }
                setHTMLDOMDocument((HTMLRawElement) dataObjects[0], (Document) dataObjects[1]);
                return;
        }
    }

}
