package com.kms.katalon.composer.explorer.parts;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchCommandConstants;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.explorer.constants.ImageConstants;
import com.kms.katalon.composer.explorer.constants.StringConstants;
import com.kms.katalon.composer.explorer.custom.AdvancedSearchDialog;
import com.kms.katalon.composer.explorer.custom.SearchDropDownBox;
import com.kms.katalon.composer.explorer.handlers.CopyHandler;
import com.kms.katalon.composer.explorer.handlers.CutHandler;
import com.kms.katalon.composer.explorer.handlers.DeleteHandler;
import com.kms.katalon.composer.explorer.handlers.PasteHandler;
import com.kms.katalon.composer.explorer.handlers.RefreshHandler;
import com.kms.katalon.composer.explorer.providers.EntityLabelProvider;
import com.kms.katalon.composer.explorer.providers.EntityProvider;
import com.kms.katalon.composer.explorer.providers.EntityViewerFilter;
import com.kms.katalon.composer.explorer.util.TransferTypeCollection;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.preferences.internal.ScopedPreferenceStore;

@SuppressWarnings("restriction")
public class ExplorerPart {

    private static final String SEARCH_TEXT_DEFAULT_VALUE = StringConstants.PA_SEARCH_TEXT_DEFAULT_VALUE;
    private static final String EXPLORER_POPUPMENU_ID = "com.kms.katalon.composer.explorer.popupmenu";

    private static final String IMAGE_SEARCH_TOOLTIP = StringConstants.PA_IMAGE_TIP_SEARCH;
    private static final String IMAGE_CLOSE_SEARCH_TOOLTIP = StringConstants.PA_IMAGE_TIP_CLOSE_SEARCH;
    private static final String IMAGE_ADVANCED_SEARCH_TOOLTIP = StringConstants.PA_IMAGE_TIP_ADVANCED_SEARCH;

    private static final int KEYWORD_SEARCH_ALL_INDEX = 0;
    public static final String KEYWORD_SEARCH_ALL = "all";
    private DragSource dragSource;
    private Text txtInput;
    private TreeViewer viewer;
    private CLabel lblSearch, lblFilter;
    private SearchDropDownBox searchDropDownBox;

    // input of tree explorer
    private List<ITreeEntity> treeEntities;

    private boolean isSearching;

    private TreeViewer getViewer() {
        return viewer;
    }

    private void setViewer(TreeViewer viewer) {
        this.viewer = viewer;
    }

    @Inject
    private MApplication application;

    @Inject
    private ESelectionService selectionService;

    @Inject
    private EMenuService menuService;

    private EntityLabelProvider entityLabelProvider;
    private EntityViewerFilter entityViewerFilter;
    private Composite parent;
    private Composite searchComposite;

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EHandlerService handlerService;

    @Inject
    private EPartService partService;

    private MPart part;

