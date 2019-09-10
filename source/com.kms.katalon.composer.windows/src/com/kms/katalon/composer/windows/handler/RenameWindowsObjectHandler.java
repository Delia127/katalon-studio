package com.kms.katalon.composer.windows.handler;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.event.Event;

import com.kms.katalon.composer.components.dialogs.CWizardDialog;
import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.impl.event.EventServiceAdapter;
import com.kms.katalon.composer.components.impl.tree.WindowsElementTreeEntity;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.wizard.RenameWizard;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.FolderController;
import com.kms.katalon.controller.WindowsElementController;
import com.kms.katalon.entity.repository.WindowsElementEntity;

public class RenameWindowsObjectHandler {
    @Inject
    private IEventBroker eventBroker;

    @Named(IServiceConstants.ACTIVE_SHELL)
    private Shell parentShell;

    @PostConstruct
    public void registerEventHandler() {
        eventBroker.subscribe(EventConstants.EXPLORER_RENAME_SELECTED_ITEM, new EventServiceAdapter() {
            @Override
            public void handleEvent(Event event) {
                Object object = getObject(event);
                if (object instanceof WindowsElementTreeEntity) {
                    execute((WindowsElementTreeEntity) object);
                }
            }
        });
    }

    private void execute(WindowsElementTreeEntity windowsElementTree) {
        try {
            if (!(windowsElementTree.getObject() instanceof WindowsElementEntity)) {
                return;
            }

            rename(windowsElementTree);

            eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, windowsElementTree.getParent());

            eventBroker.post(EventConstants.EXPLORER_SET_SELECTED_ITEM, windowsElementTree);
        } catch (Exception e) {
            MultiStatusErrorDialog.showErrorDialog(e, "Unable to rename Windows Object", e.getMessage());
            LoggerSingleton.logError(e);
        }
    }

    private void rename(WindowsElementTreeEntity windowsElementTree) throws Exception {
        WindowsElementEntity windowsElementEntity = (WindowsElementEntity) windowsElementTree.getObject();
        RenameWizard renameWizard = new RenameWizard(windowsElementTree, getSibblingNames(windowsElementEntity));
        if (new CWizardDialog(parentShell, renameWizard).open() != Window.OK) {
            return;
        }

        String newName = renameWizard.getNewNameValue();
        if (windowsElementEntity.getName().equals(newName)) {
            return;
        }

        String oldIdForDisplay = windowsElementEntity.getIdForDisplay();
        WindowsElementController.getInstance().renameWindowsElementEntity(newName, windowsElementEntity);

        eventBroker.post(EventConstants.EXPLORER_RENAMED_SELECTED_ITEM,
                new Object[] { oldIdForDisplay, windowsElementEntity.getIdForDisplay() });
    }

    private List<String> getSibblingNames(WindowsElementEntity testRunEntity) throws Exception {
        List<String> sibblingNames = FolderController.getInstance().getChildNames(testRunEntity.getParentFolder());
        sibblingNames.remove(testRunEntity.getName());
        return sibblingNames;
    }
}
