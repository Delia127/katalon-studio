package com.kms.katalon.composer.components.impl.menu;

import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.components.impl.constants.StringConstants;

public abstract class AbstractPropertiesMenuContribution {

    /**
     * Condition to display the menu
     * 
     * @return true to show the menu; false otherwise.
     */
    protected abstract boolean canShow();

    /**
     * Get menu class handler
     * 
     * @return Menu class handler
     */
    protected abstract Class<?> getHandlerClass();

    private String getContributionURI() {
        return "bundleclass://" + FrameworkUtil.getBundle(getHandlerClass()).getSymbolicName() + "/"
                + getHandlerClass().getName();
    }

    @AboutToShow
    public void aboutToShow(List<MMenuElement> items) {
        if (!canShow()) {
            return;
        }

        MDirectMenuItem dynamicItem = MMenuFactory.INSTANCE.createDirectMenuItem();
        dynamicItem.setLabel(StringConstants.PROPERTIES);
        dynamicItem.setContributionURI(getContributionURI());
        items.add(MMenuFactory.INSTANCE.createMenuSeparator());
        items.add(dynamicItem);
    }
}
