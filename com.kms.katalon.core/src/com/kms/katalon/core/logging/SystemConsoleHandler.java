package com.kms.katalon.core.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.ErrorManager;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class SystemConsoleHandler extends ConsoleHandler {
	public SystemConsoleHandler() {
		super();
		SimpleFormatter formatter = new SimpleFormatter() {
			public String format(LogRecord record) {
				String recordLevel = " - [" + record.getLevel() + "] ";
				String recordAppend = "";
				for (int i = 0; i < 12 - recordLevel.length(); i++) {
					recordAppend += " ";
				}
				recordAppend += "- ";

				return XMLLoggerParser.getRecordDate(record) + recordLevel + recordAppend
						+ XMLLoggerParser.unescapeString(record.getMessage()) + "\r\n";
			}
		};
		setFormatter(formatter);
	}

	@Override
	public void publish(LogRecord record) {
		try {
			String message = getFormatter().format(record);
			if (record.getLevel().intValue() >= LogLevel.WARNING.intValue()) {
				System.err.write(message.getBytes());
			} else {
				System.out.write(message.getBytes());
			}
		} catch (Exception exception) {
			reportError(null, exception, ErrorManager.FORMAT_FAILURE);
			return;
		}

	}
}
