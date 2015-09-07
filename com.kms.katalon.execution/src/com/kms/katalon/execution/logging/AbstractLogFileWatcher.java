package com.kms.katalon.execution.logging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public abstract class AbstractLogFileWatcher implements Runnable {
	protected static final String[] IGNORED_LIST = new String[] { "<log>", "</log>", 
		"<?xml version=\"1.0\" encoding=\"windows-1252\" standalone=\"no\"?>", 
		"<!DOCTYPE log SYSTEM \"logger.dtd\">" };
	//protected static final int[] IGNORED_LINE_NUMBER = new int[] { 0, 1 };

	protected static final String LINE_SEPERATOR = System.getProperty("line.separator");
	
	protected String header;
	protected int lineCount;
	protected int delay;

	protected BufferedReader reader;
	protected FileInputStream fis;
	protected InputStreamReader is;
	protected boolean stopSignal;

	public AbstractLogFileWatcher(File file, int delay) throws FileNotFoundException {
		this.delay = delay;

		this.fis = new FileInputStream(file);
		this.is = new InputStreamReader(fis);
		this.reader = new BufferedReader(is);

		this.lineCount = 0;
		this.header = "";
		this.stopSignal = false;
	}
	
	protected boolean isLineQualifier(String line) {
		return verifyLineNotInIgnoredListString(line);// && verifyLineNotInIgnoredListInt(line);
	}

	protected boolean verifyLineNotInIgnoredListString(String line) {
		for (String ignoredString : IGNORED_LIST) {
			if (line.equals(ignoredString)) {
				return false;
			}
		}
		return true;
	}

	/*protected boolean verifyLineNotInIgnoredListInt(String line) {
		for (int ignoredLine : IGNORED_LINE_NUMBER) {
			if (lineCount == ignoredLine) {
				if (ignoredLine == 0) {
					header = line;
				}
				return false;
			}
		}
		return true;
	}
	*/

	protected String prepareString(StringBuilder builder) {
		return builder.insert(0, header + IGNORED_LIST[0]).append(IGNORED_LIST[1]).toString();
	}

	public boolean isStopSignal() {
		return stopSignal;
	}

	public void setStopSignal(boolean stopSignal) {
		this.stopSignal = stopSignal;
	}
}
