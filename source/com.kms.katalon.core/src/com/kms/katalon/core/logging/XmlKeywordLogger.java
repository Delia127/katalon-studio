package com.kms.katalon.core.logging;

import static com.kms.katalon.core.constants.StringConstants.DF_CHARSET;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SocketHandler;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.constants.CoreMessageConstants;
import com.kms.katalon.core.constants.StringConstants;

public class XmlKeywordLogger implements IKeywordLogger {
    
    private static final int MAXIMUM_LOG_FILES = 100;

    private static final int MAXIMUM_LOG_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private Logger logger;

    private String pendingDescription = null;

    private Stack<KeywordStackElement> currentKeywordStack = null;

    private Stack<Stack<KeywordStackElement>> keywordStacksContainer = new Stack<Stack<KeywordStackElement>>();

    private int nestedLevel;

    private static final ThreadLocal<XmlKeywordLogger> localXmlKeywordLoggerStorage = new ThreadLocal<XmlKeywordLogger>() {
        @Override
        protected XmlKeywordLogger initialValue() {
            return new XmlKeywordLogger();
        }
    };

    public static XmlKeywordLogger getInstance() {
        return localXmlKeywordLoggerStorage.get();
    }

    private XmlKeywordLogger() {
    }

    /**
     * @return Returns current logger if it exists. Otherwise, create a new one includes: customized log file with XML
     * format and customized console handler.
     */
    private Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(XmlKeywordLogger.class.getName());

