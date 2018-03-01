package com.kms.katalon.composer.webservice.editor;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
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
import com.kms.katalon.constants.GlobalStringConstants;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.webservice.FileBodyContent;

public class BinaryBodyEditor extends HttpBodyEditor {

    private Text txtProjectLocation;

    private ProjectEntity project;

    private Button btnFolderChooser;

    private FileBodyContent fileBodyContent;

    Composite mainContent;

    private static final String DEFAULT_PROJECT_LOCATION = System.getProperty("user.home") + File.separator
            + GlobalStringConstants.APP_NAME;

    public BinaryBodyEditor(Composite parent, int style) {
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
        fileBodyContent.setAbsoluteFilePath(txtProjectLocation.getText());
        return JsonUtil.toJson(fileBodyContent);
    }

    @Override
    public void setInput(String rawBodyContentData) {
        if (fileBodyContent != null) {
            return;
        }

        if (StringUtils.isEmpty(rawBodyContentData)) {
            fileBodyContent = new FileBodyContent();
        } else {
            fileBodyContent = JsonUtil.fromJson(rawBodyContentData, FileBodyContent.class);
        }
        if (fileBodyContent.getAbsoluteFilePath() != null) {
            txtProjectLocation.setText(fileBodyContent.getAbsoluteFilePath());
        }
    }

    private void createComponentLayout() {
        GridLayout gridLayout = new GridLayout(3, false);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        this.setLayout(gridLayout);

        Label label = new Label(this, SWT.NONE);
        label.setText(StringConstants.LBL_FILE_PATH);

        Composite container = new Composite(this, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout theLayout = new GridLayout((project == null) ? 2 : 1, false);
        theLayout.marginWidth = 0;
        container.setLayout(theLayout);

        txtProjectLocation = new Text(container, SWT.BORDER);
        txtProjectLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridData gridData = new GridData();
        gridData.widthHint = 500;

        txtProjectLocation.setLayoutData(gridData);
        txtProjectLocation.setText(DEFAULT_PROJECT_LOCATION);

        if (project == null) {
            btnFolderChooser = new Button(container, SWT.FLAT);
            btnFolderChooser.setText(StringConstants.DIA_BTN_BROWSE);
        }
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
                    BinaryBodyEditor.this.notifyListeners(SWT.Modify, new Event());
                }
                txtProjectLocation.setText(path);
            }
        });
    }

    private String getProjectLocationInput() {
        if (txtProjectLocation == null || StringUtils.isBlank(txtProjectLocation.getText())) {
            return "";
        }
        String projectLocation = txtProjectLocation.getText().trim();
        if (!projectLocation.contains(File.separator)) {
            projectLocation = DEFAULT_PROJECT_LOCATION + File.separator + projectLocation;
        }
        return projectLocation;
    }

}
