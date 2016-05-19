package com.kms.katalon.composer.webui.recorder.dialog.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.util.TreeEntityUtil;
import com.kms.katalon.composer.testcase.model.InputValueEditorProvider;
import com.kms.katalon.composer.testcase.util.AstInputValueTypeOptionsProvider;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionParamMapping;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionParamValueType;
import com.kms.katalon.composer.webui.recorder.type.HTMLActionPropertyValueType;
import com.kms.katalon.composer.webui.recorder.util.HTMLActionUtil;
import com.kms.katalon.composer.webui.recorder.util.HTMLInputValueTypeProvider;

public class HTMLActionValueTypeColumnSupport extends EditingSupport {

    private List<InputValueEditorProvider> availableEditorProviders;
    
    private HTMLActionPropertyValueType additionalEditorProvider;
    
    private InputValueEditorProvider assignableType;

    public HTMLActionValueTypeColumnSupport(ColumnViewer viewer, HTMLActionPropertyValueType additionalValueType) {
        super(viewer);
        this.additionalEditorProvider = additionalValueType;
    }

    private HTMLActionParamMapping getParamMapping(Object element) {
        return ((HTMLActionParamMapping) element);
    }

    private HTMLActionParamValueType getParamValue(Object element) {
        return getParamMapping(element).getActionData();
    }

    @Override
    protected void setValue(Object element, Object value) {
        InputValueEditorProvider newEditorProvider = newEditorProviderFromValueAsIndex(value);
        if (newEditorProvider == null) {
            return;
        }
        
        HTMLActionParamValueType paramValue = getParamValue(element);
        InputValueEditorProvider oldEditorProvider = paramValue.getEditorProvider();
        if (oldEditorProvider != null && newEditorProvider.equals(oldEditorProvider)) {
            return;
        }
        paramValue.setEditorProvider(newEditorProvider);
        paramValue.setValue(newEditorProvider.newValue());
        getViewer().refresh();
    }

    private InputValueEditorProvider newEditorProviderFromValueAsIndex(Object value) {
        if (!(value instanceof Integer)) {
            return null;
        }

        int selectionIndex = (int) value;
        if (selectionIndex < 0 || selectionIndex > availableEditorProviders.size()) {
            return null;
        }

        return availableEditorProviders.get(selectionIndex);
    }

    @Override
    protected CellEditor getCellEditor(Object element) {
        collectAvaiableEditorProviders();
        
        return new ComboBoxCellEditor((Composite) getViewer().getControl(), getValueTypeNames());
    }

    private void collectAvaiableEditorProviders() {
        availableEditorProviders = new ArrayList<>();
        availableEditorProviders.addAll(Arrays.asList(AstInputValueTypeOptionsProvider.getInputValueTypeOptions(assignableType.getName())));
        if (additionalEditorProvider != null) {
            availableEditorProviders.add(additionalEditorProvider);
        }
    }

    private String[] getValueTypeNames() {
        String[] readableValueTypeNames = new String[availableEditorProviders.size()];
        for (int i = 0; i < availableEditorProviders.size(); i++) {
            readableValueTypeNames[i] = TreeEntityUtil.getReadableKeywordName(availableEditorProviders.get(i).getName());
        }
        return readableValueTypeNames;
    }

    @Override
    protected Object getValue(Object element) {
        InputValueEditorProvider paramValueType = getParamValue(element).getEditorProvider();
        if (paramValueType == null) {
            return HTMLActionUtil.DF_SELECTED_INDEX_IF_NULL;
        }
        for (int index = 0; index < availableEditorProviders.size(); index++) {
            if (paramValueType.equals(availableEditorProviders.get(index))) {
                return index;
            }
        }
        return HTMLActionUtil.DF_SELECTED_INDEX_IF_NULL;
    }

    @Override
    protected boolean canEdit(Object element) {
        if (!(element instanceof HTMLActionParamMapping)) {
            assignableType = null;
            return false;
        }

        assignableType = AstInputValueTypeOptionsProvider.getAssignableValueType(getParamMapping(element).getActionParam()
                .getClazz());

        return assignableType != null;
    }
}
