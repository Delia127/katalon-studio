package com.kms.katalon.composer.windows.element;

import java.util.List;

import org.openqa.selenium.WebElement;

import io.appium.java_client.windows.WindowsDriver;

public interface TreeWindowsElement extends BasicWindowsElement {

    String getOptinalName();
    
    TreeWindowsElement getParent();

    List<? extends TreeWindowsElement> getChildren();

    CapturedWindowsElement getCapturedElement();

    CapturedWindowsElement newCapturedElement(WindowsDriver<WebElement> windowsDriver);

    void setCapturedElement(CapturedWindowsElement object);

    TreeWindowsElement findBestMatch(CapturedWindowsElement needToVerify);
}
