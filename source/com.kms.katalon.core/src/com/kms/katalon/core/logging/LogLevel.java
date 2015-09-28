package com.kms.katalon.core.logging;

import java.util.logging.Level;

import com.kms.katalon.core.constants.StringConstants;

public class LogLevel extends Level {

	private static final long serialVersionUID = 6700699007644941984L;

	public static final LogLevel START = new LogLevel(StringConstants.LOG_LVL_START, 1002);
	public static final LogLevel END = new LogLevel(StringConstants.LOG_LVL_END, 1003);

	public static final LogLevel PASSED = new LogLevel(StringConstants.LOG_LVL_PASSED, 1000);
	public static final LogLevel INFO = new LogLevel(StringConstants.LOG_LVL_INFO, 1001);

	public static final LogLevel WARNING = new LogLevel(StringConstants.LOG_LVL_WARNING, 1004);
	public static final LogLevel FAILED = new LogLevel(StringConstants.LOG_LVL_FAILED, 1005);
	public static final LogLevel ERROR = new LogLevel(StringConstants.LOG_LVL_ERROR, 1006);
	public static final LogLevel ABORTED = new LogLevel(StringConstants.LOG_LVL_ABORTED, 1009);
	public static final LogLevel INCOMPLETE = new LogLevel(StringConstants.LOG_LVL_INCOMPLETE, 1010);

	protected LogLevel(String arg0, int arg1) {
		super(arg0, arg1);
	}

	public static Level parse(String levelString) {
		switch (levelString) {
		case (StringConstants.LOG_LVL_PASSED):
			return PASSED;
		case (StringConstants.LOG_LVL_ERROR):
			return ERROR;
		case (StringConstants.LOG_LVL_INFO):
			return INFO;
		case (StringConstants.LOG_LVL_WARNING):
			return WARNING;
		case (StringConstants.LOG_LVL_FAILED):
			return FAILED;
		case (StringConstants.LOG_LVL_ABORTED):
			return ABORTED;
		case (StringConstants.LOG_LVL_START):
			return START;
		case (StringConstants.LOG_LVL_END):
			return END;
		case (StringConstants.LOG_LVL_INCOMPLETE):
			return INCOMPLETE;
		}
		return null;
	}
}
