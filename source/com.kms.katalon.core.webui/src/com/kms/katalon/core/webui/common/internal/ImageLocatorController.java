package com.kms.katalon.core.webui.common.internal;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.sikuli.api.ScreenRegion;

import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.webui.common.ScreenUtil;

/**
 * A controller contains logic relating to Image-based Object Recognition
 * 
 */
public class ImageLocatorController {

    private static final KeywordLogger logger = KeywordLogger.getInstance(ImageLocatorController.class);

    /**
     * Retrieve image at the given path, then look for similar images using
     * Sikuli. Given a matched image's position, use the coordinates to retrieve
     * the corresponding web elements, then sort ascending in size differences
     * with the target image.
     * 
     * @param webDriver
     * @param pathToScreenshot
     * @return A list of {@link WebElement} whose visuals match the specified image and are sorted ascending in size
     * differences with the target image
     */
    public static List<WebElement> findElementByScreenShot(WebDriver webDriver, String pathToScreenshot) {
        ScreenUtil screen = new ScreenUtil(0.2);
        logger.logInfo("Attempting to find element by its screenshot !");
        Map<ScreenRegion, List<WebElement>> mapOfCandidates = new HashMap<ScreenRegion, List<WebElement>>();
        int iterationCount = 0;
        int viewPortHeight = 100;
        int scrolledAmount = 0;
        int pageScrollHeight = getPageScrollHeight(webDriver);
        logger.logDebug("Page Scroll Height: " + pageScrollHeight);
        do {
            try {
                scrolledAmount = iterationCount * viewPortHeight;
                if (!scroll(webDriver, scrolledAmount)) {
                    break;
                }
                Thread.sleep(500);
                List<ScreenRegion> matchedRegions = screen.findImages(pathToScreenshot);
                if (matchedRegions.size() == 0) {
                    break;
                }
                ScreenRegion matchedRegion = matchedRegions.get(0);
                Point coordinatesRelativeToDriver = getCoordinatesRelativeToWebDriver(webDriver, matchedRegion);
                double xRelativeToDriver = coordinatesRelativeToDriver.getX();
                double yRelativeToDriver = coordinatesRelativeToDriver.getY();
                logger.logDebug("Coordinates of matched region relative to driver: (" + xRelativeToDriver + " , "
                        + yRelativeToDriver + ")");
                List<WebElement> elementsAtPointXandY = elementsFromPoint(webDriver, xRelativeToDriver,
                        yRelativeToDriver);
                sortMinimizingDifferencesInSize(elementsAtPointXandY, matchedRegion);
                mapOfCandidates.put(matchedRegion, elementsAtPointXandY);
            } catch (Exception e) {
                logger.logInfo("Unable to find element within the current viewport !");
            }
            iterationCount++;
        } while (scrolledAmount <= pageScrollHeight);
        logger.logDebug("Highest matched region's score: " + getHighestMatchedRegionScore(mapOfCandidates));
        debug_printChosenWebElement(pathToScreenshot, webDriver, mapOfCandidates);
        try {
            scroll(webDriver, 0);
        } catch (InterruptedException e) {
            logger.logError(e.getMessage());
        }
        return getHighestMatchedRegionWebElements(mapOfCandidates);
    }

    private static void debug_printChosenWebElement(String pathToScreenshot, WebDriver webDriver,
            Map<ScreenRegion, List<WebElement>> mapOfCandidates) {
        File screenshotFile = new File(pathToScreenshot);
        File targetFolder = new File(screenshotFile.getParentFile().getAbsolutePath() + "/matched-web-elements");
        targetFolder.mkdirs();
        try {
            saveWebElementScreenshot(webDriver, getWebElementOfHighestMatchedRegion(mapOfCandidates),
                    screenshotFile.getName(), targetFolder.getAbsolutePath());
        } catch (IOException e) {
            logger.logInfo(e.getMessage());
        }
    }

    private static WebElement getWebElementOfHighestMatchedRegion(Map<ScreenRegion, List<WebElement>> mapOfCandidates) {
        return getHighestMatchedRegionWebElements(mapOfCandidates).stream().findFirst().orElse(null);
    }

    /**
     * Get the list of web elements of the highest matched region in the map
     * 
     * @param mapOfCandidates
     * @return
     */
    private static List<WebElement> getHighestMatchedRegionWebElements(
            Map<ScreenRegion, List<WebElement>> mapOfCandidates) {
        return getHighestMatchedRegionEntry(mapOfCandidates).map(entry -> entry.getValue())
                .orElse(Collections.emptyList());
    }

