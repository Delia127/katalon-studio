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

import javax.inject.Named;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.e4.compatibility.CompatibilityEditor;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.components.services.PartServiceSingleton;
import com.kms.katalon.composer.parts.MultipleTabsCompositePart;
import com.kms.katalon.composer.util.groovy.GroovyEditorUtil;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.StringConstants;

@SuppressWarnings("restriction")
public class SaveHandler implements IHandler {

    @CanExecute
    public boolean canExecute() {
        EPartService partService = PartServiceSingleton.getInstance().getPartService();
        MPart part = partService.getActivePart();
        if (getCompositeParentPart(part) != null) {
            return true;
        } else if (part != null) {
            return part.isDirty();
        }
        return false;
    }

    private MultipleTabsCompositePart getCompositeParentPart(MPart part) {
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
    public void execute(@Named(IServiceConstants.ACTIVE_PART) MPart part) {
        try {
            MultipleTabsCompositePart parentCompositePart = getCompositeParentPart(part);
            if (parentCompositePart != null) {
                if (parentCompositePart.getChildParts().contains(part)) {
                    parentCompositePart.save();
                }
            } else {
                if (part.getObject() instanceof CompatibilityEditor) {
                    GroovyEditorUtil.saveEditor(part);
                    EventBrokerSingleton.getInstance().getEventBroker()
                            .post(EventConstants.ECLIPSE_EDITOR_SAVED, part);
                } else {
                    EPartService partService = PartServiceSingleton.getInstance().getPartService();
                    partService.savePart(part, false);
                }
            }

        } catch (Exception e) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), StringConstants.ERROR_TITLE,
                    StringConstants.HAND_SAVE_DIA_MSG);
            LoggerSingleton.logError(e);
        }
    }

    @Override
    public void addHandlerListener(IHandlerListener handlerListener) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        EPartService partService = PartServiceSingleton.getInstance().getPartService();
        MPart part = partService.getActivePart();
        execute(part);
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
    }
}