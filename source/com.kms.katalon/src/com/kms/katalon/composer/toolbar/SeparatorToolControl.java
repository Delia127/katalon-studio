package com.kms.katalon.composer.toolbar;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SeparatorToolControl {
    @PostConstruct
    private void createWidget(Composite parent, MApplication app) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        
        Label label = new Label(composite, SWT.SEPARATOR);
        GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gridData.heightHint = 24;
        label.setLayoutData(gridData);
    }
}
