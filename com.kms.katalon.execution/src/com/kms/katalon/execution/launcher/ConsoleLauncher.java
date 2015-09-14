package com.kms.katalon.execution.launcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.RuntimeProcess;

import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.controller.TestCaseController;
import com.kms.katalon.controller.TestSuiteController;
import com.kms.katalon.core.logging.LogLevel;
import com.kms.katalon.core.logging.XmlLogRecord;
import com.kms.katalon.core.logging.XmlLogRecordException;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.core.reporting.ReportUtil;
import com.kms.katalon.core.util.PathUtils;
import com.kms.katalon.entity.project.ProjectEntity;
import com.kms.katalon.entity.testcase.TestCaseEntity;
import com.kms.katalon.entity.testsuite.TestSuiteEntity;
import com.kms.katalon.execution.constants.StringConstants;
import com.kms.katalon.execution.entity.IRunConfiguration;
import com.kms.katalon.execution.entity.TestSuiteExecutedEntity;
import com.kms.katalon.execution.generator.TestSuiteScriptGenerator;
import com.kms.katalon.execution.launcher.manager.ConsoleMain;
import com.kms.katalon.execution.launcher.manager.LauncherManager;
import com.kms.katalon.execution.launcher.model.LaunchMode;
import com.kms.katalon.execution.logging.ConsoleLogFileWatcher;
import com.kms.katalon.execution.logging.LogExceptionFilter;
import com.kms.katalon.groovy.util.GroovyUtil;

public class ConsoleLauncher extends AbstractLauncher {
	public ConsoleLauncher(IRunConfiguration runConfig) {
		super(runConfig);
	}

	protected static IFile generateTempTestSuiteScript(TestSuiteEntity testSuite, IRunConfiguration config,
			TestSuiteExecutedEntity testSuiteExecutedEntity) throws Exception {
		if (testSuite != null) {
			File tempTestSuiteFile = new TestSuiteScriptGenerator(testSuite, config, testSuiteExecutedEntity)
					.generateScriptFile();
			return GroovyUtil.getTempScriptIFile(tempTestSuiteFile, testSuite.getProject());
		}
		return null;
	}

	public void launch(TestSuiteEntity testSuite, TestSuiteExecutedEntity testSuiteExecutedEntity) throws Exception {
		if (testSuite != null) {
			executedEntity = testSuite;
			this.testSuiteExecutedEntity = testSuiteExecutedEntity;
			scriptFile = generateTempTestSuiteScript(testSuite, runConfig, testSuiteExecutedEntity);
			LauncherManager.getInstance().addLauncher(this);
		}
	}

	private CustomGroovyScriptLaunchShortcut executeScript(ProjectEntity project, IFile testSuiteScript)
			throws Exception {
		System.out.println(MessageFormat.format(StringConstants.LAU_PRT_LAUNCHING_X, getDisplayID()));
		CustomGroovyScriptLaunchShortcut launchShortcut = getLauncher();
		launchShortcut.launch(scriptFile, project, LaunchMode.RUN);
		String name = FilenameUtils.getBaseName(scriptFile.getName());

		while (launch == null) {
			for (ILaunch launch : CustomGroovyScriptLaunchShortcut.getLaunchManager().getLaunches()) {
				if (launch.getLaunchConfiguration() != null) {
					if (launch.getLaunchConfiguration().getName().equals(name)) {
						this.launch = launch;
					}
				}
			}
		}
		return launchShortcut;
	}

	public String getDisplayID() throws Exception {
		return TestSuiteController.getInstance().getIdForDisplay((TestSuiteEntity) executedEntity) + " - "
				+ runConfig.getName();
	}

	private void terminateProcess() throws DebugException {
		if (launch.getProcesses() != null && launch.getProcesses().length > 0) {
			RuntimeProcess process = (RuntimeProcess) launch.getProcesses()[0];
			if (process.canTerminate()) {
				process.terminate();
			}
		}
	}

