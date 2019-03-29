package com.kms.katalon.logging;

import java.util.Date;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

public class LogUtil {

    private LogUtil() {
        // Disable default constructor
    }

    public static void printOutputLine(String message) {
        println(LogManager.getOutputLogger(), message, LogMode.CONSOLE);
    }

    public static void printErrorLine(String message) {
        println(LogManager.getErrorLogger(), message, LogMode.CONSOLE);
    }

    public static void logErrorMessage(String message) {
        logError(null, message);
    }

    public static void logError(Throwable t) {
        logError(t, "");
    }
    
    private static void writeError(final LogMode logMode, final Throwable t, final String message) {
        final SystemLogger errorLogger = LogManager.getErrorLogger();

        logSync(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                errorLogger.changeMode(logMode);

                errorLogger.println();
                errorLogger.println(new Date().toString());
                if (StringUtils.isNotEmpty(message)) {
                    errorLogger.println(message);
                }

                if (t != null) {
                    errorLogger.println(ExceptionUtils.getStackTrace(t));
                }
                errorLogger.println();
                return null;
            }
        }, errorLogger);
    }
    
    public static void logInfo(String message) {
        println(LogManager.getOutputLogger(), message, LogMode.CONSOLE);
        println(LogManager.getOutputLogger(), message, LogMode.LOG);
    }
    
    public static void logError(String message) {
        println(LogManager.getErrorLogger(), message, LogMode.CONSOLE);
        println(LogManager.getErrorLogger(), message, LogMode.LOG);
    }

    public static void printAndLogError(final Throwable t) {
        writeError(LogMode.CONSOLE, t, "");
        writeError(LogMode.LOG, t, "");
    }

    public static void printAndLogError(final Throwable t, final String message) {
        writeError(LogMode.CONSOLE, t, message);
        writeError(LogMode.LOG, t, message);
    }

    public static void logError(final Throwable t, final String message) {
        writeError(LogMode.LOG, t, message);
    }

    public static void println(final SystemLogger logger, final String line, final LogMode mode) {
        if (logger == null) {
            return;
        }

        logSync(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                logger.changeMode(mode);
                logger.println(line);
                return null;
            }
        }, logger);
    }

    private static void logSync(Callable<Object> caller, final SystemLogger logger) {
        synchronized (logger) {
            logger.waitForAndLock();
            LogMode oldMode = logger.getMode();
            try {
                caller.call();
            } catch (Exception e) {
                // Nothing to log here
            } finally {
                logger.changeMode(oldMode);
                logger.unlock();
            }
        }
    }
}
