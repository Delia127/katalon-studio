package com.kms.katalon.objectspy.dialog;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
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
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.objectspy.constants.ImageConstants;
import com.kms.katalon.objectspy.constants.StringConstants;
import com.kms.katalon.objectspy.core.HTMLElementCaptureServer;
import com.kms.katalon.objectspy.core.InspectSession;
import com.kms.katalon.objectspy.element.DomElementXpath;
import com.kms.katalon.objectspy.element.HTMLElement;
import com.kms.katalon.objectspy.element.HTMLFrameElement;
import com.kms.katalon.objectspy.element.HTMLPageElement;
import com.kms.katalon.objectspy.element.HTMLRawElement;
import com.kms.katalon.objectspy.element.HTMLElement.HTMLStatus;
import com.kms.katalon.objectspy.element.tree.CheckboxTreeSelectionHelper;
import com.kms.katalon.objectspy.element.tree.HTMLElementLabelProvider;
import com.kms.katalon.objectspy.element.tree.HTMLElementTreeContentProvider;
import com.kms.katalon.objectspy.element.tree.HTMLRawElementLabelProvider;
import com.kms.katalon.objectspy.element.tree.HTMLRawElementTreeContentProvider;
import com.kms.katalon.objectspy.element.tree.HTMLRawElementTreeViewerFilter;
import com.kms.katalon.objectspy.exception.DOMException;
import com.kms.katalon.objectspy.exception.IEAddonNotInstalledException;
import com.kms.katalon.objectspy.util.DOMUtils;
import com.kms.katalon.objectspy.util.HTMLElementUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ObjectRepositoryController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.folder.FolderEntity;
import com.kms.katalon.entity.repository.WebElementEntity;

@SuppressWarnings("restriction")
public class ObjectSpyDialog extends Dialog implements EventHandler {
    private static final String CANCEL_BUTTON_LABEL = StringConstants.DIA_BTN_CLOSE;
    private static final String OK_BUTTON_LABEL = StringConstants.DIA_BTN_ADD;
    private static final String START_BROWSER_BUTTON_LABEL = StringConstants.DIA_BTN_START_BROWSER;
    private static final String DIALOG_TITLE = StringConstants.DIA_TITLE_OBJ_SPY;
    private static final int START_BROWSER_BUTTON_ID = IDialogConstants.CLIENT_ID + 1;
    private List<HTMLPageElement> elements;

    private Text elementNameText;
    private Text elementTypeText;
    private TableViewer attributesTableViewer;
    private CheckboxTreeViewer elementTreeViewer;
    private Logger logger;
    private HTMLElementCaptureServer server;
    private InspectSession session;
    private ToolItem addPageElementToolItem, addFrameElementToolItem, addElementToolItem, removeElementToolItem;
    private IEventBroker eventBroker;
    private HTMLElement selectedElement;

    private Combo browserTypeCombo;
    private FolderEntity parentFolder;

    private boolean isDisposed;
    private TreeViewer domTreeViewer;
    private HTMLRawElementLabelProvider domTreeViewerLabelProvider;
    private HTMLRawElementTreeViewerFilter domTreeViewerFilter;
    private Document currentHTMLDocument;
    private Text txtXpathInput;

