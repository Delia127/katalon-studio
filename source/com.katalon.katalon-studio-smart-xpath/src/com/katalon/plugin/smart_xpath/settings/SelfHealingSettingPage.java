package com.katalon.plugin.smart_xpath.settings;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolItem;

import com.kms.katalon.composer.components.dialogs.PreferencePageWithHelp;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.execution.setting.ExecutionDefaultSettingStore;
import com.kms.katalon.execution.webui.setting.WebUiExecutionSettingStore;
import com.kms.katalon.util.collections.Pair;

public class SelfHealingSettingPage extends PreferencePageWithHelp {
	
    private ExecutionDefaultSettingStore defaultSettingStore;
    
    private SelfHealingSettingStore selfHealingSettingStore;

    private Composite container;

	public SelfHealingSettingPage() {
        defaultSettingStore = ExecutionDefaultSettingStore.getStore();
        selfHealingSettingStore = new SelfHealingSettingStore(ProjectController.getInstance().getCurrentProject());
	}

	@Override
	protected Control createContents(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.verticalSpacing = 10;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        container.setLayout(layout);

		return container;
	}
}
