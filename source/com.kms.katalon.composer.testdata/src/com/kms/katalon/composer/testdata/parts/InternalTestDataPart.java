package com.kms.katalon.composer.testdata.parts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.kms.katalon.composer.components.impl.control.CTableViewer;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testdata.constants.ImageConstants;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.composer.testdata.parts.provider.InternalDataAddColumnLabelProvider;
import com.kms.katalon.composer.testdata.parts.provider.InternalDataColumViewerEditor;
import com.kms.katalon.composer.testdata.parts.provider.InternalDataEditingSupport;
import com.kms.katalon.composer.testdata.parts.provider.InternalDataLabelProvider;
import com.kms.katalon.composer.testdata.views.NewTestDataColumnDialog;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testdata.DataFileEntity.DataFileDriverType;
import com.kms.katalon.entity.testdata.InternalDataColumnEntity;
import com.kms.katalon.entity.testdata.InternalDataFilePropertyEntity;

public class InternalTestDataPart extends TestDataMainPart {

    private static final int DF_UNREMOVEVABLE_COLUMN_WIDTH = 40;

    private static final int DF_REMOVEVABLE_COLUMN_WIDTH = 100;

    private static final int COLUMN_NO_IDX = 0;

    public static final int BASE_COLUMN_INDEX = 1;

    private static final int UNREMOVABLE_COLUMNS = 2;

    private static final int LEFT_CLICK = 1;

    @Inject
    private EPartService partService;

    private List<InternalDataRow> input;

    private Table table;

    private CTableViewer tableViewer;

    @Override
    protected EPartService getPartService() {
        return partService;
    }

    @Override
    protected void preDestroy() {
    }

    @Focus
    public void setFocus() {
        table.setFocus();
    }

    @Override
    protected Composite createFileInfoPart(Composite parent) {
        // Internal data doesn't need to create File Info Part
        return null;
    }

    @PostConstruct
    public void createControls(Composite parent, MPart mpart) {
        super.createControls(parent, mpart);
    }

    @Override
    public Composite createDataTablePart(Composite parent) {
        parent.setLayout(new GridLayout(1, true));
        parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite tableViewerComposite = new Composite(parent, SWT.BORDER);
        tableViewerComposite.setLayout(new GridLayout(1, true));
        tableViewerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createContents(tableViewerComposite);
        return tableViewerComposite;
    }

    /**
     * @wbp.parser.entryPoint
     */
    private void createContents(final Composite parent) {
        parent.setLayout(new FillLayout());

        // Create the table
        tableViewer = new CTableViewer(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
        table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        createColumnViewer(StringConstants.PA_COL_NO, SWT.CENTER, false, DF_UNREMOVEVABLE_COLUMN_WIDTH,
                new InternalDataAddColumnLabelProvider(), null);

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());

        // Enable editing on Tab move
        InternalDataColumViewerEditor.create(tableViewer, new ColumnViewerEditorActivationStrategy(tableViewer),
                InternalDataColumViewerEditor.TABBING_HORIZONTAL | InternalDataColumViewerEditor.KEYBOARD_ACTIVATION
                        | InternalDataColumViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
                        | InternalDataColumViewerEditor.TABBING_CYCLE_IN_TABLE);

        addMouseEventListenersForTable(table);

        // Enable tool-tip
        table.setToolTipText(StringUtils.EMPTY);
        ColumnViewerToolTipSupport.enableFor(tableViewer);
    }

    private TableViewerColumn createColumnViewer(String name, int style, boolean resizable, int width,
            CellLabelProvider labelProvider, EditingSupport editingSupport) {
        TableViewerColumn tableColumnViewer = new TableViewerColumn(tableViewer, style);
        addInfoForTableColumn(tableColumnViewer, name, resizable, width, labelProvider, editingSupport);
        return tableColumnViewer;
    }

    private static void addInfoForTableColumn(TableViewerColumn tableViewerColumn, String name, boolean resizable,
            int width, CellLabelProvider labelProvider, EditingSupport editingSupport) {
        TableColumn tableColumn = tableViewerColumn.getColumn();
        tableColumn.setWidth(width);
        tableColumn.setText(name);
        tableColumn.setResizable(resizable);
        tableViewerColumn.setLabelProvider(labelProvider);
        if (editingSupport != null) {
            tableViewerColumn.setEditingSupport(editingSupport);
        }
    }

