package com.kms.katalon.composer.checkpoint.parts;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.checkpoint.constants.StringConstants;
import com.kms.katalon.composer.checkpoint.parts.providers.CheckpointCellLabelProvider;
import com.kms.katalon.composer.checkpoint.parts.supports.CheckpointCellEditingSupport;
import com.kms.katalon.composer.components.impl.constants.ImageConstants;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.tree.CheckpointTreeEntity;
import com.kms.katalon.composer.components.impl.tree.FolderTreeEntity;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.impl.util.EntityPartUtil;
import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.part.IComposerPart;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.CheckpointController;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.dal.exception.DALException;
import com.kms.katalon.entity.checkpoint.CheckpointCell;
import com.kms.katalon.entity.checkpoint.CheckpointEntity;
import com.kms.katalon.entity.checkpoint.CheckpointSourceInfo;

public abstract class CheckpointAbstractPart implements EventHandler, IComposerPart {

    private static final int DEFAULT_COLUMN_WIDTH = 200;

    @Inject
    protected EModelService modelService;

    @Inject
    protected MApplication application;

    @Inject
    protected IEventBroker eventBroker;

    @Inject
    protected MPart part;

    @Inject
    private MDirtyable dirtyable;

    @Inject
    private UISynchronize sync;

    private Composite compSourceInfoHeader;

    protected Composite compSourceInfoDetails;

    private Composite compTable;

    protected TableViewer tableViewer;

    private CLabel lblArrowIndicator;

    private Label lblSourceInfo;

    private Label lblStatus;

    protected Text txtSourceUrl;

    protected Button btnTakeSnapshot;

    protected CheckpointEntity checkpoint;

    protected CheckpointEntity tempCheckpoint;

    private int selectedColumnIndex = -1;

    @PostConstruct
    public void postConstruct(Composite parent) {
        createControls(parent);
        addControlListeners();
        redrawArrowIndicator();
        registerEventListeners();
        loadCheckpoint((CheckpointEntity) getPart().getObject());
        setDirty(false);
    }

    private void createControls(Composite parent) {
        parent.setLayout(new GridLayout());
        parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        createSourceInfoPart(parent);
        createDataTablePart(parent);
    }

    private Composite createSourceInfoPart(Composite parent) {
        createSourceInfoPartHeader(parent);
        createSourceInfoPartDetails(parent);
        createTakeSnapshotButton(compSourceInfoDetails);
        return parent;
    }

    private void createSourceInfoPartHeader(Composite parent) {
        // header composite (arrow bullet, source info label, status)
        compSourceInfoHeader = new Composite(parent, SWT.NONE);
        compSourceInfoHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout glCompositeFileInfoHeader = new GridLayout(3, false);
        glCompositeFileInfoHeader.marginWidth = 0;
        glCompositeFileInfoHeader.marginHeight = 0;
        compSourceInfoHeader.setLayout(glCompositeFileInfoHeader);
        compSourceInfoHeader.setCursor(compSourceInfoHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        lblArrowIndicator = new CLabel(compSourceInfoHeader, SWT.NONE);

        lblSourceInfo = new Label(compSourceInfoHeader, SWT.NONE);
        lblSourceInfo.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false, 1, 1));
        lblSourceInfo.setText(StringConstants.PART_SOURCE_INFO);
        ControlUtils.setFontToBeBold(lblSourceInfo);

