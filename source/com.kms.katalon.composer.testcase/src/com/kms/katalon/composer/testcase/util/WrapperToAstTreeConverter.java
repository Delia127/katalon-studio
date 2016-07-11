package com.kms.katalon.composer.testcase.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.kms.katalon.composer.testcase.ast.treetable.AstBuiltInKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCallTestCaseKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCompositeInputEditableStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCompositeStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstCustomKeywordTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstInputEditableStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstSwitchStatementTreeTableNode;
import com.kms.katalon.composer.testcase.ast.treetable.AstTreeTableNode;
import com.kms.katalon.composer.testcase.constants.ImageConstants;
import com.kms.katalon.composer.testcase.constants.StringConstants;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BinaryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.AssertStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.BreakStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.CatchStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ContinueStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.DoWhileStatementWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.statements.ElseIfStatementWrapper;
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

    public List<AstTreeTableNode> convert(List<? extends StatementWrapper> statementWrappers,
            AstTreeTableNode parentNode) {
        List<AstTreeTableNode> astTreeTableNodes = new ArrayList<>();
        for (StatementWrapper statement : statementWrappers) {
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
                return new AstInputEditableStatementTreeTableNode(statementWrapper, parentNode,
                        ImageConstants.IMG_16_COMMENT, StringConstants.TREE_COMMENT);
            }

            return new AstInputEditableStatementTreeTableNode(statementWrapper, parentNode);
        }

        private AstTreeTableNode convert(ExpressionStatementWrapper statementWrapper,
                BinaryExpressionWrapper expression, AstTreeTableNode parentNode) {
            if (expression.getRightExpression() instanceof MethodCallExpressionWrapper) {
                MethodCallExpressionWrapper methodCallExpression = (MethodCallExpressionWrapper) expression.getRightExpression();
                if (methodCallExpression.isBuiltInKeywordMethodCall()) {
                    return new AstBuiltInKeywordTreeTableNode(statementWrapper, parentNode);
                } else if (methodCallExpression.isCustomKeywordMethodCall()) {
                    return new AstCustomKeywordTreeTableNode(statementWrapper, parentNode);
                }
            }
            return new AstInputEditableStatementTreeTableNode(statementWrapper, parentNode,
                    ImageConstants.IMG_16_BINARY, StringConstants.TREE_BINARY_STATEMENT);
        }

        private AstTreeTableNode convert(ExpressionStatementWrapper statementWrapper,
                MethodCallExpressionWrapper methodCallExpression, AstTreeTableNode parentNode) {
            if (methodCallExpression.isBuiltInKeywordMethodCall()) {
                if (methodCallExpression.isCallTestCaseMethodCall()) {
                    return new AstCallTestCaseKeywordTreeTableNode(statementWrapper, parentNode);
                }
                return new AstBuiltInKeywordTreeTableNode(statementWrapper, parentNode);
            } else if (methodCallExpression.isCustomKeywordMethodCall()) {
                return new AstCustomKeywordTreeTableNode(statementWrapper, parentNode);
            }
            return new AstInputEditableStatementTreeTableNode(statementWrapper, parentNode,
                    ImageConstants.IMG_16_FUNCTION, StringConstants.TREE_METHOD_CALL_STATEMENT);
        }
    };

    private static Converter<ForStatementWrapper> forConverter = new Converter<ForStatementWrapper>() {
        @Override
        public List<AstTreeTableNode> convert(final ForStatementWrapper forStatement, final AstTreeTableNode parentNode) {
            return new ArrayList<AstTreeTableNode>() {
                private static final long serialVersionUID = 1L;
                {
                    add(new AstCompositeInputEditableStatementTreeTableNode(forStatement, parentNode,
                            ImageConstants.IMG_16_LOOP, StringConstants.TREE_FOR_STATEMENT));
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
                    add(new AstCompositeInputEditableStatementTreeTableNode(whileStatement, parentNode,
                            ImageConstants.IMG_16_LOOP, StringConstants.TREE_WHILE_STATEMENT));
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

    private static Converter<AssertStatementWrapper> assertConverter = new Converter<AssertStatementWrapper>() {
        @Override
        public List<AstTreeTableNode> convert(final AssertStatementWrapper assertStatement,
                final AstTreeTableNode parentNode) {
            return new ArrayList<AstTreeTableNode>() {
                private static final long serialVersionUID = 1L;
                {
                    add(new AstInputEditableStatementTreeTableNode(assertStatement, parentNode,
                            ImageConstants.IMG_16_ASSERT, StringConstants.TREE_ASSERT_STATEMENT));
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
                    add(new AstStatementTreeTableNode(breakStatement, parentNode, StringConstants.TREE_BREAK_STATEMENT));
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
                    add(new AstStatementTreeTableNode(continueStatement, parentNode,
                            StringConstants.TREE_CONTINUE_STATEMENT));
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
                    add(new AstStatementTreeTableNode(returnStatement, parentNode,
                            StringConstants.TREE_RETURN_STATEMENT));
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
                    add(new AstStatementTreeTableNode(throwStatement, parentNode, StringConstants.TREE_THROW_STATEMENT));
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
            astTreeTableNodes.add(new AstCompositeInputEditableStatementTreeTableNode(ifStatement, parentNode,
                    ImageConstants.IMG_16_IF, StringConstants.TREE_IF_STATEMENT));

            for (ElseIfStatementWrapper elseIfStatement : ifStatement.getComplexChildStatements()) {
                astTreeTableNodes.addAll(elseIfConverter.convert(elseIfStatement, parentNode));
            }

            if (ifStatement.hasLastStatement()) {
                astTreeTableNodes.add(new AstCompositeStatementTreeTableNode(ifStatement.getLastStatement(),
                        parentNode, ImageConstants.IMG_16_ELSE, StringConstants.TREE_ELSE_STATEMENT));
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
                    add(new AstCompositeInputEditableStatementTreeTableNode(elseIfStatement, parentNode,
                            ImageConstants.IMG_16_ELSE_IF, StringConstants.TREE_ELSE_IF_STATEMENT));
                }
            };
        }
    };

    private static Converter<TryCatchStatementWrapper> tryCatchConverter = new Converter<TryCatchStatementWrapper>() {
        @Override
        public List<AstTreeTableNode> convert(final TryCatchStatementWrapper tryCatchStatement,
                final AstTreeTableNode parentNode) {
            List<AstTreeTableNode> astTreeTableNodes = new ArrayList<>();
            astTreeTableNodes.add(new AstCompositeStatementTreeTableNode(tryCatchStatement, parentNode,
                    StringConstants.TREE_TRY_STATEMENT));

            for (CatchStatementWrapper catchStatement : tryCatchStatement.getComplexChildStatements()) {
                astTreeTableNodes.addAll(catchConverter.convert(catchStatement, parentNode));
            }

            if (tryCatchStatement.hasLastStatement()) {
                astTreeTableNodes.add(new AstCompositeInputEditableStatementTreeTableNode(
                        tryCatchStatement.getLastStatement(), parentNode, StringConstants.TREE_FINALLY_STATEMENT));
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
                    add(new AstCompositeInputEditableStatementTreeTableNode(catchStatement, parentNode,
                            StringConstants.TREE_CATCH_STATEMENT));
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
                    add(new AstCompositeInputEditableStatementTreeTableNode(doWhileStatement, parentNode,
                            ImageConstants.IMG_16_LOOP, StringConstants.TREE_DO_WHILE_STATEMENT));
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
