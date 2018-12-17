
package com.kms.katalon.composer.testcase.addons;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.composer.components.impl.transfer.TreeEntityTransfer;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.explorer.util.TransferTypeCollection;
import com.kms.katalon.composer.testcase.handlers.DeleteTestCaseFolderHandler;
import com.kms.katalon.composer.testcase.handlers.DeleteTestCaseHandler;
import com.kms.katalon.composer.testcase.handlers.EvaluateIntegrationContributionViewHandler;
import com.kms.katalon.composer.testcase.handlers.OpenTestCaseHandler;
import com.kms.katalon.composer.testcase.handlers.RefreshTestCaseHandler;
import com.kms.katalon.composer.testcase.handlers.RenameTestCaseHandler;
import com.kms.katalon.composer.testcase.preferences.TestCasePreferenceDefaultValueInitializer;
import com.kms.katalon.constants.EventConstants;

public class TestCaseInjectionManagerAddon implements EventHandler {

    @Inject
    private IEventBroker eventBroker;

    @Inject
    private EPartService partService;

    @PostConstruct
    public void initHandlers(IEclipseContext context) {
        ContextInjectionFactory.make(DeleteTestCaseHandler.class, context);
        ContextInjectionFactory.make(DeleteTestCaseFolderHandler.class, context);
        ContextInjectionFactory.make(OpenTestCaseHandler.class, context);
        ContextInjectionFactory.make(RenameTestCaseHandler.class, context);
        ContextInjectionFactory.make(RefreshTestCaseHandler.class, context);
        ContextInjectionFactory.make(EvaluateIntegrationContributionViewHandler.class, context);

        TransferTypeCollection.getInstance().addTreeEntityTransferType(TreeEntityTransfer.getInstance());
        eventBroker.subscribe(EventConstants.ACTIVATION_CHECKED, this);
    }

    @Override
    public void handleEvent(Event event) {
        if (event.getTopic().equals(EventConstants.ACTIVATION_CHECKED)) {
            partService.showPart("com.kms.katalon.composer.testcase.part.keywordsBrowser", PartState.CREATE);
        }
    }

    @PreDestroy
    public void handlePreClose() {
        try {
            TestCasePreferenceDefaultValueInitializer.saveStore();
        } catch (IOException e) {
            LoggerSingleton.logError(e);
        }
    }
}
