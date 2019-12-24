package com.kms.katalon.logging.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.core.runtime.Platform;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.kms.katalon.logging.LogManager;
import com.kms.katalon.logging.LogUtil;

public class LogUtilTest {

    private static ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private static PrintStream originalOut = System.out;

    private static ByteArrayOutputStream errorContent = new ByteArrayOutputStream();

    private static PrintStream originalErr = System.err;

    @Before
    public void initilize() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errorContent));
        LogManager.reset();
        LogManager.active();
    }

    @After
    public void setOriginalPrintStream() {
        outContent.reset();
        errorContent.reset();
        System.setOut(originalOut);
        System.setErr(originalErr);
        LogManager.reset();
        LogManager.active();
    }

    private File getLogFile() {
        return Platform.getLogFileLocation().toFile();
    }

    @Test
    public void printOutputLineTest() {
        LogUtil.printOutputLine("hello");

        assertEquals("hello" + System.lineSeparator(), outContent.toString());
    }

    @Test
    public void printErrorLineTest() {
        LogUtil.printErrorLine("hello");

        assertEquals("hello" + System.lineSeparator(), errorContent.toString());
    }

    @Test
    public void writeOutputLineTest() throws IOException {
        File logFile = getLogFile();
        FileUtils.write(logFile, "");

        LogUtil.writeOutputLine("hello");
        assertEquals("hello" + System.lineSeparator(), FileUtils.readFileToString(logFile));

        FileUtils.write(logFile, "");
    }

    @Test
    public void logErrorMessageTest() throws IOException {
        File logFile = getLogFile();
        FileUtils.write(logFile, "");

        LogUtil.logErrorMessage("hello");
        // The result should be the same format as the lines below:
        // ******************************
        // <empty>
        // Mon Dec 23 14:28:07 ICT 2019
        // hello
        // <empty>
        // ******************************
        List<String> logLines = FileUtils.readLines(logFile);
        assertEquals(logLines.size(), 4);
        assertEquals("", logLines.get(0));
        assertEquals("hello", logLines.get(2));
        assertEquals("", logLines.get(3));

        FileUtils.write(logFile, "");
    }

    @Test
    public void logInfoTest() throws IOException {
        File logFile = getLogFile();
        FileUtils.write(logFile, "");

        LogUtil.logInfo("hello");
        assertEquals("hello" + System.lineSeparator(), outContent.toString());
        assertEquals("hello" + System.lineSeparator(), FileUtils.readFileToString(logFile));

        FileUtils.write(logFile, "");
    }

    @Test
    public void logErrorTest() throws IOException {
        File logFile = getLogFile();
        FileUtils.write(logFile, "");

        LogUtil.logError("hello");
        assertEquals("hello" + System.lineSeparator(), errorContent.toString());
        assertEquals("hello" + System.lineSeparator(), FileUtils.readFileToString(logFile));

        FileUtils.write(logFile, "");
    }

    @Test
    public void logThrowableTest() throws IOException {
        File logFile = getLogFile();
        FileUtils.write(logFile, "");

        Exception e = new Exception("Expected exception");

        LogUtil.logError(e);

        List<String> logLines = FileUtils.readLines(logFile);
        assertEquals(logLines.size() > 3, true);

        assertEquals("", logLines.get(0));
        String stacktraceString = StringUtils.join(logLines.subList(2, logLines.size()), System.lineSeparator());
        assertEquals(ExceptionUtils.getStackTrace(e).trim(), stacktraceString.trim());

        FileUtils.write(logFile, "");
    }

    @Test
    public void logThrowableWithMessageTest() throws IOException {
        File logFile = getLogFile();
        FileUtils.write(logFile, "");

        String message = "Expected message";
        Exception e = new Exception("Expected exception");

        LogUtil.logError(e, message);

        List<String> logLines = FileUtils.readLines(logFile);
        assertEquals(logLines.size() > 4, true);

        assertEquals("", logLines.get(0));
        assertEquals("Expected message", logLines.get(2));
        String stacktraceString = StringUtils.join(logLines.subList(3, logLines.size()), System.lineSeparator());
        assertEquals(ExceptionUtils.getStackTrace(e).trim(), stacktraceString.trim());

        FileUtils.write(logFile, "");
    }

    @Test
    public void printAndLogErrorTest() throws IOException {
        File logFile = getLogFile();
        FileUtils.write(logFile, "");

        Exception e = new Exception("Expected exception");

        LogUtil.printAndLogError(e);
        // Verify output in log file
        List<String> logLines = FileUtils.readLines(logFile);
        assertEquals(logLines.size() > 3, true);

        assertEquals("", logLines.get(0));
        String stacktraceString = StringUtils.join(logLines.subList(2, logLines.size()), System.lineSeparator());
        assertEquals(ExceptionUtils.getStackTrace(e).trim(), stacktraceString.trim());

        FileUtils.write(logFile, "");

        // Verify error in console
        List<String> logLinesInConsole = Arrays.asList(errorContent.toString().split(System.lineSeparator()));
        assertEquals(logLinesInConsole.size() > 3, true);

        assertEquals("", logLinesInConsole.get(0));
        String stacktraceStringInConsole = StringUtils.join(logLinesInConsole.subList(2, logLinesInConsole.size()),
                System.lineSeparator());
        assertEquals(ExceptionUtils.getStackTrace(e).trim(), stacktraceStringInConsole.trim());
    }

    @Test
    public void printAndLogErrorWithMessageTest() throws IOException {
        File logFile = getLogFile();
        FileUtils.write(logFile, "");

        String message = "Expected message";
        Exception e = new Exception("Expected exception");

        LogUtil.printAndLogError(e, message);
        // Verify output in log file
        List<String> logLines = FileUtils.readLines(logFile);
        assertEquals(logLines.size() > 4, true);

        assertEquals("", logLines.get(0));
        assertEquals("Expected message", logLines.get(2));
        String stacktraceString = StringUtils.join(logLines.subList(3, logLines.size()), System.lineSeparator());
        assertEquals(ExceptionUtils.getStackTrace(e).trim(), stacktraceString.trim());

        FileUtils.write(logFile, "");

        // Verify error in console
        List<String> logLinesInConsole = Arrays.asList(errorContent.toString().split(System.lineSeparator()));
        assertEquals(logLinesInConsole.size() > 3, true);

        assertEquals("", logLinesInConsole.get(0));
        assertEquals("Expected message", logLines.get(2));
        String stacktraceStringInConsole = StringUtils.join(logLinesInConsole.subList(3, logLinesInConsole.size()),
                System.lineSeparator());
        assertEquals(ExceptionUtils.getStackTrace(e).trim(), stacktraceStringInConsole.trim());
    }
}
