package com.kms.katalon.composer.components.impl.util;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class ControlUtil {
    private ControlUtil() {
        //Disable default constructor.
    }
    
    public static void recursiveSetEnabled(Control ctrl, boolean enabled) {
        if (ctrl instanceof Composite) {
            Composite comp = (Composite) ctrl;
            for (Control c : comp.getChildren()) {
                recursiveSetEnabled(c, enabled);
            }
        } else {
            ctrl.setEnabled(enabled);
        }
    }
}
