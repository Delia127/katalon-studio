package com.kms.katalon.core.application;

import java.util.List;
import java.util.Map;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MArea;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.handlers.IHandlerService;

import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants.IPluginPreferenceConstants;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    private IWorkbenchWindowConfigurer fConfigurer;

    private MApplication application;

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
        fConfigurer = configurer;
        application = ApplicationSingleton.getInstance().getApplication();
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }

    @Override
    public void preWindowOpen() {
        fConfigurer.setShowProgressIndicator(true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void postWindowClose() {
        if (PlatformUI.getPreferenceStore()
                .getBoolean(IPluginPreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION)) {

            // Clear Memento state
            Map<String, String> appPersistedState = application.getPersistedState();
            if (appPersistedState != null) {
                appPersistedState.remove("memento");
            }

            // Re-shape Editor Area
            List<MUIElement> sharedElements = application.getChildren().get(0).getSharedElements();
            for (MUIElement element : sharedElements) {
                if (IdConstants.SHARE_AREA_ID.equals(element.getElementId())) {
                    ((MArea) element).getChildren().clear();
                    MPartStack contentPartStack = MBasicFactory.INSTANCE.createPartStack();
                    contentPartStack.setElementId(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID);
                    contentPartStack.setParent((MElementContainer<MUIElement>) element);
                    contentPartStack.setContainerData("100");
                    contentPartStack.getTags().add("NoAutoCollapse");
                    ((MArea) element).setSelectedElement(contentPartStack);
                    break;
                }
            }
        }
    }

    @Override
    public boolean preWindowShellClose() {
        IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getService(IHandlerService.class);
        try {
            boolean confirmed = (boolean) handlerService.executeCommand(IdConstants.QUIT_COMMAND_ID, null);
            return confirmed;
        } catch (Exception e) {
            LoggerSingleton.logError(e);
            return false;
        }
    }

    /**
     * After window created, the right part stack docks on outline trim stack. However, the selected part of the right
     * part stack is not set then this method will do that.
     */
    @Override
    public void postWindowCreate() {
        EPartService partService = (EPartService) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getService(EPartService.class);

        // active the first tab of the rightPartStack.
        MPartStack rightPartStack = (MPartStack) ModelServiceSingleton.getInstance().getModelService()
                .find(IdConstants.OUTLINE_PARTSTACK_ID, application);

        if (rightPartStack != null && rightPartStack.getChildren() != null && !rightPartStack.getChildren().isEmpty()) {
            MPart globalPart = (MPart) rightPartStack.getChildren().get(0);
            rightPartStack.setSelectedElement(globalPart);
            partService.activate(globalPart);
        }

    }
}
