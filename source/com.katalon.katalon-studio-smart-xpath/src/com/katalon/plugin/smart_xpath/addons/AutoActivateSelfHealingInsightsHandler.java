package com.katalon.plugin.smart_xpath.addons;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspectiveStack;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.katalon.plugin.smart_xpath.constant.SmartXPathConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.constants.IdConstants;

public class AutoActivateSelfHealingInsightsHandler implements EventHandler {
    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EPartService partService;

    @Inject
    private MApplication application;

    @Inject
    private EModelService modelService;

    @PostConstruct
    public void postConstruct() {
        eventBroker.subscribe(EventConstants.PROJECT_OPENED, this);
    }

    private void checkInConsoleLogPart(MPartStack parent) {
        MUIElement consoleLogPart = (MUIElement) modelService.find(IdConstants.IDE_PROBLEM_VIEW_PART_ID,
                parent);
        if (consoleLogPart != null) {
            parent.setSelectedElement((MStackElement) consoleLogPart);
        }
    }

    @Override
    public void handleEvent(Event event) {
        List<MPerspectiveStack> psList = modelService.findElements(application, null, MPerspectiveStack.class, null);
        MPartStack consolePartStack = (MPartStack) modelService.find(IdConstants.CONSOLE_PART_STACK_ID,
                psList.get(0).getSelectedElement());
        MPart selfHealingInsightsPart = (MPart) modelService.find(SmartXPathConstants.SELF_HEALING_INSIGHTS_PART_ID,
                consolePartStack);

        if (selfHealingInsightsPart != null) {
            partService.activate(selfHealingInsightsPart, false);
            this.checkInConsoleLogPart(consolePartStack);
        }
    }
}
