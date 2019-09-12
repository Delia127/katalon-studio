package com.kms.katalon.composer.checkpoint.dialogs.wizard;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.checkpoint.constants.StringConstants;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.testdata.job.LoadExcelFileJob;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.PathUtil;
import com.kms.katalon.entity.checkpoint.CheckpointSourceInfo;
import com.kms.katalon.entity.checkpoint.ExcelCheckpointSourceInfo;

public class NewCheckpointExcelPage extends AbstractCheckpointWizardPage {

    private static final String[] FILTER_NAMES = { "Microsoft Excel Spreadsheet Files (*.xls, *.xlsx)" };

    private static final String[] FILTER_EXTS = { "*.xlsx; *.xls" };

    protected Combo comboContentIndicator;

    private Text txtFileLocation;

    private Button btnBrowse;

    private Button chkRelativeLocation;

    private Button chkFirstRowHeader;

    private LoadExcelFileJob loadFileJob;

    private String fileLocation;

    private String contentIndicator;

    private boolean isRelativeLocation;

    private boolean isFirstRowHeader = true;

    public NewCheckpointExcelPage() {
        this(NewCheckpointExcelPage.class.getSimpleName());
    }

    public NewCheckpointExcelPage(String pageName) {
        super(pageName, StringConstants.WIZ_TITLE_EXCEL_DATA, StringConstants.WIZ_EXCEL_SOURCE_CONFIGURATION);
    }

    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);

        container.setLayout(new GridLayout(3, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblSourceUrl = new Label(container, SWT.NONE);
        lblSourceUrl.setText(StringConstants.DIA_LBL_FILE_PATH);

        txtFileLocation = new Text(container, SWT.BORDER | SWT.READ_ONLY);
        txtFileLocation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        btnBrowse = new Button(container, SWT.PUSH | SWT.FLAT);
        btnBrowse.setText(StringConstants.BROWSE);
        btnBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, false));

        Label lblIndicator = new Label(container, SWT.NONE);
        lblIndicator.setText(getContentIndicatorLabel());
        comboContentIndicator = new Combo(container, SWT.READ_ONLY);
        comboContentIndicator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        new Label(container, SWT.NONE);

        new Label(container, SWT.NONE);
        chkFirstRowHeader = new Button(container, SWT.CHECK);
        chkFirstRowHeader.setText(StringConstants.DIA_CHK_USING_FIRST_ROW_AS_HEADER);
        comboContentIndicator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        new Label(container, SWT.NONE);

        new Label(container, SWT.NONE);
        chkRelativeLocation = new Button(container, SWT.CHECK);
        chkRelativeLocation.setText(StringConstants.DIA_CHK_IS_USING_RELATIVE_PATH);
        chkRelativeLocation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        new Label(container, SWT.NONE);

        setControlListeners();
        setControl(container);
        chkFirstRowHeader.setSelection(isFirstRowHeader);
        setPageComplete(isComplete());
    }

    private void setControlListeners() {
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(btnBrowse.getShell());
                dialog.setFilterNames(getFilterNames());
                dialog.setFilterExtensions(getFilterExtensions());
                if (StringUtils.isBlank(fileLocation)) {
                    dialog.setFilterPath(getProjectFolderLocation());
                } else {
                    dialog.setFileName(fileLocation);
                }

                String absolutePath = dialog.open();
                if (absolutePath == null || absolutePath.equals(fileLocation)) {
                    return;
                }

                setFileLocation(absolutePath);
                comboContentIndicator.clearSelection();
                txtFileLocation.setText(StringUtils.defaultString(fileLocation));
                if (isRelativeLocation()) {
                    txtFileLocation.setText(PathUtil.absoluteToRelativePath(fileLocation, getProjectFolderLocation()));
                }
                if (StringUtils.isNotBlank(fileLocation)) {
                    loadIndicatorData(StringUtils.defaultString(getContentIndicator()));
                }
            }
        });

        comboContentIndicator.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String selectedSheetName = comboContentIndicator.getText();
                if (StringUtils.equals(getContentIndicator(), selectedSheetName)) {
                    return;
                }
                setContentIndicator(selectedSheetName);
                setPageComplete(isComplete());
            }
        });

        chkFirstRowHeader.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setFirstRowHeader(chkFirstRowHeader.getSelection());
            }
        });

        chkRelativeLocation.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean isRelativeLocation = chkRelativeLocation.getSelection();
                setRelativeLocation(isRelativeLocation);

                // update file path
                String fileLocation = txtFileLocation.getText();
                if (StringUtils.isBlank(fileLocation)) {
                    return;
                }
                if (isRelativeLocation) {
                    fileLocation = PathUtil.absoluteToRelativePath(fileLocation, getProjectFolderLocation());
                } else {
                    fileLocation = PathUtil.relativeToAbsolutePath(fileLocation, getProjectFolderLocation());
                }
                txtFileLocation.setText(fileLocation);
                setFileLocation(fileLocation);
            }
        });
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getContentIndicator() {
        return contentIndicator;
    }

    public void setContentIndicator(String contentIndicator) {
        this.contentIndicator = contentIndicator;
    }

    public boolean isRelativeLocation() {
        return isRelativeLocation;
    }

    public void setRelativeLocation(boolean isRelativeLocation) {
        this.isRelativeLocation = isRelativeLocation;
    }

    public boolean isFirstRowHeader() {
        return isFirstRowHeader;
    }

    public void setFirstRowHeader(boolean isFirstRowHeader) {
        this.isFirstRowHeader = isFirstRowHeader;
    }

    private void loadIndicatorData(String indicator) {
        setIndicatorSelection(indicator, getIndicatorData());
        setPageComplete(isComplete());
    }

    private void setIndicatorSelection(String indicator, String[] indicators) {
        if (indicators == null) {
            return;
        }
        comboContentIndicator.setItems(indicators);
        int indicatorSelectionIndex = ArrayUtils.indexOf(indicators, indicator);
        if (indicatorSelectionIndex == -1) {
            // if not found, select the fist item as default
            comboContentIndicator.select(0);
            setContentIndicator(comboContentIndicator.getText());
            return;
        }
        comboContentIndicator.select(indicatorSelectionIndex);
    }

    protected String[] getIndicatorData() {
        loadFileJob = new LoadExcelFileJob(getAbsoluteFileLocation(), chkFirstRowHeader.getSelection());
        loadFileJob.setUser(true);
        loadFileJob.schedule();
        loadFileJob.addJobChangeListener(new JobChangeAdapter() {

            @Override
            public void done(final IJobChangeEvent event) {
                UISynchronizeService.syncExec(new Runnable() {
                    @Override
                    public void run() {
                        if (event.getResult() != Status.OK_STATUS || loadFileJob.getExcelData() == null) {
                            return;
                        }
                        setIndicatorSelection(getContentIndicator(), loadFileJob.getExcelData().getSheetNames());
                        setPageComplete(isComplete());
                    }
                });
            }
        });
        return null;
    }

    protected String getContentIndicatorLabel() {
        return StringConstants.DIA_LBL_SHEET_NAME;
    }

    protected String[] getFilterNames() {
        return FILTER_NAMES;
    }

    protected String[] getFilterExtensions() {
        return FILTER_EXTS;
    }

    private String getAbsoluteFileLocation() {
        String fileLocation = txtFileLocation.getText();
        if (chkRelativeLocation.getSelection()) {
            fileLocation = PathUtil.relativeToAbsolutePath(fileLocation, getProjectFolderLocation());
        }
        return fileLocation;
    }

    private String getProjectFolderLocation() {
        return ProjectController.getInstance().getCurrentProject().getFolderLocation();
    }

    public CheckpointSourceInfo getSourceInfo() {
        if (this.equals(getContainer().getCurrentPage())) {
            return new ExcelCheckpointSourceInfo(getFileLocation(), getContentIndicator(), isRelativeLocation(),
                    isFirstRowHeader());
        }
        return new ExcelCheckpointSourceInfo();
    }

    @Override
    protected boolean isComplete() {
        return StringUtils.isNotBlank(txtFileLocation.getText())
                && StringUtils.isNotBlank(comboContentIndicator.getText());
    }

    @Override
    public Point getPageSize() {
        return getShell().computeSize(600, 300);
    }

}
