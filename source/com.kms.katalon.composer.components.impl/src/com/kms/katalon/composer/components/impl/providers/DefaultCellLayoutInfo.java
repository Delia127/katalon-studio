package com.kms.katalon.composer.components.impl.providers;

public class DefaultCellLayoutInfo implements CellLayoutInfo {

    private static final int DF_CELL_MARGIN = 0;

    @Override
    public int getLeftMargin() {
        return DF_CELL_MARGIN;
    }

    @Override
    public int getRightMargin() {
        return DF_CELL_MARGIN;
    }

    @Override
    public int getSpace() {
        return DF_CELL_MARGIN;
    }
}
