package com.kms.katalon.objectspy.element;

import java.util.ArrayList;
import java.util.List;

public class WebFrame extends WebElement {

    private List<WebElement> children = new ArrayList<>();

    public WebFrame(String name) {
        super(name, WebElementType.FRAME);
    }

    protected WebFrame(String name, WebElementType type) {
        super(name, type);
    }

    public List<WebElement> getChildren() {
        return children;
    }

    public void setChildren(List<WebElement> children) {
        this.children = children;
    }

    public void addChild(WebElement child) {
        children.add(child);
    }

    @Override
    public boolean hasChild() {
        return children != null && children.size() > 0;
    }

    @Override
    public WebFrame softClone() {
        WebFrame clone = new WebFrame(getName());
        clone.setSelectorMethod(getSelectorMethod());
        getChildren().stream().forEach(child -> {
            child.softClone().setParent(clone);
        });
        clone.setProperties(new ArrayList<>(getProperties()));
        return clone;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof WebFrame)) {
            return false;
        }

        return super.equals(object);
    }

    public boolean isSameProperties(WebElement that) {
        if (!(that instanceof WebFrame)) {
            return false;
        }
        
        return super.isSameProperties(that);
    }
}
