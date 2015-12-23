package com.kms.katalon.execution.integration;

import org.apache.commons.cli.Option;

import com.kms.katalon.execution.exception.KatalonArgumentNotValidException;



public interface IntegrationCommand {
    void setValues(String[] values) throws KatalonArgumentNotValidException;
    Option getOption(); 
}
