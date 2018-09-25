package com.kms.katalon.core.logging;

import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.ErrorManager;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class SystemConsoleHandler extends ConsoleHandler {

    private static final String ANSI_CONSOLE_ESCAPE = "\u001b[";
    private static final Map<String, String> recordLevelWithPaddingLookup = new ConcurrentHashMap<>();

    public SystemConsoleHandler() {
        super();
        SimpleFormatter formatter = new SimpleFormatter() {

            public String format(LogRecord record) {
                int recordLevel = record.getLevel().intValue();
                
                String color = null;
                String bold = ""; // "1;" means bold
                String colorPrefix = "";
                String colorSuffix = "";
                
                if (recordLevel == LogLevel.PASSED.getValue()) {
                    color = "40"; // green
                } else if (recordLevel == LogLevel.WARNING.getValue() 
                        || recordLevel == LogLevel.FAILED.getValue() 
                        || recordLevel == LogLevel.ERROR.getValue()
                        || recordLevel == LogLevel.ABORTED.getValue()
                        || recordLevel == LogLevel.INCOMPLETE.getValue()) {
                    color = "1"; // red
                } else if (recordLevel == LogLevel.NOT_RUN.getValue()) {
                    color = "6"; // cyan
                } else if (recordLevel == LogLevel.RUN_DATA.getValue()) {
                    color = "12"; // blue
                }
                if (color != null) {
                    colorPrefix = ANSI_CONSOLE_ESCAPE + bold + "38;5;" + color + "m"; // xterm colors https://github.com/sindresorhus/xterm-colors
                    colorSuffix = ANSI_CONSOLE_ESCAPE + "0m";
                }

                String recordLevelWithPadding = getRecordLevelWithPadding(record);
                
                return colorPrefix 
                        + XMLLoggerParser.getRecordDate(record) 
                        + " " 
                        + recordLevelWithPadding 
                        + " : "
                        + record.getMessage() 
                        + colorSuffix 
                        + "\r\n";
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
            if (recordLevel == LogLevel.END.getValue()) {
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
