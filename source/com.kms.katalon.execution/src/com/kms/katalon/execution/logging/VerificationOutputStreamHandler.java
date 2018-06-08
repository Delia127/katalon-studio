package com.kms.katalon.execution.logging;

import static com.kms.katalon.core.constants.StringConstants.DF_CHARSET;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.eclipse.e4.core.services.events.IEventBroker;

import com.kms.katalon.composer.components.event.EventBrokerSingleton;
import com.kms.katalon.constants.EventConstants;


public class VerificationOutputStreamHandler extends Thread implements IOutputStream {
    
    private IEventBroker eventBroker = EventBrokerSingleton.getInstance().getEventBroker();
    
    private InputStream is;
    
    private VerificationOutputStreamHandler(InputStream is) {
        this.is = is;
    }
    
    public void run() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(is, DF_CHARSET);
            br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                eventBroker.post(EventConstants.WS_VERIFICATION_LOG_UPDATED, line);
            }
        } catch (IOException e) {
            // Stream closed
        } finally {
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(isr);
        }
    }
    
    public static VerificationOutputStreamHandler outputHandlerFrom(InputStream is) {
        return new VerificationOutputStreamHandler(is);
    }
    
    public static VerificationOutputStreamHandler errorHandlerFrom(InputStream is) {
        return new VerificationOutputStreamHandler(is);
    }

    @Override
    public void println(String line) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub
        
    }

}
