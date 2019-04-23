package com.kms.katalon.custom.keyword;

public class CustomKeywordConfiguration {

    private String settingId;

    private CustomKeywordSettingPage settingPage;

    public CustomKeywordSettingPage getSettingPage() {
        return settingPage;
    }

    public void setSettingPage(CustomKeywordSettingPage settingPage) {
        this.settingPage = settingPage;
    }

    public String getSettingId() {
        return settingId;
    }

    public void setSettingId(String settingId) {
        this.settingId = settingId;
    }
}
