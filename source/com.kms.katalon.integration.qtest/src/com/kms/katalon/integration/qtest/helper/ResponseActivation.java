package com.kms.katalon.integration.qtest.helper;

public class ResponseActivation {

    public static final String VERIFIED_FAIL = "FAIL";

    public static final String VERIFIED_SUCCESS = "SUCCESS";

    public static final String LICENSE_NOT_FOUND_FAIL_MSG = "LICENSE_NOT_FOUND";

    public static final String LICENSE_EXPIRED_FAIL_MSG = "LICENSE_EXPIRED";

    public static final String LICENSE_ALREADY_ACTIVATED_FAIL_MSG = "LICENSE_ALREADY_ACTIVATED";

    private String message;

    private String verified;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

}
