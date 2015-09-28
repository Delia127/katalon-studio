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

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.constants.StringConstants;
import com.kms.katalon.constants.EventConstants;


public class QuitHandler {
	@Inject
	IEventBroker eventBroker;
	
	@Execute
	public boolean execute(IWorkbench workbench, Shell shell, EPartService partService){
		if (MessageDialog.openConfirm(shell, StringConstants.HAND_QUIT_DIA_TITLE,
				StringConstants.HAND_QUIT_DIA_MSG)) {
			if (partService.saveAll(true)) {
				eventBroker.send(EventConstants.PROJECT_CLOSE, null);
				workbench.close();		
				return true;
			}
		}
		return false;
	}
}
