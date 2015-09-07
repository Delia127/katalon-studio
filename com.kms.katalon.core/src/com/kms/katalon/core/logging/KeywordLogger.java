package com.kms.katalon.core.logging;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.constants.StringConstants;

public class KeywordLogger {
	private String logFilePath;
	private Logger logger;
	private static KeywordLogger _instance;
	private static String pendingDescription = null;
	private Stack<KeywordStackElement> currentKeywordStack = null;
	private Stack<Stack<KeywordStackElement>> keywordStacksContainer = new Stack<Stack<KeywordStackElement>>();
	private int nestedLevel;

	public class KeywordStackElement {
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

	public static KeywordLogger getInstance() {
		if (_instance == null) {
			_instance = new KeywordLogger();
		}
		return _instance;
	}

	private KeywordLogger() {
	}

	/**
	 * @return Returns current logger if it exists. Otherwise, create a new one
	 *         includes: customized log file with XML format and customized
	 *         console handler.
	 */
	private Logger getLogger() {
		if (logger == null) {
			logger = Logger.getLogger(KeywordLogger.class.getName());

			// remove default parent's setting
			logger.setUseParentHandlers(false);
		}

		if (logger.getHandlers().length == 0 && getLogFilePath() != null) {
			try {
				String logFolder = new File(getLogFilePath()).getParent();

				// Split log into 100 files, every file is maximum 100MB
				// 100MB for a log file maybe too large???
				FileHandler fileHandler = new FileHandler(logFolder + File.separator + "execution%g.log",
						100 * 1024 * 1024 * 1024, 100, true);
				logger.addHandler(fileHandler);

				CustomXmlFormatter formatter = new CustomXmlFormatter();
				fileHandler.setFormatter(formatter);

				SystemConsoleHandler consoleHandler = new SystemConsoleHandler();
				logger.addHandler(consoleHandler);

			} catch (SecurityException | IOException e) {
				System.err.println("Unable to create logger. Root cause (" + e.getMessage() + ").");
			}
		}
		return logger;
	}

	public static void cleanUp() {
		if (_instance != null) {
			_instance.logFilePath = null;
			_instance.logger = null;
			_instance = null;
		}
	}

	public String getLogFilePath() {
		if (logFilePath == null) {
			logFilePath = RunConfiguration.getLogFilePath();
		}
		return logFilePath;
	}

	public void startSuite(String name, Map<String, String> attributes) {
		getLogger().log(
				new XmlLogRecord(LogLevel.START, StringConstants.LOG_START_SUITE + " : " + name, nestedLevel,
						attributes));
	}

	public void endSuite(String name, Map<String, String> attributes) {
		getLogger().log(
				new XmlLogRecord(LogLevel.END, StringConstants.LOG_END_SUITE + " : " + name, nestedLevel, attributes));
	}

	public void startTest(String name, Map<String, String> attributes, Stack<KeywordStackElement> keywordStack,
			boolean isOptional) {
		nestedLevel++;
		getLogger()
				.log(new XmlLogRecord(LogLevel.START, StringConstants.LOG_START_TEST + " : " + name, nestedLevel,
						attributes));
		if (currentKeywordStack != null) {
			keywordStacksContainer.push(currentKeywordStack);
		}
		this.currentKeywordStack = keywordStack;
	}

	public void endTest(String name, Map<String, String> attributes) {
		nestedLevel--;
		getLogger().log(
				new XmlLogRecord(LogLevel.END, StringConstants.LOG_END_TEST + " : " + name, nestedLevel, attributes));

		if (!keywordStacksContainer.isEmpty()) {
			currentKeywordStack = keywordStacksContainer.pop();
		} else {
			currentKeywordStack = null;
		}
	}

	public void startKeyword(String name, Map<String, String> attributes, int nestedLevel) {
		if (attributes == null) {
			attributes = new HashMap<String, String>();
		}
		if (pendingDescription != null) {
			attributes.put(StringConstants.XML_LOG_DESCRIPTION_PROPERTY, pendingDescription);
			pendingDescription = null;
		}
		popKeywordFromStack(nestedLevel);
		getLogger().log(
				new XmlLogRecord(LogLevel.START, StringConstants.LOG_START_KEYWORD + " : " + name, nestedLevel,
						attributes));
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

	public void endKeyword(String name, Map<String, String> attributes, int nestedLevel) {
		getLogger()
				.log(new XmlLogRecord(LogLevel.END, StringConstants.LOG_END_KEYWORD + " : " + name, nestedLevel,
						attributes));
	}

	public void logFailed(String message) {
		logMessage(LogLevel.FAILED, message);
	}

	public void logFailed(String message, Map<String, String> attributes) {
		logMessage(LogLevel.FAILED, message, attributes);
	}

	public void logWarning(String message) {
		logMessage(LogLevel.WARNING, message);
	}

	public void logWarning(String message, Map<String, String> attributes) {
		logMessage(LogLevel.WARNING, message, attributes);
	}

	public void logPassed(String message) {
		logMessage(LogLevel.PASSED, message);
	}

	public void logPassed(String message, Map<String, String> attributes) {
		logMessage(LogLevel.PASSED, message, attributes);
	}

	public void logInfo(String message) {
		logMessage(LogLevel.INFO, message);
	}

	public void logInfo(String message, Map<String, String> attributes) {
		logMessage(LogLevel.INFO, message, attributes);
	}

	public void logError(String message) {
		logMessage(LogLevel.ERROR, message);
	}

	public void logError(String message, Map<String, String> attributes) {
		logMessage(LogLevel.ERROR, message, attributes);
	}

	public void logMessage(LogLevel level, String message) {
		if (message == null) {
			message = "";
		}
		Logger logger = getLogger();
		if (logger != null) {
			logger.log(new XmlLogRecord(level, message, nestedLevel));
		}
	}

	public void logMessage(LogLevel level, String message, Map<String, String> attributes) {
		if (message == null) {
			message = "";
		}
		Logger logger = getLogger();
		if (logger != null) {
			logger.log(new XmlLogRecord(level, message, nestedLevel, attributes));
		}
	}

	public void logMessage(LogLevel level, String message, Throwable thrown) {
		if (message == null) {
			message = "";
		}
		Logger logger = getLogger();
		if (logger != null) {
			logger.log(level, message, thrown);
		}
	}

	public static void setPendingDescription(String stepDescription) {
		KeywordLogger.pendingDescription = stepDescription;
	}
}
