package com.kms.katalon.composer.testcase.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.kms.katalon.composer.testcase.ast.treetable.AstAssertStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstBinaryStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstBreakStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstBuiltInKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCallTestCaseKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCaseStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCatchStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCommentStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstContinueStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCustomKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstDoWhileStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstElseIfStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstElseStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstExpressionStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstFinallyStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstForStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstIfStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstMethodCallStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstReturnStatementWrapper;
import com.kms.katalon.composer.testcase.ast.treetable.AstStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstSwitchStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstThrowStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTryStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstWhileStatementTreeTableNode;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BinaryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.AssertStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BreakStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CaseStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CatchStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ContinueStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.DoWhileStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ElseIfStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.EmptyStatementWrapper;
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
import com.kms.katalon.controller.KeywordController;
import com.kms.katalon.custom.keyword.KeywordClass;

/**
 * Created by taittle on 3/17/16.
 */
public class WrapperToAstTreeConverter {
    private static WrapperToAstTreeConverter instance;

    private static HashMap<String, Converter<? extends StatementWrapper>> converters;

    public static WrapperToAstTreeConverter getInstance() {
        if (instance == null) {
            instance = new WrapperToAstTreeConverter();
            initConverters();
        }

        return instance;
    }

    public List<AstTreeTableNode> convert(List<? extends StatementWrapper> statementWrappers, AstTreeTableNode parentNode) {
        List<AstTreeTableNode> astTreeTableNodes = new ArrayList<>();
        for (StatementWrapper statement : statementWrappers) {
            if (statement instanceof EmptyStatementWrapper) {
                continue;
            }

            // Lookup first
            Converter<StatementWrapper> converter = getConverter(statement);
            if (converter != null) {
                astTreeTableNodes.addAll(converter.convert(statement, parentNode));
                continue;
            }
        }
        return astTreeTableNodes;
    }

    private static Converter<ExpressionStatementWrapper> expressionConverter = new Converter<ExpressionStatementWrapper>() {
        private static final String CUSTOM_KEYWORDS_CLASS_NAME = "CustomKeywords";

        @Override
        public List<AstTreeTableNode> convert(final ExpressionStatementWrapper expressionStatement,
                final AstTreeTableNode parentNode) {
            return new ArrayList<AstTreeTableNode>() {
                private static final long serialVersionUID = 1L;
                {
                    add(convertExpressionWrapper(expressionStatement, parentNode));
                }
            };
        }

        public AstTreeTableNode convertExpressionWrapper(ExpressionStatementWrapper statementWrapper,
                AstTreeTableNode parentNode) {
            ExpressionWrapper expression = statementWrapper.getExpression();

            if (expression instanceof MethodCallExpressionWrapper) {
                return convert(statementWrapper, (MethodCallExpressionWrapper) expression, parentNode);
            } else if (expression instanceof BinaryExpressionWrapper) {
                return convert(statementWrapper, (BinaryExpressionWrapper) expression, parentNode);
            } else if (expression instanceof ConstantExpressionWrapper
                    && (((ConstantExpressionWrapper) expression).getValue() instanceof String)) {
                return new AstCommentStatementTreeTableNode(statementWrapper, parentNode);
            }

            return new AstExpressionStatementTreeTableNode(statementWrapper, parentNode);
        }

        private AstTreeTableNode convert(ExpressionStatementWrapper statementWrapper,
                BinaryExpressionWrapper expression, AstTreeTableNode parentNode) {
            if (expression.getRightExpression() instanceof MethodCallExpressionWrapper) {
                MethodCallExpressionWrapper methodCallExpression = (MethodCallExpressionWrapper) expression.getRightExpression();

                if (isBuiltInKeywordMethodCall(methodCallExpression)) {
                    return new AstBuiltInKeywordTreeTableNode(statementWrapper, parentNode);
                } else if (isCustomKeywordMethodCall(methodCallExpression)) {
                    return new AstCustomKeywordTreeTableNode(statementWrapper, parentNode);
                }
            }

            return new AstBinaryStatementTreeTableNode(statementWrapper, parentNode);
        }

        private AstTreeTableNode convert(ExpressionStatementWrapper statementWrapper,
                MethodCallExpressionWrapper methodCallExpression, AstTreeTableNode parentNode) {
            if (isBuiltInKeywordMethodCall(methodCallExpression)) {
                if (AstEntityInputUtil.isCallTestCaseMethod(methodCallExpression)) {
                    return new AstCallTestCaseKeywordTreeTableNode(statementWrapper, parentNode);
                }

                return new AstBuiltInKeywordTreeTableNode(statementWrapper, parentNode);
            } else if (isCustomKeywordMethodCall(methodCallExpression)) {
                return new AstCustomKeywordTreeTableNode(statementWrapper, parentNode);
            }

            return new AstMethodCallStatementTreeTableNode(statementWrapper, parentNode);
        }

        private boolean isBuiltInKeywordMethodCall(MethodCallExpressionWrapper methodCallExpression) {
            if (methodCallExpression == null || methodCallExpression.getObjectExpression() == null) {
                return false;
            }
            for (KeywordClass keywordClass : KeywordController.getInstance().getBuiltInKeywordClasses()) {
                if (methodCallExpression.isObjectExpressionOfClass(keywordClass.getType())) {
                    return true;
                }
            }
            return false;
        }

        private boolean isCustomKeywordMethodCall(MethodCallExpressionWrapper methodCallExpression) {
            return methodCallExpression != null && methodCallExpression.getObjectExpression() != null
                    && methodCallExpression.getObjectExpressionAsString().equals(CUSTOM_KEYWORDS_CLASS_NAME);
        }
    };

