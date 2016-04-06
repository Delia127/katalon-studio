package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;

public class MapExpressionWrapper extends ExpressionWrapper {
    private List<MapEntryExpressionWrapper> mapEntryExpressions = new ArrayList<MapEntryExpressionWrapper>();

    public MapExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
    }

    public MapExpressionWrapper(List<MapEntryExpressionWrapper> mapEntryExpressions, ASTNodeWrapper parentNodeWrapper) {
        this(parentNodeWrapper);
        setMapEntryExpressions(mapEntryExpressions);
    }

    public MapExpressionWrapper(MapExpression expression, ASTNodeWrapper parentNodeWrapper) {
        super(expression, parentNodeWrapper);
        for (MapEntryExpression mapEntryExpression : expression.getMapEntryExpressions()) {
            mapEntryExpressions.add(new MapEntryExpressionWrapper(mapEntryExpression, this));
        }
    }

    public MapExpressionWrapper(MapExpressionWrapper mapExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(mapExpressionWrapper, parentNodeWrapper);
        for (MapEntryExpressionWrapper mapEntryExpressionWrapper : mapExpressionWrapper.getMapEntryExpressions()) {
            mapEntryExpressions.add(new MapEntryExpressionWrapper(mapEntryExpressionWrapper, this));
        }
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder(32);
        sb.append("[");
        int size = mapEntryExpressions.size();
        if (size > 0) {
            sb.append(mapEntryExpressions.get(0).getText());
            for (int i = 1; i < size; i++) {
                sb.append(", " + mapEntryExpressions.get(i).getText());
                if (sb.length() > 120 && i < size - 1) {
                    sb.append(", ... ");
                    break;
                }
            }
        } else {
            sb.append(":");
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean hasAstChildren() {
        return true;
    }

    @Override
    public List<ASTNodeWrapper> getAstChildren() {
        List<ASTNodeWrapper> astNodeWrappers = new ArrayList<ASTNodeWrapper>();
        astNodeWrappers.addAll(mapEntryExpressions);
        return astNodeWrappers;
    }

    public List<MapEntryExpressionWrapper> getMapEntryExpressions() {
        return mapEntryExpressions;
    }

    public void setMapEntryExpressions(List<MapEntryExpressionWrapper> mapEntryExpressions) {
        this.mapEntryExpressions = mapEntryExpressions;
    }

    @Override
    public MapExpressionWrapper clone() {
        return new MapExpressionWrapper(this, getParent());
    }
}
