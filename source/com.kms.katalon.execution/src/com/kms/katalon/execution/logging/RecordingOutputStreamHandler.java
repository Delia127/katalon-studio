package com.kms.katalon.execution.logging;

import static com.kms.katalon.core.constants.StringConstants.DF_CHARSET;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.execution.addon.ExecutionBundleActivator;

public class RecordingOutputStreamHandler extends Thread implements IOutputStream {
    
    private IEventBroker eventBroker = ExecutionBundleActivator.getInstance().getEventBroker();
    
    private final InputStream is;
    
    private final OutputType type;
    
    private RecordingOutputStreamHandler(InputStream is, OutputType type) {
        this.is = is;
        this.type = type;
    }
    
    public void run() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(is, DF_CHARSET);
            br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                println(line);
            }
        } catch (IOException e) {
            // Stream closed
        } finally {
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(isr);
        }
    }
    
    public static RecordingOutputStreamHandler outputHandlerFrom(InputStream is) {
        return new RecordingOutputStreamHandler(is, OutputType.OUTPUT);
    }
    
    public static RecordingOutputStreamHandler errorHandlerFrom(InputStream is) {
        return new RecordingOutputStreamHandler(is, OutputType.ERROR);
    }

    @Override
    public void println(String line) throws IOException {
        eventBroker.post(EventConstants.WEBUI_VERIFICATION_LOG_UPDATED, RecordedOutputLine.newInstance(type, line));
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        
    }

    public static class RecordedOutputLine {
        private final OutputType type;
        
        private final String text;
        
        private RecordedOutputLine(OutputType type, String text) {
            this.type = type;
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public OutputType getType() {
            return type;
        }
        
        public static RecordedOutputLine newInstance(OutputType type, String text) {
            return new RecordedOutputLine(type, text);
        }
    }
    
    public static enum OutputType {
        OUTPUT,
        ERROR
    }
}
