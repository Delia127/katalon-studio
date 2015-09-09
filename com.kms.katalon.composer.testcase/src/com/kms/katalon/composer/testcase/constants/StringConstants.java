package com.kms.katalon.composer.testcase.constants;

import com.kms.katalon.constants.GlobalStringConstants;

public class StringConstants extends GlobalStringConstants {
	// TreeTableMouseAdapter
	public static final String ADAP_MENU_CONTEXT_ADD = ADD;
	public static final String ADAP_MENU_CONTEXT_INSERT = INSERT;
	public static final String ADAP_MENU_CONTEXT_INSERT_BEFORE = "Insert before";
	public static final String ADAP_MENU_CONTEXT_INSERT_AFTER = "Insert after";
	public static final String ADAP_MENU_CONTEXT_REMOVE = REMOVE;
	public static final String ADAP_MENU_CONTEXT_COPY = COPY;
	public static final String ADAP_MENU_CONTEXT_CUT = CUT;
	public static final String ADAP_MENU_CONTEXT_PASTE = PASTE;
	public static final String ADAP_MENU_CONTEXT_CHANGE_TO_STATEMENTS = "Change to statements";
	public static final String ADAP_MENU_CONTEXT_CHANGE_TO_COMMENTS = "Change to comments";
	public static final String ADAP_MENU_CONTEXT_CHANGE_FAILURE_HANDLING = "Change failure handling";
	public static final String ADAP_MENU_CONTEXT_STOP_ON_FAILURE = "Stop On Failure";
	public static final String ADAP_MENU_CONTEXT_CONTINUE_ON_FAILURE = "Continue On Failure";
	public static final String ADAP_MENU_CONTEXT_OPTIONAL = "Optional";

	// FocusCellOwnerDrawHighlighterForMultiSelection
	public static final String COMP_MSG_INVALID_INTERNAL_STRUCTURE = "Internal structure invalid. Item without associated row is not possible.";

	// TreeTableMenuItemConstants
	public static final String CONS_MENU_CONTEXT_CONDITION_STATEMENT = "Control Statement";
	public static final String CONS_MENU_CONTEXT_ASSERT_STATEMENT = "Assert Statement";
	public static final String CONS_MENU_CONTEXT_BINARY_STATEMENT = "Binary Statement";
	public static final String CONS_MENU_CONTEXT_FOR_STATEMENT = "For Statement";
	public static final String CONS_MENU_CONTEXT_WHILE_STATEMENT = "While Statement";
	public static final String CONS_MENU_CONTEXT_ELSE_IF_STATEMENT = "Else If Statement";
	public static final String CONS_MENU_CONTEXT_ELSE_STATEMENT = "Else Statement";
	public static final String CONS_MENU_CONTEXT_IF_STATEMENT = "If Statement";
	public static final String CONS_MENU_CONTEXT_CALL_METHOD_STATEMENT = "Call Method Statement";
	public static final String CONS_MENU_CONTEXT_CUSTOM_KEYWORD = "Custom Keyword";
	public static final String CONS_MENU_CONTEXT_COMMENT_STATEMENT = "Comment";
	public static final String CONS_MENU_CONTEXT_CALL_TEST_CASE = "Call Test Case";
	public static final String CONS_MENU_CONTEXT_METHOD = "Method";
	
	// BinaryBuilderDialog
	public static final String DIA_TITLE_BINARY_INPUT = "Binary Input";
	public static final String DIA_COL_OBJ = OBJECT;
	public static final String DIA_COL_VALUE_TYPE = "Value Type";
	public static final String DIA_COL_CONSTANT_TYPE = "Constant Type";
	public static final String DIA_COL_VALUE = VALUE;

	// BooleanBuilderDialog
	public static final String DIA_BTN_REVERSE = "Reverse";
	public static final String DIA_TITLE_CONDITION_INPUT = "Condition Input";

	// ClosureListInputBuilderDialog
	public static final String DIA_TITLE_CLOSURE_LIST_INPUT = "Closure List Input";
	public static final String DIA_COL_NO = NO_;

	// ForInputBuilderDialog
	public static final String DIA_TITLE_FOR_INPUT = "For Input";

	// InputBuilderDialog
	public static final String DIA_TITLE_INPUT = INPUT;
	public static final String DIA_COL_PARAM = "Param";
	public static final String DIA_COL_PARAM_TYPE = "Param Type";

	// ListInputBuilderDialog
	public static final String DIA_TITLE_LIST_INPUT = "List Input";
	public static final String DIA_BTN_INSERT = INSERT;
	public static final String DIA_BTN_REMOVE = REMOVE;

