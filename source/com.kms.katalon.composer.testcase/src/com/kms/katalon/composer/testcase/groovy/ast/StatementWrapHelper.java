package com.kms.katalon.composer.testcase.groovy.ast;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;

import com.kms.katalon.composer.testcase.groovy.ast.statements.AssertStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BlockStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BreakStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ContinueStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.DoWhileStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ExpressionStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ForStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.IfStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ReturnStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.StatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.SwitchStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.SynchronizedStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ThrowStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.TryCatchStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.WhileStatementWrapper;

public class StatementWrapHelper {
    interface StatementWrapperConverter<T extends Statement> extends ASTWrapperConverter<T> {
        @Override
        public StatementWrapper wrap(T node, ASTNodeWrapper parentNode);
    }

    private static Map<String, StatementWrapperConverter<? extends Statement>> statementWrapperConverterMap;

    private static final StatementWrapperConverter<BlockStatement> blockStatementWrapperConverter = new StatementWrapperConverter<BlockStatement>() {
        @Override
        public BlockStatementWrapper wrap(BlockStatement node, ASTNodeWrapper parentNode) {
            return new BlockStatementWrapper(node, parentNode);
        }
    };

    private static final StatementWrapperConverter<ExpressionStatement> expressionStatementWrapperConverter = new StatementWrapperConverter<ExpressionStatement>() {
        @Override
        public ExpressionStatementWrapper wrap(ExpressionStatement node, ASTNodeWrapper parentNode) {
            return new ExpressionStatementWrapper(node, parentNode);
        }
    };

    private static final StatementWrapperConverter<ReturnStatement> returnStatementWrapperConverter = new StatementWrapperConverter<ReturnStatement>() {
        @Override
        public ReturnStatementWrapper wrap(ReturnStatement node, ASTNodeWrapper parentNode) {
            return new ReturnStatementWrapper(node, parentNode);
        }
    };

    private static final StatementWrapperConverter<AssertStatement> assertStatementWrapperConverter = new StatementWrapperConverter<AssertStatement>() {
        @Override
        public AssertStatementWrapper wrap(AssertStatement node, ASTNodeWrapper parentNode) {
            return new AssertStatementWrapper(node, parentNode);
        }
    };

    private static final StatementWrapperConverter<SwitchStatement> switchStatementWrapperConverter = new StatementWrapperConverter<SwitchStatement>() {
        @Override
        public SwitchStatementWrapper wrap(SwitchStatement node, ASTNodeWrapper parentNode) {
            return new SwitchStatementWrapper(node, parentNode);
        }
    };

    private static final StatementWrapperConverter<ThrowStatement> throwStatementWrapperConverter = new StatementWrapperConverter<ThrowStatement>() {
        @Override
        public ThrowStatementWrapper wrap(ThrowStatement node, ASTNodeWrapper parentNode) {
            return new ThrowStatementWrapper(node, parentNode);
        }
    };

    private static final StatementWrapperConverter<TryCatchStatement> tryCatchStatementWrapperConverter = new StatementWrapperConverter<TryCatchStatement>() {
        @Override
        public TryCatchStatementWrapper wrap(TryCatchStatement node, ASTNodeWrapper parentNode) {
            return new TryCatchStatementWrapper(node, parentNode);
        }
    };

    private static final StatementWrapperConverter<IfStatement> ifStatementWrapperConverter = new StatementWrapperConverter<IfStatement>() {
        @Override
        public IfStatementWrapper wrap(IfStatement node, ASTNodeWrapper parentNode) {
            return new IfStatementWrapper(node, parentNode);
        }
    };

    private static final StatementWrapperConverter<ForStatement> forStatementWrapperConverter = new StatementWrapperConverter<ForStatement>() {
        @Override
        public ForStatementWrapper wrap(ForStatement node, ASTNodeWrapper parentNode) {
            return new ForStatementWrapper(node, parentNode);
        }
    };

