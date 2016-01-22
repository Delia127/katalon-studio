package com.kms.katalon.objectspy.dialog;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
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
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;
import com.kms.katalon.objectspy.components.CapturedHTMLElementsComposite;
import com.kms.katalon.objectspy.constants.ImageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.core.HTMLElementCaptureServer;
import com.kms.katalon.objectspy.core.InspectSession;
import com.kms.katalon.objectspy.element.DomElementXpath;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLElement.HTMLStatus;
import com.kms.katalon.objectspy.element.HTMLFrameElement;
import com.kms.katalon.objectspy.element.HTMLPageElement;
import com.kms.katalon.objectspy.element.HTMLRawElement;
import com.kms.katalon.objectspy.element.tree.HTMLElementTreeContentProvider;
import com.kms.katalon.objectspy.element.tree.HTMLRawElementLabelProvider;
import com.kms.katalon.objectspy.element.tree.HTMLRawElementTreeContentProvider;
import com.kms.katalon.objectspy.element.tree.HTMLRawElementTreeViewerFilter;
import com.kms.katalon.objectspy.exception.DOMException;
import com.kms.katalon.objectspy.exception.IEAddonNotInstalledException;
import com.kms.katalon.objectspy.util.DOMUtils;
import com.kms.katalon.objectspy.util.HTMLElementUtil;

@SuppressWarnings("restriction")
public class ObjectSpyDialog extends Dialog implements EventHandler {
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

    private IEventBroker eventBroker;

    private boolean isDisposed;

    private TreeViewer domTreeViewer;

    private HTMLRawElementLabelProvider domTreeViewerLabelProvider;

    private HTMLRawElementTreeViewerFilter domTreeViewerFilter;

    private Document currentHTMLDocument;

    private Text txtXpathInput;

