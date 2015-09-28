package com.kms.katalon.composer.testcase.constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


public class TreeTableMenuItemConstants {
    public static final String MENU_ITEM_ACTION_KEY = "ACTION";
	public enum AddAction {
		Add, InsertBefore, InsertAfter
	}
	
	public static final String MENU_ITEM_ID_KEY = "MENU_ITEM_ID_KEY";
	
	public static final int CONTROL_STATEMENT_MENU_ITEM_ID = 1;
	public static final int IF_STATEMENT_MENU_ITEM_ID = 1;
	public static final int ELSE_STATEMENT_MENU_ITEM_ID = 2;
	public static final int ELSE_IF_STATEMENT_MENU_ITEM_ID = 3;
	public static final int WHILE_STATEMENT_MENU_ITEM_ID = 4;
	public static final int FOR_STATEMENT_MENU_ITEM_ID = 5;
	public static final int BINARY_STATEMENT_MENU_ITEM_ID = 6;
	public static final int ASSERT_STATEMENT_MENU_ITEM_ID = 7;
	public static final int CALL_METHOD_STATMENT_MENU_ITEM_ID = 8;
	
	public static final int CUSTOM_KEYWORD_MENU_ITEM_ID = 32;
	public static final int BUILTIN_KEYWORD_MENU_ITEM_ID = 33;
	
	public static final int COPY_MENU_ITEM_ID = 64;
	public static final int CUT_MENU_ITEM_ID = 65;
	public static final int PASTE_MENU_ITEM_ID = 66;
	public static final int REMOVE_MENU_ITEM_ID = 67;
	public static final int CALL_TEST_CASE_MENU_ITEM_ID = 70;
	
	public static final int METHOD_MENU_ITEM_ID = 72;
	
	public static final int CHANGE_FAILURE_HANDLING_MENU_ITEM_ID = 128;
	public static final String FAILURE_HANDLING_KEY = "FAILURE_HANDLING_KEY";

	public static final String CONDITION_STATEMENT_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_CONDITION_STATEMENT;
	public static final String ASSERT_STATEMENT_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_ASSERT_STATEMENT;
	public static final String BINARY_STATEMENT_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_BINARY_STATEMENT;
	public static final String FOR_STATEMENT_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_FOR_STATEMENT;
	public static final String WHILE_STATEMENT_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_WHILE_STATEMENT;
	public static final String ELSE_IF_STATEMENT_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_ELSE_IF_STATEMENT;
	public static final String ELSE_STATEMENT_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_ELSE_STATEMENT;
	public static final String IF_STATEMENT_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_IF_STATEMENT;
	public static final String CUSTOM_KEYWORD_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_CUSTOM_KEYWORD;
	public static final String CALL_TEST_CASE_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_CALL_TEST_CASE;
	public static final String CALL_METHOD_STATEMENT_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_CALL_METHOD_STATEMENT;
	
	public static final String METHOD_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_METHOD;
	
	private static Map<String, Integer> keywordClassOffsets = new HashMap<String, Integer>();
	
	public static void generateBuiltInKeywordMenuItemIDs(List<Class<?>> classes) {
	    keywordClassOffsets.clear();
	    int offset = 0;
	    for (Class<?> clazz : classes) {
	        keywordClassOffsets.put(clazz.getName(), offset);
	        offset++;
	    }
	}
	
	public static int getMenuItemID(String className) {
	    int offset = keywordClassOffsets.get(className);
	    return (offset >= 0) ? BUILTIN_KEYWORD_MENU_ITEM_ID + offset : offset;
	}
	
	public static boolean isBuildInKeywordID(int id) {
	    return (id >= BUILTIN_KEYWORD_MENU_ITEM_ID) && (id < BUILTIN_KEYWORD_MENU_ITEM_ID + keywordClassOffsets.size());
	}
	
	public static String getContributingClassName(int id) {
	    if (isBuildInKeywordID(id)) {
	        for (String className : keywordClassOffsets.keySet()) {
	            if (keywordClassOffsets.get(className) == id - BUILTIN_KEYWORD_MENU_ITEM_ID) {
	                return className;
	            }
	        }
	    }
	    return StringUtils.EMPTY;
	}
}