    private static Converter<ForStatementWrapper> forConverter = new Converter<ForStatementWrapper>() {
        @Override
        public List<AstTreeTableNode> convert(final ForStatementWrapper forStatement, final AstTreeTableNode parentNode) {
            return new ArrayList<AstTreeTableNode>() {
                private static final long serialVersionUID = 1L;
                {
                    add(new AstForStatementTreeTableNode(forStatement, parentNode));
                }
            };
        }
    };

    private static Converter<WhileStatementWrapper> whileConverter = new Converter<WhileStatementWrapper>() {
        @Override
        public List<AstTreeTableNode> convert(final WhileStatementWrapper whileStatement,
                final AstTreeTableNode parentNode) {
            return new ArrayList<AstTreeTableNode>() {
                private static final long serialVersionUID = 1L;
                {
                    add(new AstWhileStatementTreeTableNode(whileStatement, parentNode));
                }
            };
        }
    };

    private static Converter<SwitchStatementWrapper> switchConverter = new Converter<SwitchStatementWrapper>() {
        @Override
        public List<AstTreeTableNode> convert(final SwitchStatementWrapper switchStatement,
                final AstTreeTableNode parentNode) {
            return new ArrayList<AstTreeTableNode>() {
                private static final long serialVersionUID = 1L;
                {
                    add(new AstSwitchStatementTreeTableNode(switchStatement, parentNode));
                }
            };
        }
    };

    private static Converter<CaseStatementWrapper> caseConverter = new Converter<CaseStatementWrapper>() {
        @Override
        public List<AstTreeTableNode> convert(final CaseStatementWrapper caseStatement,
                final AstTreeTableNode parentNode) {
            return new ArrayList<AstTreeTableNode>() {
                private static final long serialVersionUID = 1L;
                {
                    add(new AstCaseStatementTreeTableNode(caseStatement, parentNode));
                }
            };
        }
    };

    private static Converter<AssertStatementWrapper> assertConverter = new Converter<AssertStatementWrapper>() {
        @Override
        public List<AstTreeTableNode> convert(final AssertStatementWrapper assertStatement,
                final AstTreeTableNode parentNode) {
            return new ArrayList<AstTreeTableNode>() {
                private static final long serialVersionUID = 1L;
                {
                    add(new AstAssertStatementTreeTableNode(assertStatement, parentNode));
                }
            };
        }
    };

