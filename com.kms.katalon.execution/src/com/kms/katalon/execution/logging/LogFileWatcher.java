package com.kms.katalon.execution.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.eclipse.e4.core.services.log.Logger;

import com.kms.katalon.core.logging.XMLLoggerParser;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.execution.launcher.IDELauncher;

@SuppressWarnings("restriction")
public class LogFileWatcher extends AbstractLogFileWatcher {
	private Logger systemLogger;
	private IDELauncher launcher;

	public LogFileWatcher(File file, int delay, Logger systemLogger, IDELauncher launcher)
			throws FileNotFoundException {
		super(file, delay);
		this.systemLogger = systemLogger;
		this.launcher = launcher;
	}

	@Override
	public void run() {
		try {
			while (!stopSignal) {
				StringBuilder builder = new StringBuilder();
				while (true) {
					Thread.sleep(delay);
					String line = reader.readLine();
					if (line == null) {
						if (!builder.toString().isEmpty()) {
							List<XmlLogRecord> records = XMLLoggerParser.parseLogString(prepareString(builder));
							launcher.addRecords(records);
						}
						break;
					}
					if (isLineQualifier(line)) {
						builder.append(LINE_SEPERATOR + line);
					}
					lineCount++;
				}
			}

		} catch (Exception e) {
			systemLogger.error(e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				systemLogger.error(e);
			}
		}
	}
}
