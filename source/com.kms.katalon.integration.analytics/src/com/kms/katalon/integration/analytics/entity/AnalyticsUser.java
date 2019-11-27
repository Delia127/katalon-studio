package com.kms.katalon.integration.analytics.entity;

import java.util.Date;

public class AnalyticsUser {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private Date trialExpirationDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getTrialExpirationDate() {
        return trialExpirationDate;
    }

    public void setTrialExpirationDate(Date trialExpirationDate) {
        this.trialExpirationDate = trialExpirationDate;
    }

}