    private static Converter<BreakStatementWrapper> breakConverter = new Converter<BreakStatementWrapper>() {
        @Override
        public List<AstTreeTableNode> convert(final BreakStatementWrapper breakStatement,
                final AstTreeTableNode parentNode) {
            return new ArrayList<AstTreeTableNode>() {
                private static final long serialVersionUID = 1L;
                {
                    add(new AstBreakStatementTreeTableNode(breakStatement, parentNode));
                }
            };
        }
    };

    private static Converter<ContinueStatementWrapper> continueConverter = new Converter<ContinueStatementWrapper>() {
        @Override
        public List<AstTreeTableNode> convert(final ContinueStatementWrapper continueStatement,
                final AstTreeTableNode parentNode) {
            return new ArrayList<AstTreeTableNode>() {
                private static final long serialVersionUID = 1L;
                {
                    add(new AstContinueStatementTreeTableNode(continueStatement, parentNode));
                }
            };
        }
    };

    private static Converter<ReturnStatementWrapper> returnConverter = new Converter<ReturnStatementWrapper>() {
        @Override
        public List<AstTreeTableNode> convert(final ReturnStatementWrapper returnStatement,
                final AstTreeTableNode parentNode) {
            return new ArrayList<AstTreeTableNode>() {
                private static final long serialVersionUID = 1L;
                {
                    add(new AstReturnStatementWrapper(returnStatement, parentNode));
                }
            };
        }
    };

    private static Converter<ThrowStatementWrapper> throwConverter = new Converter<ThrowStatementWrapper>() {
        @Override
        public List<AstTreeTableNode> convert(final ThrowStatementWrapper throwStatement,
                final AstTreeTableNode parentNode) {
            return new ArrayList<AstTreeTableNode>() {
                private static final long serialVersionUID = 1L;
                {
                    add(new AstThrowStatementTreeTableNode(throwStatement, parentNode));
                }
            };
        }
    };

    /**
     * User want if elseif else in the same level
     * 
     * @param statementWrapper
     * @param parentNode
     * @return
     */
    private static Converter<IfStatementWrapper> ifConverter = new Converter<IfStatementWrapper>() {
        @Override
        public List<AstTreeTableNode> convert(final IfStatementWrapper ifStatement, final AstTreeTableNode parentNode) {
            List<AstTreeTableNode> astTreeTableNodes = new ArrayList<>();
            astTreeTableNodes.add(new AstIfStatementTreeTableNode(ifStatement, parentNode));

            for (ElseIfStatementWrapper elseIfStatement : ifStatement.getElseIfStatements()) {
                astTreeTableNodes.addAll(elseIfConverter.convert(elseIfStatement, parentNode));
            }

            if (ifStatement.getElseStatement() != null) {
                astTreeTableNodes.add(new AstElseStatementTreeTableNode(ifStatement.getElseStatement(), parentNode));
            }
            return astTreeTableNodes;
        }
    };

    private static Converter<ElseIfStatementWrapper> elseIfConverter = new Converter<ElseIfStatementWrapper>() {
        @Override
        public List<AstTreeTableNode> convert(final ElseIfStatementWrapper elseIfStatement,
                final AstTreeTableNode parentNode) {
            return new ArrayList<AstTreeTableNode>() {
                private static final long serialVersionUID = 1L;
                {
                    add(new AstElseIfStatementTreeTableNode(elseIfStatement, parentNode));
                }
            };
        }
    };

    private static Converter<TryCatchStatementWrapper> tryCatchConverter = new Converter<TryCatchStatementWrapper>() {
        @Override
        public List<AstTreeTableNode> convert(final TryCatchStatementWrapper tryCatchStatement,
                final AstTreeTableNode parentNode) {
            List<AstTreeTableNode> astTreeTableNodes = new ArrayList<>();
            astTreeTableNodes.add(new AstTryStatementTreeTableNode(tryCatchStatement, parentNode));

            for (CatchStatementWrapper catchStatement : tryCatchStatement.getCatchStatements()) {
                astTreeTableNodes.addAll(catchConverter.convert(catchStatement, parentNode));
            }

            if (tryCatchStatement.getFinallyStatement() != null) {
                astTreeTableNodes.add(new AstFinallyStatementTreeTableNode(tryCatchStatement.getFinallyStatement(),
                        parentNode));
            }
            return astTreeTableNodes;
        }
    };

