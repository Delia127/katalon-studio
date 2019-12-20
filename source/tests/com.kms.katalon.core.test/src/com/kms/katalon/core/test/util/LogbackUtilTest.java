package com.kms.katalon.core.test.util;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.kms.katalon.core.util.LogbackUtil;

public class LogbackUtilTest {
    
    @Test
    public void getLogbackConfigFileTest() throws IOException {
        File file = LogbackUtil.getLogbackConfigFile();
        assert file.exists();
    }
}