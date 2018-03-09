package com.kms.katalon.composer.webservice.editor;

import java.io.File;
import java.text.DecimalFormat;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.webservice.constants.StringConstants;
import com.kms.katalon.composer.webservice.util.MimetypesFileTypeMapUtil;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.core.util.internal.PathUtil;
import com.kms.katalon.entity.webservice.FileBodyContent;

public class FileBodyEditor extends HttpBodyEditor {

    private final long KB = 1024;

    private final long MB = 1024 * 1024;

    private final DecimalFormat format = new DecimalFormat("#.##");

    private Text txtProjectLocation;

    private Button btnFolderChooser;

    private FileBodyContent fileBodyContent;

    private Label fileSizeLabel;

    private long fileSize;

    Composite mainContent;

    private static final String currentProjectFolder = ProjectController.getInstance()
            .getCurrentProject()
            .getFolderLocation();

    public FileBodyEditor(Composite parent, int style) {
        super(parent, style);
        createComponentLayout();
        addControlModifyListeners();
    }

    @Override
    public String getContentType() {
        return fileBodyContent.getContentType();
    }

    @Override
    public String getContentData() {
        fileBodyContent.setFilePath(txtProjectLocation.getText());
        fileBodyContent.setFileSize(fileSize);
        String type = MimetypesFileTypeMapUtil.getInstance()
                .getContentType(FilenameUtils.getExtension(txtProjectLocation.getText()));
        if (StringUtils.isNotEmpty(type)) {
            fileBodyContent.setContentType(type);
        }
        return JsonUtil.toJson(fileBodyContent);
    }

    @Override
    public void setInput(String rawBodyContentData) {

        if (StringUtils.isEmpty(rawBodyContentData)) {
            fileBodyContent = new FileBodyContent();
        } else {
            fileBodyContent = JsonUtil.fromJson(rawBodyContentData, FileBodyContent.class);
        }
    }

    @Override
    public void onBodyTypeChanged() {
        if (fileBodyContent == null) {
            fileBodyContent = new FileBodyContent();
        }

        if (fileBodyContent.getFilePath() != null) {
            txtProjectLocation.setText(fileBodyContent.getFilePath());
            fileSizeLabel.setText(StringConstants.LBL_FILE_SIZE + fommatFileSize(fileBodyContent.getContentLength()));
            fileSizeLabel.getParent().layout();
        }
        setContentTypeUpdated(true);
    }

    private void createComponentLayout() {
        GridLayout gridLayout = new GridLayout(3, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.setLayout(gridLayout);

        Label filePathLabel = new Label(this, SWT.NONE);
        filePathLabel.setText(StringConstants.LBL_FILE_PATH);

        txtProjectLocation = new Text(this, SWT.BORDER);
        txtProjectLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridData gridData = new GridData();
        gridData.widthHint = 500;
        txtProjectLocation.setLayoutData(gridData);
        txtProjectLocation.setText(currentProjectFolder);

        btnFolderChooser = new Button(this, SWT.FLAT);
        btnFolderChooser.setText(StringConstants.DIA_BTN_BROWSE);

        fileSizeLabel = new Label(this, SWT.NONE);
        fileSizeLabel.setText(StringConstants.LBL_FILE_SIZE);
        GridData gData = new GridData();
        gData.horizontalSpan = 3;
        fileSizeLabel.setLayoutData(gData);
    }

    private void addControlModifyListeners() {
        if (btnFolderChooser == null) {
            return;
        }
        btnFolderChooser.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(btnFolderChooser.getShell());
                dialog.setFilterPath(getProjectLocationInput());
                String path = dialog.open();
                if (path == null) {
                    return;
                }
                if (!txtProjectLocation.getText().equals(path)) {
                    FileBodyEditor.this.notifyListeners(SWT.Modify, new Event());
                }

                txtProjectLocation.setText(getRelativePath(path));
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    fileSize = file.length();
                    fileSizeLabel.setText(StringConstants.LBL_FILE_SIZE + fommatFileSize(fileSize));
                    fileSizeLabel.getParent().layout();
                }
            }
        });

        txtProjectLocation.addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                String newFilePath = ((Text) e.getSource()).getText();
                File file = new File(newFilePath);
                if (!file.isAbsolute()) {
                    file = new File(PathUtil.relativeToAbsolutePath(newFilePath, currentProjectFolder));
                }
                if (!file.exists() || !file.isFile()) {
                    MessageDialog.openWarning(null, StringConstants.WARN,
                            StringConstants.MSG_SPECIFIED_FILE_NOT_EXIST_WARN + newFilePath);
                    
                    fileSizeLabel.setText(StringConstants.LBL_FILE_SIZE);
                    fileSizeLabel.getParent().layout();
                }

                setContentTypeUpdated(true);
            }

            @Override
            public void focusGained(FocusEvent e) {
                // Do nothing.
            }
        });

    }

    private String fommatFileSize(long fileSizeBytes) {
        if (fileSizeBytes > MB) {
            return format.format(fileSizeBytes / MB) + " " + StringConstants.MEGABYTES_UNIT;
        }
        if (fileSizeBytes > KB) {
            return format.format(fileSizeBytes / KB) + " " + StringConstants.KILOBYTES_UNIT;
        }

        return fileSizeBytes + " " + StringConstants.BYTES_UNIT;
    }

    private String getRelativePath(String filePath) {
        File file = new File(filePath);
        if (file.isAbsolute()) {
            return PathUtil.absoluteToRelativePath(filePath, currentProjectFolder);
        }
        return filePath;
    }

    private String getProjectLocationInput() {
        if (txtProjectLocation == null || StringUtils.isBlank(txtProjectLocation.getText())) {
            return "";
        }
        String projectLocation = txtProjectLocation.getText().trim();
        if (!projectLocation.contains(File.separator)) {
            projectLocation = currentProjectFolder + File.separator + projectLocation;
        }
        return projectLocation;
    }

}
