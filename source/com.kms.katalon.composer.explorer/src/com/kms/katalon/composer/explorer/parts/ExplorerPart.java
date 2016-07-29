package com.kms.katalon.composer.explorer.parts;

import static com.kms.katalon.composer.components.impl.util.TreeEntityUtil.getTreeEntityIds;
import static com.kms.katalon.composer.components.log.LoggerSingleton.logError;
import static com.kms.katalon.preferences.internal.PreferenceStoreManager.getPreferenceStore;
import static org.eclipse.ui.PlatformUI.getPreferenceStore;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
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
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
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
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.control.CTreeViewer;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.tree.KeywordTreeEntity;
import com.kms.katalon.composer.components.impl.tree.PackageTreeEntity;
import com.kms.katalon.composer.components.impl.tree.ReportTreeEntity;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.explorer.constants.ExplorerPreferenceConstants;
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
import com.kms.katalon.composer.explorer.providers.TreeEntityDropListener;
import com.kms.katalon.composer.explorer.util.TransferTypeCollection;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.folder.FolderEntity.FolderType;
import com.kms.katalon.entity.project.ProjectEntity;
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

    private CTreeViewer treeViewer;

    private CLabel lblSearch, lblFilter;

    private SearchDropDownBox searchDropDownBox;

    // input of tree explorer
    private List<ITreeEntity> treeEntities;

    private boolean isSearching;

    // Represent path of the tree items that each one is expanded before searching
    private Object[] expandedTreeElements;

    private TreeViewer getViewer() {
        return treeViewer;
    }

    private void setViewer(CTreeViewer viewer) {
        this.treeViewer = viewer;
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

        txtInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
                    filterTreeEntitiesBySearchedText();
                }
            }
        });

        application.getContext().set(ExplorerPart.class.getName(), this);

        setViewer(new CTreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.VIRTUAL));
        treeViewer.setUseHashlookup(true);
        getViewer().getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

        treeViewer.getTree().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (((e.stateMask & SWT.CTRL) == SWT.CTRL)) {
                    if (e.keyCode == 'c') {
                        CopyHandler.getInstance().execute();
                    }
                }
            }
        });

        EntityProvider contentProvider = new EntityProvider();
        getViewer().setContentProvider(contentProvider);
        entityLabelProvider = new EntityLabelProvider();
        getViewer().setLabelProvider(entityLabelProvider);

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
        hookDropEvent();

        menuService.registerContextMenu(getViewer().getControl(), EXPLORER_POPUPMENU_ID);

        activateHandler();

        // loadSavedState(part);
    }

    private void activateHandler() {
        handlerService.activateHandler(IWorkbenchCommandConstants.EDIT_DELETE, new DeleteHandler());
        handlerService.activateHandler(IWorkbenchCommandConstants.EDIT_COPY, new CopyHandler());
        handlerService.activateHandler(IWorkbenchCommandConstants.EDIT_CUT, new CutHandler());
        handlerService.activateHandler(IWorkbenchCommandConstants.EDIT_PASTE, new PasteHandler());
        handlerService.activateHandler(IWorkbenchCommandConstants.FILE_REFRESH, new RefreshHandler());
    }

    private void updateToolItemStatus() {
        ScopedPreferenceStore store = getPreferenceStore(ExplorerPart.class);
        for (MToolBarElement toolbarElement : part.getToolbar().getChildren()) {
            if (!(toolbarElement instanceof MHandledToolItem)) {
                continue;
            }
            MHandledToolItem toolItem = (MHandledToolItem) toolbarElement;
            switch (toolItem.getElementId()) {
                case IdConstants.EXPLORER_TOOL_ITEM_LINK_PART: {
                    toolItem.setSelected(store.getBoolean(ExplorerPreferenceConstants.EXPLORER_LINK_WITH_PART));
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
            LoggerSingleton.logError(e);
            return StringUtils.EMPTY;
        }
    }

    /**
     * After user select a type of tree entity on drop down box, all entities will be filtered by all elements by
     * selection type and searched string
     */
    public void filterTreeEntitiesByType() {
        fitlterTreeEntities(false, false);
        txtInput.setFocus();
    }

    /**
     * After user type a text on searched text box, all entities will be filtered by all elements by the text
     */
    private void filterTreeEntitiesBySearchedText() {
        fitlterTreeEntities(true, true);
    }

    private void fitlterTreeEntities(final boolean updateLabel, final boolean keepExpandedState) {
        if (treeViewer.getTree().isDisposed()) {
            return;
        }

        final String searchString = txtInput.getText();
        while (treeViewer.isBusy()) {
            // wait for the tree until it is not busy
        }

        if (!searchString.equals(txtInput.getText()) || treeViewer.getInput() == null) {
            return;
        }

        // Remember last expanded state
        if (keepExpandedState && StringUtils.isNotBlank(searchString) && !isSearching) {
            expandedTreeElements = treeViewer.getExpandedElements();
        }

        Job job = new Job("Indexing...") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    monitor.beginTask("Indexing...", treeEntities.size());
                    for (ITreeEntity folderEntity : treeEntities) {
                        try {
                            ((FolderTreeEntity) folderEntity).loadAllDescentdantEntities();
                            monitor.worked(1);
                        } catch (Exception e) {
                            LoggerSingleton.logError(e);
                        }
                    }
                    return Status.OK_STATUS;
                } finally {
                    monitor.done();
                }
            }
        };
        job.setUser(true);
        job.schedule();

        job.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                UISynchronizeService.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String broadcastMessage = getMessage();
                            entityLabelProvider.setSearchString(broadcastMessage);
                            entityViewerFilter.setSearchString(broadcastMessage);
                            treeViewer.getTree().setRedraw(false);
                            treeViewer.refresh(true);

                            if (StringUtils.isNotBlank(searchString)) {
                                treeViewer.expandAll();
                            } else {
                                treeViewer.collapseAll();

                                if (expandedTreeElements != null) {
                                    // Restore last expanded state
                                    for (Object treePath : expandedTreeElements) {
                                        treeViewer.setExpandedState(treePath, true);
                                    }
                                }
                            }

                            if (updateLabel) {
                                isSearching = StringUtils.isNotBlank(searchString);
                                updateStatusSearchLabel();
                            }
                        } catch (Exception e) {
                            LoggerSingleton.logError(e);
                        } finally {
                            treeViewer.getTree().setRedraw(true);
                        }
                    }
                });
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
                        if (treeEntity.getSearchTags() == null) {
                            continue;
                        }

                        for (String tag : treeEntity.getSearchTags()) {
                            if (!searchTags.contains(tag)) {
                                searchTags.add(tag);
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
            LoggerSingleton.logError(e);
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

    @Inject
    @Optional
    private void reloadTreeEventHandler(@UIEventTopic(EventConstants.EXPLORER_RELOAD_DATA) Object object) {
        try {
            List<ITreeEntity> treeEntities = TreeEntityUtil.getAllTreeEntity(ProjectController.getInstance()
                    .getCurrentProject());
            eventBroker.post(EventConstants.EXPLORER_RELOAD_INPUT, treeEntities);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Inject
    @Optional
    private void reloadTreeInputEventHandler(
            @UIEventTopic(EventConstants.EXPLORER_RELOAD_INPUT) final List<Object> treeEntities) {
        try {
            if (treeViewer.getTree().isDisposed())
                return;
            while (treeViewer.isBusy()) {
                // wait for tree is not busy
            }
            // wait for reseting search field complete
            resetSearchField();
            searchDropDownBox.clearInput();
            treeViewer.getTree().clearAll(true);
            treeViewer.setInput(treeEntities);
            updateTreeEntities(treeEntities);
            treeViewer.refresh();

            reloadTreeEntityTransfers();
            EntityProvider dataProvider = (EntityProvider) treeViewer.getContentProvider();
            if (treeEntities != null && !treeEntities.isEmpty()) {
                getViewer().setSelection(new TreeSelection(dataProvider.getTreePath(treeEntities.get(0))), true);
            }

        } catch (Exception e) {
            LoggerSingleton.logError(e);
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
        treeViewer.expandToLevel(object, 1);
    }

    private void refresh(Object object) {
        treeViewer.getControl().setRedraw(false);
        TreePath[] expandedTreePaths = getViewer().getExpandedTreePaths();
        getViewer().collapseAll();
        if (object == null) {
            treeViewer.refresh();
        } else {
            treeViewer.refresh(object);
        }
        getViewer().setExpandedTreePaths(expandedTreePaths);
        treeViewer.getControl().setRedraw(true);
    }

    @Inject
    @Optional
    private void setSelectedItem(@UIEventTopic(EventConstants.EXPLORER_SET_SELECTED_ITEM) Object object) {
        if (object == null || !(object instanceof ITreeEntity)) {
            return;
        }
        TreePath[] expandedTreePaths = getViewer().getExpandedTreePaths();
        getViewer().getControl().setRedraw(false);
        // reload input
        getViewer().setInput(getViewer().getInput());

        // restore expanded tree paths
        getViewer().setExpandedTreePaths(expandedTreePaths);
        getViewer().getControl().setRedraw(true);

        // set new selection
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
        if (treeViewer != null) {
            treeViewer.collapseAll();
        }
    }

    private void hookDoubleClickEvent() {
        getViewer().addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                if (selectionService == null || event.getSelection() == null) {
                    return;
                }

                Object selectedElement = ((IStructuredSelection) event.getSelection()).getFirstElement();

                if (selectedElement == null || !(selectedElement instanceof ITreeEntity)) {
                    return;
                }

                EntityProvider contentProvider = (EntityProvider) treeViewer.getContentProvider();
                if (contentProvider.hasChildren(selectedElement)) {
                    treeViewer.setExpandedState(selectedElement, !treeViewer.getExpandedState(selectedElement));
                } else {
                    try {
                        eventBroker.send(EventConstants.EXPLORER_OPEN_SELECTED_ITEM,
                                ((ITreeEntity) selectedElement).getObject());
                    } catch (Exception e) {
                        LoggerSingleton.logError(e);
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

                // do not allow drag in Reports and Keywords area
                try {
                    for (TreeItem item : selection) {
                        if (item.getData() instanceof ReportTreeEntity || item.getData() instanceof KeywordTreeEntity
                                || item.getData() instanceof PackageTreeEntity) {
                            event.doit = false;
                        } else if (item.getData() instanceof FolderTreeEntity
                                && ((FolderTreeEntity) item.getData()).getCopyTag()
                                        .equals(FolderType.REPORT.toString())) {
                            event.doit = false;
                        }
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
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

    private void hookDropEvent() {
        DropTarget dt = new DropTarget(treeViewer.getTree(), DND.DROP_MOVE);
        List<Transfer> treeEntityTransfers = TransferTypeCollection.getInstance().getTreeEntityTransfer();
        dt.setTransfer(treeEntityTransfers.toArray(new Transfer[treeEntityTransfers.size()]));
        dt.addDropListener(new TreeEntityDropListener(treeViewer, eventBroker));
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
        treeViewer.getTree().dispose();
    }

    /**
     * Restore saved state from last opened session.
     * 
     * @see #saveExplorerState()
     */
    @Inject
    @Optional
    private void restorePersistedState() {
        if (isNotAutoRestoreSession()) {
            return;
        }
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, new EventHandler() {

            @Override
            public void handleEvent(org.osgi.service.event.Event event) {
                try {
                    ProjectEntity project = ProjectController.getInstance().getRecentProjects().get(0);
                    restoreExplorerState(project.getRecentExpandedTreeEntityIds());
                    restoreOpenedEntitiesState(project.getRecentOpenedTreeEntityIds());
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }
            }
        });
    }

    /**
     * Restore Tests Explorer state from last opened session
     * 
     * @see #saveExplorerState()
     * @param expandedTreeEntityIds
     * @throws Exception
     */
    private void restoreExplorerState(List<String> expandedTreeEntityIds) throws Exception {
        getViewer().getControl().setRedraw(false);
        getViewer().setExpandedElements(TreeEntityUtil.getExpandedTreeEntitiesFromIds(expandedTreeEntityIds).toArray());
        getViewer().getControl().setRedraw(true);
    }

    /**
     * Restore opened entities from last session.
     * 
     * @see com.kms.katalon.composer.project.handlers.CloseProjectHandler#saveOpenedEntitiesState()
     * @param openedEntityIds
     * @throws Exception
     */
    private void restoreOpenedEntitiesState(List<String> openedEntityIds) throws Exception {
        // Need to activate ExplorerPart before open any entity
        partService.activate(part);

        List<ITreeEntity> treeEntities = TreeEntityUtil.getOpenedTreeEntitiesFromIds(openedEntityIds);
        for (ITreeEntity entity : treeEntities) {
            if (entity != null && entity.getObject() != null) {
                eventBroker.send(EventConstants.EXPLORER_OPEN_SELECTED_ITEM, entity.getObject());
            }
        }
    }

    /**
     * Save Tests Explorer state for next open session
     * 
     * @see #restoreExplorerState(String, String)
     */
    @PersistState
    public void savePersistedState() {
        if (isNotAutoRestoreSession()) {
            return;
        }
        try {
            List<String> expandedTreeEntityIds = getTreeEntityIds(getViewer().getExpandedElements());
            ProjectController.getInstance().keepStateOfExpandedTreeEntities(expandedTreeEntityIds);
        } catch (Exception e) {
            logError(e);
        }
    }

    /**
     * Save Tests Explorer state for next open session.
     * <p>
     * This method will run on the scenario of switching between projects. The state of current one will be saved before
     * open the next target.
     * 
     * @see #savePersistedState()
     * @param projectId
     */
    @Inject
    @Optional
    private void saveExplorerState(@UIEventTopic(EventConstants.PROJECT_SWITCH) String toProjectId) {
        savePersistedState();
    }

    private boolean isNotAutoRestoreSession() {
        return !getPreferenceStore().getBoolean(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION);
    }
}
