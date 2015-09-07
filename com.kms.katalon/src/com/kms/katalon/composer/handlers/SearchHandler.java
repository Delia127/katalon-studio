package com.kms.katalon.composer.handlers;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.search.internal.ui.OpenSearchDialogAction;
import org.eclipse.ui.PlatformUI;

import com.kms.katalon.composer.components.application.ApplicationSingleton;
import com.kms.katalon.composer.components.services.ModelServiceSingleton;
import com.kms.katalon.composer.components.services.PartServiceSingleton;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;

@SuppressWarnings("restriction")
public class SearchHandler implements IHandler {
	
	public static void openSearchView() {		
		
		EModelService modelService = ModelServiceSingleton.getInstance().getModelService();
		MApplication application = ApplicationSingleton.getInstance().getApplication();
		EPartService partService = PartServiceSingleton.getInstance().getPartService();

		List<MPerspectiveStack> psList = modelService.findElements(application, null, MPerspectiveStack.class, null);
		
		MPartStack consolePartStack = (MPartStack) modelService
				.find(IdConstants.CONSOLE_PARTSTACK_ID, psList.get(0).getSelectedElement());
		
		// set console partStack visible		
		consolePartStack.getTags().remove("Minimized");
		consolePartStack.setVisible(true);
		if (!consolePartStack.isToBeRendered()) {
			consolePartStack.setToBeRendered(true);
		}

		//set current page of console partStack is search viewer
		MPlaceholder searchViewPart = (MPlaceholder) modelService
				.find(IdConstants.IDE_SEARCH_PART_ID, consolePartStack);
		
		if (!consolePartStack.getChildren().contains(searchViewPart)) {
			partService.createPart(IdConstants.IDE_SEARCH_PART_ID);
			consolePartStack.getChildren().add(searchViewPart);
		}
		
		//maybe searchViewPart has been closed.
		if (!searchViewPart.isToBeRendered()) {
			searchViewPart.setToBeRendered(true);
		}
		
		//always set it visible
		searchViewPart.setVisible(true);	

		consolePartStack.setSelectedElement(searchViewPart);
		partService.activate((MPart) searchViewPart.getRef(), true);
	}

	@Execute
	public static void execute() {
		if (ProjectController.getInstance().getCurrentProject() == null) return;
		openSearchView();

		new OpenSearchDialogAction(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				"com.kms.katalon.composer.search.page").run();
	}

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		execute();
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// TODO Auto-generated method stub

	}
}
