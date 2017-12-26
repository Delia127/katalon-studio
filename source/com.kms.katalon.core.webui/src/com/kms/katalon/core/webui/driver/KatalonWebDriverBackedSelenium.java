package com.kms.katalon.core.webui.driver;

import org.openqa.selenium.WebDriver;

import com.thoughtworks.selenium.webdriven.ElementFinder;
import com.thoughtworks.selenium.webdriven.JavascriptLibrary;
import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;

public class KatalonWebDriverBackedSelenium extends WebDriverBackedSelenium {

	public static final String WAIT_FOR_PAGE_TO_LOAD_IN_SECONDS = "30000";
	
	private JavascriptLibrary javascriptLibrary;
	private ElementFinder elementFinder;

	public KatalonWebDriverBackedSelenium(WebDriver maker, String baseUrl) {
		super(maker, baseUrl);
		
		javascriptLibrary = new JavascriptLibrary();
		elementFinder = new ElementFinder(javascriptLibrary);
	}

	public KatalonWebDriverBackedSelenium(String baseUrl) {
		this(DriverFactory.getWebDriver(), baseUrl);
	}
	
	public void chooseCancelOnNextPrompt() {
		this.getWrappedDriver().switchTo().alert().dismiss();
	}
	
	public void sendKeys(String locator, String value) {
		elementFinder.findElement(this.getWrappedDriver(), locator).sendKeys(value);
	}
	
}
