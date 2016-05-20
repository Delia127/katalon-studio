package com.kms.katalon.composer.testcase.providers;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import com.kms.katalon.composer.components.util.ColorUtil;

public abstract class UneditableTableCellLabelProvider extends StyledCellLabelProvider {
    public abstract String getText(Object element);
    
    @Override
    public void update(ViewerCell cell) {
        cell.setText(getText(cell.getElement()));
        cell.setBackground(ColorUtil.getUnEditableTableCellBackgroundColor());
        super.update(cell);
    }
}