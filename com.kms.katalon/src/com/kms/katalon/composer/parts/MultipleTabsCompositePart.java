package com.kms.katalon.composer.parts;

import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

public interface MultipleTabsCompositePart {
	public List<MPart> getChildParts();
	public void save() throws Exception;
}
