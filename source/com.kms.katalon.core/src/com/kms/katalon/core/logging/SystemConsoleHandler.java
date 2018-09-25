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
    
    private static final String ANSI_CONSOLE_RESET = ANSI_CONSOLE_ESCAPE + "0m";
    
    private static final Map<String, String> recordLevelWithPaddingLookup = new ConcurrentHashMap<>();
    
    private static final Map<Integer, String> foregroundColorPrefixLookup = new ConcurrentHashMap<>();

    public SystemConsoleHandler() {
        super();
        SimpleFormatter formatter = new SimpleFormatter() {

            public String format(LogRecord record) {
                
                String foregroundColorPrefix = getForegroundColor(record);
                
                String foregroundColorSuffix = "";
                if (foregroundColorPrefix != null) {
                    foregroundColorSuffix = ANSI_CONSOLE_RESET;
                }

                String recordLevelWithPadding = getRecordLevelWithPadding(record);
                
                String message = record.getMessage()
                        .replaceAll("\r\n", "\n")
                        .replaceAll("\n", foregroundColorSuffix + "\r\n" + foregroundColorPrefix);
                
                return foregroundColorPrefix
                        + XMLLoggerParser.getRecordDate(record) 
                        + " " 
                        + recordLevelWithPadding 
                        + " : "
                        + message
                        + foregroundColorSuffix 
                        + "\r\n";
            }

            private String getForegroundColor(LogRecord record) {
                
                int recordLevel = record.getLevel().intValue();
                String foregroundColorPrefix = foregroundColorPrefixLookup.get(recordLevel);
                
                if (foregroundColorPrefix == null) {

                    String foregroundColor = null;
                    
                    if (recordLevel == LogLevel.PASSED.getValue()) {
                        foregroundColor = "2"; // green
                    } else if (recordLevel == LogLevel.WARNING.getValue() 
                            || recordLevel == LogLevel.FAILED.getValue() 
                            || recordLevel == LogLevel.ERROR.getValue()
                            || recordLevel == LogLevel.ABORTED.getValue()
                            || recordLevel == LogLevel.INCOMPLETE.getValue()) {
                        foregroundColor = "9"; // red
                    } else if (recordLevel == LogLevel.NOT_RUN.getValue()) {
                        foregroundColor = "6"; // cyan
                    } else if (recordLevel == LogLevel.RUN_DATA.getValue()) {
                        foregroundColor = "12"; // blue
                    }
                    if (foregroundColor == null) {
                        foregroundColorPrefix = "";
                    } else {
                        foregroundColorPrefix = ANSI_CONSOLE_ESCAPE + "38;5;" + foregroundColor + "m"; // xterm colors https://github.com/sindresorhus/xterm-colors
                    }
                    
                    foregroundColorPrefixLookup.put(recordLevel, foregroundColorPrefix);
                }
                return foregroundColorPrefix;
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
