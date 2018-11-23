package com.kms.katalon.composer.webui.recorder.core;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.webui.recorder.dialog.RecorderDialog;
import com.kms.katalon.composer.webui.recorder.util.HTMLActionJsonParser;
import com.kms.katalon.objectspy.core.HTMLElementServlet;
import com.kms.katalon.objectspy.util.HTMLElementUtil;

public class HTMLElementRecorderServlet extends HttpServlet {
    private static final String ELEMENT_KEY = "element";
    private static final long serialVersionUID = 1L;
    private RecorderDialog recorderDialog;

    public HTMLElementRecorderServlet(RecorderDialog recorderDialog) {
        this.recorderDialog = recorderDialog;
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_OK);
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
        if (sb.indexOf("=") == -1) {
            return;
        }
        String key = sb.substring(0, sb.indexOf("="));
        if (!HTMLElementUtil.decodeURIComponent(key).equals(ELEMENT_KEY)) {
            return;
        }
        String value = sb.substring(sb.indexOf("=") + 1, sb.length());
        response.setContentType(HTMLElementServlet.TEXT_HTML);
        response.addHeader(HTMLElementServlet.ACCESS_CONTROL_ALLOW_ORIGIN, HTMLElementServlet.WILD_CARD);
        try {
            recorderDialog.addNewActionMapping(HTMLActionJsonParser.parseJsonIntoHTMLActionMapping(value));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}