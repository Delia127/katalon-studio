package com.kms.katalon.composer.handlers;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.dialogs.WorkbenchPreferenceDialog;

import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;

@SuppressWarnings("restriction")
public class PreferenceDialog extends WorkbenchPreferenceDialog {

    public PreferenceDialog(Shell parentShell, PreferenceManager manager) {
        super(parentShell, manager);
    }

    @Override
    public String getSelectedNodePreference() {
        String selectedNode = super.getSelectedNodePreference();
        return StringUtils.isNotEmpty(selectedNode) ? selectedNode : PreferenceHandler.DEFAULT_PREFERENCE_PAGE_ID;
    }

    @Override
    protected void setContentAndLabelProviders(TreeViewer treeViewer) {
        super.setContentAndLabelProviders(treeViewer);
        treeViewer.setLabelProvider(new TypeCheckedStyleCellLabelProvider<IPreferenceNode>(0) {

            @Override
            protected Class<IPreferenceNode> getElementType() {
                return IPreferenceNode.class;
            }

            @Override
            protected Image getImage(IPreferenceNode element) {
                return null;
            }

            @Override
            protected String getText(IPreferenceNode element) {
                return element.getLabelText();
            }
        });
    }
}