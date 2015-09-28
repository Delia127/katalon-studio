package com.kms.katalon.composer.components.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public static String getElapsedTime(long startTime, long endTime) {
		if (endTime < startTime) {
			return "";
		}
		long totalMillis = endTime - startTime;
		long totalSeconds = totalMillis / 1000;
		long totalMinutes = totalSeconds / 60;

		int elapsedMillis = (int) (totalMillis % 1000);
		int elapsedSeconds = (int) (totalSeconds % 60);
		int elapsedMinutes = (int) (totalMinutes % 60);
		int elapsedHours = (int) (totalMinutes / 60);

		return ((elapsedHours > 0) ? Integer.toString(elapsedHours) + "h - " : "")
				+ ((elapsedMinutes > 0) ? Integer.toString(elapsedMinutes) + "m - " : "")
				+ Integer.toString(elapsedSeconds) + "." + Integer.toString(elapsedMillis) + "s";
	}
	
	public static String getDateTimeFormatted(long timeValue) {
		Date date = new Date(timeValue);
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df2.format(date);
	}
}
