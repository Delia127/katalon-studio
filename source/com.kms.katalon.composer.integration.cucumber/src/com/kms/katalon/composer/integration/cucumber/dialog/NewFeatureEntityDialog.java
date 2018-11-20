package com.kms.katalon.composer.integration.cucumber.dialog;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.controls.HelpComposite;
import com.kms.katalon.composer.components.impl.constants.StringConstants;
import com.kms.katalon.composer.components.impl.dialogs.CustomTitleAreaDialog;
import com.kms.katalon.constants.DocumentationMessageConstants;
import com.kms.katalon.constants.GlobalMessageConstants;
import com.kms.katalon.controller.EntityNameController;
import com.kms.katalon.entity.file.FileEntity;

public class NewFeatureEntityDialog extends CustomTitleAreaDialog {

    private static final String NEW_FEATURE_FILE_NAME = "New Feature File";
    private static final String FEATURE_FILE_EXTESION = "feature";

    private Text txtName;

    private List<FileEntity> currentFeatures;

    private NewFeatureResult result;

    private Button chckGenerateSampleContent;

    public NewFeatureEntityDialog(Shell parentShell, List<FileEntity> currentFeatures) {
        super(parentShell);
        setShellStyle(SWT.RESIZE);
        this.currentFeatures = currentFeatures;
    }

    @Override
    protected void registerControlModifyListeners() {
        txtName.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                checkNewName(txtName.getText());
            }
        });
    }

    @Override
    protected void setInput() {
        chckGenerateSampleContent.setSelection(true);
        txtName.setText(getSuggestion(NEW_FEATURE_FILE_NAME, FEATURE_FILE_EXTESION));
        int dotIndex = txtName.getText().indexOf(".");
        if (dotIndex < 0) {
            txtName.selectAll();
        } else {
            txtName.setSelection(0, dotIndex);
        }
        setMessage("Create new Feature file", IMessageProvider.INFORMATION);
    }

    private boolean isNameDupplicated(String newName) {
        return this.currentFeatures.parallelStream().filter(l -> l.getName().equals(newName)).findAny().isPresent();
    }

    private String getSuggestion(String suggestion, String extension) {
        String newName = String.format("%s.%s", suggestion, extension);
        int index = 0;

        while (isNameDupplicated(newName)) {
            index += 1;
            newName = String.format("%s %d.%s", suggestion, index, extension);
        }
        return newName;
    }

    private void checkNewName(String newName) {
        if (isNameDupplicated(newName)) { 
            setMessage(StringConstants.DIA_NAME_EXISTED, IMessageProvider.ERROR);
            getButton(OK).setEnabled(false);
            return;
        }

        try {
            EntityNameController.getInstance().validateName(newName);
            setMessage("Create new Feature file", IMessageProvider.INFORMATION);
            getButton(OK).setEnabled(true);
        } catch (Exception e) {
            setMessage(e.getMessage(), IMessageProvider.ERROR);
            getButton(OK).setEnabled(false);
        }
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("New Feature File");
    }

    @Override
    protected Point getInitialSize() {
        return new Point(400, 250);
    }

    @Override
    protected void okPressed() {
        result = new NewFeatureResult(txtName.getText(), chckGenerateSampleContent.getSelection());
        super.okPressed();
    }

    public NewFeatureResult getResult() {
        return result;
    }
    
    public class NewFeatureResult {
        private final String newName;

        private final boolean generateTemplateAllowed;

        public NewFeatureResult(String newName, boolean generateTemplateAllowed) {
            this.newName = newName;
            this.generateTemplateAllowed = generateTemplateAllowed;
        }

        public boolean isGenerateTemplateAllowed() {
            return generateTemplateAllowed;
        }

        public String getNewName() {
            return newName;
        }
    }

    @Override
    protected Composite createContentArea(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite nameComposite = new Composite(container, SWT.NONE);
        nameComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 15;
        nameComposite.setLayout(layout);

        Label lblName = new Label(nameComposite, SWT.NONE);
        lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblName.setText(GlobalMessageConstants.NAME);

        txtName = new Text(nameComposite, SWT.BORDER);
        GridData gdTxtName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gdTxtName.minimumWidth = 200;
        txtName.setLayoutData(gdTxtName);

        chckGenerateSampleContent = new Button(container, SWT.CHECK);
        chckGenerateSampleContent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        chckGenerateSampleContent.setText("Generate sample Feature template");

        return container;
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        ((GridLayout) parent.getLayout()).numColumns++;
        Composite helpComposite = new Composite(parent, SWT.NONE);
        helpComposite.setLayout(new GridLayout(1, false));
        helpComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));
        new HelpComposite(helpComposite, DocumentationMessageConstants.CUCUMBER_FEATURE_FILE);
        
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
    }
}
