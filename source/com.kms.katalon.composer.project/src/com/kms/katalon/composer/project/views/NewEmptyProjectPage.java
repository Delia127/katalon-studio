package com.kms.katalon.composer.project.views;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

// TODO: Consider to reuse NewProjectDialog GUI
public class NewEmptyProjectPage extends ResizableProjectPage {

    private static final String DEFAULT_PROJECT_LOCATION = System.getProperty("user.home") + File.separator
            + StringConstants.APP_NAME;

    private static final int PROJECT_DESC_TEXT_LEFT_MARGIN = 3;

    private static final int PROJECT_DESC_DISPLAY_LINE_NUMBER = 4;

    private Text txtProjectName;

    private Text txtProjectLocation;

    private StyledText txtProjectDescription;

    private ProjectEntity project;

    private Button btnFolderChooser;

    Composite mainContent;

    public NewEmptyProjectPage() {
        super(StringConstants.VIEW_NEW_EMPTY_PROJECT_PAGE_NAME);
        setTitle(StringConstants.VIEW_TITLE_NEW_PROJ);
        setDescription(StringConstants.VIEW_MSG_PLS_ENTER_PROJ_INFO);
    }

    @Override
    public void createControl(Composite parent) {

        mainContent = new Composite(parent, SWT.NONE);
        mainContent.setLayoutData(new GridData(GridData.FILL_BOTH));
        mainContent.setLayout(new GridLayout(2, false));

        Label label = new Label(mainContent, SWT.NONE);
        label.setText(StringConstants.VIEW_LBL_NAME);

        txtProjectName = new Text(mainContent, SWT.BORDER);
        txtProjectName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // Add
        label = new Label(mainContent, SWT.NONE);
        label.setText(StringConstants.VIEW_LBL_LOCATION);
        createFileChooserComposite(mainContent);

        label = new Label(mainContent, SWT.NONE);
        label.setText(StringConstants.VIEW_LBL_DESCRIPTION);
        label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

        txtProjectDescription = new StyledText(mainContent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        // increase projectDescription's height on MAC OS so that it is able to show 4 text line
        // and modify the left margin so that it is vertical alignment with project name and project location
        if (Platform.getOS().equals(Platform.OS_MACOSX)) {
            GridData layout = new GridData(GridData.FILL_BOTH);
            GC graphicContext = new GC(txtProjectDescription);
            FontMetrics fm = graphicContext.getFontMetrics();
            layout.heightHint = PROJECT_DESC_DISPLAY_LINE_NUMBER * fm.getHeight();
            txtProjectDescription.setLayoutData(layout);
            graphicContext.dispose();

            txtProjectDescription.setLeftMargin(PROJECT_DESC_TEXT_LEFT_MARGIN);
        } else {
            txtProjectDescription.setLayoutData(new GridData(GridData.FILL_BOTH));
        }

        new Label(mainContent, SWT.NONE);
        label = new Label(mainContent, SWT.NONE);
        label.setText(StringConstants.VIEW_LBL_NEW_PROJECT_WIZARD_TIP);
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        if (project != null) {
            txtProjectName.setText(project.getName());
            txtProjectLocation.setText(project.getFolderLocation());
            txtProjectDescription.setText(project.getDescription());
            txtProjectLocation.setEnabled(false);
        }

        addControlModifyListeners();

        // required to avoid an error in the system
        setControl(mainContent);
        setPageComplete(false);
    }

    private Composite createFileChooserComposite(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout theLayout = new GridLayout((project == null) ? 2 : 1, false);
        theLayout.marginWidth = 0;
        container.setLayout(theLayout);

        txtProjectLocation = new Text(container, SWT.BORDER);
        txtProjectLocation.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        txtProjectLocation.setText(DEFAULT_PROJECT_LOCATION);

        if (project == null) {
            btnFolderChooser = new Button(container, SWT.FLAT);
            btnFolderChooser.setText(StringConstants.VIEW_BTN_BROWSE);
        }

        return container;
    }

    private void addControlModifyListeners() {
        txtProjectLocation.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                checkInput();
            }
        });

        txtProjectName.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                checkInput();
            }
        });
        if (Platform.getOS().equals(Platform.OS_MACOSX)) {
            txtProjectName.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    checkInput();
                }

            });
        }
        // On MAC OS, with some input source language that enable Auto correct spelling (ex: Telex or VNI)
        // if the input text not accept by the system then when the input control lost focus the text will be remove as
        // default.
        if (Platform.getOS().equals(Platform.OS_MACOSX)) {
            txtProjectName.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    String projectName = txtProjectName.getText();
                    super.focusLost(e);
                    txtProjectName.setText(projectName);
                }
            });
        }

        if (btnFolderChooser == null) {
            return;
        }
        btnFolderChooser.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dialog = new DirectoryDialog(btnFolderChooser.getShell());
                dialog.setFilterPath(getProjectLocationInput());
                String path = dialog.open();
                if (path == null) {
                    return;
                }
                txtProjectLocation.setText(path);
            }
        });
    }

    private void checkInput() {
        setErrorMessage(null);
        setPageComplete(validateProjectFolderLocation() && validateProjectName() && validateProjectNameDuplication());
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

    private boolean validateProjectFolderLocation() {
        String projectLocation = getProjectLocationInput();
        if (StringUtils.isBlank(projectLocation)) {
            setErrorMessage(StringConstants.VIEW_ERROR_MSG_PROJ_LOC_CANNOT_BE_BLANK);
            return false;
        }
        Path folderPath = null;
        try {
            folderPath = Paths.get(projectLocation);
        } catch (InvalidPathException invalidPathException) {
            setErrorMessage(StringConstants.VIEW_ERROR_MSG_PROJ_LOC_INVALID);
            return false;
        }

        File folderLocation = folderPath.toFile();
        if (!folderLocation.exists()) {
            return true;
        }
        if (!folderLocation.canRead()) {
            setErrorMessage(StringConstants.VIEW_ERROR_MSG_PROJ_LOC_NOT_READABLE);
            return false;
        }
        if (!folderLocation.canWrite()) {
            setErrorMessage(StringConstants.VIEW_ERROR_MSG_PROJ_LOC_NOT_WRITEABLE);
            return false;
        }
        return true;
    }

    private boolean validateProjectName() {
        if (StringUtils.isBlank(txtProjectName.getText())) {
            setErrorMessage(StringConstants.VIEW_ERROR_MSG_PROJ_NAME_CANNOT_BE_BLANK);
            return false;
        }
        return true;
    }

    private boolean validateProjectNameDuplication() {
        String projectLocation = getProjectLocationInput();
        String projectName = txtProjectName.getText().trim();
        try {
            if (!ProjectController.getInstance().validateNewProjectName(projectLocation, projectName)) {
                setErrorMessage(StringConstants.VIEW_ERROR_MSG_PROJ_NAME_EXISTED_IN_LOC);
                return false;
            }
            return true;
        } catch (Exception e) {
            setErrorMessage(e.getMessage());
        }
        return false;
    }

    public String getProjectName() {
        return txtProjectName.getText();
    }

    public String getProjectLocation() {
        return txtProjectLocation.getText();
    }

    public String getProjectDescription() {
        return txtProjectDescription.getText();
    }

    @Override
    public Point getPageSize() {
        return new Point(550, 350);
    }
}
