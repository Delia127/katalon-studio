package com.kms.katalon.tracking.facade;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.greenrobot.eventbus.EventBus;

import com.kms.katalon.application.RunningMode;
import com.kms.katalon.application.usagetracking.TrackingEvent;
import com.kms.katalon.application.usagetracking.UsageActionTrigger;
import com.kms.katalon.application.utils.ActivationInfoCollector;
import com.kms.katalon.core.event.EventBusSingleton;
import com.kms.katalon.tracking.core.TrackingManager;
import com.kms.katalon.tracking.event.subscriber.GenerateCommandEventSubscriber;
import com.kms.katalon.tracking.event.subscriber.KatalonOpenFirstTimeEventSubscriber;
import com.kms.katalon.tracking.event.subscriber.KatalonStudioTrackEventSubscriber;
import com.kms.katalon.tracking.event.subscriber.RecordEventSubscriber;
import com.kms.katalon.tracking.event.subscriber.SaveEventSubscriber;
import com.kms.katalon.tracking.event.subscriber.SpyEventSubscriber;
import com.kms.katalon.tracking.event.subscriber.TestCaseExecutionEventSubscriber;
import com.kms.katalon.tracking.event.subscriber.TestSuiteCollectionExecutionEventSubscriber;
import com.kms.katalon.tracking.event.subscriber.TestSuiteExecutionEventSubscriber;

public class TrackingFacade {

    public TrackingFacade() {
    }

    public void init(RunningMode runningMode) {
        registerSubscribers();

        scheduleCollectingStatistics(runningMode);
    }
    
    public void scheduleCollectingStatistics(RunningMode runningMode) {
        int trackingTime = TrackingManager.getInstance().getTrackingTime();
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            EventBus eventBus = EventBusSingleton.getInstance().getEventBus();
            eventBus.post(new TrackingEvent(UsageActionTrigger.COLLECT_STATISTICS, new HashMap<String, Object>() {{
                put("isAnonymous", !ActivationInfoCollector.isActivated());
                put("runningMode", runningMode.getMode());
            }}));
        }, trackingTime, trackingTime, TimeUnit.SECONDS);
    }

    public void registerSubscribers() {
        EventBus eventBus = EventBusSingleton.getInstance().getEventBus();
        eventBus.register(new KatalonStudioTrackEventSubscriber());
        eventBus.register(new KatalonOpenFirstTimeEventSubscriber());
        eventBus.register(new TestCaseExecutionEventSubscriber());
        eventBus.register(new TestSuiteExecutionEventSubscriber());
        eventBus.register(new TestSuiteCollectionExecutionEventSubscriber());
        eventBus.register(new SaveEventSubscriber());
        eventBus.register(new SpyEventSubscriber());
        eventBus.register(new RecordEventSubscriber());
        eventBus.register(new GenerateCommandEventSubscriber());
    }
}
