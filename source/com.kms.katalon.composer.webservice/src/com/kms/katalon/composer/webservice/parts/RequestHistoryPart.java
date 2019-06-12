package com.kms.katalon.composer.webservice.parts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.controls.HelpToolBarForMPart;
import com.kms.katalon.composer.components.impl.control.CTreeViewer;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.webservice.constants.ImageConstants;
import com.kms.katalon.composer.webservice.handlers.IRequestHistoryListener;
import com.kms.katalon.composer.webservice.handlers.OpenWebServiceRequestObjectHandler;
import com.kms.katalon.composer.webservice.handlers.RequestHistoryHandler;
import com.kms.katalon.composer.webservice.handlers.SaveDraftRequestHandler;
import com.kms.katalon.composer.webservice.parts.tree.IRequestHistoryItem;
import com.kms.katalon.composer.webservice.parts.tree.RequestDateTreeItem;
import com.kms.katalon.composer.webservice.parts.tree.RequestHistoryStyleCellProvider;
import com.kms.katalon.composer.webservice.parts.tree.RequestHistoryTreeItem;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.ExceptionsUtil;
import com.kms.katalon.entity.webservice.RequestHistoryEntity;
import com.kms.katalon.tracking.service.Trackings;
import com.kms.katalon.util.DateTimes;

public class RequestHistoryPart implements IRequestHistoryListener {

    @Inject
    private IEventBroker eventBroker;

    @Inject
    IEclipseContext context;

    private CTreeViewer treeViewer;

    private HistoryRequestContentProvider contentProvider;

    private RequestHistoryHandler requestHistoryHandler;

    private ToolItem imgBtnSave;

    private ToolItem imgBtnDelete;

    @PostConstruct
    public void createPart(MPart mpart, Composite parent) {
        requestHistoryHandler = context.get(RequestHistoryHandler.class);
        createControl(parent);
        registerEventBroker();
        
        new HelpToolBarForMPart(mpart, DocumentationMessageConstants.REQUEST_HISTORY);
    }

    private void registerEventBroker() {
        requestHistoryHandler.setListener(this);
    }

    private void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout glContainer = new GridLayout();
        glContainer.marginWidth = 0;
        glContainer.marginHeight = 0;
        container.setLayout(glContainer);
        container.setBackground(ColorUtil.getWhiteBackgroundColor());

        Composite headerComposite = new Composite(container, SWT.NONE);
        headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        headerComposite.setLayout(new GridLayout(2, false));

        Label lblRequestHistoryTitle = new Label(headerComposite, SWT.NONE);
        lblRequestHistoryTitle.setText("History");
        lblRequestHistoryTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        ControlUtils.setFontToBeBold(lblRequestHistoryTitle);

        ToolBar buttonsComposite = new ToolBar(headerComposite, SWT.FLAT | SWT.WRAP);
        buttonsComposite.setForeground(ColorUtil.getToolBarForegroundColor());
        buttonsComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
        buttonsComposite.setLayout(new GridLayout(2, false));

        imgBtnSave = new ToolItem(buttonsComposite, SWT.PUSH);
        imgBtnSave.setImage(ImageConstants.IMG_16_SAVE);
        imgBtnSave.setText("Save");
        imgBtnSave.setEnabled(false);

        imgBtnDelete = new ToolItem(buttonsComposite, SWT.PUSH);
        imgBtnDelete.setImage(ImageConstants.IMG_16_DELETE);
        imgBtnDelete.setText("Remove");
        imgBtnDelete.setEnabled(false);

        Label lblHorizontalSeparator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
        lblHorizontalSeparator.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        Composite treeComposite = new Composite(container, SWT.NONE);
        treeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        TreeColumnLayout treeLayout = new TreeColumnLayout();
        treeComposite.setLayout(treeLayout);

        treeViewer = new CTreeViewer(treeComposite, SWT.MULTI);
        TreeViewerColumn tvcItem = new TreeViewerColumn(treeViewer, SWT.NONE);
        tvcItem.setLabelProvider(new RequestHistoryStyleCellProvider());
        treeLayout.setColumnData(tvcItem.getColumn(), new ColumnWeightData(98, 150));

        contentProvider = new HistoryRequestContentProvider();
        treeViewer.setContentProvider(contentProvider);
        treeViewer.enableTooltipSupport();

