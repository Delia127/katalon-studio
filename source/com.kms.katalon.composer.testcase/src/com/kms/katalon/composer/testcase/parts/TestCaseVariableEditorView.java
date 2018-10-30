package com.kms.katalon.composer.testcase.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.components.impl.constants.TextContentType;
import com.kms.katalon.composer.components.impl.editors.MirrorEditor;
import com.kms.katalon.composer.components.impl.handler.DocumentReadyHandler;
import com.kms.katalon.composer.components.util.ColorUtil;

public class TestCaseVariableEditorView {

    Composite composite;

    MirrorEditor mirrorEditor;

    boolean contentChanged = false;

    IVariablePart variablePart;

    public TestCaseVariableEditorView(IVariablePart variablePart) {
        this.variablePart = variablePart;
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

}
