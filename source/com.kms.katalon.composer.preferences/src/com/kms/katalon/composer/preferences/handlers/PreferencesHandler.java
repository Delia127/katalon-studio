/*******************************************************************************
 * Copyright (c) 2014 OPCoach. All rights reserved. This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Manumitting Technologies : Brian de Alwis for initial API and implementation OPCoach : O.Prouvost fix
 * bugs on hierarchy
 *******************************************************************************/
/*
 * Handler to open up a configured preferences dialog. Written by Brian de Alwis, Manumitting Technologies. Placed in
 * the public domain. This code comes from : http://www.eclipse.org/forums/index.php/fa/4347/ and was referenced in the
 * thread : http://www.eclipse.org/forums/index.php/m/750139/
 */
package com.kms.katalon.composer.preferences.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.preferences.internal.PreferencesRegistry;

public class PreferencesHandler {

    @CanExecute
    public boolean canExecute() {
        return true;
    }

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, PreferencesRegistry preferencesRegistry) {
        PreferenceManager pm = preferencesRegistry.getPreferenceManager(PreferencesRegistry.PREFS_PAGE_XP);
        PreferenceDialog dialog = new PreferenceDialog(shell, pm);
        dialog.create();
        dialog.getTreeViewer().setComparator(new CViewerComparator());
        dialog.getTreeViewer().expandAll();
        dialog.setMinimumPageSize(500, 500);
        dialog.open();
    }

    /**
     * Custom View Comparator is used to sort preference pages and keep General page on top of others
     */
    private class CViewerComparator extends ViewerComparator {

        @SuppressWarnings("unchecked")
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            int cat1 = category(e1);
            int cat2 = category(e2);

            if (cat1 != cat2) {
                return cat1 - cat2;
            }

            String name1 = getLabel(viewer, e1);
            String name2 = getLabel(viewer, e2);

            // Keep General preference on top of the list
            if ("General".equals(name1) && getComparator().compare(name1, name2) > 0) {
                return -1;
            }
            // use the comparator to compare the strings
            return getComparator().compare(name1, name2);
        }

        private String getLabel(Viewer viewer, Object e1) {
            String name1;
            if (viewer == null || !(viewer instanceof ContentViewer)) {
                name1 = e1.toString();
            } else {
                IBaseLabelProvider prov = ((ContentViewer) viewer).getLabelProvider();
                if (prov instanceof ILabelProvider) {
                    ILabelProvider lprov = (ILabelProvider) prov;
                    name1 = lprov.getText(e1);
                } else {
                    name1 = e1.toString();
                }
            }
            if (name1 == null) {
                name1 = "";//$NON-NLS-1$
            }
            return name1;
        }

    }

}
