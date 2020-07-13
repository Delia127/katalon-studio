package com.kms.katalon.composer.toolbar;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.composer.components.impl.constants.ComposerComponentsImplMessageConstants;
import com.kms.katalon.composer.components.impl.handler.KSEFeatureAccessHandler;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.feature.FeatureServiceConsumer;
import com.kms.katalon.feature.IFeatureService;
import com.kms.katalon.feature.KSEFeature;

public class ApplitoolsSettingHandler {
    private static final String APPLITOOLS_CUSTOM_KEYWORD_ID = Long.toString(44);

    private IFeatureService featureService = FeatureServiceConsumer.getServiceInstance();

    @CanExecute
    public boolean canExecute() {
        return ProjectController.getInstance().getCurrentProject() != null;
    }

    @Execute
    public void execute() {
        if (featureService.canUse(KSEFeature.APPLITOOLS_PLUGIN)) {
            EventBrokerSingleton.getInstance().getEventBroker().post(EventConstants.PROJECT_SETTINGS_PAGE,
                    APPLITOOLS_CUSTOM_KEYWORD_ID);
        } else {
            KSEFeatureAccessHandler.handleUnauthorizedAccess(KSEFeature.APPLITOOLS_PLUGIN,
                    ComposerComponentsImplMessageConstants.PREF_WARN_KSE_APPLITOOLS_PLUGIN);
        }
    }
}
