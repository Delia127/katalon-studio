package com.kms.katalon.composer.webservice.editor;

import java.io.File;
import java.text.DecimalFormat;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

    private Text txtFilePath;

    private Button btnFolderChooser;

    private FileBodyContent fileBodyContent;

    private Label fileSizeLabel;

    private Label size;

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
        return getViewModel().getContentType();
    }

    @Override
    public String getContentData() {
        fileBodyContent.setFilePath(txtFilePath.getText());
        fileBodyContent.setFileSize(fileSize);
        String type = MimetypesFileTypeMapUtil.getContentType(FilenameUtils.getExtension(txtFilePath.getText()));
        
        setContentTypeUpdated(!StringUtils.isEmpty(type));
        if (StringUtils.isNotEmpty(type)) {
            fileBodyContent.setContentType(type);
        }
        updateViewModel();
        return getViewModel().getContentData();
    }

    @Override
    public void setInput(String rawBodyContentData) {

        if (StringUtils.isEmpty(rawBodyContentData)) {
            fileBodyContent = new FileBodyContent();
        } else {
            fileBodyContent = JsonUtil.fromJson(rawBodyContentData, FileBodyContent.class);
        }
        updateViewModel();
    }

    @Override
    public void onBodyTypeChanged() {
        if (fileBodyContent == null) {
            fileBodyContent = new FileBodyContent();
        }

        if (fileBodyContent.getFilePath() != null) {
            txtFilePath.setText(fileBodyContent.getFilePath());
            size.setText(fommatFileSize(fileBodyContent.getContentLength()));
            size.getParent().layout();
        }
        updateViewModel();
    }

    private void createComponentLayout() {
        GridLayout gridLayout = new GridLayout(3, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.setLayout(gridLayout);

        GridLayout filePartLayout = new GridLayout();
        filePartLayout.marginWidth = 15;

        Label filePathLabel = new Label(this, SWT.NONE);
        filePathLabel.setText(StringConstants.LBL_FILE_PATH);

        txtFilePath = new Text(this, SWT.BORDER);
        txtFilePath.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        txtFilePath.setMessage(currentProjectFolder);

        btnFolderChooser = new Button(this, SWT.FLAT);
        btnFolderChooser.setText(StringConstants.DIA_BTN_BROWSE);

        fileSizeLabel = new Label(this, SWT.NONE);
        fileSizeLabel.setText(StringConstants.LBL_FILE_SIZE);

        size = new Label(this, SWT.NONE);
    }

    private void addControlModifyListeners() {
        if (btnFolderChooser == null) {
            return;
        }
        btnFolderChooser.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(btnFolderChooser.getShell());
                dialog.setFilterPath(currentProjectFolder);
                String path = dialog.open();
                if (path == null) {
                    return;
                }

                String type = MimetypesFileTypeMapUtil.getContentType(FilenameUtils.getExtension(getRelativePath(path)));
                if (!txtFilePath.getText().equals(path) && StringUtils.isNotEmpty(type)) {
                    fileBodyContent.setContentType(type);
                    setContentTypeUpdated(true);
                }
                FileBodyEditor.this.notifyListeners(SWT.Modify, new Event());

                File file = new File(path);
                String savePath = (file.isAbsolute() && path.contains(currentProjectFolder)) 
                                    ? getRelativePath(path): path;
                                    
                txtFilePath.setText(savePath);
                if (file.exists() && file.isFile()) {
                    fileSize = file.length();
                    size.setText(fommatFileSize(fileSize));
                    size.getParent().layout();
                }
            }
        });
        
        txtFilePath.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                FileBodyEditor.this.fireModifyEvent();
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
    
    private void updateViewModel() {
        getViewModel().setContentData(JsonUtil.toJson(fileBodyContent));
        getViewModel().setContentType(fileBodyContent.getContentType());
    }

}
