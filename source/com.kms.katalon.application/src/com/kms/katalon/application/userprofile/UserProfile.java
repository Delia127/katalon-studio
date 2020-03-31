package com.kms.katalon.application.userprofile;

public class UserProfile {
    private String email;

    private boolean doneFirstTimeUseSurvey;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isDoneFirstTimeUseSurvey() {
        return doneFirstTimeUseSurvey;
    }

    public void setDoneFirstTimeUseSurvey(boolean doneFirstTimeUseSurvey) {
        this.doneFirstTimeUseSurvey = doneFirstTimeUseSurvey;
    }
}
