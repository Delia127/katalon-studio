package com.kms.katalon.core.util;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.prefs.CsvPreference;

import com.kms.katalon.core.logging.model.ILogRecord;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.logging.model.TestStatus.TestStatusValue;

public class CsvWriter {

	public static void writeCsvReport(TestSuiteLogRecord suiteLog, File file) throws Exception {

		final String[] header = new String[] { "Suite/Test/Step Name", "Browser", "Start time", "End time", "Duration",
				"Status" };

		final CellProcessor[] processors = new CellProcessor[] { new NotNull(), // Test
																				// Name
				new NotNull(), // Browser
				new NotNull(), // Start time
				new NotNull(), // End Time
				new NotNull(), // Duration
				new NotNull(), // Status
		};

		ICsvListWriter csvWriter = new CsvListWriter(new FileWriter(file), CsvPreference.STANDARD_PREFERENCE);

		SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss.SSS");

		try {
			// Test Suite
			String browser = suiteLog.getBrowser();
			List<Object> suiteObject = Arrays.asList(new Object[] { suiteLog.getName(), browser,
					sdf.format(new Date(suiteLog.getStartTime())), sdf.format(new Date(suiteLog.getEndTime())),
					elapsedTime(suiteLog.getEndTime() - suiteLog.getStartTime()),
					suiteLog.getStatus().getStatusValue().toString() });
			csvWriter.writeHeader(header);
			csvWriter.write(suiteObject, processors);

			// Test cases
			for (ILogRecord testLog : suiteLog.getChildRecords()) {
				String testName = testLog.getName();
				// Blank line
				csvWriter.write(Arrays.asList(new Object[] { "", "", "", "", "", "" }), processors);
				// Write test case line
				List<Object> testObject = Arrays.asList(new Object[] { testName, browser,
						sdf.format(new Date(testLog.getStartTime())), sdf.format(new Date(testLog.getEndTime())),
						elapsedTime(testLog.getEndTime() - testLog.getStartTime()),
						testLog.getStatus().getStatusValue() + "" });
				csvWriter.write(testObject, processors);

				// Test steps
				for (ILogRecord step : testLog.getChildRecords()) {
					String stepName = step.getName();
					TestStatusValue stepStat = step.getStatus() == null ? TestStatusValue.NOT_RUN : step.getStatus()
							.getStatusValue();
					List<Object> stepObject = Arrays.asList(new Object[] { stepName, browser,
							sdf.format(new Date(step.getStartTime())), sdf.format(new Date(step.getEndTime())),
							elapsedTime(step.getEndTime() - step.getStartTime()), stepStat + "" });
					csvWriter.write(stepObject, processors);
				}
			}
		} catch (Exception ex) {
			if (csvWriter != null) {
				csvWriter.close();
			}
			throw ex;
		}
		csvWriter.close();
	}

	public static void writeArraysToCsv(List<Object[]> datas, File csvFile) throws Exception {
		// final String[] header = new String[] { "Suite/Test Name",
		// "Start time", "End time", "Duration", "Status"};
		final String[] header = new String[] { "Suite Name", "Test Name", "Browser", "Start time", "End time",
				"Duration", "Status" };
		final CellProcessor[] processors = new CellProcessor[] { new NotNull(), // Suite
																				// Name
				new NotNull(), // Test Name
				new NotNull(), // Browser
				new NotNull(), // Start time
				new NotNull(), // End Time
				new NotNull(), // Duration
				new NotNull(), // Status
		};
		ICsvListWriter csvWriter = new CsvListWriter(new FileWriter(csvFile), CsvPreference.STANDARD_PREFERENCE);
		csvWriter.writeHeader(header);
		for (Object[] arr : datas) {
			csvWriter.write(Arrays.asList(arr), processors);
		}
		csvWriter.close();
	}

	private static String elapsedTime(long duration) {
		long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
		long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
		long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);
		long diffInMiliSecs = duration - diffInSeconds * 1000;
		return String.format(" %s:%s:%s.%s", diffInHours, diffInMinutes, diffInSeconds, diffInMiliSecs);
	}

}
