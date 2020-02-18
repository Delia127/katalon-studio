package com.kms.katalon.composer.webservice.viewmodel;

import com.kms.katalon.entity.repository.WebElementPropertyEntity;
import com.kms.katalon.entity.repository.WebServiceRequestEntity;

public class HttpBodyEditorCompositeViewModel {
    private WebServiceRequestEntity model;

    public void setModel(WebServiceRequestEntity model) {
        this.model = model;
    }

    public WebServiceRequestEntity getModel() {
        return model;
    }

    public void updateContentTypeByEditorViewModel(HttpBodyEditorViewModel httpBodyEditorViewModel) {
        if (httpBodyEditorViewModel.isContentTypeUpdated()
                && httpBodyEditorViewModel.doesUserAllowAutoUpdateContentType()) {
            WebElementPropertyEntity propertyEntity = findContentTypeProperty();
            String newContentType = httpBodyEditorViewModel.getContentType();
            if (propertyEntity != null) {
                propertyEntity.setValue(newContentType);
            } else {
                propertyEntity = new WebElementPropertyEntity("Content-Type", newContentType);
                model.getHttpHeaderProperties().add(0, propertyEntity);
            }
            httpBodyEditorViewModel.setContentTypeUpdated(false);
        }
    }

    private WebElementPropertyEntity findContentTypeProperty() {
        WebElementPropertyEntity propertyEntity = model.getHttpHeaderProperties()
                .stream()
                .filter(h -> "Content-Type".equals(h.getName()))
                .findFirst()
                .orElse(null);
        return propertyEntity;
    }
}
