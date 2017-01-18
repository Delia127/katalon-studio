package com.kms.katalon.composer.testcase.addons;

import static com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants.generateBuiltInKeywordMenuItemIDs;
import static com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants.getMenuItemID;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.IEclipseContext;

import com.kms.katalon.composer.components.impl.command.KCommand;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.constants.EventConstants;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.core.keyword.internal.KeywordContributorCollection;

public class TestCaseManualCommandAddon {

    @PostConstruct
    public void postConstruct(IEclipseContext context) {
        context.set(EventConstants.TESTCASE_ADD_STEP, createTestCaseStepCommands());
    }

    private List<KCommand> createTestCaseStepCommands() {
        generateBuiltInKeywordMenuItemIDs(KeywordController.getInstance().getBuiltInKeywordClasses());

        List<KCommand> testStepCommands = KeywordContributorCollection.getKeywordContributors()
                .stream()
                .map(contributor -> KCommand.create(contributor.getLabelName())
                        .setEventName(EventConstants.TESTCASE_ADD_STEP)
                        .setEventData(getMenuItemID(contributor.getAliasName())))
                .collect(Collectors.toList());

        testStepCommands.add(KCommand.create(StringConstants.CONS_MENU_CONTEXT_CUSTOM_KEYWORD)
                .setEventName(EventConstants.TESTCASE_ADD_STEP)
                .setEventData(TreeTableMenuItemConstants.CUSTOM_KEYWORD_MENU_ITEM_ID));

        testStepCommands.add(KCommand.create(StringConstants.CONS_MENU_CONTEXT_CALL_TEST_CASE)
                .setEventName(EventConstants.KATALON_LOAD_COMMANDS)
                .setEventData(EventConstants.TESTCASE_ADD_STEP_CALL_TESTCASE));

        testStepCommands.add(KCommand.create(StringConstants.CONS_MENU_CONTEXT_DECISION_MAKING_STATEMENT)
                .addChild(KCommand.create(StringConstants.TREE_IF_STATEMENT)
                        .setEventName(EventConstants.TESTCASE_ADD_STEP)
                        .setEventData(TreeTableMenuItemConstants.IF_STATEMENT_MENU_ITEM_ID))
                .addChild(KCommand.create(StringConstants.TREE_ELSE_STATEMENT)
                        .setEventName(EventConstants.TESTCASE_ADD_STEP)
                        .setEventData(TreeTableMenuItemConstants.ELSE_STATEMENT_MENU_ITEM_ID))
                .addChild(KCommand.create(StringConstants.TREE_ELSE_IF_STATEMENT)
                        .setEventName(EventConstants.TESTCASE_ADD_STEP)
                        .setEventData(TreeTableMenuItemConstants.ELSE_IF_STATEMENT_MENU_ITEM_ID))
                .addChild(KCommand.create(StringConstants.TREE_SWITCH_STATEMENT)
                        .setEventName(EventConstants.TESTCASE_ADD_STEP)
                        .setEventData(TreeTableMenuItemConstants.SWITCH_STATMENT_MENU_ITEM_ID))
                .addChild(KCommand.create(StringConstants.TREE_CASE_STATEMENT)
                        .setEventName(EventConstants.TESTCASE_ADD_STEP)
                        .setEventData(TreeTableMenuItemConstants.CASE_STATMENT_MENU_ITEM_ID))
                .addChild(KCommand.create(StringConstants.TREE_DEFAULT_STATEMENT)
                        .setEventName(EventConstants.TESTCASE_ADD_STEP)
                        .setEventData(TreeTableMenuItemConstants.DEFAULT_STATMENT_MENU_ITEM_ID)));

        testStepCommands.add(KCommand.create(StringConstants.CONS_MENU_CONTEXT_LOOPING_STATEMENT)
                .addChild(KCommand.create(StringConstants.TREE_FOR_STATEMENT)
                        .setEventName(EventConstants.TESTCASE_ADD_STEP)
                        .setEventData(TreeTableMenuItemConstants.FOR_STATEMENT_MENU_ITEM_ID))
                .addChild(KCommand.create(StringConstants.TREE_WHILE_STATEMENT)
                        .setEventName(EventConstants.TESTCASE_ADD_STEP)
                        .setEventData(TreeTableMenuItemConstants.WHILE_STATEMENT_MENU_ITEM_ID)));

        testStepCommands.add(KCommand.create(StringConstants.CONS_MENU_CONTEXT_BRANCHING_STATEMENT)
                .addChild(KCommand.create(StringConstants.TREE_BREAK_STATEMENT)
                        .setEventName(EventConstants.TESTCASE_ADD_STEP)
                        .setEventData(TreeTableMenuItemConstants.BREAK_STATMENT_MENU_ITEM_ID))
                .addChild(KCommand.create(StringConstants.TREE_CONTINUE_STATEMENT)
                        .setEventName(EventConstants.TESTCASE_ADD_STEP)
                        .setEventData(TreeTableMenuItemConstants.CONTINUE_STATMENT_MENU_ITEM_ID))
                .addChild(KCommand.create(StringConstants.TREE_RETURN_STATEMENT)
                        .setEventName(EventConstants.TESTCASE_ADD_STEP)
                        .setEventData(TreeTableMenuItemConstants.RETURN_STATMENT_MENU_ITEM_ID)));

        testStepCommands.add(KCommand.create(StringConstants.CONS_MENU_CONTEXT_EXCEPTION_HANDLING_STATEMENT)
                .addChild(KCommand.create(StringConstants.TREE_TRY_STATEMENT)
                        .setEventName(EventConstants.TESTCASE_ADD_STEP)
                        .setEventData(TreeTableMenuItemConstants.TRY_STATEMENT_MENU_ITEM_ID))
                .addChild(KCommand.create(StringConstants.TREE_CATCH_STATEMENT)
                        .setEventName(EventConstants.TESTCASE_ADD_STEP)
                        .setEventData(TreeTableMenuItemConstants.CATCH_STATMENT_MENU_ITEM_ID))
                .addChild(KCommand.create(StringConstants.TREE_FINALLY_STATEMENT)
                        .setEventName(EventConstants.TESTCASE_ADD_STEP)
                        .setEventData(TreeTableMenuItemConstants.FINALLY_STATMENT_MENU_ITEM_ID))
                .addChild(KCommand.create(StringConstants.TREE_THROW_STATEMENT)
                        .setEventName(EventConstants.TESTCASE_ADD_STEP)
                        .setEventData(TreeTableMenuItemConstants.THROW_STATMENT_MENU_ITEM_ID)));

        testStepCommands.add(KCommand.create(StringConstants.TREE_BINARY_STATEMENT)
                .setEventName(EventConstants.TESTCASE_ADD_STEP)
                .setEventData(TreeTableMenuItemConstants.BINARY_STATEMENT_MENU_ITEM_ID));

        testStepCommands.add(KCommand.create(StringConstants.TREE_ASSERT_STATEMENT)
                .setEventName(EventConstants.TESTCASE_ADD_STEP)
                .setEventData(TreeTableMenuItemConstants.ASSERT_STATEMENT_MENU_ITEM_ID));

        testStepCommands.add(KCommand.create(StringConstants.TREE_METHOD_CALL_STATEMENT)
                .setEventName(EventConstants.TESTCASE_ADD_STEP)
                .setEventData(TreeTableMenuItemConstants.CALL_METHOD_STATEMENT_MENU_ITEM_ID));

        testStepCommands.add(KCommand.create(StringConstants.CONS_MENU_CONTEXT_METHOD)
                .setEventName(EventConstants.TESTCASE_ADD_STEP)
                .setEventData(TreeTableMenuItemConstants.METHOD_MENU_ITEM_ID));
        return Collections.unmodifiableList(testStepCommands);
    }
}
