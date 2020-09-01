package com.kms.katalon.core.webui.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import com.kms.katalon.core.exception.StepFailedException;
import com.kms.katalon.core.testobject.SelectorMethod;
import com.kms.katalon.core.testobject.TestObject;
import com.kms.katalon.core.util.internal.TestOpsUtil;
import com.kms.katalon.core.webui.common.WebUiCommonHelper;
import com.kms.katalon.core.webui.driver.DriverFactory;

import com.assertthat.selenium_shutterbug.core.Capture;
import com.assertthat.selenium_shutterbug.core.Shutterbug;
import com.google.common.io.Files;

public class FileUtil {

    private static final String SCREENSHOT_FOLDER = "resources/screen";

    private static final String KMS_IE_DRIVER_FOLDER = "resources/drivers/kmsie";

    private static final String AUTHENTICATION_FOLDER = "resources/authentication";

    private static final String EXTENSIONS_FOLDER_NAME = "resources/extensions";

    public static String takesScreenshot(String fileName, boolean isTestOpsVisionCheckPoint)
            throws IOException, WebDriverException, StepFailedException {
        if (isTestOpsVisionCheckPoint && StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        String savedFileName = isTestOpsVisionCheckPoint ? TestOpsUtil.replaceTestOpsVisionFileName(fileName)
                : fileName;
        File savedFile = new File(savedFileName);
        WebDriver driver = DriverFactory.getWebDriver();
        Shutterbug.shootPage(driver, Capture.VIEWPORT).withName(Files.getNameWithoutExtension(savedFileName)).save(savedFile.getParent());
        return savedFileName;
    }

    public static String takeFullPageScreenshot(String fileName, List<TestObject> ignoredElements,
            boolean isTestOpsVisionCheckPoint) throws IOException {
        if (isTestOpsVisionCheckPoint && StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        String savedFileName = isTestOpsVisionCheckPoint ? TestOpsUtil.replaceTestOpsVisionFileName(fileName)
                : fileName;
        WebDriver driver = DriverFactory.getWebDriver();
        Map<String, String> states = hideElements(driver, ignoredElements);
        File savedFile = new File(savedFileName);
        Shutterbug.shootPage(driver, Capture.FULL_SCROLL).withName(Files.getNameWithoutExtension(savedFileName)).save(savedFile.getParent());
        restoreElements(driver, states);
        return savedFileName;
    }

    public static String takeElementScreenshot(String fileName, TestObject element, boolean isTestOpsVisionCheckPoint)
            throws IOException, NoSuchElementException {
        if (isTestOpsVisionCheckPoint && StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        if (element == null) {
            throw new IllegalArgumentException("Captured element cannot be null");
        }

        String savedFileName = isTestOpsVisionCheckPoint ? TestOpsUtil.replaceTestOpsVisionFileName(fileName)
                : fileName;
        WebDriver driver = DriverFactory.getWebDriver();
        WebElement capturedElement = driver
                .findElement(By.cssSelector(WebUiCommonHelper.getSelectorValue(element, SelectorMethod.CSS)));
        File savedFile = new File(savedFileName);
        Shutterbug.shootElement(driver, capturedElement).withName(Files.getNameWithoutExtension(savedFileName)).save(savedFile.getParent());
        return savedFileName;
    }

    public static String takeAreaScreenshot(String fileName, Rectangle rect, boolean isTestOpsVisionCheckPoint)
            throws IOException, IllegalArgumentException {
        if (isTestOpsVisionCheckPoint && StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }
        if (rect == null) {
            throw new IllegalArgumentException("Captured area cannot be null");
        }

        String savedFileName = isTestOpsVisionCheckPoint ? TestOpsUtil.replaceTestOpsVisionFileName(fileName)
                : fileName;
        WebDriver driver = DriverFactory.getWebDriver();
        BufferedImage image = Shutterbug.shootPage(driver, Capture.VIEWPORT).getImage();
        if ((rect.x + rect.width) > image.getWidth() || (rect.y + rect.height) > image.getHeight()) {
            throw new IllegalArgumentException("Captured area is larger than actual viewport");
        }

        ImageIO.write(image.getSubimage(rect.x, rect.y, rect.width, rect.height), TestOpsUtil.DEFAULT_IMAGE_EXTENSION,
                TestOpsUtil.ensureDirectory(new File(savedFileName), true));
        return savedFileName;
    }

    private static Map<String, String> hideElements(WebDriver driver, List<TestObject> testObjects) {
        if (testObjects == null || driver == null) {
            return null;
        }

        Map<String, String> preState = new HashMap<>();
        testObjects.stream().map(o -> WebUiCommonHelper.getSelectorValue(o, SelectorMethod.CSS)).filter(selector -> {
            return driver.findElement(By.cssSelector(selector)) != null;
        }).collect(Collectors.toList()).forEach(e -> {
            JavascriptExecutor jsx = (JavascriptExecutor) driver;
            String state = jsx.executeScript("return document.querySelector('" + e + "').style.visibility").toString();
            jsx.executeScript("document.querySelector('" + e + "').style.visibility = 'hidden'");
            preState.put(e, state);
        });
        return preState;
    }

    private static void restoreElements(WebDriver driver, Map<String, String> states) {
        if (states == null || driver == null) {
            return;
        }

        states.keySet().forEach(key -> {
            JavascriptExecutor jsx = (JavascriptExecutor) driver;
            jsx.executeScript(
                    String.format("document.querySelector('%s').style.visibility = '%s'", key, states.get(key)));
        });
    }

    public static File extractScreenFiles() throws Exception {
        String path = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        path = URLDecoder.decode(path, "utf-8");
        File jarFile = new File(path);
        if (jarFile.isFile()) {
            JarFile jar = new JarFile(jarFile);
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName();
                if (name.startsWith(SCREENSHOT_FOLDER) && name.endsWith(".png")) {
                    String mappingFileName = name.replace(SCREENSHOT_FOLDER + "/", "");
                    File tmpFile = new File(System.getProperty("java.io.tmpdir") + mappingFileName);
                    if (tmpFile.exists()) {
                        tmpFile.delete();
                    }
                    FileOutputStream fos = new FileOutputStream(tmpFile);
                    IOUtils.copy(jar.getInputStream(jarEntry), fos);
                    fos.flush();
                    fos.close();
                }
            }
            jar.close();
            return new File(System.getProperty("java.io.tmpdir"));
        } else { // Run with IDE
            File folder = new File(path + "../" + SCREENSHOT_FOLDER);
            return folder;
        }
    }

    public static File getKmsIeDriverDirectory() throws IOException {
        String path = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        path = URLDecoder.decode(path, "utf-8");
        File jarFile = new File(path);
        if (jarFile.isFile()) {
            String kmsIePath = jarFile.getParentFile().getParentFile().getAbsolutePath() + "/configuration/"
                    + KMS_IE_DRIVER_FOLDER;
            return new File(kmsIePath);
        } else { // Run with IDE
            File folder = new File(path + "../" + KMS_IE_DRIVER_FOLDER);
            return folder;
        }
    }

    public static File getAuthenticationDirectory() throws IOException {
        String path = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        path = URLDecoder.decode(path, "utf-8");
        File jarFile = new File(path);
        if (jarFile.isFile()) {
            String kmsIePath = jarFile.getParentFile().getParentFile().getAbsolutePath() + "/configuration/"
                    + AUTHENTICATION_FOLDER;
            return new File(kmsIePath);
        } else { // Run with IDE
            File folder = new File(path + "../" + AUTHENTICATION_FOLDER);
            return folder;
        }
    }

    /**
     * Return a file representing directory resources/extensions
     * 
     * @return {@link File}
     * @throws IOException
     */
    public static File getExtensionsDirectory() throws IOException {
        String path = FileUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        path = URLDecoder.decode(path, "utf-8");
        File jarFile = new File(path);
        if (jarFile.isFile()) {
            String kmsIePath = jarFile.getParentFile().getParentFile().getAbsolutePath() + "/configuration/"
                    + EXTENSIONS_FOLDER_NAME;
            return new File(kmsIePath);
        } else { // Run with IDE
            File folder = new File(path + ".." + File.separator + EXTENSIONS_FOLDER_NAME);
            return folder;
        }
    }

    public static String getRelativePath(String path, String baseDir) {
        String relativePath = new File(baseDir).toPath().relativize(new File(path).toPath()).toString();
        return FilenameUtils.separatorsToUnix(relativePath);
    }

    public static boolean isInBaseFolder(String absolutePath, String absoluteBaseDir) {
        File file = new File(absolutePath);
        File baseDir = new File(absoluteBaseDir);
        return file.getAbsolutePath().startsWith(baseDir.getAbsolutePath());
    }

}
