package com.kms.katalon.composer.execution.launcher;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;

@SuppressWarnings("restriction")
public class IDEConsoleManager {    
    public static IConsole getIConsole(ILaunch launch) {        
        IConsole[] consoles = ConsolePlugin.getDefault().getConsoleManager().getConsoles();
        if (consoles == null) {
            return null;
        }

        for (IConsole console : consoles) {
            if (console instanceof ProcessConsole && launch == ((ProcessConsole) console).getProcess().getLaunch()) {
                return console;
            }
        }
        return null;
    }
    
    public static void openLaunchConsole(ILaunch launch) {
        IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()[0];
        IConsoleView consoleView = (IConsoleView) workbenchPage.findView(IConsoleConstants.ID_CONSOLE_VIEW);
        
        if (!workbenchPage.isPartVisible(consoleView)) {
            return;
        }
        
        IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();

        IConsole console = getIConsole(launch);        
        if (console != null) {
            manager.showConsoleView(console);
        }
    }
}
