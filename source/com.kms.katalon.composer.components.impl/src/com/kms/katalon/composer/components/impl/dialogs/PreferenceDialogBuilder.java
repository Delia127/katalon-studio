package com.kms.katalon.composer.components.impl.dialogs;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.preferences.internal.PreferencesRegistry;

public class PreferenceDialogBuilder {
    private Point size;
    private String selectedNode;
    private String dialogName;
    private int levelToExpand;
    private Shell shell;

    private PreferenceDialogBuilder() {
        setExpandLevel(1);
    }

    public static PreferenceDialogBuilder create() {
        PreferenceDialogBuilder instance = new PreferenceDialogBuilder();
        return instance;
    }
    
    public PreferenceDialogBuilder addDialogName(String dialogName) {
        setDialogName(dialogName);
        return this;
    }
    
    public PreferenceDialogBuilder addSelectedNode(String selectedNode) {
        setSelectedNode(selectedNode);
        return this;
    }
    
    public PreferenceDialogBuilder addSize(Point size) {
        setSize(size);
        return this;
    }
    
    public PreferenceDialogBuilder addLevelToExpandAll(int level) {
        setExpandLevel(level);
        return this;
    }
    
    public PreferenceDialogBuilder addShell(Shell shell) {
        setShell(shell);
        return this;
    }

    public PreferenceDialog build() {
        IEclipseContext eclipseContext = (IEclipseContext) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getService(IEclipseContext.class);
        PreferencesRegistry preferencesRegistry = ContextInjectionFactory.make(PreferencesRegistry.class,
                eclipseContext);        
        PreferenceManager pm = preferencesRegistry.getPreferenceManager(PreferencesRegistry.PREFS_PROJECT_XP);
        
        PreferenceDialog dialog = new PreferenceDialog(getShell(), pm);
        dialog.setSelectedNode(getSelectedNode());
        dialog.create();
        dialog.getTreeViewer().setComparator(new ViewerComparator());
        dialog.getTreeViewer().expandToLevel(getLevelToExpandAll());
        dialog.getShell().setText(getDialogName());
        dialog.getShell().setSize(getSize());

        return dialog;
    }

    private Point getSize() {
        if (size == null) {
            size = new Point(400, 400);
        }
        return size;
    }

    private void setSize(Point size) {
        this.size = size;
    }

    private String getSelectedNode() {
        if (selectedNode == null) {
            selectedNode = "";
        }
        return selectedNode;
    }

    private void setSelectedNode(String selectedNode) {
        this.selectedNode = selectedNode;
    }

    private String getDialogName() {
        if (dialogName == null) {
            dialogName = "";
        }
        return dialogName;
    }

    private void setDialogName(String dialogName) {
        this.dialogName = dialogName;
    }

    private int getLevelToExpandAll() {
        return levelToExpand;
    }

    private void setExpandLevel(int expandLevel) {
        if (expandLevel <= 0) {
            throw new IllegalArgumentException("Level to expand all must be greater than zero.");
        }
        this.levelToExpand = expandLevel;
    }

    private Shell getShell() {
        if (shell == null) {
            shell = Display.getCurrent().getActiveShell();
        }
        return shell;
    }

    private void setShell(Shell shell) {
        this.shell = shell;
    }
}
