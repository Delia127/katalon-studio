package com.kms.katalon.composer.testcase.parts;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.constants.TextContentType;
import com.kms.katalon.composer.components.impl.editors.MirrorEditor;
import com.kms.katalon.composer.components.impl.handler.DocumentReadyHandler;
import com.kms.katalon.composer.components.util.ColorUtil;
import com.kms.katalon.composer.parts.CPart;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.entity.variable.VariableEntity;
import com.kms.katalon.entity.variable.VariableEntityWrapper;

public class TestCaseVariableEditorView extends CPart{

    Composite composite;

    MirrorEditor mirrorEditor;

    boolean contentChanged = false;

    IVariablePart variablePart;

    String contentScript = StringUtils.EMPTY;
    
    Composite parentTestCaseCompositePart;

    public TestCaseVariableEditorView(IVariablePart variablePart, Composite parent) {        
        this.variablePart = variablePart;
        parentTestCaseCompositePart = parent;
        createComponents(parent);
    }

    public void createComponents(Composite variableEditorPartComposite) {

        composite = new Composite(variableEditorPartComposite, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
        composite.setBackground(ColorUtil.getExtraLightGrayBackgroundColor());

        mirrorEditor = new MirrorEditor(composite, SWT.NONE);
        mirrorEditor.setEditable(true);
        mirrorEditor.registerDocumentHandler(new DocumentReadyHandler() {

            @Override
            public void onDocumentReady() {
                mirrorEditor.changeMode(TextContentType.XML.getText());
            }
        });

        mirrorEditor.addListener(SWT.Modify, event -> {
            if (contentChanged == false) {
                contentChanged = true;
            } else {
                setDirty(true);
            }
        });

    }
    

    private void setDirty(boolean b) {
        variablePart.setDirty(b);
    }
    
    public void setScriptContentFrom(VariableEntityWrapper entityWrapper) throws Exception {
        String incomingContentScript = getScriptContentFromVariableEntityWrapper(entityWrapper);
        if (!contentScript.equals(incomingContentScript)) {
            mirrorEditor.setText(incomingContentScript);
            if (!contentScript.equals(StringUtils.EMPTY))
                contentChanged = true;
            contentScript = incomingContentScript;
        }
    }
    
    public String getScriptContentFromVariableEntityWrapper(VariableEntityWrapper entityWrapper) throws Exception{
        String content = StringUtils.EMPTY;
        if (entityWrapper != null && entityWrapper.getVariables() != null && entityWrapper.getVariables().size() != 0) {
            // Arrays.asList returns unmodifiable list and therefore
            // operations (add, delete, modify, etc) are not supported
            List<VariableEntity> incomingVariablesList = entityWrapper.getVariables();
            VariableEntityWrapper variableEntityWrapper = new VariableEntityWrapper();
            variableEntityWrapper.setVariables(incomingVariablesList);
            content = GlobalVariableController.toXmlString(variableEntityWrapper);
        }
        return content;
    }

    public String getScriptContent() {
       return mirrorEditor.getText();
    }

}
