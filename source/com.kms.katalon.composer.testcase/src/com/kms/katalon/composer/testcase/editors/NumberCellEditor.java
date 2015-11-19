package com.kms.katalon.composer.testcase.editors;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class NumberCellEditor extends TextCellEditor {

    public NumberCellEditor(Composite parent) {
        super(parent);
    }

    @Override
    protected Control createControl(Composite parent) {
        super.createControl(parent);
        text.addVerifyListener(new VerifyListener() {

            @Override
            public void verifyText(VerifyEvent e) {
                String oldValue = ((Text) e.getSource()).getText();
                String enterValue = e.text;
                String newValue = oldValue.substring(0, e.start) + enterValue + oldValue.substring(e.end);
                // allow number format 132; -546; 12.5; .2; -.4; 456.; -879.
                if (!newValue.matches("[+-]?\\d*((\\.)?(\\d+)?)?")) {
                    e.doit = false;
                    return;
                }
            }
        });
        return text;
    }

}