    private static final StatementWrapperConverter<WhileStatement> whileStatementWrapperConverter = new StatementWrapperConverter<WhileStatement>() {
        @Override
        public WhileStatementWrapper wrap(WhileStatement node, ASTNodeWrapper parentNode) {
            return new WhileStatementWrapper(node, parentNode);
        }
    };

    private static final StatementWrapperConverter<DoWhileStatement> doWhileStatementWrapperConverter = new StatementWrapperConverter<DoWhileStatement>() {
        @Override
        public DoWhileStatementWrapper wrap(DoWhileStatement node, ASTNodeWrapper parentNode) {
            return new DoWhileStatementWrapper(node, parentNode);
        }
    };

    private static final StatementWrapperConverter<SynchronizedStatement> synchronizedStatementWrapperConverter = new StatementWrapperConverter<SynchronizedStatement>() {
        @Override
        public SynchronizedStatementWrapper wrap(SynchronizedStatement node, ASTNodeWrapper parentNode) {
            return new SynchronizedStatementWrapper(node, parentNode);
        }
    };

    private static final StatementWrapperConverter<ContinueStatement> continueStatementWrapperConverter = new StatementWrapperConverter<ContinueStatement>() {
        @Override
        public ContinueStatementWrapper wrap(ContinueStatement node, ASTNodeWrapper parentNode) {
            return new ContinueStatementWrapper(node, parentNode);
        }
    };

    private static final StatementWrapperConverter<BreakStatement> breakStatementWrapperConverter = new StatementWrapperConverter<BreakStatement>() {
        @Override
        public BreakStatementWrapper wrap(BreakStatement node, ASTNodeWrapper parentNode) {
            return new BreakStatementWrapper(node, parentNode);
        }
    };

    static {
        initStatementWrapperConverterMap();
    }

    private static void initStatementWrapperConverterMap() {
        statementWrapperConverterMap = new HashMap<>();
        statementWrapperConverterMap.put(BlockStatement.class.getSimpleName(), blockStatementWrapperConverter);
        statementWrapperConverterMap.put(ExpressionStatement.class.getSimpleName(), expressionStatementWrapperConverter);
        statementWrapperConverterMap.put(ReturnStatement.class.getSimpleName(), returnStatementWrapperConverter);
        statementWrapperConverterMap.put(AssertStatement.class.getSimpleName(), assertStatementWrapperConverter);
        statementWrapperConverterMap.put(SwitchStatement.class.getSimpleName(), switchStatementWrapperConverter);
        statementWrapperConverterMap.put(ThrowStatement.class.getSimpleName(), throwStatementWrapperConverter);
        statementWrapperConverterMap.put(TryCatchStatement.class.getSimpleName(), tryCatchStatementWrapperConverter);
        statementWrapperConverterMap.put(IfStatement.class.getSimpleName(), ifStatementWrapperConverter);
        statementWrapperConverterMap.put(ForStatement.class.getSimpleName(), forStatementWrapperConverter);
        statementWrapperConverterMap.put(WhileStatement.class.getSimpleName(), whileStatementWrapperConverter);
        statementWrapperConverterMap.put(DoWhileStatement.class.getSimpleName(), doWhileStatementWrapperConverter);
        statementWrapperConverterMap.put(SynchronizedStatement.class.getSimpleName(),
                synchronizedStatementWrapperConverter);
        statementWrapperConverterMap.put(ContinueStatement.class.getSimpleName(), continueStatementWrapperConverter);
        statementWrapperConverterMap.put(BreakStatement.class.getSimpleName(), breakStatementWrapperConverter);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Statement> StatementWrapper wrap(T statement, ASTNodeWrapper parentNode) {
        StatementWrapperConverter<T> provider = (StatementWrapperConverter<T>) statementWrapperConverterMap.get(statement.getClass()
                .getSimpleName());
        if (provider != null) {
            return provider.wrap(statement, parentNode);
        }
        return null;
    }
}
