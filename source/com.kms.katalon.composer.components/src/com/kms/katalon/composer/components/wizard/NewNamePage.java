package com.kms.katalon.composer.components.wizard;

import java.text.MessageFormat;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.components.constants.StringConstants;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.controller.EntityNameController;

@SuppressWarnings("restriction")
public class NewNamePage extends WizardPage {

    private static final String NEW_NAME_PAGE_DESCRIPTION_TEMPLATE = StringConstants.WIZ_NEW_NAME_PAGE_DESCRIPTION_TEMPLATE;

    private static final String LABEL_NEW_NAME_TEXT = StringConstants.WIZ_LABEL_NEW_NAME_TEXT;

    private static final String NEW_NAME_PAGE_TITLE = StringConstants.WIZ_NEW_NAME_PAGE_TITLE;

    private Text txtName;

    private Composite container;

    private ITreeEntity treeEntity;

    public NewNamePage() {
        super(NewNamePage.class.getSimpleName(), NEW_NAME_PAGE_TITLE);
    }

    @Override
    public void createControl(Composite parent) {
        try {
            treeEntity = ((RenameWizard) getWizard()).getTreeEntity();
            setTitle(treeEntity.getTypeName());
            setMessage(MessageFormat.format(NEW_NAME_PAGE_DESCRIPTION_TEMPLATE, treeEntity.getTypeName(),
                    treeEntity.getTypeName()), IMessageProvider.INFORMATION);

            container = new Composite(parent, SWT.NONE);
            container.setLayout(new GridLayout(2, false));

            Label lblNewName = new Label(container, SWT.NONE);
            lblNewName.setText(LABEL_NEW_NAME_TEXT);

            txtName = new Text(container, SWT.BORDER | SWT.SINGLE);
            txtName.setText(treeEntity.getText());

            txtName.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    try {
                        if (canFlipToNextPage()) {
                            setErrorMessage(null);
                            ((RenameWizard) getWizard()).setNewNameValue(txtName.getText());
                            setPageComplete(true);
                        } else {
                            setPageComplete(false);
                        }
                    } catch (Exception exception) {
                        LoggerSingleton.getInstance().getLogger().error(exception);
                    }
                }
            });

            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            txtName.setLayoutData(gd);

            setControl(container);
            setPageComplete(false);
        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
        }

    }

    @Override
    public boolean canFlipToNextPage() {
        try {
            return validateName(treeEntity) && hasReferences(treeEntity) && !isDuplicated() && validateVariantName();
        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
        }
        return false;
    }

    public boolean isDuplicated() throws Exception {
        boolean isDuplicated = ((RenameWizard) getWizard()).getExistingNames().contains(txtName.getText());
        if (isDuplicated) {
            setErrorMessage(StringConstants.WIZ_NAME_ALREADY_EXISTS);
        }
        return isDuplicated;
    }

    public boolean validateVariantName() {
        try {
            for (String containedName : ((RenameWizard) getWizard()).getExistingNames()) {
                if (containedName.equalsIgnoreCase(txtName.getText())) {
                    setErrorMessage(StringConstants.WIZ_NAME_ALREADY_EXISTS_IN_DIFFERENT_CASE);
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean validateName(ITreeEntity treeEntity) {
        try {
            String name = txtName.getText();
            EntityNameController.getInstance().validateName(name);
            // please do not check for equalsIgnoreCase here
            // validateVariantName() will do the job
            return !name.equals(treeEntity.getText());
        } catch (Exception e) {
            setErrorMessage(e.getMessage());
            return false;
        }
    }

    /**
     * Check this entity is being called or used somewhere TODO: add a hasReferences() method to ITreeEntity to check if
     * a entity object is referenced by anything else.
     */
    private boolean hasReferences(ITreeEntity treeEntity) {
        return true;
    }

}
