package com.kms.katalon.composer.testdata.parts;

import java.io.IOException;
import java.text.MessageFormat;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.controls.HelpToolBarForMPart;
import com.kms.katalon.composer.components.impl.control.ImageButton;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.testdata.constants.ImageConstants;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.composer.testdata.job.LoadExcelFileJob;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.core.testdata.ExcelData;
import com.kms.katalon.core.testdata.TestData;
import com.kms.katalon.core.util.internal.PathUtil;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testdata.DataFileEntity.DataFileDriverType;
import com.kms.katalon.entity.testdata.DataFilePropertyInputEntity;

public class ExcelTestDataPart extends TestDataMainPart {
    private static final String[] FILTER_NAMES = { "Microsoft Excel Spreadsheet Files (*.xls, *.xlsx)" };

    private static final String[] FILTER_EXTS = { "*.xlsx; *.xls" };

    @Inject
    private EPartService partService;

    @Inject
    private UISynchronize sync;

    private Text txtFileName;

    private Combo cbbSheets;

    private TableViewer tableViewer;

    private Label lblSheetName;

    private Button ckcbEnableHeader;

    private Button ckcbUseRelativePath;

    private Button btnBrowse;

    private ImageButton btnExpandFileInfo;

    private Composite compositeFileInfoDetails;

    private Composite compositeFileInfoHeader;

    private Composite compositeTable;

    private Composite compositeFileInfo;

    // Control status
    private boolean isFileInfoExpanded;

    private boolean ableToReload;

    // Field
    private String fCurrentPath;

    private String fCurrentSheetName;

    private String[][] fData;

    private LoadExcelFileJob loadFileJob;

    private ExcelData excelData;

    private Label lblFileInfoStatus;

    private Listener layoutFileInfoCompositeListener = new Listener() {

        @Override
        public void handleEvent(org.eclipse.swt.widgets.Event event) {
            layoutFileInfoComposite();
        }
    };

    private Label lblFileInfo;

    @Override
    @PostConstruct
    public void createControls(Composite parent, MPart mpart) {
        ableToReload = true;
        new HelpToolBarForMPart(mpart, DocumentationMessageConstants.TEST_DATA_EXCEL);
        super.createControls(parent, mpart);
    }

