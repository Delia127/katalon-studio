package com.kms.katalon.composer.webui.recorder.action;

import com.kms.katalon.objectspy.element.HTMLElement;

public class HTMLAction {
	private String actionName;
	private String actionData;
	private HTMLElement targetElement;
	private String windowId;
	
	public HTMLAction(String actionName, HTMLElement targetElement, String actionData) {
		this.actionName = actionName;
		this.targetElement = targetElement;
		this.actionData = actionData;
	}
	
	public String getActionName() {
		return actionName;
	}
	
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	
	public HTMLElement getTargetElement() {
		return targetElement;
	}
	
	public void setTargetElement(HTMLElement targetElement) {
		this.targetElement = targetElement;
	}

	public String getActionData() {
		return actionData;
	}

	public void setActionData(String actionData) {
		this.actionData = actionData;
	}

	public String getWindowId() {
		return windowId;
	}

	public void setWindowId(String windowId) {
		this.windowId = windowId;
	}
}
