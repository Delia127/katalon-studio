package com.kms.katalon.core.model;

import java.util.ArrayList;
import java.util.List;

public enum FailureHandling {	
	STOP_ON_FAILURE,	//Default
	CONTINUE_ON_FAILURE,
	OPTIONAL;
	
	public static String[] valueStrings() {
	    List<String> valueStrings = new ArrayList<String>();
	    for (FailureHandling failure : values()) {
	        valueStrings.add(failure.name());
	    }
	    return valueStrings.toArray(new String[0]);
	}
}