	private void handleExecutionEvents(final File logFile, final TestSuiteEntity testSuite, final IFile scriptFile)
			throws FileNotFoundException {
		logRecords = new ArrayList<XmlLogRecord>();

		final Thread threadWatcher = new Thread(new ConsoleLogFileWatcher(logFile, 1, this));
		threadWatcher.start();

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean terminated = false;
				while (true) {
					if (stopSignal && !terminated) {
						try {
							terminateProcess();
							terminated = true;
						} catch (DebugException e) {
							System.out.println(e.getMessage());
						}
					}

					if (launch.getProcesses() != null && launch.getProcesses().length > 0
							&& launch.getProcesses()[0].isTerminated()) {
						terminated = true;
					}

					if (terminated) {
						try {
							long currentTime = System.currentTimeMillis();
							while (System.currentTimeMillis() - currentTime < 30000) {
								try {
									updateLastRun(testSuite, logFile);
									break;
								} catch (Exception e) {
									// Concurrency modifier exception
								}
							}

							System.out.println(MessageFormat.format(StringConstants.LAU_PRT_X_DONE, getDisplayID(),
									launcherResult.toString()));

							// For report summary
							prepareReport(testSuite, logFile);

							stopAndSchedule();

						} catch (Exception e) {
							System.out.println(e.getMessage());
						}
						return;
					}
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// Do nothing
					}
				}
			}
		});
		thread.start();
	}

	private boolean prepareReport(TestSuiteEntity testSuite, File logFile) {
		if (testSuite != null) {
			try {
				File testSuiteReportSourceFolder = logFile.getParentFile();
				File htmlFile = new File(testSuiteReportSourceFolder,
						FilenameUtils.getBaseName(testSuiteReportSourceFolder.getName()) + ".html");
				TestSuiteLogRecord suiteLog = ReportUtil.generate(testSuiteReportSourceFolder.getAbsolutePath());
				// Generate HTML file if it does not exist.
				if (!htmlFile.exists()) {
					ReportUtil.writeLogRecordToFiles(suiteLog, testSuiteReportSourceFolder);
				}

				System.out.println(StringConstants.LAU_PRT_SENDING_RPT_TO_INTEGRATING_PRODUCTS);
				uploadReportToIntegratingProduct(suiteLog);
				System.out.println(StringConstants.LAU_PRT_REPORT_SENT);

				// report folder that is set by user.
				File userReportFolder = getUserReportFolder(testSuite);

				if (userReportFolder != null && htmlFile.exists()) {
					System.out.println(StringConstants.LAU_PRT_COPYING_RPT_TO_USR_RPT_FOLDER);
					System.out.println(MessageFormat.format(StringConstants.LAU_PRT_USR_REPORT_FOLDER_X,
							userReportFolder.getAbsolutePath()));
					System.out.println(StringConstants.LAU_PRT_CLEANING_USR_RPT_FOLDER);

					cleanUserReportFolder(testSuite);

					for (File reportChildSourceFile : testSuiteReportSourceFolder.listFiles()) {
						String fileName = FilenameUtils.getBaseName(reportChildSourceFile.getName());
						String fileExtension = FilenameUtils.getExtension(reportChildSourceFile.getName());

						// ignore LOCK file
						if (fileExtension.equalsIgnoreCase("lck"))
							continue;

						// Rename .csv, .log and .html file to user's format
						if ((ConsoleMain.getReportFileName() != null)
								&& (fileExtension.equals("csv") || fileExtension.equals("log") || fileExtension
										.equals("html"))) {
							fileName = ConsoleMain.getReportFileName();
						}

						// Copy child file to user's report folder
						FileUtils.copyFile(reportChildSourceFile, new File(userReportFolder, fileName + "."
								+ fileExtension));
					}
					System.out.println(StringConstants.LAU_PRT_REPORT_COPIED);
				}
			} catch (Exception ex) {
				System.out.println(MessageFormat.format(StringConstants.LAU_PRT_CANNOT_CREATE_REPORT_FOLDER,
						ex.getMessage()));
			}

			try {
				sendReportEmail(testSuite, logFile);
				return true;
			} catch (Exception e) {
				System.out.println(MessageFormat.format(StringConstants.LAU_PRT_CANNOT_SEND_EMAIL, e.getMessage()));
			}
		}
		return false;
	}

	private void cleanUserReportFolder(TestSuiteEntity testSuite) throws IOException {
		FileUtils.cleanDirectory(getUserReportFolder(testSuite));
	}

	private File getUserReportFolder(TestSuiteEntity testSuite) {
		if (testSuiteExecutedEntity.getReportFolderPath() == null)
			return null;
		try {
			File reportFolder = new File(PathUtils.relativeToAbsolutePath(
					testSuiteExecutedEntity.getReportFolderPath(), testSuite.getProject().getFolderLocation()));

			if (reportFolder != null && !reportFolder.exists()) {
				reportFolder.mkdirs();
			}
			return reportFolder;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void execute() {
		try {
			executeScript(ProjectController.getInstance().getCurrentProject(), scriptFile);
			handleExecutionEvents(getCurrentLogFile(), (TestSuiteEntity) executedEntity, scriptFile);
		} catch (Exception e) {
			System.out.println(StringConstants.LAU_PRT_CANNOT_EXECUTE_TEST_SUITE);
		}
	}

	@Override
	public List<XmlLogRecord> getAllRecords() {
		return logRecords;
	}

	/**
	 * Only print line number of the failed step. Users don't want to see the
	 * test case that the line number belongs to.
	 * 
	 * @author Tuan Nguyen Manh
	 * @param record
	 * @throws Exception
	 */
	private synchronized void printErrorLineLogs(XmlLogRecord record) throws Exception {

		if ((record.getLevel() == LogLevel.FAILED || record.getLevel() == LogLevel.ERROR)
				&& record.getExceptions() != null) {
			for (XmlLogRecordException logRecordException : record.getExceptions()) {
				if (!LogExceptionFilter.isTraceableException(logRecordException))
					continue;
				if (LogExceptionFilter.isTestCaseScript(logRecordException.getClassName())) {
					TestCaseEntity testCase = LogExceptionFilter.getTestCaseByLogException(logRecordException);
					if (testCase != null) {
						System.out.println(record);
						System.err.println(MessageFormat.format(StringConstants.LAU_PRT_X_FAILED_AT_LINE_Y,
								TestCaseController.getInstance().getIdForDisplay(testCase),
								logRecordException.getLineNumber()));
						continue;
					}
				}
				System.out.println(record);
				System.err.println(MessageFormat.format(StringConstants.LAU_PRT_FAILED_AT_LINE_X,
						logRecordException.toString()));
				break;
			}
		} else {
			System.out.println(record);
		}
	}

	@Override
	public void addRecords(List<XmlLogRecord> records) {
		synchronized (this) {
			try {
				for (XmlLogRecord record : records) {
					if (logDepth == 2) {
						logRecords.add(record);
					}
					printErrorLineLogs(record);

					if (record.getLevel().equals(LogLevel.END)) {
						logDepth--;
						if (record.getSourceMethodName().equals(
								com.kms.katalon.core.constants.StringConstants.LOG_END_TEST_METHOD)) {
							if (logDepth == 0 || logDepth == 1) {
								XmlLogRecord resultRecord = logRecords.get(logRecords.size() - 2);
								if (resultRecord.getLevel() == LogLevel.PASSED) {
									launcherResult.increasePasses();
								} else {
									if (resultRecord.getLevel() == LogLevel.FAILED) {
										launcherResult.increaseFailures();
									} else if (resultRecord.getLevel() == LogLevel.ERROR) {
										launcherResult.increaseErrors();
									}
								}

								logRecords.clear();
							}
						}

						if (logDepth == 0) {
							stopSignal = true;
						}

					} else if (record.getLevel().equals(LogLevel.START)) {
						logDepth++;
					} else if (record.getLevel().equals(LogLevel.ERROR)) {
						if (logDepth == 1) {
							launcherResult.increaseErrors();
						}
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	@Override
	protected void deleteScriptFile() {
		try {
			scriptFile.delete(true, null);
			IFolder libFolder = GroovyUtil.getCustomKeywordLibFolder(ProjectController.getInstance()
					.getCurrentProject());
			libFolder.refreshLocal(IResource.DEPTH_ONE, null);
		} catch (Exception e) {
			System.out.println(StringConstants.LAU_PRT_CANNOT_CLEAN_TEMP_FILES);
		}
	}
}