    private void createColumns(DataFileEntity dataFile) {
        for (InternalDataColumnEntity dataCol : dataFile.getInternalDataColumns()) {
            createColumnViewer(dataCol.getName(), SWT.NONE, true, DF_REMOVEVABLE_COLUMN_WIDTH,
                    new InternalDataLabelProvider(), new InternalDataEditingSupport(this, tableViewer));
        }

        // create Add column
        TableViewerColumn tableViewerColumnAdd = createColumnViewer(StringUtils.EMPTY, SWT.CENTER, false,
                DF_UNREMOVEVABLE_COLUMN_WIDTH, new InternalDataLabelProvider(), null);
        TableColumn columnAdd = tableViewerColumnAdd.getColumn();
        columnAdd.setImage(ImageConstants.IMG_16_ADD);
        columnAdd.setToolTipText(StringConstants.PA_TOOL_TIP_ADD_COLUMN);

        columnAdd.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                openAddColumnDialog(table.getColumnCount() - BASE_COLUMN_INDEX);
            }
        });
        table.redraw();
    }

    private void addMouseEventListenersForTable(final Table table) {
        table.addListener(SWT.MenuDetect, new Listener() {
            public void handleEvent(Event event) {
                Point cursorPt = table.getDisplay().map(null, table, new Point(event.x, event.y));
                Point plusedHorizontalPt = new Point(cursorPt.x + table.getHorizontalBar().getSelection(), cursorPt.y);
                Rectangle clientArea = table.getClientArea();
                boolean isHeader = cursorPt.y >= clientArea.y && cursorPt.y < (clientArea.y + table.getHeaderHeight());
                int colIndex = -1;
                int theWidth = 0;
                for (int i = 0; i < table.getColumns().length; i++) {
                    TableColumn tc = table.getColumns()[i];
                    if (theWidth < plusedHorizontalPt.x && plusedHorizontalPt.x < theWidth + tc.getWidth()) {
                        colIndex = i;
                        break;
                    }
                    theWidth += tc.getWidth();
                }
                createContextMenu(colIndex, isHeader, cursorPt);
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseDown(MouseEvent event) {
                if (event.button == LEFT_CLICK) {
                    handleLeftClick(new Point(event.x, event.y));
                }
            }
        });
    }

    private void handleLeftClick(Point pt) {
        ViewerCell viewerCell = tableViewer.getCell(pt);
        if (viewerCell == null) {
            return;
        }

        Object selectedObject = viewerCell.getElement();
        if (!(selectedObject instanceof InternalDataRow)) {
            return;
        }

        InternalDataRow element = (InternalDataRow) selectedObject;
        if (element.isLastRow()) {
            int lastCollectionIndex = lastCollectionIndex(input);
            insertNewRow(lastCollectionIndex);
        }
    }

    private void insertNewRow(int indexToInsert) {
        executeOperation(new InsertNewRowOperation(indexToInsert));
    }

    private MenuItem createMenuItem(Menu parent, String text, int style, SelectionListener listener) {
        MenuItem menuItemInsertRow = new MenuItem(parent, style);
        menuItemInsertRow.setText(text);
        if (listener != null) {
            menuItemInsertRow.addSelectionListener(listener);
        }
        return menuItemInsertRow;
    }

    private Menu createContextMenu(final int selectedColIndex, boolean isHeader, Point point) {
        Menu menu = table.getMenu();
        if (menu != null && menu.isDisposed()) {
            menu.dispose();
        }

        menu = new Menu(table);
        menu.setData(point);

        createMenuItem(menu, StringConstants.PA_MENU_CONTEXT_INSERT_ROW, SWT.PUSH, new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                insertNewRow(getRowIndexOnMouse(event));
            }
        });

        Menu menuInsertColumn = new Menu(menu);
        MenuItem menuItemInsertColumn = createMenuItem(menu, StringConstants.PA_MENU_CONTEXT_INSERT_COL, SWT.CASCADE,
                null);
        menuItemInsertColumn.setMenu(menuInsertColumn);

        // Insert column left is available unless the selected item is "No." column
        if (selectedColIndex > COLUMN_NO_IDX) {
            createMenuItem(menuInsertColumn, StringConstants.PA_MENU_CONTEXT_INSERT_COL_TO_THE_LEFT, SWT.PUSH,
                    new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            openAddColumnDialog(selectedColIndex);
                        }
                    });
        }

        // Insert column right is available unless the selected item is "Add" column
        if (selectedColIndex < lastColumnIndex()) {
            createMenuItem(menuInsertColumn, StringConstants.PA_MENU_CONTEXT_INSERT_COL_TO_THE_RIGHT, SWT.PUSH,
                    new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            int newColumnIndex = selectedColIndex >= 0
                                    ? Math.min(selectedColIndex + BASE_COLUMN_INDEX, lastColumnIndex())
                                    : lastColumnIndex();
                            openAddColumnDialog(newColumnIndex);
                        }
                    });
        }

        // Add "Rename Column" menu item
        if (isEditableColumn(selectedColIndex)) {
            createMenuItem(menu, StringConstants.PA_MENU_CONTEXT_RENAME_COL, SWT.PUSH, new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    TableColumn selectedCol = table.getColumn(selectedColIndex);
                    NewTestDataColumnDialog dialog = new NewTestDataColumnDialog(Display.getDefault().getActiveShell(),
                            selectedCol.getText(), getCurrentColumnNames());
                    if (dialog.open() != Window.OK) {
                        return;
                    }

                    String columnName = dialog.getName();
                    renameColumn(selectedCol, columnName);
                }
            });
        }

        Menu menuDelete = new Menu(menu);
        MenuItem menuItemDelete = createMenuItem(menu, StringConstants.PA_MENU_CONTEXT_DEL, SWT.CASCADE, null);
        menuItemDelete.setMenu(menuDelete);

        if (isEditableColumn(selectedColIndex)) {
            createMenuItem(menuDelete, StringConstants.PA_MENU_CONTEXT_DEL_COL, SWT.PUSH, new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    deleteColumn(selectedColIndex);
                    dirtyable.setDirty(true);
                }
            });
        }

        if (isTableContainingRow()) {
            createMenuItem(menuDelete, StringConstants.PA_MENU_CONTEXT_DEL_ROWS, SWT.PUSH, new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    deleteSelectedRows();
                }
            });
        }
        table.setMenu(menu);

        return menu;
    }

    private void renameColumn(TableColumn selectedCol, String columnName) {
        executeOperation(new RenameColumnOperation(selectedCol, columnName));
    }

    private void deleteSelectedRows() {
        executeOperation(new DeleteRowsOperation());
    }

    private int getRowIndexOnMouse(SelectionEvent event) {
        Point pt = (Point) table.getMenu().getData();
        ViewerCell viewerCell = tableViewer.getCell(pt);
        if (viewerCell == null) {
            return lastCollectionIndex(input);
        }
        return input.indexOf(viewerCell.getElement());
    }

    private boolean isEditableColumn(int selectedColIndex) {
        return selectedColIndex > COLUMN_NO_IDX && selectedColIndex < lastColumnIndex();
    }

    private boolean isTableContainingRow() {
        return table.getItemCount() > 1;
    }

    private void insertNewColumn(int index, String title) {
        executeOperation(new InsertColumnOperation(index, title));
    }

    private void internalInsertNewColumn(int index, String title) {
        TableViewerColumn tableViewColumn = new TableViewerColumn(tableViewer, SWT.NONE, index);
        addInfoForTableColumn(tableViewColumn, title, true, DF_REMOVEVABLE_COLUMN_WIDTH,
                new InternalDataLabelProvider(), new InternalDataEditingSupport(this, tableViewer));
        for (InternalDataRow dataRow : input) {
            dataRow.getCells().add(getUpperBoundColumnIndex(dataRow, index), InternalDataCell.newEmptyCell());
        }
        refresh();
    }

    private void refresh() {
        tableViewer.refresh();
        tableViewer.getTable().setFocus();
    }

    private void deleteColumn(int index) {
        executeOperation(new DeleteColumnOperation(index));
    }

    private void internalDeleteColumn(int index) {
        for (InternalDataRow dataRow : input) {
            if (dataRow.isLastRow()) {
                continue;
            }
            dataRow.getCells().remove(index - BASE_COLUMN_INDEX);
        }

        TableColumn tblColumn = table.getColumn(index);
        if (tblColumn != null) {
            tblColumn.dispose();
            table.redraw();
        }

        refresh();
    }

    @Persist
    public void save() {
        // commit edit
        table.forceFocus();

        try {
            originalDataFile = updateInternalDataFileProperty(originalDataFile.getLocation(),
                    originalDataFile.getName(), originalDataFile.getDescription(), originalDataFile.getDriver(),
                    originalDataFile.getDataSourceUrl(), "", originalDataFile.getTableDataName(),
                    getCurrentDataColumnEntities(), tableInputToData());
            updateDataFile(originalDataFile);
            dirtyable.setDirty(false);
            refreshTreeEntity();
            sendTestDataUpdatedEvent(originalDataFile.getId());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PA_ERROR_MSG_UNABLE_TO_SAVE_TEST_DATA,
                    e.getClass().getSimpleName());
        }
    }

    private DataFileEntity updateInternalDataFileProperty(String pk, String name, String description,
            DataFileDriverType dataFileDriver, String dataSourceURL, String sheetName, String tableName,
            List<Object> headerColumn, List<List<Object>> data) throws Exception {

        InternalDataFilePropertyEntity internalData = new InternalDataFilePropertyEntity();

        internalData.setPk(pk);
        internalData.setName(name);
        internalData.setDescription(description);
        internalData.setDataFileDriverName(dataFileDriver.name());
        internalData.setDataSourceURL(dataSourceURL);
        internalData.setSheetName(sheetName);
        internalData.setTableName(tableName);
        internalData.setHeaderColumn(headerColumn);
        internalData.setData(data);

        DataFileEntity dataFileEntity = TestDataController.getInstance().updateDataFile(internalData);
        return dataFileEntity;
    }

    @Override
    protected void updateChildInfo(DataFileEntity dataFile) {
        clearTable();

        createColumns(dataFile);

        updateTableInput(dataFile);
    }

    private void updateTableInput(DataFileEntity dataFile) {
        input = getTableInput(dataFile);
        tableViewer.setInput(input);
    }

    private void openAddColumnDialog(int selectedColIndex) {
        NewTestDataColumnDialog dialog = new NewTestDataColumnDialog(Display.getDefault().getActiveShell(),
                getCurrentColumnNames());
        if (dialog.open() == Window.OK) {
            insertNewColumn(selectedColIndex, dialog.getName());
            dirtyable.setDirty(true);
        }
    }

    private void clearTable() {
        table.setRedraw(false);

        while (table.getColumnCount() > BASE_COLUMN_INDEX) {
            table.getColumns()[BASE_COLUMN_INDEX].dispose();
        }
        table.removeAll();
        table.setRedraw(true);
    }

    private int lastColumnIndex() {
        return table.getColumnCount() - BASE_COLUMN_INDEX;
    }

    private int lastCollectionIndex(Collection<?> collection) {
        return collection.size() - 1;
    }

    private String[] getCurrentColumnNames() {
        if (table.getColumnCount() < UNREMOVABLE_COLUMNS) {
            return new String[0];
        }
        String[] columnNames = new String[getEditableColumnsSize()];
        for (int i = BASE_COLUMN_INDEX; i < lastColumnIndex(); i++) {
            columnNames[i - BASE_COLUMN_INDEX] = table.getColumn(i).getText();
        }
        return columnNames;
    }

    private int getEditableColumnsSize() {
        return Math.max(0, table.getColumnCount() - UNREMOVABLE_COLUMNS);
    }

    private List<Object> getCurrentDataColumnEntities() {
        List<Object> dataColumnEntities = new ArrayList<>();
        String[] currentColumnNames = getCurrentColumnNames();
        for (int i = 0; i < currentColumnNames.length; i++) {
            InternalDataColumnEntity dataColumn = new InternalDataColumnEntity();
            dataColumn.setColumnIndex(i);
            dataColumn.setName(currentColumnNames[i]);
            dataColumn.setSize(input.size() - 1);

            dataColumnEntities.add(dataColumn);
        }
        return dataColumnEntities;
    }

    private List<InternalDataRow> getTableInput(DataFileEntity dataFile) {
        List<InternalDataRow> dataRows = new ArrayList<>();

        List<InternalDataColumnEntity> internalDataColumns = dataFile.getInternalDataColumns();
        for (List<Object> rowData : dataFile.getData()) {
            InternalDataRow tableRow = new InternalDataRow();
            for (int i = 0; i < internalDataColumns.size(); i++) {
                InternalDataCell tableCell = new InternalDataCell(Objects.toString(rowData.get(i)));
                tableRow.getCells().add(i, tableCell);
            }

            dataRows.add(tableRow);
        }
        dataRows.add(InternalDataRow.newLastRow());
        return dataRows;
    }

    private List<List<Object>> tableInputToData() {
        List<List<Object>> data = new ArrayList<>();
        for (InternalDataRow tableRow : input) {
            if (tableRow.isLastRow()) {
                continue;
            }

            List<Object> rowData = new ArrayList<>();
            for (int i = 0; i < getCurrentColumnNames().length; i++) {
                rowData.add(tableRow.getCells().get(i).getValue());

            }
            data.add(rowData);
        }
        return data;
    }

    private int getUpperBoundColumnIndex(int index) {
        return index >= 0 ? Math.min(index, lastColumnIndex()) : lastColumnIndex();
    }

    private int getUpperBoundColumnIndex(InternalDataRow dataRow, int index) {
        return Math.min(lastCollectionIndex(dataRow.getCells()), index - BASE_COLUMN_INDEX);
    }

    private class InsertNewRowOperation extends AbstractOperation {
        private int insertIndex;

        public InsertNewRowOperation(int insertIndex) {
            super(InsertNewRowOperation.class.getName());
            this.insertIndex = insertIndex;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return redo(monitor, info);
        }

        private void doInsert(int indexToInsert) {
            InternalDataRow newRow = new InternalDataRow();
            int editableColumnsSize = getEditableColumnsSize();
            for (int i = 0; i < editableColumnsSize; i++) {
                newRow.getCells().add(i, InternalDataCell.newEmptyCell());
            }
            input.add(indexToInsert, newRow);
            tableViewer.refresh();
            tableViewer.setSelection(new StructuredSelection(newRow));
            table.showItem(table.getItem(indexToInsert));
            dirtyable.setDirty(true);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doInsert(insertIndex);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            input.remove(insertIndex);
            refresh();
            dirtyable.setDirty(true);
            return Status.OK_STATUS;
        }
    }

    private class InsertColumnOperation extends AbstractOperation {
        private int index;

        private String title;

        public InsertColumnOperation(int index, String title) {
            super(InsertColumnOperation.class.getName());
            this.index = index;
            this.title = title;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            internalInsertNewColumn(index, title);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            internalDeleteColumn(getUpperBoundColumnIndex(index));
            return Status.OK_STATUS;
        }
    }

    private class DeleteColumnOperation extends AbstractOperation {
        private int index;

        private List<InternalDataCell> deleteDatas = new ArrayList<>();

        private String oldColumnTitle;

        public DeleteColumnOperation(int index) {
            super(DeleteColumnOperation.class.getName());
            this.index = index;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            collectDeletedData();
            return redo(monitor, info);
        }

        private void collectDeletedData() {
            for (InternalDataRow dataRow : input) {
                if (dataRow.isLastRow()) {
                    continue;
                }
                deleteDatas.add(dataRow.getCells().get(index - BASE_COLUMN_INDEX));
            }

            TableColumn tblColumn = table.getColumn(index);
            if (tblColumn != null) {
                oldColumnTitle = tblColumn.getText();
            }
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            internalDeleteColumn(index);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            internalInsertNewColumn(index, oldColumnTitle);
            int deleteDataIndex = 0;
            for (InternalDataRow dataRow : input) {
                if (dataRow.isLastRow()) {
                    continue;
                }
                dataRow.getCells().set(getUpperBoundColumnIndex(dataRow, index), deleteDatas.get(deleteDataIndex));
                deleteDataIndex++;
            }
            refresh();
            return Status.OK_STATUS;
        }
    }
    
    private class DeleteRowsOperation extends AbstractOperation {
        private Map<InternalDataRow, Integer> deletedDataRows = new HashMap<>();
        
        public DeleteRowsOperation() {
            super(DeleteRowsOperation.class.getName());
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            StructuredSelection selection = (StructuredSelection) tableViewer.getSelection();
            if (selection != null && selection.isEmpty()) {
                return Status.CANCEL_STATUS;
            }

            boolean changed = false;
            for (Object selected : selection.toArray()) {
                InternalDataRow dataRow = (InternalDataRow) selected;
                if (!dataRow.isLastRow()) {
                    deletedDataRows.put(dataRow, input.indexOf(dataRow));
                    input.remove(dataRow);
                    changed = true;
                }
            }
            if (!changed) {
                return Status.CANCEL_STATUS;
            }
            refresh();
            dirtyable.setDirty(true);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            for (Entry<InternalDataRow, Integer> deletedRowEntry : deletedDataRows.entrySet()) {
                input.remove(deletedRowEntry.getKey());
            }
            refresh();
            dirtyable.setDirty(true);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            for (Entry<InternalDataRow, Integer> deletedRowEntry : deletedDataRows.entrySet()) {
                input.add(deletedRowEntry.getValue(), deletedRowEntry.getKey());
            }
            refresh();
            dirtyable.setDirty(true);
            tableViewer.setSelection(new StructuredSelection(deletedDataRows.keySet().toArray()));
            return Status.OK_STATUS;
        }
    }
    
    private class RenameColumnOperation extends AbstractOperation {
        private TableColumn selectedColumn;
        private String newName;
        private String oldName;
        
        public RenameColumnOperation(TableColumn selectedColumn, String newName) {
            super(RenameColumnOperation.class.getName());
            this.selectedColumn = selectedColumn;
            this.newName = newName;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            oldName = selectedColumn.getText();
            return redo(monitor, info);
        }

        private void doRename(String columnName) {
            table.setRedraw(false);
            selectedColumn.setText(columnName);
            table.setRedraw(true);
            dirtyable.setDirty(true);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doRename(newName);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doRename(oldName);
            return Status.OK_STATUS;
        }
        
    }
}
