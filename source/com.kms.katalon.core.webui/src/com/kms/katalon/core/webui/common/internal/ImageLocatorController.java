package com.kms.katalon.core.webui.common.internal;

import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.sikuli.api.ScreenRegion;

import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.webui.common.ScreenUtil;

/**
 * A controller contains logic relating to finding a {@link WebElement} by its image
 * 
 */
public class ImageLocatorController {

    private static final KeywordLogger logger = KeywordLogger.getInstance(ImageLocatorController.class);

    /**
     * Retrieve image at the given path, then look for similar images using
     * Sikuli. Given the closest matched image, use the coordinates to retrieve
     * the element at that location using:
     * 
     * <pre>
     * ((JavascriptExecutor) driver)
     * .executeJavascript('document.elementsFromPoint(args[0], args[1])', x, y)
     * </pre>
     * 
     * @param webDriver
     * @param pathToScreenshot
     * @return
     */
    public static List<WebElement> findElementByScreenShot(WebDriver webDriver, String pathToScreenshot) {
        // try {
        // webDriver.manage().window().maximize();
        // Thread.sleep(1000);
        // } catch (InterruptedException e1) {
        // logger.logError(ExceptionUtils.getFullStackTrace(e1));
        // }

        ScreenUtil screen = new ScreenUtil(0.2);
        logger.logInfo("Attempting to find element by its screenshot !");
        File screenshotFile = new File(pathToScreenshot);
        String path = screenshotFile.getParent() + "/sikuli-generated";
        File tmpFile = new File(path);

        try {
            List<ScreenRegion> matchedRegions = screen.findImages(pathToScreenshot);
            sikuliDebug(screenshotFile, matchedRegions);
            if (matchedRegions.size() == 0) {
                return Collections.emptyList();
            }

            ScreenRegion matchedRegion = matchedRegions.get(0);
            Point coordinatesRelativeToDriver = getCoordinatesRelativeToWebDriver(webDriver, matchedRegion);
            double xRelativeToDriver = coordinatesRelativeToDriver.getX();
            double yRelativeToDriver = coordinatesRelativeToDriver.getY();

            List<WebElement> elementsAtPointXandY = getWebElementsAt(webDriver, xRelativeToDriver, yRelativeToDriver);

            // Find element closest in size to the matched region
            WebElement elementAtPointXandY = null;
            double min = Double.MAX_VALUE;
            for (WebElement element : elementsAtPointXandY) {
                double widthDiff = Math.abs(element.getRect().getWidth() - matchedRegion.getBounds().getWidth());
                double heightDiff = Math.abs(element.getRect().getHeight() - matchedRegion.getBounds().getHeight());
                if (widthDiff + heightDiff <= min) {
                    elementAtPointXandY = element;
                    min = widthDiff + heightDiff;
                }
            }

            if (elementAtPointXandY != null) {
                // String name = "highestMatched_" + screenshotFile.getName().replaceAll(".png", "");
                // String imageFolderPath = screenshotFile.getParent() + "/sikuli_matched_web_element";
                // WebUiCommonHelper.saveWebElementScreenshot(webDriver, elementAtPointXandY, name, imageFolderPath);
                return Arrays.asList(elementAtPointXandY);
            }
            return Collections.emptyList();
        } catch (Exception e) {
            logger.logError(ExceptionUtils.getFullStackTrace(e));
        } finally {
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
        }
        return Collections.emptyList();
    }

