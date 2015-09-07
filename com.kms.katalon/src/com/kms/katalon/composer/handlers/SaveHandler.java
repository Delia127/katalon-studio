/*******************************************************************************
 * Copyright (c) 2010 - 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <lars.Vogel@gmail.com> - Bug 419770
 *******************************************************************************/
package com.kms.katalon.composer.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.PartServiceSingleton;
import com.kms.katalon.composer.parts.MultipleTabsCompositePart;
import com.kms.katalon.constants.StringConstants;

@SuppressWarnings("restriction")
public class SaveHandler implements IHandler {
	
	@CanExecute
	public static boolean canExecute() {
		EPartService partService = PartServiceSingleton.getInstance().getPartService();
		MPart part = partService.getActivePart();
		if (getCompositeParentPart(part) != null) {
			return true;
		} else if (part != null) {
			return part.isDirty();
		}
		return false;
	}

	private static MultipleTabsCompositePart getCompositeParentPart(MPart part) {
		EPartService partService = PartServiceSingleton.getInstance().getPartService();
		MultipleTabsCompositePart parentCompositePart = null;
		for (MPart dirtyPart : partService.getDirtyParts()) {
			if (dirtyPart.getObject() instanceof MultipleTabsCompositePart) {
				MultipleTabsCompositePart compositePart = (MultipleTabsCompositePart) dirtyPart.getObject();
				if (compositePart.getChildParts() != null && compositePart.getChildParts().contains(part)) {
					return compositePart;
				}
			}
		}
		return parentCompositePart;
	}

	@Execute
	public static void execute() {
		try {
			EPartService partService = PartServiceSingleton.getInstance().getPartService();
			MPart part = partService.getActivePart();
			MultipleTabsCompositePart parentCompositePart = getCompositeParentPart(part);
			if (parentCompositePart != null) {
				if (parentCompositePart.getChildParts().contains(part)) {
					parentCompositePart.save();
				}
			} else {
				if (part.getObject() instanceof CompatibilityEditor) {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.saveEditor(((CompatibilityEditor) part.getObject()).getEditor(), false);
				} else {
					partService.savePart(part, false);
				}
			}
		} catch (Exception e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(),
					StringConstants.ERROR_TITLE,
					StringConstants.HAND_SAVE_DIA_MSG);
			LoggerSingleton.getInstance().getLogger().error(e);
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