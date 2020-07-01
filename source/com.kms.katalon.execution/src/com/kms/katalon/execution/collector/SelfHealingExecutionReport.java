package com.kms.katalon.execution.collector;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.kms.katalon.core.webui.common.internal.BrokenTestObject;

public class SelfHealingExecutionReport {
    private boolean isEnabled;

    private boolean isTriggered;

    private Set<BrokenTestObject> brokenTestObjects;

    public SelfHealingExecutionReport(boolean isEnabled, boolean isTriggered, Set<BrokenTestObject> brokenTestObjects) {
        super();
        this.isEnabled = isEnabled;
        this.isTriggered = isTriggered;
        this.brokenTestObjects = brokenTestObjects;
    }

    public String getHealingInfo() {
        if (!hasHealedAnyObject()) {
            return null;
        }
        
        List<String> healingRecords = new ArrayList<String>();
        brokenTestObjects.forEach(brokenTestObject -> {
            String healingRecord = MessageFormat.format("'{'\"broken\": \"{0}\", \"recover\": \"{1}\"'}'",
                    brokenTestObject.getBrokenLocatorMethod(), brokenTestObject.getRecoveryMethod());
            healingRecords.add(healingRecord);
        });

        String healingInfo = MessageFormat.format("[{0}]", healingRecords.stream().collect(Collectors.joining(", ")));
        return healingInfo;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isTriggered() {
        return isTriggered;
    }

    public void setTriggered(boolean isTriggered) {
        this.isTriggered = isTriggered;
    }

    public Set<BrokenTestObject> getBrokenTestObjects() {
        return brokenTestObjects;
    }

    public void setBrokenTestObjects(Set<BrokenTestObject> brokenTestObjects) {
        this.brokenTestObjects = brokenTestObjects;
    }

    public boolean hasHealedAnyObject() {
        return isTriggered && brokenTestObjects != null && !brokenTestObjects.isEmpty();
    }
}
