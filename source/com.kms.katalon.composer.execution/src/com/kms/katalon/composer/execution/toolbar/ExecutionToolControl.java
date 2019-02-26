package com.kms.katalon.composer.execution.toolbar;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.control.CDropdownBox;
import com.kms.katalon.composer.execution.constants.ComposerExecutionMessageConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.controller.GlobalVariableController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.exception.ControllerException;
import com.kms.katalon.entity.global.ExecutionProfileEntity;

public class ExecutionToolControl implements EventHandler {

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EModelService modelService;

    private CDropdownBox dropdownBox;

    private MApplication app;

    private List<ExecutionProfileEntity> profiles;

    /**
     * Concretes a tool-bar for execution group.
     */
    @PostConstruct
    private void createWidget(Composite parent, MApplication app) {
        this.app = app;
        dropdownBox = new CDropdownBox(parent, SWT.BORDER, ImageConstants.IMG_PROFILE);

        dropdownBox.setToolTipText(ComposerExecutionMessageConstants.TOOLTIP_CONTROL_EXECUTION_PROFILE);

        GridData gridData = new GridData(SWT.RIGHT, SWT.FILL, false, true);
        dropdownBox.setLayoutData(gridData);

        setVisible(false);

        registerEventListeners();
    }

    private void registerEventListeners() {
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, this);
        eventBroker.subscribe(EventConstants.PROJECT_CLOSED, this);
        eventBroker.subscribe(EventConstants.EXECUTION_PROFILE_CREATED, this);
        eventBroker.subscribe(EventConstants.EXECUTION_PROFILE_RENAMED, this);
        eventBroker.subscribe(EventConstants.EXECUTION_PROFILE_DELETED, this);

        dropdownBox.addListener(SWT.Selection, new Listener() {

            @Override
            public void handleEvent(org.eclipse.swt.widgets.Event event) {
                fireSelectedProfileChange();
            }
        });
    }

    private void updateComboboxInput() {
        try {
            profiles = GlobalVariableController.getInstance()
                    .getAllGlobalVariableCollections(ProjectController.getInstance().getCurrentProject());
            String[] profileNames = profiles.stream()
                    .map(p -> p.getName())
                    .collect(Collectors.toList())
                    .toArray(new String[0]);
            dropdownBox.setItems(profileNames);

            fireSelectedProfileChange();
        } catch (ControllerException e) {
            setVisible(true);
        }
    }

    private void fireSelectedProfileChange() {
        if (dropdownBox.getSelectionIndex() < 0 || profiles.isEmpty()) {
            return;
        }
        ExecutionProfileEntity selectedProfile = profiles.get(dropdownBox.getSelectionIndex());
        eventBroker.post(EventConstants.PROFILE_SELECTED_PROIFE_CHANGED, selectedProfile);
    }

    @Override
    public void handleEvent(Event event) {
        switch (event.getTopic()) {
            case EventConstants.PROJECT_OPENED:
                updateComboboxInput();
                setVisible(true);
                break;

            case EventConstants.PROJECT_CLOSED:
                setVisible(false);
                break;
            case EventConstants.EXECUTION_PROFILE_CREATED: {
                updateComboboxInput();
            }
            case EventConstants.EXECUTION_PROFILE_RENAMED: {
                ExecutionProfileEntity selectedProfile = profiles.get(dropdownBox.getSelectionIndex());
                updateComboboxInput();
                dropdownBox.setSelectionIndex(profiles.indexOf(selectedProfile));
                break;
            }
            case EventConstants.EXECUTION_PROFILE_DELETED: {
                ExecutionProfileEntity selectedProfile = profiles.get(dropdownBox.getSelectionIndex());

                updateComboboxInput();

                Object object = event.getProperty(EventConstants.EVENT_DATA_PROPERTY_NAME);
                if (!(object instanceof Object)) {
                    return;
                }
                ExecutionProfileEntity deletedProfile = (ExecutionProfileEntity) object;
                if (deletedProfile.equals(selectedProfile)) {
                    selectedProfile = profiles.get(0);
                }
                dropdownBox.setSelectionIndex(profiles.indexOf(selectedProfile));
                break;
            }
            default:
                break;
        }
    }

    private void setVisible(boolean visible) {
        dropdownBox.setVisible(visible);
        ((GridData) dropdownBox.getLayoutData()).exclude = !visible;
        dropdownBox.pack();

        modelService.find("com.kms.katalon.composer.toolbar.execution", app).setToBeRendered(visible);

        dropdownBox.getParent().layout(true, true);
    }
}
