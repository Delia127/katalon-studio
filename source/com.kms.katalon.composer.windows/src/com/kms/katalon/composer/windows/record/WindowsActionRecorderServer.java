package com.kms.katalon.composer.windows.record;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.kms.katalon.composer.windows.dialog.WindowsRecorderDialogV2;

public class WindowsActionRecorderServer extends WindowsActionsCaptureServer {
    private static final long serialVersionUID = 1L;

    public WindowsActionRecorderServer(int port, WindowsRecorderDialogV2 recorderDialog, Class<?> socketClass) {
        super(port, socketClass);
        addServlets(recorderDialog, context);
    }
    
    protected void addServlets(WindowsRecorderDialogV2 recorderDialog, ServletContextHandler context) {
        context.addServlet(new ServletHolder(new WindowsActionRecorderServlet(recorderDialog)), "/*");
    }
}
