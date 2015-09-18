package com.kms.katalon.composer.testdata.parts;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
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
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.control.ImageButton;
import com.kms.katalon.composer.components.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.testdata.constants.ImageConstants;
import com.kms.katalon.composer.testdata.constants.StringConstants;
import com.kms.katalon.composer.testdata.util.Util;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.core.testdata.ExcelData;
import com.kms.katalon.core.util.PathUtils;
import com.kms.katalon.entity.dal.exception.DuplicatedFileNameException;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testdata.DataFileEntity.DataFileDriverType;
import com.kms.katalon.entity.testdata.DataFilePropertyInputEntity;

public class ExcelTestDataPart extends TestDataMainPart {
    private static final String[] FILTER_NAMES = { "Microsoft Excel Spreadsheet Files (*.xls, *.xlsx)" };

    private static final String[] FILTER_EXTS = { "*.xlsx; *.xls" };

    private Text txtFileName;
    private Combo cbbSheets;
    private TableViewer tableViewer;
    private Label lblSheetName;
    private Button ckcbUseRelativePath;
    private Button btnBrowse;
    private ImageButton btnExpandFileInfo;
    private Composite compositeFileInfoDetails;
    private Composite compositeFileInfoHeader;
    private Composite compositeTable;
    private Composite compositeFileInfo;
    private boolean isFileInfoExpanded;

    private Listener layoutFileInfoCompositeListener = new Listener() {

        @Override
        public void handleEvent(org.eclipse.swt.widgets.Event event) {
            layoutFileInfoComposite();
        }
    };

    private Label lblFileInfo;

    @PostConstruct
    public void createControls(Composite parent, MPart mpart) {
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
                    compositeFileInfo.setSize(compositeFileInfo.getSize().x, compositeFileInfo.getSize().y
                            - compositeTable.getSize().y);
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
        GridLayout glFileCompositeHeader = new GridLayout(2, false);
        glFileCompositeHeader.marginWidth = 0;
        glFileCompositeHeader.marginHeight = 0;
        compositeFileInfoHeader.setLayout(glFileCompositeHeader);
        compositeFileInfoHeader.setCursor(compositeFileInfoHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        btnExpandFileInfo = new ImageButton(compositeFileInfoHeader, SWT.NONE);
        GridData gdBtnExpandFileInfo = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        btnExpandFileInfo.setLayoutData(gdBtnExpandFileInfo);

        lblFileInfo = new Label(compositeFileInfoHeader, SWT.NONE);
        lblFileInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        lblFileInfo.setText(StringConstants.PA_LBL_FILE_INFO);
        lblFileInfo.setFont(JFaceResources.getFontRegistry().getBold(""));

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
        gdTxtFileName.heightHint = 18;
        txtFileName.setLayoutData(gdTxtFileName);
        txtFileName.setEditable(false);
        btnBrowse = new Button(compositeFileName, SWT.PUSH);
        btnBrowse.setText(StringConstants.PA_BTN_BROWSE);

        Composite compositeSheetName = new Composite(compositeFileInfoDetails, SWT.NONE);
        compositeSheetName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        compositeSheetName.setLayout(new GridLayout(2, false));

        lblSheetName = new Label(compositeSheetName, SWT.NONE);
        GridData gdLblSheetName = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        lblSheetName.setLayoutData(gdLblSheetName);
        lblSheetName.setText(StringConstants.PA_LBL_SHEET_NAME);

        cbbSheets = new Combo(compositeSheetName, SWT.READ_ONLY);
        GridData gdCbbSheets = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdCbbSheets.heightHint = 20;
        cbbSheets.setLayoutData(gdCbbSheets);

        Composite compositeCheckBoxes = new Composite(compositeFileInfoDetails, SWT.NONE);
        compositeCheckBoxes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        GridLayout glCompositeCheckBoxes = new GridLayout(2, false);
        glCompositeCheckBoxes.horizontalSpacing = 15;
        glCompositeCheckBoxes.marginWidth = 0;
        compositeCheckBoxes.setLayout(glCompositeCheckBoxes);

        ckcbUseRelativePath = new Button(compositeCheckBoxes, SWT.CHECK);
        ckcbUseRelativePath.setText(StringConstants.PA_CHKBOX_USE_RELATIVE_PATH);

        isFileInfoExpanded = true;
        redrawBtnExpandFileInfo();

        addControlListeners();
        return compositeFileInfo;
    }

    private String getSourceUrlAbsolutePath() throws Exception {
        String sourceUrl = txtFileName.getText();
        if (ckcbUseRelativePath.getSelection()) {
            sourceUrl = PathUtils.relativeToAbsolutePath(sourceUrl, getProjectFolderLocation());
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
        txtFileName.setText(dataFile.getDataSourceUrl());
        ckcbUseRelativePath.setSelection(dataFile.getIsInternalPath());

        Thread loadSheetThread = new Thread(new Runnable() {

            @Override
            public void run() {
                loadSheetNames();
                if (!cbbSheets.isDisposed()) {
                    cbbSheets.setText(dataFile.getSheetName());
                    loadExcelData();
                }
            }
        });

        currentThreads.add(loadSheetThread);
        Display.getCurrent().asyncExec(loadSheetThread);

    }

    private void addControlListeners() {
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(btnBrowse.getShell());
                dialog.setFilterNames(FILTER_NAMES);
                dialog.setFilterExtensions(FILTER_EXTS);
                dialog.setFilterPath(getProjectFolderLocation());

                String absolutePath = dialog.open();
                if (absolutePath == null)
                    return;
                if (ckcbUseRelativePath.getSelection()) {
                    txtFileName.setText(PathUtils.absoluteToRelativePath(absolutePath, getProjectFolderLocation()));
                } else {
                    txtFileName.setText(absolutePath);
                }
                loadSheetNames();
                loadExcelData();
                dirtyable.setDirty(true);
            }
        });

