package com.kms.katalon.execution.logging;

import java.io.IOException;

public interface IOutputStream {
    public void println(String line) throws IOException;
    
    public void close() throws IOException;
}
