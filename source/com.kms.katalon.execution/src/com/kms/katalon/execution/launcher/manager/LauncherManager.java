package com.kms.katalon.execution.launcher.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.runtime.CoreException;

import com.kms.katalon.controller.ReportController;
import com.kms.katalon.execution.launcher.AbstractLauncher;
import com.kms.katalon.execution.launcher.CustomGroovyScriptLaunchShortcut;
import com.kms.katalon.execution.launcher.model.LauncherStatus;

public class LauncherManager {
	private static LauncherManager _instance;
	private List<AbstractLauncher> runningLaunchers;
	private List<AbstractLauncher> waitingLaunchers;
	private List<AbstractLauncher> teminatedLaunchers;

	private LauncherManager() {
		runningLaunchers = new ArrayList<AbstractLauncher>();
		waitingLaunchers = new ArrayList<AbstractLauncher>();
		teminatedLaunchers = new ArrayList<AbstractLauncher>();
	}

	public static LauncherManager getInstance() throws CoreException {
		if (_instance == null) {
			_instance = new LauncherManager();
			CustomGroovyScriptLaunchShortcut.cleanAllConfigurations();
		}
		return _instance;
	}

	public static void refresh() {
		_instance = null;
	}

	public void addLauncher(AbstractLauncher launcher) {
		addLauncherToWaitingList(launcher);
		schedule();
	}

	private void addLauncherToRunningList(AbstractLauncher launcher) {
		runningLaunchers.add(launcher);
		launcher.setStatus(LauncherStatus.RUNNING);
	}

	private void removeLauncherFromRunningList(AbstractLauncher launcher) {
		runningLaunchers.remove(launcher);
	}

	private void addLauncherToWaitingList(AbstractLauncher launcher) {
		waitingLaunchers.add(launcher);
		launcher.setStatus(LauncherStatus.WAITING);
	}

	private void removeLauncherFromWaitingList(AbstractLauncher launcher) {
		waitingLaunchers.remove(launcher);
	}

	private void addLauncherToTerminatedList(AbstractLauncher launcher) {
		teminatedLaunchers.add(0, launcher);
		if (launcher.isForcedStop()) {
			launcher.setStatus(LauncherStatus.TERMINATED);
		} else {
			launcher.setStatus(LauncherStatus.DONE);
		}
	}

	public void removeAllTerminated() {
		for (AbstractLauncher launcher : teminatedLaunchers) {
			launcher.cleanLauncher();
		}
		teminatedLaunchers.clear();
	}

	public List<AbstractLauncher> getIDELaunchers() {
		List<AbstractLauncher> launchers = new ArrayList<AbstractLauncher>();
		launchers.addAll(runningLaunchers);
		launchers.addAll(waitingLaunchers);
		launchers.addAll(teminatedLaunchers);
		return launchers;
	}

	public List<AbstractLauncher> getConsoleLaunchers() {
		List<AbstractLauncher> launchers = getIDELaunchers();
		Collections.sort(launchers, new Comparator<AbstractLauncher>() {
			@Override
			public int compare(AbstractLauncher launcher1, AbstractLauncher launcher2) {
				try {
					String lcFolderName1 = FilenameUtils.getBaseName(new File(launcher1.getRunConfiguration()
							.getLogFilePath()).getParentFile().toString());
					String lcFolderName2 = FilenameUtils.getBaseName(new File(launcher2.getRunConfiguration()
							.getLogFilePath()).getParentFile().toString());
					Date lcDate1 = ReportController.getInstance().getDateFromReportFolderName(lcFolderName1);
					Date lcDate2 = ReportController.getInstance().getDateFromReportFolderName(lcFolderName2);
					return lcDate1.after(lcDate2) ? 1 : -1;
				} catch (Exception e) {
					return 1;
				}

			}
		});
		return launchers;
	}

	public boolean isAnyLauncherRunning() {
		return (runningLaunchers.size() + waitingLaunchers.size()) != 0;
	}

	public boolean isAnyLauncherTerminated() {
		return teminatedLaunchers.size() != 0;
	}

	private boolean isLauncherReadyToRun(AbstractLauncher launcher) {
		if (runningLaunchers.size() > 0)
			return false;

//		for (AbstractLauncher runningLauncher : runningLaunchers) {
//			String driverType = runningLauncher.getRunConfiguration().getPropertyMap()
//					.get(DriverFactory.EXECUTED_BROWSER_PROPERTY);
//			if (driverType == null) {
//				if (runningLauncher.getRunConfiguration().getPropertyMap()
//						.get(MobileDriverFactory.EXECUTED_DEVICE_NAME) != null
//						&& !runningLauncher.getRunConfiguration().getPropertyMap()
//								.get(MobileDriverFactory.EXECUTED_DEVICE_NAME).equals("")) {
//					return false;
//				}
//			} else if (driverType.equals(launcher.getRunConfiguration().getPropertyMap()
//					.get(DriverFactory.EXECUTED_BROWSER_PROPERTY))) {
//				return false;
//			}
//		}
		return true;
	}

	public void stopLauncher(AbstractLauncher launcher) {
		if (launcher.getStatus() == LauncherStatus.RUNNING) {
			launcher.forceStop();
		} else if (launcher.getStatus() == LauncherStatus.WAITING) {
			stopWaiting(launcher);
		}
	}

	public synchronized void stopAllLauncher() {
		while (waitingLaunchers.size() > 0) {
			stopWaiting(waitingLaunchers.get(0));
		}

		int index = 0;
		while (runningLaunchers.size() > index) {
			if (!runningLaunchers.get(index).isForcedStop()) {
				runningLaunchers.get(index).forceStop();
			} else {
				index++;
			}
		}
	}

	public synchronized void stopRunningAndSchedule(AbstractLauncher launcher) throws InterruptedException {
		removeLauncherFromRunningList(launcher);
		addLauncherToTerminatedList(launcher);
		// Thread.sleep(3000);
		schedule();
	}

	private void stopWaiting(AbstractLauncher launcher) {
		launcher.forceStop();
		removeLauncherFromWaitingList(launcher);
		addLauncherToTerminatedList(launcher);
	}

	private void schedule() {
		int index = 0;
		while (index < waitingLaunchers.size()) {
			AbstractLauncher launcher = waitingLaunchers.get(index);
			if (isLauncherReadyToRun(launcher)) {
				removeLauncherFromWaitingList(launcher);
				addLauncherToRunningList(launcher);
				launcher.execute();
			} else {
				index++;
			}
		}
	}

	public AbstractLauncher getLauncherInRunningList(String launcherId) throws Exception {
		for (AbstractLauncher runningLauncher : runningLaunchers) {
			if (runningLauncher.getId().equals(launcherId)) {
				return runningLauncher;
			}
		}
		return null;
	}
}
