package com.kms.katalon.composer.checkpoint.dialogs.wizard;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.graphics.Point;

public abstract class AbstractCheckpointWizardPage extends WizardPage {

    protected AbstractCheckpointWizardPage(String pageName, String title, String description) {
        super(pageName, title, JFaceResources.getImageRegistry().getDescriptor(TitleAreaDialog.DLG_IMG_TITLE_BANNER));
        setDescription(description);
        setMessage(description, INFORMATION);
    }

    protected abstract boolean isComplete();

    public abstract Point getPageSize();

}
