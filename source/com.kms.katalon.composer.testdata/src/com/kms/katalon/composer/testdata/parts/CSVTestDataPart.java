package com.kms.katalon.composer.testdata.parts;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

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
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.core.testdata.CSVData;
import com.kms.katalon.core.testdata.reader.CSVSeparator;
import com.kms.katalon.core.util.internal.PathUtil;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testdata.DataFileEntity.DataFileDriverType;
import com.kms.katalon.entity.testdata.DataFilePropertyInputEntity;

public class CSVTestDataPart extends TestDataMainPart {
    private static final String[] FILTER_NAMES = { "Comma Separated Values Files (*.csv)", "All Files (*.*)" };

    private static final String[] FILTER_EXTS = { "*.csv", "*.*" };

    private TableViewer tableViewer;

    private Text txtFileName;

    private Button chckIsRelativePath, chckEnableHeader;

    private Button btnBrowse;

    private Label lblSeperator;

    private Combo cbSeperator;

    private Composite compositeFileInfo;

    private Composite compositeFileInfoDetails;

    private Composite compositeFileInfoHeader;

    private Composite compositeTable;

    private ImageButton btnExpandFileInfoComposite;

    private Label lblFileInfo;

    private boolean isFileInfoExpanded;

    private boolean enableToReload;

    private String fCurrentFilePath;

    private String fSelectedSeperator;

    @Inject
    private EPartService partService;

    @Inject
    private UISynchronize sync;

    private Listener layoutFileInfoCompositeListener = new Listener() {

        @Override
        public void handleEvent(org.eclipse.swt.widgets.Event event) {
            layoutFileInfoComposite();
        }
    };

    private Label lblFileInfoStatus;

    @Override
    @PostConstruct
    public void createControls(Composite parent, MPart mpart) {
        enableToReload = true;
        fCurrentFilePath = "";
        fSelectedSeperator = CSVSeparator.COMMA.toString();

        new HelpToolBarForMPart(mpart, DocumentationMessageConstants.TEST_DATA_CSV);
        super.createControls(parent, mpart);
    }

