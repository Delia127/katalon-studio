package com.kms.katalon.composer.project.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.impl.providers.TypeCheckedStyleCellLabelProvider;
import com.kms.katalon.composer.project.constants.StringConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.preferences.internal.PreferencesRegistry;

public class SettingHandler {

    @CanExecute
    public boolean canExecute() {
        return (ProjectController.getInstance().getCurrentProject() != null && !LauncherManager.getInstance()
                .isAnyLauncherRunning());
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, PreferencesRegistry preferencesRegistry) {
        PreferenceManager pm = preferencesRegistry.getPreferenceManager(PreferencesRegistry.PREFS_PROJECT_XP);
        PreferenceDialog dialog = new PreferenceDialog(shell, pm) {
            @Override
            protected TreeViewer createTreeViewer(Composite parent) {
                TreeViewer treeViewer = super.createTreeViewer(parent);
                treeViewer.setLabelProvider(new PreferenceLabelProvider());
                return treeViewer;
            }
        };
        dialog.create();
        dialog.getTreeViewer().setComparator(new ViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                if (e1 instanceof PreferenceNode && e2 instanceof PreferenceNode) {
                    return ((PreferenceNode) e1).getId().compareToIgnoreCase(((PreferenceNode) e2).getId());
                }
                return super.compare(viewer, e1, e2);
            }
        });
        dialog.getTreeViewer().expandToLevel(2);
        dialog.getShell().setText(StringConstants.HAND_PROJ_SETTING);
        dialog.getShell().setMinimumSize(800, 500);
        dialog.open();
    }

    private final class PreferenceLabelProvider extends TypeCheckedStyleCellLabelProvider<PreferenceNode> {
        private PreferenceLabelProvider() {
            super(0);
        }

        @Override
        protected Class<PreferenceNode> getElementType() {
            return PreferenceNode.class;
        }

        @Override
        protected Image getImage(PreferenceNode element) {
            return null;
        }

        @Override
        protected String getText(PreferenceNode element) {
            return element.getLabelText();
        }
    }
}
