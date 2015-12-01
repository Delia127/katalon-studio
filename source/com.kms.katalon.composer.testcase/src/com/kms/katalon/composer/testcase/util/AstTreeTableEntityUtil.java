package com.kms.katalon.composer.testcase.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants;
import com.kms.katalon.composer.testcase.constants.TreeTableMenuItemConstants.AddAction;
import com.kms.katalon.composer.testcase.model.InputValueType;
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.keyword.IKeywordContributor;
import com.kms.katalon.core.testdata.TestDataFactory;

public class AstTreeTableEntityUtil {

    // Can only add keyword statement for now
    public static ExpressionStatement getNewKeyword(boolean custom, String builtInKeywordClassName) throws Exception {
        ExpressionStatement keywordCallStatement = null;
        if (custom) {
            List<MethodNode> customKeywords = KeywordController.getInstance().getCustomKeywords(
                    ProjectController.getInstance().getCurrentProject());
            if (customKeywords != null && !customKeywords.isEmpty()) {
                String rawKeywordName = customKeywords.get(0).getName();
                keywordCallStatement = AstTreeTableInputUtil.createCustomKeywordMethodCall(customKeywords.get(0)
                        .getDeclaringClass().getName(),
                        KeywordController.getInstance().getCustomKeywordName(rawKeywordName));
            }
        } else {
            List<Method> builtInKeywords = KeywordController.getInstance().getBuiltInKeywords(builtInKeywordClassName);
            if (builtInKeywords != null && !builtInKeywords.isEmpty()) {
                keywordCallStatement = AstTreeTableInputUtil.createBuiltInKeywordMethodCall(builtInKeywordClassName,
                        builtInKeywords.get(0).getName());
            }
        }
        return keywordCallStatement;
    }

    public static IfStatement getNewIfStatement() {
        return new IfStatement(new BooleanExpression(new ConstantExpression(true)), new BlockStatement(),
                new EmptyStatement());
    }

    public static WhileStatement getNewWhileStatement() {
        return new WhileStatement(new BooleanExpression(new ConstantExpression(true)), new BlockStatement());
    }

    public static ForStatement getNewForStatement() {
        return new ForStatement(new Parameter(new ClassNode(Object.class), "index"), new RangeExpression(
                new ConstantExpression(0), new ConstantExpression(0), true), new BlockStatement());
    }

    public static SwitchStatement getNewSwitchStatement() {
        return new SwitchStatement(new BooleanExpression(new ConstantExpression(true)), getNewDefaultStatement());
    }

    public static CaseStatement getNewCaseStatement() {
        return new CaseStatement(new BooleanExpression(new ConstantExpression(true)), getNewDefaultStatement());
    }
    
    public static Statement getNewDefaultStatement() {
        BlockStatement blockStatement = new BlockStatement();
        blockStatement.addStatement(new BreakStatement());
        return blockStatement;
    }

    public static CatchStatement getNewCatchStatement() {
        return new CatchStatement(new Parameter(new ClassNode(Exception.class), "e"), new BlockStatement());
    }

    public static TryCatchStatement getNewTryCatchStatement() {
        return new TryCatchStatement(new BlockStatement(), getNewFinallyStatement());
    }
    
    public static Statement getNewFinallyStatement() {
        BlockStatement blockStatement = new BlockStatement();
        blockStatement.addStatement(new BlockStatement());
        return blockStatement;
    }
    
    public static ReturnStatement getNewReturnStatement() {
        return new ReturnStatement(new ConstantExpression(null));
    }

    public static ConstantExpression getNewStringConstantExpression() {
        return new ConstantExpression("");
    }

    public static ConstantExpression getNewNumberConstantExpression() {
        return new ConstantExpression(0);
    }

    public static ConstantExpression getNewBooleanConstantExpression() {
        return new ConstantExpression(true);
    }

    public static ConstantExpression getNewConstantExpression(Object object) {
        return new ConstantExpression(object);
    }

    public static VariableExpression getNewVariableExpression() {
        return new VariableExpression("a");
    }

    public static BinaryExpression getNewBinaryExpression() {
        return new BinaryExpression(new ConstantExpression(null), Token.newSymbol(Types.COMPARE_EQUAL, -1, -1),
                new ConstantExpression(null));
    }

    public static MethodCallExpression getNewMethodCallExpression() {
        return new MethodCallExpression(new ConstantExpression(""), "toString", new ArgumentListExpression());
    }

    public static MethodCallExpression getNewTestDataExpression(Expression objectExpression) {
        ArgumentListExpression argumentExpression = new ArgumentListExpression();
        argumentExpression.addExpression(objectExpression);
        return new MethodCallExpression(new VariableExpression(TestDataFactory.class.getSimpleName()), "findTestData",
                argumentExpression);
    }

