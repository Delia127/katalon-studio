package com.kms.katalon.core.logging;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.constants.CoreConstants;
import com.kms.katalon.core.constants.StringConstants;
import com.kms.katalon.core.main.ScriptEngine;

public class KeywordLogger implements IKeywordLogger {
    
    private static final int HR_LENGTH = 20;
    
    private static final String TEST_CASE_HR = String.join("", Collections.nCopies(HR_LENGTH, "-"));
    
    private static final String TEST_SUITE_HR = String.join("", Collections.nCopies(HR_LENGTH, "="));

    private static final String PASSED = "\u2713"; // check
    
    private static final String FAILED = "\u274C"; // X
    
    private static final Logger selfLogger = LoggerFactory.getLogger(KeywordLogger.class);
    
    private static final Map<String, KeywordLogger> keywordLoggerLookup = new ConcurrentHashMap<>();
    
    private final Logger logger;
    
    private final XmlKeywordLogger xmlKeywordLogger;
    
    private boolean shouldLogTestSteps;
    
    public static KeywordLogger getInstance(Class<?> clazz) {
        if (clazz == null) { // just in case
            selfLogger.error("Logger name is null. This should be a bug of Katalon Studio.");
            clazz = KeywordLogger.class;
        }
        return getInstance(clazz.getName());
    }

    private static KeywordLogger getInstance(String name) {
        KeywordLogger keywordLogger = keywordLoggerLookup.get(name);
        if (keywordLogger == null) {
            String testCaseName = ScriptEngine.getTestCaseName(name);
            if (testCaseName == null) {
                keywordLogger = new KeywordLogger(name, null);
            } else {
                String fullTestCaseName = "testcase." + testCaseName;
                keywordLogger = new KeywordLogger(fullTestCaseName, null);
            }
            keywordLoggerLookup.put(name, keywordLogger);
        }
        return keywordLogger;
    }
    
    private KeywordLogger(String className, String dummy) {
        logger = LoggerFactory.getLogger(className);
        xmlKeywordLogger = XmlKeywordLogger.getInstance();
        initShouldLogTestSteps();
    }

	private void initShouldLogTestSteps() {
		Map<String, Object> executionProperties = RunConfiguration.getExecutionProperties();
		if (executionProperties == null) {
			shouldLogTestSteps = true;
		} else {
			shouldLogTestSteps = (boolean) Optional
	                .ofNullable(executionProperties.get(RunConfiguration.LOG_TEST_STEPS))
	                .orElse(CoreConstants.DEFAULT_LOG_TEST_STEPS);
		}
	}
    
    private boolean shouldLogTestSteps() {
    	return shouldLogTestSteps;
    }

    public KeywordLogger(String className) {
        
        selfLogger.warn("Please use \"KeywordUtil.logInfo()\" instead of \"new KeywordLogger()\" constructor. \"KeywordLogger\" is an internal API and might be changed in the future.");

        if (StringUtils.isBlank(className)) {
            className = KeywordLogger.class.getName();
        }
        logger = LoggerFactory.getLogger(className);
        xmlKeywordLogger = XmlKeywordLogger.getInstance();
        initShouldLogTestSteps();
    }
    
