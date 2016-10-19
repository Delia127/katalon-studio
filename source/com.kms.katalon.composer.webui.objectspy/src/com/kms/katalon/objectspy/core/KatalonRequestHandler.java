package com.kms.katalon.objectspy.core;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.concurrent.Executors;

import org.eclipse.e4.core.services.log.Logger;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.objectspy.highlight.HighlightRequest;

@SuppressWarnings("restriction")
public class KatalonRequestHandler {
    private static KatalonRequestHandler instance;

    private KatalonRequest currentRequest;

    private RequestFailedListener requestFailedListener;
    
    private Logger logger = LoggerSingleton.getInstance().getLogger();

    private static int clientId = 0;

    private KatalonRequestHandler() {
    }

    public static KatalonRequestHandler getInstance() {
        if (instance == null) {
            instance = new KatalonRequestHandler();
        }
        return instance;
    }

    public void setRequest(KatalonRequest request, RequestFailedListener listener) {
        requestFailedListener = listener;
        currentRequest = request;
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(MessageConstant.REQUEST_TIMEOUT);
                    removeCurrentRequest();
                } catch (Exception ex) {
                    logger.error(ex);
                } finally {
                    currentRequest = null;
                    requestFailedListener = null;
                }
            }
        });
    }

    public void processIncomeRequest(ClientMessage clientMessage, OutputStream out) {
        switch (clientMessage.messageType) {
            case RequestType.GET_CLIENT_ID:
                setClientId(out);
                break;
            case RequestType.GET_REQUEST:
                sendCurrentRequest(clientMessage, out);
                break;
            default:
                processRequestResult(clientMessage, out);

        }
    }

    private void sendCurrentRequest(ClientMessage clientMessage, OutputStream out) {
        try (PrintWriter pw = new PrintWriter(out, true)) {
            pw.print(getStringCurrentRequest(clientMessage));
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    private void setClientId(OutputStream out) {
        try (PrintWriter pw = new PrintWriter(out, true)) {
            pw.print(nextClientId());
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    private static synchronized int nextClientId() {
        return ++clientId;
    }

    private String getStringCurrentRequest(ClientMessage clientMessage) {
        if (currentRequest == null || currentRequest.getRequestId() <= clientMessage.prevRequestId) {
            return RequestType.NO_REQUEST;
        }

        if (currentRequest instanceof HighlightRequest && !((HighlightRequest) currentRequest).isFound()) {
            return currentRequest.getRequestId() + MessageConstant.REQUEST_SEPARATOR + RequestType.FIND_TEST_OBJECT + MessageConstant.REQUEST_SEPARATOR + currentRequest.getData();
        }

        if (currentRequest instanceof HighlightRequest && ((HighlightRequest) currentRequest).isFound()) {
            if (((HighlightRequest) currentRequest).getClientId() == clientMessage.clientId) {
                return currentRequest.getRequestId() + MessageConstant.REQUEST_SEPARATOR
                        + RequestType.HIGHLIGHT_TEST_OBJECT + MessageConstant.REQUEST_SEPARATOR
                        + currentRequest.getData();
            }
            return RequestType.NO_REQUEST;
        }

        return currentRequest.getRequestId() + MessageConstant.REQUEST_SEPARATOR + currentRequest.getRequestType() + MessageConstant.REQUEST_SEPARATOR + currentRequest.getData();
    }

    public void processRequestResult(ClientMessage clientMessage, OutputStream out) {
        if (currentRequest.getRequestId() != clientMessage.prevRequestId) {
            return;
        }
        switch (currentRequest.getRequestType()) {
            case RequestType.HIGHLIGHT_TEST_OBJECT:
                processHighlightObjectResult(clientMessage, out);
                break;
        }
    }

    public synchronized void removeCurrentRequest() {
        if (currentRequest == null) {
            return;
        }
        if (currentRequest.isFailed() && requestFailedListener != null) {
            requestFailedListener.requestFailed(currentRequest.processFailed());
        }

        currentRequest = null;
    }

    private synchronized void processHighlightObjectResult(ClientMessage clientMessage, OutputStream out) {
        if (clientMessage.messageType.equals("FOUND") && clientMessage.prevRequestId == currentRequest.getRequestId()) {
            HighlightRequest highlightRequest = (HighlightRequest) currentRequest;
            highlightRequest.setSuccess(true);
            highlightRequest.setFound(true);
            highlightRequest.setClientId(clientMessage.clientId);
            highlightRequest.nextRequestId();
            sendCurrentRequest(clientMessage, out);
        }
    }
}