	// MapInputBuilderDialog
	public static final String DIA_TITLE_MAP_INPUT = "Map Input";
	public static final String DIA_COL_KEY = "Key";
	public static final String DIA_COL_KEY_TYPE = "Key Type";

	// MethodCallInputBuilderDialog
	public static final String DIA_TITLE_METHOD_CALL_INPUT = "Method Call Input";
	public static final String DIA_COL_METHOD = "Method";
	public static final String DIA_COL_INPUT = INPUT;
	public static final String DIA_COL_OBJ_TYPE = "Object Type";

	// NewTestCaseDialog
	public static final String DIA_TITLE_TEST_CASE = TEST_CASE;
	public static final String DIA_MSG_CREATE_NEW_TEST_CASE = "Create Test Case";

	// PropertyInputBuilderDialog
	public static final String DIA_TITLE_PROPERTY_INPUT = "Property Input";
	public static final String DIA_COL_PROPERTY = "Property";

	// PropertyInputBuilderDialog
	public static final String DIA_TITLE_RANGE_INPUT = "Range Input";

	// PropertyInputBuilderDialog
	public static final String DIA_TITLE_TEST_DATA_VALUE_INPUT = "Test Data Value Input";

	// TestObjectBuilderDialog
	public static final String DIA_TITLE_TEST_OBJ_INPUT = "Test Object Input";
	public static final String DIA_TAB_OTHER = "Other";
	public static final String DIA_LBL_OBJ_TYPE = DIA_COL_OBJ_TYPE;

	// MethodObjectBuilderDialog
	public static final String DIA_TITLE_METHOD_BUILDER = "Method builder";
	public static final String DIA_COL_PARAM_NAME = "Param Name";
	public static final String PA_LBL_RETURN_TYPE = "Return type";
	public static final String PA_LBL_RETURN_TYPE_BROWSE_BUTTON = "...";
	public static final String DIA_ERROR_METHOD_NAME_EMPTY = "Unable to build method: method name is empty";
	public static final String DIA_ERROR_METHOD_NAME_X_INVALID_JAVA_KEYWORD = "Unable to build method: method name ''{0}'' is invalid because it is an internal keyword";
	public static final String PA_LBL_SET_UP_BUTTON = "Set up";
	public static final String PA_LBL_TEAR_DOWN_BUTTON = "Tear Down";
	public static final String PA_LBL_TEAR_DOWN_FAILED_BUTTON = "Tear Down If FAILED";
	public static final String PA_LBL_TEAR_DOWN_PASSED_BUTTON = "Tear Down If PASSED";
	public static final String PA_LBL_TEAR_DOWN_ERROR_BUTTON = "Tear Down If ERROR";

	// AbstractDialogCellEditor
	public static final String EDI_MSG_VALIDATOR_REQUIRE_MESSAGE = "The cell editor required object as type {0} to work.";

	// CallTestCaseCellEditor
	public static final String EDI_TITLE_TEST_CASE_BROWSER = "Test Case Browser";
	public static final String EDI_ERROR_MSG_CANNOT_OPEN_DIALOG = "Can not open dialog";
	public static final String ERROR_TITLE = ERROR;

	// DeleteTestCaseHandler
	public static final String HAND_ERROR_MSG_UNABLE_TO_DEL_TEST_CASE = "Unable to delete Test Case.";

	// NewTestCaseHandler
	public static final String HAND_NEW_TEST_CASE = "New Test Case";
	public static final String HAND_ERROR_MSG_UNABLE_TO_CREATE_TEST_CASE = "Unable to create Test Case";

	// OpenTestCaseHandler
	public static final String HAND_DEFAULT_CONTAINER_DATA = "100";
	public static final String HAND_ERROR_MSG_CANNOT_OPEN_TEST_CASE = "Cannot open Test Case.";
	public static final String BUNDLE_URI_TEST_CASE = "bundleclass://com.kms.katalon.composer.testcase/";

	// RenameTestCaseHandler
	public static final String HAND_ERROR_MSG_UNABLE_TO_RENAME_TEST_CASE = "Unable to rename Test Case";

	// NewTestCasePopupMenuContribution
	public static final String MENU_CONTEXT_TEST_CASE = TEST_CASE;
	public static final String COMMAND_ID_ADD_TEST_CASE = "com.kms.katalon.composer.testcase.command.add";

	// TestCaseCompositePart
	public static final String PA_TAB_MANUAL = "Manual";
	public static final String PA_TAB_SCRIPT = "Script";
	public static final String PA_TAB_VARIABLE = "Variables";
	public static final String PA_TAB_INTEGRATION = "Integration";
	public static final String PA_ERROR_MSG_PLS_FIX_ERROR_IN_SCRIPT = "There are errors in the script. Please fix it before switching to manual mode.";
	public static final String PA_ERROR_MSG_UNABLE_TO_SAVE_PART = "Unable to save part.";
	public static final String WARN_TITLE = WARN;

