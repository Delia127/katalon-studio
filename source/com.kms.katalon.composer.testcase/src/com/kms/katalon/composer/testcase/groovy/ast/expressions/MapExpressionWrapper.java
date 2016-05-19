package com.kms.katalon.composer.testcase.groovy.ast.expressions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;

import com.kms.katalon.composer.testcase.groovy.ast.ASTNodeWrapper;
import com.kms.katalon.composer.testcase.groovy.ast.ClassNodeWrapper;

public class MapExpressionWrapper extends ExpressionWrapper {
    private List<MapEntryExpressionWrapper> mapEntryExpressions = new ArrayList<MapEntryExpressionWrapper>();

    public MapExpressionWrapper(ASTNodeWrapper parentNodeWrapper) {
        super(parentNodeWrapper);
        this.type = new ClassNodeWrapper(ClassHelper.MAP_TYPE, this);
    }

    public MapExpressionWrapper(List<MapEntryExpressionWrapper> mapEntryExpressions, ASTNodeWrapper parentNodeWrapper) {
        this(parentNodeWrapper);
        if (mapEntryExpressions == null) {
            return;
        }
        for (MapEntryExpressionWrapper mapEntryExpression : mapEntryExpressions) {
            if (mapEntryExpression == null) {
                continue;
            }
            mapEntryExpression.setParent(this);
        }
        this.mapEntryExpressions = mapEntryExpressions;
        this.type = new ClassNodeWrapper(ClassHelper.MAP_TYPE, this);
    }

    public MapExpressionWrapper(MapExpression expression, ASTNodeWrapper parentNodeWrapper) {
        super(expression, parentNodeWrapper);
        for (MapEntryExpression mapEntryExpression : expression.getMapEntryExpressions()) {
            mapEntryExpressions.add(new MapEntryExpressionWrapper(mapEntryExpression, this));
        }
    }

    public MapExpressionWrapper(MapExpressionWrapper mapExpressionWrapper, ASTNodeWrapper parentNodeWrapper) {
        super(mapExpressionWrapper, parentNodeWrapper);
        copyMapProperties(mapExpressionWrapper);
    }

    private void copyMapProperties(MapExpressionWrapper mapExpressionWrapper) {
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
        return Collections.unmodifiableList(mapEntryExpressions);
    }

    public void addExpression(MapEntryExpressionWrapper expression) {
        if (expression == null) {
            return;
        }
        expression.setParent(this);
        mapEntryExpressions.add(expression);
    }

    public boolean addExpression(MapEntryExpressionWrapper expression, int index) {
        if (expression == null || index < 0 || index > mapEntryExpressions.size()) {
            return false;
        }
        expression.setParent(this);
        mapEntryExpressions.add(index, expression);
        return true;
    }

    public void addExpressions(List<MapEntryExpressionWrapper> listOfExpressions) {
        if (listOfExpressions == null) {
            return;
        }
        for (MapEntryExpressionWrapper expression : listOfExpressions) {
            if (expression == null) {
                continue;
            }
            expression.setParent(this);
            mapEntryExpressions.add(expression);
        }
    }

    public boolean removeExpression(MapEntryExpressionWrapper expression) {
        return mapEntryExpressions.remove(expression);
    }

    public boolean removeExpression(int index) {
        if (index < 0 || index >= mapEntryExpressions.size()) {
            return false;
        }
        mapEntryExpressions.remove(index);
        return true;
    }

    public boolean setExpression(MapEntryExpressionWrapper expression, int index) {
        if (expression == null || index < 0 || index > mapEntryExpressions.size()) {
            return false;
        }
        expression.setParent(this);
        mapEntryExpressions.set(index, expression);
        return true;
    }

    @Override
    public MapExpressionWrapper clone() {
        return new MapExpressionWrapper(this, getParent());
    }

    @Override
    public boolean isInputEditatble() {
        return true;
    }

    @Override
    public ASTNodeWrapper getInput() {
        return this;
    }

    @Override
    public boolean updateInputFrom(ASTNodeWrapper input) {
        if (!(input instanceof MapExpressionWrapper) || this.isEqualsTo(input)) {
            return false;
        }
        copyMapProperties((MapExpressionWrapper) input);
        return true;
    }

    @Override
    public boolean replaceChild(ASTNodeWrapper oldChild, ASTNodeWrapper newChild) {
        if (!(newChild instanceof ExpressionWrapper) || !(oldChild instanceof ExpressionWrapper)) {
            return false;
        }
        ExpressionWrapper originalExpression = (ExpressionWrapper) oldChild;
        ExpressionWrapper newExpression = (ExpressionWrapper) newChild;
        for (MapEntryExpressionWrapper mapEntry : getMapEntryExpressions()) {
            if (mapEntry.getKeyExpression() == originalExpression) {
                mapEntry.setKeyExpression(newExpression);
                return true;
            } else if (mapEntry.getValueExpression() == originalExpression) {
                mapEntry.setValueExpression(newExpression);
                return true;
            }
        }
        return false;
    }
}
