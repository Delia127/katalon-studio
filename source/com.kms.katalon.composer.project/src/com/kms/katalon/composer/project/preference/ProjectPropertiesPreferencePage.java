package com.kms.katalon.composer.project.preference;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.util.ControlUtils;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.composer.project.handlers.OpenProjectHandler;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.EntityNameController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

public class ProjectPropertiesPreferencePage extends PreferencePage {
    private static ProjectController projectController = ProjectController.getInstance();

    private static final int DF_DESCRIPTION_LINE_NUMBER = 4;

    private Text txtName;

    private Text txtLocation;

    private StyledText txtDescription;

    private ProjectEntity project;

    private Label lblErrorMessage;

    private Composite container;

    public ProjectPropertiesPreferencePage() {
        project = projectController.getCurrentProject();
    }

    @Override
    protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout glContainer = new GridLayout(2, false);
        glContainer.horizontalSpacing = 15;
        glContainer.verticalSpacing = 10;
        container.setLayout(glContainer);

        Label lblName = new Label(container, SWT.NONE);
        lblName.setText(StringConstants.NAME);

        txtName = new Text(container, SWT.BORDER);
        txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblLocation = new Label(container, SWT.NONE);
        lblLocation.setText(StringConstants.VIEW_LBL_LOCATION);

        txtLocation = new Text(container, SWT.BORDER | SWT.READ_ONLY);
        txtLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        Label lblDescription = new Label(container, SWT.NONE);
        lblDescription.setText(StringConstants.VIEW_LBL_DESCRIPTION);

        txtDescription = new StyledText(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
        txtDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

        GridData layout = new GridData(GridData.FILL_BOTH);
        layout.grabExcessVerticalSpace = false;
        GC graphicContext = new GC(txtDescription);
        FontMetrics fm = graphicContext.getFontMetrics();
        layout.heightHint = DF_DESCRIPTION_LINE_NUMBER * fm.getHeight();
        txtDescription.setLayoutData(layout);
        new Label(container, SWT.NONE);

        lblErrorMessage = new Label(container, SWT.WRAP);
        lblErrorMessage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        lblErrorMessage.setForeground(ColorUtil.getTextErrorColor());
        graphicContext.dispose();

        setInput();

        registerControlModifyListener();
        return container;
    }

    private void setInput() {
        txtName.setText(project.getName());
        txtLocation.setText(project.getFolderLocation());
        txtDescription.setText(project.getDescription());
        lblErrorMessage.setText(StringUtils.EMPTY);
    }

    private void registerControlModifyListener() {
        txtName.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                getApplyButton().setEnabled(validateProjectName());
            }
        });
        txtDescription.addListener(SWT.Modify , ControlUtils.getAutoHideStyledTextScrollbarListener);
        txtDescription.addListener(SWT.Resize , ControlUtils.getAutoHideStyledTextScrollbarListener);
    }

    private boolean validateProjectName() {
        String newProjectName = txtName.getText();
        if (newProjectName.equals(project.getName())) {
            return true;
        }
        if (StringUtils.isBlank(newProjectName)) {
            lblErrorMessage.setText(StringConstants.VIEW_ERROR_MSG_PROJ_NAME_CANNOT_BE_BLANK);
            return false;
        }
        try {
            EntityNameController.getInstance().validateName(newProjectName);
        } catch (Exception e) {
            lblErrorMessage.setText(e.getMessage());
            return false;
        }
        lblErrorMessage.setText(StringUtils.EMPTY);
        return true;
    }

    @Override
    public boolean okToLeave() {
        return isNotOpenedYet() || isValidInput();
    }

    @Override
    public boolean performOk() {
        if (isNotOpenedYet()) {
            return true;
        }
        boolean valid = isValidInput();
        if (valid) {
            updateProject();
        }
        return valid;
    }

    private void updateProject() {
        try {
            projectController.updateProject(txtName.getText(), txtDescription.getText(), project.getId());
            OpenProjectHandler.updateProjectTitle(project, ModelServiceSingleton.getInstance().getModelService(),
                    ApplicationSingleton.getInstance().getApplication());
            EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.PROJECT_UPDATED, true);
        } catch (Exception e) {
            MessageDialog.openError(getShell(), StringConstants.ERROR_TITLE, e.getMessage());
        }
    }

    private boolean isValidInput() {
        return lblErrorMessage.getText().isEmpty();
    }

    private boolean isNotOpenedYet() {
        return container == null || container.isDisposed();
    }

    @Override
    protected void performDefaults() {
        setInput();
    }
}
