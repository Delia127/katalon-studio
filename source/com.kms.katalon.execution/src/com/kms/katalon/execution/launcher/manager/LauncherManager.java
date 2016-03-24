package com.kms.katalon.execution.launcher.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.kms.katalon.controller.ReportController;
import com.kms.katalon.execution.launcher.ILauncher;
import com.kms.katalon.execution.launcher.model.LauncherStatus;

public class LauncherManager {
    private static LauncherManager _instance;
    private List<ILauncher> runningLaunchers;
    private List<ILauncher> waitingLaunchers;
    private List<ILauncher> teminatedLaunchers;

    private LauncherManager() {
        runningLaunchers = new ArrayList<ILauncher>();
        waitingLaunchers = new ArrayList<ILauncher>();
        teminatedLaunchers = new ArrayList<ILauncher>();
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
        teminatedLaunchers.add(0, launcher);
    }

    public void removeAllTerminated() {
        for (ILauncher launcher : teminatedLaunchers) {
            launcher.clean();
        }
        teminatedLaunchers.clear();
    }
    
    public List<ILauncher> getAllLaunchers() {
        List<ILauncher> launchers = new ArrayList<ILauncher>();
        launchers.addAll(runningLaunchers);
        launchers.addAll(waitingLaunchers);
        launchers.addAll(teminatedLaunchers);
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
                    String lcFolderName1 = launcher1.getRunConfig().getExecutionSetting().getFolderPath();
                    String lcFolderName2 = launcher2.getRunConfig().getExecutionSetting().getFolderPath();
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

    // Let all the launcher run parallel for now
    private boolean isLauncherReadyToRun(ILauncher launcher) {
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

    private void schedule() {
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
}
