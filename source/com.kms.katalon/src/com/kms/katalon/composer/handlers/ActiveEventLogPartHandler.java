package com.kms.katalon.composer.handlers;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.kms.katalon.logging.LogManager;

public class ActiveEventLogPartHandler {

    @Inject
    private IEventBroker eventBroker;

    private Collection<ColorString> bufferredStrings = new ConcurrentLinkedQueue<>();

    private static ActiveEventLogPartHandler instance;

    @PostConstruct
    public void registerWorkbenchCreated() {
        instance = this;

        LogManager.getOutputLogger().setWriter(new PrintStream(LogManager.getOutputLogger()) {
            @Override
            public void write(byte[] buf, int off, int len) {
                writeLog(buf, off, len);
            }
        });

        LogManager.getErrorLogger().setWriter(new PrintStream(LogManager.getOutputLogger()) {
            @Override
            public void write(byte[] buf, int off, int len) {
                writeErrorLog(buf, off, len);
            }
        });
        
        eventBroker.subscribe("KATALON_STUDIO/EVENT_LOG/CLEAR_LOG", new EventHandler() {

            @Override
            public void handleEvent(Event event) {
                bufferredStrings.clear();
            }
        });
    }

    @PreDestroy
    public void onDestroy() {
        LogManager.getOutputLogger().setWriter(null);

        LogManager.getErrorLogger().setWriter(null);
    }

    private void writeLog(byte[] buf, int off, int len) {
        String string = new String(ArrayUtils.subarray(buf, off, len));
        bufferredStrings.add(new ColorString(string, false));
        truncateIfReachMaxSize();
    }

    private void truncateIfReachMaxSize() {
        if (bufferredStrings.size() > 1000) { 
            ColorString[] bufferredStringAsArray = bufferredStrings.toArray(new ColorString[0]);
            bufferredStringAsArray = Arrays.copyOf(bufferredStringAsArray, 1000);
            
            bufferredStrings.clear();
            bufferredStrings.addAll(Arrays.asList(bufferredStringAsArray));
        }
    }

    private void writeErrorLog(byte[] buf, int off, int len) {
        String string = new String(ArrayUtils.subarray(buf, off, len));
        bufferredStrings.add(new ColorString(string, true));
        truncateIfReachMaxSize();
    }

    public static ActiveEventLogPartHandler getInstance() {
        return instance;
    }
    
    public List<ColorString> getBufferredStrings() {
        return new ArrayList<>(bufferredStrings);
    }

    public void clearBufferredStrings() {
        bufferredStrings.clear();
    }

    public static class ColorString {
        private final boolean isError;

        private final String string;

        public ColorString(String string, boolean isError) {
            this.string = string;
            this.isError = isError;
        }

        public boolean isError() {
            return isError;
        }

        public String getString() {
            return string;
        }
    }
}
