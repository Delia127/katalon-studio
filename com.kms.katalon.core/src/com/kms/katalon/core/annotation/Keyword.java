package com.kms.katalon.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Keyword {
	String keywordObject() default "General";
}
