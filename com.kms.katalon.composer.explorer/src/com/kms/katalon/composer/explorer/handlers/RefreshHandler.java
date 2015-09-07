package com.kms.katalon.composer.explorer.handlers;

import javax.inject.Named;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.ui.handlers.HandlerUtil;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.SelectionServiceSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;

public class RefreshHandler implements IHandler {
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
		String activePartId = HandlerUtil.getActivePartId(event);
		if (activePartId != null && activePartId.equals(IdConstants.EXPLORER_PART_ID)) {
			execute(SelectionServiceSingleton.getInstance().getSelectionService(), EventBrokerSingleton.getInstance()
					.getEventBroker());
		}
		return null;
	}

	@SuppressWarnings("restriction")
	@Execute
	private void execute(ESelectionService selectionService, IEventBroker eventBroker) {
		if (selectionService != null) {
			if (selectionService.getSelection(IdConstants.EXPLORER_PART_ID) != null) {
				for (Object selectedItem : (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID)) {
					if (selectedItem instanceof ITreeEntity) {
						try {
							eventBroker.send(EventConstants.EXPLORER_REFRESH_TREE_ENTITY, selectedItem);
							eventBroker.post(EventConstants.EXPLORER_REFRESH_SELECTED_ITEM, selectedItem);
						} catch (Exception e) {
							LoggerSingleton.getInstance().getLogger().error(e);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean isEnabled() {
		ESelectionService selectionService = SelectionServiceSingleton.getInstance().getSelectionService();
		Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
		return canExecute(selectedObjects);
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
	}

	@CanExecute
	public boolean canExecute(@Named(IServiceConstants.ACTIVE_SELECTION) @Optional Object[] selectedObjects) {
		if (selectedObjects == null || selectedObjects.length == 0) {
			return false;
		}
		return true;
	}
}