    public KeywordLogger() {
        this(null);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#close()
     */
    @Override
    public void close() {
        xmlKeywordLogger.close();
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#getLogFolderPath()
     */
    @Override
    public String getLogFolderPath() {
        return xmlKeywordLogger.getLogFolderPath();
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#startSuite(java.lang.String, java.util.Map)
     */
    @Override
    public void startSuite(String name, Map<String, String> attributes) {
        logger.info("START {}", name);
        xmlKeywordLogger.startSuite(name, attributes);
        
        logRunData(RunConfiguration.HOST_NAME, RunConfiguration.getHostName());
        logRunData(RunConfiguration.HOST_OS, RunConfiguration.getOS());
        logRunData(RunConfiguration.HOST_ADDRESS, RunConfiguration.getHostAddress());
        logRunData(RunConfiguration.APP_VERSION, RunConfiguration.getAppVersion());

        RunConfiguration.getCollectedTestDataProperties().entrySet().stream().forEach(collectedDataInfo -> {
            logRunData(collectedDataInfo.getKey(), collectedDataInfo.getValue());
        });
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#endSuite(java.lang.String, java.util.Map)
     */
    @Override
    public void endSuite(String name, Map<String, String> attributes) {
        logger.info(TEST_CASE_HR);
        logger.info("END {}", name);
        logger.info(TEST_SUITE_HR);
        xmlKeywordLogger.endSuite(name, attributes);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#startTest(java.lang.String, java.util.Map, java.util.Stack)
     */
    @Override
    public void startTest(String name, Map<String, String> attributes, Stack<KeywordStackElement> keywordStack) {
        logger.info(TEST_CASE_HR);
        logger.info("START {}", name);
        xmlKeywordLogger.startTest(name, attributes, keywordStack);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#endTest(java.lang.String, java.util.Map)
     */
    @Override
    public void endTest(String name, Map<String, String> attributes) {
        logger.info("END {}", name);
        xmlKeywordLogger.endTest(name, attributes);
    }
    
    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#startCalledTest(java.lang.String, java.util.Map, java.util.Stack)
     */
    @Override
    public void startCalledTest(String name, Map<String, String> attributes, Stack<KeywordStackElement> keywordStack) {
        logger.info(TEST_CASE_HR);
        logger.info("CALL {}", name);
        xmlKeywordLogger.startTest(name, attributes, keywordStack);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#endCalledTest(java.lang.String, java.util.Map)
     */
    @Override
    public void endCalledTest(String name, Map<String, String> attributes) {
        logger.info("END CALL {}", name);
        logger.info(TEST_CASE_HR);
        xmlKeywordLogger.endTest(name, attributes);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#startListenerKeyword(java.lang.String, java.util.Map, java.util.Stack)
     */
    @Override
    public void startListenerKeyword(
            String name, 
            Map<String, String> attributes,
            Stack<KeywordStackElement> keywordStack) {
        
        logStartKeyword(name, attributes);
        xmlKeywordLogger.startListenerKeyword(name, attributes, keywordStack);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#startKeyword(java.lang.String, java.lang.String, java.util.Map, java.util.Stack)
     */
    @Override
    public void startKeyword(
            String name, 
            String actionType, 
            Map<String, String> attributes,
            Stack<KeywordStackElement> keywordStack) {
    	if (shouldLogTestSteps()) {
    		logStartKeyword(name, attributes);
    		xmlKeywordLogger.startKeyword(name, actionType, attributes, keywordStack);
    	}
    }

    private void logStartKeyword(String name, Map<String, String> attributes) {   	
        String stepIndex = getStepIndex(attributes);
        if (stepIndex == null) {
            logger.debug("STEP {}", name);
        } else {
            logger.debug("{}: {}", stepIndex, name);
        }
    }

    private String getStepIndex(Map<String, String> attributes) {
        String stepIndex = null;
        if (attributes != null) {
            stepIndex = attributes.get(StringConstants.XML_LOG_STEP_INDEX);
        }
        return stepIndex;
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#startKeyword(java.lang.String, java.util.Map, java.util.Stack)
     */
    @Override
    public void startKeyword(String name, Map<String, String> attributes, Stack<KeywordStackElement> keywordStack) {
    	
    	if (shouldLogTestSteps()) {
	        logStartKeyword(name, attributes);
	        xmlKeywordLogger.startKeyword(name, attributes, keywordStack);
    	}
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#startKeyword(java.lang.String, java.util.Map, int)
     */
    @Override
    public void startKeyword(String name, Map<String, String> attributes, int nestedLevel) {
    	
    	if (shouldLogTestSteps()) {
	        logStartKeyword(name, attributes);
	        xmlKeywordLogger.startKeyword(name, attributes, nestedLevel);
    	}
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#endKeyword(java.lang.String, java.util.Map, int)
     */
    @Override
    public void endKeyword(String name, Map<String, String> attributes, int nestedLevel) {
    	
    	if (shouldLogTestSteps()) {
	        logEndKeyword(name, attributes);
	        xmlKeywordLogger.endKeyword(name, attributes, nestedLevel);
    	}
    }

    private void logEndKeyword(String name, Map<String, String> attributes) {
        String stepIndex = getStepIndex(attributes);
        if (stepIndex == null) {
            logger.trace("END {}: {}", stepIndex, name);
        } else {
            logger.trace("END STEP {}", name);
        }
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#endListenerKeyword(java.lang.String, java.util.Map, java.util.Stack)
     */
    @Override
    public void endListenerKeyword(
            String name, 
            Map<String, String> attributes,
            Stack<KeywordStackElement> keywordStack) {
        logEndKeyword(name, attributes);
        xmlKeywordLogger.endListenerKeyword(name, attributes, keywordStack);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#endKeyword(java.lang.String, java.lang.String, java.util.Map, java.util.Stack)
     */
    @Override
    public void endKeyword(
            String name, 
            String keywordType, 
            Map<String, String> attributes,
            Stack<KeywordStackElement> keywordStack) {
    	
    	if (shouldLogTestSteps()) {
	        logEndKeyword(name, attributes);
	        xmlKeywordLogger.endKeyword(name, keywordType, attributes, keywordStack);
    	}
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#endKeyword(java.lang.String, java.util.Map, java.util.Stack)
     */
    @Override
    public void endKeyword(String name, Map<String, String> attributes, Stack<KeywordStackElement> keywordStack) {
    	
    	if (shouldLogTestSteps()) {
	        logEndKeyword(name, attributes);
	        xmlKeywordLogger.endKeyword(name, attributes, keywordStack);
    	}
    }
    
    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logFailed(java.lang.String)
     */
    @Override
    public void logFailed(String message) {
        logFailed(message, null);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logFailed(java.lang.String, java.util.Map, java.lang.Throwable)
     */
    @Override
    public void logFailed(String message, Map<String, String> attributes, Throwable throwable) {
        if (attributes == null) {
            attributes = new HashMap<>();
        } else {
            attributes = new HashMap<>(attributes);
        }
        Map<String, String> exceptionAttributes = xmlKeywordLogger.getAttributesFrom(throwable);
        attributes.putAll(exceptionAttributes);
        logFailed(message, attributes);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logFailed(java.lang.String, java.util.Map)
     */
    @Override
    public void logFailed(String message, Map<String, String> attributes) {
        logger.error("{} {}", FAILED, message);
        xmlKeywordLogger.logFailed(message, attributes);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logWarning(java.lang.String)
     */
    @Override
    public void logWarning(String message) {
        logWarning(message, null);
    }
    
    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logWarning(java.lang.String, java.util.Map, java.lang.Throwable)
     */
    @Override
    public void logWarning(String message, Map<String, String> attributes, Throwable throwable) {
        if (attributes == null) {
            attributes = new HashMap<>();
        } else {
            attributes = new HashMap<>(attributes);
        }
        Map<String, String> exceptionAttributes = xmlKeywordLogger.getAttributesFrom(throwable);
        attributes.putAll(exceptionAttributes);
        logWarning(message, attributes);
    }
    
    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logWarning(java.lang.String, java.util.Map, java.lang.Throwable, boolean)
     */
    @Override
    public void logWarning(String message, Map<String, String> attributes, Throwable throwable, boolean isKeyword) {
        if (isKeyword && !shouldLogTestSteps()) {
            return;
        }
        logWarning(message, attributes, throwable);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logWarning(java.lang.String, java.util.Map)
     */
    @Override
    public void logWarning(String message, Map<String, String> attributes) {
        logger.warn(message);
        xmlKeywordLogger.logWarning(message, attributes);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logPassed(java.lang.String)
     */
    @Override
    public void logPassed(String message) {
        logPassed(message, null);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logPassed(java.lang.String, java.util.Map)
     */
    @Override
    public void logPassed(String message, Map<String, String> attributes) {
        logger.debug("{} {}", PASSED, message);
        xmlKeywordLogger.logPassed(message, attributes);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logPassed(java.lang.String, java.util.Map, boolean)
     */
    @Override
    public void logPassed(String message, Map<String, String> attributes, boolean isKeyword) {
        if (isKeyword && !shouldLogTestSteps()) {
            return;
        }
        logPassed(message, attributes);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logInfo(java.lang.String)
     */
    @Override
    public void logInfo(String message) {
        logInfo(message, null);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logInfo(java.lang.String, java.util.Map)
     */
    @Override
    public void logInfo(String message, Map<String, String> attributes) {
        logger.info(message);
        xmlKeywordLogger.logInfo(this, message, attributes);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logRunData(java.lang.String, java.lang.String)
     */
    @Override
    public void logRunData(String dataKey, String dataValue) {
        logger.info("{} = {}", dataKey, dataValue);
        xmlKeywordLogger.logRunData(dataKey, dataValue);
    }
    
    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logError(java.lang.String, java.util.Map, java.lang.Throwable)
     */
    @Override
    public void logError(String message, Map<String, String> attributes, Throwable throwable) {
        if (attributes == null) {
            attributes = new HashMap<>();
        } else {
            attributes = new HashMap<>(attributes);
        }
        Map<String, String> exceptionAttributes = xmlKeywordLogger.getAttributesFrom(throwable);
        attributes.putAll(exceptionAttributes);
        logError(message, attributes);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logError(java.lang.String, java.util.Map, java.lang.Throwable, boolean)
     */
    @Override
    public void logError(String message, Map<String, String> attributes, Throwable throwable, boolean isKeyword) {
        if (isKeyword && !shouldLogTestSteps()) {
            return;
        }
        logError(message, attributes, throwable);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logError(java.lang.String)
     */
    @Override
    public void logError(String message) {
        logError(message, null);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logError(java.lang.String, java.util.Map)
     */
    @Override
    public void logError(String message, Map<String, String> attributes) {
        logger.error("{} {}", FAILED, message);
        xmlKeywordLogger.logError(message, attributes);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logMessage(com.kms.katalon.core.logging.LogLevel, java.lang.String)
     */
    @Override
    public void logMessage(LogLevel level, String message) {
        logMessage(level, message, new HashMap<>());
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logMessage(com.kms.katalon.core.logging.LogLevel, java.lang.String, java.util.Map)
     */
    @Override
    public void logMessage(LogLevel level, String message, Map<String, String> attributes) {
        log(level, message);
        xmlKeywordLogger.logMessage(this, level, message, attributes);
    }

    private void log(LogLevel level, String message) {
        switch (level) {
            case WARNING:
                logger.warn(message);
                break;
            case NOT_RUN:
                logger.warn("SKIP {}", message);
                break;
            case FAILED:
            case ERROR:
            case ABORTED:
            case INCOMPLETE:
                logger.error("{} {}", FAILED, message);
                break;
            default:
                logger.info(message);
        }
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logMessage(com.kms.katalon.core.logging.LogLevel, java.lang.String, java.lang.Throwable)
     */
    @Override
    public void logMessage(LogLevel level, String message, Throwable thrown) {
        log(level, message);
        xmlKeywordLogger.logMessage(level, message, thrown);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#setPendingDescription(java.lang.String)
     */
    @Override
    public void setPendingDescription(String stepDescription) {
        xmlKeywordLogger.setPendingDescription(stepDescription);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logNotRun(java.lang.String)
     */
    @Override
    public void logNotRun(String message) {
        logNotRun(message, null);
    }


    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logNotRun(java.lang.String, java.util.Map)
     */
    @Override
    public void logNotRun(String message, Map<String, String> attributes) {
        logger.warn("SKIPPED {}", message);
        xmlKeywordLogger.logNotRun(message, attributes);
    }

    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#logDebug(java.lang.String)
     */
    @Override
    public void logDebug(String message) {
        logger.debug(message);
        xmlKeywordLogger.logDebug(this, message, null);
    }
    
    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#isInfoEnabled()
     */
    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }
    
    /* (non-Javadoc)
     * @see com.kms.katalon.core.logging.IKeywordLogger#isDebugEnabled()
     */
    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public static class KeywordStackElement {
        private String keywordName;

        private int nestedLevel;

        public KeywordStackElement(String keywordName, int nestedLevel) {
            this.setKeywordName(keywordName);
            this.setNestedLevel(nestedLevel);
        }

        public String getKeywordName() {
            return keywordName;
        }

        public void setKeywordName(String keywordName) {
            this.keywordName = keywordName;
        }

        public int getNestedLevel() {
            return nestedLevel;
        }

        public void setNestedLevel(int nestedLevel) {
            this.nestedLevel = nestedLevel;
        }
    }
}
