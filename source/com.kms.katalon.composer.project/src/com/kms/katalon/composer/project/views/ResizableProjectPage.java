package com.kms.katalon.composer.project.views;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.graphics.Point;

public abstract class ResizableProjectPage extends WizardPage {

    protected ResizableProjectPage(String pageName) {
        super(pageName);
    }

    public abstract Point getPageSize();

}
