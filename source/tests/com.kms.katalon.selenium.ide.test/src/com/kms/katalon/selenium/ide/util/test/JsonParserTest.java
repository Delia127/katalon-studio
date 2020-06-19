package com.kms.katalon.selenium.ide.util.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
        Map<String, String> monoSuiteTests = result.getMonoSuiteTests();

        assertEquals(testSuites.size(), 1);
        assertEquals(testCases.size(), 2);
        assertEquals(monoSuiteTests.size(), 1);

        TestSuite testSuite = testSuites.get(0);
        assertEquals(testSuite.getName(), "Default Suite");
        List<String> testIds = testSuite.getTests();
        assertEquals(testIds.size(), 1);
        String testId = testIds.get(0);
        assertEquals(testId, "8e1e0cb5-1a26-4ad2-b337-2033a48ac9ad");
        assertEquals(monoSuiteTests.get("8e1e0cb5-1a26-4ad2-b337-2033a48ac9ad"), "Default Suite");

        TestCase testCase = testCases.get(0);
        assertEquals(testCase.getName(), "Test Case 1");
        assertEquals(testCase.getBaseUrl(), "https://katalon-demo-cura.herokuapp.com");
        assertEquals(testCase.getFilePath(), seleniumIdeV3File.getAbsolutePath());
        List<Command> cmds = testCase.getCommands();
        assertEquals(cmds.size(), 11);
        Command cmd = cmds.get(0);
        assertEquals(cmd.getCommand(), "open");
        assertEquals(cmd.getComment(), "open demo app");
        assertEquals(cmd.getTarget(), "/");
        assertEquals(cmd.getValue(), "");
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
