package com.kms.katalon.composer.testdata.parts;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.controls.HelpToolBarForMPart;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.testdata.constants.ImageConstants;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.composer.testdata.dialog.EditTestDataQueryDialog;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.core.db.DatabaseConnection;
import com.kms.katalon.core.testdata.DBData;
import com.kms.katalon.entity.testdata.DataFileEntity;

public class DBTestDataPart extends TestDataMainPart {

    private static final int MAX_ROW_LIMIT = 500;

    @Inject
    private EPartService partService;

    @Inject
    private UISynchronize sync;

    private Composite compFileInfoHeader;

    private Composite compFileInfoDetails;

    private Composite compTable;

    private TableViewer tableViewer;

    private CLabel lblArrowIndicator;

    private Label lblFileInfo;

    private Label lblStatus;

    private Text txtQuery;

    private Button btnFetchData;

    private Button btnEdit;

    private Button ckcbReadAsString;
    
    @Override
    protected EPartService getPartService() {
        return partService;
    }

    @Override
    @PostConstruct
    public void createControls(Composite parent, MPart mpart) {
        new HelpToolBarForMPart(mpart, DocumentationMessageConstants.TEST_DATA_DATABASE);
        super.createControls(parent, mpart);
        addControlListeners();
        redrawArrowIndicator();
    }

    @Override
    protected Composite createFileInfoPart(Composite parent) {
        parent.setLayout(new GridLayout());
        parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // header composite (arrow bullet, file info label, status fetched data date status)
        compFileInfoHeader = new Composite(parent, SWT.NONE);
        compFileInfoHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout glCompositeFileInfoHeader = new GridLayout(3, false);
        glCompositeFileInfoHeader.marginWidth = 0;
        glCompositeFileInfoHeader.marginHeight = 0;
        compFileInfoHeader.setLayout(glCompositeFileInfoHeader);
        compFileInfoHeader.setCursor(compFileInfoHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        lblArrowIndicator = new CLabel(compFileInfoHeader, SWT.NONE);

        lblFileInfo = new Label(compFileInfoHeader, SWT.NONE);
        lblFileInfo.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false, 1, 1));
        lblFileInfo.setText(StringConstants.PA_LBL_FILE_INFO);
        ControlUtils.setFontToBeBold(lblFileInfo);

        lblStatus = new Label(compFileInfoHeader, SWT.NONE);
        GridData gd_lblFileInfoStatus = new GridData(SWT.TRAIL, SWT.CENTER, true, false, 1, 1);
        gd_lblFileInfoStatus.horizontalIndent = 5;
        lblStatus.setLayoutData(gd_lblFileInfoStatus);

        // info composite
        compFileInfoDetails = new Composite(parent, SWT.NONE);
        GridLayout glCompositeFileInfoDetails = new GridLayout(2, false);
        glCompositeFileInfoDetails.marginWidth = 0;
        glCompositeFileInfoDetails.marginHeight = 0;
        compFileInfoDetails.setLayout(glCompositeFileInfoDetails);
        compFileInfoDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        
        ckcbReadAsString = new Button(compFileInfoDetails, SWT.CHECK);
        ckcbReadAsString.setText(StringConstants.VIEW_LBL_READ_AS_STRING);

        Label lblQuery = new Label(compFileInfoDetails, SWT.BOLD);
        lblQuery.setLayoutData(new GridData(SWT.LEAD, SWT.CENTER, false, false, 2, 1));
        lblQuery.setText(StringConstants.DIA_LBL_SQL_QUERY);

        txtQuery = new Text(compFileInfoDetails, SWT.BORDER | SWT.READ_ONLY | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 2);
        gd.heightHint = 50;
        txtQuery.setLayoutData(gd);

        btnEdit = new Button(compFileInfoDetails, SWT.PUSH | SWT.FLAT | SWT.TOP);
        btnEdit.setText(StringConstants.DIA_BTN_EDIT_QUERY);
        btnEdit.setImage(com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_EDIT);
        btnEdit.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