    @PostConstruct
    public void createPartControl(final Composite parent, MPart mpart) {
        this.parent = parent;
        this.part = mpart;
        updateToolItemStatus();
        parent.setLayoutData(new GridData(GridData.FILL_BOTH));
        parent.setLayout(new GridLayout(1, false));

        searchComposite = new Composite(parent, SWT.BORDER);
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

        searchDropDownBox = new SearchDropDownBox(searchComposite, SWT.NONE, this);
        Label seperator = new Label(searchComposite, SWT.SEPARATOR | SWT.VERTICAL);
        GridData gdSeperator = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        gdSeperator.heightHint = 22;
        seperator.setLayoutData(gdSeperator);

        txtInput = new Text(searchComposite, SWT.NONE);
        txtInput.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        txtInput.setMessage(SEARCH_TEXT_DEFAULT_VALUE);
        GridData gdTxtInput = new GridData(GridData.FILL_HORIZONTAL);
        gdTxtInput.grabExcessVerticalSpace = true;
        gdTxtInput.verticalAlignment = SWT.CENTER;
        txtInput.setLayoutData(gdTxtInput);
        txtInput.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (isSearching) {
                    isSearching = false;
                    updateStatusSearchLabel();
                }
            }
        });

        txtInput.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    filterTreeEntitiesBySearchedText();
                }
            }
        });

        application.getContext().set(ExplorerPart.class.getName(), this);

        setViewer(new TreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.VIRTUAL));
        getTreeViewer().setUseHashlookup(true);
        getViewer().getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

        viewer.getTree().addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
                //Nothing to do here
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (((e.stateMask & SWT.CTRL) == SWT.CTRL)) {
                    if (e.keyCode == 'c') {
                        CopyHandler.execute(selectionService);
                    }
                }
            }
        });

        EntityProvider contentProvider = new EntityProvider();
        getViewer().setContentProvider(contentProvider);
        entityLabelProvider = new EntityLabelProvider();
        getViewer().setLabelProvider(entityLabelProvider);
        // viewer.setAutoExpandLevel(2);

        getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                if (selectionService != null) {
                    selectionService.setSelection(((IStructuredSelection) event.getSelection()).toArray());
                }
            }
        });

        entityViewerFilter = new EntityViewerFilter(contentProvider);
        getViewer().addFilter(entityViewerFilter);

        // label Search
        Canvas canvasSearch = new Canvas(searchComposite, SWT.NONE);
        canvasSearch.setLayout(new FillLayout(SWT.HORIZONTAL));

        isSearching = false;
        lblSearch = new CLabel(canvasSearch, SWT.NONE);
        updateStatusSearchLabel();

        lblSearch.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));
        lblSearch.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
                if (isSearching) {
                    isSearching = false;
                    txtInput.setText(StringUtils.EMPTY);
                    filterTreeEntitiesBySearchedText();
                } else {
                    isSearching = true;
                    filterTreeEntitiesBySearchedText();
                }
            }
        });

        Label seperator1 = new Label(searchComposite, SWT.SEPARATOR | SWT.VERTICAL);
        GridData gdSeperator1 = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        gdSeperator1.heightHint = 22;
        seperator1.setLayoutData(gdSeperator1);

        // label Filter
        lblFilter = new CLabel(searchComposite, SWT.NONE);
        lblFilter.setImage(ImageConstants.IMG_16_ADVANCED_SEARCH);
        lblFilter.setToolTipText(IMAGE_ADVANCED_SEARCH_TOOLTIP);
        lblFilter.setCursor(new Cursor(Display.getCurrent(), SWT.CURSOR_HAND));
        lblFilter.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
                openAdvancedSearchDialog();
            }
        });

        hookDoubleClickEvent();
        hookDragEvent();

        menuService.registerContextMenu(getViewer().getControl(), EXPLORER_POPUPMENU_ID);

        activateHandler();
    }

    private void activateHandler() {
        handlerService.activateHandler(IWorkbenchCommandConstants.EDIT_DELETE, new DeleteHandler());
        handlerService.activateHandler(IWorkbenchCommandConstants.EDIT_COPY, new CopyHandler());
        handlerService.activateHandler(IWorkbenchCommandConstants.EDIT_CUT, new CutHandler());
        handlerService.activateHandler(IWorkbenchCommandConstants.EDIT_PASTE, new PasteHandler());
        handlerService.activateHandler(IWorkbenchCommandConstants.FILE_REFRESH, new RefreshHandler());
    }

    private void updateToolItemStatus() {
        IPreferenceStore store = (IPreferenceStore) new ScopedPreferenceStore(InstanceScope.INSTANCE,
                PreferenceConstants.ExplorerPreferenceConstants.QUALIFIER);
        for (MToolBarElement toolbarElement : part.getToolbar().getChildren()) {
            if (!(toolbarElement instanceof MHandledToolItem)) { continue; }
            MHandledToolItem toolItem = (MHandledToolItem) toolbarElement;
            switch (toolItem.getElementId()) {
                case IdConstants.EXPLORER_TOOL_ITEM_LINK_PART: {
                    toolItem.setSelected(store
                            .getBoolean(PreferenceConstants.ExplorerPreferenceConstants.EXPLORER_LINK_WITH_PART));
                    break;
                }
            }
        }
    }

    /**
     * the message will be broadcasted to viewer filter and label provider after searching or filtering
     * 
     * @return
     */
    private String getMessage() {
        try {
            int index = searchDropDownBox.getSelectionIndex();
            if (index == KEYWORD_SEARCH_ALL_INDEX) {
                return KEYWORD_SEARCH_ALL + ":" + txtInput.getText();
            } else {
                return treeEntities.get(index - 1).getKeyWord() + ":" + txtInput.getText();
            }
        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * After user select a type of tree entity on drop down box, all entities will be filtered by all elements by
     * selection type and searched string
     */
    public void filterTreeEntitiesByType() {
        if (getTreeViewer().getTree().isDisposed()) return;
        while (getTreeViewer().isBusy()) {
            // wait for tree is not busy
        }
        BusyIndicator.showWhile(getTreeViewer().getTree().getDisplay(), new Runnable() {
            @Override
            public void run() {
                TreePath[] expandedTreePaths = getViewer().getExpandedTreePaths();
                String searchedText = txtInput.getText();
                String broadcastMessage = getMessage();
                entityLabelProvider.setSearchString(broadcastMessage);
                entityViewerFilter.setSearchString(broadcastMessage);
                getTreeViewer().getTree().setRedraw(false);
                getTreeViewer().refresh();
                // keep the tree expands all if it is being searched
                if (searchedText != null && StringUtils.isNotEmpty(searchedText)) {
                    getViewer().expandAll();
                } else {
                    for (TreePath treePath : expandedTreePaths) {
                        getTreeViewer().expandToLevel(treePath, 1);
                    }
                }
                getTreeViewer().getTree().setRedraw(true);
                txtInput.setFocus();
            }
        });
    }

    /**
     * After user type a text on searched text box, all entities will be filtered by all elements by the text
     */
    private void filterTreeEntitiesBySearchedText() {
        if (getTreeViewer().getTree().isDisposed()) return;
        final String searchString = txtInput.getText();
        while (getTreeViewer().isBusy()) {
            // wait for tree is not busy
        }
        BusyIndicator.showWhile(getTreeViewer().getTree().getDisplay(), new Runnable() {
            @Override
            public void run() {
                try {
                    if (searchString.equals(txtInput.getText()) && getTreeViewer().getInput() != null) {
                        String broadcastMessage = getMessage();
                        entityLabelProvider.setSearchString(broadcastMessage);
                        entityViewerFilter.setSearchString(broadcastMessage);
                        // getTreeViewer().getTree().setRedraw(false);
                        getTreeViewer().refresh(true);
                        if (searchString != null && !searchString.isEmpty()) {
                            isSearching = true;
                            getTreeViewer().expandAll();
                        } else {
                            isSearching = false;
                            getTreeViewer().collapseAll();
                        }
                        updateStatusSearchLabel();
                    }
                } catch (Exception e) {
                    LoggerSingleton.getInstance().getLogger().error(e);
                } finally {
                    // getTreeViewer().getTree().setRedraw(true);
                }
            }
        });
    }

    private void openAdvancedSearchDialog() {
        try {
            if (!getSearchDropBoxElements().isEmpty()) {
                int selectionIndex = searchDropDownBox.getSelectionIndex();
                Shell shell = new Shell(parent.getShell());
                shell.setSize(0, 0);
                List<String> searchTags = new ArrayList<String>();

                Point pt = searchComposite.toDisplay(1, 1);
                Point location = new Point(pt.x + searchComposite.getBounds().width, pt.y);
                AdvancedSearchDialog dialog;
                if (selectionIndex > 0) {
                    ITreeEntity treeEntity = treeEntities.get(selectionIndex - 1);
                    dialog = new AdvancedSearchDialog(shell, treeEntity.getSearchTags(), txtInput.getText(), location);
                } else {
                    for (ITreeEntity treeEntity : treeEntities) {
                        if (treeEntity.getSearchTags() != null) {
                            for (String tag : treeEntity.getSearchTags()) {
                                if (!searchTags.contains(tag)) {
                                    searchTags.add(tag);
                                }
                            }
                        }
                    }
                    dialog = new AdvancedSearchDialog(shell, searchTags.toArray(new String[searchTags.size()]),
                            txtInput.getText(), location);
                }
                // set position for dialog
                if (dialog.open() == Window.OK) {
                    txtInput.setText(dialog.getOutput());
                    filterTreeEntitiesBySearchedText();
                }

                shell.getSize();
                shell.dispose();
            }
        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
        }
    }

    private void updateStatusSearchLabel() {
        if (isSearching) {
            lblSearch.setImage(ImageConstants.IMG_16_CLOSE_SEARCH);
            lblSearch.setToolTipText(IMAGE_CLOSE_SEARCH_TOOLTIP);
        } else {
            lblSearch.setImage(ImageConstants.IMG_16_SEARCH);
            lblSearch.setToolTipText(IMAGE_SEARCH_TOOLTIP);
        }
    }

    @Focus
    public void setFocus() {
        getViewer().getControl().setFocus();
        partService.activate(part, true);
    }

    public TreeViewer getTreeViewer() {
        return getViewer();
    }

    @Inject
    @Optional
    private void reloadTreeInputEventHandler(
            @UIEventTopic(EventConstants.EXPLORER_RELOAD_INPUT) final List<Object> treeEntities) {
        try {
            if (getTreeViewer().getTree().isDisposed()) return;
            while (getTreeViewer().isBusy()) {
                // wait for tree is not busy
            }
            // wait for reseting search field complete
            resetSearchField();
            searchDropDownBox.clearInput();
            getTreeViewer().getTree().clearAll(true);
            getTreeViewer().setInput(treeEntities);
            updateTreeEntities(treeEntities);
            getTreeViewer().refresh();

            reloadTreeEntityTransfers();
            EntityProvider dataProvider = (EntityProvider) getTreeViewer().getContentProvider();
            getViewer().setSelection(new TreeSelection(dataProvider.getTreePath(treeEntities.get(0))), true);

        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
        }
    }

    private void reloadTreeEntityTransfers() {
        List<Transfer> treeEntityTransfers = TransferTypeCollection.getInstance().getTreeEntityTransfer();
        dragSource.setTransfer(treeEntityTransfers.toArray(new Transfer[treeEntityTransfers.size()]));
    }

    private void resetSearchField() {
        txtInput.setText("");
        isSearching = false;
        updateStatusSearchLabel();
        entityLabelProvider.setSearchString("");
        entityViewerFilter.setSearchString("");
    }

    @Inject
    @Optional
    private void refreshTree(@UIEventTopic(EventConstants.EXPLORER_REFRESH) Object object) {
        refresh(null);
    }

    @Inject
    @Optional
    private void refreshTreeEntity(@UIEventTopic(EventConstants.EXPLORER_REFRESH_TREE_ENTITY) Object object) {
        refresh(object);
    }
    
    @Inject
    @Optional
    private void expandTreeEntity(@UIEventTopic(EventConstants.EXPLORER_EXPAND_TREE_ENTITY) Object object) {
        if (object == null) {
            return;
        }
        viewer.expandToLevel(object, 1);
    }

    private void refresh(Object object) {
        viewer.getControl().setRedraw(false);
        Object[] expandedElements = viewer.getExpandedElements();
        if (object == null) {
            viewer.refresh();
        } else {
            viewer.refresh(object);
        }
        for (Object element : expandedElements) {
            viewer.setExpandedState(element, true);
        }
        viewer.getControl().setRedraw(true);
    }

    @Inject
    @Optional
    private void setSelectedItem(@UIEventTopic(EventConstants.EXPLORER_SET_SELECTED_ITEM) Object object) {
        if (object == null || !(object instanceof ITreeEntity)) {
            return;
        }
        getViewer().setSelection(new StructuredSelection(object));
        getViewer().setExpandedState(object, true);
        setFocus();
    }
    
    @Inject
    @Optional
    private void showItem(@UIEventTopic(EventConstants.EXPLORER_SHOW_ITEM) Object object) {
        if (object == null || !(object instanceof ITreeEntity)) {
            return;
        }
        getViewer().setSelection(new StructuredSelection(object));
        getViewer().setExpandedState(object, true);
    }

    @Inject
    @Optional
    private void refreshAllItems(@UIEventTopic(EventConstants.EXPLORER_REFRESH_ALL_ITEMS) Object object) {
        for (ITreeEntity treeRootEntity : treeEntities) {
            eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, treeRootEntity);
        }
    }

    @Inject
    @Optional
    private void collapseAllItems(@UIEventTopic(EventConstants.EXPLORER_COLLAPSE_ALL_ITEMS) Object object) {
        if (getTreeViewer() != null) {
            getTreeViewer().collapseAll();
        }
    }

    private void hookDoubleClickEvent() {
        getViewer().addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                if (selectionService == null) return;
                Object selectedElement = ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (selectedElement == null || !(selectedElement instanceof ITreeEntity)) return;

                EntityProvider contentProvider = (EntityProvider) viewer.getContentProvider();
                if (contentProvider.hasChildren(selectedElement)) {
                    viewer.setExpandedState(selectedElement, !viewer.getExpandedState(selectedElement));
                } else {
                    try {
                        eventBroker.send(EventConstants.EXPLORER_OPEN_SELECTED_ITEM,
                                ((ITreeEntity) selectedElement).getObject());
                    } catch (Exception e) {
                        LoggerSingleton.getInstance().getLogger().error(e);
                    }
                }
            }
        });
    }

    private void hookDragEvent() {
        int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;

        dragSource = new DragSource(getViewer().getTree(), operations);
        List<Transfer> treeEntityTransfers = TransferTypeCollection.getInstance().getTreeEntityTransfer();
        dragSource.setTransfer(treeEntityTransfers.toArray(new Transfer[treeEntityTransfers.size()]));
        dragSource.addDragListener(new DragSourceListener() {
            public void dragStart(DragSourceEvent event) {
                TreeItem[] selection = getViewer().getTree().getSelection();
                if (selection.length > 0) {
                    event.doit = true;
                } else {
                    event.doit = false;
                }
            };

            public void dragSetData(DragSourceEvent event) {
                TreeItem[] selection = getViewer().getTree().getSelection();
                List<ITreeEntity> treeEntity = new ArrayList<ITreeEntity>();
                for (TreeItem item : selection) {
                    treeEntity.add((ITreeEntity) item.getData());
                }
                event.data = treeEntity.toArray(new ITreeEntity[treeEntity.size()]);
            }

            public void dragFinished(DragSourceEvent event) {
            }
        });
    }

    public List<ITreeEntity> getSearchDropBoxElements() {
        if (treeEntities == null) {
            treeEntities = new ArrayList<ITreeEntity>();
        }
        return treeEntities;
    }

    private void updateTreeEntities(List<Object> input) {
        if (treeEntities == null) {
            treeEntities = new ArrayList<ITreeEntity>();
        } else {
            treeEntities.clear();
        }
        for (Object o : input) {
            treeEntities.add((ITreeEntity) o);
        }
    }

    @PreDestroy
    public void dispose() {
        getTreeViewer().getTree().dispose();
    }

}