    public static MethodCallExpression getNewTestDataValueExpression(Expression objectExpression,
            Expression columnExpression, Expression rowExpression) {
        ArgumentListExpression argumentExpression = new ArgumentListExpression();
        argumentExpression.addExpression(columnExpression);
        argumentExpression.addExpression(rowExpression);
        return new MethodCallExpression(objectExpression, "getValue", argumentExpression);
    }

    public static MethodCallExpression getNewTestCaseExpression() {
        return AstTreeTableInputUtil.generateTestCaseMethodCall(null);
    }

    public static ClosureListExpression getNewClosureListExpression() {
        List<Expression> expressionList = new LinkedList<Expression>();
        expressionList.add(new BinaryExpression(new VariableExpression("index"), Token.newSymbol(Types.EQUAL, -1, -1),
                new ConstantExpression(0)));
        expressionList.add(new BinaryExpression(new VariableExpression("index"), Token.newSymbol(
                Types.COMPARE_LESS_THAN, -1, -1), new ConstantExpression(0)));
        expressionList.add(new BinaryExpression(new VariableExpression("index"), Token.newSymbol(Types.PLUS, -1, -1),
                new ConstantExpression(1)));
        return new ClosureListExpression(expressionList);
    }

    public static BooleanExpression getNewBooleanExpression() {
        return new BooleanExpression(new ConstantExpression(true));
    }

    public static MapExpression getNewMapExpression() {
        return new MapExpression(new ArrayList<MapEntryExpression>());
    }

    public static MapEntryExpression getNewMapEntryExpression() {
        return new MapEntryExpression(getNewStringConstantExpression(), getNewStringConstantExpression());
    }

    public static ListExpression getNewListExpression() {
        return new ListExpression(new ArrayList<Expression>());
    }

    public static RangeExpression getNewRangeExpression() {
        return new RangeExpression(new ConstantExpression(0), new ConstantExpression(0), true);
    }

    public static PropertyExpression getNewPropertyExpression() {
        return new PropertyExpression(new VariableExpression("this"), new ConstantExpression(null));
    }

    public static PropertyExpression getNewGlobalVariablePropertyExpression() {
        return new PropertyExpression(new VariableExpression(InputValueType.GlobalVariable.name()),
                new ConstantExpression(null));
    }

    public static Expression createNewPropertyExpressionFromTypeName(String typeName) {
        int index = typeName.lastIndexOf('.');
        if (index != -1 && index < typeName.length()) {
            return new PropertyExpression(createNewPropertyExpressionFromTypeName(typeName.substring(0, index)),
                    typeName.substring(index + 1, typeName.length()));
        } else {
            return new VariableExpression(typeName);
        }
    }

    public static Expression createNewClassExpressionFromType(Class<?> type) {
        return new ClassExpression(new ClassNode(type));
    }

    public static MenuItem addActionSubMenu(Menu menu, AddAction addAction, String MenuText,
            SelectionListener selectionListener) {
        MenuItem actionMenuItem = new MenuItem(menu, SWT.CASCADE);
        actionMenuItem.setText(MenuText);

        Menu actionMenu = new Menu(menu);
        actionMenuItem.setMenu(actionMenu);

        fillActionMenu(addAction, selectionListener, actionMenu);

        return actionMenuItem;
    }

    public static void fillActionMenu(AddAction addAction, SelectionListener selectionListener, Menu actionMenu) {

        addBuiltInKeywordMenuItems(addAction, selectionListener, actionMenu);

        addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.CUSTOM_KEYWORD_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.CUSTOM_KEYWORD_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.CALL_TEST_CASE_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.CALL_TEST_CASE_MENU_ITEM_ID, SWT.PUSH);

