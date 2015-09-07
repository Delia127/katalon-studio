package com.kms.katalon.composer.testcase.model;

public class ContentProposalCheck {
	public ContentProposalCheck() {
		isProposing = false;
	}

	private boolean isProposing;

	public boolean isProposing() {
		return isProposing;
	}

	public void setProposing(boolean isProposing) {
		this.isProposing = isProposing;
	}
}