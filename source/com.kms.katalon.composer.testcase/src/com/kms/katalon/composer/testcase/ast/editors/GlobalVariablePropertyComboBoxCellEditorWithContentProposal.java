package com.kms.katalon.composer.testcase.ast.editors;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.testcase.editors.ComboBoxCellEditorWithContentProposal;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PropertyExpressionWrapper;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.entity.global.GlobalVariableEntity;
import com.kms.katalon.execution.util.ExecutionProfileStore;

public class GlobalVariablePropertyComboBoxCellEditorWithContentProposal extends ComboBoxCellEditorWithContentProposal
        implements EventHandler {

    private Object[] items;

    private PropertyExpressionWrapper parentWrapper;

    private static String GLOBAL_VARIABLE_CLASS_ALIAS_Name = "GlobalVariable";

    public GlobalVariablePropertyComboBoxCellEditorWithContentProposal(Composite parent,
            PropertyExpressionWrapper parentWrapper, Object[] items, Object[] displayedItems, String[] toolTips) {
        super(parent, displayedItems, toolTips);
        this.items = items;
        this.parentWrapper = parentWrapper;
        registerEvent();
    }

    private void loadData() {
        List<GlobalVariableEntity> variables = ExecutionProfileStore.getInstance()
                .getSelectedProfile()
                .getGlobalVariableEntities();
        this.items = variables.toArray(new GlobalVariableEntity[variables.size()]);
    }

    private void registerEvent() {
        EventBrokerSingleton.getInstance().getEventBroker().subscribe(EventConstants.PROFILE_SELECTED_PROIFE_CHANGED,
                this);

    }

    @Override
    protected Object doGetValue() {
        String variableName = null;

        int selectedIndex = (int) super.doGetValue();
        if (selectedIndex >= 0) {
            Object selectedItem = items[selectedIndex];
            variableName = ((GlobalVariableEntity) selectedItem).getName();
            if (StringUtils.isBlank(variableName)) {
                variableName = null;
            }
        }

        return new PropertyExpressionWrapper(GLOBAL_VARIABLE_CLASS_ALIAS_Name, variableName, parentWrapper);
    }

    @Override
    protected void doSetValue(Object value) {
        if (!(value instanceof PropertyExpressionWrapper)) {
            super.doSetValue(value);
            return;
        }

        PropertyExpressionWrapper variable = (PropertyExpressionWrapper) value;
        String variableName = variable.getPropertyAsString();
        for (int index = 0; index < items.length; index++) {
            if (StringUtils.equals(getVariableName(items[index]), variableName)) {
                super.doSetValue(index);
                return;
            }
        }

        super.doSetValue(-1);
    }

    private String getVariableName(Object selectedItem) {
        if (selectedItem instanceof String) {
            return (String) selectedItem;
        }
        if (selectedItem instanceof GlobalVariableEntity) {
            return ((GlobalVariableEntity) selectedItem).getName();
        }
        return null;
    }

    @Override
    public void handleEvent(Event event) {
        if (EventConstants.PROFILE_SELECTED_PROIFE_CHANGED.equals(event.getTopic())) {
            loadData();
        }
    }
}
