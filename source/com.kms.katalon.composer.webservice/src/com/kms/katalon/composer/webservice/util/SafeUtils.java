package com.kms.katalon.composer.webservice.util;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class SafeUtils {

	public static List<?> safeList(List<?> list){
		return list == null ? Collections.EMPTY_LIST : list;
	}
	
	public static String safeString(String string){
		return string == null ? StringUtils.EMPTY : string;
	}
	
	public static Set<?> safeSet(Set<?> entrySet){
		return entrySet == null ? Collections.EMPTY_SET : entrySet;
		
	}
	
}