    /**
     * Get the coordinates of the matched region produced by Sikuli and
     * transform them into the coordinates relative to the web driver
     * 
     * @param webDriver
     * A current running {@link WebDriver}
     * @param matchedRegion
     * An instance of {@link ScreenRegion} produced by Sikuli
     * @return a {@link Point) representing the new coordinates
     */
    private static Point getCoordinatesRelativeToWebDriver(WebDriver webDriver, ScreenRegion matchedRegion) {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;

        double viewHeight = ((Number) js.executeScript("return window.innerHeight")).doubleValue();
        double driverHeight = webDriver.manage().window().getSize().getHeight();
        double driverX = webDriver.manage().window().getPosition().getX();
        double driverY = webDriver.manage().window().getPosition().getY();

        double X = matchedRegion.getBounds().getX();
        double Y = matchedRegion.getBounds().getY();
        double xRelativeToDriver = X - driverX;
        double yRelativeToDriver = Y - (driverHeight - viewHeight) - driverY;
        return new Point((int) xRelativeToDriver, (int) yRelativeToDriver);
    }

    /**
     * Create a <b>sikuli</b> folder that contains target and candidate images
     * 
     * @param screenshotFile
     * A {@link File} containing the screenshot
     * @param matchedRegions
     * A list of {@link ScreenRegion} of matched regions
     * @throws IOException
     */
    @SuppressWarnings("unused")
    private static void sikuliDebug(File screenshotFile, List<ScreenRegion> matchedRegions) throws IOException {
        String imageFolderPath = screenshotFile.getParent() + "/sikuli/"
                + screenshotFile.getName().replaceAll(".png", "");
        File imageFolder = new File(imageFolderPath + "/target.png");
        imageFolder.mkdirs();
        imageFolder.createNewFile();
        FileUtils.copyFileToDirectory(screenshotFile, imageFolder);
        matchedRegions.forEach(a -> {
            try {
                Robot robot = new Robot();
                BufferedImage image = robot.createScreenCapture(a.getBounds());
                File imageFile = new File(imageFolderPath + "/candidate_" + a.getScore() + ".png");
                ImageIO.write(image, "png", imageFile);
            } catch (IOException | AWTException e) {
                logger.logError(ExceptionUtils.getFullStackTrace(e));
            }
        });
    }

    private static List<WebElement> getWebElementsAt(WebDriver webDriver, double x, double y) {

        @SuppressWarnings("unchecked")
        List<Object> objectsAtPointXandY = (List<Object>) ((JavascriptExecutor) webDriver)
                .executeScript("return document.elementsFromPoint(arguments[0], arguments[1])", (int) x, (int) y);
        if (objectsAtPointXandY.size() == 0) {
            return Collections.emptyList();
        }
        // Document root is not a Web Element
        objectsAtPointXandY.remove(objectsAtPointXandY.size() - 1);
        List<WebElement> elementsAtPointXandY = objectsAtPointXandY.stream()
                .map(e -> (WebElement) e)
                .collect(Collectors.toList());
        return elementsAtPointXandY;
    }

    public static String saveWebElementScreenshot(WebDriver driver, WebElement ele, String name, String path)
            throws IOException {
        File screenshot = ele.getScreenshotAs(OutputType.FILE);
        BufferedImage screenshotBeforeResized = ImageIO.read(screenshot);
        int eleWidth = ele.getRect().getWidth();
        int eleHeight = ele.getRect().getHeight();
        BufferedImage screenshotAfterResized = resize(screenshotBeforeResized, eleHeight, eleWidth);
        ImageIO.write(screenshotAfterResized, "png", screenshot);
        String screenshotPath = path;
        screenshotPath = screenshotPath.replaceAll("\\\\", "/");
        if (screenshotPath.endsWith("/")) {
            screenshotPath += name;
        } else {
            screenshotPath += "/" + name;
        }
        screenshotPath += ".png";
        File fileScreenshot = new File(screenshotPath);
        FileUtils.copyFile(screenshot, fileScreenshot);
        // Delete temporary image
        screenshot.deleteOnExit();
        return screenshotPath;
    }

    /**
     * Resize the given image to the specified height and width
     * 
     * @param img An {@link BufferedImage} instance representing the image to be resized
     * @param height Height to resize to
     * @param width Width to resize to
     * @return A {@link BufferedImage}
     */
    private static BufferedImage resize(BufferedImage img, int height, int width) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
}