    /**
     * @wbp.parser.entryPoint
     */
    @Override
    protected Composite createFileInfoPart(Composite parent) {
        compositeFileInfo = new Composite(parent, SWT.NONE);
        compositeFileInfo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

        GridLayout glCompositeFileInfo = new GridLayout(1, true);
        glCompositeFileInfo.marginWidth = 0;
        glCompositeFileInfo.marginHeight = 0;
        compositeFileInfo.setLayout(glCompositeFileInfo);
        compositeFileInfo.setBackground(ColorUtil.getCompositeBackgroundColor());

        compositeFileInfoHeader = new Composite(compositeFileInfo, SWT.NONE);
        compositeFileInfoHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        GridLayout glCompositeFileInfoHeader = new GridLayout(3, false);
        glCompositeFileInfoHeader.marginWidth = 0;
        glCompositeFileInfoHeader.marginHeight = 0;
        compositeFileInfoHeader.setLayout(glCompositeFileInfoHeader);
        compositeFileInfoHeader.setCursor(compositeFileInfoHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        btnExpandFileInfoComposite = new ImageButton(compositeFileInfoHeader, SWT.NONE);

        lblFileInfo = new Label(compositeFileInfoHeader, SWT.NONE);
        lblFileInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
        lblFileInfo.setText(StringConstants.PA_LBL_FILE_INFO);
        ControlUtils.setFontToBeBold(lblFileInfo);

        lblFileInfoStatus = new Label(compositeFileInfoHeader, SWT.NONE);
        GridData gd_lblFileInfoStatus = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
        gd_lblFileInfoStatus.horizontalIndent = 5;
        lblFileInfoStatus.setLayoutData(gd_lblFileInfoStatus);
        lblFileInfoStatus.setForeground(ColorUtil.getWarningForegroudColor());
        ControlUtils.setFontToBeBold(lblFileInfoStatus);

        compositeFileInfoDetails = new Composite(compositeFileInfo, SWT.NONE);
        compositeFileInfoDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        GridLayout glCompositeFileInfoDetails = new GridLayout(2, true);
        glCompositeFileInfoDetails.marginWidth = 0;
        glCompositeFileInfoDetails.marginHeight = 0;
        glCompositeFileInfoDetails.horizontalSpacing = 30;
        glCompositeFileInfoDetails.marginRight = 40;
        glCompositeFileInfoDetails.marginLeft = 40;
        compositeFileInfoDetails.setLayout(glCompositeFileInfoDetails);

        Composite compositeName = new Composite(compositeFileInfoDetails, SWT.NONE);
        compositeName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout glCompositeName = new GridLayout(3, false);
        compositeName.setLayout(glCompositeName);

        Label lblFileName = new Label(compositeName, SWT.NONE);
        GridData gdLblFileName = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdLblFileName.widthHint = TestDataMainPart.MAX_LABEL_WIDTH;
        lblFileName.setLayoutData(gdLblFileName);
        lblFileName.setText(StringConstants.PA_LBL_FILE_NAME);
        txtFileName = new Text(compositeName, SWT.BORDER);
        GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gridData.heightHint = 18;
        txtFileName.setLayoutData(gridData);
        txtFileName.setEditable(false);

        btnBrowse = new Button(compositeName, SWT.FLAT);
        btnBrowse.setText(StringConstants.PA_BTN_BROWSE);

        Composite compositeSeperator = new Composite(compositeFileInfoDetails, SWT.NONE);
        compositeSeperator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        GridLayout glCompositeSeperator = new GridLayout(2, false);
        compositeSeperator.setLayout(glCompositeSeperator);

        lblSeperator = new Label(compositeSeperator, SWT.NONE);
        GridData gdLabelSeperator = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gdLabelSeperator.widthHint = TestDataMainPart.MAX_LABEL_WIDTH;
        lblSeperator.setLayoutData(gdLabelSeperator);
        lblSeperator.setText(StringConstants.PA_LBL_SEPARATOR);

        cbSeperator = new Combo(compositeSeperator, SWT.READ_ONLY);
        cbSeperator.setItems(CSVSeparator.stringValues());
        cbSeperator.select(0);
        cbSeperator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Composite compositeCheckBoxes = new Composite(compositeFileInfoDetails, SWT.NONE);
        compositeCheckBoxes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        GridLayout glCompositeCheckBoxes = new GridLayout(2, false);
        glCompositeCheckBoxes.horizontalSpacing = 15;
        compositeCheckBoxes.setLayout(glCompositeCheckBoxes);

        chckEnableHeader = new Button(compositeCheckBoxes, SWT.CHECK);
        chckEnableHeader.setText(StringConstants.PA_CHKBOX_USE_FIRST_ROW_AS_HEADER);

        chckIsRelativePath = new Button(compositeCheckBoxes, SWT.CHECK);
        chckIsRelativePath.setText(StringConstants.PA_CHKBOX_USE_RELATIVE_PATH);
        new Label(compositeCheckBoxes, SWT.NONE);

        isFileInfoExpanded = true;
        redrawBtnExpandFileInfo();

        return compositeFileInfo;
    }
    
    @Override
    protected void initValues() {
        // Do nothing
    }

    @Override
    protected Composite createDataTablePart(Composite parent) {
        parent.setLayout(new GridLayout(1, true));
        parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        compositeTable = new Composite(parent, SWT.BORDER);
        GridLayout glTableComposite = new GridLayout(1, true);
        glTableComposite.marginWidth = 0;
        glTableComposite.marginHeight = 0;
        compositeTable.setLayout(glTableComposite);
        compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        tableViewer = new TableViewer(compositeTable, SWT.VIRTUAL | SWT.FULL_SELECTION);
        tableViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tableViewer.getTable().setLinesVisible(ControlUtils.shouldLineVisble(tableViewer.getTable().getDisplay()));

        TableViewerColumn tbvclmnNo = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tbclmnNo = tbvclmnNo.getColumn();
        tbclmnNo.setText(StringConstants.NO_);
        tbclmnNo.setWidth(40);
        tbvclmnNo.setLabelProvider(new CellLabelProvider() {

            @Override
            public void update(ViewerCell cell) {
                int order = tableViewer.getTable().indexOf((TableItem) cell.getItem()) + 1;
                cell.setText(Integer.toString(order));
            }
        });

        tableViewer.setContentProvider(ArrayContentProvider.getInstance());

        addControlModifyListeners();
        return compositeTable;
    }

    private void addControlModifyListeners() {
        btnExpandFileInfoComposite.addListener(SWT.MouseDown, layoutFileInfoCompositeListener);
        lblFileInfo.addListener(SWT.MouseDown, layoutFileInfoCompositeListener);
        lblFileInfoStatus.addListener(SWT.MouseDown, layoutFileInfoCompositeListener);

        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(btnBrowse.getShell());
                dialog.setFilterNames(FILTER_NAMES);
                dialog.setFilterExtensions(FILTER_EXTS);
                dialog.setFilterPath(getProjectFolderLocation());

                executeOperation(new ChangeCSVFileOperation(dialog.open()));
                enableRelativePath();
            }
        });

        cbSeperator.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                executeOperation(new ChangeSeparatorOperation(cbSeperator.getText()));
            }
        });

        chckEnableHeader.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                executeOperation(new ChangeUseFirstRowAsHeaderOperation(chckEnableHeader.getSelection()));
            }
        });

        chckIsRelativePath.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                executeOperation(new ChangeUseRelativePathOperation(chckIsRelativePath.getSelection()));
            }
        });
    }

    private void layoutFileInfoComposite() {
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

    private void redrawBtnExpandFileInfo() {
        btnExpandFileInfoComposite.getParent().setRedraw(false);
        if (isFileInfoExpanded) {
            btnExpandFileInfoComposite.setImage(ImageConstants.IMG_16_ARROW_DOWN);
        } else {
            btnExpandFileInfoComposite.setImage(ImageConstants.IMG_16_ARROW);
        }
        btnExpandFileInfoComposite.getParent().setRedraw(true);

    }

    private void loadTestData(DataFileEntity dataFile) {
        if (!enableToReload) {
            return;
        }

        fCurrentFilePath = dataFile.getDataSourceUrl();
        txtFileName.setText(fCurrentFilePath);

        if (dataFile.getCsvSeperator() != null) {
            fSelectedSeperator = dataFile.getCsvSeperator();
            cbSeperator.setText(fSelectedSeperator);
        }

        chckEnableHeader.setSelection(dataFile.isContainsHeaders());

        chckIsRelativePath.setSelection(dataFile.getIsInternalPath());

        if (cbSeperator.getText() != null && !cbSeperator.getText().isEmpty()) {
            loadCSVDataToTable();
        }
    }

    private boolean validateTestDataInfo() throws Exception {
        if (cbSeperator.getText() != null && !cbSeperator.getText().isEmpty()) {
            if (txtFileName.getText() != null) {
                File filePath = new File(getSourceUrlAbsolutePath());
                if (filePath.isFile()) {
                    return true;
                }
            }
        }
        return false;
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

    private String getSourceUrlAbsolutePath() throws Exception {
        String sourceUrl = txtFileName.getText();
        if (chckIsRelativePath.getSelection()) {
            sourceUrl = PathUtil.relativeToAbsolutePath(sourceUrl, getProjectFolderLocation());
        }
        return sourceUrl;
    }

    private void warnFileToLarge() {
        sync.asyncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialog.openWarning(null, StringConstants.WARN,
                        MessageFormat.format(StringConstants.PA_FILE_TOO_LARGE, MAX_COLUMN_COUNT));

            }
        });
    }

    private void loadCSVDataToTable() {
        try {
            tableViewer.getTable().setRedraw(false);
            clearTable();
            if (validateTestDataInfo()) {
                String fileName = getSourceUrlAbsolutePath();
                CSVSeparator separator = CSVSeparator.fromValue(cbSeperator.getText());
                final CSVData csvData = new CSVData(fileName, chckEnableHeader.getSelection(), separator);

                int columnNumbers = csvData.getColumnNumbers();
                if (columnNumbers > MAX_COLUMN_COUNT) {
                    warnFileToLarge();
                    columnNumbers = MAX_COLUMN_COUNT;
                }

                for (int i = 0; i < columnNumbers; i++) {
                    final int idx = i;
                    TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
                    column.getColumn().setWidth(COLUMN_WIDTH);
                    column.setLabelProvider(new ColumnLabelProvider() {
                        @SuppressWarnings("unchecked")
                        @Override
                        public String getText(Object element) {
                            if (element instanceof List) {
                                List<String> rowData = ((List<String>) element);
                                if (rowData.size() > idx) {
                                    return rowData.get(idx);
                                }
                            }
                            return StringUtils.EMPTY;
                        }
                    });
                }

                int numEmptyHeader = 0;
                // The first column is No. column
                for (int tableColumnIdx = 1; tableColumnIdx < tableViewer.getTable()
                        .getColumnCount(); tableColumnIdx++) {
                    TableColumn column = tableViewer.getTable().getColumns()[tableColumnIdx];
                    String header = csvData.getColumnNames()[tableColumnIdx - 1];
                    if (StringUtils.isBlank(header)) {
                        numEmptyHeader++;
                        header = StringUtils.EMPTY;
                        column.setImage(ImageConstants.IMG_16_WARN_TABLE_ITEM);
                        column.setToolTipText(StringConstants.PA_TOOLTIP_WARNING_COLUMN_HEADER);
                    }
                    column.setText(header);
                }

                if (numEmptyHeader > 0 && chckEnableHeader.getSelection()) {
                    lblFileInfoStatus.setText(MessageFormat.format(StringConstants.PA_LBL_WARNING_COLUMN_HEADER,
                            numEmptyHeader, columnNumbers));
                }
                tableViewer.setInput(csvData.getData());
            }
            tableViewer.getTable().setHeaderVisible(chckEnableHeader.getSelection());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        } finally {
            tableViewer.getTable().setRedraw(true);
        }
    }

    @Persist
    public void save() {
        try {
            enableToReload = false;
            originalDataFile = updateDataFileProperty(originalDataFile.getLocation(), originalDataFile.getName(),
                    originalDataFile.getDescription(), DataFileDriverType.CSV, txtFileName.getText(),
                    chckEnableHeader.getSelection(), cbSeperator.getText(), chckIsRelativePath.getSelection());

            updateDataFile(originalDataFile);
            dirtyable.setDirty(false);
            refreshTreeEntity();
            sendTestDataUpdatedEvent(originalDataFile.getId());
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PA_ERROR_MSG_UNABLE_TO_SAVE_TEST_DATA,
                    e.getClass().getSimpleName());
        } finally {
            enableToReload = true;
        }
    }

    public DataFileEntity updateDataFileProperty(String pk, String name, String description,
            DataFileDriverType dataFileDriver, String dataSourceURL, boolean hasHeaders, String csvSeperator,
            boolean isInternalPath) throws Exception {

        DataFilePropertyInputEntity dataFileInputPro = new DataFilePropertyInputEntity();

        dataFileInputPro.setPk(pk);
        dataFileInputPro.setName(name);
        dataFileInputPro.setDescription(description);
        dataFileInputPro.setDataFileDriver(dataFileDriver.name());
        dataFileInputPro.setdataSourceURL(dataSourceURL);
        dataFileInputPro.setEnableHeader(hasHeaders);
        dataFileInputPro.setCsvSeperator(csvSeperator);
        dataFileInputPro.setIsInternalPath(isInternalPath);

        DataFileEntity dataFileEntity = TestDataController.getInstance().updateDataFile(dataFileInputPro);
        return dataFileEntity;
    }

    private String getProjectFolderLocation() {
        return ProjectController.getInstance().getCurrentProject().getFolderLocation();
    }

    @Override
    protected void updateChildInfo(DataFileEntity dataFile) {
        loadTestData(dataFile);
        enableRelativePath();
    }

    private void enableRelativePath() {
        chckIsRelativePath.setEnabled(StringUtils.isNotBlank(txtFileName.getText()));
    }

    @Override
    protected EPartService getPartService() {
        return partService;
    }

    private class ChangeCSVFileOperation extends AbstractOperation {
        private String oldCSVFilePath;

        private String newCSVFilePath;

        public ChangeCSVFileOperation(String newCSVFilePath) {
            super(ChangeCSVFileOperation.class.getName());
            this.newCSVFilePath = newCSVFilePath;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (newCSVFilePath == null || newCSVFilePath.equals(fCurrentFilePath)) {
                return Status.CANCEL_STATUS;
            }
            oldCSVFilePath = fCurrentFilePath;
            return redo(monitor, info);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doLoadExcelFile(newCSVFilePath);
            return Status.OK_STATUS;
        }

        private void doLoadExcelFile(String csvFileAbsolutePath) {

            lblFileInfoStatus.setText("");

            fCurrentFilePath = csvFileAbsolutePath;

            if (chckIsRelativePath.getSelection()) {
                txtFileName.setText(PathUtil.absoluteToRelativePath(csvFileAbsolutePath, getProjectFolderLocation()));
            } else {
                txtFileName.setText(fCurrentFilePath);
            }

            loadCSVDataToTable();
            dirtyable.setDirty(true);
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doLoadExcelFile(oldCSVFilePath);
            return Status.OK_STATUS;
        }
    }

    private class ChangeSeparatorOperation extends AbstractOperation {
        private String oldSeparator;

        private String newSeparator;

        public ChangeSeparatorOperation(String newSeparator) {
            super(ChangeSeparatorOperation.class.getName());
            this.newSeparator = newSeparator;
        }

        @Override
        public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            if (newSeparator.equals(fSelectedSeperator)) {
                return Status.CANCEL_STATUS;
            }
            oldSeparator = fSelectedSeperator.isEmpty() ? CSVSeparator.COMMA.toString().toUpperCase()
                    : fSelectedSeperator;
            doSetSeparator(newSeparator);
            return Status.OK_STATUS;
        }

        private void doSetSeparator(String separator) {
            fSelectedSeperator = separator;
            cbSeperator.setText(fSelectedSeperator);
            lblFileInfoStatus.setText("");

            loadCSVDataToTable();
            dirtyable.setDirty(true);
        }

        @Override
        public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doSetSeparator(newSeparator);
            return Status.OK_STATUS;
        }

        @Override
        public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
            doSetSeparator(oldSeparator);
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
            chckEnableHeader.setSelection(isSelected);
            loadCSVDataToTable();
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
            chckIsRelativePath.setSelection(isSelected);
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
