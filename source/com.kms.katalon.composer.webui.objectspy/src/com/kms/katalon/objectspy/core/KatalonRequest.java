package com.kms.katalon.objectspy.core;

public abstract class KatalonRequest {
    private String request;

    private Object data;

    private boolean success;

    private int requestId;

    private static int id = 0;
    
    public KatalonRequest(String request, Object data) {
        this.request = request;
        this.data = data;
        success = false;
        nextRequestId();
    }
    
    public KatalonRequest(String request) {
        this.request = request;
        success = false;
        nextRequestId();
    }
    
    protected void setRequestData(Object data) {
        this.data = data;
    }

    public synchronized void nextRequestId() {
        requestId = ++id;
    }

    public boolean isFailed() {
        return success == false;
    }

    public void setSuccess(boolean isSuccess) {
        success = isSuccess;
    }

    public String getRequestType() {
        return request;
    }

    public int getRequestId() {
        return requestId;
    }

    public Object getData() {
        return data;
    }

    public abstract String processFailed();
}
