package com.kms.katalon.execution.launcher.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.controller.ReportController;
import com.kms.katalon.core.logging.model.TestSuiteLogRecord;
import com.kms.katalon.execution.launcher.IConsoleLauncher;
import com.kms.katalon.execution.launcher.ILauncher;
import com.kms.katalon.execution.launcher.result.LauncherStatus;

import static com.kms.katalon.constants.GlobalStringConstants.CR_HYPHEN;
import static com.kms.katalon.constants.GlobalStringConstants.CR_EOL;

public class LauncherManager {
    private static LauncherManager _instance;
    private List<ILauncher> runningLaunchers;
    private List<ILauncher> waitingLaunchers;
    private List<ILauncher> terminatedLaunchers;
    private List<TestSuiteLogRecord> fileReports;

    protected LauncherManager() {
        runningLaunchers = new ArrayList<ILauncher>();
        waitingLaunchers = new ArrayList<ILauncher>();
        terminatedLaunchers = new ArrayList<ILauncher>();
        fileReports = new ArrayList<TestSuiteLogRecord>();
    }

    public static LauncherManager getInstance() {
        if (_instance == null) {
            _instance = new LauncherManager();
        }
        return _instance;
    }

    public static void refresh() {
        _instance = null;
    }

    public void addLauncher(ILauncher launcher) {
        addLauncherToWaitingList(launcher);
        schedule();
    }

    private void addLauncherToRunningList(ILauncher launcher) {
        runningLaunchers.add(launcher);
        launcher.setStatus(LauncherStatus.RUNNING);
    }

    private void removeLauncherFromRunningList(ILauncher launcher) {
        runningLaunchers.remove(launcher);
    }

    private void addLauncherToWaitingList(ILauncher launcher) {
        waitingLaunchers.add(launcher);
        launcher.setStatus(LauncherStatus.WAITING);
    }

    private void removeLauncherFromWaitingList(ILauncher launcher) {
        waitingLaunchers.remove(launcher);
    }

    private void addLauncherToTerminatedList(ILauncher launcher) {
    	removeAllTerminatedBut(5);
        terminatedLaunchers.add(0, launcher);
    }

    public void removeAllTerminated() {
        for (ILauncher launcher : terminatedLaunchers) {
            launcher.clean();
        }
        terminatedLaunchers.clear();
    }
    
    public void removeAllTerminatedBut(int n){
    	if(terminatedLaunchers.size() < n - 1){
    		return;
    	}
    	
        for (int i = terminatedLaunchers.size() - 1; i >= n - 1; i--) {
        	terminatedLaunchers.get(i).clean();
        	terminatedLaunchers.remove(i);
        }
       
    }
    
    public List<ILauncher> getAllLaunchers() {
        List<ILauncher> launchers = new ArrayList<ILauncher>();
        launchers.addAll(runningLaunchers);
        launchers.addAll(waitingLaunchers);
        launchers.addAll(terminatedLaunchers);
        return launchers;
    }
    
    public ILauncher getLauncher(String id) {
        for (ILauncher currentLauncher : getAllLaunchers()) {
            if (currentLauncher.getId().equals(id)) {
                return currentLauncher;
            }
        }
        return null;
    }

    public List<ILauncher> getSortedLaunchers() {
        List<ILauncher> launchers = getAllLaunchers();
        Collections.sort(launchers, new Comparator<ILauncher>() {
            @Override
            public int compare(ILauncher launcher1, ILauncher launcher2) {
                try {
                    String lcFolderName1 = launcher1.getId();
                    String lcFolderName2 = launcher2.getId();
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
        return terminatedLaunchers.size() != 0;
    }

    // Let all the launcher run parallel for now
    protected boolean isLauncherReadyToRun(ILauncher launcher) {
        // if (runningLaunchers.size() > 0)
        // return false;

        // for (AbstractLauncher runningLauncher : runningLaunchers) {
        // String driverType = runningLauncher.getRunConfiguration().getPropertyMap()
        // .get(DriverFactory.EXECUTED_BROWSER_PROPERTY);
        // if (driverType == null) {
        // if (runningLauncher.getRunConfiguration().getPropertyMap()
        // .get(MobileDriverFactory.EXECUTED_DEVICE_NAME) != null
        // && !runningLauncher.getRunConfiguration().getPropertyMap()
        // .get(MobileDriverFactory.EXECUTED_DEVICE_NAME).equals("")) {
        // return false;
        // }
        // } else if (driverType.equals(launcher.getRunConfiguration().getPropertyMap()
        // .get(DriverFactory.EXECUTED_BROWSER_PROPERTY))) {
        // return false;
        // }
        // }
        return true;
    }

    public void stopLauncher(ILauncher launcher) {
        if (launcher.getStatus() == LauncherStatus.RUNNING) {
            launcher.stop();
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
            if (runningLaunchers.get(index).getStatus() != LauncherStatus.TERMINATED) {
                runningLaunchers.get(index).stop();
            } else {
                index++;
            }
        }
    }

    public synchronized void stopRunningAndSchedule(ILauncher launcher) throws InterruptedException {
        removeLauncherFromRunningList(launcher);
        addLauncherToTerminatedList(launcher);
        // Thread.sleep(3000);
        schedule();
    }

    private void stopWaiting(ILauncher launcher) {
        launcher.stop();
        removeLauncherFromWaitingList(launcher);
        addLauncherToTerminatedList(launcher);
    }

    protected void schedule() {
        int index = 0;
        while (index < waitingLaunchers.size()) {
            ILauncher launcher = waitingLaunchers.get(index);
            if (isLauncherReadyToRun(launcher)) {
                removeLauncherFromWaitingList(launcher);
                addLauncherToRunningList(launcher);
                try {
                    launcher.start();
                } catch (IOException e) {
                    
                }
            } else {
                index++;
            }
        }
    }

    public ILauncher getLauncherInRunningList(String launcherId) throws Exception {
        for (ILauncher runningLauncher : runningLaunchers) {
            if (runningLauncher.getId().equals(launcherId)) {
                return runningLauncher;
            }
        }
        return null;
    }
    
    public List<ILauncher> getRunningLaunchers() {
        return runningLaunchers;
    }

    public String getStatus(int consoleWidth) {
        return new StringBuilder()
                .append(CR_EOL)
                .append(StringUtils.repeat(CR_HYPHEN, consoleWidth))
                .append(CR_EOL)
                .append(getChildrenLauncherStatus(consoleWidth))
                .append(CR_EOL)
                .append(StringUtils.repeat(CR_HYPHEN, consoleWidth))
                .append(CR_EOL).toString();
    }

    protected String getChildrenLauncherStatus(int consoleWidth) {
        StringBuilder launcherMessageStatus = new StringBuilder();
        for (ILauncher launcher : getSortedLaunchers()) {
            if (!(launcher instanceof IConsoleLauncher)) {
                continue;
            }
            if (StringUtils.isNotEmpty(launcherMessageStatus.toString())) {
                launcherMessageStatus.append(CR_EOL);
            }
            IConsoleLauncher consoleLauncher = (IConsoleLauncher) launcher;
            launcherMessageStatus.append(consoleLauncher.getStatusMessage(consoleWidth));
        }
        return launcherMessageStatus.toString();
    }
    
    public long getWaitingLaunchers() {
    	return waitingLaunchers.size();
    }
    
    public void addFolderReport(TestSuiteLogRecord report) {
    	fileReports.add(report);
    }
    
    public List<TestSuiteLogRecord> getFolderReport() {
    	return fileReports;
    }
}
