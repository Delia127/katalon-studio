package com.kms.katalon.composer.artifact.plugin;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.katalon.platform.api.extension.ToolItemWithMenuDescription;
import com.katalon.platform.api.model.ProjectEntity;
import com.kms.katalon.composer.artifact.constant.StringConstants;
import com.kms.katalon.composer.artifact.core.util.PlatformUtil;
import com.kms.katalon.composer.artifact.handler.ExportTestArtifactHandler;
import com.kms.katalon.composer.artifact.handler.ImportTestArtifactHandler;

public class PluginMenuItemDescription implements ToolItemWithMenuDescription {

    private Menu menu;
    
    @Override
    public String toolItemId() {
        return "com.kms.katalon.composer.artifact.plugin.PluginMenuItemDescription";
    }

    @Override
    public String name() {
        return "Katashare";
    }

    @Override
    public String iconUrl() {
        return "platform:/plugin/" + StringConstants.PLUGIN_BUNDLE_ID + "/icons/import_test_artifacts_32x24.png";
    }

    @Override
    public Menu getMenu(Control parent) {
        menu = new Menu(parent);
        
        MenuItem exportTestArtifactMenuItem = new MenuItem(menu, SWT.PUSH);
        exportTestArtifactMenuItem.setText("Export Test Artifacts");
        exportTestArtifactMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ProjectEntity project = PlatformUtil.getCurrentProject();
                if (project != null) {
                    ExportTestArtifactHandler handler = new ExportTestArtifactHandler(e.widget.getDisplay().getActiveShell());
                    handler.execute();
                } else {
                    MessageDialog.openInformation(e.widget.getDisplay().getActiveShell(), StringConstants.INFO,
                            StringConstants.MSG_OPEN_A_PROJECT);
                }
            }
        });
        
        MenuItem importTestArtifactMenuItem = new MenuItem(menu, SWT.PUSH);
        importTestArtifactMenuItem.setText("Import Test Artifacts");
        importTestArtifactMenuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ProjectEntity project = PlatformUtil.getCurrentProject();
                if (project != null) {
                    ImportTestArtifactHandler handler = new ImportTestArtifactHandler(e.widget.getDisplay().getActiveShell());
                    handler.execute();
                } else {
                    MessageDialog.openInformation(e.widget.getDisplay().getActiveShell(), StringConstants.INFO,
                            StringConstants.MSG_OPEN_A_PROJECT);
                }
            }
        });
        return menu;
    }

}
