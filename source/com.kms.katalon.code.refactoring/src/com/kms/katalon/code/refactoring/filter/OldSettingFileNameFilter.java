package com.kms.katalon.code.refactoring.filter;

import java.io.File;
import java.io.FilenameFilter;

public class OldSettingFileNameFilter implements FilenameFilter {

	/**
	 * Accepts only property file has name that contains <code>qautomate<code> only
	 */
	@Override
	public boolean accept(File dir, String name) {
		if (dir == null || name == null) return false;
		return name.contains("qautomate");
	}

}
