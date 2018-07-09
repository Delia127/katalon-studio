package com.kms.katalon.objectspy.element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HTMLFrameElement extends HTMLElement {
    protected List<HTMLElement> childElements;

    public HTMLFrameElement() {
        super();
        childElements = new ArrayList<HTMLElement>();
    }

    public HTMLFrameElement(String name, String elementType, Map<String, String> attributes,
            HTMLFrameElement parentElement, List<HTMLElement> childElements) {
        super(name, elementType, attributes, parentElement);
        this.childElements = childElements;
    }

    public List<HTMLElement> getChildElements() {
        return childElements;
    }

    public void setChildElements(List<HTMLElement> childElements) {
        this.childElements = childElements;
    }

    public boolean contains(HTMLElement element) {
        boolean result = false;
        for (HTMLElement childElement : getChildElements()) {
            if (childElement.equals(element)) {
                result = true;
                break;
            }
            if (childElement instanceof HTMLFrameElement) {
                boolean isChildContains = ((HTMLFrameElement) childElement).contains(element);
                if (isChildContains) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HTMLFrameElement)) {
            return false;
        }
        return super.equals(object);
    }

    @Override
    public HTMLElement softClone() {
        List<HTMLElement> childrenClone = new ArrayList<>();
        Iterator<HTMLElement> iterator = getChildElements().iterator();
        while (iterator.hasNext()) {
            childrenClone.add(iterator.next().softClone());
        }
        HTMLFrameElement frame = new HTMLFrameElement(getName(), getType(), getAttributes(), null, childrenClone);
        childrenClone.forEach(child -> {
            child.setParentElement(frame);
        });
        return frame;
    }
}
