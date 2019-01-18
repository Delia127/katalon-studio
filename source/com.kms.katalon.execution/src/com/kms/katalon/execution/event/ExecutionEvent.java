package com.kms.katalon.execution.event;

public class ExecutionEvent {
    private String topic;
    
    private Object data;

    public ExecutionEvent() {
        
    }
    
    public ExecutionEvent(String topic, Object data) {
        this.topic = topic;
        this.data = data;
    }
    
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    
    
}
