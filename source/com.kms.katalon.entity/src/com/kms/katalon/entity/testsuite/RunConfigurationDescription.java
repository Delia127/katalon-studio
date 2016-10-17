package com.kms.katalon.entity.testsuite;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.kms.katalon.entity.file.ClonableObject;

public class RunConfigurationDescription extends ClonableObject {
    private static final long serialVersionUID = -1684965350446072500L;

    private String groupName;

    private String runConfigurationId;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getRunConfigurationId() {
        return runConfigurationId;
    }

    public void setRunConfigurationId(String runConfigurationId) {
        this.runConfigurationId = runConfigurationId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
        result = prime * result + ((runConfigurationId == null) ? 0 : runConfigurationId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || getClass() != obj.getClass()) {
            return false;
        }

        RunConfigurationDescription other = (RunConfigurationDescription) obj;
        return new EqualsBuilder().append(getGroupName(), other.getGroupName())
                .append(getRunConfigurationId(), other.getRunConfigurationId())
                .isEquals();
    }

    public static RunConfigurationDescription from(String groupName, String runConfigurationId) {
        RunConfigurationDescription configuration = new RunConfigurationDescription();
        configuration.setGroupName(groupName);
        configuration.setRunConfigurationId(runConfigurationId);
        return configuration;
    }
}
