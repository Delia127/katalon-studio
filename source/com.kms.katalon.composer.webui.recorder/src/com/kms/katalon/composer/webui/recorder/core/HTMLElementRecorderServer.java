package com.kms.katalon.composer.webui.recorder.core;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.kms.katalon.composer.webui.recorder.dialog.RecorderDialog;
import com.kms.katalon.objectspy.core.HTMLElementCaptureServer;

@SuppressWarnings("restriction")
public class HTMLElementRecorderServer extends HTMLElementCaptureServer {
	public HTMLElementRecorderServer(Logger logger, RecorderDialog recorderDialog) {
		this(0, logger, recorderDialog);
	}
	
	public HTMLElementRecorderServer(int port, Logger logger, RecorderDialog recorderDialog) {
        super(port, logger);
        addServlets(logger, recorderDialog, context);
    }
	
	private void addServlets(Logger logger, RecorderDialog recorderDialog, ServletContextHandler context) {
	    context.addServlet(new ServletHolder(new HTMLElementRecorderServlet(logger, recorderDialog)), "/*");
    }
}
