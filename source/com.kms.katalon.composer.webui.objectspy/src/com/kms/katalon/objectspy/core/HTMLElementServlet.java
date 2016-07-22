package com.kms.katalon.objectspy.core;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.e4.core.services.log.Logger;
import org.w3c.dom.Document;

import com.kms.katalon.objectspy.dialog.ObjectSpyDialog;
import com.kms.katalon.objectspy.element.HTMLRawElement;
import com.kms.katalon.objectspy.util.HTMLElementUtil;

@SuppressWarnings("restriction")
public class HTMLElementServlet extends HttpServlet {
    public static final String WILD_CARD = "*";

    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    public static final String TEXT_HTML = "text/html";

    private static final String EQUALS = "=";

    private static final String ELEMENT_KEY = "element";

    private static final String ELEMENT_MAP_KEY = "elementsMap";

    private static final long serialVersionUID = 1L;

    private Logger logger;

    private ObjectSpyDialog objectSpyDialog;

    public HTMLElementServlet(Logger logger, ObjectSpyDialog objectSpyDialog) {
        this.logger = logger;
        this.objectSpyDialog = objectSpyDialog;
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
        if (sb.indexOf(EQUALS) == -1) {
            return;
        }
        String key = HTMLElementUtil.decodeURIComponent(sb.substring(0, sb.indexOf(EQUALS)));
        switch (key) {
            case ELEMENT_KEY: 
                addNewElement(response, sb.substring(sb.indexOf(EQUALS) + 1, sb.length()));
                break;
            case ELEMENT_MAP_KEY:
                updateHTMLDOM(response, sb);
                break;
        }
    }

    private void updateHTMLDOM(HttpServletResponse response, StringBuilder sb) {
        final String value = sb.substring(sb.indexOf(EQUALS) + 1, sb.length());

        response.setContentType(TEXT_HTML);
        response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, WILD_CARD);
        response.setStatus(HttpServletResponse.SC_OK);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Document htmlDocument = null;
                HTMLRawElement newRootElement = null;
                try {
                    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                    // root elements
                    htmlDocument = docBuilder.newDocument();
                    newRootElement = HTMLElementUtil.buildHTMLRawElement(htmlDocument, value);
                } catch (Exception e) {
                    logger.error(e);
                }
                if (htmlDocument != null && newRootElement != null) {
                    objectSpyDialog.setHTMLDOMDocument(newRootElement, htmlDocument);
                }
            }
        }).run();
    }

    private void addNewElement(HttpServletResponse response, String value) {
        response.setContentType(TEXT_HTML);
        response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, WILD_CARD);
        try {
            objectSpyDialog.addNewElement(HTMLElementUtil.buildHTMLElement(value));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            logger.error(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
