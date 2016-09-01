package com.kms.katalon.addons;

/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation<br>
 * René Brandstetter - Bug 404231 - resetPerspectiveModel() does not reset the perspective
 ******************************************************************************/

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.services.PerspectiveRestoreService;

/**
 * An add-on which enables a perspective restore/reset in E4.
 * 
 * This add-on will store the perspective states in the application snippet container. It will also register an
 * {@link PerspectiveRestoreService} implementation in the {@link IEclipseContext} of the application which is able to
 * restore them.
 * 
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=404231#c6
 */
/* EventTopic and IEclipseContext which are needed. */
public class PerspectiveResetAddon {

    /**
     * Initializes/Registers an {@link PerspectiveRestoreService} which is able to restore {@link MPerspective} states
     * which are stored by this add-on.
     * 
     * @param eclipseContext the {@link IEclipseContext} of the application
     */
    @PostConstruct
    public void init(IEclipseContext eclipseContext) {
        eclipseContext.set(PerspectiveRestoreService.class, new SnippetBasedPerspectiveRestoreService(eclipseContext));
    }

    /**
     * Event handling method which will store the state of a newly opened perspective in the application snippet
     * container.
     * 
     * <p>
     * This method will check if there is already a perspective snippet stored for the newly opened perspective. If this
     * isn't the case it will create a new snippet of the perspective in the application snippet container, otherwise it
     * will do nothing.
     * </p>
     * 
     * @param object the event data send by the event broker
     * @param application the current application
     * @param modelService the model service of the application (used to find and create the snippets)
     */
    @Inject
    public void keepState(@Optional @UIEventTopic(EventConstants.WORKSPACE_CREATED) Object object,
            MApplication application, EModelService modelService) {
        MUIElement perspectiveStack = modelService.find(IdConstants.MAIN_PERSPECTIVE_STACK_ID, application);
        if (!(perspectiveStack instanceof MPerspectiveStack)) {
            return;
        }

        // backup all perspectives
        for (MPerspective perspective : ((MPerspectiveStack) perspectiveStack).getChildren()) {
            if (modelService.findSnippet(application, perspective.getElementId()) != null) {
                continue;
            }
            // no snippet exists so far, create a new one
            modelService.cloneElement(perspective, application);
        }

        // backup main menu
        MMenu mainMenu = application.getChildren().get(0).getMainMenu();
        if (mainMenu == null || !IdConstants.MAIN_MENU_ID.equals(mainMenu.getElementId())
                || modelService.findSnippet(application, IdConstants.MAIN_MENU_ID) != null) {
            return;
        }
        modelService.cloneElement(mainMenu, application);
    }

    /**
     * An {@link PerspectiveRestoreService} implementation which will restore an MPerspective from the application
     * snippet container.
     */
    private static final class SnippetBasedPerspectiveRestoreService implements PerspectiveRestoreService {

        /**
         * The application IEclipseContext which will be used to find the snippet.
         */
        private final IEclipseContext appContext;

        /**
         * Sole Constructor.
         * 
         * @param appContext the application {@link IEclipseContext}
         * @throws IllegalArgumentException if the given context is <code>null</code>
         */
        public SnippetBasedPerspectiveRestoreService(IEclipseContext appContext) {
            if (appContext == null) {
                throw new IllegalArgumentException("No IEclipseContext given!"); //$NON-NLS-1$
            }

            this.appContext = appContext;
        }

        /**
         * {@inheritDoc}
         * <p>
         * Uses the application snippet container to retrieve/reload a {@link MPerspective} state.
         * </p>
         */
        @Override
        public MPerspective reloadPerspective(String perspectiveID, MWindow window) {
            EModelService modelService = appContext.get(EModelService.class);
            if (window == null || perspectiveID == null || modelService == null) {
                return null;
            }

            MApplication application = appContext.get(MApplication.class);

            MUIElement storedPerspState = modelService.cloneSnippet(application, perspectiveID, window);
            if (storedPerspState instanceof MPerspective) {
                return (MPerspective) storedPerspState;
            }

            return null;
        }

    }
}
