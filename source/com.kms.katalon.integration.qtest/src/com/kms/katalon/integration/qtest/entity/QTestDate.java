package com.kms.katalon.integration.qtest.entity;

import java.util.Date;

public class QTestDate {
    private Date startDate;
    private Date endDate;
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
  
    public QTestDate(Date startDate, Date endDate) {
        super();
        
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
         this.startDate=startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    
    
}
