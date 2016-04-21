package com.kms.katalon.composer.webui.recorder.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.parser.GroovyWrapperParser;
import com.kms.katalon.composer.testcase.model.InputValueEditorProvider;
import com.kms.katalon.composer.webui.recorder.util.HTMLActionUtil;
import com.kms.katalon.groovy.util.GroovyStringUtil;
import com.kms.katalon.objectspy.element.HTMLElement;

public class HTMLActionPropertyValueType implements InputValueEditorProvider {

    private HTMLElement htmlElement;

    private List<String> htmlPropertyNames;

    public HTMLActionPropertyValueType(HTMLElement htmlElement) {
        this.htmlElement = htmlElement;
        htmlPropertyNames = new ArrayList<String>();

        if (htmlElement != null && htmlElement.getAttributes() != null && !htmlElement.getAttributes().isEmpty()) {
            for (Entry<String, String> entry : htmlElement.getAttributes().entrySet()) {
                htmlPropertyNames.add(entry.getKey());
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((htmlElement == null) ? 0 : htmlElement.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        HTMLActionPropertyValueType other = (HTMLActionPropertyValueType) obj;
        if (htmlElement == null) {
            if (other.htmlElement != null) {
                return false;
            }
        } else if (!htmlElement.equals(other.htmlElement)) {
            return false;
        }
        return true;
    }

    @Override
    public String getName() {
        return "HTML Property";
    }

    @Override
    public boolean isEditable(Object astObject) {
        return astObject instanceof String;
    }

    @Override
    public CellEditor getCellEditorForValue(Composite parent, Object astObject) {
        return new ComboBoxCellEditor(
            parent, htmlPropertyNames.toArray(new String[htmlPropertyNames.size()]));
    }

    @Override
    public Object newValue() {
        return HTMLActionUtil.DF_SELECTED_INDEX_IF_NULL;
    }

    @Override
    public Object getValueToEdit(Object selectedIndexAsObject) {
        return getSelectedIndex(selectedIndexAsObject);
    }

    @Override
    public String getValueToDisplay(Object selectedIndexAsObject) {
        return getPropertyName(selectedIndexAsObject);
    }

    @Override
    public ExpressionWrapper toASTNodeWrapper(Object selectedIndexAsObject) {
        return GroovyWrapperParser.parseGroovyScriptAndGetFirstExpression(GroovyStringUtil.toGroovyStringFormat(
                getPropertyName(selectedIndexAsObject)));
    }

    private String getPropertyName(Object selectedIndexAsObject) {
        return htmlPropertyNames.get(getSelectedIndex(selectedIndexAsObject));
    }

    private int getSelectedIndex(Object selectedIndexAsObject) {
        return isValidSeletion(selectedIndexAsObject) ? (int) selectedIndexAsObject
                : HTMLActionUtil.DF_SELECTED_INDEX_IF_NULL;
    }

    private boolean isValidSeletion(Object selectedIndexAsObject) {
        return (selectedIndexAsObject instanceof Integer && ((int) selectedIndexAsObject < htmlPropertyNames.size()
                && (int) selectedIndexAsObject >= 0));
    }

}
