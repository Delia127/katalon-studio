package com.kms.katalon.composer.execution.debug;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.internal.debug.ui.JavaDebugHover;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;

@SuppressWarnings("restriction")
public class DebugTextHover extends JavaDebugHover {

    @Override
    public Object getHoverInfo2(ITextViewer textViewer, IRegion hoverRegion) {
        Object parentValue = super.getHoverInfo2(textViewer, hoverRegion);
        return (parentValue != null) ? parentValue : getVariableValue(textViewer, hoverRegion);
    }

    private Object getVariableValue(ITextViewer textViewer, IRegion hoverRegion) {
        if (getFrame() == null) {
            return null;
        }
        try {
            String codeAssist = textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());
            return getFrame().findVariable(codeAssist);
        } catch (BadLocationException | DebugException ignored) {
            // Cannot find variable
        }
        return null;
    }
}
