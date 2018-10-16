package com.kms.katalon.core.logging;

import java.util.Map;
import java.util.Stack;

public interface IKeywordLogger {

    void close();

    String getLogFolderPath();

    void startSuite(String name, Map<String, String> attributes);

    void endSuite(String name, Map<String, String> attributes);

    void startTest(String name, Map<String, String> attributes, Stack<KeywordStackElement> keywordStack);

    void endTest(String name, Map<String, String> attributes);

    void startListenerKeyword(String name, Map<String, String> attributes, Stack<KeywordStackElement> keywordStack);

    void startKeyword(String name, String actionType, Map<String, String> attributes,
            Stack<KeywordStackElement> keywordStack);

    void startKeyword(String name, Map<String, String> attributes, Stack<KeywordStackElement> keywordStack);

    void startKeyword(String name, Map<String, String> attributes, int nestedLevel);

    void endKeyword(String name, Map<String, String> attributes, int nestedLevel);

    void endListenerKeyword(String name, Map<String, String> attributes, Stack<KeywordStackElement> keywordStack);

    void endKeyword(String name, String keywordType, Map<String, String> attributes,
            Stack<KeywordStackElement> keywordStack);

    void endKeyword(String name, Map<String, String> attributes, Stack<KeywordStackElement> keywordStack);

    void logFailed(String message);

    void logFailed(String message, Map<String, String> attributes);

    void logWarning(String message);

    void logWarning(String message, Map<String, String> attributes);

    void logPassed(String message);

    void logPassed(String message, Map<String, String> attributes);

    void logInfo(String message);

    void logInfo(String message, Map<String, String> attributes);

    void logRunData(String dataKey, String dataValue);

    void logError(String message);

    void logError(String message, Map<String, String> attributes);

    void logMessage(LogLevel level, String message);

    void logMessage(LogLevel level, String message, Map<String, String> attributes);

    void logMessage(LogLevel level, String message, Throwable thrown);

    void setPendingDescription(String stepDescription);

    void logNotRun(String message);

    void logNotRun(String message, Map<String, String> attributes);
    
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

}