package com.kms.katalon.core.util.test;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.junit.Test;

import com.kms.katalon.core.util.ConsoleCommandExecutor;

public class ConsoleCommandExecutorTest {

    @Test
    public void runConsoleCommandAndCollectResultsTest() throws IOException, InterruptedException {
        String[] commands = null;
        if (Platform.OS_WIN32.equals(Platform.getOS())) {
            commands = new String[] { "cmd", "/c", "echo hello" };
        } else {
            commands = new String[] { "/bin/sh", "-c", "echo hello" };
        }
        List<String> results = ConsoleCommandExecutor
                .runConsoleCommandAndCollectResults(commands);
        assertThat("The command results should be 1 line", results.size() == 1);
        assertThat("The command results should be hello", "hello".equals(results.get(0)));
    }
    
    @Test
    public void runConsoleCommandAndCollectResultsWithAdditionalEnvironmentsTest() throws IOException, InterruptedException {
        Map<String, String> env = new HashMap<>();
        env.put("TEST_VAR", "hello");

        String[] commands = null;
        if (Platform.OS_WIN32.equals(Platform.getOS())) {
            commands = new String[] { "cmd", "/c", "echo %TEST_VAR%" };
        } else {
            commands = new String[] { "/bin/sh", "-c", "echo $TEST_VAR" };
        }

        List<String> results = ConsoleCommandExecutor
                .runConsoleCommandAndCollectResults(commands, env);
        assertThat("The command results should be 1 line", results.size() == 1);
        assertThat("The command results should be hello", "hello".equals(results.get(0)));
    }
}
