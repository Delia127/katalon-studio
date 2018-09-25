package com.kms.katalon.core.logging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.ErrorManager;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class SystemConsoleHandler extends ConsoleHandler {

    private static final Map<String, String> recordLevelWithPaddingLookup = new ConcurrentHashMap<>();

    public SystemConsoleHandler() {
        super();
        SimpleFormatter formatter = new SimpleFormatter() {

            public String format(LogRecord record) {
                String recordLevelWithPadding = getRecordLevelWithPadding(record);

                return XMLLoggerParser.getRecordDate(record) + " " + recordLevelWithPadding + " : "
                        + record.getMessage() + "\r\n";
            }

            private String getRecordLevelWithPadding(LogRecord record) {
                String originalRecordLevel = record.getLevel().toString();
                String recordLevelWithPadding = recordLevelWithPaddingLookup.get(originalRecordLevel);
                if (recordLevelWithPadding == null) {
                    String recordLevel = originalRecordLevel;
                    String recordPadding = "";
                    int longestRecordLevelLength = LogLevel.INCOMPLETE.toString().length();
                    for (int i = 0; i < longestRecordLevelLength - recordLevel.length(); i++) {
                        recordPadding += " ";
                    }
                    recordPadding += " ";
                    recordLevelWithPadding = recordLevel + recordPadding;
                    recordLevelWithPaddingLookup.put(originalRecordLevel, recordLevelWithPadding);
                }
                return recordLevelWithPadding;
            }
        };
        setFormatter(formatter);
    }

    @Override
    public synchronized void publish(LogRecord record) {
        try {
            String message = getFormatter().format(record);
            int recordLevel = record.getLevel().intValue();
            if (recordLevel == LogLevel.START.getValue()
                    || recordLevel == LogLevel.END.getValue()) {
            } else if (recordLevel >= LogLevel.WARNING.getValue()) {
                System.err.write(message.getBytes());
            } else {
                System.out.write(message.getBytes());
            }
        } catch (Exception exception) {
            reportError(null, exception, ErrorManager.FORMAT_FAILURE);
            return;
        }
        flush();
    }
}
