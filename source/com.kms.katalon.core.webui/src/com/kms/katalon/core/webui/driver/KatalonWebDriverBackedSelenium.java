package com.kms.katalon.core.webui.driver;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;

import com.thoughtworks.selenium.webdriven.ElementFinder;
import com.thoughtworks.selenium.webdriven.JavascriptLibrary;
import com.thoughtworks.selenium.webdriven.WebDriverBackedSelenium;

import groovy.lang.Closure;

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
	
	@SuppressWarnings("deprecation")
	public void andWait() {
		super.waitForPageToLoad(WAIT_FOR_PAGE_TO_LOAD_IN_SECONDS);
	}
	
	public void waitFor(Closure<Boolean> callable) {
		try {
			for (int second = 0;; second++) {
				Boolean satisfied = callable.call();
				if (second >= 60) Assert.fail("timeout");
				try {
					if (satisfied)
						break;
				} catch (Exception e) {
				}
				Thread.sleep(1000);
			}
		} catch (Exception e) {
		}
	}
}