        lblStatus = new Label(compSourceInfoHeader, SWT.NONE);
        GridData gdLblFileInfoStatus = new GridData(SWT.TRAIL, SWT.CENTER, true, false, 1, 1);
        gdLblFileInfoStatus.horizontalIndent = 5;
        lblStatus.setLayoutData(gdLblFileInfoStatus);
    }

    protected abstract Composite createSourceInfoPartDetails(Composite parent);

    private void createTakeSnapshotButton(Composite parent) {
        btnTakeSnapshot = new Button(parent, SWT.PUSH | SWT.FLAT);
        btnTakeSnapshot.setText(StringConstants.PART_BTN_TAKE_SNAPSHOT);
        btnTakeSnapshot.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));
    }

    private Composite createDataTablePart(Composite parent) {
        compTable = new Composite(parent, SWT.BORDER);
        GridLayout glCompTable = new GridLayout();
        glCompTable.marginHeight = 0;
        glCompTable.marginWidth = 0;
        compTable.setLayout(glCompTable);
        compTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        tableViewer = new TableViewer(compTable, SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.SINGLE);
        tableViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final Table table = tableViewer.getTable();
        table.setLinesVisible(true);

        TableViewerColumn tableViewerColumnNo = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tableColumnNo = tableViewerColumnNo.getColumn();
        tableColumnNo.setText(StringConstants.NO_);
        tableColumnNo.setWidth(40);
        tableViewerColumnNo.setLabelProvider(new CellLabelProvider() {

            @Override
            public void update(ViewerCell cell) {
                int order = table.indexOf((TableItem) cell.getItem()) + 1;
                cell.setText(Integer.toString(order));
            }
        });
        tableViewer.setContentProvider(ArrayContentProvider.getInstance());

        createContextMenu(table);
        return parent;
    }

    private void createContextMenu(final Table table) {
        // Create table context menu
        Menu tableContextMenu = new Menu(table);
        table.setMenu(tableContextMenu);

        // Create menu items
        MenuItem checkColumn = new MenuItem(tableContextMenu, SWT.PUSH);
        checkColumn.setText(StringConstants.PART_MENU_CHECK_COLUMN);
        checkColumn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                checkUncheckTableColumn(true);
            }
        });
        MenuItem uncheckColumn = new MenuItem(tableContextMenu, SWT.PUSH);
        uncheckColumn.setText(StringConstants.PART_MENU_UNCHECK_COLUMN);
        uncheckColumn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                checkUncheckTableColumn(false);
            }
        });

        MenuItem checkRow = new MenuItem(tableContextMenu, SWT.PUSH);
        checkRow.setText(StringConstants.PART_MENU_CHECK_ROW);
        checkRow.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                checkUncheckTableRow(true);
            }
        });
        MenuItem uncheckRow = new MenuItem(tableContextMenu, SWT.PUSH);
        uncheckRow.setText(StringConstants.PART_MENU_UNCHECK_ROW);
        uncheckRow.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                checkUncheckTableRow(false);
            }
        });

        MenuItem checkAll = new MenuItem(tableContextMenu, SWT.PUSH);
        checkAll.setText(StringConstants.PART_MENU_CHECK_ALL);
        checkAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                checkUncheckAll(true);
            }
        });
        MenuItem uncheckAll = new MenuItem(tableContextMenu, SWT.PUSH);
        uncheckAll.setText(StringConstants.PART_MENU_UNCHECK_ALL);
        uncheckAll.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                checkUncheckAll(false);
            }
        });

        table.addMenuDetectListener(new MenuDetectListener() {

            @Override
            public void menuDetected(MenuDetectEvent e) {
                selectedColumnIndex = -1;
                ViewerCell cell = tableViewer.getCell(Display.getDefault().map(null, table, new Point(e.x, e.y)));
                if (cell != null) {
                    selectedColumnIndex = cell.getColumnIndex();
                }
                e.doit = selectedColumnIndex != -1;
            }
        });
    }

    private void checkUncheckTableColumn(boolean isChecked) {
        if (selectedColumnIndex == -1) {
            return;
        }

        TableItem[] rows = tableViewer.getTable().getItems();
        if (rows == null || rows.length == 0) {
            return;
        }

        for (TableItem row : rows) {
            Object rowData = row.getData();
            if (rowData == null || !(rowData instanceof List<?>) || ((List<?>) rowData).isEmpty()
                    || selectedColumnIndex > ((List<?>) rowData).size()) {
                continue;
            }

            ((CheckpointCell) ((List<?>) rowData).get(selectedColumnIndex - 1)).setChecked(isChecked);
        }

        tableViewer.refresh();
        dirtyable.setDirty(true);
    }

    protected void checkUncheckTableRow(boolean isChecked) {
        Table table = tableViewer.getTable();
        int index = table.getSelectionIndex();
        if (index == -1) {
            // no row selected
            return;
        }

        Object rowData = table.getItem(index).getData();
        if (rowData == null || !(rowData instanceof List<?>) || ((List<?>) rowData).isEmpty()) {
            return;
        }
        for (Object cellData : (List<?>) rowData) {
            if (!(cellData instanceof CheckpointCell)) {
                continue;
            }
            ((CheckpointCell) cellData).setChecked(isChecked);
        }
        tableViewer.refresh(rowData);
        dirtyable.setDirty(true);
    }

    protected void checkUncheckAll(boolean isChecked) {
        TableItem[] rows = tableViewer.getTable().getItems();
        if (rows == null || rows.length == 0) {
            return;
        }
        for (TableItem row : rows) {
            Object rowData = row.getData();
            if (rowData == null || !(rowData instanceof List<?>) || ((List<?>) rowData).isEmpty()) {
                continue;
            }

            for (Object cellData : (List<?>) rowData) {
                if (!(cellData instanceof CheckpointCell)) {
                    continue;
                }
                ((CheckpointCell) cellData).setChecked(isChecked);
            }
        }
        tableViewer.refresh();
        dirtyable.setDirty(true);
    }

    private void redrawArrowIndicator() {
        lblArrowIndicator.getParent().setRedraw(false);
        lblArrowIndicator.setImage(compSourceInfoDetails.isVisible() ? ImageConstants.IMG_16_ARROW_DOWN
                : ImageConstants.IMG_16_ARROW);
        lblArrowIndicator.getParent().setRedraw(true);
    }

    private void resizeTableOnElasticSourceInfo() {
        Display.getDefault().timerExec(10, new Runnable() {

            @Override
            public void run() {
                // hide source info composite
                compSourceInfoDetails.setVisible(!compSourceInfoDetails.isVisible());
                // elasticate data table
                GridData fileInfoGridData = (GridData) compSourceInfoDetails.getLayoutData();
                fileInfoGridData.exclude = !compSourceInfoDetails.isVisible();
                compTable.layout(true, true);
                compTable.getParent().layout();
                redrawArrowIndicator();
            }
        });
    }

    private void addControlListeners() {
        MouseAdapter sourceInfoClickListener = new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                resizeTableOnElasticSourceInfo();
            }
        };
        lblArrowIndicator.addMouseListener(sourceInfoClickListener);
        lblSourceInfo.addMouseListener(sourceInfoClickListener);
        compSourceInfoHeader.addMouseListener(sourceInfoClickListener);

        btnTakeSnapshot.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    if (getCheckpoint().getCheckpointData() != null
                            && !MessageDialog.openConfirm(Display.getCurrent().getActiveShell(),
                                    StringConstants.PART_TITLE_TAKE_CHECKPOINT_SNAPSHOT,
                                    StringConstants.PART_MSG_CLEAR_CHECKPOINT_DATA)) {
                        return;
                    }
                    loadCheckpoint(CheckpointController.getInstance().takeSnapshot(getCheckpoint()));
                    setDirty(false);
                } catch (Exception ex) {
                    LoggerSingleton.logError(ex);
                    MultiStatusErrorDialog.showErrorDialog(ex, StringConstants.PART_MSG_CANNOT_TAKE_SNAPSHOT,
                            ex.getMessage());
                }
            }
        });

        addSourceInfoConstrolListeners();
    }

    protected abstract void addSourceInfoConstrolListeners();

    private void registerEventListeners() {
        eventBroker.subscribe(EventConstants.CHECKPOINT_UPDATED, this);
        eventBroker.subscribe(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, this);
    }

    @Override
    public void handleEvent(Event event) {
        Object eventData = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
        if (eventData == null) {
            return;
        }

        switch (event.getTopic()) {
            case EventConstants.CHECKPOINT_UPDATED:
                updateCheckpointEventAction(eventData);
                break;
            case EventConstants.EXPLORER_REFRESH_SELECTED_ITEM:
                refreshCheckpointEventAction(eventData);
                break;
            default:
                break;
        }
    }

    private void refreshCheckpointEventAction(Object eventData) {
        if (!(eventData instanceof CheckpointTreeEntity)) {
            return;
        }

        try {
            CheckpointEntity checkpointEntity = ((CheckpointTreeEntity) eventData).getObject();
            if (!StringUtils.equals(checkpointEntity.getId(), getCheckpoint().getId())) {
                return;
            }

            if (CheckpointController.getInstance().getById(checkpointEntity.getId()) == null) {
                preDestroy();
                return;
            }

            if (isDirty()) {
                verifySourceChanged();
                return;
            }

            loadCheckpoint(checkpointEntity);
            setDirty(false);
        } catch (DALException e) {
            LoggerSingleton.logError(e);
        }
    }

    private void updateCheckpointEventAction(Object eventData) {
        if (!eventData.getClass().isArray() || ((Object[]) eventData).length != 2) {
            return;
        }

        Object[] data = (Object[]) eventData;
        String id = EntityPartUtil.getCheckpointPartId(ObjectUtils.toString(data[0]));
        if (!StringUtils.equals(id, getPart().getElementId())) {
            return;
        }

        loadCheckpoint((CheckpointEntity) data[1]);
    }

    private void verifySourceChanged() {
        try {
            if (getCheckpoint() == null) {
                return;
            }

            CheckpointEntity reloadedCheckpoint = CheckpointController.getInstance().getById(getCheckpoint().getId());
            if (reloadedCheckpoint == null) {
                FolderTreeEntity parentFolderTreeEntity = TreeEntityUtil.createSelectedTreeEntityHierachy(
                        getCheckpoint().getParentFolder(),
                        FolderController.getInstance().getCheckpointRoot(getCheckpoint().getProject()));
                if (parentFolderTreeEntity != null) {
                    eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, parentFolderTreeEntity);
                }
                preDestroy();
                return;
            }

            if (getCheckpoint().equals(reloadedCheckpoint)) {
                return;
            }

            if (MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), StringConstants.PART_TITLE_RELOAD,
                    StringConstants.PART_MSG_RELOAD_FILE_CONTENT)) {
                loadCheckpoint(reloadedCheckpoint);
                setDirty(false);
            }
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    protected void loadCheckpoint(CheckpointEntity checkpoint) {
        this.checkpoint = checkpoint;
        this.tempCheckpoint = (CheckpointEntity) checkpoint.clone();
        getPart().setLabel(checkpoint.getName());
        getPart().setElementId(EntityPartUtil.getCheckpointPartId(checkpoint.getId()));
        loadCheckpointSourceInfo(checkpoint.getSourceInfo());
        loadCheckpointData(tempCheckpoint);
        updateStatus();
    }

    protected abstract void loadCheckpointSourceInfo(CheckpointSourceInfo checkpoint);

    /**
     * Load checkpoint data into table
     * 
     * @param checkpoint {@link CheckpointEntity}
     */
    private void loadCheckpointData(CheckpointEntity checkpoint) {
        tableViewer.getTable().setRedraw(false);

        clearTable();
        tableViewer.getTable().setHeaderVisible(true);

        CheckpointSourceInfo sourceInfo = checkpoint.getSourceInfo();
        List<List<CheckpointCell>> checkpointData = checkpoint.getCheckpointData();
        List<String> columnNames = checkpoint.getColumnNames();
        if (sourceInfo == null || checkpointData == null || checkpointData.isEmpty()) {
            tableViewer.getTable().setRedraw(true);
            return;
        }

        for (int i = 0; i < checkpointData.get(0).size(); i++) {
            final TableViewerColumn columnViewer = new TableViewerColumn(tableViewer, SWT.NONE);
            columnViewer.setLabelProvider(new CheckpointCellLabelProvider(i));
            columnViewer.setEditingSupport(new CheckpointCellEditingSupport(columnViewer.getViewer(), i, dirtyable));
            TableColumn column = columnViewer.getColumn();
            column.setWidth(DEFAULT_COLUMN_WIDTH);
            column.setText(StringUtils.defaultString(columnNames.get(i)));
        }

        tableViewer.setInput(checkpointData);
        tableViewer.getTable().setRedraw(true);
    }

    private void clearTable() {
        Table table = tableViewer.getTable();
        while (table.getColumnCount() > 1) {
            table.getColumns()[1].dispose();
        }
        tableViewer.setInput(Collections.emptyList());
    }

    private void updateStatus() {
        sync.asyncExec(new Runnable() {
            @Override
            public void run() {
                Color color = ColorUtil.getWarningForegroudColor();
                String info = StringConstants.PART_LBL_NO_CHECKPOINT_DATA;
                Date takenDate = getCheckpoint().getTakenDate();
                if (takenDate != null) {
                    color = ColorUtil.getTextSuccessfulColor();
                    info = MessageFormat.format(StringConstants.PART_LBL_SNAPSHOT_WAS_TAKEN_ON_X, takenDate.toString());
                }
                lblStatus.setForeground(color);
                lblStatus.setText(info);
                compSourceInfoHeader.layout();
            }
        });
    }

    @Persist
    public void save() {
        try {
            checkpoint.setCheckpointData(tempCheckpoint.getCheckpointData());
            CheckpointController.getInstance().update(checkpoint);
            setDirty(false);
            eventBroker.post(EventConstants.CHECKPOINT_UPDATED,
                    new Object[] { checkpoint.getIdForDisplay(), checkpoint });
            CheckpointTreeEntity checkpointTreeEntity = TreeEntityUtil.getCheckpointTreeEntity(checkpoint);
            eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, checkpointTreeEntity);
            eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, checkpointTreeEntity);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.ERROR,
                    StringConstants.PART_MSG_UNABLE_TO_SAVE_CHECKPOINT);
        }
    }

    @Focus
    public void onFocused() {
        verifySourceChanged();
    }

    @PreDestroy
    public void preDestroy() {
        eventBroker.unsubscribe(this);
        MPartStack mStackPart = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
        mStackPart.getChildren().remove(getPart());
    }

    protected void setDirty(boolean isDirty) {
        dirtyable.setDirty(isDirty);
    }

    public boolean isDirty() {
        return dirtyable.isDirty();
    }

    public MPart getPart() {
        return part;
    }

    public CheckpointEntity getCheckpoint() {
        return checkpoint;
    }

    @Override
    public String getEntityId() {
        return getCheckpoint().getIdForDisplay();
    }

}
