package com.kms.katalon.execution.launcher;

import java.io.IOException;

public interface Executable {
    void start() throws IOException;

    void stop();

    void clean();
}
