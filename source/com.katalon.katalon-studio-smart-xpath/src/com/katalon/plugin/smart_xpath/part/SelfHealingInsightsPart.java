package com.katalon.plugin.smart_xpath.part;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.katalon.plugin.smart_xpath.controller.AutoHealingController;
import com.katalon.plugin.smart_xpath.part.composites.BrokenTestObjectsTableComposite;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.execution.launcher.listener.LauncherEvent;
import com.kms.katalon.execution.launcher.listener.LauncherListener;
import com.kms.katalon.execution.launcher.listener.LauncherNotifiedObject;

public class SelfHealingInsightsPart implements EventHandler, LauncherListener {

    @Inject
    private UISynchronize sync;

    @Inject
    private IEventBroker eventBroker;

    protected BrokenTestObjectsTableComposite brokenTestObjectsTableComposite;

    @PostConstruct
    public void init(Composite parent) {
        createContents(parent);
        registerEventListeners();
        initialize();
    }

    protected Control createContents(Composite parent) {
        createBrokenTestObjectsTable(parent);
//        createToolbar(parent);

        return parent;
    }
    
    protected Control createToolbar(Composite parent) {
        Composite toolbarComposite = new Composite(parent, SWT.NONE);
        return toolbarComposite;
    }

    private Composite createBrokenTestObjectsTable(Composite parent) {
        brokenTestObjectsTableComposite = new BrokenTestObjectsTableComposite(parent, SWT.NONE);
        return brokenTestObjectsTableComposite;
    }
    
    protected void initialize() {
        loadBrokenTestObjects();
    }

    public void loadBrokenTestObjects() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        brokenTestObjectsTableComposite.setProject(currentProject);
        brokenTestObjectsTableComposite.setInput(AutoHealingController.readUnapprovedBrokenTestObjects());
    }

    private void registerEventListeners() {
        eventBroker.subscribe(EventConstants.JOB_UPDATE_PROGRESS, this);
        eventBroker.subscribe(EventConstants.JOB_COMPLETED, this);
        eventBroker.subscribe(EventConstants.EXPLORER_REFRESH, this);
    }

    @Focus
    public void onFocus() {
        refresh();
    }

    @Override
    public void handleLauncherEvent(LauncherEvent event, LauncherNotifiedObject notifiedObject) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleEvent(Event event) {
        refresh();
    }

    private void refresh() {
        loadBrokenTestObjects();
    }
}
