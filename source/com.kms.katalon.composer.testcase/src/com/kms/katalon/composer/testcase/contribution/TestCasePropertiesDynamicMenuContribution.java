package com.kms.katalon.composer.testcase.contribution;

import java.util.List;

import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.handlers.EditTestCasePropertiesHandler;

public class TestCasePropertiesDynamicMenuContribution {

    private static final String CONTRIBUTION_URI = "bundleclass://"
            + FrameworkUtil.getBundle(EditTestCasePropertiesHandler.class).getSymbolicName() + "/"
            + EditTestCasePropertiesHandler.class.getName();

    @AboutToShow
    public void aboutToShow(List<MMenuElement> items) {
        if (!EditTestCasePropertiesHandler.canExecute()) {
            return;
        }

        MDirectMenuItem dynamicItem = MMenuFactory.INSTANCE.createDirectMenuItem();
        dynamicItem.setLabel(StringConstants.PROPERTIES);
        dynamicItem.setContributionURI(CONTRIBUTION_URI);
        items.add(MMenuFactory.INSTANCE.createMenuSeparator());
        items.add(dynamicItem);
    }
}
