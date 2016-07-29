package com.kms.katalon.entity.checkpoint;

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

}
