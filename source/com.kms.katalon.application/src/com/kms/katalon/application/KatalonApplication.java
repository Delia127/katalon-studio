package com.kms.katalon.application;

import java.util.UUID;

public class KatalonApplication {

    public static final String SESSION_ID;

    static {
        SESSION_ID = UUID.randomUUID().toString();
    }

}
