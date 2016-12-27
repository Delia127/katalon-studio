package com.kms.katalon.objectspy.util;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Platform;

import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinUser.WNDENUMPROC;

public class Win32Helper {
    private static final String CHROME_CLASS_NAME = "Chrome_WidgetWin_1";

    private static final String FIREFOX_CLASS_NAME = "MozillaWindowClass";
    
    public static void switchFocusToBrowser(WebUIDriverType browser) {
        switch (browser) {
            case CHROME_DRIVER:
                switchFocusToChrome();
                break;
            default:
                switchFocusToFirefox();
                break;
            
        }
    }

    public static void switchFocusToChrome() {
        if (isOnWin32()) {
            return;
        }
        switchFocusToWindow(CHROME_CLASS_NAME);
    }
    
    public static void switchFocusToFirefox() {
        if (isOnWin32()) {
            return;
        }
        switchFocusToWindow(FIREFOX_CLASS_NAME);
    }

    private static boolean isOnWin32() {
        return !Platform.getOS().equals(Platform.OS_WIN32);
    }

    private static void switchFocusToWindow(String windowClassName) {
        Pointer foundWindowPointer = new Memory(Pointer.SIZE);
        isWindowOpen(windowClassName, foundWindowPointer);
        if (foundWindowPointer.getPointer(0) != null) {
            activateWindow(new HWND(foundWindowPointer.getPointer(0)));
        }
    }

    public static void main(String[] args) {
        switchFocusToFirefox();
    }

    private static void activateWindow(HWND foundWindow) {
        if (foundWindow == null) {
            return;
        }
        User32.INSTANCE.ShowWindow(foundWindow, 3);
        User32.INSTANCE.SetForegroundWindow(foundWindow);
    }

    private static void isWindowOpen(String windowClassName, Pointer foundWindowPointer) {
        User32.INSTANCE.EnumWindows(new WNDENUMPROC() {
            @Override
            public boolean callback(HWND hWnd, Pointer foundWindowPointer) {
                if (foundWindowPointer == null) {
                    return true;
                }
                String className = getWindowClassName(hWnd);
                String title = getWindowTitle(hWnd);
                if (className.contains(windowClassName) && StringUtils.isNotEmpty(title)) {
                    foundWindowPointer.setPointer(0, hWnd.getPointer());
                }
                return true;

            }
        }, foundWindowPointer);
    }

    private static String getWindowClassName(HWND hWnd) {
        char[] windowText = new char[512];
        User32.INSTANCE.GetClassName(hWnd, windowText, 512);
        return Native.toString(windowText);
    }

    private static String getWindowTitle(HWND hWnd) {
        char[] windowTitle = new char[512];
        User32.INSTANCE.GetWindowText(hWnd, windowTitle, 512);
        return Native.toString(windowTitle);
    }
}