	// TestCasePart
	public static final String PA_TOOLBAR_RECORD = "Record";
	public static final String PA_TOOLBAR_TIP_RECORD_TEST = "Record test";
	public static final String PA_LBL_GENERAL_INFO = "General Information";
	public static final String PA_LBL_ID = ID;
	public static final String PA_LBL_NAME = NAME;
	public static final String PA_LBL_COMMENT = COMMENT;
	public static final String PA_LBL_TAG = TAG;
	public static final String PA_LBL_DESCRIPTION = DESCRIPTION;
	public static final String PA_BTN_TIP_ADD = ADD;
	public static final String PA_BTN_TIP_INSERT = INSERT;
	public static final String PA_MENU_SUB_BEFORE = "Insert before";
	public static final String PA_MENU_SUB_AFTER = "Insert after";
	public static final String PA_BTN_TIP_REMOVE = REMOVE;
	public static final String PA_BTN_TIP_CLEAR = CLEAR;
	public static final String PA_BTN_TIP_EDIT = EDIT;
	public static final String PA_BTN_TIP_MOVE_UP = "Move up";
	public static final String PA_BTN_TIP_MOVE_DOWN = "Move down";
	public static final String PA_COL_INDEX = "#";
	public static final String PA_COL_ITEM = "Item";
	public static final String PA_COL_OBJ = OBJECT;
	public static final String PA_COL_INPUT = INPUT;
	public static final String PA_COL_OUTPUT = OUTPUT;
	public static final String PA_COL_DESCRIPTION = DESCRIPTION;
	public static final String PA_ERROR_MSG_FAILED_TO_LOAD_TEST_CASE = "Failed to load Test Case.";
	public static final String PA_ERROR_MSG_CANNOT_ADD_STATEMENTS = "Cannot add statements.";
	public static final String PA_ERROR_MSG_CANNOT_ADD_STATEMENT = "Cannot add statement.";
	public static final String PA_ERROR_MSG_CANNOT_ADD_KEYWORD = "Cannot add keyword.";
	public static final String PA_ERROR_MSG_NO_CUSTOM_KEYWORD = "There is no custom keyword.";
	public static final String PA_ERROR_MSG_CANNOT_ADD_IF_STATEMENT = "Cannot add IF statement.";
	public static final String PA_ERROR_MSG_CANNOT_ADD_ELSE_STATEMENT = "Cannot add ELSE statement.";
	public static final String PA_ERROR_MSG_CANNOT_ADD_ELSE_IF_STATEMENT = "Cannot add ELSE IF statement.";
	public static final String PA_ERROR_MSG_CANNOT_ADD_WHILE_STATEMENT = "Cannot add WHILE statement.";
	public static final String PA_ERROR_MSG_CANNOT_ADD_FOR_STATEMENT = "Cannot add FOR statement.";
	public static final String PA_ERROR_MSG_CANNOT_ADD_BINARY_STATEMENT = "Cannot add BINARY statement.";
	public static final String PA_ERROR_MSG_CANNOT_ADD_ASSERT_STATEMENT = "Cannot add ASSERT statement.";
	public static final String PA_ERROR_MSG_CANNOT_ADD_METHOD_CALL_STATEMENT = "Cannot add METHOD CALL statement.";
	public static final String PA_ERROR_MSG_CANNOT_CHANGE_FAILURE_HANDLING = "Cannot change failure handling.";
	public static final String PA_ERROR_MSG_CANNOT_REMOVE_STATEMENT = "Cannot remove statement.";
	public static final String PA_ERROR_MSG_CANNOT_MOVE_STATEMENT_UP = "Cannot move statement up.";
	public static final String PA_ERROR_MSG_CANNOT_MOVE_STATEMENT_DOWN = "Cannot move statement down.";
	public static final String PA_ERROR_MSG_UNABLE_TO_CALL_TEST_CASE = "Unable to call Test Case(s).";
	public static final String PA_ERROR_MSG_TEST_CASE_CANNOT_CALL_ITSELF = "Test Case cannot call itself.";
	public static final String PA_ERROR_MSG_CANNOT_ADD_CALL_TESTCASE = "Cannot add Call Test Case.";
	public static final String PA_ERROR_MSG_CANNOT_COPY = "Cannot copy.";
	public static final String PA_ERROR_MSG_CANNOT_CUT = "Cannot cut.";
	public static final String PA_ERROR_MSG_CANNOT_PASTE = "Cannot paste.";
	public static final String PA_ERROR_MSG_CANNOT_COMMENT = "Cannot comment.";
	public static final String PA_ERROR_MSG_CANNOT_UNCOMMENT = "Cannot uncomment.";
	public static final String PA_ERROR_MSG_CANNOT_ADD_DESCRIPTION = "Cannot add description";
	public static final String PA_ERROR_MSG_CANNOT_ADD_METHOD = "Cannot add method";

