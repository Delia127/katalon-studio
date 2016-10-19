package com.kms.katalon.composer.testcase.components;

import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.widgets.Event;

import com.kms.katalon.composer.components.impl.control.CTreeViewer;

public class KeywordTreeViewerToolTipSupport extends ColumnViewerToolTipSupport{
    private TreeViewerKeywordTooltip tooltipHandler;
    
    protected KeywordTreeViewerToolTipSupport(TreeViewer viewer, int style, boolean manualActivation) {
        super(viewer, style, manualActivation);
        if (viewer instanceof CTreeViewer) {
            tooltipHandler = new CTreeViewerKeywordTooltip((CTreeViewer)viewer);
        } else {
            tooltipHandler = new TreeViewerKeywordTooltip(viewer);
        }
    }
    
    @Override
    protected boolean shouldCreateToolTip(Event event) {
        if (tooltipHandler.isProcessShowTooltip(event)) {
            return false;
        }
        return super.shouldCreateToolTip(event);
    }
    
    public static void enableFor(TreeViewer cv) {
        new KeywordTreeViewerToolTipSupport(cv, ToolTip.NO_RECREATE, false);
    }
}