        MenuItem decisionMakingStatementsMenuItem = addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.DECISION_MAKING_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.DECISION_MAKING_STATEMENT_MENU_ITEM_ID, SWT.CASCADE);

        Menu decisionMakingStatementsMenu = new Menu(actionMenu);
        decisionMakingStatementsMenuItem.setMenu(decisionMakingStatementsMenu);

        addNewMenuItem(addAction, selectionListener, decisionMakingStatementsMenu,
                TreeTableMenuItemConstants.IF_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.IF_STATEMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, decisionMakingStatementsMenu,
                TreeTableMenuItemConstants.ELSE_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.ELSE_STATEMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, decisionMakingStatementsMenu,
                TreeTableMenuItemConstants.ELSE_IF_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.ELSE_IF_STATEMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, decisionMakingStatementsMenu,
                TreeTableMenuItemConstants.SWITCH_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.SWITCH_STATMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, decisionMakingStatementsMenu,
                TreeTableMenuItemConstants.CASE_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.CASE_STATMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, decisionMakingStatementsMenu,
                TreeTableMenuItemConstants.DEFAULT_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.DEFAULT_STATMENT_MENU_ITEM_ID, SWT.PUSH);
        
        MenuItem loopingStatementsMenuItem = addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.LOOPING_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.LOOPING_STATEMENT_MENU_ITEM_ID, SWT.CASCADE);

        Menu loopingMakingStatementsMenu = new Menu(actionMenu);
        loopingStatementsMenuItem.setMenu(loopingMakingStatementsMenu);
        
        addNewMenuItem(addAction, selectionListener, loopingMakingStatementsMenu,
                TreeTableMenuItemConstants.FOR_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.FOR_STATEMENT_MENU_ITEM_ID, SWT.PUSH);
        
        addNewMenuItem(addAction, selectionListener, loopingMakingStatementsMenu,
                TreeTableMenuItemConstants.WHILE_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.WHILE_STATEMENT_MENU_ITEM_ID, SWT.PUSH);
        
        MenuItem branchingStatementsMenuItem = addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.BRANCHING_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.BRANCHING_STATEMENT_MENU_ITEM_ID, SWT.CASCADE);

        Menu branchingMakingStatementsMenu = new Menu(actionMenu);
        branchingStatementsMenuItem.setMenu(branchingMakingStatementsMenu);
        
        addNewMenuItem(addAction, selectionListener, branchingMakingStatementsMenu,
                TreeTableMenuItemConstants.BREAK_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.BREAK_STATMENT_MENU_ITEM_ID, SWT.PUSH);
        
        addNewMenuItem(addAction, selectionListener, branchingMakingStatementsMenu,
                TreeTableMenuItemConstants.CONTINUE_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.CONTINUE_STATMENT_MENU_ITEM_ID, SWT.PUSH);
        
        addNewMenuItem(addAction, selectionListener, branchingMakingStatementsMenu,
                TreeTableMenuItemConstants.RETURN_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.RETURN_STATMENT_MENU_ITEM_ID, SWT.PUSH);
        
        MenuItem exceptionHandlingStatementsMenuItem = addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.EXCEPTION_HANDLING_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.EXCEPTION_HANDLING_STATEMENT_MENU_ITEM_ID, SWT.CASCADE);
        
        Menu exceptionHandlingStatementsMenu = new Menu(actionMenu);
        exceptionHandlingStatementsMenuItem.setMenu(exceptionHandlingStatementsMenu);
        
        addNewMenuItem(addAction, selectionListener, exceptionHandlingStatementsMenu,
                TreeTableMenuItemConstants.TRY_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.TRY_STATEMENT_MENU_ITEM_ID, SWT.PUSH);
        
        addNewMenuItem(addAction, selectionListener, exceptionHandlingStatementsMenu,
                TreeTableMenuItemConstants.CATCH_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.CATCH_STATMENT_MENU_ITEM_ID, SWT.PUSH);
        
        addNewMenuItem(addAction, selectionListener, exceptionHandlingStatementsMenu,
                TreeTableMenuItemConstants.FINALLY_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.FINALLY_STATMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.BINARY_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.BINARY_STATEMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.ASSERT_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.ASSERT_STATEMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, actionMenu,
                TreeTableMenuItemConstants.CALL_METHOD_STATEMENT_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.CALL_METHOD_STATMENT_MENU_ITEM_ID, SWT.PUSH);

        addNewMenuItem(addAction, selectionListener, actionMenu, TreeTableMenuItemConstants.METHOD_MENU_ITEM_LABEL,
                TreeTableMenuItemConstants.METHOD_MENU_ITEM_ID, SWT.PUSH);
    }

    private static MenuItem addNewMenuItem(AddAction addAction, SelectionListener selectionListener, Menu actionMenu,
            String text, int id, int type) {
        MenuItem newMenuItem = new MenuItem(actionMenu, type);
        newMenuItem.setText(text);
        newMenuItem.addSelectionListener(selectionListener);
        newMenuItem.setID(id);
        newMenuItem.setData(TreeTableMenuItemConstants.MENU_ITEM_ACTION_KEY, addAction);
        return newMenuItem;
    }

    private static void addBuiltInKeywordMenuItems(AddAction addAction, SelectionListener selectionListener,
            Menu actionMenu) {
        // preBuild
        TreeTableMenuItemConstants.generateBuiltInKeywordMenuItemIDs(KeywordController.getInstance()
                .getBuiltInKeywordClasses());
        List<IKeywordContributor> contributors = KeywordController.getInstance().getBuiltInKeywordContributors();
        for (IKeywordContributor contributor : contributors) {
            addNewMenuItem(addAction, selectionListener, actionMenu, contributor.getLabelName(),
                    TreeTableMenuItemConstants.getMenuItemID(contributor.getKeywordClass().getName()), SWT.PUSH);
        }
    }
}