    protected void layoutFileInfoComposite() {
        Display.getDefault().timerExec(10, new Runnable() {

            @Override
            public void run() {
                isFileInfoExpanded = !isFileInfoExpanded;
                compositeFileInfoDetails.setVisible(isFileInfoExpanded);
                if (!isFileInfoExpanded) {
                    ((GridData) compositeFileInfoDetails.getLayoutData()).exclude = true;
                    compositeFileInfo.setSize(compositeFileInfo.getSize().x,
                            compositeFileInfo.getSize().y - compositeTable.getSize().y);
                } else {
                    ((GridData) compositeFileInfoDetails.getLayoutData()).exclude = false;
                }
                compositeFileInfo.layout(true, true);
                compositeFileInfo.getParent().layout();
                redrawBtnExpandFileInfo();
            }
        });
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    public Composite createFileInfoPart(Composite parent) {
        // File info part
        compositeFileInfo = new Composite(parent, SWT.NONE);
        compositeFileInfo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

        GridLayout glFileComposite = new GridLayout(1, true);
        glFileComposite.marginWidth = 0;
        glFileComposite.marginHeight = 0;
        compositeFileInfo.setLayout(glFileComposite);
        compositeFileInfo.setBackground(ColorUtil.getCompositeBackgroundColor());

        compositeFileInfoHeader = new Composite(compositeFileInfo, SWT.NONE);
        compositeFileInfoHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glFileCompositeHeader = new GridLayout(3, false);
        glFileCompositeHeader.marginWidth = 0;
        glFileCompositeHeader.marginHeight = 0;
        compositeFileInfoHeader.setLayout(glFileCompositeHeader);
        compositeFileInfoHeader.setCursor(compositeFileInfoHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        btnExpandFileInfo = new ImageButton(compositeFileInfoHeader, SWT.NONE);

        lblFileInfo = new Label(compositeFileInfoHeader, SWT.NONE);
        lblFileInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        lblFileInfo.setText(StringConstants.PA_LBL_FILE_INFO);
        ControlUtils.setFontToBeBold(lblFileInfo);

        lblFileInfoStatus = new Label(compositeFileInfoHeader, SWT.NONE);
        lblFileInfoStatus.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        lblFileInfoStatus.setForeground(ColorUtil.getWarningForegroudColor());
        ControlUtils.setFontToBeBold(lblFileInfoStatus);

        compositeFileInfoDetails = new Composite(compositeFileInfo, SWT.NONE);
        compositeFileInfoDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glFileCompositeDetails = new GridLayout(2, true);
        glFileCompositeDetails.marginHeight = 0;
        glFileCompositeDetails.horizontalSpacing = 30;
        glFileCompositeDetails.marginRight = 40;
        glFileCompositeDetails.marginLeft = 40;
        compositeFileInfoDetails.setLayout(glFileCompositeDetails);

        Composite compositeFileName = new Composite(compositeFileInfoDetails, SWT.NONE);
        compositeFileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout glCompositeFileName = new GridLayout(3, false);
        glCompositeFileName.marginRight = 5;
        glCompositeFileName.marginWidth = 0;
        compositeFileName.setLayout(glCompositeFileName);

        Label lblFileName = new Label(compositeFileName, SWT.NONE);
        GridData gdLblFileName = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdLblFileName.widthHint = TestDataMainPart.MAX_LABEL_WIDTH;
        lblFileName.setLayoutData(gdLblFileName);
        lblFileName.setText(StringConstants.PA_LBL_FILE_NAME);
        txtFileName = new Text(compositeFileName, SWT.BORDER);
        GridData gdTxtFileName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTxtFileName.heightHint = 20;
        txtFileName.setLayoutData(gdTxtFileName);
        txtFileName.setEditable(false);
        btnBrowse = new Button(compositeFileName, SWT.FLAT);
        btnBrowse.setText(StringConstants.PA_BTN_BROWSE);

        Composite compositeSheetName = new Composite(compositeFileInfoDetails, SWT.NONE);
        compositeSheetName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        compositeSheetName.setLayout(new GridLayout(2, false));

        lblSheetName = new Label(compositeSheetName, SWT.NONE);
        lblSheetName.setText(StringConstants.PA_LBL_SHEET_NAME);

        cbbSheets = new Combo(compositeSheetName, SWT.READ_ONLY);
        GridData gdCbbSheets = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gdCbbSheets.heightHint = 20;
        cbbSheets.setLayoutData(gdCbbSheets);

        Composite compositeCheckBoxes = new Composite(compositeFileInfoDetails, SWT.NONE);
        compositeCheckBoxes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        GridLayout glCompositeCheckBoxes = new GridLayout(2, false);
        glCompositeCheckBoxes.horizontalSpacing = 15;
        glCompositeCheckBoxes.marginWidth = 0;
        compositeCheckBoxes.setLayout(glCompositeCheckBoxes);

        ckcbEnableHeader = new Button(compositeCheckBoxes, SWT.CHECK);
        ckcbEnableHeader.setText(StringConstants.PA_CHKBOX_USE_FIRST_ROW_AS_HEADER);

        ckcbUseRelativePath = new Button(compositeCheckBoxes, SWT.CHECK);
        ckcbUseRelativePath.setText(StringConstants.PA_CHKBOX_USE_RELATIVE_PATH);
        new Label(compositeCheckBoxes, SWT.NONE);

        isFileInfoExpanded = true;
        redrawBtnExpandFileInfo();

        addControlListeners();
        return compositeFileInfo;
    }

    private String getSourceUrlAbsolutePath() {
        String sourceUrl = txtFileName.getText();
        if (ckcbUseRelativePath.getSelection()) {
            sourceUrl = PathUtil.relativeToAbsolutePath(sourceUrl, getProjectFolderLocation());
        }
        return sourceUrl;
    }

    @Override
    public Composite createDataTablePart(Composite parent) {
        parent.setLayout(new GridLayout(1, true));
        parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        compositeTable = new Composite(parent, SWT.BORDER);
        GridLayout glTableViewerComposite = new GridLayout(1, true);
        glTableViewerComposite.marginWidth = 0;
        glTableViewerComposite.marginHeight = 0;
        compositeTable.setLayout(glTableViewerComposite);
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        tableViewer = new TableViewer(compositeTable, SWT.VIRTUAL | SWT.FULL_SELECTION);
        tableViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // tableViewer.getTable().setHeaderVisible(true);
        tableViewer.getTable().setLinesVisible(ControlUtils.shouldLineVisble(tableViewer.getTable().getDisplay()));

        TableViewerColumn tbviewerClmnNo = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tbclmnNo = tbviewerClmnNo.getColumn();
        tbclmnNo.setText(StringConstants.NO_);
        tbclmnNo.setWidth(40);
        tbviewerClmnNo.setLabelProvider(new CellLabelProvider() {

            @Override
            public void update(ViewerCell cell) {
                int order = tableViewer.getTable().indexOf((TableItem) cell.getItem()) + 1;
                cell.setText(Integer.toString(order));
            }
        });

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());

        return compositeTable;
    }

    private void loadInput(final DataFileEntity dataFile) {
        fCurrentPath = dataFile.getDataSourceUrl();

        txtFileName.setText(fCurrentPath);

        ckcbEnableHeader.setSelection(dataFile.isContainsHeaders());

        ckcbUseRelativePath.setSelection(dataFile.getIsInternalPath());

        fCurrentSheetName = dataFile.getSheetName();

        readExcelFile();
    }

    private void readExcelFile() {
        if (ableToReload) {
            if (loadFileJob != null && loadFileJob.getState() == Job.RUNNING) {
                loadFileJob.cancel();
                loadFileJob.removeJobChangeListener(readExcelJobListener);
            }

            loadFileJob = new LoadExcelFileJob(getSourceUrlAbsolutePath(), ckcbEnableHeader.getSelection());
            loadFileJob.setUser(true);
            loadFileJob.schedule();

            loadFileJob.addJobChangeListener(readExcelJobListener);
        }
    }

    private IJobChangeListener readExcelJobListener = new JobChangeAdapter() {

        @Override
        public void done(final IJobChangeEvent event) {
            sync.syncExec(new Runnable() {
                @Override
                public void run() {
                    if (event.getResult() == Status.OK_STATUS) {
                        excelData = loadFileJob.getExcelData();
                        if (excelData == null) {
                            cbbSheets.setItems(new String[] {});
                            clearTable();
                            return;
                        }
                        loadSheetNames(excelData.getSheetNames());
                        loadExcelDataToTable();
                    }
                }
            });
        }
    };

    private void addControlListeners() {
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(btnBrowse.getShell());
                dialog.setFilterNames(FILTER_NAMES);
                dialog.setFilterExtensions(FILTER_EXTS);
                dialog.setFilterPath(getProjectFolderLocation());

                changeExcelFile(dialog.open());
                enableRelativePath();
            }

            private void changeExcelFile(String absoluteFilePath) {
                if (absoluteFilePath == null || absoluteFilePath.equals(fCurrentPath)) {
                    return;
                }

                executeOperation(new ChangeExcelFileOperation(absoluteFilePath));
            }
        });

        cbbSheets.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                executeOperation(new ChangeSheetOperation(cbbSheets.getText()));
            }
        });

        ckcbEnableHeader.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                executeOperation(new ChangeUseFirstRowAsHeaderOperation(ckcbEnableHeader.getSelection()));
            }
        });

        ckcbUseRelativePath.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                executeOperation(new ChangeUseRelativePathOperation(ckcbUseRelativePath.getSelection()));
                
            }
        });

        btnExpandFileInfo.addListener(SWT.MouseDown, layoutFileInfoCompositeListener);
        lblFileInfo.addListener(SWT.MouseDown, layoutFileInfoCompositeListener);
        lblFileInfoStatus.addListener(SWT.MouseDown, layoutFileInfoCompositeListener);
    }

    private void redrawBtnExpandFileInfo() {
        btnExpandFileInfo.getParent().setRedraw(false);
        if (isFileInfoExpanded) {
            btnExpandFileInfo.setImage(ImageConstants.IMG_16_ARROW_DOWN);
        } else {
            btnExpandFileInfo.setImage(ImageConstants.IMG_16_ARROW);
        }
        btnExpandFileInfo.getParent().setRedraw(true);

    }

    private void selectDefaultSheet(String[] sheetNames) {
        if (sheetNames.length > 0) {
            fCurrentSheetName = sheetNames[0];
            cbbSheets.select(0);
        }
    }

    private void loadSheetNames(String[] sheetNames) {
        try {
            if (cbbSheets.isDisposed()) {
                return;
            }

            cbbSheets.setItems(sheetNames);

            if (StringUtils.isBlank(fCurrentSheetName)) {
                selectDefaultSheet(sheetNames);
            } else {
                int currentIdx = cbbSheets.indexOf(fCurrentSheetName);
                if (currentIdx < 0) {
                    MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                            MessageFormat.format(StringConstants.PA_WARN_MSG_SHEET_NOT_FOUND, fCurrentSheetName));
                    selectDefaultSheet(sheetNames);
                } else {
                    cbbSheets.select(cbbSheets.indexOf(fCurrentSheetName));
                }
            }

        } catch (Exception e) {
            MessageDialog.openWarning(null, StringConstants.WARN_TITLE,
                    StringConstants.PA_WARN_MSG_UNABLE_TO_LOAD_SHEET_NAME);
        }
    }

    private void clearTable() {
        Table table = tableViewer.getTable();
        while (table.getColumnCount() > 1) {
            table.getColumns()[1].dispose();
        }
        table.clearAll();
        tableViewer.setItemCount(0);
        table.setHeaderVisible(false);
    }

    private void loadExcelDataToTable() {
        try {
            tableViewer.getTable().setRedraw(false);
            clearTable();

            String[] headers = null;
            if (cbbSheets.getSelectionIndex() < 0) {
                return;
            }

            excelData.changeSheet(cbbSheets.getText());
            excelData.activeHeaders(ckcbEnableHeader.getSelection());

            headers = excelData.getColumnNames();

            int rowNumbers = excelData.getRowNumbers();
            int columnNumbers = excelData.getColumnNumbers();

            if (columnNumbers > MAX_COLUMN_COUNT) {
                MessageDialog.openWarning(null, StringConstants.WARN,
                        MessageFormat.format(StringConstants.PA_FILE_TOO_LARGE, MAX_COLUMN_COUNT));
                columnNumbers = MAX_COLUMN_COUNT;
            }

            fData = new String[rowNumbers][columnNumbers];

            tableViewer.getTable().setItemCount(rowNumbers);

            int numEmptyHeader = 0;
            for (int i = 0; i < columnNumbers; i++) {
                TableViewerColumn columnViewer = new TableViewerColumn(tableViewer, SWT.NONE);
                String header = (headers == null ? "" : headers[i]);
                if (!StringUtils.isBlank(header)) {
                    columnViewer.getColumn().setText(header);
                } else {
                    columnViewer.getColumn().setImage(ImageConstants.IMG_16_WARN_TABLE_ITEM);
                    columnViewer.getColumn().setToolTipText(StringConstants.PA_TOOLTIP_WARNING_COLUMN_HEADER);
                    columnViewer.getColumn().setText(StringUtils.EMPTY);
                    numEmptyHeader++;
                }

                columnViewer.getColumn().setWidth(COLUMN_WIDTH);
                columnViewer.setLabelProvider(new ColumnLabelProvider() {
                    private String getCellText(ExcelData excelData, int columnIndex, int rowIndex) {
                        try {
                            return excelData.getValue(columnIndex + TestData.BASE_INDEX,
                                    rowIndex + TestData.BASE_INDEX);
                        } catch (IOException e) {
                            return "";
                        }
                    }

                    @Override
                    public void update(final ViewerCell cell) {
                        final int columnIndex = cell.getColumnIndex() - 1;
                        final int rowIndex = tableViewer.getTable().indexOf((TableItem) cell.getItem());
                        String text = "...";
                        cell.setText(text);

                        sync.asyncExec(new Runnable() {
                            @Override
                            public void run() {
                                if (fData == null || cell.getItem().isDisposed()) {
                                    return;
                                }

                                if (fData[rowIndex][columnIndex] == null) {
                                    fData[rowIndex][columnIndex] = getCellText(excelData, columnIndex, rowIndex);
                                }

                                cell.setText(fData[rowIndex][columnIndex]);
                            }
                        });
                    }
                });
            }

            if (numEmptyHeader > 0 && ckcbEnableHeader.getSelection()) {
                lblFileInfoStatus.setText(MessageFormat.format(StringConstants.PA_LBL_WARNING_COLUMN_HEADER,
                        numEmptyHeader, columnNumbers));
            }

            tableViewer.setInput(fData);
            tableViewer.getTable().setHeaderVisible(ckcbEnableHeader.getSelection());

        } catch (IllegalArgumentException ex) {
            fData = null;
        } catch (Exception e) {
            fData = null;
            LoggerSingleton.logError(e);
        } finally {
            tableViewer.getTable().setRedraw(true);
        }
    }

    @Persist
    public void save() {
        try {
            ableToReload = false;
            originalDataFile = updateDataFileProperty(originalDataFile.getLocation(), originalDataFile.getName(),
                    originalDataFile.getDescription(), DataFileDriverType.ExcelFile, txtFileName.getText(),
                    cbbSheets.getText(), ckcbUseRelativePath.getSelection(), ckcbEnableHeader.getSelection());
            updateDataFile(originalDataFile);
            dirtyable.setDirty(false);
            refreshTreeEntity();
            sendTestDataUpdatedEvent(originalDataFile.getId());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PA_ERROR_MSG_UNABLE_TO_SAVE_TEST_DATA,
                    e.getClass().getSimpleName());
        } finally {
            ableToReload = true;
        }
    }

    public DataFileEntity updateDataFileProperty(String pk, String name, String description,
            DataFileDriverType dataFileDriver, String dataSourceURL, String sheetName, boolean isInternalPath,
            boolean isHeaderEnabled) throws Exception {

        DataFilePropertyInputEntity dataFileInputPro = new DataFilePropertyInputEntity();

        dataFileInputPro.setPk(pk);
        dataFileInputPro.setName(name);
        dataFileInputPro.setDescription(description);
        dataFileInputPro.setDataFileDriver(dataFileDriver.name());
        dataFileInputPro.setdataSourceURL(dataSourceURL);
        dataFileInputPro.setSheetName(sheetName);
        dataFileInputPro.setIsInternalPath(isInternalPath);
        dataFileInputPro.setEnableHeader(isHeaderEnabled);
        DataFileEntity dataFileEntity = TestDataController.getInstance().updateDataFile(dataFileInputPro);
        return dataFileEntity;
    }

    private String getProjectFolderLocation() {
        return ProjectController.getInstance().getCurrentProject().getFolderLocation();
    }

    @Override
    protected void updateChildInfo(DataFileEntity dataFile) {
        loadInput(dataFile);
        enableRelativePath();
    }

    private void enableRelativePath() {
        ckcbUseRelativePath.setEnabled(StringUtils.isNotBlank(txtFileName.getText()));
    }

    @Override
    protected EPartService getPartService() {
        return partService;
    }

    @Override
    @PreDestroy
    public void onClose() {
        fCurrentPath = "";
        fCurrentSheetName = "";
        fData = null;
        super.onClose();
    }

    private class ChangeExcelFileOperation extends AbstractOperation {
        private String oldExcelFilePath;
        private String oldSheetName;
        
        private String newExcelFilePath;

        public ChangeExcelFileOperation(String newExcelFilePath) {
            super(ChangeExcelFileOperation.class.getName());
            this.newExcelFilePath = newExcelFilePath;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (newExcelFilePath == null) {
                return Status.CANCEL_STATUS;
            }
            oldExcelFilePath = fCurrentPath;
            oldSheetName = fCurrentSheetName;
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doLoadExcelFile(newExcelFilePath, "");
            return Status.OK_STATUS;
        }

        private void doLoadExcelFile(String excelFileAbsolutePath, String sheetName) {
            lblFileInfoStatus.setText("");

            fCurrentPath = excelFileAbsolutePath;
            fCurrentSheetName = sheetName;

            if (ckcbUseRelativePath.getSelection()) {
                txtFileName.setText(PathUtil.absoluteToRelativePath(fCurrentPath, getProjectFolderLocation()));
            } else {
                txtFileName.setText(fCurrentPath);
            }

            readExcelFile();

            dirtyable.setDirty(true);
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doLoadExcelFile(oldExcelFilePath, oldSheetName);
            return Status.OK_STATUS;
        }
    }
    
    private class ChangeSheetOperation extends AbstractOperation {
        private String oldSheetName;
        private String newSheetName;
        
        public ChangeSheetOperation(String newSheetName) {
            super(ChangeSheetOperation.class.getName());
            this.newSheetName = newSheetName;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (newSheetName.equals(fCurrentSheetName)) {
                return Status.CANCEL_STATUS;
            }
            oldSheetName = fCurrentSheetName;
            doSetSheetName(newSheetName);
            return Status.OK_STATUS;
        }

        private void doSetSheetName(String selectedSheetName) {
            lblFileInfoStatus.setText("");

            fCurrentSheetName = selectedSheetName;
            cbbSheets.setText(fCurrentSheetName);
            loadExcelDataToTable();
            dirtyable.setDirty(true);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doSetSheetName(newSheetName);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doSetSheetName(oldSheetName);
            return Status.OK_STATUS;
        }
        
    }
    
    private class ChangeUseFirstRowAsHeaderOperation extends AbstractOperation {
        boolean oldCheckedValue;
        
        public ChangeUseFirstRowAsHeaderOperation(boolean oldCheckedValue) {
            super(ChangeUseFirstRowAsHeaderOperation.class.getName());
            this.oldCheckedValue = oldCheckedValue;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doChangeCheckValue(oldCheckedValue);
            return Status.OK_STATUS;
        }

        private void doChangeCheckValue(boolean isSelected) {
            ckcbEnableHeader.setSelection(isSelected);
            loadExcelDataToTable();
            dirtyable.setDirty(true);
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doChangeCheckValue(!oldCheckedValue);
            return Status.OK_STATUS;
        }
    }
    
    private class ChangeUseRelativePathOperation extends AbstractOperation {
        boolean oldCheckedValue;
        
        public ChangeUseRelativePathOperation(boolean oldCheckedValue) {
            super(ChangeUseRelativePathOperation.class.getName());
            this.oldCheckedValue = oldCheckedValue;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doChangeCheckValue(oldCheckedValue);
            return Status.OK_STATUS;
        }

        private void doChangeCheckValue(boolean isSelected) {
            ckcbUseRelativePath.setSelection(isSelected);
            dirtyable.setDirty(true);
            if (txtFileName.getText() == null) {
                return;
            }
            String sourceUrl = txtFileName.getText();
            if (isSelected) {
                txtFileName.setText(PathUtil.absoluteToRelativePath(sourceUrl, getProjectFolderLocation()));
            } else {
                txtFileName.setText(PathUtil.relativeToAbsolutePath(sourceUrl, getProjectFolderLocation()));
            }
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doChangeCheckValue(!oldCheckedValue);
            return Status.OK_STATUS;
        }
    }
}
