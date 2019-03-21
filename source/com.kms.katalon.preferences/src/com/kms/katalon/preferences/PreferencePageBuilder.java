package com.kms.katalon.preferences;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.jface.preference.PreferencePage;

@SuppressWarnings("restriction")
public interface PreferencePageBuilder {

    PreferencePage build(IContributionFactory contributionFactory, IEclipseContext context);
}
