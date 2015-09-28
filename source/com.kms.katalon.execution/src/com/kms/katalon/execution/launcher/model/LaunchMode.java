package com.kms.katalon.execution.launcher.model;

public enum LaunchMode {
	RUN("run"), DEBUG("debug");
	
	private final String text;

	private LaunchMode(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
