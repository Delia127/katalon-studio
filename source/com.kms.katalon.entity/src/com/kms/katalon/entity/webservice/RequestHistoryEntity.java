package com.kms.katalon.entity.webservice;

import java.util.Date;
import java.util.UUID;

import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class RequestHistoryEntity {
    
    private String uid;

    private Date receivedResponseTime;

    private WebServiceRequestEntity request;
    
    public RequestHistoryEntity(Date receivedResponseTime, WebServiceRequestEntity request) {
        uid = UUID.randomUUID().toString();
        this.receivedResponseTime = receivedResponseTime;
        this.request = request; 
    }

    public WebServiceRequestEntity getRequest() {
        return request;
    }

    public void setRequest(WebServiceRequestEntity request) {
        this.request = request;
    }

    public Date getReceivedResponseTime() {
        return receivedResponseTime;
    }

    public void setReceivedResponseTime(Date receivedResponseTime) {
        this.receivedResponseTime = receivedResponseTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((receivedResponseTime == null) ? 0 : receivedResponseTime.hashCode());
        result = prime * result + ((request == null) ? 0 : request.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RequestHistoryEntity other = (RequestHistoryEntity) obj;
        if (receivedResponseTime == null) {
            if (other.receivedResponseTime != null)
                return false;
        } else if (receivedResponseTime.toInstant().getEpochSecond() != other.receivedResponseTime.toInstant().getEpochSecond())
            return false;
        if (request == null) {
            if (other.request != null)
                return false;
        } else if (!request.equals(other.request))
            return false;
        return true;
    }

    public String getUid() {
        return uid;
    }
}
