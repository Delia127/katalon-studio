package com.kms.katalon.objectspy.element;

import java.util.ArrayList;
import java.util.List;

import com.kms.katalon.objectspy.element.WebElement.WebElementType;

public class ConflictWebElementWrapper {

    private boolean isConflicted = false;

    private WebElement originalWebElement;

    private List<ConflictWebElementWrapper> children = new ArrayList<>();

    private ConflictWebElementWrapper parent;

    public ConflictWebElementWrapper(WebElement originalWebElement, boolean isConflicted) {
        this.originalWebElement = originalWebElement;
        this.isConflicted = isConflicted;
    }

    public void setIsConflicted(boolean isConflicted) {
        this.isConflicted = isConflicted;
    }

    public boolean isConflicted() {
        return isConflicted;
    }

    public boolean hasChild() {
        return children != null && children.size() > 0;
    }

    public WebElement getOriginalWebElement() {
        return originalWebElement;
    }

    public void setOriginalWebElement(WebElement originalWebElement) {
        this.originalWebElement = originalWebElement;
    }

    public List<ConflictWebElementWrapper> getChildren() {
        return children;
    }

    public ConflictWebElementWrapper getParent() {
        return parent;
    }

    public void setParent(ConflictWebElementWrapper parent) {
        this.parent = parent;
    }

    public void setChildren(List<ConflictWebElementWrapper> children) {
        if (getType() != WebElementType.ELEMENT) {
            this.children = children;
        }
    }

    public WebElementType getType() {
        return originalWebElement.getType();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ConflictWebElementWrapper)) {
            return false;
        }
        return originalWebElement.equals(((ConflictWebElementWrapper) object).getOriginalWebElement());
    }

    public ConflictWebElementWrapper softClone() {
        ConflictWebElementWrapper clone = new ConflictWebElementWrapper(getOriginalWebElement(), isConflicted());
        clone.setChildren(getChildren());
        clone.setParent(getParent());
        return clone;
    }

    public int hashCode() {
        return originalWebElement.hashCode();
    }

}
