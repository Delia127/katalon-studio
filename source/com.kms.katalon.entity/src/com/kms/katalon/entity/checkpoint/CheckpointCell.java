package com.kms.katalon.entity.checkpoint;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.kms.katalon.entity.file.ClonableObject;

/**
 * Checkpoint Cell
 */
public class CheckpointCell extends ClonableObject {

    private static final long serialVersionUID = -1694838499619995640L;

    private Object value;

    private boolean checked;

    public CheckpointCell(Object value) {
        this(value, false);
    }

    public CheckpointCell(Object value, boolean checked) {
        this.value = value;
        this.checked = checked;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
    @Override
    public CheckpointCell clone() {
        return (CheckpointCell) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CheckpointCell)) {
            return false;
        }
        CheckpointCell that = (CheckpointCell) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder().append(this.getValue(), that.getValue())
                .append(this.isChecked(), that.isChecked());
        return equalsBuilder.isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(7, 31).append(this.getValue()).append(this.isChecked()).toHashCode();
    }
}
