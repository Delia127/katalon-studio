package com.kms.katalon.core.logging;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kms.katalon.core.main.ScriptEngine;

public class KeywordLogger implements IKeywordLogger {
    
    private static final Logger selfLogger = LoggerFactory.getLogger(KeywordLogger.class);
    
    private static final Map<Class, KeywordLogger> keywordLoggerLookup = new ConcurrentHashMap<>();
    
    private final Logger logger;
    
    private final XmlKeywordLogger xmlKeywordLogger;
    
    public static KeywordLogger getInstance(Class clazz) {
        if (clazz == null) { // just in case
            selfLogger.error("Logger name is null. This should be a bug of Katalon Studio.");
            clazz = KeywordLogger.class;
        }
        KeywordLogger keywordLogger = keywordLoggerLookup.get(clazz);
        if (keywordLogger == null) {
            keywordLogger = new KeywordLogger(clazz.getName());
            keywordLoggerLookup.put(clazz, keywordLogger);
        }
        return keywordLogger;
    }

    private KeywordLogger(String className) {
        className = ScriptEngine.getTestCaseName(className);
        logger = LoggerFactory.getLogger(className);
        xmlKeywordLogger = XmlKeywordLogger.getInstance();
    }

    @Override
    public void close() {
        xmlKeywordLogger.close();
    }

    @Override
    public String getLogFolderPath() {
        return xmlKeywordLogger.getLogFolderPath();
    }

    @Override
    public void startSuite(String name, Map<String, String> attributes) {
        logger.info(name);
        xmlKeywordLogger.startSuite(name, attributes);
    }

    @Override
    public void endSuite(String name, Map<String, String> attributes) {
        logger.info(name);
        xmlKeywordLogger.endSuite(name, attributes);
    }

    @Override
    public void startTest(String name, Map<String, String> attributes, Stack<KeywordStackElement> keywordStack) {
        logger.info(name);
        xmlKeywordLogger.startTest(name, attributes, keywordStack);
    }

    @Override
    public void endTest(String name, Map<String, String> attributes) {
        logger.info(name);
        xmlKeywordLogger.endTest(name, attributes);
    }

    @Override
    public void startListenerKeyword(String name, Map<String, String> attributes,
            Stack<KeywordStackElement> keywordStack) {
        logger.info(name);
        xmlKeywordLogger.startListenerKeyword(name, attributes, keywordStack);
    }

    @Override
    public void startKeyword(String name, String actionType, Map<String, String> attributes,
            Stack<KeywordStackElement> keywordStack) {
        logger.info(name);
        xmlKeywordLogger.startKeyword(name, actionType, attributes, keywordStack);
    }

    @Override
    public void startKeyword(String name, Map<String, String> attributes, Stack<KeywordStackElement> keywordStack) {
        logger.info(name);
        xmlKeywordLogger.startKeyword(name, attributes, keywordStack);
    }

    @Override
    public void startKeyword(String name, Map<String, String> attributes, int nestedLevel) {
        logger.info(name);
        xmlKeywordLogger.startKeyword(name, attributes, nestedLevel);
    }

    @Override
    public void endKeyword(String name, Map<String, String> attributes, int nestedLevel) {
        logger.info(name);
        xmlKeywordLogger.endKeyword(name, attributes, nestedLevel);
    }

    @Override
    public void endListenerKeyword(String name, Map<String, String> attributes,
            Stack<KeywordStackElement> keywordStack) {
        logger.info(name);
        xmlKeywordLogger.endListenerKeyword(name, attributes, keywordStack);
    }

    @Override
    public void endKeyword(String name, String keywordType, Map<String, String> attributes,
            Stack<KeywordStackElement> keywordStack) {
        logger.info(name);
        xmlKeywordLogger.endKeyword(name, keywordType, attributes, keywordStack);
    }

    @Override
    public void endKeyword(String name, Map<String, String> attributes, Stack<KeywordStackElement> keywordStack) {
        logger.info(name);
        xmlKeywordLogger.endKeyword(name, attributes, keywordStack);
    }

    @Override
    public void logFailed(String message) {
        logger.error(message);
        xmlKeywordLogger.logFailed(message);
    }

    @Override
    public void logFailed(String message, Map<String, String> attributes) {
        logFailed(message);
        xmlKeywordLogger.logFailed(message, attributes);
    }

    @Override
    public void logWarning(String message) {
        logger.warn(message);
        xmlKeywordLogger.logWarning(message);
    }

    @Override
    public void logWarning(String message, Map<String, String> attributes) {
        logWarning(message);
        xmlKeywordLogger.logWarning(message, attributes);
    }

    @Override
    public void logPassed(String message) {
        logger.info(message);
        xmlKeywordLogger.logPassed(message);
    }

    @Override
    public void logPassed(String message, Map<String, String> attributes) {
        logPassed(message);
        xmlKeywordLogger.logPassed(message, attributes);
    }

    @Override
    public void logInfo(String message) {
        logger.info(message);
        xmlKeywordLogger.logInfo(message);
    }

    @Override
    public void logInfo(String message, Map<String, String> attributes) {
        logInfo(message);
        xmlKeywordLogger.logInfo(message, attributes);
    }

    @Override
    public void logRunData(String dataKey, String dataValue) {
        logger.info("{} = {}", dataKey, dataValue);
        xmlKeywordLogger.logRunData(dataKey, dataValue);
    }

    @Override
    public void logError(String message) {
        logger.error(message);
        xmlKeywordLogger.logError(message);
    }

    @Override
    public void logError(String message, Map<String, String> attributes) {
        logError(message);
        xmlKeywordLogger.logError(message, attributes);
    }

    @Override
    public void logMessage(LogLevel level, String message) {
        switch (level) {
            case WARNING:
            case NOT_RUN:
                logger.warn(message);
                break;
            case FAILED:
            case ERROR:
            case ABORTED:
            case INCOMPLETE:
                logger.error(message);
                break;
            default:
                logger.info(message);
        }
        xmlKeywordLogger.logMessage(level, message);
    }

    @Override
    public void logMessage(LogLevel level, String message, Map<String, String> attributes) {
        logMessage(level, message);
        xmlKeywordLogger.logMessage(level, message, attributes);
    }

    @Override
    public void logMessage(LogLevel level, String message, Throwable thrown) {
        logMessage(level, message);
        xmlKeywordLogger.logMessage(level, message, thrown);
    }

    @Override
    public void setPendingDescription(String stepDescription) {
        xmlKeywordLogger.setPendingDescription(stepDescription);
    }

    @Override
    public void logNotRun(String message) {
        logWarning(message);
        xmlKeywordLogger.logNotRun(message);
    }

    @Override
    public void logNotRun(String message, Map<String, String> attributes) {
        logWarning(message, attributes);
        xmlKeywordLogger.logNotRun(message, attributes);
    }

}