        btnFetchData = new Button(compFileInfoDetails, SWT.PUSH | SWT.FLAT | SWT.TOP);
        btnFetchData.setText(StringConstants.DIA_BTN_FETCH_DATA);
        btnFetchData.setImage(com.kms.katalon.composer.components.impl.constants.ImageConstants.IMG_16_REFRESH);
        btnFetchData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        return parent;
    }
    
    @Override
    protected void initValues() {
        String strReadAsString = getDataFile().getProperty("readAsString");
        ckcbReadAsString.setSelection((strReadAsString == null || strReadAsString.isEmpty()) ? true
                : Boolean.valueOf(strReadAsString).booleanValue());
    }

    @Override
    protected Composite createDataTablePart(Composite parent) {
        compTable = new Composite(parent, SWT.BORDER);
        GridLayout glCompTable = new GridLayout();
        glCompTable.marginHeight = 0;
        glCompTable.marginWidth = 0;
        compTable.setLayout(glCompTable);
        compTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        tableViewer = new TableViewer(compTable, SWT.VIRTUAL | SWT.FULL_SELECTION);
        tableViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tableViewer.getTable().setLinesVisible(ControlUtils.shouldLineVisble(tableViewer.getTable().getDisplay()));

        TableViewerColumn tableViewerColumnNo = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tableColumnNo = tableViewerColumnNo.getColumn();
        tableColumnNo.setText(StringConstants.NO_);
        tableColumnNo.setWidth(40);
        tableViewerColumnNo.setLabelProvider(new CellLabelProvider() {

            @Override
            public void update(ViewerCell cell) {
                int order = tableViewer.getTable().indexOf((TableItem) cell.getItem()) + 1;
                cell.setText(Integer.toString(order));
            }
        });

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());

        return parent;
    }

    private void redrawArrowIndicator() {
        lblArrowIndicator.getParent().setRedraw(false);
        lblArrowIndicator.setImage(
                compFileInfoDetails.isVisible() ? ImageConstants.IMG_16_ARROW_DOWN : ImageConstants.IMG_16_ARROW);
        lblArrowIndicator.getParent().setRedraw(true);
    }

    private void resizeTableOnElasticFileInfo() {
        Display.getDefault().timerExec(10, new Runnable() {

            @Override
            public void run() {
                // hide file info composite
                compFileInfoDetails.setVisible(!compFileInfoDetails.isVisible());

                // elasticate data table
                GridData fileInfoGridData = (GridData) compFileInfoDetails.getLayoutData();
                fileInfoGridData.exclude = !compFileInfoDetails.isVisible();
                compTable.layout(true, true);
                compTable.getParent().layout();
                redrawArrowIndicator();
            }
        });
    }

    private void addControlListeners() {
        MouseAdapter fileInfoClickListener = new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {
                resizeTableOnElasticFileInfo();
            }
        };
        lblArrowIndicator.addMouseListener(fileInfoClickListener);
        lblFileInfo.addMouseListener(fileInfoClickListener);
        compFileInfoHeader.addMouseListener(fileInfoClickListener);

        btnFetchData.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                updateChildInfo(getDataFile());
            }
        });

        btnEdit.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                executeOperation(new ChangeQueryOperation());
            }
        });
        
        ckcbReadAsString.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                getDataFile().setProperty("readAsString", String.valueOf(ckcbReadAsString.getSelection()));
                dirtyable.setDirty(true);
            }
        });
    }

    @Override
    protected void updateChildInfo(final DataFileEntity dataFile) {
        btnFetchData.setEnabled(false);
        txtQuery.setText(dataFile.getQuery());
        clearTable();
        if (!canFetchData(dataFile)) {
            return;
        }

        // fetch data after opened 1 second
        Display.getCurrent().timerExec(1000, new Runnable() {

            @Override
            public void run() {
                try {
                    // fetch data and load into table
                    DatabaseConnection dbConnection = TestDataController.getInstance().getDatabaseConnection(dataFile);
                    if (dbConnection == null) {
                        throw new Exception(StringConstants.DIA_MSG_CONNECTION_EMPTY);
                    }
                    DBData dbData = new DBData(dbConnection, dataFile.getQuery());
                    loadDataIntoTable(dbData);
                    setStatusLabel(MessageFormat.format(StringConstants.DIA_LBL_STATUS_LOADED_ON,
                            dbData.getRetrievedDate().toString()), ColorUtil.getTextSuccessfulColor());
                } catch (Exception e) {
                    setStatusLabel(StringConstants.DIA_MSG_CANNOT_FETCH_DATA, ColorUtil.getTextErrorColor());
                    MultiStatusErrorDialog.showErrorDialog(e, StringConstants.DIA_MSG_CANNOT_FETCH_DATA,
                            e.getMessage());
                } finally {
                    btnFetchData.setEnabled(true);
                }
            }
        });
    }

    /**
     * A simple check for configured Database and SQL info.
     * 
     * @param dataFile DataFileEntity
     * @return Is Database Test Data ready to load from DB
     */
    private boolean canFetchData(DataFileEntity dataFile) {
        boolean isNotBlankQuery = isNotBlank(dataFile.getQuery());
        boolean useGlobalDBSetting = dataFile.isUsingGlobalDBSetting();

        if (useGlobalDBSetting) {
            return isNotBlankQuery;
        }

        return isNotBlankQuery && startsWithIgnoreCase(dataFile.getDataSourceUrl(), "jdbc");
    }

    private void loadDataIntoTable(DBData dbData) {
        tableViewer.getTable().setRedraw(false);

        // chop the column names
        String[] colNames = dbData.getColumnNames();
        if (dbData.getColumnNumbers() > MAX_COLUMN_COUNT) {
            colNames = (String[]) ArrayUtils.subarray(colNames, 0, MAX_COLUMN_COUNT);
        }

        // chop the data if the size is greater than the maximum allowance
        List<Object[]> data = limitDataForPreview(dbData);
        for (int i = 0; i < colNames.length; i++) {
            final int columnIndex = i;
            TableViewerColumn columnViewer = new TableViewerColumn(tableViewer, SWT.NONE);
            columnViewer.setLabelProvider(new ColumnLabelProvider() {
                @Override
                public String getText(Object rowElement) {
                    if (!rowElement.getClass().isArray() || ((Object[]) rowElement).length <= columnIndex) {
                        return StringUtils.EMPTY;
                    }
                    return ObjectUtils.toString(((Object[]) rowElement)[columnIndex]);
                }
            });
            TableColumn column = columnViewer.getColumn();
            column.setWidth(COLUMN_WIDTH);
            column.setText(colNames[i]);
        }

        tableViewer.setInput(data);
        tableViewer.getTable().setHeaderVisible(true);
        tableViewer.getTable().setRedraw(true);
    }

    private void warningIfDataOverSize(final boolean isOverSize, final String retrievedDate) {
        sync.asyncExec(new Runnable() {
            @Override
            public void run() {
                if (!isOverSize) {
                    return;
                }
                setStatusLabel(MessageFormat.format(StringConstants.DIA_LBL_STATUS_PARTIALLY_LOADED_ON, retrievedDate),
                        ColorUtil.getWarningForegroudColor());
                MessageDialog.openWarning(null, StringConstants.WARN_TITLE, MessageFormat.format(
                        StringConstants.DIA_MSG_DATA_IS_TOO_LARGE_FOR_PREVIEW, MAX_COLUMN_COUNT, MAX_ROW_LIMIT));
            }
        });
    }

    private void setStatusLabel(final String text, final Color color) {
        sync.asyncExec(new Runnable() {
            @Override
            public void run() {
                lblStatus.setForeground(color);
                lblStatus.setText(text);
                compFileInfoHeader.layout();
            }
        });
    }

    private void clearTable() {
        Table table = tableViewer.getTable();
        while (table.getColumnCount() > 1) {
            table.getColumns()[1].dispose();
        }
        tableViewer.setInput(Collections.emptyList());
    }

    private List<Object[]> limitDataForPreview(DBData dbData) {
        List<List<Object>> fetchedData = dbData.getData();
        int rowCount = fetchedData.size();
        if (rowCount == 0) {
            return Collections.emptyList();
        }

        List<Object[]> data = new ArrayList<>();
        int columnCount = dbData.getColumnNumbers();

        boolean isTooManyRows = rowCount > MAX_ROW_LIMIT;
        boolean isTooManyColumns = columnCount > MAX_COLUMN_COUNT;
        warningIfDataOverSize(isTooManyRows || isTooManyColumns, dbData.getRetrievedDate().toString());

        if (isTooManyRows) {
            // get first 500 row for preview only
            fetchedData = fetchedData.subList(0, MAX_ROW_LIMIT);
        }

        if (isTooManyColumns) {
            for (List<Object> row : fetchedData) {
                data.add(row.subList(0, MAX_COLUMN_COUNT).toArray(new Object[MAX_COLUMN_COUNT]));
            }
            return data;
        }

        for (List<Object> row : fetchedData) {
            data.add(row.toArray(new Object[columnCount]));
        }

        return data;
    }

    @Persist
    public void save() {
        try {
            TestDataController.getInstance().updateTestData(originalDataFile, originalDataFile.getParentFolder());
            dirtyable.setDirty(false);
            refreshTreeEntity();
            sendTestDataUpdatedEvent(originalDataFile.getId());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PA_ERROR_MSG_UNABLE_TO_SAVE_TEST_DATA,
                    e.getClass().getSimpleName());
        }
    }

    private class ChangeQueryOperation extends AbstractOperation {
        private DataFileEntity oldDataFile;

        private DataFileEntity newDataFile;

        public ChangeQueryOperation() {
            super(ChangeQueryOperation.class.getName());
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            oldDataFile = originalDataFile.clone();
            newDataFile = originalDataFile.clone();
            EditTestDataQueryDialog dialog = new EditTestDataQueryDialog(Display.getCurrent().getActiveShell(),
                    newDataFile);

            if (dialog.open() != Window.OK || !dialog.isChanged()) {
                return Status.CANCEL_STATUS;
            }
            return redo(monitor, info);
        }

        private void doChangeDataFile(DataFileEntity dataFile) {
            dirtyable.setDirty(true);
            originalDataFile = dataFile;
            updateChildInfo(originalDataFile);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doChangeDataFile(newDataFile);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doChangeDataFile(oldDataFile);
            return Status.OK_STATUS;
        }

    }
}