    /**
     * Get the score of the highest matched region in the map
     * 
     * @param mapOfCandidates
     * @return
     */
    private static double getHighestMatchedRegionScore(Map<ScreenRegion, List<WebElement>> mapOfCandidates) {
        return getHighestMatchedRegionEntry(mapOfCandidates).map(entry -> entry.getKey().getScore()).orElse(-1.0);
    }

    private static Optional<Entry<ScreenRegion, List<WebElement>>> getHighestMatchedRegionEntry(
            Map<ScreenRegion, List<WebElement>> mapOfCandidates) {
        return mapOfCandidates.entrySet().stream().max((entry1, entry2) -> {
            double reg1Score = entry1.getKey().getScore();
            double reg2Score = entry2.getKey().getScore();
            if (reg1Score < reg2Score)
                return -1;
            if (reg1Score > reg2Score)
                return 1;
            return 0;
        });
    }

    /**
     * Scroll the current page by the provided distance
     * 
     * @param webDriver
     * @param heightPos
     * @return
     * @throws InterruptedException
     */
    private static boolean scroll(WebDriver webDriver, int heightPos) throws InterruptedException {
        try {
            ((JavascriptExecutor) webDriver).executeScript("window.scrollTo(0," + heightPos + ")");
            logger.logDebug("Scrolled to (0 , " + heightPos + ")");
            return true;
        } catch (Exception e) {
            logger.logInfo("Cannot scroll viewport anymore !");
        }
        return false;
    }

    private static int getPageScrollHeight(WebDriver webDriver) {
        return (((Number) ((JavascriptExecutor) webDriver).executeScript("return document.body.scrollHeight"))
                .intValue());
    }

    private static void sortMinimizingDifferencesInSize(List<WebElement> elementsAtPointXandY,
            ScreenRegion matchedRegion) {
        elementsAtPointXandY.sort((ele1, ele2) -> {
            double ele1H = getDifferenceInSizeHeuristic(ele1, matchedRegion);
            double ele2H = getDifferenceInSizeHeuristic(ele2, matchedRegion);
            if (ele1H < ele2H)
                return -1;
            if (ele1H > ele2H)
                return 1;
            return 0;
        });
    }

    /**
     * Return the sum of differences between height and width of the matched region versus the size of the web element
     * 
     * @param element
     * @param matchedRegion
     * @return
     */
    private static double getDifferenceInSizeHeuristic(WebElement element, ScreenRegion matchedRegion) {
        double widthDiff = Math.abs(element.getRect().getWidth() - matchedRegion.getBounds().getWidth());
        double heightDiff = Math.abs(element.getRect().getHeight() - matchedRegion.getBounds().getHeight());
        return widthDiff + heightDiff;
    }

    /**
     * Calling {@link ImageLocatorController#findElementByScreenShot(WebDriver, String)} and then
     * get the first element from the list or null otherwise
     * 
     * @param webDriver
     * @param pathToScreenshot
     * @return An {@link WebElement} whose visuals match the specified image the most
     */
    public static WebElement findElementByScreenshot(WebDriver webDriver, String pathToScreenshot) {
        return findElementByScreenShot(webDriver, pathToScreenshot).stream().findFirst().orElse(null);
    }

    /**
     * Get the <b>center</b> coordinates of the matched region produced by Sikuli and
     * transform them into the coordinates relative to the web driver.
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
        double X = matchedRegion.getBounds().getCenterX();
        double Y = matchedRegion.getBounds().getCenterY();
        logger.logDebug("Viewport Height: " + viewHeight);
        logger.logDebug("Driver coordinates: " + driverX + " , " + driverY + ")");
        logger.logDebug("Matched Region center coordinates: (" + X + " , " + Y + ")");
        double xRelativeToDriver = X - driverX;
        double yRelativeToDriver = Y - driverY - (driverHeight - viewHeight);
        return new Point((int) xRelativeToDriver, (int) yRelativeToDriver);
    }

    /**
     * Retrieve all {@link WebElement} at the specified location using:
     * 
     * <pre>
     * "return document.elementsFromPoint(x,y)"
     * </pre>
     * 
     * @param webDriver
     * @param x
     * @param y
     * @return A list of @{link WebElement} returned from calling the above browser API
     */
    private static List<WebElement> elementsFromPoint(WebDriver webDriver, double x, double y) {
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
