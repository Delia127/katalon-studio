package com.kms.katalon.core.webui.common.internal;

import java.awt.AWTException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.sikuli.api.ScreenRegion;

import com.kms.katalon.core.configuration.RunConfiguration;
import com.kms.katalon.core.logging.KeywordLogger;
import com.kms.katalon.core.webui.common.ScreenUtil;

/**
 * A controller contains logic relating to finding a {@link WebElement} by its Test Object's screenshot property
 * 
 */
public class ImageLocatorController {

    private static final KeywordLogger logger = KeywordLogger.getInstance(ImageLocatorController.class);

    /**
     * Retrieve image at the given path, then look for similar images using
     * Sikuli. Given a matched image's position, use the coordinates to retrieve
     * the corresponding web element and add it to the resulting list
     * 
     * @param webDriver
     * @param pathToScreenshot
     * @return A list of {@link WebElement} whose visuals match the specified image
     */
    public static List<WebElement> findElementByScreenShot(WebDriver webDriver, String pathToScreenshot) {
        ScreenUtil screen = new ScreenUtil(0.2);
        logger.logInfo("Attempting to find element by its screenshot !");
        Map<ScreenRegion, List<WebElement>> mapOfCandidates = new HashMap<ScreenRegion, List<WebElement>>();
        int iterationCount = 0;
        int viewPortHeight = ((Number) ((JavascriptExecutor) webDriver).executeScript("return window.innerHeight"))
                .intValue();
        viewPortHeight = 100;
        double largestMatchedRegionScore = 0.0;
        int scrolledAmount = 0;
        int pageScrollHeight = getPageScrollHeight(webDriver);
        do {
            File screenshotFile = new File(pathToScreenshot);
            String path = screenshotFile.getParent() + "/sikuli-generated";
            File tmpFile = new File(path);
            try {
                scrolledAmount = iterationCount * viewPortHeight;
                if (!scroll(webDriver, scrolledAmount)) {
                    break;
                }
                
//                File screenshotFolder = new File(screenshotFile.getParentFile().getAbsolutePath() + "/screenshots");
//                screenshotFolder.mkdirs();                
//                BufferedImage image = screen.getScreenRegionImage();
//                File imageFile = new File(screenshotFolder.getAbsolutePath() + "/screenshot-at-" + iterationCount
//                        + screenshotFile.getName() + ".png");
//                ImageIO.write(image, "png", imageFile);
                
                List<ScreenRegion> matchedRegions = screen.findImages(pathToScreenshot);
                // sikuliDebug(screenshotFile, matchedRegions, iterationCount);
                if (matchedRegions.size() == 0) {
                    break;
                }
                ScreenRegion matchedRegion = matchedRegions.get(0);
                Point coordinatesRelativeToDriver = getCoordinatesRelativeToWebDriver(webDriver, matchedRegion);
                double xRelativeToDriver = coordinatesRelativeToDriver.getX();
                double yRelativeToDriver = coordinatesRelativeToDriver.getY();
                List<WebElement> elementsAtPointXandY = elementsFromPoint(webDriver, xRelativeToDriver,
                        yRelativeToDriver);
                
                sortMinimizingDifferencesInSize(elementsAtPointXandY, matchedRegion);
                
                if (matchedRegion.getScore() >= largestMatchedRegionScore) {
                    largestMatchedRegionScore = matchedRegion.getScore();
//                    Robot robot1 = new Robot();
//                    File targetFolder = new File(screenshotFile.getParentFile().getAbsolutePath() + "/targets");
//                    targetFolder.mkdirs();
//                    BufferedImage image1 = robot1.createScreenCapture(matchedRegion.getBounds());
//                    File imageFile1 = new File(targetFolder.getAbsolutePath() + "/reg-"
//                            + screenshotFile.getName() + ".png");
//                    ImageIO.write(image1, "png", imageFile1);
                    
//                    saveWebElementScreenshot(webDriver, elementsAtPointXandY.get(0), "we-" + screenshotFile.getName(),
//                            screenshotFile.getParentFile().getAbsolutePath());
                }
                
                mapOfCandidates.put(matchedRegion, elementsAtPointXandY);
            } catch (Exception e) {
                logger.logInfo("Unable to find element within the current viewport !");
            } finally {
                if (tmpFile.exists()) {
                    tmpFile.delete();
                }
            }
            iterationCount++;
        } while (scrolledAmount <= pageScrollHeight);
        ((JavascriptExecutor) webDriver).executeScript("window.scrollTo(0, 0);");
        return getWebElementsOfHighestMatchedRegion(mapOfCandidates);
    }

    private static List<WebElement> getWebElementsOfHighestMatchedRegion(
            Map<ScreenRegion, List<WebElement>> mapOfCandidates) {
        return mapOfCandidates.entrySet().stream().max((entry1, entry2) -> {
            double reg1Score = entry1.getKey().getScore();
            double reg2Score = entry2.getKey().getScore();
            if (reg1Score < reg2Score)
                return -1;
            if (reg1Score > reg2Score)
                return 1;
            return 0;
        }).map(entry -> entry.getValue()).orElse(Collections.emptyList());
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
        Thread.sleep(250);
        try {
            ((JavascriptExecutor) webDriver).executeScript("window.scrollTo(0," + heightPos + ")");
            logger.logInfo("" + (((Number) ((JavascriptExecutor) webDriver).executeScript("return document.body.scrollHeight")).intValue()));
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

    private static double getDifferenceInSizeHeuristic(WebElement element, ScreenRegion matchedRegion) {
        double widthDiff = Math.abs(element.getSize().getWidth() - matchedRegion.getBounds().getWidth());
        double heightDiff = Math.abs(element.getSize().getHeight() - matchedRegion.getBounds().getHeight());
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
    private static void sikuliDebug(File screenshotFile, List<ScreenRegion> matchedRegions, int iter)
            throws IOException {
        String imageFolderPath = screenshotFile.getParent() + "/sikuli-" + iter + "/"
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
}
