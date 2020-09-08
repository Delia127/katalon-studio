package com.katalon.plugin.smart_xpath.part.composites;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.components.controls.HelpComposite;
import com.kms.katalon.tracking.service.Trackings;

public class HelpSelfHealingInsightsComposite extends HelpComposite {

    public HelpSelfHealingInsightsComposite(Composite parent, String documentationUrl) {
        super(parent, documentationUrl);
        addMouseListener(parent, new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                Trackings.trackClickOnSelfHealingInsightsHelp();
            }
        });
    }

    private void addMouseListener(Control control, MouseListener listener) {
        control.addMouseListener(listener);
        if (control instanceof Composite) {
            for (final Control child : ((Composite) control).getChildren()) {
                addMouseListener(child, listener);
            }
        }
    }
}
