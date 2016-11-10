package com.kms.katalon.support.testing.katserver;

import com.kms.katalon.support.selftest.server.Server;

public class KatServer {
    public void start() {
        new Server().start();
    }
}
