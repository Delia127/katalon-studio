package com.kms.katalon.composer.windows.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.windows.action.WindowsAction;
import com.kms.katalon.composer.windows.action.WindowsActionMapping;
import com.kms.katalon.composer.windows.dialog.WindowsRecorderDialogV2;
import com.kms.katalon.composer.windows.element.CapturedWindowsElement;
import com.kms.katalon.composer.windows.record.model.RecordedElementLocatorHelper;
import com.kms.katalon.composer.windows.record.model.WindowsRecordedPayload;
import com.kms.katalon.composer.windows.record.model.WindowsStartAppPayload;
import com.kms.katalon.composer.windows.socket.WindowsServerSocketMessage.ServerMessageType;
import com.kms.katalon.core.util.internal.JsonUtil;

public class WindowsSocketServer {

    private ServerSocket server;

    private Socket socket;

    private boolean shouldReturn = false;

    private WindowsRecorderDialogV2 recorderDialog;
    
    private boolean isClientConnected = false;

    public WindowsSocketServer(WindowsRecorderDialogV2 recorderDialog) {
        this.recorderDialog = recorderDialog;
    }

    public void start() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (server == null) {
                        server = new ServerSocket(50005);
                    }
                    while (true && !shouldReturn) {
                        System.out.println("Waiting for the client request");
                        try {
                            socket = server.accept();
                            try (BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(socket.getInputStream()))) {

                                String line = null;

                                while ((line = reader.readLine()) != null) {
                                    analyseMessage(line);
                                }
                            }
                        } catch (IOException ex) {
                            LoggerSingleton.logError(ex);
                            isClientConnected = false;
                        }
                    }
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                } finally {
                    if (socket != null & !socket.isClosed()) {
                        try {
                            socket.close();
                        } catch (IOException ignored) {

                        }
                    }
                    if (server != null & !server.isClosed()) {
                        try {
                            server.close();
                        } catch (IOException ignored) {

                        }
                    }
                }
            }
        });
        thread.start();
    }

    private void analyseMessage(String message) {
        WindowsClientSocketMessage clientMessage = WindowsSocketMessageUtil.parseClientMessage(message);
        switch (clientMessage.getType()) {
            case CONNECT:
                isClientConnected = true;
                return;
            case APP_INFO: {
                WindowsStartAppPayload payload = JsonUtil.fromJson(clientMessage.getData(), WindowsStartAppPayload.class);
                WindowsActionMapping actionMapping = new WindowsActionMapping(WindowsAction.StartApplicationWithTitle, null);
                actionMapping.getData()[0].setValue(new ConstantExpressionWrapper(payload.getAppPath()));
                actionMapping.getData()[1].setValue(new ConstantExpressionWrapper(payload.getAppTitle()));
                recorderDialog.addActionMapping(actionMapping);
                return;
            }
            case RECORDING_ACTION: {
                try {
                    WindowsRecordedPayload payload = JsonUtil.fromJson(clientMessage.getData(), WindowsRecordedPayload.class);
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
                    actionMapping.setRecordedTime(payload.getRecordedTime());
                    recorderDialog.addActionMapping(actionMapping);
                } catch (Exception e) {
                    LoggerSingleton.logError(e);
                }
                break;
            }
        }
    }

    public void sendMessage(String message) throws IOException {
        OutputStream os = socket.getOutputStream();
        os.write(message.getBytes());
    }

    public void close() {
        if (socket != null) {
            try {
                sendMessage(WindowsSocketMessageUtil.createServerMessage(ServerMessageType.EXIT, ""));
            } catch (IOException ignored) {}
        }
        try {
            server.close();
        } catch (IOException e) {
        }
        shouldReturn = true;
    }

    public boolean isClientConnected() {
        return isClientConnected;
    }
}