    /**
     * Create the dialog.
     * 
     * @param parentShell
     * @param parentFolder
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
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new FillLayout(SWT.HORIZONTAL));

        SashForm mainForm = new SashForm(container, SWT.NONE);

        Composite explorerComposite = new Composite(mainForm, SWT.NONE);
        explorerComposite.setLayout(new GridLayout());

        addElementTreeToolbar(explorerComposite);

        SashForm explorerSashForm = new SashForm(explorerComposite, SWT.NONE);
        explorerSashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        Composite treeComposite = new Composite(explorerSashForm, SWT.NONE);
        treeComposite.setLayout(new GridLayout());

        elementTreeViewer = new CheckboxTreeViewer(treeComposite, SWT.BORDER | SWT.MULTI);
        HTMLElementTreeContentProvider contentProvider = new HTMLElementTreeContentProvider();
        CheckboxTreeSelectionHelper.attach(elementTreeViewer, contentProvider);
        elementTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

        elementTreeViewer.setContentProvider(contentProvider);
        elementTreeViewer.setLabelProvider(new HTMLElementLabelProvider());

        elementTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                if (event.getSelection() instanceof TreeSelection) {
                    TreeSelection treeSelection = (TreeSelection) event.getSelection();
                    if (treeSelection.getFirstElement() instanceof HTMLElement) {
                        selectedElement = (HTMLElement) treeSelection.getFirstElement();
                        refreshAttributesTable(selectedElement);
                    }
                }
            }

        });

        addContextMenuForElementTree();
        ColumnViewerToolTipSupport.enableFor(elementTreeViewer, ToolTip.NO_RECREATE);
        elementTreeViewer.setInput(elements);

        Composite contentComposite = new Composite(explorerSashForm, SWT.NONE);
        contentComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
        GridLayout gl_composite_1 = new GridLayout(1, false);
        gl_composite_1.horizontalSpacing = 10;
        contentComposite.setLayout(gl_composite_1);

        Label nameLabel = new Label(contentComposite, SWT.NONE);
        nameLabel.setText(StringConstants.DIA_LBL_NAME);

        elementNameText = new Text(contentComposite, SWT.BORDER);
        elementNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        elementNameText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (selectedElement != null) {
                    selectedElement.setName(elementNameText.getText());
                    refreshTree(elementTreeViewer, null);
                }
            }
        });

        Label typeLabel = new Label(contentComposite, SWT.NONE);
        typeLabel.setText(StringConstants.DIA_LBL_TYPE);

        elementTypeText = new Text(contentComposite, SWT.BORDER);
        elementTypeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        elementTypeText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (selectedElement != null) {
                    selectedElement.setType(elementTypeText.getText());
                }
            }
        });

        Composite attributesTableComposite = new Composite(contentComposite, SWT.NONE);

        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        attributesTableComposite.setLayout(tableColumnLayout);

        GridData attributesTableCompositeGridData = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
        attributesTableCompositeGridData.heightHint = 10000;
        attributesTableCompositeGridData.widthHint = 10000;
        attributesTableComposite.setLayoutData(attributesTableCompositeGridData);

        attributesTableViewer = new TableViewer(attributesTableComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL
                | SWT.FULL_SELECTION | SWT.BORDER);

        createColumns(attributesTableViewer, tableColumnLayout);

        // make lines and header visible
        final Table table = attributesTableViewer.getTable();

        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        attributesTableViewer.setContentProvider(ArrayContentProvider.getInstance());
        attributesTableViewer.setInput(Collections.emptyList());

        Composite domExplorerComposite = new Composite(mainForm, SWT.NONE);
        domExplorerComposite.setLayout(new GridLayout());

        Composite searchComposite = new Composite(domExplorerComposite, SWT.BORDER);
        searchComposite.setBackground(ColorUtil.getWhiteBackgroundColor());
        GridLayout glSearchComposite = new GridLayout(6, false);
        glSearchComposite.verticalSpacing = 0;
        glSearchComposite.horizontalSpacing = 0;
        glSearchComposite.marginWidth = 0;
        glSearchComposite.marginHeight = 0;
        searchComposite.setLayout(glSearchComposite);
        GridData grSearchComposite = new GridData(GridData.FILL_HORIZONTAL);
        grSearchComposite.heightHint = 24;
        searchComposite.setLayoutData(grSearchComposite);

        txtXpathInput = new Text(searchComposite, SWT.NONE);
        txtXpathInput.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        txtXpathInput.setMessage("");
        GridData gdTxtInput = new GridData(GridData.FILL_HORIZONTAL);
        gdTxtInput.grabExcessVerticalSpace = true;
        gdTxtInput.verticalAlignment = SWT.CENTER;
        txtXpathInput.setLayoutData(gdTxtInput);
        new Label(searchComposite, SWT.NONE);
        new Label(searchComposite, SWT.NONE);
        new Label(searchComposite, SWT.NONE);
        new Label(searchComposite, SWT.NONE);
        new Label(searchComposite, SWT.NONE);
        txtXpathInput.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub
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

        domTreeViewer = new TreeViewer(domExplorerComposite, SWT.BORDER | SWT.MULTI);
        Tree tree = domTreeViewer.getTree();
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));

        domTreeViewer.setContentProvider(new HTMLRawElementTreeContentProvider());
        domTreeViewerLabelProvider = new HTMLRawElementLabelProvider();
        domTreeViewer.setLabelProvider(domTreeViewerLabelProvider);
        domTreeViewerFilter = new HTMLRawElementTreeViewerFilter();
        domTreeViewer.setFilters(new ViewerFilter[] { domTreeViewerFilter });

        explorerSashForm.setOrientation(SWT.VERTICAL);
        explorerSashForm.setWeights(new int[] { 4, 5 });
        mainForm.setWeights(new int[] { 3, 8 });

        return container;
    }

    protected void addContextMenuForElementTree() {
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
                            // TODO Auto-generated method stub

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
        refreshTree(elementTreeViewer, null);
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
                        LoggerSingleton.getInstance().getLogger().error(e);
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
                    LoggerSingleton.getInstance().getLogger().error(e);
                    return Status.CANCEL_STATUS;
                } finally {
                    monitor.done();
                }
            }
        };
        job.setUser(true);
        job.schedule();
    }

    private void setSelectedTreeItem(Object object) {
        HTMLElementTreeContentProvider dataProvider = (HTMLElementTreeContentProvider) elementTreeViewer
                .getContentProvider();
        elementTreeViewer.setSelection(new TreeSelection(dataProvider.getTreePath(object)), true);
        elementTreeViewer.setExpandedState(object, true);
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
        ToolBar elementTreeToolbar = new ToolBar(explorerComposite, SWT.FLAT | SWT.RIGHT);

        addPageElementToolItem = new ToolItem(elementTreeToolbar, SWT.NONE);
        addPageElementToolItem.setImage(ImageConstants.IMG_24_NEW_PAGE_ELEMENT);
        addPageElementToolItem.setToolTipText(StringConstants.DIA_TOOLITEM_TIP_NEW_PAGE_ELEMENT);
        addPageElementToolItem.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (elementTreeViewer.getSelection() instanceof ITreeSelection) {
                    HTMLPageElement newPageElement = HTMLElementUtil.generateNewPageElement();
                    elements.add(newPageElement);
                    refreshTree(elementTreeViewer, null);
                    setSelectedTreeItem(newPageElement);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
            }
        });

        addFrameElementToolItem = new ToolItem(elementTreeToolbar, SWT.NONE);
        addFrameElementToolItem.setImage(ImageConstants.IMG_24_NEW_FRAME_ELEMENT);
        addFrameElementToolItem.setToolTipText(StringConstants.DIA_TOOLITEM_TIP_NEW_FRAME_ELEMENT);
        addFrameElementToolItem.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (elementTreeViewer.getSelection() instanceof ITreeSelection) {
                    ITreeSelection selection = (ITreeSelection) elementTreeViewer.getSelection();
                    if (selection.getFirstElement() instanceof HTMLElement) {
                        HTMLFrameElement parentElement = getParentElement(selection);
                        if (parentElement != null) {
                            HTMLFrameElement newFrameElement = HTMLElementUtil.generateNewFrameElement(parentElement);
                            refreshTree(elementTreeViewer, parentElement);
                            setSelectedTreeItem(newFrameElement);
                        }
                    }
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
            }
        });

        addElementToolItem = new ToolItem(elementTreeToolbar, SWT.NONE);
        addElementToolItem.setImage(ImageConstants.IMG_24_NEW_ELEMENT);
        addElementToolItem.setToolTipText(StringConstants.DIA_TOOLITEM_TIP_NEW_ELEMENT);
        addElementToolItem.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (elementTreeViewer.getSelection() instanceof ITreeSelection) {
                    ITreeSelection selection = (ITreeSelection) elementTreeViewer.getSelection();
                    if (selection.getFirstElement() instanceof HTMLElement) {
                        HTMLFrameElement parentElement = getParentElement(selection);
                        if (parentElement != null) {
                            HTMLElement newElement = HTMLElementUtil.generateNewElement(parentElement);
                            refreshTree(elementTreeViewer, parentElement);
                            setSelectedTreeItem(newElement);
                        }
                    }
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
            }
        });

        removeElementToolItem = new ToolItem(elementTreeToolbar, SWT.NONE);
        removeElementToolItem.setImage(ImageConstants.IMG_16_DELETE);
        removeElementToolItem.setToolTipText(StringConstants.DIA_TOOLITEM_TIP_REMOVE_ELEMENT);
        removeElementToolItem.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (elementTreeViewer.getSelection() instanceof ITreeSelection) {
                    ITreeSelection selection = (ITreeSelection) elementTreeViewer.getSelection();
                    for (TreePath treePath : selection.getPaths()) {
                        if (treePath.getLastSegment() instanceof HTMLElement) {
                            HTMLElement element = (HTMLElement) treePath.getLastSegment();
                            HTMLFrameElement frameElement = element.getParentElement();
                            if (frameElement != null) {
                                frameElement.getChildElements().remove(element);
                                refreshTree(elementTreeViewer, frameElement);
                            } else if (element instanceof HTMLPageElement) {
                                elements.remove(element);
                                refreshTree(elementTreeViewer, null);
                            }

                            if (selectedElement.equals(element)) {
                                refreshAttributesTable(null);
                            }
                        }
                    }
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void createColumns(TableViewer viewer, TableColumnLayout tableColumnLayout) {
        TableViewerColumn keyColumn = new TableViewerColumn(viewer, SWT.NONE);
        keyColumn.getColumn().setWidth(30);
        keyColumn.getColumn().setText(StringConstants.DIA_COL_NAME);
        keyColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof Entry) {
                    Entry<String, String> entry = (Entry<String, String>) element;
                    if (entry.getKey() != null) {
                        return entry.getKey().toString();
                    }
                }
                return "";
            }
        });

        TableViewerColumn valueColumn = new TableViewerColumn(viewer, SWT.NONE);
        valueColumn.getColumn().setWidth(50);
        valueColumn.getColumn().setText(StringConstants.DIA_COL_VALUE);
        valueColumn.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                if (element instanceof Entry) {
                    Entry<String, String> entry = (Entry<String, String>) element;
                    if (entry.getValue() != null) {
                        return entry.getValue().toString();
                    }
                }
                return "";
            }
        });

        valueColumn.setEditingSupport(new EditingSupport(viewer) {
            @Override
            protected void setValue(Object element, Object value) {
                if (element instanceof Entry && value instanceof String) {
                    Entry<String, String> entry = (Entry<String, String>) element;
                    entry.setValue(String.valueOf(value));
                    attributesTableViewer.refresh(element);
                }
            }

            @Override
            protected Object getValue(Object element) {
                if (element instanceof Entry) {
                    Entry<String, String> entry = (Entry<String, String>) element;
                    if (entry.getValue() != null) {
                        return entry.getValue().toString();
                    }
                }
                return "";
            }

            @Override
            protected CellEditor getCellEditor(Object element) {
                if (element instanceof Entry) {
                    return new TextCellEditor(attributesTableViewer.getTable());
                }
                return null;
            }

            @Override
            protected boolean canEdit(Object element) {
                if (element instanceof Entry) {
                    return true;
                }
                return false;
            }
        });

        tableColumnLayout.setColumnData(keyColumn.getColumn(), new ColumnWeightData(20, 70, true));
        tableColumnLayout.setColumnData(valueColumn.getColumn(), new ColumnWeightData(80, 120, true));

    }

    /**
     * Create contents of the button bar.
     * 
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, OK_BUTTON_LABEL, false);
        createButton(parent, IDialogConstants.CANCEL_ID, CANCEL_BUTTON_LABEL, false);

        addBrowserTypeComboBox(parent);

        createButton(parent, START_BROWSER_BUTTON_ID, START_BROWSER_BUTTON_LABEL, false);
    }

    private void addBrowserTypeComboBox(Composite parent) {
        // increment the number of columns in the button bar
        ((GridLayout) parent.getLayout()).numColumns++;
        browserTypeCombo = new Combo(parent, SWT.DROP_DOWN);
        browserTypeCombo.setItems(new String[] { WebUIDriverType.CHROME_DRIVER.toString(),
                WebUIDriverType.FIREFOX_DRIVER.toString() });
        if (Platform.getOS().equals(Platform.WS_WIN32)) {
            browserTypeCombo.add(WebUIDriverType.IE_DRIVER.toString());
        }
        browserTypeCombo.select(0);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        Point minSize = browserTypeCombo.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
        data.widthHint = Math.max(widthHint, minSize.x);
        browserTypeCombo.setLayoutData(data);
    }

    @Override
    protected void handleShellCloseEvent() {
        super.handleShellCloseEvent();
        dispose();
    }

    @Override
    protected void buttonPressed(int buttonId) {
        switch (buttonId) {
            case IDialogConstants.OK_ID:
                try {
                    eventBroker.send(EventConstants.OBJECT_SPY_RESET_SELECTED_TARGET, "");
                    if (parentFolder != null) {
                        for (HTMLPageElement pageElement : elements) {
                            FolderEntity importedFolder = null;
                            if (elementTreeViewer.getChecked(pageElement) || elementTreeViewer.getGrayed(pageElement)) {
                                importedFolder = ObjectRepositoryController.getInstance().importWebElementFolder(
                                        HTMLElementUtil.convertPageElementToFolderEntity(pageElement, parentFolder),
                                        parentFolder);
                            }
                            for (HTMLElement childElement : pageElement.getChildElements()) {
                                addElement(childElement, (importedFolder != null) ? importedFolder : parentFolder, null);
                            }
                            eventBroker.post(EventConstants.OBJECT_SPY_REFRESH_SELECTED_TARGET, "");
                        }
                    }
                } catch (Exception e) {
                    logger.error(e);
                    MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, e.getMessage());
                }
                break;
            case IDialogConstants.CANCEL_ID:
                dispose();
                close();
                break;
            case START_BROWSER_BUTTON_ID:
                try {
                    if (session != null) {
                        session.stop();
                    }
                    session = new InspectSession(server.getServerUrl(),
                            WebUIDriverType.fromStringValue(browserTypeCombo.getItem(browserTypeCombo
                                    .getSelectionIndex())), ProjectController.getInstance().getCurrentProject(), logger);
                    new Thread(session).start();
                } catch (IEAddonNotInstalledException e) {
                    MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, e.getMessage());
                } catch (Exception e) {
                    logger.error(e);
                    MessageDialog.openError(getParentShell(), StringConstants.ERROR_TITLE, e.getMessage());
                }
                break;

        }
    }

    public void addElement(HTMLElement element, FolderEntity parentFolder, WebElementEntity refElement)
            throws Exception {
        WebElementEntity importedElement = null;
        if (elementTreeViewer.getChecked(element) || elementTreeViewer.getGrayed(element)) {
            importedElement = ObjectRepositoryController.getInstance().importWebElement(
                    HTMLElementUtil.convertElementToWebElementEntity(element, refElement, parentFolder), parentFolder);
        }
        if (element instanceof HTMLFrameElement) {
            for (HTMLElement childElement : ((HTMLFrameElement) element).getChildElements()) {
                addElement(childElement, parentFolder, importedElement);
            }
        }
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
        shell.setText(DIALOG_TITLE);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.getTopic().equals(EventConstants.OBJECT_SPY_ELEMENT_ADDED)
                && event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME) instanceof HTMLElement) {
            HTMLElement newElement = (HTMLElement) event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
            addNewElement(newElement);
            refreshTree(elementTreeViewer, null);
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
            refreshTree(elementTreeViewer, null);
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

    private void refreshAttributesTable(HTMLElement selectedElement) {
        if (selectedElement != null) {
            elementNameText.setText(selectedElement.getName());
            elementTypeText.setText(selectedElement.getType());
            elementTypeText.setEditable(!(selectedElement instanceof HTMLPageElement));
            elementNameText.setEditable(true);
            attributesTableViewer.setInput(new ArrayList<Entry<String, String>>(selectedElement.getAttributes()
                    .entrySet()));
        } else {
            elementNameText.setText("");

            elementTypeText.setText("");
            elementNameText.setEditable(false);
            elementTypeText.setEditable(false);
            attributesTableViewer.setInput(Collections.emptyList());
        }
        attributesTableViewer.refresh();
    }

    public boolean isDisposed() {
        return isDisposed;
    }

    public FolderEntity getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(FolderEntity parentFolder) {
        this.parentFolder = parentFolder;
    }

}
