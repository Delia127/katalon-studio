package com.kms.katalon.composer.webservice.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.composer.webservice.view.ApiQuickStartPart;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.constants.ImageConstants;
import com.kms.katalon.constants.StringConstants;

public class OpenApiQuickStartHandler {

    private static final String KATALON_BUNDLE_URI = "bundleclass://"
            + FrameworkUtil.getBundle(ApiQuickStartPart.class).getSymbolicName() + "/";

    private static final String QUICKSTART_PART_URI = KATALON_BUNDLE_URI + ApiQuickStartPart.class.getName();

    @Inject
    private MApplication application;

    @Inject
    private EModelService modelService;

    @Inject
    private EPartService partService;

    @Execute
    public void execute() {
        MPartStack stack = (MPartStack) modelService.find(IdConstants.COMPOSER_CONTENT_PARTSTACK_ID, application);
        if (stack == null) {
            return;
        }

        String partId = getPartId();
        MPart mPart = (MPart) modelService.find(partId, application);
        if (mPart == null) {
            mPart = modelService.createModelElement(MPart.class);
            mPart.setElementId(partId);
            mPart.setLabel(StringConstants.PA_QUICKSTART);

            mPart.setIconURI(getIconURI());

            mPart.setContributionURI(getContributionURI());
            mPart.setCloseable(true);
            stack.getChildren().add(mPart);
        }
        partService.showPart(mPart, PartState.ACTIVATE);
        partService.activate(mPart, true);
        partService.bringToTop(mPart);

        mPart.setVisible(true);
        stack.setSelectedElement(mPart);
    }

    @Inject
    @Optional
    public void execute(@UIEventTopic(EventConstants.API_QUICK_START_DIALOG_OPEN) Object eventData) {
        execute();
    }
    private String getPartId() {
        return IdConstants.QUICKSTART_PART_ID;
    }

    private String getIconURI() {
        return ImageConstants.URI_IMG_WELCOME;
    }

    private String getContributionURI() {
        return QUICKSTART_PART_URI;
    }
}
