package com.kms.katalon.logging;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.runtime.Platform;

public class SystemLogger extends PrintStream {

    private LogMode logMode;

    private boolean locked;

    private PrintStream writer;

    SystemLogger(OutputStream os, LogMode defaultMode) {
        super(os, true);
        logMode = defaultMode;
        locked = false;
    }

    SystemLogger(OutputStream os, LogMode defaultMode, String encode) throws UnsupportedEncodingException {
        super(os, true, encode);
        logMode = defaultMode;
        locked = false;
    }

    void changeMode(LogMode mode) {
        logMode = mode;
    }

    LogMode getMode() {
        return logMode;
    }

    void lock() {
        locked = true;
    }

    void unlock() {
        locked = false;
    }

    void waitFor() {
        while (locked) {

        }
    }

    synchronized void waitForAndLock() {
        waitFor();
        lock();
    }

    @Override
    public synchronized void write(byte buf[], int off, int len) {
        try {
            switch (logMode) {
                case CONSOLE:
                    super.write(buf, off, len);

                    break;
                case LOG:
                    File logFile = getLogFile();
                    FileUtils.writeByteArrayToFile(logFile, ArrayUtils.subarray(buf, off, len), true);
                    break;
            }
        } catch (IOException ignored) {}
        finally {
            if (writer != null) {
                writer.write(buf, off, len);
            }
        }
    }

    private File getLogFile() {
        return Platform.getLogFileLocation().toFile();
    }

    public void addWriter(PrintStream printStream) {
        this.writer = printStream;
    }
}