    private static Converter<CatchStatementWrapper> catchConverter = new Converter<CatchStatementWrapper>() {
        @Override
        public List<AstTreeTableNode> convert(final CatchStatementWrapper catchStatement,
                final AstTreeTableNode parentNode) {
            return new ArrayList<AstTreeTableNode>() {
                private static final long serialVersionUID = 1L;
                {
                    add(new AstCatchStatementTreeTableNode(catchStatement, parentNode));
                }
            };
        }
    };

    /**
     * Groovy not support yet. Return generic tree node.
     *
     * @param doWhileStatement
     * @param parentNode
     * @return
     */
    private static Converter<DoWhileStatementWrapper> doWhileConverter = new Converter<DoWhileStatementWrapper>() {
        @Override
        public List<AstTreeTableNode> convert(final DoWhileStatementWrapper doWhileStatement,
                final AstTreeTableNode parentNode) {
            return new ArrayList<AstTreeTableNode>() {
                private static final long serialVersionUID = 1L;
                {
                    add(new AstDoWhileStatementTreeTableNode(doWhileStatement, parentNode));
                }
            };
        }
    };

    /**
     * User not want to use it yet. Return generic
     *
     * @param synchronizedStatement
     * @param parentNode
     * @return
     */
    private static Converter<SynchronizedStatementWrapper> synchronizedConverter = new Converter<SynchronizedStatementWrapper>() {
        @Override
        public List<AstTreeTableNode> convert(final SynchronizedStatementWrapper synchronizedStatement,
                final AstTreeTableNode parentNode) {
            return new ArrayList<AstTreeTableNode>() {
                private static final long serialVersionUID = 1L;
                {
                    add(new AstStatementTreeTableNode(synchronizedStatement, parentNode) {});
                }
            };
        }
    };

    @SuppressWarnings("unchecked")
    public <T extends StatementWrapper> Converter<T> getConverter(T statementWrapper) {
        return (Converter<T>) converters.get(statementWrapper.getClass().getSimpleName());
    }

    private static void initConverters() {
        converters = new HashMap<>();
        converters.put(ForStatementWrapper.class.getSimpleName(), forConverter);
        converters.put(WhileStatementWrapper.class.getSimpleName(), whileConverter);
        converters.put(SwitchStatementWrapper.class.getSimpleName(), switchConverter);
        converters.put(CaseStatementWrapper.class.getSimpleName(), caseConverter);
        converters.put(AssertStatementWrapper.class.getSimpleName(), assertConverter);
        converters.put(BreakStatementWrapper.class.getSimpleName(), breakConverter);
        converters.put(ContinueStatementWrapper.class.getSimpleName(), continueConverter);
        converters.put(ReturnStatementWrapper.class.getSimpleName(), returnConverter);
        converters.put(ThrowStatementWrapper.class.getSimpleName(), throwConverter);
        converters.put(SynchronizedStatementWrapper.class.getSimpleName(), synchronizedConverter);
        converters.put(DoWhileStatementWrapper.class.getSimpleName(), doWhileConverter);
        converters.put(CatchStatementWrapper.class.getSimpleName(), catchConverter);
        converters.put(TryCatchStatementWrapper.class.getSimpleName(), tryCatchConverter);
        converters.put(ElseIfStatementWrapper.class.getSimpleName(), elseIfConverter);
        converters.put(IfStatementWrapper.class.getSimpleName(), ifConverter);
        converters.put(ExpressionStatementWrapper.class.getSimpleName(), expressionConverter);
    }

    public interface Converter<T extends StatementWrapper> {
        List<AstTreeTableNode> convert(T statement, AstTreeTableNode parentNode);
    }
}
