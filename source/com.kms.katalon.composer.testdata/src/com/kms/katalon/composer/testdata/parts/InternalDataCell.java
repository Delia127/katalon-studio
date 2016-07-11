package com.kms.katalon.composer.testdata.parts;

import org.apache.commons.lang.StringUtils;

public class InternalDataCell {

    private boolean lastCell;

    private String value;

    /* package */ InternalDataCell(String value) {
        setValue(value);
        setLastCell(false);
    }

    private InternalDataCell(String value, boolean lastCell) {
        setValue(value);
        setLastCell(lastCell);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isLastCell() {
        return lastCell;
    }

    public void setLastCell(boolean lastCell) {
        this.lastCell = lastCell;
    }

    public static InternalDataCell newLastCell() {
        return new InternalDataCell(StringUtils.EMPTY, true);
    }

    public static InternalDataCell newEmptyCell() {
        return new InternalDataCell(StringUtils.EMPTY);
    }
}
