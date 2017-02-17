package com.kms.katalon.composer.handlers;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.internal.dialogs.WorkbenchPreferenceDialog;

import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;
import com.kms.katalon.preferences.internal.PreferencesRegistry;

@SuppressWarnings("restriction")
public class PreferenceHandler extends AbstractHandler {

    private static final String DEFAULT_PREFERENCE_PAGE_ID = "com.kms.katalon.composer.preferences.GeneralPreferencePage";

    private static final String[] UNNECESSARY_PREF_NODES = new String[] {};

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute() {
        PreferenceManager applicationPref = ContextInjectionFactory.make(PreferencesRegistry.class,
                getWorkbenchContext()).getPreferenceManager(PreferencesRegistry.PREFS_PAGE_XP);

        IPreferenceNode[] applicationNodes = applicationPref.getRootSubNodes();

        PreferenceManager workbenchPref = getActiveWorkbench().getPreferenceManager();

        removeUnnecessaryNodes(workbenchPref);

        addApplicationNodesToWorkbenchPreferenceManger(workbenchPref, applicationNodes);

        openPreferenceDialog(workbenchPref);

        removeApplicationNodesFromWorkbenchPreferenceManager(workbenchPref, applicationNodes);
    }

    private void removeUnnecessaryNodes(PreferenceManager workbenchPref) {
        for (String unnecessaryPreferenceId : UNNECESSARY_PREF_NODES) {
            workbenchPref.remove(unnecessaryPreferenceId);
        }
    }

    private void addApplicationNodesToWorkbenchPreferenceManger(PreferenceManager workbenchPref,
            IPreferenceNode[] applicationNodes) {
        for (IPreferenceNode subRoot : applicationNodes) {
            if (workbenchPref.find(subRoot.getId()) != null) {
                continue;
            }
            workbenchPref.addToRoot(subRoot);
        }
    }

    private void removeApplicationNodesFromWorkbenchPreferenceManager(PreferenceManager workbenchPref,
            IPreferenceNode[] applicationNodes) {
        for (IPreferenceNode subRoot : applicationNodes) {
            workbenchPref.remove(subRoot);
        }
    }

    private void openPreferenceDialog(PreferenceManager workbenchPref) {
        PreferenceDialog dialog = new PreferenceDialog(getActiveWorkbenchWindow().getShell(), workbenchPref);
        dialog.create();
        TreeViewer dialogTreeViewer = dialog.getTreeViewer();
        dialogTreeViewer.setComparator(new PreferencePageViewerComparator());
        dialog.setMinimumPageSize(500, 500);

        if (DEFAULT_PREFERENCE_PAGE_ID.equals(dialog.getSelectedNodePreference())) {
            dialogTreeViewer.expandToLevel(dialog.getPreferenceManager().find(DEFAULT_PREFERENCE_PAGE_ID), 1);
        }
        dialogTreeViewer.getTree().forceFocus();
        dialog.open();
    }

    /**
     * Custom View Comparator is used to sort preference pages and keep General page on top and Katalon page at the
     * bottom of others.
     */
    private class PreferencePageViewerComparator extends ViewerComparator {

        private static final String KATALON_PAGE_NAME = "Katalon";

        private static final String GENERAL_PAGE_NAME = "General";

        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            int cat1 = category(e1);
            int cat2 = category(e2);

            if (cat1 != cat2) {
                return cat1 - cat2;
            }

            String name1 = getLabel(viewer, e1);
            String name2 = getLabel(viewer, e2);

            // Keep General preference on top of the list
            if (GENERAL_PAGE_NAME.equals(name1)) {
                return -1;
            }
            if (GENERAL_PAGE_NAME.equals(name2)) {
                return 1;
            }

            // Keep Katalon preference at the bottom of the list
            if (KATALON_PAGE_NAME.equals(name1)) {
                return 1;
            }
            if (KATALON_PAGE_NAME.equals(name2)) {
                return -1;
            }

            // use the comparator to compare the strings
            return getComparator().compare(name1, name2);
        }

        private String getLabel(Viewer viewer, Object node) {
            if (node instanceof IPreferenceNode) {
                return ((IPreferenceNode) node).getLabelText();
            }
            return StringUtils.EMPTY;
        }

    }

    private class PreferenceDialog extends WorkbenchPreferenceDialog {

        public PreferenceDialog(Shell parentShell, PreferenceManager manager) {
            super(parentShell, manager);
        }

        @Override
        protected String getSelectedNodePreference() {
            String selectedNode = super.getSelectedNodePreference();
            return StringUtils.isNotEmpty(selectedNode) ? selectedNode : DEFAULT_PREFERENCE_PAGE_ID;
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
}
