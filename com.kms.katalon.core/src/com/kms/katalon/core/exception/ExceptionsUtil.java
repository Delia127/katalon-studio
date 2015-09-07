package com.kms.katalon.core.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionsUtil {
	public static String getMessageForThrowable(Throwable t) {
		if (t == null) {
			return "";
		}
		StringBuilder message = null;

		if (!(t instanceof StepFailedException)) {
			message = new StringBuilder(t.getClass().getName()
					+ (t.getMessage() != null ? (": " + t.getMessage()) : ""));
			StringWriter sw = new StringWriter();
			t.printStackTrace(new PrintWriter(sw));
			message.append(" (Stack trace: " + sw.toString() + ")");
		} else {
			message = new StringBuilder(t.getMessage() != null ? (t.getMessage()) : "");
		}
		if (t.getCause() != null) {
			message.append(" (caused by: ");
			message.append(getMessageForThrowable(t.getCause()));
			message.append(")");
		}
		return message.toString();
	}
}
