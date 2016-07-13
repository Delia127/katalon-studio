package com.kms.katalon.composer.testdata.parts;

import java.util.ArrayList;
import java.util.List;

public class InternalDataRow {
    private boolean lastRow;

    private List<InternalDataCell> cells;
    
    /* package */ InternalDataRow() {
        this(false);
    }
    
    private InternalDataRow(boolean lastRow) {
        setLastRow(lastRow);
        getCells().add(InternalDataCell.newLastCell());
    }

    public boolean isLastRow() {
        return lastRow;
    }

    public void setLastRow(boolean lastRow) {
        this.lastRow = lastRow;
    }

    public List<InternalDataCell> getCells() {
        if (cells == null) {
            cells = new ArrayList<>();
        }
        return cells;
    }

    public void setCells(List<InternalDataCell> cells) {
        this.cells = cells;
    }
    
    /* package */ static InternalDataRow newLastRow() {
        return new InternalDataRow(true);
    }
}
