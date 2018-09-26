package com.kms.katalon.core.logging;

import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.ErrorManager;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

public class SystemConsoleHandler extends ConsoleHandler {
    
    private static final Map<String, String> recordLevelWithPaddingLookup = new ConcurrentHashMap<>();
    
    private static final Map<Integer, Color> foregroundColorLookup = new ConcurrentHashMap<>();

    public SystemConsoleHandler() {
        super();
        SimpleFormatter formatter = new SimpleFormatter() {

            public String format(LogRecord record) {
                
                Color foregroundColor = getForegroundColor(record);
                
                String recordLevelWithPadding = getRecordLevelWithPadding(record);
                
                String prologue = XMLLoggerParser.getRecordDate(record) 
                        + " " 
                        + recordLevelWithPadding 
                        + " : ";
                if (foregroundColor != null) {
                    prologue = ansi().fg(foregroundColor).a(prologue).reset().toString(); 
                }
                return prologue + record.getMessage() + "\r\n";
            }

            private Color getForegroundColor(LogRecord record) {
                
                int recordLevel = record.getLevel().intValue();
                Color foregroundColor = foregroundColorLookup.get(recordLevel);
                
                if (foregroundColor == null) {

                    if (recordLevel == LogLevel.PASSED.getValue()) {
                        foregroundColor = GREEN; // green
                    } else if (recordLevel == LogLevel.WARNING.getValue() 
                            || recordLevel == LogLevel.FAILED.getValue() 
                            || recordLevel == LogLevel.ERROR.getValue()
                            || recordLevel == LogLevel.ABORTED.getValue()
                            || recordLevel == LogLevel.INCOMPLETE.getValue()) {
                        foregroundColor = RED; // red
                    } else if (recordLevel == LogLevel.NOT_RUN.getValue()) {
                        foregroundColor = CYAN; // cyan
                    } else if (recordLevel == LogLevel.RUN_DATA.getValue()) {
                        foregroundColor = BLUE; // blue
                    }
                   
                    if (foregroundColor != null) {
                        foregroundColorLookup.put(recordLevel, foregroundColor);
                    }
                }
                return foregroundColor;
            }

            private String getRecordLevelWithPadding(LogRecord record) {

                String originalRecordLevel = record.getLevel().toString();
                String recordLevelWithPadding = recordLevelWithPaddingLookup.get(originalRecordLevel);
                if (recordLevelWithPadding == null) {
                    String recordLevel = originalRecordLevel;
                    String recordPadding = "";
                    int longestRecordLevelLength = LogLevel.RUN_DATA.toString().length(); // INCOMPLETE never gets logged
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
            if (recordLevel != LogLevel.RUN_DATA.getValue() 
                    && recordLevel >= LogLevel.WARNING.getValue()) {
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
