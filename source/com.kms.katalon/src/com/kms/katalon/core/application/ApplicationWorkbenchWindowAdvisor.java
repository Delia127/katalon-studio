package com.kms.katalon.core.application;

import java.util.List;
import java.util.Map;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.ide.EditorAreaDropAdapter;

import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.PreferenceConstants;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
    private static final String[] ECLIPSE_DEFAULT_ACTION_SET_IDS = {
            "org.codehaus.groovy.eclipse.ui.groovyElementCreation", "org.eclipse.search.searchActionSet",
            "org.eclipse.ui.edit.text.actionSet.annotationNavigation",
            "org.eclipse.ui.edit.text.actionSet.navigation" };

    private IWorkbenchWindowConfigurer fConfigurer;

    private MApplication application;

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
        fConfigurer = configurer;
        application = ApplicationSingleton.getInstance().getApplication();
        ModelServiceSingleton.getInstance().getModelService();
    }

    @Override
    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }

    @Override
    public void preWindowOpen() {
        fConfigurer.setShowProgressIndicator(true);
        fConfigurer.configureEditorAreaDropListener(new EditorAreaDropAdapter(fConfigurer.getWindow()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void postWindowClose() {
        if (PlatformUI.getPreferenceStore().getBoolean(PreferenceConstants.GENERAL_AUTO_RESTORE_PREVIOUS_SESSION)) {

            // Clear Memento state
            Map<String, String> appPersistedState = application.getPersistedState();
            if (appPersistedState != null) {
                appPersistedState.remove("memento");
            }

//            // Re-shape Editor Area
//            List<MUIElement> sharedElements = application.getChildren().get(0).getSharedElements();
//            for (MUIElement element : sharedElements) {
//                if (IdConstants.SHARE_AREA_ID.equals(element.getElementId())) {
//                    ((MArea) element).getChildren().clear();
//                    MPartStack contentPartStack = MBasicFactory.INSTANCE.createPartStack();
//                    contentPartStack.setElementId(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID);
//                    contentPartStack.setParent((MElementContainer<MUIElement>) element);
//                    contentPartStack.setContainerData("100");
//                    contentPartStack.getTags().add("NoAutoCollapse");
//                    ((MArea) element).setSelectedElement(contentPartStack);
//                    break;
//                }
//            }
        }
       
        try {
            ActivationInfoCollector.postEndSession();
            ActivationInfoCollector.releaseLicense();
        } catch (Exception e) {
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public boolean preWindowShellClose() {
        IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow()
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
        hideUnwantedActionSets();

        EPartService partService = (EPartService) PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow()
                .getService(EPartService.class);

        // active the first tab of the rightPartStack.
        selectAndActivateChildPart(partService, IdConstants.OUTLINE_PARTSTACK_ID, 0);

        // active the first tab of the leftOutlinePartStack
        selectAndActivateChildPart(partService, IdConstants.COMPOSER_PARTSTACK_LEFT_OUTLINE_ID, 0);
    }

    private void selectAndActivateChildPart(EPartService partService, String partStackId, int selectIndex) {
        MPartStack partStack = (MPartStack) ModelServiceSingleton.getInstance().getModelService().find(partStackId,
                application);
        if (partStack == null) {
            return;
        }

        // This is a non-null list
        List<MStackElement> leftOutlineChildren = partStack.getChildren();
        if (leftOutlineChildren.isEmpty()) {
            return;
        }

        MStackElement part = leftOutlineChildren.get(selectIndex);
        partStack.setSelectedElement(part);
        partService.activate((MPart) part);
    }

    private void hideUnwantedActionSets() {
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (activePage != null) {
            for (String actionSet : ECLIPSE_DEFAULT_ACTION_SET_IDS) {
                activePage.hideActionSet(actionSet);
            }
        }
    }
}
