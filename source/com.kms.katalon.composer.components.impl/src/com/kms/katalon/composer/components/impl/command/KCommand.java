package com.kms.katalon.composer.components.impl.command;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KCommand implements Serializable {

    private static final long serialVersionUID = 3678851887580727121L;

    private String name;

    private List<KCommand> children;

    private String eventName;

    private Object eventData;

    public static KCommand create(String name) {
        return new KCommand(name, null);
    }

    private KCommand(String name, String eventName) {
        this.name = name;
        this.eventName = eventName;
        this.children = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<KCommand> getChildren() {
        if (children == null) {
            return Collections.emptyList();
        }
        return children;
    }

    public KCommand setChildren(List<KCommand> children) {
        this.children = children;
        return this;
    }

    public String getEventName() {
        return eventName;
    }

    public KCommand setEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public Object getEventData() {
        return eventData;
    }

    public KCommand setEventData(Object eventData) {
        this.eventData = eventData;
        return this;
    }

    public KCommand addChild(KCommand child) {
        children.add(child);
        return this;
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

}
