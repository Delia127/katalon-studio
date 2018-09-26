package com.kms.katalon.composer.initializer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.constants.GlobalMessageConstants;

public class DisplayInitializer implements ApplicationInitializer {

    @Override
    public void setup() {
        Display display = Display.getDefault();
        addObjectSpyDialogMinimizeFilter(display);
    }

    private void addObjectSpyDialogMinimizeFilter(Display display) {
        display.addFilter(SWT.Activate, new ShellActiveListener(GlobalMessageConstants.OBJECT_SPY, display));
        display.addFilter(SWT.Activate, new ShellActiveListener(GlobalMessageConstants.WEB_RECORDER, display));
    }

    private class ShellActiveListener implements Listener {
        private final String shellName;

        private final Display display;

        public ShellActiveListener(String shellName, Display display) {
            this.shellName = shellName;
            this.display = display;
        }

        @Override
        public void handleEvent(Event event) {
            if (event.widget instanceof Shell) {
                final Shell shell = (Shell) event.widget;

                if ((shell.getStyle() & SWT.APPLICATION_MODAL) != 0) {
                    Shell[] shells = display.getShells();
                    Shell expectedShell = null;
                    int modalCount = 0;
                    for (Shell s : shells) {
                        int style = s.getStyle();
                        if (shellName.equals(s.getText()) && (style & SWT.ON_TOP) != 0) {
                            expectedShell = s;
                        }

                        if ((style & SWT.APPLICATION_MODAL) != 0) {
                            modalCount++;
                        }

                    }

                    if (expectedShell != null && shell.getParent() != expectedShell && modalCount == 1) {
                        final Shell oss = expectedShell;
                        Display.getDefault().asyncExec(() -> {
                            oss.setMinimized(true);
                        });
                    }
                }

            }
        }
    }
}
