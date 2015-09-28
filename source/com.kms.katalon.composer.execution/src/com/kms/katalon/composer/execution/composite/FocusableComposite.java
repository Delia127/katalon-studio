package com.kms.katalon.composer.execution.composite;

import org.eclipse.swt.widgets.Composite;

public class FocusableComposite extends Composite {
	public FocusableComposite(Composite parent, int style) {
		super(parent, style);
	}

	public boolean setFocus() {
		return super.forceFocus();
	}
}
