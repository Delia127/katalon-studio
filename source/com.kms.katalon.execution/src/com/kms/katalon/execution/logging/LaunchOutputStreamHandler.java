package com.kms.katalon.execution.logging;

import static com.kms.katalon.core.constants.StringConstants.DF_CHARSET;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;

import com.kms.katalon.logging.LogManager;
import com.kms.katalon.logging.LogMode;
import com.kms.katalon.logging.LogUtil;
import com.kms.katalon.logging.SystemLogger;

public class LaunchOutputStreamHandler extends Thread implements IOutputStream {
    private InputStream is;
    private SystemLogger logger;
    private boolean printAllowed;

    private LaunchOutputStreamHandler(InputStream is, SystemLogger logger, boolean printAllowed) {
        this.is = is;
        this.logger = logger;
        this.printAllowed = printAllowed;
    }

    public void run() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(is, DF_CHARSET);
            br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (printAllowed) {
                    println(line);
                }
            }
        } catch (IOException e) {
            // Stream closed
        } finally {
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(isr);
        }
    }

    public synchronized void println(String line) {
        LogUtil.println(logger, line, LogMode.CONSOLE);
    }

    @Override
    public void close() throws IOException {
    }

    public static LaunchOutputStreamHandler outputHandlerFrom(InputStream is) {
        return new LaunchOutputStreamHandler(is, LogManager.getOutputLogger(), true);
    }
    
    public static LaunchOutputStreamHandler outputHandlerFrom(InputStream is, boolean printAllowed) {
        return new LaunchOutputStreamHandler(is, LogManager.getOutputLogger(), printAllowed);
    }

    public static LaunchOutputStreamHandler errorHandlerFrom(InputStream is) {
        return new LaunchOutputStreamHandler(is, LogManager.getErrorLogger(), true);
    }
    
    public static LaunchOutputStreamHandler errorHandlerFrom(InputStream is, boolean printAllowed) {
        return new LaunchOutputStreamHandler(is, LogManager.getErrorLogger(), printAllowed);
    }
}
