package com.kms.katalon.composer.testdata.parts;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
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
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestDataController;
import com.kms.katalon.core.testdata.reader.CSVReader;
import com.kms.katalon.core.testdata.reader.CSVSeperator;
import com.kms.katalon.core.util.PathUtils;
import com.kms.katalon.entity.dal.exception.DuplicatedFileNameException;
import com.kms.katalon.entity.testdata.DataFileEntity;
import com.kms.katalon.entity.testdata.DataFilePropertyInputEntity;
import com.kms.katalon.entity.testdata.DataFileEntity.DataFileDriverType;

public class CSVTestDataPart extends TestDataMainPart {
    private static final String[] FILTER_NAMES = { "Comma Separated Values Files (*.csv)", "All Files (*.*)" };

    private static final String[] FILTER_EXTS = { "*.csv", "*.*" };

    private TableViewer tableViewer;
    private Text txtFileName;
    private Button chckIsRelativePath;
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


    private Listener layoutFileInfoCompositeListener = new Listener() {

        @Override
        public void handleEvent(org.eclipse.swt.widgets.Event event) {
            layoutFileInfoComposite();
        }
    };
    
    
    @PostConstruct
    public void createControls(Composite parent, MPart mpart) {
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
        GridLayout glCompositeFileInfoHeader = new GridLayout(2, false);
        glCompositeFileInfoHeader.marginWidth = 0;
        glCompositeFileInfoHeader.marginHeight = 0;
        compositeFileInfoHeader.setLayout(glCompositeFileInfoHeader);
        compositeFileInfoHeader.setCursor(compositeFileInfoHeader.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

        btnExpandFileInfoComposite = new ImageButton(compositeFileInfoHeader, SWT.NONE);

        lblFileInfo = new Label(compositeFileInfoHeader, SWT.NONE);
        lblFileInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
        lblFileInfo.setText(StringConstants.PA_LBL_FILE_INFO);
        lblFileInfo.setFont(JFaceResources.getFontRegistry().getBold(""));

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
        cbSeperator.setItems(CSVSeperator.stringValues());
        cbSeperator.select(0);
        cbSeperator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

        Composite compositeCheckBoxes = new Composite(compositeFileInfoDetails, SWT.NONE);
        compositeCheckBoxes.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
        GridLayout glCompositeCheckBoxes = new GridLayout(2, false);
        glCompositeCheckBoxes.horizontalSpacing = 15;
        compositeCheckBoxes.setLayout(glCompositeCheckBoxes);

        chckIsRelativePath = new Button(compositeCheckBoxes, SWT.CHECK);
        chckIsRelativePath.setText(StringConstants.PA_CHKBOX_USE_RELATIVE_PATH);
        new Label(compositeCheckBoxes, SWT.NONE);

        isFileInfoExpanded = true;
        redrawBtnExpandFileInfo();

        return compositeFileInfo;
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
        tableViewer.getTable().setLinesVisible(true);

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

        addListeners();
        return compositeTable;
    }

    private void addListeners() {
        btnExpandFileInfoComposite.addListener(SWT.MouseDown, layoutFileInfoCompositeListener);
        lblFileInfo.addListener(SWT.MouseDown, layoutFileInfoCompositeListener);
        
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(btnBrowse.getShell());
                dialog.setFilterNames(FILTER_NAMES);
                dialog.setFilterExtensions(FILTER_EXTS);
                dialog.setFilterPath(getProjectFolderLocation());
                String path = dialog.open();
                if (path == null) return;
                if (chckIsRelativePath.getSelection()) {
                    txtFileName.setText(PathUtils.absoluteToRelativePath(path, getProjectFolderLocation()));
                } else {
                    txtFileName.setText(path);
                }

                loadCSVData();
                dirtyable.setDirty(true);

            }
        });

        cbSeperator.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                loadCSVData();
                dirtyable.setDirty(true);
            }
        });

        chckIsRelativePath.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    if (txtFileName.getText() != null) {
                        String sourceUrl = txtFileName.getText();
                        if (chckIsRelativePath.getSelection()) {
                            txtFileName.setText(PathUtils.absoluteToRelativePath(sourceUrl, getProjectFolderLocation()));
                        } else {
                            txtFileName.setText(PathUtils.relativeToAbsolutePath(sourceUrl, getProjectFolderLocation()));
                        }
                    }
                    dirtyable.setDirty(true);
                } catch (Exception e1) {
                    LoggerSingleton.logError(e1);
                }
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

    private void redrawBtnExpandFileInfo() {
        btnExpandFileInfoComposite.getParent().setRedraw(false);
        if (isFileInfoExpanded) {
            btnExpandFileInfoComposite.setImage(ImageConstants.IMG_16_ARROW_UP_BLACK);
        } else {
            btnExpandFileInfoComposite.setImage(ImageConstants.IMG_16_ARROW_DOWN_BLACK);
        }
        btnExpandFileInfoComposite.getParent().setRedraw(true);

    }

    private void loadTestData(DataFileEntity dataFile) {
        txtFileName.setText(dataFile.getDataSourceUrl());

        if (dataFile.getCsvSeperator() != null) {
            cbSeperator.setText(dataFile.getCsvSeperator());
        }

        chckIsRelativePath.setSelection(dataFile.getIsInternalPath());

        if (cbSeperator.getText() != null && !cbSeperator.getText().isEmpty()) {
            loadCSVData();
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
        while (tableViewer.getTable().getColumnCount() > 1) {
            tableViewer.getTable().getColumns()[1].dispose();
        }
    }

    private String getSourceUrlAbsolutePath() throws Exception {
        String sourceUrl = txtFileName.getText();
        if (chckIsRelativePath.getSelection()) {
            sourceUrl = PathUtils.relativeToAbsolutePath(sourceUrl, getProjectFolderLocation());
        }
        return sourceUrl;
    }

    private void loadCSVData() {
        try {
            tableViewer.getTable().setRedraw(false);
            clearTable();
            if (validateTestDataInfo()) {
                String fileName = getSourceUrlAbsolutePath();
                CSVSeperator seperator = CSVSeperator.fromValue(cbSeperator.getText());
                final CSVReader reader = new CSVReader(fileName, seperator, true);

                List<String[]> data = reader.getData();
                for (int i = 0; i < reader.getColumnCount(); i++) {
                    final int idx = i;
                    TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
                    column.getColumn().setWidth(200);
                    column.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(Object element) {
                            if (element != null && element instanceof String[]) {
                                String[] rowData = ((String[]) element);
                                if (rowData.length > idx) {
                                    return rowData[idx];
                                }
                            }
                            return StringUtils.EMPTY;
                        }
                    });
                }

                for (int i = 0; i < tableViewer.getTable().getColumnCount() - 1; i++) {
                    TableColumn column = tableViewer.getTable().getColumns()[i + 1];
                    String header = reader.getColumnNames()[i];
                    if (header == null) {
                        header = StringUtils.EMPTY;
                    }
                    column.setText(header);
                }

                tableViewer.setInput(data);
            }

            tableViewer.getTable().setRedraw(true);
            tableViewer.getTable().setHeaderVisible(true);
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
                    DataFileDriverType.CSV, txtFileName.getText(), cbSeperator.getText(),
                    chckIsRelativePath.getSelection());

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
            DataFileDriverType dataFileDriver, String dataSourceURL, String csvSeperator, boolean isInternalPath)
            throws Exception {

        DataFilePropertyInputEntity dataFileInputPro = new DataFilePropertyInputEntity();

        dataFileInputPro.setPk(pk);
        dataFileInputPro.setName(name);
        dataFileInputPro.setDescription(description);
        dataFileInputPro.setDataFileDriver(dataFileDriver.name());
        dataFileInputPro.setdataSourceURL(dataSourceURL);
        dataFileInputPro.setEnableHeader(true);
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
    }

}
