package com.kms.katalon.composer.components.impl.control;

import org.eclipse.swt.widgets.Composite;

public class FocusableComposite extends Composite {
	public FocusableComposite(Composite parent, int style) {
		super(parent, style);
	}

	public boolean setFocus() {
		return super.forceFocus();
	}
}