            // remove default parent's setting
            logger.setUseParentHandlers(false);
        }

        String logFolder = getLogFolderPath();
        if (logger.getHandlers().length == 0 && StringUtils.isNotEmpty(logFolder)) {
            try {
                // Split log into 100 files, every file is maximum 10MB
                FileHandler fileHandler = new FileHandler(logFolder + File.separator + "execution%g.log",
                        MAXIMUM_LOG_FILE_SIZE, MAXIMUM_LOG_FILES, true);

                fileHandler.setEncoding(DF_CHARSET);
                fileHandler.setFormatter(new CustomXmlFormatter());
                logger.addHandler(fileHandler);

                SocketHandler socketHandler = new SystemSocketHandler(StringConstants.DF_LOCAL_HOST_ADDRESS, getPort());
                socketHandler.setEncoding(DF_CHARSET);
                socketHandler.setFormatter(new CustomSocketLogFomatter());
                logger.addHandler(socketHandler);
            } catch (SecurityException e) {
                System.err.println(
                        MessageFormat.format(CoreMessageConstants.MSG_ERR_UNABLE_TO_CREATE_LOGGER, e.getMessage()));
            } catch (IOException e) {
                System.err.println(
                        MessageFormat.format(CoreMessageConstants.MSG_ERR_UNABLE_TO_CREATE_LOGGER, e.getMessage()));
            }
        }
        return logger;
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#close()
     */
    @Override
    public void close() {
        for (Handler handler : logger.getHandlers()) {
            handler.close();
        }
    }

    public static void cleanUp() {

    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#getLogFolderPath()
     */
    @Override
    public String getLogFolderPath() {
        String logFilePath = RunConfiguration.getSettingFilePath();
        return (logFilePath != null) ? new File(logFilePath).getParentFile().getAbsolutePath() : null;
    }

    private int getPort() {
        return RunConfiguration.getPort();
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#startSuite(java.lang.String, java.util.Map)
     */
    @Override
    public void startSuite(String name, Map<String, String> attributes) {
        getLogger().log(new XmlLogRecord(LogLevel.START.getLevel(), StringConstants.LOG_START_SUITE + " : " + name,
                nestedLevel, attributes));
        logRunData(RunConfiguration.HOST_NAME, RunConfiguration.getHostName());
        logRunData(RunConfiguration.HOST_OS, RunConfiguration.getOS());
        logRunData(RunConfiguration.HOST_ADDRESS, RunConfiguration.getHostAddress());
        logRunData(RunConfiguration.APP_VERSION, RunConfiguration.getAppVersion());

        for (Entry<String, String> collectedDataInfo : RunConfiguration.getCollectedTestDataProperties().entrySet()) {
            logRunData(collectedDataInfo.getKey(), collectedDataInfo.getValue());
        }
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#endSuite(java.lang.String, java.util.Map)
     */
    @Override
    public void endSuite(String name, Map<String, String> attributes) {
        getLogger().log(new XmlLogRecord(LogLevel.END.getLevel(), StringConstants.LOG_END_SUITE + " : " + name,
                nestedLevel, attributes));
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#startTest(java.lang.String, java.util.Map, java.util.Stack)
     */
    @Override
    public void startTest(String name, Map<String, String> attributes, Stack<KeywordStackElement> keywordStack) {
        nestedLevel++;
        getLogger().log(new XmlLogRecord(LogLevel.START.getLevel(), StringConstants.LOG_START_TEST + " : " + name,
                nestedLevel, attributes));
        setCurrentKeywordStack(keywordStack);
    }
    
    private void setCurrentKeywordStack(Stack<KeywordStackElement> newKeywordStack) {
        if (currentKeywordStack != null) {
            keywordStacksContainer.push(currentKeywordStack);
        }
        this.currentKeywordStack = newKeywordStack;
    }
    
    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#endTest(java.lang.String, java.util.Map)
     */
    @Override
    public void endTest(String name, Map<String, String> attributes) {
        nestedLevel--;
        getLogger().log(new XmlLogRecord(LogLevel.END.getLevel(), StringConstants.LOG_END_TEST + " : " + name,
                nestedLevel, attributes));
        restorePreviousKeywordStack();
    }
    
    private void restorePreviousKeywordStack() {
        if (!keywordStacksContainer.isEmpty()) {
            currentKeywordStack = keywordStacksContainer.pop();
        } else {
            currentKeywordStack = null;
        }
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#startListenerKeyword(java.lang.String, java.util.Map, java.util.Stack)
     */
    @Override
    public void startListenerKeyword(String name, Map<String, String> attributes,
            Stack<KeywordStackElement> keywordStack) {
        Map<String, String> modifiedAttr = new HashMap<>(attributes != null ? attributes : Collections.emptyMap());
        modifiedAttr.put(StringConstants.XML_LOG_IS_IGNORED_IF_FAILED, Boolean.toString(true));
        startKeyword(name, StringConstants.LOG_LISTENER_ACTION, attributes, keywordStack);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#startKeyword(java.lang.String, java.lang.String, java.util.Map, java.util.Stack)
     */
    @Override
    public void startKeyword(String name, String actionType, Map<String, String> attributes,
            Stack<KeywordStackElement> keywordStack) {
        if (attributes == null) {
            attributes = new HashMap<String, String>();
        }
        if (pendingDescription != null) {
            attributes.put(StringConstants.XML_LOG_DESCRIPTION_PROPERTY, pendingDescription);
            pendingDescription = null;
        }
        getLogger()
                .log(new XmlLogRecord(LogLevel.START.getLevel(), 
                        "Start " + actionType + " : " + name, nestedLevel, attributes));
        if (currentKeywordStack != null) {
            keywordStacksContainer.push(currentKeywordStack);
        }
        this.currentKeywordStack = keywordStack;
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#startKeyword(java.lang.String, java.util.Map, java.util.Stack)
     */
    @Override
    public void startKeyword(String name, Map<String, String> attributes, Stack<KeywordStackElement> keywordStack) {
        if (attributes == null) {
            attributes = new HashMap<String, String>();
        }
        if (pendingDescription != null) {
            attributes.put(StringConstants.XML_LOG_DESCRIPTION_PROPERTY, pendingDescription);
            pendingDescription = null;
        }
        getLogger().log(new XmlLogRecord(LogLevel.START.getLevel(), StringConstants.LOG_START_ACTION_PREFIX + name,
                nestedLevel, attributes));
        if (currentKeywordStack != null) {
            keywordStacksContainer.push(currentKeywordStack);
        }
        this.currentKeywordStack = keywordStack;
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#startKeyword(java.lang.String, java.util.Map, int)
     */
    @Override
    public void startKeyword(String name, Map<String, String> attributes, int nestedLevel) {
        if (attributes == null) {
            attributes = new HashMap<String, String>();
        }
        if (pendingDescription != null) {
            attributes.put(StringConstants.XML_LOG_DESCRIPTION_PROPERTY, pendingDescription);
            pendingDescription = null;
        }
        popKeywordFromStack(nestedLevel);
        getLogger().log(new XmlLogRecord(LogLevel.START.getLevel(), StringConstants.LOG_START_ACTION_PREFIX + name,
                nestedLevel, attributes));
        pushKeywordToStack(name, nestedLevel);
    }

    private void pushKeywordToStack(String keywordName, int nestedLevel) {
        if (currentKeywordStack != null) {
            currentKeywordStack.push(new KeywordStackElement(keywordName, nestedLevel));
        }
    }

    private void popKeywordFromStack(int nestedLevel) {
        while (currentKeywordStack != null && !currentKeywordStack.isEmpty()
                && currentKeywordStack.peek().getNestedLevel() >= nestedLevel) {
            KeywordStackElement keywordStackElement = currentKeywordStack.pop();
            endKeyword(keywordStackElement.getKeywordName(), null, keywordStackElement.getNestedLevel());
        }
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#endKeyword(java.lang.String, java.util.Map, int)
     */
    @Override
    public void endKeyword(String name, Map<String, String> attributes, int nestedLevel) {
        getLogger().log(new XmlLogRecord(LogLevel.END.getLevel(), StringConstants.LOG_END_KEYWORD + " : " + name,
                nestedLevel, attributes));
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#endListenerKeyword(java.lang.String, java.util.Map, java.util.Stack)
     */
    @Override
    public void endListenerKeyword(String name, Map<String, String> attributes,
            Stack<KeywordStackElement> keywordStack) {
        endKeyword(name, StringConstants.LOG_LISTENER_ACTION, attributes, keywordStack);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#endKeyword(java.lang.String, java.lang.String, java.util.Map, java.util.Stack)
     */
    @Override
    public void endKeyword(String name, String keywordType, Map<String, String> attributes,
            Stack<KeywordStackElement> keywordStack) {
        getLogger().log(new XmlLogRecord(LogLevel.END.getLevel(), 
                "End " + keywordType + " : " + name, nestedLevel, attributes));
        if (currentKeywordStack == keywordStack && !keywordStacksContainer.isEmpty()) {
            currentKeywordStack = keywordStacksContainer.pop();
        } else {
            currentKeywordStack = null;
        }
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#endKeyword(java.lang.String, java.util.Map, java.util.Stack)
     */
    @Override
    public void endKeyword(String name, Map<String, String> attributes, Stack<KeywordStackElement> keywordStack) {
        endKeyword(name, StringConstants.LOG_END_KEYWORD, attributes, keywordStack);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logFailed(java.lang.String)
     */
    @Override
    public void logFailed(String message) {
        logMessage(LogLevel.FAILED, message);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logFailed(java.lang.String, java.util.Map)
     */
    @Override
    public void logFailed(String message, Map<String, String> attributes) {
        logMessage(LogLevel.FAILED, message, attributes);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logWarning(java.lang.String)
     */
    @Override
    public void logWarning(String message) {
        logMessage(LogLevel.WARNING, message);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logWarning(java.lang.String, java.util.Map)
     */
    @Override
    public void logWarning(String message, Map<String, String> attributes) {
        logMessage(LogLevel.WARNING, message, attributes);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logPassed(java.lang.String)
     */
    @Override
    public void logPassed(String message) {
        logMessage(LogLevel.PASSED, message);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logPassed(java.lang.String, java.util.Map)
     */
    @Override
    public void logPassed(String message, Map<String, String> attributes) {
        logMessage(LogLevel.PASSED, message, attributes);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logInfo(java.lang.String)
     */
    @Override
    public void logInfo(String message) {
        logMessage(LogLevel.INFO, message);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logInfo(java.lang.String, java.util.Map)
     */
    @Override
    public void logInfo(String message, Map<String, String> attributes) {
        logMessage(LogLevel.INFO, message, attributes);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logRunData(java.lang.String, java.lang.String)
     */
    @Override
    public void logRunData(String dataKey, String dataValue) {
        Map<String, String> attributeMap = new HashMap<String, String>();
        attributeMap.put(dataKey, dataValue);
        logMessage(LogLevel.RUN_DATA, dataKey + " = " + dataValue,
                attributeMap);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logError(java.lang.String)
     */
    @Override
    public void logError(String message) {
        logMessage(LogLevel.ERROR, message);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logError(java.lang.String, java.util.Map)
     */
    @Override
    public void logError(String message, Map<String, String> attributes) {
        logMessage(LogLevel.ERROR, message, attributes);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logMessage(com.kms.katalon.core.logging.LogLevel, java.lang.String)
     */
    @Override
    public void logMessage(LogLevel level, String message) {
        if (message == null) {
            message = "";
        }
        Logger logger = getLogger();
        if (logger != null) {
            logger.log(new XmlLogRecord(level.getLevel(), message, nestedLevel));
        }
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logMessage(com.kms.katalon.core.logging.LogLevel, java.lang.String, java.util.Map)
     */
    @Override
    public void logMessage(LogLevel level, String message, Map<String, String> attributes) {
        if (message == null) {
            message = "";
        }
        Logger logger = getLogger();
        if (logger != null) {
            logger.log(new XmlLogRecord(level.getLevel(), message, nestedLevel, attributes));
        }

    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logMessage(com.kms.katalon.core.logging.LogLevel, java.lang.String, java.lang.Throwable)
     */
    @Override
    public void logMessage(LogLevel level, String message, Throwable thrown) {
        if (message == null) {
            message = "";
        }
        Logger logger = getLogger();
        if (logger != null) {
            logger.log(level.getLevel(), message, thrown);
        }
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#setPendingDescription(java.lang.String)
     */
    @Override
    public void setPendingDescription(String stepDescription) {
        pendingDescription = stepDescription;
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logNotRun(java.lang.String)
     */
    @Override
    public void logNotRun(String message) {
        logMessage(LogLevel.NOT_RUN, message);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logNotRun(java.lang.String, java.util.Map)
     */
    @Override
    public void logNotRun(String message, Map<String, String> attributes) {
        logMessage(LogLevel.NOT_RUN, message, attributes);
    }
}