    private WebUIDriverType defaultBrowser = WebUIDriverType.FIREFOX_DRIVER;

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
        registerEventHandler();
        isDisposed = false;
        elements = new ArrayList<HTMLPageElement>();
        server = new HTMLElementCaptureServer(logger, eventBroker);
        server.start();
    }

    protected void registerEventHandler() {
        eventBroker.subscribe(EventConstants.OBJECT_SPY_ELEMENT_ADDED, this);
        eventBroker.subscribe(EventConstants.OBJECT_SPY_ELEMENT_DOM_MAP_ADDED, this);
        eventBroker.subscribe(EventConstants.OBJECT_SPY_TEST_OBJECT_ADDED, this);
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

        Composite bodyComposite = (Composite) super.createDialogArea(mainContainer);
        bodyComposite.setLayout(new FillLayout(SWT.VERTICAL));

        SashForm hSashForm = new SashForm(bodyComposite, SWT.NONE);
        hSashForm.setSashWidth(5);

        Composite objectComposite = new Composite(hSashForm, SWT.NONE);
        GridLayout gl_objectComposite = new GridLayout();
        gl_objectComposite.marginLeft = 5;
        gl_objectComposite.marginWidth = 0;
        gl_objectComposite.marginBottom = 5;
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

        return mainContainer;
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
                    if (txtXpathInput == null || txtXpathInput.isDisposed() || currentHTMLDocument == null) {
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
                String elementXpath = HTMLElementUtil.buildXpathForHTMLElement(element);
                try {
                    NodeList nodeList = evaluateXpath(elementXpath);
                    if (nodeList == null) {
                        element.setStatus(HTMLStatus.Invalid);
                        continue;
                    }
                    if (nodeList.getLength() <= 0) {
                        element.setStatus(HTMLStatus.Missing);
                    } else if (nodeList.getLength() == 1) {
                        try {
                            DOMUtils.compareNodeAttributes(element, (Element) nodeList.item(0));
                            element.setStatus(HTMLStatus.Exists);
                        } catch (DOMException exception) {
                            element.setStatus(HTMLStatus.Changed);
                        }
                    } else {
                        element.setStatus(HTMLStatus.Multiple);
                    }
                    if (!isFirst) {
                        xpathQueryString.append(" | ");
                    }
                    xpathQueryString.append(elementXpath);
                    isFirst = false;
                } catch (XPathExpressionException e) {
                    element.setStatus(HTMLStatus.Invalid);
                }
            }
        }
        txtXpathInput.setText(xpathQueryString.toString());
        setDomTreeXpath(xpathQueryString.toString());
        refreshTree(capturedObjectComposite.getElementTreeViewer(), null);
    }

    private NodeList evaluateXpath(final String xpath) throws XPathExpressionException {
        if (currentHTMLDocument != null) {
            XPathExpression xPathExpression = XPathFactory.newInstance().newXPath().compile(xpath);
            return (NodeList) xPathExpression
                    .evaluate(currentHTMLDocument.getDocumentElement(), XPathConstants.NODESET);
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
                                    domTreeViewer.expandToLevel(
                                            new TreePath(selectedDOMElementXpath.getXpathTreePath()), 1);
                                    monitor.worked(1);
                                }
                            });
                        }
                        return Status.OK_STATUS;

                    } catch (final XPathExpressionException exception) {
                        Display.getDefault().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                MultiStatusErrorDialog.showErrorDialog(exception,
                                        StringConstants.DIA_ERROR_MSG_CANNOT_PARSE_XPATH, MessageFormat.format(
                                                StringConstants.DIA_ERROR_REASON_INVALID_XPATH, exception.getCause()
                                                        .getMessage()));
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

    private void refreshDomTree() {
        Job job = new Job(StringConstants.DIA_REFRESHING_DOM_EXPLORER) {
            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                try {
                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            txtXpathInput.setText("");
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
        capturedObjectComposite.getElementTreeViewer().setSelection(
                new TreeSelection(dataProvider.getTreePath(object)), true);
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
        mainToolbar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));

        addPageElementToolItem = new ToolItem(mainToolbar, SWT.NONE);
        addPageElementToolItem.setImage(ImageConstants.IMG_24_NEW_PAGE_ELEMENT);
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
        removeElementToolItem.setImage(ImageConstants.IMG_16_DELETE);
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

        final ToolBar startBrowserToolbar = new ToolBar(explorerComposite, SWT.FLAT | SWT.RIGHT);
        startBrowserToolbar.setLayout(new FillLayout(SWT.HORIZONTAL));
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(true, false).applyTo(startBrowserToolbar);

        final ToolItem startBrowser = new ToolItem(startBrowserToolbar, SWT.DROP_DOWN);
        startBrowser.setImage(ImageConstants.IMG_24_OBJECT_SPY);

        SelectionAdapter browserSelectionListener = new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                defaultBrowser = WebUIDriverType.fromStringValue(((MenuItem) e.getSource()).getText());
                changeBrowserName(startBrowser);
                startBrowser();
            }

        };

        final Menu menu = new Menu(getShell(), SWT.POP_UP);

        final MenuItem startFirefox = new MenuItem(menu, SWT.PUSH);
        startFirefox.setImage(ImageConstants.IMG_16_BROWSER_FIREFOX);
        startFirefox.setText(WebUIDriverType.FIREFOX_DRIVER.toString());
        startFirefox.addSelectionListener(browserSelectionListener);

        final MenuItem startChrome = new MenuItem(menu, SWT.PUSH);
        startChrome.setImage(ImageConstants.IMG_16_BROWSER_CHROME);
        startChrome.setText(WebUIDriverType.CHROME_DRIVER.toString());
        startChrome.addSelectionListener(browserSelectionListener);

        if (Platform.getOS().equals(Platform.WS_WIN32)) {
            MenuItem startIE = new MenuItem(menu, SWT.PUSH);
            startIE.setImage(ImageConstants.IMG_16_BROWSER_IE);
            startIE.setText(WebUIDriverType.IE_DRIVER.toString());
            startIE.addSelectionListener(browserSelectionListener);
        }

        startBrowser.setText(StringConstants.DIA_BTN_START_BROWSER);
        startBrowser.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                ToolItem item = (ToolItem) e.widget;
                if (e.detail == SWT.ARROW) {
                    Rectangle bounds = item.getBounds();
                    Point point = item.getParent().toDisplay(bounds.x, bounds.y + bounds.height);
                    menu.setLocation(point);
                    menu.setVisible(true);
                } else {
                    changeBrowserName(item);
                    startBrowser();
                }
            }
        });
    }

    private void addElementToObjectRepository() throws Exception {
        AddToObjectRepositoryDialog addToObjectRepositoryDialog = new AddToObjectRepositoryDialog(getParentShell(),
                true, elements, capturedObjectComposite.getElementTreeViewer().getExpandedElements());
        if (addToObjectRepositoryDialog.open() == Window.OK) {
            FolderTreeEntity targetFolderTreeEntity = (FolderTreeEntity) addToObjectRepositoryDialog.getFirstResult();
            FolderEntity parentFolder = (FolderEntity) (targetFolderTreeEntity).getObject();
            for (HTMLPageElement pageElement : addToObjectRepositoryDialog.getHtmlElements()) {
                for (HTMLElement childElement : pageElement.getChildElements()) {
                    addCheckedElement(childElement, parentFolder, null);
                }
            }
            eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, targetFolderTreeEntity.getParent());
            eventBroker.send(EventConstants.EXPLORER_SET_SELECTED_ITEM, targetFolderTreeEntity);
            eventBroker.send(EventConstants.EXPLORER_EXPAND_TREE_ENTITY, targetFolderTreeEntity);
        }
    }

    private void addCheckedElement(HTMLElement element, FolderEntity parentFolder, WebElementEntity refElement)
            throws Exception {
        WebElementEntity importedElement = ObjectRepositoryController.getInstance().importWebElement(
                HTMLElementUtil.convertElementToWebElementEntity(element, refElement, parentFolder), parentFolder);
        if (element instanceof HTMLFrameElement) {
            for (HTMLElement childElement : ((HTMLFrameElement) element).getChildElements()) {
                addCheckedElement(childElement, parentFolder, importedElement);
            }
        }
    }

    @Override
    protected void handleShellCloseEvent() {
        super.handleShellCloseEvent();
        dispose();
    }

    public void dispose() {
        if (server != null && server.isRunning()) {
            try {
                server.stop();
            } catch (Exception e) {
                logger.error(e);
            }
        }
        if (session != null && session.isRunning()) {
            session.stop();
        }
        eventBroker.unsubscribe(this);
        isDisposed = true;
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(800, 600);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setMinimumSize(getInitialSize());
        shell.setText(StringConstants.DIA_TITLE_OBJ_SPY);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.getTopic().equals(EventConstants.OBJECT_SPY_ELEMENT_ADDED)
                && event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME) instanceof HTMLElement) {
            HTMLElement newElement = (HTMLElement) event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
            addNewElement(newElement);
            capturedObjectComposite.getElementTreeViewer().refresh(null);
        } else if (event.getTopic().equals(EventConstants.OBJECT_SPY_ELEMENT_DOM_MAP_ADDED)
                && event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME) instanceof Object[]) {
            currentHTMLDocument = (Document) ((Object[]) event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME))[0];
            HTMLRawElement bodyElement = (HTMLRawElement) ((Object[]) event
                    .getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME))[1];
            setTreeInput(domTreeViewer, new HTMLRawElement[] { bodyElement });
            refreshDomTree();
        } else if (event.getTopic().equals(EventConstants.OBJECT_SPY_TEST_OBJECT_ADDED)
                && event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME) instanceof Object[]) {
            Object[] selectedObjects = (Object[]) event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
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
                } else if (selectedObject instanceof FolderEntity) {
                    elements.addAll(HTMLElementUtil.createHTMLElementFromFolder((FolderEntity) selectedObject,
                            generatingElementMap));
                }
            }
            capturedObjectComposite.getElementTreeViewer().refresh(null);
        }
    }

    private void addNewElement(HTMLElement newElement) {
        HTMLPageElement parentPageElement = newElement.getParentPageElement();
        if (parentPageElement != null) {
            if (elements.contains(parentPageElement)) {
                addNewElement(elements.get(elements.indexOf(parentPageElement)), parentPageElement.getChildElements()
                        .get(0), parentPageElement);
            } else {
                elements.add(parentPageElement);
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
        if (newInput != null) {
            treeViewer.getControl().setRedraw(false);
            Object[] expandedElements = treeViewer.getExpandedElements();
            treeViewer.setInput(newInput);
            for (Object element : expandedElements) {
                treeViewer.setExpandedState(element, true);
            }
            treeViewer.getControl().setRedraw(true);
        }
    }

    public boolean isDisposed() {
        return isDisposed;
    }

    private Font getFontBold(Label label) {
        FontDescriptor boldDescriptor = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);
        return boldDescriptor.createFont(label.getDisplay());
    }

    private void startBrowser() {
        try {
            if (session != null) {
                session.stop();
            }
            session = new InspectSession(server.getServerUrl(), defaultBrowser, ProjectController.getInstance()
                    .getCurrentProject(), logger);
            new Thread(session).start();
        } catch (IEAddonNotInstalledException ex) {
            MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, ex.getMessage());
        } catch (Exception ex) {
            logger.error(ex);
            MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, ex.getMessage());
        }
    }

    private void changeBrowserName(final ToolItem startBrowser) {
        UISynchronizeService.getInstance().getSync().asyncExec(new Runnable() {

            @Override
            public void run() {
                // Set browser name into toolbar item label
                startBrowser.setText(defaultBrowser.toString());
                // reload layout
                startBrowser.getParent().getParent().layout(true, true);
            }
        });
    }

    private void enableToolItem(ToolItem toolItem, boolean isEnabled) {
        toolItem.setEnabled(isEnabled);
    }

}
