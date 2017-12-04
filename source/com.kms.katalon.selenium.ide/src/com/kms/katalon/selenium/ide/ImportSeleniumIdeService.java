package com.kms.katalon.selenium.ide;

import java.io.File;

import com.kms.katalon.selenium.ide.model.TestSuite;

public final class ImportSeleniumIdeService {

	private static final ImportSeleniumIdeService INSTANCE = new ImportSeleniumIdeService();
	
	public static ImportSeleniumIdeService getInstance() {
        return INSTANCE;
    }
	
	public TestSuite parseTestSuite(File file) {
		return HtmlParser.parseTestSuite(file);
	}
	
}
