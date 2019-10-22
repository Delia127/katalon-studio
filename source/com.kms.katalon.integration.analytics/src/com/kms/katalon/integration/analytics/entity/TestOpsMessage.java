package com.kms.katalon.integration.analytics.entity;

public class TestOpsMessage {
    
    private String timestamp;
    
    private String status;
    
    private String error;
    
    private String message;
    
    private String error_description;
    
    private String trace;
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getTrace() {
        return trace;
    }
    
    public void setTrace(String trace) {
        this.trace = trace;
    }
    
    public String getError_description() {
        return error_description;
    }
    
    public void setError_description(String error_description) {
        this.error_description = error_description;
    }
}
