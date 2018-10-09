package com.kms.katalon.core.logging;

import static org.fusesource.jansi.Ansi.ansi;
import static org.fusesource.jansi.Ansi.Color.BLUE;
import static org.fusesource.jansi.Ansi.Color.CYAN;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.ErrorManager;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

import org.fusesource.jansi.Ansi.Color;

import com.kms.katalon.core.constants.StringConstants;

public class SystemConsoleHandler extends ConsoleHandler {
    
    private static final int LOG_START_ACTION_PREFIX_LENGTH = StringConstants.LOG_START_ACTION_PREFIX.length();

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
                String message = record.getMessage();
                if (message.startsWith(StringConstants.LOG_START_ACTION_PREFIX)) {
                    message = message.substring(LOG_START_ACTION_PREFIX_LENGTH);
                }
                return prologue + message + "\n";
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
            if (recordLevel == LogLevel.END.getValue()) {
                
            } else if (recordLevel != LogLevel.RUN_DATA.getValue() 
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
