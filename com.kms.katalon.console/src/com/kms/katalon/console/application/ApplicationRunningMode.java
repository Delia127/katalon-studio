package com.kms.katalon.console.application;

public class ApplicationRunningMode {
	public enum RunningMode {
		GUI, Console
	}

	private static ApplicationRunningMode _instance;
	private RunningMode runnningMode;
	private String[] runArguments;

	private ApplicationRunningMode() {
		runnningMode = RunningMode.GUI;
	}

	public static ApplicationRunningMode getInstance() {
		if (_instance == null) {
			_instance = new ApplicationRunningMode();
		}
		return _instance;
	}

	public RunningMode getRunnningMode() {
		return runnningMode;
	}

	public void setRunnningMode(RunningMode runnningMode) {
		this.runnningMode = runnningMode;
	}

	public String[] getRunArguments() {
		return runArguments;
	}

	public void setRunArguments(String[] runArguments) {
		this.runArguments = runArguments;
	}
}
