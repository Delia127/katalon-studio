package com.kms.katalon.composer.samples;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.project.constants.StringConstants;

public class NewSampleRemoteProjectDialog extends TitleAreaDialog {
    private static final String DEFAULT_PROJECT_LOCATION = System.getProperty("user.home") + File.separator
            + StringConstants.APP_NAME;

    private SampleProject sampleProject;

    private Text txtSourceURL;

    private Text txtProjectLocation;

    private Button btnFolderChooser;
    
    private String selectedProjectLocation;

    public NewSampleRemoteProjectDialog(Shell parentShell, SampleProject sampleProject) {
        super(parentShell);
        this.sampleProject = sampleProject;
    }

    protected void registerControlModifyListeners() {
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
        
        txtProjectLocation.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent event) {
                String newLocation = txtProjectLocation.getText();
                if (newLocation.isEmpty()) {
                    setMessage("Project location cannot be empty.", 
                            IMessageProvider.WARNING);
                    getButton(OK).setEnabled(false);
                    return;
                }
                if (new File(newLocation).exists()) {
                    setMessage("A project with the same name already exists in the selected location.", 
                            IMessageProvider.ERROR);
                    getButton(OK).setEnabled(false);
                    return;
                } else {
                    setInfoMessage();
                    getButton(OK).setEnabled(true);
                }
            }
        });
    }
    private void setInfoMessage() {
        setMessage("Please enter your project location", IMessageProvider.INFORMATION);
    }

    protected void setInput() {
        txtSourceURL.setText(sampleProject.getHref());
        String suggestedProjectLocation = getSuggestedName();
        
        txtProjectLocation.setText(suggestedProjectLocation);
        txtProjectLocation.selectAll();
        txtProjectLocation.forceFocus();

        setInfoMessage();
    }

    private String getSuggestedName() {
        String suggestedProjectLocation = new File(DEFAULT_PROJECT_LOCATION, sampleProject.getName())
                .getAbsolutePath();
        if (!new File(suggestedProjectLocation).exists()) {
            return suggestedProjectLocation;
        }
        int num = 1;
        while (true) {
            String newSuggestedLocation = String.format("%s_%d", suggestedProjectLocation, num);
            if (!new File(newSuggestedLocation).exists()) {
                return newSuggestedLocation;
            }
            num++;
        }
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

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.horizontalSpacing = 15;
        gridLayout.verticalSpacing = 15;
        container.setLayout(gridLayout);

        Label lblSourceURL = new Label(container, SWT.NONE);
        lblSourceURL.setText("Repository URL");

        txtSourceURL = new Text(container, SWT.BORDER);
        txtSourceURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtSourceURL.setEditable(false);
        txtSourceURL.setBackground(ColorUtil.getDisabledItemBackgroundColor());

        Label lblProjectLocation = new Label(container, SWT.NONE);
        lblProjectLocation.setText("Project Location");

        Composite locationControlsComposite = new Composite(container, SWT.NONE);
        locationControlsComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        GridLayout glLocationControl = new GridLayout(2, false);
        glLocationControl.marginWidth = 0;
        glLocationControl.marginHeight = 0;
        locationControlsComposite.setLayout(glLocationControl);
        txtProjectLocation = new Text(locationControlsComposite, SWT.BORDER);
        txtProjectLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        btnFolderChooser = new Button(locationControlsComposite, SWT.NONE);
        btnFolderChooser.setText("Browse...");
        
        setInput();
        registerControlModifyListeners();
        return container;
    }

    @Override
    protected Point getInitialSize() {
        return new Point(600, super.getInitialSize().y);
    }
    
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Clone Remote Project");
    }
    
    @Override
    protected void setShellStyle(int newShellStyle) {
        super.setShellStyle(newShellStyle | SWT.RESIZE);
    }
    
    @Override
    protected void okPressed() {
       this.selectedProjectLocation = txtProjectLocation.getText();
        super.okPressed();
    }

    public String getSelectedProjectLocation() {
        return selectedProjectLocation;
    }
}
