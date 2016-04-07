package com.kms.katalon.composer.testcase.groovy.ast;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.MethodPointerExpression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.SpreadMapExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.UnaryMinusExpression;
import org.codehaus.groovy.ast.expr.UnaryPlusExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;

import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArgumentListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ArrayExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BinaryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BitwiseNegationExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.BooleanExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.CastExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClassExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClosureExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ClosureListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstructorCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.DeclarationExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.FieldExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.GStringExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ListExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MapEntryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MapExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.MethodPointerExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PostfixExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PrefixExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.PropertyExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.RangeExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.SpreadExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.SpreadMapExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.StaticMethodCallExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.TernaryExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.TupleExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.UnaryMinusExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.UnaryPlusExpressionWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.VariableExpressionWrapper;

public class ExpressionWrapHelper {
    interface ExpressionWrapperConverter<T extends Expression> extends ASTWrapperConverter<T> {
        @Override
        public ExpressionWrapper wrap(T node, ASTNodeWrapper parentNode);
    }

    private static Map<String, ExpressionWrapperConverter<? extends Expression>> expressionWrapperConverterMap;

    private static final ExpressionWrapperConverter<ArgumentListExpression> argumentListWrapperConverter = new ExpressionWrapperConverter<ArgumentListExpression>() {
        @Override
        public ArgumentListExpressionWrapper wrap(ArgumentListExpression node, ASTNodeWrapper parentNode) {
            return new ArgumentListExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<ArrayExpression> arrayWrapperConverter = new ExpressionWrapperConverter<ArrayExpression>() {
        @Override
        public ArrayExpressionWrapper wrap(ArrayExpression node, ASTNodeWrapper parentNode) {
            return new ArrayExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<BooleanExpression> booleanListWrapperConverter = new ExpressionWrapperConverter<BooleanExpression>() {
        @Override
        public BooleanExpressionWrapper wrap(BooleanExpression node, ASTNodeWrapper parentNode) {
            return new BooleanExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<BitwiseNegationExpression> bitWiseWrapperConverter = new ExpressionWrapperConverter<BitwiseNegationExpression>() {
        @Override
        public BitwiseNegationExpressionWrapper wrap(BitwiseNegationExpression node, ASTNodeWrapper parentNode) {
            return new BitwiseNegationExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<CastExpression> castWrapperConverter = new ExpressionWrapperConverter<CastExpression>() {
        @Override
        public CastExpressionWrapper wrap(CastExpression node, ASTNodeWrapper parentNode) {
            return new CastExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<ClosureExpression> closureWrapperConverter = new ExpressionWrapperConverter<ClosureExpression>() {
        @Override
        public ClosureExpressionWrapper wrap(ClosureExpression node, ASTNodeWrapper parentNode) {
            return new ClosureExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<ConstructorCallExpression> construsctorCallWrapperConverter = new ExpressionWrapperConverter<ConstructorCallExpression>() {
        @Override
        public ConstructorCallExpressionWrapper wrap(ConstructorCallExpression node, ASTNodeWrapper parentNode) {
            return new ConstructorCallExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<DeclarationExpression> declarationWrapperConverter = new ExpressionWrapperConverter<DeclarationExpression>() {
        @Override
        public DeclarationExpressionWrapper wrap(DeclarationExpression node, ASTNodeWrapper parentNode) {
            return new DeclarationExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<BinaryExpression> binaryWrapperConverter = new ExpressionWrapperConverter<BinaryExpression>() {
        @Override
        public BinaryExpressionWrapper wrap(BinaryExpression node, ASTNodeWrapper parentNode) {
            return new BinaryExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<FieldExpression> fieldWrapperConverter = new ExpressionWrapperConverter<FieldExpression>() {
        @Override
        public FieldExpressionWrapper wrap(FieldExpression node, ASTNodeWrapper parentNode) {
            return new FieldExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<VariableExpression> variableWrapperConverter = new ExpressionWrapperConverter<VariableExpression>() {
        @Override
        public VariableExpressionWrapper wrap(VariableExpression node, ASTNodeWrapper parentNode) {
            return new VariableExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<GStringExpression> gStringWrapperConverter = new ExpressionWrapperConverter<GStringExpression>() {
        @Override
        public GStringExpressionWrapper wrap(GStringExpression node, ASTNodeWrapper parentNode) {
            return new GStringExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<MapExpression> mapWrapperConverter = new ExpressionWrapperConverter<MapExpression>() {
        @Override
        public MapExpressionWrapper wrap(MapExpression node, ASTNodeWrapper parentNode) {
            return new MapExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<MapEntryExpression> mapEntryWrapperConverter = new ExpressionWrapperConverter<MapEntryExpression>() {
        @Override
        public MapEntryExpressionWrapper wrap(MapEntryExpression node, ASTNodeWrapper parentNode) {
            return new MapEntryExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<MethodCallExpression> methodCallWrapperConverter = new ExpressionWrapperConverter<MethodCallExpression>() {
        @Override
        public MethodCallExpressionWrapper wrap(MethodCallExpression node, ASTNodeWrapper parentNode) {
            return new MethodCallExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<MethodPointerExpression> methodPointerWrapperConverter = new ExpressionWrapperConverter<MethodPointerExpression>() {
        @Override
        public MethodPointerExpressionWrapper wrap(MethodPointerExpression node, ASTNodeWrapper parentNode) {
            return new MethodPointerExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<ConstantExpression> constantWrapperConverter = new ExpressionWrapperConverter<ConstantExpression>() {
        @Override
        public ConstantExpressionWrapper wrap(ConstantExpression node, ASTNodeWrapper parentNode) {
            return new ConstantExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<PostfixExpression> postfixWrapperConverter = new ExpressionWrapperConverter<PostfixExpression>() {
        @Override
        public PostfixExpressionWrapper wrap(PostfixExpression node, ASTNodeWrapper parentNode) {
            return new PostfixExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<PrefixExpression> prefixWrapperConverter = new ExpressionWrapperConverter<PrefixExpression>() {
        @Override
        public PrefixExpressionWrapper wrap(PrefixExpression node, ASTNodeWrapper parentNode) {
            return new PrefixExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<RangeExpression> rangeWrapperConverter = new ExpressionWrapperConverter<RangeExpression>() {
        @Override
        public RangeExpressionWrapper wrap(RangeExpression node, ASTNodeWrapper parentNode) {
            return new RangeExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<PropertyExpression> propertyWrapperConverter = new ExpressionWrapperConverter<PropertyExpression>() {
        @Override
        public PropertyExpressionWrapper wrap(PropertyExpression node, ASTNodeWrapper parentNode) {
            return new PropertyExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<ClassExpression> classWrapperConverter = new ExpressionWrapperConverter<ClassExpression>() {
        @Override
        public ClassExpressionWrapper wrap(ClassExpression node, ASTNodeWrapper parentNode) {
            return new ClassExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<ClosureListExpression> closureListWrapperConverter = new ExpressionWrapperConverter<ClosureListExpression>() {
        @Override
        public ClosureListExpressionWrapper wrap(ClosureListExpression node, ASTNodeWrapper parentNode) {
            return new ClosureListExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<ListExpression> listWrapperConverter = new ExpressionWrapperConverter<ListExpression>() {
        @Override
        public ListExpressionWrapper wrap(ListExpression node, ASTNodeWrapper parentNode) {
            return new ListExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<SpreadExpression> spreadWrapperConverter = new ExpressionWrapperConverter<SpreadExpression>() {
        @Override
        public SpreadExpressionWrapper wrap(SpreadExpression node, ASTNodeWrapper parentNode) {
            return new SpreadExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<SpreadMapExpression> spreadMapWrapperConverter = new ExpressionWrapperConverter<SpreadMapExpression>() {
        @Override
        public SpreadMapExpressionWrapper wrap(SpreadMapExpression node, ASTNodeWrapper parentNode) {
            return new SpreadMapExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<StaticMethodCallExpression> staticMethodCallWrapperConverter = new ExpressionWrapperConverter<StaticMethodCallExpression>() {
        @Override
        public StaticMethodCallExpressionWrapper wrap(StaticMethodCallExpression node, ASTNodeWrapper parentNode) {
            return new StaticMethodCallExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<TernaryExpression> ternaryWrapperConverter = new ExpressionWrapperConverter<TernaryExpression>() {
        @Override
        public TernaryExpressionWrapper wrap(TernaryExpression node, ASTNodeWrapper parentNode) {
            return new TernaryExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<TupleExpression> tupleWrapperConverter = new ExpressionWrapperConverter<TupleExpression>() {
        @Override
        public TupleExpressionWrapper wrap(TupleExpression node, ASTNodeWrapper parentNode) {
            return new TupleExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<UnaryMinusExpression> unaryMinusWrapperConverter = new ExpressionWrapperConverter<UnaryMinusExpression>() {
        @Override
        public UnaryMinusExpressionWrapper wrap(UnaryMinusExpression node, ASTNodeWrapper parentNode) {
            return new UnaryMinusExpressionWrapper(node, parentNode);
        }
    };

    private static final ExpressionWrapperConverter<UnaryPlusExpression> unaryPlusWrapperConverter = new ExpressionWrapperConverter<UnaryPlusExpression>() {
        @Override
        public UnaryPlusExpressionWrapper wrap(UnaryPlusExpression node, ASTNodeWrapper parentNode) {
            return new UnaryPlusExpressionWrapper(node, parentNode);
        }
    };

    static {
        initExpressionWrapperConverterMap();
    }

    private static void initExpressionWrapperConverterMap() {
        expressionWrapperConverterMap = new HashMap<>();
        expressionWrapperConverterMap.put(ArgumentListExpression.class.getSimpleName(), argumentListWrapperConverter);
        expressionWrapperConverterMap.put(ArrayExpression.class.getSimpleName(), arrayWrapperConverter);
        expressionWrapperConverterMap.put(BooleanExpression.class.getSimpleName(), booleanListWrapperConverter);
        expressionWrapperConverterMap.put(BitwiseNegationExpression.class.getSimpleName(), bitWiseWrapperConverter);
        expressionWrapperConverterMap.put(CastExpression.class.getSimpleName(), castWrapperConverter);
        expressionWrapperConverterMap.put(ClosureExpression.class.getSimpleName(), closureWrapperConverter);
        expressionWrapperConverterMap.put(ConstructorCallExpression.class.getSimpleName(),
                construsctorCallWrapperConverter);
        expressionWrapperConverterMap.put(DeclarationExpression.class.getSimpleName(), declarationWrapperConverter);
        expressionWrapperConverterMap.put(BinaryExpression.class.getSimpleName(), binaryWrapperConverter);
        expressionWrapperConverterMap.put(FieldExpression.class.getSimpleName(), fieldWrapperConverter);
        expressionWrapperConverterMap.put(VariableExpression.class.getSimpleName(), variableWrapperConverter);
        expressionWrapperConverterMap.put(GStringExpression.class.getSimpleName(), gStringWrapperConverter);
        expressionWrapperConverterMap.put(MapExpression.class.getSimpleName(), mapWrapperConverter);
        expressionWrapperConverterMap.put(MapEntryExpression.class.getSimpleName(), mapEntryWrapperConverter);
        expressionWrapperConverterMap.put(MethodCallExpression.class.getSimpleName(), methodCallWrapperConverter);
        expressionWrapperConverterMap.put(MethodPointerExpression.class.getSimpleName(), methodPointerWrapperConverter);
        expressionWrapperConverterMap.put(ConstantExpression.class.getSimpleName(), constantWrapperConverter);
        expressionWrapperConverterMap.put(PostfixExpression.class.getSimpleName(), postfixWrapperConverter);
        expressionWrapperConverterMap.put(PrefixExpression.class.getSimpleName(), prefixWrapperConverter);
        expressionWrapperConverterMap.put(RangeExpression.class.getSimpleName(), rangeWrapperConverter);
        expressionWrapperConverterMap.put(PropertyExpression.class.getSimpleName(), propertyWrapperConverter);
        expressionWrapperConverterMap.put(ClassExpression.class.getSimpleName(), classWrapperConverter);
        expressionWrapperConverterMap.put(ClosureListExpression.class.getSimpleName(), closureListWrapperConverter);
        expressionWrapperConverterMap.put(ListExpression.class.getSimpleName(), listWrapperConverter);
        expressionWrapperConverterMap.put(SpreadExpression.class.getSimpleName(), spreadWrapperConverter);
        expressionWrapperConverterMap.put(SpreadMapExpression.class.getSimpleName(), spreadMapWrapperConverter);
        expressionWrapperConverterMap.put(StaticMethodCallExpression.class.getSimpleName(),
                staticMethodCallWrapperConverter);
        expressionWrapperConverterMap.put(TernaryExpression.class.getSimpleName(), ternaryWrapperConverter);
        expressionWrapperConverterMap.put(TupleExpression.class.getSimpleName(), tupleWrapperConverter);
        expressionWrapperConverterMap.put(UnaryMinusExpression.class.getSimpleName(), unaryMinusWrapperConverter);
        expressionWrapperConverterMap.put(UnaryPlusExpression.class.getSimpleName(), unaryPlusWrapperConverter);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Expression> ExpressionWrapper wrap(T expression, ASTNodeWrapper parentNode) {
        ExpressionWrapperConverter<T> provider = (ExpressionWrapperConverter<T>) expressionWrapperConverterMap.get(expression.getClass()
                .getSimpleName());
        if (provider != null) {
            return provider.wrap(expression, parentNode);
        }
        return null;
    }
}
