package com.kms.katalon.composer.testsuite.support;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testsuite.parts.TestSuitePart;
import com.kms.katalon.entity.link.VariableLink;
import com.kms.katalon.entity.link.VariableLink.VariableType;

public class VariableTypeEditingSupport extends EditingSupport {
    private static final List<String> variableTypes = VariableType.getValueStrings();
    private TestSuitePart mpart;

    public VariableTypeEditingSupport(ColumnViewer viewer, TestSuitePart mpart) {
        super(viewer);
        this.mpart = mpart;
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        return new ComboBoxCellEditor((Composite) this.getViewer().getControl(), variableTypes.toArray(new String[0]));
    }

    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    @Override
    protected Object getValue(Object element) {
        if (element != null && element instanceof VariableLink) {
            VariableLink link = (VariableLink) element;
            return variableTypes.indexOf(link.getType().getDisplayName());
        }
        return 0;
    }

    @Override
    protected void setValue(Object element, Object value) {
        if (element != null && element instanceof VariableLink && value != null && value instanceof Integer) {
            VariableLink link = (VariableLink) element;
            int chosenIndex = (int) value;
            VariableType variableType = VariableType.fromValue(variableTypes.get(chosenIndex));
            if (variableType != link.getType()) {
            	link.setTestDataLinkId(StringUtils.EMPTY);
                link.setType(variableType);                
                getViewer().update(element, null);
                mpart.setDirty(true);
            }
        }
    }

}
