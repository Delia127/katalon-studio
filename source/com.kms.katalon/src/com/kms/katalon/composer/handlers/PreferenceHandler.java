package com.kms.katalon.composer.handlers;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import com.kms.katalon.composer.components.impl.handler.AbstractHandler;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.preferences.internal.PreferencesRegistry;

@SuppressWarnings("restriction")
public class PreferenceHandler extends AbstractHandler {

    public static final String DEFAULT_PREFERENCE_PAGE_ID = "com.kms.katalon.composer.preferences.GeneralPreferencePage";

    private static final String[] UNNECESSARY_PREF_NODES = new String[] {};

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public void execute() {
        doExecute();
    }

    public static void doExecute() {
        doExecute(null);
    }

    public static int doExecute(String initPreferencePageId) {
        PreferenceManager applicationPref = ContextInjectionFactory
                .make(PreferencesRegistry.class, getWorkbenchContext())
                .getPreferenceManager(PreferencesRegistry.PREFS_PAGE_XP);

        IPreferenceNode[] applicationNodes = applicationPref.getRootSubNodes();

        PreferenceManager workbenchPref = getActiveWorkbench().getPreferenceManager();

        removeUnnecessaryNodes(workbenchPref);

        addApplicationNodesToWorkbenchPreferenceManger(workbenchPref, applicationNodes);

        int result = openPreferenceDialog(workbenchPref, initPreferencePageId);

        removeApplicationNodesFromWorkbenchPreferenceManager(workbenchPref, applicationNodes);

        return result;
    }

    @Inject
    @Optional
    public void execute(@UIEventTopic(EventConstants.KATALON_PREFERENCES) Object eventData) {
        if (eventData instanceof String) {
            doExecute((String) eventData);
        } else {
            doExecute();
        }
    }

    private static void removeUnnecessaryNodes(PreferenceManager workbenchPref) {
        for (String unnecessaryPreferenceId : UNNECESSARY_PREF_NODES) {
            workbenchPref.remove(unnecessaryPreferenceId);
        }
    }

    private static void addApplicationNodesToWorkbenchPreferenceManger(PreferenceManager workbenchPref,
            IPreferenceNode[] applicationNodes) {
        for (IPreferenceNode subRoot : applicationNodes) {
            if (workbenchPref.find(subRoot.getId()) != null) {
                continue;
            }
            workbenchPref.addToRoot(subRoot);
        }
    }

    private static void removeApplicationNodesFromWorkbenchPreferenceManager(PreferenceManager workbenchPref,
            IPreferenceNode[] applicationNodes) {
        for (IPreferenceNode subRoot : applicationNodes) {
            workbenchPref.remove(subRoot);
        }
    }

    private static int openPreferenceDialog(PreferenceManager workbenchPref, String initPreferencePagePath) {
        PreferenceDialog dialog = new PreferenceDialog(getActiveWorkbenchWindow().getShell(), workbenchPref);
        dialog.create();
        TreeViewer dialogTreeViewer = dialog.getTreeViewer();
        dialogTreeViewer.setComparator(new PreferencePageViewerComparator());
        dialog.setMinimumPageSize(500, 500);

        if (DEFAULT_PREFERENCE_PAGE_ID.equals(dialog.getSelectedNodePreference())) {
            dialogTreeViewer.expandToLevel(dialog.getPreferenceManager().find(DEFAULT_PREFERENCE_PAGE_ID), 1);
        }
        if (StringUtils.isNotEmpty(initPreferencePagePath)) {
            IPreferenceNode preference = dialog.getPreferenceManager().find(initPreferencePagePath);
            if (preference != null) {
                dialogTreeViewer.setSelection(new StructuredSelection(preference));
            }
        }
        dialogTreeViewer.getTree().forceFocus();
        return dialog.open();
    }
}
