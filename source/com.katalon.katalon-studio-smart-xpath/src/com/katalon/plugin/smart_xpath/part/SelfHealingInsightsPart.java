package com.katalon.plugin.smart_xpath.part;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SelfHealingInsightsPart {
    @PostConstruct
    public void init(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setText("Hello from Huyen");
    }
}