        cbbSheets.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                loadExcelData();
                dirtyable.setDirty(true);
            }
        });

        ckcbUseRelativePath.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (txtFileName.getText() != null) {
                    String sourceUrl = txtFileName.getText();
                    if (ckcbUseRelativePath.getSelection()) {
                        txtFileName.setText(PathUtils.absoluteToRelativePath(sourceUrl, getProjectFolderLocation()));
                    } else {
                        txtFileName.setText(PathUtils.relativeToAbsolutePath(sourceUrl, getProjectFolderLocation()));
                    }
                }
                dirtyable.setDirty(true);
            }
        });

        btnExpandFileInfo.addListener(SWT.MouseDown, layoutFileInfoCompositeListener);
        lblFileInfo.addListener(SWT.MouseDown, layoutFileInfoCompositeListener);

    }

    private void redrawBtnExpandFileInfo() {
        btnExpandFileInfo.getParent().setRedraw(false);
        if (isFileInfoExpanded) {
            btnExpandFileInfo.setImage(ImageConstants.IMG_16_ARROW_UP_BLACK);
        } else {
            btnExpandFileInfo.setImage(ImageConstants.IMG_16_ARROW_DOWN_BLACK);
        }
        btnExpandFileInfo.getParent().setRedraw(true);

    }

    private void loadSheetNames() {
        try {
            if (cbbSheets.isDisposed()) {
                return;
            }
            cbbSheets.setItems(Util.loadSheetName(getSourceUrlAbsolutePath()).toArray(new String[] {}));
            cbbSheets.select(0);
        } catch (Exception e) {
            MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), StringConstants.WARN_TITLE,
                    StringConstants.PA_WARN_MSG_UNABLE_TO_LOAD_SHEET_NAME);
        }
    }

    private void clearTable() {
        while (tableViewer.getTable().getColumnCount() > 1) {
            tableViewer.getTable().getColumns()[1].dispose();
        }
        tableViewer.getTable().clearAll();
    }

    private void loadExcelData() {
        try {
            String[] headers = new String[] {};
            List<String[]> data = new ArrayList<>();

            if (cbbSheets.getSelectionIndex() >= 0) {
                try {
                    ExcelData excelData = new ExcelData(cbbSheets.getText(), getSourceUrlAbsolutePath());
                    headers = excelData.getColumnNames();
                    if (headers.length > 0) {
                        for (int i = 1; i <= excelData.getRowNumbers(); i++) {
                            List<String> arrayValues = new ArrayList<>();
                            for (int columnIndex = 1; columnIndex <= excelData.getColumnNumbers(); columnIndex++) {
                                arrayValues.add(excelData.getValue(columnIndex, i));
                            }
                            data.add(arrayValues.toArray(new String[arrayValues.size()]));
                        }
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }
            }

            tableViewer.getTable().setRedraw(false);
            clearTable();
            for (int i = 0; i < headers.length; i++) {
                final int idx = i;
                if (idx >= tableViewer.getTable().getColumnCount() - 1) {
                    TableViewerColumn columnViewer = new TableViewerColumn(tableViewer, SWT.NONE);
                    String header = headers[i];
                    if (header != null) {
                        columnViewer.getColumn().setText(header);
                    } else {
                        columnViewer.getColumn().setText(StringUtils.EMPTY);
                    }

                    columnViewer.getColumn().setWidth(200);
                    columnViewer.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(Object element) {
                            if (element != null) {
                                if (element instanceof String[]) {
                                    return ((String[]) element)[idx];
                                }
                            }
                            return element != null ? element.toString() : StringUtils.EMPTY;
                        }
                    });
                }
            }

            tableViewer.setInput(data);
            tableViewer.getTable().setHeaderVisible(true);
            tableViewer.getTable().setLinesVisible(true);
            tableViewer.getTable().setRedraw(true);

        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Persist
    public void save() {
        try {
            String oldPk = dataFile.getId();
            String oldName = dataFile.getName();
            String oldIdForDisplay = TestDataController.getInstance().getIdForDisplay(dataFile);
            dataFile = updateDataFileProperty(dataFile.getLocation(), txtName.getText(), txtDesc.getText(),
                    DataFileDriverType.ExcelFile, txtFileName.getText(), cbbSheets.getText(),
                    ckcbUseRelativePath.getSelection(), true);
            updateDataFile(dataFile);
            dirtyable.setDirty(false);
            eventBroker.post(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, null);
            if (!StringUtils.equalsIgnoreCase(oldName, dataFile.getName())) {
                eventBroker.post(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM, new Object[] { oldIdForDisplay,
                        TestDataController.getInstance().getIdForDisplay(dataFile) });
            }
            sendTestDataUpdatedEvent(oldPk);
        } catch (DuplicatedFileNameException e) {
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PA_ERROR_MSG_UNABLE_TO_SAVE_TEST_DATA,
                    MessageFormat.format(StringConstants.PA_ERROR_REASON_TEST_DATA_EXISTED, txtName.getText()));
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            MultiStatusErrorDialog.showErrorDialog(e, StringConstants.PA_ERROR_MSG_UNABLE_TO_SAVE_TEST_DATA, e
                    .getClass().getSimpleName());
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
    }
}
