package com.kms.katalon.composer.explorer.handlers;

import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.SelectionServiceSingleton;
import com.kms.katalon.composer.components.tree.ITreeEntity;
import com.kms.katalon.composer.explorer.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;

public class DeleteHandler implements IHandler {

	@CanExecute
	public static boolean canExecute(ESelectionService selectionService) {
		if (selectionService.getSelection(IdConstants.EXPLORER_PART_ID) instanceof Object[]) {
			Object[] selectedObjects = (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID);
			for (Object entity : selectedObjects) {
				if (entity instanceof ITreeEntity) {
					try {
						return ((ITreeEntity) entity).isRemoveable();
					} catch (Exception e) {
						LoggerSingleton.logError(e);
					}
				}
			}
			return true;
		}
		return false;
	}

	@Execute
	public static void execute(ESelectionService selectionService, IEventBroker eventBroker) {
		if (selectionService != null && selectionService.getSelection(IdConstants.EXPLORER_PART_ID) != null) {
			delete(eventBroker, (Object[]) selectionService.getSelection(IdConstants.EXPLORER_PART_ID), true);
		}
	}

	public static void delete(IEventBroker eventBroker, Object[] objects, boolean needConfirm) {
		try {
			if (needConfirm) {
				boolean canDelete = false;
				String message = "";
				if (objects.length == 1 && objects[0] instanceof ITreeEntity) {
					message = MessageFormat.format(StringConstants.HAND_DELETE_CONFIRM_MSG, 
							((ITreeEntity) objects[0]).getTypeName() + " '" + ((ITreeEntity) objects[0]).getText() + "'");
				} else if (objects.length > 1) {
					message = MessageFormat.format(StringConstants.HAND_MULTI_DELETE_CONFIRM_MSG, objects.length);
				}
				canDelete = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(), 
						StringConstants.HAND_DELETE_TITLE, message);
				if (canDelete) {
					delete(eventBroker, objects);
				}
			} else {
				delete(eventBroker, objects);
			}
		} catch (Exception e) {
			LoggerSingleton.logError(e);
		}
	}

	private static void delete(IEventBroker eventBroker, Object[] objects) {
		for (Object selectedItem : objects) {
			if (selectedItem instanceof ITreeEntity) {
				eventBroker.post(EventConstants.EXPLORER_DELETE_SELECTED_ITEM, (ITreeEntity) selectedItem);
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
		String activePartId = HandlerUtil.getActivePartId(event);
		if (activePartId != null && activePartId.equals(IdConstants.EXPLORER_PART_ID)) {
			execute(SelectionServiceSingleton.getInstance().getSelectionService(), EventBrokerSingleton.getInstance()
					.getEventBroker());
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return canExecute(SelectionServiceSingleton.getInstance().getSelectionService());
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
