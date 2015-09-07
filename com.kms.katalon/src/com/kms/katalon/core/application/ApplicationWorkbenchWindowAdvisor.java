package com.kms.katalon.core.application;

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


@SuppressWarnings("restriction")
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    @Override
    public boolean preWindowShellClose() {
        IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getService(IHandlerService.class);
        try {
            boolean confirmed = (boolean) handlerService.executeCommand(IdConstants.QUIT_COMMAND_ID, null);
            return confirmed;
        } catch (Exception e) {
            LoggerSingleton.getInstance().getLogger().error(e);
            return false;
        }
    }
    
	/**
	 * After window created, the right part stack docks on outline trim stack.
	 * However, the selected part of the right part stack is not set then this method will
	 * do that.
	 */
	@Override
    public void postWindowCreate() {
		EPartService partService = (EPartService) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getService(EPartService.class);

		// active the first tab of the rightPartStack.
		MPartStack rightPartStack = (MPartStack) ModelServiceSingleton.getInstance().getModelService()
				.find(IdConstants.OUTLINE_PARTSTACK_ID, ApplicationSingleton.getInstance().getApplication());

		if (rightPartStack != null && rightPartStack.getChildren() != null && !rightPartStack.getChildren().isEmpty()) {
			MPart globalPart = (MPart) rightPartStack.getChildren().get(0);
			rightPartStack.setSelectedElement(globalPart);
			partService.activate(globalPart);
		}

		
    }
}
