package com.kms.katalon.objectspy.element;

import java.util.List;

import com.kms.katalon.entity.repository.WebElementPropertyEntity;

public class WebPage extends WebFrame {

    public WebPage(String name) {
        super(name, WebElementType.PAGE);
    }

    @Override
    public String getTag() {
        return null;
    }

    @Override
    public void setTag(String tag) {
        // Do nothing
    }

    @Override
    public void setProperties(List<WebElementPropertyEntity> properties) {
        // Do nothing
    }

    @Override
    public void addProperty(WebElementPropertyEntity property) {
        // Do nothing
    }

    @Override
    public boolean hasProperty() {
        return false;
    }

    @Override
    public WebPage softClone() {
        WebPage clone = new WebPage(getName());
        getChildren().stream().forEach(child -> {
            child.softClone().setParent(clone);
        });
        return clone;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WebPage)) {
            return false;
        }

        return super.equals(object);
    }

}
