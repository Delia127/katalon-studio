package com.kms.katalon.composer.checkpoint.dialogs;

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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.checkpoint.constants.StringConstants;
import com.kms.katalon.composer.components.impl.dialogs.AbstractDialog;
import com.kms.katalon.composer.components.services.UISynchronizeService;
import com.kms.katalon.composer.testdata.job.LoadExcelFileJob;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.PathUtil;
import com.kms.katalon.entity.checkpoint.ExcelCheckpointSourceInfo;

public class EditCheckpointExcelSourceDialog extends AbstractDialog {

    private static final String[] FILTER_NAMES = { "Microsoft Excel Spreadsheet Files (*.xls, *.xlsx)" };

    private static final String[] FILTER_EXTS = { "*.xlsx; *.xls" };

    private ExcelCheckpointSourceInfo sourceInfo;

    protected Combo comboSheetNameOrCsvSeparator;

    private Text txtSourceUrl;

    private Button btnBrowse;

    private Button chkUsingRelativePath;

    private Button chkUsingFirstRowAsHeader;

    protected boolean isChanged;

    private LoadExcelFileJob loadFileJob;

    public EditCheckpointExcelSourceDialog(Shell parentShell, ExcelCheckpointSourceInfo sourceInfo) {
        super(parentShell);
        this.sourceInfo = sourceInfo;
        setDialogTitle(StringConstants.DIA_TITLE_CHECKPOINT_SOURCE_INFO);
    }

    @Override
    protected Control createDialogContainer(Composite parent) {
        Composite main = new Composite(parent, SWT.NONE);
        GridLayout glMain = new GridLayout();
        glMain.marginHeight = 0;
        glMain.marginWidth = 0;
        main.setLayout(glMain);

        createFileInfoPart(main);
        return parent;
    }