        reloadTreeData();
        treeViewer.expandAll();
        registerEventListeners();
    }

    private void reloadTreeData() {
        treeViewer.getTree().setRedraw(false);
        Object[] expandedElements = treeViewer.getExpandedElements();
        treeViewer.setInput(
                requestHistoryHandler.getRequestHistoryEntities(ProjectController.getInstance().getCurrentProject()));

        List<RequestDateTreeItem> expandedDateItems = new ArrayList<>();
        for (Object e : expandedElements) {
            if (!(e instanceof RequestDateTreeItem)) {
                continue;
            }
            RequestDateTreeItem dateItem = (RequestDateTreeItem) e;
            RequestDateTreeItem newDateItem = contentProvider.findElementByDate(dateItem.getDate());
            if (newDateItem != null) {
                expandedDateItems.add(newDateItem);
            }
        }
        if (expandedDateItems.size() > 0) {
            treeViewer.setExpandedElements(expandedDateItems.toArray());
        }

        treeViewer.getTree().setRedraw(true);
    }

    private void registerEventListeners() {
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent event) {
                IRequestHistoryItem selectedItem = (IRequestHistoryItem) treeViewer.getStructuredSelection()
                        .getFirstElement();
                if (selectedItem instanceof RequestDateTreeItem) {
                    treeViewer.setExpandedState(selectedItem, !treeViewer.getExpandedState(selectedItem));
                } else if (selectedItem instanceof RequestHistoryTreeItem) {
                    RequestHistoryTreeItem treeItem = (RequestHistoryTreeItem) selectedItem;
                    context.get(OpenWebServiceRequestObjectHandler.class)
                            .openRequestHistoryObject(treeItem.getRequestHistoryEntity());
                }
            }
        });

        treeViewer.getTree().addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ITreeSelection structuredSelection = treeViewer.getStructuredSelection();
                if (structuredSelection == null || structuredSelection.isEmpty()) {
                    imgBtnSave.setEnabled(false);
                    imgBtnDelete.setEnabled(false);
                    return;
                }

                imgBtnDelete.setEnabled(true);
                imgBtnSave.setEnabled(structuredSelection.size() == 1
                        && structuredSelection.getFirstElement() instanceof RequestHistoryTreeItem);
            }
        });

        imgBtnSave.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ITreeSelection structuredSelection = treeViewer.getStructuredSelection();
                if (structuredSelection == null || structuredSelection.isEmpty() || structuredSelection.size() != 1
                        || !(structuredSelection.getFirstElement() instanceof RequestHistoryTreeItem)) {
                    return;
                }
                RequestHistoryTreeItem selectedTreeItem = (RequestHistoryTreeItem) structuredSelection
                        .getFirstElement();
                SaveDraftRequestHandler.saveDraftRequest(imgBtnSave.getDisplay().getActiveShell(),
                        selectedTreeItem.getRequestHistoryEntity().getRequest());
            }
        });

        imgBtnDelete.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Trackings.trackClickDeletingDraftRequest();
                ITreeSelection structuredSelection = treeViewer.getStructuredSelection();
                if (structuredSelection == null || structuredSelection.isEmpty()) {
                    return;
                }
                Set<RequestHistoryEntity> removedEntities = new HashSet<>();
                for (Object obj : structuredSelection.toArray()) {
                    if (obj instanceof RequestHistoryTreeItem) {
                        RequestHistoryTreeItem requestHistoryTreeItem = (RequestHistoryTreeItem) obj;
                        removedEntities.add(requestHistoryTreeItem.getRequestHistoryEntity());
                    } else if (obj instanceof RequestDateTreeItem) {
                        for (RequestHistoryTreeItem item : ((RequestDateTreeItem) obj).getItems()) {
                            removedEntities.add(item.getRequestHistoryEntity());
                        }
                    }
                }
                if (removedEntities.isEmpty()) {
                    return;
                }
                try {
                    imgBtnSave.setEnabled(false);
                    imgBtnDelete.setEnabled(false);
                    requestHistoryHandler.removeRequestHistories(new ArrayList<>(removedEntities),
                            ProjectController.getInstance().getCurrentProject());
                    Trackings.trackDeleteDraftRequest(removedEntities.size());
                } catch (IOException ex) {
                    MultiStatusErrorDialog.showErrorDialog("Unable to remove selected items", ex.getMessage(),
                            ExceptionsUtil.getStackTraceForThrowable(ex));
                    LoggerSingleton.logError(ex);
                }
            }
        });
    }

    private class HistoryRequestContentProvider implements ITreeContentProvider {

        private List<RequestDateTreeItem> elements;

        private RequestDateTreeItem findElementByDate(Date date) {
            long firstTimeOfDate = DateTimes.toFirstDate(date).getTime();
            for (Object e : elements) {
                RequestDateTreeItem dateItem = (RequestDateTreeItem) e;
                if (dateItem.getDate().equals(new Date(firstTimeOfDate))) {
                    return dateItem;
                }
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                List<RequestHistoryEntity> requestHistoryEntity = (List<RequestHistoryEntity>) inputElement;
                Map<Long, List<RequestHistoryEntity>> requestHistoryPerDate = new HashMap<>();
                requestHistoryEntity.stream().forEach(req -> {
                    long firstTimeOfDate = DateTimes.toFirstDate(req.getReceivedResponseTime()).getTime();
                    List<RequestHistoryEntity> requestInADay = new ArrayList<>();
                    if (requestHistoryPerDate.containsKey(firstTimeOfDate)) {
                        requestInADay.addAll(requestHistoryPerDate.get(firstTimeOfDate));
                    }

                    requestInADay.add(req);
                    requestHistoryPerDate.put(firstTimeOfDate, requestInADay);
                });
                List<RequestDateTreeItem> items = requestHistoryPerDate.entrySet().stream().map(e -> {
                    RequestDateTreeItem dateTreeItem = new RequestDateTreeItem(new Date(e.getKey()));
                    List<RequestHistoryTreeItem> chilren = e.getValue().stream().map(historyEntity -> {
                        return new RequestHistoryTreeItem(historyEntity, dateTreeItem);
                    }).sorted(new Comparator<RequestHistoryTreeItem>() {

                        @Override
                        public int compare(RequestHistoryTreeItem dateItem, RequestHistoryTreeItem dateItem2) {
                            return dateItem.getRequestHistoryEntity().getReceivedResponseTime().before(
                                    dateItem2.getRequestHistoryEntity().getReceivedResponseTime()) ? 1 : -1;
                        }
                    }).collect(Collectors.toList());
                    dateTreeItem.setItems(chilren);
                    return dateTreeItem;
                }).sorted(new Comparator<RequestDateTreeItem>() {

                    @Override
                    public int compare(RequestDateTreeItem dateItem, RequestDateTreeItem dateItem2) {
                        return dateItem.getDate().before(dateItem2.getDate()) ? 1 : -1;
                    }
                }).collect(Collectors.toList());
                elements = items;
                return elements.toArray();
            }
            return null;
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof IRequestHistoryItem) {
                IRequestHistoryItem requestDate = (IRequestHistoryItem) parentElement;
                return requestDate.getChildren().toArray();
            }
            return null;
        }

        @Override
        public Object getParent(Object element) {
            if (element instanceof IRequestHistoryItem) {
                return ((IRequestHistoryItem) element).getParent();
            }
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            if (element instanceof IRequestHistoryItem) {
                return ((IRequestHistoryItem) element).hasChildren();
            }
            return false;
        }
    }

    @Override
    public void addHistoryRequest(RequestHistoryEntity addedRequest) {
        if (treeViewer == null || treeViewer.getTree() == null || treeViewer.getTree().isDisposed()) {
            return;
        }

        reloadTreeData();

        RequestDateTreeItem dateItem = contentProvider.findElementByDate(addedRequest.getReceivedResponseTime());
        RequestHistoryTreeItem historyItem = new RequestHistoryTreeItem(addedRequest, dateItem);
        treeViewer.refresh(dateItem);
        treeViewer.setSelection(new StructuredSelection(historyItem));
        Event event = new Event();
        treeViewer.getTree().notifyListeners(SWT.Selection, event);
    }

    @Override
    public void removeHistoryRequests(List<RequestHistoryEntity> removedEntities) {
        reloadTreeData();
    }

    @Override
    public void resetInput() {
        if (treeViewer.getTree().isDisposed()) {
            return;
        }
        reloadTreeData();
    }
}
