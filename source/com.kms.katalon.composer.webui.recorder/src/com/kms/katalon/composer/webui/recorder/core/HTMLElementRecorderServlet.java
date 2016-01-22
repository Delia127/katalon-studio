package com.kms.katalon.composer.webui.recorder.core;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;

import com.kms.katalon.composer.webui.recorder.action.HTMLActionMapping;
import com.kms.katalon.composer.webui.recorder.util.HTMLActionUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.objectspy.util.HTMLElementUtil;

@SuppressWarnings("restriction")
public class HTMLElementRecorderServlet extends HttpServlet {
    private static final String ELEMENT_KEY = "element";
    private static final long serialVersionUID = 1L;
    private Logger logger;
    private IEventBroker eventBroker;

    public HTMLElementRecorderServlet(Logger logger, IEventBroker eventBroker) {
        this.logger = logger;
        this.eventBroker = eventBroker;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } finally {
            reader.close();
        }
        if (sb.indexOf("=") != -1) {
            String key = sb.substring(0, sb.indexOf("="));
            if (HTMLElementUtil.decodeURIComponent(key).equals(ELEMENT_KEY)) {
                String value = sb.substring(sb.indexOf("=") + 1, sb.length());
                HTMLActionMapping newActionMapping = null;
                try {
                    newActionMapping = HTMLActionUtil.buildActionMapping(value);
                } catch (Exception e) {
                    logger.error(e);
                }
                response.setContentType("text/html");
                response.addHeader("Access-Control-Allow-Origin", "*");
                if (newActionMapping != null) {
                    eventBroker.post(EventConstants.RECORDER_ELEMENT_ADDED, newActionMapping);
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }
        }
    }
}