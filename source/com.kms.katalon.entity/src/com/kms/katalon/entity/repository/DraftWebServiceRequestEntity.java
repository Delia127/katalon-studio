package com.kms.katalon.entity.repository;

import java.util.UUID;

public class DraftWebServiceRequestEntity extends WebServiceRequestEntity {

    private static final long serialVersionUID = -8350983392772794263L;
    
    private String draftUid;

    public String getDraftUid() {
        if (draftUid == null) {
            draftUid = UUID.randomUUID().toString();
        }
        return draftUid;
    }

    public void setDraftUid(String draftUid) {
        this.draftUid = draftUid;
    }

   public String getNameAsUrl() {
       if (DraftWebServiceRequestEntity.RESTFUL.equals(getServiceType())) {
           return getRestUrl();
       }
       return getWsdlAddress();
   }
}
