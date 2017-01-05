package com.kms.katalon.composer.objectrepository.view;

import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class ObjectPropertyTableRow {

    private WebElementPropertyEntity webElementPropertyEntity;

    private int position;

    public ObjectPropertyTableRow(WebElementPropertyEntity webElementPropertyEntity, int position) {
        this.webElementPropertyEntity = webElementPropertyEntity;
        this.position = position;
    }

    public WebElementPropertyEntity getWebElementPropertyEntity() {
        return webElementPropertyEntity;
    }

    public int getPosition() {
        return position;
    }
}
