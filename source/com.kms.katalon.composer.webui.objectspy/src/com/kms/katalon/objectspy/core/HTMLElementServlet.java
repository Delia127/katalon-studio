package com.kms.katalon.objectspy.core;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.objectspy.util.HTMLElementUtil;
import com.kms.katalon.objectspy.util.WebElementUtils;

public class HTMLElementServlet extends HttpServlet {
    public static final String WILD_CARD = "*";

    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    public static final String TEXT_HTML = "text/html";

    private static final String EQUALS = "=";

    private static final String ELEMENT_KEY = "element";

    private static final String ELEMENT_MAP_KEY = "elementsMap";

    private static final long serialVersionUID = 1L;

    private HTMLElementCollector objectSpyDialog;

    public HTMLElementServlet(HTMLElementCollector objectSpyDialog) {
        this.objectSpyDialog = objectSpyDialog;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
        if (sb.indexOf(EQUALS) == -1) {
            return;
        }
        String key = HTMLElementUtil.decodeURIComponent(sb.substring(0, sb.indexOf(EQUALS)));
        switch (key) {
            case ELEMENT_KEY:
                addNewElement(response, sb.substring(sb.indexOf(EQUALS) + 1, sb.length()));
                break;
            case ELEMENT_MAP_KEY:
                // No longer needed
                break;
            default:
                KatalonRequestHandler.getInstance().processIncomeRequest(new ClientMessage(sb.toString()),
                        response.getOutputStream());
        }
    }

    private void addNewElement(HttpServletResponse response, String value) {
        response.setContentType(TEXT_HTML);
        response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, WILD_CARD);
        try {
            objectSpyDialog.addNewElement(WebElementUtils.buildWebElement(value));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
