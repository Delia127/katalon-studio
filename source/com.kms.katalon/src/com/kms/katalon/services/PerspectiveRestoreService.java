package com.kms.katalon.services;

import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;

/**
 * A service which will be used by EModelService implementation to reload a perspective so it can be restored to its
 * original state.
 * 
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=404231#c6
 */
public interface PerspectiveRestoreService {
    /**
     * Reloads a perspective state from a persistence storage.
     * 
     * @param perspectiveID the ID of the perspective to reload
     * @param window the window which requested the perspective reload
     * @return a newly reloaded {@link MPerspective} object for the given perspectiveID, or <code>null</code> if no
     * {@link MPerspective} for the given perspectiveID can be found
     */
    public MPerspective reloadPerspective(String perspectiveID, MWindow window);
}