    private void createFileInfoPart(Composite parent) {
        Composite compFileInfo = new Composite(parent, SWT.NONE);
        compFileInfo.setLayout(new GridLayout(3, false));
        compFileInfo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblSourceUrl = new Label(compFileInfo, SWT.NONE);
        lblSourceUrl.setText(StringConstants.DIA_LBL_FILE_PATH);

        txtSourceUrl = new Text(compFileInfo, SWT.BORDER | SWT.READ_ONLY);
        txtSourceUrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        btnBrowse = new Button(compFileInfo, SWT.PUSH | SWT.FLAT);
        btnBrowse.setText(StringConstants.BROWSE);
        btnBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));

        Label lblIndicator = new Label(compFileInfo, SWT.NONE);
        lblIndicator.setText(getIndicatorLabel());
        comboSheetNameOrCsvSeparator = new Combo(compFileInfo, SWT.READ_ONLY);
        comboSheetNameOrCsvSeparator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        new Label(compFileInfo, SWT.NONE);

        new Label(compFileInfo, SWT.NONE);
        chkUsingFirstRowAsHeader = new Button(compFileInfo, SWT.CHECK);
        chkUsingFirstRowAsHeader.setText(StringConstants.DIA_CHK_USING_FIRST_ROW_AS_HEADER);
        comboSheetNameOrCsvSeparator.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        new Label(compFileInfo, SWT.NONE);

        new Label(compFileInfo, SWT.NONE);
        chkUsingRelativePath = new Button(compFileInfo, SWT.CHECK);
        chkUsingRelativePath.setText(StringConstants.DIA_CHK_IS_USING_RELATIVE_PATH);
        chkUsingRelativePath.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        new Label(compFileInfo, SWT.NONE);
    }

    private String getProjectFolderLocation() {
        return ProjectController.getInstance().getCurrentProject().getFolderLocation();
    }

    @Override
    protected void registerControlModifyListeners() {
        btnBrowse.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(btnBrowse.getShell());
                dialog.setFilterNames(getFilterNames());
                dialog.setFilterExtensions(getFilterExtensions());
                String filePath = sourceInfo.getSourceUrl();
                if (StringUtils.isBlank(filePath)) {
                    dialog.setFilterPath(getProjectFolderLocation());
                } else {
                    dialog.setFileName(filePath);
                }

                String absolutePath = dialog.open();
                if (absolutePath == null || absolutePath.equals(filePath)) {
                    return;
                }

                sourceInfo.setSourceUrl(absolutePath);
                comboSheetNameOrCsvSeparator.clearSelection();
                setInput();
                setChanged();
            }
        });

        comboSheetNameOrCsvSeparator.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String selectedSheetName = comboSheetNameOrCsvSeparator.getText();
                if (StringUtils.equals(sourceInfo.getSheetNameOrSeparator(), selectedSheetName)) {
                    return;
                }
                sourceInfo.setSheetNameOrSeparator(selectedSheetName);
                setChanged();
            }
        });

        chkUsingFirstRowAsHeader.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                sourceInfo.setUsingFirstRowAsHeader(chkUsingFirstRowAsHeader.getSelection());
                setChanged();
            }
        });

        chkUsingRelativePath.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean isRelativePath = chkUsingRelativePath.getSelection();
                sourceInfo.setUsingRelativePath(isRelativePath);
                setChanged();

                // update file path
                String sourceUrl = txtSourceUrl.getText();
                if (StringUtils.isBlank(sourceUrl)) {
                    return;
                }
                if (isRelativePath) {
                    sourceUrl = PathUtil.absoluteToRelativePath(sourceUrl, getProjectFolderLocation());
                } else {
                    sourceUrl = PathUtil.relativeToAbsolutePath(sourceUrl, getProjectFolderLocation());
                }
                txtSourceUrl.setText(sourceUrl);
                sourceInfo.setSourceUrl(sourceUrl);
            }
        });
    }

    @Override
    protected void setInput() {
        String sourceUrl = sourceInfo.getSourceUrl();
        boolean isRelativePath = sourceInfo.isUsingRelativePath();
        chkUsingRelativePath.setSelection(isRelativePath);
        chkUsingFirstRowAsHeader.setSelection(sourceInfo.isUsingFirstRowAsHeader());
        txtSourceUrl.setText(StringUtils.defaultString(sourceUrl));
        if (isRelativePath) {
            txtSourceUrl.setText(PathUtil.absoluteToRelativePath(sourceUrl, getProjectFolderLocation()));
        }
        if (StringUtils.isNotBlank(sourceUrl)) {
            loadIndicatorData(StringUtils.defaultString(sourceInfo.getSheetNameOrSeparator()));
        }
    }

    private void loadIndicatorData(String indicator) {
        loadSheetNames(indicator, getIndicatorData());
    }

    private void loadSheetNames(String indicator, String[] sheetNames) {
        if (sheetNames == null) {
            return;
        }
        comboSheetNameOrCsvSeparator.setItems(sheetNames);
        int indicatorSelectionIndex = ArrayUtils.indexOf(sheetNames, indicator);
        if (indicatorSelectionIndex == -1) {
            // if not found, select the fist item as default
            comboSheetNameOrCsvSeparator.select(0);
            sourceInfo.setSheetNameOrSeparator(comboSheetNameOrCsvSeparator.getText());
            return;
        }
        comboSheetNameOrCsvSeparator.select(indicatorSelectionIndex);
    }

    /**
     * @return Excel sheet names or CSV separators
     */
    protected String[] getIndicatorData() {
        loadFileJob = new LoadExcelFileJob(getSourceUrlAbsolutePath(), chkUsingFirstRowAsHeader.getSelection());
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
                        loadSheetNames(sourceInfo.getSheetNameOrSeparator(), loadFileJob.getExcelData().getSheetNames());
                    }
                });
            }
        });
        return null;
    }

    protected String getIndicatorLabel() {
        return StringConstants.DIA_LBL_SHEET_NAME;
    }

    protected String[] getFilterNames() {
        return FILTER_NAMES;
    }

    protected String[] getFilterExtensions() {
        return FILTER_EXTS;
    }

    public ExcelCheckpointSourceInfo getSourceInfo() {
        return sourceInfo;
    }

    public boolean isChanged() {
        return isChanged;
    }

    private void setChanged() {
        if (isChanged) {
            return;
        }

        this.isChanged = true;
    }

    private String getSourceUrlAbsolutePath() {
        String sourceUrl = txtSourceUrl.getText();
        if (chkUsingRelativePath.getSelection()) {
            sourceUrl = PathUtil.relativeToAbsolutePath(sourceUrl, getProjectFolderLocation());
        }
        return sourceUrl;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, super.getInitialSize().y);
    }

    @Override
    protected int getShellStyle() {
        return SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL | getDefaultOrientation();
    }

}
