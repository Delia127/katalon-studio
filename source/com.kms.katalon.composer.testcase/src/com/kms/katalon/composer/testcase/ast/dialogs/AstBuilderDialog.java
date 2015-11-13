package com.kms.katalon.composer.testcase.ast.dialogs;


public interface AstBuilderDialog {
	public void changeObject(Object originalObject, Object newObject);
	public Object getReturnValue();
	public int open();
	public void refresh();
	public String getDialogTitle();
}
