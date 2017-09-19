package com.kms.katalon.composer.initializer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.constants.GlobalStringConstants;

public class DisplayInitializer implements ApplicationInitializer {

    @Override
    public void setup() {
        Display display = Display.getDefault();
        addObjectSpyDialogMinimizeFilter(display);
    }
    
    private void addObjectSpyDialogMinimizeFilter(Display display) {
        display.addFilter(SWT.Activate, new Listener() {

            @Override
            public void handleEvent(Event event) {
                if (event.widget instanceof Shell) {
                    final Shell shell = (Shell) event.widget;
                    
                    if ((shell.getStyle() & SWT.APPLICATION_MODAL) != 0) {
                        Shell[] shells = display.getShells();
                        Shell objectSpyShell = null;
                        int modalCount = 0;
                        for (Shell s : shells) {
                            int style = s.getStyle();
                            if (GlobalStringConstants.OBJECT_SPY.equals(s.getText()) &&
                                    (style & SWT.ON_TOP) != 0) {
                                    objectSpyShell = s;
                            } 
                            
                            if ((style & SWT.APPLICATION_MODAL) != 0) {
                                modalCount++;
                            }
                            
                        }
                        
                        if (objectSpyShell != null && shell.getParent() != objectSpyShell && modalCount == 1) {
                            final Shell oss = objectSpyShell;
                            Display.getDefault().asyncExec(() -> {
                                oss.setMinimized(true);
                            });
                        }
                    }
                    
                   
                }
            }
        });
    }
}
