package com.kms.katalon.composer.webui.recorder.dialog.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.kms.katalon.composer.testcase.model.InputValueEditorProvider;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionParamMapping;
import com.kms.katalon.composer.webui.recorder.action.HTMLActionParamValueType;

public class HTMLActionParamLabelProvider extends LabelProvider implements ITableLabelProvider {
    private static final int COLUMN_PARAM_NAME_INDEX = 0;

    private static final int COLUMN_PARAM_TYPE_INDEX = 1;

    private static final int COLUMN_VALUE_TYPE_INDEX = 2;

    private static final int COLUMN_VALUE_INDEX      = 3;

    private HTMLActionParamMapping getParamMapping(Object element) {
        return ((HTMLActionParamMapping) element);
    }

    private InputValueEditorProvider getValueType(Object element) {
        return getParamMapping(element).getActionData().getEditorProvider();
    }

    private HTMLActionParamValueType getParamValue(Object element) {
        return getParamMapping(element).getActionData();
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (!(element instanceof HTMLActionParamMapping) || columnIndex < 0 || columnIndex > COLUMN_VALUE_INDEX) {
            return "";
        }
        HTMLActionParamMapping actionParamMapping = (HTMLActionParamMapping) element;
        switch (columnIndex) {
            case COLUMN_PARAM_NAME_INDEX:
                return actionParamMapping.getActionParam().getName();
            case COLUMN_PARAM_TYPE_INDEX:
                return actionParamMapping.getActionParam().getClazz().getSimpleName();
            case COLUMN_VALUE_TYPE_INDEX:
                return getValueType(element).getName();
            case COLUMN_VALUE_INDEX: {
                return getParamValue(element).getValueToDisplay();
            }
        }

        return "";
    }

}
