package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.util.AstKeywordsInputUtil;

public class ArgumentListExpressionWrapper extends TupleExpressionWrapper {
    public ArgumentListExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }

    public ArgumentListExpressionWrapper(ArgumentListExpression argumentListExpression, ASTNodeWrapper parentNodeWrapper) {
        super(argumentListExpression, parentNodeWrapper);
    }

    public ArgumentListExpressionWrapper(ArgumentListExpressionWrapper argumentListExpression,
            ASTNodeWrapper parentNodeWrapper) {
        super(argumentListExpression, parentNodeWrapper);
    }

    @Override
    public String getText() {
        StringBuilder value = new StringBuilder();
        value.append("(");
        value.append(StringUtils.join(Iterables.transform(expressions, new Function<ExpressionWrapper, String>() {
            @Override
            public String apply(ExpressionWrapper expression) {
                return expression.getText();
            }
        }).iterator(), ", "));
        value.append(")");
        return value.toString();
    }

    @Override
    public ArgumentListExpressionWrapper clone() {
        return new ArgumentListExpressionWrapper(this, getParent());
    }

    @Override
    public ExpressionWrapper copy(ASTNodeWrapper newParent) {
        return super.copy(newParent);
    }
    
    public String[] getArgumentListParameterTypes() {
        String[] paramTypes = new String[this.getExpressions().size()];
        for (int i = 0; i < this.getExpressions().size(); i++) {
            ExpressionWrapper exWrapper = this.getExpression(i);
            paramTypes[i] = exWrapper.getType().getTypeClass().getName();
//            String resolvedType = paramTypes[i];
//            try {
//                resolvedType = Signature.toString(paramTypes[i]);
//            } catch (Exception e) {
//                resolvedType = paramTypes[i];
//            }
//            paramTypes[i] = resolvedType;
//            try {
//                paramTypes[i] = resolveType1(ProjectController.getInstance()
//                        .getProjectClassLoader(ProjectController.getInstance().getCurrentProject()), paramTypes[i])
//                                .getName();
//            } catch (Exception e) {
//                paramTypes[i] = resolvedType;
//            }
        }
        return paramTypes;
    }
    
    public Class<?> resolveType1(ClassLoader classLoader, String type) {
        Class<?> foundClass = loadClassQuietly1(type, classLoader);
        if (foundClass != null) {
            return foundClass;
        }
        return null;
    }

    protected Class<?> loadClassQuietly1(String className, ClassLoader classLoader) {
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            return AstKeywordsInputUtil.loadClassFromImportedPackage(className, classLoader);
        }
    }

}
