package com.kms.katalon.composer.testcase.constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.kms.katalon.custom.keyword.KeywordClass;


public class TreeTableMenuItemConstants {
    public static final String MENU_ITEM_ACTION_KEY = "ACTION";
	public enum AddAction {
		Add, InsertBefore, InsertAfter
	}
	
	public static final String MENU_ITEM_ID_KEY = "MENU_ITEM_ID_KEY";
	
	public static final int IF_STATEMENT_MENU_ITEM_ID = 1;
	public static final int ELSE_STATEMENT_MENU_ITEM_ID = 2;
	public static final int ELSE_IF_STATEMENT_MENU_ITEM_ID = 3;
	public static final int WHILE_STATEMENT_MENU_ITEM_ID = 4;
	public static final int FOR_STATEMENT_MENU_ITEM_ID = 5;
	public static final int BINARY_STATEMENT_MENU_ITEM_ID = 6;
	public static final int ASSERT_STATEMENT_MENU_ITEM_ID = 7;
	public static final int CALL_METHOD_STATEMENT_MENU_ITEM_ID = 8;
	public static final int BREAK_STATMENT_MENU_ITEM_ID = 9;
	public static final int CONTINUE_STATMENT_MENU_ITEM_ID = 10;
	public static final int RETURN_STATMENT_MENU_ITEM_ID = 11;
	public static final int SWITCH_STATMENT_MENU_ITEM_ID = 12;
	public static final int CASE_STATMENT_MENU_ITEM_ID = 13;
	public static final int DEFAULT_STATMENT_MENU_ITEM_ID = 14;
	public static final int TRY_STATEMENT_MENU_ITEM_ID = 15;
	public static final int CATCH_STATMENT_MENU_ITEM_ID = 16;
	public static final int FINALLY_STATMENT_MENU_ITEM_ID = 17;
	public static final int THROW_STATMENT_MENU_ITEM_ID = 18;
	
	public static final int DECISION_MAKING_STATEMENT_MENU_ITEM_ID = 20;
    public static final int LOOPING_STATEMENT_MENU_ITEM_ID = 21;
    public static final int BRANCHING_STATEMENT_MENU_ITEM_ID = 22;
    public static final int EXCEPTION_HANDLING_STATEMENT_MENU_ITEM_ID = 23;
	
	public static final int CUSTOM_KEYWORD_MENU_ITEM_ID = 32;
	public static final int BUILTIN_KEYWORD_MENU_ITEM_ID = 33;
	
	public static final int EXECUTE_FROM_TEST_STEP_MENU_ITEM_ID = 50;
	
	public static final int COPY_MENU_ITEM_ID = 64;
	public static final int CUT_MENU_ITEM_ID = 65;
	public static final int PASTE_MENU_ITEM_ID = 66;
	public static final int REMOVE_MENU_ITEM_ID = 67;
	public static final int DISABLE_MENU_ITEM_ID = 68;
	public static final int ENABLE_MENU_ITEM_ID = 69;
	public static final int CALL_TEST_CASE_MENU_ITEM_ID = 70;
	
	public static final int METHOD_MENU_ITEM_ID = 72;
	
	public static final int CHANGE_FAILURE_HANDLING_MENU_ITEM_ID = 128;
	
	public static final int RUN_FROM_THIS_STEP_ID = 256;
	public static final int RUN_SELECTED_STEPS_ID = 257;
	
//	----------------------
	public static final int ADD_TO_AN_EXISTING_TEST_SUITE_ID = 80;
	public static final int ADD_TO_A_NEW_TEST_SUITE_ID = 81;
//	----------------------

	public static final String FAILURE_HANDLING_KEY = "FAILURE_HANDLING_KEY";

