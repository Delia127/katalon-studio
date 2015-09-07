package com.kms.katalon.execution.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.kms.katalon.core.logging.XMLLoggerParser;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.execution.launcher.ConsoleLauncher;

public class ConsoleLogFileWatcher extends AbstractLogFileWatcher {
	
	private ConsoleLauncher launcher;

	public ConsoleLogFileWatcher(File file, int delay, ConsoleLauncher launcher) throws FileNotFoundException {
		super(file, delay);
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
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
