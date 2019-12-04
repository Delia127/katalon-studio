package com.kms.katalon.composer.toolbar;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.constants.MessageConstants;

public class SubscriptionToolControl {

    @PostConstruct
    void createWidget(Composite parent, MToolControl toolControl) {
        ToolBar toolbar = new ToolBar(parent, SWT.FLAT | SWT.RIGHT);
        toolbar.setForeground(ColorUtil.getToolBarForegroundColor());
        ToolItem subscriptionToolItem = new ToolItem(toolbar, SWT.PUSH);
        subscriptionToolItem.setImage(ImageConstants.IMG_KATALON_SUBSCRIPTION_24);
        subscriptionToolItem.setToolTipText(MessageConstants.TOOLTIP_SUBSCRIPTION);
        subscriptionToolItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Program.launch(MessageConstants.URL_KATALON_PRICING);
            }
        });
    }
}