	public static final String ASSERT_STATEMENT_MENU_ITEM_LABEL = StringConstants.TREE_ASSERT_STATEMENT;
	public static final String BINARY_STATEMENT_MENU_ITEM_LABEL = StringConstants.TREE_BINARY_STATEMENT;
	public static final String FOR_STATEMENT_MENU_ITEM_LABEL = StringConstants.TREE_FOR_STATEMENT;
	public static final String WHILE_STATEMENT_MENU_ITEM_LABEL = StringConstants.TREE_WHILE_STATEMENT;
	public static final String ELSE_IF_STATEMENT_MENU_ITEM_LABEL = StringConstants.TREE_ELSE_IF_STATEMENT;
	public static final String ELSE_STATEMENT_MENU_ITEM_LABEL = StringConstants.TREE_ELSE_STATEMENT;
	public static final String IF_STATEMENT_MENU_ITEM_LABEL = StringConstants.TREE_IF_STATEMENT;
	public static final String CUSTOM_KEYWORD_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_CUSTOM_KEYWORD;
	public static final String CALL_TEST_CASE_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_CALL_TEST_CASE;
	public static final String SWITCH_STATEMENT_MENU_ITEM_LABEL = StringConstants.TREE_SWITCH_STATEMENT;
	public static final String CASE_STATEMENT_MENU_ITEM_LABEL = StringConstants.TREE_CASE_STATEMENT;
	public static final String DEFAULT_STATEMENT_MENU_ITEM_LABEL = StringConstants.TREE_DEFAULT_STATEMENT;
	public static final String BREAK_STATEMENT_MENU_ITEM_LABEL = StringConstants.TREE_BREAK_STATEMENT;
	public static final String CONTINUE_STATEMENT_MENU_ITEM_LABEL = StringConstants.TREE_CONTINUE_STATEMENT;
	public static final String RETURN_STATEMENT_MENU_ITEM_LABEL = StringConstants.TREE_RETURN_STATEMENT;
	public static final String TRY_STATEMENT_MENU_ITEM_LABEL = StringConstants.TREE_TRY_STATEMENT;
	public static final String CATCH_STATEMENT_MENU_ITEM_LABEL = StringConstants.TREE_CATCH_STATEMENT;
	public static final String FINALLY_STATEMENT_MENU_ITEM_LABEL = StringConstants.TREE_FINALLY_STATEMENT;
	public static final String THROW_STATEMENT_MENU_ITEM_LABEL = StringConstants.TREE_THROW_STATEMENT;
	public static final String CALL_METHOD_STATEMENT_MENU_ITEM_LABEL = StringConstants.TREE_METHOD_CALL_STATEMENT;
	public static final String DECISION_MAKING_STATEMENT_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_DECISION_MAKING_STATEMENT;
    public static final String LOOPING_STATEMENT_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_LOOPING_STATEMENT;
    public static final String BRANCHING_STATEMENT_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_BRANCHING_STATEMENT;
    public static final String EXCEPTION_HANDLING_STATEMENT_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_EXCEPTION_HANDLING_STATEMENT;
    
    
//	--------------------------
    public static final String ADD_TO_AN_EXISTING_TEST_SUITE_LABEL = StringConstants.CONS_MENU_CONTEXT_ADD_TO_AN_EXISTING_TEST_SUITE;
    public static final String ADD_TO_A_NEW_TEST_SUITE_LABEL = StringConstants.CONS_MENU_CONTEXT_ADD_TO_A_NEW_TEST_SUITE;
    
	public static final String METHOD_MENU_ITEM_LABEL = StringConstants.CONS_MENU_CONTEXT_METHOD;
	
	private static Map<String, Integer> keywordClassOffsets = new HashMap<String, Integer>();
	
	public static void generateBuiltInKeywordMenuItemIDs(List<KeywordClass> keywordClasses) {
	    keywordClassOffsets.clear();
	    int offset = 0;
	    for (KeywordClass keywordClass : keywordClasses) {
	        keywordClassOffsets.put(keywordClass.getAliasName(), offset);
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
	
	public static int getBuildInKeywordID(String aliasName) {
	    return BUILTIN_KEYWORD_MENU_ITEM_ID + keywordClassOffsets.get(aliasName);
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
