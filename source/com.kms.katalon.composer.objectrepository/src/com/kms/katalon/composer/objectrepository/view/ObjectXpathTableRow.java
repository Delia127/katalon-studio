package com.kms.katalon.composer.objectrepository.view;

import com.kms.katalon.entity.repository.WebElementXpathEntity;

public class ObjectXpathTableRow {

    private WebElementXpathEntity webElementXpathEntity;

    private int position;

    public ObjectXpathTableRow(WebElementXpathEntity webElementXpathEntity, int position) {
        this.webElementXpathEntity = webElementXpathEntity;
        this.position = position;
    }

    public WebElementXpathEntity getWebElementXpathEntity() {
        return webElementXpathEntity;
    }

    public int getPosition() {
        return position;
    }
}