package com.kms.katalon.composer.webui.recorder.core;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.kms.katalon.composer.webui.recorder.dialog.RecorderDialog;
import com.kms.katalon.objectspy.core.HTMLElementCaptureServer;

public class HTMLElementRecorderServer extends HTMLElementCaptureServer {
	public HTMLElementRecorderServer(int port, RecorderDialog recorderDialog, Class<?> socketClass) {
        super(port, socketClass);
        addServlets(recorderDialog, context);
    }
	
	private void addServlets(RecorderDialog recorderDialog, ServletContextHandler context) {
	    context.addServlet(new ServletHolder(new HTMLElementRecorderServlet(recorderDialog)), "/*");
    }
}
