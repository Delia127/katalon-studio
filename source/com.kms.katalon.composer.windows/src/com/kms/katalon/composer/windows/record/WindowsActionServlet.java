package com.kms.katalon.composer.windows.record;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.windows.action.WindowsAction;
import com.kms.katalon.composer.windows.action.WindowsActionMapping;
import com.kms.katalon.composer.windows.dialog.WindowsRecorderDialogV2;
import com.kms.katalon.composer.windows.element.CapturedWindowsElement;
import com.kms.katalon.composer.windows.record.model.RecordedElementLocatorHelper;
import com.kms.katalon.composer.windows.record.model.WindowsRecordedPayload;
import com.kms.katalon.core.util.internal.JsonUtil;

public class WindowsActionServlet extends HttpServlet {
    public static final String WILD_CARD = "*";

    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    public static final String TEXT_HTML = "text/html";

    private static final long serialVersionUID = 1L;

    private WindowsRecorderDialogV2 recorderDialog;

    public WindowsActionServlet(WindowsRecorderDialogV2 objectSpyDialog) {
        this.recorderDialog = objectSpyDialog;
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
        addNewElement(response, sb.toString());
    }

    private void addNewElement(HttpServletResponse response, String value) {
        response.setContentType(TEXT_HTML);
        response.addHeader(ACCESS_CONTROL_ALLOW_ORIGIN, WILD_CARD);
        try {

            WindowsRecordedPayload payload = JsonUtil.fromJson(value, WindowsRecordedPayload.class);
            RecordedElementLocatorHelper locatorHelper = new RecordedElementLocatorHelper(payload);
            CapturedWindowsElement element = locatorHelper.getCapturedElement();
            WindowsActionMapping actionMapping = null;
            switch (payload.getActionName()) {
                case "click": {
                    actionMapping = new WindowsActionMapping(WindowsAction.Click, element);
                    break;
                }
                case "rightClick": {
                    actionMapping = new WindowsActionMapping(WindowsAction.RightClick, element);
                    break;
                }
                case "setText": {
                    actionMapping = new WindowsActionMapping(WindowsAction.SetText, element);
                    actionMapping.getData()[0].setValue(new ConstantExpressionWrapper(payload.getActionData()));
                }
            }
            recorderDialog.addActionMapping(actionMapping);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
