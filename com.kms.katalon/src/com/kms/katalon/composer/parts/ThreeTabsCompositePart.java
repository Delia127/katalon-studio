package com.kms.katalon.composer.parts;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public interface ThreeTabsCompositePart {
	public MPart getChildManualPart();
	public MPart getChildCompatibilityPart();
	public MPart getChildVariablesPart();
	public void save() throws Exception;
}
