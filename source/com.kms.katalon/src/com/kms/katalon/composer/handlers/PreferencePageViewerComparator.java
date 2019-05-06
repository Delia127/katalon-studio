package com.kms.katalon.composer.handlers;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * Custom View Comparator is used to sort preference pages and keep General page on top and Katalon page at the
 * bottom of others.
 */
public class PreferencePageViewerComparator extends ViewerComparator {

    private static final String KATALON_PAGE_NAME = "Katalon";

    private static final String GENERAL_PAGE_NAME = "General";
    
    private static final String WEBUI_PAGE_NAME = "Web UI";
    
    private static final String MOBILE_PAGE_NAME = "Mobile";

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
        if (GENERAL_PAGE_NAME.equals(name1)) {
            return -1;
        }
        if (GENERAL_PAGE_NAME.equals(name2)) {
            return 1;
        }

        // Keep Katalon preference at the bottom of the list
        if (KATALON_PAGE_NAME.equals(name1)) {
            return 1;
        }
        if (KATALON_PAGE_NAME.equals(name2)) {
            return -1;
        }
        
        if (WEBUI_PAGE_NAME.equals(name1) && MOBILE_PAGE_NAME.equals(name2)) {
            return -1;
        }
        
        if (MOBILE_PAGE_NAME.equals(name1) && WEBUI_PAGE_NAME.equals(name2)) {
            return 1;
        }

        // use the comparator to compare the strings
        return getComparator().compare(name1, name2);
    }

    private String getLabel(Viewer viewer, Object node) {
        if (node instanceof IPreferenceNode) {
            return ((IPreferenceNode) node).getLabelText();
        }
        return StringUtils.EMPTY;
    }

}