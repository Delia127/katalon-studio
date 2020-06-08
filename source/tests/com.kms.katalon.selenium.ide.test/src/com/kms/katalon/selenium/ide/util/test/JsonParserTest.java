package com.kms.katalon.selenium.ide.util.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.junit.Test;

import com.kms.katalon.execution.classpath.ClassPathResolver;
import com.kms.katalon.selenium.ide.model.Command;
import com.kms.katalon.selenium.ide.model.TestCase;
import com.kms.katalon.selenium.ide.model.TestSuite;
import com.kms.katalon.selenium.ide.util.JsonParser;
import com.kms.katalon.selenium.ide.util.ParsedResult;

public class JsonParserTest {

    @Test
    public void parseTest() throws IOException {
        File seleniumIdeV3File = getFile("resources/SeleniumIdeProject.side");
        ParsedResult result = JsonParser.parse(seleniumIdeV3File);
        List<TestSuite> testSuites = result.getTestSuites();
        List<TestCase> testCases = result.getTestCases();

        assertEquals(testSuites.size(), 1);
        assertEquals(testCases.size(), 1);

        TestSuite ts = testSuites.get(0);
        assertEquals(ts.getName(), "Default Suite");
        List<TestCase> tcs = ts.getTestCases();
        assertEquals(tcs.size(), 1);

        TestCase tc = tcs.get(0);
        assertEquals(tc.getName(), "Test Case 1");
        assertEquals(tc.getBaseUrl(), "https://katalon-demo-cura.herokuapp.com");
        assertEquals(tc.getFilePath(), seleniumIdeV3File.getAbsolutePath());

        List<Command> cmds = tc.getCommands();
        assertEquals(cmds.size(), 11);
        Command cmd = cmds.get(0);
        assertEquals(cmd.getCommand(), "open");
        assertEquals(cmd.getComment(), "open demo app");
        assertEquals(cmd.getTarget(), "/");
        assertEquals(cmd.getValue(), "");

        TestCase tc2 = testCases.get(0);
        assertEquals(tc2.getName(), "Test Case 2");
    }

    private File getFile(String path) throws IOException {
        File bundleFile = FileLocator.getBundleFile(Platform.getBundle("com.kms.katalon.selenium.ide.test"));
        if (bundleFile.isDirectory()) { // run by IDE
            return new File(bundleFile + File.separator + path);
        } else { // run as product
            return new File(ClassPathResolver.getConfigurationFolder(), path);
        }
    }
}
