package com.kms.katalon.core.webservice.exception;

import com.kms.katalon.core.webservice.constants.StringConstants;

public class WSSocketTimeoutException extends WebServiceException {

    private static final long serialVersionUID = -1096596641112244215L;

    private Throwable coreException;

    public WSSocketTimeoutException() {
        this(StringConstants.MSG_SOCKET_TIMEOUT_EXCEPTION);
    }

    public WSSocketTimeoutException(String message) {
        this(message, null);
    }

    public WSSocketTimeoutException(Throwable exception) {
        this(StringConstants.MSG_SOCKET_TIMEOUT_EXCEPTION, exception);
    }

    public WSSocketTimeoutException(String message, Throwable exception) {
        super(message);
        this.coreException = exception;
    }

    public Throwable getCoreException() {
        return coreException;
    }

    public void setCoreException(Throwable coreException) {
        this.coreException = coreException;
    }
}
