package com.kms.katalon.composer.webservice.parts.tree;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.webservice.util.WebServiceUtil;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;
import com.kms.katalon.entity.webservice.RequestHistoryEntity;

public class RequestHistoryTreeItem implements IRequestHistoryItem {

    private RequestHistoryEntity requestHistoryEntity;
    private RequestDateTreeItem parent;

    public RequestHistoryTreeItem(RequestHistoryEntity requestHistoryEntity, RequestDateTreeItem parent) {
        this.requestHistoryEntity = requestHistoryEntity;
        this.parent = parent;
    }

    public RequestHistoryEntity getRequestHistoryEntity() {
        return requestHistoryEntity;
    }

    @Override
    public String getName() {
        return requestHistoryEntity.getRequest().getRestUrl();
    }

    @Override
    public Image getImage() {
        WebServiceRequestEntity request = requestHistoryEntity.getRequest();
        String serviceType = request.getServiceType();
        if (WebServiceRequestEntity.RESTFUL.equals(serviceType)) {
            return WebServiceUtil.getRequestMethodImage(serviceType, request.getRestRequestMethod());
        }
        return WebServiceUtil.getRequestMethodImage(serviceType, request.getSoapRequestMethod());
    }

    @Override
    public List<IRequestHistoryItem> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((requestHistoryEntity == null) ? 0 : requestHistoryEntity.hashCode());
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
        RequestHistoryTreeItem other = (RequestHistoryTreeItem) obj;
        if (requestHistoryEntity == null) {
            if (other.requestHistoryEntity != null)
                return false;
        } else if (!requestHistoryEntity.equals(other.requestHistoryEntity))
            return false;
        return true;
    }
    
    @Override
    public IRequestHistoryItem getParent() {
        return parent;
    }

}
