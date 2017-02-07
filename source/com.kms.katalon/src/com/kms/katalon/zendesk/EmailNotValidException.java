package com.kms.katalon.zendesk;

public class EmailNotValidException extends ZendeskRequestException {

    /**
     * 
     */
    private static final long serialVersionUID = -4119700590607122033L;

    public EmailNotValidException(String email) {
        super(email);
    }

}
