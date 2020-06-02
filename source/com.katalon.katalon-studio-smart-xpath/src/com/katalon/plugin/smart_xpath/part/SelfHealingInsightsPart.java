package com.katalon.plugin.smart_xpath.part;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.katalon.plugin.smart_xpath.constant.SmartXPathConstants;
import com.katalon.plugin.smart_xpath.controller.AutoHealingController;
import com.katalon.plugin.smart_xpath.entity.BrokenTestObject;
import com.katalon.plugin.smart_xpath.entity.BrokenTestObjects;
import com.katalon.plugin.smart_xpath.part.composites.BrokenTestObjectsTableComposite;
import com.katalon.plugin.smart_xpath.part.composites.SelfHealingToolbarComposite;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.entity.project.ProjectEntity;

public class SelfHealingInsightsPart implements EventHandler {

    @Inject
    private IEventBroker eventBroker;

    protected BrokenTestObjectsTableComposite brokenTestObjectsTableComposite;

    protected SelfHealingToolbarComposite toolbarComposite;

    @PostConstruct
    public void init(Composite parent) {
        createContents(parent);
        registerEventListeners();
        initialize();
    }

    protected Control createContents(Composite parent) {
        Composite container = createContainer(parent);

        createBrokenTestObjectsTable(container);
        createToolbar(container);

        return parent;
    }

    protected Composite createContainer(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        return container;
    }

    private Composite createBrokenTestObjectsTable(Composite parent) {
        brokenTestObjectsTableComposite = new BrokenTestObjectsTableComposite(parent, SWT.NONE);
        return brokenTestObjectsTableComposite;
    }

    protected Composite createToolbar(Composite parent) {
        toolbarComposite = new SelfHealingToolbarComposite(parent, SWT.NONE);

        toolbarComposite.addApproveListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                AutoHealingController.setEventBroker(eventBroker);
                Set<BrokenTestObject> approvedBrokenTestObjects = brokenTestObjectsTableComposite.getApprovedTestObjects();
                int numApprovedTestObjects = approvedBrokenTestObjects.size();
                AutoHealingController.autoHealBrokenTestObjects(Display.getCurrent().getActiveShell(),
                        approvedBrokenTestObjects);

                ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
                String pathToApprovedJson = currentProject.getFolderLocation()
                        + SmartXPathConstants.WAITING_FOR_APPROVAL_FILE_SUFFIX;
                Set<BrokenTestObject> unapprovedBrokenTestObjects = brokenTestObjectsTableComposite
                        .getUnapprovedTestObjects();
                BrokenTestObjects brokenTestObjects = new BrokenTestObjects();
                brokenTestObjects.setBrokenTestObjects(unapprovedBrokenTestObjects);
                AutoHealingController.writeBrokenTestObjects(brokenTestObjects, pathToApprovedJson);

                refresh();
                toolbarComposite.notifyRecoverSucceeded(numApprovedTestObjects);
            }
        });

        toolbarComposite.addDiscardListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
                String pathToApprovedJson = currentProject.getFolderLocation()
                        + SmartXPathConstants.WAITING_FOR_APPROVAL_FILE_SUFFIX;
                BrokenTestObjects brokenTestObjects = new BrokenTestObjects();
                AutoHealingController.writeBrokenTestObjects(brokenTestObjects, pathToApprovedJson);

                refresh();
            }
        });

        return toolbarComposite;
    }

    private void registerEventListeners() {
        eventBroker.subscribe(EventConstants.SEFL_HEALING_INSIGHTS_REFRESH, this);
        eventBroker.subscribe(EventConstants.JOB_UPDATE_PROGRESS, this);
        eventBroker.subscribe(EventConstants.JOB_COMPLETED, this);
    }

    protected void initialize() {
        loadBrokenTestObjects();
    }

    public void loadBrokenTestObjects() {
        ProjectEntity currentProject = ProjectController.getInstance().getCurrentProject();
        brokenTestObjectsTableComposite.setProject(currentProject);
        brokenTestObjectsTableComposite.setInput(AutoHealingController.readUnapprovedBrokenTestObjects());
    }

    @Focus
    public void onFocus() {
        refresh();
    }

    @Override
    public void handleEvent(Event event) {
        refresh();
    }

    private void refresh() {
        loadBrokenTestObjects();
        toolbarComposite.clearStatusMessage();
    }
}