	// TestCaseVariablePart
	public static final String PA_COL_NO = NO_;
	public static final String PA_COL_NAME = NAME;
	public static final String PA_COL_DEFAULT_VALUE = "Default value";
	public static final String PA_ERROR_MSG_VAR_AT_INDEX_CANNOT_BE_NULL_OR_EMPTY = "The variable at index: {0} cannot be null or empty.\n";
	public static final String PA_ERROR_MSG_INVALID_VAR = "Variable {0} is not valid qualifier.\n";
	public static final String PA_ERROR_MSG_DUPLICATE_VAR = "Variable {0} is duplicated.\n";
	public static final String PA_ERROR_MSG_INVALID_DEFAULT_VAR_VAL_AT_INDEX = "Default value of variable at index {0} is not valid: {1}.\n";
	public static final String PA_ERROR_MSG_UNABLE_TO_SAVE_TEST_CASE = "Unable to save Test Case.";
	public static final String PA_ERROR_REASON_INVALID_TEST_CASE = "Test Case is not valid.";

	// TestCasePreferencePage
	public static final String PREF_TITLE_TEST_CASE = TEST_CASE;
	public static final String PREF_GRP_DEFAULT_VAR_TYPE = "Default Variable Type";
	public static final String PREF_GRP_TEST_CASE_CALLING = "Test Case Calling";
	public static final String PREF_BTN_GEN_VAR_WITH_DEFAULT_VAL = "Generate variable with default value";
	public static final String PREF_BTN_GEN_VAR_WITH_THE_SAME_NAME = "Generate variable with the same name as the exposed variable of the called test case";
	public static final String PREF_BTN_EXPOSE_VARS_AUTO = "Expose variables automatically after choosing the called test case";
	public static final String PREF_LBL_DEFAULT_FAILURE_HANDLING = "Default Failure Handling";
	public static final String PREF_GRP_DEFAULT_KEYWORD = "Default Keyword";
	public static final String PREF_LBL_KEYWORK_TYPE = "Keyword Type";
	public static final String PREF_LBL_KEYWORK_NAME = "Keyword Name";

	// AstAssertStatementTreeTableNode
	public static final String TREE_ASSERT_STATEMENT = "Assert Statement";

	// AstBinaryStatementTreeTableNode
	public static final String TREE_BINARY_STATEMENT = "Binary Statement";

	// AstCaseStatementTreeTableNode
	public static final String TREE_CASE_STATEMENT = "Case Statement";

	// AstCatchStatementTreeTableNode
	public static final String TREE_CATCH_STATEMENT = "Catch Statement";

	// AstClassTreeTableNode
	public static final String TREE_CLASS = "Class";

	// AstCommentStatementTreeTableNode
	public static final String TREE_COMMENT = "Comment";

	// AstElseIfStatementTreeTableNode
	public static final String TREE_ELSE_IF_STATEMENT = "Else If Statement";
	public static final String TREE_ELSE_VALUE = "Else {0}";

	// AstElseStatementTreeTableNode
	public static final String TREE_ELSE_STATEMENT = "Else Statement";

	// AstFieldTreeTableNode
	public static final String TREE_FIELD = "Field";

	// AstForStatementTreeTableNode
	public static final String TREE_FOR_STATEMENT = "For Statement";

	// AstWhileStatementTreeTableNode
	public static final String TREE_WHILE_STATEMENT = "While Statement";

	// AstIfStatementTreeTableNode
	public static final String TREE_IF_STATEMENT = "If Statement";

	// AstStatementTreeTableNode
	public static final String TREE_STATEMENT = "Statement";

	// AstSwitchDefaultStatementTreeTableNode
	public static final String TREE_DEFAULT_STATEMENT = "Default Statement";

	// AstSwitchStatementTreeTableNode
	public static final String TREE_SWITCH_STATEMENT = "Switch Statement";

	// AstTryStatementTreeTableNode
	public static final String TREE_TRY_STATEMENT = "Try Statement";
	
	// AstBinaryStatementTreeTableNode
	public static final String TREE_METHOD_CALL_STATEMENT = "Method Call Statement";

	// KeywordBrowserPart
	public static final String KEYWORD_BROWSER_BUILTIN_KEYWORD_ROOT_TREE_ITEM_LABEL = "Built-in Keywords";
	public static final String KEYWORD_BROWSER_CUSTOM_KEYWORD_ROOT_TREE_ITEM_LABEL = "Custom Keywords";
}
