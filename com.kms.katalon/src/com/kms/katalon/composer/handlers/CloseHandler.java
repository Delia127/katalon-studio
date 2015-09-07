package com.kms.katalon.composer.handlers;

import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;

import com.kms.katalon.composer.components.services.PartServiceSingleton;
import com.kms.katalon.composer.parts.MultipleTabsCompositePart;

@SuppressWarnings("restriction")
public class CloseHandler implements IHandler {

	@CanExecute
	public static boolean canExecute() {
		EPartService partService = PartServiceSingleton.getInstance().getPartService();
		MPart part = partService.getActivePart();
		
		if (getCompositeParentPart(part, partService) != null) {
			return true;
		} else if (part != null) {
			return true;
		}
		return false;
	}

	private static MPart getCompositeParentPart(MPart part, EPartService partService) {
		for (MPart dirtyPart : partService.getParts()) {
			if (dirtyPart.getObject() instanceof MultipleTabsCompositePart) {
				MultipleTabsCompositePart compositePart = (MultipleTabsCompositePart) dirtyPart.getObject();
				List<MPart> childrenParts = compositePart.getChildParts();
				if (childrenParts != null && compositePart.getChildParts().contains(part)) {
					return dirtyPart;
				}
			}
		}
		return null;
	}

	@Execute
	public static void execute() {
		EPartService partService = PartServiceSingleton.getInstance().getPartService();
		MPart part = partService.getActivePart();
		
		MPart parentCompositePart = getCompositeParentPart(part, partService);
		if (parentCompositePart != null) {
			if (partService.savePart(parentCompositePart, true)) {
				partService.hidePart(parentCompositePart);
			}
		} else {
			if (part.getObject() instanceof CompatibilityEditor) {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.closeEditor(((CompatibilityEditor) part.getObject()).getEditor(), true);
			} else {
				if (partService.savePart(part, true)) {
					partService.hidePart(part);
				}
			}
		}
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
		return canExecute();
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