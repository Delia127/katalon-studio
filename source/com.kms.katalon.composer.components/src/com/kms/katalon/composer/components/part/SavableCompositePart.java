package com.kms.katalon.composer.components.part;

import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public interface SavableCompositePart {
	public List<MPart> getChildParts();
	public void save() throws Exception;
}
