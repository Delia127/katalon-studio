package com.kms.katalon.composer.webservice.handlers;

import java.util.List;

import com.kms.katalon.entity.webservice.RequestHistoryEntity;

public interface IRequestHistoryListener {
    void addHistoryRequest(RequestHistoryEntity addedEntity);
    
    void removeHistoryRequests(List<RequestHistoryEntity> removedEntities);

    void resetInput();
}
